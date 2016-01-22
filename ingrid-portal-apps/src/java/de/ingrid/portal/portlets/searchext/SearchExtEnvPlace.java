/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2016 wemove digital solutions GmbH
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
abstract class SearchExtEnvPlace extends SearchExtEnv{

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
