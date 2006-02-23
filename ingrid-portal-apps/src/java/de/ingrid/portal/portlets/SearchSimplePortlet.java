package de.ingrid.portal.portlets;

import java.io.IOException;
import java.util.ResourceBundle;

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
        ResourceBundle messages = getPortletConfig().getResourceBundle(request.getLocale());
        context.put("MESSAGES", messages);

        // read preferences and adapt title (passed from page)
        // NOTICE: this portlet is on start and main search page, may have different titles
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

        // get ActionForm, we use get method without instantiation, so we can do
        // special initialisation !
        SearchSimpleForm af = (SearchSimpleForm) Utils.getActionForm(request, SearchSimpleForm.SESSION_KEY,
                PortletSession.APPLICATION_SCOPE);
        if (af == null) {
            af = (SearchSimpleForm) Utils.addActionForm(request, SearchSimpleForm.SESSION_KEY, SearchSimpleForm.class,
                    PortletSession.APPLICATION_SCOPE);
            af.setINITIAL_QUERY(messages.getString("searchSimple.query.initial"));
            af.init();
        }
        // check for passed render parameters (the query string which can be bookmarked) and
        // adapt our message (should be the same anyway !)
        String queryInRequest = request.getParameter(SearchSimpleForm.FIELD_QUERY);
        if (log.isInfoEnabled()) {
            log.info("Query passed as REQUEST PARAMETER, " + SearchSimpleForm.FIELD_QUERY + "=" + queryInRequest);
        }
        if (queryInRequest != null) {
            af.setInput(SearchSimpleForm.FIELD_QUERY, queryInRequest.trim());
        }
        // put ActionForm to context. use variable name "actionForm" so velocity
        // macros work !
        context.put("actionForm", af);

        String selectedDS = (String) receiveRenderMessage(request, Settings.MSG_DATASOURCE);
        if (selectedDS == null) {
            selectedDS = Settings.SEARCH_INITIAL_DATASOURCE;
            publishRenderMessage(request, Settings.MSG_DATASOURCE, selectedDS);
        }
        context.put("ds", selectedDS);

        String searchAction = response.createActionURL().toString();
        if (startPage) {
            searchAction = SEARCH_RESULT_PAGE;
        }
        context.put("formAction", searchAction);

        // if action is "doSearch" we should perform a search !!! 
        String action = request.getParameter(PARAM_ACTION);
        if (action != null && action.equalsIgnoreCase(PARAM_ACTION_SEARCH)) {
            // set up all the stuff for the search result portlet
            prepareSearch(request, af);
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
            SearchSimpleForm af = (SearchSimpleForm) Utils.getActionForm(request, SearchSimpleForm.SESSION_KEY,
                    SearchSimpleForm.class, PortletSession.APPLICATION_SCOPE);

            // set up all the stuff for the search result portlet
            prepareSearch(request, af);

            // pass render parameters, so bookmarking works
            String newQueryString = af.getInput(SearchSimpleForm.FIELD_QUERY);
            actionResponse.setRenderParameter(SearchSimpleForm.FIELD_QUERY, newQueryString);

        } else if (action.equalsIgnoreCase("doChangeDS")) {
            publishRenderMessage(request, Settings.MSG_DATASOURCE, request.getParameter(PARAM_DATASOURCE));
            // do not requery on datasource change
            publishRenderMessage(request, Settings.MSG_NO_QUERY, Settings.MSG_VALUE_TRUE);
            // don't populate action form, this is no submit, so no form parameters are in request !
            // TODO use JavaScript to submit form on datasource change ! then populate ActionForm, 
            // in that way we don't loose query changes on data source change, when changes weren't submitted before
        }
    }

    private void prepareSearch(PortletRequest request, SearchSimpleForm af) {
        // remove old query message
        cancelRenderMessage(request, Settings.MSG_QUERY);
        cancelRenderMessage(request, Settings.MSG_QUERY_STRING);
        // remove "ibus query disable"
        cancelRenderMessage(request, Settings.MSG_NO_QUERY);
        // set messages that a new query was performed ! set separate messages for every portlet, because every
        // portlet removes it's message (we don't know processing order) !
        publishRenderMessage(request, Settings.MSG_NEW_QUERY, Settings.MSG_VALUE_TRUE);
        publishRenderMessage(request, Settings.MSG_NEW_QUERY_FOR_SIMILAR, Settings.MSG_VALUE_TRUE);

        // validate input
        // NOTICE: resets input to default value if validation fails (INITIAL_QUERY)
        af.populate(request);
        if (!af.validate()) {
            return;
        }

        // check query input
        String queryString = af.getInput(SearchSimpleForm.FIELD_QUERY);
        if (queryString.equals(af.getINITIAL_QUERY()) || queryString.length() == 0) {
            return;
        }

        // Create IngridQuery from form input !
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
