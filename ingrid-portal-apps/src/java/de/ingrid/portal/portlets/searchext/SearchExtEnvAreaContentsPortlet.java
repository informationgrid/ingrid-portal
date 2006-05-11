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

import de.ingrid.portal.forms.SearchExtEnvAreaContentsForm;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.global.UtilsQueryString;

/**
 * This portlet handles the fragment of "Contents" input in the extended search
 *
 * @author martin@wemove.com
 */
public class SearchExtEnvAreaContentsPortlet extends SearchExtEnvArea {

    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);

        SearchExtEnvAreaContentsForm f = (SearchExtEnvAreaContentsForm) Utils.getActionForm(request, SearchExtEnvAreaContentsForm.SESSION_KEY, SearchExtEnvAreaContentsForm.class);        

        String cmd = request.getParameter("cmd");
        
        if (cmd == null) {
            f.init();
        }
        
        String preSelect = request.getParameter("select");
        if (preSelect != null && preSelect.length() > 0) {
            f.setInput(SearchExtEnvAreaContentsForm.FIELD_CONTENT_TYPE, preSelect);
        }
        
        context.put("actionForm", f);
        
        
        // set positions in main and sub tab
        context.put(VAR_MAIN_TAB, PARAMV_TAB_AREA);
        context.put(VAR_SUB_TAB, PARAMV_TAB_CONTENTS);

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
            SearchExtEnvAreaContentsForm f = (SearchExtEnvAreaContentsForm) Utils.getActionForm(request, SearchExtEnvAreaContentsForm.SESSION_KEY, SearchExtEnvAreaContentsForm.class);        
            f.clearErrors();
            
            f.populate(request);
            if (!f.validate()) {
                return;
            }

            // Zur Suchanfrage hinzufuegen
            String subTerm = "";
            if (f.getInput(SearchExtEnvAreaContentsForm.FIELD_CONTENT_TYPE).equals(SearchExtEnvAreaContentsForm.VALUE_CONTENT_TYPE_ALL)) {
                subTerm = subTerm.concat("datatype:default");
            } else if (f.getInput(SearchExtEnvAreaContentsForm.FIELD_CONTENT_TYPE).equals(SearchExtEnvAreaContentsForm.VALUE_CONTENT_TYPE_SERVICE)) {
                subTerm = subTerm.concat("datatype:service");
            } else if (f.getInput(SearchExtEnvAreaContentsForm.FIELD_CONTENT_TYPE).equals(SearchExtEnvAreaContentsForm.VALUE_CONTENT_TYPE_TOPICS)) {
                subTerm = subTerm.concat("datatype:topics");
            }

            String queryStr = (String) PortletMessaging.receive(request, Settings.MSG_TOPIC_SEARCH, Settings.PARAM_QUERY_STRING);
            queryStr = UtilsQueryString.replaceTerm(queryStr, "datatype:default", "");
            queryStr = UtilsQueryString.replaceTerm(queryStr, "datatype:topics", "");
            queryStr = UtilsQueryString.replaceTerm(queryStr, "datatype:service", "");
                
            PortletMessaging.publish(request, Settings.MSG_TOPIC_SEARCH, Settings.PARAM_QUERY_STRING, UtilsQueryString.addTerm(queryStr, subTerm, UtilsQueryString.OP_AND));

        } else if (action.equalsIgnoreCase(Settings.PARAMV_ACTION_CHANGE_TAB)) {
            String newTab = request.getParameter(Settings.PARAM_TAB);
            processTab(actionResponse, newTab);
        }
    }
}