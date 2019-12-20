/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2019 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.portal.search;

import de.ingrid.iplug.sns.utils.Topic;
import de.ingrid.portal.config.IngridSessionPreferences;
import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.forms.SearchExtEnvAreaSourcesForm;
import de.ingrid.portal.global.*;
import de.ingrid.portal.interfaces.WMSInterface;
import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;
import de.ingrid.portal.interfaces.impl.WMSInterfaceImpl;
import de.ingrid.portal.interfaces.om.WMSServiceDescriptor;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.PlugDescription;
import de.ingrid.utils.query.ClauseQuery;
import de.ingrid.utils.query.FieldQuery;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.query.TermQuery;
import de.ingrid.utils.queryparser.IDataTypes;
import de.ingrid.utils.tool.UrlTool;
import de.ingrid.utils.udk.UtilsCSWDate;
import de.ingrid.utils.udk.UtilsDate;
import org.apache.portals.messaging.PortletMessaging;
import org.apache.velocity.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.portlet.ActionRequest;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import java.io.IOException;
import java.io.NotSerializableException;
import java.security.Principal;
import java.util.*;

/**
 * Global STATIC data and utility methods for SEARCH !
 * 
 * @author Martin Maidhof
 */
public class UtilsSearch {

    private static final Logger log = LoggerFactory.getLogger(UtilsSearch.class);

    public static final String DETAIL_VALUES_SEPARATOR = ", ";

    /**
     * Generate PageNavigation data for rendering
     * 
     * @param startHit
     * @param hitsPerPage
     * @param numberOfHits
     * @param numSelectorPages
     * @return
     */
    public static Map<String, Object> getPageNavigation(int startHit, int hitsPerPage, int numberOfHits, int numSelectorPages) {

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
                firstSelectorPage = currentSelectorPage - (numSelectorPages / 2);
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
        pageSelector.put("currentSelectorPage", currentSelectorPage);
        pageSelector.put("numberOfPages", numberOfPages);
        pageSelector.put("numberOfSelectorPages", numSelectorPages);
        pageSelector.put("firstSelectorPage", firstSelectorPage);
        pageSelector.put("lastSelectorPage", lastSelectorPage);
        pageSelector.put("selectorHasPreviousPage", selectorHasPreviousPage);
        pageSelector.put("selectorHasNextPage", selectorHasNextPage);
        pageSelector.put("hitsPerPage", hitsPerPage);
        pageSelector.put("numberOfFirstHit", numberOfFirstHit);
        pageSelector.put("numberOfLastHit", numberOfLastHit);
        pageSelector.put("numberOfHits", numberOfHits);

        return pageSelector;
    }

    /**
     * Transform "normal" page navigation to the grouped navigation dependent from grouping state !
     */
    public static Map<String, Object> adaptRankedPageNavigationToGrouping(
    	Map<String, Object> pageNavi,
    	int currentPage,
    	boolean hasMoreGroupedPages,
    	int totalNumberOfHits,
    	RenderRequest request)
    {
        String grouping = (String) SearchState.getSearchStateObject(request, Settings.PARAM_GROUPING);
        if (totalNumberOfHits <= 0 ||
        	grouping == null || 
        	grouping.equals(IngridQuery.GROUPED_OFF))
        {
        	return pageNavi;
        }

        // NOTICE: when grouping by domain the navigation is DIFFERENT !
        // partner/provider grouping: only current page in navigation followed by ">>" 
        // domain grouping: multiple pages in navigation -> equals ungrouped navigation ! 
        // we take care, that navigation data is set up correctly !

        if (grouping.equals(IngridQuery.GROUPED_BY_DATASOURCE)) {
        	
            int firstSelectorPage = 1;
            boolean selectorHasPreviousPage = false;

            if (currentPage >= Settings.SEARCH_RANKED_NUM_PAGES_TO_SELECT) {
                firstSelectorPage = currentPage - (Settings.SEARCH_RANKED_NUM_PAGES_TO_SELECT / 2);
                selectorHasPreviousPage = true;
            }
            // default last page
            int lastSelectorPage = firstSelectorPage + Settings.SEARCH_RANKED_NUM_PAGES_TO_SELECT - 1;
            
        	// add missing positions in array containing starthits of pages
            ArrayList groupedStartHits = 
            	(ArrayList) SearchState.getSearchStateObject(request, Settings.PARAM_GROUPING_STARTHITS);
        	while (lastSelectorPage > groupedStartHits.size()) {
        		groupedStartHits.add(0);
        	}

        	// fill missing starthits of pages and set whether there are further pages !
        	// also reduce number of last page if necessary
            int prevStartHit = 0;
			hasMoreGroupedPages = true;
            // start with second position, first one is always 0 (start at beginning)
            for (int i=1; i < groupedStartHits.size(); i++) {

            	// check whether current start hit is a remain from former calculation
            	int currStartHit = ((Integer) groupedStartHits.get(i)).intValue();
            	int updatedStartHit = prevStartHit + Settings.SEARCH_RANKED_HITS_PER_PAGE;
            	// ADAPT already calculated starthits of pages ! -> number of pages CHANGES !
            	boolean updateCurrent = currStartHit < updatedStartHit;

            	// update if not set yet or outdated !
            	if (currStartHit == 0 || updateCurrent) {
            		currStartHit = updatedStartHit;
            		// reduce starthit if already beyond total number of hits -> show last hit on that page !
            		if (updatedStartHit >= totalNumberOfHits) {
            			updatedStartHit = totalNumberOfHits - 1;
            		}
            		groupedStartHits.set(i, updatedStartHit);
            	}
            		
        		// if start hit of this page already "out of bounds" set former page as last page
        		if (currStartHit >= totalNumberOfHits) {
        			if (i <= lastSelectorPage) {
            			lastSelectorPage = i;        				
            			hasMoreGroupedPages = false;
        			}
        			break;
        		}

            	prevStartHit = currStartHit;
            }
            
            pageNavi.put("firstSelectorPage", firstSelectorPage);
            pageNavi.put("lastSelectorPage", lastSelectorPage);
            pageNavi.put("selectorHasPreviousPage", selectorHasPreviousPage);
        }

        pageNavi.put(Settings.PARAM_CURRENT_SELECTOR_PAGE, currentPage);
        pageNavi.put("selectorHasNextPage", hasMoreGroupedPages);

        return pageNavi;
    }

    /**
     * Transfer commonly used detail parameters from detail object to hitobject.
     * 
     * @param result
     * @param detail
     */
    public static void transferHitDetails(IngridHitWrapper result, IngridHitDetail detail) {
        try {
        	// dummy hit if unranked iplug without results should be displayed 
            result.put(Settings.RESULT_KEY_DUMMY_HIT, false);
        	if (detail.isDummyHit()) {
                result.put(Settings.RESULT_KEY_DUMMY_HIT, true);
        		return;
        	}

            // also cuts title: maximum length = 2 Lines, length of first line
            // is shorter because of icon !
            int iconLength = 15;
            String title = detail.getTitle();
            boolean isCutTitle = PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_SEARCH_HIT_CUT_TITLE, true);
            if(isCutTitle) {
                title = UtilsString.cutString(detail.getTitle(), 2 * Settings.SEARCH_RANKED_MAX_ROW_LENGTH - iconLength,
                    Settings.SEARCH_RANKED_MAX_ROW_LENGTH - iconLength);
            }
            result.put(Settings.RESULT_KEY_TITLE, title);
            // strip all HTML tags from summary
            String summary = detail.getSummary();
            if (summary == null) {
            	summary = "".intern();
            }
            result.put(Settings.RESULT_KEY_ABSTRACT, UtilsString.cutString(summary.replaceAll("\\<.*?\\>",
                    ""), 400));
            String documentId = result.getHit().getDocumentId();
            if (documentId != null && !"null".equals( documentId )) {
                result.put(Settings.RESULT_KEY_DOC_ID, documentId);
            } else {
                // fallback to old id (integer)
                result.put(Settings.RESULT_KEY_DOC_ID, result.getHit().getInt(0));
            }
            
            // use internal provider instead of one set in plugdescription
            // e.g. results from Opensearch might support partner and provider
            String provider = UtilsSearch.getDetailValue(detail, Settings.RESULT_KEY_PROVIDER);
            if (!provider.isEmpty()) {
                result.put(Settings.RESULT_KEY_PROVIDER, provider);
            } else {
                result.put(Settings.RESULT_KEY_PROVIDER, detail.getOrganisation());
            }
            result.put(Settings.RESULT_KEY_SOURCE, detail.getDataSourceName());
            result.put(Settings.RESULT_KEY_PLUG_ID, detail.getPlugId());

            String value = UtilsSearch.getDetailValue(detail, Settings.RESULT_KEY_URL);
            if (value.length() > 0) {
                result.put(Settings.RESULT_KEY_URL, value);
                String urlStr = null;
                try {
                    urlStr = UrlTool.getDecodedUnicodeUrl(value);
                } catch (Exception e) {
                    urlStr = value;
                }
                result.put(Settings.RESULT_KEY_URL_STR, UtilsString.getShortURLStr(urlStr,
                        Settings.SEARCH_RANKED_MAX_ROW_LENGTH));
                result.put(Settings.RESULT_KEY_URL_DOMAIN, UtilsString.getURLDomain(urlStr));

                String urlLowerCase = value.toLowerCase();
                if (urlLowerCase.indexOf(".pdf") != -1) {
                    result.put(Settings.RESULT_KEY_URL_TYPE, "pdf");
                } else if (urlLowerCase.indexOf(".ppt") != -1) {
                    result.put(Settings.RESULT_KEY_URL_TYPE, "ppt");
                } else if (urlLowerCase.indexOf(".doc") != -1) {
                    result.put(Settings.RESULT_KEY_URL_TYPE, "doc");
                }
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
        } catch (Exception t) {
            if (log.isErrorEnabled()) {
                log.error("Problems taking over Hit Details into result:" + result, t);
            }
        }
    }

    /**
     * Transfer commonly used plug parameters from PlugDescription to hitobject.
     * 
     * @param result
     * @param plugDescr
     */
    public static void transferPlugDescription(IngridHitWrapper result, PlugDescription plugDescr) {
        try {
            result.put("hasPlugDescr", Settings.MSGV_TRUE);
            result.put(PlugDescription.DATA_SOURCE_NAME, plugDescr.getDataSourceName());
            result.put(PlugDescription.DATA_SOURCE_DESCRIPTION, plugDescr.getDataSourceDescription().replaceAll(
                    "\\<.*?\\>", ""));
            result.put(PlugDescription.DATA_TYPE, plugDescr.getDataTypes());
            result.put(PlugDescription.ORGANISATION, plugDescr.getOrganisation());
            if (plugDescr.containsKey("domainGroupingSupport"))
                result.put("domainGroupingSupport", plugDescr.getBoolean("domainGroupingSupport"));
        } catch (Exception t) {
            if (log.isErrorEnabled()) {
                log.error("Problems taking over PlugDescription into result:" + result, t);
            }
        }
    }

    /**
     * Returns all values stored with the passed key and returns them as one
     * String (concatenated with ", "). If no values are stored an empty string
     * is returned !). NOTICE: Also returns Single Values CORRECTLY ! FURTHER
     * MAPS idValues to correct "names", e.g. partnerId to partnerName.
     * 
     * @param detail
     * @param key
     * @return
     */
    public static String getDetailValue(IngridHit detail, String key) {
        return getDetailValue(detail, key, null);
    }

    /**
     * Returns values stored with the passed key and returns them as one
     * String (concatenated with ", "). If no values are stored an empty string
     * is returned !). NOTICE: Also returns Single Values CORRECTLY ! FURTHER
     * MAPS idValues to correct "names", e.g. partnerId to partnerName.
     * 
     * @param detail
     * @param key
     * @param noOfElements If the result is an array, return only the first number of elements, concated by ', ' . If 0 all elements will be returned.
     * @return
     */
    public static String getDetailValue(IngridHit detail, String key, int noOfElements) {
        return getDetailValue(detail, key, null, false, noOfElements);
    }

    /**
     * The same as getDetailValue(detail, key), but here the result is mapped
     * with the passed resource bundle. Pass null for the resource bundle, if no
     * resource mapping should occur.
     * 
     * @param detail
     * @param key
     * @param resources
     * @return
     */
    public static String getDetailValue(IngridHit detail, String key, IngridResourceBundle resources) {
        return getDetailValue(detail, key, resources, false, 0);
    }

    /**
     * Get raw detail value with given key, meaning no Mapping occurs ! (same as
     * getDetailValue() but without mapping)
     * 
     * @param detail
     * @param key
     * @return
     */
    public static String getRawDetailValue(IngridHit detail, String key) {
        return getDetailValue(detail, key, null, true, 0);
    }

    /**
     * Private method handling all Detail Fetching (Mapping)
     * 
     * @param detail
     * @param key
     * @param resources
     * @param raw
     * @param noOfElements If the result is an array, return only the first number of elements, concated by ', ' . If 0 all elements will be returned.
     * 
     * @return
     */
    private static String getDetailValue(IngridHit detail, String key, IngridResourceBundle resources, boolean raw, int noOfElements) {
        Object obj = detail.get(key);
        // since elastic search index keys are lowercase, make sure to also look for those
        if (obj == null) obj = detail.get(key.toLowerCase());
        if (obj == null) {
            return "";
        }

        StringBuilder values = new StringBuilder();
        if (obj instanceof String[]) {
            String[] valueArray = (String[]) obj;
            for (int i = 0; i < (noOfElements == 0 ? valueArray.length : Math.min( valueArray.length, noOfElements )); i++) {
            	String value = null;
                
                if (raw) {
                	value = valueArray[i];
                } else {
                	if(key.equals(Settings.RESULT_KEY_TOPIC)){
                		String tmpValue = mapResultValue(key, valueArray[i], resources);
                		if(!tmpValue.equals(valueArray[i])){
                			value = tmpValue;
                		}
                	}else{
                		value = mapResultValue(key, valueArray[i], resources);
                	}
                }
                if(value != null){
                	if (values.length() > 0 && i != 0) {
                        values.append(DETAIL_VALUES_SEPARATOR);
                    }
                	values.append(value);
                }
            }
        } else if (obj instanceof ArrayList) {
            ArrayList valueList = (ArrayList) obj;
            for (int i = 0; i < (noOfElements == 0 ? valueList.size() : Math.min( valueList.size(), noOfElements )); i++) {
                if (i != 0) {
                    values.append(DETAIL_VALUES_SEPARATOR);
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
     * Returns all values stored with the passed key by filter duplicate values and returns them as one
     * String (concatenated with ", ").
     * 
     * @param detail
     * @param key
     * @return
     */
    public static String getFilteredDuplicateDetailValue(IngridHit detail, String key){
        StringBuilder filteredValues = new StringBuilder("");
        String [] values = getDetailValue( detail, key ).split(DETAIL_VALUES_SEPARATOR); 
        for (int i = 0; i < values.length; i++) {
            String v = values[i].trim();
            if(filteredValues.indexOf(v) == -1){
                if(i != 0){
                    filteredValues.append(DETAIL_VALUES_SEPARATOR);
                }
                filteredValues.append(v);
            }
        }
        if(filteredValues.length() > 0){
            return filteredValues.toString();
        }
        return null;
    }
    
    public static String getDateFormatValue (String value){
        String result = UtilsCSWDate.mapFromIgcToIso8601(value);
        if(result != null) {
            value = result;
        }
        return UtilsSearch.convertDateString(value);
    }

    public static String convertDateString(String value){
        if(value != null){
            String content = UtilsDate.convertDateString(value, "yyyy-MM-dd", "dd.MM.yyyy");
            if(content.length() > 0){
                return content;
            }
        }
        return value;
    }

    /**
     * Map the given value to a "real" value, e.g. map partner id to partner
     * name. The passed key determines what kind of id the passed value is (this
     * is the key with which the value was read from result/detail)
     * 
     * @param detailKey
     * @param detailValue
     * @param resources
     * @return
     */
    public static String mapResultValue(String detailKey, String detailValue, IngridResourceBundle resources) {
        String mappedValue = detailValue;
        if (detailKey.equals(Settings.RESULT_KEY_PARTNER)) {
            mappedValue = UtilsDB.getPartnerFromKey(detailValue);
        } else if (detailKey.equals(Settings.RESULT_KEY_PROVIDER)) {
            mappedValue = UtilsDB.getProviderFromKey(detailValue);
        } else if (detailKey.equals(Settings.RESULT_KEY_PLUG_ID)) {
            mappedValue = (IBUSInterfaceImpl.getInstance().getIPlug(detailValue)).getDataSourceName();
        } else if (resources != null) {
            mappedValue = resources.getString(detailValue);
        }

        return mappedValue;
    }

    /**
     * Get all terms in Query. NOTICE: If multiple TermQuerys contain the same
     * term, every TermQuery is returned (may differ in "required",
     * "prohibited"). To remove "double" terms use removeDoubleTerms(...).
     * 
     * @param q
     * @return
     */
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

    /**
     * Remove the Terms which contain the same term String !
     * 
     * @param terms
     * @return
     */
    public static TermQuery[] removeDoubleTerms(TermQuery[] terms) {
        ArrayList result = new ArrayList(Arrays.asList(terms));

        for (int i = 0; i < terms.length; i++) {
            String term1 = terms[i].getTerm().trim();

            // check for double terms and remove them !
            Iterator iterator = result.iterator();
            boolean removeFlag = false;
            while (iterator.hasNext()) {
                String term2 = ((TermQuery) iterator.next()).getTerm().trim();
                if (term2.equals(term1)) {
                    if (removeFlag) {
                        iterator.remove();
                    } else {
                        removeFlag = true;
                    }
                }
            }
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
            	FieldQuery[] fq = getField(q, Topic.REQUEST_TYPE);
                int rType;
                if (fq != null && fq.length > 0) {
                	rType = Integer.parseInt(fq[0].getFieldValue());
                } else {
                	rType = q.getInt(Topic.REQUEST_TYPE);
                }
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

    public static boolean hasDatatype(IngridQuery q, String datatype) {
        return hasNegativeDataType(q, datatype) || hasPositiveDataType(q, datatype);
    }

    /**
     * Check whether query contains a field of the given name. Ignores whether
     * it is in positive or negative list ! NOTICE: - only FIELD QUERIES and
     * direct map keys are checked in query and all clause queries ! -
     * comparison is done case insensitive.
     * 
     * @param query
     * @param fieldName
     * @return True if the field was found, false if not.
     */
    public static boolean containsFieldOrKey(IngridQuery query, String fieldName) {
        IngridQuery[] clauses = query.getAllClauses();
        for (int i = 0; i < clauses.length; i++) {
            FieldQuery[] fields = clauses[i].getFields();
            for (int j = 0; j < fields.length; j++) {
                if (fields[j].getFieldName().equalsIgnoreCase(fieldName)) {
                    return true;
                }
            }
            if (clauses[i].get(fieldName) != null) {
                return true;
            }
        }
        if (query.get(fieldName) != null) {
            return true;
        }
        FieldQuery[] fields = query.getFields();
        for (int j = 0; j < fields.length; j++) {
            if (fields[j].getFieldName().equalsIgnoreCase(fieldName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check whether query contains a field of the given name and a value.
     * Ignores whether it is in positive or negative list ! NOTICE: - only FIELD
     * QUERIES and direct map keys are checked in query and all clause queries ! -
     * comparison is done case insensitive.
     * 
     * @param query
     *            The query.
     * @param fieldName
     *            The field name.
     * @param value
     *            The field's value
     * @return True if the field,value was found, false if not.
     */
    public static boolean containsField(IngridQuery query, String fieldName, String value) {
        IngridQuery[] clauses = query.getAllClauses();
        for (int i = 0; i < clauses.length; i++) {
            FieldQuery[] fields = query.getFields();
            for (int j = 0; j < fields.length; j++) {
                if (fields[j].getFieldName().equalsIgnoreCase(fieldName)
                        && fields[i].getFieldValue().equalsIgnoreCase(value)) {
                    return true;
                }
            }
            if (query.get(fieldName) != null && ((String) query.get(fieldName)).equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Process basic datatype in query dependent from selected datatype in
     * UserInterface (the ones above the Simple Search Input) and entered datatypes.
     * May add basic datatype to query.
     * NOTICE: see http://jira.media-style.com/browse/INGRID-1076
     * 
     * @param query
     * @param selectedDS
     */
    public static void processBasicDataType(PortletRequest request, IngridQuery query, String selectedDS) {
        // !!! NOTICE: see http://jira.media-style.com/browse/INGRID-1076

    	String basicDatatypeForQuery = null;
    	String defaultDS = PortalConfig.getInstance().getString(PortalConfig.PORTAL_SEARCH_DEFAULT_DATASOURCE, null);
    	boolean hasDefaultDS = false;
    	if(request.getParameter(Settings.PARAM_DATASOURCE) == null && defaultDS != null && defaultDS.length() > 0){
    	    selectedDS = defaultDS;
    	    hasDefaultDS = true;
    	}
    	// DO NOT ALWAYS ADD BASIC DATA TYPE !!! datatypes are connected with OR !!!
    	// see http://jira.media-style.com/browse/INGRID-1076
        if (!UtilsSearch.containsFieldOrKey(query, Settings.QFIELD_DATATYPE)) {
            if (selectedDS.equals(Settings.PARAMV_DATASOURCE_ENVINFO)) {
            	basicDatatypeForQuery = Settings.QVALUE_DATATYPE_AREA_ENVINFO;

            } else if (selectedDS.equals(Settings.PARAMV_DATASOURCE_ADDRESS)) {
            	basicDatatypeForQuery = Settings.QVALUE_DATATYPE_AREA_ADDRESS;

            } else if (selectedDS.equals(Settings.PARAMV_DATASOURCE_RESEARCH)) {
            	basicDatatypeForQuery = Settings.QVALUE_DATATYPE_AREA_RESEARCH;
            
            } else if (selectedDS.equals(Settings.PARAMV_DATASOURCE_LAW)) {
            	basicDatatypeForQuery = Settings.QVALUE_DATATYPE_AREA_LAW;
            
            } else if (selectedDS.equals(Settings.PARAMV_DATASOURCE_CATALOG)) {
            	basicDatatypeForQuery = Settings.QVALUE_DATATYPE_AREA_CATALOG;
            }        	
        }

    	if(basicDatatypeForQuery != null && request.getParameter(Settings.PARAM_DATASOURCE) != null || hasDefaultDS){
            query.addField(new FieldQuery(true, false,
            		Settings.QFIELD_DATATYPE, basicDatatypeForQuery));
        }
    }

    /**
     * Compares current datasource in GUI (selected search area above query input) with the entered
     * datatypes. Returns the final PORTAL datasource according to input and selection. 
     * @param currentPortalDS selection in GUI ("envinfo", "address", "research", "law")
     * @param q the entered IngridQuery
     * @return the final datasource
     */
    public static String determineFinalPortalDatasource(String currentPortalDS, IngridQuery q) {
        String qDS = UtilsSearch.getBasicDataTypeFromQuery(q);
        if (qDS != null) {
        	return mapQueryBasicDatasourceToPortalDatasource(qDS);
        }
        
        if (isAddressQuery(q)) {
        	return mapQueryBasicDatasourceToPortalDatasource(Settings.QVALUE_DATATYPE_AREA_ADDRESS);
        }

        return currentPortalDS;
    	
    }

    /**
     * Tries to extract the basic datatype (="search area" above query input) dependent from entered datatypes in query !
     * @param q
     * @return null if no datatype in query
     */
    private static String getBasicDataTypeFromQuery(IngridQuery q) {
    	String ret = null;
    	if (q != null) {
        	List basicDataTypes = Arrays.asList(Settings.getQValuesDatatypesAreaBasic());
            FieldQuery[] dtFields = q.getDataTypes();
            for (int i = 0; i < dtFields.length; i++) {
                String dt = dtFields[i].getFieldValue();
                if (basicDataTypes.contains(dt)) {
                    // return the first basic type discovered !
                	ret = dt;
                	break;
                }
            }    		
    	}

    	return ret;
    }

    /**
     * Returns true if the query contains explicit datatypes for addresses else false
     */
    private static boolean isAddressQuery(IngridQuery q) {
    	if (q != null) {
        	List addressDataTypes = Arrays.asList(Settings.getQValuesDatatypesAddress());
            FieldQuery[] dtFields = q.getDataTypes();
            for (int i = 0; i < dtFields.length; i++) {
                String dt = dtFields[i].getFieldValue();
                if (addressDataTypes.contains(dt)) {
                	return true;
                }
            }    		
    	}

    	return false;
    }

    private static String mapQueryBasicDatasourceToPortalDatasource(String qDatatype) {
        if (qDatatype.equals(Settings.QVALUE_DATATYPE_AREA_ENVINFO))
        	return Settings.PARAMV_DATASOURCE_ENVINFO;
        if (qDatatype.equals(Settings.QVALUE_DATATYPE_AREA_ADDRESS))
        	return Settings.PARAMV_DATASOURCE_ADDRESS;
        if (qDatatype.equals(Settings.QVALUE_DATATYPE_AREA_RESEARCH))
        	return Settings.PARAMV_DATASOURCE_RESEARCH;

        // default
        return Settings.PARAMV_DATASOURCE_ENVINFO;
    }

    /**
     * Add language to query
     * 
     * @param query
     * @param myLocale
     */
    public static void processLanguage(IngridQuery query, Locale myLocale) {
        String lang = null;
        if (myLocale != null) {
            lang = myLocale.getLanguage();
        }
        if (lang != null) {
            query.addField(new FieldQuery(true, false, Settings.QFIELD_LANG, lang));
        } else {
            query.addField(new FieldQuery(true, false, Settings.QFIELD_LANG, Settings.QVALUE_LANG_DE));
        }
    }

    /**
     * Add provider(s) to query
     * 
     * @param query
     * @param providers
     */
    public static void processProvider(IngridQuery query, String[] providers) {
        boolean added  = false;
        
        if (providers != null && providers.length > 0 && Utils.getPosInArray(providers, Settings.PARAMV_ALL) == -1) {
            for (int i = 0; i < providers.length; i++) {
                if (providers[i] != null && providers[i].length() > 0) {
                    query.addToList("provider", new FieldQuery(true, false, Settings.QFIELD_PROVIDER, providers[i]));
                    added = true;
                }
            }
        }
        
        // remove grouped-field
        if (added && IngridQuery.GROUPED_BY_ORGANISATION.equals(query.get(Settings.QFIELD_GROUPED))) {
            query.remove(Settings.QFIELD_GROUPED);
        }
    }

    /**
     * Add partner(s) to query and remove grouped-field if it was set to partner.
     * Otherwise the already grouped results will be grouped again
     * 
     * @param query
     * @param partners
     */
    public static void processPartner(IngridQuery query, String[] partners) {
        ClauseQuery cq = null;
        boolean added  = false;

        // don't set anything if "all" is selected
        if (partners != null && Utils.getPosInArray(partners, Settings.PARAMV_ALL) == -1) {
            for (int i = 0; i < partners.length; i++) {
            	if (partners[i] != null && partners[i].length() > 0) {
            		if(cq == null){
            			cq = new ClauseQuery(true, false);
            		}
                    cq.addField(new FieldQuery(false, false, Settings.QFIELD_PARTNER, partners[i]));
                    added = true;
                }
            }
            if(cq != null){
            	query.addClause(cq);
            }
        }
        
        // remove grouped-field
        if (added && IngridQuery.GROUPED_BY_PARTNER.equals(query.get(Settings.QFIELD_GROUPED))) {
            query.remove(Settings.QFIELD_GROUPED);
        }
    }

    /**
     * Add domain to query
     * 
     * @param query
     * @param subject
     */
    public static void processDomain(IngridQuery query, String subject) {
        boolean added = false;
        if (subject != null && subject.trim().length() > 0) {
        	// domain subject already contains key, e.g. "site:..." or "plugid:..."
        	String[] keyValuePair = getDomainKeyValuePair(subject);
        	String domainKey = keyValuePair[0];
        	String domainValue = keyValuePair[1];
        	// domain can be plugid or site or ...
        	// we extract the according value from our subject
        	if (domainKey.equals(Settings.QFIELD_PLUG_ID)) {
        		processIPlugs(query, new String[] { domainValue });
        	} else {
        		if (!UtilsSearch.containsFieldOrKey(query, domainKey)) {
                	query.addField(new FieldQuery(true, false, domainKey, domainValue));
                	added = true;
                }        		
        	}
        }
        
        // remove grouped-field
        if (added && IngridQuery.GROUPED_BY_DATASOURCE.equals(query.get(Settings.QFIELD_GROUPED))) {
            query.remove(Settings.QFIELD_GROUPED);
        }
    }

    /**
     * When grouping by domain is active and all results of one domain should be shown
     * then this method extracts the key/value pair from the domain subject (was passed
     * in request from "Zeige alle ...").
     * NOTICE: a domain can be a datasource (plugid) or a top level domain of a URL ...
     * ( e.g. domain subject like "site:???" or "plugid:???")  
     * 
     * @param domainSubject the subject as its was passed from "Zeige alle" request
     */
    public static String[] getDomainKeyValuePair(String domainSubject) {
    	return domainSubject.split("::");
    }

    /**
     * Add grouping to query
     * 
     * @param query
     * @param grouping
     */
    public static void processGrouping(IngridQuery query, String grouping) {
        if (grouping != null) {
        	// check Frontend-Consts (when from IngridSessionPreferences) and Backend-consts (when from IngridQuery/SearchState)
            if (grouping.equals(Settings.PARAMV_GROUPING_PARTNER) || grouping.equals(IngridQuery.GROUPED_BY_PARTNER)) {
                query.put(Settings.QFIELD_GROUPED, IngridQuery.GROUPED_BY_PARTNER);
            } else if (grouping.equals(Settings.PARAMV_GROUPING_PROVIDER) || grouping.equals(IngridQuery.GROUPED_BY_ORGANISATION)) {
                query.put(Settings.QFIELD_GROUPED, IngridQuery.GROUPED_BY_ORGANISATION);
            } else if (grouping.equals(Settings.PARAMV_GROUPING_DOMAIN) || grouping.equals(IngridQuery.GROUPED_BY_DATASOURCE)) {
                query.put(Settings.QFIELD_GROUPED, IngridQuery.GROUPED_BY_DATASOURCE);
            } else if (grouping.equals(Settings.PARAMV_GROUPING_PLUG_ID) || grouping.equals(IngridQuery.GROUPED_BY_PLUGID)) {
                query.put(Settings.QFIELD_GROUPED, IngridQuery.GROUPED_BY_PLUGID);
            } else {
                query.remove(Settings.QFIELD_GROUPED);
            }
        }
    }

    /**
     * Encapsulates common doView functionality for all partner selection
     * portlets
     * 
     * @param request
     * @param context
     */
    public static void doViewForPartnerPortlet(RenderRequest request, Context context) {
        PortletSession session = request.getPortletSession();
        DisplayTreeNode partnerRoot = (DisplayTreeNode) session.getAttribute("partnerRoot");
        if (partnerRoot == null) {
            String partnerRestriction = PortalConfig.getInstance().getString(
                    PortalConfig.PORTAL_SEARCH_RESTRICT_PARTNER);
            if (partnerRestriction == null || partnerRestriction.length() == 0) {
                partnerRoot = DisplayTreeFactory.getTreeFromPartnerProviderFromDB(null);
            } else {
                ArrayList filter = new ArrayList();
                filter.add(partnerRestriction);
                partnerRoot = DisplayTreeFactory.getTreeFromPartnerProviderFromDB(filter);
            }
            session.setAttribute("partnerRoot", partnerRoot);
        }

        context.put("partnerRoot", partnerRoot);
    }

    /**
     * Encapsulates common processAction functionality for all term-adding
     * portlets.
     * 
     * @param request
     * @throws NotSerializableException
     */
    public static void processActionForTermPortlets(ActionRequest request)
            throws NotSerializableException {
        String addingType = request.getParameter("adding_type");
        String searchTerm = request.getParameter("search_term");
        String queryStr = (String) PortletMessaging.receive(request, Settings.MSG_TOPIC_SEARCH,
                Settings.PARAM_QUERY_STRING);
        if (addingType.equals("1")) {
            PortletMessaging.publish(request, Settings.MSG_TOPIC_SEARCH, Settings.PARAM_QUERY_STRING, UtilsQueryString
                    .addTerm(queryStr, searchTerm, UtilsQueryString.OP_AND));
        } else if (addingType.equals("2")) {
            PortletMessaging.publish(request, Settings.MSG_TOPIC_SEARCH, Settings.PARAM_QUERY_STRING, UtilsQueryString
                    .addTerm(queryStr, searchTerm, UtilsQueryString.OP_OR));
        } else if (addingType.equals("3")) {
            PortletMessaging.publish(request, Settings.MSG_TOPIC_SEARCH, Settings.PARAM_QUERY_STRING, UtilsQueryString
                    .addTerm(queryStr, searchTerm, UtilsQueryString.OP_PHRASE));
        } else if (addingType.equals("4")) {
            PortletMessaging.publish(request, Settings.MSG_TOPIC_SEARCH, Settings.PARAM_QUERY_STRING, UtilsQueryString
                    .addTerm(queryStr, searchTerm, UtilsQueryString.OP_NOT));
        }
    }

    /**
     * Encapsulates common processAction functionality for all partner selection
     * portlets
     * 
     * @param request
     */
    public static void processActionForPartnerPortlet(ActionRequest request)
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
            String resultQuery = processSearchPartner(queryStr, partnerRoot, request);
            PortletMessaging.publish(request, Settings.MSG_TOPIC_SEARCH, Settings.PARAM_QUERY_STRING, resultQuery);

        } else if (action.equalsIgnoreCase("doOpenPartner")) {
            DisplayTreeNode partnerRoot = (DisplayTreeNode) session.getAttribute("partnerRoot");
            if (partnerRoot != null) {
                DisplayTreeNode node = partnerRoot.getChild(request.getParameter("id"));
                node.setOpen(true);
            }
        } else if (action.equalsIgnoreCase("doClosePartner")) {
            DisplayTreeNode partnerRoot = (DisplayTreeNode) session.getAttribute("partnerRoot");
            if (partnerRoot != null) {
                DisplayTreeNode node = partnerRoot.getChild(request.getParameter("id"));
                node.setOpen(false);
            }
        }
    }

    public static String processSearchPartner(String queryStr, DisplayTreeNode partnerRoot, ActionRequest request) {
        Iterator it = partnerRoot.getChildren().iterator();
        String chk;
        HashMap partnerHash = new HashMap();
        HashMap providerHash = new HashMap();
        while (it.hasNext()) {
            DisplayTreeNode partnerNode = (DisplayTreeNode) it.next();
            chk = request.getParameter("chk_".concat(partnerNode.getId()));
            if (chk != null && chk.equals("on")) {
                partnerHash.put("partner:".concat(partnerNode.getId()), "1");
                partnerNode.put("checked", "true");
            } else {
                partnerNode.remove("checked");
            }
            if (partnerNode.isOpen()) {
                Iterator it2 = partnerNode.getChildren().iterator();
                while (it2.hasNext()) {
                    DisplayTreeNode providerNode = (DisplayTreeNode) it2.next();
                    chk = request.getParameter("chk_".concat(providerNode.getId()));
                    if (chk != null && chk.equals("on")) {
                        providerNode.put("checked", "true");
                        providerHash.put("provider:".concat(providerNode.getId()), "1");
                        partnerHash.put("partner:".concat(partnerNode.getId()), "1");
                    } else {
                        providerNode.remove("checked");
                    }
                }
            }
        }

        String subTerm = "";
        it = partnerHash.keySet().iterator();
        while (it.hasNext()) {
            String term = (String) it.next();
            if (it.hasNext()) {
                subTerm = subTerm.concat(term).concat(" ");
            } else {
                subTerm = subTerm.concat(term);
            }
        }
        if (partnerHash.size() > 0 && providerHash.size() > 0) {
            subTerm = subTerm.concat(" ");
        }

        it = providerHash.keySet().iterator();
        while (it.hasNext()) {
            String term = (String) it.next();
            if (it.hasNext()) {
                subTerm = subTerm.concat(term).concat(" ");
            } else {
                subTerm = subTerm.concat(term);
            }
        }

        if (subTerm.length() > 0) {
            return UtilsQueryString.addTerm(queryStr, subTerm, UtilsQueryString.OP_SIMPLE);
        } else {
            return queryStr;
        }
    }

    /**
     * Adds partner restrictions if they are definied in the portal
     * configuration AND if no partner has been added to the query so far.
     * 
     * @param query
     */
    public static void processRestrictingPartners(IngridQuery query) {
        if (query.getPositivePartner().length > 0 || query.getNegativePartner().length > 0) {
            return;
        }
        String[] partners = PortalConfig.getInstance().getStringArray(PortalConfig.PORTAL_SEARCH_RESTRICT_PARTNER);
        if (partners == null || partners.length == 0) {
            return;
        }

        processPartner(query, partners);
    }

    /**
     * Add a provider from simple search dialog to the query. Add if the query
     * does not already define a provider AND the provider is valid (not null,
     * not empty).
     * 
     * @param query
     *            The IngridQuery
     * @param provider
     *            The provider ident string.
     */
    public static void processRestrictingProvider(IngridQuery query, String provider) {
        if (query.getPositiveProvider().length > 0 || query.getNegativeProvider().length > 0) {
            return;
        }
        if (provider == null || provider.length() == 0 || provider.equals(Settings.PARAMV_ALL)) {
            return;
        }
        query.addToList("provider", new FieldQuery(true, false, Settings.QFIELD_PROVIDER, provider));
    }

    /**
     * Add a querystring to the session querystring history.
     * 
     * @param request
     *            The request.
     */
    public static void addQueryToHistory(RenderRequest request) {
        String action = request.getParameter(Settings.PARAM_ACTION);
        if (action == null || !action.equals(Settings.PARAMV_ACTION_NEW_SEARCH)) {
            return;
        }

        String queryString = SearchState.getSearchStateObjectAsString(request, Settings.PARAM_QUERY_STRING);
        String urlStr = Settings.PAGE_SEARCH_RESULT + SearchState.getURLParamsMainSearch(request);
        IngridSessionPreferences sessionPrefs = Utils.getSessionPreferences(request,
                IngridSessionPreferences.SESSION_KEY);
        QueryHistory history = (QueryHistory) sessionPrefs.getInitializedObject(IngridSessionPreferences.QUERY_HISTORY,
                QueryHistory.class);
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
                    query.addToList(IngridQuery.IPLUGS,
                            new FieldQuery(true, false, Settings.QFIELD_PLUG_ID, plugIds[i]));
                }
            }
        }
    }

    public static String removeSearchCatalogues(String queryStr) {
        queryStr = UtilsQueryString.replaceTerm(queryStr, "datatype:default", "");
        queryStr = UtilsQueryString.replaceTerm(queryStr, "datatype:topics", "");
        queryStr = UtilsQueryString.replaceTerm(queryStr, "datatype:service", "");

        return queryStr.trim();
    }

    public static String removeSearchSources(String queryString) {
        String resultingQueryStr;
        resultingQueryStr = UtilsQueryString.replaceTerm(queryString, Settings.QFIELD_DATATYPE + ":"
                + Settings.QVALUE_DATATYPE_SOURCE_WWW, "");
        resultingQueryStr = UtilsQueryString.replaceTerm(resultingQueryStr, Settings.QFIELD_DATATYPE + ":"
                + Settings.QVALUE_DATATYPE_SOURCE_METADATA, "");
        resultingQueryStr = UtilsQueryString.replaceTerm(resultingQueryStr, Settings.QFIELD_DATATYPE + ":"
                + Settings.QVALUE_DATATYPE_SOURCE_FIS, "");
        resultingQueryStr = UtilsQueryString.replaceTerm(resultingQueryStr, Settings.QFIELD_METACLASS + ":"
                + Settings.QVALUE_METACLASS_DATABASE, "");
        resultingQueryStr = UtilsQueryString.replaceTerm(resultingQueryStr, Settings.QFIELD_METACLASS + ":"
                + Settings.QVALUE_METACLASS_SERVICE, "");
        resultingQueryStr = UtilsQueryString.replaceTerm(resultingQueryStr, Settings.QFIELD_METACLASS + ":"
            + Settings.QVALUE_METACLASS_GEOSERVICE, "");
        resultingQueryStr = UtilsQueryString.replaceTerm(resultingQueryStr, Settings.QFIELD_METACLASS + ":"
                + Settings.QVALUE_METACLASS_DOCUMENT, "");
        resultingQueryStr = UtilsQueryString.replaceTerm(resultingQueryStr, Settings.QFIELD_METACLASS + ":"
                + Settings.QVALUE_METACLASS_MAP, "");
        resultingQueryStr = UtilsQueryString.replaceTerm(resultingQueryStr, Settings.QFIELD_METACLASS + ":"
                + Settings.QVALUE_METACLASS_JOB, "");
        resultingQueryStr = UtilsQueryString.replaceTerm(resultingQueryStr, Settings.QFIELD_METACLASS + ":"
                + Settings.QVALUE_METACLASS_PROJECT, "");

        resultingQueryStr = UtilsQueryString.stripQueryWhitespace(resultingQueryStr);

        return resultingQueryStr.trim();
    }

    public static String processSearchSources(String queryString, String[] sources, String[] meta) {
        if ((sources == null || sources.length == 0) && (meta == null || meta.length == 0)) {
            return queryString;
        }

        HashMap datatypes = new LinkedHashMap();
        String subTerm = "";

        // clean up query string
        // ALSO REMOVE Catalogues from other tab, so we get a query that makes
        // sense !
        String resultingQueryStr = UtilsSearch.removeSearchSources(queryString);
        resultingQueryStr = UtilsSearch.removeSearchCatalogues(resultingQueryStr);

        if(sources != null) {
            for (int i = 0; i < sources.length; i++) {
                if (sources[i].equals(SearchExtEnvAreaSourcesForm.VALUE_SOURCE_ALL)) {
                    datatypes.put(Settings.QFIELD_DATATYPE + ":" + Settings.QVALUE_DATATYPE_AREA_ENVINFO, "1");
                }
                if (sources[i].equals(SearchExtEnvAreaSourcesForm.VALUE_SOURCE_WWW)) {
                    datatypes.put(Settings.QFIELD_DATATYPE + ":" + Settings.QVALUE_DATATYPE_SOURCE_WWW, "1");
                }
                if (sources[i].equals(SearchExtEnvAreaSourcesForm.VALUE_SOURCE_FIS)) {
                    datatypes.put(Settings.QFIELD_DATATYPE + ":" + Settings.QVALUE_DATATYPE_SOURCE_FIS, "1");
                }
            }
        }
        HashMap metaclasses = new LinkedHashMap();
        for (int i = 0; i < meta.length; i++) {
            if (meta[i].equals(SearchExtEnvAreaSourcesForm.VALUE_META_ALL)) {
                datatypes.put(Settings.QFIELD_DATATYPE + ":" + Settings.QVALUE_DATATYPE_SOURCE_METADATA, "1");
                // empty meta classes, all metaclasses are selected
                metaclasses = new LinkedHashMap();
                break;
            }
            if (meta[i].equals(SearchExtEnvAreaSourcesForm.VALUE_META_0)) {
                metaclasses.put(Settings.QFIELD_METACLASS + ":" + Settings.QVALUE_METACLASS_JOB, "1");
            }
            if (meta[i].equals(SearchExtEnvAreaSourcesForm.VALUE_META_1)) {
                metaclasses.put(Settings.QFIELD_METACLASS + ":" + Settings.QVALUE_METACLASS_MAP, "1");
            }
            if (meta[i].equals(SearchExtEnvAreaSourcesForm.VALUE_META_2)) {
                metaclasses.put(Settings.QFIELD_METACLASS + ":" + Settings.QVALUE_METACLASS_DOCUMENT, "1");
            }
            if (meta[i].equals(SearchExtEnvAreaSourcesForm.VALUE_META_3)) {
                metaclasses.put(Settings.QFIELD_METACLASS + ":" + Settings.QVALUE_METACLASS_GEOSERVICE, "1");
            }
            if (meta[i].equals(SearchExtEnvAreaSourcesForm.VALUE_META_4)) {
                metaclasses.put(Settings.QFIELD_METACLASS + ":" + Settings.QVALUE_METACLASS_PROJECT, "1");
            }
            if (meta[i].equals(SearchExtEnvAreaSourcesForm.VALUE_META_5)) {
                metaclasses.put(Settings.QFIELD_METACLASS + ":" + Settings.QVALUE_METACLASS_DATABASE, "1");
            }
            if (meta[i].equals(SearchExtEnvAreaSourcesForm.VALUE_META_6)) {
              metaclasses.put(Settings.QFIELD_METACLASS + ":" + Settings.QVALUE_METACLASS_SERVICE, "1");
            }
        }
        // remove meta exclusion if we have a meta data class selection
        if (metaclasses.size() > 0) {
            datatypes.put(Settings.QFIELD_DATATYPE + ":" + Settings.QVALUE_DATATYPE_SOURCE_METADATA, "1");
        }
        // build the subquery
        if (!datatypes.containsKey(Settings.QFIELD_DATATYPE + ":" + Settings.QVALUE_DATATYPE_AREA_ENVINFO)
                && (metaclasses.size() > 0 || datatypes.size() > 0)) {
            Iterator it = datatypes.keySet().iterator();
            while (it.hasNext()) {
                String datatype = (String) it.next();
                subTerm = subTerm.concat(datatype);
                if (it.hasNext()) {
                    subTerm = subTerm.concat(" ");
                }
            }

            it = metaclasses.keySet().iterator();
            if (it.hasNext()) {
                subTerm = subTerm.concat(" (");
                while (it.hasNext()) {
                    String metaclass = (String) it.next();
                    subTerm = subTerm.concat(metaclass);
                    if (it.hasNext()) {
                        subTerm = subTerm.concat(" OR ");
                    }
                }
                subTerm = subTerm.concat(")");
            }
        }
        return UtilsQueryString.addTerm(resultingQueryStr, subTerm, UtilsQueryString.OP_SIMPLE);
    }

    /**
     * Get all fields with a specific field name.
     * 
     * @param q
     *            The query.
     * @param fieldName
     *            The fieldname.
     * @return The Array of resulting fields.
     */
    public static FieldQuery[] getField(IngridQuery q, String fieldName) {
        FieldQuery[] fields = q.getFields();
        ArrayList resultFields = new ArrayList();
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].getFieldName().equals(fieldName)) {
                resultFields.add(fields[i]);
            }
        }
        return (FieldQuery[]) resultFields.toArray(new FieldQuery[resultFields.size()]);
    }

    /**
     * Returns the WMS URL to cal the WMS Server. It adds necessary data such as
     * session id, locale, javascript enabled. It adds personalized WMS URLs and
     * allows to inject a custom WMS url.
     * 
     * @param request
     *            The PortletRequest.
     * @param wmsServiceUrl
     *            The additional to inject WMS Service URL
     * @param isViewer
     *            True to generate a URL to the WMS Viewer Template, alse for
     *            the WMS Search template.
     * @return
     */
    public static String getWMSURL(PortletRequest request, String wmsServiceUrl, boolean isViewer) {
        List wmsServices = null;

        WMSInterface service = WMSInterfaceImpl.getInstance();
        PortletSession session = request.getPortletSession();

        // check for personalizes wms services
        if (Utils.getLoggedOn(request) && service.getMapbenderVersion().equals(WMSInterface.MAPBENDER_VERSION_2_1)) {
            Principal principal = request.getUserPrincipal();
            Object obj = IngridPersistencePrefs.getPref(principal.getName(), IngridPersistencePrefs.WMS_SERVICES);
            if (obj instanceof List) {
                wmsServices = (List) obj;
            }
        }
        // make sure the list is not null
        if (wmsServices == null) {
            wmsServices = new ArrayList();
        }

        // add wms service from request parameters, if exist
        if (wmsServiceUrl != null && wmsServiceUrl.length() > 0) {
            wmsServices.add(new WMSServiceDescriptor("", wmsServiceUrl));
        }

        // create the wms url
        if (!wmsServices.isEmpty()) {
            return service.getWMSAddedServiceURL(wmsServices, session.getId(), request.getLocale(),
                    isViewer);
        } else if (isViewer) {
            return service.getWMSViewerURL(session.getId(), request.getLocale());
        } else {
            return service.getWMSSearchURL(session.getId(), request.getLocale());
        }
    }

    public static String updateQueryString(String addQueryString, PortletRequest request) {
        String paramQueryString = SearchState.getSearchStateObjectAsString(request, Settings.PARAM_QUERY_STRING);
        if(paramQueryString != null && paramQueryString.length() > 0){
            String [] tmpQueries = paramQueryString.split(" ");
            for(String tmpQuery: tmpQueries) {
                if(tmpQuery != null && tmpQuery.length() > 0 && !tmpQuery.equals("OR") && tmpQuery.indexOf(':') > -1 && addQueryString.indexOf(tmpQuery) > -1) {
                    return paramQueryString;
                }
            }
            if(paramQueryString.indexOf(" OR ") > -1) {
                paramQueryString = "(" + paramQueryString + ")";
            }
            if(addQueryString.indexOf(" OR ") > -1) {
                addQueryString = paramQueryString +  " (" + addQueryString + ")";
            } else {
                addQueryString = paramQueryString + " " + addQueryString;
            }
        }
        return addQueryString;
    }
}
