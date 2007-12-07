/*
 * Copyright (c) 2007 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets.searchcatalog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ResourceBundle;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.container.url.PortalURL;
import org.apache.jetspeed.request.RequestContext;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.search.PageState;
import de.ingrid.portal.search.QueryPreProcessor;
import de.ingrid.portal.search.QueryResultPostProcessor;
import de.ingrid.portal.search.SearchState;
import de.ingrid.portal.search.UtilsSearch;
import de.ingrid.portal.search.net.QueryDescriptor;
import de.ingrid.portal.search.net.ThreadedQueryController;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.QueryStringParser;


public class SearchCatalogThesaurusResultPortlet extends GenericVelocityPortlet {

    private final static Log log = LogFactory.getLog(SearchCatalogThesaurusResultPortlet.class);

    // Location of the custom Title (resource bundle key)
	private final static String PORTLET_TITLE = "searchCatThesaurus.result.title";

	// TAB values from action request (request parameter)
    /** tab param value if tab enviromental is clicked */
    private final static String PARAMV_TAB_ENV = "1";

    /** tab param value if tab addresses is clicked */
    private final static String PARAMV_TAB_ADR = "2";

    /* Defines which search state topic to use */
    private final static String SEARCH_TOPIC = Settings.MSG_TOPIC_SEARCH;

    
    private String CURRENT_TAB = PARAMV_TAB_ENV;
    private String CURRENT_QUERY_TERM = null;
    private boolean tabChanged = false;
    
    // VARIABLE NAMES FOR VELOCITY
    /** velocity variable name for main tab, has to be put to context, so correct tab is selected */
    protected final static String VAR_MAIN_TAB = "tab";

    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);
        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()));
        context.put("MESSAGES", messages);

        context.put(VAR_MAIN_TAB, CURRENT_TAB);

        PortletSession session = request.getPortletSession();
        PageState ps = (PageState) session.getAttribute("portlet_state");
        if (ps == null) {
            ps = new PageState(this.getClass().getName());
            session.setAttribute("portlet_state", ps);
        }
        context.put("ps", ps);

        // -------------- BEGIN NAVIGATION LOGIC --------------
        String reqParam = null;
        int rankedStartHit = 0;
        try {
            reqParam = request.getParameter(Settings.PARAM_STARTHIT_RANKED);
            if (SearchState.adaptSearchState(request, Settings.PARAM_STARTHIT_RANKED, reqParam, SEARCH_TOPIC)) {
                rankedStartHit = (new Integer(reqParam)).intValue();
            }
        } catch (Exception ex) {
            if (log.isErrorEnabled()) {
                log.error("Problems parsing starthit from Catalog/Thesaurus render request, set starthit to 0 ! reqParam="
                        + reqParam, ex);
            }
        }

        // get the current page number, default to 1
        int currentSelectorPage = 1;
        try {
            reqParam = request.getParameter(Settings.PARAM_CURRENT_SELECTOR_PAGE);
            if (SearchState.adaptSearchState(request, Settings.PARAM_CURRENT_SELECTOR_PAGE, reqParam, SEARCH_TOPIC)) {
                currentSelectorPage = new Integer(reqParam).intValue();
            }
        } catch (Exception ex) {
            if (log.isErrorEnabled()) {
                log.error(
                        "Problems parsing currentSelectorPage from render request, set currentSelectorPage to 1 ! reqParam="
                                + reqParam, ex);
            }
        }
        // -------------- END NAVIGATION LOGIC --------------

        reqParam = request.getParameter("action"); 
        if (reqParam == null)
        	reqParam = "";
        
        if (reqParam.equalsIgnoreCase(Settings.PARAMV_ACTION_NEW_SEARCH) || tabChanged) {
        	tabChanged = false;

        	String queryTerm = request.getParameter(Settings.PARAM_QUERY_STRING);

        	if (queryTerm == null)
        		queryTerm = CURRENT_QUERY_TERM;
        	else
        		CURRENT_QUERY_TERM = queryTerm;

        	// Build query Term (append the correct datatype depending on the current Tab)
        	if (CURRENT_TAB.equals(PARAMV_TAB_ENV) && queryTerm.indexOf(Settings.QFIELD_DATATYPE) == -1)
            	queryTerm += " "+Settings.QFIELD_DATATYPE+":"+Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS;
            else
            	queryTerm += " "+Settings.QFIELD_DATATYPE+":"+Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS_ADDRESS;


        	String selectedDS = Settings.SEARCH_INITIAL_DATASOURCE;
            ThreadedQueryController controller = new ThreadedQueryController();
            controller.setTimeout(PortalConfig.getInstance().getInt(PortalConfig.QUERY_TIMEOUT_THREADED, 120000));

            QueryDescriptor qd = null;
            IngridHits rankedHits = null;
            IngridQuery query = null;
       
            try {
                query = QueryStringParser.parse(queryTerm);
            } catch (Throwable t) {}

            // Initialize the SearchState Object
//            SearchState.resetSearchState(request, SEARCH_TOPIC);
            SearchState.adaptSearchState(request, Settings.MSG_QUERY, query, SEARCH_TOPIC);
            SearchState.adaptSearchState(request, Settings.PARAM_QUERY_STRING, queryTerm, SEARCH_TOPIC);
            SearchState.adaptSearchState(request, Settings.PARAM_RANKING, IngridQuery.SCORE_RANKED, SEARCH_TOPIC);
            SearchState.adaptSearchState(request, Settings.PARAM_GROUPING, IngridQuery.GROUPED_BY_PLUGID, SEARCH_TOPIC);

            if (CURRENT_TAB.equals(PARAMV_TAB_ENV))
            	SearchState.adaptSearchState(request, Settings.PARAM_DATASOURCE, Settings.PARAMV_DATASOURCE_ENVINFO, SEARCH_TOPIC);
            else
            	SearchState.adaptSearchState(request, Settings.PARAM_DATASOURCE, Settings.PARAMV_DATASOURCE_ADDRESS, SEARCH_TOPIC);


            // process query, create QueryDescriptor
            qd = QueryPreProcessor.createRankedQueryDescriptor(request);
            qd.setGetPlugDescription(true);

            if (qd != null) {
                controller.addQuery(Settings.MSGV_RANKED_QUERY, qd);
                SearchState.resetSearchStateObject(request, Settings.MSG_SEARCH_FINISHED_RANKED, SEARCH_TOPIC);
            }

            // ----- Execute the query -----
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
                	// Do nothing
                }
                
                // post process ranked hits if exists
                boolean rankedColumnHasMoreGroupedPages = true;
                if (results.containsKey(Settings.MSGV_RANKED_QUERY)) {
                    rankedHits = QueryResultPostProcessor.processRankedHits((IngridHits) results.get(Settings.MSGV_RANKED_QUERY), selectedDS);
                    SearchState.adaptSearchState(request, Settings.MSG_SEARCH_RESULT_RANKED, rankedHits, SEARCH_TOPIC);
                    SearchState.adaptSearchState(request, Settings.MSG_SEARCH_FINISHED_RANKED, Settings.MSGV_TRUE, SEARCH_TOPIC);

                    // -------------- BEGIN NAVIGATION LOGIC --------------
                    // get the grouping starthits history from session
                    // create and initialize if not exists
                    try {
                    	ArrayList groupedStartHits = 
                    		(ArrayList) SearchState.getSearchStateObject(request, Settings.PARAM_GROUPING_STARTHITS, SEARCH_TOPIC);
                    	if (groupedStartHits == null) {
                    		groupedStartHits = new ArrayList();
                    		SearchState.adaptSearchState(request, Settings.PARAM_GROUPING_STARTHITS, groupedStartHits, SEARCH_TOPIC);
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

                int numberOfRankedHits = 0;
                if (rankedHits != null) {
                	numberOfRankedHits = (int) rankedHits.length();
                }
                // adapt settings of ranked page navigation
                HashMap rankedPageNavigation = UtilsSearch.getPageNavigation(rankedStartHit,
                		Settings.SEARCH_RANKED_HITS_PER_PAGE, numberOfRankedHits,
                		Settings.SEARCH_RANKED_NUM_PAGES_TO_SELECT);

                Object rankedSearchFinished = SearchState.getSearchStateObject(request, Settings.MSG_SEARCH_FINISHED_RANKED, SEARCH_TOPIC);

                // ----------------------------------
                // prepare view
                // ----------------------------------

                // GROUPING
                // adapt page navigation for grouping in left column 
            	UtilsSearch.adaptRankedPageNavigationToGrouping(
                   		rankedPageNavigation, currentSelectorPage, rankedColumnHasMoreGroupedPages, numberOfRankedHits, request);

            	// prepare view
                context.put("grouping", "domain");
                context.put("rankedPageSelector", rankedPageNavigation);
                context.put("rankedResultList", rankedHits);
                context.put("rankedSearchFinished", rankedSearchFinished);

                ps.put("rankedPageSelector", rankedPageNavigation);
                ps.put("rankedResultList", rankedHits);
                ps.put("rankedSearchFinished", rankedSearchFinished);
            }
            // -------------- END NAVIGATION LOGIC --------------

            ps.put("rankedResultList", rankedHits);
        }
    	
        // Decide if the user is navigating the tree or coming from another page 
        else if (reqParam.equalsIgnoreCase("nav"))
        {
        	// Tree Navigation (keep search state)
        	context.put("grouping", "domain");
        	context.put("rankedPageSelector", ps.get("rankedPageSelector"));
        	context.put("rankedResultList", ps.get("rankedResultList"));
        	context.put("rankedSearchFinished", ps.get("rankedSearchFinished"));
        }
        
        else
        {
        	// ------------
        	// Delete Search State (user coming from another page)
        	ps.remove("rankedResultList");
        	// ------------
        }
        
        // TODO Test if the site navigation works after we delete the search state
        // remove if needed
//        SearchState.resetSearchState(request);
//        SearchState.adaptSearchState(request, Settings.MSG_QUERY, query);
//        SearchState.adaptSearchState(request, Settings.PARAM_QUERY_STRING, null);
        
        super.doView(request, response);
    }

    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException, IOException {
    	String action = request.getParameter(Settings.PARAM_ACTION);

    	// Build URL to the current psml
        RequestContext requestContext = (RequestContext) request.getAttribute(RequestContext.REQUEST_PORTALENV);                	
    	PortalURL portalURL = requestContext.getPortalURL(); 
    	String redirectPage = portalURL.getBasePath() + portalURL.getPath(); 
    	
    	if (action == null) {
    		action = "";
    		// Page Navigation
            // currentSelectorPage is set, send according message (Search state)
            String currentSelectorPage = request.getParameter(Settings.PARAM_CURRENT_SELECTOR_PAGE);
            if (currentSelectorPage != null) {
                SearchState.adaptSearchState(request, Settings.MSG_QUERY_EXECUTION_TYPE, Settings.MSGV_RANKED_QUERY, SEARCH_TOPIC);
                SearchState.adaptSearchState(request, Settings.PARAM_CURRENT_SELECTOR_PAGE, currentSelectorPage, SEARCH_TOPIC);
            }
    		tabChanged = true;
            actionResponse.sendRedirect(redirectPage + SearchState.getURLParamsMainSearch(request, SEARCH_TOPIC));
    	}

    	else if (action.equalsIgnoreCase(Settings.PARAMV_ACTION_CHANGE_TAB)) {
        // changed tab
    		CURRENT_TAB = request.getParameter(Settings.PARAM_TAB);

            PortletSession session = request.getPortletSession();
            PageState ps = (PageState) session.getAttribute("portlet_state");
            if (ps.get("rankedResultList") != null)
        		tabChanged = true;
    	}

    	else if (action.equalsIgnoreCase("showAllForDomain"))
    	{
    		SearchState.adaptSearchState(request, Settings.PARAM_FILTER, request.getParameter(Settings.PARAM_GROUPING), SEARCH_TOPIC);
    		SearchState.adaptSearchState(request, Settings.PARAM_SUBJECT, request.getParameter(Settings.PARAM_SUBJECT), SEARCH_TOPIC);

    		actionResponse.sendRedirect(Settings.PAGE_SEARCH_RESULT + SearchState.getURLParamsMainSearch(request, SEARCH_TOPIC));    		
    	}
    }

    
    protected String getTitle(RenderRequest request) {
    	ResourceBundle bundle = getPortletConfig().getResourceBundle(request.getLocale());
    	return bundle.getString(PORTLET_TITLE);
    }
}