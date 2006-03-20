/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets.searchext;

import java.io.IOException;

import javax.portlet.ActionResponse;
import javax.portlet.PortletException;

/**
 * This portlet is the abstract base class of all "wizard" portlets in the Address/Place
 * tab of the extended search. Encapsulates common stuff.
 *
 * @author martin@wemove.com
 */
abstract class SearchExtAdrPlace extends SearchExtAdr {

    // TAB values from action request (request parameter)

    protected final static String PARAMV_TAB_REFERENCE = "4";

    // PAGES FOR TABS

    protected final static String PAGE_REFERENCE = "/ingrid-portal/portal/search-extended/search-ext-adr-place-reference.psml";

    protected void processTab(ActionResponse actionResponse, String tab) throws PortletException, IOException {
        if (tab.equals(PARAMV_TAB_REFERENCE)) {
            actionResponse.sendRedirect(PAGE_REFERENCE);
        } else {
            super.processTab(actionResponse, tab);
        }
    }
}