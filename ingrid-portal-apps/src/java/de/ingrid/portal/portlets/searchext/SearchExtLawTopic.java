/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets.searchext;

import java.io.IOException;

import javax.portlet.ActionResponse;
import javax.portlet.PortletException;

/**
 * This portlet is the abstract base class of all "wizard" portlets in the Law/Topic
 * tab of the extended search. Encapsulates common stuff.
 *
 * @author martin@wemove.com
 */
abstract class SearchExtLawTopic extends SearchExtLaw {

    // TAB values from action request (request parameter)

    protected final static String PARAMV_TAB_TERMS = "5";

    protected final static String PARAMV_TAB_THESAURUS = "6";

    // PAGES FOR TABS

    protected final static String PAGE_TERMS = "/portal/search-extended/search-ext-law-topic-terms.psml";

    protected final static String PAGE_THESAURUS = "/portal/search-extended/search-ext-law-topic-thesaurus.psml";

    protected void processTab(ActionResponse actionResponse, String tab) throws PortletException, IOException {
        if (tab.equals(PARAMV_TAB_TERMS)) {
            actionResponse.sendRedirect(actionResponse.encodeURL(PAGE_TERMS));
        } else if (tab.equals(PARAMV_TAB_THESAURUS)) {
            actionResponse.sendRedirect(actionResponse.encodeURL(PAGE_THESAURUS));
        } else {
            super.processTab(actionResponse, tab);
        }
    }
}