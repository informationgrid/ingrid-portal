/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2018 wemove digital solutions GmbH
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

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.administration.AdministrationEmailException;
import org.apache.jetspeed.administration.PortalAdministration;
import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.portals.bridges.common.GenericServletPortlet;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.forms.PasswordForgottenForm;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Utils;

import org.apache.jetspeed.security.SecurityException;

/**
 * TODO Describe your created type (class, etc.) here.
 *
 * @author joachim@wemove.com
 */
public class MyPortalPasswordForgottenPortlet extends GenericVelocityPortlet {

    private final static Logger log = LoggerFactory.getLogger(MyPortalPasswordForgottenPortlet.class);

    private static final String CTX_NEW_PASSWORD = "password";

    private static final String CTX_USER_NAME = "username";

    private static final String IP_EMAIL_TEMPLATE = "emailTemplate";

    private static final String STATE_PASSWORD_SENT = "password_sent";

    private static final String STATE_PASSWORD_NOT_SENT = "not_sent";

    private static final String TEMPLATE_PASSWORD_DONE = "/WEB-INF/templates/myportal/myportal_password_forgotten_done.vm";

    private static final String USER_NOT_FOUND_FROM_EMAIL = "User not found for Email address: ";

    private PortalAdministration admin;

    private UserManager userManager;

    /** email template to use for merging */
    private String emailTemplate;

    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#init(javax.portlet.PortletConfig)
     */
    public void init(PortletConfig config) throws PortletException {
        super.init(config);

        admin = (PortalAdministration) getPortletContext()
                .getAttribute(CommonPortletServices.CPS_PORTAL_ADMINISTRATION);
        if (null == admin) {
            throw new PortletException("Failed to find the Portal Administration on portlet initialization");
        }
        userManager = (UserManager) getPortletContext().getAttribute(CommonPortletServices.CPS_USER_MANAGER_COMPONENT);
        if (null == userManager) {
            throw new PortletException("Failed to find the User Manager on portlet initialization");
        }

        this.emailTemplate = config.getInitParameter(IP_EMAIL_TEMPLATE);
    }

    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#doView(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
     */
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        Context context = getContext(request);

        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()), request.getLocale());
        context.put("MESSAGES", messages);

        PasswordForgottenForm f = (PasswordForgottenForm) Utils.getActionForm(request,
                PasswordForgottenForm.SESSION_KEY, PasswordForgottenForm.class);
        context.put("actionForm", f);

        String cmd = request.getParameter("cmd");

        if (cmd == null) {
            f.clear();
        } else if (cmd.equals(STATE_PASSWORD_SENT)) {
            context.put("success", "true");
            response.setTitle(messages.getString("password.sent.title"));
            request.setAttribute(GenericServletPortlet.PARAM_VIEW_PAGE, TEMPLATE_PASSWORD_DONE);
        } else if (cmd.equals(STATE_PASSWORD_NOT_SENT)) {
            context.put("success", "false");
            response.setTitle(messages.getString("password.problems.title"));
            request.setAttribute(GenericServletPortlet.PARAM_VIEW_PAGE, TEMPLATE_PASSWORD_DONE);
        }

        super.doView(request, response);
    }

    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#processAction(javax.portlet.ActionRequest, javax.portlet.ActionResponse)
     */
    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {
        actionResponse.setRenderParameter("cmd", request.getParameter("cmd"));

        PasswordForgottenForm f = (PasswordForgottenForm) Utils.getActionForm(request,
                PasswordForgottenForm.SESSION_KEY, PasswordForgottenForm.class);

        f.clearErrors();

        f.populate(request);
        if (!f.validate()) {
            return;
        }

        // search user with email
        String email = f.getInput(PasswordForgottenForm.FIELD_EMAIL);
        String login = f.getInput(PasswordForgottenForm.FIELD_LOGIN);
        User user = null;
        try {
            Collection<User> users;
            try {
                users = userManager.lookupUsers("user.business-info.online.email", email);
            } catch (SecurityException e) {
                throw new AdministrationEmailException(e);
            }
            if (users.isEmpty()) {
                throw new AdministrationEmailException(USER_NOT_FOUND_FROM_EMAIL + email);
            } else {
                if(users.size() == 1){
                    user = users.iterator().next();
                } else {
                    if(login != null && !login.isEmpty()){
                        for (User tmpUser : users) {
                            String tmpUserLogin = tmpUser.getName();
                            login = login.trim();
                            if(tmpUserLogin.equals( login )){
                                user = tmpUser;
                                break;
                            }
                        }
                        if (user == null) {
                            f.setError(PasswordForgottenForm.FIELD_LOGIN, "password.forgotten.error.loginNotExists");
                            return;
                        }
                    } else {
                        f.setError(PasswordForgottenForm.FIELD_LOGIN, "password.forgotten.error.loginMiss");
                        return;
                    }
                }
            }
        } catch (AdministrationEmailException e1) {
            f.setError(PasswordForgottenForm.FIELD_EMAIL, "password.forgotten.error.emailNotExists");
            return;
        }
        
        if(user == null) {
            f.setError(PasswordForgottenForm.FIELD_EMAIL, "password.forgotten.error.emailNotExists");
            return;
        } else {
            try {
                String userName = user.getName();
                String newPassword = admin.generatePassword();
        
                PasswordCredential credential = userManager.getPasswordCredential(user);
        		credential.setPassword(null, newPassword);
        		userManager.storePasswordCredential(credential);
        
        		Map<String, String> userAttributes = new HashMap<String, String>();
        		userAttributes.putAll(user.getInfoMap());
                // special attributes
                userAttributes.put(CTX_NEW_PASSWORD, newPassword);
                userAttributes.put(CTX_USER_NAME, userName);
                // map coded stuff
                Locale locale = request.getLocale();
                IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(locale), locale);
                String salutationFull = messages.getString("account.edit.salutation.option", (String) userAttributes
                        .get("user.name.prefix"));
                userAttributes.put("user.custom.ingrid.user.salutation.full", salutationFull);
        
                String language = locale.getLanguage();
                String localizedTemplatePath = this.emailTemplate;
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
                    actionResponse.setRenderParameter("cmd", STATE_PASSWORD_NOT_SENT);
                    return;
                }
        
                String emailSubject = messages.getString("password.forgotten.email.subject");
        
                String from = PortalConfig.getInstance().getString(PortalConfig.EMAIL_REGISTRATION_CONFIRMATION_SENDER,
                        "foo@bar.com");
                String to = (String) userAttributes.get("user.business-info.online.email");
                String text = Utils.mergeTemplate(getPortletConfig(), userAttributes, "map", localizedTemplatePath);
                if (Utils.sendEmail(from, emailSubject, new String[] { to }, text, null)) {
                    actionResponse.setRenderParameter("cmd", STATE_PASSWORD_SENT);
                } else {
                    actionResponse.setRenderParameter("cmd", STATE_PASSWORD_NOT_SENT);
                }
        
            } catch (Exception e) {
                log.error("error sending new password.", e);
                actionResponse.setRenderParameter("cmd", STATE_PASSWORD_NOT_SENT);
            }
        }
    }
}
