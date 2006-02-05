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

public class EnvironmentResultPortlet extends AbstractVelocityMessagingPortlet {

    private final static Log log = LogFactory.getLog(EnvironmentResultPortlet.class);

    /** view templates */
    private final static String TEMPLATE_NO_QUERY = "/WEB-INF/templates/empty.vm";

    private final static String TEMPLATE_RESULT = "/WEB-INF/templates/environment_result.vm";

    private final static String TEMPLATE_NO_RESULT = "/WEB-INF/templates/environment_no_result.vm";

    /** Keys of parameters in session/request */
    private final static String KEY_START_HIT = "rstart";

    public void init(PortletConfig config) throws PortletException {
        // set our message "scope" for inter portlet messaging
        setTopic(Settings.MSG_TOPIC_ENVIRONMENT);

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
                log.debug("Problems fetching starthit of ENVIRONMENT page from render request, set starthit to 0");
            }
        }

        // ----------------------------------
        // business logic
        // ----------------------------------

        int HITS_PER_PAGE = Settings.RANKED_HITS_PER_PAGE;

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

        // adapt settings of page nagihation
        HashMap pageNavigation = Utils.getPageNavigation(startHit, HITS_PER_PAGE, numberOfHits,
                Settings.RANKED_NUM_PAGES_TO_SELECT);

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
            log.debug("Umweltthemen IngridQuery = " + query);
        }

        int SEARCH_TIMEOUT = 5000;
        int SEARCH_REQUESTED_NUM_HITS = hitsPerPage;
        int currentPage = (int) (startHit / hitsPerPage) + 1;

        String RESULT_KEY_TITLE = "title";
        String RESULT_KEY_ABSTRACT = "abstract";
        String RESULT_KEY_TOPIC = "topic";
        String RESULT_KEY_FUNCT_CATEGORY = "funct_category";
        String RESULT_KEY_URL = "url";
        String RESULT_KEY_URL_STR = "url_str";
        String RESULT_KEY_PROVIDER = "provider";
        String RESULT_KEY_SOURCE = "source";

        IngridHits hits = null;

        try {

            IBUSInterface ibus = IBUSInterfaceImpl.getInstance();
            hits = ibus.search(query, hitsPerPage, currentPage, SEARCH_REQUESTED_NUM_HITS, SEARCH_TIMEOUT);
            IngridHit[] results = hits.getHits();

            IngridHit result = null;
            IngridHitDetail detail = null;
            String plugId = null;
            PlugDescription plug = null;
            for (int i = 0; i < results.length; i++) {
                detail = null;
                plug = null;
                try {
                    result = results[i];
                    detail = ibus.getDetails(result, query);
                    plugId = result.getPlugId();
                    plug = ibus.getIPlug(plugId);
                } catch (Throwable t) {
                    if (log.isErrorEnabled()) {
                        log.error("Problems fetching result details or iPlug", t);
                    }
                }
                if (result == null) {
                    continue;
                }
                if (detail != null) {
                    result.put(RESULT_KEY_TITLE, detail.getTitle());
                    result.put(RESULT_KEY_ABSTRACT, detail.getSummary());
                    result.put(RESULT_KEY_TOPIC, detail.get(RESULT_KEY_TOPIC));
                    result.put(RESULT_KEY_FUNCT_CATEGORY, detail.get(RESULT_KEY_FUNCT_CATEGORY));
                    if (detail.get("url") != null) {
                        result.put(RESULT_KEY_URL, detail.get("url"));
                        result.put(RESULT_KEY_URL_STR, Utils.getShortURLStr((String) detail.get("url"), 80));
                    }

                }
                if (plug != null) {
                    result.put(RESULT_KEY_PROVIDER, plug.getOrganisation());
                    result.put(RESULT_KEY_SOURCE, plug.getDataSourceName());
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