/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets.admin;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.Permission;
import java.security.Permissions;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.prefs.Preferences;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;

import com.thoughtworks.xstream.XStream;

import de.ingrid.portal.global.IngridPersistencePrefs;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.UtilsString;
import de.ingrid.portal.portlets.security.SecurityUtil;
import de.ingrid.portal.security.UserManager;

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
                request.getLocale()));
        context.put("MESSAGES", messages);

        response.setTitle(messages.getString("admin.title.migration"));
        
        String action = request.getParameter("action");
        if (action != null && action.equals("doExport")) {
            try {
                HashMap export = new HashMap();
                export.put("users", new ArrayList());
                List userList = (List) export.get("users");
                // iterate over all users
                Iterator users = userManager.getUsers("");
                while (users.hasNext()) {
                    HashMap userMap = new HashMap();
                    userList.add(userMap);
                    User user = (User) users.next();
                    String password = "";
                    Iterator it = user.getSubject().getPrivateCredentials().iterator();
                    while (it.hasNext()) {
                        PasswordCredential pc = (PasswordCredential) it.next();
                        if (pc.getPassword() != null && pc.getPassword().length > 0) {
                            password = new String(pc.getPassword());
                            break;
                        }
                    }
                    userMap.put("userPasswordCredential", password);

                    // userMap.put("userSubject", user.getSubject());
                    Principal userPrincipal = SecurityUtil.getPrincipal(user.getSubject(), UserPrincipal.class);
                    userMap.put("userPrincipalName", userPrincipal.getName());

                    Permissions userPermissions = permissionManager.getPermissions(userPrincipal);
                    ArrayList ingridPermissionList = new ArrayList();
                    Enumeration el = userPermissions.elements();
                    while (el.hasMoreElements()) {
                        Permission p = (Permission) el.nextElement();
                        if (p.getClass().getName().startsWith("de.ingrid.portal.security.permission.Ingrid")) {
                            ingridPermissionList.add(p);
                        }
                    }
                    userMap.put("userPermissions", ingridPermissionList);

                    HashMap userAttribs = new HashMap();
                    Preferences prefs = user.getUserAttributes();
                    String[] prefKeys = prefs.keys();
                    for (int i = 0; i < prefKeys.length; i++) {
                        userAttribs.put(prefKeys[i], prefs.get(prefKeys[i], ""));
                    }
                    userMap.put("userAttributes", userAttribs);
                    Collection roles = roleManager.getRolesForUser(userPrincipal.getName());
                    ArrayList rolesList = new ArrayList();
                    it = roles.iterator();
                    while (it.hasNext()) {
                        Role role = (Role) it.next();
                        rolesList.add(role.getPrincipal().getName());
                    }
                    userMap.put("userRoles", rolesList);

                    Collection groups = groupManager.getGroupsForUser(userPrincipal.getName());
                    ArrayList groupsList = new ArrayList();
                    it = groups.iterator();
                    while (it.hasNext()) {
                        Group group = (Group) it.next();
                        rolesList.add(group.getPrincipal().getName());
                    }
                    userMap.put("userGroups", groupsList);

                    Collection rules = profiler.getRulesForPrincipal(userPrincipal);
                    HashMap userRules = new HashMap();
                    it = rules.iterator();
                    while (it.hasNext()) {
                        PrincipalRule rule = (PrincipalRule) it.next();
                        userRules.put(rule.getProfilingRule().getId(), rule.getLocatorName());
                    }
                    userMap.put("userRules", userRules);

                    HashMap ingridPrefs = IngridPersistencePrefs.getPrefs(userPrincipal.getName());
                    Set ingridPrefKeys = ingridPrefs.entrySet();
                    it = ingridPrefKeys.iterator();
                    HashMap ingridPrefsMap = new HashMap();
                    while (it.hasNext()) {
                        Map.Entry entry = (Map.Entry) it.next();
                        ingridPrefsMap.put((String) entry.getKey(), ((String) entry.getValue()));
                    }
                    userMap.put("userIngridPrefs", ingridPrefsMap);
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
            String path = request.getParameter("path");
            FileInputStream fis = new FileInputStream(path);
            int x = fis.available();
            byte b[] = new byte[x];
            fis.read(b);
            String xmlData = new String(b);

            Object obj = xstream.fromXML(xmlData);
            if (obj instanceof HashMap) {
                HashMap map = (HashMap) obj;
                ArrayList sqlStatements = new ArrayList();
                ArrayList users = (ArrayList) map.get("users");
                for (int i = 0; i < users.size(); i++) {
                    HashMap userMap = (HashMap) users.get(i);
                    String userName = (String) userMap.get("userPrincipalName");
                    String oldPassword = (String) userMap.get("userPasswordCredential");
                    if (!userManager.userExists(userName) && oldPassword != null && oldPassword.length() > 0) {
                        String password = "geheim";
                        String passwordUpdateSql = "UPDATE security_credential, security_principal SET security_credential.COLUMN_VALUE=\""
                                + oldPassword
                                + "\" WHERE security_principal.FULL_PATH=\"/user/"
                                + userName
                                + "\" AND security_principal.PRINCIPAL_ID=security_credential.PRINCIPAL_ID;";
                        sqlStatements.add(passwordUpdateSql);
                        Map userAttributes = (HashMap) userMap.get("userAttributes");
                        List roles = (ArrayList) userMap.get("userRoles");
                        List groups = (ArrayList) userMap.get("userGroups");
                        Map rules = (HashMap) userMap.get("userRules");
                        ArrayList userPermissions = (ArrayList) userMap.get("userPermissions");
                        HashMap ingridPrefs = (HashMap) userMap.get("userIngridPrefs");

                        try {
                            // create user
                            admin.registerUser(userName, password, roles, groups, userAttributes, rules, null);
                            // activate user
                            userManager.setUserEnabled(userName, true);

                            // create ingrid specific permissions
                            User user = userManager.getUser(userName);
                            Principal userPrincipal = SecurityUtil.getPrincipal(user.getSubject(), UserPrincipal.class);

                            for (int j = 0; j < userPermissions.size(); j++) {
                                Permission permission = (Permission) userPermissions.get(j);
                                if (!permissionManager.permissionExists(permission)) {
                                    permissionManager.addPermission(permission);
                                }
                                permissionManager.grantPermission(userPrincipal, permission);
                            }

                            // create ingrid specific user preferences
                            Set ingridPrefKeys = ingridPrefs.entrySet();
                            Iterator it = ingridPrefKeys.iterator();
                            while (it.hasNext()) {
                                Map.Entry entry = (Map.Entry) it.next();
                                String prefValue = ((String) entry.getValue());
                                IngridPersistencePrefs.setPref(userName, (String) entry.getKey(), xstream
                                        .fromXML(prefValue));
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
