/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets.searchext;

import java.io.IOException;

import javax.portlet.ActionResponse;
import javax.portlet.PortletException;

/**
 * This portlet is the abstract base class of all "wizard" portlets in the Address/Topic
 * tab of the extended search. Encapsulates common stuff.
 *
 * @author martin@wemove.com
 */
abstract class SearchExtAdrTopic extends SearchExtAdr {

    // TAB values from action request (request parameter)

    protected final static String PARAMV_TAB_TERMS = "4";

    protected final static String PARAMV_TAB_MODE = "5";

    // PAGES FOR TABS

    protected final static String PAGE_TERMS = "/ingrid-portal/portal/search-extended/search-ext-adr-topic-terms.psml";

    protected final static String PAGE_MODE = "/ingrid-portal/portal/search-extended/search-ext-adr-topic-mode.psml";

    protected void processTab(ActionResponse actionResponse, String tab) throws PortletException, IOException {
        if (tab.equals(PARAMV_TAB_TERMS)) {
            actionResponse.sendRedirect(PAGE_TERMS);
        } else if (tab.equals(PARAMV_TAB_MODE)) {
            actionResponse.sendRedirect(PAGE_MODE);
        } else {
            super.processTab(actionResponse, tab);
        }
    }
}