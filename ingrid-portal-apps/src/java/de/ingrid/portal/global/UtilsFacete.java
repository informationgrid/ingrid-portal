/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.portal.global;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;

import javax.portlet.ActionRequest;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;

import org.apache.log4j.Logger;
import org.apache.velocity.context.Context;

import de.ingrid.codelists.CodeListService;
import de.ingrid.codelists.model.CodeList;
import de.ingrid.codelists.model.CodeListEntry;
import de.ingrid.iplug.sns.utils.Topic;
import de.ingrid.portal.config.FacetsConfig;
import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.forms.SearchExtAdrPlaceReferenceForm;
import de.ingrid.portal.forms.SearchExtEnvPlaceGeothesaurusForm;
import de.ingrid.portal.forms.SearchExtResTopicAttributesForm;
import de.ingrid.portal.forms.SearchExtTimeReferenceForm;
import de.ingrid.portal.interfaces.IBUSInterface;
import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;
import de.ingrid.portal.interfaces.impl.SNSSimilarTermsInterfaceImpl;
import de.ingrid.portal.om.IngridEnvTopic;
import de.ingrid.portal.om.IngridFacet;
import de.ingrid.portal.om.IngridPartner;
import de.ingrid.portal.om.IngridProvider;
import de.ingrid.portal.search.DisplayTreeFactory;
import de.ingrid.portal.search.DisplayTreeNode;
import de.ingrid.portal.search.IPlugVersionInspector;
import de.ingrid.portal.search.UtilsSearch;
import de.ingrid.portal.search.catalog.CatalogTreeDataProvider;
import de.ingrid.portal.search.catalog.CatalogTreeDataProviderFactory;
import de.ingrid.utils.IngridDocument;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.PlugDescription;
import de.ingrid.utils.query.ClauseQuery;
import de.ingrid.utils.query.FieldQuery;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.query.RangeQuery;
import de.ingrid.utils.query.WildCardFieldQuery;
import de.ingrid.utils.queryparser.ParseException;
import de.ingrid.utils.queryparser.QueryStringParser;
import de.ingrid.utils.udk.UtilsDate;
import de.ingrid.utils.udk.UtilsUDKCodeLists;

/**
 * TODO Describe your created type (class, etc.) here.
 *
 * @author ktt@wemove.com
 */


@SuppressWarnings("unchecked")
public class UtilsFacete {

    private static final Logger log = Logger.getLogger(UtilsFacete.class);

    private static final String GEOTHESAURUS_CURRENT_TOPIC = "facete_current_topic_geothesaurus";
    private static final String GEOTHESAURUS_TOPICS = "facete_topics_geothesaurus";
    private static final String GEOTHESAURUS_SIMILAR_TOPICS = "facete_similar_topics_geothesaurus";
    private static final String GEOTHESAURUS_ERROR = "facete_error_geothesaurus";
    private static final String GEOTHESAURUS_LIST_SIZE = "facete_list_size_geothesaurus";
    private static final String GEOTHESAURUS_DO = "doGeothesaurus";
    private static final String GEOTHESAURUS_SELECTED_TOPICS_IDS = "geothesaurusSelectTopicsId";
    private static final String GEOTHESAURUS_TERM = "geothesaurusTerm";
    private static final String GEOTHESAURUS_SELECTED_TOPICS = "geothesaurusSelectTopics";
    private static final String GEOTHESAURUS_ALL_TOPICS = "allGeoThesaurusTopics";

    private static final String ELEMENTS_GEOTHESAURUS = "elementsGeothesaurus";
    private static final String ELEMENTS_MAP = "elementsMap";

    private static final String SELECTED_GEOTHESAURUS = "selectedGeothesaurus";
    private static final String SELECTED_MAP = "selectedMap";

    private static final String WILDCARD_IDS = "wildcard_ids";

    private static final String PARAMS_GEOTHESAURUS = "geothesaurus";
    private static final String PARAMS_MAP_X1 = "x1";
    private static final String PARAMS_MAP_X2 = "x2";
    private static final String PARAMS_MAP_Y1 = "y1";
    private static final String PARAMS_MAP_Y2 = "y2";
    private static final String PARAMS_MAP_OPTIONS = "options";
    private static final String PARAMS_AREA_ADDRESS_STREET = "street";
    private static final String PARAMS_AREA_ADDRESS_ZIP = "zip";
    private static final String PARAMS_AREA_ADDRESS_CITY = "city";
    private static final String PARAMS_ATTRIBUTE_TITLE = "db_title";
    private static final String PARAMS_ATTRIBUTE_INSTITUTE = "db_institute";
    private static final String PARAMS_ATTRIBUTE_PM = "db_pm";
    private static final String PARAMS_ATTRIBUTE_STAFF = "db_staff";
    private static final String PARAMS_ATTRIBUTE_ORG = "db_org";
    private static final String PARAMS_ATTRIBUTE_TERM_FROM = "term_from";
    private static final String PARAMS_ATTRIBUTE_TERM_TO = "term_to";
    private static final String PARAMS_WILDCARD = "wildcard";
    private static final String PARAMS_TIMEREF_FROM = "from";
    private static final String PARAMS_TIMEREF_TO = "to";

    // keys for accessing node data
    private static final String NODE_LEVEL = "level"; 
    private static final String NODE_PLUG_TYPE = "plugType"; 
    private static final String NODE_PLUG_ID = "plugId"; 
    private static final String NODE_DOC_ID = "docId"; 
    private static final String NODE_ORIG_DOC_ID = "origDocId";
    private static final String NODE_UDK_DOC_ID = "udk_docId";
    private static final String NODE_UDK_CLASS = "udk_class"; 
    private static final String NODE_UDK_CLASS_VALUE = "udk_class_value"; 
    private static final String NODE_EXPANDABLE = "expandable"; 

    public static final String FACET_CONFIG_DEFAULT = "config_default";
    public static final String FACET_CONFIG = "config";

    public static final String SESSION_PARAMS_FACET_GROUPING = "facet_grouping";

    private static Set<String> keys = null;

    /**
     * Prepare query by facet activity
     *
     * @param ps
     * @param query
     * @param messages 
     * @throws ParseException
     */
    public static IngridQuery facetePrepareInGridQuery (PortletRequest request, IngridQuery query, IngridResourceBundle messages) throws ParseException{

        // Check for pre prozess doAction.
        String portalTerm = request.getParameter("q");
        String paramsFacet = request.getParameter("f");

        if(paramsFacet == null) {
            removeAllFaceteSelections(request);
        }

        ArrayList<IngridFacet> config = (ArrayList<IngridFacet>) getAttributeFromSession(request, FACET_CONFIG_DEFAULT);
        if (config == null) {
            config = (ArrayList<IngridFacet>) FacetsConfig.getFacets();
            addDefaultIngridFacets(request, config);
            setAttributeToSession(request, FACET_CONFIG, config);
            setAttributeToSession(request, FACET_CONFIG_DEFAULT, config);
        }
        getFacetAttributsParamsFromUrl(request, config, messages);

        if(portalTerm != null){
            setAttributeToSession(request, "faceteTerm", portalTerm);
        }

        // Get all existing selection keys
        if(keys == null || keys.isEmpty()){
            keys = getExistingSelectionKeys(request);
        }

        // Set selection to query
        query = getQueryFacets(request, config, query, messages);

        // Get facet query from config file.
        if(query.get("FACETS") == null){
            ArrayList<IngridDocument> facetQueries = new ArrayList<>();
            getConfigFacetQuery(config, facetQueries, true, null);
            if(facetQueries != null){
                query.put("FACETS", facetQueries);
                setAttributeToSession(request, "FACETS_QUERY", facetQueries);
            }
        }

        setAttributeToSession(request, "SEARCH_QUERY", query);
        if(log.isDebugEnabled()){
            log.debug("Query Facete: " + query);
        }
        return query;
    }
    public static IngridQuery getQueryFacets(PortletRequest request, List<IngridFacet> config, IngridQuery query, IngridResourceBundle messages) throws ParseException {
        return getQueryFacets(request, config, query, messages, null);
    }

    public static IngridQuery getQueryFacets(PortletRequest request, List<IngridFacet> config, IngridQuery query, IngridResourceBundle messages, String parentId) throws ParseException {
        query = setFacetQuery(config, query, parentId);
        addToQueryMap(request, query);
        addToQueryGeothesaurus(request, query, messages);
        addToQueryAttribute(request, query);
        addToQueryAreaAddress(request, query);
        addToQueryWildcard(request, query);
        addToQueryTimeref(request, query);
        return query;
    }
    /**
     * Set facet to context
     *
     * @param request
     * @param context
     * @param facete
     */
    public static void setParamsToContext(RenderRequest request, Context context, IngridResourceBundle messages) {

        ArrayList<IngridFacet> config = (ArrayList<IngridFacet>) getAttributeFromSession(request, FACET_CONFIG);
        getFacetAttributsParamsFromUrl(request, config, messages);

        setParamsToContextMap(request, context);
        setParamsToContextGeothesaurus(request, context, messages);
        setParamsToContextAttribute(request, context);
        setParamsToContextAreaAddress(request, context);
        setParamsToContextWildcard(request, context);
        setParamsToContextTimeref(request, context);

        sortingFacet(config);
        selectedFacet(config);
        hiddenFacet(config);
        context.put("facetConfig", config);
        context.put("facetKeys", keys);
        context.put(Settings.PARAM_FACET, request.getParameter(Settings.PARAM_FACET));
        context.put("facetsQuery", getAttributeFromSession(request, "FACETS_QUERY"));
        context.put("searchQuery", getAttributeFromSession(request, "SEARCH_QUERY"));
        context.put("enableFacetSelection", isFacetSelection(request));
        // Set flag to check if facet component is select
        if(getAttributeFromSession(request, "isSelection") == null){
            context.put("isSelection", isAnyFacetConfigSelect(config, false));
        }else{
            context.put("isSelection", isAnyFacetConfigSelect(config, (Boolean) getAttributeFromSession(request, "isSelection")));
        }
        ArrayList<String> list = new ArrayList<String>();
        getSelectedGroup(config, list);
        context.put("selectedGroup", list);
        context.put("enableFacetDateSelection", PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_SEARCH_FACETE_DISPLAY_DATE_SELECTION, false));
        context.put("subFacetsCount", PortalConfig.getInstance().getInt(PortalConfig.PORTAL_SEARCH_FACETE_SUB_COUNT, 5));
        context.put("facetMapCenter", PortalConfig.getInstance().getStringArray(PortalConfig.PORTAL_SEARCH_FACETE_MAP_CENTER));
        context.put("leafletBgLayerWMTS", PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_LEAFLET_BG_LAYER_WMTS));
        context.put("leafletBgLayerAttribution", PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_LEAFLET_BG_LAYER_ATTRIBUTION));
        context.put("leafletBgLayerOpacity", PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_LEAFLET_BG_LAYER_OPACITY));

        String [] leafletBgLayerWMS = PortalConfig.getInstance().getStringArray(PortalConfig.PORTAL_MAPCLIENT_LEAFLET_BG_LAYER_WMS);
        String leafletBgLayerWMSURL = leafletBgLayerWMS[0];
        if(leafletBgLayerWMSURL.length() > 0 && leafletBgLayerWMS.length > 1){
            context.put( "leafletBgLayerWMSUrl", leafletBgLayerWMSURL);
            StringBuilder leafletBgLayerWMSName = new StringBuilder("");
            for (int i = 1; i < leafletBgLayerWMS.length; i++) {
                leafletBgLayerWMSName.append(leafletBgLayerWMS[i]);
                if(i < (leafletBgLayerWMS.length - 1)) {
                    leafletBgLayerWMSName.append(",");
                }
            }
            context.put( "leafletBgLayerWMSName", leafletBgLayerWMSName.toString());
        }
        // Remove flag
        removeAttributeFromSession(request, "isSelection");
    }

    /**
     * Set action parameters to facet session
     *
     * @param request
     * @param reponse
     * @return URL with facet parameters
     */
    @SuppressWarnings("rawtypes")
    public static String setFaceteParamsToSessionByAction(ActionRequest request, IngridResourceBundle messages) {
        StringBuilder facetUrl = new StringBuilder("&f=");

        general(request);
        ArrayList<IngridFacet> config = (ArrayList<IngridFacet>) getAttributeFromSession(request, FACET_CONFIG);
        String doRemove = request.getParameter("doRemove");
        if(doRemove != null){
            String[] split = doRemove.split(":");
            IngridFacet tmpFacetKey = getFacetById(config, split[0]);
            if(tmpFacetKey != null && tmpFacetKey.getFacets() != null){
                IngridFacet tmpFacetValue = getFacetById(tmpFacetKey.getFacets(), split[1]);
                if(tmpFacetValue != null && tmpFacetValue.getFacets() != null){
                    resetFacetConfigSelect(tmpFacetValue.getFacets());
                }
            }
        }else{
            // Get all existing selection keys
            if(keys == null || keys.isEmpty()){
                keys = getExistingSelectionKeys(request);
            }
            Set set = new TreeSet(request.getParameterMap().keySet());
            if(config != null){
                HashMap<String, String> lastSelection = null;
                boolean facetIsSelect = false; 
                boolean isQueryTypeOr = false;
                String keyQueryTypeOr = null;
                
                if(request.getParameter("doMultiFacets") != null) {
                    isQueryTypeOr = true;
                    keyQueryTypeOr = request.getParameter("doMultiFacets");
                    IngridFacet tmpFacet = getFacetById(config, keyQueryTypeOr);
                    if(tmpFacet.getFacets() != null) {
                        for(IngridFacet tmpSubFacet : tmpFacet.getFacets()){
                            if(tmpSubFacet.isSelect()) {
                                tmpSubFacet.setSelect(false);
                            }
                        }
                    }
                }
                for (Iterator<String> iterator = set.iterator(); iterator.hasNext();) {
                    String key = iterator.next();
                    String value = request.getParameter(key);
                    // Set facet selection
                    if(value != null){
                        if(isQueryTypeOr) {
                            key = keyQueryTypeOr;
                            if(key.equals(value)) {
                               continue;
                            }
                        }
                        resetFacetConfigValues(config, null);
                        IngridFacet tmpFacetKey = getFacetById(config, key);
                        if(tmpFacetKey != null){
                            List<String> valueIds = new ArrayList<>(Arrays.asList(value.split(",")));
                            if (valueIds.size() > 1) {
                                valueIds.add(value);
                            }
                            for (String valueId : valueIds) {
                                IngridFacet tmpFacetValue = getFacetById(tmpFacetKey.getFacets(), valueId);
                                if(tmpFacetValue != null){
                                    if(tmpFacetValue.isSelect()){
                                        tmpFacetValue.setSelect(false);
                                        if(tmpFacetValue.getFacets() != null){
                                            for(IngridFacet tmpSubFacet : tmpFacetValue.getFacets()){
                                                tmpSubFacet.setSelect(false);
                                            }
                                        }
                                        if(tmpFacetValue.isSelect() == false && tmpFacetValue.getSelectGroup() != null) {
                                            IngridFacet tmpSelectGroup = getFacetById(config, tmpFacetValue.getSelectGroup());
                                            if(tmpSelectGroup != null && tmpSelectGroup.getFacets() != null) {
                                                for(IngridFacet tmpSubFacet : tmpSelectGroup.getFacets()){
                                                    tmpSubFacet.setSelect(false);
                                                }
                                            }
                                        }
                                    }else{
                                        if(tmpFacetValue.isHierarchyTreeLeaf() &&
                                            tmpFacetValue.getFacets().isEmpty() &&
                                            tmpFacetValue.getHierarchyTreeNode() != null) {
                                            DisplayTreeNode node = tmpFacetValue.getHierarchyTreeNode();
                                            if(node != null) {
                                                openNode(node, node.getId());
                                                addHierarchyNodesToFacets(tmpFacetValue, node.getChildren(), config);
                                            }
                                        }
                                        tmpFacetValue.setSelect(true);
                                        facetIsSelect = true;
                                        // Set last selection
                                        if(lastSelection == null){
                                            lastSelection = new HashMap<>();
                                        }
                                        lastSelection.put(tmpFacetKey.getId() + ":" + tmpFacetValue.getId(), tmpFacetValue.getId());
                                    }
                                    if(isFacetConfigSelect(tmpFacetKey.getFacets())){
                                        tmpFacetKey.setSelect(facetIsSelect);
                                    }
                                }
                            }
                            String queryType = tmpFacetKey.getQueryType();
                            if(queryType != null && queryType.equals("OR")) {
                                if(tmpFacetKey.getFacets() != null) {
                                    boolean isToggleGroup = false;
                                    for(IngridFacet tmpSubFacet : tmpFacetKey.getFacets()){
                                        IngridFacet toggle = tmpSubFacet.getToggle();
                                        if(toggle != null) {
                                            if(valueIds.indexOf(toggle.getId()) > -1){
                                                isToggleGroup = true;
                                                break;
                                            }
                                        }
                                    }
                                    if(isToggleGroup) {
                                        for(IngridFacet tmpSubFacet : tmpFacetKey.getFacets()){
                                            IngridFacet toggle = tmpSubFacet.getToggle();
                                            if(toggle != null) {
                                                if(toggle.isSelect()) {
                                                    toggle.setSelect(false);
                                                } else {
                                                    toggle.setSelect(true);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    // Set dependency selection
                    if(config != null && value != null){
                        ArrayList<IngridFacet> facetDepList = (ArrayList<IngridFacet>) getDependencyFacetById(config, new ArrayList<>(), value);
                        for(IngridFacet facetDep : facetDepList){
                            IngridFacet dependencyValue  = getFacetById(config, facetDep.getDependency());
                            if(dependencyValue != null) {
                                if(dependencyValue.isSelect()){
                                    facetDep.setDependencySelect(dependencyValue.isSelect());
                                }else{
                                    if(facetDep.getFacets() != null){
                                        for(IngridFacet facetChild : facetDep.getFacets()){
                                            facetChild.setSelect(dependencyValue.isSelect());
                                        }
                                    }
                                    facetDep.setDependencySelect(dependencyValue.isSelect());
                                }
                            }
                        }
                    }

                    // Set hidden selection
                    if(config != null && value != null) {
                        ArrayList<IngridFacet> facetHidList = (ArrayList<IngridFacet>) getHiddenFacetById(config, new ArrayList<>(), value);
                        for(IngridFacet facetHid : facetHidList){
                            boolean isHiddenSelect = false;
                            if(facetIsSelect){
                                isHiddenSelect = true;
                            }
                            facetHid.setHiddenSelect(isHiddenSelect);
                        }
                    }
                }
                // Set last selection
                if(lastSelection != null){
                    setAttributeToSession(request, "lastConfigSelection", lastSelection, true);
                }
                // Set flag for selection
                setAttributeToSession(request, "isSelection", facetIsSelect);
            }
        }
        setAttributeToSession(request, FACET_CONFIG, config);
        setFaceteParamsToSessionMap(request);
        setFaceteParamsToSessionGeothesaurus(request, messages);
        setFaceteParamsToSessionAttribute(request);
        setFaceteParamsToSessionAreaAddress(request);
        setFaceteParamsToSessionWildcard(request);
        setFaceteParamsToSessionTimeref(request);

        // Create facet params URL
        setFacetUrlParamsToUrl(request, facetUrl);

        return facetUrl.toString();
    }

    /**
     * Check getting facets for categories
     *
     * @param facetes
     * @param request
     */
    public static void checkForExistingFacete(IngridHitsWrapper hits, PortletRequest request) {
        HashMap<String, Long> elementsGeothesaurus = null;
        HashMap<String, Long> elementsMap = null;
        Map<String, Object> facets = (Map<String, Object>) hits.get("FACETS");
        
        ArrayList<String> facetsOrFields = new ArrayList<>();
        if(log.isDebugEnabled()) {
            log.debug("Facets from hits: " + facets);
        }
        if(facets != null && !facets.isEmpty()){
            ArrayList<IngridFacet> config = (ArrayList<IngridFacet>) getAttributeFromSession(request, FACET_CONFIG);
            config = (ArrayList<IngridFacet>) cleanupFieldFacets(config);
            if(log.isDebugEnabled()) {
                log.debug("Facet keys: '" + keys + "'");
                if(config != null) {
                    for (IngridFacet ingridFacet : config) {
                        log.debug("Facet id: " + ingridFacet.getId());
                        if(ingridFacet.getFacets() != null) {
                            for (IngridFacet ingridFacetChild : ingridFacet.getFacets()) {
                                log.debug("Child facet id: " + ingridFacetChild.getId() + "::" + ingridFacetChild.getFacetValue());
                            }
                        }
                    }
                } else {
                    log.debug("Facet config is null!");
                }
            }
            for (Iterator<String> iterator = facets.keySet().iterator(); iterator.hasNext();) {
                String key = iterator.next();
                Long value = (Long) facets.get(key);
                if(log.isDebugEnabled()) {
                    log.debug("Facet key: '" + key + "'" + " value: " + value);
                }
                if(value > 0){
                    // Default facet
                    if(key.startsWith("geothesaurus:")){
                        if(elementsGeothesaurus == null){
                            elementsGeothesaurus = new HashMap<>();
                        }
                        elementsGeothesaurus.put(key.replace("geothesaurus:", ""), value);

                    }else if(key.startsWith("coords:")){
                        if(elementsMap == null){
                            elementsMap = new HashMap<>();
                        }
                        elementsMap.put(key.replace("coords:", ""), value);

                    }else{
                        // Generic facet by config
                        for (Iterator<String> iteratorKeys = keys.iterator(); iteratorKeys.hasNext();) {
                            String facetKey = iteratorKeys.next();
                            if(log.isDebugEnabled()) {
                                log.debug("Facet key from keys: '" + facetKey + "'");
                            }
                            if(key.startsWith("partner")){
                                IngridFacet ingridFacet = getFacetById(config, key.replace("partner:", ""));
                                if(ingridFacet != null){
                                    ingridFacet.setFacetValue(value.toString());
                                }
                            }else if(key.startsWith(facetKey)){
                                String facetSubkey = key.replace(facetKey + ":", "");
                                if(facetSubkey.indexOf(':') > -1){
                                    String[] list = facetSubkey.split(":");
                                    if(list.length > 1) {
                                        facetSubkey = list[1];
                                    } else {
                                        continue;
                                    }
                                }
                                if(log.isDebugEnabled()) {
                                    log.debug("Facet facetSubkey: '" + facetSubkey + "' from '" + facetKey + "'.");
                                }
                                IngridFacet ingridFacet = getFacetById(config, facetKey);
                                if(ingridFacet != null && facetSubkey != null && !facetSubkey.isEmpty()) {
                                    if(ingridFacet.getFacets() != null && ingridFacet.getField() == null){
                                        if(log.isDebugEnabled()) {
                                            log.debug("Facet facetSubkey: '" + facetSubkey + "' from '" + facetKey + "'.");
                                        }
                                        String queryType = ingridFacet.getQueryType();
                                        IngridFacet facet = getFacetById(ingridFacet.getFacets(), facetSubkey);
                                        if(facet != null) {
                                            if(log.isDebugEnabled()) {
                                                log.debug("Facet '" + facetKey + "' queryType: '" + queryType + "'.");
                                                log.debug("Subfacet '" + facetSubkey + "' value: '" + facet.getFacetValue() + "'.");
                                            }
                                            if(queryType == null || (queryType.equals("OR") && facet.getFacetValue() == null)){
                                                if(log.isDebugEnabled()) {
                                                    log.debug("Facet '" + facetSubkey + "' set value: '" + value.toString() + "'.");
                                                }
                                                facet.setFacetValue(value.toString());
                                            }
                                        }
                                    } else if(ingridFacet.getField() != null) {
                                        if(ingridFacet.getFacets() == null) {
                                            ingridFacet.setFacets(new ArrayList<IngridFacet>());
                                        }
                                        List<IngridFacet> newFieldFacets = ingridFacet.getFacets();

                                        boolean isNewFacet = false;
                                        IngridFacet newFieldFacet = getFacetById(newFieldFacets, key.replace(ingridFacet.getId() + ":", ""));
                                        if(newFieldFacet == null) {
                                            newFieldFacet= new IngridFacet();
                                            isNewFacet = true;
                                        }

                                        if(ingridFacet.getCodelist() != null && ingridFacet.getCodelistField() != null) {
                                            CodeListService codelistService = CodeListServiceFactory.instance();
                                            String codelistId = ingridFacet.getCodelist();
                                            String codelistField = ingridFacet.getCodelistField();
                                            CodeList codelist = codelistService.getCodeList(codelistId);
                                            List<CodeListEntry> codelistEntries = codelist.getEntries();
                                            String[] splitKey = facetSubkey.split("_");
                                            for (CodeListEntry codeListEntry : codelistEntries) {
                                                if(codeListEntry.getId().equals(splitKey[splitKey.length -1])) {
                                                    newFieldFacet.setId(codelistId + "_" + codeListEntry.getId());
                                                    newFieldFacet.setName(codeListEntry.getField(codelistField));
                                                    newFieldFacet.setQuery(ingridFacet.getField() + ":\"" + codeListEntry.getField(codelistField) + "\"");
                                                }
                                            }
                                            newFieldFacet.setParent(ingridFacet);
                                            newFieldFacet.setFacetValue(value.toString());
                                            if(isNewFacet) {
                                                newFieldFacets.add(newFieldFacet);
                                            }
                                            ingridFacet.setFacets(newFieldFacets);
                                        } else {
                                            newFieldFacet.setFacetValue(value.toString());
                                            if(isNewFacet) {
                                                String query = ingridFacet.getField().concat(":\"").concat(facetSubkey).concat("\"");
                                                newFieldFacet.setId(facetSubkey);
                                                newFieldFacet.setName(facetSubkey);
                                                newFieldFacet.setQuery(query);
                                                newFieldFacet.setParent(ingridFacet);
                                                newFieldFacets.add(newFieldFacet);
                                            }
                                            // Add all possible field facets
                                            if(ingridFacet.getQueryType() != null && ingridFacet.getQueryType().equals("OR")) {
                                                if(!facetsOrFields.contains(facetKey)) {
                                                    facetsOrFields.add(facetKey);
                                                    String portalQueryString = UtilsSearch.updateQueryString("", request);
                                                    try {
                                                        IngridQuery tmpQuery = QueryStringParser.parse( portalQueryString );
                                                        ArrayList<IngridDocument> facetQueries = new ArrayList<>();
                                                        if (tmpQuery.get( "FACETS" ) == null) {
                                                            IngridDocument tmpFacets = new IngridDocument();
                                                            tmpFacets.put("id", ingridFacet.getId());
                                                            tmpFacets.put("field", ingridFacet.getField());
                                                            facetQueries.add(tmpFacets);
                                                            if (!facetQueries.isEmpty()) {
                                                                tmpQuery.put( "FACETS", facetQueries );
                                                            }
                                                        }
        
                                                        IBUSInterface ibus = IBUSInterfaceImpl.getInstance();
                                                        IngridHits newHits = ibus.search( tmpQuery, Settings.SEARCH_RANKED_HITS_PER_PAGE, 1, 0, PortalConfig.getInstance().getInt( PortalConfig.QUERY_TIMEOUT_RANKED, 5000 ) );
        
                                                        if (newHits == null) {
                                                            if (log.isErrorEnabled()) {
                                                                log.error( "Problems fetching details to hit list !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
                                                            }
                                                        } else {
                                                            if(newHits.length() > 0) {
                                                                Map<String, Object> newFacets = (Map<String, Object>) newHits.get("FACETS");
                                                                if(newFacets != null){
                                                                    for (Iterator<String> newIter = newFacets.keySet().iterator(); newIter.hasNext();) {
                                                                        String newKey = newIter.next();
                                                                        IngridFacet tmpNewFieldFacet = getFacetById(newFieldFacets, newKey.replace(ingridFacet.getId() + ":", ""));
                                                                        if(tmpNewFieldFacet == null) {
                                                                            if(newKey.startsWith(ingridFacet.getId() + ":")) {
                                                                                String newValue = null;
                                                                                String[] list = newKey.split(":");
                                                                                if(list.length > 1) {
                                                                                    newValue = list[1];
                                                                                } else {
                                                                                    continue;
                                                                                }
                                                                                if(newValue != null) {
                                                                                    String newQuery = ingridFacet.getField().concat(":\"").concat(newValue).concat("\"");
                                                                                    tmpNewFieldFacet = new IngridFacet();
                                                                                    tmpNewFieldFacet.setId(newValue);
                                                                                    tmpNewFieldFacet.setName(newValue);
                                                                                    tmpNewFieldFacet.setQuery(newQuery);
                                                                                    tmpNewFieldFacet.setParent(ingridFacet);
                                                                                    tmpNewFieldFacet.setDisplay(true);
                                                                                    tmpNewFieldFacet.setFacetValue("0");
                                                                                    newFieldFacets.add(tmpNewFieldFacet);
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    } catch (Exception e) {
                                                        log.error("Error create subfacet for: " + facetSubkey);
                                                    }
                                                }
                                            }
                                            ingridFacet.setFacets(newFieldFacets);
                                        }
                                    }
                                }
                            }
                        }
                        setAttributeToSession(request, FACET_CONFIG, config);
                        if(log.isDebugEnabled()) {
                            if(config != null) {
                                for (IngridFacet ingridFacet : config) {
                                    log.debug("Facet id: " + ingridFacet.getId());
                                    if(ingridFacet.getFacets() != null) {
                                        for (IngridFacet ingridFacetChild : ingridFacet.getFacets()) {
                                            log.debug("Child facet id: " + ingridFacetChild.getId() + "::" + ingridFacetChild.getFacetValue());
                                        }
                                    }
                                }
                            } else {
                                log.debug("Facet config is null!");
                            }
                        }
                    }
                }
            }
            if (elementsMap != null){
                setAttributeToSession(request, ELEMENTS_MAP, sortHashMapAsArrayList(elementsMap));
            } else{
                removeAttributeFromSession(request, ELEMENTS_MAP);
            }
        }
    }

    private static List<IngridFacet> cleanupFieldFacets(List<IngridFacet> config) {
        for (IngridFacet ingridFacet : config) {
            if(ingridFacet.getField() != null && ingridFacet.getFacets() != null) {
                for (int i=0; i < ingridFacet.getFacets().size(); i++) {
                    IngridFacet ingridFacetChild = ingridFacet.getFacets().get(i);
                    if(!ingridFacetChild.isSelect()) {
                        ingridFacet.getFacets().remove(i);
                        i--;
                    }
                }
            }
            ingridFacet.setFacetValue(null);
        }
        return config;
    }

    /***************************** WILDCARD ***********************************************/

    private static void setParamsToContextWildcard(RenderRequest request, Context context) {
        ArrayList<String> wildcardIds = (ArrayList<String>) getAttributeFromSession( request, WILDCARD_IDS );
        if(wildcardIds != null && !wildcardIds.isEmpty()){
            HashMap<String, HashMap<String, String>> wildcardMap = new HashMap<>();
            for (String wildcardId : wildcardIds) {
                HashMap<String, String> wildcard = (HashMap<String, String>) getAttributeFromSession( request, wildcardId );
                if(wildcard != null){
                    wildcardMap.put( wildcardId, wildcard );
                }
            }
            if(!wildcardMap.isEmpty()){
                setFacetSelectionState(context, request, "isWildcardSelect", true);
                context.put( "wildcardMap", wildcardMap );
            }else{
                setFacetSelectionState(context, request, "isWildcardSelect", false);
            }
        }else{
            setFacetSelectionState(context, request, "isWildcardSelect", false);
        }
    }

    private static void setFaceteParamsToSessionWildcard(ActionRequest request) {
        String id = request.getParameter( "wildcard_id" );
        String wildcardKey = request.getParameter( "wildcard" );
        String term = null;
        String action = null;
        String actionClear = null;

        if(id != null){
            HashMap<String, String> wildcard = (HashMap<String, String>) getAttributeFromSession( request, id );
            ArrayList<String> wildcardIds = (ArrayList<String>) getAttributeFromSession( request, WILDCARD_IDS );

            Enumeration<String> paramNames = request.getParameterNames();
            while (paramNames.hasMoreElements()) {
                String paramName = paramNames.nextElement();
                String paramValue = request.getParameter( paramName );
                if(paramName.indexOf( "termWildcard" ) > -1){
                    term = paramValue;
                }else if(paramName.indexOf( "doSearchWildcard" ) > -1){
                    action = paramValue;
                }else if(paramName.indexOf( "doClearSearchWildcard" ) > -1){
                    actionClear = paramName;
                }
            }
            if(term != null && action != null){
                if(term.trim().length() > 0){
                    if(wildcard == null){
                        wildcard  = new HashMap<>();
                    }
                    wildcard.put( "wildcard", wildcardKey );
                    wildcard.put( "term", term );
                    setAttributeToSession(request, id, wildcard, true);

                    if(wildcardIds == null){
                        wildcardIds = new ArrayList<>();
                    }
                    wildcardIds.add( id );
                    setAttributeToSession(request, WILDCARD_IDS, wildcardIds );
                }
            }else if(actionClear != null){
                if(wildcardIds != null){
                    wildcardIds.remove( id );
                    ArrayList<IngridFacet> config = (ArrayList<IngridFacet>) getAttributeFromSession(request, FACET_CONFIG);
                    IngridFacet facetById = getFacetById(config, id);
                    if (facetById != null && facetById.getParent() != null) {
                        boolean open = false;
                        ArrayList<IngridFacet> children = (ArrayList<IngridFacet>) facetById.getParent().getFacets();
                        for(IngridFacet child: children) {
                            open = open || child.isOpen();
                        }
                        facetById.getParent().setOpen(open);
                    }
                    if(wildcardIds.isEmpty()){
                        removeAttributeFromSession( request, id);
                    }else{
                        setAttributeToSession(request, WILDCARD_IDS, wildcardIds );
                    }
                }
                removeAttributeFromSession( request, id);
            }
        }
    }

    public static void addToQueryWildcard(PortletRequest request, IngridQuery query) {
        ArrayList<String> wildcardIds = (ArrayList<String>) getAttributeFromSession( request, WILDCARD_IDS );
        if(wildcardIds != null){
            for (String wildcardId : wildcardIds) {
                if(wildcardId != null){
                    boolean required = true;
                    boolean prohibited = false;
                    String fieldName = null;
                    String wildCardValue = null;

                    HashMap<String, String> wildcard = (HashMap<String, String>) getAttributeFromSession(request, wildcardId);
                    if(wildcard != null){
                        fieldName = wildcard.get( "wildcard" );
                        wildCardValue = wildcard.get( "term" );
                        if(wildCardValue != null && wildCardValue.length() > 0 && wildCardValue.indexOf('*') == -1){
                            wildCardValue = wildCardValue + "*";
                        }

                        if (fieldName.contains("|")) {
                            for (String s: fieldName.split("\\|")) {
                                query.addWildCardFieldQuery(new WildCardFieldQuery(!required, prohibited, s, wildCardValue));
                            }
                        } else {
                            query.addWildCardFieldQuery(new WildCardFieldQuery(required, prohibited, fieldName, wildCardValue));
                        }
                    }
                }
            }
        }
    }

    /***************************** KARTE ***********************************************/

    @SuppressWarnings("rawtypes")
    private static void setFaceteParamsToSessionMap(ActionRequest request) {

        String doAddMap = request.getParameter("doAddMap");
        String doRemoveMap = request.getParameter("doRemoveMap");
        HashMap<String, String> doMapCoords = null;
        HashMap<String, String> webmapclientCoords = null;
        ArrayList<String> coordOptions = null;
        HashMap selectedMap = null;

        if(doAddMap != null){
            if(!request.getParameter("areaid").equals("")){
                if(selectedMap == null)
                    selectedMap = new HashMap();
                doMapCoords = new HashMap<>();
                doMapCoords.put("inside", "AGS: "+request.getParameter("areaid"));
                selectedMap.put("areaid", request.getParameter("areaid"));
            }else{
                coordOptions = new ArrayList<>();
                webmapclientCoords = new HashMap<>();

                ArrayList<IngridFacet> config = (ArrayList<IngridFacet>) getAttributeFromSession(request, FACET_CONFIG);
                IngridFacet facet = getFacetById(config, "area.map");
                if(facet != null && facet.getOptions() != null) {
                    ArrayList<String> options = facet.getOptions();
                    for (String option : options) {
                        coordOptions.add(option);
                    }
                }
                if(coordOptions.isEmpty()){
                    coordOptions.add("inside");
                }

                if(request.getParameter("x1") != null){
                    webmapclientCoords.put("x1", request.getParameter("x1"));
                }
                if(request.getParameter("x2") != null){
                    webmapclientCoords.put("x2", request.getParameter("x2"));
                }
                if(request.getParameter("y1") != null){
                    webmapclientCoords.put("y1", request.getParameter("y1"));
                }
                if(request.getParameter("y2") != null){
                    webmapclientCoords.put("y2", request.getParameter("y2"));
                }
                if(coordOptions != null && !coordOptions.isEmpty()){
                    doMapCoords = new HashMap<>();
                    for(int i=0; i < coordOptions.size(); i++){
                        String searchTerm = "";
                        if(request.getParameter("x1") != null){
                            searchTerm = webmapclientCoords.get("x1").concat(", ");
                        }
                        if(request.getParameter("y1") != null){
                            searchTerm = searchTerm.concat(webmapclientCoords.get("y1")).concat(", ");
                        }
                        if(request.getParameter("x2") != null){
                            searchTerm = searchTerm.concat(webmapclientCoords.get("x2")).concat(", ");
                        }
                        if(request.getParameter("y2") != null){
                            searchTerm = searchTerm.concat(webmapclientCoords.get("y2"));
                        }
                        doMapCoords.put(coordOptions.get(i), searchTerm);
                    }
                }
            }

            if(doMapCoords != null){
                if(selectedMap == null){
                    selectedMap = new HashMap();
                }
                selectedMap.put("doMapCoords", doMapCoords);
            }
            if(coordOptions != null){
                if(selectedMap == null){
                    selectedMap = new HashMap();
                }
                selectedMap.put("coordOptions", coordOptions);
            }
            if(webmapclientCoords != null){
                if(selectedMap == null){
                    selectedMap = new HashMap();
                }
                selectedMap.put("webmapclientCoords", webmapclientCoords);
            }
        }
        if(selectedMap != null){
            setAttributeToSession(request, SELECTED_MAP, selectedMap, true);
        }
        if(doRemoveMap != null){
            removeAttributeFromSession(request, SELECTED_MAP);
        }
    }

    @SuppressWarnings("rawtypes")
    private static void setParamsToContextMap (RenderRequest request, Context context){

        HashMap selectedMap = (HashMap) getAttributeFromSession(request, SELECTED_MAP);

        if(selectedMap != null && !selectedMap.isEmpty()){
            setFacetSelectionState(context, request, "isMapSelect", true);
            context.put(SELECTED_MAP, selectedMap);
            context.put(ELEMENTS_MAP, getAttributeFromSession(request, ELEMENTS_MAP));
        }else{
            setFacetSelectionState(context, request, "isMapSelect", false);
        }
    }

    @SuppressWarnings("rawtypes")
    public static void addToQueryMap(PortletRequest request, IngridQuery query) {

        HashMap selectedMap = (HashMap) getAttributeFromSession(request, SELECTED_MAP);
        if(selectedMap != null){
            HashMap<String, String> webmapclientCoords = (HashMap<String, String>) selectedMap.get("webmapclientCoords");
            if (webmapclientCoords != null && !webmapclientCoords.isEmpty()){
                ArrayList<String> coordOptions = (ArrayList<String>) selectedMap.get("coordOptions");
                if(coordOptions != null && !coordOptions.isEmpty()){
                    ClauseQuery cq = new ClauseQuery(true, false);
                    if(coordOptions.size() == 1) {
                        cq.addField(new FieldQuery(true, false, "x1", webmapclientCoords.get("x1")));
                        cq.addField(new FieldQuery(true, false, "y1", webmapclientCoords.get("y1")));
                        cq.addField(new FieldQuery(true, false, "x2", webmapclientCoords.get("x2")));
                        cq.addField(new FieldQuery(true, false, "y2", webmapclientCoords.get("y2")));
                        cq.addField(new FieldQuery(true, false, "coord", coordOptions.get(0)));
                        query.addClause(cq);
                    }else{
                        for(int i=0; i<coordOptions.size();i++){
                            ClauseQuery coordQuery = new ClauseQuery(false, false);
                            coordQuery.addField(new FieldQuery(true, false, "x1", webmapclientCoords.get("x1")));
                            coordQuery.addField(new FieldQuery(true, false, "y1", webmapclientCoords.get("y1")));
                            coordQuery.addField(new FieldQuery(true, false, "x2", webmapclientCoords.get("x2")));
                            coordQuery.addField(new FieldQuery(true, false, "y2", webmapclientCoords.get("y2")));
                            coordQuery.addField(new FieldQuery(true, false, "coord", coordOptions.get(i)));
                            cq.addClause(coordQuery);
                        }
                        query.addClause(cq);
                    }
                }
            }else if(selectedMap.get("areaid") != null){
                ClauseQuery cq = new ClauseQuery(true, false);
                cq.addField(new FieldQuery(true, false, "areaid", (String)selectedMap.get("areaid")));
                query.addClause(cq);
            }
        }
    }

    /***************************** GEOTHESAURUS ****************************************/

    /**
     * Action Params for "Search Geothesaurus
     *
     * @param request
     */

    @SuppressWarnings("rawtypes")
    private static void setFaceteParamsToSessionGeothesaurus(ActionRequest request, IngridResourceBundle messages) {
        String doGeothesaurus = request.getParameter("doGeothesaurus");
        String doCancelGeothesaurus = request.getParameter("doCancelGeothesaurus");
        String doAddGeothesaurus = request.getParameter("doAddGeothesaurus");
        String doSearchGeothesaurus = request.getParameter("doSearchGeothesaurus");
        String doBrowseGeothesaurus = request.getParameter("doBrowseGeothesaurus");
        String doBrowseSimilarGeothesaurus = request.getParameter("doBrowseSimilarGeothesaurus");
        String doRemoveGeothesaurus = request.getParameter("doRemoveGeothesaurus");
        String doClearSearchGeothesaurus = request.getParameter("doClearSearchGeothesaurus");

        if(doCancelGeothesaurus != null){
            setAttributeToSession(request, GEOTHESAURUS_DO, false);
            request.removeAttribute("doCancelGeothesaurus");
        }else{
            HashMap selectedGeothesaurus = null;

            if(doGeothesaurus != null){
                boolean isGeothesaurus = false;
                if(doGeothesaurus.equals("true")){
                    isGeothesaurus = true;
                }
                setAttributeToSession(request, GEOTHESAURUS_DO, isGeothesaurus);
            }

            if(doAddGeothesaurus != null){
                setAttributeToSession(request, GEOTHESAURUS_DO, false);
                if(getAttributeFromSession(request, GEOTHESAURUS_LIST_SIZE) != null){
                    selectedGeothesaurus = (HashMap) getAttributeFromSession(request, SELECTED_GEOTHESAURUS);
                    if(selectedGeothesaurus == null){
                        selectedGeothesaurus = new HashMap();
                    }
                    String listSize = getAttributeFromSession(request, GEOTHESAURUS_LIST_SIZE).toString();
                    ArrayList<String> selectedIds = (ArrayList<String>) selectedGeothesaurus.get(GEOTHESAURUS_SELECTED_TOPICS_IDS);
                    if(selectedIds == null){
                        selectedIds = new ArrayList<>();
                    }

                    for(int i = 0; i < selectedIds.size(); i++){
                        String selectedId = selectedIds.get(i);
                        IngridHit[] topics = (IngridHit[]) getAttributeFromSession(request, GEOTHESAURUS_TOPICS);
                        if(topics != null){
                            for (IngridHit hit : topics){
                                Topic topic = (Topic) hit;
                                if(selectedId.equals(topic.getTopicNativeKey())){
                                    selectedIds.remove(selectedId);
                                    i--;
                                }
                            }
                        }
                    }

                    if(listSize != null){
                        for (int i=0; i< Integer.parseInt(listSize); i++) {
                            String chkVal = request.getParameter("chk"+(i+1));
                            boolean isFound = false;
                            if (chkVal != null) {
                                for(int j = 0; j < selectedIds.size(); j++){
                                    String selectedId = selectedIds.get(j);
                                    if(selectedId.equals(chkVal)){
                                        isFound = true;
                                        break;
                                    }
                                }
                                if(!isFound){
                                    selectedIds.add(chkVal);
                                }
                            }
                        }
                    }

                    if(selectedIds != null){
                        selectedGeothesaurus.put(GEOTHESAURUS_SELECTED_TOPICS_IDS, selectedIds);
                    }
                }
            }

            if(doRemoveGeothesaurus != null && doAddGeothesaurus == null){
                if(doRemoveGeothesaurus.equals("all")){
                    removeAttributeFromSession(request, SELECTED_GEOTHESAURUS);
                }else{
                    selectedGeothesaurus = (HashMap) getAttributeFromSession(request, SELECTED_GEOTHESAURUS);
                    if(selectedGeothesaurus != null){
                        ArrayList<String> selectedIds = (ArrayList<String>) selectedGeothesaurus.get(GEOTHESAURUS_SELECTED_TOPICS_IDS);
                        if (selectedIds != null){
                            for(int i = 0; i < selectedIds.size(); i++){
                                if(selectedIds.get(i).equals(doRemoveGeothesaurus)){
                                    selectedIds.remove(i);
                                }
                            }
                            selectedGeothesaurus.put(GEOTHESAURUS_SELECTED_TOPICS_IDS, selectedIds);
                            if(!selectedIds.isEmpty()){
                                setAttributeToSession(request, SELECTED_GEOTHESAURUS, selectedGeothesaurus, true);
                            }else{
                                removeAttributeFromSession(request, SELECTED_GEOTHESAURUS);
                            }
                        }
                    }
                }
                setAttributeToSession(request, GEOTHESAURUS_DO, false);
            }

            if (doSearchGeothesaurus != null){
                setAttributeToSession(request, GEOTHESAURUS_DO, true);
                removeAttributeFromSession(request, GEOTHESAURUS_TOPICS);
                removeAttributeFromSession(request, GEOTHESAURUS_SIMILAR_TOPICS);
                removeAttributeFromSession(request, GEOTHESAURUS_CURRENT_TOPIC);

                String geothesaurusTerm = null;
                SearchExtEnvPlaceGeothesaurusForm f = (SearchExtEnvPlaceGeothesaurusForm) Utils.getActionForm(request, SearchExtEnvPlaceGeothesaurusForm.SESSION_KEY, SearchExtEnvPlaceGeothesaurusForm.class);
                f.clearErrors();
                f.populate(request);
                if (f.validate()) {
                    geothesaurusTerm = f.getInput(SearchExtEnvPlaceGeothesaurusForm.FIELD_SEARCH_TERM);
                    if(geothesaurusTerm != null){
                        setAttributeToSession(request, GEOTHESAURUS_TERM, geothesaurusTerm);
                    }
                }

                if(geothesaurusTerm != null){
                    IngridHit[] topics = SNSSimilarTermsInterfaceImpl.getInstance().getTopicsFromText(geothesaurusTerm, "/location", request.getLocale());
                    if (topics != null && topics.length > 0) {
                        for (int i=0; i<topics.length; i++) {
                            String href = UtilsSearch.getDetailValue(topics[i], "href");
                            if (href != null && href.lastIndexOf('#') != -1) {
                                topics[i].put("topic_ref", href.substring(href.lastIndexOf('#')+1));
                            }
                            addToListOfTopicsGeoThesaurus(topics[i], request);
                        }
                        setAttributeToSession(request, GEOTHESAURUS_TOPICS, topics);
                        setAttributeToSession(request, GEOTHESAURUS_LIST_SIZE, topics.length);
                        setAttributeToSession(request, GEOTHESAURUS_ERROR, false);
                    }else{
                        setAttributeToSession(request, GEOTHESAURUS_ERROR, true);
                    }
                }
            }

            if(doBrowseGeothesaurus != null || doBrowseSimilarGeothesaurus != null){
                IngridHit[] topics = null;
                if (doBrowseGeothesaurus != null) {
                    topics = (IngridHit[]) getAttributeFromSession(request, GEOTHESAURUS_TOPICS);
                } else {
                    doBrowseGeothesaurus = doBrowseSimilarGeothesaurus;
                    if (doBrowseGeothesaurus != null) {
                        topics = (IngridHit[]) getAttributeFromSession(request, GEOTHESAURUS_SIMILAR_TOPICS);

                    }
                }

                if (topics != null && topics.length > 0) {
                    int listSize = 0;
                    for (int i=0; i<topics.length; i++) {
                        String tid = UtilsSearch.getDetailValue(topics[i], "topicID");
                        if (tid != null && tid.equals(doBrowseGeothesaurus)) {
                            listSize = listSize + 1;
                            setAttributeToSession(request, GEOTHESAURUS_CURRENT_TOPIC, topics[i]);
                            IngridHit[] similarTopics = SNSSimilarTermsInterfaceImpl.getInstance().getTopicSimilarLocationsFromTopic(doBrowseGeothesaurus, request.getLocale());
                            if (similarTopics == null) {
                                SearchExtEnvPlaceGeothesaurusForm f = (SearchExtEnvPlaceGeothesaurusForm) Utils.getActionForm(request, SearchExtEnvPlaceGeothesaurusForm.SESSION_KEY, SearchExtEnvPlaceGeothesaurusForm.class);
                                f.setError("", "searchExtEnvPlaceGeothesaurus.error.no_term_found");
                                break;
                            }
                            for (int j=0; j<similarTopics.length; j++) {
                                listSize = listSize + 1;
                                String href = UtilsSearch.getDetailValue(similarTopics[j], "abstract");
                                if (href != null && href.lastIndexOf('#') != -1) {
                                    similarTopics[j].put("topic_ref", href.substring(href.lastIndexOf('#')+1));
                                }
                                addToListOfTopicsGeoThesaurus(similarTopics[j], request);
                            }
                            setAttributeToSession(request, GEOTHESAURUS_SIMILAR_TOPICS, similarTopics);
                            break;
                        }
                    }
                    setAttributeToSession(request, GEOTHESAURUS_LIST_SIZE, listSize);
                }
            }

            if(doGeothesaurus == null){
                ArrayList<HashMap<String, String>> geothesaurusSelectTopics = getSelectedGeothesaurusTopics(request, messages);
                if(selectedGeothesaurus != null){
                    selectedGeothesaurus.put(GEOTHESAURUS_SELECTED_TOPICS, geothesaurusSelectTopics);
                    ArrayList<String> selectedTopicIds = (ArrayList<String>) selectedGeothesaurus.get(GEOTHESAURUS_SELECTED_TOPICS_IDS);
                    boolean isSelectedGeothesaurus = false;
                    if(selectedTopicIds != null && !selectedTopicIds.isEmpty()){
                        isSelectedGeothesaurus = true;
                    }
                    setAttributeToSession(request, SELECTED_GEOTHESAURUS, selectedGeothesaurus, isSelectedGeothesaurus);
                }
            }

            if(doClearSearchGeothesaurus != null || doAddGeothesaurus != null){
                removeAttributeFromSession(request, GEOTHESAURUS_TERM);
                removeAttributeFromSession(request, GEOTHESAURUS_TOPICS);
                removeAttributeFromSession(request, GEOTHESAURUS_SIMILAR_TOPICS);
                removeAttributeFromSession(request, GEOTHESAURUS_CURRENT_TOPIC);
            }
        }
    }

    private static void setParamsToContextGeothesaurus (RenderRequest request, Context context, IngridResourceBundle messages){
        // Nach Raumbezug suchen
        IngridHit[] geothesaurusTopics = (IngridHit []) getAttributeFromSession(request, GEOTHESAURUS_TOPICS);
        context.put("geothesaurusTopics", geothesaurusTopics);
        context.put("geothesaurusTopicsBrowse", getAttributeFromSession(request, GEOTHESAURUS_SIMILAR_TOPICS));
        context.put("geothesaurusCurrentTopic", getAttributeFromSession(request, GEOTHESAURUS_CURRENT_TOPIC));
        context.put("list_size", getAttributeFromSession(request, GEOTHESAURUS_LIST_SIZE));

        ArrayList<HashMap<String, String>> geothesaurusSelectTopics = getSelectedGeothesaurusTopics(request, messages);
        ArrayList<HashMap<String, String>> geothesaurusSelectTopicsSorted = new ArrayList<>();
        ArrayList<HashMap<String, Long>> elementsGeothesaurus = (ArrayList<HashMap<String, Long>>) getAttributeFromSession(request, ELEMENTS_GEOTHESAURUS);

        if(elementsGeothesaurus != null){
            for(int i=0; i<elementsGeothesaurus.size(); i++){
                HashMap<String, Long> geothesaurus = elementsGeothesaurus.get(i);
                String geothesaurusFacetId = null;
                for (Iterator<String> iterator = geothesaurus.keySet().iterator(); iterator.hasNext();) {
                    geothesaurusFacetId = iterator.next();
                }
                if(geothesaurusFacetId != null){
                    for(int j=0; j<geothesaurusSelectTopics.size();j++){
                        HashMap <String, String> geothesaurusSelectTopic = geothesaurusSelectTopics.get(j);
                        String geothesaurusSelectId = geothesaurusSelectTopic.get("topicId");
                        if(geothesaurusSelectId != null && geothesaurusFacetId.equals(geothesaurusSelectId)){
                            geothesaurusSelectTopicsSorted.add(geothesaurusSelectTopic);
                            geothesaurusSelectTopics.remove(j);
                            break;
                        }
                    }
                }
            }
            geothesaurusSelectTopicsSorted.addAll(geothesaurusSelectTopics);
        }else{
            geothesaurusSelectTopicsSorted.addAll(geothesaurusSelectTopics);
        }
        if(!geothesaurusSelectTopicsSorted.isEmpty()){
            context.put(GEOTHESAURUS_SELECTED_TOPICS, geothesaurusSelectTopicsSorted);
            setFacetSelectionState(context, request, "isGeothesaurusSelect", true);
        }else{
            setFacetSelectionState(context, request, "isGeothesaurusSelect", false);
        }
        if(getAttributeFromSession(request, GEOTHESAURUS_DO) != null){
            context.put("doGeothesaurus", getAttributeFromSession(request, GEOTHESAURUS_DO));
        }
        context.put(GEOTHESAURUS_TERM, getAttributeFromSession(request, GEOTHESAURUS_TERM));
        context.put("geothesaurusError", getAttributeFromSession(request, GEOTHESAURUS_ERROR));
        context.put(ELEMENTS_GEOTHESAURUS, elementsGeothesaurus);
    }


    public static void addToQueryGeothesaurus(PortletRequest request, IngridQuery query, IngridResourceBundle messages) {
        ArrayList<HashMap<String, String>> geothesaurusSelectTopics = getSelectedGeothesaurusTopics(request, messages);
        if(!geothesaurusSelectTopics.isEmpty()){
            ClauseQuery cq = null;

            for (int i = 0; i < geothesaurusSelectTopics.size(); i++) {
                HashMap<String, String> map = geothesaurusSelectTopics.get(i);
                if (map != null) {
                    String topicId = map.get("topicId");
                    if (i == 0) {
                        if(geothesaurusSelectTopics.size() == 1){
                            if(topicId != null){
                                query.addField(new FieldQuery(true, false, "areaid", topicId));
                            }
                        }else{
                            if(topicId != null){
                                if(cq == null){
                                    cq = new ClauseQuery(true, false);
                                }
                                      cq.addField(new FieldQuery(true, false, "areaid", topicId));
                            }
                        }
                    } else {
                        if(cq != null) {
                            cq.setLastQueryUnrequired();
                            cq.addField(new FieldQuery(false, false, "areaid", topicId));
                        }
                    }
                }
            }
            if(cq != null){
                query.addClause(cq);
            }
        }
    }

    @SuppressWarnings("rawtypes")
    private static ArrayList<HashMap<String, String>> getSelectedGeothesaurusTopics(PortletRequest request, IngridResourceBundle messages){
        HashMap selectedGeothesaurus = (HashMap) getAttributeFromSession(request, SELECTED_GEOTHESAURUS);
        ArrayList<IngridHit> allGeoThesaurusTopics = (ArrayList<IngridHit>) getAttributeFromSession(request, GEOTHESAURUS_ALL_TOPICS);
        ArrayList<HashMap<String, String>> geothesaurusSelectTopics = new ArrayList<> ();

        if(selectedGeothesaurus != null){
            ArrayList<String> selectedIds = (ArrayList<String>) selectedGeothesaurus.get(GEOTHESAURUS_SELECTED_TOPICS_IDS);
            if(allGeoThesaurusTopics != null &&  selectedIds != null){
                for(int i = 0; i < selectedIds.size(); i++){
                    String selectedId = selectedIds.get(i);
                    for(int j = 0; j < allGeoThesaurusTopics.size(); j++){
                        Topic topic = (Topic) allGeoThesaurusTopics.get(j);
                        String topicId = (String) topic.get("topicID");
                        if(topic.getTopicNativeKey() != null){
                            topicId = topic.getTopicNativeKey();
                        }
                        if(topicId != null && topicId.equals(selectedId)){
                            HashMap<String, String> map = new HashMap<>();
                            String type = "";
                            if(topic.get("abstract") != null) {
                                String[] splitType = topic.get("abstract").toString().split("#");
                                if(splitType != null && splitType.length > 1) {
                                    type = ", " + messages.getString("sns.topic.ref." + splitType[1]);
                                }
                            }
                            map.put("topicTitle", topic.get("topicName") + "" + type);
                            map.put("topicId", selectedId);
                            geothesaurusSelectTopics.add(map);
                            break;
                        }
                    }
                }
            }
        }
        return geothesaurusSelectTopics;
    }

    private static void addToListOfTopicsGeoThesaurus(IngridHit ingridHit, PortletRequest request) {
        ArrayList<IngridHit> allGeoThesaurusTopics = (ArrayList<IngridHit>) getAttributeFromSession(request, GEOTHESAURUS_ALL_TOPICS);
        if(allGeoThesaurusTopics == null){
            allGeoThesaurusTopics = new ArrayList<>();
        }

        boolean isFound = false;
        for(int i=0; i < allGeoThesaurusTopics.size(); i++){
            IngridHit hit = allGeoThesaurusTopics.get(i);
            if(ingridHit.get("nativeKeyOcc") == hit.get("nativeKeyOcc")){
                isFound = true;
                break;
            }
        }
        if(!isFound){
            allGeoThesaurusTopics.add(ingridHit);
        }
        setAttributeToSession(request, GEOTHESAURUS_ALL_TOPICS, allGeoThesaurusTopics, false);
    }

    /***************************** Attribute ****************************************/

    private static void setFaceteParamsToSessionAttribute(ActionRequest request) {
        String doAddAttribute = request.getParameter("doAddAttribute");
        String doRemoveAttribute = request.getParameter("doRemoveAttribute");
        HashMap<String, String>  attribute = null;

        if(doAddAttribute != null){
            SearchExtResTopicAttributesForm f = (SearchExtResTopicAttributesForm) Utils.getActionForm(request,
                     SearchExtResTopicAttributesForm.SESSION_KEY, SearchExtResTopicAttributesForm.class);
            f.clearErrors();

            f.populate(request);
            if (!f.validate()) {
                return;
            }

            attribute = new HashMap<> ();
            if (f.hasInput(SearchExtResTopicAttributesForm.FIELD_DB_INSTITUTE)) {
                attribute.put(SearchExtResTopicAttributesForm.FIELD_DB_INSTITUTE, f.getInput(SearchExtResTopicAttributesForm.FIELD_DB_INSTITUTE));
            }
            if (f.hasInput(SearchExtResTopicAttributesForm.FIELD_DB_ORG)) {
                attribute.put(SearchExtResTopicAttributesForm.FIELD_DB_ORG, f.getInput(SearchExtResTopicAttributesForm.FIELD_DB_ORG));
            }
            if (f.hasInput(SearchExtResTopicAttributesForm.FIELD_DB_PM)) {
                attribute.put(SearchExtResTopicAttributesForm.FIELD_DB_PM, f.getInput(SearchExtResTopicAttributesForm.FIELD_DB_PM));
            }
            if (f.hasInput(SearchExtResTopicAttributesForm.FIELD_DB_STAFF)) {
                attribute.put(SearchExtResTopicAttributesForm.FIELD_DB_STAFF, f.getInput(SearchExtResTopicAttributesForm.FIELD_DB_STAFF));
            }
            if (f.hasInput(SearchExtResTopicAttributesForm.FIELD_DB_TITLE)) {
                attribute.put(SearchExtResTopicAttributesForm.FIELD_DB_TITLE, f.getInput(SearchExtResTopicAttributesForm.FIELD_DB_TITLE));
            }
            if (f.hasInput(SearchExtResTopicAttributesForm.FIELD_TERM_FROM)) {
                attribute.put(SearchExtResTopicAttributesForm.FIELD_TERM_FROM, f.getInput(SearchExtResTopicAttributesForm.FIELD_TERM_FROM));
            }
            if (f.hasInput(SearchExtResTopicAttributesForm.FIELD_TERM_TO)) {
                attribute.put(SearchExtResTopicAttributesForm.FIELD_TERM_TO, f.getInput(SearchExtResTopicAttributesForm.FIELD_TERM_TO));
            }
            f.clear();
        }

        if(doRemoveAttribute != null){
            if(doRemoveAttribute.equals("all")){
                removeAttributeFromSession(request, "doAddAttribute");
            }else {
                attribute = (HashMap<String, String>) getAttributeFromSession(request, "doAddAttribute");

                if (doRemoveAttribute.equals(SearchExtResTopicAttributesForm.FIELD_DB_INSTITUTE)) {
                    attribute.remove(SearchExtResTopicAttributesForm.FIELD_DB_INSTITUTE);
                }
                if (doRemoveAttribute.equals(SearchExtResTopicAttributesForm.FIELD_DB_ORG)) {
                    attribute.remove(SearchExtResTopicAttributesForm.FIELD_DB_ORG);
                }
                if (doRemoveAttribute.equals(SearchExtResTopicAttributesForm.FIELD_DB_PM)) {
                    attribute.remove(SearchExtResTopicAttributesForm.FIELD_DB_PM);
                }
                if (doRemoveAttribute.equals(SearchExtResTopicAttributesForm.FIELD_DB_STAFF)) {
                    attribute.remove(SearchExtResTopicAttributesForm.FIELD_DB_STAFF);
                }
                if (doRemoveAttribute.equals(SearchExtResTopicAttributesForm.FIELD_DB_TITLE)) {
                    attribute.remove(SearchExtResTopicAttributesForm.FIELD_DB_TITLE);
                }
                if (doRemoveAttribute.equals(SearchExtResTopicAttributesForm.FIELD_TERM_FROM)) {
                    attribute.remove(SearchExtResTopicAttributesForm.FIELD_TERM_FROM);
                }
                if (doRemoveAttribute.equals(SearchExtResTopicAttributesForm.FIELD_TERM_TO)) {
                    attribute.remove(SearchExtResTopicAttributesForm.FIELD_TERM_TO);
                }
            }
        }

        if(attribute != null){
            setAttributeToSession(request, "doAddAttributeInput", attribute, false);
            setAttributeToSession(request, "doAddAttribute", attribute, true);
        }
    }

    private static void setParamsToContextAttribute (RenderRequest request, Context context){
        HashMap<String, String>  attribute = (HashMap<String, String>) getAttributeFromSession(request, "doAddAttribute");
        if(attribute != null && !attribute .isEmpty()){
            setFacetSelectionState(context, request, "isAttributeSelect", true);
            context.put("doAddAttribute", getAttributeFromSession(request, "doAddAttribute"));
        }else{
            setFacetSelectionState(context, request, "isAttributeSelect", false);
        }
        context.put("doAddAttributeInput", getAttributeFromSession(request, "doAddAttributeInput"));
    }

    public static void addToQueryAttribute(PortletRequest request, IngridQuery query) {
        HashMap<String, String>  doAddAttribute = (HashMap<String, String>) getAttributeFromSession(request, "doAddAttribute");

        if (doAddAttribute != null && !doAddAttribute.isEmpty()){
            if(doAddAttribute.get(SearchExtResTopicAttributesForm.FIELD_DB_TITLE) != null)
                query.addField(new FieldQuery(false, false, "title", (String) doAddAttribute.get(SearchExtResTopicAttributesForm.FIELD_DB_TITLE)));
            if(doAddAttribute.get(SearchExtResTopicAttributesForm.FIELD_DB_INSTITUTE) != null)
                query.addField(new FieldQuery(false, false, "fs_institution", (String) doAddAttribute.get(SearchExtResTopicAttributesForm.FIELD_DB_INSTITUTE)));
            if(doAddAttribute.get(SearchExtResTopicAttributesForm.FIELD_DB_PM) != null)
                query.addField(new FieldQuery(false, false, "fs_projectleader", (String) doAddAttribute.get(SearchExtResTopicAttributesForm.FIELD_DB_PM)));
            if(doAddAttribute.get(SearchExtResTopicAttributesForm.FIELD_DB_STAFF) != null)
                query.addField(new FieldQuery(false, false, "fs_participants", (String) doAddAttribute.get(SearchExtResTopicAttributesForm.FIELD_DB_STAFF)));
            if(doAddAttribute.get(SearchExtResTopicAttributesForm.FIELD_DB_ORG) != null)
                query.addField(new FieldQuery(false, false, "fs_project_executing_organisation", (String) doAddAttribute.get(SearchExtResTopicAttributesForm.FIELD_DB_ORG)));
            if(doAddAttribute.get(SearchExtResTopicAttributesForm.FIELD_TERM_FROM) != null)
                query.addField(new FieldQuery(false, false, "fs_runtime_from", UtilsDate.convertDateString((String) doAddAttribute.get(SearchExtResTopicAttributesForm.FIELD_TERM_FROM), "dd.MM.yyyy", "yyyy-MM-dd")));
            if(doAddAttribute.get(SearchExtResTopicAttributesForm.FIELD_TERM_TO) != null)
                query.addField(new FieldQuery(false, false, "fs_runtime_to", UtilsDate.convertDateString((String) doAddAttribute.get(SearchExtResTopicAttributesForm.FIELD_TERM_TO), "dd.MM.yyyy", "yyyy-MM-dd")));
        }
    }


    /***************************** Raumbezug - Addressen ****************************************/

    private static void setFaceteParamsToSessionAreaAddress(ActionRequest request) {
        String doAddAreaAddress = request.getParameter("doAddAreaAddress");
        String doRemoveAreaAddress = request.getParameter("doRemoveAreaAddress");
        HashMap<String, String> areaAddress = null;

        if(doAddAreaAddress != null){
            SearchExtAdrPlaceReferenceForm f = (SearchExtAdrPlaceReferenceForm) Utils.getActionForm(request, SearchExtAdrPlaceReferenceForm.SESSION_KEY, SearchExtAdrPlaceReferenceForm.class);
            f.clearErrors();

            f.populate(request);
            if (!f.validate()) {
                return;
            }

            areaAddress = new HashMap<>();
            if (f.hasInput(SearchExtAdrPlaceReferenceForm.FIELD_STREET)) {
                areaAddress.put(SearchExtAdrPlaceReferenceForm.FIELD_STREET, f.getInput(SearchExtAdrPlaceReferenceForm.FIELD_STREET));
            }
            if (f.hasInput(SearchExtAdrPlaceReferenceForm.FIELD_ZIP)) {
                areaAddress.put(SearchExtAdrPlaceReferenceForm.FIELD_ZIP, f.getInput(SearchExtAdrPlaceReferenceForm.FIELD_ZIP));
            }
            if (f.hasInput(SearchExtAdrPlaceReferenceForm.FIELD_CITY)) {
                areaAddress.put(SearchExtAdrPlaceReferenceForm.FIELD_CITY, f.getInput(SearchExtAdrPlaceReferenceForm.FIELD_CITY));
            }
            f.clear();
        }

        if(doRemoveAreaAddress != null){
            if(doRemoveAreaAddress.equals("all")){
                removeAttributeFromSession(request, "doAddAreaAddress");
            }else {
                areaAddress = (HashMap<String, String>) getAttributeFromSession(request, "doAddAreaAddress");

                if (doRemoveAreaAddress.equals(SearchExtAdrPlaceReferenceForm.FIELD_STREET)) {
                    areaAddress.remove(SearchExtAdrPlaceReferenceForm.FIELD_STREET);
                }
                if (doRemoveAreaAddress.equals(SearchExtAdrPlaceReferenceForm.FIELD_ZIP)) {
                    areaAddress.remove(SearchExtAdrPlaceReferenceForm.FIELD_ZIP);
                }
                if (doRemoveAreaAddress.equals(SearchExtAdrPlaceReferenceForm.FIELD_CITY)) {
                    areaAddress.remove(SearchExtAdrPlaceReferenceForm.FIELD_CITY);
                }
            }
        }

        if(areaAddress != null){
            setAttributeToSession(request, "doAddAreaAddress", areaAddress, true);
            setAttributeToSession(request, "doAddAreaAddressInput", areaAddress, false);
        }
    }

    private static void setParamsToContextAreaAddress (RenderRequest request, Context context){
        HashMap<String, String> areaAddress = (HashMap<String, String>) getAttributeFromSession(request, "doAddAreaAddress");
        if(areaAddress != null && !areaAddress .isEmpty()){
            setFacetSelectionState(context, request, "isAreaAddressSelect", true);
            context.put("doAddAreaAddress", getAttributeFromSession(request, "doAddAreaAddress"));
        }else{
            setFacetSelectionState(context, request, "isAreaAddressSelect", false);
        }
        context.put("doAddAreaAddressInput", getAttributeFromSession(request, "doAddAreaAddressInput"));
    }

    public static void addToQueryAreaAddress(PortletRequest request, IngridQuery query) {
        HashMap<String, String> doAddAreaAddress = (HashMap<String, String>) getAttributeFromSession(request, "doAddAreaAddress");

        if (doAddAreaAddress != null && !doAddAreaAddress.isEmpty() && query != null){
            if(doAddAreaAddress.get(SearchExtAdrPlaceReferenceForm.FIELD_STREET) != null)
                query.addField(new FieldQuery(true, false, "street", (String) doAddAreaAddress.get(SearchExtAdrPlaceReferenceForm.FIELD_STREET)));
            if(doAddAreaAddress.get(SearchExtAdrPlaceReferenceForm.FIELD_ZIP) != null)
                query.addField(new FieldQuery(true, false, "zip", (String) doAddAreaAddress.get(SearchExtAdrPlaceReferenceForm.FIELD_ZIP)));
            if(doAddAreaAddress.get(SearchExtAdrPlaceReferenceForm.FIELD_CITY) != null)
                query.addField(new FieldQuery(true, false, "city", (String) doAddAreaAddress.get(SearchExtAdrPlaceReferenceForm.FIELD_CITY)));
        }
    }

    /***************************** Timeref ****************************************/

    private static void setFaceteParamsToSessionTimeref(ActionRequest request) {
        String doAddTimeref = request.getParameter("doAddTimeref");
        String doRemoveTimeref = request.getParameter("doRemoveTimeref");
        HashMap<String, String> timeref = null;

        if(doAddTimeref != null){
            SearchExtTimeReferenceForm f = (SearchExtTimeReferenceForm) Utils.getActionForm(request, SearchExtTimeReferenceForm.SESSION_KEY, SearchExtTimeReferenceForm.class);
            f.clearErrors();

            f.populate(request);
            if (!f.validate()) {
                return;
            }

            timeref = new HashMap<>();
            if (f.hasInput(SearchExtTimeReferenceForm.FIELD_FROM)) {
                timeref.put(SearchExtTimeReferenceForm.FIELD_FROM, f.getInput(SearchExtTimeReferenceForm.FIELD_FROM));
            }
            if (f.hasInput(SearchExtTimeReferenceForm.FIELD_TO)) {
                timeref.put(SearchExtTimeReferenceForm.FIELD_TO, f.getInput(SearchExtTimeReferenceForm.FIELD_TO));
            }
            f.clear();
        }

        if(doRemoveTimeref != null){
            if(doRemoveTimeref.equals("all")){
                removeAttributeFromSession(request, "doAddTimeref");
                removeAttributeFromSession(request, "doAddTimerefInput");
            }else {
                timeref = (HashMap<String, String>) getAttributeFromSession(request, "doAddTimeref");

                if (doRemoveTimeref.equals(SearchExtTimeReferenceForm.FIELD_FROM)) {
                    timeref.remove(SearchExtTimeReferenceForm.FIELD_FROM);
                }
                if (doRemoveTimeref.equals(SearchExtTimeReferenceForm.FIELD_TO)) {
                    timeref.remove(SearchExtTimeReferenceForm.FIELD_TO);
                }
            }
        }

        if(timeref != null){
            setAttributeToSession(request, "doAddTimeref", timeref, true);
            setAttributeToSession(request, "doAddTimerefInput", timeref, false);
        }
    }

    private static void setParamsToContextTimeref (RenderRequest request, Context context){
        HashMap<String, String> timeref = (HashMap<String, String>) getAttributeFromSession(request, "doAddTimeref");
        if(timeref != null && !timeref .isEmpty()){
            setFacetSelectionState(context, request, "isTimerefSelect", true);
            context.put("doAddTimeref", getAttributeFromSession(request, "doAddTimeref"));
        }else{
            setFacetSelectionState(context, request, "isTimerefSelect", false);
        }
        context.put("doAddTimerefInput", getAttributeFromSession(request, "doAddTimerefInput"));
    }

    public static void addToQueryTimeref(PortletRequest request, IngridQuery query) throws ParseException {
        HashMap<String, String> doAddTimeref = (HashMap<String, String>) getAttributeFromSession(request, "doAddTimeref");

        if (doAddTimeref != null && !doAddTimeref.isEmpty() && query != null){
            String from = null;
            String to = null;
            
            if(doAddTimeref.get(SearchExtTimeReferenceForm.FIELD_FROM) != null)
                from = (String) doAddTimeref.get(SearchExtTimeReferenceForm.FIELD_FROM).replace("-", "") + "0*";
            if(doAddTimeref.get(SearchExtTimeReferenceForm.FIELD_TO) != null)
                to = (String) doAddTimeref.get(SearchExtTimeReferenceForm.FIELD_TO).replace("-", "") + "9*";
            ClauseQuery cq = null;
            if(from != null && to != null) {
                cq = new ClauseQuery(true, false);
                IngridQuery q = QueryStringParser.parse("t01_object.time_from:[" + from + " TO " + to + "] OR t01_object.time_to:[" + from + "TO " + to +"]");
                RangeQuery[] rq = q.getRangeQueries();
                for (RangeQuery rangeQuery : rq) {
                    cq.addRangeQuery(rangeQuery);
                }
            }else if(from != null) {
                cq = new ClauseQuery(true, false);
                IngridQuery q = QueryStringParser.parse("t01_object.time_from:[" + from + " TO 9*] OR t01_object.time_to:[" + from + " TO 9*]");
                RangeQuery[] rq = q.getRangeQueries();
                for (RangeQuery rangeQuery : rq) {
                    cq.addRangeQuery(rangeQuery);
                }
            }else if(to != null) {
                cq = new ClauseQuery(true, false);
                IngridQuery q = QueryStringParser.parse("t01_object.time_from:[0* TO " + to + "] OR t01_object.time_to:[0* TO " + to + "]");
                RangeQuery[] rq = q.getRangeQueries();
                for (RangeQuery rangeQuery : rq) {
                    cq.addRangeQuery(rangeQuery);
                }
            }
            if(cq != null) {
                query.addClause(cq);
            }
        }
    }


    /***************************** Funktionen ****************************************/

    private static void removeAttributeFromSession(PortletRequest request, String key){
        request.getPortletSession().removeAttribute(key, PortletSession.APPLICATION_SCOPE);
    }

    public static void setAttributeToSession(PortletRequest request, String key, Object value){
        setAttributeToSession(request, key, value, false);
    }

    @SuppressWarnings("rawtypes")
    private static void setAttributeToSession(PortletRequest request, String key, Object value, boolean isSelection){
        Set<String> faceteSessionKeys =
            (Set<String>) getAttributeFromSession(request, "faceteSessionKeys");

        if(faceteSessionKeys == null){
            faceteSessionKeys = new HashSet<>();
            request.getPortletSession().setAttribute("faceteSessionKeys", faceteSessionKeys, PortletSession.APPLICATION_SCOPE);
        }

        // remember all our set keys
        faceteSessionKeys.add(key);
        // and set in session
        request.getPortletSession().setAttribute(key, value, PortletSession.APPLICATION_SCOPE);

        // remember last facet selection in special way
        if(isSelection){
            ArrayList<HashMap> faceteLastSelection =
                (ArrayList<HashMap>) getAttributeFromSession(request, "faceteLastSelection");
            if(faceteLastSelection == null){
                faceteLastSelection = new ArrayList<>();
                setAttributeToSession(request, "faceteLastSelection", faceteLastSelection);
            }

            HashMap lastSelection = new HashMap();
            lastSelection.put(key, value);
            faceteLastSelection.add(lastSelection);
            // Set selection flag of facet to session
            setAttributeToSession(request, "isSelection", true);
        }
    }

    public static Object getAttributeFromSession(PortletRequest request, String key){
        return request.getPortletSession().getAttribute(key, PortletSession.APPLICATION_SCOPE);
    }

    private static void general(ActionRequest request) {

        String doRemoveAll = request.getParameter("doRemoveAll");
        String doRemoveLast = request.getParameter("doRemoveLast");
        if(doRemoveAll != null){
            removeAllFaceteSelections(request);
            removeFaceteElementsFromSession(request);
        }
        if(doRemoveLast != null){
            removeLastFaceteSelection(request);
        }
    }

    private static void removeAllFaceteSelections(PortletRequest request){
        Set<String> faceteSessionKeys =
            (Set<String>) getAttributeFromSession(request, "faceteSessionKeys");
        if (faceteSessionKeys != null) {
            for (String sessionKey : faceteSessionKeys) {
                removeAttributeFromSession(request, sessionKey);
            }
        }
        removeAttributeFromSession(request, "faceteSessionKeys");
        removeFaceteElementsFromSession(request);
    }

    @SuppressWarnings("rawtypes")
    private static void removeLastFaceteSelection(ActionRequest request) {
        ArrayList<HashMap>  faceteLastSelection =
            (ArrayList<HashMap>) getAttributeFromSession(request, "faceteLastSelection");
        if(faceteLastSelection != null){
            HashMap lastSelection = faceteLastSelection.get(faceteLastSelection.size() - 1);
            if(lastSelection != null){
                for (Iterator<String> iterator = lastSelection.keySet().iterator(); iterator.hasNext();) {
                    String key = iterator.next();
                    removeAttributeFromSession(request, key);
                    if(key.equals("lastConfigSelection")){
                        HashMap<String, String> facets = (HashMap<String, String>) lastSelection.get(key);
                        if(facets!= null){
                            ArrayList<IngridFacet> config = (ArrayList<IngridFacet>) getAttributeFromSession(request, FACET_CONFIG);
                            if(config != null){
                                for (Iterator<String> facetsIterator = facets.keySet().iterator(); facetsIterator.hasNext();) {
                                    String facetsKey = facetsIterator.next();
                                    String[] split = facetsKey.split(":");
                                    IngridFacet tmpFacetKey = getFacetById(config, split[0]);
                                    if(tmpFacetKey != null){
                                        IngridFacet tmpFacetValue = getFacetById(tmpFacetKey.getFacets(), split[1]);
                                        if(tmpFacetValue != null && tmpFacetValue.isSelect()){
                                            tmpFacetValue.setSelect(false);
                                        }
                                    }
                                }
                                setAttributeToSession(request, FACET_CONFIG, config);
                            }
                        }
                    }
                }
            }
            faceteLastSelection.remove(faceteLastSelection.size() - 1);
        }
    }

    private static void removeFaceteElementsFromSession(PortletRequest request){

        removeAttributeFromSession(request, ELEMENTS_GEOTHESAURUS);
        removeAttributeFromSession(request, ELEMENTS_MAP);
    }

    private static ArrayList<HashMap<String, Long>> sortHashMapAsArrayList(HashMap<String, Long> input){
        return sortHashMapAsArrayList(input, null);
    }

    @SuppressWarnings("rawtypes")
    private static ArrayList<HashMap<String, Long>> sortHashMapAsArrayList(HashMap<String, Long> input, String[] sortedRanking){
        List<String> keys = new ArrayList<>(input.keySet());
        final Map<String, Long> tmpInput = input;
        ArrayList<HashMap<String, Long>> sortedInput = new ArrayList<>();

        if(sortedRanking != null && sortedRanking.length > 0){
            if(sortedRanking[0].length() > 0){
                for(int i=0; i < sortedRanking.length; i++){
                    String key = sortedRanking[i];
                    Long value = input.get(key);
                    if(key != null && value != null){
                        HashMap<String, Long> map = new HashMap<>();
                        map.put(key, input.get(key));
                        sortedInput.add(map);
                    }

                }
            }else{
                Collections.sort(keys,new Comparator(){
                    public int compare(Object left, Object right){
                        String leftKey = (String)left;
                        String rightKey = (String)right;

                        Long leftValue = tmpInput.get(leftKey);
                        Long rightValue = tmpInput.get(rightKey);
                        return leftValue.compareTo(rightValue) * -1;
                    }
                });

                for(Iterator<String> i=keys.iterator(); i.hasNext();){
                    String k = i.next();
                    HashMap<String, Long> map = new HashMap<>();
                       map.put(k, tmpInput.get(k));
                    sortedInput.add(map);
                }
            }
        }else{
            Collections.sort(keys,new Comparator(){
                public int compare(Object left, Object right){
                    String leftKey = (String)left;
                    String rightKey = (String)right;

                    Long leftValue = tmpInput.get(leftKey);
                    Long rightValue = tmpInput.get(rightKey);
                    return leftValue.compareTo(rightValue) * -1;
                }
            });

            for(Iterator<String> i=keys.iterator(); i.hasNext();){
                String k = i.next();
                HashMap<String, Long> map = new HashMap<>();
                   map.put(k, tmpInput.get(k));
                sortedInput.add(map);
            }
        }

        return sortedInput;
    }

    private static void setFacetSelectionState(Context context, PortletRequest request, String key, boolean value) {
        context.put(key, value);
        HashMap<String, Boolean> facetSelectionState =
            (HashMap<String, Boolean>) getAttributeFromSession(request, "facetSelectionState");
        if(facetSelectionState == null){
            facetSelectionState = new HashMap<>();
            setAttributeToSession(request, "facetSelectionState", facetSelectionState);
        }
        facetSelectionState.put(key, value);
    }

    private static HashMap<String, Boolean> getFacetSelectionState(PortletRequest request){
        return (HashMap<String, Boolean>) getAttributeFromSession(request, "facetSelectionState");
    }

    private static boolean isFacetSelection(PortletRequest request){
        HashMap<String, Boolean> facetSelectionState = getFacetSelectionState(request);
        if(facetSelectionState != null){
            for (Iterator<String> iterator = facetSelectionState.keySet().iterator(); iterator.hasNext();) {
                String key = iterator.next();
                if(facetSelectionState.get(key)){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Create facet session by URL parameters
     *
     * @param request
     * @param config 
     */
    @SuppressWarnings("rawtypes")
    private static void getFacetAttributsParamsFromUrl(PortletRequest request, ArrayList<IngridFacet> config, IngridResourceBundle messages){

        String paramsFacet = request.getParameter("f");

        if(paramsFacet != null){
            // Timref
            String paramsTimerefFrom = getFacetParam(paramsFacet, PARAMS_TIMEREF_FROM);
            String paramsTimrefTo = getFacetParam(paramsFacet, PARAMS_TIMEREF_TO);
            HashMap<String, String> timeref = null;

            if(paramsTimerefFrom != null || paramsTimrefTo != null){
                timeref = new HashMap<>();
                if(paramsTimerefFrom != null){
                    timeref.put(SearchExtTimeReferenceForm.FIELD_FROM, paramsTimerefFrom);
                }
                if(paramsTimrefTo != null){
                    timeref.put(SearchExtTimeReferenceForm.FIELD_TO, paramsTimrefTo);
                }
                setAttributeToSession(request, "doAddTimeref", timeref);
                setAttributeToSession(request, "doAddTimerefInput", timeref);
            }

            // Geothesaurus
            ArrayList<String> paramsGeothesaurus = getFacetParamsList(paramsFacet, PARAMS_GEOTHESAURUS);
            if(paramsGeothesaurus != null && !paramsGeothesaurus.isEmpty()){
                HashMap geothesaurusHashMap = new HashMap();
                ArrayList<String> selectedIds = null;
                ArrayList<HashMap<String, String>> geothesaurusSelectTopics = null;
                for(int i=0; i<paramsGeothesaurus.size(); i++){
                    if(selectedIds == null){
                        selectedIds = new ArrayList<>();
                    }
                    if(geothesaurusSelectTopics == null){
                        geothesaurusSelectTopics = new ArrayList<>();
                    }
                    String geothesaurus = paramsGeothesaurus.get(i);
                    String topicId = geothesaurus.split(",")[0];
                    String topicName = geothesaurus.split(",")[1];
                    selectedIds.add(topicId);

                    IngridHit[] topics = SNSSimilarTermsInterfaceImpl.getInstance().getTopicsFromText(topicName, "/location", request.getLocale());
                    for(int j=0; j<topics.length; j++){
                        Topic topic = (Topic) topics[j];
                        addToListOfTopicsGeoThesaurus((IngridHit)topics[j], request);
                        if(topicId.equals(topic.getTopicNativeKey())){
                            HashMap<String, String> addedTopic = new HashMap<>();
                            String type = "";
                            if(topic.get("abstract") != null) {
                                String[] splitType = topic.get("abstract").toString().split("#");
                                if(splitType != null && splitType.length > 1) {
                                    type = ", " + messages.getString("sns.topic.ref." + splitType[1]);
                                }
                            }
                            addedTopic.put("topicTitle", topic.getTopicName() + "" + type);
                            addedTopic.put("topicId", topic.getTopicNativeKey());
                            geothesaurusSelectTopics.add(addedTopic);
                        }
                    }

                }
                geothesaurusHashMap.put(GEOTHESAURUS_SELECTED_TOPICS, geothesaurusSelectTopics);
                geothesaurusHashMap.put(GEOTHESAURUS_SELECTED_TOPICS_IDS, selectedIds);
                setAttributeToSession(request, SELECTED_GEOTHESAURUS, geothesaurusHashMap);
            }

            // AreaAddress
            String paramsAreaAddressStreet = getFacetParam(paramsFacet, PARAMS_AREA_ADDRESS_STREET);
            String paramsAreaAddressZip = getFacetParam(paramsFacet, PARAMS_AREA_ADDRESS_ZIP);
            String paramsAreaAddressCity = getFacetParam(paramsFacet, PARAMS_AREA_ADDRESS_CITY);
            HashMap<String, String> areaAddress = null;

            if(paramsAreaAddressStreet != null || paramsAreaAddressZip != null || paramsAreaAddressCity != null){
                areaAddress = new HashMap<>();
                if(paramsAreaAddressStreet != null){
                    areaAddress.put(SearchExtAdrPlaceReferenceForm.FIELD_STREET, paramsAreaAddressStreet);
                }
                if(paramsAreaAddressZip != null){
                    areaAddress.put(SearchExtAdrPlaceReferenceForm.FIELD_ZIP, paramsAreaAddressZip);
                }
                if(paramsAreaAddressCity != null){
                    areaAddress.put(SearchExtAdrPlaceReferenceForm.FIELD_CITY, paramsAreaAddressCity);
                }
                setAttributeToSession(request, "doAddAreaAddress", areaAddress);
                setAttributeToSession(request, "doAddAreaAddressInput", timeref);
            }

            // Attribute
            String paramsAttributeInstitute = getFacetParam(paramsFacet, PARAMS_ATTRIBUTE_INSTITUTE);
            String paramsAttributeOrg = getFacetParam(paramsFacet, PARAMS_ATTRIBUTE_ORG);
            String paramsAttributePm = getFacetParam(paramsFacet, PARAMS_ATTRIBUTE_PM);
            String paramsAttributeStaff = getFacetParam(paramsFacet, PARAMS_ATTRIBUTE_STAFF);
            String paramsAttributeTitle = getFacetParam(paramsFacet, PARAMS_ATTRIBUTE_TITLE);
            String paramsAttributeTermForm = getFacetParam(paramsFacet, PARAMS_ATTRIBUTE_TERM_FROM);
            String paramsAttributeTermTo = getFacetParam(paramsFacet, PARAMS_ATTRIBUTE_TERM_TO);
            HashMap<String, String> attribute = null;

            if(paramsAttributeInstitute != null || paramsAttributeOrg != null || paramsAttributePm != null
                    || paramsAttributeStaff != null || paramsAttributeTitle != null || paramsAttributeTermForm != null
                    || paramsAttributeTermTo != null){
                attribute = new HashMap<>();
                if(paramsAreaAddressStreet != null){
                    attribute.put(SearchExtResTopicAttributesForm.FIELD_DB_INSTITUTE, paramsAttributeInstitute);
                }
                if(paramsAreaAddressZip != null){
                    attribute.put(SearchExtResTopicAttributesForm.FIELD_DB_ORG, paramsAttributeOrg);
                }
                if(paramsAreaAddressCity != null){
                    attribute.put(SearchExtResTopicAttributesForm.FIELD_DB_PM, paramsAttributePm);
                }
                if(paramsAreaAddressStreet != null){
                    attribute.put(SearchExtResTopicAttributesForm.FIELD_DB_STAFF, paramsAttributeStaff);
                }
                if(paramsAreaAddressZip != null){
                    attribute.put(SearchExtResTopicAttributesForm.FIELD_DB_TITLE, paramsAttributeTitle);
                }
                if(paramsAreaAddressCity != null){
                    attribute.put(SearchExtResTopicAttributesForm.FIELD_TERM_FROM, paramsAttributeTermForm);
                }
                if(paramsAreaAddressCity != null){
                    attribute.put(SearchExtResTopicAttributesForm.FIELD_TERM_TO, paramsAttributeTermTo);
                }
                setAttributeToSession(request, "doAddAttribute", attribute);
            }

            // Map
            ArrayList<String> paramsOptions = getFacetParamsList(paramsFacet, PARAMS_MAP_OPTIONS);
            String paramsMapX1 = getFacetParam(paramsFacet, PARAMS_MAP_X1);
            String paramsMapX2 = getFacetParam(paramsFacet, PARAMS_MAP_X2);
            String paramsMapY1 = getFacetParam(paramsFacet, PARAMS_MAP_Y1);
            String paramsMapY2 = getFacetParam(paramsFacet, PARAMS_MAP_Y2);

            HashMap<String,String> coords = null;

            if(paramsMapX1 != null && paramsMapX2 != null && paramsMapY1 != null && paramsMapY2 != null){
                coords = new HashMap<>();

                coords.put("x1", paramsMapX1);
                coords.put("y1", paramsMapY1);
                coords.put("x2", paramsMapX2);
                coords.put("y2", paramsMapY2);
            }

            if(paramsOptions != null && coords != null){
                HashMap<String, String> selectedMap = new HashMap<>();
                for(int i=0; i < paramsOptions.size(); i++){
                    String value = "";
                    if(getFacetParam(paramsFacet, "x1") != null){
                        value = coords.get("x1").concat(", ");
                    }
                    if(getFacetParam(paramsFacet, "y1") != null){
                        value = value.concat(coords.get("y1")).concat(", ");
                    }
                    if(getFacetParam(paramsFacet, "x2") != null){
                        value = value.concat(coords.get("x2")).concat(", ");
                    }
                    if(getFacetParam(paramsFacet, "y2") != null){
                        value = value.concat(coords.get("y2"));
                    }
                    selectedMap.put(paramsOptions.get(i), value);
                }
                HashMap mapHashMap = new HashMap();
                mapHashMap.put("webmapclientCoords", coords);
                mapHashMap.put("coordOptions", paramsOptions);
                mapHashMap.put("doMapCoords", selectedMap);
                setAttributeToSession(request, SELECTED_MAP, mapHashMap);
            }

            // Wildcard
            ArrayList<String> paramsWildcard = getFacetParamsList(paramsFacet, PARAMS_WILDCARD);
            if(paramsWildcard != null && !paramsWildcard.isEmpty()){
                ArrayList<String> wildcardIds = new ArrayList<>();
                for (String paramWildcard : paramsWildcard) {
                    String[] splitParam = paramWildcard.split( "," );
                    if(splitParam.length == 3){
                        HashMap<String, String> map = new HashMap<>();
                        String id = splitParam[0];
                        String wildcard = splitParam[1];
                        String term = splitParam[2];

                        map.put( "wildcard", wildcard );
                        map.put( "term", term );
                        setAttributeToSession(request, id, map);
                        wildcardIds.add( id );
                    }
                }
                if(!wildcardIds.isEmpty()){
                    setAttributeToSession(request, WILDCARD_IDS, wildcardIds);
                }
            }

            // Config
            if(config != null){
                String[] paramsSplits = paramsFacet.split(";");
                resetFacetConfigSelect(config, true);
                for(String paramsSplit : paramsSplits){
                    String[] split = paramsSplit.split(":");
                    String split0 = split[0];
                    IngridFacet tmpFacetKey;
                    if (split0.equals("wildcard")) {
                        if (split.length > 1) {
                            tmpFacetKey = getFacetById(config, split[1].split(",")[0]);
                            if(tmpFacetKey != null && tmpFacetKey.getParent() != null) {
                                tmpFacetKey.getParent().setOpen(true);
                            }
                        }
                    } else {
                        tmpFacetKey = getFacetById(config, split[0]);
                        //Set facet parent isSelect
                        if(tmpFacetKey != null) {
                            if(tmpFacetKey.getFacets() != null && tmpFacetKey.getField() == null){
                                //Set facet isSelect
                                IngridFacet tmpFacetValue = getFacetById(tmpFacetKey.getFacets(), split[1]);
                                if(tmpFacetValue != null) {
                                    if(tmpFacetValue.isHierarchyTreeLeaf() &&
                                        tmpFacetValue.getFacets().isEmpty() &&
                                        tmpFacetValue.getHierarchyTreeNode() != null) {
                                        DisplayTreeNode node = tmpFacetValue.getHierarchyTreeNode();
                                        if(node != null) {
                                            openNode(node, node.getId());
                                            addHierarchyNodesToFacets(tmpFacetValue, node.getChildren(), config);
                                        }
                                    }
                                    tmpFacetValue.setSelect(true);
                                    //Check dependency
                                    if(tmpFacetValue.getId() != null){
                                        ArrayList<IngridFacet> facetDepList = new ArrayList<>();
                                        getDependencyFacetById(config, facetDepList, tmpFacetValue.getId());
                                        for(IngridFacet facetDep : facetDepList){
                                            IngridFacet dependencyValue  = getFacetById(config, facetDep.getDependency());
                                            if(dependencyValue != null) {
                                                if(dependencyValue.isSelect()){
                                                    facetDep.setDependencySelect(dependencyValue.isSelect());
                                                }else {
                                                    if (facetDep.getFacets() != null) {
                                                        for (IngridFacet facetChild : facetDep.getFacets()) {
                                                            facetChild.setSelect(dependencyValue.isSelect());
                                                        }
                                                    }
                                                    facetDep.setDependencySelect(dependencyValue.isSelect());
                                                }
                                            }
                                        }
                                    }
                                    if(tmpFacetValue.isSelect() && tmpFacetValue.getSelectFacet() != null) {
                                        IngridFacet tmpSelectFacet = getFacetById(config, tmpFacetValue.getSelectFacet());
                                        if(tmpSelectFacet != null) {
                                            tmpSelectFacet.setSelect(true);
                                        }
                                        ArrayList<IngridFacet> facetDepList = new ArrayList<>();
                                        getDependencyFacetById(config, facetDepList, tmpSelectFacet.getId());
                                        for(IngridFacet facetDep : facetDepList){
                                            IngridFacet dependencyValue  = getFacetById(config, facetDep.getDependency());
                                            if(dependencyValue != null) {
                                                if(dependencyValue.isSelect()){
                                                    facetDep.setDependencySelect(dependencyValue.isSelect());
                                                }else {
                                                    if (facetDep.getFacets() != null) {
                                                        for (IngridFacet facetChild : facetDep.getFacets()) {
                                                            facetChild.setSelect(dependencyValue.isSelect());
                                                        }
                                                    }
                                                    facetDep.setDependencySelect(dependencyValue.isSelect());
                                                }
                                            }
                                        }
                                    } 
                                }
                                for (IngridFacet tmpSubFacet : tmpFacetKey.getFacets()) {
                                    if(tmpSubFacet.getToggle() != null) {
                                        IngridFacet toggle = tmpSubFacet.getToggle();
                                        if(toggle.getId().equals(split[1])) {
                                            toggle.setSelect(true);
                                        }
                                    }
                                }
                            } else if(tmpFacetKey.getField() != null) {
                                if(tmpFacetKey.getFacets() == null) {
                                    tmpFacetKey.setFacets(new ArrayList<IngridFacet>());
                                }
                                List<IngridFacet> newFieldFacets = tmpFacetKey.getFacets();
                                boolean isNewFacet = false;
                                String newFieldId = split[split.length - 1];
                                IngridFacet newFieldFacet = getFacetById(newFieldFacets, newFieldId);
                                if(newFieldFacet == null) {
                                    newFieldFacet= new IngridFacet();
                                    isNewFacet = true;
                                }
                                newFieldFacet.setId(newFieldId);
                                if(tmpFacetKey.getCodelist() != null) {
                                    CodeListService codelistService = CodeListServiceFactory.instance();
                                    CodeList codelist = codelistService.getCodeList(tmpFacetKey.getCodelist());
                                    List<CodeListEntry> codelistEntries = codelist.getEntries();
                                    String facetCodelistEntryId = newFieldId.replace(tmpFacetKey.getCodelist() + "_", "");
                                    for (CodeListEntry codeListEntry : codelistEntries) {
                                        if(codeListEntry.getId().equals(facetCodelistEntryId)) {
                                            newFieldFacet.setName(codeListEntry.getField(tmpFacetKey.getCodelistField()));
                                            break;
                                        }
                                    }
                                } else {
                                    newFieldFacet.setName(newFieldId);
                                }
                                String query = tmpFacetKey.getField().concat(":\"").concat(newFieldFacet.getName()).concat("\"");
                                newFieldFacet.setQuery(query);
                                newFieldFacet.setParent(tmpFacetKey);
                                newFieldFacet.setSelect(true);
                                if(isNewFacet) {
                                    newFieldFacets.add(newFieldFacet);
                                }
                                tmpFacetKey.setFacets(newFieldFacets);
                            }

                        }
                    }
                }
                setAttributeToSession(request, FACET_CONFIG, config);
            }
        }
    }

    /**
     * Get facet parameters list by key
     *
     * @param facetParams
     * @param facetKey
     * @return list of facet parameters.
     */
    private static ArrayList<String> getFacetParamsList(String facetParams, String facetKey) {
        String[] params = facetParams.split(";");
        ArrayList<String> list = new ArrayList<>();
        for(int i=0; i<params.length; i++){
            String[] param = params[i].split(":");
            if(param != null && param.length == 2){
                String key = param[0];
                String value = param[1];

                if(key != null && value != null && key.equals(facetKey)){
                    list.add(value);
                }
            }
        }

        return list;
    }

    /**
     * Get facet parameter by key
     *
     * @param facetParams
     * @param facetKey
     * @return facet parameter.
     */
    private static String getFacetParam(String facetParams, String facetKey) {
        String[] params = facetParams.split(";");
        for(int i=0; i<params.length; i++){
            String[] param = params[i].split(":");
            if(param != null && param.length == 2){
                String key = param[0];
                String value = param[1];
                if(key != null && value != null && key.equals(facetKey)){
                    return value;
                }
            }
        }
        return null;
    }

    /**
     * Create URL by facet session
     *
     * @param request
     * @param facetUrl
     * @param config
     */
    @SuppressWarnings("rawtypes")
    public static void setFacetUrlParamsToUrl(PortletRequest request, StringBuilder facetUrl) {

        // Timeref
        if(getAttributeFromSession(request, "doAddTimeref") != null){
            HashMap<String, String> timeref = (HashMap<String, String>) getAttributeFromSession(request, "doAddTimeref");
            if (timeref.get(SearchExtTimeReferenceForm.FIELD_FROM) != null) {
                appendURLParameterFacet(facetUrl, toURLParamFacet(PARAMS_TIMEREF_FROM, timeref.get(SearchExtTimeReferenceForm.FIELD_FROM)));
            }
            if (timeref.get(SearchExtTimeReferenceForm.FIELD_TO) != null) {
                appendURLParameterFacet(facetUrl, toURLParamFacet(PARAMS_TIMEREF_TO, timeref.get(SearchExtTimeReferenceForm.FIELD_TO)));
            }
        }

        // Geothesaurus
        if(getAttributeFromSession(request, SELECTED_GEOTHESAURUS) != null){
            HashMap geothesaurusHashMap = (HashMap) getAttributeFromSession(request, SELECTED_GEOTHESAURUS);
            if(geothesaurusHashMap.get(GEOTHESAURUS_SELECTED_TOPICS_IDS) != null){
                ArrayList<String> selectedTopicIds = (ArrayList<String>) geothesaurusHashMap.get(GEOTHESAURUS_SELECTED_TOPICS_IDS);
                if(selectedTopicIds != null){
                    for(int i=0; i<selectedTopicIds.size(); i++){
                        String selectedTopicId = selectedTopicIds.get(i);
                        ArrayList<IngridHit> topics = (ArrayList<IngridHit>) getAttributeFromSession(request, GEOTHESAURUS_ALL_TOPICS);
                        if(topics != null){
                            for(int j=0; j<topics.size();j++){
                                Topic topic = (Topic) topics.get(j);
                                String topicId = topic.getTopicNativeKey();
                                String topicName = topic.getTopicName();

                                if(topicId == null){
                                    topicId = topic.getTopicID();
                                }
                                if(selectedTopicId.equals(topicId)){
                                    appendURLParameterFacet(facetUrl, toURLParamFacet(PARAMS_GEOTHESAURUS, topicId +","+topicName));
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        // Map
        if(getAttributeFromSession(request, SELECTED_MAP) != null){
            HashMap mapHashMap = (HashMap) getAttributeFromSession(request, SELECTED_MAP);
            ArrayList<String> options = (ArrayList<String>) mapHashMap.get("coordOptions");
            HashMap<String,String> coords = (HashMap<String,String>) mapHashMap.get("webmapclientCoords");
            if(coords != null){
                appendURLParameterFacet(facetUrl, toURLParamFacet(PARAMS_MAP_X1, coords.get("x1")));
                appendURLParameterFacet(facetUrl, toURLParamFacet(PARAMS_MAP_Y1, coords.get("y1")));
                appendURLParameterFacet(facetUrl, toURLParamFacet(PARAMS_MAP_X2, coords.get("x2")));
                appendURLParameterFacet(facetUrl, toURLParamFacet(PARAMS_MAP_Y2, coords.get("y2")));
            }

            if(options != null){
                addUrlParameterForFacet(facetUrl, PARAMS_MAP_OPTIONS, (ArrayList<String>) options);
            }
        }

        // AreaAddress
        if(getAttributeFromSession(request, "doAddAreaAddress") != null){
            HashMap<String, String> areaAddress = (HashMap<String, String>) getAttributeFromSession(request, "doAddAreaAddress");
            if (areaAddress.get(SearchExtAdrPlaceReferenceForm.FIELD_STREET) != null) {
                appendURLParameterFacet(facetUrl, toURLParamFacet(PARAMS_AREA_ADDRESS_STREET, areaAddress.get(SearchExtAdrPlaceReferenceForm.FIELD_STREET)));
            }
            if (areaAddress.get(SearchExtAdrPlaceReferenceForm.FIELD_ZIP) != null) {
                appendURLParameterFacet(facetUrl, toURLParamFacet(PARAMS_AREA_ADDRESS_ZIP, areaAddress.get(SearchExtAdrPlaceReferenceForm.FIELD_ZIP)));
            }
            if (areaAddress.get(SearchExtAdrPlaceReferenceForm.FIELD_CITY) != null) {
                appendURLParameterFacet(facetUrl, toURLParamFacet(PARAMS_AREA_ADDRESS_CITY, areaAddress.get(SearchExtAdrPlaceReferenceForm.FIELD_CITY)));
            }
        }

        // Attribute
        if(getAttributeFromSession(request, "doAddAttribute") != null){
            HashMap<String, String> attribute = (HashMap<String, String>) getAttributeFromSession(request, "doAddAttribute");

            if (attribute.get(SearchExtResTopicAttributesForm.FIELD_DB_INSTITUTE) != null) {
                appendURLParameterFacet(facetUrl, toURLParamFacet(PARAMS_ATTRIBUTE_INSTITUTE, attribute.get(SearchExtResTopicAttributesForm.FIELD_DB_INSTITUTE)));
            }
            if (attribute.get(SearchExtResTopicAttributesForm.FIELD_DB_ORG) != null) {
                appendURLParameterFacet(facetUrl, toURLParamFacet(PARAMS_ATTRIBUTE_ORG, attribute.get(SearchExtResTopicAttributesForm.FIELD_DB_ORG)));
            }
            if (attribute.get(SearchExtResTopicAttributesForm.FIELD_DB_PM) != null) {
                appendURLParameterFacet(facetUrl, toURLParamFacet(PARAMS_ATTRIBUTE_PM, attribute.get(SearchExtResTopicAttributesForm.FIELD_DB_PM)));
            }
            if (attribute.get(SearchExtResTopicAttributesForm.FIELD_DB_STAFF) != null) {
                appendURLParameterFacet(facetUrl, toURLParamFacet(PARAMS_ATTRIBUTE_STAFF, attribute.get(SearchExtResTopicAttributesForm.FIELD_DB_STAFF)));
            }
            if (attribute.get(SearchExtResTopicAttributesForm.FIELD_DB_TITLE) != null) {
                appendURLParameterFacet(facetUrl, toURLParamFacet(PARAMS_ATTRIBUTE_TITLE, attribute.get(SearchExtResTopicAttributesForm.FIELD_DB_TITLE)));
            }
            if (attribute.get(SearchExtResTopicAttributesForm.FIELD_TERM_FROM) != null) {
                appendURLParameterFacet(facetUrl, toURLParamFacet(PARAMS_ATTRIBUTE_TERM_FROM, attribute.get(SearchExtResTopicAttributesForm.FIELD_TERM_FROM)));
            }
            if (attribute.get(SearchExtResTopicAttributesForm.FIELD_TERM_TO) != null) {
                appendURLParameterFacet(facetUrl, toURLParamFacet(PARAMS_ATTRIBUTE_TERM_TO, attribute.get(SearchExtResTopicAttributesForm.FIELD_TERM_TO)));
            }
        }

        // Wildcard
        if(getAttributeFromSession(request, WILDCARD_IDS) != null){
            ArrayList<String> wildcardIds = (ArrayList<String>) getAttributeFromSession(request, WILDCARD_IDS);
            if(wildcardIds != null && !wildcardIds.isEmpty()){
                for (String wildcardId : wildcardIds) {
                    HashMap<String, String> map = (HashMap<String, String>) getAttributeFromSession( request, wildcardId );
                    if(map != null){
                        String wildcard = map.get( "wildcard" );
                        String term = map.get( "term" );
                        String param = "";
                        if(wildcardId != null){
                            param = param.concat( wildcardId );
                        }
                        if(wildcard != null){
                            param = param.concat( "," );
                            param = param.concat( wildcard );
                        }
                        if(term != null){
                            param = param.concat( "," );
                            param = param.concat( term );
                        }
                        appendURLParameterFacet(facetUrl, toURLParamFacet("wildcard", param));
                    }
                }
            }

        }
        ArrayList<IngridFacet> config = (ArrayList<IngridFacet>) getAttributeFromSession(request, FACET_CONFIG);
        if(config != null){
            addConfigParamsToURL(config, facetUrl, null);
        }
    }

    private static void addConfigParamsToURL(List<IngridFacet> config, StringBuilder facetUrl, String parentId){
        if(config != null){
            for(IngridFacet facet : config){
                if(parentId != null && facet.isSelect()){
                    appendURLParameterFacet(facetUrl, toURLParamFacet(facet.getParent().getId(), facet.getId()));
                }
                if(facet.getToggle() != null) {
                    IngridFacet toggle = facet.getToggle();
                    if(toggle.isSelect()) {
                        appendURLParameterFacet(facetUrl, toURLParamFacet(facet.getParent().getId(), toggle.getId()));
                    }
                }
                if(facet.getFacets() != null){
                    addConfigParamsToURL(facet.getFacets(), facetUrl, facet.getId());
                }
            }
        }
    }

    /**
     * Set parameters from session to URL
     *
     * @param url
     * @param paramsKey
     * @param parameterFromSession
     */
    private static void addUrlParameterForFacet(StringBuilder url, String paramsKey, ArrayList<String> parameterFromSession) {
        for(int i=0; i<parameterFromSession.size();i++){
            appendURLParameterFacet(url, toURLParamFacet(paramsKey, parameterFromSession.get(i)));
        }
    }


    /**
     * Append a new facet urlParameter to the given URL Parameters.
     *
     * @param currentURLParams
     * @param newURLParam
     */
    private static void appendURLParameterFacet(StringBuilder currentURLParams, String newURLParam) {
        if (newURLParam != null && newURLParam.length() > 0) {
            if (!currentURLParams.toString().equals("?")) {
                currentURLParams.append("");
            }
            currentURLParams.append(newURLParam);
            currentURLParams.append(";");
        }
    }

    /**
     * Returns the facet "GET" Parameter representation for the URL
     *
     * @param values
     * @param paramName
     * @return
     */
    private static String toURLParamFacet(String paramName, String value) {
        if (value == null || value.length() == 0) {
            return "";
        }

        String urlParam = null;
        try {
            urlParam = paramName + ":" + URLEncoder.encode(value, "UTF-8");
        } catch (Exception ex) {
            if (log.isErrorEnabled()) {
                log.error("Problems generating URL representation of parameter: " + paramName + "=" + value
                        + "We generate NO Parameter !", ex);
            }
            urlParam = "";
        }

        return urlParam;
    }

    private static void sortingFacet(List<IngridFacet> config){
        if(config != null){
            for(IngridFacet facet : config){
                if(facet.getSort() != null){
                    String sorting = facet.getSort();
                    if(facet.getFacets() != null){
                        sortFacetConfig(facet.getFacets(), sorting);
                    }
                }
                if(facet.getFacets() != null){
                    sortingFacet(facet.getFacets());
                }
            }
        }
    }

    private static void selectedFacet(List<IngridFacet> config){
        if(config != null){
            for(IngridFacet facet : config){
                if(facet.getFacets() != null){
                    facet.setSelect(selectedFacetCheck(facet.getFacets()));
                }
            }
        }
    }

    private static void hiddenFacet(List<IngridFacet> config){
        hiddenFacet(config, null);
    }

    private static void hiddenFacet(List<IngridFacet> config, List<IngridFacet> children){
        if(config != null){
            List<IngridFacet> tmpList = config;
            if(children != null) {
                tmpList = children;
            }
            for(IngridFacet facet : tmpList){
                ArrayList<IngridFacet> facetHidList = (ArrayList<IngridFacet>) getHiddenFacetById(config, new ArrayList<>(), facet.getId());
                for(IngridFacet facetHid : facetHidList){
                    boolean isHiddenSelect = false;
                    if(facet.isSelect()){
                        isHiddenSelect = true;
                    }
                    facetHid.setHiddenSelect(isHiddenSelect);
                }
                if(facet.getToggle() != null){
                    IngridFacet toggle = facet.getToggle();
                    facetHidList = (ArrayList<IngridFacet>) getHiddenFacetById(config, new ArrayList<>(), toggle.getId());
                    for(IngridFacet facetHid : facetHidList){
                        boolean isHiddenSelect = false;
                        if(toggle.isSelect()){
                            isHiddenSelect = true;
                        }
                        facetHid.setHiddenSelect(isHiddenSelect);
                    }
                }
                if(facet.getFacets() != null){
                    hiddenFacet(config, facet.getFacets());
                }
            }
        }
    }

    private static boolean selectedFacetCheck(List<IngridFacet> config){
        boolean isSelect = false;
        if(config != null){
            for(IngridFacet facet : config){
                if(facet.isSelect()){
                    isSelect = facet.isSelect();
                }
                if(!isSelect && facet.getFacets() != null){
                    isSelect = selectedFacetCheck(facet.getFacets());
                }
            }
        }
        return isSelect;
    }

    @SuppressWarnings("rawtypes")
    private static void sortFacetConfig(List<IngridFacet> config, String sorting){
        if(sorting.equals("SORT_BY_VALUE_ASC")){
            Collections.sort(config ,new Comparator(){
                public int compare(Object left, Object right){
                    Long leftValue = 0L;
                    Long rightValue = 0L;
                    if(left != null && ((IngridFacet) left).getFacetValue() != null){
                        leftValue = Long.parseLong(((IngridFacet) left).getFacetValue());
                    }
                    if(right != null && ((IngridFacet) right).getFacetValue() != null){
                        rightValue = Long.parseLong(((IngridFacet) right).getFacetValue());
                    }
                    return leftValue.compareTo(rightValue);
                }
            });
        }else if(sorting.equals("SORT_BY_VALUE_DESC")){
            Collections.sort(config ,new Comparator(){
                public int compare(Object left, Object right){
                    Long leftValue = 0L;
                    Long rightValue = 0L;
                    if(left != null && ((IngridFacet) left).getFacetValue() != null){
                        leftValue = Long.parseLong(((IngridFacet) left).getFacetValue());
                    }
                    if(right != null && ((IngridFacet) right).getFacetValue() != null){
                        rightValue = Long.parseLong(((IngridFacet) right).getFacetValue());
                    }
                    return leftValue.compareTo(rightValue) * (-1);
                }
            });
        }else if(sorting.equals("SORT_BY_NAME")){
            Collections.sort(config ,new Comparator(){
                public int compare(Object left, Object right){
                    String leftValue = "";
                    String rightValue = "";
                    if(left != null && ((IngridFacet) left).getName() != null){
                        leftValue = ((IngridFacet) left).getName();
                    }
                    if(right != null && ((IngridFacet) right).getName() != null){
                        rightValue = ((IngridFacet) right).getName();
                    }
                    return leftValue.compareTo(rightValue);
                }
            });
        }
    }

    public static void addDefaultIngridFacets(PortletRequest request, List<IngridFacet> config) {
        if(config != null){
            for(int i = 0; i < config.size(); i++){
                IngridFacet facet = config.get(i);
                if(facet.getId().equals("topic")){
                    ArrayList<IngridFacet> list = (ArrayList<IngridFacet>) facet.getFacets();
                    facet.setSort("SORT_BY_VALUE_DESC");
                    CodeListService codelistService = CodeListServiceFactory.instance();
                    ResourceBundle bundle = ResourceBundle.getBundle("de.ingrid.portal.resources.EnvironmentSearchResources", Locale.GERMAN);
                    IngridResourceBundle resources = new IngridResourceBundle(bundle, Locale.GERMAN);
                    List<IngridEnvTopic> topics = UtilsDB.getEnvTopics(resources);
                    for(IngridEnvTopic topic : topics){
                        IngridFacet tmpFacet = new IngridFacet();
                        String id = UtilsDB.getTopicFromKey(topic.getFormValue());
                        tmpFacet.setId(id);
                        tmpFacet.setQuery("topic:"+ id);
                        String entryId = codelistService.getCodeListEntryId("1410", id, UtilsUDKCodeLists.LANG_ID_INGRID_QUERY_VALUE);
                        String localizedValue = codelistService.getCodeListValue("1410", entryId, request.getLocale().getLanguage());
                        tmpFacet.setName(localizedValue);
                        tmpFacet.setParent(facet);
                        if(list == null){
                            list = new ArrayList<>();
                        }
                        list.add(tmpFacet);
                    }
                    facet.setFacets(list);
                }else if(facet.getId().equals("provider")){
                    ArrayList<IngridFacet> list = (ArrayList<IngridFacet>) facet.getFacets();
                    String restrictPartner = PortalConfig.getInstance().getString(PortalConfig.PORTAL_SEARCH_RESTRICT_PARTNER);
                    List<IngridPartner> partners = UtilsDB.getPartners();
                    if(partners != null){
                        boolean isQueryOr = facet.getQueryType() != null && facet.getQueryType().equals("OR");
                        if(restrictPartner != null && restrictPartner.length() > 0){
                            // Partner restriction (set tmpFacet.setParentHidden(true))
                            for(IngridPartner partner : partners){
                                if(partner.getIdent().equals(restrictPartner)){
                                    List<IngridProvider> providers = UtilsDB.getProvidersFromPartnerKey(partner.getIdent());
                                    ArrayList<IngridFacet> listProviders = null;
                                    for(IngridProvider provider : providers){
                                        IngridFacet tmpProvidersFacet = new IngridFacet();
                                        tmpProvidersFacet.setId(provider.getIdent());
                                        tmpProvidersFacet.setQuery("provider:"+ provider.getIdent());
                                        tmpProvidersFacet.setName(provider.getName());
                                        tmpProvidersFacet.setParent(facet);
                                        if(isQueryOr) {
                                            tmpProvidersFacet.setDisplay(true);
                                        }
                                        if(listProviders == null){
                                            listProviders = new ArrayList<>();
                                        }
                                        listProviders.add(tmpProvidersFacet);
                                    }
                                    list = listProviders;
                                    break;
                                }
                            }
                        }else{
                            for(IngridPartner partner : partners){
                                IngridFacet tmpFacet = new IngridFacet();
                                tmpFacet.setId(partner.getIdent());
                                tmpFacet.setQuery("partner:"+ partner.getIdent());
                                tmpFacet.setName(partner.getName());
                                tmpFacet.setSort(facet.getSort());
                                tmpFacet.setParent(facet);
                                IngridFacet newFacet = null;
                                if(isQueryOr) {
                                    newFacet = new IngridFacet();
                                    newFacet.setId(partner.getIdent() + "_group");
                                    newFacet.setDependency(partner.getIdent());
                                    newFacet.setName(partner.getName());
                                    // Get some values from initial facet
                                    newFacet.setListLength(facet.getListLength());
                                    newFacet.setSort(facet.getSort());
                                    newFacet.setQueryType(facet.getQueryType());
                                    tmpFacet.setDisplay(true);
                                }
                                List<IngridProvider> providers = UtilsDB.getProvidersFromPartnerKey(partner.getIdent());
                                ArrayList<IngridFacet> listProviders = null;
                                for(IngridProvider provider : providers){
                                    IngridFacet tmpProvidersFacet = new IngridFacet();
                                    tmpProvidersFacet.setId(provider.getIdent());
                                    tmpProvidersFacet.setQuery("provider:"+ provider.getIdent());
                                    tmpProvidersFacet.setName(provider.getName());
                                    if(isQueryOr) {
                                        tmpProvidersFacet.setParent(newFacet);
                                        tmpProvidersFacet.setDisplay(true);
                                    } else {
                                        tmpProvidersFacet.setParent(tmpFacet);
                                    }
                                    if(listProviders == null){
                                        listProviders = new ArrayList<>();
                                    }
                                    listProviders.add(tmpProvidersFacet);
                                }
                                if(listProviders != null){
                                    if(isQueryOr) {
                                        if(newFacet != null) {
                                            newFacet.setFacets(listProviders);
                                            config.add(i+1, newFacet);
                                            i++;
                                        }
                                    } else {
                                        tmpFacet.setFacets(listProviders);
                                    }
                                }
                                if(list == null){
                                    list = new ArrayList<>();
                                }
                                list.add(tmpFacet);
                            }
                        }
                        facet.setFacets(list);
                    }
                } else if(facet.isHierarchyTree()) {
                    getFacetFromHierarchyTree(config, facet);
                }
                if(facet.getFacets()!= null){
                    addDefaultIngridFacets(request, facet.getFacets());
                }
            }
        }
    }

    public static void getConfigFacetQuery(List<IngridFacet> configNode, List<IngridDocument> facetQueries, boolean isDefault, String mainFacetId){
        getConfigFacetQuery( configNode, facetQueries, isDefault, mainFacetId, false);
    }

    public static void getConfigFacetQuery(List<IngridFacet> config, List<IngridDocument> facetQueries, boolean isDefault, String mainFacetId, boolean isAll){
        IngridDocument facet = null;
        if(config != null){
            config = cleanupFieldFacets(config);
            ArrayList<HashMap<String, String>> facetList = new ArrayList<> ();
            for(IngridFacet ingridFacet : config){
                String facetId = ingridFacet.getId();
                String facetQuery = ingridFacet.getQuery();
                String facetField = ingridFacet.getField();
                String facetCodeList = ingridFacet.getCodelist();
                String facetCodelistField = ingridFacet.getCodelistField();
                IngridFacet toggle = ingridFacet.getToggle();
                if(facetId != null) {
                    if(ingridFacet.getParent() != null){
                        if((!ingridFacet.getParent().getId().equals("topic") || isAll) && !ingridFacet.getParent().getId().equals("partner") && !ingridFacet.getParent().getId().equals("provider")){
                            if(facetQuery != null && ingridFacet.getParent() != null){
                                if(ingridFacet.getParent().getDependency() == null){
                                    // Set sub facets by no define dependency
                                    HashMap<String, String> facetEntry = new HashMap<>();
                                    facetEntry.put("id", facetId);
                                    facetEntry.put("query", facetQuery);
                                    facetList.add(facetEntry);
                                    if(toggle != null) {
                                        if(toggle.isSelect()) {
                                            if(toggle.getQuery() != null) {
                                                facetQuery += " " + toggle.getQuery();
                                                HashMap<String, String> facetEntryToggle = new HashMap<>();
                                                facetEntryToggle.put("id", toggle.getId());
                                                facetEntryToggle.put("query", facetQuery);
                                                facetList.add(facetEntryToggle);
                                            }
                                        }
                                    }
                                    
                                }else{
                                    // Set sub facets only by selected dependency
                                    if(ingridFacet.getParent().isDependencySelect()){
                                        HashMap<String, String> facetEntry = new HashMap<>();
                                        facetEntry.put("id", facetId);
                                        facetEntry.put("query", facetQuery);
                                        facetList.add(facetEntry);
                                    }
                                }
                            }
                            // Set sub facets only by selected
                            if((ingridFacet.isSelect() || isAll) && ingridFacet.getFacets() != null){
                                getConfigFacetQuery(ingridFacet.getFacets(), facetQueries, false, facetId, isAll);
                            }
                        }
                    }else{
                        if(facetQuery != null){
                            IngridDocument tmpFacet = new IngridDocument();
                            tmpFacet.put("id", facetId);
                            tmpFacet.put("query", facetQuery);
                            facetQueries.add(tmpFacet);
                        }else if(facetField != null) {
                            IngridDocument tmpFacet = new IngridDocument();
                            tmpFacet.put("id", facetId);
                            if(facetCodeList != null && facetCodelistField != null){
                                CodeListService codelistService = CodeListServiceFactory.instance();
                                CodeList codelist = codelistService.getCodeList(facetCodeList);
                                List<CodeListEntry> codelistEntries = codelist.getEntries();
                                ArrayList<IngridDocument> tmpFacetEntries = new ArrayList<> ();
                                for (CodeListEntry codeListEntry : codelistEntries) {
                                    IngridDocument tmpFacetEntry = new IngridDocument();
                                    tmpFacetEntry.put("id", facetCodeList+ "_" + codeListEntry.getId());
                                    tmpFacetEntry.put("query",ingridFacet.getField() + ":\"" + codeListEntry.getField(facetCodelistField) + "\"");
                                    tmpFacetEntries.add(tmpFacetEntry);
                                }
                                tmpFacet.put("classes", tmpFacetEntries);
                            } else {
                                tmpFacet.put("field", facetField);
                            }
                            facetQueries.add(tmpFacet);
                        }
                        if(ingridFacet.getFacets() != null){
                              if(ingridFacet.getField() == null) {
                                  getConfigFacetQuery(ingridFacet.getFacets(), facetQueries, false, facetId, isAll);
                              }
                        }
                    }
                }
            }
            if(mainFacetId != null && !facetList.isEmpty()){
                facet = new IngridDocument();
                facet.put("id", mainFacetId);
                facet.put("classes", facetList);
                facetQueries.add(facet);
            }
        }

        if(isDefault){
            // Set facet partner
            IngridDocument faceteEntry = new IngridDocument();
            faceteEntry.put("id", "partner");
            facetQueries.add(faceteEntry);

            // Set facet provider
            String restrictPartner = PortalConfig.getInstance().getString(PortalConfig.PORTAL_SEARCH_RESTRICT_PARTNER);
            if(restrictPartner != null && restrictPartner.length() > 0){
                // Personalized partner restriction in portal: set always facet for defined partner
                faceteEntry = new IngridDocument();
                faceteEntry.put("id", "provider");
                faceteEntry.put("field", "provider");
                faceteEntry.put("query", "partner:" + restrictPartner);
                facetQueries.add(faceteEntry);
            }else{
                IngridFacet tmpFacets = getFacetById(config, "provider");
                IngridFacet tmpFacet = null;
                if(tmpFacets != null){
                    for(IngridFacet tmpFacetEntry : tmpFacets.getFacets()){
                        if(tmpFacetEntry.isSelect()){
                            tmpFacet = tmpFacetEntry;
                            break;
                        }
                    }
                    if(tmpFacet != null){
                        faceteEntry = new IngridDocument();
                        // NEW FACET ID ! Facet is restricted to providers of partner, so we
                        // have to use NEW ID to separate from normal "provider" facet !
                        // This way caching works in backend, e.g. SE-iPlug where id is cache key !
                        // So facet id is e.g. "provider_by" when "by" is selected as partner.
                        faceteEntry.put("id", "provider_" + tmpFacet.getId());
                        faceteEntry.put("field", "provider");
                        faceteEntry.put("query", "partner:" + tmpFacet.getId());
                        facetQueries.add(faceteEntry);
                    }
                }
            }

            // Set facet topic
            IngridFacet tmpFacets = getFacetById(config, "topic");
            if(tmpFacets != null && tmpFacets.isSelect()){
                faceteEntry = new IngridDocument();
                faceteEntry.put("id", "topic");
                faceteEntry.put("field", "topic");
                facetQueries.add(faceteEntry);
            }
        }
    }

    private static void getFacetFromHierarchyTree(List<IngridFacet> config, IngridFacet ingridFacet) {
        String facetHierarchyTreeType = ingridFacet.getHierarchyTreeTypes();
        int facetHierarchyTreeLevel = ingridFacet.getHierarchyTreeLevel();
        
        PlugDescription[] allPlugs = IBUSInterfaceImpl.getInstance().getAllActiveIPlugs();
        // filter types
        String[] plugTypes = facetHierarchyTreeType.split(",");
        PlugDescription[] plugs = IPlugHelper.filterIPlugsByType(allPlugs, plugTypes);

        // filter corrupt ones
        plugs = IPlugHelper.filterCorruptECSIPlugs(plugs);

        // sort
        plugs = IPlugHelper.sortPlugs(plugs, new IPlugHelperDscEcs.PlugComparatorECS());

        DisplayTreeNode plugsRoot = DisplayTreeFactory.getTreeFromECSIPlugs(plugs);

        int openNodesInitial = facetHierarchyTreeLevel;
        if (openNodesInitial > 0) {
            getSubFacetsUntilHierarchyLevel(plugsRoot, plugsRoot, openNodesInitial);
        }
        if(plugsRoot != null) {
            if(plugsRoot.getChildren() != null) {
                String partnerRestriction = PortalConfig.getInstance().getString(
                        PortalConfig.PORTAL_SEARCH_RESTRICT_PARTNER);
                if(partnerRestriction.isEmpty()) {
                    DisplayTreeNode partnerNode = (DisplayTreeNode) plugsRoot.getChildren().get(0);
                    if(partnerNode != null) {
                        if(partnerNode.getChildren() != null) {
                            DisplayTreeNode iPlugNode = (DisplayTreeNode) partnerNode.getChildren().get(0);
                            if(iPlugNode != null) {
                                if(iPlugNode.getChildren() != null) {
                                    DisplayTreeNode objectNode = (DisplayTreeNode) iPlugNode.getChildren().get(0);
                                    if(objectNode != null) {
                                        if(objectNode.getChildren() != null) {
                                            addHierarchyNodesToFacets(ingridFacet, objectNode.getChildren(), config);
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    DisplayTreeNode iPlugNode = (DisplayTreeNode) plugsRoot.getChildren().get(0);
                    if(iPlugNode != null) {
                        if(iPlugNode.getChildren() != null) {
                            DisplayTreeNode objectNode = (DisplayTreeNode) iPlugNode.getChildren().get(0);
                            if(objectNode != null) {
                                if(objectNode.getChildren() != null) {
                                    addHierarchyNodesToFacets(ingridFacet, objectNode.getChildren(), config);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void addHierarchyNodesToFacets(IngridFacet ingridFacet, List<DisplayTreeNode> nodes, List<IngridFacet> config) {
        List<IngridFacet> newFacets = new ArrayList<IngridFacet>();
        for (DisplayTreeNode node : nodes) {
            String docId = node.get("docId").toString();
            String id = ingridFacet.getId() + "_" + docId;
            String name = node.getName();
            boolean nodeIsExpandable = (boolean) node.get("expandable");
            if(nodeIsExpandable) {
                IngridFacet tmpFacet = new IngridFacet();
                tmpFacet.setId(id);
                tmpFacet.setQuery("object_node.tree_path.uuid:" + docId + " isFolder:false");
                tmpFacet.setName(name);
                tmpFacet.setParent(ingridFacet);
                tmpFacet.setHierarchyTreeLeaf(true);
                tmpFacet.setHierarchyTreeNode(node);
                if(ingridFacet.getListLength() > 0) {
                    tmpFacet.setListLength(ingridFacet.getListLength());
                }
                if(ingridFacet.getSort() != null) {
                    tmpFacet.setSort(ingridFacet.getSort());
                }
                newFacets.add(tmpFacet);
                addHierarchyNodesToFacets(tmpFacet, node.getChildren(), config) ;
            }
        }
        ingridFacet.setFacets(newFacets);
    }

    private static void getSubFacetsUntilHierarchyLevel(DisplayTreeNode node, DisplayTreeNode rootNode, int openNodesInitial) {
        ArrayList list = (ArrayList) node.getChildren();
        String rootNodeLevel = openNodesInitial + "";

        if (rootNodeLevel != null) {
            for (int i = 0; i < list.size(); i++) {
                if (node.get("level").toString().equals(rootNodeLevel)) {
                    break;
                }
                DisplayTreeNode subNode = (DisplayTreeNode) list.get(i);
                if ((Boolean) subNode.get("expandable")) {
                    String plugType = (String) subNode.get("plugType");
                    if(plugType == null || (plugType != null && plugType.equals( "dsc_ecs" ))){
                        openNode(rootNode, subNode.getId());
                        getSubFacetsUntilHierarchyLevel(subNode, rootNode, openNodesInitial);
                    }
                }
            }
        }
    }

    private static void openNode(DisplayTreeNode rootNode, String nodeId) {
        DisplayTreeNode node = rootNode.getChild(nodeId);
        if (node == null) {
            node = rootNode;
        }
        if (node != null) {
            node.setOpen(true);

            // only load if not loaded yet !
            if (!node.isLoading() && node.getChildren().isEmpty()) {
                node.setLoading(true);

                // handles all stuff
                String plugType = (String) node.get(NODE_PLUG_TYPE);
                String plugId = (String) node.get(NODE_PLUG_ID);

                if (plugType == null || plugId == null) {
                    // no iplug node, is parent folder
                    return;
                }

                // get ALL children (IngridHits)
                String udkDocId = (String) node.get(NODE_UDK_DOC_ID);
                String keyUdkDocId = "";
                String keyUdkClass = "";
                List hits;
                
                PlugDescription pd = IBUSInterfaceImpl.getInstance().getIPlug(plugId);
                CatalogTreeDataProvider ctdp = CatalogTreeDataProviderFactory.getDetailDataPreparer(IPlugVersionInspector.getIPlugVersion(pd));
                
                if (udkDocId == null) {
                    hits = ctdp.getTopEntities(plugId, plugType);
                } else {
                    hits = ctdp.getSubEntities(udkDocId, plugId, plugType, null);
                }

                // keys for extracting data
                if (plugType.equals(Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS)) {
                    keyUdkDocId = Settings.HIT_KEY_OBJ_ID;
                    keyUdkClass = Settings.HIT_KEY_UDK_CLASS;

                }

                // set up according children nodes in tree
                int parentLevel = ((Integer) node.get(NODE_LEVEL)).intValue();
                int childrenLevel = parentLevel + 1;
                ArrayList<DisplayTreeNode> childNodes = new ArrayList(hits.size());
                Iterator it = hits.iterator();
                while (it.hasNext()) {
                    IngridHit hit = (IngridHit) it.next();
                    IngridHitDetail detail = (IngridHitDetail) hit.get(Settings.RESULT_KEY_DETAIL);
                    udkDocId = UtilsSearch.getDetailValue(detail, keyUdkDocId);
                    String udkClass = UtilsSearch.getDetailValue(detail, keyUdkClass);
                    String origDocId = UtilsSearch.getDetailValue(detail, Settings.HIT_KEY_ORG_OBJ_ID);

                    String nodeName = detail.getTitle();

                    // check whether child node has children as well -> request only 1 child !
                    boolean hasChildren = ctdp.hasChildren(udkDocId, plugId, plugType);
                    
                    DisplayTreeNode childNode = new DisplayTreeNode(Utils.getMD5Hash(plugId + udkDocId), nodeName, false);
                    childNode.setType(DisplayTreeNode.GENERIC);
                    childNode.put(NODE_LEVEL, childrenLevel);
                    childNode.put(NODE_PLUG_TYPE, plugType);
                    childNode.put(NODE_PLUG_ID, plugId);
                    childNode.put(NODE_DOC_ID, hit.getId());
                    childNode.put(NODE_UDK_DOC_ID, udkDocId);
                    childNode.put(NODE_ORIG_DOC_ID, origDocId);
                    childNode.put(NODE_UDK_CLASS, udkClass);
                    childNode.put(NODE_EXPANDABLE, hasChildren);

                    childNodes.add(childNode);
                }
                // and add them to the parent
                it = childNodes.iterator();
                while (it.hasNext()) {
                    DisplayTreeNode childNode = (DisplayTreeNode) it.next();
                    childNode.setParent(node);
                    node.addChild(childNode);
                }
                node.setLoading(false);
            }
        }
    }

    public static IngridQuery setFacetQuery(List<IngridFacet> configNode, IngridQuery query) throws ParseException {
        return setFacetQuery(configNode, query, null);
    }

    public static IngridQuery setFacetQuery(List<IngridFacet> configNode, IngridQuery query, String parent) throws ParseException {
        if(query != null) {
            String term = "";
            for (IngridFacet ingridFacet : configNode){
                if(parent == null || parent != ingridFacet.getId()) {
                    if(ingridFacet.getFacets() != null){
                        term = getQuerySelection(term, ingridFacet.getQueryType(), ingridFacet.getFacets());
                    }
                }
            }
            if(!term.isEmpty()) {
                IngridQuery tmpQuery = QueryStringParser.parse(term);
                if(tmpQuery != null) {
                    ClauseQuery clauseQuery = UtilsSearch.createClauseQuery(tmpQuery, true, false);
                    query.addClause(clauseQuery);
                }
            }
        }
        return query;
    }

    private static String getQuerySelection(String term, String type, List<IngridFacet> facets) {
        if(facets != null){
            if(type != null){
                // OR
                if(type.equals("OR")){
                    boolean hasSelected = false;
                    boolean hasSelectedToggle = false;
                    ArrayList<String> queries = new ArrayList<>();
                    String orQuery = "";
                    for(IngridFacet ingridFacet : facets){
                        if(ingridFacet.isSelect()) {
                            hasSelected = true;
                        }
                        if(ingridFacet.getToggle() != null && ingridFacet.getToggle().isSelect()) {
                            hasSelectedToggle = true;
                        }
                        if(hasSelected && hasSelectedToggle) {
                            break;
                        }
                    }
                    for(IngridFacet ingridFacet : facets){
                        IngridFacet toggle = ingridFacet.getToggle();
                        String query = "";
                        if(hasSelected && !hasSelectedToggle) {
                            if(ingridFacet.getQuery() != null && ingridFacet.isSelect()){
                                query = ingridFacet.getQuery();
                            }
                        } else if(!hasSelected && hasSelectedToggle) {
                            if(ingridFacet.getQuery() != null){
                                query = ingridFacet.getQuery();
                            }
                            if(toggle != null && toggle.isSelect() && toggle.getQuery() != null) {
                                query = toggle.getQuery();
                            }
                        } else if(hasSelected && hasSelectedToggle) {
                            if(ingridFacet.getQuery() != null && ingridFacet.isSelect()){
                                query = ingridFacet.getQuery();
                            }
                            if(ingridFacet.getQuery() != null && ingridFacet.isSelect() && toggle != null && toggle.isSelect() && toggle.getQuery() != null) {
                                query = toggle.getQuery();
                            }
                        }
                        if(!query.isEmpty()) {
                            queries.add(query);
                        }
                    }
                    for (String q : queries) {
                        if(!orQuery.isEmpty()) {
                            orQuery += " OR ";
                        }
                        if(queries.size() > 1 ) {
                            orQuery += "(" + q + ")";
                        } else {
                            orQuery += q;
                        }
                    }
                    if(!orQuery.isEmpty()){
                        if(!term.isEmpty()) {
                            term = "(" + term + ") ";
                            orQuery = "(" + orQuery + ")";
                        }
                        term = term + orQuery;
                    }
                }
            }else{
                // AND
                for(IngridFacet ingridFacet : facets){
                    IngridFacet toggle = ingridFacet.getToggle();
                    if((ingridFacet.isSelect() || ingridFacet.isParentHidden() || (toggle != null && toggle.isSelect())) && ingridFacet.getQuery() != null){
                        term = term + " " + ingridFacet.getQuery();
                        if(toggle != null && toggle.isSelect() && toggle.getQuery() != null) {
                            term += " " + toggle.getQuery();
                        }
                        if(ingridFacet.getFacets() != null){
                            term = getQuerySelection(term, ingridFacet.getQueryType(), ingridFacet.getFacets());
                        }
                    }
                }
            }
        }
        return term;
    }

    private static void getActionKeysFromConfig(List<IngridFacet> configNode, HashMap<String, String> configKeys) {
        if(configNode != null){
            for (IngridFacet ingridFacet : configNode){
                IngridFacet ingridFacetParent = ingridFacet.getParent();
                if(ingridFacetParent != null){
                    String key = ingridFacetParent.getId();
                    if(key != null){
                        configKeys.put(key, key);
                    }
                }
                if(ingridFacet.getField() != null) {
                    configKeys.put(ingridFacet.getId(), ingridFacet.getId());
                } else {
                    if(ingridFacet.getFacets() != null){
                        getActionKeysFromConfig(ingridFacet.getFacets(), configKeys);
                    }
                }
            }
        } else {
            if(log.isDebugEnabled()) {
                log.debug("Facet config is null!");
            }
        }
    }

    private static Set<String> getExistingSelectionKeys(PortletRequest request){
        HashMap<String, String> configIds = new HashMap<>();
        getActionKeysFromConfig((ArrayList<IngridFacet>) getAttributeFromSession(request, FACET_CONFIG), configIds);
        if(log.isDebugEnabled()) {
            log.debug("Facet keys: " + configIds.keySet());
        }
        return configIds.keySet();
    }

    public static IngridFacet getFacetById(List<IngridFacet> facets, String key){
        IngridFacet ingridFacet = null;
        if(facets != null){
            for(IngridFacet tmpIngridFacet: facets){
                if(tmpIngridFacet.getId() != null && key.equals(tmpIngridFacet.getId())){
                    ingridFacet = tmpIngridFacet;
                }
                if(ingridFacet == null && tmpIngridFacet.getFacets() != null){
                    ingridFacet = getFacetById(tmpIngridFacet.getFacets(), key);
                }
            }
        }
        return ingridFacet;
    }

    private static List<IngridFacet> getDependencyFacetById(List<IngridFacet> facets, List<IngridFacet> list, String key){
        if(facets != null){
            for(IngridFacet tmpIngridFacet: facets){
                if(tmpIngridFacet.getDependency() != null && key.indexOf(tmpIngridFacet.getDependency()) > -1){
                    if(list == null){
                        list = new ArrayList<>();
                    }
                    list.add(tmpIngridFacet);
                }
                if(tmpIngridFacet.getFacets() != null){
                    list = getDependencyFacetById(tmpIngridFacet.getFacets(), list, key);
                }
            }
        }
        return list;
    }

    private static List<IngridFacet> getHiddenFacetById(List<IngridFacet> facets, List<IngridFacet> list, String key){
        if(facets != null){
            for(IngridFacet tmpIngridFacet: facets){
                if(tmpIngridFacet.getHidden() != null && key.indexOf(tmpIngridFacet.getHidden()) > -1){
                    if(list == null){
                        list = new ArrayList<>();
                    }
                    list.add(tmpIngridFacet);
                }
                if(tmpIngridFacet.getFacets() != null){
                    list = getHiddenFacetById(tmpIngridFacet.getFacets(), list, key);
                }
            }
        }
        return list;
    }

    private static boolean isAnyFacetConfigSelect(List<IngridFacet> config, boolean sessionSelect) {
        boolean isSelect = false;
        if(config != null) {
            for(IngridFacet facet : config){
                if(facet.isSelect()){
                    isSelect = true;
                    break;
                }
                if(facet.getToggle() != null) {
                    if(facet.getToggle().isSelect()) {
                        isSelect = true;
                        break;
                    }
                }
                if(facet.getFacets() != null){
                    isSelect = isAnyFacetConfigSelect(facet.getFacets(), false);
                    if(isSelect){
                        break;
                    }
                }
            }
        }
        if(!isSelect){
            isSelect = sessionSelect;
        }
        return isSelect;
    }

    private static void getSelectedGroup(List<IngridFacet> config, ArrayList<String> list) {
        if(config != null) {
            for(IngridFacet facet : config){
                if(facet.isSelect() && facet.getSelectGroup() != null){
                    list.add(facet.getSelectGroup());
                }
                if(facet.getFacets() != null){
                    getSelectedGroup(facet.getFacets(), list);
                }
            }
        }
    }

    private static boolean isFacetConfigSelect(List<IngridFacet> config) {
        boolean isSelect = false;
        for(IngridFacet facet : config){
            if(facet.isSelect()){
                isSelect = true;
                break;
            }
        }
        return isSelect;
    }


    private static void resetFacetConfigValues(List<IngridFacet> config, String key) {
        if(config != null){
            for(IngridFacet facet : config){
                boolean isOrSelect = false;
                if(key != null && facet.getId().equals(key) &&  facet.getQueryType() != null && facet.getQueryType().equals("OR")){
                    isOrSelect = true;
                }
                if(!isOrSelect){
                    facet.setFacetValue(null);
                    if(facet.getFacets() != null){
                        resetFacetConfigValues(facet.getFacets(), key);
                    }
                }
            }
        }
    }

    private static void resetFacetConfigSelect(List<IngridFacet> config) {
        resetFacetConfigSelect(config, false);
    }

    private static void resetFacetConfigSelect(List<IngridFacet> config, boolean resetToggle) {
        if(config != null){
            for(IngridFacet facet : config){
                facet.setSelect(false);
                if (resetToggle) {
                    if(facet.getToggle() != null) {
                        facet.getToggle().setSelect(false);
                    }
                }
                if(facet.getFacets() != null){
                    resetFacetConfigSelect(facet.getFacets(), resetToggle);
                }
            }
        }
    }
}
