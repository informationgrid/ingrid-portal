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
package de.ingrid.portal.portlets;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.ResourceURL;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringUtils;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.generic.EscapeTool;
import org.codehaus.jettison.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ingrid.geo.utils.transformation.GmlToWktTransformUtil;
import de.ingrid.geo.utils.transformation.WktToGeoJsonTransformUtil;
import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.global.CodeListServiceFactory;
import de.ingrid.portal.global.IngridHitsWrapper;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.IngridSysCodeList;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.UniversalSorter;
import de.ingrid.portal.global.UtilsFacete;
import de.ingrid.portal.global.UtilsPortletServeResources;
import de.ingrid.portal.global.UtilsString;
import de.ingrid.portal.om.IngridFacet;
import de.ingrid.portal.search.QueryPreProcessor;
import de.ingrid.portal.search.QueryResultPostProcessor;
import de.ingrid.portal.search.SearchState;
import de.ingrid.portal.search.UtilsSearch;
import de.ingrid.portal.search.net.QueryDescriptor;
import de.ingrid.portal.search.net.ThreadedQueryController;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.ParseException;

/**
 * This portlet handles the "Result Display" fragment of the result page
 * 
 * @author martin@wemove.com
 */
public class SearchResultPortlet extends GenericVelocityPortlet {

    private static final Logger log = LoggerFactory.getLogger(SearchResultPortlet.class);

    /** view templates */
    private static final String TEMPLATE_NO_QUERY_SET = "/WEB-INF/templates/empty.vm";

    private static final String TEMPLATE_RESULT = "/WEB-INF/templates/search_result.vm";

    private static final String TEMPLATE_RESULT_ADDRESS = "/WEB-INF/templates/search_result_address.vm";

    private static final String TEMPLATE_RESULT_FILTERED_ONECOLUMN = "/WEB-INF/templates/search_result_iplug.vm";
    
    private HttpClient client;

    @Override
    public void serveResource(ResourceRequest request, ResourceResponse response) throws IOException {
        String resourceID = request.getResourceID();
        String paramURL = request.getParameter( "url" );

        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()), request.getLocale());

        String login = PortalConfig.getInstance().getString(PortalConfig.PORTAL_DETAIL_UVP_DOCUMENTS_HTACCESS_LOGIN);
        String password = PortalConfig.getInstance().getString(PortalConfig.PORTAL_DETAIL_UVP_DOCUMENTS_HTACCESS_PASSWORD);

        if(paramURL != null) {
            if (resourceID.equals( "httpURLDataType" )) {
                UtilsPortletServeResources.getHttpUrlDatatype(paramURL, login, password, response);
            }
            if (resourceID.equals( "httpURLImage" )) {
                UtilsPortletServeResources.getHttpUrlImage(paramURL, response, resourceID);
            }
        } else {
            if (resourceID.equals( "httpURLSearchDownload" )) {
                try {
                    // String[] requestFields = PortalConfig.getInstance().getStringArray(PortalConfig.PORTAL_DETAIL_UVP_DOCUMENTS_HTACCESS_LOGIN);
                    String paramRequestFields = request.getParameter("requestedFields");
                    if(paramRequestFields == null) {
                        paramRequestFields = PortalConfig.getInstance().getString(PortalConfig.PORTAL_SEARCH_EXPORT_CSV_REQUESTED_FIELDS);
                    }
                    String paramCodelistParam = request.getParameter("codelists");
                    HashMap<String, String> codelists = new HashMap<String, String>();
                    if(paramCodelistParam != null) {
                        for (String codelist : paramCodelistParam.split(",")) {
                            String key = codelist.split(":")[0];
                            String value = codelist.split(":")[1];
                            codelists.put(key, value);
                        }
                    }
                    UtilsPortletServeResources.getHttpURLSearchDownload(request, response, paramRequestFields.split(";"), codelists, messages);
                } catch (ParseException | JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        ArrayList<IngridFacet> config = (ArrayList<IngridFacet>) UtilsFacete.getAttributeFromSession(request, UtilsFacete.FACET_CONFIG);
        try {
            if(resourceID.equals( "facetValue" )){
                String facetId = request.getParameter("facetId");
                if(facetId != null) {
                    UtilsPortletServeResources.getHttpFacetValue(request, response, config, facetId, messages);
                }
            }
        } catch (Exception e) {
            log.error( "Error creating resource for resource ID: " + resourceID, e );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.portlet.Portlet#init(javax.portlet.PortletConfig)
     */
    @Override
    public void init(PortletConfig config) throws PortletException {
        super.init(config);
        client = new HttpClient();
        client.getHttpConnectionManager().getParams().setConnectionTimeout(1000);
    }

    @Override
    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);
        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()), request.getLocale());
        context.put("MESSAGES", messages);
        context.put("stringTool", new UtilsString());
        context.put("Codelists", CodeListServiceFactory.instance());
        context.put( "sysCodeList", new IngridSysCodeList(request.getLocale()));
        context.put("enableFacete", PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_ENABLE_SEARCH_FACETE, false));
        context.put("showHitPartnerLogo", PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_SEARCH_HIT_PARTNER_LOGO, false));
        context.put("checkedCategoryDevPlan", PortalConfig.getInstance().getBoolean( PortalConfig.PORTAL_MAPCLIENT_UVP_CATEGORY_DEV_PLAN_CHECKED, false ));
        context.put("checkedCategory10", PortalConfig.getInstance().getBoolean( PortalConfig.PORTAL_MAPCLIENT_UVP_CATEGORY_10_CHECKED, false ));
        context.put("checkedCategory11", PortalConfig.getInstance().getBoolean( PortalConfig.PORTAL_MAPCLIENT_UVP_CATEGORY_11_CHECKED, false ));
        context.put("checkedCategory12", PortalConfig.getInstance().getBoolean( PortalConfig.PORTAL_MAPCLIENT_UVP_CATEGORY_12_CHECKED, false ));
        context.put("checkedCategory13", PortalConfig.getInstance().getBoolean( PortalConfig.PORTAL_MAPCLIENT_UVP_CATEGORY_13_CHECKED, false ));
        context.put("checkedCategory14", PortalConfig.getInstance().getBoolean( PortalConfig.PORTAL_MAPCLIENT_UVP_CATEGORY_14_CHECKED, false ));
        context.put("ranking", request.getParameter(Settings.PARAM_RANKING));
        context.put("sorter", new UniversalSorter(Locale.GERMAN) );
        context.put("escTool", new EscapeTool());
        context.put("mapLinksNewTab", PortalConfig.getInstance().getBoolean( PortalConfig.PORTAL_MAPS_LINKS_NEW_TAB, false ));

        context.put("transformCoupledCSWUrl", PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_SEARCH_HIT_TRANSFORM_COUPLED_CSW_URL, false)); 
        context.put("hideGeodataSetOnOpenData", PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_SEARCH_HIT_HIDE_GEODATASET_ON_OPENDATA, false));
        context.put("hvdDisplayIcon", PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_SEARCH_HIT_HVD_DISPLAY_ICON, false));

        context.put("exportCSV", PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_SEARCH_EXPORT_CSV, false));
        context.put("exportCSVRequestedFields", PortalConfig.getInstance().getString(PortalConfig.PORTAL_SEARCH_EXPORT_CSV_REQUESTED_FIELDS));
        
        ResourceURL restUrl = response.createResourceURL();
        restUrl.setResourceID( "httpURLDataType" );
        request.setAttribute( "restUrlHttpGetDataType", restUrl.toString() );

        restUrl = response.createResourceURL();
        restUrl.setResourceID( "httpURLImage" );
        request.setAttribute( "restUrlHttpGetImage", restUrl.toString() );

        // Geotools
        context.put("geoGmlToWkt", GmlToWktTransformUtil.class);
        context.put("geoWktToGeoJson", WktToGeoJsonTransformUtil.class);

        String[] mapPosition = PortalConfig.getInstance().getStringArray( PortalConfig.PORTAL_MAPCLIENT_LEAFLET_POSITION);
        if(mapPosition != null && mapPosition.length == 3) {
            context.put("mapPosition", mapPosition);
            context.put("mapExtent", "");
        } else if(mapPosition != null && mapPosition.length == 4) {
            context.put("mapExtent", mapPosition);
            context.put("mapPosition", "");
        }
        context.put("leafletEpsg", PortalConfig.getInstance().getString( PortalConfig.PORTAL_MAPCLIENT_LEAFLET_EPSG, "3857"));
        context.put("mapParamE", request.getParameter("E") != null ? request.getParameter("E"): "");
        context.put("mapParamN", request.getParameter("N") != null ? request.getParameter("N"): "");
        context.put("mapParamZoom", request.getParameter("zoom") != null ? request.getParameter("zoom"): "");
        context.put("mapParamExtent", request.getParameter("extent") != null ? request.getParameter("extent"): "");
        context.put("mapParamLayer", request.getParameter("layer") != null ? request.getParameter("layer"): "");

        context.put( "leafletBboxInverted", PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_MAPCLIENT_LEAFLET_BBOX_INVERTED));
        context.put( "leafletBboxColor", PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_LEAFLET_BBOX_COLOR));
        context.put( "leafletBboxFillOpacity", PortalConfig.getInstance().getFloat(PortalConfig.PORTAL_MAPCLIENT_LEAFLET_BBOX_FILLOPACITY));
        context.put( "leafletBboxWeight", PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_LEAFLET_BBOX_WEIGHT));

        context.put("detailUseParamPlugid", PortalConfig.getInstance().getBoolean( PortalConfig.PORTAL_DETAIL_USE_PARAMETER_PLUGID));

        context.put("cutSumHTMLNewLine", PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_SEARCH_HIT_CUTTED_SUMMARY_HTML_NEW_LINE, false));
        context.put("isCutSummary", PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_SEARCH_HIT_CUT_SUMMARY, true));
        context.put("cutSummaryLines", PortalConfig.getInstance().getInt(PortalConfig.PORTAL_SEARCH_HIT_CUT_SUMMARY_LINES, 2));
        
        context.put(Settings.PARAM_QUERY_STRING, request.getParameter(Settings.PARAM_QUERY_STRING));
        context.put(Settings.PARAM_STARTHIT_RANKED, request.getParameter(Settings.PARAM_STARTHIT_RANKED));
        context.put(Settings.PARAM_CURRENT_SELECTOR_PAGE, request.getParameter(Settings.PARAM_CURRENT_SELECTOR_PAGE));
        context.put(Settings.PARAM_RANKING, request.getParameter(Settings.PARAM_RANKING));

        // add request language, used to localize the map client
        context.put("languageCode",request.getLocale().getLanguage());
        
        if(request.getParameter("filter") != null && request.getParameter("filter").equals("domain")){
              context.put("isFilterDomain", true);
        }
        // ----------------------------------
        // check for passed RENDER PARAMETERS (for bookmarking) and
        // ADAPT OUR PERMANENT STATE VIA MESSAGES
        // ----------------------------------
        // starthit RANKED

        // WHEN NO GROUPING !!!
        String reqParam = null;
        
        int rankedStartHit = 0;
        try {
            reqParam = request.getParameter(Settings.PARAM_STARTHIT_RANKED);
            if (SearchState.adaptSearchState(request, Settings.PARAM_STARTHIT_RANKED, reqParam)) {
                rankedStartHit = (Integer.valueOf(reqParam)).intValue();
            }
        } catch (Exception ex) {
            if (log.isErrorEnabled()) {
                log.error("Problems parsing RANKED starthit from render request, set RANKED starthit to 0 ! reqParam="
                        + reqParam, ex);
            }
        }

        // WHEN GROUPING !!!
        // get the current page number, default to 1
        int currentSelectorPage = 1;
        try {
            reqParam = request.getParameter(Settings.PARAM_CURRENT_SELECTOR_PAGE);
            if (SearchState.adaptSearchState(request, Settings.PARAM_CURRENT_SELECTOR_PAGE, reqParam)) {
                currentSelectorPage = Integer.parseInt(reqParam);
            }
        } catch (Exception ex) {
            if (log.isErrorEnabled()) {
                log.error(
                        "Problems parsing currentSelectorPage from render request, set currentSelectorPage to 1 ! reqParam="
                                + reqParam, ex);
            }
        }

        // indicates whether we do a query or we read results from cache
        String queryType = (String) SearchState.getSearchStateObject(request, Settings.MSG_QUERY_EXECUTION_TYPE);
        if (queryType == null) {
            queryType = "";
        }

        // get the filter from REQUEST, not search state ! -> so back button works !
        // ALSO ADAPT SEARCH STATE, so following steps (query pre processor) work !
        String filter = request.getParameter(Settings.PARAM_FILTER);
        if (filter != null && filter.length() == 0) {
             filter = null;
        }
        SearchState.adaptSearchState(request, Settings.PARAM_FILTER, filter);
        String filterSubject = request.getParameter(Settings.PARAM_SUBJECT);
        SearchState.adaptSearchState(request, Settings.PARAM_SUBJECT, filterSubject);

        // set filter params into context for filter display
        if (filter != null) {
            context.put("filteredBy", filter);
            if (filter.equals(Settings.PARAMV_GROUPING_PARTNER)) {
                context.put("filterSubject", UtilsSearch.mapResultValue(Settings.RESULT_KEY_PARTNER, filterSubject, null));
            } else if (filter.equals(Settings.PARAMV_GROUPING_PROVIDER)) {
                context.put("filterSubject", UtilsSearch.mapResultValue(Settings.RESULT_KEY_PROVIDER, filterSubject, null));
            } else if (filter.equals(Settings.PARAMV_GROUPING_PLUG_ID)) {
                context.put("filterSubject", UtilsSearch.mapResultValue(Settings.RESULT_KEY_PLUG_ID, filterSubject, null));
            } else if (filter.equals(Settings.PARAMV_GROUPING_DOMAIN)) {
                 String[] keyValuePair = UtilsSearch.getDomainKeyValuePair(filterSubject);
                 String domainKey = keyValuePair[0];
                 String domainValue = keyValuePair[1];
                 // domain can be plugid or site or ...
                 // we extract the according value from our subject
                 if (domainKey.equals(Settings.QFIELD_PLUG_ID)) {
                      domainValue = UtilsSearch.mapResultValue(Settings.RESULT_KEY_PLUG_ID, domainValue, null);
                 }
                 context.put("filterSubject", domainValue);
            }
        }

        // datasource from state (set in SimpleSearch Portlet)
        String selectedDS = SearchState.getSearchStateObjectAsString(request, Settings.PARAM_DATASOURCE);

        // IngridQuery from state  (set in SimpleSearch Portlet)
        IngridQuery query = (IngridQuery) SearchState.getSearchStateObject(request, Settings.MSG_QUERY);
        
        // change datasource dependent from query input
        selectedDS = UtilsSearch.determineFinalPortalDatasource(selectedDS, query);

        // ----------------------------------
        // set initial view template
        // ----------------------------------

        // if no query set display "nothing"
        if (query == null) {
            setDefaultViewPage(TEMPLATE_NO_QUERY_SET);
            super.doView(request, response);
            return;
        }

        // selected data source ("Umweltinfo", Adressen" or "Forschungsprojekte")
        if (selectedDS == null) {
            selectedDS = Settings.SEARCH_INITIAL_DATASOURCE;
        }
        if (selectedDS.equals(Settings.PARAMV_DATASOURCE_ADDRESS)) {
            setDefaultViewPage(TEMPLATE_RESULT_ADDRESS);
        } else {
            setDefaultViewPage(TEMPLATE_RESULT);

            // default: right column IS grouped (by plugid)
             // set in context e.g. to show grouped navigation
             context.put("grouping_right", true);

             // set one column result template if "Zeige alle ..." of plug or domain
             if (filter != null && (filter.equals(Settings.PARAMV_GROUPING_PLUG_ID) ||
                  filter.equals(Settings.PARAMV_GROUPING_DOMAIN))) {
                setDefaultViewPage(TEMPLATE_RESULT_FILTERED_ONECOLUMN);
                 // only one column to render we switch off grouping_right to show ungrouped navigation !
                context.put("grouping_right", false);
             }
        }

        // ----------------------------------
        // business logic
        // ----------------------------------

        // create threaded query controller
        ThreadedQueryController controller = new ThreadedQueryController();
        controller.setTimeout(PortalConfig.getInstance().getInt(PortalConfig.QUERY_TIMEOUT_THREADED, 120000));

        QueryDescriptor qd = null;

        // RANKED
        IngridHitsWrapper rankedHits = null;
        // check if query must be executed
        if (queryType.equals(Settings.MSGV_NO_QUERY)) {
            rankedHits = (IngridHitsWrapper) SearchState.getSearchStateObject(request, Settings.MSG_SEARCH_RESULT_RANKED);
            if (log.isDebugEnabled()) {
                log.debug("Read RANKED hits from CACHE !!! rankedHits=" + rankedHits);
            }
        } else {
            // process query, create QueryDescriptor
            qd = QueryPreProcessor.createRankedQueryDescriptor(request, messages);
            if (qd != null) {
                query = qd.getQuery();
                if(request.getParameter("rank") != null) {
                    query.put(IngridQuery.RANKED, request.getParameter("rank"));
                }
                controller.addQuery("ranked", qd);
                SearchState.resetSearchStateObject(request, Settings.MSG_SEARCH_FINISHED_RANKED);
            }
        }

        // Grouping may have changed !
        String grouping = (String) SearchState.getSearchStateObject(request, Settings.PARAM_GROUPING);

        // fire query, post process results
        boolean rankedColumnHasMoreGroupedPages = true;
        if (controller.hasQueries()) {
            // fire queries
            Map<Object, Object> results = controller.search();
            
            // check for zero results
            // log the result to the resource logger
            Iterator<Object> it = results.keySet().iterator();
            boolean noResults = true;
            String queryTypes = "";
            while (it.hasNext()) {
                Object key = it.next();
                if (queryTypes.length() > 0) {
                    queryTypes = queryTypes.concat(",");
                } else {
                    queryTypes = queryTypes.concat(key.toString());
                }
                IngridHitsWrapper hits = (IngridHitsWrapper)results.get(key);
                if (hits != null && hits.length() > 0) {
                    noResults = false;
                    break;
                }
            }
            if (noResults) {
                
                String url = PortalConfig.getInstance().getString(PortalConfig.PORTAL_LOGGER_RESOURCE);
                if (url != null) {
                    String queryString = SearchState.getSearchStateObjectAsString(request, Settings.PARAM_QUERY_STRING);
                    HttpMethod method = null;
                     try{
                         url = url.concat("?code=NO_RESULTS_FOR_QUERY&q=").concat(URLEncoder.encode(queryString, "UTF-8")).concat("&qtypes=").concat(URLEncoder.encode(queryTypes, "UTF-8"));
                         method = new GetMethod(url);
                         method.setFollowRedirects(true);
                         client.executeMethod(method);
                     } catch (Exception t) {
                         if (log.isErrorEnabled()) {
                             log.error("Cannot make connection to logger resource: ".concat(url), t);
                         }
                     } finally {
                         if (method != null) {
                             try{
                                 method.releaseConnection();
                             } catch (Exception t) {
                                 if (log.isErrorEnabled()) {
                                     log.error("Cannot close connection to logger resource: ".concat(url), t);
                                 }
                             }
                         }
                     }
                }
            }
            
            
            // post process ranked hits if exists
            if (results.containsKey("ranked")) {
                rankedHits = QueryResultPostProcessor.processRankedHits((IngridHitsWrapper) results.get("ranked"));
                SearchState.adaptSearchState(request, Settings.MSG_SEARCH_RESULT_RANKED, rankedHits);
                SearchState.adaptSearchState(request, Settings.MSG_SEARCH_FINISHED_RANKED, Settings.MSGV_TRUE);

                // GROUPING ONLY !!!
                if (grouping != null && !grouping.equals(IngridQuery.GROUPED_OFF)) {
                    // get the grouping starthits history from session
                    // create and initialize if not exists
                     // NOTICE: when grouping by domain the navigation is like ungrouped navigation, so multiple pages are rendered !
                    try {
                        @SuppressWarnings("unchecked")
                        List<Object> groupedStartHits = 
                             (List<Object>) SearchState.getSearchStateObject(request, Settings.PARAM_GROUPING_STARTHITS);
                        if (groupedStartHits == null) {
                            groupedStartHits = new ArrayList<>();
                            SearchState.adaptSearchState(request, Settings.PARAM_GROUPING_STARTHITS, groupedStartHits);
                        }
                        // set starthit of NEXT page ! ensure correct size of Array ! Notice: currentSelectorPage is 1 for first page !
                         while (currentSelectorPage >= groupedStartHits.size()) {
                              groupedStartHits.add(0);
                         }
                        // set start hit for next page (grouping)
                         int nextStartHit = rankedHits.getGoupedHitsLength();
                        groupedStartHits.set(currentSelectorPage, nextStartHit);

                        // check whether there are more pages for grouped hits ! this is done due to former Bug in Backend !
                        // still necessary ? well, these former checks don't damage anything ...
                        if (rankedHits.length() <= rankedHits.getGoupedHitsLength()) {
                            // total number of hits (ungrouped) already processed -> no more pages
                             rankedColumnHasMoreGroupedPages = false; 
                        } else {
                             int currentStartHit = ((Integer) groupedStartHits.get(currentSelectorPage - 1)).intValue();
                             if (nextStartHit == currentStartHit) {
                                // start hit for next page same as start hit for current page -> no more pages
                                 rankedColumnHasMoreGroupedPages = false;
                             }
                        }
                    } catch (Exception ex) {
                        if (log.isInfoEnabled()) {
                            log.info("Problems processing grouping starthits RANKED", ex);
                        }
                    }
                }

            }
        }

        int totalNumberOfRankedHits = 0;
        if (rankedHits != null) {
            totalNumberOfRankedHits = (int) rankedHits.length();
        }
        // adapt settings of ranked page navigation
        Map<String, Object> rankedPageNavigation = UtilsSearch.getPageNavigation(rankedStartHit,
                Settings.SEARCH_RANKED_HITS_PER_PAGE, totalNumberOfRankedHits,
                Settings.SEARCH_RANKED_NUM_PAGES_TO_SELECT);

        Object rankedSearchFinished = SearchState.getSearchStateObject(request, Settings.MSG_SEARCH_FINISHED_RANKED);

        // GROUPING
        // adapt page navigation for grouping in left column 
        if (grouping != null && !grouping.equals(IngridQuery.GROUPED_OFF)) {
             UtilsSearch.adaptRankedPageNavigationToGrouping(
                     rankedPageNavigation, currentSelectorPage, rankedColumnHasMoreGroupedPages, totalNumberOfRankedHits, request);

            if (grouping.equals(IngridQuery.GROUPED_BY_PARTNER)) {
                context.put("grouping", "partner");
            } else if (grouping.equals(IngridQuery.GROUPED_BY_ORGANISATION)) {
                context.put("grouping", "provider");
            } else {
                context.put("grouping", "domain");
            }
        }

        boolean showAdminContent = false;
        if (request.getUserPrincipal() != null) {
             showAdminContent = request.getUserPrincipal().getName().equals("admin");
        }
        String ranking = request.getParameter(Settings.PARAM_RANKING);
        if(UtilsSearch.containsFieldOrKey(query, IngridQuery.RANKED)) {
            ranking = query.getRankingType();
        } else if (qd != null && qd.getQuery() != null && UtilsSearch.containsFieldOrKey(qd.getQuery(), IngridQuery.RANKED)) {
            ranking = qd.getQuery().getRankingType();
        }
        context.put("ranking", ranking);
        context.put("ds", request.getParameter("ds"));
        context.put("adminContent", showAdminContent);
        context.put("rankedPageSelector", rankedPageNavigation);
        if(rankedHits != null && PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_ENABLE_SEARCH_FACETE, false)){
            UtilsFacete.checkForExistingFacete((IngridHitsWrapper) rankedHits, request);
            UtilsFacete.setParamsToContext(request, context, messages);
        }
        context.put("rankedResultList", rankedHits);
        context.put("rankedSearchFinished", rankedSearchFinished);
        context.put( "UTIL_SEARCH", UtilsSearch.class );
        
        restUrl = response.createResourceURL();
        restUrl.setResourceID( "httpURLSearchDownload" );
        request.setAttribute( "restUrlHttpSearchDownload", restUrl.toString() );
        //context.put("providerMap", getProviderMap());

        if(PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_SEARCH_FACETE_MULTISELECTION, false)) {
            restUrl.setResourceID( "facetValue" );
            request.setAttribute( "restUrlFacetValue", restUrl.toString() );
            boolean displayEmptyFacets = PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_SEARCH_FACETE_MULTISELECTION_DISPLAY_EMPTY, false);
            context.put("displayEmptyFacets", displayEmptyFacets);
        }

        super.doView(request, response);
    }

    /**
     * Get provider to be able to map them from their abbreviation. 
     * @return a map of short:long values of the provider
     */
    /*private HashMap<String, String> getProviderMap() {
        HashMap<String, String> providerMap = new HashMap<>();
        List<IngridProvider> providers = UtilsDB.getProviders();
        
        for (IngridProvider ingridProvider : providers) {
            providerMap.put(ingridProvider.getIdent(), ingridProvider.getName());
        }
        return providerMap;
    }*/

    @Override
    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {
        // check whether page navigation was clicked and send according message (Search state)
         PortletSession ps = request.getPortletSession();
         IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                 request.getLocale()), request.getLocale());

        // NO GROUPING
        String rankedStarthit = request.getParameter(Settings.PARAM_STARTHIT_RANKED);
        if (rankedStarthit != null) {
            SearchState.adaptSearchState(request, Settings.MSG_QUERY_EXECUTION_TYPE, Settings.MSGV_RANKED_QUERY);
            SearchState.adaptSearchState(request, Settings.PARAM_STARTHIT_RANKED, rankedStarthit);
        }

        // GROUPING
        // currentSelectorPage is set, send according message (Search state)
        String currentSelectorPage = request.getParameter(Settings.PARAM_CURRENT_SELECTOR_PAGE);
        if (currentSelectorPage != null) {
            SearchState.adaptSearchState(request, Settings.MSG_QUERY_EXECUTION_TYPE, Settings.MSGV_RANKED_QUERY);
            SearchState.adaptSearchState(request, Settings.PARAM_CURRENT_SELECTOR_PAGE, currentSelectorPage);
            ps.setAttribute("isPagingRanked", currentSelectorPage);
        }

        // adapt filter params, set state only if we do have a subject
        // avoid reset the states while browsing the resultpages
        if (request.getParameter(Settings.PARAM_SUBJECT) != null) {
            SearchState.adaptSearchState(request, Settings.PARAM_SUBJECT, request.getParameter(Settings.PARAM_SUBJECT));
            SearchState.adaptSearchState(request, Settings.PARAM_FILTER, request.getParameter(Settings.PARAM_GROUPING));
        }
        
        String url = "";
        
        if(PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_ENABLE_SEARCH_FACETE, false)){
            String queryType = (String) SearchState.getSearchStateObject(request, Settings.MSG_QUERY_EXECUTION_TYPE);
            if (queryType != null && queryType.equals(Settings.MSGV_NO_QUERY)) {
                SearchState.adaptSearchState(request, Settings.MSG_QUERY_EXECUTION_TYPE, Settings.MSGV_RANKED_QUERY);
            }
            url = UtilsFacete.setFaceteParamsToSessionByAction(request, messages);
        }
        
        String doRanking = request.getParameter("ranking");
        if(doRanking != null) {
            SearchState.adaptSearchState(request, Settings.PARAM_RANKING, doRanking);
        }
        
        boolean doRemoveFilter = Boolean.parseBoolean(request.getParameter("doRemoveFilter")); 
        // redirect to our page wih parameters for bookmarking
        if(doRemoveFilter){
             // reset filter and grouping and page selector
             SearchState.adaptSearchState(request, Settings.PARAM_GROUPING, "");
             SearchState.adaptSearchState(request, Settings.PARAM_SUBJECT, "");
             SearchState.adaptSearchState(request, Settings.PARAM_FILTER, "");
             SearchState.adaptSearchState(request, Settings.PARAM_CURRENT_SELECTOR_PAGE, 1);
             SearchState.adaptSearchState(request, Settings.PARAM_STARTHIT_RANKED, 0);
             actionResponse.sendRedirect(actionResponse.encodeURL(Settings.PAGE_SEARCH_RESULT + SearchState.getURLParamsMainSearch(request)) + ps.getAttribute("facetsURL"));
        }else{
             if(!url.equals(ps.getAttribute("facetsURL")) && (StringUtils.isNotEmpty(url) || (StringUtils.isEmpty(url) && ps.getAttribute("facetsURL") != null && ps.getAttribute("facetsURL") != ""))){
                 ps.setAttribute("facetsURL", url);
                // reset page and page selector by facets activity
                SearchState.adaptSearchState(request, Settings.PARAM_GROUPING, "");
                SearchState.adaptSearchState(request, Settings.PARAM_CURRENT_SELECTOR_PAGE, 1);
                SearchState.adaptSearchState(request, Settings.PARAM_STARTHIT_RANKED, rankedStarthit != null ? rankedStarthit : 0);
             }
             actionResponse.sendRedirect(actionResponse.encodeURL(Settings.PAGE_SEARCH_RESULT + SearchState.getURLParamsMainSearch(request)) + url);
        }
    }
}
