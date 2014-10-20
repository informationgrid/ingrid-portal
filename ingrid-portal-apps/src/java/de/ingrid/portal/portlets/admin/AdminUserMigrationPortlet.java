/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets.admin;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.Permission;
import java.security.Permissions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.administration.PortalAdministration;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.profiler.Profiler;
import org.apache.jetspeed.profiler.rules.PrincipalRule;
import org.apache.jetspeed.security.Group;
import org.apache.jetspeed.security.GroupManager;
import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.PermissionManager;
import org.apache.jetspeed.security.Role;
import org.apache.jetspeed.security.RoleManager;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

import de.ingrid.portal.global.IngridPersistencePrefs;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.UtilsString;
import de.ingrid.portal.security.permission.IngridPermission;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class AdminUserMigrationPortlet extends GenericVelocityPortlet {

    private final static Logger log = LoggerFactory.getLogger(AdminUserMigrationPortlet.class);

    private PortalAdministration admin;

    private UserManager userManager;

    private RoleManager roleManager;

    private GroupManager groupManager;

    private PermissionManager permissionManager;

    private PageManager pageManager;

    private Profiler profiler;

    private static final XStream xstream;

    static {
        try {
            // Create the SessionFactory
            xstream = new XStream();
        } catch (Throwable ex) {
            log.error("Initial Xstream creation failed.", ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#init(javax.portlet.PortletConfig)
     */
    public void init(PortletConfig config) throws PortletException {
        super.init(config);

        profiler = (Profiler) getPortletContext().getAttribute(CommonPortletServices.CPS_PROFILER_COMPONENT);
        if (null == profiler) {
            throw new PortletException("Failed to find the Portal Profiler on portlet initialization");
        }
        pageManager = (PageManager) getPortletContext().getAttribute(CommonPortletServices.CPS_PAGE_MANAGER_COMPONENT);
        if (null == pageManager) {
            throw new PortletException("Failed to find the Portal Pagemanager on portlet initialization");
        }
        admin = (PortalAdministration) getPortletContext()
                .getAttribute(CommonPortletServices.CPS_PORTAL_ADMINISTRATION);
        if (null == admin) {
            throw new PortletException("Failed to find the Portal Administration on portlet initialization");
        }
        userManager = (UserManager) getPortletContext().getAttribute(CommonPortletServices.CPS_USER_MANAGER_COMPONENT);
        if (null == userManager) {
            throw new PortletException("Failed to find the User Manager on portlet initialization");
        }
        roleManager = (RoleManager) getPortletContext().getAttribute(CommonPortletServices.CPS_ROLE_MANAGER_COMPONENT);
        if (null == roleManager) {
            throw new PortletException("Failed to find the Role Manager on portlet initialization");
        }
        groupManager = (GroupManager) getPortletContext().getAttribute(
                CommonPortletServices.CPS_GROUP_MANAGER_COMPONENT);
        if (null == groupManager) {
            throw new PortletException("Failed to find the Group Manager on portlet initialization");
        }
        permissionManager = (PermissionManager) getPortletContext().getAttribute(
                CommonPortletServices.CPS_PERMISSION_MANAGER);
        if (permissionManager == null) {
            throw new PortletException("Could not get instance of portal permission manager component");
        }
    }

    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#doView(javax.portlet.RenderRequest,
     *      javax.portlet.RenderResponse)
     */
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        Context context = getContext(request);

        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()), request.getLocale());
        context.put("MESSAGES", messages);

        response.setTitle(messages.getString("admin.title.migration"));
        
        String action = request.getParameter("action");
        if (action != null && action.equals("doExport")) {
            try {
                HashMap<String, List<HashMap<String, Object>>> export = new HashMap<String, List<HashMap<String, Object>>>();
                export.put("users", new ArrayList<HashMap<String, Object>>());
                List<HashMap<String, Object>> userList = export.get("users");
                // iterate over all users
                List<User> users = userManager.getUsers("");
                for (User user : users) {
                    HashMap<String, Object> userMap = new HashMap<String, Object>();
                    userList.add(userMap);
                    String password = "";
                    Iterator<Object> it = userManager.getSubject(user).getPrivateCredentials().iterator();
                    while (it.hasNext()) {
                        PasswordCredential pc = (PasswordCredential) it.next();
                        if (pc.getPassword() != null && pc.getPassword().length() > 0) {
                            password = new String(pc.getPassword());
                            break;
                        }
                    }
                    userMap.put("userPasswordCredential", password);

                    // userMap.put("userSubject", user.getSubject());
                    //Principal userPrincipal = SecurityUtil.getPrincipal(userManager.getSubject(user), User.class);
                    userMap.put("userPrincipalName", user.getName());

                    Permissions userPermissions = permissionManager.getPermissions(user);
                    List<IngridPermission> ingridPermissionList = new ArrayList<IngridPermission>();
                    Enumeration<Permission> el = userPermissions.elements();
                    while (el.hasMoreElements()) {
                        Permission p = el.nextElement();
                        if (p instanceof IngridPermission) {
                            ingridPermissionList.add((IngridPermission) p);
                        }
                    }
                    userMap.put("userPermissions", ingridPermissionList);

                    HashMap<String, String> userAttribs = new HashMap<String, String>();
                    Map<String, String> prefs = user.getInfoMap();
                    for (String prefKey : prefs.keySet()) {
                    	String prefValue = prefs.get(prefKey);
                        userAttribs.put(prefKey, (prefValue == null ? "" : prefValue));
                    }
                    userMap.put("userAttributes", userAttribs);

                    List<Role> roles = roleManager.getRolesForUser(user.getName());
                    List<String> rolesList = new ArrayList<String>();
                    for (Role role : roles) {
                        rolesList.add(role.getName());
                    }
                    userMap.put("userRoles", rolesList);

                    List<Group> groups = groupManager.getGroupsForUser(user.getName());
                    List<String> groupsList = new ArrayList<String>();
                    for (Group group : groups) {
                    	groupsList.add(group.getName());
                    }
                    userMap.put("userGroups", groupsList);

                    Collection<PrincipalRule> rules = profiler.getRulesForPrincipal(user);
                    HashMap<String, String> userRules = new HashMap<String, String>();
                    for (PrincipalRule rule : rules) {
                        userRules.put(rule.getProfilingRule().getId(), rule.getLocatorName());
                    }
                    userMap.put("userRules", userRules);

                    HashMap<String, String> ingridPrefs = IngridPersistencePrefs.getPrefs(user.getName());
                    userMap.put("userIngridPrefs", ingridPrefs);
                }
                context.put("xmlData", UtilsString.htmlescape(xstream.toXML(export)));
            } catch (Exception e) {
                log.error("Error serializing data", e);
            }
        } else if (action != null && action.equals("doImport")) {
            context.put("sqlStatements", request.getPortletSession().getAttribute("sqlStatements"));
        }
        super.doView(request, response);
    }

    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#processAction(javax.portlet.ActionRequest,
     *      javax.portlet.ActionResponse)
     */
    public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {
        if (request.getParameter("doExport") != null) {
            response.setRenderParameter("action", "doExport");
        } else if (request.getParameter("doImport") != null) {
// TODO mm : ? User Import (Creation) better according to JS23 UserManagerService.createUser ?
            String path = request.getParameter("path");
            FileInputStream fis = new FileInputStream(path);
            int x = fis.available();
            byte b[] = new byte[x];
            fis.read(b);
            String xmlData = new String(b);

            Object obj = xstream.fromXML(xmlData);
            if (obj instanceof HashMap) {
                Map<String, Object> map = (Map<String, Object>) obj;
                List<String> sqlStatements = new ArrayList<String>();
                List<Map<String, Object>> users = (List<Map<String, Object>>) map.get("users");
                for (Map<String, Object> userMap : users) {
                    String userName = (String) userMap.get("userPrincipalName");
                    String oldPassword = (String) userMap.get("userPasswordCredential");
                    if (!userManager.userExists(userName) && oldPassword != null && oldPassword.length() > 0) {
                        String password = "geheim";
// TODO mm: FIX SQL ??? better do it JS 23 way !
                        String passwordUpdateSql = "UPDATE security_credential, security_principal SET security_credential.COLUMN_VALUE=\""
                                + oldPassword
                                + "\" WHERE security_principal.FULL_PATH=\"/user/"
                                + userName
                                + "\" AND security_principal.PRINCIPAL_ID=security_credential.PRINCIPAL_ID;";
                        sqlStatements.add(passwordUpdateSql);
                        Map<String, String> userAttributes = (Map<String, String>) userMap.get("userAttributes");
                        List<String> roles = (List<String>) userMap.get("userRoles");
                        List<String> groups = (List<String>) userMap.get("userGroups");
                        Map<String, String> rules = (Map<String, String>) userMap.get("userRules");
                        List<IngridPermission> userPermissions = (List<IngridPermission>) userMap.get("userPermissions");
                        Map<String, String> ingridPrefs = (Map<String, String>) userMap.get("userIngridPrefs");

                        try {
                            // create user
                            admin.registerUser(userName, password, roles, groups, userAttributes, rules, null);

                            User user = userManager.getUser(userName);
                            // activate user
                            user.setEnabled(true);

                            // create ingrid specific permissions
                            for (IngridPermission userPermission : userPermissions) {
                                if (!permissionManager.permissionExists(userPermission)) {
                                    permissionManager.addPermission(userPermission);
                                }
                                permissionManager.grantPermission(userPermission, user);
                            }

                            // create ingrid specific user preferences
                            Set<Entry<String, String>> ingridPrefKeys = ingridPrefs.entrySet();
                            Iterator<Entry<String, String>> it = ingridPrefKeys.iterator();
                            while (it.hasNext()) {
                                Entry<String, String> entry = it.next();
                                String prefValue = entry.getValue();
                                IngridPersistencePrefs.setPref(userName, entry.getKey(), xstream.fromXML(prefValue));
                            }

                            request.getPortletSession().setAttribute("sqlStatements", sqlStatements);
                            response.setRenderParameter("action", "doImport");

                        } catch (Exception e) {
                            log.error("Error registering new user", e);
                        }
                    }
                }
            }
        }
    }
}
