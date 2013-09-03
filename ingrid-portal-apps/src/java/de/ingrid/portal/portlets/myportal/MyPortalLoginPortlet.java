/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets.myportal;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.administration.PortalAdministration;
import org.apache.jetspeed.administration.RegistrationException;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.login.LoginConstants;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.PermissionManager;
import org.apache.jetspeed.security.RoleManager;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.pluto.om.common.Parameter;
import org.apache.pluto.om.common.ParameterSet;
import org.apache.portals.bridges.common.GenericServletPortlet;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.app.FieldMethodizer;
import org.apache.velocity.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ingrid.portal.forms.LoginForm;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.global.UtilsSecurity;
import de.ingrid.portal.portlets.security.SecurityUtil;
import de.ingrid.portal.security.UserManager;
import de.ingrid.portal.security.role.IngridRole;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class MyPortalLoginPortlet extends GenericVelocityPortlet {
	
	private final static Logger log = LoggerFactory.getLogger(MyPortalLoginPortlet.class);
	
	private static final String VIEW_SHIB = "/WEB-INF/templates/myportal/myportal_shib.vm";

    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#doView(javax.portlet.RenderRequest,
     *      javax.portlet.RenderResponse)
     */
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        Context context = getContext(request);
        //boolean isShibbolethAuth = "shibboleth".equals(request.getAuthType());
        boolean isLoggedInExternally = request.getAttribute(Settings.USER_AUTH_INFO) != null;
        
        if (log.isDebugEnabled()) {
	        log.debug("is logged in externally: " + isLoggedInExternally + " : " + request.getAttribute(Settings.USER_AUTH_INFO));
	        log.debug("auth type is: " + request.getAuthType());
        }

        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()));
        context.put("MESSAGES", messages);
        
        // when using shibboleth authentication just show a page to create a profile
        // for the new user
        if (isLoggedInExternally) {
        	request.setAttribute(GenericServletPortlet.PARAM_VIEW_PAGE, VIEW_SHIB);
        	super.doView(request, response);
        	return;
        }

        LoginForm frm = (LoginForm) Utils.getActionForm(request, LoginForm.SESSION_KEY, LoginForm.class);

        String errorCode = request.getParameter("errorCode");
        if (errorCode != null) {
            frm.clearErrors();
            if (errorCode.equals(LoginConstants.ERROR_UNKNOWN_USER.toString())) {
                frm.setError(LoginForm.FIELD_USERNAME, "login.error.unknownUser");
            } else if (errorCode.equals(LoginConstants.ERROR_INVALID_PASSWORD.toString())) {
                frm.setError(LoginForm.FIELD_PASSWORD, "login.error.invalidPassword");
            } else if (errorCode.equals(LoginConstants.ERROR_CREDENTIAL_DISABLED.toString())) {
                frm.setError(LoginForm.FIELD_PASSWORD, "login.error.userDisabled");
            } else {
                frm.setError("", "login.error.general");
            }
        } else if (!frm.hasErrors()) {
            frm.setINITIAL_USERNAME(messages.getString("login.form.username.initialValue"));
            frm.setINITIAL_PASSWORD(messages.getString("login.form.passwd.initialValue"));
            frm.init();
            String loginRedirect = request.getParameter(Settings.PARAM_LOGIN_REDIRECT);
            if (loginRedirect == null || loginRedirect.length() == 0) {
                frm.setInput(LoginForm.FIELD_DESTINATION, response.createActionURL().toString());
            } else {
            	frm.setInput(LoginForm.FIELD_DESTINATION, request.getParameter(Settings.PARAM_LOGIN_REDIRECT));
            }
            frm.clearErrors();
        }
        context.put("actionForm", frm);
        context.put("loginConstants", new FieldMethodizer(new LoginConstants()));

        super.doView(request, response);
    }

    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#processAction(javax.portlet.ActionRequest,
     *      javax.portlet.ActionResponse)
     */
    public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {

        LoginForm frm = (LoginForm) Utils.getActionForm(request, LoginForm.SESSION_KEY, LoginForm.class);
        frm.clearErrors();

        String cmd = request.getParameter("cmd");
        boolean isLoggedInExternally = request.getAttribute(Settings.USER_AUTH_INFO) != null;
        
        if (cmd != null && cmd.equals("doLogin")) {
            HttpSession session = ((RequestContext) request.getAttribute(RequestContext.REQUEST_PORTALENV))
                    .getRequest().getSession(true);

            frm.populate(request);
            if (!frm.validate()) {
                return;
            }

            if (frm.getInput(LoginConstants.DESTINATION) != null)
                session.setAttribute(LoginConstants.DESTINATION, frm.getInput(LoginConstants.DESTINATION));
            else
                session.removeAttribute(LoginConstants.DESTINATION);

            if (frm.getInput(LoginConstants.USERNAME) != null)
                session.setAttribute(LoginConstants.USERNAME, frm.getInput(LoginConstants.USERNAME));
            else
                session.removeAttribute(LoginConstants.USERNAME);

            if (frm.getInput(LoginConstants.PASSWORD) != null)
                session.setAttribute(LoginConstants.PASSWORD, frm.getInput(LoginConstants.PASSWORD));
            else
                session.removeAttribute(LoginConstants.PASSWORD);

            // signalize that the user is about to log in
            // see MyPortalOverviewPortlet::doView()
            session.setAttribute(Settings.SESSION_LOGIN_STARTED, "1");

            response.sendRedirect(response.encodeURL(((RequestContext) request
                    .getAttribute(RequestContext.REQUEST_PORTALENV)).getRequest().getContextPath()
                    + "/login/redirector"));
        } else if (cmd != null && cmd.equals("doCreateProfile") && isLoggedInExternally) {
        	PortalAdministration admin = (PortalAdministration) getPortletContext()
                    .getAttribute(CommonPortletServices.CPS_PORTAL_ADMINISTRATION);
        	
        	List<String> roles = getInitialParameterFromOtherPortlet("roles");
        	List<String> groups = getInitialParameterFromOtherPortlet("groups");
        	List<String> rulesNames = getInitialParameterFromOtherPortlet("rulesNames");
        	List<String> rulesValues = getInitialParameterFromOtherPortlet("rulesValues");
        	Map<String, String> rules = new HashMap<String, String>();
        	for (int ix = 0; ix < ((rulesNames.size() < rulesValues.size()) ? rulesNames.size() : rulesValues.size()); ix++) {
                rules.put(rulesNames.get(ix), rulesValues.get(ix));
            }
        	Map<String, String> userAttributes = getUserAttributes(request);
        	String username = (String)request.getAttribute(Settings.USER_AUTH_INFO);
        	String password = RandomStringUtils.randomAlphanumeric(8);
        	Boolean isAdminPortalUser = (Boolean)request.getAttribute(Settings.USER_AUTH_INFO_IS_ADMIN);
        	
        	// generate login id
            String confirmId = Utils.getMD5Hash(username.concat(password).concat(
                    Long.toString(System.currentTimeMillis())));
            userAttributes.put("user.custom.ingrid.user.confirmid", confirmId);
            
            // add admin-portal role 
            if (isAdminPortalUser) {
	            roles.add("user");
	            roles.add(IngridRole.ROLE_ADMIN_PORTAL);
            }
        	
        	try {
        		log.debug("username: " + username + ", roles: " + roles + ", groups: " + groups + ", userAttr: " + userAttributes + ", rules: " + rules);
				admin.registerUser(username, password, roles, groups, userAttributes, rules, null);
				//UserManager userManager = (UserManager) getPortletContext().getAttribute(CommonPortletServices.CPS_USER_MANAGER_COMPONENT);
				//userManager.setPasswordEnabled(username, false);
				if (isAdminPortalUser) {
				    addAdminSystemRole(username);
				}
			} catch (RegistrationException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
                e.printStackTrace();
            }
        } else {
            Integer errorCode = (Integer) ((RequestContext) request.getAttribute(RequestContext.REQUEST_PORTALENV))
                    .getSessionAttribute(LoginConstants.ERRORCODE);
            if (errorCode != null) {
                response.setRenderParameter("errorCode", errorCode.toString());
            } else if (request.getUserPrincipal() == null) {
                response.setRenderParameter("errorCode", "login.error.principal.null");
            } else {
                frm.clearErrors();
            }
        }
    }
    
    /**
     * @param username
     * @throws PortletException 
     * @throws SecurityException 
     */
    private void addAdminSystemRole(String username) throws PortletException, SecurityException {
        // set system specific flags
        UserManager userManager = (UserManager) getPortletContext().getAttribute(CommonPortletServices.CPS_USER_MANAGER_COMPONENT);
        if (null == userManager) {
            throw new PortletException("Failed to find the User Manager on portlet initialization");
        }
        
        Principal userPrincipal = SecurityUtil.getPrincipal(userManager.getUser(username).getSubject(), UserPrincipal.class);
        RoleManager roleManager = (RoleManager) getPortletContext().getAttribute(CommonPortletServices.CPS_ROLE_MANAGER_COMPONENT);
        if (null == roleManager) {
            throw new PortletException("Failed to find the Role Manager on portlet initialization");
        }
        PermissionManager permissionManager = (PermissionManager) getPortletContext().getAttribute(CommonPortletServices.CPS_PERMISSION_MANAGER);
        if (permissionManager == null) {
            throw new PortletException("Could not get instance of portal permission manager component");
        }
        permissionManager.grantPermission(userPrincipal, UtilsSecurity.ADMIN_PORTAL_INGRID_PORTAL_PERMISSION);
        roleManager.addRoleToUser(userPrincipal.getName(), IngridRole.ROLE_ADMIN_PORTAL);
        
    }

    private List<String> getInitialParameterFromOtherPortlet(String parameter) {
    	PortletRegistry registry = (PortletRegistry) getPortletContext().getAttribute(CommonPortletServices.CPS_REGISTRY_COMPONENT);
    	ParameterSet initParams = registry.getPortletDefinitionByIdentifier("MyPortalCreateAccountPortlet").getInitParameterSet();
    	
    	return getInitParameterList(initParams, parameter);
	}

	private Map<String, String> getUserAttributes(ActionRequest request) {
    	Map<String, String> userAttributes = new HashMap<String, String>();
    	String username = (String)request.getAttribute(Settings.USER_AUTH_INFO);
    	
        // we'll assume that these map back to PLT.D values
        //userAttributes.put("user.name.prefix", f.getInput(CreateAccountForm.FIELD_SALUTATION));
        userAttributes.put("user.name.given", checkNullValue(request.getProperty("givenName"), "n/a"));
        userAttributes.put("user.name.family", checkNullValue(request.getProperty("sn"), username));
        userAttributes.put("user.business-info.online.email", checkNullValue(request.getProperty("mail"), username));
        //userAttributes.put("user.business-info.postal.street", f.getInput(CreateAccountForm.FIELD_STREET));
        //userAttributes.put("user.business-info.postal.postalcode", f.getInput(CreateAccountForm.FIELD_POSTALCODE));
        //userAttributes.put("user.business-info.postal.city", f.getInput(CreateAccountForm.FIELD_CITY));

        // theses are not PLT.D values but ingrid specifics
        //userAttributes.put("user.custom.ingrid.user.age.group", f.getInput(CreateAccountForm.FIELD_AGE));
        //userAttributes.put("user.custom.ingrid.user.attention.from", f.getInput(CreateAccountForm.FIELD_ATTENTION));
        //userAttributes.put("user.custom.ingrid.user.interest", f.getInput(CreateAccountForm.FIELD_INTEREST));
        //userAttributes.put("user.custom.ingrid.user.profession", f.getInput(CreateAccountForm.FIELD_PROFESSION));
        //userAttributes.put("user.custom.ingrid.user.subscribe.newsletter", f.getInput(CreateAccountForm.FIELD_SUBSCRIBE_NEWSLETTER));
        
        return userAttributes;
    }
    
    private List<String> getInitParameterList(ParameterSet initParams, String ipName) {
        Parameter parameter = initParams.get(ipName);
        if (parameter == null)
            return new ArrayList<String>();
        
        String temp = parameter.getValue();

        String[] temps = temp.split("\\,");
        for (int ix = 0; ix < temps.length; ix++)
            temps[ix] = temps[ix].trim();

        return Arrays.asList(temps);
    }
    
    private String checkNullValue(String value, String defaultValue) {
    	if (value == null) {
    		return defaultValue;
    	}
    	return value;
    }

}
