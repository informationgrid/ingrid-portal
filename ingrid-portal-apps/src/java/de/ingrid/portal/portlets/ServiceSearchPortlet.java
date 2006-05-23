package de.ingrid.portal.portlets;

import java.io.IOException;
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

import de.ingrid.portal.forms.ServiceSearchForm;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.global.UtilsDB;
import de.ingrid.portal.search.SearchState;
import de.ingrid.portal.search.UtilsSearch;
import de.ingrid.utils.query.ClauseQuery;
import de.ingrid.utils.query.FieldQuery;
import de.ingrid.utils.query.IngridQuery;

public class ServiceSearchPortlet extends AbstractVelocityMessagingPortlet {

    private final static Log log = LogFactory.getLog(ServiceSearchPortlet.class);

    public void init(PortletConfig config) throws PortletException {
        // set our message "scope" for inter portlet messaging
        setTopic(Settings.MSG_TOPIC_SERVICE);

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
        // indicates whether form parameters were passed -> then we're called from Service page !
        String grouping = request.getParameter(Settings.PARAM_GROUPING);

        // search if
        // - new search submitted
        // - called from teaser
        // - parameters are passed, so maybe we process bookmark !
        boolean doSearch = false;
        if (action.equals(Settings.PARAMV_ACTION_NEW_SEARCH) || action.equals(Settings.PARAMV_ACTION_FROM_TEASER)
                || grouping != null) {
            // remove query message for result portlet -> no results
            cancelRenderMessage(request, Settings.MSG_QUERY);
            doSearch = true;
        }

        // ----------------------------------
        // set data for view template 
        // ----------------------------------

        // get rubrics
        List rubrics = UtilsDB.getServiceRubrics();
        context.put("rubricList", rubrics);

        // get partners
        List partners = UtilsDB.getPartners();
        context.put("partnerList", partners);

        // update ActionForm !
        ServiceSearchForm af = (ServiceSearchForm) Utils.getActionForm(request, ServiceSearchForm.SESSION_KEY,
                ServiceSearchForm.class, PortletSession.APPLICATION_SCOPE);
        // if no initial rubric selection set, set it and initialize the form (first instantiation)
        if (ServiceSearchForm.getINITIAL_RUBRIC().length() == 0) {
            // compute initial selection string for all rubrics and initialize selection
            ServiceSearchForm.setInitialSelectedRubrics(rubrics);
            af.init();
        }

        if (action.equals(Settings.PARAMV_ACTION_NEW_SEARCH)) {
            // empty form on new search
            af.clear();
        } else if (action.equals(Settings.PARAMV_ACTION_FROM_TEASER)) {
            // default values when called from teaser
            af.init();
        } else if (grouping == null) {
            // no URL parameters, we're called from other page -> default values
            af.init();
        }
        // replaces only the ones in request
        af.populate(request);

        // check for "zeige alle Ergebnisse von" and set the form fields accordingly
        String[] subjects = request.getParameterValues(Settings.PARAM_SUBJECT);
        if (subjects != null && subjects.length > 0) {
            if (af.getInput(ServiceSearchForm.FIELD_GROUPING).equals(Settings.PARAMV_GROUPING_PARTNER)) {
                af.setInput(ServiceSearchForm.FIELD_PARTNER, subjects);
            } else if (af.getInput(ServiceSearchForm.FIELD_GROUPING).equals(Settings.PARAMV_GROUPING_PROVIDER)) {
                // NOTICE: "hidden field" in ActionForm encapsulates passed providers (no real field for this one)
                // will only be cleared, if new Action in Form is performed (see processAction() ...
                // in result portlet, the parameters will automatically be generated also for this one (e.g.
                // for page navigation)
                af.setInput(ServiceSearchForm.STORAGE_PROVIDER, subjects);
            }
            af.setInput(ServiceSearchForm.FIELD_GROUPING, "none");
        }

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
        ServiceSearchForm af = (ServiceSearchForm) Utils.getActionForm(request, ServiceSearchForm.SESSION_KEY,
                ServiceSearchForm.class, PortletSession.APPLICATION_SCOPE);
        af.clear();
        // populate doesn't clear
        af.populate(request);

        // redirect to our page with URL parameters for bookmarking
        actionResponse.sendRedirect(Settings.PAGE_SERVICE + SearchState.getURLParamsCatalogueSearch(request, null));
    }

    public void setupQuery(PortletRequest request) {
        // remove old query message for result portlet
        cancelRenderMessage(request, Settings.MSG_QUERY);

        ServiceSearchForm af = (ServiceSearchForm) Utils.getActionForm(request, ServiceSearchForm.SESSION_KEY,
                ServiceSearchForm.class, PortletSession.APPLICATION_SCOPE);

        IngridQuery query = null;
        try {
            query = new IngridQuery();
            query
                    .addField(new FieldQuery(true, false, Settings.QFIELD_DATATYPE,
                            Settings.QVALUE_DATATYPE_AREA_SERVICE));

            // RUBRIC
            String queryValue = null;
            ClauseQuery cq = null;
            String[] rubrics = af.getInputAsArray(ServiceSearchForm.FIELD_RUBRIC);
            // don't set anything if "all" is selected
            if (rubrics != null && Utils.getPosInArray(rubrics, Settings.PARAMV_ALL) == -1) {
                cq = new ClauseQuery(true, false);
                for (int i = 0; i < rubrics.length; i++) {
                    if (rubrics[i] != null) {
                        queryValue = UtilsDB.getServiceRubricFromKey(rubrics[i]);
                        cq.addField(new FieldQuery(false, false, Settings.QFIELD_RUBRIC, queryValue));
                    }
                }
                query.addClause(cq);
            }

            // PARTNER
            UtilsSearch.processPartner(query, af.getInputAsArray(ServiceSearchForm.FIELD_PARTNER));

            // PROVIDER restriction (from hidden Field in ActionForm !)
            UtilsSearch.processProvider(query, af.getInputAsArray(ServiceSearchForm.STORAGE_PROVIDER));

            // GROUPING
            UtilsSearch.processGrouping(query, af.getInput(ServiceSearchForm.FIELD_GROUPING));

            // RANKING
            query.put(IngridQuery.RANKED, IngridQuery.DATE_RANKED);

        } catch (Throwable t) {
            if (log.isErrorEnabled()) {
                log.error("Problems setting up Query !", t);
            }
        }
        // set query message for result portlet
        publishRenderMessage(request, Settings.MSG_QUERY, query);
    }
}