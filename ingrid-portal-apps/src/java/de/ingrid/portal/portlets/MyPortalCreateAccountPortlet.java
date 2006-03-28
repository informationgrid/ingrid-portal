/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.administration.PortalAdministration;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.om.common.SecurityConstraints;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.page.FolderNotUpdatedException;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.profiler.Profiler;
import org.apache.jetspeed.security.RoleManager;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.portal.forms.CreateAccountForm;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.portlets.security.SecurityUtil;

/**
 * TODO Describe your created type (class, etc.) here.
 *
 * @author joachim@wemove.com
 */
public class MyPortalCreateAccountPortlet extends GenericVelocityPortlet {

    private static final String STATE_ACCOUNT_CREATED = "account_created";

    private PortalAdministration admin;
    
    private UserManager userManager;

    // Init Parameters
    private static final String IP_ROLES = "roles"; // comma separated

    private static final String IP_GROUPS = "groups"; // comma separated
    
    /** roles */
    private List roles;

    /** groups */
    private List groups;

    /** profile rules */
    private Map rules;
    
    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#init(javax.portlet.PortletConfig)
     */
    public void init(PortletConfig config) throws PortletException {
        super.init(config);

        admin = (PortalAdministration) getPortletContext().getAttribute(CommonPortletServices.CPS_PORTAL_ADMINISTRATION);
        if (null == admin) { 
            throw new PortletException("Failed to find the Portal Administration on portlet initialization"); 
        }
        userManager = (UserManager)getPortletContext().getAttribute(CommonPortletServices.CPS_USER_MANAGER_COMPONENT);
        if (null == userManager)
        {
            throw new PortletException("Failed to find the User Manager on portlet initialization");
        }

        // roles
        this.roles = getInitParameterList(config, IP_ROLES);

        // groups
        this.groups = getInitParameterList(config, IP_GROUPS);
        
    }

    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#doView(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
     */
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        Context context = getContext(request);
        
        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()));
        context.put("MESSAGES", messages);

        
        String cmd = request.getParameter("cmd");

        CreateAccountForm f = (CreateAccountForm) Utils.getActionForm(request, CreateAccountForm.SESSION_KEY, CreateAccountForm.class);        
        
        if (cmd == null) {
//            f.clear();
        }
        
        context.put("actionForm", f);
        
        super.doView(request, response);
    }

    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#processAction(javax.portlet.ActionRequest, javax.portlet.ActionResponse)
     */
    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException, IOException {

        actionResponse.setRenderParameter("cmd", request.getParameter("cmd"));

        CreateAccountForm f = (CreateAccountForm) Utils.getActionForm(request, CreateAccountForm.SESSION_KEY, CreateAccountForm.class);
        
        f.clearErrors();
        
        f.populate(request);
        if (!f.validate()) {
            return;
        }
        
        try {
            String userName = f.getInput(CreateAccountForm.FIELD_LOGIN);
            String password = f.getInput(CreateAccountForm.FIELD_PASSWORD);

            Map userAttributes = new HashMap();
            // we'll assume that these map back to PLT.D values
            userAttributes.put("user.firstname", f.getInput(CreateAccountForm.FIELD_FIRSTNAME));
            
            
            admin.registerUser(userName,
                    password, this.roles,
                    this.groups, userAttributes, // note use of only
                                                    // PLT.D values here.
                    rules, null); // passing in null causes use of default
                                    // template
            
        } catch (JetspeedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        actionResponse.setRenderParameter("cmd", STATE_ACCOUNT_CREATED);
        
    }

    protected List getInitParameterList(PortletConfig config, String ipName)
    {
        String temp = config.getInitParameter(ipName);
        if (temp == null) return new ArrayList();

        String[] temps = temp.split("\\,");
        for (int ix = 0; ix < temps.length; ix++)
            temps[ix] = temps[ix].trim();

        return Arrays.asList(temps);
    }

    
}
