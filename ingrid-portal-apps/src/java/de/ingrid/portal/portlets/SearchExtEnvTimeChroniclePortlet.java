/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;

import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;

/**
 * This portlet handles the fragment of the time references (events in chronicle) in the extended search
 *
 * @author martin@wemove.com
 */
public class SearchExtEnvTimeChroniclePortlet extends GenericVelocityPortlet {

    // VIEW TEMPLATES

    private final static String TEMPLATE_START = "/WEB-INF/templates/search_extended/search_ext_env_time_chronicle.vm";

    private final static String TEMPLATE_RESULTS = "/WEB-INF/templates/search_extended/search_ext_env_time_chronicle_results.vm";

    // PAGES

    /** our current page, for redirecting with URL params */
    private final static String PAGE_CURRENT = "/ingrid-portal/portal/search-extended/search-ext-env-time-constraint.psml";

    // PARAMETER VALUES

    private final static String PARAMV_VIEW_RESULTS = "1";

    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);
        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()));
        context.put("MESSAGES", messages);

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
        }

        super.doView(request, response);
    }

    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {
        String action = request.getParameter(Settings.PARAM_ACTION);
        if (action == null) {
            return;
        }
        // TODO: implement functionality
        if (action.equalsIgnoreCase(Settings.PARAMV_ACTION_NEW_SEARCH)) {

            // Ereignisse suchen

            // redirect to same page with view param setting view !
            String urlViewParam = "?" + Utils.toURLParam(PARAMV_VIEW_RESULTS, Settings.PARAM_ACTION);
            actionResponse.sendRedirect(PAGE_CURRENT + urlViewParam);

        } else if (action.equalsIgnoreCase("doEvent")) {

            // Ereignis anzeigen

            // redirect to same page with view param setting view !
            String urlViewParam = "?" + Utils.toURLParam(PARAMV_VIEW_RESULTS, Settings.PARAM_ACTION);
            actionResponse.sendRedirect(PAGE_CURRENT + urlViewParam);
        }
    }
}