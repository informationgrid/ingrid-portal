/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets;

import java.io.IOException;
import java.util.HashMap;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.request.RequestContext;
import org.apache.portals.bridges.common.GenericServletPortlet;
import org.apache.portals.bridges.velocity.AbstractVelocityMessagingPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.search.QueryPreProcessor;
import de.ingrid.portal.search.QueryResultPostProcessor;
import de.ingrid.portal.search.SearchState;
import de.ingrid.portal.search.UtilsSearch;
import de.ingrid.portal.search.net.QueryDescriptor;
import de.ingrid.portal.search.net.ThreadedQueryController;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.query.IngridQuery;

/**
 * This portlet handles the "Result Display" fragment of the result page
 * 
 * @author martin@wemove.com
 */
public class SearchResultPortlet extends AbstractVelocityMessagingPortlet {

    private final static Log log = LogFactory.getLog(SearchResultPortlet.class);

    /** view templates */
    private final static String TEMPLATE_NO_QUERY_SET = "/WEB-INF/templates/empty.vm";

    private final static String TEMPLATE_RESULT = "/WEB-INF/templates/search_result.vm";

    private final static String TEMPLATE_RESULT_ADDRESS = "/WEB-INF/templates/search_result_address.vm";

    private final static String TEMPLATE_NO_RESULT = "/WEB-INF/templates/search_no_result.vm";

    private static final String TEMPLATE_RESULT_JS = "/WEB-INF/templates/search_result_js.vm";

    private static final String TEMPLATE_RESULT_JS_RANKED = "/WEB-INF/templates/search_result_js_ranked.vm";

    private static final String TEMPLATE_RESULT_JS_UNRANKED = "/WEB-INF/templates/search_result_js_unranked.vm";

    /*
     * (non-Javadoc)
     * 
     * @see javax.portlet.Portlet#init(javax.portlet.PortletConfig)
     */
    public void init(PortletConfig config) throws PortletException {
        // set our message "scope" for inter portlet messaging
        setTopic(Settings.MSG_TOPIC_SEARCH);

        super.init(config);
    }

    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);
        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()));
        context.put("MESSAGES", messages);

        // ----------------------------------
        // check for passed RENDER PARAMETERS (for bookmarking) and
        // ADAPT OUR PERMANENT STATE VIA MESSAGES
        // ----------------------------------
        // starthit RANKED
        String reqParam = null;
        int rankedStartHit = 0;
        try {
            reqParam = request.getParameter(Settings.PARAM_STARTHIT_RANKED);
            if (SearchState.adaptSearchState(request, Settings.PARAM_STARTHIT_RANKED, reqParam)) {
                rankedStartHit = (new Integer(reqParam)).intValue();
            }
        } catch (Exception ex) {
            if (log.isErrorEnabled()) {
                log.error("Problems parsing RANKED starthit from render request, set RANKED starthit to 0", ex);
            }
        }

        // starthit UNRANKED
        int unrankedStartHit = 0;
        try {
            reqParam = request.getParameter(Settings.PARAM_STARTHIT_UNRANKED);
            if (SearchState.adaptSearchState(request, Settings.PARAM_STARTHIT_UNRANKED, reqParam)) {
                unrankedStartHit = (new Integer(reqParam)).intValue();
            }
        } catch (Exception ex) {
            if (log.isErrorEnabled()) {
                log.error("Problems parsing UNRANKED starthit from render request, set UNRANKED starthit to 0", ex);
            }
        }

        // indicates whether we do a query or we read results from cache
        String queryType = (String) this.receiveRenderMessage(request, Settings.MSG_QUERY_EXECUTION_TYPE);
        if (queryType == null) {
            queryType = "";
        }

        // ----------------------------------
        // set initial view template
        // ----------------------------------

        // if no query set display "nothing"
        IngridQuery query = (IngridQuery) receiveRenderMessage(request, Settings.MSG_QUERY);
        if (query == null) {
            setDefaultViewPage(TEMPLATE_NO_QUERY_SET);
            super.doView(request, response);
            return;
        }

        // selected data source ("Umweltinfo", Adressen" or "Forschungsprojekte")
        String selectedDS = (String) receiveRenderMessage(request, Settings.PARAM_DATASOURCE);
        if (selectedDS == null) {
            selectedDS = Settings.SEARCH_INITIAL_DATASOURCE;
        }
        if (selectedDS.equals(Settings.PARAMV_DATASOURCE_ENVINFO)) {
            setDefaultViewPage(TEMPLATE_RESULT);
        } else if (selectedDS.equals(Settings.PARAMV_DATASOURCE_ADDRESS)) {
            setDefaultViewPage(TEMPLATE_RESULT_ADDRESS);
        }

        String currentView = getDefaultViewPage();

        // ----------------------------------
        // business logic
        // ----------------------------------
        
        // check for Javascript
        boolean hasJavaScript = Utils.isJavaScriptEnabled(request);
        
        // find out if we have to render only one result column
        boolean renderOneResultColumnRanked = true;
        boolean renderOneResultColumnUnranked = true;
        reqParam = request.getParameter("js_ranked");
        // check for one column rendering
        if (reqParam != null) {
            // check if we have to render only the ranked column
            if (reqParam.equals("true")) {
                request.setAttribute(GenericServletPortlet.PARAM_VIEW_PAGE, TEMPLATE_RESULT_JS_RANKED);
                renderOneResultColumnUnranked = false;
            } else {
                request.setAttribute(GenericServletPortlet.PARAM_VIEW_PAGE, TEMPLATE_RESULT_JS_UNRANKED);
                renderOneResultColumnRanked = false;
            }
        // check for js enabled iframe rendering
        } else if (hasJavaScript && queryType.equals(Settings.MSGV_NEW_QUERY) && !currentView.equals(TEMPLATE_RESULT_ADDRESS)) {
            // if javascript and new query, set template to javascript enabled iframe template
            // exit method!!
            request.setAttribute(GenericServletPortlet.PARAM_VIEW_PAGE, TEMPLATE_RESULT_JS);
            context.put("rankedResultUrl", response.encodeURL(((RequestContext)request.getAttribute(RequestContext.REQUEST_PORTALENV)).getRequest().getContextPath() + "/portal/search-result-js.psml" + SearchState.getURLParamsMainSearch(request) + "&js_ranked=true"));
            context.put("unrankedResultUrl", response.encodeURL(((RequestContext)request.getAttribute(RequestContext.REQUEST_PORTALENV)).getRequest().getContextPath() + "/portal/search-result-js.psml" + SearchState.getURLParamsMainSearch(request) + "&js_ranked=false"));
            super.doView(request, response);
            return;
        }

        // create threaded query controller
        ThreadedQueryController controller = new ThreadedQueryController();
        controller.setTimeout(30000);

        QueryDescriptor qd = null;
        
        // RANKED
        IngridHits rankedHits = null;
        int numberOfRankedHits = 0;
        if (renderOneResultColumnRanked) {
            // check if query must be executed
            if (queryType.equals(Settings.MSGV_NO_QUERY) || queryType.equals(Settings.MSGV_UNRANKED_QUERY)) {
                rankedHits = (IngridHits) receiveRenderMessage(request, Settings.MSG_SEARCH_RESULT_RANKED);
            }
            if (rankedHits == null) {
                // process query, create QueryDescriptor
                qd = QueryPreProcessor.createRankedQueryDescriptor(query, selectedDS, rankedStartHit);
                if (qd != null) {
                    controller.addQuery("ranked", qd);
                }
            }
        }
        
        // UNRANKED
        IngridHits unrankedHits = null;
        int numberOfUnrankedHits = 0;
        if (renderOneResultColumnUnranked) {
            if (!currentView.equals(TEMPLATE_RESULT_ADDRESS)) {
                // check if query must be executed
                if (queryType.equals(Settings.MSGV_NO_QUERY) || queryType.equals(Settings.MSGV_RANKED_QUERY)) {
                    unrankedHits = (IngridHits) receiveRenderMessage(request, Settings.MSG_SEARCH_RESULT_UNRANKED);
                }
                if (unrankedHits == null) {
                    // process query, create QueryDescriptor
                    qd = QueryPreProcessor.createUnrankedQueryDescriptor(query, selectedDS, unrankedStartHit);
                    if (qd != null) {
                        controller.addQuery("unranked", qd);
                    }
                }
            }            
        }

        // fire query, post process results
        if (controller.hasQueries()) {
            // fire queries
            HashMap results = controller.search();
            // post process ranked hits if exists
            if (results.containsKey("ranked")) {
                rankedHits = QueryResultPostProcessor.processRankedHits((IngridHits) results.get("ranked"), selectedDS);
                this.publishRenderMessage(request, Settings.MSG_SEARCH_RESULT_RANKED, rankedHits);
            }
            // post process unranked hits if exists
            if (results.containsKey("unranked")) {
                unrankedHits = QueryResultPostProcessor.processUnrankedHits((IngridHits) results.get("unranked"), selectedDS);
                this.publishRenderMessage(request, Settings.MSG_SEARCH_RESULT_UNRANKED, unrankedHits);
            }
        }

        if (rankedHits != null) {
            numberOfRankedHits = (int) rankedHits.length();
        }
        // adapt settings of ranked page navigation
        HashMap rankedPageNavigation = UtilsSearch.getPageNavigation(rankedStartHit,
                Settings.SEARCH_RANKED_HITS_PER_PAGE, numberOfRankedHits, Settings.SEARCH_RANKED_NUM_PAGES_TO_SELECT);

        
        if (unrankedHits != null) {
            numberOfUnrankedHits = (int) unrankedHits.length();
        }
        // adapt settings of unranked page navigation
        HashMap unrankedPageNavigation = UtilsSearch.getPageNavigation(unrankedStartHit,
                Settings.SEARCH_UNRANKED_HITS_PER_PAGE, numberOfUnrankedHits,
                Settings.SEARCH_UNRANKED_NUM_PAGES_TO_SELECT);

        
        if (numberOfRankedHits == 0 && numberOfUnrankedHits == 0 && (renderOneResultColumnUnranked && renderOneResultColumnRanked)) {
            // query string will be displayed when no results !
            String queryString = (String) receiveRenderMessage(request, Settings.PARAM_QUERY_STRING);
            context.put("queryString", queryString);

            setDefaultViewPage(TEMPLATE_NO_RESULT);
            super.doView(request, response);
            return;
        }
        
        // ----------------------------------
        // prepare view
        // ----------------------------------

        context.put("rankedPageSelector", rankedPageNavigation);
        context.put("unrankedPageSelector", unrankedPageNavigation);
        context.put("rankedResultList", rankedHits);
        context.put("unrankedResultList", unrankedHits);
        super.doView(request, response);
        this.cancelRenderMessage(request, Settings.MSG_QUERY_EXECUTION_TYPE);
    }

    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {
        // check whether page navigation was clicked and send according message (Search state)
        String rankedStarthit = request.getParameter(Settings.PARAM_STARTHIT_RANKED);
        if (rankedStarthit != null) {
            publishRenderMessage(request, Settings.MSG_QUERY_EXECUTION_TYPE, Settings.MSGV_RANKED_QUERY);
        }
        String unrankedStarthit = request.getParameter(Settings.PARAM_STARTHIT_UNRANKED);
        if (unrankedStarthit != null) {
            publishRenderMessage(request, Settings.MSG_QUERY_EXECUTION_TYPE, Settings.MSGV_UNRANKED_QUERY);
        }

        // adapt SearchState for bookmarking !:
        // - set new result pages
        SearchState.adaptSearchState(request, Settings.PARAM_STARTHIT_RANKED, rankedStarthit);
        SearchState.adaptSearchState(request, Settings.PARAM_STARTHIT_UNRANKED, unrankedStarthit);

        // redirect to our page wih parameters for bookmarking
        actionResponse.sendRedirect(Settings.PAGE_SEARCH_RESULT + SearchState.getURLParamsMainSearch(request));
    }

}