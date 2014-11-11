/*
 * **************************************************-
 * Ingrid Portal Base
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
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
package org.apache.jetspeed.administration;

import java.io.FileReader;
import java.io.StringWriter;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.FolderNotFoundException;
import org.apache.jetspeed.om.folder.InvalidFolderException;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.document.NodeException;
import org.apache.jetspeed.prefs.PreferencesProvider;
import org.apache.jetspeed.prefs.om.Node;
import org.apache.jetspeed.profiler.Profiler;
import org.apache.jetspeed.profiler.rules.ProfilingRule;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.GroupManager;
import org.apache.jetspeed.security.JSSubject;
import org.apache.jetspeed.security.RoleManager;
import org.apache.jetspeed.security.SecurityHelper;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * PortalAdministrationImpl
 * Implements aggregate portal administration functions:
 *  - Emails
 *  - Registration
 *  - Password Generation
 *  - 
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a href="mailto:chris@bluesunrise.com">Chris Schaefer</a>
 * @version $Id: $
 */

public class PortalAdministrationImpl implements PortalAdministration
{
    private final static Log log = LogFactory.getLog(PortalAdministrationImpl.class);
    
    /** administration services */
    protected Configuration config;
    protected UserManager userManager;
    protected RoleManager roleManager;
    protected GroupManager groupManager;
    protected PageManager pageManager;
    private PreferencesProvider preferences;    
    protected Profiler profiler;
    protected JavaMailSender mailSender;
    protected VelocityEngine velocityEngine;
    protected AdminUtil adminUtil;
    
    /** list of default roles for a registered user */
    protected List defaultRoles;
    /** list of default groups for a registered user */
    protected List defaultGroups;
    /** map of default profiling rules for a registered user */
    protected Map defaultRules;
    /** name of PSML Folder Template to clone from when registering new user */
    protected String folderTemplate;
    /** default administrative user */
    protected String adminUser;
        
    public PortalAdministrationImpl( UserManager userManager,
                                     RoleManager roleManager,
                                     GroupManager groupManager, 
                                     PageManager pageManager,
                                     PreferencesProvider preferences,
                                     Profiler profiler,
                                     JavaMailSender mailSender,
                                     VelocityEngine velocityEngine)
    {
        this.userManager = userManager;
        this.roleManager = roleManager;
        this.groupManager = groupManager;
        this.pageManager = pageManager;
        this.preferences = preferences;
        this.profiler = profiler;
        this.mailSender = mailSender;
        this.velocityEngine = velocityEngine;
        this.adminUtil = new AdminUtil();
    }

    public void start()
    {
        this.config = (Configuration) Jetspeed.getComponentManager().getComponent("portal_configuration");
        
        this.defaultRoles = 
            config.getList(PortalConfigurationConstants.REGISTRATION_ROLES_DEFAULT);
        this.defaultGroups = 
            config.getList(PortalConfigurationConstants.REGISTRATION_GROUPS_DEFAULT);
    
        Object[] profileRuleNames = (Object[]) 
            config.getList(PortalConfigurationConstants.PROFILER_RULE_NAMES_DEFAULT).toArray();
        Object[] profileRuleValues = (Object[])
            config.getList(PortalConfigurationConstants.PROFILER_RULE_VALUES_DEFAULT).toArray();
        defaultRules = new HashMap();
        if (profileRuleNames != null && profileRuleValues != null)
        {
            for (int ix = 0; ix < ((profileRuleNames.length < profileRuleValues.length) ? profileRuleNames.length : profileRuleValues.length); ix++)
            {
                defaultRules.put(profileRuleNames[ix], profileRuleValues[ix]);
            }
        }
        this.folderTemplate = 
            config.getString(PortalConfigurationConstants.PSML_TEMPLATE_FOLDER);
        this.adminUser = config.getString(PortalConfigurationConstants.USERS_DEFAULT_ADMIN);
        
    }
    
    public void registerUser(String userName, String password)
    throws RegistrationException
    {
        registerUser(userName, password, (List)null, null, null, null, null);
    }
        
    /* (non-Javadoc)
     * @see org.apache.jetspeed.administration.PortalAdministration#registerUser(java.lang.String, java.lang.String, java.util.Map, java.awt.List, java.awt.List, java.lang.String)
     */    
    public void registerUser(
            String userName, 
            String password, 
            List roles, 
            List groups, 
            Map userInfo, 
            Map rules, 
            String folderTemplate)
    throws RegistrationException    
    {
        try 
        {
            // create the user
            userManager.addUser(userName, password);
            User user = userManager.getUser(userName);
                        
            // assign roles to user
            if (roles == null || roles.isEmpty())
            {
                roles = this.defaultRoles;
            }
            if (roles != null)
            {
                Iterator roleList = roles.iterator();
                while (roleList.hasNext())
                {
                    String role = (String)roleList.next();
                    if (role.trim().length() > 0)
                        roleManager.addRoleToUser(userName, role);
                }
            }
            
            // assign groups to user
            if (groups == null || groups.isEmpty())
            {
                groups = this.defaultGroups;
            }
            if (groups != null)
            {
                Iterator groupsList = groups.iterator();
                while (groupsList.hasNext())
                {
                    String group = (String)groupsList.next();
                    if (group.trim().length() > 0)
                    {
                        groupManager.addUserToGroup(userName, group);
                    }
                }
            }
            
            // assign user attributes to user
            if (userInfo != null)
            {
                Iterator info = userInfo.entrySet().iterator();
                while (info.hasNext())
                {           
                    Map.Entry entry = (Map.Entry)info.next();
                    user.getUserAttributes().put((String)entry.getKey(), (String)entry.getValue());
                }
            }
            
            // assign profiling rules to user
            if (rules == null || rules.isEmpty())
            {
                rules = this.defaultRules;
            }
            if (rules != null)
            {
                Iterator ruleEntries = rules.entrySet().iterator();
                while (ruleEntries.hasNext())
                {           
                    Map.Entry entry = (Map.Entry)ruleEntries.next();                    
                    ProfilingRule rule = profiler.getRule((String)entry.getKey());
                    if (rule != null)
                    {
                        Principal principal = SecurityHelper.getBestPrincipal(user.getSubject(), UserPrincipal.class);
                        profiler.setRuleForPrincipal(principal, rule, (String)entry.getValue());
                    }
                }
            }
            
            if (folderTemplate == null)
            {
                folderTemplate = this.folderTemplate; 
            }
            
            // This next chunk of code is the fancy way to force the creation of the user
            // template pages to be created with subject equal to the new user
            // otherwise it would be created as guest, and guest does not have enough privs.
            final String innerFolderTemplate = folderTemplate;
            final String innerUserName = userName;
            final PageManager innerPageManager = pageManager;
            final String innerUser = userName;
            User powerUser = userManager.getUser(this.adminUser);
            JetspeedException pe = (JetspeedException) JSSubject.doAsPrivileged(powerUser.getSubject(), new PrivilegedAction()
                {
                    public Object run() 
                    {
                         try
                        {
//                           create user's home folder                        
                             // deep copy from the default folder template tree, creating a deep-copy of the template
                             // in the new user's folder tree
                            Folder source = innerPageManager.getFolder(innerFolderTemplate);                            
                            innerPageManager.deepCopyFolder(source, Folder.USER_FOLDER + innerUserName, innerUser);
                            Folder newFolder = pageManager.getFolder(Folder.USER_FOLDER + innerUserName);                            
                            newFolder.setTitle("Home Folder");
                            newFolder.setShortTitle("Home");
                             
                            return null;
                        }
                         catch (FolderNotFoundException e1) {
                             return e1;
                         } catch (InvalidFolderException e1){
                             return e1;
                         } catch (NodeException e1){
                             return e1;
                         }
                    }
                }, null);
                
            if(pe != null)
            {
                // rollback user creation and cascade roles, groups, etc
                try
                {
                    if (userManager.getUser(userName) != null)
                    {
                        userManager.removeUser(userName);
                    }
                }
                catch (Exception e)
                {
                    log.error("Registration Error: Failed to rollback user " + userName);
                }
                log.error("Registration Error: Failed to create user folders for " + userName + ", " + pe.toString());
                throw pe;
            }
                        
        }
        catch (Exception e) 
        {
            log.error("Registration Error: Failed to create registered user " + userName + ", " + e.toString());
            throw new RegistrationException(e); 
        }        
    }
    
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.administration.PortalAdministration#generatePassword()
     */
    public String generatePassword()
    {
        return adminUtil.generatePassword();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.administration.PortalAdministration#sendPassword(java.lang.String)
     */
    public void sendEmail(PortletConfig  portletConfig,
                          String emailAddress, 
                          String localizedSubject, 
                          String localizedTemplatePath,
                          Map userAttributes)                            
    throws AdministrationEmailException    
    {       
        
        String from = config.getString(PortalConfigurationConstants.EMAIL_SENDER);
        String subject = localizedSubject;
        String to = emailAddress;
        String text = mergeEmailTemplate(portletConfig, userAttributes, "map", localizedTemplatePath);
        sendEmail(from, subject, to, text);
        
    }
    
    /**
     * @param from
     * @param subject
     * @param to
     * @param text
     * @throws AdministrationEmailException
     */
    public void sendEmail(String from, String subject, String to, String text) throws AdministrationEmailException
    {
        SimpleMailMessage msg = new SimpleMailMessage();
        if(from == null) 
        {
            from = "jetspeed-admin@apache.org";
        }
        msg.setFrom(from);
        if(subject == null) 
        {
            subject = "message from jetspeed";
        }
        msg.setSubject(subject);
        msg.setTo(to);
        msg.setText(text);
        try
        {
            mailSender.send(msg);
        } 
        catch (MailException ex)
        {
            throw new AdministrationEmailException(
                    "Failed to send forgotten password email to user with email address because "+ex.getMessage()
                            ); //+ user.getEmail());
        }
    }
    
    public String mergeEmailTemplate(PortletConfig  portletConfig, Map attributes, String attributesName, String template)
    throws AdministrationEmailException
    {
        VelocityContext context = new VelocityContext();
        context.put(attributesName, attributes);
        StringWriter writer = new StringWriter();
        
        try
        {
            String realTemplatePath = portletConfig.getPortletContext().getRealPath(template);
            FileReader templateReader = new FileReader(realTemplatePath);
            velocityEngine.evaluate(context, writer, "UserEmailProcessor", templateReader);
        } catch (Exception e)
        {
            throw new AdministrationEmailException(
                    "Failed to generate email text for email template "
                            + template, e);
        }
        
        String buffer = writer.getBuffer().toString();
        
        return buffer;
    }
    
    private static final String USER_NOT_FOUND_FROM_EMAIL = "User not found for Email address: ";
    
    public User lookupUserFromEmail(String email)
        throws AdministrationEmailException    
    {
        Collection result = preferences.lookupPreference("userinfo", "user.business-info.online.email", email);
        if (result.size() == 0)
        {
            throw new AdministrationEmailException(USER_NOT_FOUND_FROM_EMAIL + email);
        }
        Iterator nodes = result.iterator();
        Node node = (Node)nodes.next();
        String nodePath = node.getFullPath();
        if (nodePath == null)
        {
            throw new AdministrationEmailException(USER_NOT_FOUND_FROM_EMAIL + email);
        }
        String[] paths = nodePath.split("/");
        
        // joachim@wemove.com 20120127: fix problem with users containing a '/' in fullpath 
        // (i.e. users that have been created with a '.' in the login id. 
        // Jetspeed replaces '.' in ids with '/').
        if (paths == null && paths.length > 4) {
            String[] newPaths = new String[4];
            newPaths[0] = paths[0];
            newPaths[1] = paths[1];
            newPaths[2] = paths[2];
            for (int i=3; i<paths.length -1; i++) {
                newPaths[2] = newPaths[2] + "/" + paths[i];
            }
            newPaths[3] = paths[paths.length -1];
            paths = newPaths; 
        }
        // joachim@wemove.com: end
        
        if (paths == null || paths.length != 4)
        {
            throw new AdministrationEmailException(USER_NOT_FOUND_FROM_EMAIL + email);
        }
        String userName = paths[2];
        try
        {
            return userManager.getUser(userName);
        }
        catch (Exception e)
        {
            throw new AdministrationEmailException(USER_NOT_FOUND_FROM_EMAIL + email);
        }
    }

    /**
     * Helper for admin portlets to generate portal urls
     */
    public String getPortalURL(PortletRequest request, PortletResponse response, String path)
    {
        // get internal request context
        RequestContext context = (RequestContext)
            request.getAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE);
        String baseUrl = context.getPortalURL().getBaseURL();
        String jetspeedPath = adminUtil.concatenatePaths(baseUrl, context.getPortalURL().getBasePath());
        if (path == null)
            return jetspeedPath;
        return adminUtil.concatenatePaths(jetspeedPath, response.encodeURL(path));
    }
        
    
    Map forgottenPasswordData = new HashMap();

    /* (non-Javadoc)
     * @see org.apache.jetspeed.administration.PortalAdministration#getNewLoginInfo(java.lang.String)
     */
    public Map getNewLoginInfo(String guid)
    {
        synchronized(forgottenPasswordData) {
            return (Map) forgottenPasswordData.get(guid);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.administration.PortalAdministration#setNewLoginInfo(java.lang.String, org.apache.jetspeed.administration.PortalAdministration.ResetPasswordInfo)
     */
    public void putNewLoginInfo(String guid, Map info)
    {
        synchronized(forgottenPasswordData) {
            forgottenPasswordData.put(guid,info);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.administration.PortalAdministration#removeNewLoginInfo(java.lang.String)
     */
    public void removeNewLoginInfo(String guid)
    {
        synchronized(forgottenPasswordData) {
            forgottenPasswordData.remove(guid);
        }
    }
    
    
    
    
    
}
