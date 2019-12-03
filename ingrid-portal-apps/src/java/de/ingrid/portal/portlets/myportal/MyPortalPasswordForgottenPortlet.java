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
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.administration.AdministrationEmailException;
import org.apache.jetspeed.administration.PortalAdministration;
import org.apache.jetspeed.security.SecurityException;
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

/**
 * TODO Describe your created type (class, etc.) here.
 *
 * @author joachim@wemove.com
 */
public class MyPortalPasswordForgottenPortlet extends GenericVelocityPortlet {

    private static final Logger log = LoggerFactory.getLogger(MyPortalPasswordForgottenPortlet.class);

    private static final String CTX_USER_NAME = "username";

    private static final String CTX_RETURN_URL = "returnURL";

    private static final String IP_EMAIL_TEMPLATE = "emailTemplate";

    private static final String STATE_PW_SENT = "password_sent";

    private static final String STATE_PW_NOT_SENT = "not_sent";

    private static final String TEMPLATE_PW_DONE = "/WEB-INF/templates/myportal/myportal_password_forgotten_done.vm";

    private static final String USER_NOT_FOUND_FROM_EMAIL = "User not found for Email address: ";

    private String returnURL;

    private PortalAdministration admin;

    private UserManager userManager;

    /** email template to use for merging */
    private String emailTemplate;

    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#init(javax.portlet.PortletConfig)
     */
    @Override
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

        this.returnURL = "/service-myportal.psml";
        this.emailTemplate = config.getInitParameter(IP_EMAIL_TEMPLATE);
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

        PasswordForgottenForm f = (PasswordForgottenForm) Utils.getActionForm(request,
                PasswordForgottenForm.SESSION_KEY, PasswordForgottenForm.class);
        context.put("actionForm", f);

        String cmd = request.getParameter("cmd");

        if (cmd == null) {
            f.clear();
        } else if (cmd.equals(STATE_PW_SENT)) {
            context.put("success", "true");
            response.setTitle(messages.getString("password.sent.title"));
            request.setAttribute(GenericServletPortlet.PARAM_VIEW_PAGE, TEMPLATE_PW_DONE);
        } else if (cmd.equals(STATE_PW_NOT_SENT)) {
            context.put("success", "false");
            response.setTitle(messages.getString("password.problems.title"));
            request.setAttribute(GenericServletPortlet.PARAM_VIEW_PAGE, TEMPLATE_PW_DONE);
        }

        super.doView(request, response);
    }

    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#processAction(javax.portlet.ActionRequest, javax.portlet.ActionResponse)
     */
    @Override
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
        } else {
            try {
                String userName = user.getName();
                String returnUrl = generateReturnURL(request, actionResponse, userName, user.getInfoMap().get("user.custom.ingrid.user.confirmid"));

                Map<String, String> userAttributes = new HashMap<>();
                userAttributes.putAll(user.getInfoMap());
                // special attributes
                userAttributes.put(CTX_USER_NAME, userName);
                userAttributes.put(CTX_RETURN_URL, returnUrl);

                // map coded stuff
                Locale locale = request.getLocale();
                IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(locale), locale);
                String salutationFull = messages.getString("account.edit.salutation.option", (String) userAttributes
                        .get("user.name.prefix"));
                userAttributes.put("user.custom.ingrid.user.salutation.full", salutationFull);
        
                String language = locale.getLanguage();
                String localizedTemplatePath = this.emailTemplate;
                int period = localizedTemplatePath.lastIndexOf('.');
                if (period > 0) {
                    String fixedTempl = localizedTemplatePath.substring(0, period) + "_" + language + "."
                            + localizedTemplatePath.substring(period + 1);
                    if (new File(getPortletContext().getRealPath(fixedTempl)).exists()) {
                        this.emailTemplate = fixedTempl;
                        localizedTemplatePath = fixedTempl;
                    }
                }
        
                if (localizedTemplatePath.isEmpty()) {
                    log.error("email template not available");
                    actionResponse.setRenderParameter("cmd", STATE_PW_NOT_SENT);
                    return;
                }
        
                String emailSubject = messages.getString("password.forgotten.email.subject");
        
                String from = PortalConfig.getInstance().getString(PortalConfig.EMAIL_REGISTRATION_CONFIRMATION_SENDER,
                        "foo@bar.com");
                String to = userAttributes.get("user.business-info.online.email");
                String text = Utils.mergeTemplate(getPortletConfig(), userAttributes, "map", localizedTemplatePath);
                if (Utils.sendEmail(from, emailSubject, new String[] { to }, text, null)) {
                    actionResponse.setRenderParameter("cmd", STATE_PW_SENT);
                } else {
                    actionResponse.setRenderParameter("cmd", STATE_PW_NOT_SENT);
                }
                if(!login.isEmpty()) {
                    f.clearInput(PasswordForgottenForm.FIELD_LOGIN);
                }
            } catch (Exception e) {
                log.error("error sending new password.", e);
                actionResponse.setRenderParameter("cmd", STATE_PW_NOT_SENT);
            }
        }
    }
    
    protected String generateReturnURL(PortletRequest request, PortletResponse response, String userName, String urlGUID) {
        String fullPath = this.returnURL + "?userName=" + userName + "&userGUID=" + urlGUID;
        
        String hostname = PortalConfig.getInstance().getString(PortalConfig.EMAIL_REGISTRATION_CONFIRMATION_URL);
        // NOTE: getPortalURL will encode the fullPath for us
        if(hostname != null && hostname.length() > 0){
            return hostname.concat(fullPath);
        }else{
            return admin.getPortalURL(request, response, fullPath);
        }
    }
}
