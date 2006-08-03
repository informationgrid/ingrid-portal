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

import de.ingrid.portal.forms.SearchExtEnvPlaceMapForm;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.global.UtilsQueryString;
import de.ingrid.portal.interfaces.WMSInterface;
import de.ingrid.portal.interfaces.impl.WMSInterfaceImpl;
import de.ingrid.portal.interfaces.om.WMSSearchDescriptor;

/**
 * This portlet handles the fragment of the map input in the extended search.
 *
 * @author martin@wemove.com
 */
public class SearchExtEnvPlaceMapPortlet extends SearchExtEnvPlace {

    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);
        boolean hasJavaScript = Utils.isJavaScriptEnabled(request);

        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()));
        context.put("MESSAGES", messages);

        SearchExtEnvPlaceMapForm f = (SearchExtEnvPlaceMapForm) Utils.getActionForm(request,
                SearchExtEnvPlaceMapForm.SESSION_KEY, SearchExtEnvPlaceMapForm.class);
        String cmd = request.getParameter("cmd");

        if (cmd == null) {
            f.init();
        }
        context.put("actionForm", f);

        // set positions in main and sub tab
        context.put(VAR_MAIN_TAB, PARAMV_TAB_PLACE);
        context.put(VAR_SUB_TAB, PARAMV_TAB_MAP);

        // get and set URL to WMS Server
        WMSInterface service = WMSInterfaceImpl.getInstance();
        String wmsURL = service
                .getWMSSearchURL(request.getPortletSession().getId(), hasJavaScript, request.getLocale());
        context.put("wmsURL", wmsURL);

        super.doView(request, response);
    }

    /**
     * NOTICE: on actions in same page we redirect to ourself with url param determining the view
     * template. If no view template is passed per URL param, the start template is rendered !
     * @see javax.portlet.Portlet#processAction(javax.portlet.ActionRequest, javax.portlet.ActionResponse)
     */
    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {
        String action = request.getParameter(Settings.PARAM_ACTION);
        if (action == null) {
            action = "";
        }
        String submittedAddToQuery = request.getParameter("submitAddToQuery");

        if (submittedAddToQuery != null) {
            actionResponse.setRenderParameter("cmd", "form_sent");
            SearchExtEnvPlaceMapForm f = (SearchExtEnvPlaceMapForm) Utils.getActionForm(request,
                    SearchExtEnvPlaceMapForm.SESSION_KEY, SearchExtEnvPlaceMapForm.class);
            f.clearErrors();

            f.populate(request);
            if (!f.validate()) {
                return;
            }
            // Zur Suchanfrage hinzufuegen
            WMSSearchDescriptor wmsDescriptor = WMSInterfaceImpl.getInstance().getWMSSearchParameter(
                    request.getPortletSession().getId());
            if (wmsDescriptor == null) {
                f.setError("", "searchExtEnvPlaceMap.error.no_spacial_constraint");
                return;
            } else {
                String searchTerm = "";
                if (wmsDescriptor.getType() == WMSSearchDescriptor.WMS_SEARCH_BBOX) {
                    searchTerm = "x1:".concat(Double.toString(wmsDescriptor.getMinX()));
                    searchTerm = searchTerm.concat(" y1:").concat(Double.toString(wmsDescriptor.getMinY()));
                    searchTerm = searchTerm.concat(" x2:").concat(Double.toString(wmsDescriptor.getMaxX()));
                    searchTerm = searchTerm.concat(" y2:").concat(Double.toString(wmsDescriptor.getMaxY()));
                    String subTerm = "";
                    if (f.hasInput(SearchExtEnvPlaceMapForm.FIELD_CHK1)) {
                        subTerm = subTerm.concat("coord:inside");
                    }
                    if (f.hasInput(SearchExtEnvPlaceMapForm.FIELD_CHK2)) {
                        if (subTerm.length() > 0) {
                            subTerm = subTerm.concat(" OR ");
                        }
                        subTerm = subTerm.concat("coord:intersect ");
                    }
                    if (f.hasInput(SearchExtEnvPlaceMapForm.FIELD_CHK3)) {
                        if (subTerm.length() > 0) {
                            subTerm = subTerm.concat(" OR ");
                        }
                        subTerm = subTerm.concat("coord:include");
                    }
                    searchTerm = searchTerm.concat(" (").concat(subTerm).concat(")");
                } else if (wmsDescriptor.getType() == WMSSearchDescriptor.WMS_SEARCH_COMMUNITY_CODE) {
                    searchTerm = searchTerm.concat("areaid:").concat(wmsDescriptor.getCommunityCode());
                }

                String queryStr = (String) PortletMessaging.receive(request, Settings.MSG_TOPIC_SEARCH,
                        Settings.PARAM_QUERY_STRING);
                PortletMessaging.publish(request, Settings.MSG_TOPIC_SEARCH, Settings.PARAM_QUERY_STRING,
                        UtilsQueryString.addTerm(queryStr, searchTerm, UtilsQueryString.OP_AND));
            }
        } else if (action.equalsIgnoreCase("doSave")) {

            // Kartendienste speichern

        } else if (action.equalsIgnoreCase(Settings.PARAMV_ACTION_CHANGE_TAB)) {
            String newTab = request.getParameter(Settings.PARAM_TAB);
            processTab(actionResponse, newTab);
        }
    }
}