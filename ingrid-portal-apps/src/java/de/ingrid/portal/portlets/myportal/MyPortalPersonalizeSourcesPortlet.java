/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets.myportal;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.portal.forms.SearchExtEnvAreaSourcesForm;
import de.ingrid.portal.global.IngridPersistencePrefs;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class MyPortalPersonalizeSourcesPortlet extends GenericVelocityPortlet {

    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#doView(javax.portlet.RenderRequest,
     *      javax.portlet.RenderResponse)
     */
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        Context context = getContext(request);

        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()));
        context.put("MESSAGES", messages);

        PortletPreferences prefs = request.getPreferences();
        String titleKey = prefs.getValue("titleKey", "searchSettings.title.rankingAndGrouping");
        response.setTitle(messages.getString(titleKey));

        SearchExtEnvAreaSourcesForm f = (SearchExtEnvAreaSourcesForm) Utils.getActionForm(request,
                SearchExtEnvAreaSourcesForm.SESSION_KEY, SearchExtEnvAreaSourcesForm.class);

        String cmd = request.getParameter("cmd");

        if (cmd == null) {
            f.init();
            Principal principal = request.getUserPrincipal();
            HashMap searchSources = (HashMap) IngridPersistencePrefs.getPref(principal.getName(),
                    IngridPersistencePrefs.SEARCH_SOURCES);
            if (searchSources != null) {
                String[] sources = (String[]) searchSources.get("sources");
                String[] meta = (String[]) searchSources.get("meta");
                f.setInput(SearchExtEnvAreaSourcesForm.FIELD_CHK_SOURCES, sources);
                f.setInput(SearchExtEnvAreaSourcesForm.FIELD_CHK_META, meta);
            }

        }
        context.put("actionForm", f);

        // set positions in main and sub tab
        super.doView(request, response);
    }

    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#processAction(javax.portlet.ActionRequest,
     *      javax.portlet.ActionResponse)
     */
    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {
        SearchExtEnvAreaSourcesForm f = (SearchExtEnvAreaSourcesForm) Utils.getActionForm(request,
                SearchExtEnvAreaSourcesForm.SESSION_KEY, SearchExtEnvAreaSourcesForm.class);

        f.clearErrors();
        f.populate(request);
        if (!f.validate()) {
            return;
        }

        String action = request.getParameter(Settings.PARAM_ACTION);
        if (action != null && action.equals("doOriginalSettings")) {
            f.init();
        }
        String[] sources = f.getInputAsArray(SearchExtEnvAreaSourcesForm.FIELD_CHK_SOURCES);
        String[] meta = f.getInputAsArray(SearchExtEnvAreaSourcesForm.FIELD_CHK_META);
        Principal principal = request.getUserPrincipal();
        HashMap searchSources = new HashMap();
        searchSources.put("meta", meta);
        searchSources.put("sources", sources);
        IngridPersistencePrefs.setPref(principal.getName(), IngridPersistencePrefs.SEARCH_SOURCES, searchSources);
    }
}
