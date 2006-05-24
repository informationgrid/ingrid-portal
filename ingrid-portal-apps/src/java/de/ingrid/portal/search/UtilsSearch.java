/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.search;

import java.io.IOException;
import java.io.NotSerializableException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.portals.messaging.PortletMessaging;
import org.apache.velocity.context.Context;

import de.ingrid.iplug.sns.utils.Topic;
import de.ingrid.portal.config.IngridSessionPreferences;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.global.UtilsDB;
import de.ingrid.portal.global.UtilsQueryString;
import de.ingrid.portal.global.UtilsString;
import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.PlugDescription;
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
            result.put(Settings.RESULT_KEY_ABSTRACT, UtilsString.cutString(detail.getSummary().replaceAll("\\<.*?\\>", ""), 400));
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
            value = UtilsSearch.getRawDetailValue(detail, Settings.RESULT_KEY_PARTNER);
            if (value.length() > 0) {
                result.put(Settings.RESULT_KEY_PARTNER + "Key", value);
                // mapped partner
                value = UtilsSearch.getDetailValue(detail, Settings.RESULT_KEY_PARTNER);
                result.put(Settings.RESULT_KEY_PARTNER, value);
            }
            // Provider
            value = UtilsSearch.getRawDetailValue(detail, Settings.RESULT_KEY_PROVIDER);
            if (value.length() > 0) {
                result.put(Settings.RESULT_KEY_PROVIDER + "Key", value);
                // mapped provider
                value = UtilsSearch.getDetailValue(detail, Settings.RESULT_KEY_PROVIDER);
                result.put(Settings.RESULT_KEY_PROVIDER, value);
            }
        } catch (Throwable t) {
            if (log.isErrorEnabled()) {
                log.error("Problems taking over Hit Details into result:" + result, t);
            }
        }
    }

    /**
     * Transfer commonly used plug parameters from PlugDescription to hitobject.
     * @param result
     * @param plugDescr
     */
    public static void transferPlugDescription(IngridHit result, PlugDescription plugDescr) {
        try {
            result.put("hasPlugDescr", Settings.MSGV_TRUE);
            result.put(PlugDescription.DATA_SOURCE_NAME, plugDescr.getDataSourceName());
            result.put(PlugDescription.DATA_SOURCE_DESCRIPTION, plugDescr.getDataSourceDescription().replaceAll(
                    "\\<.*?\\>", ""));
            result.put(PlugDescription.DATA_TYPE, plugDescr.getDataTypes());
            result.put(PlugDescription.ORGANISATION, plugDescr.getOrganisation());
        } catch (Throwable t) {
            if (log.isErrorEnabled()) {
                log.error("Problems taking over PlugDescription into result:" + result, t);
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
    public static String getDetailValue(IngridHit detail, String key) {
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
    public static String getDetailValue(IngridHit detail, String key, IngridResourceBundle resources) {
        return getDetailValue(detail, key, resources, false);
    }

    /**
     * Get raw detail value with given key, meaning no Mapping occurs ! (same as getDetailValue() but
     * without mapping)
     * @param detail
     * @param key
     * @return
     */
    public static String getRawDetailValue(IngridHit detail, String key) {
        return getDetailValue(detail, key, null, true);
    }

    /**
     * Private method handling all Detail Fetching (Mapping)
     * @param detail
     * @param key
     * @param resources
     * @param raw
     * @return
     */
    private static String getDetailValue(IngridHit detail, String key, IngridResourceBundle resources, boolean raw) {
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
                if (raw) {
                    values.append(valueArray[i]);
                } else {
                    values.append(mapResultValue(key, valueArray[i], resources));
                }
            }
        } else if (obj instanceof ArrayList) {
            ArrayList valueList = (ArrayList) obj;
            for (int i = 0; i < valueList.size(); i++) {
                if (i != 0) {
                    values.append(", ");
                }
                if (raw) {
                    values.append(valueList.get(i).toString());
                } else {
                    values.append(mapResultValue(key, valueList.get(i).toString(), resources));

                }
            }
        } else {
            if (raw) {
                values.append(obj.toString());
            } else {
                values.append(mapResultValue(key, obj.toString(), resources));
            }
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
        } else if (detailKey.equals(Settings.RESULT_KEY_PROVIDER)) {
            mappedValue = UtilsDB.getProviderFromKey(detailValue);
        } else if (detailKey.equals(Settings.RESULT_KEY_PLUG_ID)) {
            mappedValue = ((PlugDescription)IBUSInterfaceImpl.getInstance().getIPlug(detailValue)).getDataSourceName();
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
                } else if (rType == Topic.SIMILARLOCATIONS_FROM_TOPIC) {
                    qStr = qStr.concat(" ").concat(Topic.REQUEST_TYPE).concat(":").concat(Integer.toString(rType))
                            .concat("(SIMILARLOCATIONS_FROM_TOPIC)");
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
                qStr = qStr.concat(" ").concat(key.toString()).concat(":");

                if (value instanceof String[]) {
                    qStr = qStr.concat("[");
                    String[] valueArray = (String[]) value;
                    for (int i = 0; i < valueArray.length; i++) {
                        if (i != 0) {
                            qStr = qStr.concat(", ");
                        }
                        qStr = qStr.concat(valueArray[i]);
                    }
                    qStr = qStr.concat("]");
                } else if (value instanceof ArrayList) {
                    qStr = qStr.concat("[");
                    ArrayList valueList = (ArrayList) value;
                    for (int i = 0; i < valueList.size(); i++) {
                        if (i != 0) {
                            qStr = qStr.concat(", ");
                        }
                        qStr = qStr.concat(valueList.get(i).toString());
                    }
                    qStr = qStr.concat("]");
                } else {
                    qStr = qStr.concat(value.toString());
                }
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
     * Add provider(s) to query
     * @param query
     * @param partners
     */
    public static void processProvider(IngridQuery query, String[] providers) {
        if (providers != null && providers.length > 0) {
            for (int i = 0; i < providers.length; i++) {
                if (providers[i] != null) {
                    query.addToList("provider", new FieldQuery(true, false, Settings.QFIELD_PROVIDER, providers[i]));
                }
            }
        }
    }

    /**
     * Add partner(s) to query
     * @param query
     * @param partners
     */
    public static void processPartner(IngridQuery query, String[] partners) {
        ClauseQuery cq = null;

        // don't set anything if "all" is selected
        if (partners != null && Utils.getPosInArray(partners, Settings.PARAMV_ALL) == -1) {
            cq = new ClauseQuery(true, false);
            for (int i = 0; i < partners.length; i++) {
                if (partners[i] != null) {
                    cq.addField(new FieldQuery(false, false, Settings.QFIELD_PARTNER, partners[i]));
                }
            }
            query.addClause(cq);
        }
    }
    
    
    /**
     * Add grouping to query
     * @param query
     * @param grouping
     */
    public static void processGrouping(IngridQuery query, String grouping) {
        if (grouping != null) {
            if (grouping.equals(Settings.PARAMV_GROUPING_PARTNER)) {
                query.put(Settings.QFIELD_GROUPED, IngridQuery.GROUPED_BY_PARTNER);
            } else if (grouping.equals(Settings.PARAMV_GROUPING_PROVIDER)) {
                query.put(Settings.QFIELD_GROUPED, IngridQuery.GROUPED_BY_ORGANISATION);
            }
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
        PortletSession session = request.getPortletSession();
        DisplayTreeNode partnerRoot = (DisplayTreeNode) session.getAttribute("partnerRoot");
        if (partnerRoot == null) {
            partnerRoot = DisplayTreeFactory.getTreeFromPartnerProvider();
            session.setAttribute("partnerRoot", partnerRoot);
        }

        context.put("partnerRoot", partnerRoot);
    }


    /**
     * Encapsulates common processAction functionality for all term-adding portlets.
     * 
     * @param request
     * @param response
     * @throws NotSerializableException
     */
    public static void processActionForTermPortlets(ActionRequest request, ActionResponse response) throws NotSerializableException {
        String addingType = request.getParameter("adding_type");
        String searchTerm = request.getParameter("search_term");
        String queryStr = (String) PortletMessaging.receive(request, Settings.MSG_TOPIC_SEARCH, Settings.PARAM_QUERY_STRING);
        if (addingType.equals("1")) {
            PortletMessaging.publish(request, Settings.MSG_TOPIC_SEARCH, Settings.PARAM_QUERY_STRING, UtilsQueryString.addTerm(queryStr, searchTerm, UtilsQueryString.OP_AND));
        } else if (addingType.equals("2")) {
            PortletMessaging.publish(request, Settings.MSG_TOPIC_SEARCH, Settings.PARAM_QUERY_STRING, UtilsQueryString.addTerm(queryStr, searchTerm, UtilsQueryString.OP_OR));
        } else if (addingType.equals("3")) {
            PortletMessaging.publish(request, Settings.MSG_TOPIC_SEARCH, Settings.PARAM_QUERY_STRING, UtilsQueryString.addTerm(queryStr, searchTerm, UtilsQueryString.OP_PHRASE));
        } else if (addingType.equals("4")) {
            PortletMessaging.publish(request, Settings.MSG_TOPIC_SEARCH, Settings.PARAM_QUERY_STRING, UtilsQueryString.addTerm(queryStr, searchTerm, UtilsQueryString.OP_NOT));
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

        PortletSession session = request.getPortletSession();

        if (submittedAddToQuery != null) {

            // Zur Suchanfrage hinzufuegen
            String queryStr = (String) PortletMessaging.receive(request, Settings.MSG_TOPIC_SEARCH,
                    Settings.PARAM_QUERY_STRING);
            DisplayTreeNode partnerRoot = (DisplayTreeNode) session.getAttribute("partnerRoot");
            Iterator it = partnerRoot.getChildren().iterator();
            String chk;
            HashMap partnerHash = new HashMap();
            HashMap providerHash = new HashMap();
            while (it.hasNext()) {
                DisplayTreeNode partnerNode = (DisplayTreeNode) it.next();
                chk = request.getParameter("chk_".concat(partnerNode.getId()));
                if (chk != null && chk.equals("on")) {
                    partnerHash.put("partner:".concat(partnerNode.getId()), "1");
                }
                if (partnerNode.isOpen()) {
                    Iterator it2 = partnerNode.getChildren().iterator();
                    while (it2.hasNext()) {
                        DisplayTreeNode providerNode = (DisplayTreeNode) it2.next();
                        chk = request.getParameter("chk_".concat(providerNode.getId()));
                        if (chk != null && chk.equals("on")) {
                            providerHash.put("provider:".concat(providerNode.getId()), "1");
                        }
                    }
                }
            }

            String subTerm = "";
            if (partnerHash.size() > 1) {
                subTerm = subTerm.concat("(");
            }
            it = partnerHash.keySet().iterator();
            while (it.hasNext()) {
                String term = (String) it.next();
                if (it.hasNext()) {
                    subTerm = subTerm.concat(term).concat(" OR ");
                } else {
                    subTerm = subTerm.concat(term);
                }
            }
            if (partnerHash.size() > 1) {
                subTerm = subTerm.concat(")");
            }
            if (partnerHash.size() > 0 && providerHash.size() > 0) {
                subTerm = subTerm.concat(" ");
            }

            if (providerHash.size() > 1) {
                subTerm = subTerm.concat("(");
            }
            it = providerHash.keySet().iterator();
            while (it.hasNext()) {
                String term = (String) it.next();
                if (it.hasNext()) {
                    subTerm = subTerm.concat(term).concat(" OR ");
                } else {
                    subTerm = subTerm.concat(term);
                }
            }
            if (providerHash.size() > 1) {
                subTerm = subTerm.concat(")");
            }
            if (subTerm.length() > 0) {
                PortletMessaging.publish(request, Settings.MSG_TOPIC_SEARCH, Settings.PARAM_QUERY_STRING,
                        UtilsQueryString.addTerm(queryStr, subTerm, UtilsQueryString.OP_AND));
            }

        } else if (action.equalsIgnoreCase("doOpenPartner")) {
            DisplayTreeNode partnerRoot = (DisplayTreeNode) session.getAttribute("partnerRoot");
            DisplayTreeNode node = partnerRoot.getChild(request.getParameter("id"));
            node.setOpen(true);
        } else if (action.equalsIgnoreCase("doClosePartner")) {
            DisplayTreeNode partnerRoot = (DisplayTreeNode) session.getAttribute("partnerRoot");
            DisplayTreeNode node = partnerRoot.getChild(request.getParameter("id"));
            node.setOpen(false);
        }
    }

    /**
     * Add a querystring to the session querystring history.
     * 
     * @param request The request.
     */
    public static void addQueryToHistory(RenderRequest request) {
        String queryString = SearchState.getSearchStateObjectAsString(request, Settings.PARAM_QUERY_STRING);
        String urlStr = Settings.PAGE_SEARCH_RESULT + SearchState.getURLParamsMainSearch(request);
        IngridSessionPreferences sessionPrefs = Utils.getSessionPreferences(request, IngridSessionPreferences.SESSION_KEY, IngridSessionPreferences.class);
        QueryHistory history = (QueryHistory)sessionPrefs.getInitializedObject(IngridSessionPreferences.QUERY_HISTORY, QueryHistory.class);
        String selectedDS = SearchState.getSearchStateObjectAsString(request, Settings.PARAM_DATASOURCE);
        if (selectedDS == null) {
            selectedDS = Settings.SEARCH_INITIAL_DATASOURCE;
        }
        history.add(queryString, urlStr, selectedDS);
    }

    /**
     * Add plug ids to the query.
     * 
     * @param query
     * @param plugIds
     */
    public static void processIPlugs(IngridQuery query, String[] plugIds) {
        if (plugIds != null && plugIds.length > 0) {
            for (int i = 0; i < plugIds.length; i++) {
                if (plugIds[i] != null) {
                    query.addToList(IngridQuery.IPLUGS, new FieldQuery(true, false, Settings.QFIELD_PLUG_ID, plugIds[i]));
                }
            }
        }
    }
}
