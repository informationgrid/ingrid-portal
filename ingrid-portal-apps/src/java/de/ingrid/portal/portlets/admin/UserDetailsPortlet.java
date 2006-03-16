/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.ingrid.portal.portlets.admin;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.prefs.Preferences;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.security.auth.Subject;

import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.container.JetspeedPortletContext;
import org.apache.jetspeed.om.common.SecurityConstraints;
import org.apache.jetspeed.om.common.UserAttribute;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.profiler.Profiler;
import org.apache.jetspeed.profiler.rules.PrincipalRule;
import org.apache.jetspeed.security.Group;
import org.apache.jetspeed.security.GroupManager;
import org.apache.jetspeed.security.InvalidNewPasswordException;
import org.apache.jetspeed.security.InvalidPasswordException;
import org.apache.jetspeed.security.PasswordAlreadyUsedException;
import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.Role;
import org.apache.jetspeed.security.RoleManager;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.jetspeed.security.om.InternalCredential;
import org.apache.portals.bridges.beans.TabBean;
import org.apache.portals.bridges.common.GenericServletPortlet;
import org.apache.portals.bridges.util.PreferencesHelper;
import org.apache.portals.messaging.PortletMessaging;

import de.ingrid.portal.portlets.security.SecurityResources;
import de.ingrid.portal.portlets.security.SecurityUtil;

/**
 * This portlet is a tabbed editor user interface for editing user attributes
 * and security definitions.
 *
 * @author <a href="mailto:jford@apache.com">Jeremy Ford</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: UserDetailsPortlet.java 348264 2005-11-22 22:06:45Z taylor $
 */
public class UserDetailsPortlet extends GenericServletPortlet
{
    private final String VIEW_USER = "user"; 
    private final String VIEW_ROLES = "roles";
    private final String VIEW_GROUPS = "groups";
    private final String VIEW_RULES = "rules";
    private final String VIEW_CREDENTIAL = "credential"; 
    private final String VIEW_ALL_RULES = "prules";
    private final String VIEW_PA_USER_ATTRIBUTES = "paUserAttributes";
    
    private final String USER_ACTION_PREFIX = "security_user.";
    private final String ACTION_EDIT_USER = "edit_user";
    private final String ACTION_UPDATE_ATTRIBUTE = "update_user_attribute";
    private final String ACTION_REMOVE_ATTRIBUTE = "remove_user_attribute";
    private final String ACTION_ADD_ATTRIBUTE = "add_user_attribute";
    private final String ACTION_REMOVE_ROLE = "remove_user_role";
    private final String ACTION_ADD_ROLE = "add_user_role";
    private final String ACTION_REMOVE_GROUP = "remove_user_group";
    private final String ACTION_ADD_GROUP = "add_user_group";
    private final String ACTION_REMOVE_RULE = "remove_user_rule";
    private final String ACTION_ADD_RULE = "add_rule";
    private final String ACTION_UPDATE_CREDENTIAL = "update_user_credential";
    
    private final String TAB_USER = "user";
    private final String TAB_ATTRIBUTES = "user_attributes";
    private final String TAB_ROLE = "user_role";
    private final String TAB_GROUP = "user_group";
    private final String TAB_PROFILE = "user_profile";
    private final String TAB_CREDENTIAL = "user_credential";
    
    /** the id of the roles control */
    private static final String ROLES_CONTROL = "jetspeedRoles";
    
    /** the id of the rules control */
    private static final String RULES_CONTROL = "jetspeedRules";
    

    /** the id of the groups control */
    private static final String GROUPS_CONTROL = "jetspeedGroups";
    
    private PageManager pageManager;
    private UserManager userManager;
    private RoleManager roleManager;
    private GroupManager groupManager;
    private Profiler profiler;
    private PortletRegistry registry;
    private String paIdentifier;
    private Collection paUserAttributes;
    private boolean initPrefsAndAttr;
    
    private LinkedHashMap userTabMap;
    private LinkedHashMap anonymousUserTabMap;
    
    public void init(PortletConfig config)
    throws PortletException 
    {
        super.init(config);
        userManager = (UserManager)getPortletContext().getAttribute(CommonPortletServices.CPS_USER_MANAGER_COMPONENT);
        if (null == userManager)
        {
            throw new PortletException("Failed to find the User Manager on portlet initialization");
        }
        roleManager = (RoleManager)getPortletContext().getAttribute(CommonPortletServices.CPS_ROLE_MANAGER_COMPONENT);
        if (null == roleManager)
        {
            throw new PortletException("Failed to find the Role Manager on portlet initialization");
        }
        groupManager = (GroupManager)getPortletContext().getAttribute(CommonPortletServices.CPS_GROUP_MANAGER_COMPONENT);
        if (null == groupManager)
        {
            throw new PortletException("Failed to find the Group Manager on portlet initialization");
        }
        profiler = (Profiler)getPortletContext().getAttribute(CommonPortletServices.CPS_PROFILER_COMPONENT);
        if (null == profiler)
        {
            throw new PortletException("Failed to find the Profiler on portlet initialization");
        }        
        registry = (PortletRegistry)getPortletContext().getAttribute(CommonPortletServices.CPS_REGISTRY_COMPONENT);
        if (null == registry)
        {
            throw new PortletException("Failed to find the Portlet Registry on portlet initialization");
        }
        
        pageManager = (PageManager)getPortletContext().getAttribute(CommonPortletServices.CPS_PAGE_MANAGER_COMPONENT);
        if (null == pageManager)
        {
            throw new PortletException("Failed to find the Page Manager on portlet initialization");
        }
        
        paIdentifier = ((MutablePortletApplication)((JetspeedPortletContext)config.getPortletContext())
                .getApplication()).getApplicationIdentifier();
    }
    
    
    public void doView(RenderRequest request, RenderResponse response)
    throws PortletException, IOException
    {
        response.setContentType("text/html");

        if ( !initPrefsAndAttr )
        {
            initPrefsAndAttr(request);
        }
        
        String userName = (String)PortletMessaging.receive(request, 
                                SecurityResources.TOPIC_USERS, SecurityResources.MESSAGE_SELECTED);

        User user = null;
        if (userName != null)
        {
            user = lookupUser(request, userName);
        }
        
        if (user != null)
        {       
            LinkedHashMap tabMap = null;
            if ( userManager.getAnonymousUser().equals(userName) )
            {
                tabMap = anonymousUserTabMap;
            }
            else
            {
                tabMap = userTabMap;
            }
            
            // Tabs
            request.setAttribute("tabs", tabMap.values());        
            TabBean selectedTab = 
                (TabBean) request.getPortletSession().getAttribute(SecurityResources.REQUEST_SELECT_TAB);

            if(selectedTab != null && !tabMap.containsKey(selectedTab.getId()))
            {
                selectedTab = null;
            }
            
            if(selectedTab == null)
            {
                selectedTab = (TabBean) tabMap.values().iterator().next();
            }
            JetspeedUserBean bean = new JetspeedUserBean(user);
            request.setAttribute(VIEW_USER, bean);
            
            if (selectedTab.getId().equals(TAB_USER))
            {
                request.setAttribute(VIEW_PA_USER_ATTRIBUTES, paUserAttributes);
                if ( "true".equals(request.getPreferences().getValue("showPasswordOnUserTab", "false")))
                {
                    request.setAttribute(VIEW_CREDENTIAL, getCredential(request, userName));
                }
            }
            else if (selectedTab.getId().equals(TAB_ROLE))
            {                
                Collection userRoles = getRoles(request, userName);
                request.setAttribute(VIEW_ROLES, userRoles );
                
                // check for refresh on roles list
                String refreshRoles = (String)PortletMessaging.consume(request, SecurityResources.TOPIC_USERS, "roles");
                List roles = null;
                if (refreshRoles == null)
                {        
                    roles = (List) request.getPortletSession().getAttribute(ROLES_CONTROL);
                }
                
                // build the roles control and provide it to the view
                try
                {
                    if (roles == null)
                    {
                        roles = new LinkedList();
                        Iterator fullRoles = roleManager.getRoles("");
                        while (fullRoles.hasNext())
                        {
                            Role role = (Role)fullRoles.next();
                            roles.add(role.getPrincipal().getName());
                        }
                        request.getPortletSession().setAttribute(ROLES_CONTROL, roles);
                    }
                }
                catch (SecurityException se)
                {
                    throw new PortletException(se);
                }
                ArrayList selectableRoles = new ArrayList(roles);
                Iterator rolesIter = userRoles.iterator();
                while ( rolesIter.hasNext() )
                {
                    Role role = (Role)rolesIter.next();
                    int index = selectableRoles.indexOf(role.getPrincipal().getName());
                    if (index != -1)
                    {
                        selectableRoles.remove(index);
                    }
                }
                request.setAttribute(ROLES_CONTROL, selectableRoles);
                
            }
            else if (selectedTab.getId().equals(TAB_GROUP))
            {
                Collection userGroups = getGroups(request, userName);
                request.setAttribute(VIEW_GROUPS, userGroups);
                
                // check for refresh on groups list
                String refreshGroups = (String)PortletMessaging.consume(request, SecurityResources.TOPIC_USERS, "groups");
                List groups = null;
                if (refreshGroups == null)
                {        
                    groups = (List) request.getPortletSession().getAttribute(GROUPS_CONTROL);
                }
                
                // build the groups control and provide it to the view
                try
                {
                    if (groups == null)
                    {
                        groups = new LinkedList();
                        Iterator fullGroups = groupManager.getGroups("");
                        while (fullGroups.hasNext())
                        {
                            Group group = (Group)fullGroups.next();
                            groups.add(group.getPrincipal().getName());
                        }
                        request.getPortletSession().setAttribute(GROUPS_CONTROL, groups);
                    }
                }
                catch (SecurityException se)
                {
                    throw new PortletException(se);
                }        
                ArrayList selectableGroups = new ArrayList(groups);
                Iterator groupsIter = userGroups.iterator();
                while ( groupsIter.hasNext() )
                {
                    Group group = (Group)groupsIter.next();
                    int index = selectableGroups.indexOf(group.getPrincipal().getName());
                    if (index != -1)
                    {
                        selectableGroups.remove(index);
                    }
                }
                request.setAttribute(GROUPS_CONTROL, selectableGroups);
                
            }
            else if (selectedTab.getId().equals(TAB_PROFILE))
            {
                request.setAttribute(VIEW_RULES, getRules(user));
                request.setAttribute(VIEW_ALL_RULES, getProfilerRules());
            }
            else if (selectedTab.getId().equals(TAB_CREDENTIAL))
            {
                request.setAttribute(VIEW_CREDENTIAL, getCredential(request, userName));
            }
           
            request.setAttribute(SecurityResources.REQUEST_SELECT_TAB, selectedTab);
        }
        else
        {
            renderRoleInformation(request);
            renderProfileInformation(request);            
        }
        // check for ErrorMessages
        ArrayList errorMessages = (ArrayList)PortletMessaging.consume(request, SecurityResources.TOPIC_USER, SecurityResources.ERROR_MESSAGES);
        if (errorMessages != null )
        {
            request.setAttribute(SecurityResources.ERROR_MESSAGES, errorMessages);
        }
        
        super.doView(request, response);
    }
    
    protected void initPrefsAndAttr(PortletRequest request)
    {
        initPrefsAndAttr = true;
        if ( userTabMap == null )
        {
            userTabMap = new LinkedHashMap();
            anonymousUserTabMap = new LinkedHashMap();
        }
        else
        {
            userTabMap.clear();
            anonymousUserTabMap.clear();
        }        
        
        TabBean tb;
        PortletPreferences prefs = request.getPreferences();
        
        if ( "true".equals(prefs.getValue("showUserTab", "true")) )
        {
            tb = new TabBean(TAB_USER);
            userTabMap.put(tb.getId(), tb);
        }
        if ( "true".equals(prefs.getValue("showAttributesTab", "true")) )
        {
            tb = new TabBean(TAB_ATTRIBUTES);
            userTabMap.put(tb.getId(), tb);
        }
        if ( "true".equals(prefs.getValue("showPasswordTab", "true")) )
        {
            tb = new TabBean(TAB_CREDENTIAL);
            userTabMap.put(tb.getId(), tb);
        }
        if ( "true".equals(prefs.getValue("showRoleTab", "true")) )
        {
            tb = new TabBean(TAB_ROLE);
            userTabMap.put(tb.getId(), tb);
            anonymousUserTabMap.put(tb.getId(), tb);
        }
        if ( "true".equals(prefs.getValue("showGroupTab", "true")) )
        {
            tb = new TabBean(TAB_GROUP);
            userTabMap.put(tb.getId(), tb);
            anonymousUserTabMap.put(tb.getId(), tb);
        }
        if ( "true".equals(prefs.getValue("showProfileTab", "true")) )
        {
            tb = new TabBean(TAB_PROFILE);
            userTabMap.put(tb.getId(), tb);
            anonymousUserTabMap.put(tb.getId(), tb);
        }
        // refresh PA UserAttributes (kinda hack but can't communicate between PAM and Security PA yet to signal a refresh is needed)
        paUserAttributes = registry.getPortletApplicationByIdentifier(paIdentifier).getUserAttributes();
    }

    protected void renderRoleInformation(RenderRequest request)
    throws PortletException
    {
        // check for refresh on roles list
        String refreshRoles = (String)PortletMessaging.consume(request, SecurityResources.TOPIC_USERS, SecurityResources.MESSAGE_REFRESH_ROLES);
        List roles = null;
        if (refreshRoles == null)
        {        
            roles = (List) request.getPortletSession().getAttribute(ROLES_CONTROL);
        }
        
        // build the roles control and provide it to the view
        try
        {
            if (roles == null)
            {
                roles = new LinkedList();
                Iterator fullRoles = roleManager.getRoles("");
                while (fullRoles.hasNext())
                {
                    Role role = (Role)fullRoles.next();
                    roles.add(role.getPrincipal().getName());
                }
                request.getPortletSession().setAttribute(ROLES_CONTROL, roles);
            }
        }
        catch (SecurityException se)
        {
            throw new PortletException(se);
        }        
        request.setAttribute(ROLES_CONTROL, roles);        
    }
    
    protected void renderProfileInformation(RenderRequest request)
    {
        // check for refresh on profiles list
        String refreshProfiles = (String)PortletMessaging.consume(request, 
                        SecurityResources.TOPIC_USERS, SecurityResources.MESSAGE_REFRESH_PROFILES);
        Collection rules = null;
        if (refreshProfiles == null)
        {        
            rules = (Collection) request.getPortletSession().getAttribute(RULES_CONTROL);
        }
        
        // build the profiles control and provide it to the view
        if (rules == null)
        {
            rules = profiler.getRules();
            request.getPortletSession().setAttribute(RULES_CONTROL, rules);
        }
        request.setAttribute(RULES_CONTROL, rules);        
    }
    
    public void doEdit(RenderRequest request, RenderResponse response)
    throws PortletException, IOException
    {
        response.setContentType("text/html");
        renderRoleInformation(request);
        renderProfileInformation(request);            
        super.doEdit(request, response);
    }

    public void processAction(ActionRequest actionRequest, ActionResponse actionResponse) 
        throws PortletException, IOException
    {   
        if (actionRequest.getPortletMode() == PortletMode.EDIT)
        {
            PortletPreferences prefs = actionRequest.getPreferences();
            PreferencesHelper.requestParamsToPreferences(actionRequest);
            prefs.store();
            actionResponse.setPortletMode(PortletMode.VIEW);
            initPrefsAndAttr(actionRequest);
            return;
        }
        
        String selectedTab = actionRequest.getParameter(SecurityResources.REQUEST_SELECT_TAB);
        if (selectedTab != null)
        {
            TabBean tab = (TabBean) userTabMap.get(selectedTab);
            if (tab != null)
            {
                actionRequest.getPortletSession().setAttribute(
                        SecurityResources.REQUEST_SELECT_TAB, tab);
            }            
        }             
        String action = actionRequest.getParameter(SecurityResources.PORTLET_ACTION);
        if (action != null && action.equals("remove.user"))
        {
            removeUser(actionRequest, actionResponse);
        }
        else if (action != null && action.equals("add.new.user"))
        {
            PortletMessaging.cancel(actionRequest, SecurityResources.TOPIC_USERS, SecurityResources.MESSAGE_SELECTED);
        }
        else if (action != null && action.equals("add.user"))
        {
            addUser(actionRequest);
        }
        else if (action != null && isUserPortletAction(action))
        {
            action = getAction(USER_ACTION_PREFIX, action);                
            if (action.endsWith(ACTION_EDIT_USER))
            {
                editUser(actionRequest, actionResponse);
            }
            else if (action.endsWith(ACTION_UPDATE_ATTRIBUTE))
            {
                updateUserAttribute(actionRequest, actionResponse);
            }
            else if (action.endsWith(ACTION_REMOVE_ATTRIBUTE))
            {
                removeUserAttributes(actionRequest, actionResponse);
            }
            else if (action.endsWith(ACTION_ADD_ATTRIBUTE))
            {
                addUserAttribute(actionRequest, actionResponse);
            }
            else if (action.endsWith(ACTION_REMOVE_ROLE))
            {
                removeUserRoles(actionRequest, actionResponse);
            }
            else if (action.endsWith(ACTION_ADD_ROLE))
            {
                addUserRole(actionRequest, actionResponse);
            }
            else if (action.endsWith(ACTION_REMOVE_GROUP))
            {
                removeUserGroups(actionRequest, actionResponse);
            }
            else if (action.endsWith(ACTION_ADD_GROUP))
            {
                addUserGroup(actionRequest, actionResponse);
            }
            else if (action.endsWith(this.ACTION_ADD_RULE))
            {
                addUserProfile(actionRequest, actionResponse);
            }
            else if (action.endsWith(this.ACTION_REMOVE_RULE))
            {
                removeUserProfile(actionRequest, actionResponse);
            }
            else if (action.endsWith(this.ACTION_UPDATE_CREDENTIAL))
            {
                updateUserCredential(actionRequest, actionResponse);
            }
        }
    }
        
    public void removeUser(ActionRequest actionRequest, ActionResponse actionResponse) 
    throws PortletException
    {
        String userName = (String)PortletMessaging.receive(actionRequest, 
                SecurityResources.TOPIC_USERS, SecurityResources.MESSAGE_SELECTED);        
        User user = lookupUser(actionRequest, userName);
        if (user != null)
        {
            try
            {
                userManager.removeUser(userName);
                PortletMessaging.publish(actionRequest, SecurityResources.TOPIC_USERS, SecurityResources.MESSAGE_REFRESH, "true");
                
                // TODO: remove ALL user from PSML
                Page page = pageManager.getPage(Folder.USER_FOLDER + userName + "/default-page.psml");
                pageManager.removePage(page);
                
                Folder folder = pageManager.getFolder(Folder.USER_FOLDER + userName);
                pageManager.removeFolder(folder);
                            
                // remove selected user from USERS_TOPIC
                PortletMessaging.cancel(actionRequest,SecurityResources.TOPIC_USERS, SecurityResources.MESSAGE_SELECTED);
                // TODO: send message to site manager portlet
                
            }
            catch (Exception ex)
            {
                SecurityUtil.publishErrorMessage(actionRequest, SecurityResources.TOPIC_USER, ex.getMessage());
            }
        }
    }
    
    public Principal createPrincipal(Subject subject, Class classe)
    {
        Principal principal = null;
        Iterator principals = subject.getPrincipals().iterator();
        while (principals.hasNext())
        {
            Principal p = (Principal) principals.next();
            if (classe.isInstance(p))
            {
                principal = p;
                break;
            }
        }
        return principal;
    }    

    private void updateUserCredential(ActionRequest actionRequest, ActionResponse actionResponse)
    {
        ResourceBundle bundle = ResourceBundle.getBundle("org.apache.jetspeed.portlets.security.resources.UsersResources",actionRequest.getLocale());

        String userName = (String)PortletMessaging.receive(actionRequest, 
                SecurityResources.TOPIC_USERS, SecurityResources.MESSAGE_SELECTED);
        User user = lookupUser(actionRequest, userName);
        if (user != null)
        {
            try
            {
                String password = actionRequest.getParameter("user_cred_value");
                boolean passwordSet = false;
                if ( password != null && password.trim().length() > 0 )
                {
                    userManager.setPassword(userName, null, password);
                    passwordSet = true;
                }
                PasswordCredential credential = getCredential(actionRequest, userName);
                if ( credential != null )
                {
                    String updateRequiredStr = actionRequest.getParameter("user_cred_updreq");
                    if (updateRequiredStr != null)
                    {
                        boolean updateRequired = Boolean.valueOf(updateRequiredStr).booleanValue();
                        if (updateRequired != credential.isUpdateRequired())
                        {
                            userManager.setPasswordUpdateRequired(userName,updateRequired);
                        }
                    }
                    String enabledStr = actionRequest.getParameter("user_cred_enabled");
                    if (enabledStr != null)
                    {
                        boolean enabled = Boolean.valueOf(enabledStr).booleanValue();
                        if (enabled != credential.isEnabled())
                        {
                            userManager.setPasswordEnabled(userName,enabled);
                        }
                    }
                    String expiredFlagStr = actionRequest.getParameter("user_expired_flag");
                    if (expiredFlagStr != null)
                    {
                        if ( !passwordSet && expiredFlagStr.equals("expired"))
                        {
                            java.sql.Date today = new java.sql.Date(new Date().getTime());
                            userManager.setPasswordExpiration(userName,today);
                        }
                        else if (expiredFlagStr.equals("extend"))
                        {
                            userManager.setPasswordExpiration(userName,null);
                        }
                        else if (expiredFlagStr.equals("unlimited"))
                        {
                            userManager.setPasswordExpiration(userName,InternalCredential.MAX_DATE);
                        }
                    }
                }
            }
            catch ( InvalidPasswordException ipe )
            {
                SecurityUtil.publishErrorMessage(actionRequest, SecurityResources.TOPIC_USER, bundle.getString("chgpwd.error.invalidPassword"));
            }
            catch ( InvalidNewPasswordException inpe )
            {
                SecurityUtil.publishErrorMessage(actionRequest, SecurityResources.TOPIC_USER, bundle.getString("chgpwd.error.invalidNewPassword"));
            }
            catch ( PasswordAlreadyUsedException paue )
            {
                SecurityUtil.publishErrorMessage(actionRequest, SecurityResources.TOPIC_USER, bundle.getString("chgpwd.error.passwordAlreadyUsed"));
            }
            catch (SecurityException e)
            {
                SecurityUtil.publishErrorMessage(actionRequest, SecurityResources.TOPIC_USER, e.getMessage());
            }
        }
    }
    
    private void editUser(ActionRequest actionRequest, ActionResponse actionResponse)
    {
        String userName = (String)PortletMessaging.receive(actionRequest, 
                SecurityResources.TOPIC_USERS, SecurityResources.MESSAGE_SELECTED);
        User user = lookupUser(actionRequest, userName);
        if (user != null)
        {
            Iterator attrIter = paUserAttributes.iterator();
            UserAttribute attr;
            String value;
            while( attrIter.hasNext() )
            {
                attr = (UserAttribute)attrIter.next();
                value = actionRequest.getParameter("attr_"+attr.getName());
                if (value != null)
                {
                    user.getUserAttributes().put(attr.getName(), value);
                }
            }
        }
        if ( "true".equals(actionRequest.getPreferences().getValue("showPasswordOnUserTab", "false")))
        {
            updateUserCredential(actionRequest, actionResponse);
        }
    }
    
    private void updateUserAttribute(ActionRequest actionRequest, ActionResponse actionResponse)
    {
        String userName = (String)PortletMessaging.receive(actionRequest, 
                SecurityResources.TOPIC_USERS, SecurityResources.MESSAGE_SELECTED);
        User user = lookupUser(actionRequest, userName);
        if (user != null)
        {
            String[] userAttrNames = actionRequest.getParameterValues("user_attr_id");
            if(userAttrNames != null)
            {                
                for (int i=0; i<userAttrNames.length; i++)
                {
                    String userAttrName = userAttrNames[i];
                    String value = actionRequest.getParameter(userAttrName + ":value");
                    user.getUserAttributes().put(userAttrName, value);
                }                
            }        
        }
    }
    
    private void addUserAttribute(ActionRequest actionRequest, ActionResponse actionResponse)
    {
        String userName = (String)PortletMessaging.receive(actionRequest, 
                SecurityResources.TOPIC_USERS, SecurityResources.MESSAGE_SELECTED);        
        User user = lookupUser(actionRequest, userName);
        if (user != null)
        {
            String userAttrName = actionRequest.getParameter("user_attr_name");
            String userAttrValue = actionRequest.getParameter("user_attr_value");
            if (userAttrName != null && userAttrName.trim().length() > 0)
            {
                Preferences attributes = user.getUserAttributes();
                attributes.put(userAttrName, userAttrValue);
            }
        }
    }

    private void removeUserAttributes(ActionRequest actionRequest, ActionResponse actionResponse)
    {
        String userName = (String)PortletMessaging.receive(actionRequest, 
                SecurityResources.TOPIC_USERS, SecurityResources.MESSAGE_SELECTED);        
        
        User user = lookupUser(actionRequest, userName);
        if (user != null)
        {
            String[] userAttrNames = actionRequest.getParameterValues("user_attr_id");

            if(userAttrNames != null)
            {
                Preferences attributes = user.getUserAttributes();
                for(int ix = 0; ix < userAttrNames.length; ix++)
                {
                    try
                    {
                        attributes.remove(userAttrNames[ix]);
                    }
                    catch (Exception e) 
                    {
                      e.printStackTrace();  
                    }
                }
            }            
        }
    }
    
    private void removeUserRoles(ActionRequest actionRequest, ActionResponse actionResponse)
    {
        String userName = (String)PortletMessaging.receive(actionRequest, 
                SecurityResources.TOPIC_USERS, SecurityResources.MESSAGE_SELECTED);
        User user = lookupUser(actionRequest, userName);
        if (user != null)
        {
            String[] roleNames = actionRequest.getParameterValues("user_role_id");

            if(roleNames != null)
            {
                for (int ix = 0; ix < roleNames.length; ix++)
                {
                    try
                    {
                        if (roleManager.roleExists(roleNames[ix]))
                        {
                            roleManager.removeRoleFromUser(userName, roleNames[ix]);
                        }
                    }
                    catch (SecurityException e)
                    {
                        SecurityUtil.publishErrorMessage(actionRequest, SecurityResources.TOPIC_USER, e.getMessage());
                    }                
                }
            }            
        }
    }    
    
    private void addUserRole(ActionRequest actionRequest, ActionResponse actionResponse)
    {
        String userName = (String)PortletMessaging.receive(actionRequest, 
                SecurityResources.TOPIC_USERS, SecurityResources.MESSAGE_SELECTED);       
        User user = lookupUser(actionRequest, userName);
        if (user != null)
        {
            String roleName = actionRequest.getParameter("role_name");
            if (roleName != null && roleName.trim().length() > 0)
            {
                try
                {
                    roleManager.addRoleToUser(userName, roleName);
                }
                catch (SecurityException e)
                {
                    SecurityUtil.publishErrorMessage(actionRequest, SecurityResources.TOPIC_USER, e.getMessage());
                }
            }
        }
    }
    
    private void removeUserGroups(ActionRequest actionRequest, ActionResponse actionResponse)
    {
        String userName = (String)PortletMessaging.receive(actionRequest, 
                SecurityResources.TOPIC_USERS, SecurityResources.MESSAGE_SELECTED);
        User user = lookupUser(actionRequest, userName);
        if (user != null)
        {
            String[] groupNames = actionRequest.getParameterValues("user_group_id");

            if(groupNames != null)
            {
                for (int ix = 0; ix < groupNames.length; ix++)
                {
                    try
                    {
                        if (groupManager.groupExists(groupNames[ix]))
                        {
                            groupManager.removeUserFromGroup(userName, groupNames[ix]);
                        }
                    }
                    catch (SecurityException e)
                    {
                        SecurityUtil.publishErrorMessage(actionRequest, SecurityResources.TOPIC_USER, e.getMessage());
                    }                
                }
            }            
        }
    }    
    
    private void addUserGroup(ActionRequest actionRequest, ActionResponse actionResponse)
    {
        String userName = (String)PortletMessaging.receive(actionRequest, 
                SecurityResources.TOPIC_USERS, SecurityResources.MESSAGE_SELECTED);
        User user = lookupUser(actionRequest, userName);
        if (user != null)
        {
            String groupName = actionRequest.getParameter("group_name");
            if (groupName != null && groupName.trim().length() > 0)
            {
                try
                {
                    groupManager.addUserToGroup(userName, groupName);
                }
                catch (SecurityException e)
                {
                    SecurityUtil.publishErrorMessage(actionRequest, SecurityResources.TOPIC_USER, e.getMessage());
                }
            }
        }
    }
        
    private String getAction(String prefix, String action)
    {
        return action.substring(prefix.length());
    }

    private boolean isUserPortletAction(String action)
    {
        return action.startsWith(USER_ACTION_PREFIX);
    }
    
    private Collection getRoles(PortletRequest request, String userName)
    {
        try
        {
            return roleManager.getRolesForUser(userName); 
        }
        catch (SecurityException e)
        {
            SecurityUtil.publishErrorMessage(request, SecurityResources.TOPIC_USER, e.getMessage());
        }
        return new LinkedList();
    }
    
    private Collection getGroups(PortletRequest request, String userName)
    {
        try
        {
            return groupManager.getGroupsForUser(userName); 
        }
        catch (SecurityException e)
        {
            SecurityUtil.publishErrorMessage(request, SecurityResources.TOPIC_USER, e.getMessage());
        }
        return new LinkedList();
    }
    
    private PasswordCredential getCredential(User user)
    {
        PasswordCredential credential = null;
        
        Set credentials = user.getSubject().getPrivateCredentials();
        Iterator iter = credentials.iterator();
        while (iter.hasNext())
        {
            Object o = iter.next();
            if (o instanceof PasswordCredential)
            {
                credential = (PasswordCredential)o;
                break;
            }
        }
        return credential;
    }
    private PasswordCredential getCredential(PortletRequest request, String userName)
    {
        return getCredential(lookupUser(request, userName));
    }
    
    private User lookupUser(PortletRequest request, String userName)
    {
        User user = null;
        try
        {
            user = userManager.getUser(userName);
        }
        catch (Exception e)
        {
            SecurityUtil.publishErrorMessage(request, SecurityResources.TOPIC_USER, e.getMessage());
        }    
        return user;
    }
    
    private Collection getProfilerRules()
    {        
        return profiler.getRules();
    }
    
    private Collection getRules(User user)
    {
        Principal userPrincipal = createPrincipal(user.getSubject(), UserPrincipal.class);
        return profiler.getRulesForPrincipal(userPrincipal);
    }

    private void addUserProfile(ActionRequest actionRequest, ActionResponse actionResponse)
    {
        String userName = (String)PortletMessaging.receive(actionRequest, 
                SecurityResources.TOPIC_USERS, SecurityResources.MESSAGE_SELECTED);
        User user = lookupUser(actionRequest, userName);
        if (user != null)
        {
            String locatorName = actionRequest.getParameter("locator_name");
            if (locatorName != null && locatorName.trim().length() > 0)
            {
                try
                {
                    Principal userPrincipal = createPrincipal(user.getSubject(), UserPrincipal.class);                          
                    String ruleName = actionRequest.getParameter("select_rule");
                    profiler.setRuleForPrincipal(userPrincipal, 
                            profiler.getRule(ruleName),
                            locatorName);                                                         
                }
                catch (Exception e)
                {
                    SecurityUtil.publishErrorMessage(actionRequest, SecurityResources.TOPIC_USER, e.getMessage());
                }
            }
            
        }
    }
    
    private void removeUserProfile(ActionRequest actionRequest, ActionResponse actionResponse)
    {
        String userName = (String)PortletMessaging.receive(actionRequest, 
                SecurityResources.TOPIC_USERS, SecurityResources.MESSAGE_SELECTED);
        User user = lookupUser(actionRequest, userName);
        if (user != null)
        {
            String[] locatorNames = actionRequest.getParameterValues("user_profile_id");

            if(locatorNames != null)
            {
                Principal userPrincipal = createPrincipal(user.getSubject(), UserPrincipal.class);                                              
                Collection rules = profiler.getRulesForPrincipal(userPrincipal);
                for (int ix = 0; ix < locatorNames.length; ix++)
                {
                    try
                    {
                        Iterator it = rules.iterator();
                        while (it.hasNext())
                        {
                            PrincipalRule rule = (PrincipalRule)it.next();
                            if (rule.getLocatorName().equals(locatorNames[ix]))
                            {
                                profiler.deletePrincipalRule(rule);
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        SecurityUtil.publishErrorMessage(actionRequest, SecurityResources.TOPIC_USER, e.getMessage());
                    }                
                }
            }                                    
        }
    }        
    
    protected void addUser(ActionRequest actionRequest)
    {
        String userName = actionRequest.getParameter("jetspeed.user");
        String password = actionRequest.getParameter("jetspeed.password");            
        if (!SecurityUtil.isEmpty(userName))
        {
            try
            {            
                if (SecurityUtil.isEmpty(password))
                {
                    throw new SecurityException(SecurityException.PASSWORD_REQUIRED);
                }
                userManager.addUser(userName, password);
                PortletMessaging.publish(actionRequest, SecurityResources.TOPIC_USERS, SecurityResources.MESSAGE_REFRESH, "true");
                PortletMessaging.publish(actionRequest, SecurityResources.TOPIC_USERS, SecurityResources.MESSAGE_SELECTED, userName);
                                
                User user = userManager.getUser(userName);
                
                PasswordCredential credential = getCredential(user);
                if ( credential != null )
                {
                    String updateRequiredStr = actionRequest.getParameter("user_cred_updreq");
                    if (updateRequiredStr != null)
                    {
                        boolean updateRequired = Boolean.valueOf(updateRequiredStr).booleanValue();
                        if (updateRequired != credential.isUpdateRequired())
                        {
                            userManager.setPasswordUpdateRequired(userName,updateRequired);
                        }
                    }                    
                }

                String role = actionRequest.getParameter(ROLES_CONTROL);
                if (!SecurityUtil.isEmpty(role) && user != null) 
                {
                    roleManager.addRoleToUser(userName, role);
                }
                
                // create user's home page and folder                                
                Folder folder = pageManager.newFolder(Folder.USER_FOLDER + userName);
                setSecurityConstraints(folder, userName);                
                pageManager.updateFolder(folder);
                
                String templateFolder = actionRequest.getPreferences().getValue("newUserTemplateDirectory", "/_user/template/");

                // TODO: copy the entire dir tree, not just the default-page.psml                 
                Page template = pageManager.getPage(templateFolder + "default-page.psml");                
                Page copy = pageManager.copyPage(template, Folder.USER_FOLDER + userName + "/default-page.psml");
                pageManager.updatePage(copy);
                
                // TODO: send message that site tree portlet invalidated
                
                String rule = actionRequest.getParameter(RULES_CONTROL);
                if (!SecurityUtil.isEmpty(rule) && user != null) 
                {
                    Principal principal = SecurityUtil.getPrincipal(user.getSubject(), UserPrincipal.class);                         
                    profiler.setRuleForPrincipal(principal, profiler.getRule(rule), "page");
                }                
            }
            catch (SecurityException sex)
            {
                SecurityUtil.publishErrorMessage(actionRequest, SecurityResources.TOPIC_USER, sex.getMessage());
            }
            catch (Exception ex)
            {
                SecurityUtil.publishErrorMessage(actionRequest, SecurityResources.TOPIC_USER, ex.getMessage());
            }
        }
    }
    
    private void setSecurityConstraints(Folder folder, String userName)
    {
        SecurityConstraints constraints = pageManager.newSecurityConstraints();
        constraints.setOwner(userName);
        folder.setSecurityConstraints(constraints);        
    }
}
