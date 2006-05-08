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

import de.ingrid.portal.forms.SearchExtAdrPlaceReferenceForm;
import de.ingrid.portal.forms.SearchExtAdrPlaceReferenceForm;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.global.UtilsQueryString;

/**
 * This portlet handles the fragment of the place reference input in the extended search
 * for ADDRESSES.
 *
 * @author martin@wemove.com
 */
public class SearchExtAdrPlaceReferencePortlet extends SearchExtAdrPlace {

    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);

        SearchExtAdrPlaceReferenceForm f = (SearchExtAdrPlaceReferenceForm) Utils.getActionForm(request, SearchExtAdrPlaceReferenceForm.SESSION_KEY, SearchExtAdrPlaceReferenceForm.class);        

        String cmd = request.getParameter("cmd");
        
        if (cmd == null) {
            f.init();
        }
        context.put("actionForm", f);
        
        // set positions in main and sub tab
        context.put(VAR_MAIN_TAB, PARAMV_TAB_PLACE);
        context.put(VAR_SUB_TAB, PARAMV_TAB_REFERENCE);

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

            actionResponse.setRenderParameter("cmd", "form_sent");
            SearchExtAdrPlaceReferenceForm f = (SearchExtAdrPlaceReferenceForm) Utils.getActionForm(request, SearchExtAdrPlaceReferenceForm.SESSION_KEY, SearchExtAdrPlaceReferenceForm.class);        
            f.clearErrors();
            
            f.populate(request);
            if (!f.validate()) {
                return;
            }
            
            String queryStr = (String) PortletMessaging.receive(request, Settings.MSG_TOPIC_SEARCH, Settings.PARAM_QUERY_STRING);
            String subTerm = "";
            if (f.hasInput(SearchExtAdrPlaceReferenceForm.FIELD_STREET)) {
                subTerm = subTerm.concat("street:").concat(UtilsQueryString.getPhrasedTerm(f.getInput(SearchExtAdrPlaceReferenceForm.FIELD_STREET)));
            }
            if (f.hasInput(SearchExtAdrPlaceReferenceForm.FIELD_ZIP)) {
                if (subTerm.length() > 0) {
                    subTerm = subTerm.concat(" ");
                }
                subTerm = subTerm.concat("zip:").concat(UtilsQueryString.getPhrasedTerm(f.getInput(SearchExtAdrPlaceReferenceForm.FIELD_ZIP)));
            }
            if (f.hasInput(SearchExtAdrPlaceReferenceForm.FIELD_CITY)) {
                if (subTerm.length() > 0) {
                    subTerm = subTerm.concat(" ");
                }
                subTerm = subTerm.concat("city:").concat(UtilsQueryString.getPhrasedTerm(f.getInput(SearchExtAdrPlaceReferenceForm.FIELD_CITY)));
            }
            if (subTerm.length() > 0) {
                queryStr = UtilsQueryString.addTerm(queryStr, subTerm, UtilsQueryString.OP_AND);
                PortletMessaging.publish(request, Settings.MSG_TOPIC_SEARCH, Settings.PARAM_QUERY_STRING, queryStr);
            }

        } else if (action.equalsIgnoreCase(Settings.PARAMV_ACTION_CHANGE_TAB)) {
            String newTab = request.getParameter(Settings.PARAM_TAB);
            processTab(actionResponse, newTab);
        }
    }
}