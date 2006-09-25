/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets.searchext;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;

import org.apache.portals.messaging.PortletMessaging;
import org.apache.velocity.context.Context;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.forms.SearchExtEnvAreaSourcesForm;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.search.UtilsSearch;

/**
 * This portlet handles the fragment of "Sources" input in the extended search
 * 
 * @author martin@wemove.com
 */
public class SearchExtEnvAreaSourcesPortlet extends SearchExtEnvArea {

    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);

        SearchExtEnvAreaSourcesForm f = (SearchExtEnvAreaSourcesForm) Utils.getActionForm(request,
                SearchExtEnvAreaSourcesForm.SESSION_KEY, SearchExtEnvAreaSourcesForm.class);

        String cmd = request.getParameter("cmd");

        if (cmd == null) {
            f.init();
        }
        context.put("actionForm", f);
        context.put("enableContents", PortalConfig.getInstance().getBoolean("portal.enable.search.ext.env.area.contents", Boolean.TRUE));
        context.put("enableSources", PortalConfig.getInstance().getBoolean("portal.enable.search.ext.env.area.sources", Boolean.TRUE));
        context.put("enablePartner", PortalConfig.getInstance().getBoolean("portal.enable.search.ext.env.area.partner", Boolean.TRUE));
        context.put("enableSourcesMetaOnly", PortalConfig.getInstance().getBoolean("portal.enable.search.ext.env.area.sources.meta.only", Boolean.FALSE));

        // set positions in main and sub tab
        context.put(VAR_MAIN_TAB, PARAMV_TAB_AREA);
        context.put(VAR_SUB_TAB, PARAMV_TAB_SOURCES);

        super.doView(request, response);
    }

    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {
        String action = request.getParameter(Settings.PARAM_ACTION);
        if (action == null) {
            action = "";
        }
        String submittedAddToQuery = request.getParameter("submitAddToQuery");

        if (submittedAddToQuery != null) {
            actionResponse.setRenderParameter("cmd", "form_sent");
            SearchExtEnvAreaSourcesForm f = (SearchExtEnvAreaSourcesForm) Utils.getActionForm(request,
                    SearchExtEnvAreaSourcesForm.SESSION_KEY, SearchExtEnvAreaSourcesForm.class);
            f.clearErrors();

            f.populate(request);
            if (!f.validate()) {
                return;
            }

            // Zur Suchanfrage hinzufuegen
            String queryStr = (String) PortletMessaging.receive(request, Settings.MSG_TOPIC_SEARCH,
                    Settings.PARAM_QUERY_STRING);
            String[] sources = f.getInputAsArray(SearchExtEnvAreaSourcesForm.FIELD_CHK_SOURCES);
            String[] meta = f.getInputAsArray(SearchExtEnvAreaSourcesForm.FIELD_CHK_META);
            PortletMessaging.publish(request, Settings.MSG_TOPIC_SEARCH, Settings.PARAM_QUERY_STRING, UtilsSearch
                    .processSearchSources(queryStr, sources, meta));

        } else if (action.equalsIgnoreCase(Settings.PARAMV_ACTION_CHANGE_TAB)) {
            String newTab = request.getParameter(Settings.PARAM_TAB);
            processTab(actionResponse, newTab);
        }
    }
}