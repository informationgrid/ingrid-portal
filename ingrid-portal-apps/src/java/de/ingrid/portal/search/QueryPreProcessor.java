/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.search;

import java.util.ArrayList;

import javax.portlet.PortletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.portal.config.IngridSessionPreferences;
import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.forms.SearchSettingsForm;
import de.ingrid.portal.forms.ServiceSearchForm;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.search.net.QueryDescriptor;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.query.FieldQuery;
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
            requestedMetadata = new String[4];
            requestedMetadata[0] = Settings.HIT_KEY_WMS_URL;
            requestedMetadata[1] = Settings.HIT_KEY_UDK_CLASS;
            requestedMetadata[2] = Settings.RESULT_KEY_PARTNER;
            requestedMetadata[3] = Settings.RESULT_KEY_PROVIDER;
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


        // set properties according to the session preferences
        IngridSessionPreferences sessionPrefs = Utils.getSessionPreferences(request, IngridSessionPreferences.SESSION_KEY, IngridSessionPreferences.class);
        // set ranking ! ONLY IF NO RANKING IN Query String Input !
        if (!UtilsSearch.containsField(query, IngridQuery.RANKED)) {
            // adapt ranking to Search State
            String ranking = (String)sessionPrefs.get(IngridSessionPreferences.SEARCH_SETTING_RANKING);
            if (ranking == null || ranking.length() == 0) {
                ranking = IngridQuery.SCORE_RANKED;
                String stateRanking = (String)SearchState.getSearchStateObject(request, Settings.PARAM_RANKING);
                if (stateRanking != null) {
                    ranking = stateRanking;
                }
            }
            query.put(IngridQuery.RANKED, ranking);
        }

        // set filter params. If no filter is set, process grouping
        // FILTERING AND GROUPING are mutually exclusive 
        String filter = SearchState.getSearchStateObjectAsString(request, Settings.PARAM_FILTER);
        if (filter != null && filter.length() > 0) {
            String subject = SearchState.getSearchStateObjectAsString(request, Settings.PARAM_SUBJECT);
            if (filter.equals(Settings.PARAMV_GROUPING_PARTNER)) {
                if (!UtilsSearch.containsField(query, Settings.QFIELD_PARTNER)) {
                    // set filter for partner 
                    UtilsSearch.processPartner(query, new String[] {subject});
                }
            } else if (filter.equals(Settings.PARAMV_GROUPING_PROVIDER)) {
                if (!UtilsSearch.containsField(query, Settings.QFIELD_PROVIDER)) {
                    // set filter for provider 
                    UtilsSearch.processProvider(query, new String[] {subject});
                }
            }
            
        } else {
            // no grouping when filter is set.
            // set grouping ! ONLY IF NO GROUPING IN Query String Input !
            if (!UtilsSearch.containsField(query, Settings.QFIELD_GROUPED)) {
                // adapt ranking to Search State
                String grouping = (String)sessionPrefs.get(IngridSessionPreferences.SEARCH_SETTING_GROUPING);
                
                // set grouping
                UtilsSearch.processGrouping(query, grouping);
            }
        }
        
        
        String inclMetaData = (String)sessionPrefs.get(IngridSessionPreferences.SEARCH_SETTING_INCL_META);
        if (inclMetaData != null && inclMetaData.equals("on")) {
            query.addField(new FieldQuery(true, false, Settings.QFIELD_INCL_META, "on"));
        }
        
        // if grouping, adapt search parameters to deliver the startHit for the next ibus query
        int newStartHit = 0;
        if (query.getGrouped() != null && query.getGrouped().length() > 0 && !query.getGrouped().equals(IngridQuery.GROUPED_OFF)) {
            
            // get the current page number, default to 1
            int currentSelectorPage;
            try {
                currentSelectorPage = (new Integer(request.getParameter(Settings.PARAM_CURRENT_SELECTOR_PAGE))).intValue();
            } catch (Exception ex) {
                currentSelectorPage = 1;
            }

            // get the grouping starthits history from session
            // create and initialize if not exists
            ArrayList groupedStartHits = null;
            groupedStartHits = (ArrayList)SearchState.getSearchStateObject(request, Settings.PARAM_GROUPING_STARTHITS);
            if (groupedStartHits == null) {
                groupedStartHits = new ArrayList();
                groupedStartHits.add(new Integer(0));
                SearchState.adaptSearchState(request, Settings.PARAM_GROUPING_STARTHITS, groupedStartHits);
            }
            currentPage = 0;
            newStartHit = ((Integer)groupedStartHits.get(currentSelectorPage - 1)).intValue();
        }
        
        //      TODO If no query should be submitted, return null
        return new QueryDescriptor(query, Settings.SEARCH_RANKED_HITS_PER_PAGE, currentPage,
                newStartHit, PortalConfig.getInstance().getInt(
                        PortalConfig.QUERY_TIMEOUT_RANKED, 30000), true, false, requestedMetadata);
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

        // set filter params. If no filter is set, process grouping
        // FILTERING AND GROUPING are mutually exclusive 
        String filter = SearchState.getSearchStateObjectAsString(request, Settings.PARAM_FILTER);
        if (filter != null && filter.length() > 0) {
            String subject = SearchState.getSearchStateObjectAsString(request, Settings.PARAM_SUBJECT);
            if (filter.equals(Settings.RESULT_KEY_PARTNER)) {
                if (!UtilsSearch.containsField(query, Settings.QFIELD_PARTNER)) {
                    // set filter for partner 
                    UtilsSearch.processPartner(query, new String[] {subject});
                }
            } else if (filter.equals(Settings.RESULT_KEY_PROVIDER)) {
                if (!UtilsSearch.containsField(query, Settings.QFIELD_PROVIDER)) {
                    // set filter for provider 
                    UtilsSearch.processProvider(query, new String[] {subject});
                }
            } else if (filter.equals(Settings.RESULT_KEY_PLUG_ID)) {
                if (!UtilsSearch.containsField(query, Settings.QFIELD_PLUG_ID)) {
                    // set filter for iplugs 
                    UtilsSearch.processIPlugs(query, new String[] {subject});
                }
            }
        } else {
            if (!UtilsSearch.containsField(query, Settings.QFIELD_GROUPED)) {
                // grouping per iPlug !
                query.put(Settings.QFIELD_GROUPED, IngridQuery.GROUPED_BY_PLUGID);
            }            
        }
        
        // TODO If no query should be submitted, return null
        return new QueryDescriptor(query, Settings.SEARCH_UNRANKED_HITS_PER_PAGE, currentPage,
                Settings.SEARCH_UNRANKED_HITS_PER_PAGE, PortalConfig.getInstance().getInt(
                        PortalConfig.QUERY_TIMEOUT_UNRANKED, 120000), true, true, null);
    }

}
