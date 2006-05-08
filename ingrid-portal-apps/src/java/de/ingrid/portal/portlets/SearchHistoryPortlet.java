/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets;

import java.io.IOException;
import java.util.ArrayList;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;

import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.portal.config.IngridSessionPreferences;
import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.search.QueryHistory;

/**
 * This portlet handles the "History" fragment of the search-history page
 *
 * @author martin@wemove.com
 */
public class SearchHistoryPortlet extends GenericVelocityPortlet {

    //    private final static Log log = LogFactory.getLog(SearchSettingsPortlet.class);

    private final static String PARAMCV_ACTION_SHOW_ALL_QUERIES = "doShowAllQueries";

    /* (non-Javadoc)
     * @see javax.portlet.Portlet#init(javax.portlet.PortletConfig)
     */
    public void init(PortletConfig config) throws PortletException {
        super.init(config);
    }

    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);
        IngridSessionPreferences sessionPrefs = Utils.getSessionPreferences(request, IngridSessionPreferences.SESSION_KEY, IngridSessionPreferences.class);
        QueryHistory history = (QueryHistory)sessionPrefs.getInitializedObject(IngridSessionPreferences.QUERY_HISTORY, QueryHistory.class);
        int historySize = PortalConfig.getInstance().getInt(PortalConfig.QUERY_HISTORY_DISPLAY_SIZE, 10);
        if (historySize > history.size()) {
            context.put("queries", history);
        } else {
            ArrayList queries = new ArrayList();
            queries.addAll(history.subList(0, historySize));
            context.put("queries", queries);
        }
        super.doView(request, response);
    }

    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {

        String action = request.getParameter("action");
        if (action == null) {
            action = "";
        }

        // TODO: implement functionality
        if (action.equalsIgnoreCase(Settings.PARAMV_ACTION_SUBMIT)) {
            System.out.println("ACTION: form submit");

        } else if (action.equalsIgnoreCase(PARAMCV_ACTION_SHOW_ALL_QUERIES)) {
            System.out.println("ACTION: show all queries");
        }

    }
}