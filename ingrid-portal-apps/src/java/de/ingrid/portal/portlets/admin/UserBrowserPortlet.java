/* Copyright 2004 Apache Software Foundation
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
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
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.security.Role;
import org.apache.jetspeed.security.RoleManager;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.portals.messaging.PortletMessaging;
import org.apache.velocity.context.Context;

import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.UtilsDB;
import de.ingrid.portal.portlets.browser.BrowserIterator;
import de.ingrid.portal.portlets.browser.BrowserPortlet;
import de.ingrid.portal.portlets.browser.DatabaseBrowserIterator;
import de.ingrid.portal.portlets.browser.StatusMessage;
import de.ingrid.portal.portlets.security.SecurityResources;
import de.ingrid.portal.portlets.security.SecurityUtil;

/**
 * Role Browser - flat non-hierarchical view
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: UserBrowserPortlet.java 348264 2005-11-22 22:06:45Z taylor $
 */
public class UserBrowserPortlet extends BrowserPortlet
{
    protected UserManager userManager;
    protected RoleManager roleManager;    

    // view context
    public static final String STATUS = "statusMsg";
    public static final String SELECTED = "selected";

    public void init(PortletConfig config)
    throws PortletException 
    {
        super.init(config);
        userManager = (UserManager) 
            getPortletContext().getAttribute(CommonPortletServices.CPS_USER_MANAGER_COMPONENT);
        if (null == userManager)
        {
            throw new PortletException("Failed to find the User Manager on portlet initialization");
        }
        roleManager = (RoleManager) 
        getPortletContext().getAttribute(CommonPortletServices.CPS_ROLE_MANAGER_COMPONENT);
        if (null == roleManager)
        {
            throw new PortletException("Failed to find the Role Manager on portlet initialization");
        }
    }

    public void doView(RenderRequest request, RenderResponse response)
    throws PortletException, IOException
    {
        Context context = this.getContext(request);
        
        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()));
        context.put("MESSAGES", messages);
        
        String selected = (String)PortletMessaging.receive(request, SecurityResources.TOPIC_USERS, SecurityResources.MESSAGE_SELECTED);
        if (selected != null)
        {        
            context.put(SELECTED, selected);
        }
        StatusMessage msg = (StatusMessage)PortletMessaging.consume(request, SecurityResources.TOPIC_USERS, SecurityResources.MESSAGE_STATUS);
        if (msg != null)
        {
            this.getContext(request).put(STATUS, msg);            
        }
        String refresh = (String)PortletMessaging.consume(request, SecurityResources.TOPIC_USERS, SecurityResources.MESSAGE_REFRESH); 
        if (refresh != null)
        {        
            this.clearBrowserIterator(request);
        }                
        
        String filtered = (String)PortletMessaging.receive(request, SecurityResources.TOPIC_USERS, SecurityResources.MESSAGE_FILTERED);
        if (filtered != null)
        {
            this.getContext(request).put(FILTERED, "on");            
        }
        
        ArrayList errorMessages = (ArrayList)PortletMessaging.consume(request, SecurityResources.TOPIC_USERS, SecurityResources.ERROR_MESSAGES);
        if (errorMessages != null )
        {
            this.getContext(request).put(SecurityResources.ERROR_MESSAGES, errorMessages);
        }
        
        super.doView(request, response);
    }
        
    public void processAction(ActionRequest request, ActionResponse response)
    throws PortletException, IOException
    {
        if (request.getPortletMode() == PortletMode.VIEW)
        {
            String selected = request.getParameter("user");
            if (selected != null)
            {
                PortletMessaging.publish(request, SecurityResources.TOPIC_USERS, SecurityResources.MESSAGE_SELECTED, selected);
            }
        }
        
        // TODO: if request parameters were working correctly we could replace this with render parameters
        String filtered = (String)request.getParameter(FILTERED);
        if (filtered != null)
        {
            PortletMessaging.publish(request, SecurityResources.TOPIC_USERS, SecurityResources.MESSAGE_FILTERED, "on");            
        }
        else
        {
            PortletMessaging.cancel(request, SecurityResources.TOPIC_USERS, SecurityResources.MESSAGE_FILTERED);
        }
        
        super.processAction(request, response);            
    }
      
    public void getRows(RenderRequest request, String sql, int windowSize)
    {
        getRows(request, sql, windowSize, "");
    }

    public void getRows(RenderRequest request, String sql, int windowSize, String filter)
    {
        
/*
        // get current user
        String authUserName = request.getUserPrincipal().getName();
        ArrayList authUserRoles = new ArrayList();
        
        try {
            Collection roles = roleManager.getRolesForUser(authUserName);
            Iterator it = roles.iterator();
            while (it.hasNext()) {
                authUserRoles.add(((Role)it.next()).getPrincipal().getName());
            }
            
            List authUserPartnerRelations = UtilsDB.getUserPartnerRelations(authUserName);
            
        
            List resultSetTitleList = new ArrayList();
            List resultSetTypeList = new ArrayList();
            resultSetTypeList.add(String.valueOf(Types.VARCHAR));
            resultSetTitleList.add("user"); // resource bundle key
            resultSetTitleList.add(SecurityResources.USER_NAME_FAMILY); // resource bundle key
            resultSetTitleList.add(SecurityResources.USER_NAME_GIVEN); // resource bundle key
    
            List list = new ArrayList();
            try
            {
                Iterator users = userManager.getUsers(filter);
                
                while (users.hasNext())                   
                {
                    boolean addUser = false;
                    User user = (User)users.next();
                    Principal principal = SecurityUtil.getPrincipal(user.getSubject(),UserPrincipal.class);                
                    
                    ArrayList userRoles = new ArrayList();
                    
                    if (authUserRoles.contains("admin")) {
                        addUser = true;
                    } else  if (authUserRoles.contains("admin_portal")) {
                        addUser = true;
                    } else  if (authUserRoles.contains("admin_partner") && userRoles.contains("admin_provider")) {
                        // check if partner_ident of the authUser exists for user
                        it = authUserPartnerRelations.iterator();
                        List userPartnerRelations = UtilsDB.getUserPartnerRelations(principal.getName());
                        while (it.hasNext()) {
                            String authPartnerIdent = (String)it.next();
                            for (int i=0; i<userPartnerRelations.size(); i++ ) {
                                if (authPartnerIdent.equalsIgnoreCase((String)userPartnerRelations.get(i))) {
                                    addUser = true;
                                    break;
                                }
                            }
                            if (addUser) {
                                break;
                            }
                        }
                    }
                    if (addUser) {
                        List columns = new ArrayList();
                        columns.add(principal.getName());
                        columns.add(user.getUserAttributes().get(SecurityResources.USER_NAME_FAMILY, ""));
                        columns.add(user.getUserAttributes().get(SecurityResources.USER_NAME_GIVEN, ""));
                        list.add(columns);
                    }
                }            
            }
            catch (SecurityException sex)
            {
                SecurityUtil.publishErrorMessage(request, SecurityResources.TOPIC_USERS, sex.getMessage());
            }                                    
            BrowserIterator iterator = new DatabaseBrowserIterator(list, resultSetTitleList, resultSetTypeList, windowSize);
            setBrowserIterator(request, iterator);
            iterator.sort("user"); // resource bundle key
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
*/
    }    
}
