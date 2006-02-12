/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.portals.bridges.velocity.AbstractVelocityMessagingPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.iplug.PlugDescription;
import de.ingrid.iplug.sns.utils.Topic;
import de.ingrid.portal.forms.SimpleSearchForm;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.interfaces.IBUSInterface;
import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;
import de.ingrid.portal.interfaces.impl.SNSInterfaceImpl;
import de.ingrid.portal.search.DisplayTreeFactory;
import de.ingrid.portal.search.DisplayTreeNode;
import de.ingrid.portal.search.PageState;
import de.ingrid.portal.search.SearchResultList;
import de.ingrid.portal.search.mockup.SearchResultListMockup;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.dsc.Column;
import de.ingrid.utils.dsc.Record;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.ParseException;
import de.ingrid.utils.queryparser.QueryStringParser;

/**
 * TODO Describe your created type (class, etc.) here.
 *
 * @author joachim@wemove.com
 */
public class SearchResultPortlet extends AbstractVelocityMessagingPortlet {

    private final static Log log = LogFactory.getLog(SearchResultPortlet.class);

    /** Keys of parameters in session/request */
    private final static String PARAM_START_HIT_RANKED = "rstart";

    private final static String PARAM_START_HIT_UNRANKED = "nrstart";

    /* (non-Javadoc)
     * @see javax.portlet.Portlet#init(javax.portlet.PortletConfig)
     */
    public void init(PortletConfig config) throws PortletException {
        // set our message "scope" for inter portlet messaging
        setTopic(Settings.MSG_TOPIC_SEARCH);

        super.init(config);
    }

    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);

        // TODO remove page state in future, when separate portlets
        // use messages and render parameters instead !!!
        PortletSession session = request.getPortletSession();
        PageState ps = (PageState) session.getAttribute("portlet_state");
        if (ps == null) {
            ps = new PageState(this.getClass().getName());
            ps = initPageState(ps);
            session.setAttribute("portlet_state", ps);
        }

        // BEGIN: "simple_search" Portlet
        // TODO: MERGE THIS WITH THE SimpleSearch"Teaser" portlet !!!!

        // NOTICE:
        // may be called directly from Start page, then this method has to be an action method !
        // When called from startPage, the action request parameter is "doSearch" !!! 

        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()));
        context.put("MESSAGES", messages);

        // get ActionForm, we use get method without instantiation, so we can do
        // special initialisation
        SimpleSearchForm af = (SimpleSearchForm) Utils.getActionForm(request, SimpleSearchForm.SESSION_KEY,
                SimpleSearchForm.class, PortletSession.APPLICATION_SCOPE);
        if (af == null) {
            af = (SimpleSearchForm) Utils.addActionForm(request, SimpleSearchForm.SESSION_KEY, SimpleSearchForm.class,
                    PortletSession.APPLICATION_SCOPE);
            // TODO when separate portlet, then read this from lokale !!!! we don't do this here because we
            // don't wanna add it to resource bundle of RESULT PORTLET !
            //            af.setINITIAL_QUERY(messages.getString("simpleSearch.query.initial"));
            af.setINITIAL_QUERY("Geben Sie hier Ihre Suchanfrage ein");
            af.init();
        }
        // put ActionForm to context. use variable name "actionForm" so velocity macros work !
        context.put("actionForm", af);

        // if action is "doSearch" page WAS CALLED FROM STARTPAGE and no action was triggered ! 
        // (then we are on the result psml page !!!)
        String action = request.getParameter("action");
        if (action != null && action.equalsIgnoreCase("doSearch")) {
            // all ActionStuff encapsulated in separate method !
            doSimpleSearchPortletActionStuff(request, af);
        }

        String selectedDS = (String) receiveRenderMessage(request, Settings.MSG_DATASOURCE);
        if (selectedDS == null) {
            selectedDS = Settings.SEARCH_INITIAL_DATASOURCE;
            publishRenderMessage(request, Settings.MSG_DATASOURCE, selectedDS);
        }
        context.put("ds", selectedDS);
        // END: "simple_search" Portlet

        // BEGIN: search_result Portlet
        // if no query display "nothing"
        IngridQuery query = (IngridQuery) receiveRenderMessage(request, Settings.MSG_QUERY);
        String queryString = (String) receiveRenderMessage(request, Settings.MSG_QUERY_STRING);
        if (query == null) {
            //            setDefaultViewPage(TEMPLATE_NO_QUERY);
            //            super.doView(request, response);
            //            return;
        }

        // if query assume we have results
        //        setDefaultViewPage(TEMPLATE_RESULT);

        // no "if" clause necessary when separate portlet, then we return above !
        if (query != null) {

            // ----------------------------------
            // fetch data
            // ----------------------------------
            int rankedStartHit = 0;
            int unrankedStartHit = 0;

            // indicates whether a new query was performed !
            Object newQuery = receiveRenderMessage(request, Settings.MSG_NEW_QUERY);
            try {
                // if no new query was performed, read render parameters from former action request
                if (newQuery == null) {
                    String reqParam = request.getParameter(PARAM_START_HIT_RANKED);
                    if (reqParam != null) {
                        rankedStartHit = (new Integer(reqParam)).intValue();
                    }
                    reqParam = request.getParameter(PARAM_START_HIT_UNRANKED);
                    if (reqParam != null) {
                        unrankedStartHit = (new Integer(reqParam)).intValue();
                    }
                }
            } catch (Exception ex) {
                if (log.isErrorEnabled()) {
                    log.error("Problems parsing starthit (ranked or unranked) from render request, set starthit to 0");
                }
            }

            /*String*/selectedDS = (String) receiveRenderMessage(request, Settings.MSG_DATASOURCE);
            if (selectedDS == null) {
                selectedDS = Settings.SEARCH_INITIAL_DATASOURCE;
            }

            // ----------------------------------
            // business logic
            // ----------------------------------

            // ranked results	
            int RANKED_HITS_PER_PAGE = Settings.SEARCH_RANKED_HITS_PER_PAGE;
            IngridHits rankedHits = null;
            int numberOfHits = 0;
            try {
                rankedHits = doRankedSearch(query, selectedDS, rankedStartHit, RANKED_HITS_PER_PAGE);
                numberOfHits = (int) rankedHits.length();
            } catch (Exception ex) {
                if (log.isInfoEnabled()) {
                    log
                            .info("Problems fetching ranked hits ! hits = " + rankedHits + ", numHits = "
                                    + numberOfHits, ex);
                }
            }

            // adapt settings of ranked page navigation
            HashMap rankedPageNavigation = Utils.getPageNavigation(rankedStartHit, RANKED_HITS_PER_PAGE, numberOfHits,
                    Settings.SEARCH_RANKED_NUM_PAGES_TO_SELECT);

            // unranked results	
            int UNRANKED_HITS_PER_PAGE = Settings.SEARCH_UNRANKED_HITS_PER_PAGE;
            SearchResultList unrankedSRL = doUnrankedSearch(query, selectedDS, unrankedStartHit, UNRANKED_HITS_PER_PAGE);
            numberOfHits = unrankedSRL.getNumberOfHits();

            // adapt settings of unranked page navigation
            HashMap unrankedPageNavigation = Utils.getPageNavigation(unrankedStartHit, UNRANKED_HITS_PER_PAGE,
                    numberOfHits, Settings.SEARCH_UNRANKED_NUM_PAGES_TO_SELECT);

            // ----------------------------------
            // prepare view
            // ----------------------------------

            context.put("rankedPageSelector", rankedPageNavigation);
            context.put("unrankedPageSelector", unrankedPageNavigation);
            context.put("rankedResultList", rankedHits);
            context.put("unrankedResultList", unrankedSRL);
            // query string will be displayed when no results !
            context.put("queryString", queryString);
        }
        // END: search_result Portlet

        context.put("ps", ps);

        super.doView(request, response);
    }

    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {
        PortletSession session = request.getPortletSession();
        PageState ps = (PageState) session.getAttribute("portlet_state");
        if (ps == null) {
            ps = new PageState(this.getClass().getName());
            ps = initPageState(ps);
            session.setAttribute("portlet_state", ps);
        }

        // BEGIN: "simple_search" Portlet
        // TODO: MERGE THIS WITH THE SimpleSearch"Teaser" portlet !!!!
        String action = request.getParameter("action");
        if (action == null) {
            action = "";
        }
        if (action.equalsIgnoreCase("doSearch")) {

            // check form input
            SimpleSearchForm af = (SimpleSearchForm) Utils.getActionForm(request, SimpleSearchForm.SESSION_KEY,
                    SimpleSearchForm.class, PortletSession.APPLICATION_SCOPE);
            // encapsulate all ActionStuff in separate method, has to be called in view method too (when called
            // from start page !)
            doSimpleSearchPortletActionStuff(request, af);

            // when separate portlets: just send a message that a new query was executed and do this in the
            // search_result_similar Portlet
            ps.putBoolean("isSimilarOpen", false);
            ps.put("similarRoot", null);
            session.removeAttribute("similarRoot");

        } else if (action.equalsIgnoreCase("doChangeDS")) {
            publishRenderMessage(request, Settings.MSG_DATASOURCE, request.getParameter("ds"));
        }
        // END: "simple_search" Portlet

        // BEGIN: search_result_similar Portlet
        DisplayTreeNode similarRoot = null;
        if (action.equalsIgnoreCase("doOpenSimilar")) {
            ps.putBoolean("isSimilarOpen", true);
            similarRoot = (DisplayTreeNode)session.getAttribute("similarRoot");
            if (similarRoot == null) {
                IngridQuery query = (IngridQuery) receiveRenderMessage(request, Settings.MSG_QUERY);
                similarRoot = DisplayTreeFactory.getTree(query);
                session.setAttribute("similarRoot", similarRoot);
            }
            ps.put("similarRoot", similarRoot);
        } else if (action.equalsIgnoreCase("doCloseSimilar")) {
            ps.putBoolean("isSimilarOpen", false);
            ps.put("similarRoot", null);
        } else if (action.equalsIgnoreCase("doOpenNode")) {
            similarRoot = (DisplayTreeNode)session.getAttribute("similarRoot");
            if (similarRoot != null) {
                DisplayTreeNode node = similarRoot.getChild(request.getParameter("nodeId"));
                node.setOpen(true);
                if (node != null && node.getType() == DisplayTreeNode.SEARCH_TERM) {
                    IngridHit[] hits = SNSInterfaceImpl.getInstance().getSimilarTerms(node.getName());
                    for (int i=0; i<hits.length; i++) {
                        Topic hit = (Topic) hits[i];
                        if (!hit.getTopicName().equalsIgnoreCase(node.getName())) {
                            DisplayTreeNode snsNode = new DisplayTreeNode(node.getId()+i, hit.getTopicName(), false);
                            snsNode.setType(DisplayTreeNode.SNS_TERM);
                            snsNode.setParent(node);
                            node.addChild(snsNode);
                        }
                    }
                }
                ps.put("similarRoot", similarRoot);
            }
        } else if (action.equalsIgnoreCase("doCloseNode")) {
            similarRoot = (DisplayTreeNode)session.getAttribute("similarRoot");
            if (similarRoot != null) {
                DisplayTreeNode node = similarRoot.getChild(request.getParameter("nodeId"));
                if (node != null) {
                    node.setOpen(false);
                }
            }
        } else if (action.equalsIgnoreCase("doAddSimilar")) {
            // TODO add snsTerms to the query term with operator OR
            // the query object is still a mystery
            
/*            similarRoot = (DisplayTreeNode)session.getAttribute("similarRoot");
            if (similarRoot != null) {
                IngridQuery query = (IngridQuery) receiveRenderMessage(request, Settings.MSG_QUERY);
                ArrayList nodes = similarRoot.getAllChildren();
                Iterator it = nodes.iterator();
                while (it.hasNext()) {
                    DisplayTreeNode node = (DisplayTreeNode) it.next();
                    if (request.getParameter("chk_" + node.getId()) != null) {
                        boolean isInQuery = false;
                        TermQuery[] terms = query.getTerms();
                        for (int i=0; i<terms.length; i++) {
                            if (terms[i].getTerm().equalsIgnoreCase(node.getName()) && terms[i].getType() == TermQuery.TERM) {
                                isInQuery = true;
                                break;
                            }
                        }
                        if (!isInQuery) {
                            DisplayTreeNode parent = node.getParent();
                            for (int i=0; i<terms.length; i++) {
                                if (terms[i].getTerm().equalsIgnoreCase(parent.getName()) && terms[i].getType() == TermQuery.TERM) {
                                    terms[i].addClause(new ClauseQuery(true, false));
                                    terms[i].addTerm(new TermQuery(true, false, node.getName()));
                                    break;
                                }
                            }
                        }
                        System.out.println(query.toString());
                    } else if (node.getDepth() == 2) {
                        node.setOpen(false);
                    }
                }
            }
            */
        }
        // END: search_result_similar Portlet

        // BEGIN: search_result Portlet
        // ----------------------------------
        // fetch parameters
        // ----------------------------------
        String rankedStarthit = request.getParameter(PARAM_START_HIT_RANKED);
        String unrankedStarthit = request.getParameter(PARAM_START_HIT_UNRANKED);

        // ----------------------------------
        // business logic
        // ----------------------------------
        // no new query anymore, we remove messages, so portlets read our render parameters (set below)
        cancelRenderMessage(request, Settings.MSG_NEW_QUERY);
        cancelRenderMessage(request, Settings.MSG_NEW_QUERY_FOR_SIMILAR);

        // ----------------------------------
        // set render parameters
        // ----------------------------------
        if (rankedStarthit != null) {
            actionResponse.setRenderParameter(PARAM_START_HIT_RANKED, rankedStarthit);
        }
        if (unrankedStarthit != null) {
            actionResponse.setRenderParameter(PARAM_START_HIT_UNRANKED, unrankedStarthit);
        }
        // END: search_result Portlet
    }

    private IngridHits doRankedSearch(IngridQuery query, String ds, int startHit, int hitsPerPage) {

        int currentPage = (int) (startHit / hitsPerPage) + 1;

        IngridHits hits = null;
        try {
            IBUSInterface ibus = IBUSInterfaceImpl.getInstance();
            hits = ibus.search(query, hitsPerPage, currentPage, hitsPerPage, Settings.SEARCH_DEFAULT_TIMEOUT);
            IngridHit[] results = hits.getHits();

            IngridHit result = null;
            IngridHitDetail detail = null;
            PlugDescription plug = null;
            for (int i = 0; i < results.length; i++) {
                result = results[i];
                detail = ibus.getDetails(result, query);
                plug = ibus.getIPlug(result);

                if (result == null) {
                    continue;
                }
                if (detail != null) {
                    ibus.transferHitDetails(result, detail);
                }
                if (plug != null) {
                    ibus.transferPlugDetails(result, plug);
                    if (plug.getIPlugClass().equals("de.ingrid.iplug.dsc.index.DSCSearcher")) {
                        result.put(Settings.RESULT_KEY_TYPE, "dsc");

                        Record record = ibus.getRecord(result);
                        Column c = ibus.getColumn(record, Settings.HIT_KEY_WMS_URL);
                        if (c != null) {
                            result.put(Settings.RESULT_KEY_WMS_URL, URLEncoder.encode(record.getValueAsString(c),
                                    "UTF-8"));
                        }
                        c = ibus.getColumn(record, Settings.HIT_KEY_UDK_CLASS);
                        if (c != null) {
                            result.put(Settings.RESULT_KEY_UDK_CLASS, record.getValueAsString(c));
                        }

                    } else if (plug.getIPlugClass().equals("de.ingrid.iplug.se.NutchSearcher")) {
                        result.put(Settings.RESULT_KEY_TYPE, "nutch");
                    } else {
                        result.put(Settings.RESULT_KEY_TYPE, "unknown");
                    }
                }
            }
        } catch (Throwable t) {
            if (log.isErrorEnabled()) {
                log.error("Problems performing Search !", t);
            }
        }

        return hits;
    }

    private SearchResultList doUnrankedSearch(IngridQuery query, String ds, int startHit, int hitsPerPage) {

        SearchResultList result = new SearchResultList();

        SearchResultList l = SearchResultListMockup.getUnrankedSearchResultList();
        for (int i = 0; i < hitsPerPage; i++) {
            if (i >= l.size())
                break;
            result.add(l.get(i));
        }
        result.setNumberOfHits(l.getNumberOfHits());
        return result;
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
        String queryString = af.getInput(af.FIELD_QUERY);
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

    private PageState initPageState(PageState ps) {
        ps.put("query", null);
        ps.putBoolean("isSimilarOpen", false);
        ps.put("similarRoot", null);
        return ps;
    }
}