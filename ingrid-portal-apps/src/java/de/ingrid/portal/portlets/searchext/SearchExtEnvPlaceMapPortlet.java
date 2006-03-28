/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets.searchext;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;

import org.apache.velocity.context.Context;

import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.interfaces.WMSInterface;
import de.ingrid.portal.interfaces.impl.WMSInterfaceImpl;

/**
 * This portlet handles the fragment of the map input in the extended search.
 *
 * @author martin@wemove.com
 */
public class SearchExtEnvPlaceMapPortlet extends SearchExtEnvPlace {

    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);
        boolean hasJavaScript = Utils.isJavaScriptEnabled(request);

        // set positions in main and sub tab
        context.put(VAR_MAIN_TAB, PARAMV_TAB_PLACE);
        context.put(VAR_SUB_TAB, PARAMV_TAB_MAP);

        // get and set URL to WMS Server
        WMSInterface service = WMSInterfaceImpl.getInstance();
        String wmsURL = service.getWMSSearchURL(request.getPortletSession().getId(), hasJavaScript);
        context.put("wmsURL", wmsURL);

        super.doView(request, response);
    }

    /**
     * NOTICE: on actions in same page we redirect to ourself with url param determining the view
     * template. If no view template is passed per URL param, the start template is rendered !
     * @see javax.portlet.Portlet#processAction(javax.portlet.ActionRequest, javax.portlet.ActionResponse)
     */
    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {
        String action = request.getParameter(Settings.PARAM_ACTION);
        if (action == null) {
            action = "";
        }
        String submittedAddToQuery = request.getParameter("submitAddToQuery");

        // TODO: implement functionality
        if (submittedAddToQuery != null) {

            // Zur Suchanfrage hinzufuegen

        } else if (action.equalsIgnoreCase("doSave")) {

            // Kartendienste speichern

        } else if (action.equalsIgnoreCase(Settings.PARAMV_ACTION_CHANGE_TAB)) {
            String newTab = request.getParameter(Settings.PARAM_TAB);
            processTab(actionResponse, newTab);
        }
    }
}