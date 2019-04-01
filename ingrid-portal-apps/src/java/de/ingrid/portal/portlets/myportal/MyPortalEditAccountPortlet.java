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
package de.ingrid.portal.portlets.myportal;

import de.ingrid.portal.forms.AdminUserForm;
import de.ingrid.portal.forms.EditAccountForm;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Utils;
import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.administration.PortalAdministration;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.*;
import org.apache.portals.bridges.common.GenericServletPortlet;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.portlet.*;
import java.io.IOException;
import java.util.Map;

/**
 * TODO Describe your created type (class, etc.) here.
 *
 * @author joachim@wemove.com
 */
public class MyPortalEditAccountPortlet extends GenericVelocityPortlet {

    private static final Logger log = LoggerFactory.getLogger(MyPortalEditAccountPortlet.class);

    private static final String STATE_ACCOUNT_SAVED = "account_saved";

    private UserManager userManager;

    private static final String TEMPLATE_ACCOUNT_DONE = "/WEB-INF/templates/myportal/myportal_edit_account_done.vm";

    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#init(javax.portlet.PortletConfig)
     */
    @Override
    public void init(PortletConfig config) throws PortletException {
        super.init(config);

        PortalAdministration admin = (PortalAdministration) getPortletContext()
                .getAttribute(CommonPortletServices.CPS_PORTAL_ADMINISTRATION);
        if (null == admin) {
            throw new PortletException("Failed to find the Portal Administration on portlet initialization");
        }
        userManager = (UserManager) getPortletContext().getAttribute(CommonPortletServices.CPS_USER_MANAGER_COMPONENT);
        if (null == userManager) {
            throw new PortletException("Failed to find the User Manager on portlet initialization");
        }
    }

    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#doView(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
     */
    @Override
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        Context context = getContext(request);

        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()), request.getLocale());
        context.put("MESSAGES", messages);

        response.setTitle(messages.getString(messages.getString("account.edit.title")));

        EditAccountForm f = (EditAccountForm) Utils.getActionForm(request, EditAccountForm.SESSION_KEY,
                EditAccountForm.class);

        String cmd = request.getParameter("cmd");

        if (cmd == null) {
            f.clear();
            String userName = request.getUserPrincipal().getName();
            User user;
            try {
                user = userManager.getUser(userName);
                Map<String, String> userAttributes = user.getInfoMap();
                f.setInput(EditAccountForm.FIELD_SALUTATION, replaceNull(userAttributes.get("user.name.prefix")));
                f.setInput(EditAccountForm.FIELD_FIRSTNAME, replaceNull(userAttributes.get("user.name.given")));
                f.setInput(EditAccountForm.FIELD_LASTNAME, replaceNull(userAttributes.get("user.name.family")));
                f.setInput(EditAccountForm.FIELD_EMAIL, replaceNull(userAttributes.get("user.business-info.online.email")));
                f.setInput(EditAccountForm.FIELD_STREET, replaceNull(userAttributes.get("user.business-info.postal.street")));
                f.setInput(EditAccountForm.FIELD_POSTALCODE, replaceNull(userAttributes.get("user.business-info.postal.postalcode")));
                f.setInput(EditAccountForm.FIELD_CITY, replaceNull(userAttributes.get("user.business-info.postal.city")));

                f.setInput(EditAccountForm.FIELD_AGE, replaceNull(userAttributes.get("user.custom.ingrid.user.age.group")));
                f.setInput(EditAccountForm.FIELD_ATTENTION, replaceNull(userAttributes.get(
                        "user.custom.ingrid.user.attention.from")));
                f.setInput(EditAccountForm.FIELD_INTEREST, replaceNull(userAttributes.get("user.custom.ingrid.user.interest")));
                f.setInput(EditAccountForm.FIELD_PROFESSION, replaceNull(userAttributes.get("user.custom.ingrid.user.profession")));
            } catch (SecurityException e) {
                f.setError("", "account.edit.error.user.notfound");
                log.error("Error getting current user.", e);
            }

        } else if (cmd.equals(STATE_ACCOUNT_SAVED)) {
            response.setTitle(messages.getString("account.edited.title"));
            request.setAttribute(GenericServletPortlet.PARAM_VIEW_PAGE, TEMPLATE_ACCOUNT_DONE);
        }

        context.put("actionForm", f);
        
        super.doView(request, response);
    }

    /** Replaces the input with "" if input is null. */
    private String replaceNull(String input) {
    	return input == null ? "" : input;
    }

    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#processAction(javax.portlet.ActionRequest, javax.portlet.ActionResponse)
     */
    @Override
    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {

        actionResponse.setRenderParameter("cmd", request.getParameter("cmd"));

        EditAccountForm f = (EditAccountForm) Utils.getActionForm(request, EditAccountForm.SESSION_KEY,
                EditAccountForm.class);
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

        try {
            user.getSecurityAttributes().getAttribute("user.name.prefix", true).setStringValue(f.getInput(AdminUserForm.FIELD_SALUTATION));
            user.getSecurityAttributes().getAttribute("user.name.given", true).setStringValue(f.getInput(AdminUserForm.FIELD_FIRSTNAME));
            user.getSecurityAttributes().getAttribute("user.name.family", true).setStringValue(f.getInput(AdminUserForm.FIELD_LASTNAME));
            user.getSecurityAttributes().getAttribute("user.business-info.online.email", true).setStringValue(f.getInput(AdminUserForm.FIELD_EMAIL));
            user.getSecurityAttributes().getAttribute("user.business-info.postal.street", true).setStringValue(f.getInput(AdminUserForm.FIELD_STREET));
            user.getSecurityAttributes().getAttribute("user.business-info.postal.postalcode", true).setStringValue(f.getInput(AdminUserForm.FIELD_POSTALCODE));
            user.getSecurityAttributes().getAttribute("user.business-info.postal.city", true).setStringValue(f.getInput(AdminUserForm.FIELD_CITY));

            userManager.updateUser(user);
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Problems saving user.", e);
            }
        }

        try {
            // update password only if a old password was provided
            String oldPassword = f.getInput(EditAccountForm.FIELD_PW_OLD);
            if (oldPassword != null && oldPassword.length() > 0) {
        		PasswordCredential credential = userManager.getPasswordCredential(user);
        		credential.setPassword(oldPassword, f.getInput(EditAccountForm.FIELD_PW_NEW));
        		userManager.storePasswordCredential(credential);
            }
        } catch (PasswordAlreadyUsedException e) {
            f.setError(EditAccountForm.FIELD_PW_NEW, "account.edit.error.password.in.use");
            return;
        } catch (SecurityException e) {
            f.setError(EditAccountForm.FIELD_PW_OLD, "account.edit.error.wrong.password");
            return;
        }

        actionResponse.setRenderParameter("cmd", STATE_ACCOUNT_SAVED);
    }
}
