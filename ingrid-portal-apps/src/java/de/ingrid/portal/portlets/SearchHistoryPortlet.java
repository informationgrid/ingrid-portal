/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;

import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.portal.config.IngridSessionPreferences;
import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.global.IngridPersistencePrefs;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.search.QueryHistory;
import de.ingrid.portal.search.SavedSearchQueries;

/**
 * This portlet handles the "History" fragment of the search-history page
 * 
 * @author martin@wemove.com
 */
public class SearchHistoryPortlet extends GenericVelocityPortlet {

    // private final static Logger log =
    // LoggerFactory.getLogger(SearchSettingsPortlet.class);

    /*
     * (non-Javadoc)
     * 
     * @see javax.portlet.Portlet#init(javax.portlet.PortletConfig)
     */
    public void init(PortletConfig config) throws PortletException {
        super.init(config);
    }

    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);

        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()), request.getLocale());
        context.put("MESSAGES", messages);

        IngridSessionPreferences sessionPrefs = Utils.getSessionPreferences(request,
                IngridSessionPreferences.SESSION_KEY, IngridSessionPreferences.class);
        QueryHistory history = (QueryHistory) sessionPrefs.getInitializedObject(IngridSessionPreferences.QUERY_HISTORY,
                QueryHistory.class);

        int historySize = PortalConfig.getInstance().getInt(PortalConfig.QUERY_HISTORY_DISPLAY_SIZE, 10);
        if (historySize > history.size()) {
            context.put("queries", history);
        } else {
            ArrayList queries = new ArrayList();
            queries.addAll(history.subList(0, historySize));
            context.put("queries", queries);
        }

        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            SavedSearchQueries searchSaveEntries = (SavedSearchQueries) IngridPersistencePrefs.getPref(principal.getName(),
                    IngridPersistencePrefs.SEARCH_SAVE_ENTRIES);
            if (searchSaveEntries == null) {
                searchSaveEntries = new SavedSearchQueries();
            }
            context.put("max_entries", PortalConfig.getInstance().getString(PortalConfig.SAVE_ENTRIES_MAX_NUMBER, "10"));
            context.put("searchSaveEntries", searchSaveEntries);
            context.put("no_entries", new Integer(searchSaveEntries.size()));
        }

        super.doView(request, response);
    }

    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {

        String action = request.getParameter("action");
        String deleteEntries = request.getParameter("deleteEntries");
        if (action == null) {
            action = "";
        }

        if (action.equalsIgnoreCase(Settings.PARAMV_ACTION_SUBMIT) && deleteEntries != null) {
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
        }

    }
}
