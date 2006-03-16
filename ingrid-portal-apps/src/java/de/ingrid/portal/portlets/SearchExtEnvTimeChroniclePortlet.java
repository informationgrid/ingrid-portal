/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;

import org.apache.portals.bridges.velocity.AbstractVelocityMessagingPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;

/**
 * This portlet handles the fragment of the time references (events in chronicle) in the extended search
 *
 * @author martin@wemove.com
 */
public class SearchExtEnvTimeChroniclePortlet extends AbstractVelocityMessagingPortlet {

    // PAGES
    /** our current page, for redirecting with URL params */
    private final static String PAGE_CURRENT = "/ingrid-portal/portal/search-extended/search-ext-env-time-constraint.psml";

    // URL PARAMETER VALUES
    private final static String PARAMV_VIEW_RESULTS = "results";

    // MESSAGES (in portlet session, because we don't set message topic)
    private static final String MSG_RESULTS = "results";

    private static final String MSG_EVENT = "event";

    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);
        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()));
        context.put("MESSAGES", messages);

        // ----------------------------------
        // check for passed RENDER PARAMETERS and react
        // ----------------------------------
        String action = request.getParameter(Settings.PARAM_ACTION);
        if (action == null) {
            action = "";
        }
        if (action.equals(PARAMV_VIEW_RESULTS)) {
            // fetch data and pass it to template, HERE DUMMY
            Object results = receiveRenderMessage(request, MSG_RESULTS);
            context.put("results", results);
            Object event = receiveRenderMessage(request, MSG_EVENT);
            context.put("event", event);
        } else {
            // remove data from session
            cancelRenderMessage(request, MSG_RESULTS);
            cancelRenderMessage(request, MSG_EVENT);
        }

        super.doView(request, response);
    }

    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {
        String action = request.getParameter(Settings.PARAM_ACTION);
        if (action == null) {
            return;
        }

        // TODO: implement functionality
        if (action.equalsIgnoreCase(Settings.PARAMV_ACTION_NEW_SEARCH)) {

            // Ereignisse suchen und publishen, hier dummy
            publishRenderMessage(request, MSG_RESULTS, Settings.MSGV_TRUE);
            cancelRenderMessage(request, MSG_EVENT);

            // redirect to same page with URL parameter indicating that action was called !
            String urlViewParam = "?" + Utils.toURLParam(PARAMV_VIEW_RESULTS, Settings.PARAM_ACTION);
            actionResponse.sendRedirect(PAGE_CURRENT + urlViewParam);

        } else if (action.equalsIgnoreCase("doOpenEvent")) {

            // Ereignis anzeigen (publishen, hier dummy)
            publishRenderMessage(request, MSG_EVENT, Settings.MSGV_TRUE);

            // redirect to same page with URL parameter indicating that action was called !
            String urlViewParam = "?" + Utils.toURLParam(PARAMV_VIEW_RESULTS, Settings.PARAM_ACTION);
            actionResponse.sendRedirect(PAGE_CURRENT + urlViewParam);

        } else if (action.equalsIgnoreCase("doCloseEvent")) {

            // Ereignis entfernen (hier dummy)
            cancelRenderMessage(request, MSG_EVENT);

            // redirect to same page with URL parameter indicating that action was called !
            String urlViewParam = "?" + Utils.toURLParam(PARAMV_VIEW_RESULTS, Settings.PARAM_ACTION);
            actionResponse.sendRedirect(PAGE_CURRENT + urlViewParam);
        }

    }
}