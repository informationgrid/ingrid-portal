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
        // ----------------------------------
        String queryInRequest = request.getParameter(PARAM_QUERY);
        String dsInRequest = request.getParameter(PARAM_DATASOURCE);
        if (dsInRequest != null) {
            publishRenderMessage(request, Settings.MSG_DATASOURCE, dsInRequest);
        }
        String actionInRequest = request.getParameter(PARAM_ACTION);
        if (actionInRequest != null && actionInRequest.equalsIgnoreCase(PARAM_ACTION_SEARCH)) {
            // search was submitted by start page 
            publishRenderMessage(request, Settings.MSG_NEW_QUERY, Settings.MSG_VALUE_TRUE);
        }
        if (log.isInfoEnabled()) {
            log.info("Query passed as REQUEST PARAMETER, " + PARAM_QUERY + "=" + queryInRequest);
            log.info("Search Area passed as REQUEST PARAMETER, " + PARAM_DATASOURCE + "=" + dsInRequest);
            log.info("action passed as REQUEST PARAMETER, " + PARAM_ACTION + "=" + actionInRequest);
            log.info("NEW INITIAL QUERY = " + receiveRenderMessage(request, Settings.MSG_NEW_QUERY));
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
        // just to be sure we synchronize action form with query in request
        if (queryInRequest != null) {
            af.setInput(SearchSimpleForm.FIELD_QUERY, queryInRequest);
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
        resetQuery(request);

        // only query was passed per request and is valid set up IngridQuery
        // NOTICE: af.validate() checks for default text and only returns true if "valid" query was entered
        if (queryInRequest != null && af.validate()) {
            // set up Ingrid Query !
            setUpQuery(request, af.getInput(SearchSimpleForm.FIELD_QUERY));
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
            // indicate, that new search was triggered by form submit
            publishRenderMessage(request, Settings.MSG_NEW_QUERY, Settings.MSG_VALUE_TRUE);

            // pass all search parameters as render parameters, so bookmarking works
            String param = request.getParameter(PARAM_QUERY);
            actionResponse.setRenderParameter(PARAM_QUERY, param);
            // datasource was set in other action, we get this one via messaging, see below
            param = (String) receiveRenderMessage(request, Settings.MSG_DATASOURCE);
            actionResponse.setRenderParameter(PARAM_DATASOURCE, param);

        } else if (action.equalsIgnoreCase("doChangeDS")) {
            // NOTICE: all render parameters are reset ! we only publish new data source
            publishRenderMessage(request, Settings.MSG_DATASOURCE, request.getParameter(PARAM_DATASOURCE));
            // don't populate action form, this is no submit, so no form parameters are in request !
            // TODO use JavaScript to submit form on datasource change ! then populate ActionForm, 
            // in that way we don't loose query changes on data source change, when changes weren't submitted before
        }
    }

    private void resetQuery(PortletRequest request) {
        cancelRenderMessage(request, Settings.MSG_QUERY_STRING);
        cancelRenderMessage(request, Settings.MSG_QUERY);
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

        // set query in message for result portlet
        if (query != null) {
            publishRenderMessage(request, Settings.MSG_QUERY, query);
            publishRenderMessage(request, Settings.MSG_QUERY_STRING, queryString);
        }
    }
}
