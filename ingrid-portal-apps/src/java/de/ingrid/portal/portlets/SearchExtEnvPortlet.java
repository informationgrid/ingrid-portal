/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets;

import java.io.IOException;

import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;

import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.portal.global.IngridResourceBundle;

/**
 * This portlet is the abstract base class of all "wizard" portlets in the EnvironmentInfo
 * area of the extended search.
 *
 * @author martin@wemove.com
 */
abstract class SearchExtEnvPortlet extends GenericVelocityPortlet {

    /** tab param value if main tab place is clicked */
    protected final static String TAB_TOPIC = "1";

    /** tab param value if main tab place is clicked */
    protected final static String TAB_PLACE = "2";

    /** tab param value if main tab time is clicked */
    protected final static String TAB_TIME = "3";

    /** tab param value if main tab search area is clicked */
    protected final static String TAB_AREA = "4";

    /** main extended search page for datasource "environmentinfos" -> envinfo: topic/thesaurus */
    protected final static String PAGE_TOPIC = "/ingrid-portal/portal/search-extended/search-ext-env-topic-terms.psml";

    /** main extended search page for datasource "environmentinfos" -> envinfo: topic/thesaurus */
    protected final static String PAGE_TOPIC_THESAURUS = "/ingrid-portal/portal/search-extended/search-ext-env-topic-thesaurus.psml";

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

        super.doView(request, response);
    }

    protected void processMainTab(ActionResponse actionResponse, String tab) throws PortletException, IOException {
        if (tab.equals(TAB_TOPIC)) {
            actionResponse.sendRedirect(PAGE_TOPIC);
        } else if (tab.equals(TAB_PLACE)) {

        } else if (tab.equals(TAB_TIME)) {

        } else if (tab.equals(TAB_AREA)) {

        }

        System.out.println("TAB: new Tab=" + tab);
    }
}