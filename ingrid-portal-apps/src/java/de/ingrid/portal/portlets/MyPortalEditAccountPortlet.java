/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets;

import java.io.IOException;
import java.util.prefs.Preferences;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.administration.PortalAdministration;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.security.InvalidPasswordException;
import org.apache.jetspeed.security.PasswordAlreadyUsedException;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.portals.bridges.common.GenericServletPortlet;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.portal.forms.EditAccountForm;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Utils;

/**
 * TODO Describe your created type (class, etc.) here.
 *
 * @author joachim@wemove.com
 */
public class MyPortalEditAccountPortlet extends GenericVelocityPortlet {

    private final static Log log = LogFactory.getLog(MyPortalEditAccountPortlet.class);
    
    private static final String STATE_ACCOUNT_SAVED = "account_saved";

    private PortalAdministration admin;
    
    private UserManager userManager;

    private static final String TEMPLATE_ACCOUNT_SAVED = "/WEB-INF/templates/myportal/myportal_edit_account_done.vm";

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
    }

    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#doView(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
     */
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        Context context = getContext(request);
        
        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()));
        context.put("MESSAGES", messages);
        
        response.setTitle(messages.getString(messages.getString("account.edit.title")));

        EditAccountForm f = (EditAccountForm) Utils.getActionForm(request, EditAccountForm.SESSION_KEY, EditAccountForm.class);        

        String cmd = request.getParameter("cmd");
        
        if (cmd == null) {
            f.clear();
            String userName = request.getUserPrincipal().getName();
            User user;
            try {
                user = userManager.getUser(userName);
                Preferences userAttributes = user.getUserAttributes();
                f.setInput(EditAccountForm.FIELD_SALUTATION, userAttributes.get("user.name.prefix", ""));
                f.setInput(EditAccountForm.FIELD_FIRSTNAME, userAttributes.get("user.name.given", ""));
                f.setInput(EditAccountForm.FIELD_LASTNAME, userAttributes.get("user.name.family", ""));
                f.setInput(EditAccountForm.FIELD_EMAIL, userAttributes.get("user.business-info.online.email", ""));
                f.setInput(EditAccountForm.FIELD_STREET, userAttributes.get("user.business-info.postal.street", ""));
                f.setInput(EditAccountForm.FIELD_POSTALCODE, userAttributes.get("user.business-info.postal.postalcode", ""));
                f.setInput(EditAccountForm.FIELD_CITY, userAttributes.get("user.business-info.postal.city", ""));

                f.setInput(EditAccountForm.FIELD_AGE, userAttributes.get("user.custom.ingrid.user.age.group", ""));
                f.setInput(EditAccountForm.FIELD_ATTENTION, userAttributes.get("user.custom.ingrid.user.attention.from", ""));
                f.setInput(EditAccountForm.FIELD_INTEREST, userAttributes.get("user.custom.ingrid.user.interest", ""));
                f.setInput(EditAccountForm.FIELD_PROFESSION, userAttributes.get("user.custom.ingrid.user.profession", ""));
                f.setInput(EditAccountForm.FIELD_SUBSCRIBE_NEWSLETTER, userAttributes.get("user.custom.ingrid.user.subscribe.newsletter", ""));

            } catch (SecurityException e) {
                f.setError("", "account.edit.error.user.notfound");
                log.error("Error getting current user.", e);
            }
            
        } else if (cmd.equals(STATE_ACCOUNT_SAVED)) {
            request.setAttribute(GenericServletPortlet.PARAM_VIEW_PAGE, TEMPLATE_ACCOUNT_SAVED);
        }
        
        context.put("actionForm", f);
        super.doView(request, response);
    }

    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#processAction(javax.portlet.ActionRequest, javax.portlet.ActionResponse)
     */
    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException, IOException {

        actionResponse.setRenderParameter("cmd", request.getParameter("cmd"));

        EditAccountForm f = (EditAccountForm) Utils.getActionForm(request, EditAccountForm.SESSION_KEY, EditAccountForm.class);
        
        f.clearErrors();
        
        f.populate(request);
        if (!f.validate()) {
            return;
        }
        
        String userName = null;
        User user = null;
        try {
            userName = request.getUserPrincipal().getName();
            user = userManager.getUser(userName);
        } catch (JetspeedException e) {
            f.setError("", "account.edit.error.user.notfound");
            log.error("Error getting current user.", e);
            return;
        }
            
        Preferences userAttributes = user.getUserAttributes();
        userAttributes.put("user.name.prefix", f.getInput(EditAccountForm.FIELD_SALUTATION));
        userAttributes.put("user.name.given", f.getInput(EditAccountForm.FIELD_FIRSTNAME));
        userAttributes.put("user.name.family", f.getInput(EditAccountForm.FIELD_LASTNAME));
        userAttributes.put("user.business-info.online.email", f.getInput(EditAccountForm.FIELD_EMAIL));
        userAttributes.put("user.business-info.postal.street", f.getInput(EditAccountForm.FIELD_STREET));
        userAttributes.put("user.business-info.postal.postalcode", f.getInput(EditAccountForm.FIELD_POSTALCODE));
        userAttributes.put("user.business-info.postal.city", f.getInput(EditAccountForm.FIELD_CITY));

        // theses are not PLT.D values but ingrid specifics
        userAttributes.put("user.custom.ingrid.user.age.group", f.getInput(EditAccountForm.FIELD_AGE));
        userAttributes.put("user.custom.ingrid.user.attention.from", f.getInput(EditAccountForm.FIELD_ATTENTION));
        userAttributes.put("user.custom.ingrid.user.interest", f.getInput(EditAccountForm.FIELD_INTEREST));
        userAttributes.put("user.custom.ingrid.user.profession", f.getInput(EditAccountForm.FIELD_PROFESSION));
        userAttributes.put("user.custom.ingrid.user.subscribe.newsletter", f.getInput(EditAccountForm.FIELD_SUBSCRIBE_NEWSLETTER));
        try {
            // update password only if a old password was provided
            String oldPassword = f.getInput(EditAccountForm.FIELD_PASSWORD_OLD);
            if (oldPassword != null && oldPassword.length() > 0) {
                userManager.setPassword(userName, f.getInput(EditAccountForm.FIELD_PASSWORD_OLD), f.getInput(EditAccountForm.FIELD_PASSWORD_NEW));
            }
        } catch (PasswordAlreadyUsedException e) {
            f.setError(EditAccountForm.FIELD_PASSWORD_NEW, "account.edit.error.password.in.use");
            return;
        } catch (InvalidPasswordException e) {
            f.setError(EditAccountForm.FIELD_PASSWORD_OLD, "account.edit.error.wrong.password");
            return;
        } catch (SecurityException e) {
            f.setError("", "account.edit.error.wrong.password");
            return;
        }
        
        actionResponse.setRenderParameter("cmd", STATE_ACCOUNT_SAVED);
        
    }

}
