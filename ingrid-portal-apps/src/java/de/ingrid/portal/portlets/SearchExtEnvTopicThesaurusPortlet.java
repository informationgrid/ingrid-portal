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
 * This portlet handles the fragment of the thesaurus input in the extended search.
 *
 * @author martin@wemove.com
 */
public class SearchExtEnvTopicThesaurusPortlet extends SearchExtEnvPortlet {

    /** tab param value if sub tab thesaurus is clicked */
    private final static String TAB_TOPIC_TERMS = "5";

    /** main extended search page for datasource "environmentinfos" -> envinfo: topic/thesaurus */
    private final static String PAGE_TOPIC_TERMS = "/ingrid-portal/portal/search-extended/search-ext-env-topic-terms.psml";

    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);
        context.put("tab", TAB_TOPIC);

        super.doView(request, response);
    }

    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {
        String action = request.getParameter(Settings.PARAM_ACTION);
        if (action == null) {
            return;
        }

        // TODO: implement functionality
        if (action.equalsIgnoreCase(Settings.PARAMV_ACTION_SUBMIT)) {
            System.out.println("ACTION: form submit");

        } else if (action.equalsIgnoreCase(Settings.PARAMV_ACTION_CHANGE_TAB)) {
            String newTab = request.getParameter(Settings.PARAM_TAB);
            if (newTab.equals(TAB_TOPIC_TERMS)) {
                actionResponse.sendRedirect(PAGE_TOPIC_TERMS);

            } else {
                processMainTab(actionResponse, newTab);
            }
        }
    }
}