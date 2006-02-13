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

import de.ingrid.portal.forms.SimpleSearchForm;
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
public class SimpleSearchPortlet extends AbstractVelocityMessagingPortlet {

    private final static Log log = LogFactory.getLog(SimpleSearchPortlet.class);

    /** keys of possible titles, can be set via PSML portlet preferences */
    private final static String SEARCH_RESULT_PAGE = "portal/main-search.psml";

    /** keys of possible titles, can be set via PSML portlet preferences */
    private final static String TITLE_KEY_SEARCH = "simpleSearch.title.search";
    private final static String TITLE_KEY_RESULT = "simpleSearch.title.result";

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
        PortletPreferences prefs = request.getPreferences();
        String titleKey = prefs.getValue("titleKey", TITLE_KEY_SEARCH);
        boolean startPage = true;
        if (titleKey.equals(TITLE_KEY_RESULT)) {
            // we're on main page, check whether there's a query and adapt title
            startPage = false;
            if (receiveRenderMessage(request, Settings.MSG_QUERY) == null) {
                titleKey = TITLE_KEY_SEARCH;
            }
        }
        response.setTitle(messages.getString(titleKey));

        // get ActionForm, we use get method without instantiation, so we can do
        // special initialisation !
        SimpleSearchForm af = (SimpleSearchForm) Utils.getActionForm(request, SimpleSearchForm.SESSION_KEY,
                PortletSession.APPLICATION_SCOPE);
        if (af == null) {
            af = (SimpleSearchForm) Utils.addActionForm(request, SimpleSearchForm.SESSION_KEY, SimpleSearchForm.class,
                    PortletSession.APPLICATION_SCOPE);
            af.setINITIAL_QUERY(messages.getString("simpleSearch.query.initial"));
            af.init();
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

        // if action is "doSearch" page WAS CALLED FROM STARTPAGE and no action was triggered ! 
        // (then we are on the result psml page !!!)
        String action = request.getParameter("action");
        if (action != null && action.equalsIgnoreCase("doSearch")) {
            // all ActionStuff encapsulated in separate method !
            doSimpleSearchPortletActionStuff(request, af);
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
        String action = request.getParameter("action");
        if (action == null) {
            return;
        }

        if (action.equalsIgnoreCase("doSearch")) {
            // encapsulate all ActionStuff in separate method, has to be called in view method too (when called
            // from start page !)
            SimpleSearchForm af = (SimpleSearchForm) Utils.getActionForm(request, SimpleSearchForm.SESSION_KEY,
                    SimpleSearchForm.class, PortletSession.APPLICATION_SCOPE);
            doSimpleSearchPortletActionStuff(request, af);

        } else if (action.equalsIgnoreCase("doChangeDS")) {
            publishRenderMessage(request, Settings.MSG_DATASOURCE, request.getParameter("ds"));
            // don't populate action form, this is no submit, so no form parameters are in request !
            // TODO use JavaScript to submit form on datasource change ! then use ActionForm like below !
        }
    }

    private void doSimpleSearchPortletActionStuff(PortletRequest request, SimpleSearchForm af) {
        // remove old query message
        cancelRenderMessage(request, Settings.MSG_QUERY);
        cancelRenderMessage(request, Settings.MSG_QUERY_STRING);
        // set messages that a new query was performed ! set separate messages for every portlet, because every
        // portlet removes it's message (we don't know processing order) !
        publishRenderMessage(request, Settings.MSG_NEW_QUERY, Settings.MSG_VALUE_TRUE);
        publishRenderMessage(request, Settings.MSG_NEW_QUERY_FOR_SIMILAR, Settings.MSG_VALUE_TRUE);

        af.populate(request);
        if (!af.validate()) {
            return;
        }

        // check query input
        String queryString = af.getInput(SimpleSearchForm.FIELD_QUERY);
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
