/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets.admin;

import java.io.IOException;
import java.security.Permission;
import java.security.Permissions;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.security.PermissionManager;
import org.apache.jetspeed.security.Role;
import org.apache.jetspeed.security.RoleManager;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.velocity.context.Context;

import de.ingrid.portal.global.UtilsString;
import de.ingrid.portal.portlets.security.SecurityResources;
import de.ingrid.portal.portlets.security.SecurityUtil;
import de.ingrid.portal.security.permission.IngridPartnerPermission;
import de.ingrid.portal.security.permission.IngridPortalPermission;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class UserAdminPortlet extends ContentPortlet {

    private final static Log log = LogFactory.getLog(UserAdminPortlet.class);

    private final static IngridPortalPermission adminIngridPortalPermission = new IngridPortalPermission("admin");

    private final static IngridPortalPermission adminPortalIngridPortalPermission = new IngridPortalPermission(
            "admin.portal");

    private final static IngridPortalPermission adminPortalSTARIngridPortalPermission = new IngridPortalPermission(
            "admin.portal.*");

    private final static IngridPortalPermission adminPortalPartnerIngridPortalPermission = new IngridPortalPermission(
            "admin.portal.partner");

    private final static IngridPortalPermission adminPortalPartnerSTARIngridPortalPermission = new IngridPortalPermission(
            "admin.portal.partner.*");

    private static final String KEY_ENTITIES = "entities";

    private UserManager userManager;

    private RoleManager roleManager;

    private PermissionManager permissionManager;

    /**
     * @see de.ingrid.portal.portlets.admin.ContentPortlet#refreshBrowserState(javax.portlet.PortletRequest)
     */
    protected void refreshBrowserState(PortletRequest request) {
        ContentBrowserState state = getBrowserState(request);
        state.setTotalNumRows(getEntitiesFromSession(request).size());
    }

    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#init(javax.portlet.PortletConfig)
     */
    public void init(PortletConfig config) throws PortletException {
        super.init(config);
        userManager = (UserManager) getPortletContext().getAttribute(CommonPortletServices.CPS_USER_MANAGER_COMPONENT);
        if (null == userManager) {
            throw new PortletException("Failed to find the User Manager on portlet initialization");
        }
        roleManager = (RoleManager) getPortletContext().getAttribute(CommonPortletServices.CPS_ROLE_MANAGER_COMPONENT);
        if (null == roleManager) {
            throw new PortletException("Failed to find the Role Manager on portlet initialization");
        }
        permissionManager = (PermissionManager) getPortletContext().getAttribute(
                CommonPortletServices.CPS_PERMISSION_MANAGER);
        if (permissionManager == null) {
            throw new PortletException("Could not get instance of portal permission manager component");
        }

        // set specific stuff in mother class
        psmlPage = "/ingrid-portal/portal/administration/admin-usermanagement.psml";
        viewDefault = "/WEB-INF/templates/administration/user_admin_browse.vm";
        viewEdit = "/WEB-INF/templates/administration/user_admin_edit.vm";
        viewNew = "/WEB-INF/templates/administration/user_admin_edit.vm";
    
    }

    /**
     * @see de.ingrid.portal.portlets.admin.ContentPortlet#doDefaultView(javax.portlet.RenderRequest)
     */
    protected boolean doDefaultView(RenderRequest request) {
        try {
            // get entities
            List rows = getEntities(request);
            
            String sortColumn = getSortColumn(request, "id");
            boolean ascendingOrder = isAscendingOrder(request);
            orderEntities(rows, sortColumn, ascendingOrder);
            
            // put rows into session
            setEntitiesInSession(request, rows);

            // always refresh !
            refreshBrowserState(request);
            ContentBrowserState state = getBrowserState(request);
            
            int firstRow = state.getFirstRow();
            int maxRows = state.getMaxRows();
            int lastRow = firstRow + maxRows;
            if (lastRow > rows.size()) {
                lastRow = rows.size();
            }
            
            // put to render context
            Context context = getContext(request);
            context.put(CONTEXT_ENTITIES, rows.subList(firstRow, lastRow));
            context.put(CONTEXT_UTILS_STRING, new UtilsString());
            context.put(CONTEXT_BROWSER_STATE, state);
            setDefaultViewPage(viewDefault);
        } catch (Exception ex) {
            if (log.isErrorEnabled()) {
                log.error("Problems processing default view:", ex);
            }
            return false;
        }
        return true;
    }

    private void orderEntities(List rows, String sortColumn, boolean ascendingOrder) {
        Collections.sort(rows, new UserListSortComparator(sortColumn, ascendingOrder));
    }
    
    /**
     * Internal Comparator class.
     *
     * @author joachim@wemove.com
     */
    private class UserListSortComparator implements Comparator {

        private boolean ascendingOrder = true;
        private String sortColumn = "id";
        
        public UserListSortComparator(String sortColumn, boolean ascendingOrder) {
            this.sortColumn = sortColumn;
            this.ascendingOrder = ascendingOrder;
        }
        
        public int compare(Object arg0, Object arg1) {
            Object val1 = ((HashMap)arg0).get(sortColumn);
            Object val2 = ((HashMap)arg1).get(sortColumn);
            if (val1 == null && val2 == null) {
                return 0;
            }
            if ((val1 == null && val2 != null)) {
                if (ascendingOrder) {
                    return -1;
                } else {
                    return 1;
                }
            }
            if ((val1 != null && val2 == null)) {
                if (ascendingOrder) {
                    return 1;
                } else {
                    return -1;
                }
            }
            if (val1 instanceof String && val2 instanceof String) {
                if (ascendingOrder) {
                    return ((String)val1).compareTo((String)val2);
                } else {
                    return -1 * ((String)val1).compareTo((String)val2);
                }
            }
            if (val1 instanceof Integer && val2 instanceof Integer) {
                if (ascendingOrder) {
                    return ((Integer)val1).compareTo((Integer)val2);
                } else {
                    return -1 * ((Integer)val1).compareTo((Integer)val2);
                }
            }
            return 0;
        }
        
    }
    

    /**
     * Merge role permissions with user permissions
     * 
     * @param p
     *            The Principal of the user to merge the role permission with.
     * @return The merged Permissions.
     */
    private Permissions getMergedPermissions(Principal p) {
        Permissions result = permissionManager.getPermissions(p);
        try {
            Collection roles = roleManager.getRolesForUser(p.getName());
            Iterator roleIterator = roles.iterator();
            while (roleIterator.hasNext()) {
                // check for role based permission to show the user
                Role role = (Role) roleIterator.next();
                Permissions rp = permissionManager.getPermissions(role.getPrincipal());
                Enumeration en = rp.elements();
                while (en.hasMoreElements()) {
                    result.add((Permission) en.nextElement());
                }
            }
        } catch (SecurityException e) {
            if (log.isErrorEnabled()) {
                log.error("Error merging roles of principal '" + p.getName() + "'!", e);
            }
        }
        return result;
    }

    /**
     * Checks for the permission / role combination to include a user.
     * 
     * Only users with 'user' as ONLY role will be included for admins with
     * permission "admin.portal";
     * 
     * @param authUserPermissions
     * @param userRoles
     * @return
     */
    public static boolean includeUserByRole(Permissions authUserPermissions, Collection userRoles) {
        // for permission "admin.portal", include users with role "user"
        if (authUserPermissions.implies(adminPortalIngridPortalPermission)) {
            Iterator it = userRoles.iterator();
            // check for user with no roles, exit
            if (!it.hasNext()) {
                return false;
            }
            // check for users with roles others than 'user' -> exit if found
            while (it.hasNext()) {
                Role r = (Role) it.next();
                if (!r.getPrincipal().getName().equals("user")) {
                    return false;
                }
            }
            // user has only one role 'user'
            return true;
        }
        // no permission
        return false;
    }

    /**
     * Checks for the right permission condition to include a user.
     * 
     * @param authUserPermissions
     *            The Permissions of the authenticated user.
     * @param userPermissions
     *            The Permissions of the user to test.
     * @return true if the Permissions of the auth user allow to edit the user
     *         with userPermissions, false if not.
     */
    public static boolean includeUserByPermission(Permissions authUserPermissions, Permissions userPermissions) {
        Permission p;
        Enumeration en;

        // WE ARE KING, STEP ASIDE!
        if (authUserPermissions.implies(adminIngridPortalPermission)) {
            return true;
        }
        // for permission "admin.portal", include users with permission
        // "admin.portal.*"
        if (authUserPermissions.implies(adminPortalIngridPortalPermission)) {
            en = userPermissions.elements();
            while (en.hasMoreElements()) {
                if (adminPortalSTARIngridPortalPermission.implies((Permission) en.nextElement())) {
                    return true;
                }
            }
        }
        // for permission "admin.portal.partner", include with permission
        // "admin.portal.partner.*" AND IngridPartnerPermission("partner",
        // <partner_of_auth_user>)
        if (authUserPermissions.implies(adminPortalPartnerIngridPortalPermission)) {
            // get the partner from the auth users permission
            ArrayList authUserPartner = new ArrayList();
            en = authUserPermissions.elements();
            while (en.hasMoreElements()) {
                p = (Permission) en.nextElement();
                if (p instanceof IngridPartnerPermission) {
                    authUserPartner.addAll(((IngridPartnerPermission) p).getPartners());
                }
            }
            // add users that imply admin.portal.provider.* AND
            // IngridPartnerPermission(partner, <partner_of_auth_user>)
            boolean implyPermission = false;
            boolean implyPartner = false;
            en = userPermissions.elements();
            Permission userPermission;
            while (en.hasMoreElements()) {
                userPermission = (Permission) en.nextElement();
                // check for permission IngridPartnerPermission(partner,
                // <partner_of_auth_user>)
                if (userPermission instanceof IngridPartnerPermission) {
                    ArrayList userPartners = ((IngridPartnerPermission) userPermission).getPartners();
                    for (int i = 0; i < authUserPartner.size(); i++) {
                        for (int j = 0; j < userPartners.size(); j++) {
                            if (((String) authUserPartner.get(i)).equals((String) userPartners.get(j))) {
                                implyPartner = true;
                                break;
                            }
                        }
                        if (implyPartner) {
                            break;
                        }
                    }
                }
                // check for permission "admin.portal.provider.*"
                if (adminPortalPartnerSTARIngridPortalPermission.implies(userPermission)) {
                    implyPermission = true;
                }
                if (implyPartner && implyPermission) {
                    return true;
                }
            }
        }
        return false;
    }

    protected List getEntities(RenderRequest request) {
        // get data from database
        ArrayList rows = new ArrayList();

        try {
            // get current user
            Principal authUserPrincipal = request.getUserPrincipal();
            Permissions authUserPermissions = getMergedPermissions(authUserPrincipal);

            // iterate over all users
            Iterator users = userManager.getUsers("");
            while (users.hasNext()) {
                User user = (User) users.next();
                Principal userPrincipal = SecurityUtil.getPrincipal(user.getSubject(), UserPrincipal.class);
                Permissions userPermissions = getMergedPermissions(userPrincipal);

                // get the user roles
                Collection userRoles = roleManager.getRolesForUser(userPrincipal.getName());

                boolean addUser = includeUserByPermission(authUserPermissions, userPermissions)
                        || includeUserByRole(authUserPermissions, userRoles);

                if (addUser) {
                    HashMap record = new HashMap();
                    record.put("id", userPrincipal.getName());
                    record.put("firstName", user.getUserAttributes().get(SecurityResources.USER_NAME_GIVEN, ""));
                    record.put("lastName", user.getUserAttributes().get(SecurityResources.USER_NAME_FAMILY, ""));
                    record.put("email", user.getUserAttributes().get("user.business-info.online.email", ""));
                    String roleString = "";
                    Iterator it = userRoles.iterator();
                    while (it.hasNext()) {
                        Role r = (Role) it.next();
                        roleString = roleString.concat(r.getPrincipal().getName());
                        if (it.hasNext()) {
                            roleString = roleString.concat(", ");
                        }
                    }
                    record.put("roles", roleString);
                    record.put("confirmed", "Y");
                    rows.add(record);
                }
            }
        } catch (SecurityException e) {
            if (log.isErrorEnabled()) {
                log.error("Error getting entities!", e);
            }
        }
        refreshBrowserState(request);
        return rows;
    }
    
    /**
     * @see de.ingrid.portal.portlets.admin.ContentPortlet#doSave(javax.portlet.ActionRequest)
     */
    protected void doSave(ActionRequest request) {
        // TODO Auto-generated method stub

    }

    /**
     * @see de.ingrid.portal.portlets.admin.ContentPortlet#doUpdate(javax.portlet.ActionRequest)
     */
    protected void doUpdate(ActionRequest request) {
        // TODO Auto-generated method stub

    }

    /**
     * @see de.ingrid.portal.portlets.admin.ContentPortlet#doDelete(javax.portlet.ActionRequest)
     */
    protected void doDelete(ActionRequest request) {
        // TODO Auto-generated method stub

    }

    /**
     * @see de.ingrid.portal.portlets.admin.ContentPortlet#processAction(javax.portlet.ActionRequest,
     *      javax.portlet.ActionResponse)
     */
    public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {
        // TODO Auto-generated method stub
        super.processAction(request, response);
    }

    /**
     * @see de.ingrid.portal.portlets.admin.ContentPortlet#doEdit(javax.portlet.RenderRequest)
     */
    protected boolean doEdit(RenderRequest request) {
        // TODO Auto-generated method stub
        return super.doEdit(request);
    }

    /**
     * @see de.ingrid.portal.portlets.admin.ContentPortlet#doNew(javax.portlet.RenderRequest)
     */
    protected boolean doNew(RenderRequest request) {
        // TODO Auto-generated method stub
        return super.doNew(request);
    }

    protected Object[] getDBEntities(ActionRequest request) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Get the entities of the browser portlet.
     * Only ONE entities object per portlet (PORTLET_SCOPE).
     * @param request
     * @return
     */
    static protected List getEntitiesFromSession(PortletRequest request) {
        List entities = (List) request.getPortletSession().getAttribute(KEY_ENTITIES,
                PortletSession.PORTLET_SCOPE);
        if (entities == null) {
            entities = new ArrayList();
            setEntitiesInSession(request, entities);
        }
        return entities;
    }
    
    /**
     * Set the entities of the browser portlet.
     * @param request
     * @param state
     */
    static protected void setEntitiesInSession(PortletRequest request, List entities) {
        request.getPortletSession().setAttribute(KEY_ENTITIES, entities, PortletSession.PORTLET_SCOPE);
    }

}
