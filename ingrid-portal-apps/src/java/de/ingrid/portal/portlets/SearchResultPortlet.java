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
import javax.portlet.PortletSession;

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

/**
 * TODO Describe your created type (class, etc.) here.
 *
 * @author joachim@wemove.com
 */
public class SearchResultPortlet extends AbstractVelocityMessagingPortlet {

    /* (non-Javadoc)
     * @see javax.portlet.Portlet#init(javax.portlet.PortletConfig)
     */
    public void init(PortletConfig config) throws PortletException {
        // set our message "scope" for inter portlet messaging
        setTopic(Settings.MSG_TOPIC_SERVICE);

        super.init(config);
    }

    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);
        PortletSession session = request.getPortletSession();

// BEGIN: "simple_search" Portlet
        // get ActionForm, we use get method without instantiation, so we can do
        // special initialisation
        SimpleSearchForm af = (SimpleSearchForm) Utils.getActionForm(request, SimpleSearchForm.SESSION_KEY,
                SimpleSearchForm.class, PortletSession.APPLICATION_SCOPE);
        if (af == null) {
            af = (SimpleSearchForm) Utils.addActionForm(request, SimpleSearchForm.SESSION_KEY, SimpleSearchForm.class,
                    PortletSession.APPLICATION_SCOPE);
            //            af.setINITIAL_QUERY(messages.getString("simpleSearch.query.initial"));
            af.init();
        }

        // if action is "doSearch" page WAS CALLED FROM STARTPAGE and no action was triggered ! 
        String action = request.getParameter("action");
        if (action != null && action.equalsIgnoreCase("doSearch")) {
            // we are on the result psml page !!!
            af.populate(request);
        }

        String selectedDS = (String) receiveRenderMessage(request, Settings.MSG_DATASOURCE);
        if (selectedDS == null) {
            selectedDS = Settings.INITIAL_DATASOURCE;
            publishRenderMessage(request, Settings.MSG_DATASOURCE, selectedDS);
        }
        context.put("ds", selectedDS);
// END: "simple_search" Portlet

        PageState ps = (PageState) session.getAttribute("portlet_state");
        if (ps == null) {
            ps = new PageState(this.getClass().getName());
            ps = initPageState(ps);
            session.setAttribute("portlet_state", ps);
        }

        // initialization if no action has been processed before
        if (!ps.getBoolean("isActionProcessed")) {
            ps = initPageState(ps);
        }
        ps.setBoolean("isActionProcessed", false);

// BEGIN: search_result Portlet
        String q = request.getParameter("q");
        if (q != null && q.length() > 0) {
            ps.put("query", q);
        }
        if (ps.get("query") != null) {

            // ----------------------------------
            // business logic
            // ----------------------------------

            // ranked results	
            int startHit = ps.getInt("rankedNavStart");
            int RANKED_HITS_PER_PAGE = Settings.RANKED_HITS_PER_PAGE;
            String query = ps.getString("query");
            /*String*/ selectedDS = (String) receiveRenderMessage(request, Settings.MSG_DATASOURCE);
            if (selectedDS == null) {
                selectedDS = Settings.INITIAL_DATASOURCE;
            }

            SearchResultList rankedSRL = doSearch(query, selectedDS, startHit, RANKED_HITS_PER_PAGE, true);
            int numberOfHits = rankedSRL.getNumberOfHits();

            // adapt settings of ranked page navihation
            HashMap rankedPageNavigation = Utils.getPageNavigation(startHit, RANKED_HITS_PER_PAGE, numberOfHits,
                    Settings.RANKED_NUM_PAGES_TO_SELECT);

            // unranked results	
            startHit = ps.getInt("unrankedNavStart");
            int UNRANKED_HITS_PER_PAGE = Settings.UNRANKED_HITS_PER_PAGE;

            SearchResultList unrankedSRL = doSearch(query, selectedDS, startHit, UNRANKED_HITS_PER_PAGE, false);
            numberOfHits = unrankedSRL.getNumberOfHits();

            // adapt settings of unranked page navihation
            HashMap unrankedPageNavigation = Utils.getPageNavigation(startHit, UNRANKED_HITS_PER_PAGE, numberOfHits,
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
        } else if (action.equalsIgnoreCase("doSearch")) {

            // check form input
            SimpleSearchForm af = (SimpleSearchForm) Utils.getActionForm(request, SimpleSearchForm.SESSION_KEY,
                    SimpleSearchForm.class, PortletSession.APPLICATION_SCOPE);
            af.populate(request);
            if (!af.validate()) {
                return;
            }

            ps.putString("query", null);
            String q = request.getParameter("q");
            if (q != null && q.length() > 0) {
                ps.putString("query", q);
            }
            
            // when separate portlets: just send a message that a new query was executed and do this in the
            // search_result_similar Portlet
            ps.setBoolean("isSimilarOpen", false);
            ps.put("similarRoot", null);
            
            // when separate portlets: just send a message that a new query was executed and do this in the
            // search_result Portlet
            ps.putInt("rankedNavStart", 0);
            ps.putInt("unrankedNavStart", 0);
            
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
        try {
            ps.putInt("rankedNavStart", Integer.parseInt(request.getParameter("rstart")));
        } catch (NumberFormatException e) {
            //ps.setRankedNavStart(0);
        }
        try {
            ps.putInt("unrankedNavStart", Integer.parseInt(request.getParameter("nrstart")));
        } catch (NumberFormatException e) {
            //ps.setUnrankedNavStart(0);
        }
// END: search_result Portlet

        ps.setBoolean("isActionProcessed", true);
    }

    private SearchResultList doSearch(String qryStr, String ds, int start, int limit, boolean ranking) {

        // force lower case due to an iPlug/iBus bug
        qryStr = qryStr.toLowerCase();

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
             IngridQuery query = null;
             try {
             query = QueryStringParser.parse(qryStr);
             } catch (ParseException e1) {
             // TODO Auto-generated catch block
             e1.printStackTrace();
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

    private PageState initPageState(PageState ps) {
        ps.setBoolean("isActionProcessed", false);
        ps.put("query", null);
        ps.setBoolean("isSimilarOpen", false);
        ps.put("similarRoot", null);
        ps.putInt("rankedNavStart", 0);
        ps.putInt("unrankedNavStart", 0);
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