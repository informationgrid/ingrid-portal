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
import de.ingrid.portal.search.UtilsSearch;
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
        // ADAPT OUR PERMANENT STATE VIA MESSAGES
        // ----------------------------------
        String action = request.getParameter(UtilsSearch.PARAM_ACTION);
        if (action != null && action.equals(UtilsSearch.PARAM_ACTION_NEW_SEARCH)) {
            // reset relevant search stuff, we perform a new one !
            UtilsSearch.resetSearchState(request);
            publishRenderMessage(request, Settings.MSG_NEW_QUERY, Settings.MSG_VALUE_TRUE);
        }
        String queryInRequest = request.getParameter(UtilsSearch.PARAM_QUERY);
        if (queryInRequest != null) {
            publishRenderMessage(request, Settings.MSG_QUERY_STRING, queryInRequest);
        } else {
            // NOTICE: WE REMOVE THE QUERY ! this leads to empty result portlet, empty similar portlet etc.
            cancelRenderMessage(request, Settings.MSG_QUERY_STRING);
            cancelRenderMessage(request, Settings.MSG_QUERY);
        }
        String dsInRequest = request.getParameter(UtilsSearch.PARAM_DATASOURCE);
        if (dsInRequest != null) {
            publishRenderMessage(request, Settings.MSG_DATASOURCE, dsInRequest);
        }

        // ----------------------------------
        // read PREFERENCES and adapt title (passed from page)
        // NOTICE: this portlet is on start and main search page, may have different titles
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

        // ----------------------------------
        // set data for view template 
        // ----------------------------------

        // set datasource
        String selectedDS = (String) receiveRenderMessage(request, Settings.MSG_DATASOURCE);
        if (selectedDS == null) {
            selectedDS = Settings.SEARCH_INITIAL_DATASOURCE;
            publishRenderMessage(request, Settings.MSG_DATASOURCE, selectedDS);
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
        String queryString = (String) receiveRenderMessage(request, Settings.MSG_QUERY_STRING);
        af.setInput(SearchSimpleForm.FIELD_QUERY, queryString);
        // put ActionForm to context. use variable name "actionForm" so velocity macros work !
        context.put("actionForm", af);

        // ----------------------------------
        // prepare Search, Search will be performed in SearchResult portlet 
        // ----------------------------------
        // NOTICE: af.validate() checks for default text and only returns true if "valid" query was entered
        if (af.validate()) {
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
        String action = request.getParameter(UtilsSearch.PARAM_ACTION);
        if (action == null) {
            return;
        }

        if (action.equalsIgnoreCase(UtilsSearch.PARAM_ACTION_NEW_SEARCH)) {
            // redirect to our page wih parameters for bookmarking
            actionResponse.sendRedirect(UtilsSearch.PAGE_SEARCH_RESULT + UtilsSearch.getURLParams(request));

        } else if (action.equalsIgnoreCase("doChangeDS")) {
            // redirect to our page wih parameters for bookmarking
            actionResponse.sendRedirect(UtilsSearch.PAGE_SEARCH_RESULT + UtilsSearch.getURLParams(request));

            // don't populate action form, this is no submit, so no form parameters are in request !
            // TODO use JavaScript to submit form on datasource change ! then populate ActionForm, 
            // in that way we don't loose query changes on data source change, when changes weren't submitted before
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

        // set query in message for result portlet
        if (query != null) {
            publishRenderMessage(request, Settings.MSG_QUERY, query);
            publishRenderMessage(request, Settings.MSG_QUERY_STRING, queryString);
        }
    }
}
