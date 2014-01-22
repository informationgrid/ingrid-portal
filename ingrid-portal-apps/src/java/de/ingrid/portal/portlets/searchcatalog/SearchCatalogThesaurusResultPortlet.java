/*
 * Copyright (c) 2007 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets.searchcatalog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jetspeed.container.url.PortalURL;
import org.apache.jetspeed.request.RequestContext;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.global.CodeListServiceFactory;
import de.ingrid.portal.global.IngridHitsWrapper;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.UtilsString;
import de.ingrid.portal.interfaces.impl.SNSSimilarTermsInterfaceImpl;
import de.ingrid.portal.search.PageState;
import de.ingrid.portal.search.QueryPreProcessor;
import de.ingrid.portal.search.QueryResultPostProcessor;
import de.ingrid.portal.search.SearchState;
import de.ingrid.portal.search.UtilsSearch;
import de.ingrid.portal.search.net.QueryDescriptor;
import de.ingrid.portal.search.net.ThreadedQueryController;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHits;


public class SearchCatalogThesaurusResultPortlet extends GenericVelocityPortlet {

    private final static Logger log = LoggerFactory.getLogger(SearchCatalogThesaurusResultPortlet.class);

    // Location of the custom Title (resource bundle key)
	private final static String PORTLET_TITLE = "searchCatThesaurus.result.title";

	// TAB values from action request (request parameter)
    /** tab param value if tab enviromental is clicked */
    private final static String PARAMV_TAB_ENV = "1";

    /** tab param value if tab addresses is clicked */
    private final static String PARAMV_TAB_ADR = "2";

    /* Defines which search state to use */
    private final static String SEARCH_STATE_TOPIC = Settings.MSG_TOPIC_SEARCH;

    // VARIABLE NAMES FOR VELOCITY
    /** velocity variable name for main tab, has to be put to context, so correct tab is selected */
    protected final static String VAR_MAIN_TAB = "tab";

    // Query to pass to "Zeige Alle" page
    protected String fullQueryZeigeAlle = null; 

    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);
        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()));
        context.put("MESSAGES", messages);

        PageState ps = getPageState(request);
        context.put("ps", ps);
        
        context.put("Codelists", CodeListServiceFactory.instance());
        // add request language, used to localize the map client
        context.put("languageCode", request.getLocale().getLanguage());

        context.put("enable_address", PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_ENABLE_SEARCH_CATALOG_THESAURUS_RESULT_ADDRESS, Boolean.TRUE));
        
        // READ REQUEST PARAMETERS (so BOOKMARKING, BACK BUTTON etc. WORKS)
        // -----------------------------------------------------------

        // datasource
        String selectedDS = request.getParameter(Settings.PARAM_DATASOURCE);
        if (selectedDS == null) {
        	selectedDS = Settings.PARAMV_DATASOURCE_ENVINFO;
        }
        String tab = getTabFromDatasource(selectedDS);
        context.put(VAR_MAIN_TAB, tab);

        // pure thesaurus query
    	String queryThesaurusTerm = request.getParameter(Settings.PARAM_QUERY_STRING);
    	
    	// Check for englisch topic name
		String topicNameEn = "";
		String nodeId = request.getParameter("nodeId");
		if( nodeId != null){
			boolean addThesaurusSearchEnTerm = PortalConfig.getInstance().getBoolean(PortalConfig.THESAURUS_SEARCH_ADD_EN_TERM);
            if(addThesaurusSearchEnTerm && request.getLocale().getLanguage() != "en"){
            	nodeId = request.getParameter("nodeId");
            	IngridHit[] hitsTermsEN = SNSSimilarTermsInterfaceImpl.getInstance().getTopicFromID(nodeId, Locale.ENGLISH);
            	if(hitsTermsEN != null && hitsTermsEN.length > 0){
            		topicNameEn = hitsTermsEN[0].toString();
            		if(topicNameEn.length() > 0){
            			if(queryThesaurusTerm != null){
            				queryThesaurusTerm = "\'" + queryThesaurusTerm + "\' OR \'" + topicNameEn + "\'";
            			}
                	}
                }
            }
 		}
		
    	ps.put("queryTerm", queryThesaurusTerm);

        // Page Navigation, only set if really navigated results before !
        String currentSelectorPage = request.getParameter(Settings.PARAM_CURRENT_SELECTOR_PAGE);

        // Start Hits of pages in navigation read from SearchState, not transported in Request :( :( :(
    	ArrayList groupedStartHits = 
    		(ArrayList) SearchState.getSearchStateObject(request, Settings.PARAM_GROUPING_STARTHITS, SEARCH_STATE_TOPIC);

        String action = request.getParameter("action"); 
        if (action == null)
        	action = "";
        
        // PROCESS
        // -------

        boolean thesaurusTreeNavigated =
        	action.equalsIgnoreCase("doOpenNode") ||
			action.equalsIgnoreCase("doCloseNode"); 
        boolean newSearch =
        	// thesaurus term clicked in tree
        	action.equalsIgnoreCase(Settings.PARAMV_ACTION_NEW_SEARCH) ||
       	 	// changed tab in result
        	action.equalsIgnoreCase(Settings.PARAMV_ACTION_CHANGE_TAB);
        // indicates page navigation in results
        boolean resultsNavigated = (currentSelectorPage != null);
        
        // reset page navigation stuff if new search !
        if (newSearch) {
        	currentSelectorPage = null;
        	groupedStartHits = null;
        }

        // FIRST check whether thesaurus tree was navigated ! (no search) 
        if (thesaurusTreeNavigated)
        {
        	// read from cache !
        	context.put("grouping", "plugid");
        	context.put("rankedPageSelector", ps.get("rankedPageSelector"));
        	context.put("rankedResultList", ps.get("rankedResultList"));
        	context.put("rankedSearchFinished", ps.get("rankedSearchFinished"));
        }
        
        // ELSE check whether a search should be performed
        else if (newSearch || resultsNavigated)
        {
        	// No term e.g. when switched tabs initially before any search 
        	if (queryThesaurusTerm != null) {
        		performSearch(queryThesaurusTerm, selectedDS, currentSelectorPage, groupedStartHits, request);
        	}
        }

        else
        {
        	// Delete Search Results (user coming from another page)
        	SearchState.resetSearchState(request, SEARCH_STATE_TOPIC);
        	ps.remove("rankedResultList");
        }
        
        super.doView(request, response);
    }

    private void performSearch(String queryThesaurusTerm,
    		String selectedDS,
    		String currentSelectorPageFromRequest,
    		ArrayList groupedStartHits,
    		RenderRequest request) {

    	// SET UP SEARCH STATE for Query Preprocessor !!!
    	// ----------------------------------------------

        // Reset the SearchState Object to guarantee we have no wrong data (e.g. filter and subject from before)    	
    	SearchState.resetSearchState(request, SEARCH_STATE_TOPIC);

    	// data source
        SearchState.adaptSearchState(request, Settings.PARAM_DATASOURCE, selectedDS, SEARCH_STATE_TOPIC);
        // ALSO GROUPING, but this should be part of request to guarantee no "user settings" grouping (see handling in QueryPreprocessor)
        SearchState.adaptSearchState(request, Settings.PARAM_GROUPING, Settings.PARAMV_GROUPING_PLUG_ID, SEARCH_STATE_TOPIC);
        // Page navigation, only set in state if really navigated before !
        int currentSelectorPage = 1;
        if (currentSelectorPageFromRequest != null) {
        	currentSelectorPage = new Integer(currentSelectorPageFromRequest).intValue();
            SearchState.adaptSearchState(request, Settings.PARAM_CURRENT_SELECTOR_PAGE, new Integer(currentSelectorPage), SEARCH_STATE_TOPIC);
        }
        SearchState.adaptSearchState(request, Settings.PARAM_GROUPING_STARTHITS, groupedStartHits, SEARCH_STATE_TOPIC);

    	// Build query Term !
        // we extend thesaurus term with restrictions for correct query (iplug, sns keywords ...)
    	
       if (selectedDS.equals(Settings.PARAMV_DATASOURCE_ENVINFO)) {
     		fullQueryZeigeAlle = "(";
     		fullQueryZeigeAlle += "t04_search.searchterm:" + UtilsString.escapeChars(queryThesaurusTerm.replace(" OR ", " OR t04_search.searchterm:"), "\"").replace("'", "\"");
     		fullQueryZeigeAlle += " (t04_search.type:2 OR t04_search.type:T OR t04_search.type:G)";
     		fullQueryZeigeAlle += getSearchDataTypes();
     		fullQueryZeigeAlle += ")";
     		fullQueryZeigeAlle += " OR ";
     		fullQueryZeigeAlle += "(";
     		fullQueryZeigeAlle += "searchterm_value.term:" + UtilsString.escapeChars(queryThesaurusTerm.replace(" OR ", " OR searchterm_value.term:"), "\"").replace("'", "\"");
     		fullQueryZeigeAlle += getSearchDataTypes();
     		fullQueryZeigeAlle += ")";
     	}
         else {
            fullQueryZeigeAlle = "t04_search.searchterm:" + UtilsString.escapeChars(queryThesaurusTerm.replace(" OR ", " OR t04_search.searchterm:"), "\"").replace("'", "\"");
         	fullQueryZeigeAlle += " (t04_search.type:4 OR t04_search.type:T OR t04_search.type:G)";
         	fullQueryZeigeAlle += " "+Settings.QFIELD_DATATYPE+":"+Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS_ADDRESS;
        }
        SearchState.adaptSearchState(request, Settings.PARAM_QUERY_STRING, fullQueryZeigeAlle, SEARCH_STATE_TOPIC);

        // process query, create QueryDescriptor
        QueryDescriptor qd = QueryPreProcessor.createRankedQueryDescriptor(request);
        qd.setGetPlugDescription(true);
        
        // THEN RESET queryTerm in search state (remove iplug data for correct next query) !!!
        SearchState.adaptSearchState(request, Settings.PARAM_QUERY_STRING, queryThesaurusTerm, SEARCH_STATE_TOPIC);


        // EXECUTE
        // -------

        ThreadedQueryController controller = new ThreadedQueryController();
        controller.setTimeout(PortalConfig.getInstance().getInt(PortalConfig.QUERY_TIMEOUT_THREADED, 120000));

        if (qd != null) {
            controller.addQuery(Settings.MSGV_RANKED_QUERY, qd);
            SearchState.resetSearchStateObject(request, Settings.MSG_SEARCH_FINISHED_RANKED, SEARCH_STATE_TOPIC);
        }

        // ----- Execute the query -----
        if (controller.hasQueries()) {
            IngridHits rankedHits = null;

            // fire queries
            HashMap results = controller.search();

            // post process ranked hits if exists
            boolean rankedColumnHasMoreGroupedPages = true;
            if (results.containsKey(Settings.MSGV_RANKED_QUERY)) {
            	rankedHits = QueryResultPostProcessor.processRankedHits((IngridHitsWrapper) results.get(Settings.MSGV_RANKED_QUERY), selectedDS);
                SearchState.adaptSearchState(request, Settings.MSG_SEARCH_RESULT_RANKED, rankedHits, SEARCH_STATE_TOPIC);
                SearchState.adaptSearchState(request, Settings.MSG_SEARCH_FINISHED_RANKED, Settings.MSGV_TRUE, SEARCH_STATE_TOPIC);

                // -------------- BEGIN NAVIGATION LOGIC --------------
                // get the grouping starthits history from session
                // create and initialize if not exists
                try {
                	groupedStartHits = 
                		(ArrayList) SearchState.getSearchStateObject(request, Settings.PARAM_GROUPING_STARTHITS, SEARCH_STATE_TOPIC);
                	if (groupedStartHits == null) {
                		groupedStartHits = new ArrayList();
                		SearchState.adaptSearchState(request, Settings.PARAM_GROUPING_STARTHITS, groupedStartHits, SEARCH_STATE_TOPIC);
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
            // always grouped navigation (see below), so we can set rankedStartHit to zero
            int rankedStartHit = 0;
            HashMap rankedPageNavigation = UtilsSearch.getPageNavigation(rankedStartHit,
            		Settings.SEARCH_RANKED_HITS_PER_PAGE, numberOfRankedHits,
            		Settings.SEARCH_RANKED_NUM_PAGES_TO_SELECT);

            Object rankedSearchFinished = SearchState.getSearchStateObject(request, Settings.MSG_SEARCH_FINISHED_RANKED, SEARCH_STATE_TOPIC);

            // ----------------------------------
            // prepare view
            // ----------------------------------

            // GROUPING
            // adapt page navigation for grouping in left column 
        	UtilsSearch.adaptRankedPageNavigationToGrouping(
               		rankedPageNavigation, currentSelectorPage, rankedColumnHasMoreGroupedPages, numberOfRankedHits, request);

        	// prepare view
            Context context = getContext(request);
            context.put("grouping", "plugid");
            context.put("rankedPageSelector", rankedPageNavigation);
            context.put("rankedResultList", rankedHits);
            context.put("rankedSearchFinished", rankedSearchFinished);

            PageState ps = getPageState(request);
            ps.put("rankedPageSelector", rankedPageNavigation);
            ps.put("rankedResultList", rankedHits);
            ps.put("rankedSearchFinished", rankedSearchFinished);
        }
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
                SearchState.adaptSearchState(request, Settings.MSG_QUERY_EXECUTION_TYPE, Settings.MSGV_RANKED_QUERY, SEARCH_STATE_TOPIC);
                SearchState.adaptSearchState(request, Settings.PARAM_CURRENT_SELECTOR_PAGE, currentSelectorPage, SEARCH_STATE_TOPIC);
            }
            actionResponse.sendRedirect(actionResponse.encodeURL(redirectPage + SearchState.getURLParamsMainSearch(request, SEARCH_STATE_TOPIC)));
    	}

    	else if (action.equalsIgnoreCase(Settings.PARAMV_ACTION_CHANGE_TAB)) {
            // changed tab, adapt Search State to current data source
    		String currentTab = request.getParameter(Settings.PARAM_TAB);
    		String selectedDS = getDatasourceFromTab(currentTab);
    		if (selectedDS != null) {
        		SearchState.adaptSearchState(request, Settings.PARAM_DATASOURCE, selectedDS, SEARCH_STATE_TOPIC);
    		}
    		// reset result page navigation !!!
            SearchState.resetSearchStateObject(request, Settings.PARAM_CURRENT_SELECTOR_PAGE, SEARCH_STATE_TOPIC);
    		
            actionResponse.sendRedirect(actionResponse.encodeURL(redirectPage + SearchState.getURLParamsMainSearch(request, SEARCH_STATE_TOPIC)));
    	}

    	else if (request.getParameter(Settings.PARAM_SUBJECT) != null) {
    		// "Zeige Alle"
            SearchState.adaptSearchState(request, Settings.PARAM_SUBJECT, request.getParameter(Settings.PARAM_SUBJECT));
            SearchState.adaptSearchState(request, Settings.PARAM_FILTER, request.getParameter(Settings.PARAM_GROUPING));

            // create URL Params with manipulated Query !!!
            // Reset afterwards, to keep original Query.
            Object origQuery = SearchState.getSearchStateObject(request, Settings.PARAM_QUERY_STRING, SEARCH_STATE_TOPIC);
            SearchState.adaptSearchState(request, Settings.PARAM_QUERY_STRING, fullQueryZeigeAlle, SEARCH_STATE_TOPIC);
            String urlParams = SearchState.getURLParamsMainSearch(request, SEARCH_STATE_TOPIC);
            SearchState.adaptSearchState(request, Settings.PARAM_QUERY_STRING, origQuery, SEARCH_STATE_TOPIC);

    		actionResponse.sendRedirect(actionResponse.encodeURL(Settings.PAGE_SEARCH_RESULT + urlParams));    		
    	}
    }

    
    protected PageState getPageState(RenderRequest request) {
        PortletSession session = request.getPortletSession();
        PageState ps = (PageState) session.getAttribute("portlet_state");
        if (ps == null) {
            ps = new PageState(this.getClass().getName());
            session.setAttribute("portlet_state", ps);
        }
        
        return ps;
    }

    protected String getTitle(RenderRequest request) {
    	ResourceBundle bundle = getPortletConfig().getResourceBundle(request.getLocale());
    	return bundle.getString(PORTLET_TITLE);
    }

    protected String getTabFromDatasource(String datasource) {
    	String tab = null;
		if (Settings.PARAMV_DATASOURCE_ENVINFO.equals(datasource)) {
			tab = PARAMV_TAB_ENV;
		} else if (Settings.PARAMV_DATASOURCE_ADDRESS.equals(datasource)) {
			tab = PARAMV_TAB_ADR;    			
		}
    	return tab;
    }
    protected String getDatasourceFromTab(String tab) {
    	String datasource = null;
		if (PARAMV_TAB_ENV.equals(tab)) {
			datasource = Settings.PARAMV_DATASOURCE_ENVINFO;
		} else if (PARAMV_TAB_ADR.equals(tab)) {
			datasource = Settings.PARAMV_DATASOURCE_ADDRESS;    			
		}
    	return datasource;
    }
    
    private String getSearchDataTypes(){
    	String dataTypes = "";
    	
    	dataTypes += " (";
    	dataTypes += Settings.QFIELD_DATATYPE+":"+Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS;
    	dataTypes += " "+Settings.QFIELD_DATATYPE+":"+Settings.QVALUE_DATATYPE_IPLUG_DSC_CSW;
		
		String queryTypes[] = PortalConfig.getInstance().getString(PortalConfig.THESAURUS_SEARCH_QUERY_TYPES).split(";");
		if(queryTypes != null){
    		for(int i=0; i < queryTypes.length; i++){
    			if(queryTypes[i].length() > 0 ){
    				dataTypes += " "+Settings.QFIELD_DATATYPE+":"+queryTypes[i];	
    			}
        	}
		}
		dataTypes += ")";
    	
    	return dataTypes;
    }
}
