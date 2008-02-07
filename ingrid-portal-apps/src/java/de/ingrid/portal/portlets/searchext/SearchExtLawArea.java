/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets.searchext;

import java.io.IOException;

import javax.portlet.ActionResponse;
import javax.portlet.PortletException;

/**
 * This portlet is the abstract base class of all "wizard" portlets in the EnvironmentInfo/Searcharea
 * tab of the extended search. Encapsulates common stuff.
 *
 * @author martin@wemove.com
 */
abstract class SearchExtLawArea extends SearchExtLaw {

    // TAB values from action request (request parameter)

    protected final static String PARAMV_TAB_PARTNER = "7";

    // PAGES FOR TABS

    protected final static String PAGE_PARTNER = "/ingrid-portal/portal/search-extended/search-ext-law-area-partner.psml";

    protected void processTab(ActionResponse actionResponse, String tab) throws PortletException, IOException {
        if (tab.equals(PARAMV_TAB_PARTNER)) {
            actionResponse.sendRedirect(PAGE_PARTNER);
        } else {
            super.processTab(actionResponse, tab);
        }
    }
}