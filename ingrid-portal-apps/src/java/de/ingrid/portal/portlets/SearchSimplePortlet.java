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

    /** page for displaying results, is called directly from start page */
    private final static String SEARCH_RESULT_PAGE = "portal/main-search.psml";

    /** keys of possible titles, can be set via PSML portlet preferences */
    private final static String TITLE_KEY_SEARCH = "searchSimple.title.search";

    private final static String TITLE_KEY_RESULT = "searchSimple.title.result";

    /** Keys of parameters in session/request */
    private final static String PARAM_DATASOURCE = "ds";

    private final static String PARAM_QUERY = SearchSimpleForm.FIELD_QUERY;

    private final static String PARAM_ACTION = "action";

    /** Values of parameters in session/request */
    private final static String PARAM_ACTION_SEARCH = "doSearch";

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
        // read PREFERENCES and adapt title (passed from page)
        // NOTICE: this portlet is on start and main search page, may have different titles
        // ----------------------------------
        PortletPreferences prefs = request.getPreferences();
        String titleKey = prefs.getValue("titleKey", TITLE_KEY_SEARCH);
        boolean startPage = true;
        if (titleKey.equals(TITLE_KEY_RESULT)) {
            // we're on main search page, check whether there's a query and adapt title
            startPage = false;
            if (receiveRenderMessage(request, Settings.MSG_QUERY) == null) {
                titleKey = TITLE_KEY_SEARCH;
            }
        }
        response.setTitle(messages.getString(titleKey));

        // ----------------------------------
        // check for passed RENDER PARAMETERS (for bookmarking) and adapt our messages !
        // Render Parameters will be passed in every RenderRequest until next action method is called !
        // NOTICE: Here messages should be reset, if they're dependent from URL parameters !
        // e.g. SEARCH IS ONLY PERFORMED, IF THE QUERY IS PART OF THE URL !!!
        // ----------------------------------
        String queryInRequest = request.getParameter(PARAM_QUERY);
        if (queryInRequest != null) {
            publishRenderMessage(request, Settings.MSG_QUERY_STRING, queryInRequest);
        } else {
            // RESET Query, so no search is performed !
            cancelRenderMessage(request, Settings.MSG_QUERY_STRING);
            cancelRenderMessage(request, Settings.MSG_QUERY);
        }
        String dsInRequest = request.getParameter(PARAM_DATASOURCE);
        if (dsInRequest != null) {
            publishRenderMessage(request, Settings.MSG_DATASOURCE, dsInRequest);
        }
        boolean searchActionTriggered = false;
        String actionInRequest = request.getParameter(PARAM_ACTION);
        if (actionInRequest != null && actionInRequest.equalsIgnoreCase(PARAM_ACTION_SEARCH)) {
            // search was submitted by start page 
            searchActionTriggered = true;
        } else if (receiveRenderMessage(request, Settings.MSG_NEW_QUERY) != null) {
            // search was submitted by main-search page 
            searchActionTriggered = true;
        }

        if (log.isInfoEnabled()) {
            log.info("Query passed as REQUEST PARAMETER, " + PARAM_QUERY + "=" + queryInRequest);
            log.info("Search Area passed as REQUEST PARAMETER, " + PARAM_DATASOURCE + "=" + dsInRequest);
            log.info("action passed as REQUEST PARAMETER, " + PARAM_ACTION + "=" + actionInRequest);
            log.info("search explicitly triggered = " + searchActionTriggered);
        }

        // ----------------------------------
        // set data for view template 
        // ----------------------------------

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
        // just to be sure we synchronize action form with message :)
        if (receiveRenderMessage(request, Settings.MSG_QUERY_STRING) != null) {
            af.setInput(SearchSimpleForm.FIELD_QUERY,
                    ((String) receiveRenderMessage(request, Settings.MSG_QUERY_STRING)));
        }
        // put ActionForm to context. use variable name "actionForm" so velocity
        // macros work !
        context.put("actionForm", af);

        // set datasource
        String selectedDS = (String) receiveRenderMessage(request, Settings.MSG_DATASOURCE);
        if (selectedDS == null) {
            selectedDS = Settings.SEARCH_INITIAL_DATASOURCE;
            publishRenderMessage(request, Settings.MSG_DATASOURCE, selectedDS);
        }
        context.put("ds", selectedDS);

        // set form action for template
        String searchAction = response.createActionURL().toString();
        if (startPage) {
            searchAction = SEARCH_RESULT_PAGE;
        }
        context.put("formAction", searchAction);

        // ----------------------------------
        // prepare Search if necessary, Search will be performed in SearchResult portlet 
        // ----------------------------------
        boolean newIngridQuery = true;
        
        // if not explicitly triggered, check whether new Query is necessary
        if (searchActionTriggered) {
            // set messages that a brand new query is performed !
            publishRenderMessage(request, Settings.MSG_NEW_QUERY, Settings.MSG_VALUE_TRUE);
        } else {
            if (queryInRequest == null) {
                newIngridQuery = false;
            } else if (receiveRenderMessage(request, Settings.MSG_NO_QUERY) != null) {
                // something on result page was clicked, that shouldn't perform a search, e.g. similar portlet !
                newIngridQuery = false;
            } else if (receiveRenderMessage(request, Settings.MSG_OLD_QUERY) != null) {
                // something on result page was clicked, that shouldn't set up a new Query, e.g. next result page !
                newIngridQuery = false;
            }
        }

        if (newIngridQuery) {
            // set up new Ingrid Query !
            doNewIngridQuery(request, af);
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
        String action = request.getParameter(PARAM_ACTION);
        if (action == null) {
            return;
        }

        if (action.equalsIgnoreCase(PARAM_ACTION_SEARCH)) {
            // indicate, that we have to perform a brand new query !
            publishRenderMessage(request, Settings.MSG_NEW_QUERY, Settings.MSG_VALUE_TRUE);

            // pass all search parameters as render parameters, so bookmarking works
            String param = request.getParameter(PARAM_QUERY);
            actionResponse.setRenderParameter(PARAM_QUERY, param);
            // datasource was set in other action, we get this one via messaging, see below
            param = (String) receiveRenderMessage(request, Settings.MSG_DATASOURCE);
            actionResponse.setRenderParameter(PARAM_DATASOURCE, param);

        } else if (action.equalsIgnoreCase("doChangeDS")) {
            publishRenderMessage(request, Settings.MSG_DATASOURCE, request.getParameter(PARAM_DATASOURCE));
            // do not requery on datasource change
            publishRenderMessage(request, Settings.MSG_NO_QUERY, Settings.MSG_VALUE_TRUE);
            // don't populate action form, this is no submit, so no form parameters are in request !
            // TODO use JavaScript to submit form on datasource change ! then populate ActionForm, 
            // in that way we don't loose query changes on data source change, when changes weren't submitted before
        }
    }

    private void doNewIngridQuery(PortletRequest request, SearchSimpleForm af) {
        // remove old query message
        cancelRenderMessage(request, Settings.MSG_QUERY);
        cancelRenderMessage(request, Settings.MSG_QUERY_STRING);

        // check query input
        if (!af.validate()) {
            return;
        }

        // Create IngridQuery from form input !
        String queryString = af.getInput(SearchSimpleForm.FIELD_QUERY);
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

        // set query in message for result portlet
        if (query != null) {
            publishRenderMessage(request, Settings.MSG_QUERY, query);
            publishRenderMessage(request, Settings.MSG_QUERY_STRING, queryString);
        }
    }
}
