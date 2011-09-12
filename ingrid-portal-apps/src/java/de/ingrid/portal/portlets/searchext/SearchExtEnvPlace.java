/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets.searchext;

import java.io.IOException;

import javax.portlet.ActionResponse;
import javax.portlet.PortletException;

/**
 * This portlet is the abstract base class of all "wizard" portlets in the EnvironmentInfo/Place
 * tab of the extended search. Encapsulates common stuff.
 *
 * @author martin@wemove.com
 */
abstract class SearchExtEnvPlace extends SearchExtEnv {

    // TAB values from action request (request parameter)

    protected final static String PARAMV_TAB_GEOTHESAURUS = "5";

    protected final static String PARAMV_TAB_MAP = "6";

    // PAGES FOR TABS

    protected final static String PAGE_GEOTHESAURUS = "/portal/search-extended/search-ext-env-place-geothesaurus.psml";

    protected final static String PAGE_MAP = "/portal/search-extended/search-ext-env-place-map.psml";

    protected void processTab(ActionResponse actionResponse, String tab) throws PortletException, IOException {
        if (tab.equals(PARAMV_TAB_GEOTHESAURUS)) {
            actionResponse.sendRedirect(actionResponse.encodeURL(PAGE_GEOTHESAURUS));
        } else if (tab.equals(PARAMV_TAB_MAP)) {
            actionResponse.sendRedirect(actionResponse.encodeURL(PAGE_MAP));
        } else {
            super.processTab(actionResponse, tab);
        }
    }
}