/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.search;

import javax.portlet.PortletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.search.net.QueryDescriptor;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.ParseException;
import de.ingrid.utils.queryparser.QueryStringParser;

/**
 * TODO Describe your created type (class, etc.) here.
 *
 * @author joachim@wemove.com
 */
public class QueryPreProcessor {

    private final static Log log = LogFactory.getLog(QueryPreProcessor.class);

    /**
     * Prepares an ranked query for submitting to the ibus. If no query should be submitted,
     * return null.
     * 
     * @param myQuery The query to submit.
     * @param ds The datasource type of the query.
     * @param startHit The hit count to start with.
     * @return The QueryDescriptor describing the query or null if no query should be submitted.
     */
    public static QueryDescriptor createRankedQueryDescriptor(PortletRequest request) {
        // create new IngridQuery, so we can manipulate it in ranked search without affecting unranked search
        // NOTICE: we don't copy it from IngridQuery in state, would be only shallow copy (putAll()), but
        // we won't complete copy
        String queryString = SearchState.getSearchStateObjectAsString(request, Settings.PARAM_QUERY_STRING);
        IngridQuery query = null;
        try {
            query = QueryStringParser.parse(queryString);
        } catch (ParseException ex) {
            if (log.isErrorEnabled()) {
                log.error("Problems creating separate IngridQuery for ranked search, parsed query string: "
                        + queryString, ex);
            }
        }

        // set basic datatype according to GUI ! ONLY IF NO DATATYPE IN Query String Input !
        String ds = SearchState.getSearchStateObjectAsString(request, Settings.PARAM_DATASOURCE);
        if (!UtilsSearch.containsField(query, Settings.QFIELD_DATATYPE)) {
            UtilsSearch.processBasicDataTypes(query, ds);
        }

        // start hit
        int startHit = 0;
        String stateStartHit = SearchState.getSearchStateObjectAsString(request, Settings.PARAM_STARTHIT_RANKED);
        if (stateStartHit.length() != 0) {
            startHit = (new Integer(stateStartHit)).intValue();
        }

        int currentPage = (int) (startHit / Settings.SEARCH_RANKED_HITS_PER_PAGE) + 1;

        String[] requestedMetadata = null;
        if (ds.equals(Settings.PARAMV_DATASOURCE_ENVINFO)) {
            requestedMetadata = new String[2];
            requestedMetadata[0] = Settings.HIT_KEY_WMS_URL;
            requestedMetadata[1] = Settings.HIT_KEY_UDK_CLASS;
        } else if (ds.equals(Settings.PARAMV_DATASOURCE_ADDRESS)) {
            requestedMetadata = new String[7];
            requestedMetadata[0] = Settings.HIT_KEY_WMS_URL;
            requestedMetadata[1] = Settings.HIT_KEY_ADDRESS_CLASS;
            requestedMetadata[2] = Settings.HIT_KEY_ADDRESS_FIRSTNAME;
            requestedMetadata[3] = Settings.HIT_KEY_ADDRESS_LASTNAME;
            requestedMetadata[4] = Settings.HIT_KEY_ADDRESS_TITLE;
            requestedMetadata[5] = Settings.HIT_KEY_ADDRESS_ADDRESS;
            requestedMetadata[6] = Settings.HIT_KEY_ADDRESS_ADDRID;
        }

        // set ranking ! ONLY IF NO RANKING IN Query String Input !
        if (!UtilsSearch.containsField(query, IngridQuery.RANKED)) {
            // adapt ranking to Search State
            Object ranking = IngridQuery.SCORE_RANKED;
            Object stateRanking = SearchState.getSearchStateObject(request, Settings.PARAM_RANKING);
            if (stateRanking != null) {
                ranking = stateRanking;
            }
            query.put(IngridQuery.RANKED, ranking);
        }

        //      TODO If no query should be submitted, return null
        return new QueryDescriptor(query, Settings.SEARCH_RANKED_HITS_PER_PAGE, currentPage,
                Settings.SEARCH_RANKED_HITS_PER_PAGE, PortalConfig.getInstance().getInt(
                        PortalConfig.QUERY_TIMEOUT_RANKED, 30000), true, requestedMetadata);
    }

    /**
     * Prepares an unranked query for submitting to the ibus. If no query should be submitted,
     * return null.
     * 
     * @param myQuery The query to submit.
     * @param ds The datasource type of the query.
     * @param startHit The hit count to start with.
     * @return The QueryDescriptor describing the query or null if no query should be submitted.
     */
    public static QueryDescriptor createUnrankedQueryDescriptor(PortletRequest request) {
        // create new IngridQuery, so we can manipulate it in ranked search without affecting unranked search
        // NOTICE: we don't copy it from IngridQuery in state, would be only shallow copy (putAll()), but
        // we won't complete copy
        String queryString = SearchState.getSearchStateObjectAsString(request, Settings.PARAM_QUERY_STRING);
        IngridQuery query = null;
        try {
            query = QueryStringParser.parse(queryString);
        } catch (ParseException ex) {
            if (log.isErrorEnabled()) {
                log.error("Problems creating separate IngridQuery for ranked search, parsed query string: "
                        + queryString, ex);
            }
        }

        // set basic datatype according to GUI ! ONLY IF NO DATATYPE IN Query String Input !
        if (!UtilsSearch.containsField(query, Settings.QFIELD_DATATYPE)) {
            String ds = SearchState.getSearchStateObjectAsString(request, Settings.PARAM_DATASOURCE);
            UtilsSearch.processBasicDataTypes(query, ds);
        }

        // start hit
        int startHit = 0;
        String stateStartHit = SearchState.getSearchStateObjectAsString(request, Settings.PARAM_STARTHIT_UNRANKED);
        if (stateStartHit.length() != 0) {
            startHit = (new Integer(stateStartHit)).intValue();
        }

        int currentPage = (int) (startHit / Settings.SEARCH_UNRANKED_HITS_PER_PAGE) + 1;

        // set ranking ! ONLY IF NO RANKING IN Query String Input !
        if (!UtilsSearch.containsField(query, IngridQuery.RANKED)) {
            // switch ranking OFF
            query.put(IngridQuery.RANKED, IngridQuery.NOT_RANKED);
        }

        // TODO If no query should be submitted, return null
        return new QueryDescriptor(query, Settings.SEARCH_UNRANKED_HITS_PER_PAGE, currentPage,
                Settings.SEARCH_UNRANKED_HITS_PER_PAGE, PortalConfig.getInstance().getInt(
                        PortalConfig.QUERY_TIMEOUT_UNRANKED, 120000), true, null);
    }

}
