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

import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
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
 * TODO Please add comments!
 *
 * created 11.01.2006
 *
 * @author joachim
 * @version
 * 
 */
public class SearchResultPortlet extends GenericVelocityPortlet {

    /* (non-Javadoc)
     * @see javax.portlet.Portlet#init(javax.portlet.PortletConfig)
     */
    public void init(PortletConfig config) throws PortletException {
        super.init(config);

    }

    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);

        // -------------------
        // NOTICE: THIS STUFF HAS TO BE MERGED WITH SIMPLE SEARCH PORTLET
        // (separate portlets for query input and results))
        // THIS IS A TEMPORARY SOLUTION !

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

        // if action is "doSearch" page WAS CALLED FROM STARTPAGE !
        String action = request.getParameter("action");
        if (action != null && action.equalsIgnoreCase("doSearch")) {
            // we are on the result psml page !!!
            af.populate(request);
        }
        // -------------------

        PortletSession session = request.getPortletSession();
        String selectedDS = (String) session.getAttribute("selectedDS");
        if (selectedDS == null || selectedDS.length() == 0) {
            selectedDS = "1";
        }
        context.put("ds", selectedDS);

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
            selectedDS = ps.getString("selectedDS");
            String query = ps.getString("query");

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

        context.put("ps", ps);

        super.doView(request, response);
    }

    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {
        String action = request.getParameter("action");

        // -------------------
        // NOTICE: THIS STUFF HAS TO BE MERGED WITH SIMPLE SEARCH PORTLET
        // (separate portlets for query input and results))
        // THIS IS A TEMPORARY SOLUTION !

        // check form input
        SimpleSearchForm af = (SimpleSearchForm) Utils.getActionForm(request, SimpleSearchForm.SESSION_KEY,
                SimpleSearchForm.class, PortletSession.APPLICATION_SCOPE);
        af.populate(request);
        if (!af.validate()) {
            return;
        }
        // -------------------

        PortletSession session = request.getPortletSession();
        PageState ps = (PageState) session.getAttribute("portlet_state");
        if (ps == null) {
            ps = new PageState(this.getClass().getName());
            ps = initPageState(ps);
            session.setAttribute("portlet_state", ps);
        }

        if (action == null) {
            action = "";
        } else if (action.equalsIgnoreCase("doSearch")) {
            ps.putString("query", null);
            String q = request.getParameter("q");
            if (q != null && q.length() > 0) {
                ps.putString("query", q);
            }
            ps.setBoolean("isSimilarOpen", false);
            ps.put("similarRoot", null);
            ps.putInt("rankedNavStart", 0);
            ps.putInt("unrankedNavStart", 0);
        } else if (action.equalsIgnoreCase("doChangeDS")) {
            session.setAttribute("selectedDS", request.getParameter("ds"), PortletSession.APPLICATION_SCOPE);
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
        ps.putInt("rankedNavLimit", 10);
        ps.putInt("unrankedNavStart", 0);
        ps.putInt("unrankedNavLimit", 10);
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