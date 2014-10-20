/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;

import org.apache.portals.bridges.velocity.AbstractVelocityMessagingPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.portal.config.IngridSessionPreferences;
import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.forms.SearchSettingsForm;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;

/**
 * This portlet handles the "Settings" fragment of the search-settings page
 *
 * @author martin@wemove.com
 */
public class SearchSettingsPortlet extends AbstractVelocityMessagingPortlet {

    //    private final static Logger log = LoggerFactory.getLogger(SearchSettingsPortlet.class);

    /* (non-Javadoc)
     * @see javax.portlet.Portlet#init(javax.portlet.PortletConfig)
     */
    public void init(PortletConfig config) throws PortletException {
        // set our message "scope" for inter portlet messaging
        setTopic(Settings.MSG_TOPIC_SEARCH);

        super.init(config);
    }

    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {

        Context context = getContext(request);

        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()), request.getLocale());
        context.put("MESSAGES", messages);
        
        String partnerRestriction = PortalConfig.getInstance().getString(
                PortalConfig.PORTAL_SEARCH_RESTRICT_PARTNER);
        if (partnerRestriction != null && partnerRestriction.length() > 0) {
            context.put("onePartnerOnly", "true");
        }

        SearchSettingsForm f = (SearchSettingsForm) Utils.getActionForm(request, SearchSettingsForm.SESSION_KEY,
                SearchSettingsForm.class);

        String cmd = request.getParameter("cmd");
        if (cmd == null) {
            f.clear();
            IngridSessionPreferences sessionPrefs = Utils.getSessionPreferences(request,
                    IngridSessionPreferences.SESSION_KEY, IngridSessionPreferences.class);
            f.setInput(SearchSettingsForm.FIELD_RANKING, (String) sessionPrefs
                    .get(IngridSessionPreferences.SEARCH_SETTING_RANKING));
            f.setInput(SearchSettingsForm.FIELD_GROUPING, (String) sessionPrefs
                    .get(IngridSessionPreferences.SEARCH_SETTING_GROUPING));
            f.setInput(SearchSettingsForm.FIELD_INCL_META, (String) sessionPrefs
                    .get(IngridSessionPreferences.SEARCH_SETTING_INCL_META));
        }

        context.put("actionForm", f);

        super.doView(request, response);
    }

    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {

        String action = request.getParameter("action");
        actionResponse.setRenderParameter("cmd", action);

        if (action == null) {
            action = "";
        }
        SearchSettingsForm f = (SearchSettingsForm) Utils.getActionForm(request, SearchSettingsForm.SESSION_KEY,
                SearchSettingsForm.class);

        if (action.equalsIgnoreCase(Settings.PARAMV_ACTION_SUBMIT)) {

            f.clearErrors();

            f.populate(request);
            if (!f.validate()) {
                return;
            }

            f.setError("", "searchSettings.message.settings.stored");
        } else if (action.equalsIgnoreCase(Settings.PARAMV_ACTION_ORIGINAL_SETTINGS)) {
            f.init();
            f.setError("", "searchSettings.message.settings.resetted");
        }

        IngridSessionPreferences sessionPrefs = Utils.getSessionPreferences(request,
                IngridSessionPreferences.SESSION_KEY, IngridSessionPreferences.class);
        sessionPrefs.put(IngridSessionPreferences.SEARCH_SETTING_RANKING, f.getInput(SearchSettingsForm.FIELD_RANKING));
        sessionPrefs.put(IngridSessionPreferences.SEARCH_SETTING_GROUPING, f
                .getInput(SearchSettingsForm.FIELD_GROUPING));
        sessionPrefs.put(IngridSessionPreferences.SEARCH_SETTING_INCL_META, f
                .getInput(SearchSettingsForm.FIELD_INCL_META));
        
        // adapt SearchState to New Settings !
        sessionPrefs.adaptSearchState(request);
    }
}
