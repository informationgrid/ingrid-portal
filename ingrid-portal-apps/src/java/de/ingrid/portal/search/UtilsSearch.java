/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.portals.messaging.PortletMessaging;
import org.apache.velocity.context.Context;

import de.ingrid.iplug.sns.utils.Topic;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.global.UtilsDB;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.query.ClauseQuery;
import de.ingrid.utils.query.FieldQuery;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.query.TermQuery;
import de.ingrid.utils.queryparser.IDataTypes;

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
            // strip all HTML tags from summary
            result.put(Settings.RESULT_KEY_ABSTRACT, detail.getSummary().replaceAll("\\<.*?\\>",""));
            result.put(Settings.RESULT_KEY_DOC_ID, new Integer(result.getDocumentId()));
            result.put(Settings.RESULT_KEY_PROVIDER, detail.getOrganisation());
            result.put(Settings.RESULT_KEY_SOURCE, detail.getDataSourceName());
            result.put(Settings.RESULT_KEY_PLUG_ID, detail.getPlugId());

            String value = UtilsSearch.getDetailValue(detail, Settings.RESULT_KEY_URL);
            if (value.length() > 0) {
                result.put(Settings.RESULT_KEY_URL, value);
                result.put(Settings.RESULT_KEY_URL_STR, Utils.getShortURLStr(value, 80));
            }
            // Partner
            value = UtilsSearch.getDetailValue(detail, Settings.RESULT_KEY_PARTNER);
            if (value.length() > 0) {
                result.put(Settings.RESULT_KEY_PARTNER, value);
            }
        } catch (Throwable t) {
            if (log.isErrorEnabled()) {
                log.error("Problems taking over Hit Details into result:" + result, t);
            }
        }
    }

    /**
     * Returns all values stored with the passed key and returns them as one String (concatenated with ", ").
     * If no values are stored an emty string is returned !).
     * NOTICE: Also returns Single Values CORRECTLY ! FURTHER MAPS idValues to correct "names", e.g. partnerId to
     * partnerName.
     * @param detail
     * @param key
     * @return
     */
    public static String getDetailValue(IngridHitDetail detail, String key) {
        return getDetailValue(detail, key, null);
    }

    /**
     * The same as getDetailValue(detail, key), but here the result is mapped with the passed resource bundle.
     * Pass null for the resource bundle, if no resource mapping should occur.
     * @param detail
     * @param key
     * @param resources
     * @return
     */
    public static String getDetailValue(IngridHitDetail detail, String key, IngridResourceBundle resources) {
        Object obj = detail.get(key);
        if (obj == null) {
            return "";
        }

        StringBuffer values = new StringBuffer();
        if (obj instanceof String[]) {
            String[] valueArray = (String[]) obj;
            for (int i = 0; i < valueArray.length; i++) {
                if (i != 0) {
                    values.append(", ");
                }
                values.append(mapResultValue(key, valueArray[i], resources));
            }
        } else if (obj instanceof ArrayList) {
            ArrayList valueList = (ArrayList) obj;
            for (int i = 0; i < valueList.size(); i++) {
                if (i != 0) {
                    values.append(", ");
                }
                values.append(mapResultValue(key, valueList.get(i).toString(), resources));
            }
        } else {
            values.append(mapResultValue(key, obj.toString(), resources));
        }

        return values.toString();
    }

    /**
     * Map the given value to a "real" value, e.g. map partner id to partner name.
     * The passed key determines what kind of id the passed value is (this is the
     * key with which the value was read from result/detail)
     * @param resultKey
     * @param resultValue
     * @return
     */
    public static String mapResultValue(String detailKey, String detailValue, IngridResourceBundle resources) {
        String mappedValue = detailValue;
        if (detailKey.equals(Settings.RESULT_KEY_PARTNER)) {
            mappedValue = UtilsDB.getPartnerFromKey(detailValue);
        } else if (resources != null) {
            mappedValue = resources.getString(detailValue);
        }

        return mappedValue;
    }

    public static TermQuery[] getAllTerms(IngridQuery q) {
        ArrayList result = new ArrayList();
        TermQuery[] terms = q.getTerms();
        for (int i = 0; i < terms.length; i++) {
            if (terms[i].getType() == TermQuery.TERM) {
                result.add(terms[i]);
            }
        }
        ClauseQuery[] clauses = q.getClauses();
        for (int i = 0; i < clauses.length; i++) {
            result.addAll(Arrays.asList(getAllTerms(clauses[i])));
        }

        return ((TermQuery[]) result.toArray(new TermQuery[result.size()]));
    }

    public static String queryToString(IngridQuery q) {
        String qStr = "";
        boolean isFirstTerm = true;

        try {
            TermQuery[] terms = q.getTerms();
            for (int i = 0; i < terms.length; i++) {
                if (!terms[i].isRequred()) {
                    qStr = qStr.concat(" OR ").concat(terms[i].getTerm());
                } else {
                    if (isFirstTerm) {
                        qStr = qStr.concat(terms[i].getTerm());
                        isFirstTerm = false;
                    } else {
                        qStr = qStr.concat(" ").concat(terms[i].getTerm());
                    }
                }
            }
            ClauseQuery[] clauses = q.getClauses();
            for (int i = 0; i < clauses.length; i++) {
                if (!clauses[i].isRequred()) {
                    qStr = qStr.concat(" OR (").concat(queryToString(clauses[i])).concat(")");
                } else {
                    qStr = qStr.concat(" (").concat(queryToString(clauses[i])).concat(")");
                }

            }

            FieldQuery[] fields = q.getFields();
            for (int i = 0; i < fields.length; i++) {
                qStr = qStr.concat(" ").concat(buildFieldQueryStr(fields[i]));
            }

            FieldQuery[] datatypes = q.getDataTypes();
            for (int i = 0; i < datatypes.length; i++) {
                qStr = qStr.concat(" ").concat(buildFieldQueryStr(datatypes[i]));
            }

            String ranking = q.getRankingType();
            if (ranking != null) {
                qStr = qStr.concat(" ranking:").concat(ranking);
            }

            if (hasPositiveDataType(q, IDataTypes.SNS)) {
                int rType = q.getInt(Topic.REQUEST_TYPE);
                if (rType == Topic.ANNIVERSARY_FROM_TOPIC) {
                    qStr = qStr.concat(" ").concat(Topic.REQUEST_TYPE).concat(":").concat(Integer.toString(rType))
                            .concat("(ANNIVERSARY_FROM_TOPIC)");
                } else if (rType == Topic.EVENT_FROM_TOPIC) {
                    qStr = qStr.concat(" ").concat(Topic.REQUEST_TYPE).concat(":").concat(Integer.toString(rType))
                            .concat("(EVENT_FROM_TOPIC)");
                } else if (rType == Topic.SIMILARTERMS_FROM_TOPIC) {
                    qStr = qStr.concat(" ").concat(Topic.REQUEST_TYPE).concat(":").concat(Integer.toString(rType))
                            .concat("(SIMILARTERMS_FROM_TOPIC)");
                } else if (rType == Topic.TOPIC_FROM_TERM) {
                    qStr = qStr.concat(" ").concat(Topic.REQUEST_TYPE).concat(":").concat(Integer.toString(rType))
                            .concat("(TOPIC_FROM_TERM)");
                } else if (rType == Topic.TOPIC_FROM_TEXT) {
                    qStr = qStr.concat(" ").concat(Topic.REQUEST_TYPE).concat(":").concat(Integer.toString(rType))
                            .concat("(TOPIC_FROM_TEXT)");
                } else {
                    qStr = qStr.concat(" ").concat(Topic.REQUEST_TYPE).concat(":").concat(Integer.toString(rType))
                            .concat("(UNKNOWN)");
                }
            }

            // remaining keys in IngridQuery
            Iterator iterator = q.keySet().iterator();
            qStr = qStr.concat(" /MAP->");
            while (iterator.hasNext()) {
                Object key = iterator.next();
                Object value = q.get(key);
                if (value == null) {
                    value = "null";
                }
                qStr = qStr.concat(" ").concat(key.toString()).concat(":").concat(value.toString());
            }

        } catch (Exception ex) {
            if (log.isErrorEnabled()) {
                log.error("Problems transforming IngridQuery to String, string so far = " + qStr, ex);
            }
        }

        return qStr;

        /*        
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
         */
    }

    private static String buildFieldQueryStr(FieldQuery field) {
        String result = "";
        if (field.isRequred() && !field.isProhibited()) {
            result = result.concat(field.getFieldName()).concat(":").concat(field.getFieldValue());
        } else if (!field.isRequred() && field.isProhibited()) {
            result = result.concat("-").concat(field.getFieldName()).concat(":").concat(field.getFieldValue());
        } else if (field.isRequred() && field.isProhibited()) {
            result = result.concat("+-").concat(field.getFieldName()).concat(":").concat(field.getFieldValue());
        } else {
            result = result.concat("?").concat(field.getFieldName()).concat(":").concat(field.getFieldValue());
        }
        return result;
    }

    public static boolean hasPositiveDataType(IngridQuery q, String datatype) {
        String[] dtypes = q.getPositiveDataTypes();
        for (int i = 0; i < dtypes.length; i++) {
            if (dtypes[i].equalsIgnoreCase(datatype)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasNegativeDataType(IngridQuery q, String datatype) {
        String[] dtypes = q.getNegativeDataTypes();
        for (int i = 0; i < dtypes.length; i++) {
            if (dtypes[i].equalsIgnoreCase(datatype)) {
                return true;
            }
        }
        return false;
    }
    
    
    /**
     * Check whether query contains a field of the given name.
     * Ignores whether it is in positive or negative list !
     * NOTICE:
     * - only FIELD QUERIES and direct map keys are checked, NO CLAUSE QUERIES !
     * - comparison is done with lowercase representation of field name.
     * @param query
     * @param fieldName
     * @return
     */
    public static boolean containsField(IngridQuery query, String fieldName) {
        String fieldNameLowCase = fieldName.toLowerCase();
        FieldQuery[] fields = query.getDataTypes();
        int count = fields.length;
        for (int i = 0; i < count; i++) {
            if (fields[i].getFieldName().toLowerCase().equals(fieldNameLowCase)) {
                return true;
            }
        }
        if (query.get(fieldName) != null) {
            return true;
        }
        
        return false;
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
            query
                    .addField(new FieldQuery(true, false, Settings.QFIELD_DATATYPE,
                            Settings.QVALUE_DATATYPE_AREA_ENVINFO));
        } else if (selectedDS.equals(Settings.PARAMV_DATASOURCE_ADDRESS)) {
            query
                    .addField(new FieldQuery(true, false, Settings.QFIELD_DATATYPE,
                            Settings.QVALUE_DATATYPE_AREA_ADDRESS));
        } else if (selectedDS.equals(Settings.PARAMV_DATASOURCE_RESEARCH)) {
            query
                    .addField(new FieldQuery(true, false, Settings.QFIELD_DATATYPE,
                            Settings.QVALUE_DATATYPE_AREA_RESEARCH));
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
     if (value == null || value.equals(Settings.QVALUE_DATATYPE_AREA_ENVINFO)
     || value.equals(Settings.QVALUE_DATATYPE_AREA_ADDRESS)
     || value.equals(Settings.QVALUE_DATATYPE_AREA_RESEARCH)) {
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

    /**
     * Encapsulates common doView functionality for all partner selection portlets 
     * @param request
     * @param context
     */
    public static void doViewForPartnerPortlet(RenderRequest request, Context context) {
        // ----------------------------------
        // check for passed RENDER PARAMETERS and react
        // ----------------------------------
        String action = request.getParameter(Settings.PARAM_ACTION);
        if (action != null) {
            // fetch data and pass it to template, HERE DUMMY
            Object partner = PortletMessaging.receive(request, Settings.MSG_PARTNER);
            context.put("partner", partner);
        } else {
            // remove data from session
            PortletMessaging.cancel(request, Settings.MSG_PARTNER);
        }
    }

    /**
     * Encapsulates common processAction functionality for all partner selection portlets 
     * @param request
     * @param context
     */
    public static void processActionForPartnerPortlet(ActionRequest request, ActionResponse response, String page)
            throws IOException {
        String action = request.getParameter(Settings.PARAM_ACTION);
        if (action == null) {
            action = "";
        }
        String submittedAddToQuery = request.getParameter("submitAddToQuery");

        // TODO: implement functionality
        if (submittedAddToQuery != null) {

            // Zur Suchanfrage hinzufuegen

            // redirect to same page with URL parameter indicating that action was called !
            String urlViewParam = "?" + Utils.toURLParam(Settings.PARAM_ACTION, Settings.MSGV_TRUE);
            response.sendRedirect(page + urlViewParam);

        } else if (action.equalsIgnoreCase("doOpenPartner")) {

            // Partner anzeigen (publishen, hier dummy)
            PortletMessaging.publish(request, Settings.MSG_PARTNER, Settings.MSGV_TRUE);

            // redirect to same page with URL parameter indicating that action was called !
            String urlViewParam = "?" + Utils.toURLParam(Settings.PARAM_ACTION, Settings.MSGV_TRUE);
            response.sendRedirect(page + urlViewParam);

        } else if (action.equalsIgnoreCase("doClosePartner")) {

            // Partner entfernen (hier dummy)
            PortletMessaging.cancel(request, Settings.MSG_PARTNER);

            // redirect to same page with URL parameter indicating that action was called !
            String urlViewParam = "?" + Utils.toURLParam(Settings.PARAM_ACTION, Settings.MSGV_TRUE);
            response.sendRedirect(page + urlViewParam);
        }
    }
}
