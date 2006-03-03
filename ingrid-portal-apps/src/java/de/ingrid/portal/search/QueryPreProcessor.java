/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.search;

import de.ingrid.portal.global.Settings;
import de.ingrid.portal.search.net.QueryDescriptor;
import de.ingrid.utils.query.FieldQuery;
import de.ingrid.utils.query.IngridQuery;

/**
 * TODO Describe your created type (class, etc.) here.
 *
 * @author joachim@wemove.com
 */
public class QueryPreProcessor {
    
    /**
     * Prepares an ranked query for submitting to the ibus. If no query should be submitted,
     * return null.
     * 
     * @param myQuery The query to submit.
     * @param ds The datasource type of the query.
     * @param startHit The hit count to start with.
     * @return The QueryDescriptor describing the query or null if no query should be submitted.
     */
    public static QueryDescriptor createRankedQueryDescriptor(IngridQuery myQuery, String ds, int startHit) {
        // copy IngridQuery, so we can manipulate it in ranked search without affecting unranked search
        IngridQuery query = new IngridQuery();
        query.putAll(myQuery);        
        // first adapt selected search area in UI (ds) ! 
        UtilsSearch.processBasicDataTypes(query, ds);

        // TODO: adapt this to better structure of datatypes in future (search area, ranked field etc.)
        if (ds.equals(Settings.PARAMV_DATASOURCE_ENVINFO)) {
            // check for entered datatypes which lead to no results
            if (UtilsSearch.containsPositiveDataType(query, Settings.QVALUE_DATATYPE_G2K)
                    || UtilsSearch.containsPositiveDataType(query, Settings.QVALUE_DATATYPE_ADDRESS)) {
                // no results
                query.remove(Settings.QFIELD_DATATYPE);
                query
                        .addField(new FieldQuery(true, false, Settings.QFIELD_DATATYPE,
                                Settings.QVALUE_DATATYPE_NORESULTS));
            } else {
                // explicitly prohibit g2k
                query.addField(new FieldQuery(false, true, Settings.QFIELD_DATATYPE, Settings.QVALUE_DATATYPE_G2K));
            }
        } else if (ds.equals(Settings.PARAMV_DATASOURCE_ADDRESS)) {
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
        } else if (ds.equals(Settings.PARAMV_DATASOURCE_RESEARCH)) {
        }

        int currentPage = (int) (startHit / Settings.SEARCH_RANKED_HITS_PER_PAGE) + 1;

        String[] requestedMetadata = null;
        if (ds.equals(Settings.PARAMV_DATASOURCE_ENVINFO)) {
            requestedMetadata = new String[2];
            requestedMetadata[0] = Settings.HIT_KEY_WMS_URL;
            requestedMetadata[1] = Settings.HIT_KEY_UDK_CLASS;
        } else if (ds.equals(Settings.PARAMV_DATASOURCE_ADDRESS)) {
            requestedMetadata = new String[2];
            requestedMetadata[0] = Settings.HIT_KEY_WMS_URL;
            requestedMetadata[1] = Settings.HIT_KEY_ADDRESS_CLASS;
        }
//      TODO If no query should be submitted, return null
        return new QueryDescriptor(query, Settings.SEARCH_RANKED_HITS_PER_PAGE, currentPage, Settings.SEARCH_RANKED_HITS_PER_PAGE, 5000, true, requestedMetadata);
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
    public static QueryDescriptor createUnrankedQueryDescriptor(IngridQuery myQuery, String ds, int startHit) {
        // copy IngridQuery, so we can manipulate it in ranked search without affecting unranked search
        IngridQuery query = new IngridQuery();
        query.putAll(myQuery);        

        // first adapt selected search area in UI (ds) ! 
        UtilsSearch.processBasicDataTypes(query, ds);

        // TODO: adapt this to better structure of datatypes in future (search area, ranked field etc.)
        if (ds.equals(Settings.PARAMV_DATASOURCE_ENVINFO)) {
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
        } else if (ds.equals(Settings.PARAMV_DATASOURCE_ADDRESS)) {
            // no address data type in right column
            query.remove(Settings.QFIELD_DATATYPE);
            query.addField(new FieldQuery(true, false, Settings.QFIELD_DATATYPE, Settings.QVALUE_DATATYPE_NORESULTS));
        } else if (ds.equals(Settings.PARAMV_DATASOURCE_RESEARCH)) {
        }        

        int currentPage = (int) (startHit / Settings.SEARCH_UNRANKED_HITS_PER_PAGE) + 1;
        
        // TODO If no query should be submitted, return null
        return new QueryDescriptor(query, Settings.SEARCH_UNRANKED_HITS_PER_PAGE, currentPage, Settings.SEARCH_UNRANKED_HITS_PER_PAGE, 5000, true, null);
    }    

}
