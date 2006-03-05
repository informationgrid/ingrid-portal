package de.ingrid.portal.portlets;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.PortletURL;

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

    /** keys of possible titles, can be set via PSML portlet preferences */
    private final static String TITLE_KEY_SEARCH = "searchSimple.title.search";

    private final static String TITLE_KEY_RESULT = "searchSimple.title.result";

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
        // check for passed RENDER PARAMETERS (for bookmarking) and
        // ADAPT OUR PERMANENT STATE (MESSAGES)
        // ----------------------------------
        String action = request.getParameter(Settings.PARAM_ACTION);
        if (action != null) {
            if (action.equals(Settings.PARAMV_ACTION_NEW_SEARCH)
                    || action.equals(Settings.PARAMV_ACTION_NEW_DATASOURCE)) {
                // reset relevant search stuff, we perform a new one !
                SearchState.resetSearchState(request);
                publishRenderMessage(request, Settings.MSG_QUERY_EXECUTION_TYPE, Settings.MSGV_NEW_QUERY);
            }
        } else {
            action = "";
        }
        String queryInRequest = request.getParameter(Settings.PARAM_QUERY_STRING);
        if (queryInRequest != null) {
            publishRenderMessage(request, Settings.PARAM_QUERY_STRING, queryInRequest);
        } else {
            // NOTICE: WE REMOVE THE QUERY ! this leads to empty result portlet, empty similar portlet etc.
            // BUT WE KEEP THE OLD QUERY STRING IN THE STATE (message), so it is displayed (for convenience,
            // although not bookmarkable)
            cancelRenderMessage(request, Settings.MSG_QUERY);
        }
        String dsInRequest = request.getParameter(Settings.PARAM_DATASOURCE);
        // NOTICE: if no datasource in request WE KEEP THE OLD ONE IN THE STATE (message), so it is
        // displayed (for convenience, although not bookmarkable)
        if (dsInRequest != null) {
            publishRenderMessage(request, Settings.PARAM_DATASOURCE, dsInRequest);
        }

        // ----------------------------------
        // set data for view template 
        // ----------------------------------

        // set datasource
        String selectedDS = (String) receiveRenderMessage(request, Settings.PARAM_DATASOURCE);
        if (selectedDS == null) {
            selectedDS = Settings.SEARCH_INITIAL_DATASOURCE;
            publishRenderMessage(request, Settings.PARAM_DATASOURCE, selectedDS);
        }
        context.put("ds", selectedDS);

        // set form submit action for template
        PortletURL searchAction = response.createActionURL();
        context.put("formAction", searchAction);

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
        String queryString = (String) receiveRenderMessage(request, Settings.PARAM_QUERY_STRING);
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
        if (action.equals(Settings.PARAMV_ACTION_NEW_DATASOURCE) || queryInRequest == null || !validInput) {
            setUpNewQuery = false;
        }
        if (setUpNewQuery) {
            // set up Ingrid Query !
            setUpQuery(request, af.getInput(SearchSimpleForm.FIELD_QUERY));
        }

        // ----------------------------------
        // read PREFERENCES and adapt title (passed from page)
        // NOTICE: this portlet is on start and main search page, may have different titles,
        // also depends on search state (query submitted or not ...)
        // ----------------------------------
        PortletPreferences prefs = request.getPreferences();
        String titleKey = prefs.getValue("titleKey", TITLE_KEY_SEARCH);
        if (titleKey.equals(TITLE_KEY_RESULT)) {
            // we're on main search page, check whether there's a query and adapt title
            if (receiveRenderMessage(request, Settings.MSG_QUERY) == null) {
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

        if (action.equalsIgnoreCase(Settings.PARAMV_ACTION_NEW_SEARCH)) {
            // adapt SearchState (base for generating URL params for render request):
            // - query string
            SearchState.adaptSearchState(request, Settings.PARAM_QUERY_STRING, request
                    .getParameter(Settings.PARAM_QUERY_STRING));

        } else if (action.equalsIgnoreCase(Settings.PARAMV_ACTION_NEW_DATASOURCE)) {
            // adapt SearchState (base for generating URL params for render request):
            // - set datasource
            SearchState.adaptSearchState(request, Settings.PARAM_DATASOURCE, request
                    .getParameter(Settings.PARAM_DATASOURCE));
            // don't populate action form, this is no submit, so no form parameters are in request !
            // TODO use JavaScript to submit form on datasource change ! then populate ActionForm, 
            // in that way we don't loose query changes on data source change, when changes weren't submitted before
        }

        // adapt SearchState (base for generating URL params for render request):
        // - reset result pages, start with first ones
        SearchState.adaptSearchState(request, Settings.PARAM_STARTHIT_RANKED, null);
        SearchState.adaptSearchState(request, Settings.PARAM_STARTHIT_UNRANKED, null);

        // redirect to our page wih parameters for bookmarking
        actionResponse.sendRedirect(Settings.PAGE_SEARCH_RESULT + SearchState.getURLParamsMainSearch(request));
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
            publishRenderMessage(request, Settings.PARAM_QUERY_STRING, queryString);
        }
    }
}
