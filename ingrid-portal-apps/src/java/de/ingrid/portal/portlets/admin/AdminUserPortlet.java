/*

 */
package de.ingrid.portal.portlets.admin;

import java.io.File;
import java.io.IOException;
import java.security.Permission;
import java.security.Permissions;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.apache.jetspeed.security.JSSubject;
import org.apache.jetspeed.security.PasswordAlreadyUsedException;
import org.apache.jetspeed.security.PermissionManager;
import org.apache.jetspeed.security.Role;
import org.apache.jetspeed.security.RoleManager;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.velocity.context.Context;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.forms.ActionForm;
import de.ingrid.portal.forms.AdminUserForm;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.global.UtilsDB;
import de.ingrid.portal.global.UtilsSecurity;
import de.ingrid.portal.global.UtilsString;
import de.ingrid.portal.hibernate.HibernateUtil;
import de.ingrid.portal.om.IngridNewsletterData;
import de.ingrid.portal.om.IngridSecurityCredential;
import de.ingrid.portal.portlets.security.SecurityResources;
import de.ingrid.portal.portlets.security.SecurityUtil;
import de.ingrid.portal.security.permission.IngridPartnerPermission;
import de.ingrid.portal.security.permission.IngridPortalPermission;
import de.ingrid.portal.security.permission.IngridProviderPermission;
import de.ingrid.portal.security.role.IngridRole;
import de.ingrid.portal.security.util.SecurityHelper;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class AdminUserPortlet extends ContentPortlet {

    private final static Logger log = LoggerFactory.getLogger(AdminUserPortlet.class);

    private static final String KEY_ENTITIES = "entities";
    
    private static final String GUEST = "guest";

    // Init Parameters
    private static final String IP_ROLES = "roles"; // comma separated

    private static final String IP_GROUPS = "groups"; // comma separated

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
     * Get current state of DB Browser. NOTICE: Only ONE state for all DB
     * Browsers (APPLICATION_SCOPE).
     * 
     * @param request
     * @return
     */
    static protected ContentBrowserState getBrowserState(PortletRequest request) {
        ContentBrowserState state = (ContentBrowserState) request.getPortletSession().getAttribute(KEY_BROWSER_STATE,
                PortletSession.APPLICATION_SCOPE);
        if (state == null) {
            state = new ContentBrowserState();
            state.setMaxRows(PortalConfig.getInstance().getInt(PortalConfig.USER_ADMIN_MAX_ROW));
            setBrowserState(request, state);
        }
        return state;
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
        psmlPage = "/portal/administration/admin-usermanagement.psml";
        viewDefault = "/WEB-INF/templates/administration/admin_user_browser.vm";
        viewEdit = "/WEB-INF/templates/administration/admin_user_edit.vm";
        viewNew = "/WEB-INF/templates/administration/admin_user_new.vm";
        viewTitleKey = "admin.title.user";
    }

    /**
     * @see de.ingrid.portal.portlets.admin.ContentPortlet#doView(javax.portlet.RenderRequest,
     *      javax.portlet.RenderResponse)
     */
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        super.doView(request, response);
    }

    /**
     * @see de.ingrid.portal.portlets.admin.ContentPortlet#doViewDefault(javax.portlet.RenderRequest)
     */
    protected boolean doViewDefault(RenderRequest request) {
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
     * Checks for the right permission condition to include a user into the user
     * browser.
     * 
     * The following users will be included:
     * 
     * <li>for admins with permission "admin": all users </li>
     * <li>for admins with permission "admin.portal": users with only role
     * "user" or "mdek" </li>
     * <li>for admins with permission "admin.portal": users with a permission
     * "admin.portal.*" </li>
     * <li>for admins with permission "admin.portal.partner": users with
     * IngridPortalPermission("admin.portal.partner.*") AND
     * IngridPartnerPermission("partner".<partner of the admin></li>
     * <li>for admins with permission "admin.portal.partner": users with role
     * "ingrid-provider" AND the IngridPartnerPermission("partner.<partner of
     * the admin>")</li>
     * 
     * @param authUserPermissions
     *            The Permissions of the authenticated user.
     * @param userPermissions
     *            The Permissions of the user to test.
     * @param userRoles
     *            The roles of the user to test.
     * @return true if the auth user has permission to edit the user with
     *         userPermissions, false if not.
     */
    public static boolean includeUserByRoleAndPermission(Permissions authUserPermissions, Collection userRoles,
            Permissions userPermissions) {
        Enumeration en;

        // WE ARE KING, STEP ASIDE!
        if (authUserPermissions.implies(UtilsSecurity.ADMIN_INGRID_PORTAL_PERMISSION)) {
            return true;
        }
        // for permission "admin.portal", include users with permission
        // "admin.portal.*" OR with the only role "user"
        if (authUserPermissions.implies(UtilsSecurity.ADMIN_PORTAL_INGRID_PORTAL_PERMISSION)) {
            en = userPermissions.elements();
            while (en.hasMoreElements()) {
                if (UtilsSecurity.ADMIN_PORTAL_STAR_INGRID_PORTAL_PERMISSION.implies((Permission) en.nextElement())) {
                    return true;
                }
            }
            Iterator it = userRoles.iterator();
            // check for user with no roles, exit
            if (!it.hasNext()) {
                return false;
            }
            // check for users with roles others than 'user' or 'mdek' -> exit if found
            while (it.hasNext()) {
                Role r = (Role) it.next();
                if (!r.getPrincipal().getName().equals("user") && !r.getPrincipal().getName().equals("mdek")) {
                    return false;
                }
            }
            return true;
        }
        // for permission "admin.portal.partner", include with permission
        // "admin.portal.partner.*" AND IngridPartnerPermission("partner",
        // <partner_of_auth_user>)
        if (authUserPermissions.implies(UtilsSecurity.ADMIN_PORTAL_PARTNER_INGRID_PORTAL_PERMISSION)) {
            // get the partner from the auth users permission
            ArrayList authUserPartner = UtilsSecurity.getPartnersFromPermissions(authUserPermissions, true);
            // add users that imply admin.portal.provider.* AND
            // IngridPartnerPermission(partner, <partner_of_auth_user>)
            boolean implyPermission = false;
            boolean implyRole = false;
            boolean implyPartner = false;
            en = userPermissions.elements();
            Permission userPermission;
            while (en.hasMoreElements()) {
                userPermission = (Permission) en.nextElement();
                // check for permission IngridPartnerPermission(partner
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
                if (UtilsSecurity.ADMIN_PORTAL_PARTNER_STAR_INGRID_PORTAL_PERMISSION.implies(userPermission)) {
                    implyPermission = true;
                } else {
                    // check for users with roles admin-provider
                    Iterator it = userRoles.iterator();
                    while (it.hasNext()) {
                        Role r = (Role) it.next();
                        if (r.getPrincipal().getName().equals(IngridRole.ROLE_ADMIN_PROVIDER)) {
                            implyRole = true;
                        }
                    }
                }
                if (implyPartner && (implyPermission || implyRole)) {
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
            if (log.isDebugEnabled()) {
            	if (authUserPrincipal == null) {
                	log.debug("GetUsers request by the following principal: NULL");
            	} else {
            		log.debug("GetUsers request by the following principal:" + authUserPrincipal.getName() + ", class:" + authUserPrincipal.getClass().getName());
            	}
            }
            Permissions authUserPermissions = SecurityHelper.getMergedPermissions(authUserPrincipal, permissionManager,
                    roleManager);

            // iterate over all users
            Iterator users = userManager.getUsers("");
            while (users.hasNext()) {
            	User user = (User) users.next();
                Principal userPrincipal = SecurityUtil.getPrincipal(user.getSubject(), UserPrincipal.class);
                if(!userPrincipal.getName().equals(AdminUserPortlet.GUEST)){
	                Permissions userPermissions = SecurityHelper.getMergedPermissions(userPrincipal, permissionManager,
	                        roleManager);
	
	                // get the user roles
	                Collection userRoles = roleManager.getRolesForUser(userPrincipal.getName());
	
	                boolean addUser = includeUserByRoleAndPermission(authUserPermissions, userRoles, userPermissions);
	
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
	                    
	                    Session session = HibernateUtil.currentSession();
	                	ProjectionList projList = Projections.projectionList();
	                	projList.add(Projections.groupProperty("securityLastAuthDate"));
	                	Criteria crit = session.createCriteria(IngridSecurityCredential.class)
	                    .setProjection(projList)
	                    .add(Restrictions.sqlRestriction("PRINCIPAL_ID = (SELECT PRINCIPAL_ID FROM security_principal where FULL_PATH = '/user/" + userPrincipal.getName()+ "')"));
	                   
	            		List lastLogin = UtilsDB.getValuesFromDB(crit, session, null, true);
	            		Object lastLoginDate;
	            		if(lastLogin.size() > 0){
	            			lastLoginDate = lastLogin.get(0);
	            			record.put("lastLogin", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(lastLoginDate));
	            		}else{
	            			record.put("lastLogin", "");
	            		}
	                    
	                    rows.add(record);
	                }
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
     * Create a 'normal' portal user with the role "user". If the creator has
     * the permission "admin.portal.partner", the created user will get:
     * 
     * <li>role "admin-provider"</li>
     * <li>copy of all IngridPartnerPermissions of the creator</li>
     * 
     * so the creator is able to see the user in his user browser.
     * 
     * 
     * @see de.ingrid.portal.portlets.admin.ContentPortlet#doActionSave(javax.portlet.ActionRequest)
     */
    protected void doActionSave(ActionRequest request) {
        AdminUserForm f = (AdminUserForm) Utils.getActionForm(request, AdminUserForm.SESSION_KEY, AdminUserForm.class);
        f.clearErrors();
        f.clearMessages();
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

            IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                    request.getLocale()));

            HashMap userInfo = new HashMap(userAttributes);
            // map coded stuff
            String salutationFull = messages.getString("account.edit.salutation.option", (String) userInfo
                    .get("user.name.prefix"));
            userInfo.put("user.custom.ingrid.user.salutation.full", salutationFull);
            userInfo.put("login", userName);
            userInfo.put("password", password);

            // set basic permissions and roles depending on the auth users
            // permission
            Principal authUserPrincipal = request.getUserPrincipal();
            Permissions authUserPermissions = SecurityHelper.getMergedPermissions(authUserPrincipal, permissionManager,
                    roleManager);
            if (getLayoutPermission(authUserPermissions).equalsIgnoreCase("admin.portal.partner")) {
                // get user
                User user = null;
                try {
                    user = userManager.getUser(userName);
                } catch (JetspeedException e) {
                    f.setError("", "account.edit.error.user.notfound");
                    log.error("Error getting current user.", e);
                    return;
                }
                // get principal
                Principal userPrincipal = SecurityUtil.getPrincipal(user.getSubject(), UserPrincipal.class);

                // add
                // IngridPartnerPermissions("partner.<partner_of_auth_user>") to
                // the new user
                // get current user
                List partners = UtilsSecurity.getPartnersFromPermissions(authUserPermissions, true);
                Iterator it = partners.iterator();
                while (it.hasNext()) {
                    String partner = (String) it.next();
                    // add IngridPartnerPermissions for specified partner
                    createAndGrantPermission(userPrincipal, new IngridPartnerPermission("partner." + partner));
                }

                // add provider role
                roleManager.addRoleToUser(userPrincipal.getName(), IngridRole.ROLE_ADMIN_PROVIDER);
            }

            // send confirmation email
            String language = Utils.checkSupportedLanguage(request.getLocale().getLanguage());
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
                if (((String)userAttributes.get("user.custom.ingrid.user.subscribe.newsletter")).equals("1")) {
                	Session session = HibernateUtil.currentSession();
                    Transaction tx = null;
                    tx = session.beginTransaction();
                    List newsletterDataList = session.createCriteria(IngridNewsletterData.class)
                    .add(Restrictions.eq("emailAddress", to))
                    .list();
                    tx.commit();
                    
                    if (newsletterDataList.isEmpty()) {
                    
                        IngridNewsletterData data = new IngridNewsletterData(); 
                        data.setFirstName((String)userAttributes.get("user.name.given"));
                        data.setLastName((String)userAttributes.get("user.name.family"));
                        data.setEmailAddress(to);
                        data.setDateCreated(new Date());
                        
                        tx = session.beginTransaction();
                        session.save(data);
                        tx.commit();
                    }                       	
                }
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
     * Updates a users data and permission.
     * 
     * CAUTION: if the admin is "admin.portal.partner", the following specials
     * apply:
     * 
     * <li>the users partner cannot be removed</li>
     * <li>the users role "admin-provider" will not be removed</li>
     * 
     * 
     * @see de.ingrid.portal.portlets.admin.ContentPortlet#doActionUpdate(javax.portlet.ActionRequest)
     */
    protected void doActionUpdate(ActionRequest request) {
    	Principal authUserPrincipal = request.getUserPrincipal();
    	Permissions authUserPermissions = SecurityHelper.getMergedPermissions(authUserPrincipal, permissionManager,
                roleManager);
    	Permissions userPermissions = SecurityHelper.getMergedPermissions(authUserPrincipal, permissionManager,
                roleManager);
    	Collection userRoles;
    	boolean isAdmin = false;
    	
		try {
			userRoles = roleManager.getRolesForUser(authUserPrincipal.getName());
			isAdmin = includeUserByRoleAndPermission(authUserPermissions, userRoles, userPermissions);
		} catch (SecurityException e1) {
			e1.printStackTrace();
		}
    	AdminUserForm f = (AdminUserForm) Utils.getActionForm(request, AdminUserForm.SESSION_KEY, AdminUserForm.class);
        f.clearErrors();
        f.clearMessages();
        f.populate(request);
        if (!f.validate(isAdmin)) {
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
                	if(isAdmin){
                		userManager.setPassword(userName, null, f.getInput(AdminUserForm.FIELD_PASSWORD_NEW));
                	}else{
                		String oldPassword = f.getInput(AdminUserForm.FIELD_PASSWORD_OLD);
                        if (oldPassword != null && oldPassword.length() > 0) {
                            userManager.setPassword(userName, f.getInput(AdminUserForm.FIELD_PASSWORD_OLD), 
						                            f.getInput(AdminUserForm.FIELD_PASSWORD_NEW));
                        }
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
                    permissionManager.grantPermission(userPrincipal,
                            UtilsSecurity.ADMIN_PORTAL_INGRID_PORTAL_PERMISSION);
                    roleManager.addRoleToUser(userPrincipal.getName(), IngridRole.ROLE_ADMIN_PORTAL);

                    // remove all other roles and permissions
                    permissionManager.revokePermission(userPrincipal,
                            UtilsSecurity.ADMIN_PORTAL_PARTNER_INGRID_PORTAL_PERMISSION);
                    roleManager.removeRoleFromUser(userPrincipal.getName(), IngridRole.ROLE_ADMIN_PARTNER);
                    
                    permissionManager.revokePermission(userPrincipal,
                            UtilsSecurity.ADMIN_PORTAL_PARTNER_PROVIDER_INDEX_INGRID_PORTAL_PERMISSION);
                    
                    permissionManager.revokePermission(userPrincipal,
                            UtilsSecurity.ADMIN_PORTAL_PARTNER_PROVIDER_CATALOG_INGRID_PORTAL_PERMISSION);
                
                } else {
                    permissionManager.revokePermission(userPrincipal,
                            UtilsSecurity.ADMIN_PORTAL_INGRID_PORTAL_PERMISSION);
                    roleManager.removeRoleFromUser(userPrincipal.getName(), IngridRole.ROLE_ADMIN_PORTAL);
                }

                // update the admin.portal.partner permission
                if (f.hasInput(AdminUserForm.FIELD_CHK_ADMIN_PARTNER)) {
                    permissionManager.grantPermission(userPrincipal,
                            UtilsSecurity.ADMIN_PORTAL_PARTNER_INGRID_PORTAL_PERMISSION);
                    roleManager.addRoleToUser(userPrincipal.getName(), IngridRole.ROLE_ADMIN_PARTNER);

                    // remove all other roles and permissions
                    permissionManager.revokePermission(userPrincipal,
                            UtilsSecurity.ADMIN_PORTAL_PARTNER_PROVIDER_INDEX_INGRID_PORTAL_PERMISSION);
                    
                    permissionManager.revokePermission(userPrincipal,
                            UtilsSecurity.ADMIN_PORTAL_PARTNER_PROVIDER_CATALOG_INGRID_PORTAL_PERMISSION);

                    
                } else {
                    permissionManager.revokePermission(userPrincipal,
                            UtilsSecurity.ADMIN_PORTAL_PARTNER_INGRID_PORTAL_PERMISSION);
                    roleManager.removeRoleFromUser(userPrincipal.getName(), IngridRole.ROLE_ADMIN_PARTNER);
                }

                // update the admin.portal.partner.provider.index permission
                if (f.hasInput(AdminUserForm.FIELD_CHK_ADMIN_INDEX)) {
                    permissionManager.grantPermission(userPrincipal,
                            UtilsSecurity.ADMIN_PORTAL_PARTNER_PROVIDER_INDEX_INGRID_PORTAL_PERMISSION);
                } else {
                    permissionManager.revokePermission(userPrincipal,
                            UtilsSecurity.ADMIN_PORTAL_PARTNER_PROVIDER_INDEX_INGRID_PORTAL_PERMISSION);
                }

                // update the admin.portal.partner.provider.catalog permission
                if (f.hasInput(AdminUserForm.FIELD_CHK_ADMIN_CATALOG)) {
                    permissionManager.grantPermission(userPrincipal,
                            UtilsSecurity.ADMIN_PORTAL_PARTNER_PROVIDER_CATALOG_INGRID_PORTAL_PERMISSION);
                } else {
                    permissionManager.revokePermission(userPrincipal,
                            UtilsSecurity.ADMIN_PORTAL_PARTNER_PROVIDER_CATALOG_INGRID_PORTAL_PERMISSION);
                }

                // remove all IngridPartnerPermissions, they will be reset if a
                // provider or partner permission was granted
                // special: DO RESET the partner if the auth user is
                // "admin.portal.partner"
                revokePermissionsByClass(userPrincipal, IngridPartnerPermission.class);
                if (f.hasInput(AdminUserForm.FIELD_CHK_ADMIN_PARTNER)
                        || f.hasInput(AdminUserForm.FIELD_CHK_ADMIN_CATALOG)
                        || f.hasInput(AdminUserForm.FIELD_CHK_ADMIN_INDEX)
                        || f.getInput(AdminUserForm.FIELD_LAYOUT_PERMISSION).equals("admin.portal.partner")) {
                    // add IngridPartnerPermissions for specified partner
                    createAndGrantPermission(userPrincipal, new IngridPartnerPermission("partner."
                            + f.getInput(AdminUserForm.FIELD_PARTNER)));
                } else {
                    f.clearInput(AdminUserForm.FIELD_PARTNER);
                }

                // remove all IngridProviderPermissions, they will be reset if a
                // provider permission was granted
                revokePermissionsByClass(userPrincipal, IngridProviderPermission.class);
                // set providers if any provider permission was granted
                if (f.hasInput(AdminUserForm.FIELD_CHK_ADMIN_CATALOG)
                        || f.hasInput(AdminUserForm.FIELD_CHK_ADMIN_INDEX)) {
                    // add IngridProviderPermissions for specified partner
                    String[] providers = f.getInputAsArray(AdminUserForm.FIELD_PROVIDER);
                    for (int i = 0; i < providers.length; i++) {
                        createAndGrantPermission(userPrincipal,
                                new IngridProviderPermission("provider." + providers[i]));
                    }
                    // add provider role
                    roleManager.addRoleToUser(userPrincipal.getName(), IngridRole.ROLE_ADMIN_PROVIDER);
                } else {
                    f.clearInput(AdminUserForm.FIELD_PROVIDER);
                    // DO NOT remove admin-provider permission if the auth user
                    // is "admin.portal.partner"
                    if (!f.getInput(AdminUserForm.FIELD_LAYOUT_PERMISSION).equals("admin.portal.partner")) {
                        roleManager.removeRoleFromUser(userPrincipal.getName(), IngridRole.ROLE_ADMIN_PROVIDER);
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
     * @see de.ingrid.portal.portlets.admin.ContentPortlet#doActionDelete(javax.portlet.ActionRequest)
     */
    protected void doActionDelete(ActionRequest request) {

        String[] ids = (String[]) getDBEntities(request);
        for (int i = 0; i < ids.length; i++) {
            try {

                final String innerFolder = Folder.USER_FOLDER;
                final String innerUserName = ids[i];
                final PageManager innerPageManager = pageManager;
                User powerUser = userManager.getUser("admin");
                JetspeedException pe = (JetspeedException) JSSubject.doAsPrivileged(powerUser.getSubject(),
                        new PrivilegedAction() {
                            public Object run() {
                                try {
                                    // remove user's home folder
                                    if (log.isDebugEnabled()) {
                                    	log.debug("Try to remove folder: " + innerFolder + innerUserName);
                                    }
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
                
                if (pe != null) {
                    log.error("Registration Error: Failed to remove user folders for " + ids[i] + ", " + pe.toString());
                }
                // remove user creation and cascade roles, groups, etc
                try {
                    if (userManager.getUser(ids[i]) != null) {
                        userManager.removeUser(ids[i]);
                    }
                } catch (Exception e) {
                    log.error("Registration Error: Failed to remove user " + ids[i]);
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
            doActionUpdate(request);
            // reset the sction to edit, to show the edit screen and collect all
            // necessary data
            response.setRenderParameter(Settings.PARAM_ACTION, PARAMV_ACTION_DO_EDIT);
            response.setRenderParameter("cmd", "action processed");
        } else if (request.getParameter(PARAMV_ACTION_DB_DO_SAVE) != null) {
            // call sub method
            doActionSave(request);
            // reset the sction to edit, to show the edit screen and collect all
            // necessary data
            response.setRenderParameter(Settings.PARAM_ACTION, PARAMV_ACTION_DO_NEW);
            response.setRenderParameter("cmd", "action processed");

            // check for cancel to avoid an unnecesarry "doChangeTab" action
        } else if (request.getParameter(PARAMV_ACTION_DO_REFRESH) != null) {
            AdminUserForm f = (AdminUserForm) Utils.getActionForm(request, AdminUserForm.SESSION_KEY, AdminUserForm.class);
            f.clearErrors();
            f.clearMessages();
            f.populate(request);
            // save the tab
            f.setInput(AdminUserForm.FIELD_TAB, request.getParameter("tab"));
            // save the id of the edited user, to keep the reference
            f.setInput(AdminUserForm.FIELD_ID, request.getParameter("id"));
            f.validate();
            response.setRenderParameter(Settings.PARAM_ACTION, PARAMV_ACTION_DO_REFRESH);
            response.setRenderParameter("cmd", "action processed");

        } else if (request.getParameter(PARAMV_ACTION_DB_DO_CANCEL) != null) {
            response.setRenderParameter(PARAM_NOT_INITIAL, Settings.MSGV_TRUE);
            return;
        } else if (action != null && action.equals("doChangeTab")) {
            AdminUserForm f = (AdminUserForm) Utils.getActionForm(request, AdminUserForm.SESSION_KEY,
                    AdminUserForm.class);
            f.clearMessages();
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
     * @see de.ingrid.portal.portlets.admin.ContentPortlet#doViewEdit(javax.portlet.RenderRequest)
     */
    protected boolean doViewEdit(RenderRequest request) {

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

            // show newsletter option if configured that way
            context.put("enableNewsletter", PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_ENABLE_NEWSLETTER, Boolean.TRUE));

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
     * @see de.ingrid.portal.portlets.admin.ContentPortlet#doViewNew(javax.portlet.RenderRequest)
     */
    protected boolean doViewNew(RenderRequest request) {
        AdminUserForm f = (AdminUserForm) Utils.getActionForm(request, AdminUserForm.SESSION_KEY, AdminUserForm.class);

        Context context = getContext(request);
        context.put(CONTEXT_MODE, CONTEXTV_MODE_NEW);
        context.put(CONTEXT_UTILS_STRING, new UtilsString());

        String cmd = request.getParameter("cmd");
        if (cmd == null) {
            f.clear();
        }

        context.put("actionForm", f);

        // show newsletter option if configured that way
        context.put("enableNewsletter", PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_ENABLE_NEWSLETTER, Boolean.TRUE));

        setDefaultViewPage(viewNew);
        return true;
    }

    protected boolean doRefresh(RenderRequest request) {
        AdminUserForm f = (AdminUserForm) Utils.getActionForm(request, AdminUserForm.SESSION_KEY, AdminUserForm.class);
        if (f.hasInput(AdminUserForm.FIELD_TAB) && f.getInput(AdminUserForm.FIELD_TAB).equals("2")) {
            return !doViewEdit(request);
        } else {
            return super.doRefresh(request);
        }
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

    private static String getLayoutPermission(Permissions editorPermissions) {
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
            Permissions userPermissions = SecurityHelper.getMergedPermissions(userPrincipal, permissionManager,
                    roleManager);

            // get partner from permissions, set to context
            List userPartners = UtilsSecurity.getPartnersFromPermissions(userPermissions, true);
            List userProviders = UtilsSecurity.getProvidersFromPermissions(userPermissions, true);

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
            Permissions authUserPermissions = SecurityHelper.getMergedPermissions(authUserPrincipal, permissionManager,
                    roleManager);

            // set type of layout
            String layoutPermission = getLayoutPermission(authUserPermissions);
            f.setInput(AdminUserForm.FIELD_LAYOUT_PERMISSION, layoutPermission);

            // get user roles
            // Collection userRoles =
            // roleManager.getRolesForUser(userPrincipal.getName());

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
