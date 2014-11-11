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
package de.ingrid.portal.portlets;

import java.io.IOException;
import java.security.Principal;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.forms.SearchSaveForm;
import de.ingrid.portal.global.IngridPersistencePrefs;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.search.SavedSearchQueries;
import de.ingrid.portal.search.SearchState;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class SearchSavePortlet extends GenericVelocityPortlet {

    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#doView(javax.portlet.RenderRequest,
     *      javax.portlet.RenderResponse)
     */
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        Context context = getContext(request);

        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()));
        context.put("MESSAGES", messages);

        SearchSaveForm f = (SearchSaveForm) Utils.getActionForm(request, SearchSaveForm.SESSION_KEY,
                SearchSaveForm.class);

        String cmd = request.getParameter("cmd");
        if (cmd == null) {
            f.clear();
        }

        Principal principal = request.getUserPrincipal();
        SavedSearchQueries searchSaveEntries = (SavedSearchQueries) IngridPersistencePrefs.getPref(principal.getName(),
                IngridPersistencePrefs.SEARCH_SAVE_ENTRIES);
        if (searchSaveEntries == null) {
            searchSaveEntries = new SavedSearchQueries();
        }

        context.put("max_entries", PortalConfig.getInstance().getString(PortalConfig.SAVE_ENTRIES_MAX_NUMBER, "10"));
        context.put("searchSaveEntries", searchSaveEntries);
        context.put("no_entries", new Integer(searchSaveEntries.size()));
        context.put("actionForm", f);

        super.doView(request, response);
    }

    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#processAction(javax.portlet.ActionRequest,
     *      javax.portlet.ActionResponse)
     */
    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {
        String action = request.getParameter("action");
        String deleteEntries = request.getParameter("deleteEntries");
        actionResponse.setRenderParameter("cmd", action);

        if (action == null) {
            action = "";
        }
        SearchSaveForm f = (SearchSaveForm) Utils.getActionForm(request, SearchSaveForm.SESSION_KEY,
                SearchSaveForm.class);

        if (action.equalsIgnoreCase(Settings.PARAMV_ACTION_SUBMIT) && deleteEntries == null) {
            f.clearErrors();
            f.populate(request);
            if (!f.validate()) {
                f.setError("", "searchSave.error.no.name.supplied");
                return;
            }
            // save the current query to the list
            Principal principal = request.getUserPrincipal();
            SavedSearchQueries searchSaveEntries = (SavedSearchQueries) IngridPersistencePrefs.getPref(principal
                    .getName(), IngridPersistencePrefs.SEARCH_SAVE_ENTRIES);
            if (searchSaveEntries == null) {
                searchSaveEntries = new SavedSearchQueries();
            }

            String queryString = SearchState.getSearchStateObjectAsString(request, Settings.PARAM_QUERY_STRING);
            String urlStr = Settings.PAGE_SEARCH_RESULT + SearchState.getURLParamsMainSearch(request);
            String selectedDS = SearchState.getSearchStateObjectAsString(request, Settings.PARAM_DATASOURCE);
            searchSaveEntries.add(queryString, urlStr, selectedDS, f.getInput(SearchSaveForm.FIELD_SAVE_ENTRY_NAME));
            IngridPersistencePrefs.setPref(principal.getName(), IngridPersistencePrefs.SEARCH_SAVE_ENTRIES,
                    searchSaveEntries);

        } else if (action.equalsIgnoreCase(Settings.PARAMV_ACTION_SUBMIT) && deleteEntries != null) {
            Principal principal = request.getUserPrincipal();
            SavedSearchQueries searchSaveEntries = (SavedSearchQueries) IngridPersistencePrefs.getPref(principal
                    .getName(), IngridPersistencePrefs.SEARCH_SAVE_ENTRIES);
            for (int i = 0; i < searchSaveEntries.size(); i++) {
                String chk = request.getParameter("chk_" + i);
                if (chk != null) {
                    searchSaveEntries.remove(i);
                }
            }
            IngridPersistencePrefs.setPref(principal.getName(), IngridPersistencePrefs.SEARCH_SAVE_ENTRIES,
                    searchSaveEntries);

            f.init();
            f.setError("", "searchSave.msg.entries.deleted");
        }
    }

}
