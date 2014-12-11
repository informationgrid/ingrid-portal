/*

 */
package de.ingrid.portal.portlets.admin;

import java.io.File;
import java.io.IOException;
import java.security.PrivilegedAction;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.administration.PortalAdministration;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.FolderNotFoundException;
import org.apache.jetspeed.om.folder.InvalidFolderException;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.document.NodeException;
import org.apache.jetspeed.profiler.Profiler;
import org.apache.jetspeed.security.InvalidPasswordException;
import org.apache.jetspeed.security.JSSubject;
import org.apache.jetspeed.security.JetspeedPrincipalQueryContext;
import org.apache.jetspeed.security.PasswordAlreadyUsedException;
import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.Role;
import org.apache.jetspeed.security.RoleManager;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.UserResultList;
import org.apache.velocity.context.Context;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.forms.ActionForm;
import de.ingrid.portal.forms.AdminUserForm;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.global.UtilsString;
import de.ingrid.portal.hibernate.HibernateUtil;
import de.ingrid.portal.om.IngridNewsletterData;
import de.ingrid.portal.portlets.security.SecurityResources;
import de.ingrid.portal.security.role.IngridRole;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class AdminUserPortlet extends ContentPortlet {

    private final static Logger log = LoggerFactory.getLogger(AdminUserPortlet.class);

    private static final String KEY_ENTITIES = "entities";
    
    // Init Parameters
    private static final String IP_ROLES = "roles"; // comma separated

    private static final String IP_GROUPS = "groups"; // comma separated

    private static final String IP_RULES_NAMES = "rulesNames";

    private static final String IP_RULES_VALUES = "rulesValues";

    private static final String IP_EMAIL_TEMPLATE = "emailTemplate";

    private PortalAdministration admin;

    private UserManager userManager;

    private RoleManager roleManager;

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

        // roles
        this.roles = getInitParameterList(config, IP_ROLES);

        // groups
        this.groups = getInitParameterList(config, IP_GROUPS);

        // rules (name,value pairs)
        List names = getInitParameterList(config, IP_RULES_NAMES);
        List values = getInitParameterList(config, IP_RULES_VALUES);
        rules = new HashMap();
        for (int ix = 0; ix < ((names.size() < values.size()) ? names.size() : values.size()); ix++) {
        // jetspeed 2.3 reads rule key/values vice versa than Jetspeed 2.1 !!!
        // see PortalAdministrationImpl.registerUser
        // 2.1: ProfilingRule rule = profiler.getRule((String)entry.getKey());
        // 2.3: ProfilingRule rule = profiler.getRule(entry.getValue());
//            rules.put(names.get(ix), values.get(ix));
            rules.put(values.get(ix), names.get(ix));
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
            
            for (Map.Entry<String, String> filter : state.getFilterCriteria().entrySet()) {
                context.put(filter.getKey(), filter.getValue());
            }
            
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

    protected List<UserInfo> getEntities(RenderRequest request) {
        List<UserInfo> rows = new ArrayList<UserInfo>();

        try {
            long start = 0;
            if (log.isDebugEnabled()) {
                start = System.currentTimeMillis();
            }

            // get filter criteria
            ContentBrowserState state = getBrowserState(request);
            Map<String, String> filterCriteria = state.getFilterCriteria();

            String userId = filterCriteria.get("filterCriteriaId");

            Map<String, String> attributeMap = new HashMap<String, String>();
            if (filterCriteria.get("filterCriteriaFirstName") != null && filterCriteria.get("filterCriteriaFirstName").length() > 0) {
            	attributeMap.put(SecurityResources.USER_NAME_GIVEN, filterCriteria.get("filterCriteriaFirstName"));
            }
            if (filterCriteria.get("filterCriteriaLastName") != null && filterCriteria.get("filterCriteriaLastName").length() > 0) {
            	attributeMap.put(SecurityResources.USER_NAME_FAMILY, filterCriteria.get("filterCriteriaLastName"));
            }
            if (filterCriteria.get("filterCriteriaEmail") != null && filterCriteria.get("filterCriteriaEmail").length() > 0) {
            	attributeMap.put(SecurityResources.USER_EMAIL, filterCriteria.get("filterCriteriaEmail"));
            }

            List<String> roles = new ArrayList<String>();
            if (filterCriteria.get("filterCriteriaRole") != null && filterCriteria.get("filterCriteriaRole").length() > 0) {
            	roles.add(filterCriteria.get("filterCriteriaRole").replaceAll("\\*", "\\%"));
            }

            String sortOrder = null;

            // fetch users, filtering via QueryContext
            JetspeedPrincipalQueryContext qc = new JetspeedPrincipalQueryContext(
            	userId, 0, Integer.MAX_VALUE, sortOrder, roles, null, null, attributeMap);

            UserResultList ul = userManager.getUsersExtended(qc);

            if (log.isDebugEnabled()) {
                log.debug("fetchUsers: " + (System.currentTimeMillis() - start));
            }

            // iterate over all users
    		for (User user : ul.getResults()) {
    			// and create UserInfo for view
                UserInfo userInfo = new UserInfo();
                userInfo.setId(user.getName());
                
                // get the user roles
                Collection<Role> userRoles = roleManager.getRolesForUser(user.getName());
                for (Role r : userRoles) {
                	userInfo.addRole(r.getName());
                }

                userInfo.setFirstName(user.getInfoMap().get(SecurityResources.USER_NAME_GIVEN));
                userInfo.setLastName(user.getInfoMap().get(SecurityResources.USER_NAME_FAMILY));
                userInfo.setEmail(user.getInfoMap().get(SecurityResources.USER_EMAIL));

                PasswordCredential pc = userManager.getPasswordCredential(user);
                Timestamp t = pc.getLastAuthenticationDate();
                if(t != null){
                    userInfo.setLastLogin(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(t));
                } else {
                    userInfo.setLastLogin("");
                }

                rows.add(userInfo);
            }

        } catch (Exception e) {
        	log.error("Error getting entities!", e);
        }

        refreshBrowserState(request);
        
        return rows;
    }
    
    public class UserInfo extends HashMap<String, String> {
        private static final long serialVersionUID = -7920936432515328718L;

        List<String> rolesList = new ArrayList<String>();
        
        public UserInfo() {
            super();
        }
        
        public String getId() {
            return this.get("id");
        }

        public void setId(String name) {
            this.put("id", name);
        }
        
        public String getFirstName() {
            return (String) this.get("firstName");
        }
        
        public void setFirstName(String firstName) {
        	if (firstName == null) {
        		firstName = "";
        	}
            this.put("firstName", firstName);
        }
        
        public String getLastName() {
            return (String) this.get("lastName");
        }
        
        public void setLastName(String lastName) {
        	if (lastName == null) {
        		lastName = "";
        	}
            this.put("lastName", lastName);
        }

        public String getEmail() {
            return (String) this.get("email");
        }
        
        public void setEmail(String email) {
        	if (email == null) {
        		email = "";
        	}
            this.put("email", email);
        }
        
        public String getLastLogin() {
            return (String) this.get("lastLogin");
        }
        
        public void setLastLogin(String lastLogin) {
            this.put("lastLogin", lastLogin);
        }

        
        public String getRoles() {
            return (String) this.get("roles");
        }

        public void setRoles(String roles) {
            this.put("roles", roles);
        }

        public boolean hasRole(String role) {
            return rolesList.contains(role);
        }
        
        public void addRole(String role) {
            if (!hasRole(role)) {
                rolesList.add(role);
                this.put("roles", join(rolesList, ", "));
            }
        }
    }
    
    private static String join(Collection<String> s, String delimiter) {
        StringBuilder buffer = new StringBuilder();
        Iterator<String> iter = s.iterator();
        while (iter.hasNext()) {
            buffer.append(iter.next());
            if (iter.hasNext()) {
                buffer.append(delimiter);
            }
        }
        return buffer.toString();
    }

    /**
     * Create a 'normal' portal user with the role "user".
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
                    request.getLocale()), request.getLocale());

            HashMap userInfo = new HashMap(userAttributes);
            // map coded stuff
            String salutationFull = messages.getString("account.edit.salutation.option", (String) userInfo
                    .get("user.name.prefix"));
            userInfo.put("user.custom.ingrid.user.salutation.full", salutationFull);
            userInfo.put("login", userName);
            userInfo.put("password", password);

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
            f.setError("", "account.created.problems.general");
        }

    }

    /**
     * Updates a users data.
     * 
     * @see de.ingrid.portal.portlets.admin.ContentPortlet#doActionUpdate(javax.portlet.ActionRequest)
     */
    protected void doActionUpdate(ActionRequest request) {
    	// always admin ! With Jetspeed 2.3 we drop "admin-partner", "admin-provider" ... so all users having access
    	// to user administration have full access ("admin" or "admin-portal").
    	boolean isAdmin = true;
    	
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
                user.getSecurityAttributes().getAttribute("user.name.prefix", true).setStringValue(f.getInput(AdminUserForm.FIELD_SALUTATION));
                user.getSecurityAttributes().getAttribute("user.name.given", true).setStringValue(f.getInput(AdminUserForm.FIELD_FIRSTNAME));
                user.getSecurityAttributes().getAttribute("user.name.family", true).setStringValue(f.getInput(AdminUserForm.FIELD_LASTNAME));
                user.getSecurityAttributes().getAttribute("user.business-info.online.email", true).setStringValue(f.getInput(AdminUserForm.FIELD_EMAIL));
                user.getSecurityAttributes().getAttribute("user.business-info.postal.street", true).setStringValue(f.getInput(AdminUserForm.FIELD_STREET));
                user.getSecurityAttributes().getAttribute("user.business-info.postal.postalcode", true).setStringValue(f.getInput(AdminUserForm.FIELD_POSTALCODE));
                user.getSecurityAttributes().getAttribute("user.business-info.postal.city", true).setStringValue(f.getInput(AdminUserForm.FIELD_CITY));

                // theses are not PLT.D values but ingrid specifics
                user.getSecurityAttributes().getAttribute("user.custom.ingrid.user.age.group", true).setStringValue(f.getInput(AdminUserForm.FIELD_AGE));
                user.getSecurityAttributes().getAttribute("user.custom.ingrid.user.attention.from", true).setStringValue(f.getInput(AdminUserForm.FIELD_ATTENTION));
                user.getSecurityAttributes().getAttribute("user.custom.ingrid.user.interest", true).setStringValue(f.getInput(AdminUserForm.FIELD_INTEREST));
                user.getSecurityAttributes().getAttribute("user.custom.ingrid.user.profession", true).setStringValue(f.getInput(AdminUserForm.FIELD_PROFESSION));
                user.getSecurityAttributes().getAttribute("user.custom.ingrid.user.subscribe.newsletter", true).setStringValue(f.getInput(AdminUserForm.FIELD_SUBSCRIBE_NEWSLETTER));

                userManager.updateUser(user);

                try {
                    // update password only if a old password was provided
            		String oldPassword = f.getInput(AdminUserForm.FIELD_PASSWORD_OLD);
            		String newPassword = f.getInput(AdminUserForm.FIELD_PASSWORD_NEW);
            		PasswordCredential credential = userManager.getPasswordCredential(user);
                	if(isAdmin){
                		credential.setPassword(null, newPassword);
                	}else{
                        if (oldPassword != null && oldPassword.length() > 0) {
                    		credential.setPassword(oldPassword, newPassword);
                        }
                	}
            		userManager.storePasswordCredential(credential);
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
                // update the admin-portal role
                if (f.hasInput(AdminUserForm.FIELD_CHK_ADMIN_PORTAL)) {
                    roleManager.addRoleToUser(user.getName(), IngridRole.ROLE_ADMIN_PORTAL);

                } else {
                    roleManager.removeRoleFromUser(user.getName(), IngridRole.ROLE_ADMIN_PORTAL);
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
                JetspeedException pe = (JetspeedException) JSSubject.doAsPrivileged(userManager.getSubject(powerUser),
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
            Map<String, String> userAttributes = user.getInfoMap();
            f.setInput(AdminUserForm.FIELD_ID, editId);
            f.setInput(AdminUserForm.FIELD_SALUTATION, replaceNull(userAttributes.get("user.name.prefix")));
            f.setInput(AdminUserForm.FIELD_FIRSTNAME, replaceNull(userAttributes.get("user.name.given")));
            f.setInput(AdminUserForm.FIELD_LASTNAME, replaceNull(userAttributes.get("user.name.family")));
            f.setInput(AdminUserForm.FIELD_EMAIL, replaceNull(userAttributes.get("user.business-info.online.email")));
            f.setInput(AdminUserForm.FIELD_STREET, replaceNull(userAttributes.get("user.business-info.postal.street")));
            f.setInput(AdminUserForm.FIELD_POSTALCODE, replaceNull(userAttributes.get("user.business-info.postal.postalcode")));
            f.setInput(AdminUserForm.FIELD_CITY, replaceNull(userAttributes.get("user.business-info.postal.city")));

            f.setInput(AdminUserForm.FIELD_AGE, replaceNull(userAttributes.get("user.custom.ingrid.user.age.group")));
            f.setInput(AdminUserForm.FIELD_ATTENTION, replaceNull(userAttributes.get("user.custom.ingrid.user.attention.from")));
            f.setInput(AdminUserForm.FIELD_INTEREST, replaceNull(userAttributes.get("user.custom.ingrid.user.interest")));
            f.setInput(AdminUserForm.FIELD_PROFESSION, replaceNull(userAttributes.get("user.custom.ingrid.user.profession")));
            f.setInput(AdminUserForm.FIELD_SUBSCRIBE_NEWSLETTER, replaceNull(userAttributes.get(
                    "user.custom.ingrid.user.subscribe.newsletter")));

            // set admin-portal role
            Collection<Role> userRoles = roleManager.getRolesForUser(user.getName());
            for (Role r : userRoles) {
            	if (IngridRole.ROLE_ADMIN_PORTAL.equals(r.getName())) {
                    f.setInput(AdminUserForm.FIELD_CHK_ADMIN_PORTAL, "1");
            	}
            }

        } catch (Exception e) {
        	log.error("Problems fetching user data.", e);
        }

    }

    /** Replaces the input with "" if input is null. */
    private String replaceNull(String input) {
    	return input == null ? "" : input;
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
