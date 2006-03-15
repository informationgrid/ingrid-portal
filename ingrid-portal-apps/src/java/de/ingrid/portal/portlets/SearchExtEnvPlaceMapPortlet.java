/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;

import org.apache.velocity.context.Context;

import de.ingrid.portal.global.Settings;
import de.ingrid.portal.interfaces.WMSInterface;
import de.ingrid.portal.interfaces.impl.WMSInterfaceImpl;

/**
 * This portlet handles the fragment of the map input in the extended search.
 *
 * @author martin@wemove.com
 */
public class SearchExtEnvPlaceMapPortlet extends SearchExtEnvBase {

    // PAGES

    /** main extended search page for datasource "environmentinfos" -> envinfo: place/map */
    private final static String PAGE_PLACE_GEOTHESAURUS = "/ingrid-portal/portal/search-extended/search-ext-env-place-geothesaurus.psml";

    // PARAMETER VALUES

    /** tab param value if sub tab "geothesaurus" is clicked */
    private final static String PARAMV_TAB_PLACE_GEOTHESAURUS = "5";

    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);

        // set positions in main and sub tab
        context.put("tab", TAB_PLACE);
        context.put("subtab", "2");

        // get and set URL to WMS Server
        WMSInterface service = WMSInterfaceImpl.getInstance();
        String wmsURL = service.getWMSSearchURL(request.getPortletSession().getId());
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
            // changed main or sub tab
            String newTab = request.getParameter(Settings.PARAM_TAB);
            if (newTab.equals(PARAMV_TAB_PLACE_GEOTHESAURUS)) {
                actionResponse.sendRedirect(PAGE_PLACE_GEOTHESAURUS);

            } else {
                processMainTab(actionResponse, newTab);
            }
        }
    }
}