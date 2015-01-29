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
 * Copyright (c) 2007 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets.searchcatalog;

import java.io.IOException;

import javax.portlet.ActionResponse;
import javax.portlet.PortletException;

import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.global.IngridResourceBundle;

/**
 * This portlet is the abstract base class of all "tab" portlets in the search/catalog area. 
 * Encapsulates common stuff (Main Tab Navigation, resources ...).
 *
 * @author martin@wemove.com
 */
abstract class SearchCatalog extends GenericVelocityPortlet {

    // TAB values from action request (request parameter)

    /** tab param value if tab hierarchy is clicked */
    protected final static String PARAMV_TAB_HIERARCHY = "1";

    /** tab param value if tab thesaurus is clicked */
    protected final static String PARAMV_TAB_THESAURUS = "2";

    // START PAGES FOR TABS

    /** page for tab "hierarchy" */
    protected final static String PAGE_HIERARCHY = "/portal/search-catalog/search-catalog-hierarchy.psml";

    /** page for tab "thesaurus" */
    protected final static String PAGE_THESAURUS = "/portal/search-catalog/search-catalog-thesaurus.psml";

    // VARIABLE NAMES FOR VELOCITY

    /** velocity variable name for main tab, has to be put to context, so correct tab is selected */
    protected final static String VAR_MAIN_TAB = "tab";

    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);
        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()), request.getLocale());
        context.put("MESSAGES", messages);

        context.put("enable_thesaurus", PortalConfig.getInstance().getBoolean(
                PortalConfig.PORTAL_ENABLE_SEARCH_CATALOG_THESAURUS, Boolean.FALSE));

        context.put("enable_hierarchy_tree", PortalConfig.getInstance().getBoolean(
                PortalConfig.PORTAL_ENABLE_SEARCH_CATALOG_HIERARCHY_TREE, Boolean.FALSE));
		
        super.doView(request, response);
    }

    protected void processTab(ActionResponse actionResponse, String tab) throws PortletException, IOException {
        if (tab.equals(PARAMV_TAB_HIERARCHY)) {
            actionResponse.sendRedirect(actionResponse.encodeURL(PAGE_HIERARCHY));
        } else if (tab.equals(PARAMV_TAB_THESAURUS)) {
            actionResponse.sendRedirect(actionResponse.encodeURL(PAGE_THESAURUS));
        }
    }
}