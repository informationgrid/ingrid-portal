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

import net.weta.components.communication_sockets.SocketCommunication;
import net.weta.components.communication_sockets.util.AddressUtil;
import net.weta.components.proxies.ProxyService;
import net.weta.components.proxies.remote.RemoteInvocationController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.portals.bridges.velocity.AbstractVelocityMessagingPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.ibus.Bus;
import de.ingrid.iplug.PlugDescription;
import de.ingrid.portal.forms.SimpleSearchForm;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.search.PageState;
import de.ingrid.portal.search.SearchResultList;
import de.ingrid.portal.search.SimilarTreeNode;
import de.ingrid.portal.search.mockup.SearchResultListMockup;
import de.ingrid.portal.search.mockup.SimilarNodeFactoryMockup;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;
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
                    rankedStartHit = (new Integer(request.getParameter(KEY_START_HIT_RANKED))).intValue();
                    unrankedStartHit = (new Integer(request.getParameter(KEY_START_HIT_UNRANKED))).intValue();
                }
            } catch (Exception ex) {
                if (log.isDebugEnabled()) {
                    log.debug("Problems fetching starthits of RESULT page from render request, set starthits to 0", ex);
                }
            }

            /*String*/selectedDS = (String) receiveRenderMessage(request, Settings.MSG_DATASOURCE);
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
            HashMap unrankedPageNavigation = Utils.getPageNavigation(unrankedStartHit, UNRANKED_HITS_PER_PAGE,
                    numberOfHits, Settings.UNRANKED_NUM_PAGES_TO_SELECT);

            // ----------------------------------
            // prepare view
            // ----------------------------------

            context.put("rankedPageSelector", rankedPageNavigation);
            context.put("unrankedPageSelector", unrankedPageNavigation);
            context.put("rankedResultList", rankedSRL);
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
            ps.setBoolean("isSimilarOpen", false);
            ps.put("similarRoot", null);

        } else if (action.equalsIgnoreCase("doChangeDS")) {
            publishRenderMessage(request, Settings.MSG_DATASOURCE, request.getParameter("ds"));
        }
        // END: "simple_search" Portlet

        // BEGIN: search_result_similar Portlet
        if (action.equalsIgnoreCase("doOpenSimilar")) {
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
    }

    private SearchResultList doSearch(IngridQuery query, String ds, int startHit, int hitsPerPage, boolean ranking) {

        // TODO put this into iBus Service or global Settings !
        int COMMUNICATION_MULTICAST_PORT = 11114;
        int COMMUNICATION_UNICAST_PORT = 50000;

        String IBUS_HOST = "213.144.28.233";
        int IBUS_PORT = 11112;

        int SEARCH_TIMEOUT = 2000;
        //int SEARCH_REQUESTED_NUM_HITS = Integer.MAX_VALUE;
        int SEARCH_REQUESTED_NUM_HITS = 2 * hitsPerPage;
        int currentPage = (int) (startHit / hitsPerPage) + 1;

        String RESULT_IS_NULL_TEXT = "result is null !";

        String RESULT_KEY_TITLE = "title";
        String RESULT_KEY_ABSTRACT = "abstract";
        String RESULT_KEY_PROVIDER = "provider";
        String RESULT_KEY_SOURCE = "source";
        String RESULT_KEY_RANKING = "ranking";
        String RESULT_KEY_URL = "url";

        SearchResultList result = new SearchResultList();

        SocketCommunication com = null;
        ProxyService proxyService = null;

        if (ranking) {
            try {
                com = new SocketCommunication();
                com.setMulticastPort(COMMUNICATION_MULTICAST_PORT);
                com.setUnicastPort(COMMUNICATION_UNICAST_PORT);
                com.startup();

                proxyService = new ProxyService();
                proxyService.setCommunication(com);
                proxyService.startup();

                String iBusUrl = AddressUtil.getWetagURL(IBUS_HOST, IBUS_PORT);
                RemoteInvocationController ric = proxyService.createRemoteInvocationController(iBusUrl);

                Bus bus = (Bus) ric.invoke(Bus.class, Bus.class.getMethod("getInstance", null), null);
                IngridHits hits = bus
                        .search(query, hitsPerPage, currentPage, SEARCH_REQUESTED_NUM_HITS, SEARCH_TIMEOUT);
                IngridHit[] hitArray = hits.getHits();
                if (hitArray == null) {
                    result.setNumberOfHits(0);
                    return result;
                }

                long numHits = hits.length();
                result.setNumberOfHits((int) numHits);

                int numHitsToDisplay = hitsPerPage;
                if (numHits < hitsPerPage) {
                    numHitsToDisplay = (int) numHits;
                }
                IngridHit hit = null;
                IngridHitDetail details = null;
                String plugId = null;
                PlugDescription plug = null;
                for (int i = 0; i < numHitsToDisplay; i++) {
                    hit = hitArray[i];
                    try {
                        details = bus.getDetails(hit, query);
                        plugId = hit.getPlugId();
                        plug = bus.getIPlug(plugId);
                    } catch (Exception ex) {
                        if (log.isErrorEnabled()) {
                            log.error("Problems fetching result details in search result page, hit = " + hit, ex);
                        }
                        details = null;
                    }

                    hit.put(RESULT_KEY_RANKING, Float.toString(hit.getScore()));

                    if (details != null) {
                        hit.put(RESULT_KEY_TITLE, details.getTitle());
                        hit.put(RESULT_KEY_ABSTRACT, details.getSummary());
                        hit.put(RESULT_KEY_URL, details.get("url"));
                    } else {
                        hit.put(RESULT_KEY_ABSTRACT, RESULT_IS_NULL_TEXT);
                    }
                    if (plug != null) {
                        hit.put(RESULT_KEY_PROVIDER, plug.getOrganisation());
                        hit.put(RESULT_KEY_SOURCE, plug.getDataSourceName());
                    }

                    result.add(hit);
                }
            } catch (Throwable t) {
                if (log.isErrorEnabled()) {
                    log.error("Problems performing Search !", t);
                }
            } finally {
                if (proxyService != null) {
                    proxyService.shutdown();
                }
                if (com != null) {
                    com.shutdown();
                }
            }

            /*
             SearchResultList l = SearchResultListMockup.getRankedSearchResultList();
             if (startHit > l.size())
             startHit = l.size() - hitsPerPage - 1;
             for (int i = startHit; i < startHit + hitsPerPage; i++) {
             if (i >= l.size())
             break;
             result.add(l.get(i));
             }
             result.setNumberOfHits(l.getNumberOfHits());
             */
        } else {
            SearchResultList l = SearchResultListMockup.getUnrankedSearchResultList();
            for (int i = 0; i < hitsPerPage; i++) {
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