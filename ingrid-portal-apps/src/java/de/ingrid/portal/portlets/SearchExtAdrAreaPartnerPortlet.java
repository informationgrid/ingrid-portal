/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;

import org.apache.portals.messaging.PortletMessaging;
import org.apache.velocity.context.Context;

import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;

/**
 * This portlet handles the fragment of "Partner" input in the extended search for addresses.
 *
 * @author martin@wemove.com
 */
public class SearchExtAdrAreaPartnerPortlet extends SearchExtAdrArea {

    private static final String MSG_PARTNER = "partner";

    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);

        // set positions in main and sub tab
        context.put(VAR_MAIN_TAB, PARAMV_TAB_AREA);
        context.put(VAR_SUB_TAB, PARAMV_TAB_PARTNER);

        // ----------------------------------
        // check for passed RENDER PARAMETERS and react
        // ----------------------------------
        String action = request.getParameter(Settings.PARAM_ACTION);
        if (action != null) {
            // fetch data and pass it to template, HERE DUMMY
            Object partner = PortletMessaging.receive(request, MSG_PARTNER);
            context.put("partner", partner);
        } else {
            // remove data from session
            PortletMessaging.cancel(request, MSG_PARTNER);
        }

        super.doView(request, response);
    }

    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {
        String action = request.getParameter(Settings.PARAM_ACTION);
        if (action == null) {
            action = "";
        }
        String submittedAddToQuery = request.getParameter("submitAddToQuery");

        // TODO: implement functionality
        if (submittedAddToQuery != null) {

            // Zur Suchanfrage hinzufuegen

            // redirect to same page with URL parameter indicating that action was called !
            String urlViewParam = "?" + Utils.toURLParam(Settings.MSGV_TRUE, Settings.PARAM_ACTION);
            actionResponse.sendRedirect(PAGE_PARTNER + urlViewParam);

        } else if (action.equalsIgnoreCase("doOpenPartner")) {

            // Partner anzeigen (publishen, hier dummy)
            PortletMessaging.publish(request, MSG_PARTNER, Settings.MSGV_TRUE);

            // redirect to same page with URL parameter indicating that action was called !
            String urlViewParam = "?" + Utils.toURLParam(Settings.MSGV_TRUE, Settings.PARAM_ACTION);
            actionResponse.sendRedirect(PAGE_PARTNER + urlViewParam);

        } else if (action.equalsIgnoreCase("doClosePartner")) {

            // Partner entfernen (hier dummy)
            PortletMessaging.cancel(request, MSG_PARTNER);

            // redirect to same page with URL parameter indicating that action was called !
            String urlViewParam = "?" + Utils.toURLParam(Settings.MSGV_TRUE, Settings.PARAM_ACTION);
            actionResponse.sendRedirect(PAGE_PARTNER + urlViewParam);

        } else if (action.equalsIgnoreCase(Settings.PARAMV_ACTION_CHANGE_TAB)) {
            String newTab = request.getParameter(Settings.PARAM_TAB);
            processTab(actionResponse, newTab);
        }
    }
}