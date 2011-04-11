/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.search;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;

import javax.portlet.PortletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.portal.config.IngridSessionPreferences;
import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.global.IngridPersistencePrefs;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.search.net.QueryDescriptor;
import de.ingrid.utils.IngridQueryTools;
import de.ingrid.utils.query.ClauseQuery;
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
        	log.debug("The QueryString: " + queryString);
            query = QueryStringParser.parse(queryString);
        } catch (ParseException ex) {
            if (log.isErrorEnabled()) {
                log.error("Problems creating separate IngridQuery for ranked search, parsed query string: "
                        + queryString, ex);
            }
        }

        // get the datasource
        String ds = SearchState.getSearchStateObjectAsString(request, Settings.PARAM_DATASOURCE);

        // set search sources according to the persistence preferences
        // only for ds = PARAMV_DATASOURCE_ENVINFO AND if quer has no custom datatype AND query has no custom metaclass
        processQuerySources(request, ds, query);

        // add basic data type dependent from query and GUI ! may manipulate the query.
        // NOTICE: see http://jira.media-style.com/browse/INGRID-1076
        UtilsSearch.processBasicDataType(query, ds);

        // change datasource dependent from query input
        ds = UtilsSearch.determineFinalPortalDatasource(ds, query);

        // start hit
        int startHit = 0;
        String stateStartHit = SearchState.getSearchStateObjectAsString(request, Settings.PARAM_STARTHIT_RANKED);
        if (stateStartHit.length() != 0) {
            startHit = (new Integer(stateStartHit)).intValue();
        }

        int currentPage = (int) (startHit / Settings.SEARCH_RANKED_HITS_PER_PAGE) + 1;

        // always request ALL DATA !!! all kind of hits can be rendered in one page when datatypes are entered in query !
        String[] requestedMetadata = new String[] {
        		// udk object metadata
        		Settings.HIT_KEY_UDK_CLASS,
        		Settings.HIT_KEY_UDK_CLASS.toLowerCase(),
        		Settings.HIT_KEY_OBJ_ID,
        		Settings.HIT_KEY_OBJ_SERV_HAS_ACCESS_CONSTRAINT,
        		Settings.HIT_KEY_OBJ_SERV_TYPE,        		
        		Settings.HIT_KEY_OBJ_SERV_TYPE_KEY,        		
        		// udk address metadata
        		Settings.HIT_KEY_ADDRESS_CLASS,
        		Settings.HIT_KEY_ADDRESS_CLASS2,
        		Settings.HIT_KEY_ADDRESS_CLASS3,
        		Settings.HIT_KEY_ADDRESS_FIRSTNAME,
        		Settings.HIT_KEY_ADDRESS_LASTNAME,
        		Settings.HIT_KEY_ADDRESS_TITLE,
        		Settings.HIT_KEY_ADDRESS_ADDRESS,
        		Settings.HIT_KEY_ADDRESS_ADDRID,
        		Settings.HIT_KEY_ADDRESS_ADDRID2,
        		Settings.HIT_KEY_ADDRESS_ADDRID3,
        		Settings.HIT_KEY_ADDRESS_INSTITUTION2,
        		Settings.HIT_KEY_ADDRESS_INSTITUTION3,
        		Settings.HIT_KEY_ADDRESS_ADDR_FROM_ID,
        		Settings.HIT_KEY_ADDRESS_ADDR_FROM_ID3,
        		// both
        		Settings.HIT_KEY_WMS_URL,
        		Settings.RESULT_KEY_PARTNER,
        		Settings.RESULT_KEY_PROVIDER,
        		Settings.RESULT_KEY_WMS_TMP_COORD_X
        };
        
        // set properties according to the session preferences
        IngridSessionPreferences sessionPrefs = Utils.getSessionPreferences(request,
                IngridSessionPreferences.SESSION_KEY, IngridSessionPreferences.class);
        // set ranking ! ONLY IF NO RANKING IN Query String Input !
        if (!UtilsSearch.containsFieldOrKey(query, IngridQuery.RANKED)) {
            // adapt ranking to Search State
            String ranking = (String) sessionPrefs.get(IngridSessionPreferences.SEARCH_SETTING_RANKING);
            if (ranking == null || ranking.length() == 0) {
                ranking = IngridQuery.SCORE_RANKED;
                String stateRanking = (String) SearchState.getSearchStateObject(request, Settings.PARAM_RANKING);
                if (stateRanking != null) {
                    ranking = stateRanking;
                }
            }
            query.put(IngridQuery.RANKED, ranking);
        }

        // set filter params. If no filter is set, process GROUPING
        // FILTERING AND GROUPING are mutually exclusive 
        String filter = SearchState.getSearchStateObjectAsString(request, Settings.PARAM_FILTER);
        if (filter != null && filter.length() > 0) {
            String subject = SearchState.getSearchStateObjectAsString(request, Settings.PARAM_SUBJECT);
            if (filter.equals(Settings.PARAMV_GROUPING_PARTNER)) {
                if (!UtilsSearch.containsFieldOrKey(query, Settings.QFIELD_PARTNER)) {
                    // set filter for partner 
                    UtilsSearch.processPartner(query, new String[] { subject });
                }
            } else if (filter.equals(Settings.PARAMV_GROUPING_PROVIDER)) {
                if (!UtilsSearch.containsFieldOrKey(query, Settings.QFIELD_PROVIDER)) {
                    // set filter for provider 
                    UtilsSearch.processProvider(query, new String[] { subject });
                }
            } else if (filter.equals(Settings.PARAMV_GROUPING_DOMAIN)) {
                // set filter for domain 
                UtilsSearch.processDomain(query, subject);
            }
        } else {
            // no grouping when filter is set.
            // set GROUPING ! ONLY IF NO GROUPING IN Query String Input !
            if (!UtilsSearch.containsFieldOrKey(query, Settings.QFIELD_GROUPED)) {
            	
            	// IF NO GROUPING IN Query String input then use request parameters !!!
            	// bookmarking has higher prio than user preferences (see below) !
                String grouping = request.getParameter(Settings.PARAM_GROUPING);

                // If no grouping yet, adapt grouping to USER preferences
                if (grouping == null) {
                    grouping = (String) sessionPrefs.get(IngridSessionPreferences.SEARCH_SETTING_GROUPING);                	
                }

                // set grouping
                UtilsSearch.processGrouping(query, grouping);
            }
        }

        // adapt search state after processing grouping
        SearchState.adaptSearchState(request, Settings.PARAM_GROUPING, query.get(Settings.QFIELD_GROUPED));

        // process persistent partner/provider
        processQueryPartnerProvider(request, query);

        String inclMetaData = (String) sessionPrefs.get(IngridSessionPreferences.SEARCH_SETTING_INCL_META);
        if (inclMetaData != null && inclMetaData.equals("on")) {
            query.addField(new FieldQuery(true, false, Settings.QFIELD_INCL_META, "on"));
        }

        // if grouping, adapt search parameters to deliver the startHit for the next ibus query
        if (query.getGrouped() != null && query.getGrouped().length() > 0
                && !query.getGrouped().equals(IngridQuery.GROUPED_OFF)) {

            // get the current page number, default to 1
            int currentSelectorPage;
            try {
                currentSelectorPage =
                	(new Integer(request.getParameter(Settings.PARAM_CURRENT_SELECTOR_PAGE))).intValue();
            } catch (Exception ex) {
                currentSelectorPage = 1;
            }

            // get the grouping starthits history from session
            // create and initialize if not exists
            ArrayList groupedStartHits = null;
            groupedStartHits = (ArrayList) SearchState.getSearchStateObject(request, Settings.PARAM_GROUPING_STARTHITS);
            if (groupedStartHits == null) {
                groupedStartHits = new ArrayList();
                SearchState.adaptSearchState(request, Settings.PARAM_GROUPING_STARTHITS, groupedStartHits);
            }
            // ensure correct size of Array ! Notice: currentSelectorPage is 1 for first page !
        	while (currentSelectorPage >= groupedStartHits.size()) {
        		groupedStartHits.add(new Integer(0));
        	}

            currentPage = 0;
            startHit = ((Integer) groupedStartHits.get(currentSelectorPage - 1)).intValue();
        }

        // set language for query
        if (!UtilsSearch.containsFieldOrKey(query, Settings.QFIELD_LANG)) {
            //            UtilsSearch.processLanguage(query, request.getLocale());
        }
        
        // add partner to query if the portal is restricted to a certain partner
        UtilsSearch.processRestrictingPartners(query);
        
        // add provider to the query if the provider was selected in simple search form
        UtilsSearch.processRestrictingProvider(query, (String)sessionPrefs.get(IngridSessionPreferences.RESTRICTING_PROVIDER));

        //      TODO If no query should be submitted, return null
        // now with PlugDescription for determining type of Hit (Address, Object)
        return new QueryDescriptor(query, Settings.SEARCH_RANKED_HITS_PER_PAGE, currentPage, startHit, PortalConfig
        		.getInstance().getInt(PortalConfig.QUERY_TIMEOUT_RANKED, 30000), true, true, requestedMetadata);
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
        // NOTICE: we don't copy it from IngridQuery in state, would be only shallow copy (putAll())
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

        // get the datasource
        String ds = SearchState.getSearchStateObjectAsString(request, Settings.PARAM_DATASOURCE);

        // set search sources according to the persistence preferences
        // only for ds = PARAMV_DATASOURCE_ENVINFO AND if query has no custom datatype AND query has no custom metaclass
        processQuerySources(request, ds, query);

        // add basic data type dependent from query and GUI ! may manipulate the query.
        // NOTICE: see http://jira.media-style.com/browse/INGRID-1076
        UtilsSearch.processBasicDataType(query, ds);

        // change datasource dependent from query input
        ds = UtilsSearch.determineFinalPortalDatasource(ds, query);

        // start hit
        int startHit = 0;
        String stateStartHit = SearchState.getSearchStateObjectAsString(request, Settings.PARAM_STARTHIT_UNRANKED);
        if (stateStartHit.length() != 0) {
            startHit = (new Integer(stateStartHit)).intValue();
        }

        int currentPage = (int) (startHit / Settings.SEARCH_UNRANKED_HITS_PER_PAGE) + 1;

        // set ranking ! ONLY IF NO RANKING IN Query String Input !
        if (!UtilsSearch.containsFieldOrKey(query, IngridQuery.RANKED)) {
            // switch ranking OFF
            query.put(IngridQuery.RANKED, IngridQuery.NOT_RANKED);
        }
        // also add flag determining whether iPlugs with no results should be delivered !
        if (PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_ENABLE_SEARCH_RESULTS_UNRANKED_ALLIPLUGS, false)) {
            // deliver also dummy hit of iPlugs with no result
            query.addField(new FieldQuery(true, false, IngridQuery.GET_UNRANKED_IPLUGS_WITH_NO_RESULTS, "true"));
        }

        // set filter params. 
        String filter = SearchState.getSearchStateObjectAsString(request, Settings.PARAM_FILTER);
        if (filter != null && filter.length() > 0) {
            String subject = SearchState.getSearchStateObjectAsString(request, Settings.PARAM_SUBJECT);
            if (filter.equals(Settings.RESULT_KEY_PARTNER)) {
                if (!UtilsSearch.containsFieldOrKey(query, Settings.QFIELD_PARTNER)) {
                    // set filter for partner 
                    UtilsSearch.processPartner(query, new String[] { subject });
                }
            } else if (filter.equals(Settings.RESULT_KEY_PROVIDER)) {
                if (!UtilsSearch.containsFieldOrKey(query, Settings.QFIELD_PROVIDER)) {
                    // set filter for provider 
                    UtilsSearch.processProvider(query, new String[] { subject });
                }
            } else if (filter.equals(Settings.RESULT_KEY_PLUG_ID)) {
                if (!UtilsSearch.containsFieldOrKey(query, Settings.QFIELD_PLUG_ID)) {
                    // set filter for iplugs 
                    UtilsSearch.processIPlugs(query, new String[] { subject });
                }
            }
        }

        // ALWAYS GROUP BY iPlugs if no iPlug filter !
        if (filter == null || !filter.equals(Settings.RESULT_KEY_PLUG_ID)) {
            if (!UtilsSearch.containsFieldOrKey(query, Settings.QFIELD_GROUPED)) {
                // grouping per iPlug !
                query.put(Settings.QFIELD_GROUPED, IngridQuery.GROUPED_BY_PLUGID);
            }
        }

        // process persistent partner/provider
        processQueryPartnerProvider(request, query);

        // Compute the new start hit for the always groued unranked query

        // get the current page number, default to 1
        int currentSelectorPage;
        try {
            currentSelectorPage = (new Integer(request.getParameter(Settings.PARAM_CURRENT_SELECTOR_PAGE_UNRANKED)))
                    .intValue();
        } catch (Exception ex) {
            currentSelectorPage = 1;
        }

        // get the grouping starthits history from session
        // create and initialize if not exists
        ArrayList groupedStartHits = null;
        groupedStartHits = (ArrayList) SearchState.getSearchStateObject(request,
                Settings.PARAM_GROUPING_STARTHITS_UNRANKED);
        if (groupedStartHits == null) {
            groupedStartHits = new ArrayList();
            groupedStartHits.add(new Integer(0));
            SearchState.adaptSearchState(request, Settings.PARAM_GROUPING_STARTHITS_UNRANKED, groupedStartHits);
        }
        // set current page to 0 when the unranked search is showing a page > 0
        // this will be ignored for showing the first page AND 
        // navigate in the iplug detail page 'show all result of iplug'
        if (currentSelectorPage > 1) {
            currentPage = 0;
        }
        
        int newStartHit;
        // set new starthit to next expected result
        // if we navigate through the iplug detail page 'show all result of iplug' use the former calulated start hit as a start
        // otherwise use the grouped hit history to obtain the nest start hit.
        if (filter != null && filter.equals(Settings.RESULT_KEY_PLUG_ID)) {
            newStartHit = startHit;
        } else {
            newStartHit = ((Integer) groupedStartHits.get(currentSelectorPage - 1)).intValue();
        }

        // set language for query
        if (!UtilsSearch.containsFieldOrKey(query, Settings.QFIELD_LANG)) {
            //            UtilsSearch.processLanguage(query, request.getLocale());
        }

        UtilsSearch.processRestrictingPartners(query);

        // add provider to the query if the provider was selected in simple search form
        IngridSessionPreferences sessionPrefs = Utils.getSessionPreferences(request,
                IngridSessionPreferences.SESSION_KEY, IngridSessionPreferences.class);
        UtilsSearch.processRestrictingProvider(query, (String)sessionPrefs.get(IngridSessionPreferences.RESTRICTING_PROVIDER));

        // need at least the docuuid to create a correct link if igc is connected
        String[] requestedMetadata = new String[] {
                // udk object metadata
                Settings.HIT_KEY_OBJ_ID,
                Settings.HIT_KEY_UDK_CLASS,
                Settings.HIT_KEY_UDK_CLASS.toLowerCase(),
        };
        
        // TODO If no query should be submitted, return null
        return new QueryDescriptor(query, Settings.SEARCH_UNRANKED_HITS_PER_PAGE, currentPage, newStartHit,
                PortalConfig.getInstance().getInt(PortalConfig.QUERY_TIMEOUT_UNRANKED, 120000), true, true, requestedMetadata);
    }

    private static void processQuerySources(PortletRequest request, String ds, IngridQuery query) {
        // set search sources according to the persistence preferences
        // only for ds = PARAMV_DATASOURCE_ENVINFO AND if quer has no custom datatype AND query has no custom metaclass
        if (ds.equals(Settings.PARAMV_DATASOURCE_ENVINFO) && query.getDataTypes().length == 0
                && !query.containsField(Settings.QFIELD_METACLASS)) {
            Principal principal = request.getUserPrincipal();
            if (principal != null) {
                HashMap searchSources = (HashMap) IngridPersistencePrefs.getPref(principal.getName(),
                        IngridPersistencePrefs.SEARCH_SOURCES);
                if (searchSources != null
                        && searchSources.get("sources") != null
                        && searchSources.get("meta") != null
                        && (((String[]) searchSources.get("sources")).length > 0 || ((String[]) searchSources
                                .get("meta")).length > 0)) {
                    String qStr = UtilsSearch.processSearchSources("", (String[]) searchSources.get("sources"),
                            (String[]) searchSources.get("meta"));
                    try {
                        IngridQuery q = QueryStringParser.parse(qStr);
                        // add datatypes
                        FieldQuery[] datatypes = q.getDataTypes();
                        for (int i = 0; i < datatypes.length; i++) {
                            FieldQuery datatype = datatypes[i];
                            // add datatype if it does not already exist in the query
                            //                            if ((!datatype.isProhibited() && !UtilsSearch.hasPositiveDataType(query,datatype.getFieldName())) 
                            //                                    || (datatype.isProhibited() && !UtilsSearch.hasPositiveDataType(query,datatype.getFieldName()))) {
                            query.addField(datatype);
                            //                            }
                        }
                        // add metaclass
                        //check for field metaclass (if only one metaclass was selected)
                        if (q.containsField(Settings.QFIELD_METACLASS)) {
                            FieldQuery[] metaclassFields = UtilsSearch.getField(q, Settings.QFIELD_METACLASS);
                            for (int i = 0; i < metaclassFields.length; i++) {
                                query.addField(metaclassFields[i]);
                            }
                            // if more metaclasses are selected, they are in a clause query 
                        } else {
                            ClauseQuery[] clauses = q.getClauses();
                            for (int i = 0; i < clauses.length; i++) {
                                query.addClause(clauses[i]);
                            }
                        }

                    } catch (ParseException e) {
                        log.error("Error parsing sources query string.", e);
                    }
                }
            }
        }
    }

    /**
     * Add persistent partner/provider preferences.
     *  - ONLY if no partner/provider is added manually or by filter
     * 
     * @param request
     * @param query
     */
    public static void processQueryPartnerProvider(PortletRequest request, IngridQuery query) {
        Principal principal = request.getUserPrincipal();
        if (principal != null && query.getPositivePartner().length == 0 && query.getNegativePartner().length == 0
                && query.getPositiveProvider().length == 0 && query.getNegativeProvider().length == 0) {
            String searchPartnerStr = (String) IngridPersistencePrefs.getPref(principal.getName(),
                    IngridPersistencePrefs.SEARCH_PARTNER);
            if (searchPartnerStr != null && searchPartnerStr.length() > 0) {
                try {
                    IngridQuery q = QueryStringParser.parse(searchPartnerStr);
                    query.put(IngridQuery.PARTNER, q.get(IngridQuery.PARTNER));
                    query.put(IngridQuery.PROVIDER, q.get(IngridQuery.PROVIDER));
                } catch (ParseException e) {
                    log.error("Error parsing sources query string.", e);
                }

            }
        }
    }

    
    /**
     * Add persistent partner preferences.
     *  - ONLY if no partner/provider is added manually or by filter
     * 
     * @param request
     * @param query
     */
    public static void processQueryPartner(PortletRequest request, IngridQuery query) {
        Principal principal = request.getUserPrincipal();
        if (principal != null && query.getPositivePartner().length == 0 && query.getNegativePartner().length == 0
                && query.getPositiveProvider().length == 0 && query.getNegativeProvider().length == 0) {
            String searchPartnerStr = (String) IngridPersistencePrefs.getPref(principal.getName(),
                    IngridPersistencePrefs.SEARCH_PARTNER);
            if (searchPartnerStr != null && searchPartnerStr.length() > 0) {
                try {
                    IngridQuery q = QueryStringParser.parse(searchPartnerStr);
                    query.put(IngridQuery.PARTNER, q.get(IngridQuery.PARTNER));
                } catch (ParseException e) {
                    log.error("Error parsing sources query string.", e);
                }

            }
        }
    }
    
    /**
     * Get a key (actually a hash-code) to a query which can be used for caching.
     * Since the original query contains some data that changes with every new query,
     * it cannot be compared with an older one. These data will be manipulated
     * and a hash-code will be created.
     * @param query, is the original IngridQuery
     * @param postfix, contains extra data
     * @return the hash-code of the manipulated query
     */
    public static int getQueryCacheKey(IngridQuery query, String postfix) {
    	IngridQueryTools queryTool = new IngridQueryTools();
    	String cacheKey = "";
    	
    	String stringQuery = UtilsSearch.queryToString(query);
    	cacheKey = queryTool.getComparableString(stringQuery,postfix);
    	
    	return cacheKey.hashCode();    	
    }
    
}
