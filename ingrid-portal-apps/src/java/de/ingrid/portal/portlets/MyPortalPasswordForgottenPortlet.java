/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
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
import org.apache.jetspeed.administration.AdministrationEmailException;
import org.apache.jetspeed.administration.PortalAdministration;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.portals.bridges.common.GenericServletPortlet;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;

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

    private final static Log log = LogFactory.getLog(MyPortalPasswordForgottenPortlet.class);

    private static final String CTX_NEW_PASSWORD = "password";

    private static final String CTX_USER_NAME = "username";

    private static final String IP_EMAIL_TEMPLATE = "emailTemplate";

    private static final String STATE_PASSWORD_SENT = "password_sent";

    private static final String STATE_PASSWORD_NOT_SENT = "not_sent";

    private static final String TEMPLATE_PASSWORD_DONE = "/WEB-INF/templates/myportal/myportal_password_forgotten_done.vm";

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
                request.getLocale()));
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

        String email = f.getInput(PasswordForgottenForm.FIELD_EMAIL);

        User user;
        try {
            user = admin.lookupUserFromEmail(email);
        } catch (AdministrationEmailException e1) {
            f.setError(PasswordForgottenForm.FIELD_EMAIL, "password.forgotten.error.emailNotExists");
            return;
        }

        try {
            String userName = getUserName(user);
            String newPassword = admin.generatePassword();
            userManager.setPassword(userName, null, newPassword);

            Preferences pref = user.getUserAttributes();
            String[] keys = pref.keys();
            Map userAttributes = new HashMap();
            if (keys != null) {
                for (int ix = 0; ix < keys.length; ix++) {
                    // TODO: how the hell do i tell the pref type
                    // ASSuming they are all strings (sigh)
                    userAttributes.put(keys[ix], pref.get(keys[ix], ""));
                }
            }
            // special attributes
            userAttributes.put(CTX_NEW_PASSWORD, newPassword);
            userAttributes.put(CTX_USER_NAME, userName);

            Locale locale = request.getLocale();

            String language = locale.getLanguage();
            String localizedTemplatePath = this.emailTemplate;
            int period = localizedTemplatePath.lastIndexOf(".");
            if (period > 0) {
                String fixedTempl = localizedTemplatePath.substring(0, period) + "_" + language + "."
                        + localizedTemplatePath.substring(period + 1);
                if (new File(getPortletContext().getRealPath(fixedTempl)).exists()) {
                    this.emailTemplate = fixedTempl;
                }
            }

            if (localizedTemplatePath == null) {
                log.error("email template not available");
                f.setError("", "email template not available");
                return;
            }

            IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                    request.getLocale()));
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
        }

    }

    protected String getUserName(User user) {
        Principal principal = null;
        Iterator principals = user.getSubject().getPrincipals().iterator();
        while (principals.hasNext()) {
            Object o = principals.next();
            if (o instanceof UserPrincipal) {
                principal = (Principal) o;
                return principal.toString();
            }

        }
        return null;
    }

}
