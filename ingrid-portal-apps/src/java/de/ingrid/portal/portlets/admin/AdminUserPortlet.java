/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets.admin;

import java.io.File;
import java.io.IOException;
import java.security.Permission;
import java.security.Permissions;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.security.auth.Subject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.administration.PortalAdministration;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.FolderNotFoundException;
import org.apache.jetspeed.om.folder.InvalidFolderException;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.document.NodeException;
import org.apache.jetspeed.profiler.Profiler;
import org.apache.jetspeed.security.GroupManager;
import org.apache.jetspeed.security.InvalidPasswordException;
import org.apache.jetspeed.security.PasswordAlreadyUsedException;
import org.apache.jetspeed.security.PermissionManager;
import org.apache.jetspeed.security.Role;
import org.apache.jetspeed.security.RoleManager;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.velocity.context.Context;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.forms.ActionForm;
import de.ingrid.portal.forms.AdminUserForm;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.global.UtilsDB;
import de.ingrid.portal.global.UtilsString;
import de.ingrid.portal.portlets.security.SecurityResources;
import de.ingrid.portal.portlets.security.SecurityUtil;
import de.ingrid.portal.security.permission.IngridPartnerPermission;
import de.ingrid.portal.security.permission.IngridPortalPermission;
import de.ingrid.portal.security.permission.IngridProviderPermission;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class AdminUserPortlet extends ContentPortlet {

    private final static Log log = LogFactory.getLog(AdminUserPortlet.class);

    private final static IngridPortalPermission adminIngridPortalPermission = new IngridPortalPermission("admin");

    private final static IngridPortalPermission adminPortalIngridPortalPermission = new IngridPortalPermission(
            "admin.portal");

    private final static IngridPortalPermission adminPortalSTARIngridPortalPermission = new IngridPortalPermission(
            "admin.portal.*");

    private final static IngridPortalPermission adminPortalPartnerIngridPortalPermission = new IngridPortalPermission(
            "admin.portal.partner");

    private final static IngridPortalPermission adminPortalPartnerSTARIngridPortalPermission = new IngridPortalPermission(
            "admin.portal.partner.*");

    private final static IngridPortalPermission adminPortalPartnerProviderIndexIngridPortalPermission = new IngridPortalPermission(
            "admin.portal.partner.provider.index");

    private final static IngridPortalPermission adminPortalPartnerProviderCatalogIngridPortalPermission = new IngridPortalPermission(
            "admin.portal.partner.provider.catalog");

    private static final String KEY_ENTITIES = "entities";

    // Init Parameters
    private static final String IP_ROLES = "roles"; // comma separated

    private static final String IP_GROUPS = "groups"; // comma separated

    private static final String IP_RETURN_URL = "returnURL";

    private static final String IP_RULES_NAMES = "rulesNames";

    private static final String IP_RULES_VALUES = "rulesValues";

    private static final String IP_EMAIL_TEMPLATE = "emailTemplate";

    private PortalAdministration admin;

    private UserManager userManager;

    private RoleManager roleManager;

    private GroupManager groupManager;

    private PermissionManager permissionManager;

    private PageManager pageManager;

    private Profiler profiler;

    /** email template to use for merging */
    private String emailTemplate;

    /** roles */
    private List roles;

    /** groups */
    private List groups;

    /** profile rules */
    private Map rules;

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

        // roles
        this.roles = getInitParameterList(config, IP_ROLES);

        // groups
        this.groups = getInitParameterList(config, IP_GROUPS);

        // rules (name,value pairs)
        List names = getInitParameterList(config, IP_RULES_NAMES);
        List values = getInitParameterList(config, IP_RULES_VALUES);
        rules = new HashMap();
        for (int ix = 0; ix < ((names.size() < values.size()) ? names.size() : values.size()); ix++) {
            rules.put(names.get(ix), values.get(ix));
        }

        this.emailTemplate = config.getInitParameter(IP_EMAIL_TEMPLATE);

        // set specific stuff in mother class
        psmlPage = "/ingrid-portal/portal/administration/admin-usermanagement.psml";
        viewDefault = "/WEB-INF/templates/administration/admin_user_browser.vm";
        viewEdit = "/WEB-INF/templates/administration/admin_user_edit.vm";
        viewNew = "/WEB-INF/templates/administration/admin_user_new.vm";

    }

    /**
     * @see de.ingrid.portal.portlets.admin.ContentPortlet#doView(javax.portlet.RenderRequest,
     *      javax.portlet.RenderResponse)
     */
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        super.doView(request, response);
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
            Object val1 = ((HashMap) arg0).get(sortColumn);
            Object val2 = ((HashMap) arg1).get(sortColumn);
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
                    return ((String) val1).compareTo((String) val2);
                } else {
                    return -1 * ((String) val1).compareTo((String) val2);
                }
            }
            if (val1 instanceof Integer && val2 instanceof Integer) {
                if (ascendingOrder) {
                    return ((Integer) val1).compareTo((Integer) val2);
                } else {
                    return -1 * ((Integer) val1).compareTo((Integer) val2);
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
            ArrayList authUserPartner = getPartnersFromPermissions(authUserPermissions);
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
                    String userPartner = ((IngridPartnerPermission) userPermission).getPartner();
                    for (int i = 0; i < authUserPartner.size(); i++) {
                        if (((String) authUserPartner.get(i)).equals(userPartner)) {
                            implyPartner = true;
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
        AdminUserForm f = (AdminUserForm) Utils.getActionForm(request, AdminUserForm.SESSION_KEY, AdminUserForm.class);
        f.clearErrors();
        f.populate(request);
        if (!f.validate()) {
            return;
        }

        try {

            String userName = f.getInput(AdminUserForm.FIELD_ID);
            String password = f.getInput(AdminUserForm.FIELD_PASSWORD_NEW);

            // check if the user name exists
            boolean userIdExistsFlag = true;
            try {
                userManager.getUser(userName);
            } catch (SecurityException e) {
                userIdExistsFlag = false;
            }
            if (userIdExistsFlag) {
                f.setError(AdminUserForm.FIELD_ID, "account.create.error.user.exists");
                return;
            }

            Map userAttributes = new HashMap();
            // we'll assume that these map back to PLT.D values
            userAttributes.put("user.name.prefix", f.getInput(AdminUserForm.FIELD_SALUTATION));
            userAttributes.put("user.name.given", f.getInput(AdminUserForm.FIELD_FIRSTNAME));
            userAttributes.put("user.name.family", f.getInput(AdminUserForm.FIELD_LASTNAME));
            userAttributes.put("user.business-info.online.email", f.getInput(AdminUserForm.FIELD_EMAIL));
            userAttributes.put("user.business-info.postal.street", f.getInput(AdminUserForm.FIELD_STREET));
            userAttributes.put("user.business-info.postal.postalcode", f.getInput(AdminUserForm.FIELD_POSTALCODE));
            userAttributes.put("user.business-info.postal.city", f.getInput(AdminUserForm.FIELD_CITY));

            // theses are not PLT.D values but ingrid specifics
            userAttributes.put("user.custom.ingrid.user.age.group", f.getInput(AdminUserForm.FIELD_AGE));
            userAttributes.put("user.custom.ingrid.user.attention.from", f.getInput(AdminUserForm.FIELD_ATTENTION));
            userAttributes.put("user.custom.ingrid.user.interest", f.getInput(AdminUserForm.FIELD_INTEREST));
            userAttributes.put("user.custom.ingrid.user.profession", f.getInput(AdminUserForm.FIELD_PROFESSION));
            userAttributes.put("user.custom.ingrid.user.subscribe.newsletter", f
                    .getInput(AdminUserForm.FIELD_SUBSCRIBE_NEWSLETTER));

            // generate login id
            String confirmId = Utils.getMD5Hash(userName.concat(password).concat(
                    Long.toString(System.currentTimeMillis())));
            userAttributes.put("user.custom.ingrid.user.confirmid", confirmId);

            admin.registerUser(userName, password, this.roles, this.groups, userAttributes, rules, null);

            userManager.setUserEnabled(userName, true);

            IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                    request.getLocale()));

            HashMap userInfo = new HashMap(userAttributes);
            // map coded stuff
            String salutationFull = messages.getString("account.edit.salutation.option", (String) userInfo
                    .get("user.name.prefix"));
            userInfo.put("user.custom.ingrid.user.salutation.full", salutationFull);
            userInfo.put("login", userName);
            userInfo.put("password", password);

            String language = request.getLocale().getLanguage();
            String localizedTemplatePath = this.emailTemplate;
            if (localizedTemplatePath == null) {
                log.error("email template not available");
                f.setError("nofield", "account.created.problems.email");
                return;
            }
            int period = localizedTemplatePath.lastIndexOf(".");
            if (period > 0) {
                String fixedTempl = localizedTemplatePath.substring(0, period) + "_" + language + "."
                        + localizedTemplatePath.substring(period + 1);
                if (new File(getPortletContext().getRealPath(fixedTempl)).exists()) {
                    this.emailTemplate = fixedTempl;
                    localizedTemplatePath = fixedTempl;
                }
            }

            if (localizedTemplatePath == null) {
                log.error("email template not available");
                f.setError("nofield", "account.created.problems.email");
                return;
            }

            String emailSubject = messages.getString("account.create.confirmation.email.subject");

            String from = PortalConfig.getInstance().getString(PortalConfig.EMAIL_REGISTRATION_CONFIRMATION_SENDER,
                    "foo@bar.com");
            String to = (String) userInfo.get("user.business-info.online.email");
            String text = Utils.mergeTemplate(getPortletConfig(), userInfo, "map", localizedTemplatePath);
            if (Utils.sendEmail(from, emailSubject, new String[] { to }, text, null)) {
                f.addMessage("account.created.title");
            } else {
                f.setError("", "account.created.problems.email");
                return;
            }

        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Problems creating new user.", e);
            }
        }

    }

    /**
     * @see de.ingrid.portal.portlets.admin.ContentPortlet#doUpdate(javax.portlet.ActionRequest)
     */
    protected void doUpdate(ActionRequest request) {
        AdminUserForm f = (AdminUserForm) Utils.getActionForm(request, AdminUserForm.SESSION_KEY, AdminUserForm.class);
        f.clearErrors();
        f.populate(request);
        if (!f.validate()) {
            return;
        }

        try {

            String userName = f.getInput(AdminUserForm.FIELD_ID);
            User user = null;
            try {
                user = userManager.getUser(userName);
            } catch (JetspeedException e) {
                f.setError("", "account.edit.error.user.notfound");
                log.error("Error getting current user.", e);
                return;
            }

            if (f.getInput(AdminUserForm.FIELD_TAB).equals("1")) {

                Preferences userAttributes = user.getUserAttributes();
                userAttributes.put("user.name.prefix", f.getInput(AdminUserForm.FIELD_SALUTATION));
                userAttributes.put("user.name.given", f.getInput(AdminUserForm.FIELD_FIRSTNAME));
                userAttributes.put("user.name.family", f.getInput(AdminUserForm.FIELD_LASTNAME));
                userAttributes.put("user.business-info.online.email", f.getInput(AdminUserForm.FIELD_EMAIL));
                userAttributes.put("user.business-info.postal.street", f.getInput(AdminUserForm.FIELD_STREET));
                userAttributes.put("user.business-info.postal.postalcode", f.getInput(AdminUserForm.FIELD_POSTALCODE));
                userAttributes.put("user.business-info.postal.city", f.getInput(AdminUserForm.FIELD_CITY));

                // theses are not PLT.D values but ingrid specifics
                userAttributes.put("user.custom.ingrid.user.age.group", f.getInput(AdminUserForm.FIELD_AGE));
                userAttributes.put("user.custom.ingrid.user.attention.from", f.getInput(AdminUserForm.FIELD_ATTENTION));
                userAttributes.put("user.custom.ingrid.user.interest", f.getInput(AdminUserForm.FIELD_INTEREST));
                userAttributes.put("user.custom.ingrid.user.profession", f.getInput(AdminUserForm.FIELD_PROFESSION));
                userAttributes.put("user.custom.ingrid.user.subscribe.newsletter", f
                        .getInput(AdminUserForm.FIELD_SUBSCRIBE_NEWSLETTER));
                try {
                    // update password only if a old password was provided
                    String oldPassword = f.getInput(AdminUserForm.FIELD_PASSWORD_OLD);
                    if (oldPassword != null && oldPassword.length() > 0) {
                        userManager.setPassword(userName, f.getInput(AdminUserForm.FIELD_PASSWORD_OLD), f
                                .getInput(AdminUserForm.FIELD_PASSWORD_NEW));
                    }
                } catch (PasswordAlreadyUsedException e) {
                    f.setError(AdminUserForm.FIELD_PASSWORD_NEW, "account.edit.error.password.in.use");
                    return;
                } catch (InvalidPasswordException e) {
                    f.setError(AdminUserForm.FIELD_PASSWORD_OLD, "account.edit.error.wrong.password");
                    return;
                } catch (SecurityException e) {
                    f.setError("", "account.edit.error.wrong.password");
                    return;
                }
            } else if (f.getInput(AdminUserForm.FIELD_TAB).equals("2")) {
                Principal userPrincipal = SecurityUtil.getPrincipal(user.getSubject(), UserPrincipal.class);

                // update the admin.portal permission
                if (f.hasInput(AdminUserForm.FIELD_CHK_ADMIN_PORTAL)) {
                    permissionManager.grantPermission(userPrincipal, adminPortalIngridPortalPermission);
                } else {
                    permissionManager.revokePermission(userPrincipal, adminPortalIngridPortalPermission);
                }

                // update the admin.portal.partner permission
                if (f.hasInput(AdminUserForm.FIELD_CHK_ADMIN_PARTNER)) {
                    permissionManager.grantPermission(userPrincipal, adminPortalPartnerIngridPortalPermission);
                } else {
                    permissionManager.revokePermission(userPrincipal, adminPortalPartnerIngridPortalPermission);
                }

                // update the admin.portal.partner.provider.index permission
                if (f.hasInput(AdminUserForm.FIELD_CHK_ADMIN_INDEX)) {
                    permissionManager.grantPermission(userPrincipal,
                            adminPortalPartnerProviderIndexIngridPortalPermission);
                } else {
                    permissionManager.revokePermission(userPrincipal,
                            adminPortalPartnerProviderIndexIngridPortalPermission);
                }

                // update the admin.portal.partner.provider.catalog permission
                if (f.hasInput(AdminUserForm.FIELD_CHK_ADMIN_CATALOG)) {
                    permissionManager.grantPermission(userPrincipal,
                            adminPortalPartnerProviderCatalogIngridPortalPermission);
                } else {
                    permissionManager.revokePermission(userPrincipal,
                            adminPortalPartnerProviderIndexIngridPortalPermission);
                }

                // remove all IngridPartnerPermissions, they will be reset if a
                // provider or partner permission was granted
                revokePermissionsByClass(userPrincipal, IngridPartnerPermission.class);
                if (f.hasInput(AdminUserForm.FIELD_CHK_ADMIN_PARTNER)
                        || f.hasInput(AdminUserForm.FIELD_CHK_ADMIN_CATALOG)
                        || f.hasInput(AdminUserForm.FIELD_CHK_ADMIN_INDEX)) {
                    // add IngridPartnerPermissions for specified partner
                    createAndGrantPermission(userPrincipal, new IngridPartnerPermission("partner."
                            + f.getInput(AdminUserForm.FIELD_PARTNER)));
                }

                // remove all IngridProviderPermissions, they will be reset if a
                // provider permission was granted
                revokePermissionsByClass(userPrincipal, IngridProviderPermission.class);
                // set providers if any provider permission was granted
                if (f.hasInput(AdminUserForm.FIELD_CHK_ADMIN_CATALOG)
                        || f.hasInput(AdminUserForm.FIELD_CHK_ADMIN_INDEX)) {
                    // add IngridProviderPermissions for specified partner
                    String[] providers = f.getInputAsArray("provider");
                    for (int i = 0; i < providers.length; i++) {
                        createAndGrantPermission(userPrincipal,
                                new IngridProviderPermission("provider." + providers[i]));
                    }
                }
            }
            f.addMessage("account.edited.title");

        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Problems saving user.", e);
            }
        }
    }

    /**
     * Remove all permissions of a principal, of a specific class.
     * 
     * @param principal
     * @param permissionClass
     */
    private void revokePermissionsByClass(Principal principal, Class permissionClass) {
        try {
            Permissions partnerPermissions = permissionManager.getPermissions(principal);
            Enumeration en = partnerPermissions.elements();
            while (en.hasMoreElements()) {
                Permission p = (Permission) en.nextElement();
                if (permissionClass.isInstance(p)) {
                    permissionManager.revokePermission(principal, p);
                }
            }
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Problems revoking Permission by Class (" + permissionClass + ").", e);
            }
        }
    }

    private void createAndGrantPermission(Principal principal, Permission permission) {
        try {
            if (!permissionManager.permissionExists(permission)) {
                permissionManager.addPermission(permission);
            }
            permissionManager.grantPermission(principal, permission);
        } catch (SecurityException e) {
            if (log.isErrorEnabled()) {
                log.error("Problems create or grant permission (" + permission.toString() + ").", e);
            }
        }
    }

    /**
     * @see de.ingrid.portal.portlets.admin.ContentPortlet#doDelete(javax.portlet.ActionRequest)
     */
    protected void doDelete(ActionRequest request) {

        String[] ids = (String[]) getDBEntities(request);
        for (int i = 0; i < ids.length; i++) {
            try {

                final String innerFolder = Folder.USER_FOLDER;
                final String innerUserName = ids[i];
                final PageManager innerPageManager = pageManager;
                User powerUser = userManager.getUser("admin");
                JetspeedException pe = (JetspeedException) Subject.doAsPrivileged(powerUser.getSubject(),
                        new PrivilegedAction() {
                            public Object run() {
                                try {
                                    // remove user's home folder
                                    Folder f = innerPageManager.getFolder(innerFolder + innerUserName);
                                    innerPageManager.removeFolder(f);

                                    return null;
                                } catch (FolderNotFoundException e1) {
                                    return e1;
                                } catch (InvalidFolderException e1) {
                                    return e1;
                                } catch (NodeException e1) {
                                    return e1;
                                }
                            }
                        }, null);
                if (pe == null) {
                    // remove user creation and cascade roles, groups, etc
                    try {
                        if (userManager.getUser(ids[i]) != null) {
                            userManager.removeUser(ids[i]);
                        }
                    } catch (Exception e) {
                        log.error("Registration Error: Failed to remove user " + ids[i]);
                    }
                } else {
                    log.error("Registration Error: Failed to remove user folders for " + ids[i] + ", " + pe.toString());
                    throw pe;
                }
            } catch (Exception e) {
                if (log.isErrorEnabled()) {
                    log.error("Problems deleting user (" + ids[i] + ").", e);
                }
            }
        }
    }

    /**
     * @see de.ingrid.portal.portlets.admin.ContentPortlet#processAction(javax.portlet.ActionRequest,
     *      javax.portlet.ActionResponse)
     */
    public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {

        String action = getAction(request);

        if (request.getParameter(PARAMV_ACTION_DB_DO_UPDATE) != null) {
            // call sub method
            doUpdate(request);
            // reset the sction to edit, to show the edit screen and collect all
            // necessary data
            response.setRenderParameter(Settings.PARAM_ACTION, PARAMV_ACTION_DO_EDIT);
            response.setRenderParameter("cmd", "action processed");
        } else if (request.getParameter(PARAMV_ACTION_DB_DO_SAVE) != null) {
            // call sub method
            doSave(request);
            // reset the sction to edit, to show the edit screen and collect all
            // necessary data
            response.setRenderParameter(Settings.PARAM_ACTION, PARAMV_ACTION_DO_NEW);
            response.setRenderParameter("cmd", "action processed");

            // check for cancel to avoid an unnecesarry "doChangeTab" action
        } else if (request.getParameter(PARAMV_ACTION_DB_DO_CANCEL) != null) {
            response.setRenderParameter(PARAM_NOT_INITIAL, Settings.MSGV_TRUE);
            return;
        } else if (action != null && action.equals("doChangeTab")) {
            AdminUserForm f = (AdminUserForm) Utils.getActionForm(request, AdminUserForm.SESSION_KEY,
                    AdminUserForm.class);
            // save the tab
            f.setInput(AdminUserForm.FIELD_TAB, request.getParameter("tab"));
            // save the id of the edited user, to keep the reference
            f.setInput(AdminUserForm.FIELD_ID, request.getParameter("id"));
            // reset the sction to edit, to show the edit screen and collect all
            // necessary data
            response.setRenderParameter(Settings.PARAM_ACTION, PARAMV_ACTION_DO_EDIT);
            response.setRenderParameter("cmd", "action processed");

        } else {
            super.processAction(request, response);
        }
    }

    /**
     * @see de.ingrid.portal.portlets.admin.ContentPortlet#doEdit(javax.portlet.RenderRequest)
     */
    protected boolean doEdit(RenderRequest request) {

        try {

            AdminUserForm f = (AdminUserForm) Utils.getActionForm(request, AdminUserForm.SESSION_KEY,
                    AdminUserForm.class);

            Context context = getContext(request);
            context.put(CONTEXT_MODE, CONTEXTV_MODE_EDIT);
            context.put(CONTEXT_UTILS_STRING, new UtilsString());

            String cmd = request.getParameter("cmd");
            if (cmd == null) {
                f.clear();
                fillEditFormFromStorage(request, f);
            }

            context.put("actionForm", f);

            context.put("partnerlist", UtilsDB.getPartners());
            if (f.hasInput(AdminUserForm.FIELD_PARTNER)) {
                context
                        .put("providerlist", UtilsDB
                                .getProvidersFromPartnerKey(f.getInput(AdminUserForm.FIELD_PARTNER)));
            }

            setDefaultViewPage(viewEdit);
            return true;
        } catch (Exception ex) {
            if (log.isErrorEnabled()) {
                log.error("Problems fetching entities to edit:", ex);
            }
        }
        return false;
    }

    /**
     * @see de.ingrid.portal.portlets.admin.ContentPortlet#doNew(javax.portlet.RenderRequest)
     */
    protected boolean doNew(RenderRequest request) {
        AdminUserForm f = (AdminUserForm) Utils.getActionForm(request, AdminUserForm.SESSION_KEY, AdminUserForm.class);

        Context context = getContext(request);
        context.put(CONTEXT_MODE, CONTEXTV_MODE_NEW);
        context.put(CONTEXT_UTILS_STRING, new UtilsString());

        String cmd = request.getParameter("cmd");
        if (cmd == null) {
            f.clear();
        }

        context.put("actionForm", f);

        setDefaultViewPage(viewNew);
        return true;
    }

    protected Object[] getDBEntities(PortletRequest request) {
        return getIds(request);
    }

    /**
     * Get the entities of the browser portlet. Only ONE entities object per
     * portlet (PORTLET_SCOPE).
     * 
     * @param request
     * @return
     */
    static protected List getEntitiesFromSession(PortletRequest request) {
        List entities = (List) request.getPortletSession().getAttribute(KEY_ENTITIES, PortletSession.PORTLET_SCOPE);
        if (entities == null) {
            entities = new ArrayList();
            setEntitiesInSession(request, entities);
        }
        return entities;
    }

    /**
     * Set the entities of the browser portlet.
     * 
     * @param request
     * @param state
     */
    static protected void setEntitiesInSession(PortletRequest request, List entities) {
        request.getPortletSession().setAttribute(KEY_ENTITIES, entities, PortletSession.PORTLET_SCOPE);
    }

    private static ArrayList getPartnersFromPermissions(Permissions permissions) {
        ArrayList result = new ArrayList();

        Enumeration en = permissions.elements();
        while (en.hasMoreElements()) {
            Permission p = (Permission) en.nextElement();
            if (p instanceof IngridPartnerPermission) {
                result.add(((IngridPartnerPermission) p).getPartner());
            }
        }
        return result;
    }

    private static ArrayList getProvidersFromPermissions(Permissions permissions) {
        ArrayList result = new ArrayList();

        Enumeration en = permissions.elements();
        while (en.hasMoreElements()) {
            Permission p = (Permission) en.nextElement();
            if (p instanceof IngridProviderPermission) {
                result.add(((IngridProviderPermission) p).getProvider());
            }
        }
        return result;
    }

    private static String getLayoutType(Permissions editorPermissions) {
        String result = null;
        if (editorPermissions.implies(new IngridPortalPermission("admin"))) {
            result = "admin";
        } else if (editorPermissions.implies(new IngridPortalPermission("admin.portal"))) {
            result = "admin.portal";
        } else if (editorPermissions.implies(new IngridPortalPermission("admin.portal.partner"))) {
            result = "admin.portal.partner";
        } else {
            result = "admin.user";
        }
        return result;
    }

    private static HashMap getIngridPortalPermissionHash(Permissions permissions) {
        HashMap result = new HashMap();
        Enumeration en = permissions.elements();
        while (en.hasMoreElements()) {
            Permission p = (Permission) en.nextElement();
            if (p instanceof IngridPortalPermission) {
                result.put(p.getName(), "1");
            }
        }
        return result;
    }

    private void fillEditFormFromStorage(RenderRequest request, ActionForm f) {

        try {
            String[] ids = getIds(request);
            String editId = ids[0];
            List l = getEntitiesFromSession(request);

            String id = null;
            boolean entityFound = false;
            for (int i = 0; i < l.size(); i++) {
                HashMap h = (HashMap) l.get(i);
                id = (String) h.get("id");
                if (id.equals(editId)) {
                    entityFound = true;
                    break;
                }
            }

            if (!entityFound) {
                return;
            }

            User user = userManager.getUser(editId);

            // put all user attributes into form
            Preferences userAttributes = user.getUserAttributes();
            f.setInput(AdminUserForm.FIELD_ID, editId);
            f.setInput(AdminUserForm.FIELD_SALUTATION, userAttributes.get("user.name.prefix", ""));
            f.setInput(AdminUserForm.FIELD_FIRSTNAME, userAttributes.get("user.name.given", ""));
            f.setInput(AdminUserForm.FIELD_LASTNAME, userAttributes.get("user.name.family", ""));
            f.setInput(AdminUserForm.FIELD_EMAIL, userAttributes.get("user.business-info.online.email", ""));
            f.setInput(AdminUserForm.FIELD_STREET, userAttributes.get("user.business-info.postal.street", ""));
            f.setInput(AdminUserForm.FIELD_POSTALCODE, userAttributes.get("user.business-info.postal.postalcode", ""));
            f.setInput(AdminUserForm.FIELD_CITY, userAttributes.get("user.business-info.postal.city", ""));

            f.setInput(AdminUserForm.FIELD_AGE, userAttributes.get("user.custom.ingrid.user.age.group", ""));
            f.setInput(AdminUserForm.FIELD_ATTENTION, userAttributes.get("user.custom.ingrid.user.attention.from", ""));
            f.setInput(AdminUserForm.FIELD_INTEREST, userAttributes.get("user.custom.ingrid.user.interest", ""));
            f.setInput(AdminUserForm.FIELD_PROFESSION, userAttributes.get("user.custom.ingrid.user.profession", ""));
            f.setInput(AdminUserForm.FIELD_SUBSCRIBE_NEWSLETTER, userAttributes.get(
                    "user.custom.ingrid.user.subscribe.newsletter", ""));

            // get permissions of the user
            Principal userPrincipal = SecurityUtil.getPrincipal(user.getSubject(), UserPrincipal.class);
            Permissions userPermissions = getMergedPermissions(userPrincipal);

            // get partner from permissions, set to context
            List userPartners = getPartnersFromPermissions(userPermissions);
            List userProviders = getProvidersFromPermissions(userPermissions);

            if (userPartners.size() > 0) {
                f.setInput(AdminUserForm.FIELD_PARTNER, (String) userPartners.get(0));
                f.setInput(AdminUserForm.FIELD_PARTNER_NAME, UtilsDB.getPartnerFromKey((String) userPartners.get(0)));
            }
            if (userProviders.size() > 0) {
                f.setInput(AdminUserForm.FIELD_PROVIDER, (String[]) userProviders.toArray(new String[] {}));
            }

            // add portal permissions (IngridPortalPermissions) to the context
            HashMap portalPermissions = getIngridPortalPermissionHash(userPermissions);
            f.setInput(AdminUserForm.FIELD_CHK_ADMIN_PORTAL, (String) portalPermissions.get("admin.portal"));
            f.setInput(AdminUserForm.FIELD_CHK_ADMIN_PARTNER, (String) portalPermissions.get("admin.portal.partner"));
            f.setInput(AdminUserForm.FIELD_CHK_ADMIN_INDEX, (String) portalPermissions
                    .get("admin.portal.partner.provider.index"));
            f.setInput(AdminUserForm.FIELD_CHK_ADMIN_CATALOG, (String) portalPermissions
                    .get("admin.portal.partner.provider.catalog"));

            // get current user
            Principal authUserPrincipal = request.getUserPrincipal();
            Permissions authUserPermissions = getMergedPermissions(authUserPrincipal);

            // set type of layout
            String layoutType = getLayoutType(authUserPermissions);
            f.setInput(AdminUserForm.FIELD_LAYOUT_TYPE, layoutType);

            // get user roles
            Collection userRoles = roleManager.getRolesForUser(userPrincipal.getName());

        } catch (Exception e) {
        }

    }

    protected List getInitParameterList(PortletConfig config, String ipName) {
        String temp = config.getInitParameter(ipName);
        if (temp == null)
            return new ArrayList();

        String[] temps = temp.split("\\,");
        for (int ix = 0; ix < temps.length; ix++)
            temps[ix] = temps[ix].trim();

        return Arrays.asList(temps);
    }

}
