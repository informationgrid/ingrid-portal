/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets.searchext;

import java.io.IOException;

import javax.portlet.ActionResponse;
import javax.portlet.PortletException;

/**
 * This portlet is the abstract base class of all "wizard" portlets in the Address/Area
 * tab of the extended search. Encapsulates common stuff.
 *
 * @author martin@wemove.com
 */
abstract class SearchExtAdrArea extends SearchExtAdr {

    // TAB values from action request (request parameter)

    protected final static String PARAMV_TAB_PARTNER = "4";

    // PAGES FOR TABS

    protected final static String PAGE_PARTNER = "/portal/search-extended/search-ext-adr-area-partner.psml";

    protected void processTab(ActionResponse actionResponse, String tab) throws PortletException, IOException {
        if (tab.equals(PARAMV_TAB_PARTNER)) {
            actionResponse.sendRedirect(actionResponse.encodeURL(PAGE_PARTNER));
        } else {
            super.processTab(actionResponse, tab);
        }
    }
}