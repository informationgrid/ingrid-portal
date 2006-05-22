/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.request.RequestContext;
import org.apache.portals.bridges.common.GenericServletPortlet;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.portal.config.PortalConfig;
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
public class SearchResultPortlet extends GenericVelocityPortlet {

    private final static Log log = LogFactory.getLog(SearchResultPortlet.class);

    /** view templates */
    private final static String TEMPLATE_NO_QUERY_SET = "/WEB-INF/templates/empty.vm";

    private final static String TEMPLATE_RESULT = "/WEB-INF/templates/search_result.vm";

    private final static String TEMPLATE_RESULT_ADDRESS = "/WEB-INF/templates/search_result_address.vm";

    //    private final static String TEMPLATE_NO_RESULT = "/WEB-INF/templates/search_no_result.vm";

    private static final String TEMPLATE_RESULT_JS = "/WEB-INF/templates/search_result_js.vm";

    private static final String TEMPLATE_RESULT_JS_RANKED = "/WEB-INF/templates/search_result_js_ranked.vm";

    private static final String TEMPLATE_RESULT_JS_UNRANKED = "/WEB-INF/templates/search_result_js_unranked.vm";

    private static final String TEMPLATE_RESULT_IPLUG = "/WEB-INF/templates/search_result_iplug.vm";

    /*
     * (non-Javadoc)
     * 
     * @see javax.portlet.Portlet#init(javax.portlet.PortletConfig)
     */
    public void init(PortletConfig config) throws PortletException {
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
        String queryType = (String) SearchState.getSearchStateObject(request, Settings.MSG_QUERY_EXECUTION_TYPE);
        if (queryType == null) {
            queryType = "";
        }

        // ----------------------------------
        // set initial view template
        // ----------------------------------

        // if no query set display "nothing"
        IngridQuery query = (IngridQuery) SearchState.getSearchStateObject(request, Settings.MSG_QUERY);
        if (query == null) {
            setDefaultViewPage(TEMPLATE_NO_QUERY_SET);
            super.doView(request, response);
            return;
        }

        // selected data source ("Umweltinfo", Adressen" or "Forschungsprojekte")
        String selectedDS = SearchState.getSearchStateObjectAsString(request, Settings.PARAM_DATASOURCE);
        if (selectedDS == null) {
            selectedDS = Settings.SEARCH_INITIAL_DATASOURCE;
        }
        // get the filter from search state
        String filter = SearchState.getSearchStateObjectAsString(request, Settings.PARAM_FILTER);

        if (selectedDS.equals(Settings.PARAMV_DATASOURCE_ADDRESS)) {
            setDefaultViewPage(TEMPLATE_RESULT_ADDRESS);
        } else {
            setDefaultViewPage(TEMPLATE_RESULT);
        }
        if (filter != null && filter.equals(Settings.RESULT_KEY_PLUG_ID)) {
            setDefaultViewPage(TEMPLATE_RESULT_IPLUG);
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
        } else if (hasJavaScript && queryType.equals(Settings.MSGV_NEW_QUERY)
                && !currentView.equals(TEMPLATE_RESULT_ADDRESS) && !currentView.equals(TEMPLATE_RESULT_IPLUG)) {
            // if javascript and new query, set template to javascript enabled iframe template
            // exit method!!
            request.setAttribute(GenericServletPortlet.PARAM_VIEW_PAGE, TEMPLATE_RESULT_JS);
            context.put("rankedResultUrl", response
                    .encodeURL(((RequestContext) request.getAttribute(RequestContext.REQUEST_PORTALENV)).getRequest()
                            .getContextPath()
                            + "/portal/search-result-js.psml"
                            + SearchState.getURLParamsMainSearch(request)
                            + "&js_ranked=true"));
            context.put("unrankedResultUrl", response.encodeURL(((RequestContext) request
                    .getAttribute(RequestContext.REQUEST_PORTALENV)).getRequest().getContextPath()
                    + "/portal/search-result-js.psml"
                    + SearchState.getURLParamsMainSearch(request)
                    + "&js_ranked=false"));
            super.doView(request, response);
            return;
        } else if (currentView.equals(TEMPLATE_RESULT_IPLUG)) {
            renderOneResultColumnRanked = false;
        } else if (currentView.equals(TEMPLATE_RESULT_ADDRESS)) {
            renderOneResultColumnUnranked = false;
        }

        // create threaded query controller
        ThreadedQueryController controller = new ThreadedQueryController();
        controller.setTimeout(PortalConfig.getInstance().getInt(PortalConfig.QUERY_TIMEOUT_THREADED, 120000));

        QueryDescriptor qd = null;

        // store query in session
        UtilsSearch.addQueryToHistory(request);

        // initialize grouping 
        String grouping = null;

        // RANKED
        IngridHits rankedHits = null;
        int numberOfRankedHits = 0;
        if (renderOneResultColumnRanked) {
            // check if query must be executed
            if (queryType.equals(Settings.MSGV_NO_QUERY) || queryType.equals(Settings.MSGV_UNRANKED_QUERY)) {
                rankedHits = (IngridHits) SearchState.getSearchStateObject(request, Settings.MSG_SEARCH_RESULT_RANKED);
                if (log.isDebugEnabled()) {
                    log.debug("Read RANKED hits from CACHE !!! rankedHits=" + rankedHits);
                }
            } else {
                // process query, create QueryDescriptor
                qd = QueryPreProcessor.createRankedQueryDescriptor(request);
                if (qd != null) {
                    controller.addQuery("ranked", qd);
                    SearchState.resetSearchStateObject(request, Settings.MSG_SEARCH_FINISHED_RANKED);

                    // check for grouping
                    // this must be done here because grouping will only be 
                    // put into the query created by the pre processor
                    grouping = (String) qd.getQuery().get(Settings.QFIELD_GROUPED);
                    if (grouping != null) {
                        if (grouping.equals(IngridQuery.GROUPED_BY_PARTNER)) {
                            context.put("grouping", "partner");
                        } else if (grouping.equals(IngridQuery.GROUPED_BY_ORGANISATION)) {
                            context.put("grouping", "provider");
                        }
                    }
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
                    unrankedHits = (IngridHits) SearchState.getSearchStateObject(request,
                            Settings.MSG_SEARCH_RESULT_UNRANKED);
                    if (log.isDebugEnabled()) {
                        log.debug("Read UNRANKED hits from CACHE !!!! unrankedHits=" + unrankedHits);
                    }
                } else {
                    // process query, create QueryDescriptor
                    qd = QueryPreProcessor.createUnrankedQueryDescriptor(request);
                    if (qd != null) {
                        controller.addQuery("unranked", qd);
                        SearchState.resetSearchStateObject(request, Settings.MSG_SEARCH_FINISHED_UNRANKED);
                    }
                }
            }
        }

        // preset currentselector page (needed only for grouping)
        int currentSelectorPage = 1;
        int currentSelectorPageUnranked = 1;

        // fire query, post process results
        if (controller.hasQueries()) {
            // fire queries
            HashMap results = controller.search();
            // post process ranked hits if exists
            if (results.containsKey("ranked")) {
                rankedHits = QueryResultPostProcessor.processRankedHits((IngridHits) results.get("ranked"), selectedDS);
                SearchState.adaptSearchState(request, Settings.MSG_SEARCH_RESULT_RANKED, rankedHits);
                SearchState.adaptSearchState(request, Settings.MSG_SEARCH_FINISHED_RANKED, Settings.MSGV_TRUE);
                // GROUPING ONLY !!!
                // store start hit for the next page (only when in grouping mode)
                if (grouping != null && !grouping.equals(IngridQuery.GROUPED_OFF)) {
                    // get the current page number, default to 1
                    try {
                        currentSelectorPage = (new Integer(request.getParameter(Settings.PARAM_CURRENT_SELECTOR_PAGE)))
                                .intValue();
                    } catch (Exception ex) {
                        currentSelectorPage = 1;
                    }
                    // get the grouping starthits history from session
                    // create and initialize if not exists
                    ArrayList groupedStartHits = null;
                    groupedStartHits = (ArrayList) SearchState.getSearchStateObject(request,
                            Settings.PARAM_GROUPING_STARTHITS);
                    if (groupedStartHits == null) {
                        groupedStartHits = new ArrayList();
                        groupedStartHits.add(new Integer(0));
                        SearchState.adaptSearchState(request, Settings.PARAM_GROUPING_STARTHITS, groupedStartHits);
                    } else {
                        // set start hit for next page (grouping)
                        groupedStartHits.add(currentSelectorPage, new Integer(rankedHits.getGoupedHitsLength()));
                    }
                }

            }
            // post process unranked hits if exists
            if (results.containsKey("unranked")) {
                unrankedHits = QueryResultPostProcessor.processUnrankedHits((IngridHits) results.get("unranked"),
                        selectedDS);
                SearchState.adaptSearchState(request, Settings.MSG_SEARCH_RESULT_UNRANKED, unrankedHits);
                SearchState.adaptSearchState(request, Settings.MSG_SEARCH_FINISHED_UNRANKED, Settings.MSGV_TRUE);
                // UNRANKED SEARCH IS ALWAYS GROUPED !!!
                // store start hit for the next page (only when in grouping mode)
                // get the current page number, default to 1
                try {
                    currentSelectorPageUnranked = (new Integer(request.getParameter(Settings.PARAM_CURRENT_SELECTOR_PAGE_UNRANKED)))
                            .intValue();
                } catch (Exception ex) {
                    currentSelectorPageUnranked = 1;
                }
                // get the grouping starthits history from session
                // create and initialize if not exists
                ArrayList groupedStartHits = null;
                groupedStartHits = (ArrayList) SearchState.getSearchStateObject(request,
                        Settings.PARAM_GROUPING_STARTHITS_UNRANKED);
                if (groupedStartHits == null) {
                    groupedStartHits = new ArrayList();
                    groupedStartHits.add(new Integer(0));
                    SearchState.adaptSearchState(request, Settings.PARAM_GROUPING_STARTHITS_UNRANKED, groupedStartHits);
                } else {
                    // set start hit for next page (grouping)
                    groupedStartHits.add(currentSelectorPageUnranked, new Integer(unrankedHits.getGoupedHitsLength()));
                }
            }
        }

        if (rankedHits != null) {
            numberOfRankedHits = (int) rankedHits.length();
        }
        // adapt settings of ranked page navigation
        HashMap rankedPageNavigation = UtilsSearch.getPageNavigation(rankedStartHit,
                Settings.SEARCH_RANKED_HITS_PER_PAGE, numberOfRankedHits, Settings.SEARCH_RANKED_NUM_PAGES_TO_SELECT);

        if (unrankedHits != null) {
            if (filter != null && filter.equals(Settings.RESULT_KEY_PLUG_ID)) {
                numberOfUnrankedHits = (int) unrankedHits.length();
            } else {
                numberOfUnrankedHits = unrankedHits.getInVolvedPlugs();
            }
        }
        // adapt settings of unranked page navigation
        HashMap unrankedPageNavigation = UtilsSearch.getPageNavigation(unrankedStartHit,
                Settings.SEARCH_UNRANKED_HITS_PER_PAGE, numberOfUnrankedHits,
                Settings.SEARCH_UNRANKED_NUM_PAGES_TO_SELECT);

        // set filter params into context for filter display
        if (filter != null && filter.length() > 0) {
            context.put("filteredBy", filter);
            if (filter.equals(Settings.RESULT_KEY_PARTNER)) {
                context.put("filterSubject", UtilsSearch.mapResultValue(Settings.RESULT_KEY_PARTNER, SearchState
                        .getSearchStateObjectAsString(request, Settings.PARAM_SUBJECT), null));
            } else if (filter.equals(Settings.RESULT_KEY_PROVIDER)) {
                context.put("filterSubject", UtilsSearch.mapResultValue(Settings.RESULT_KEY_PROVIDER, SearchState
                        .getSearchStateObjectAsString(request, Settings.PARAM_SUBJECT), null));
            } else if (filter.equals(Settings.RESULT_KEY_PLUG_ID)) {
                context.put("filterSubject", UtilsSearch.mapResultValue(Settings.RESULT_KEY_PLUG_ID, SearchState
                        .getSearchStateObjectAsString(request, Settings.PARAM_SUBJECT), null));
            }
        }

        Object rankedSearchFinished = SearchState.getSearchStateObject(request, Settings.MSG_SEARCH_FINISHED_RANKED);
        Object unrankedSearchFinished = SearchState
                .getSearchStateObject(request, Settings.MSG_SEARCH_FINISHED_UNRANKED);
        /*
         // DON'T SHOW separate Template when no results ! This one is never displayed when JS is active and search is performed
         // initially (cause then always two columns are rendered (via iframes)). Only afterwards, e.g. whene similar terms are
         // clicked, this template is displayed, causing changes of result content (when similar terms are displayed).
         // WE DON'T WANT TO CHANE RESULTS CONTENT, WHEN SIMILAR TERMS ARE CKLICKED (DO we ???)
         if (rankedSearchFinished != null && unrankedSearchFinished != null && numberOfRankedHits == 0
         && numberOfUnrankedHits == 0 && (renderOneResultColumnUnranked && renderOneResultColumnRanked)
         && filter.length() == 0) {
         // query string will be displayed when no results !
         String queryString = SearchState.getSearchStateObjectAsString(request, Settings.PARAM_QUERY_STRING);
         context.put("queryString", queryString);

         setDefaultViewPage(TEMPLATE_NO_RESULT);
         super.doView(request, response);
         return;
         }
         */
        // ----------------------------------
        // prepare view
        // ----------------------------------
        // adapt page navigation for grouping in left column 
        if (grouping != null && !grouping.equals(IngridQuery.GROUPED_OFF)) {
            rankedPageNavigation.put(Settings.PARAM_CURRENT_SELECTOR_PAGE, new Integer(currentSelectorPage));
            // check if we have more results to come
            if (numberOfRankedHits <= rankedHits.getGoupedHitsLength()) {
                rankedPageNavigation.put("selectorHasNextPage", new Boolean(false));
            }
        }
        // adapt page navigation for right column (always grouped)
        if (renderOneResultColumnUnranked) {
            unrankedPageNavigation.put(Settings.PARAM_CURRENT_SELECTOR_PAGE_UNRANKED, new Integer(currentSelectorPageUnranked));
            // check if we have more results to come
            if (unrankedHits != null && unrankedHits.getGoupedHitsLength() > 0 && numberOfUnrankedHits > 0) {
                if (numberOfUnrankedHits <= unrankedHits.getGoupedHitsLength()) {
                    unrankedPageNavigation.put("selectorHasNextPage", new Boolean(false));
                } else {
                    unrankedPageNavigation.put("selectorHasNextPage", new Boolean(true));
                }
                    
            }
        }

        context.put("rankedPageSelector", rankedPageNavigation);
        context.put("unrankedPageSelector", unrankedPageNavigation);
        context.put("rankedResultList", rankedHits);
        context.put("unrankedResultList", unrankedHits);
        context.put("rankedSearchFinished", rankedSearchFinished);
        context.put("unrankedSearchFinished", unrankedSearchFinished);

        super.doView(request, response);
    }

    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {
        // check whether page navigation was clicked and send according message (Search state)
        String rankedStarthit = request.getParameter(Settings.PARAM_STARTHIT_RANKED);
        if (rankedStarthit != null) {
            SearchState.adaptSearchState(request, Settings.MSG_QUERY_EXECUTION_TYPE, Settings.MSGV_RANKED_QUERY);
        }
        // currentSelectorPage is set, send according message (Search state)
        String currentSelectorPage = request.getParameter(Settings.PARAM_CURRENT_SELECTOR_PAGE);
        if (currentSelectorPage != null) {
            SearchState.adaptSearchState(request, Settings.MSG_QUERY_EXECUTION_TYPE, Settings.MSGV_RANKED_QUERY);
        }
        // currentSelectorPageUnranked is set, send according message (Search state)
        String currentSelectorPageUnranked = request.getParameter(Settings.PARAM_CURRENT_SELECTOR_PAGE_UNRANKED);
        if (currentSelectorPageUnranked != null) {
            SearchState.adaptSearchState(request, Settings.MSG_QUERY_EXECUTION_TYPE, Settings.MSGV_UNRANKED_QUERY);
        }

        String unrankedStarthit = request.getParameter(Settings.PARAM_STARTHIT_UNRANKED);
        if (unrankedStarthit != null) {
            SearchState.adaptSearchState(request, Settings.MSG_QUERY_EXECUTION_TYPE, Settings.MSGV_UNRANKED_QUERY);
        }

        // adapt SearchState for bookmarking !:
        // - set new result pages
        SearchState.adaptSearchState(request, Settings.PARAM_STARTHIT_RANKED, rankedStarthit);
        SearchState.adaptSearchState(request, Settings.PARAM_STARTHIT_UNRANKED, unrankedStarthit);

        // adapt filter params, set state only if we do have a subject
        // avoid reset the states while browsing the resultpages
        if (request.getParameter(Settings.PARAM_SUBJECT) != null) {
            SearchState.adaptSearchState(request, Settings.PARAM_SUBJECT, request.getParameter(Settings.PARAM_SUBJECT));
            SearchState.adaptSearchState(request, Settings.PARAM_FILTER, request.getParameter(Settings.PARAM_GROUPING));
        }

        // redirect to our page wih parameters for bookmarking
        actionResponse.sendRedirect(Settings.PAGE_SEARCH_RESULT + SearchState.getURLParamsMainSearch(request));
    }

}