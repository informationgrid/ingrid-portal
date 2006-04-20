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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.portals.bridges.velocity.AbstractVelocityMessagingPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.iplug.sns.utils.Topic;
import de.ingrid.portal.forms.ChronicleSearchForm;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.global.UtilsDB;
import de.ingrid.portal.global.UtilsDate;
import de.ingrid.portal.search.SearchState;
import de.ingrid.utils.query.FieldQuery;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.IDataTypes;
import de.ingrid.utils.queryparser.QueryStringParser;

public class ChronicleSearchPortlet extends AbstractVelocityMessagingPortlet {

    private final static Log log = LogFactory.getLog(ChronicleSearchPortlet.class);

    public void init(PortletConfig config) throws PortletException {
        // set our message "scope" for inter portlet messaging
        setTopic(Settings.MSG_TOPIC_CHRONICLE);

        super.init(config);
    }

    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);
        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()));
        context.put("MESSAGES", messages);

        // ----------------------------------
        // check for passed URL PARAMETERS (for bookmarking)
        // ----------------------------------
        // action indicates what to do !
        String action = request.getParameter(Settings.PARAM_ACTION);
        if (action == null) {
            action = "";
        }

        // search if action parameter is passed, every action on page should cause new search 
        boolean doSearch = false;
        String topicId = null;
        String topicType = null;
        String topicFrom = null;
        String topicTo = null;
        String topicTerm = null;
        if (action.length() != 0) {
            // remove query message for result portlet -> no results
            cancelRenderMessage(request, Settings.MSG_QUERY);
            doSearch = true;

            // fetch topic id from request, we may be called from teaser !
            topicId = request.getParameter(Settings.PARAM_TOPIC_ID);
            topicType = request.getParameter("topType");
            topicFrom = request.getParameter("topFrom");
            topicTo = request.getParameter("topTo");
            topicTerm = request.getParameter("topTerm");
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

            // set form values of topic, when special topic is requested 
            if (topicId != null) {

                // TODO: search topic by ID !?

                // set type
                if (topicType != null) {
                    topicType = UtilsDB.getFormValueFromQueryValue(UtilsDB.getChronicleEventTypes(), topicType);
                    af.setInput(ChronicleSearchForm.FIELD_EVENT, topicType);
                }

                // set date
                String formFrom = UtilsDate.getInputDateFrom(topicFrom, request.getLocale());
                String formTo = UtilsDate.getInputDateTo(topicTo, request.getLocale());
                if ((formFrom != null) && (formTo != null)) {
                    if (formFrom.equals(formTo)) {
                        af.setInput(ChronicleSearchForm.FIELD_TIME_SELECT, ChronicleSearchForm.FIELDV_TIME_SELECT_DATE);
                        af.setInput(ChronicleSearchForm.FIELD_TIME_AT, formFrom);
                    } else {
                        af.setInput(ChronicleSearchForm.FIELD_TIME_SELECT,
                                ChronicleSearchForm.FIELDV_TIME_SELECT_PERIOD);
                        af.setInput(ChronicleSearchForm.FIELD_TIME_FROM, formFrom);
                        af.setInput(ChronicleSearchForm.FIELD_TIME_TO, formTo);
                    }
                } else if (formFrom != null) {
                    af.setInput(ChronicleSearchForm.FIELD_TIME_SELECT, ChronicleSearchForm.FIELDV_TIME_SELECT_PERIOD);
                    af.setInput(ChronicleSearchForm.FIELD_TIME_FROM, formFrom);
                    //                    af.setInput(ChronicleSearchForm.FIELD_TIME_TO, UtilsDate.getInputDateMax());                    
                    af.setInput(ChronicleSearchForm.FIELD_TIME_TO, UtilsDate.getInputDateTo(topicFrom, request
                            .getLocale()));
                } else if (formTo != null) {
                    af.setInput(ChronicleSearchForm.FIELD_TIME_SELECT, ChronicleSearchForm.FIELDV_TIME_SELECT_PERIOD);
                    //                    af.setInput(ChronicleSearchForm.FIELD_TIME_FROM, UtilsDate.getInputDateMin());                    
                    af.setInput(ChronicleSearchForm.FIELD_TIME_FROM, UtilsDate.getInputDateFrom(topicTo, request
                            .getLocale()));
                    af.setInput(ChronicleSearchForm.FIELD_TIME_TO, formTo);
                }

                // set term
                if (topicTerm != null) {
                    af.setInput(ChronicleSearchForm.FIELD_SEARCH, topicTerm);
                }
            } else {
                // TODO: at the moment NO SEARCH when called from teaser and no details
                doSearch = false;
            }
        } else if (action.length() == 0) {
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
            setupQuery(request);
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
        actionResponse.sendRedirect(Settings.PAGE_CHRONICLE + SearchState.getURLParamsCatalogueSearch(request, null));
    }

    public void setupQuery(PortletRequest request) {
        // remove old query message for result portlet
        cancelRenderMessage(request, Settings.MSG_QUERY);

        // our action form encapsulating form input
        ChronicleSearchForm af = (ChronicleSearchForm) Utils.getActionForm(request, ChronicleSearchForm.SESSION_KEY,
                ChronicleSearchForm.class, PortletSession.APPLICATION_SCOPE);

        IngridQuery query = null;
        try {
            // INPUT: Term
            String inputTerm = af.getInput(ChronicleSearchForm.FIELD_SEARCH).trim();
            query = QueryStringParser.parse(inputTerm);

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
        }

        // set query message for result portlet
        publishRenderMessage(request, Settings.MSG_QUERY, query);
    }
}