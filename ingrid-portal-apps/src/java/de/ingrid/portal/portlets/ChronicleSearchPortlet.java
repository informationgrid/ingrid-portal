/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
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
package de.ingrid.portal.portlets;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.portals.bridges.velocity.AbstractVelocityMessagingPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.iplug.sns.utils.Topic;
import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.forms.ChronicleSearchForm;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.global.UtilsDB;
import de.ingrid.portal.search.SearchState;
import de.ingrid.portal.search.UtilsSearch;
import de.ingrid.utils.query.FieldQuery;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.IDataTypes;
import de.ingrid.utils.queryparser.QueryStringParser;

public class ChronicleSearchPortlet extends AbstractVelocityMessagingPortlet {

    private final static Logger log = LoggerFactory.getLogger(ChronicleSearchPortlet.class);

    public void init(PortletConfig config) throws PortletException {
        // set our message "scope" for inter portlet messaging
        setTopic(Settings.MSG_TOPIC_CHRONICLE);

        super.init(config);
    }

    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);
        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()), request.getLocale());
        context.put("MESSAGES", messages);

        // ----------------------------------
        // check for passed URL PARAMETERS (for bookmarking)
        // ----------------------------------
        // action indicates what to do !
        String action = request.getParameter(Settings.PARAM_ACTION);
        if (action == null) {
            action = "";
        }

        // may be passed from start page !
        String topicTerm = request.getParameter("topTerm");

        // search if action parameter is passed, every action on page should cause new search 
        boolean doSearch = false;
        if (action.length() != 0 || request.getParameter(Settings.PARAM_STARTHIT_RANKED) != null) {
            // remove query message for result portlet -> no results
            cancelRenderMessage(request, Settings.MSG_QUERY);
            doSearch = true;
        }

        // ----------------------------------
        // set data for view template 
        // ----------------------------------

        // get event types
        List eventTypes = UtilsDB.getChronicleEventTypes();
        context.put("evtypesList", eventTypes);

        // update ActionForm !
        ChronicleSearchForm af = (ChronicleSearchForm) Utils.getActionForm(request, ChronicleSearchForm.SESSION_KEY,
                ChronicleSearchForm.class, PortletSession.APPLICATION_SCOPE);
        // if no initial rubric selection set, set it and initialize the form (first instantiation)
        if (ChronicleSearchForm.getINITIAL_EVENT_TYPES().length() == 0) {
            // compute initial selection string for all event types and initialize selection
            ChronicleSearchForm.setInitialSelectedEventTypes(eventTypes);
            af.init();
        }

        if (action.equals(Settings.PARAMV_ACTION_NEW_SEARCH)) {
            // empty form on new search
            af.clear();
        } else if (action.equals(Settings.PARAMV_ACTION_FROM_TEASER)) {
            // initial default values when called from teaser
            af.init();

            // set term
            if (topicTerm != null) {
                af.setInput(ChronicleSearchForm.FIELD_SEARCH, topicTerm);
            }
        } else if (request.getParameter(Settings.PARAM_STARTHIT_RANKED) == null) {
            // no URL parameters, we're called from other page -> default values
            af.init();
        }
        // replaces only the ones in request
        af.populate(request);
        context.put("actionForm", af);

        // validate via ActionForm
        if (!af.validate()) {
            super.doView(request, response);
            return;
        }

        // ----------------------------------
        // prepare Search, Search will be performed in Result portlet 
        // ----------------------------------
        if (doSearch) {
            String errKey = setupQuery(request);
            if (errKey != null) {
                af.setError("", errKey);
            }
        } else {
            // remove query message for result portlet -> no results
            cancelRenderMessage(request, Settings.MSG_QUERY);
        }

        super.doView(request, response);
    }

    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {
        // get our ActionForm for generating URL params from current form state
        // we have a new submit, so bring form up to date !
        ChronicleSearchForm af = (ChronicleSearchForm) Utils.getActionForm(request, ChronicleSearchForm.SESSION_KEY,
                ChronicleSearchForm.class, PortletSession.APPLICATION_SCOPE);
        af.clear();
        // populate doesn't clear
        af.populate(request);

        // redirect to our page with URL parameters for bookmarking
        actionResponse.sendRedirect(actionResponse.encodeURL(Settings.PAGE_CHRONICLE + SearchState.getURLParamsCatalogueSearch(request, null)));
    }

    /**
     * Create IngridQuery and publish it.
     * RETURNS an Error String, if an error occured, which can be displayed in form.
     * @param request
     * @return
     */
    public String setupQuery(PortletRequest request) {
        // remove old query message for result portlet
        cancelRenderMessage(request, Settings.MSG_QUERY);

        // our action form encapsulating form input
        ChronicleSearchForm af = (ChronicleSearchForm) Utils.getActionForm(request, ChronicleSearchForm.SESSION_KEY,
                ChronicleSearchForm.class, PortletSession.APPLICATION_SCOPE);

        IngridQuery query = null;
        try {
            // INPUT: Term
            String inputTerm = af.getInput(ChronicleSearchForm.FIELD_SEARCH).trim();

            // ONLY USE ONE TERM !!! SNS can't process multiple Terms !
            //            inputTerm = inputTerm.split(" ")[0];

            // Check whether topicId from start page (Anniversary Event) in request and prepare query accordingly !
            // HACK: we only want a most simple query to get one Hit !
            // searching Greenpeace seems to be fast ;)
            // Important: TERM MUST EXIST IN SNS FOR THIS LANGUAGE! 
            String topicId = request.getParameter(Settings.PARAM_TOPIC_ID);
            if (topicId != null) {
                inputTerm = "Greenpeace";
            }

            try {
                query = QueryStringParser.parse(inputTerm);
            } catch (Throwable t) {
                if (log.isWarnEnabled()) {
                    log.warn("Problems creating IngridQuery from input term: " + inputTerm, t);
                }
                return "chronicle.form.error.queryFormat";
            }

            // also pass Topic ID of Anniversary Event from Start Page, so Result Portlet will react accordingly
            if (topicId != null) {
                query.put("TEASER_TOPIC_ID", topicId);
            }

            // Language
            if(!PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_SEARCH_LANGUAGE_INDEPENDENT, false)){
                UtilsSearch.processLanguage(query, request.getLocale());
            }

            // SNS Query criteria
            query.addField(new FieldQuery(true, false, Settings.QFIELD_DATATYPE, IDataTypes.SNS));
            query.putInt(Topic.REQUEST_TYPE, Topic.EVENT_FROM_TOPIC);

            // INPUT: Event Type
            String[] eventTypes = af.getInputAsArray(ChronicleSearchForm.FIELD_EVENT);
            // don't set anything if "all" is selected
            if (eventTypes != null && Utils.getPosInArray(eventTypes, Settings.PARAMV_ALL) == -1) {
                String[] queryTypes = new String[eventTypes.length];
                for (int i = 0; i < eventTypes.length; i++) {
                    queryTypes[i] = UtilsDB.getChronicleEventTypeFromKey(eventTypes[i]);
                }
                query.put(Settings.QFIELD_EVENT_TYPE, queryTypes);
            }

            // INPUT: Date
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String dateSelect = af.getInput(ChronicleSearchForm.FIELD_TIME_SELECT);
            boolean dateSet = false;
            if (dateSelect.equals(ChronicleSearchForm.FIELDV_TIME_SELECT_PERIOD)) {
                Date fromDate = ChronicleSearchForm.getDate(af.getInput(ChronicleSearchForm.FIELD_TIME_FROM));
                if (fromDate != null) {
                    Date toDate = ChronicleSearchForm.getDate(af.getInput(ChronicleSearchForm.FIELD_TIME_TO));
                    if (toDate != null) {
                        String dateStr = df.format(fromDate);
                        query.put(Settings.QFIELD_DATE_FROM, dateStr);
                        dateStr = df.format(toDate);
                        query.put(Settings.QFIELD_DATE_TO, dateStr);
                        dateSet = true;
                    }
                }
            } else if (dateSelect.equals(ChronicleSearchForm.FIELDV_TIME_SELECT_DATE)) {
                Date atDate = ChronicleSearchForm.getDate(af.getInput(ChronicleSearchForm.FIELD_TIME_AT));
                if (atDate != null) {
                    String dateStr = df.format(atDate);
                    query.put(Settings.QFIELD_DATE_AT, dateStr);
                    dateSet = true;
                }
            }

            // fix for event type selection
            if (inputTerm.length() == 0 && !dateSet) {
                // set future date for type selection
                query.put(Settings.QFIELD_DATE_TO, "3000-01-01");
            }

            // RANKING ???
            //            query.put(IngridQuery.RANKED, IngridQuery.DATE_RANKED);

        } catch (Exception ex) {
            if (log.isErrorEnabled()) {
                log.error("Problems setting up Query !", ex);
            }
            return "chronicle.form.error.exception";
        }

        // set query message for result portlet
        publishRenderMessage(request, Settings.MSG_QUERY, query);

        return null;
    }
}
