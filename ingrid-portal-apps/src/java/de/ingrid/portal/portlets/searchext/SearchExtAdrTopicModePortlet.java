/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2016 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.portal.portlets.searchext;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;

import org.apache.portals.messaging.PortletMessaging;
import org.apache.velocity.context.Context;

import de.ingrid.portal.forms.SearchExtAdrTopicModeForm;
import de.ingrid.portal.forms.SearchExtEnvAreaSourcesForm;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.global.UtilsQueryString;

/**
 * This portlet handles the fragment of the mode input in the extended search
 * for ADDRESSES.
 *
 * @author martin@wemove.com
 */
public class SearchExtAdrTopicModePortlet extends SearchExtAdrTopic {

    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);

        SearchExtAdrTopicModeForm f = (SearchExtAdrTopicModeForm) Utils.getActionForm(request, SearchExtAdrTopicModeForm.SESSION_KEY, SearchExtAdrTopicModeForm.class);        

        String cmd = request.getParameter("cmd");
        
        if (cmd == null) {
            f.init();
        }
        context.put("actionForm", f);
        
        // set positions in main and sub tab
        context.put(VAR_MAIN_TAB, PARAMV_TAB_TOPIC);
        context.put(VAR_SUB_TAB, PARAMV_TAB_MODE);

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
            SearchExtAdrTopicModeForm f = (SearchExtAdrTopicModeForm) Utils.getActionForm(request, SearchExtAdrTopicModeForm.SESSION_KEY, SearchExtAdrTopicModeForm.class);        
            f.clearErrors();
            
            f.populate(request);
            if (!f.validate()) {
                return;
            }
            
            String queryStr = (String) PortletMessaging.receive(request, Settings.MSG_TOPIC_SEARCH, Settings.PARAM_QUERY_STRING);
            String subTerm = "";
            if (f.getInput(SearchExtAdrTopicModeForm.FIELD_SEARCH_MODE).equals(SearchExtAdrTopicModeForm.VALUE_SEARCH_MODE_SUBSTRING)) {
                subTerm = "querymode:substring";
            } else {
                queryStr = UtilsQueryString.replaceTerm(queryStr, "querymode:substring", "");
                queryStr = UtilsQueryString.stripQueryWhitespace(queryStr);
            }
            if (f.getInput(SearchExtAdrTopicModeForm.FIELD_SEARCH_SCOPE).equals(SearchExtAdrTopicModeForm.VALUE_SEARCH_SCOPE_LIMITED)) {
                if (subTerm.length() > 0) {
                    subTerm = subTerm.concat(" ");
                }
                subTerm = subTerm.concat("attribrange:limited");
            } else {
                queryStr = UtilsQueryString.replaceTerm(queryStr, "attribrange:limited", "");
                queryStr = UtilsQueryString.stripQueryWhitespace(queryStr);
            }
            if (subTerm.length() > 0) {
                queryStr = UtilsQueryString.addTerm(queryStr, subTerm, UtilsQueryString.OP_AND);
            }
            PortletMessaging.publish(request, Settings.MSG_TOPIC_SEARCH, Settings.PARAM_QUERY_STRING, queryStr);
        } else if (action.equalsIgnoreCase(Settings.PARAMV_ACTION_CHANGE_TAB)) {
            String newTab = request.getParameter(Settings.PARAM_TAB);
            processTab(actionResponse, newTab);
        }
    }
}
