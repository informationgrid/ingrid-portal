/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets;

import java.io.IOException;

import javax.portlet.ActionResponse;
import javax.portlet.PortletException;

/**
 * This portlet is the abstract base class of all "wizard" portlets in the EnvironmentInfo/Topic
 * tab of the extended search. Encapsulates common stuff.
 *
 * @author martin@wemove.com
 */
abstract class SearchExtEnvTopic extends SearchExtEnv {

    // TAB values from action request (request parameter)

    protected final static String PARAMV_TAB_TERMS = "5";

    protected final static String PARAMV_TAB_THESAURUS = "6";

    // PAGES FOR TABS

    protected final static String PAGE_TERMS = "/ingrid-portal/portal/search-extended/search-ext-env-topic-terms.psml";

    protected final static String PAGE_THESAURUS = "/ingrid-portal/portal/search-extended/search-ext-env-topic-thesaurus.psml";

    protected void processTab(ActionResponse actionResponse, String tab) throws PortletException, IOException {
        if (tab.equals(PARAMV_TAB_TERMS)) {
            actionResponse.sendRedirect(PAGE_TERMS);
        } else if (tab.equals(PARAMV_TAB_THESAURUS)) {
            actionResponse.sendRedirect(PAGE_THESAURUS);
        } else {
            super.processTab(actionResponse, tab);
        }
    }
}