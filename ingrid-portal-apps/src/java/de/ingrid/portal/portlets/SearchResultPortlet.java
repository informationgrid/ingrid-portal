/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
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

    private static final String TEMPLATE_RESULT_FILTERED_ONECOLUMN = "/WEB-INF/templates/search_result_iplug.vm";
    
    private HttpClient client;

    /*
     * (non-Javadoc)
     * 
     * @see javax.portlet.Portlet#init(javax.portlet.PortletConfig)
     */
    public void init(PortletConfig config) throws PortletException {
        super.init(config);
        client = new HttpClient();
        client.getHttpConnectionManager().getParams().setConnectionTimeout(1000);
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

        // WHEN NO GROUPING !!!
        String reqParam = null;
        int rankedStartHit = 0;
        try {
            reqParam = request.getParameter(Settings.PARAM_STARTHIT_RANKED);
            if (SearchState.adaptSearchState(request, Settings.PARAM_STARTHIT_RANKED, reqParam)) {
                rankedStartHit = (new Integer(reqParam)).intValue();
            }
        } catch (Exception ex) {
            if (log.isErrorEnabled()) {
                log.error("Problems parsing RANKED starthit from render request, set RANKED starthit to 0 ! reqParam="
                        + reqParam, ex);
            }
        }

        // WHEN GROUPING !!!
        // get the current page number, default to 1
        int currentSelectorPage = 1;
        try {
            reqParam = request.getParameter(Settings.PARAM_CURRENT_SELECTOR_PAGE);
            if (SearchState.adaptSearchState(request, Settings.PARAM_CURRENT_SELECTOR_PAGE, reqParam)) {
                currentSelectorPage = new Integer(reqParam).intValue();
            }
        } catch (Exception ex) {
            if (log.isErrorEnabled()) {
                log.error(
                        "Problems parsing currentSelectorPage from render request, set currentSelectorPage to 1 ! reqParam="
                                + reqParam, ex);
            }
        }

        // starthit UNRANKED

        // TODO Remove NO GROUPING for unranked search column -> ALWAYS GROUPED !
        // NO GROUPING !!!
        int unrankedStartHit = 0;
        try {
            reqParam = request.getParameter(Settings.PARAM_STARTHIT_UNRANKED);
            if (SearchState.adaptSearchState(request, Settings.PARAM_STARTHIT_UNRANKED, reqParam)) {
                unrankedStartHit = (new Integer(reqParam)).intValue();
            }
        } catch (Exception ex) {
            if (log.isErrorEnabled()) {
                log.error(
                        "Problems parsing UNRANKED starthit from render request, set UNRANKED starthit to 0 ! reqParam="
                                + reqParam, ex);
            }
        }

        // GROUPING ONLY !!!
        // UNRANKED SEARCH IS ALWAYS GROUPED !!!
        // get the current page number, default to 1
        int currentSelectorPageUnranked = 1;
        try {
            reqParam = request.getParameter(Settings.PARAM_CURRENT_SELECTOR_PAGE_UNRANKED);
            if (SearchState.adaptSearchState(request, Settings.PARAM_CURRENT_SELECTOR_PAGE_UNRANKED, reqParam)) {
                currentSelectorPageUnranked = new Integer(reqParam).intValue();
            }
        } catch (Exception ex) {
            if (log.isErrorEnabled()) {
                log.error(
                        "Problems parsing currentSelectorPageUnranked from render request, set currentSelectorPageUnranked to 1 ! reqParam="
                                + reqParam, ex);
            }
        }

        // indicates whether we do a query or we read results from cache
        String queryType = (String) SearchState.getSearchStateObject(request, Settings.MSG_QUERY_EXECUTION_TYPE);
        if (queryType == null) {
            queryType = "";
        }

        // get the filter from REQUEST, not search state ! -> so back button works !
        // ALSO ADAPT SEARCH STATE, so following steps (query pre processor) work !
        String filter = request.getParameter(Settings.PARAM_FILTER);
        if (filter != null && filter.length() == 0) {
        	filter = null;
        }
        SearchState.adaptSearchState(request, Settings.PARAM_FILTER, filter);
        String filterSubject = request.getParameter(Settings.PARAM_SUBJECT);
        SearchState.adaptSearchState(request, Settings.PARAM_SUBJECT, filterSubject);        

        // set filter params into context for filter display
        if (filter != null) {
            context.put("filteredBy", filter);
            if (filter.equals(Settings.PARAMV_GROUPING_PARTNER)) {
                context.put("filterSubject", UtilsSearch.mapResultValue(Settings.RESULT_KEY_PARTNER, filterSubject, null));
            } else if (filter.equals(Settings.PARAMV_GROUPING_PROVIDER)) {
                context.put("filterSubject", UtilsSearch.mapResultValue(Settings.RESULT_KEY_PROVIDER, filterSubject, null));
            } else if (filter.equals(Settings.PARAMV_GROUPING_PLUG_ID)) {
                context.put("filterSubject", UtilsSearch.mapResultValue(Settings.RESULT_KEY_PLUG_ID, filterSubject, null));
            } else if (filter.equals(Settings.PARAMV_GROUPING_DOMAIN)) {
            	String[] keyValuePair = UtilsSearch.getDomainKeyValuePair(filterSubject);
            	String domainKey = keyValuePair[0];
            	String domainValue = keyValuePair[1];
            	// domain can be plugid or site or ...
            	// we extract the according value from our subject
            	if (domainKey.equals(Settings.QFIELD_PLUG_ID)) {
            		domainValue = UtilsSearch.mapResultValue(Settings.RESULT_KEY_PLUG_ID, domainValue, null);
            	}
            	context.put("filterSubject", domainValue);
            }
        }

        // datasource from state (set in SimpleSearch Portlet)
        String selectedDS = SearchState.getSearchStateObjectAsString(request, Settings.PARAM_DATASOURCE);

        // IngridQuery from state  (set in SimpleSearch Portlet)
        IngridQuery query = (IngridQuery) SearchState.getSearchStateObject(request, Settings.MSG_QUERY);

        // ----------------------------------
        // set initial view template
        // ----------------------------------

        // if no query set display "nothing"
        if (query == null) {
            setDefaultViewPage(TEMPLATE_NO_QUERY_SET);
            super.doView(request, response);
            return;
        }

        // selected data source ("Umweltinfo", Adressen" or "Forschungsprojekte")
        if (selectedDS == null) {
            selectedDS = Settings.SEARCH_INITIAL_DATASOURCE;
        }
        if (selectedDS.equals(Settings.PARAMV_DATASOURCE_ADDRESS)) {
            setDefaultViewPage(TEMPLATE_RESULT_ADDRESS);
        } else {
            setDefaultViewPage(TEMPLATE_RESULT);

            // default: right column IS grouped (by plugid)
        	// set in context e.g. to show grouped navigation
        	context.put("grouping_right", new Boolean(true));
            if (filter != null) {
            	// set one column result template if "Zeige alle ..." of plug or domain
            	if (filter.equals(Settings.PARAMV_GROUPING_PLUG_ID) ||
            		filter.equals(Settings.PARAMV_GROUPING_DOMAIN)) {
                    setDefaultViewPage(TEMPLATE_RESULT_FILTERED_ONECOLUMN);
                	// only one column to render we switch off grouping_right to show ungrouped navigation !
                    context.put("grouping_right", new Boolean(false));
            	}
            }
        }


        String currentView = getDefaultViewPage();

        // ----------------------------------
        // business logic
        // ----------------------------------

        // check for Javascript
        boolean hasJavaScript = Utils.isJavaScriptEnabled(request);

        // find out if we have to render only one result column
        boolean renderResultColumnRanked = true;
        boolean renderResultColumnUnranked = true;
        reqParam = request.getParameter("js_ranked");
        // check for one column rendering
        if (reqParam != null) {
            // check if we have to render only the ranked column
            if (reqParam.equals("true")) {
                request.setAttribute(GenericServletPortlet.PARAM_VIEW_PAGE, TEMPLATE_RESULT_JS_RANKED);
                renderResultColumnUnranked = false;
            } else {
                request.setAttribute(GenericServletPortlet.PARAM_VIEW_PAGE, TEMPLATE_RESULT_JS_UNRANKED);
                renderResultColumnRanked = false;
            }
            // check for js enabled iframe rendering

        } else if (currentView.equals(TEMPLATE_RESULT_FILTERED_ONECOLUMN)) {
        	if (filter.equals(Settings.PARAMV_GROUPING_PLUG_ID)) {
                renderResultColumnRanked = false;
                context.put("IS_RANKED", new Boolean(false));
        	} else {
        		// grouping by domain
                renderResultColumnUnranked = false;
                context.put("IS_RANKED", new Boolean(true));
        	}

        } else if (currentView.equals(TEMPLATE_RESULT_ADDRESS)) {
            renderResultColumnUnranked = false;

            // check for js enabled iframe rendering
        } else if (hasJavaScript && queryType.equals(Settings.MSGV_NEW_QUERY)) {
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
        }

        // create threaded query controller
        ThreadedQueryController controller = new ThreadedQueryController();
        controller.setTimeout(PortalConfig.getInstance().getInt(PortalConfig.QUERY_TIMEOUT_THREADED, 120000));

        QueryDescriptor qd = null;

        // store query in session
        UtilsSearch.addQueryToHistory(request);

        // RANKED
        IngridHits rankedHits = null;
        if (renderResultColumnRanked) {
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
                }
            }
        }

        // UNRANKED
        IngridHits unrankedHits = null;
        if (renderResultColumnUnranked) {
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

        // get grouping AFTER PREPROCESSING QUERY ! 
        String grouping = (String) SearchState.getSearchStateObject(request, Settings.PARAM_GROUPING);

        // fire query, post process results
        boolean rankedColumnHasMoreGroupedPages = true;
        if (controller.hasQueries()) {
            // fire queries
            HashMap results = controller.search();
            
            // check for zero results
            // log the result to the resource logger
            Iterator it = results.keySet().iterator();
            boolean noResults = true;
            String queryTypes = "";
            while (it.hasNext()) {
                Object key = it.next();
                if (queryTypes.length() > 0) {
                    queryTypes = queryTypes.concat(",");
                } else {
                    queryTypes = queryTypes.concat(key.toString());
                }
                IngridHits hits = (IngridHits)results.get(key);
                if (hits != null && hits.length() > 0) {
                    noResults = false;
                    break;
                }
            }
            if (noResults) {
                
                String url = PortalConfig.getInstance().getString(PortalConfig.PORTAL_LOGGER_RESOURCE);
                if (url != null) {
                    String queryString = SearchState.getSearchStateObjectAsString(request, Settings.PARAM_QUERY_STRING);                
                    HttpMethod method = null;
	                try{
	                    url = url.concat("?code=NO_RESULTS_FOR_QUERY&q=").concat(URLEncoder.encode(queryString, "UTF-8")).concat("&qtypes=").concat(URLEncoder.encode(queryTypes, "UTF-8"));
	                    method = new GetMethod(url);
	                    method.setFollowRedirects(true);
	                    client.executeMethod(method);
	                } catch (Throwable t) {
	                    if (log.isErrorEnabled()) {
	                        log.error("Cannot make connection to logger resource: ".concat(url), t);
	                    }
	                } finally {
	                    if (method != null) {
	                        try{
	                            method.releaseConnection();
	                        } catch (Throwable t) {
	                            if (log.isErrorEnabled()) {
	                                log.error("Cannot close connection to logger resource: ".concat(url), t);
	                            }
	                        }
	                    }
	                }
                }
            }
            
            
            // post process ranked hits if exists
            if (results.containsKey("ranked")) {
                rankedHits = QueryResultPostProcessor.processRankedHits((IngridHits) results.get("ranked"), selectedDS);
                SearchState.adaptSearchState(request, Settings.MSG_SEARCH_RESULT_RANKED, rankedHits);
                SearchState.adaptSearchState(request, Settings.MSG_SEARCH_FINISHED_RANKED, Settings.MSGV_TRUE);

                // GROUPING ONLY !!!
                if (grouping != null && !grouping.equals(IngridQuery.GROUPED_OFF)) {
                    // get the grouping starthits history from session
                    // create and initialize if not exists
                	// NOTICE: when grouping by domain the navigation is like ungrouped navigation, so multiple pages are rendered !
                    try {
                        ArrayList groupedStartHits = 
                        	(ArrayList) SearchState.getSearchStateObject(request, Settings.PARAM_GROUPING_STARTHITS);
                        if (groupedStartHits == null) {
                            groupedStartHits = new ArrayList();
                            SearchState.adaptSearchState(request, Settings.PARAM_GROUPING_STARTHITS, groupedStartHits);
                        }
                        // set starthit of NEXT page ! ensure correct size of Array ! Notice: currentSelectorPage is 1 for first page !
                    	while (currentSelectorPage >= groupedStartHits.size()) {
                    		groupedStartHits.add(new Integer(0));
                    	}
                        // set start hit for next page (grouping)
                    	int nextStartHit = rankedHits.getGoupedHitsLength();
                        groupedStartHits.set(currentSelectorPage, new Integer(nextStartHit));

                        // check whether there are more pages for grouped hits ! this is done due to former Bug in Backend !
                        // still necessary ? well, these former checks don't damage anything ...
                        if (rankedHits.length() <= rankedHits.getGoupedHitsLength()) {
                            // total number of hits (ungrouped) already processed -> no more pages
                        	rankedColumnHasMoreGroupedPages = false;                        	
                        } else {
                        	int currentStartHit = ((Integer) groupedStartHits.get(currentSelectorPage - 1)).intValue();
                        	if (nextStartHit == currentStartHit) {
                                // start hit for next page same as start hit for current page -> no more pages
                            	rankedColumnHasMoreGroupedPages = false;
                        	}
                        }
                    } catch (Exception ex) {
                        if (log.isInfoEnabled()) {
                            log.info("Problems processing grouping starthits RANKED", ex);
                        }
                    }
                }

            }
            // post process unranked hits if exists
            if (results.containsKey("unranked")) {
                unrankedHits = QueryResultPostProcessor.processUnrankedHits((IngridHits) results.get("unranked"),
                        selectedDS);
                SearchState.adaptSearchState(request, Settings.MSG_SEARCH_RESULT_UNRANKED, unrankedHits);
                SearchState.adaptSearchState(request, Settings.MSG_SEARCH_FINISHED_UNRANKED, Settings.MSGV_TRUE);
                // get the grouping starthits history from session
                // create and initialize if not exists
                try {
                    ArrayList groupedStartHits = 
                    	(ArrayList) SearchState.getSearchStateObject(request, Settings.PARAM_GROUPING_STARTHITS_UNRANKED);
                    if (groupedStartHits == null || unrankedHits == null) {
                        groupedStartHits = new ArrayList();
                        groupedStartHits.add(new Integer(0));
                        SearchState.adaptSearchState(request, Settings.PARAM_GROUPING_STARTHITS_UNRANKED,
                                groupedStartHits);
                    } else {
                        // set start hit for next page (grouping)
                    	int nextStartHit = unrankedHits.getGoupedHitsLength();
                        groupedStartHits.add(currentSelectorPageUnranked, new Integer(nextStartHit));
                    }
                } catch (Exception ex) {
                    if (log.isDebugEnabled()) {
                        log.debug("Problems processing grouping starthits UNRANKED", ex);
                    }
                    if (log.isInfoEnabled()) {
                        log.info("Problems processing grouping starthits UNRANKED. switch to debug level to get the exception logged.");
                    }
                }
            }
        }

        int numberOfRankedHits = 0;
        if (rankedHits != null) {
            numberOfRankedHits = (int) rankedHits.length();
        }
        // adapt settings of ranked page navigation
        HashMap rankedPageNavigation = UtilsSearch.getPageNavigation(rankedStartHit,
                Settings.SEARCH_RANKED_HITS_PER_PAGE, numberOfRankedHits,
                Settings.SEARCH_RANKED_NUM_PAGES_TO_SELECT);

        int numberOfUnrankedHits = 0;
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

        Object rankedSearchFinished = SearchState.getSearchStateObject(request, Settings.MSG_SEARCH_FINISHED_RANKED);
        Object unrankedSearchFinished = SearchState.getSearchStateObject(request, Settings.MSG_SEARCH_FINISHED_UNRANKED);
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

        // GROUPING
        // adapt page navigation for grouping in left column 
        if (renderResultColumnRanked) {
            if (grouping != null && !grouping.equals(IngridQuery.GROUPED_OFF)) {
            	UtilsSearch.adaptRankedPageNavigationToGrouping(
               		rankedPageNavigation, currentSelectorPage, rankedColumnHasMoreGroupedPages, numberOfRankedHits, request);

                if (grouping.equals(IngridQuery.GROUPED_BY_PARTNER)) {
                    context.put("grouping", "partner");
                } else if (grouping.equals(IngridQuery.GROUPED_BY_ORGANISATION)) {
                    context.put("grouping", "provider");
                } else if (grouping.equals(IngridQuery.GROUPED_BY_DATASOURCE)) {
                    context.put("grouping", "domain");
                }
            }
        }
        // adapt page navigation for right column (always grouped)
        if (renderResultColumnUnranked) {
            unrankedPageNavigation.put(Settings.PARAM_CURRENT_SELECTOR_PAGE_UNRANKED, new Integer(
                    currentSelectorPageUnranked));
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

        // NO GROUPING
        String rankedStarthit = request.getParameter(Settings.PARAM_STARTHIT_RANKED);
        if (rankedStarthit != null) {
            SearchState.adaptSearchState(request, Settings.MSG_QUERY_EXECUTION_TYPE, Settings.MSGV_RANKED_QUERY);
            SearchState.adaptSearchState(request, Settings.PARAM_STARTHIT_RANKED, rankedStarthit);
        }
        String unrankedStarthit = request.getParameter(Settings.PARAM_STARTHIT_UNRANKED);
        if (unrankedStarthit != null) {
            SearchState.adaptSearchState(request, Settings.MSG_QUERY_EXECUTION_TYPE, Settings.MSGV_UNRANKED_QUERY);
            SearchState.adaptSearchState(request, Settings.PARAM_STARTHIT_UNRANKED, unrankedStarthit);
        }

        // GROUPING
        // currentSelectorPage is set, send according message (Search state)
        String currentSelectorPage = request.getParameter(Settings.PARAM_CURRENT_SELECTOR_PAGE);
        if (currentSelectorPage != null) {
            SearchState.adaptSearchState(request, Settings.MSG_QUERY_EXECUTION_TYPE, Settings.MSGV_RANKED_QUERY);
            SearchState.adaptSearchState(request, Settings.PARAM_CURRENT_SELECTOR_PAGE, currentSelectorPage);
        }
        // currentSelectorPageUnranked is set, send according message (Search state)
        String currentSelectorPageUnranked = request.getParameter(Settings.PARAM_CURRENT_SELECTOR_PAGE_UNRANKED);
        if (currentSelectorPageUnranked != null) {
            SearchState.adaptSearchState(request, Settings.MSG_QUERY_EXECUTION_TYPE, Settings.MSGV_UNRANKED_QUERY);
            SearchState.adaptSearchState(request, Settings.PARAM_CURRENT_SELECTOR_PAGE_UNRANKED,
                    currentSelectorPageUnranked);
        }

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