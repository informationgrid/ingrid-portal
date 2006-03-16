/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets;

import java.io.IOException;

import javax.portlet.ActionResponse;
import javax.portlet.PortletException;

/**
 * This portlet is the abstract base class of all "wizard" portlets in the EnvironmentInfo/Searcharea
 * tab of the extended search. Encapsulates common stuff.
 *
 * @author martin@wemove.com
 */
abstract class SearchExtEnvArea extends SearchExtEnv {

    // TAB values from action request (request parameter)

    protected final static String PARAMV_TAB_CONTENTS = "5";

    protected final static String PARAMV_TAB_SOURCES = "6";

    protected final static String PARAMV_TAB_PARTNER = "7";

    // PAGES FOR TABS

    protected final static String PAGE_CONTENTS = "/ingrid-portal/portal/search-extended/search-ext-env-area-contents.psml";

    protected final static String PAGE_SOURCES = "/ingrid-portal/portal/search-extended/search-ext-env-area-sources.psml";

    protected final static String PAGE_PARTNER = "/ingrid-portal/portal/search-extended/search-ext-env-area-partner.psml";

    protected void processTab(ActionResponse actionResponse, String tab) throws PortletException, IOException {
        if (tab.equals(PARAMV_TAB_CONTENTS)) {
            actionResponse.sendRedirect(PAGE_CONTENTS);
        } else if (tab.equals(PARAMV_TAB_SOURCES)) {
            actionResponse.sendRedirect(PAGE_SOURCES);
        } else if (tab.equals(PARAMV_TAB_PARTNER)) {
            actionResponse.sendRedirect(PAGE_PARTNER);
        } else {
            super.processTab(actionResponse, tab);
        }
    }
}