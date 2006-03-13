/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets;

import java.io.IOException;
import java.security.Principal;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.jetspeed.login.LoginConstants;
import org.apache.jetspeed.request.RequestContext;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.app.FieldMethodizer;
import org.apache.velocity.context.Context;

import de.ingrid.portal.forms.ContactForm;
import de.ingrid.portal.forms.LoginForm;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Utils;

/**
 * TODO Describe your created type (class, etc.) here.
 *
 * @author joachim@wemove.com
 */
public class LoginPortlet extends GenericVelocityPortlet {

    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#doView(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
     */
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        Context context = getContext(request);

        LoginForm frm = (LoginForm) Utils.getActionForm(request, LoginForm.SESSION_KEY, LoginForm.class);
        
        String errorCode = request.getParameter("errorCode");
        if (errorCode != null) {
            frm.clearErrors();
            if (errorCode.equals(LoginConstants.ERROR_UNKNOWN_USER.toString())) {
                frm.setError(LoginForm.FIELD_USERNAME, "login.error.noUsername");
            } else if (errorCode.equals(LoginConstants.ERROR_INVALID_PASSWORD.toString())) {
                frm.setError(LoginForm.FIELD_PASSWORD, "login.error.noPassword");
            } else {
                frm.setError("", "login.error.generell");
            }
        } else {
            // TODO Localize!
            frm.setINITIAL_USERNAME("Benutzer");
            frm.setINITIAL_PASSWORD("Password");
            frm.init();
            frm.clearErrors();
        }
        context.put("actionForm", frm);
        
        context.put("loginConstants", new FieldMethodizer(new LoginConstants()));

        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()));
        context.put("MESSAGES", messages);
        super.doView(request, response);
    }

    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#processAction(javax.portlet.ActionRequest, javax.portlet.ActionResponse)
     */
    public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {
        
        LoginForm frm = (LoginForm) Utils.getActionForm(request, LoginForm.SESSION_KEY, LoginForm.class);

        String cmd = request.getParameter("cmd");
        if (cmd != null && cmd.equals("doLogin")) {
            HttpSession session = ((RequestContext)request.getAttribute(RequestContext.REQUEST_PORTALENV)).getRequest().getSession(true);
            
            frm.populate(request);
    
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
    
            response.sendRedirect(response.encodeURL(((RequestContext)request.getAttribute(RequestContext.REQUEST_PORTALENV)).getRequest().getContextPath()+ "/login/redirector"));
        } else {
            if (request.getUserPrincipal() != null) {
                response.sendRedirect(response.encodeURL("/"));
            }
            Integer errorCode = (Integer)((RequestContext)request.getAttribute(RequestContext.REQUEST_PORTALENV)).getSessionAttribute(LoginConstants.ERRORCODE);
            if (errorCode != null) {
                response.setRenderParameter("errorCode", errorCode.toString());
            }
        }
    }

}
