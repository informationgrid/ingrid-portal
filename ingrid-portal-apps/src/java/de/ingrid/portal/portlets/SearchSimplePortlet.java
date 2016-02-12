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
package de.ingrid.portal.portlets;

import java.io.IOException;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import org.apache.jetspeed.om.page.ContentPage;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ingrid.portal.config.IngridSessionPreferences;
import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.forms.SearchSimpleForm;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.global.UtilsFacete;
import de.ingrid.portal.search.SearchState;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.QueryStringParser;

/**
 * This portlet handles the "Simple Search" input fragment. NOTICE: This portlet
 * is used in the home page AND in the main-search page AND in the extended
 * search page ...
 * 
 * created 11.01.2006
 * 
 * @author joachim
 * @version
 * 
 */
public class SearchSimplePortlet extends GenericVelocityPortlet {

    private final static Logger log = LoggerFactory.getLogger(SearchSimplePortlet.class);

    // TITLE KEYS

    /**
     * key of title for default-page (start page) and main-search page, if no
     * search is performed, passed from default-page per Preferences
     */
    private final static String TITLE_KEY_SEARCH = "searchSimple.title.search";

    /**
     * key of title for main-search page if results are displayed, passed from
     * page per Preferences
     */
    private final static String TITLE_KEY_RESULT = "searchSimple.title.result";

    /*
     * (non-Javadoc)
     * 
     * @see javax.portlet.Portlet#init(javax.portlet.PortletConfig)
     */
    public void init(PortletConfig config) throws PortletException {
        super.init(config);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.portlet.GenericPortlet#doView(javax.portlet.RenderRequest,
     *      javax.portlet.RenderResponse)
     */
    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
    	Context context = getContext(request);
        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()), request.getLocale());
        context.put("MESSAGES", messages);

        // ----------------------------------
        // read PREFERENCES (title passed from page)
        // NOTICE: this portlet is on different pages (default-page,
        // main-search, search-settings ...)
        // TITLE DETERMINES PAGE
        // ----------------------------------
        PortletPreferences prefs = request.getPreferences();
        String titleKey = prefs.getValue("titleKey", TITLE_KEY_SEARCH);
        context.put("titleKey", titleKey);
        String helpKey = prefs.getValue( "helpKey", "" );
        context.put("helpKey", helpKey);
        
        // ----------------------------------
        // check for passed RENDER PARAMETERS (for bookmarking) and
        // ADAPT OUR PERMANENT STATE (MESSAGES)
        // ----------------------------------
        String [] actions = request.getParameterValues(Settings.PARAM_ACTION);
        String action = null;
        if(actions != null){
        	if(Utils.isJavaScriptEnabled(request)){
        		action = actions[actions.length-1];
        	}else{
        		if(actions.length>1){
        			action = actions[actions.length-1];
        		}
        	}
        }
        if (action == null) {
            action = "";
        }
        if (action.equals(Settings.PARAMV_ACTION_NEW_SEARCH)) {
            // reset relevant search stuff, we perform a new one !
            SearchState.resetSearchState(request);
            SearchState.adaptSearchState(request, Settings.MSG_QUERY_EXECUTION_TYPE, Settings.MSGV_NEW_QUERY);
        }

        // NOTICE: if no query string in request, we keep the old query string
        // in state, so it is
        // displayed. BUT WE REMOVE THE INGRID QUERY from state -> leads to
        // empty result portlet,
        // empty similar portlet etc.
        String queryInRequest = request.getParameter(Settings.PARAM_QUERY_STRING);

        // get queryString extension from request
        String queryExtInRequest = request.getParameter(Settings.PARAM_QUERY_STRING_EXT);
        if (queryExtInRequest != null) {
            queryInRequest = queryInRequest.concat(" ").concat(queryExtInRequest);
        }

        if (!SearchState.adaptSearchStateIfNotNull(request, Settings.PARAM_QUERY_STRING, queryInRequest)) {
            SearchState.resetSearchStateObject(request, Settings.MSG_QUERY);
        }

        // NOTICE: if no datasource in request WE KEEP THE OLD ONE IN THE STATE,
        // so it is
        // displayed (but not bookmarkable)
        SearchState.adaptSearchStateIfNotNull(request, Settings.PARAM_DATASOURCE, request
                .getParameter(Settings.PARAM_DATASOURCE));

        // grouping
        SearchState.adaptSearchStateIfNotNull(request, Settings.PARAM_GROUPING, request
                .getParameter(Settings.PARAM_GROUPING));

        // ----------------------------------
        // set data for view template
        // ----------------------------------

        // set datasource
        String selectedDS = SearchState.getSearchStateObjectAsString(request, Settings.PARAM_DATASOURCE);
        if (selectedDS.length() == 0) {
            selectedDS = Settings.SEARCH_INITIAL_DATASOURCE;
            SearchState.adaptSearchState(request, Settings.PARAM_DATASOURCE, selectedDS);
        }
        context.put("ds", selectedDS);

        // Update Action Form (simple search form content)
        // NOTICE: we use get method without instantiation, so we can do special
        // initialisation !
        SearchSimpleForm af = (SearchSimpleForm) Utils.getActionForm(request, SearchSimpleForm.SESSION_KEY,
                PortletSession.APPLICATION_SCOPE);
        if (af == null) {
            af = (SearchSimpleForm) Utils.addActionForm(request, SearchSimpleForm.SESSION_KEY, SearchSimpleForm.class,
                    PortletSession.APPLICATION_SCOPE);
            af.setINITIAL_QUERY(messages.getString("searchSimple.query.initial"));
            af.init();
        }

        // handle change of locale for initial string !
        if (af.getInput(SearchSimpleForm.FIELD_QUERY).equals(af.getINITIAL_QUERY())) {
            af.setINITIAL_QUERY(messages.getString("searchSimple.query.initial"));
            af.setInput(SearchSimpleForm.FIELD_QUERY, af.getINITIAL_QUERY());
        }

        // NOTICE: this may be former query, if no query in request ! we keep
        // that one for convenience
        // (although this state is not bookmarkable !)
        String queryString = SearchState.getSearchStateObjectAsString(request, Settings.PARAM_QUERY_STRING);
        if(queryString.length() == 0 && af != null){
        	queryString = af.getInput("q");
        }
        af.setInput(SearchSimpleForm.FIELD_QUERY, queryString);
        // put ActionForm to context. use variable name "actionForm" so velocity
        // macros work !
        context.put("actionForm", af);

        // ----------------------------------
        // prepare Search, Search will be performed in SearchResult portlet
        // ----------------------------------
        // NOTICE: af.validate() also checks for default text (and sets default
        // text if empty).
        boolean validInput = af.validate();
        boolean setUpNewQuery = true;
        // don't create IngridQuery when:
        // - only datasource was changed
        // - we have no query parameter in our URL (e.g. we entered from other
        // page)
        // - the enterd query is empty or initial value
        if (queryInRequest == null || !validInput) {
            setUpNewQuery = false;
        }
        if (setUpNewQuery) {
            // set up Ingrid Query -> triggers search in result portlet
            String errCode = setUpQuery(request, af.getInput(SearchSimpleForm.FIELD_QUERY));
            if (errCode != null) {
                af.setError(SearchSimpleForm.FIELD_QUERY, messages.getString(errCode));
            }
        }

        // ----------------------------------
        // adapt title (passed from page)
        // NOTICE: this portlet is on different pages (default-page,
        // main-search, search-settings ...)
        // title also depends on search state (query submitted or not ...)
        // ----------------------------------
        if (titleKey.equals(TITLE_KEY_RESULT)) {
            // we're on main search page, check whether there's a query and
            // adapt title
            if (SearchState.getSearchStateObject(request, Settings.MSG_QUERY) == null) {
                titleKey = TITLE_KEY_SEARCH;
            }
        }
        response.setTitle(messages.getString(titleKey));

        context.put("enableFacets", PortalConfig.getInstance().getBoolean(
                PortalConfig.PORTAL_ENABLE_SEARCH_FACETE, Boolean.FALSE));
       
        // Set up query 
        if(Utils.isJavaScriptEnabled(request)){
        	ContentPage page = (ContentPage) request.getAttribute("org.apache.jetspeed.Page");
            Map<String, String[]> params = request.getParameterMap();
            if(page != null && (params != null && params.size() == 0)){
            	if(Settings.PAGE_SEARCH_RESULT.indexOf(page.getPath()) > 0 && queryString != null && queryString.length() > 0){
            		if(af != null){
            			if(!queryString.equals(af.getINITIAL_QUERY())){
		            		response.setTitle(messages.getString(TITLE_KEY_RESULT));
		            		UtilsFacete.setAttributeToSession(request, UtilsFacete.SESSION_PARAMS_READ_FACET_FROM_SESSION, true);
		            		setUpQuery(request, queryString);
            			}
            		}
                }
            }
        }
        super.doView(request, response);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.portlet.Portlet#processAction(javax.portlet.ActionRequest,
     *      javax.portlet.ActionResponse)
     */
    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {
        String action = request.getParameter(Settings.PARAM_ACTION);
        if (action == null) {
            return;
        }

        // adapt SearchState (base for generating URL params FOR BOOKMARKING !!!)
        // Reset all Stuff which are remains of former state and shouldn't be
        // taken into account on any action in SimpleSearch form
        SearchState.adaptSearchState(request, Settings.PARAM_STARTHIT_RANKED, null);
        SearchState.adaptSearchState(request, Settings.PARAM_CURRENT_SELECTOR_PAGE, null);

        SearchState.adaptSearchState(request, Settings.PARAM_FILTER, null);
        SearchState.adaptSearchState(request, Settings.PARAM_SUBJECT, null);        

        if (action.equalsIgnoreCase(Settings.PARAMV_ACTION_NEW_SEARCH)) {
        	// adapt current SearchState (base for generating URL params for render
            // request):
            // - query string
        	
        	// set querystring to search state if the delete button was not pressed
        	if (request.getParameter("doDeleteQuery") == null) {        	
	        	SearchState.adaptSearchState(request, Settings.PARAM_QUERY_STRING, request
	                    .getParameter(Settings.PARAM_QUERY_STRING));
        	} else {
	        	SearchState.adaptSearchState(request, Settings.PARAM_QUERY_STRING, "");
	        	SearchSimpleForm af = (SearchSimpleForm) Utils.getActionForm(request, SearchSimpleForm.SESSION_KEY,
	                    PortletSession.APPLICATION_SCOPE);
	        	af.setInput("q", "");
        	}

        	// Adapt search state to settings of IngridSessionPreferences !!!
        	// On new search, no temporary stuff from bookmarks etc. should be used !
            IngridSessionPreferences sessionPrefs = Utils.getSessionPreferences(request,
                    IngridSessionPreferences.SESSION_KEY, IngridSessionPreferences.class);
            sessionPrefs.adaptSearchState(request);

            // check if submit or requery or delete query
            if (request.getParameter("doSetQuery") == null && request.getParameter("doDeleteQuery") == null) {
                // redirect to our page wih parameters for bookmarking
                actionResponse.sendRedirect(actionResponse.encodeURL(Settings.PAGE_SEARCH_RESULT + SearchState.getURLParamsMainSearch(request)));
            }

        }
    }

    /**
     * Create IngridQuery and add to state. RETURNS an Error String, if an error
     * occured, which can be displayed in form.
     * 
     * @param request
     * @param queryString
     * @return null if OK, otherwise "Error Code" (for resourceBundle)
     */
    private String setUpQuery(PortletRequest request, String queryString) {
        // Create IngridQuery
        IngridQuery query = null;
        try {
            query = QueryStringParser.parse(queryString);
        } catch (Throwable t) {
            if (log.isDebugEnabled()) {
                log.debug("Problems creating IngridQuery, parsed query string: " + queryString, t);
            } else {
                log.debug("Problems creating IngridQuery, parsed query string: " + queryString + ". switch to log level debug to see the exeption details.");
            }

            return "searchSimple.error.queryFormat";
        }

        // set query in state for result portlet
        // NOTICE: DON'T MANIPULATE IngridQuery itself here, we don't use that
        // one
        // in the Result Portlet anymore, instead we create a new one from the
        // queryString,
        // to guarantee no shallow copy !!!! BUT KEEP THE QUERY IN THE STATE !
        // is used in
        // simpleTermsPortlet and also in resultPortlet to determine whether
        // query should be executed !
        SearchState.adaptSearchState(request, Settings.MSG_QUERY, query);
        SearchState.adaptSearchState(request, Settings.PARAM_QUERY_STRING, queryString);

        return null;
    }
}
