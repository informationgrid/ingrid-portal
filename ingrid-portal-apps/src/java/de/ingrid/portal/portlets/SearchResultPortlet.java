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
import de.ingrid.portal.search.SearchState;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;
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

        // ----------------------------------
        // check for passed RENDER PARAMETERS (for bookmarking) and
        // ADAPT OUR PERMANENT STATE VIA MESSAGES
        // ----------------------------------
        // starthit RANKED
        String reqParam = null;
        int rankedStartHit = 0;
        try {
            reqParam = request.getParameter(SearchState.PARAM_STARTHIT_RANKED);
            if (SearchState.adaptSearchState(request, Settings.MSG_STARTHIT_RANKED, reqParam)) {
                rankedStartHit = (new Integer(reqParam)).intValue();
            }
        } catch (Exception ex) {
            if (log.isErrorEnabled()) {
                log.error("Problems parsing RANKED starthit from render request, set RANKED starthit to 0", ex);
            }
        }

        // starthit UNRANKED
        int unrankedStartHit = 0;
        try {
            reqParam = request.getParameter(SearchState.PARAM_STARTHIT_UNRANKED);
            if (SearchState.adaptSearchState(request, Settings.MSG_STARTHIT_UNRANKED, reqParam)) {
                unrankedStartHit = (new Integer(reqParam)).intValue();
            }
        } catch (Exception ex) {
            if (log.isErrorEnabled()) {
                log.error("Problems parsing UNRANKED starthit from render request, set UNRANKED starthit to 0", ex);
            }
        }

        // indicates whether we do a query or we read results from cache
        String queryState = (String) consumeRenderMessage(request, Settings.MSG_QUERY_STATE);
        if (queryState == null) {
            queryState = "";
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

        // selected data source ("Umweltinfo", Adressen" or "Forschungsprojekte")
        String selectedDS = (String) receiveRenderMessage(request, Settings.MSG_DATASOURCE);
        if (selectedDS == null) {
            selectedDS = Settings.SEARCH_INITIAL_DATASOURCE;
        }
        if (selectedDS.equals(Settings.SEARCH_DATASOURCE_ENVINFO)) {
            setDefaultViewPage(TEMPLATE_RESULT);
        } else if (selectedDS.equals(Settings.SEARCH_DATASOURCE_ADDRESS)) {
            setDefaultViewPage(TEMPLATE_RESULT_ADDRESS);
        }

        String currentView = getDefaultViewPage();

        // ----------------------------------
        // business logic
        // ----------------------------------

        // ranked results
        // --------------
        IngridHits rankedHits = null;
        int numberOfRankedHits = 0;
        try {
            // use cache if we don't query
            if (queryState.equals(Settings.MSGV_NO_QUERY) || queryState.equals(Settings.MSGV_UNRANKED_QUERY)) {
                rankedHits = (IngridHits) receiveRenderMessage(request, Settings.MSG_SEARCH_RESULT_RANKED);
            }
            if (rankedHits == null) {
                // copy IngridQuery, so we can manipulate it in ranked search without affecting unranked search
                IngridQuery rankedQuery = new IngridQuery();
                rankedQuery.putAll(query);
                rankedHits = doRankedSearch(rankedQuery, selectedDS, rankedStartHit,
                        Settings.SEARCH_RANKED_HITS_PER_PAGE);
                this.publishRenderMessage(request, Settings.MSG_SEARCH_RESULT_RANKED, rankedHits);
            }
            if (rankedHits != null) {
                numberOfRankedHits = (int) rankedHits.length();
            }
        } catch (Exception ex) {
            if (log.isErrorEnabled()) {
                log.error("Problems fetching RANKED hits ! hits = " + rankedHits + ", numHits = " + numberOfRankedHits,
                        ex);
            }
        }

        // adapt settings of ranked page navigation
        HashMap rankedPageNavigation = SearchState.getPageNavigation(rankedStartHit,
                Settings.SEARCH_RANKED_HITS_PER_PAGE, numberOfRankedHits, Settings.SEARCH_RANKED_NUM_PAGES_TO_SELECT);

        // unranked results
        // ----------------
        IngridHits unrankedHits = null;
        int numberOfUnrankedHits = 0;
        HashMap unrankedPageNavigation = null;

        // only do unranked stuff if necessary for view
        if (!currentView.equals(TEMPLATE_RESULT_ADDRESS)) {
            try {
                // use cache if we don't query
                if (queryState.equals(Settings.MSGV_NO_QUERY) || queryState.equals(Settings.MSGV_RANKED_QUERY)) {
                    unrankedHits = (IngridHits) receiveRenderMessage(request, Settings.MSG_SEARCH_RESULT_UNRANKED);
                }
                if (unrankedHits == null) {
                    // copy IngridQuery, so we can manipulate it in unranked search without affecting ranked search
                    IngridQuery unrankedQuery = new IngridQuery();
                    unrankedQuery.putAll(query);
                    unrankedHits = doUnrankedSearch(unrankedQuery, selectedDS, unrankedStartHit,
                            Settings.SEARCH_UNRANKED_HITS_PER_PAGE);
                    this.publishRenderMessage(request, Settings.MSG_SEARCH_RESULT_UNRANKED, unrankedHits);
                }
                if (unrankedHits != null) {
                    numberOfUnrankedHits = (int) unrankedHits.length();
                }
            } catch (Exception ex) {
                if (log.isErrorEnabled()) {
                    log.error("Problems fetching UNRANKED hits ! hits = " + unrankedHits + ", numHits = "
                            + numberOfUnrankedHits, ex);
                }
            }

            // adapt settings of unranked page navigation
            unrankedPageNavigation = SearchState.getPageNavigation(unrankedStartHit,
                    Settings.SEARCH_UNRANKED_HITS_PER_PAGE, numberOfUnrankedHits,
                    Settings.SEARCH_UNRANKED_NUM_PAGES_TO_SELECT);
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
        // check whether page navigation was clicked and adapt query state
        String rankedStarthit = request.getParameter(SearchState.PARAM_STARTHIT_RANKED);
        if (rankedStarthit != null) {
            publishRenderMessage(request, Settings.MSG_QUERY_STATE, Settings.MSGV_RANKED_QUERY);
        } else {
            String unrankedStarthit = request.getParameter(SearchState.PARAM_STARTHIT_UNRANKED);
            if (unrankedStarthit != null) {
                publishRenderMessage(request, Settings.MSG_QUERY_STATE, Settings.MSGV_UNRANKED_QUERY);
            }
        }

        // redirect to our page wih parameters for bookmarking
        actionResponse.sendRedirect(SearchState.PAGE_SEARCH_RESULT + SearchState.getURLParams(request));
    }

    private IngridHits doRankedSearch(IngridQuery query, String ds, int startHit, int hitsPerPage) {

        IngridHits hits = null;
        try {
            adaptRankedQuery(query, ds);

            if (log.isDebugEnabled()) {
                log.debug("doRankedSearch: IngridQuery = " + Utils.queryToString(query));
            }

            int currentPage = (int) (startHit / hitsPerPage) + 1;

            IBUSInterface ibus = IBUSInterfaceImpl.getInstance();
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
            // TODO: use getDetails() when it works !
            //            IngridHitDetail[] details = ibus.getDetails(results, query, requestedMetadata);

            IngridHit result = null;
            IngridHitDetail detail = null;
            String tmpString = null;
            for (int i = 0; i < results.length; i++) {
                try {
                    result = results[i];
                    //                    detail = details[i];
                    detail = ibus.getDetail(result, query, requestedMetadata);

                    if (result == null) {
                        continue;
                    }
                    if (detail != null) {
                        ibus.transferHitDetails(result, detail);
                        tmpString = detail.getIplugClassName();
                        if (tmpString == null) {
                            tmpString = "";
                            if (log.isErrorEnabled()) {
                                log.error("Hit Detail has no IplugClassName !!!! hit=" + result + ", detail=" + detail);
                            }
                        }

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

    private void adaptRankedQuery(IngridQuery query, String ds) {
        // first adapt selected search area in UI (ds) ! 
        Utils.processBasicDataTypes(query, ds);

        // TODO: adapt this to better structure of datatypes in future (search area, ranked field etc.)
        if (ds.equals(Settings.SEARCH_DATASOURCE_ENVINFO)) {
            // check for entered datatypes which lead to no results
            if (Utils.containsPositiveDataType(query, Settings.QVALUE_DATATYPE_G2K)
                    || Utils.containsPositiveDataType(query, Settings.QVALUE_DATATYPE_ADDRESS)) {
                // no results
                query.remove(Settings.QFIELD_DATATYPE);
                query
                        .addField(new FieldQuery(true, false, Settings.QFIELD_DATATYPE,
                                Settings.QVALUE_DATATYPE_NORESULTS));
            } else {
                // explicitly prohibit g2k
                query.addField(new FieldQuery(false, true, Settings.QFIELD_DATATYPE, Settings.QVALUE_DATATYPE_G2K));
            }
        } else if (ds.equals(Settings.SEARCH_DATASOURCE_ADDRESS)) {
            // check whether something different than address type was entered !
            String[] posDataTypes = query.getPositiveDataTypes();
            for (int i = 0; i < posDataTypes.length; i++) {
                if (!posDataTypes[i].equals(Settings.QVALUE_DATATYPE_ADDRESS)) {
                    // no address data type but we're in address area, show no results
                    query.remove(Settings.QFIELD_DATATYPE);
                    query.addField(new FieldQuery(true, false, Settings.QFIELD_DATATYPE,
                            Settings.QVALUE_DATATYPE_NORESULTS));
                }
            }
        } else if (ds.equals(Settings.SEARCH_DATASOURCE_RESEARCH)) {
        }
    }

    private IngridHits doUnrankedSearch(IngridQuery query, String ds, int startHit, int hitsPerPage) {

        IngridHits hits = null;
        try {
            adaptUnrankedQuery(query, ds);

            if (log.isDebugEnabled()) {
                log.debug("doUnrankedSearch: IngridQuery = " + Utils.queryToString(query));
            }

            int currentPage = (int) (startHit / hitsPerPage) + 1;

            IBUSInterface ibus = IBUSInterfaceImpl.getInstance();
            hits = ibus.search(query, hitsPerPage, currentPage, hitsPerPage, Settings.SEARCH_DEFAULT_TIMEOUT);
            IngridHit[] results = hits.getHits();
            String[] requestedMetadata = new String[0];

            // TODO: use getDetails() when it works !
            //IngridHitDetail[] details = ibus.getDetails(results, query, requestedMetadata);

            IngridHit result = null;
            IngridHitDetail detail = null;
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

    private void adaptUnrankedQuery(IngridQuery query, String ds) {
        // first adapt selected search area in UI (ds) ! 
        Utils.processBasicDataTypes(query, ds);

        // TODO: adapt this to better structure of datatypes in future (search area, ranked field etc.)
        if (ds.equals(Settings.SEARCH_DATASOURCE_ENVINFO)) {
            // unranked search result hack for checking datatypes, use "ranked" field in future 
            // check for positive data types and if set, check whether g2k should be displayed, if not
            // set no datatypes (no search)
            // NOTICE: unranked search isn't called in area "Adressen", only in "Umweltinfo", then no
            // positive data type is set per default ...
            boolean showG2K = true;
            String[] posDataTypes = query.getPositiveDataTypes();
            for (int i = 0; i < posDataTypes.length; i++) {
                if (!posDataTypes[i].equals(Settings.QVALUE_DATATYPE_G2K)) {
                    showG2K = false;
                    break;
                }
            }
            // remove all datatypes ! only show g2k
            query.remove(Settings.QFIELD_DATATYPE);
            if (showG2K) {
                query.addField(new FieldQuery(true, false, Settings.QFIELD_DATATYPE, Settings.QVALUE_DATATYPE_G2K));
            } else {
                // just to be sure there are NO RESULTS
                query
                        .addField(new FieldQuery(true, false, Settings.QFIELD_DATATYPE,
                                Settings.QVALUE_DATATYPE_NORESULTS));
            }
        } else if (ds.equals(Settings.SEARCH_DATASOURCE_ADDRESS)) {
            // no address data type in right column
            query.remove(Settings.QFIELD_DATATYPE);
            query.addField(new FieldQuery(true, false, Settings.QFIELD_DATATYPE, Settings.QVALUE_DATATYPE_NORESULTS));
        } else if (ds.equals(Settings.SEARCH_DATASOURCE_RESEARCH)) {
        }
    }
}