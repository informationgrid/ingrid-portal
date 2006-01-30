package de.ingrid.portal.portlets;

import java.io.IOException;
import java.util.HashMap;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.portals.bridges.velocity.AbstractVelocityMessagingPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.search.SearchResultList;
import de.ingrid.portal.search.mockup.SearchResultListMockup;
import de.ingrid.utils.query.IngridQuery;

public class ServiceResultPortlet extends AbstractVelocityMessagingPortlet {

    private final static Log log = LogFactory.getLog(ServiceResultPortlet.class);

    /** view templates */
    private final static String TEMPLATE_NO_QUERY = "/WEB-INF/templates/empty.vm";

    private final static String TEMPLATE_RESULT = "/WEB-INF/templates/service_result.vm";

    private final static String TEMPLATE_NO_RESULT = "/WEB-INF/templates/service_no_result.vm";

    /** Keys of parameters in session/request */
    private final static String KEY_START_HIT = "rstart";

    public void init(PortletConfig config) throws PortletException {
        // set our message "scope" for inter portlet messaging
        setTopic(Settings.MSG_TOPIC_SERVICE);

        super.init(config);
    }

    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {

        // ----------------------------------
        // set initial view template
        // ----------------------------------

        // if no query display "nothing"
        IngridQuery query = (IngridQuery) receiveRenderMessage(request, Settings.MSG_QUERY);
        if (query == null) {
            setDefaultViewPage(TEMPLATE_NO_QUERY);
            super.doView(request, response);
            return;
        }

        // if query assume we have results
        setDefaultViewPage(TEMPLATE_RESULT);

        // ----------------------------------
        // fetch data
        // ----------------------------------

        // default: start at the beginning with the first hit (display first result page)
        int startHit = 0;
        // indicates whether a new query was performed !
        Object newQuery = receiveRenderMessage(request, Settings.MSG_NEW_QUERY);
        try {
            // if no new query was performed, read render parameters from former action request
            if (newQuery == null) {
                startHit = (new Integer(request.getParameter(KEY_START_HIT))).intValue();
            }
        } catch (Exception ex) {
            if (log.isDebugEnabled()) {
                log.debug("Problems fetching starthit of SERVICE page from render request, set starthit to 0", ex);
            }
        }

        // ----------------------------------
        // business logic
        // ----------------------------------

        int HITS_PER_PAGE = Settings.RANKED_HITS_PER_PAGE;

        // do search
        SearchResultList searchRL = doSearch(query, startHit, HITS_PER_PAGE, true);
        int numberOfHits = searchRL.getNumberOfHits();
        if (numberOfHits == 0) {
            // TODO Katalog keine Einträge, WAS ANZEIGEN ??? -> Layouten
            setDefaultViewPage(TEMPLATE_NO_RESULT);
            super.doView(request, response);
            return;
        }
        // adapt settings of page navihation
        HashMap pageNavigation = Utils
                .getPageNavigation(startHit, HITS_PER_PAGE, numberOfHits, Settings.RANKED_NUM_PAGES_TO_SELECT);

        // ----------------------------------
        // prepare view
        // ----------------------------------

        Context context = getContext(request);
        context.put("rankedPageSelector", pageNavigation);
        context.put("rankedResultList", searchRL);

        super.doView(request, response);
    }

    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {
        // ----------------------------------
        // fetch parameters
        // ----------------------------------
        String pageStarthit = request.getParameter(KEY_START_HIT);

        // ----------------------------------
        // business logic
        // ----------------------------------
        // remove message from search submit portlet indicating that a new query was performed
        cancelRenderMessage(request, Settings.MSG_NEW_QUERY);

        // ----------------------------------
        // set render parameters
        // ----------------------------------        
        actionResponse.setRenderParameter(KEY_START_HIT, pageStarthit);
    }

    private SearchResultList doSearch(IngridQuery query, int start, int limit, boolean ranking) {

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