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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.portals.bridges.velocity.AbstractVelocityMessagingPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.iplug.PlugDescription;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.interfaces.IBUSInterface;
import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;
import de.ingrid.portal.search.SearchResultList;
import de.ingrid.portal.search.mockup.SearchResultListMockup;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.dsc.Column;
import de.ingrid.utils.dsc.Record;
import de.ingrid.utils.query.IngridQuery;

/**
 * TODO Describe your created type (class, etc.) here.
 *
 * @author joachim@wemove.com
 */
public class SearchResultPortletNew extends AbstractVelocityMessagingPortlet {

    private final static Log log = LogFactory.getLog(SearchResultPortletNew.class);

    /** view templates */
    private final static String TEMPLATE_NO_QUERY = "/WEB-INF/templates/empty.vm";

    private final static String TEMPLATE_RESULT = "/WEB-INF/templates/search_result_new.vm";

    private final static String TEMPLATE_NO_RESULT = "/WEB-INF/templates/search_no_result.vm";

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
        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()));
        context.put("MESSAGES", messages);

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

        String selectedDS = (String) receiveRenderMessage(request, Settings.MSG_DATASOURCE);
        if (selectedDS == null) {
            selectedDS = Settings.SEARCH_INITIAL_DATASOURCE;
        }

        // ----------------------------------
        // business logic
        // ----------------------------------

        // ranked results   
        int RANKED_HITS_PER_PAGE = Settings.SEARCH_RANKED_HITS_PER_PAGE;
        IngridHits rankedHits = null;
        int numberOfRankedHits = 0;
        try {
            rankedHits = doRankedSearch(query, selectedDS, rankedStartHit, RANKED_HITS_PER_PAGE);
            numberOfRankedHits = (int) rankedHits.length();
        } catch (Exception ex) {
            if (log.isInfoEnabled()) {
                log
                        .info("Problems fetching ranked hits ! hits = " + rankedHits + ", numHits = "
                                + numberOfRankedHits, ex);
            }
        }

        // adapt settings of ranked page navigation
        HashMap rankedPageNavigation = Utils.getPageNavigation(rankedStartHit, RANKED_HITS_PER_PAGE, numberOfRankedHits,
                Settings.SEARCH_RANKED_NUM_PAGES_TO_SELECT);

        // unranked results 
        int UNRANKED_HITS_PER_PAGE = Settings.SEARCH_UNRANKED_HITS_PER_PAGE;
        int numberOfUnrankedHits = 0;
        SearchResultList unrankedSRL = doUnrankedSearch(query, selectedDS, unrankedStartHit, UNRANKED_HITS_PER_PAGE);
        numberOfUnrankedHits = unrankedSRL.getNumberOfHits();

        // adapt settings of unranked page navigation
        HashMap unrankedPageNavigation = Utils.getPageNavigation(unrankedStartHit, UNRANKED_HITS_PER_PAGE,
                numberOfUnrankedHits, Settings.SEARCH_UNRANKED_NUM_PAGES_TO_SELECT);

        if (numberOfRankedHits == 0 && numberOfUnrankedHits == 0 ) {
            // TODO Katalog keine Einträge, WAS ANZEIGEN ??? -> Layouten

            // query string will be displayed when no results !
            String queryString = (String) receiveRenderMessage(request, Settings.MSG_QUERY_STRING);
            context.put("queryString", queryString);

            setDefaultViewPage(TEMPLATE_NO_RESULT);
            super.doView(request, response);
            return;
        }

        // ----------------------------------
        // prepare view
        // ----------------------------------

        context.put("rankedPageSelector", rankedPageNavigation);
        context.put("unrankedPageSelector", unrankedPageNavigation);
        context.put("rankedResultList", rankedHits);
        context.put("unrankedResultList", unrankedSRL);

        super.doView(request, response);
    }

    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {
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
                        if (record != null) {
                            Column c = ibus.getColumn(record, Settings.HIT_KEY_WMS_URL);
                            if (c != null) {
                                result.put(Settings.RESULT_KEY_WMS_URL, URLEncoder.encode(record.getValueAsString(c),
                                        "UTF-8"));
                            }
                            c = ibus.getColumn(record, Settings.HIT_KEY_UDK_CLASS);
                            if (c != null) {
                                result.put(Settings.RESULT_KEY_UDK_CLASS, record.getValueAsString(c));
                            }                            
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
}