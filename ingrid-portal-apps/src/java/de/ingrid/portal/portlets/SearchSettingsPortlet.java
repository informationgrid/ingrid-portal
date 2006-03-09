/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;

import org.apache.portals.bridges.velocity.AbstractVelocityMessagingPortlet;

import de.ingrid.portal.global.Settings;

/**
 * This portlet handles the "Settings" fragment of the main-search-settings page
 *
 * @author martin@wemove.com
 */
public class SearchSettingsPortlet extends AbstractVelocityMessagingPortlet {

    //    private final static Log log = LogFactory.getLog(SearchSettingsPortlet.class);

    /* (non-Javadoc)
     * @see javax.portlet.Portlet#init(javax.portlet.PortletConfig)
     */
    public void init(PortletConfig config) throws PortletException {
        // set our message "scope" for inter portlet messaging
        setTopic(Settings.MSG_TOPIC_SEARCH);

        super.init(config);
    }

    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        //        Context context = getContext(request);

        super.doView(request, response);
    }

    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {

        String action = request.getParameter("action");
        if (action == null) {
            action = "";
        }
        
        // TODO: implement functionality
        if (action.equalsIgnoreCase(Settings.PARAMV_ACTION_SUBMIT)) {
            
        } else if (action.equalsIgnoreCase(Settings.PARAMV_ACTION_ORIGINAL_SETTINGS)) {
            
        }
    }
}