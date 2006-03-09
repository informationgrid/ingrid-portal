/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.search;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.global.UtilsDB;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.query.FieldQuery;
import de.ingrid.utils.query.IngridQuery;

/**
 * Global STATIC data and utility methods for SEARCH !
 * 
 * @author Martin Maidhof
 */
public class UtilsSearch {

    private final static Log log = LogFactory.getLog(UtilsSearch.class);

    /**
     * Generate PageNavigation data for rendering
     * @param startHit
     * @param hitsPerPage
     * @param numberOfHits
     * @param numSelectorPages
     * @return
     */
    public static HashMap getPageNavigation(int startHit, int hitsPerPage, int numberOfHits, int numSelectorPages) {

        // pageSelector
        int currentSelectorPage = 0;
        int numberOfPages = 0;
        int firstSelectorPage = 0;
        int lastSelectorPage = 0;
        boolean selectorHasPreviousPage = false;
        boolean selectorHasNextPage = false;
        int numberOfFirstHit = 0;
        int numberOfLastHit = 0;

        if (numberOfHits != 0) {
            currentSelectorPage = startHit / hitsPerPage + 1;
            numberOfPages = numberOfHits / hitsPerPage;
            if (Math.ceil(numberOfHits % hitsPerPage) > 0) {
                numberOfPages++;
            }
            firstSelectorPage = 1;
            selectorHasPreviousPage = false;
            if (currentSelectorPage >= numSelectorPages) {
                firstSelectorPage = currentSelectorPage - (int) (numSelectorPages / 2);
                selectorHasPreviousPage = true;
            }
            lastSelectorPage = firstSelectorPage + numSelectorPages - 1;
            selectorHasNextPage = true;
            if (numberOfPages <= lastSelectorPage) {
                lastSelectorPage = numberOfPages;
                selectorHasNextPage = false;
            }
            numberOfFirstHit = (currentSelectorPage - 1) * hitsPerPage + 1;
            numberOfLastHit = numberOfFirstHit + hitsPerPage - 1;

            if (numberOfLastHit > numberOfHits) {
                numberOfLastHit = numberOfHits;
            }
        }

        HashMap pageSelector = new HashMap();
        pageSelector.put("currentSelectorPage", new Integer(currentSelectorPage));
        pageSelector.put("numberOfPages", new Integer(numberOfPages));
        pageSelector.put("numberOfSelectorPages", new Integer(numSelectorPages));
        pageSelector.put("firstSelectorPage", new Integer(firstSelectorPage));
        pageSelector.put("lastSelectorPage", new Integer(lastSelectorPage));
        pageSelector.put("selectorHasPreviousPage", new Boolean(selectorHasPreviousPage));
        pageSelector.put("selectorHasNextPage", new Boolean(selectorHasNextPage));
        pageSelector.put("hitsPerPage", new Integer(hitsPerPage));
        pageSelector.put("numberOfFirstHit", new Integer(numberOfFirstHit));
        pageSelector.put("numberOfLastHit", new Integer(numberOfLastHit));
        pageSelector.put("numberOfHits", new Integer(numberOfHits));

        return pageSelector;
    }

    /**
     * Transfer commonly used detail parameters from detail object to hitobject.
     * @param hit
     * @param detail
     */
    public static void transferHitDetails(IngridHit result, IngridHitDetail detail) {
        try {
            result.put(Settings.RESULT_KEY_TITLE, detail.getTitle());
            result.put(Settings.RESULT_KEY_ABSTRACT, detail.getSummary());
            result.put(Settings.RESULT_KEY_DOC_ID, new Integer(result.getDocumentId()));
            result.put(Settings.RESULT_KEY_PROVIDER, detail.getOrganisation());
            result.put(Settings.RESULT_KEY_SOURCE, detail.getDataSourceName());
            result.put(Settings.RESULT_KEY_PLUG_ID, detail.getPlugId());

            if (detail.get(Settings.RESULT_KEY_URL) != null) {
                result.put(Settings.RESULT_KEY_URL, detail.get(Settings.RESULT_KEY_URL));
                result.put(Settings.RESULT_KEY_URL_STR, Utils.getShortURLStr((String) detail
                        .get(Settings.RESULT_KEY_URL), 80));
            }
            // Partner
            Object values = UtilsSearch.getDetailMultipleValues(detail, Settings.RESULT_KEY_PARTNER); 
            if (values != null) {
                result.put(Settings.RESULT_KEY_PARTNER, UtilsDB.getPartnerFromKey(values.toString()));                
            }
/*            
            // detail values as ArrayLists !
            // Hit URL
            Object values = Utils.getDetailMultipleValues(detail, Settings.RESULT_KEY_URL); 
            if (values != null) {
                result.put(Settings.RESULT_KEY_URL, values);
                result.put(Settings.RESULT_KEY_URL_STR, Utils.getShortURLStr((String)values, 80));
            }
*/            
        } catch (Throwable t) {
            if (log.isErrorEnabled()) {
                log.error("Problems taking over Hit Details into result:" + result, t);
            }
        }
    }

    /**
     * Returns all values stored with the passed key and returns them as one String (concatenated with ", ").
     * If no values are stored an emty string is returned !)
     * @param detail
     * @param key
     * @return
     */
    public static String getDetailMultipleValues(IngridHitDetail detail, String key) {
        String[] valueArray = (String[]) detail.get(key);
        StringBuffer values = new StringBuffer();
        if (valueArray != null) {
            for (int i = 0; i < valueArray.length; i++) {
                if (i != 0) {
                    values.append(", ");
                }
                // TODO: Kataloge, Keys von Kategorien auf Werte abbilden !
                values.append(valueArray[i]);
            }
        }
/*
        ArrayList valueList = detail.getArrayList(key);
        StringBuffer values = new StringBuffer();
        if (valueList != null) {
            for (int i = 0; i < valueList.size(); i++) {
                if (i != 0) {
                    values.append(", ");
                }
                // TODO: Kataloge, Keys von Kategorien auf Werte abbilden !
                values.append(valueList.get(i));
            }
        }
*/
        return values.toString();
    }

    public static String queryToString(IngridQuery query) {
        StringBuffer qStr = new StringBuffer();
        qStr.append(query);
        qStr.append(", ");

        FieldQuery[] fields = query.getDataTypes();
        for (int i = 0; i < fields.length; i++) {
            qStr.append(" ");
            qStr.append(fields[i]);
            qStr.append("/required:");
            qStr.append(fields[i].isRequred());
            qStr.append("/prohibited:");
            qStr.append(fields[i].isProhibited());
        }

        return qStr.toString();
    }

    public static boolean containsPositiveDataType(IngridQuery query, String datatypeValue) {
        boolean contains = false;

        String[] posDataTypes = query.getPositiveDataTypes();
        for (int i = 0; i < posDataTypes.length; i++) {
            if (posDataTypes[i].equals(datatypeValue)) {
                contains = true;
                break;
            }
        }

        return contains;
    }

    /**
     * Adapt basic datatypes in query dependent from selected datatype in UserInterface
     * (the ones above the Simple Search Input).
     * @param query
     * @param selectedDS
     */
    public static void processBasicDataTypes(IngridQuery query, String selectedDS) {

        // remove not valid data sources from query
        //        removeBasicDataTypes(query);
        if (selectedDS.equals(Settings.PARAMV_DATASOURCE_ENVINFO)) {
            // TODO: do not set datatype:default, not processed in backend yet !
            // instead don't allow addresses
            //            query.addField(new FieldQuery(true, false, Settings.QFIELD_DATATYPE, Settings.QVALUE_DATATYPE_ENVINFO));
            // REMOVE ADRESS IPLUG
            query.addField(new FieldQuery(false, true, Settings.QFIELD_DATATYPE, Settings.QVALUE_DATATYPE_ADDRESS));
        } else if (selectedDS.equals(Settings.PARAMV_DATASOURCE_ADDRESS)) {
            query.addField(new FieldQuery(true, false, Settings.QFIELD_DATATYPE, Settings.QVALUE_DATATYPE_ADDRESS));
        } else if (selectedDS.equals(Settings.PARAMV_DATASOURCE_RESEARCH)) {
            query.addField(new FieldQuery(true, false, Settings.QFIELD_DATATYPE, Settings.PARAMV_DATASOURCE_RESEARCH));
        }
    }

    /**
     * Remove the passed data type from the query
     * @param query
     */
    /*
     // TODO: remove this helper method if functionality is in IngridQuery
     public static boolean removeDataType(IngridQuery query, String datatypeValue) {
     boolean removed = false;
     FieldQuery[] dataTypesInQuery = query.getDataTypes();
     if (dataTypesInQuery.length == 0) {
     return removed;
     }
     
     ArrayList processedDataTypes = new ArrayList(Arrays.asList(dataTypesInQuery));
     for (Iterator iter = processedDataTypes.iterator(); iter.hasNext();) {
     FieldQuery field = (FieldQuery) iter.next();
     String value = field.getFieldValue();
     if (value != null && value.equals(datatypeValue)) {
     iter.remove();
     removed = true;
     }
     }
     // remove all old datatypes and set our new ones
     query.remove(Settings.QFIELD_DATATYPE);
     for (int i = 0; i < processedDataTypes.size(); i++) {
     query.addField((FieldQuery) processedDataTypes.get(i));
     }
     
     return removed;
     }
     */

    /**
     * Remove the Basic DataTypes from the query (the ones above the Simple Search Input) to obtain a "clean" query
     * @param query
     */
    /*    
     // TODO: remove this helper method if functionality is in IngridQuery
     public static void removeBasicDataTypes(IngridQuery query) {
     FieldQuery[] dataTypesInQuery = query.getDataTypes();
     if (dataTypesInQuery.length == 0) {
     return;
     }
     
     ArrayList processedDataTypes = new ArrayList(Arrays.asList(dataTypesInQuery));
     for (Iterator iter = processedDataTypes.iterator(); iter.hasNext();) {
     FieldQuery field = (FieldQuery) iter.next();
     String value = field.getFieldValue();
     if (value == null || value.equals(Settings.QVALUE_DATATYPE_ENVINFO)
     || value.equals(Settings.QVALUE_DATATYPE_ADDRESS)
     || value.equals(Settings.QVALUE_DATATYPE_RESEARCH)) {
     iter.remove();
     }
     }
     // remove all old datatypes and set our new ones
     query.remove(Settings.QFIELD_DATATYPE);
     for (int i = 0; i < processedDataTypes.size(); i++) {
     query.addField((FieldQuery) processedDataTypes.get(i));
     }
     }
     */

}
