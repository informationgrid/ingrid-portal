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
 * area of the extended search. Encapsulates common stuuf (Main Tab Navigation, ressources ...).
 *
 * @author martin@wemove.com
 */
abstract class SearchExtEnv extends GenericVelocityPortlet {

    /** tab param value if main tab place is clicked */
    protected final static String PARAMV_TAB_TOPIC = "1";

    /** tab param value if main tab place is clicked */
    protected final static String PARAMV_TAB_PLACE = "2";

    /** tab param value if main tab time is clicked */
    protected final static String PARAMV_TAB_TIME = "3";

    /** tab param value if main tab search area is clicked */
    protected final static String PARAMV_TAB_AREA = "4";

    // START PAGES FOR MAIN TABS
    
    /** page for main tab "topic" */
    protected final static String PAGE_TOPIC = "/ingrid-portal/portal/search-extended/search-ext-env-topic-terms.psml";

    /** page for main tab "place" */
    protected final static String PAGE_PLACE = "/ingrid-portal/portal/search-extended/search-ext-env-place-geothesaurus.psml";

    /** page for main tab "time" */
    protected final static String PAGE_TIME = "/ingrid-portal/portal/search-extended/search-ext-env-time-constraint.psml";

    /** page for main tab "search area" */
    protected final static String PAGE_AREA = "/ingrid-portal/portal/search-extended/search-ext-env-area-contents.psml";

    // ADDITIONAL PAGES USED IN MULTIPLE PORTLETS (subclasses), so we keep them here
    
    /** page for envinfo: topic/thesaurus */
    protected final static String PAGE_TOPIC_THESAURUS = "/ingrid-portal/portal/search-extended/search-ext-env-topic-thesaurus.psml";

    /** page for envinfo: place/geothesaurus */
    protected final static String PAGE_PLACE_GEOTHESAURUS = "/ingrid-portal/portal/search-extended/search-ext-env-place-geothesaurus.psml";

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
        if (tab.equals(PARAMV_TAB_TOPIC)) {
            actionResponse.sendRedirect(PAGE_TOPIC);
        } else if (tab.equals(PARAMV_TAB_PLACE)) {
            actionResponse.sendRedirect(PAGE_PLACE);
        } else if (tab.equals(PARAMV_TAB_TIME)) {
            actionResponse.sendRedirect(PAGE_TIME);
        } else if (tab.equals(PARAMV_TAB_AREA)) {
            actionResponse.sendRedirect(PAGE_AREA);
        }
    }
}