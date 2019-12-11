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

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.forms.CreateAccountForm;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Utils;
import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.administration.PortalAdministration;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.portals.bridges.common.GenericServletPortlet;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.portlet.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * TODO Describe your created type (class, etc.) here.
 *
 * @author joachim@wemove.com
 */
public class MyPortalCreateAccountPortlet extends GenericVelocityPortlet {

    private static final Logger log = LoggerFactory.getLogger(MyPortalCreateAccountPortlet.class);

    private static final String STATE_ACCOUNT_CREATED = "account_created";

    private static final String STATE_ACCOUNT_PROBLEMS_EMAIL = "account_email";

    private static final String STATE_ACCOUNT_PROBLEMS = "account_problems";

    private PortalAdministration admin;

    private UserManager userManager;

    // Init Parameters
    private static final String IP_ROLES = "roles"; // comma separated

    private static final String IP_GROUPS = "groups"; // comma separated

    private static final String IP_RETURN_URL = "returnURL";

    private static final String IP_RULES_NAMES = "rulesNames";

    private static final String IP_RULES_VALUES = "rulesValues";

    private static final String IP_EMAIL_TEMPLATE = "emailTemplate";

    private static final String TEMPLATE_ACCOUNT_DONE = "/WEB-INF/templates/myportal/myportal_create_account_done.vm";

    private static final String TEMPLATE_ACCOUNT_CONFIRM_DONE = "/WEB-INF/templates/myportal/myportal_create_account_confirm_done.vm";

    /** servlet path of the return url to be printed and href'd in email template */
    private String returnUrlPath;

    /** email template to use for merging */
    private String emailTemplate;

    /** roles */
    private List<String> roles;

    /** groups */
    private List<String> groups;

    /** profile rules */
    private Map<String, String> rules;

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

        this.returnUrlPath = config.getInitParameter(IP_RETURN_URL);
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

        CreateAccountForm f = (CreateAccountForm) Utils.getActionForm(request, CreateAccountForm.SESSION_KEY,
                CreateAccountForm.class);

        String newUserGUID = request.getParameter("newUserGUID");
        if (newUserGUID != null) {
            String userName = request.getParameter("userName");
            User user = null;
            try {
                user = userManager.getUser(userName);
            	Map<String, String> pref = user.getInfoMap();
                String userConfirmId = pref.get("user.custom.ingrid.user.confirmid");
                userConfirmId = (userConfirmId == null ? "invalid" : userConfirmId);
                if (userConfirmId.equals(newUserGUID)) {
                	PasswordCredential pwc = userManager.getPasswordCredential(user);
                    pwc.setEnabled(true);
                    userManager.storePasswordCredential(pwc);
                    context.put("success", "true");
                    response.setTitle(messages.getString("account.confirmed.title"));
                    request.setAttribute(GenericServletPortlet.PARAM_VIEW_PAGE, TEMPLATE_ACCOUNT_CONFIRM_DONE);
                } else {
                    context.put("success", "false");
                    context.put("problemText", "account.confirmed.error.invalid.confirmid");
                    response.setTitle(messages.getString("account.confirmed.error.title"));
                    request.setAttribute(GenericServletPortlet.PARAM_VIEW_PAGE, TEMPLATE_ACCOUNT_CONFIRM_DONE);
                }
            } catch (SecurityException e) {
                context.put("success", "false");
                context.put("problemText", "account.confirmed.error.invalid.userid");
                response.setTitle(messages.getString("account.confirmed.error.title"));
                request.setAttribute(GenericServletPortlet.PARAM_VIEW_PAGE, TEMPLATE_ACCOUNT_CONFIRM_DONE);
            }
        } else {
            String cmd = request.getParameter("cmd");

            if (cmd == null) {
                f.clear();
            } else if (cmd.equals(STATE_ACCOUNT_CREATED)) {
                context.put("success", "true");
                response.setTitle(messages.getString("account.created.title"));
                request.setAttribute(GenericServletPortlet.PARAM_VIEW_PAGE, TEMPLATE_ACCOUNT_DONE);
            } else if (cmd.equals(STATE_ACCOUNT_PROBLEMS_EMAIL)) {
                context.put("success", "false");
                context.put("problemText", "account.created.problems.email");
                response.setTitle(messages.getString("account.created.problems.title"));
                request.setAttribute(GenericServletPortlet.PARAM_VIEW_PAGE, TEMPLATE_ACCOUNT_DONE);
            } else if (cmd.equals(STATE_ACCOUNT_PROBLEMS)) {
                context.put("success", "false");
                context.put("problemText", "account.created.problems.general");
                response.setTitle(messages.getString("account.created.problems.title"));
                request.setAttribute(GenericServletPortlet.PARAM_VIEW_PAGE, TEMPLATE_ACCOUNT_DONE);
            }
        }
        
        context.put("actionForm", f);
        context.put("loginLength", PortalConfig.getInstance().getInt(PortalConfig.PORTAL_FORM_LENGTH_CHECK_LOGIN, 4));
        super.doView(request, response);
    }

    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#processAction(javax.portlet.ActionRequest, javax.portlet.ActionResponse)
     */
    @Override
    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {

        Locale locale = request.getLocale();
        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(locale), locale);

        actionResponse.setRenderParameter("cmd", request.getParameter("cmd"));

        CreateAccountForm f = (CreateAccountForm) Utils.getActionForm(request, CreateAccountForm.SESSION_KEY,
                CreateAccountForm.class);
        f.clearErrors();
        f.populate(request);
        if (!f.validate()) {
            return;
        }

        try {
            String userName = f.getInput(CreateAccountForm.FIELD_LOGIN);
            String password = f.getInput(CreateAccountForm.FIELD_PW);

            // check if the user name exists
            boolean userIdExistsFlag = true;
            try {
                userManager.getUser(userName);
            } catch (SecurityException e) {
                userIdExistsFlag = false;
            }
            if (userIdExistsFlag) {
                f.setError(CreateAccountForm.FIELD_LOGIN, "account.create.error.user.exists");
                return;
            }

            Map<String, String> userAttributes = new HashMap<>();
            // we'll assume that these map back to PLT.D values
            userAttributes.put("user.name.prefix", f.getInput(CreateAccountForm.FIELD_SALUTATION));
            userAttributes.put("user.name.given", f.getInput(CreateAccountForm.FIELD_FIRSTNAME));
            userAttributes.put("user.name.family", f.getInput(CreateAccountForm.FIELD_LASTNAME));
            userAttributes.put("user.business-info.online.email", f.getInput(CreateAccountForm.FIELD_EMAIL));
            userAttributes.put("user.business-info.postal.street", f.getInput(CreateAccountForm.FIELD_STREET));
            userAttributes.put("user.business-info.postal.postalcode", f.getInput(CreateAccountForm.FIELD_POSTALCODE));
            userAttributes.put("user.business-info.postal.city", f.getInput(CreateAccountForm.FIELD_CITY));

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

            User user = userManager.getUser(userName);
            PasswordCredential pwc = userManager.getPasswordCredential(user);
            pwc.setEnabled(false);
            userManager.storePasswordCredential(pwc);

            String returnUrl = generateReturnURL(request, actionResponse, userName, confirmId);

            HashMap<String, String> userInfo = new HashMap<>(userAttributes);
            userInfo.put("returnURL", returnUrl);
            // map coded stuff
            String salutationFull = messages.getString("account.edit.salutation.option", (String) userInfo
                    .get("user.name.prefix"));
            userInfo.put("user.custom.ingrid.user.salutation.full", salutationFull);

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

            if (localizedTemplatePath == null) {
                log.error("email template not available");
                actionResponse.setRenderParameter("cmd", STATE_ACCOUNT_PROBLEMS_EMAIL);
                return;
            }

            String emailSubject = messages.getString("account.create.confirmation.email.subject");

            String from = PortalConfig.getInstance().getString(PortalConfig.EMAIL_REGISTRATION_CONFIRMATION_SENDER,
                    "foo@bar.com");
            String to = userInfo.get("user.business-info.online.email");
            String text = Utils.mergeTemplate(getPortletConfig(), userInfo, "map", localizedTemplatePath);
            if (Utils.sendEmail(from, emailSubject, new String[] { to }, text, null)) {
            	actionResponse.setRenderParameter("cmd", STATE_ACCOUNT_CREATED);
            } else {
                actionResponse.setRenderParameter("cmd", STATE_ACCOUNT_PROBLEMS_EMAIL);
            }

        } catch (JetspeedException e) {
            log.error("JetspeedException." , e);
            actionResponse.setRenderParameter("cmd", STATE_ACCOUNT_PROBLEMS);
        }
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

    protected String generateReturnURL(PortletRequest request, PortletResponse response, String userName, String urlGUID) {
        String fullPath = this.returnUrlPath + "?userName=" + userName + "&newUserGUID=" + urlGUID;
        
        String hostname = PortalConfig.getInstance().getString(PortalConfig.EMAIL_REGISTRATION_CONFIRMATION_URL);
        // NOTE: getPortalURL will encode the fullPath for us
        if(hostname != null && hostname.length() > 0){
            return hostname.concat(fullPath);
        }else{
            return admin.getPortalURL(request, response, fullPath);
        }
    }

}
