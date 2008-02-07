package de.ingrid.portal.portlets;

import java.io.IOException;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.portal.config.IngridSessionPreferences;
import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.forms.SearchSimpleForm;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.global.UtilsDB;
import de.ingrid.portal.global.UtilsString;
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

    private final static Log log = LogFactory.getLog(SearchSimplePortlet.class);

    // VIEW TEMPLATES

    private final static String TEMPLATE_SEARCH_SIMPLE = "/WEB-INF/templates/search_simple.vm";

    private final static String TEMPLATE_SEARCH_EXTENDED = "/WEB-INF/templates/search_extended/search_ext.vm";

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

    /**
     * key of title for main exstended search pages, passed from page per
     * Preferences
     */
    private final static String TITLE_KEY_EXTENDED = "searchSimple.title.extended";

    // ACTION VALUES

    /** value of action parameter if datasource was clicked */
    private final static String PARAMV_ACTION_NEW_DATASOURCE = "doChangeDS";

    /** value of action parameter if "search settings" was clicked */
    private final static String PARAMV_ACTION_SEARCH_SETTINGS = "doSearchSettings";

    /** value of action parameter if "search history" was clicked */
    private final static String PARAMV_ACTION_SEARCH_HISTORY = "doSearchHistory";

    /** value of action parameter if "Extended Search" was clicked */
    private final static String PARAMV_ACTION_SEARCH_EXTENDED = "doSearchExtended";

    /** value of action parameter if "save query" was clicked */
    private static final String PARAMV_ACTION_SAVE_QUERY = "doSaveQuery";

    // PAGES

    /** search-history page -> displays and handles search settings */
    private final static String PAGE_SEARCH_HISTORY = "/ingrid-portal/portal/search-history.psml";

    /** search-settings page -> displays and handles search settings */
    private final static String PAGE_SEARCH_SETTINGS = "/ingrid-portal/portal/search-settings.psml";

    /** save query page -> displays and handles save query functionality */
    private final static String PAGE_SAVE_QUERY = "/ingrid-portal/portal/search-save.psml";

    /**
     * main extended search page for datasource "environmentinfos" -> envinfo:
     * topic/terms
     */
    private final static String PAGE_SEARCH_EXT_ENV = "/ingrid-portal/portal/search-extended/search-ext-env-topic-terms.psml";

    /**
     * main extended search page for datasource "address" -> address:
     * topic/terms
     */
    private final static String PAGE_SEARCH_EXT_ADR = "/ingrid-portal/portal/search-extended/search-ext-adr-topic-terms.psml";

    /**
     * main extended search page for datasource "research" -> research:
     * topic/attributes
     */
    private final static String PAGE_SEARCH_EXT_RES = "/ingrid-portal/portal/search-extended/search-ext-res-topic-attributes.psml";

    /**
     * main extended search page for datasource "law" -> law:
     * topic/attributes
     */
    private final static String PAGE_SEARCH_EXT_LAW = "/ingrid-portal/portal/search-extended/search-ext-law-topic-terms.psml";
    
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
                request.getLocale()));
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

        // set view template, we use the same portlet for Simple- and Extended
        // Search, just
        // the view template differs !
        setDefaultViewPage(TEMPLATE_SEARCH_SIMPLE);
        if (titleKey.equals(TITLE_KEY_EXTENDED)) {
            setDefaultViewPage(TEMPLATE_SEARCH_EXTENDED);
        }

        // ----------------------------------
        // check for passed RENDER PARAMETERS (for bookmarking) and
        // ADAPT OUR PERMANENT STATE (MESSAGES)
        // ----------------------------------
        String action = request.getParameter(Settings.PARAM_ACTION);
        if (action == null) {
            action = "";
        }
        if (action.equals(Settings.PARAMV_ACTION_NEW_SEARCH) || action.equals(PARAMV_ACTION_NEW_DATASOURCE)) {
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
                PortletSession.PORTLET_SCOPE);
        if (af == null) {
            af = (SearchSimpleForm) Utils.addActionForm(request, SearchSimpleForm.SESSION_KEY, SearchSimpleForm.class,
                    PortletSession.PORTLET_SCOPE);
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
        if (action.equals(PARAMV_ACTION_NEW_DATASOURCE) || action.equals(PARAMV_ACTION_SEARCH_SETTINGS)
                || action.equals(PARAMV_ACTION_SEARCH_HISTORY) || action.equals(PARAMV_ACTION_SEARCH_EXTENDED)
                || queryInRequest == null || !validInput) {
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

        // enable the save button if the query was set AND a user is logged on
        if (validInput && Utils.getLoggedOn(request)) {
            context.put("enableSave", "true");
        }

        // enable/disable providers drop down
        if (PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_SEARCH_DISPLAY_PROVIDERS)) {
            String partner = PortalConfig.getInstance().getString(PortalConfig.PORTAL_SEARCH_RESTRICT_PARTNER);
            List providers;
            if (partner == null || partner.length() == 0) {
                providers = UtilsDB.getProviders();
            } else {
                providers = UtilsDB.getProvidersFromPartnerKey(partner);
            }
            context.put("displayProviders", Boolean.TRUE);
            context.put("providers", providers);
            context.put("UtilsString", new UtilsString());
            
            // get selected provider
            IngridSessionPreferences sessionPrefs = Utils.getSessionPreferences(request,
                    IngridSessionPreferences.SESSION_KEY, IngridSessionPreferences.class);
            String provider = request.getParameter(Settings.PARAM_PROVIDER);
            if (provider != null) {
                sessionPrefs.put(IngridSessionPreferences.RESTRICTING_PROVIDER, provider);
            }
            context.put("selectedProviderIdent", sessionPrefs.get(IngridSessionPreferences.RESTRICTING_PROVIDER));
        }

        context.put("enableDatasourceResearch", PortalConfig.getInstance().getBoolean(
                PortalConfig.PORTAL_ENABLE_DATASOURCE_RESEARCH, Boolean.TRUE));
        context.put("enableDatasourceAddresses", PortalConfig.getInstance().getBoolean(
                PortalConfig.PORTAL_ENABLE_DATASOURCE_ADDRESSES, Boolean.TRUE));
        context.put("enableDatasourceLaws", PortalConfig.getInstance().getBoolean(
                PortalConfig.PORTAL_ENABLE_DATASOURCE_LAWS, Boolean.TRUE));
        
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
        SearchState.adaptSearchState(request, Settings.PARAM_STARTHIT_UNRANKED, null);
        SearchState.adaptSearchState(request, Settings.PARAM_CURRENT_SELECTOR_PAGE, null);
        SearchState.adaptSearchState(request, Settings.PARAM_CURRENT_SELECTOR_PAGE_UNRANKED, null);

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
        	}

        	// Adapt search state to settings of IngridSessionPreferences !!!
        	// On new search, no temporary stuff from bookmarks etc. should be used !
            IngridSessionPreferences sessionPrefs = Utils.getSessionPreferences(request,
                    IngridSessionPreferences.SESSION_KEY, IngridSessionPreferences.class);
            sessionPrefs.adaptSearchState(request);

            // check if submit or requery or delete query
            if (request.getParameter("doSetQuery") == null && request.getParameter("doDeleteQuery") == null) {
                // redirect to our page wih parameters for bookmarking
                actionResponse.sendRedirect(Settings.PAGE_SEARCH_RESULT + SearchState.getURLParamsMainSearch(request));
            }

        } else if (action.equalsIgnoreCase(PARAMV_ACTION_NEW_DATASOURCE)) {
            String newDatasource = request.getParameter(Settings.PARAM_DATASOURCE);

            // adapt SearchState (base for generating URL params for render
            // request):
            // - set datasource
            SearchState.adaptSearchState(request, Settings.PARAM_DATASOURCE, newDatasource);

            // don't populate action form, this is no submit, so no form
            // parameters are in request !
            // TODO use JavaScript to submit form on datasource change ! then
            // populate ActionForm,
            // in that way we don't loose query changes on data source change,
            // when changes weren't submitted before

            // redirect to MAIN page with parameters for bookmarking, ONLY IF
            // WE'RE "ON MAIN PAGE"
            // Otherwise go to startpage of extended search for selected
            // datasource without support for bookmarking !
            if (getDefaultViewPage().equals(TEMPLATE_SEARCH_SIMPLE)) {
                // we're in main search
                actionResponse.sendRedirect(Settings.PAGE_SEARCH_RESULT + SearchState.getURLParamsMainSearch(request));
            } else {
                // we're in extended search
                if (newDatasource.equals(Settings.PARAMV_DATASOURCE_ENVINFO)) {
                    actionResponse.sendRedirect(PAGE_SEARCH_EXT_ENV);
                } else if (newDatasource.equals(Settings.PARAMV_DATASOURCE_ADDRESS)) {
                    actionResponse.sendRedirect(PAGE_SEARCH_EXT_ADR);
                } else if (newDatasource.equals(Settings.PARAMV_DATASOURCE_RESEARCH)) {
                    actionResponse.sendRedirect(PAGE_SEARCH_EXT_RES);
                } else if (newDatasource.equals(Settings.PARAMV_DATASOURCE_LAW)) {
                    actionResponse.sendRedirect(PAGE_SEARCH_EXT_LAW);
                }
            }

        } else if (action.equalsIgnoreCase(PARAMV_ACTION_SEARCH_SETTINGS)) {
            actionResponse.sendRedirect(PAGE_SEARCH_SETTINGS + SearchState.getURLParamsMainSearch(request));

        } else if (action.equalsIgnoreCase(PARAMV_ACTION_SEARCH_HISTORY)) {
            actionResponse.sendRedirect(PAGE_SEARCH_HISTORY + SearchState.getURLParamsMainSearch(request));

        } else if (action.equalsIgnoreCase(PARAMV_ACTION_SAVE_QUERY)) {
            actionResponse.sendRedirect(PAGE_SAVE_QUERY + SearchState.getURLParamsMainSearch(request));

        } else if (action.equalsIgnoreCase(PARAMV_ACTION_SEARCH_EXTENDED)) {
            String currentDatasource = SearchState.getSearchStateObjectAsString(request, Settings.PARAM_DATASOURCE);
            if (currentDatasource.equals(Settings.PARAMV_DATASOURCE_ENVINFO)) {
                actionResponse.sendRedirect(PAGE_SEARCH_EXT_ENV);
            } else if (currentDatasource.equals(Settings.PARAMV_DATASOURCE_ADDRESS)) {
                actionResponse.sendRedirect(PAGE_SEARCH_EXT_ADR);
            } else if (currentDatasource.equals(Settings.PARAMV_DATASOURCE_RESEARCH)) {
                actionResponse.sendRedirect(PAGE_SEARCH_EXT_RES);
            } else if (currentDatasource.equals(Settings.PARAMV_DATASOURCE_LAW)) {
                actionResponse.sendRedirect(PAGE_SEARCH_EXT_LAW);
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
