/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets.myportal;

import java.io.IOException;
import java.security.Principal;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.jetspeed.om.folder.Folder;

import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.portlets.admin.ConfigureHomepagePortlet;

/**
 * Portlet for configuration of personalized home page.
 * 
 * @author martin@wemove.com
 */
public class MyPortalPersonalizeHomePortlet extends ConfigureHomepagePortlet {

    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#doView(javax.portlet.RenderRequest,
     *      javax.portlet.RenderResponse)
     */
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()), request.getLocale());

        PortletPreferences prefs = request.getPreferences();
        String titleKey = prefs.getValue("titleKey", "searchSettings.title.rankingAndGrouping");
        response.setTitle(messages.getString(titleKey));

        if (pagePath == null) {
            Principal principal = request.getUserPrincipal();
            pagePath = Folder.USER_FOLDER + principal.getName() + "/default-page.psml";
        }

        super.doView(request, response);
    }

    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#processAction(javax.portlet.ActionRequest,
     *      javax.portlet.ActionResponse)
     */
    public void processAction(ActionRequest request, ActionResponse response) throws PortletException,
            IOException {
        if (pagePath == null) {
            Principal principal = request.getUserPrincipal();
            pagePath = Folder.USER_FOLDER + principal.getName() + "/default-page.psml";
        }
        
        super.processAction(request, response);
    }
}
