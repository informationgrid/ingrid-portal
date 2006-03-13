package de.ingrid.portal.portlets;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.portals.bridges.velocity.AbstractVelocityMessagingPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.portal.forms.SearchSimpleForm;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.search.SearchState;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.ParseException;
import de.ingrid.utils.queryparser.QueryStringParser;

/**
 * This portlet handles the "Simple Search" input fragment.
 * NOTICE: This portlet is used in the home page AND in the main-search page.
 * 
 * created 11.01.2006
 * 
 * @author joachim
 * @version
 * 
 */
public class SearchSimplePortlet extends AbstractVelocityMessagingPortlet {

    private final static Log log = LogFactory.getLog(SearchSimplePortlet.class);

    private final static String TEMPLATE_SEARCH_SIMPLE = "/WEB-INF/templates/search_simple.vm";

    private final static String TEMPLATE_SEARCH_EXTENDED = "/WEB-INF/templates/search_extended/search_extended.vm";

    /** key of title for default-page (start page) and main-search page, if no search is performed,
     * passed from default-page per Preferences */
    private final static String TITLE_KEY_SEARCH = "searchSimple.title.search";

    /** key of title for main-search page if results are displayed,
     * passed from page per Preferences */
    private final static String TITLE_KEY_RESULT = "searchSimple.title.result";

    /** key of title for main exstended search pages, passed from page per Preferences */
    private final static String TITLE_KEY_EXTENDED = "searchSimple.title.extended";

    /* key of title for search-settings page, passed from page per Preferences */
    //private final static String TITLE_KEY_SETTINGS = "searchSimple.title.settings";
    /* key of title for search-history page, passed from page per Preferences */
    //private final static String TITLE_KEY_HISTORY = "searchSimple.title.history";
    /** value of action parameter if datasource was clicked */
    private final static String PARAMV_ACTION_NEW_DATASOURCE = "doChangeDS";

    /** value of action parameter if "search settings" was clicked */
    private final static String PARAMV_ACTION_SEARCH_SETTINGS = "doSearchSettings";

    /** value of action parameter if "search history" was clicked */
    private final static String PARAMV_ACTION_SEARCH_HISTORY = "doSearchHistory";

    /** value of action parameter if "Extended Search" was clicked */
    private final static String PARAMV_ACTION_SEARCH_EXTENDED = "doSearchExtended";

    /** search-history page -> displays and handles search settings */
    private final static String PAGE_SEARCH_HISTORY = "/ingrid-portal/portal/search-history.psml";

    /** search-settings page -> displays and handles search settings */
    private final static String PAGE_SEARCH_SETTINGS = "/ingrid-portal/portal/search-settings.psml";

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
        // NOTICE: this portlet is on different pages (default-page, main-search, search-settings ...)
        // TITLE DETERMINES PAGE
        // ----------------------------------
        PortletPreferences prefs = request.getPreferences();
        String titleKey = prefs.getValue("titleKey", TITLE_KEY_SEARCH);
        context.put("titleKey", titleKey);

        // set view template, we use the same portlet for Simple- and Extended Search, just
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

        // NOTICE: if no query string in request, we keep the old query string in state, so it is
        // displayed. BUT WE REMOVE THE INGRID QUERY from state -> leads to empty result portlet,
        // empty similar portlet etc.
        String queryInRequest = request.getParameter(Settings.PARAM_QUERY_STRING);
        if (!SearchState.adaptSearchStateIfNotNull(request, Settings.PARAM_QUERY_STRING, queryInRequest)) {
            SearchState.resetSearchStateObject(request, Settings.MSG_QUERY);
        }

        // NOTICE: if no datasource in request WE KEEP THE OLD ONE IN THE STATE, so it is
        // displayed (but not bookmarkable)
        SearchState.adaptSearchStateIfNotNull(request, Settings.PARAM_DATASOURCE, request
                .getParameter(Settings.PARAM_DATASOURCE));

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
        // NOTICE: we use get method without instantiation, so we can do special initialisation !
        SearchSimpleForm af = (SearchSimpleForm) Utils.getActionForm(request, SearchSimpleForm.SESSION_KEY,
                PortletSession.PORTLET_SCOPE);
        if (af == null) {
            af = (SearchSimpleForm) Utils.addActionForm(request, SearchSimpleForm.SESSION_KEY, SearchSimpleForm.class,
                    PortletSession.PORTLET_SCOPE);
            af.setINITIAL_QUERY(messages.getString("searchSimple.query.initial"));
            af.init();
        }
        // NOTICE: this may be former query, if no query in request ! we keep that one for convenience
        // (although this state is not bookmarkable !)
        String queryString = SearchState.getSearchStateObjectAsString(request, Settings.PARAM_QUERY_STRING);
        af.setInput(SearchSimpleForm.FIELD_QUERY, queryString);
        // put ActionForm to context. use variable name "actionForm" so velocity macros work !
        context.put("actionForm", af);

        // ----------------------------------
        // prepare Search, Search will be performed in SearchResult portlet 
        // ----------------------------------
        // NOTICE: af.validate() also checks for default text (and sets default text if empty).
        boolean validInput = af.validate();
        boolean setUpNewQuery = true;
        // don't create IngridQuery when:
        // - only datasource was changed
        // - we have no query parameter in our URL (e.g. we entered from other page)
        // - the enterd query is empty or initial value
        if (action.equals(PARAMV_ACTION_NEW_DATASOURCE) || action.equals(PARAMV_ACTION_SEARCH_SETTINGS)
                || action.equals(PARAMV_ACTION_SEARCH_HISTORY) || action.equals(PARAMV_ACTION_SEARCH_EXTENDED)
                || queryInRequest == null || !validInput) {
            setUpNewQuery = false;
        }
        if (setUpNewQuery) {
            // set up Ingrid Query -> triggers search in result portlet
            setUpQuery(request, af.getInput(SearchSimpleForm.FIELD_QUERY));
        }

        // ----------------------------------
        // adapt title (passed from page)
        // NOTICE: this portlet is on different pages (default-page, main-search, search-settings ...)
        // title also depends on search state (query submitted or not ...)
        // ----------------------------------
        if (titleKey.equals(TITLE_KEY_RESULT)) {
            // we're on main search page, check whether there's a query and adapt title
            if (SearchState.getSearchStateObject(request, Settings.MSG_QUERY) == null) {
                titleKey = TITLE_KEY_SEARCH;
            }
        }
        response.setTitle(messages.getString(titleKey));

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

        // adapt SearchState (base for generating URL params):
        // - reset result pages if action in input fragment, start with first hits
        SearchState.adaptSearchState(request, Settings.PARAM_STARTHIT_RANKED, null);
        SearchState.adaptSearchState(request, Settings.PARAM_STARTHIT_UNRANKED, null);

        if (action.equalsIgnoreCase(Settings.PARAMV_ACTION_NEW_SEARCH)) {
            // adapt SearchState (base for generating URL params for render request):
            // - query string
            SearchState.adaptSearchState(request, Settings.PARAM_QUERY_STRING, request
                    .getParameter(Settings.PARAM_QUERY_STRING));

            // redirect to our page wih parameters for bookmarking
            actionResponse.sendRedirect(Settings.PAGE_SEARCH_RESULT + SearchState.getURLParamsMainSearch(request));

        } else if (action.equalsIgnoreCase(PARAMV_ACTION_NEW_DATASOURCE)) {
            // adapt SearchState (base for generating URL params for render request):
            // - set datasource
            SearchState.adaptSearchState(request, Settings.PARAM_DATASOURCE, request
                    .getParameter(Settings.PARAM_DATASOURCE));
            // don't populate action form, this is no submit, so no form parameters are in request !
            // TODO use JavaScript to submit form on datasource change ! then populate ActionForm, 
            // in that way we don't loose query changes on data source change, when changes weren't submitted before

            // redirect to MAIN page with parameters for bookmarking, ONLY IF WE'RE "ON MAIN PAGE"
            // Otherwise stay on same page without support for bookmarking !
            if (getDefaultViewPage().equals(TEMPLATE_SEARCH_SIMPLE)) {
                actionResponse.sendRedirect(Settings.PAGE_SEARCH_RESULT + SearchState.getURLParamsMainSearch(request));
            }

        } else if (action.equalsIgnoreCase(PARAMV_ACTION_SEARCH_SETTINGS)) {
            actionResponse.sendRedirect(PAGE_SEARCH_SETTINGS + SearchState.getURLParamsMainSearch(request));

        } else if (action.equalsIgnoreCase(PARAMV_ACTION_SEARCH_HISTORY)) {
            actionResponse.sendRedirect(PAGE_SEARCH_HISTORY + SearchState.getURLParamsMainSearch(request));

        } else if (action.equalsIgnoreCase(PARAMV_ACTION_SEARCH_EXTENDED)) {
            String currentDatasource = SearchState.getSearchStateObjectAsString(request, Settings.PARAM_DATASOURCE);
            if (currentDatasource.equals(Settings.PARAMV_DATASOURCE_ENVINFO)) {
                actionResponse.sendRedirect(Settings.PAGE_SEARCH_EXT_ENV_TOPIC_TERMS);
            } else if (currentDatasource.equals(Settings.PARAMV_DATASOURCE_ADDRESS)) {
                actionResponse.sendRedirect(Settings.PAGE_SEARCH_EXT_ADR_TOPIC_TERMS);
            } else if (currentDatasource.equals(Settings.PARAMV_DATASOURCE_RESEARCH)) {
                actionResponse.sendRedirect(Settings.PAGE_SEARCH_EXT_RES_TOPIC_TERMS);
            }
        }
    }

    private void setUpQuery(PortletRequest request, String queryString) {
        // Create IngridQuery
        IngridQuery query = null;
        queryString = queryString.toLowerCase();
        try {
            query = QueryStringParser.parse(queryString);
        } catch (ParseException ex) {
            if (log.isWarnEnabled()) {
                log.warn("Problems creating IngridQuery, parsed query string: " + queryString, ex);
            }
            // TODO create ERROR message when no IngridQuery because of parse error and return (OR even do that in form ???) 
        }

        // set query in state for result portlet
        SearchState.adaptSearchState(request, Settings.MSG_QUERY, query);
        SearchState.adaptSearchState(request, Settings.PARAM_QUERY_STRING, queryString);
    }
}
