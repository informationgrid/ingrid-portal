/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletSession;

import de.ingrid.portal.global.Settings;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class DetectJavaScriptPortlet extends GenericPortlet {
    
    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws javax.portlet.PortletException, java.io.IOException {
        
        if (request.getParameter("js") != null) {
            request.getPortletSession().setAttribute(Settings.MSG_HAS_JAVASCRIPT, Settings.MSGV_TRUE, PortletSession.APPLICATION_SCOPE);
        } else {
            request.getPortletSession().removeAttribute(Settings.MSG_HAS_JAVASCRIPT, PortletSession.APPLICATION_SCOPE);
        }
        
        response.setContentType("text/html");
        response.getWriter().println("");
    }
}
