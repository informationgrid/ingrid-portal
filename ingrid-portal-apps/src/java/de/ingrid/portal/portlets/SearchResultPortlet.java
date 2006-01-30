/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

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

import de.ingrid.portal.forms.SimpleSearchForm;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.search.PageState;
import de.ingrid.portal.search.SearchResultList;
import de.ingrid.portal.search.SimilarTreeNode;
import de.ingrid.portal.search.mockup.SearchResultListMockup;
import de.ingrid.portal.search.mockup.SimilarNodeFactoryMockup;
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
    private final static String KEY_START_HIT_RANKED = "rstart";

    private final static String KEY_START_HIT_UNRANKED = "nrstart";

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

        // initial state ! if no action has been processed before
        // TODO DO WE NEED THIS ???
        if (!ps.getBoolean("isActionProcessed")) {
            ps = initPageState(ps);
        }
        ps.setBoolean("isActionProcessed", false);

// BEGIN: "simple_search" Portlet
        // TODO: MERGE THIS WITH THE SimpleSearch"Teaser" portlet !!!!

        // NOTICE:
        // may be called directly from Start page, then this method has to be an action method !
        // When called from startPage, the action request parameter is "doSearch" !!! 

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
            selectedDS = Settings.INITIAL_DATASOURCE;
            publishRenderMessage(request, Settings.MSG_DATASOURCE, selectedDS);
        }
        context.put("ds", selectedDS);
// END: "simple_search" Portlet

// BEGIN: search_result Portlet
        // if no query display "nothing"
        IngridQuery query = (IngridQuery) receiveRenderMessage(request, Settings.MSG_QUERY);
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
                    rankedStartHit = (new Integer(request.getParameter(KEY_START_HIT_RANKED))).intValue();
                    unrankedStartHit = (new Integer(request.getParameter(KEY_START_HIT_UNRANKED))).intValue();
                }
            } catch (Exception ex) {
                if (log.isDebugEnabled()) {
                    log.debug("Problems fetching starthits of RESULT page from render request, set starthits to 0", ex);
                }
            }

            /*String*/ selectedDS = (String) receiveRenderMessage(request, Settings.MSG_DATASOURCE);
            if (selectedDS == null) {
                selectedDS = Settings.INITIAL_DATASOURCE;
            }

            // ----------------------------------
            // business logic
            // ----------------------------------

            // ranked results	
            int RANKED_HITS_PER_PAGE = Settings.RANKED_HITS_PER_PAGE;
            SearchResultList rankedSRL = doSearch(query, selectedDS, rankedStartHit, RANKED_HITS_PER_PAGE, true);
            int numberOfHits = rankedSRL.getNumberOfHits();

            // adapt settings of ranked page navihation
            HashMap rankedPageNavigation = Utils.getPageNavigation(rankedStartHit, RANKED_HITS_PER_PAGE, numberOfHits,
                    Settings.RANKED_NUM_PAGES_TO_SELECT);

            // unranked results	
            int UNRANKED_HITS_PER_PAGE = Settings.UNRANKED_HITS_PER_PAGE;
            SearchResultList unrankedSRL = doSearch(query, selectedDS, unrankedStartHit, UNRANKED_HITS_PER_PAGE, false);
            numberOfHits = unrankedSRL.getNumberOfHits();

            // adapt settings of unranked page navihation
            HashMap unrankedPageNavigation = Utils.getPageNavigation(unrankedStartHit, UNRANKED_HITS_PER_PAGE, numberOfHits,
                    Settings.UNRANKED_NUM_PAGES_TO_SELECT);

            // ----------------------------------
            // prepare view
            // ----------------------------------

            context.put("rankedPageSelector", rankedPageNavigation);
            context.put("unrankedPageSelector", unrankedPageNavigation);
            context.put("rankedResultList", rankedSRL);
            context.put("unrankedResultList", unrankedSRL);
        }
// END: search_result Portlet

        context.put("ps", ps);

        super.doView(request, response);
    }

    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {
        String action = request.getParameter("action");

        PortletSession session = request.getPortletSession();
        PageState ps = (PageState) session.getAttribute("portlet_state");
        if (ps == null) {
            ps = new PageState(this.getClass().getName());
            ps = initPageState(ps);
            session.setAttribute("portlet_state", ps);
        }

        if (action == null) {
            action = "";

// BEGIN: "simple_search" Portlet
            // TODO: MERGE THIS WITH THE SimpleSearch"Teaser" portlet !!!!

        } else if (action.equalsIgnoreCase("doSearch")) {

            // check form input
            SimpleSearchForm af = (SimpleSearchForm) Utils.getActionForm(request, SimpleSearchForm.SESSION_KEY,
                    SimpleSearchForm.class, PortletSession.APPLICATION_SCOPE);
            // encapsulate all ActionStuff in separate method, has to be called in view method too (when called
            // from start page !)
            doSimpleSearchPortletActionStuff(request, af);

            // when separate portlets: just send a message that a new query was executed and do this in the
            // search_result_similar Portlet
            ps.setBoolean("isSimilarOpen", false);
            ps.put("similarRoot", null);
            
        } else if (action.equalsIgnoreCase("doChangeDS")) {
            publishRenderMessage(request, Settings.MSG_DATASOURCE, request.getParameter("ds"));
// END: "simple_search" Portlet
            
// BEGIN: search_result_similar Portlet
        } else if (action.equalsIgnoreCase("doOpenSimilar")) {
            ps.setBoolean("isSimilarOpen", true);
            ps.put("similarRoot", SimilarNodeFactoryMockup.getSimilarNodes());
        } else if (action.equalsIgnoreCase("doCloseSimilar")) {
            ps.setBoolean("isSimilarOpen", false);
            ps.put("similarRoot", null);
        } else if (action.equalsIgnoreCase("doOpenNode")) {
            SimilarNodeFactoryMockup.setOpen((SimilarTreeNode) ps.get("similarRoot"), request.getParameter("nodeId"),
                    true);
        } else if (action.equalsIgnoreCase("doCloseNode")) {
            SimilarNodeFactoryMockup.setOpen((SimilarTreeNode) ps.get("similarRoot"), request.getParameter("nodeId"),
                    false);
        } else if (action.equalsIgnoreCase("doAddSimilar")) {
            ArrayList nodes = getNodeChildrenArray((SimilarTreeNode) ps.get("similarRoot"));
            Iterator it = nodes.iterator();
            StringBuffer sbQuery = new StringBuffer(ps.getString("query"));
            while (it.hasNext()) {
                SimilarTreeNode node = (SimilarTreeNode) it.next();
                if (request.getParameter("chk_" + node.getId()) != null && sbQuery.indexOf(node.getName()) == -1) {
                    sbQuery.append(" AND ").append(node.getName());
                } else if (node.getDepth() == 2) {
                    node.setOpen(false);
                }
            }
            ps.put("query", sbQuery.toString());
        }
// END: search_result_similar Portlet
        
// BEGIN: search_result Portlet
        // ----------------------------------
        // fetch parameters
        // ----------------------------------
        String rankedStarthit = request.getParameter(KEY_START_HIT_RANKED);
        String unrankedStarthit = request.getParameter(KEY_START_HIT_UNRANKED);

        // ----------------------------------
        // business logic
        // ----------------------------------
        // no new query anymore, we remove messages, so portlets read our render parameters (set below)
        cancelRenderMessage(request, Settings.MSG_NEW_QUERY);
        cancelRenderMessage(request, Settings.MSG_NEW_QUERY_FOR_SIMILAR);

        // ----------------------------------
        // set render parameters
        // ----------------------------------        
        actionResponse.setRenderParameter(KEY_START_HIT_RANKED, rankedStarthit);
        actionResponse.setRenderParameter(KEY_START_HIT_UNRANKED, unrankedStarthit);
// END: search_result Portlet

        ps.setBoolean("isActionProcessed", true);
    }

    private SearchResultList doSearch(IngridQuery query, String ds, int start, int limit, boolean ranking) {

        SearchResultList result = new SearchResultList();

        if (ranking) {
            /*
             //start the communication
             SocketCommunication communication = new SocketCommunication();
             
             communication.setMulticastPort(11111);
             communication.setUnicastPort(11112);
             
             
             try {
             communication.startup();
             } catch (IOException e) {
             System.err.println("Cannot start the communication: " + e.getMessage());
             }
             
             // start the proxy server
             ProxyService proxy = new ProxyService();
             
             proxy.setCommunication(communication);
             try {
             proxy.startup();
             } catch (IllegalArgumentException e) {
             System.err.println("Wrong arguments supplied to the proxy service: " + e.getMessage());
             } catch (Exception e) {
             System.err.println("Cannot start the proxy server: " + e.getMessage());
             }
             
             // register the IPlug
             String iBusUrl = AddressUtil.getWetagURL("213.144.28.233", 11112);
             RemoteInvocationController ric = null;
             try {
             ric = proxy.createRemoteInvocationController(iBusUrl);
             } catch (Exception e2) {
             // TODO Auto-generated catch block
             e2.printStackTrace();
             }
             Bus bus = null;
             IngridHit[] documents = new IngridHit[0];
             long numberOfHits = 0;
             int page = (int)(start / limit) + 1;
             
             try {
             Class[] args = new Class[] {IngridQuery.class, int.class, int.class, int.class, int.class};
             Object[] params = new Object[] {query, new Integer(limit), new Integer(page), new Integer(limit), new Integer(1000) };
             IngridHits hits = (IngridHits) ric.invoke(Bus.class, Bus.class.getMethod("searchR", args), params);
             documents = hits.getHits();
             numberOfHits = hits.length();
             } catch (Throwable t) {
             System.err.println("Error on getting IBus: " + t.getMessage());
             t.printStackTrace();
             }
             
             proxy.shutdown();
             communication.shutdown();
             
             if (documents != null) {
             int hitsOnPage;
             if (documents.length > limit) {
             hitsOnPage = limit;
             } else {
             hitsOnPage = documents.length;
             }
             
             for (int i =0; i< hitsOnPage; i++) {
             documents[i].put("title", "document id:" + documents[i].getId().toString());
             documents[i].put("abstract", documents[i].getContent().toString());
             documents[i].put("type", "WEBSITE");
             documents[i].put("provider", documents[i].getProvider().toString());
             documents[i].put("type", "Webseiten");
             // TODO url verkürzt darstellen, da sonst rechte Spalte nach unten springt !!!
             documents[i].put("url", "url not specified");
             documents[i].put("ranking", String.valueOf(documents[i].getScore()));
             
             result.add(documents[i]);
             }
             result.setNumberOfHits((int)numberOfHits);
             } else {
             result.setNumberOfHits(0);
             }
             */
            SearchResultList l = SearchResultListMockup.getRankedSearchResultList();
            if (start > l.size())
                start = l.size() - limit - 1;
            for (int i = start; i < start + limit; i++) {
                if (i >= l.size())
                    break;
                result.add(l.get(i));
            }
            result.setNumberOfHits(l.getNumberOfHits());
        } else {
            SearchResultList l = SearchResultListMockup.getUnrankedSearchResultList();
            for (int i = 0; i < limit; i++) {
                if (i >= l.size())
                    break;
                result.add(l.get(i));
            }
            result.setNumberOfHits(l.getNumberOfHits());
        }
        return result;
    }

    private void doSimpleSearchPortletActionStuff(PortletRequest request, SimpleSearchForm af) {
        // remove old query message
        cancelRenderMessage(request, Settings.MSG_QUERY);
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
        if (queryString.equals(af.getINITIAL_QUERY())
                || queryString.length() == 0) {
            return;
        }

        // Create IngridQuery from form input !
        IngridQuery query = null;
        try {
            query = QueryStringParser.parse(queryString.toLowerCase());
        } catch (ParseException ex) {
            if (log.isWarnEnabled()) {
                log.warn("Problems creating IngridQuery, parsed query string: " + queryString, ex);
            }
            // TODO create ERROR message when no IngridQuery because of parse error and return (OR even do that in form ???) 
        }
        // set query in message for result portlet
        if (query != null) {
            publishRenderMessage(request, Settings.MSG_QUERY, query);
        }
    }

    private PageState initPageState(PageState ps) {
        ps.setBoolean("isActionProcessed", false);
        ps.put("query", null);
        ps.setBoolean("isSimilarOpen", false);
        ps.put("similarRoot", null);
        return ps;
    }

    private ArrayList getNodeChildrenArray(SimilarTreeNode n) {

        ArrayList ret = new ArrayList();
        Iterator it = n.getChildren().iterator();
        while (it != null && it.hasNext()) {
            ret.addAll(getNodeChildrenArray((SimilarTreeNode) it.next()));
        }
        ret.add(n);

        return ret;
    }

}