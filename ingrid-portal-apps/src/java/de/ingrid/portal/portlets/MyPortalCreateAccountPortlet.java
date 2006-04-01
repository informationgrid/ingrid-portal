/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.Principal;
import java.util.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.prefs.Preferences;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.administration.PortalAdministration;
import org.apache.jetspeed.administration.PortalAdministrationImpl;
import org.apache.jetspeed.administration.PortalConfigurationConstants;
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
import org.apache.portals.bridges.common.GenericServletPortlet;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.forms.CreateAccountForm;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.portlets.security.SecurityUtil;
import de.ingrid.portal.search.net.ThreadedQueryController;

/**
 * TODO Describe your created type (class, etc.) here.
 *
 * @author joachim@wemove.com
 */
public class MyPortalCreateAccountPortlet extends GenericVelocityPortlet {

    private final static Log log = LogFactory.getLog(MyPortalCreateAccountPortlet.class);
    
    private static final String STATE_ACCOUNT_CREATED = "account_created";

    private PortalAdministration admin;
    
    private UserManager userManager;

    // Init Parameters
    private static final String IP_ROLES = "roles"; // comma separated

    private static final String IP_GROUPS = "groups"; // comma separated

    private static final String IP_RETURN_URL = "returnURL";

    private static final String IP_EMAIL_TEMPLATE = "emailTemplate";

    private static final String TEMPLATE_ACCOUNT_CREATED = "/WEB-INF/templates/myportal/myportal_create_account_done.vm";

    private static final String TEMPLATE_ACCOUNT_CONFIRMED = "/WEB-INF/templates/myportal/myportal_create_account_confirmed.vm";

    private static final String TEMPLATE_ACCOUNT_CONFIRM_ERROR = "/WEB-INF/templates/myportal/myportal_create_account_confirm_error.vm";
    
    /** servlet path of the return url to be printed and href'd in email template */
    private String returnUrlPath;

    /** email template to use for merging */
    private String emailTemplate;
    
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

        this.returnUrlPath = config.getInitParameter(IP_RETURN_URL);
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

        CreateAccountForm f = (CreateAccountForm) Utils.getActionForm(request, CreateAccountForm.SESSION_KEY, CreateAccountForm.class);        

        String newUserGUID = request.getParameter("newUserGUID");
        if (newUserGUID != null) {
            String userName = request.getParameter("userName");
            User user = null;
            try {
                user = userManager.getUser(userName);
                Preferences pref = user.getUserAttributes();
                String userConfirmId = pref.get("user.custom.ingrid.user.confirmid", "invalid");
                if (userConfirmId.equals(newUserGUID)) {
                    userManager.setUserEnabled(userName, true);
                    request.setAttribute(GenericServletPortlet.PARAM_VIEW_PAGE, TEMPLATE_ACCOUNT_CONFIRMED);
                } else {
                    f.setError("", "account.confirm.error.invalid.comfirmid");
                    request.setAttribute(GenericServletPortlet.PARAM_VIEW_PAGE, TEMPLATE_ACCOUNT_CONFIRM_ERROR);
                }
            } catch (SecurityException e) {
                f.setError("", "account.confirm.error.invalid.userid");
                request.setAttribute(GenericServletPortlet.PARAM_VIEW_PAGE, TEMPLATE_ACCOUNT_CONFIRM_ERROR);
            }
        } else {
            String cmd = request.getParameter("cmd");
            
            if (cmd == null) {
                f.clear();
            } else if (cmd.equals(STATE_ACCOUNT_CREATED)) {
                request.setAttribute(GenericServletPortlet.PARAM_VIEW_PAGE, TEMPLATE_ACCOUNT_CREATED);
            }
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

            // check if the user name exists
            boolean userIdExistsFlag = true;
            try  {
                User user = userManager.getUser(userName);
            } catch (SecurityException e)  {
                userIdExistsFlag = false;
            }
            if (userIdExistsFlag) {
                f.setError(CreateAccountForm.FIELD_LOGIN, "account.create.error.user.exists");
                return;
            }
            
            
            Map userAttributes = new HashMap();
            // we'll assume that these map back to PLT.D values
            userAttributes.put("user.name.prefix", f.getInput(CreateAccountForm.FIELD_SALUTATION));
            userAttributes.put("user.name.given", f.getInput(CreateAccountForm.FIELD_FIRSTNAME));
            userAttributes.put("user.name.family", f.getInput(CreateAccountForm.FIELD_LASTNAME));
            userAttributes.put("user.business-info.online.email", f.getInput(CreateAccountForm.FIELD_EMAIL));
            userAttributes.put("user.business-info.postal.street", f.getInput(CreateAccountForm.FIELD_STREET));
            userAttributes.put("user.business-info.postal.postalcode", f.getInput(CreateAccountForm.FIELD_POSTALCODE));
            userAttributes.put("user.business-info.postal.city", f.getInput(CreateAccountForm.FIELD_CITY));

            // theses are not PLT.D values but ingrid specifics
            userAttributes.put("user.custom.ingrid.user.age.group", f.getInput(CreateAccountForm.FIELD_AGE));
            userAttributes.put("user.custom.ingrid.user.attention.from", f.getInput(CreateAccountForm.FIELD_ATTENTION));
            userAttributes.put("user.custom.ingrid.user.interest", f.getInput(CreateAccountForm.FIELD_INTEREST));
            userAttributes.put("user.custom.ingrid.user.profession", f.getInput(CreateAccountForm.FIELD_PROFESSION));
            userAttributes.put("user.custom.ingrid.user.subscribe.newsletter", f.getInput(CreateAccountForm.FIELD_SUBSCRIBE_NEWSLETTER));
            
            // generate login id
            String confirmId = Utils.getMD5Hash(userName.concat(password).concat(Long.toString(System.currentTimeMillis())));            
            userAttributes.put("user.custom.ingrid.user.confirmid", confirmId);
            
            admin.registerUser(userName, password, this.roles, this.groups, userAttributes, rules, null);

            // TODO set this to false in production env
            userManager.setUserEnabled(userName, true);
            
            String returnUrl = generateReturnURL(request, actionResponse, userName, confirmId);

            HashMap userInfo = new HashMap(userAttributes);
            userInfo.put("returnURL", returnUrl);
            
            Locale locale = request.getLocale();

            String language = locale.getLanguage();
            String localizedTemplatePath = this.emailTemplate;
            int period = localizedTemplatePath.lastIndexOf(".");
            if (period > 0) {
                String fixedTempl = localizedTemplatePath.substring(0, period) + "_"
                        + language + "." + localizedTemplatePath.substring(period + 1);
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
            String emailSubject = messages.getString("account.create.confirmation.email.subject");

            
            String from = PortalConfig.getInstance().getString(PortalConfig.EMAIL_REGISTRATION_CONFIRMATION_SENDER, "foo@bar.com");
            String to = (String) userInfo.get("user.business-info.online.email");
            String text = Utils.mergeTemplate(getPortletConfig(), userInfo, "map", localizedTemplatePath);
            Utils.sendEmail(from, emailSubject, new String[] {to}, text, null);
            
        } catch (JetspeedException e) {
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

    protected String generateReturnURL(PortletRequest request,
            PortletResponse response, String userName, String urlGUID)
    {
        String fullPath = this.returnUrlPath + "?userName=" + userName + "&newUserGUID=" + urlGUID;
        // NOTE: getPortalURL will encode the fullPath for us
        String url = admin.getPortalURL(request, response, fullPath);
        return url;
    }

}
