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

/**
 * This portlet handles the fragment of the terms input in the extended search
 *
 * @author martin@wemove.com
 */
public class SearchExtEnvTopicTermsPortlet extends SearchExtEnv {

    /** tab param value if sub tab thesaurus is clicked */
    private final static String PARAMV_TAB_TOPIC_THESAURUS = "6";

    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);

        // set positions in main and sub tab
        context.put("tab", TAB_TOPIC);
        context.put("subtab", "1");

        super.doView(request, response);
    }

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

        } else if (action.equalsIgnoreCase(Settings.PARAMV_ACTION_CHANGE_TAB)) {
            String newTab = request.getParameter(Settings.PARAM_TAB);
            if (newTab.equals(PARAMV_TAB_TOPIC_THESAURUS)) {
                actionResponse.sendRedirect(PAGE_TOPIC_THESAURUS);

            } else {
                processMainTab(actionResponse, newTab);
            }
        }
    }
}