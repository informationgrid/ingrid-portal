/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets.searchext;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.velocity.context.Context;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.search.UtilsSearch;

/**
 * This portlet handles the fragment of "Partner" input in the extended search for "law".
 *
 * @author martin@wemove.com
 */
public class SearchExtLawAreaPartnerPortlet extends SearchExtLawArea {

    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        Context context = getContext(request);

        context.put("enableContents", PortalConfig.getInstance().getBoolean("portal.enable.search.ext.env.area.contents", Boolean.TRUE));
        context.put("enableSources", PortalConfig.getInstance().getBoolean("portal.enable.search.ext.env.area.sources", Boolean.TRUE));
        context.put("enablePartner", PortalConfig.getInstance().getBoolean("portal.enable.search.ext.env.area.partner", Boolean.TRUE));
        
        // set positions in main and sub tab
        context.put(VAR_MAIN_TAB, PARAMV_TAB_AREA);
        context.put(VAR_SUB_TAB, PARAMV_TAB_PARTNER);
        
        UtilsSearch.doViewForPartnerPortlet(request, context);

        super.doView(request, response);
    }

    public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {
        String action = request.getParameter(Settings.PARAM_ACTION);
        if (action == null) {
            action = "";
        }

        if (action.equalsIgnoreCase(Settings.PARAMV_ACTION_CHANGE_TAB)) {
            String newTab = request.getParameter(Settings.PARAM_TAB);
            processTab(response, newTab);

        } else {
            UtilsSearch.processActionForPartnerPortlet(request, response, PAGE_PARTNER);
        }
    }
}