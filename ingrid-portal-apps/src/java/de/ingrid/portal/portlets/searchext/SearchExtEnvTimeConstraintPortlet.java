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
import de.ingrid.portal.forms.SearchExtEnvTimeConstraintForm;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.global.UtilsDate;
import de.ingrid.portal.global.UtilsQueryString;

/**
 * This portlet handles the fragment of the time constraint input in the extended search
 *
 * @author martin@wemove.com
 */
public class SearchExtEnvTimeConstraintPortlet extends SearchExtEnvTime {

    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);

        SearchExtEnvTimeConstraintForm f = (SearchExtEnvTimeConstraintForm) Utils.getActionForm(request, SearchExtEnvTimeConstraintForm.SESSION_KEY, SearchExtEnvTimeConstraintForm.class);        

        String cmd = request.getParameter("cmd");
        
        if (cmd == null) {
            f.init();
        }
        context.put("actionForm", f);
        
        // set positions in main and sub tab
        context.put(VAR_MAIN_TAB, PARAMV_TAB_TIME);
        context.put(VAR_SUB_TAB, PARAMV_TAB_CONSTRAINTS);

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
            SearchExtEnvTimeConstraintForm f = (SearchExtEnvTimeConstraintForm) Utils.getActionForm(request, SearchExtEnvTimeConstraintForm.SESSION_KEY, SearchExtEnvTimeConstraintForm.class);        
            f.clearErrors();
            
            f.populate(request);
            if (!f.validate()) {
                return;
            }

            // Zur Suchanfrage hinzufuegen
            String subTerm = "";
            if (f.getInput(SearchExtEnvTimeConstraintForm.FIELD_RADIO_TIME_SELECT).equals(SearchExtEnvTimeConstraintForm.VALUE_FROM_TO)) {
                subTerm = subTerm
                    .concat("t1:").concat(UtilsDate.convertDateString(f.getInput(SearchExtEnvTimeConstraintForm.FIELD_FROM), "dd.MM.yyyy", "yyyy-MM-dd"))
                    .concat(" t2:").concat(UtilsDate.convertDateString(f.getInput(SearchExtEnvTimeConstraintForm.FIELD_TO), "dd.MM.yyyy", "yyyy-MM-dd"));
            } else if (f.getInput(SearchExtEnvTimeConstraintForm.FIELD_RADIO_TIME_SELECT).equals(SearchExtEnvTimeConstraintForm.VALUE_AT)) {
                subTerm = subTerm
                .concat("t0:").concat(UtilsDate.convertDateString(f.getInput(SearchExtEnvTimeConstraintForm.FIELD_AT), "dd.MM.yyyy", "yyyy-MM-dd"));
            }
            if (f.hasInput(SearchExtEnvTimeConstraintForm.FIELD_CHK1) && f.hasInput(SearchExtEnvTimeConstraintForm.FIELD_CHK2)) {
                subTerm = subTerm.concat(" (time:intersect OR time:include)");
            } else if (f.hasInput(SearchExtEnvTimeConstraintForm.FIELD_CHK1)) {
                subTerm = subTerm.concat(" time:intersect");
            } else if (f.hasInput(SearchExtEnvTimeConstraintForm.FIELD_CHK2)) {
                subTerm = subTerm.concat(" time:include");
            }
            
            String queryStr = (String) PortletMessaging.receive(request, Settings.MSG_TOPIC_SEARCH, Settings.PARAM_QUERY_STRING);
            PortletMessaging.publish(request, Settings.MSG_TOPIC_SEARCH, Settings.PARAM_QUERY_STRING, UtilsQueryString.addTerm(queryStr, subTerm, UtilsQueryString.OP_AND));
            f.init();

        } else if (action.equalsIgnoreCase(Settings.PARAMV_ACTION_CHANGE_TAB)) {
            String newTab = request.getParameter(Settings.PARAM_TAB);
            processTab(actionResponse, newTab);
        }
    }
}