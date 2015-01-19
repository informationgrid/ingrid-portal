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

    protected final static String PAGE_TERMS = "/portal/search-extended/search-ext-adr-topic-terms.psml";

    protected final static String PAGE_MODE = "/portal/search-extended/search-ext-adr-topic-mode.psml";

    protected void processTab(ActionResponse actionResponse, String tab) throws PortletException, IOException {
        if (tab.equals(PARAMV_TAB_TERMS)) {
            actionResponse.sendRedirect(actionResponse.encodeURL(PAGE_TERMS));
        } else if (tab.equals(PARAMV_TAB_MODE)) {
            actionResponse.sendRedirect(actionResponse.encodeURL(PAGE_MODE));
        } else {
            super.processTab(actionResponse, tab);
        }
    }
}