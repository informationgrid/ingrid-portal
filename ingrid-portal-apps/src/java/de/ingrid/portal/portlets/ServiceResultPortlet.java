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

import de.ingrid.iplug.PlugDescription;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.interfaces.IBUSInterface;
import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;
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
                log.debug("Problems fetching starthit of SERVICE page from render request, set starthit to 0");
            }
        }

        // ----------------------------------
        // business logic
        // ----------------------------------

        int HITS_PER_PAGE = Settings.SEARCH_RANKED_HITS_PER_PAGE;

        // do search
        IngridHits hits = null;
        int numberOfHits = 0;
        try {
            hits = doSearch(query, startHit, HITS_PER_PAGE);
            numberOfHits = (int) hits.length();
        } catch (Exception ex) {
            if (log.isInfoEnabled()) {
                log.info("Problems performing environment catalogue search !");
            }
        }

        if (numberOfHits == 0) {
            // TODO Katalog keine Einträge, WAS ANZEIGEN ??? -> Layouten
            setDefaultViewPage(TEMPLATE_NO_RESULT);
            super.doView(request, response);
            return;
        }

        // adapt settings of page navihation
        HashMap pageNavigation = Utils.getPageNavigation(startHit, HITS_PER_PAGE, numberOfHits,
                Settings.SEARCH_RANKED_NUM_PAGES_TO_SELECT);

        // ----------------------------------
        // prepare view
        // ----------------------------------

        Context context = getContext(request);
        context.put("rankedPageSelector", pageNavigation);
        context.put("rankedResultList", hits);

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
        if (pageStarthit != null) {
            actionResponse.setRenderParameter(KEY_START_HIT, pageStarthit);
        }
    }

    private IngridHits doSearch(IngridQuery query, int startHit, int hitsPerPage) {
        if (log.isDebugEnabled()) {
            log.debug("Service IngridQuery = " + query);
        }

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
                    //                    result.put(Settings.RESULT_KEY_TOPIC, detail.get(Settings.RESULT_KEY_TOPIC));
                }
                if (plug != null) {
                    ibus.transferPlugDetails(result, plug);
                }
            }
        } catch (Throwable t) {
            if (log.isErrorEnabled()) {
                log.error("Problems performing Search !", t);
            }
        }

        return hits;
    }
}