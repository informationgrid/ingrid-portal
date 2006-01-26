package de.ingrid.portal.portlets;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;

import org.apache.portals.bridges.velocity.AbstractVelocityMessagingPortlet;

import de.ingrid.portal.resources.PortletApplicationResources;
import de.ingrid.portal.search.PageState;
import de.ingrid.portal.search.SearchResultList;
import de.ingrid.portal.search.mockup.SearchResultListMockup;
import de.ingrid.utils.query.IngridQuery;

public class ServiceResultPortlet extends AbstractVelocityMessagingPortlet {

    private final static String TEMPLATE_NO_QUERY = "/WEB-INF/templates/default_result.vm";

    private final static String TEMPLATE_QUERY = "/WEB-INF/templates/service_result.vm";

    public void init(PortletConfig config) throws PortletException {
        // set our message "scope" for inter portlet messaging
        setTopic(PortletApplicationResources.MSG_TOPIC_SERVICE);

        super.init(config);
    }

    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        IngridQuery query = (IngridQuery) receiveRenderMessage(request, PortletApplicationResources.MSG_QUERY);
        if (query != null) {
            setDefaultViewPage(TEMPLATE_QUERY);
        } else {
            setDefaultViewPage(TEMPLATE_NO_QUERY);            
        }
        
        /*
         Context context = getContext(request);
         PortletSession session = request.getPortletSession();

         ResourceBundle messages = getPortletConfig().getResourceBundle(request.getLocale());
         context.put("MESSAGES", messages);

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

         int currentSelectorPage;
         int numberOfPages;
         int numberOfSelectorPages;
         int firstSelectorPage;
         int lastSelectorPage;
         boolean selectorHasPreviousPage;
         boolean selectorHasNextPage;
         int numberOfFirstHit;
         int numberOfLastHit;
         int numberOfHits;

         // ranked results
         SearchResultList rankedSRL = doSearch(ps.getString("query"), ps.getString("selectedDS"), ps
         .getInt("rankedNavStart"), HITS_PER_PAGE, true);
         numberOfHits = rankedSRL.getNumberOfHits();

         currentSelectorPage = ps.getInt("rankedNavStart") / HITS_PER_PAGE + 1;
         numberOfPages = numberOfHits / HITS_PER_PAGE;
         if (Math.ceil(numberOfHits % HITS_PER_PAGE) > 0) {
         numberOfPages++;
         }
         numberOfSelectorPages = 5;
         firstSelectorPage = 1;
         selectorHasPreviousPage = false;
         if (currentSelectorPage >= numberOfSelectorPages) {
         firstSelectorPage = currentSelectorPage - (int) (numberOfSelectorPages / 2);
         selectorHasPreviousPage = true;
         }
         lastSelectorPage = firstSelectorPage + numberOfSelectorPages - 1;
         selectorHasNextPage = true;
         if (numberOfPages <= lastSelectorPage) {
         lastSelectorPage = numberOfPages;
         selectorHasNextPage = false;
         }
         numberOfFirstHit = (currentSelectorPage - 1) * HITS_PER_PAGE + 1;
         numberOfLastHit = numberOfFirstHit + HITS_PER_PAGE - 1;

         if (numberOfLastHit > numberOfHits) {
         numberOfLastHit = numberOfHits;
         }

         HashMap pageSelector = new HashMap();
         pageSelector.put("currentSelectorPage", new Integer(currentSelectorPage));
         pageSelector.put("numberOfPages", new Integer(numberOfPages));
         pageSelector.put("numberOfSelectorPages", new Integer(numberOfSelectorPages));
         pageSelector.put("firstSelectorPage", new Integer(firstSelectorPage));
         pageSelector.put("lastSelectorPage", new Integer(lastSelectorPage));
         pageSelector.put("selectorHasPreviousPage", new Boolean(selectorHasPreviousPage));
         pageSelector.put("selectorHasNextPage", new Boolean(selectorHasNextPage));
         pageSelector.put("hitsPerPage", new Integer(HITS_PER_PAGE));
         pageSelector.put("numberOfFirstHit", new Integer(numberOfFirstHit));
         pageSelector.put("numberOfLastHit", new Integer(numberOfLastHit));
         pageSelector.put("numberOfHits", new Integer(numberOfHits));

         context.put("rankedPageSelector", pageSelector);

         context.put("rankedResultList", rankedSRL);
         }

         context.put("ps", ps);
         */
        super.doView(request, response);
    }

    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {
        /*
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
         }
         try {
         ps.putInt("rankedNavStart", Integer.parseInt(request.getParameter("rstart")));
         } catch (NumberFormatException e) {
         // ps.setRankedNavStart(0);
         }

         ps.setBoolean("isActionProcessed", true);
         */
    }

    private PageState initPageState(PageState ps) {
        ps.setBoolean("isActionProcessed", false);
        ps.putInt("rankedNavStart", 0);
        ps.putInt("rankedNavLimit", PortletApplicationResources.HITS_PER_PAGE);
        return ps;
    }

    private SearchResultList doSearch(String qryStr, String ds, int start, int limit, boolean ranking) {

        // force lower case due to an iPlug/iBus bug
        qryStr = qryStr.toLowerCase();

        SearchResultList result = new SearchResultList();

        /*
         // start the communication
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
         int page = (int) (start / limit) + 1;

         try {
         Class[] args = new Class[] { IngridQuery.class, int.class, int.class, int.class, int.class };
         Object[] params = new Object[] { query, new Integer(limit), new Integer(page), new Integer(limit),
         new Integer(1000) };
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

         for (int i = 0; i < hitsOnPage; i++) {
         documents[i].put("title", "document id:" + documents[i].getId().toString());
         documents[i].put("abstract", documents[i].getContent().toString());
         documents[i].put("type", "WEBSITE");
         documents[i].put("provider", documents[i].getProvider().toString());
         documents[i].put("type", "Webseiten");
         documents[i].put("url", "url not specified");
         documents[i].put("ranking", String.valueOf(documents[i].getScore()));

         result.add(documents[i]);
         }
         result.setNumberOfHits((int) numberOfHits);
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

        return result;
    }

}