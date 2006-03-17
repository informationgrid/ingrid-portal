/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets;

import java.io.IOException;

import javax.portlet.ActionResponse;
import javax.portlet.PortletException;

import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.portal.global.IngridResourceBundle;

/**
 * This portlet is the abstract base class of all "wizard" portlets in the Address
 * area of the extended search. Encapsulates common stuff (Main Tab Navigation, ressources ...).
 *
 * @author martin@wemove.com
 */
abstract class SearchExtAdr extends GenericVelocityPortlet {

    // TAB values from action request (request parameter)

    /** tab param value if main tab place is clicked */
    protected final static String PARAMV_TAB_TOPIC = "1";

    /** tab param value if main tab place is clicked */
    protected final static String PARAMV_TAB_PLACE = "2";

    /** tab param value if main tab search area is clicked */
    protected final static String PARAMV_TAB_AREA = "3";

    // START PAGES FOR MAIN TABS

    /** page for main tab "topic" */
    protected final static String PAGE_TOPIC = "/ingrid-portal/portal/search-extended/search-ext-adr-topic-terms.psml";

    /** page for main tab "place" */
    protected final static String PAGE_PLACE = "/ingrid-portal/portal/search-extended/search-ext-adr-place-reference.psml";

    /** page for main tab "search area" */
    protected final static String PAGE_AREA = "/ingrid-portal/portal/search-extended/search-ext-adr-area-partner.psml";

    // VARIABLE NAMES FOR VELOCITY

    /** velocity variable name for main tab, has to be put to context, so correct tab is selected */
    protected final static String VAR_MAIN_TAB = "tab";

    /** velocity variable name for sub tab, has to be put to context, so correct sub tab is selected */
    protected final static String VAR_SUB_TAB = "subtab";

    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);
        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()));
        context.put("MESSAGES", messages);

        super.doView(request, response);
    }

    protected void processTab(ActionResponse actionResponse, String tab) throws PortletException, IOException {
        if (tab.equals(PARAMV_TAB_TOPIC)) {
            actionResponse.sendRedirect(PAGE_TOPIC);
        } else if (tab.equals(PARAMV_TAB_PLACE)) {
            actionResponse.sendRedirect(PAGE_PLACE);
        } else if (tab.equals(PARAMV_TAB_AREA)) {
            actionResponse.sendRedirect(PAGE_AREA);
        }
    }
}