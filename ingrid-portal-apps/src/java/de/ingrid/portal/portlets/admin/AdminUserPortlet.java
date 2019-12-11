/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2019 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.administration.PortalAdministration;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.FolderNotFoundException;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.document.NodeException;
import org.apache.jetspeed.profiler.Profiler;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.forms.ActionForm;
import de.ingrid.portal.forms.AdminUserForm;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.global.UtilsString;
import de.ingrid.portal.portlets.security.SecurityResources;
import de.ingrid.portal.security.role.IngridRole;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class AdminUserPortlet extends ContentPortlet {

    private static final Logger log = LoggerFactory.getLogger(AdminUserPortlet.class);

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

    /** email template to use for merging */
    private String emailTemplate;

    /** roles */
    private List<String> roles;

    /** groups */
    private List<String> groups;

    /** profile rules */
    private Map<String, String> rules;

    private String returnURL;

    /**
     * @see de.ingrid.portal.portlets.admin.ContentPortlet#refreshBrowserState(javax.portlet.PortletRequest)
     */
    @Override
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
    protected static ContentBrowserState getBrowserState(PortletRequest request) {
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
    @Override
    public void init(PortletConfig config) throws PortletException {
        super.init(config);

        Profiler profiler = (Profiler) getPortletContext().getAttribute(CommonPortletServices.CPS_PROFILER_COMPONENT);
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
        List<String> names = getInitParameterList(config, IP_RULES_NAMES);
        List<String> values = getInitParameterList(config, IP_RULES_VALUES);
        rules = new HashMap<>();
        for (int ix = 0; ix < ((names.size() < values.size()) ? names.size() : values.size()); ix++) {
        // jetspeed 2.3 reads rule key/values vice versa than Jetspeed 2.1 !!!
        // see PortalAdministrationImpl.registerUser
        // 2.1: ProfilingRule rule = profiler.getRule((String)entry.getKey());
        // 2.3: ProfilingRule rule = profiler.getRule(entry.getValue());
//            rules.put(names.get(ix), values.get(ix));
            rules.put(values.get(ix), names.get(ix));
        }

        this.emailTemplate = config.getInitParameter(IP_EMAIL_TEMPLATE);

        this.returnURL = "/service-myportal.psml";

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
    @Override
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        super.doView(request, response);
    }

    /**
     * @see de.ingrid.portal.portlets.admin.ContentPortlet#doViewDefault(javax.portlet.RenderRequest)
     */
    @Override
    protected boolean doViewDefault(RenderRequest request) {
        try {

            // get entities
            List rows = getEntities(request);

            String defaultSortColumn = PortalConfig.getInstance().getString( PortalConfig.USER_ADMIN_SORT_COLUMN, "id" );
            String sortColumn = getSortColumn(request, defaultSortColumn);
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
            if (val1 == null) {
                if (ascendingOrder) {
                    return -1;
                } else {
                    return 1;
                }
            }
            if (val2 == null) {
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
        List<UserInfo> rows = new ArrayList<>();

        try {
            long start = 0;
            if (log.isDebugEnabled()) {
                start = System.currentTimeMillis();
            }

            // check for admin 
            boolean isAdmin = "admin".equals(request.getUserPrincipal().getName());

            // get filter criteria
            ContentBrowserState state = getBrowserState(request);
            Map<String, String> filterCriteria = state.getFilterCriteria();

            String userId = filterCriteria.get("filterCriteriaId");

            Map<String, String> attributeMap = new HashMap<>();
            if (filterCriteria.get("filterCriteriaFirstName") != null && filterCriteria.get("filterCriteriaFirstName").length() > 0) {
                attributeMap.put(SecurityResources.USER_NAME_GIVEN, filterCriteria.get("filterCriteriaFirstName"));
            }
            if (filterCriteria.get("filterCriteriaLastName") != null && filterCriteria.get("filterCriteriaLastName").length() > 0) {
                attributeMap.put(SecurityResources.USER_NAME_FAMILY, filterCriteria.get("filterCriteriaLastName"));
            }
            if (filterCriteria.get("filterCriteriaEmail") != null && filterCriteria.get("filterCriteriaEmail").length() > 0) {
                attributeMap.put(SecurityResources.USER_EMAIL, filterCriteria.get("filterCriteriaEmail"));
            }

            List<String> roles = new ArrayList<>();
            if (filterCriteria.get("filterCriteriaRole") != null && filterCriteria.get("filterCriteriaRole").length() > 0) {
                roles.add(filterCriteria.get("filterCriteriaRole").replaceAll("\\*", "\\%"));
            }

            String sortOrder = null;

            // fetch users, filtering via QueryContext
            // NOTICE: fetch max 10000 users ! Integer.MAX_VALUE leads to exception with Oracle DB !!!
            JetspeedPrincipalQueryContext qc = new JetspeedPrincipalQueryContext(
                userId, 0, 10000, sortOrder, roles, null, null, attributeMap);

            UserResultList ul = userManager.getUsersExtended(qc);

            if (log.isDebugEnabled()) {
                log.debug("fetchUsers: " + (System.currentTimeMillis() - start));
            }

            // iterate over all users
            for (User user : ul.getResults()) {
                
                // do not show admin user if not logged in as admin (superadmin) !
                if (!isAdmin && "admin".equals(user.getName())) {
                    continue;
                }
                
                // and create UserInfo for view
                UserInfo userInfo = new UserInfo();
                userInfo.setId(user.getName());
                
                // get the user roles
                Collection<Role> userRoles = roleManager.getRolesForUser(user.getName());
                boolean userIsAdminPortal = false;
                for (Role r : userRoles) {
                    // skip all admin-portal users if not logged in as admin (superadmin) !
                    if (IngridRole.ROLE_ADMIN_PORTAL.equals(r.getName())) {
                        userIsAdminPortal = true;
                    }
                    userInfo.addRole(r.getName());
                }
                // do not show admin-portal user if not logged in as admin (superadmin) !
                if (!isAdmin && userIsAdminPortal) {
                    continue;
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

        List<String> rolesList = new ArrayList<>();
        
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
            return this.get("firstName");
        }
        
        public void setFirstName(String firstName) {
            if (firstName == null) {
                firstName = "";
            }
            this.put("firstName", firstName);
        }
        
        public String getLastName() {
            return this.get("lastName");
        }
        
        public void setLastName(String lastName) {
            if (lastName == null) {
                lastName = "";
            }
            this.put("lastName", lastName);
        }

        public String getEmail() {
            return this.get("email");
        }
        
        public void setEmail(String email) {
            if (email == null) {
                email = "";
            }
            this.put("email", email);
        }
        
        public String getLastLogin() {
            return this.get("lastLogin");
        }
        
        public void setLastLogin(String lastLogin) {
            this.put("lastLogin", lastLogin);
        }

        
        public String getRoles() {
            return this.get("roles");
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
    protected void doActionSave(ActionRequest request, ActionResponse response) {
        AdminUserForm f = (AdminUserForm) Utils.getActionForm(request, AdminUserForm.SESSION_KEY, AdminUserForm.class);
        f.clearErrors();
        f.clearMessages();
        f.populate(request);
        if (!f.validate()) {
            return;
        }

        try {

            String userName = f.getInput(AdminUserForm.FIELD_ID);
            String password = f.getInput(AdminUserForm.FIELD_PW_NEW);
            boolean isUserPwUpdateRequired = !f.getInput(AdminUserForm.FIELD_CHK_PW_UPDATE_REQUIRED).isEmpty();
            boolean isUserSendInfo = !f.getInput(AdminUserForm.FIELD_CHK_SEND_INFO).isEmpty();
            User user;

            // check if the user name exists
            boolean userIdExistsFlag = true;
            try {
                user = userManager.getUser(userName);
            } catch (SecurityException e) {
                userIdExistsFlag = false;
            }
            if (userIdExistsFlag) {
                f.setError(AdminUserForm.FIELD_ID, "account.create.error.user.exists");
                return;
            }

            Map<String, String> userAttributes = new HashMap<>();
            // we'll assume that these map back to PLT.D values
            userAttributes.put("user.name.prefix", f.getInput(AdminUserForm.FIELD_SALUTATION));
            userAttributes.put("user.name.given", f.getInput(AdminUserForm.FIELD_FIRSTNAME));
            userAttributes.put("user.name.family", f.getInput(AdminUserForm.FIELD_LASTNAME));
            userAttributes.put("user.business-info.online.email", f.getInput(AdminUserForm.FIELD_EMAIL));
            userAttributes.put("user.business-info.postal.street", f.getInput(AdminUserForm.FIELD_STREET));
            userAttributes.put("user.business-info.postal.postalcode", f.getInput(AdminUserForm.FIELD_POSTALCODE));
            userAttributes.put("user.business-info.postal.city", f.getInput(AdminUserForm.FIELD_CITY));

            // generate login id
            String confirmId = Utils.getMD5Hash(userName.concat(password).concat(
                    Long.toString(System.currentTimeMillis())));
            userAttributes.put("user.custom.ingrid.user.confirmid", confirmId);

            if (log.isInfoEnabled()) {
                String myRoles = "";
                for (String myRole : this.roles) {
                    myRoles += myRole + " / ";
                }
                String myGroups = "";
                for (String myGroup : this.groups) {
                    myGroups += myGroup + " / ";
                }
                String myUserAttributes = "";
                for (String myKey : userAttributes.keySet()) {
                    myUserAttributes += myKey + ":" + userAttributes.get( myKey ) + " / ";
                }
                String myRules = "";
                for (String myKey : rules.keySet()) {
                    myRules += myKey + ":" + rules.get( myKey ) + " / ";
                }
                log.info("registerUser "
                        + "\nusername: " + userName 
                        + "\nroles: " + myRoles
                        + "\ngroups: " + myGroups
                        + "\nuserAttr: " + myUserAttributes
                        + "\nrules: " + myRules);
            }
            admin.registerUser(userName, password, this.roles, this.groups, userAttributes, rules, null);

            user = userManager.getUser(userName);
            PasswordCredential credential = userManager.getPasswordCredential(user);
            credential.setUpdateRequired(isUserPwUpdateRequired);
            userManager.storePasswordCredential(credential);

            IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                    request.getLocale()), request.getLocale());

            HashMap<String, String> userInfo = new HashMap<>(userAttributes);
            // map coded stuff
            String salutationFull = messages.getString("account.edit.salutation.option", (String) userInfo
                    .get("user.name.prefix"));
            userInfo.put("user.custom.ingrid.user.salutation.full", salutationFull);
            userInfo.put("login", userName);
            if (isUserPwUpdateRequired || isUserSendInfo) {
                // send confirmation email
                if(isUserSendInfo) {
                    userInfo.put("password", password);
                }
                if (isUserPwUpdateRequired) {
                    userInfo.put("returnURL", generateReturnURL(request, response, userName, confirmId, userAttributes.get("user.business-info.online.email")));
                }
                sendMail(request, f, messages, userInfo, true);
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
    protected void doActionUpdate(ActionRequest request, ActionResponse response) {
        // is super admin ?
        boolean isAdmin = "admin".equals(request.getUserPrincipal().getName());
        
        AdminUserForm f = (AdminUserForm) Utils.getActionForm(request, AdminUserForm.SESSION_KEY, AdminUserForm.class);
        f.clearErrors();
        f.clearMessages();
        f.populate(request);
        if (!f.validate(isAdmin)) {
            return;
        }

        try {

            String userName = f.getInput(AdminUserForm.FIELD_ID);
            boolean isUserEnabled = !f.getInput(AdminUserForm.FIELD_CHK_ENABLED).isEmpty();
            boolean isUserPwUpdateRequired = !f.getInput(AdminUserForm.FIELD_CHK_PW_UPDATE_REQUIRED).isEmpty();
            boolean isUserSendInfo = !f.getInput(AdminUserForm.FIELD_CHK_SEND_INFO).isEmpty();

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
                if(!user.getName().equals("admin")) {
                    user.setEnabled(isUserEnabled);
                }
                userManager.updateUser(user);

                PasswordCredential credential = userManager.getPasswordCredential(user);
                credential.setUpdateRequired(isUserPwUpdateRequired);
                userManager.storePasswordCredential(credential);

                try {

                    IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                            request.getLocale()), request.getLocale());
                    HashMap<String, String> userInfo = new HashMap<>(user.getInfoMap());

                    // map coded stuff
                    String salutationFull = messages.getString("account.edit.salutation.option", (String) userInfo
                            .get("user.name.prefix"));
                    userInfo.put("user.custom.ingrid.user.salutation.full", salutationFull);
                    userInfo.put("login", userName);

                    // update password only if a old password was provided
                    String oldPassword = f.getInput(AdminUserForm.FIELD_PW_OLD);
                    String newPassword = f.getInput(AdminUserForm.FIELD_PW_NEW);
                    String confirmId = user.getInfoMap().get("user.custom.ingrid.user.confirmid");
                    
                    if(newPassword != null && newPassword.length() > 0){
                        // generate login id
                        confirmId = Utils.getMD5Hash(userName.concat(newPassword).concat(
                                Long.toString(System.currentTimeMillis())));
                        user.getSecurityAttributes().getAttribute("user.custom.ingrid.user.confirmid", true).setStringValue(confirmId);
                        userManager.updateUser(user);

                        if(isAdmin){
                            credential.setPassword(null, newPassword);
                        }else{
                            if (oldPassword != null && oldPassword.length() > 0) {
                                credential.setPassword(oldPassword, newPassword);
                            }
                        }
                        userManager.storePasswordCredential(credential);
                    }
                    if (isUserSendInfo || isUserPwUpdateRequired) {
                        if(isUserSendInfo) {
                            userInfo.put("password", newPassword);
                        }
                        if (isUserPwUpdateRequired) {
                            userInfo.put("returnURL", generateReturnURL(request, response, userName, confirmId, user.getInfoMap().get("user.business-info.online.email")));
                        }
                        userInfo.put("isUpdate", "true");
                        sendMail(request, f, messages, userInfo);
                    }
                } catch (PasswordAlreadyUsedException e) {
                    f.setError(AdminUserForm.FIELD_PW_NEW, "account.edit.error.password.in.use");
                    return;
                } catch (SecurityException e) {
                    f.setError(AdminUserForm.FIELD_PW_OLD, "account.edit.error.wrong.password");
                    return;
                }
            // also check for superadmin to be sure !
            } else if (f.getInput(AdminUserForm.FIELD_TAB).equals("2") && isAdmin) {
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
    @Override
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
                                } catch (FolderNotFoundException | NodeException e1) {
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
    @Override
    public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {

        String action = getAction(request);

        if (request.getParameter(PARAMV_ACTION_DB_DO_UPDATE) != null) {
            // call sub method
            doActionUpdate(request, response);
            // reset the sction to edit, to show the edit screen and collect all
            // necessary data
            response.setRenderParameter(Settings.PARAM_ACTION, PARAMV_ACTION_DO_EDIT);
            response.setRenderParameter("cmd", "action processed");
        } else if (request.getParameter(PARAMV_ACTION_DB_DO_SAVE) != null) {
            // call sub method
            doActionSave(request, response);
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
    @Override
    protected boolean doViewEdit(RenderRequest request) {

        try {

            AdminUserForm f = (AdminUserForm) Utils.getActionForm(request, AdminUserForm.SESSION_KEY,
                AdminUserForm.class);

            Context context = getContext(request);
            context.put(CONTEXT_MODE, CONTEXTV_MODE_EDIT);
            context.put(CONTEXT_UTILS_STRING, new UtilsString());
            context.put("isAdmin", true);

            String cmd = request.getParameter("cmd");
            if (cmd == null) {
                f.clear();
                fillEditFormFromStorage(request, f);
            }

            context.put("actionForm", f);

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
    @Override
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
        context.put("loginLength", PortalConfig.getInstance().getInt(PortalConfig.PORTAL_FORM_LENGTH_CHECK_LOGIN, 4));
        setDefaultViewPage(viewNew);
        return true;
    }

    @Override
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
    protected static List getEntitiesFromSession(PortletRequest request) {
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
    protected static void setEntitiesInSession(PortletRequest request, List entities) {
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
            f.setInput(AdminUserForm.FIELD_CHK_ENABLED, user.isEnabled() ? "1": "0");

            // set admin-portal role
            Collection<Role> userRoles = roleManager.getRolesForUser(user.getName());
            for (Role r : userRoles) {
                if (IngridRole.ROLE_ADMIN_PORTAL.equals(r.getName())) {
                    f.setInput(AdminUserForm.FIELD_CHK_ADMIN_PORTAL, "1");
                }
            }

            // set type of layout 
            // superadmin can apply role "admin-portal" to user !
            String layoutPermission = "";
            if ("admin".equals(request.getUserPrincipal().getName())) {
                layoutPermission = "admin";
            }
            f.setInput(AdminUserForm.FIELD_LAYOUT_PERMISSION, layoutPermission);


        } catch (Exception e) {
            log.error("Problems fetching user data.", e);
        }

    }

    /** Replaces the input with "" if input is null. */
    private String replaceNull(String input) {
        return input == null ? "" : input;
    }

    protected List<String> getInitParameterList(PortletConfig config, String ipName) {
        String temp = config.getInitParameter(ipName);
        if (temp == null)
            return new ArrayList<>();

        String[] temps = temp.split("\\,");
        for (int ix = 0; ix < temps.length; ix++)
            temps[ix] = temps[ix].trim();

        return Arrays.asList(temps);
    }
    private void sendMail(ActionRequest request, AdminUserForm f, IngridResourceBundle messages, HashMap<String, String> userInfo) {
        sendMail(request, f, messages, userInfo, false);
    }
    private void sendMail(ActionRequest request, AdminUserForm f, IngridResourceBundle messages, HashMap<String, String> userInfo, boolean isCreateUser) {
        String language = Utils.checkSupportedLanguage(request.getLocale().getLanguage());
        String localizedTemplatePath = this.emailTemplate;
        if (localizedTemplatePath == null) {
            log.error("email template not available");
            f.setError("nofield", "account.created.problems.email");
            return;
        }
        int period = localizedTemplatePath.lastIndexOf('.');
        if (period > 0) {
            String fixedTempl = localizedTemplatePath.substring(0, period) + "_" + language + "."
                    + localizedTemplatePath.substring(period + 1);
            if (new File(getPortletContext().getRealPath(fixedTempl)).exists()) {
                this.emailTemplate = fixedTempl;
                localizedTemplatePath = fixedTempl;
            }
        }

        String emailSubject = messages.getString("account.create.confirmation.email.subject");

        if(!isCreateUser) {
            emailSubject = messages.getString("account.edit.confirmation.email.subject");
        }
        String from = PortalConfig.getInstance().getString(PortalConfig.EMAIL_REGISTRATION_CONFIRMATION_SENDER,
            "foo@bar.com");
        String to = userInfo.get("user.business-info.online.email");
        String text = Utils.mergeTemplate(getPortletConfig(), userInfo, "map", localizedTemplatePath);
        if (Utils.sendEmail(from, emailSubject, new String[] { to }, text, null)) {
            if(isCreateUser) {
                f.addMessage("account.created.title");
            }
        } else {
            f.setError("", "account.created.problems.email");
        }
    }

    private String generateReturnURL(PortletRequest request, PortletResponse response, String userName, String urlGUID, String userEmail) {
        String userId = Utils.getMD5Hash(userName.concat(userEmail).concat(urlGUID));
        String fullPath = this.returnURL + "?userChangeId=" + userId + "&userEmail=" + userEmail;
        
        String hostname = PortalConfig.getInstance().getString(PortalConfig.EMAIL_REGISTRATION_CONFIRMATION_URL);
        // NOTE: getPortalURL will encode the fullPath for us
        if(hostname != null && hostname.length() > 0){
            return hostname.concat(fullPath);
        }else{
            return admin.getPortalURL(request, response, fullPath);
        }
    }
}
