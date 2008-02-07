/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets.searchext;

import java.io.IOException;
import java.util.List;

import javax.portlet.ActionResponse;
import javax.portlet.PortletException;

import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.portal.config.IngridSessionPreferences;
import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.global.UtilsDB;
import de.ingrid.portal.global.UtilsString;

/**
 * This portlet is the abstract base class of all "wizard" portlets in the Law
 * area of the extended search. Encapsulates common stuff (Main Tab Navigation, ressources ...).
 *
 * @author martin@wemove.com
 */
abstract class SearchExtLaw extends GenericVelocityPortlet {

    // TAB values from action request (request parameter)

    /** tab param value if main tab topic is clicked */
    protected final static String PARAMV_TAB_TOPIC = "1";

    /** tab param value if main tab search area is clicked */
    protected final static String PARAMV_TAB_AREA = "4";

    // START PAGES FOR MAIN TABS

    /** page for main tab "topic" */
    protected final static String PAGE_TOPIC = "/ingrid-portal/portal/search-extended/search-ext-law-topic-terms.psml";

    /** page for main tab "search area" */
    protected final static String PAGE_AREA_PARTNER = "/ingrid-portal/portal/search-extended/search-ext-law-area-partner.psml";

    // VARIABLE NAMES FOR VELOCITY

    /** velocity variable name for main tab, has to be put to context, so correct tab is selected */
    protected final static String VAR_MAIN_TAB = "tab";

    /** velocity variable name for sub tab, has to be put to context, so correct sub tab is selected */
    protected final static String VAR_SUB_TAB = "subtab";

    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);
        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()));
        context.put("MESSAGES", messages);
        
        // enable/disable providers drop down
        if (PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_SEARCH_DISPLAY_PROVIDERS)) {
            String partner = PortalConfig.getInstance().getString(PortalConfig.PORTAL_SEARCH_RESTRICT_PARTNER);
            List providers;
            if (partner == null || partner.length() == 0) {
                providers = UtilsDB.getProviders();
            } else {
                providers = UtilsDB.getProvidersFromPartnerKey(partner);
            }
            context.put("displayProviders", Boolean.TRUE);
            context.put("providers", providers);
            context.put("UtilsString", new UtilsString());
            
            // get selected provider
            IngridSessionPreferences sessionPrefs = Utils.getSessionPreferences(request,
                    IngridSessionPreferences.SESSION_KEY, IngridSessionPreferences.class);
            String provider = request.getParameter(Settings.PARAM_PROVIDER);
            if (provider != null) {
                sessionPrefs.put(IngridSessionPreferences.RESTRICTING_PROVIDER, provider);
            }
            context.put("selectedProviderIdent", sessionPrefs.get(IngridSessionPreferences.RESTRICTING_PROVIDER));
        }

        super.doView(request, response);
    }

    protected void processTab(ActionResponse actionResponse, String tab) throws PortletException, IOException {
        if (tab.equals(PARAMV_TAB_TOPIC)) {
            actionResponse.sendRedirect(PAGE_TOPIC);
        } else if (tab.equals(PARAMV_TAB_AREA)) {
            actionResponse.sendRedirect(PAGE_AREA_PARTNER);
        }
    }
}