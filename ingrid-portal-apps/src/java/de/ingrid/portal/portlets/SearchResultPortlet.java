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

import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.interfaces.IBUSInterface;
import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.dsc.Column;
import de.ingrid.utils.dsc.Record;
import de.ingrid.utils.query.FieldQuery;
import de.ingrid.utils.query.IngridQuery;

/**
 * This portlet handles the "Result Display" fragment of the result page
 * 
 * @author martin@wemove.com
 */
public class SearchResultPortlet extends AbstractVelocityMessagingPortlet {

    private final static Log log = LogFactory.getLog(SearchResultPortlet.class);

    /** view templates */
    private final static String TEMPLATE_NO_QUERY_SET = "/WEB-INF/templates/empty.vm";

    private final static String TEMPLATE_RESULT = "/WEB-INF/templates/search_result.vm";

    private final static String TEMPLATE_RESULT_ADDRESS = "/WEB-INF/templates/search_result_address.vm";

    private final static String TEMPLATE_NO_RESULT = "/WEB-INF/templates/search_no_result.vm";

    /** Keys of parameters in session/request */
    private final static String PARAM_START_HIT_RANKED = "rstart";

    private final static String PARAM_START_HIT_UNRANKED = "nrstart";

    /*
     * (non-Javadoc)
     * 
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

        // receive Messages
        // indicates whether we do a query or we read results from cache
        boolean doQuery = true;
        if (receiveRenderMessage(request, Settings.MSG_NO_QUERY) != null) {
            doQuery = false;
        }
        // indicates whether we do a whole new query and start on first page
        boolean newQuery = true;
        if (receiveRenderMessage(request, Settings.MSG_NEW_QUERY) == null) {
            newQuery = false;
        }

        // ----------------------------------
        // set initial view template
        // ----------------------------------

        // if no query set display "nothing"
        IngridQuery query = (IngridQuery) receiveRenderMessage(request, Settings.MSG_QUERY);
        if (query == null) {
            setDefaultViewPage(TEMPLATE_NO_QUERY_SET);
            super.doView(request, response);
            return;
        }

        // selected data source ("Umweltinfo", Adressen" or
        // "Forschungsprojekte")
        String selectedDS = (String) receiveRenderMessage(request, Settings.MSG_DATASOURCE);
        if (selectedDS == null) {
            selectedDS = Settings.SEARCH_INITIAL_DATASOURCE;
            setDefaultViewPage(TEMPLATE_RESULT);
        }

        // adapt result page to selected data source, ONLY IF WE DO A NEW QUERY
        // !!!
        if (doQuery && newQuery) {
            if (selectedDS.equals(Settings.SEARCH_DATASOURCE_ENVINFO)) {
                setDefaultViewPage(TEMPLATE_RESULT);
            } else if (selectedDS.equals(Settings.SEARCH_DATASOURCE_ADDRESS)) {
                setDefaultViewPage(TEMPLATE_RESULT_ADDRESS);
            }
        }

        String currentView = getDefaultViewPage();

        // ----------------------------------
        // fetch data
        // ----------------------------------
        // default: start at the beginning with the first hit (display first
        // result page)
        int rankedStartHit = 0;
        int unrankedStartHit = 0;
        if (!newQuery) {
            // if no new query was performed, read render parameters (which
            // page) from former action request
            try {
                String reqParam = request.getParameter(PARAM_START_HIT_RANKED);
                if (reqParam != null) {
                    rankedStartHit = (new Integer(reqParam)).intValue();
                }
                reqParam = request.getParameter(PARAM_START_HIT_UNRANKED);
                if (reqParam != null) {
                    unrankedStartHit = (new Integer(reqParam)).intValue();
                }
            } catch (Exception ex) {
                if (log.isErrorEnabled()) {
                    log.error("Problems parsing starthit (ranked or unranked) from render request, set starthit to 0");
                }
            }
        }

        // ----------------------------------
        // business logic
        // ----------------------------------

        // ranked results
        // --------------
        IngridHits rankedHits = null;
        int numberOfRankedHits = 0;
        try {
            // use cache if we don't query
            if (!doQuery) {
                rankedHits = (IngridHits) receiveRenderMessage(request, Settings.MSG_SEARCH_RESULT_RANKED);
            }
            if (rankedHits == null) {
                rankedHits = doRankedSearch(query, selectedDS, rankedStartHit, Settings.SEARCH_RANKED_HITS_PER_PAGE);
                this.publishRenderMessage(request, Settings.MSG_SEARCH_RESULT_RANKED, rankedHits);
            }
            numberOfRankedHits = (int) rankedHits.length();
        } catch (Exception ex) {
            if (log.isErrorEnabled()) {
                log.error("Problems fetching RANKED hits ! hits = " + rankedHits + ", numHits = " + numberOfRankedHits,
                        ex);
            }
        }

        // adapt settings of ranked page navigation
        HashMap rankedPageNavigation = Utils.getPageNavigation(rankedStartHit, Settings.SEARCH_RANKED_HITS_PER_PAGE,
                numberOfRankedHits, Settings.SEARCH_RANKED_NUM_PAGES_TO_SELECT);

        // unranked results
        // ----------------
        IngridHits unrankedHits = null;
        int numberOfUnrankedHits = 0;
        HashMap unrankedPageNavigation = null;

        // only do unranked stuff if necessary for view
        if (!currentView.equals(TEMPLATE_RESULT_ADDRESS)) {
            try {
                // use cache if we don't query
                if (!doQuery) {
                    unrankedHits = (IngridHits) receiveRenderMessage(request, Settings.MSG_SEARCH_RESULT_UNRANKED);
                }
                if (unrankedHits == null) {
                    unrankedHits = doUnrankedSearch(query, selectedDS, unrankedStartHit,
                            Settings.SEARCH_UNRANKED_HITS_PER_PAGE);
                    this.publishRenderMessage(request, Settings.MSG_SEARCH_RESULT_UNRANKED, unrankedHits);
                }
                // TODO: Uncomment, throws Exception, at the Moment no Hits !
                numberOfUnrankedHits = (int) unrankedHits.length();
            } catch (Exception ex) {
                if (log.isErrorEnabled()) {
                    log.error("Problems fetching UNRANKED hits ! hits = " + unrankedHits + ", numHits = "
                            + numberOfUnrankedHits, ex);
                }
            }

            // adapt settings of unranked page navigation
            unrankedPageNavigation = Utils.getPageNavigation(unrankedStartHit, Settings.SEARCH_UNRANKED_HITS_PER_PAGE,
                    numberOfUnrankedHits, Settings.SEARCH_UNRANKED_NUM_PAGES_TO_SELECT);
        }

        if (numberOfRankedHits == 0 && numberOfUnrankedHits == 0) {
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
        context.put("unrankedResultList", unrankedHits);

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
        // no new query anymore, we remove message, so portlets read our render
        // parameters (set below)
        cancelRenderMessage(request, Settings.MSG_NEW_QUERY);
        // further remove cache message, we have to do query !
        cancelRenderMessage(request, Settings.MSG_NO_QUERY);

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
        if (log.isDebugEnabled()) {
            log.debug("doRankedSearch: IngridQuery = " + query);
        }

        int currentPage = (int) (startHit / hitsPerPage) + 1;

        IngridHits hits = null;
        try {
            IBUSInterface ibus = IBUSInterfaceImpl.getInstance();
            query.addField(new FieldQuery(false, true, Settings.QFIELD_DATATYPE, "g2k"));
            hits = ibus.search(query, hitsPerPage, currentPage, hitsPerPage, Settings.SEARCH_DEFAULT_TIMEOUT);
            IngridHit[] results = hits.getHits();
            String[] requestedMetadata = new String[0];
            if (ds.equals(Settings.SEARCH_DATASOURCE_ENVINFO)) {
                requestedMetadata = new String[2];
                requestedMetadata[0] = Settings.HIT_KEY_WMS_URL;
                requestedMetadata[1] = Settings.HIT_KEY_UDK_CLASS;
            } else if (ds.equals(Settings.SEARCH_DATASOURCE_ADDRESS)) {
                requestedMetadata = new String[2];
                requestedMetadata[0] = Settings.HIT_KEY_WMS_URL;
                requestedMetadata[1] = Settings.HIT_KEY_ADDRESS_CLASS;
            }
            // IngridHitDetail[] details = ibus.getDetails(results, query,
            // requestedMetadata);

            IngridHit result = null;
            IngridHitDetail detail = null;
            String tmpString = null;
            for (int i = 0; i < results.length; i++) {
                try {
                    result = results[i];
                    // detail = details[i];
                    detail = ibus.getDetail(result, query, requestedMetadata);
                    if (result == null) {
                        continue;
                    }
                    if (detail != null) {
                        ibus.transferHitDetails(result, detail);
                        tmpString = detail.getIplugClassName();

                        if (tmpString.equals("de.ingrid.iplug.dsc.index.DSCSearcher")) {
                            result.put(Settings.RESULT_KEY_TYPE, "dsc");

                            // read for all dsc iplugs
                            if (detail.get(Settings.HIT_KEY_WMS_URL) != null) {
                                tmpString = detail.get(Settings.HIT_KEY_WMS_URL).toString();
                                result.put(Settings.RESULT_KEY_WMS_URL, URLEncoder.encode(tmpString, "UTF-8"));
                            }

                            // check our selected "data source" and read
                            // according data
                            if (ds.equals(Settings.SEARCH_DATASOURCE_ENVINFO)) {
                                if (detail.get(Settings.HIT_KEY_UDK_CLASS) != null) {
                                    tmpString = detail.get(Settings.HIT_KEY_UDK_CLASS).toString();
                                    result.put(Settings.RESULT_KEY_UDK_CLASS, tmpString);
                                }
                            } else if (ds.equals(Settings.SEARCH_DATASOURCE_ADDRESS)) {
                                if (detail.get(Settings.HIT_KEY_ADDRESS_CLASS) != null) {
                                    tmpString = detail.get(Settings.HIT_KEY_ADDRESS_CLASS).toString();
                                    result.put(Settings.RESULT_KEY_UDK_CLASS, tmpString);
                                }
                            }
                        } else if (tmpString.equals("de.ingrid.iplug.se.NutchSearcher")) {
                            result.put(Settings.RESULT_KEY_TYPE, "nutch");
                        } else {
                            result.put(Settings.RESULT_KEY_TYPE, "unknown");
                        }
                    }
                } catch (Throwable t) {
                    if (log.isErrorEnabled()) {
                        log.error("Problems processing Hit, hit = " + result + ", detail = " + detail, t);
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

    private IngridHits doUnrankedSearch(IngridQuery query, String ds, int startHit, int hitsPerPage) {

        int currentPage = (int) (startHit / hitsPerPage) + 1;

        // IngridHits hits = new IngridHits();
        IngridHits hits = null;
        try {
            IBUSInterface ibus = IBUSInterfaceImpl.getInstance();
            // copy the query to NOT influence the query for ranked results
            // TODO refactor this
            IngridQuery rankedQuery = new IngridQuery();
            rankedQuery.putAll(query);
            rankedQuery.remove(Settings.QFIELD_DATATYPE);
            
//            query.remove(Settings.QFIELD_DATATYPE);
            rankedQuery.addField(new FieldQuery(true, false, Settings.QFIELD_DATATYPE, "g2k"));            
            if (log.isDebugEnabled()) {
                log.debug("doUnrankedSearch: IngridQuery = " + query);
            }
            hits = ibus.search(rankedQuery, hitsPerPage, currentPage, hitsPerPage, Settings.SEARCH_DEFAULT_TIMEOUT);
            IngridHit[] results = hits.getHits();
            
            String[] requestedMetadata = new String[1];
            requestedMetadata = new String[1];
            requestedMetadata[0] = Settings.HIT_KEY_WMS_URL;
            
            IngridHit result = null;
            IngridHitDetail detail = null;
            String tmpString = null;
            for (int i = 0; i < results.length; i++) {
                try {
                    result = results[i];
                    // detail = details[i];
                    detail = ibus.getDetail(result, rankedQuery, requestedMetadata);
                    if (result == null) {
                        continue;
                    }
                    if (detail != null) {
                        ibus.transferHitDetails(result, detail);
                        tmpString = detail.getIplugClassName();

                        if (tmpString.equals("de.ingrid.iplug.dsc.index.DSCSearcher")) {
                            result.put(Settings.RESULT_KEY_TYPE, "dsc");

                            // read for all dsc iplugs
                            if (detail.get(Settings.HIT_KEY_WMS_URL) != null) {
                                tmpString = detail.get(Settings.HIT_KEY_WMS_URL).toString();
                                result.put(Settings.RESULT_KEY_WMS_URL, URLEncoder.encode(tmpString, "UTF-8"));
                            }

                            // check our selected "data source" and read
                            // according data
                            if (ds.equals(Settings.SEARCH_DATASOURCE_ENVINFO)) {
                                if (detail.get(Settings.HIT_KEY_UDK_CLASS) != null) {
                                    tmpString = detail.get(Settings.HIT_KEY_UDK_CLASS).toString();
                                    result.put(Settings.RESULT_KEY_UDK_CLASS, tmpString);
                                }
                            } else if (ds.equals(Settings.SEARCH_DATASOURCE_ADDRESS)) {
                                if (detail.get(Settings.HIT_KEY_ADDRESS_CLASS) != null) {
                                    tmpString = detail.get(Settings.HIT_KEY_ADDRESS_CLASS).toString();
                                    result.put(Settings.RESULT_KEY_UDK_CLASS, tmpString);
                                }
                            }
                        } else if (tmpString.equals("de.ingrid.iplug.se.NutchSearcher")) {
                            result.put(Settings.RESULT_KEY_TYPE, "nutch");
                        } else {
                            result.put(Settings.RESULT_KEY_TYPE, "unknown");
                        }
                    }
                } catch (Throwable t) {
                    if (log.isErrorEnabled()) {
                        log.error("Problems processing Hit, hit = " + result + ", detail = " + detail, t);
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

}