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

/**
 * This portlet handles the fragment of the geothesaurus input in the extended search.
 *
 * @author martin@wemove.com
 */
public class SearchExtEnvPlaceGeothesaurusPortlet extends SearchExtEnvPlace {

    // VIEW TEMPLATES

    private final static String TEMPLATE_START = "/WEB-INF/templates/search_extended/search_ext_env_place_geothesaurus.vm";

    private final static String TEMPLATE_RESULTS = "/WEB-INF/templates/search_extended/search_ext_env_place_geothesaurus_results.vm";

    private final static String TEMPLATE_BROWSE = "/WEB-INF/templates/search_extended/search_ext_env_place_geothesaurus_browse.vm";

    // PARAMETER VALUES

    private final static String PARAMV_VIEW_RESULTS = "1";

    private final static String PARAMV_VIEW_BROWSE = "2";

    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);

        // set positions in main and sub tab
        context.put(VAR_MAIN_TAB, PARAMV_TAB_PLACE);
        context.put(VAR_SUB_TAB, PARAMV_TAB_GEOTHESAURUS);

        setDefaultViewPage(TEMPLATE_START);

        // ----------------------------------
        // check for passed RENDER PARAMETERS and react
        // ----------------------------------
        String action = request.getParameter(Settings.PARAM_ACTION);
        if (action == null) {
            action = "";
        }
        if (action.equals(PARAMV_VIEW_RESULTS)) {
            setDefaultViewPage(TEMPLATE_RESULTS);
        } else if (action.equals(PARAMV_VIEW_BROWSE)) {
            setDefaultViewPage(TEMPLATE_BROWSE);
        }

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
        String submittedSearch = request.getParameter("submitSearch");
        String submittedAddToQuery = request.getParameter("submitAddToQuery");

        // TODO: implement functionality
        if (submittedSearch != null) {

            // In Thesaurus suchen

            // redirect to same page with view param setting view !
            String urlViewParam = "?" + Utils.toURLParam(PARAMV_VIEW_RESULTS, Settings.PARAM_ACTION);
            actionResponse.sendRedirect(PAGE_GEOTHESAURUS + urlViewParam);

        } else if (submittedAddToQuery != null) {

            // Zur Suchanfrage hinzufuegen

            // redirect to same page with view param where we currently are (so we keep view !)
            String currView = getDefaultViewPage();
            String urlViewParam = "";
            if (currView.equals(TEMPLATE_RESULTS)) {
                urlViewParam = "?" + Utils.toURLParam(PARAMV_VIEW_RESULTS, Settings.PARAM_ACTION);
            } else if (currView.equals(TEMPLATE_BROWSE)) {
                urlViewParam = "?" + Utils.toURLParam(PARAMV_VIEW_BROWSE, Settings.PARAM_ACTION);
            }
            actionResponse.sendRedirect(PAGE_GEOTHESAURUS + urlViewParam);

        } else if (action.equalsIgnoreCase("doBrowse")) {

            // SNS Deskriptor browsen

            // redirect to same page with view param setting view !
            String urlViewParam = "?" + Utils.toURLParam(PARAMV_VIEW_BROWSE, Settings.PARAM_ACTION);
            actionResponse.sendRedirect(PAGE_GEOTHESAURUS + urlViewParam);

        } else if (action.equalsIgnoreCase(Settings.PARAMV_ACTION_CHANGE_TAB)) {
            String newTab = request.getParameter(Settings.PARAM_TAB);
            processTab(actionResponse, newTab);
        }
    }
}