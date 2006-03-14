/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;

import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;

/**
 * This portlet handles the fragment of the terms input in the extended search
 *
 * @author martin@wemove.com
 */
public class SearchExtEnvTopicTermsPortlet extends GenericVelocityPortlet {

    //    private final static Log log = LogFactory.getLog(SearchSettingsPortlet.class);

    /** tab param value if main tab place is clicked */
    private final static String TAB_PLACE = "2";

    /** tab param value if main tab time is clicked */
    private final static String TAB_TIME = "3";

    /** tab param value if main tab search area is clicked */
    private final static String TAB_AREA = "4";

    /** tab param value if sub tab thesaurus is clicked */
    private final static String TAB_TOPIC_THESAURUS = "6";

    /** main extended search page for datasource "environmentinfos" -> envinfo: topic/thesaurus */
    private final static String PAGE_TOPIC_THESAURUS = "/ingrid-portal/portal/search-extended/search-ext-env-topic-thesaurus.psml";

    /* (non-Javadoc)
     * @see javax.portlet.Portlet#init(javax.portlet.PortletConfig)
     */
    public void init(PortletConfig config) throws PortletException {
        super.init(config);
    }

    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);
        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()));
        context.put("MESSAGES", messages);

        context.put("tab", Settings.PARAMV_EXTSEARCH_TAB_TOPIC);

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
            if (newTab.equals(TAB_PLACE)) {

            } else if (newTab.equals(TAB_TIME)) {

            } else if (newTab.equals(TAB_AREA)) {

            } else if (newTab.equals(TAB_TOPIC_THESAURUS)) {
                actionResponse.sendRedirect(PAGE_TOPIC_THESAURUS);
            }

            System.out.println("TAB: new Tab=" + newTab);
        }
    }
}