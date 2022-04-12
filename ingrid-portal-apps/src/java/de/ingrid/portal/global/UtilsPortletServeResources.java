/*-
 * **************************************************-
 * InGrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2022 wemove digital solutions GmbH
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
package de.ingrid.portal.global;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.validator.UrlValidator;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.interfaces.IBUSInterface;
import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;
import de.ingrid.portal.om.IngridFacet;
import de.ingrid.portal.search.SearchState;
import de.ingrid.portal.search.UtilsSearch;
import de.ingrid.portal.search.net.IBusQueryResultIterator;
import de.ingrid.utils.IngridDocument;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.query.ClauseQuery;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.ParseException;
import de.ingrid.utils.queryparser.QueryStringParser;
import de.ingrid.utils.udk.iso19108.TM_PeriodDuration;
import de.ingrid.utils.udk.iso19108.TM_PeriodDuration.Interval;

public class UtilsPortletServeResources {

    private static final Logger log = LoggerFactory.getLogger(UtilsPortletServeResources.class);

    public static final String[] REQUESTED_FIELDS_UVP_MARKER               = PortalConfig.getInstance().getStringArray(PortalConfig.PORTAL_UVP_MAP_REQUESTED_FIELDS_MARKER);
    public static final String[] REQUESTED_FIELDS_UVP_MARKER_DETAIL        = PortalConfig.getInstance().getStringArray(PortalConfig.PORTAL_UVP_MAP_REQUESTED_FIELDS_MARKER_DETAIL);
    public static final String[] REQUESTED_FIELDS_UVP_BBOX                 = PortalConfig.getInstance().getStringArray(PortalConfig.PORTAL_UVP_MAP_REQUESTED_FIELDS_MARKER_BBOX);
    public static final String[] REQUESTED_FIELDS_UVP_BLP_MARKER           = PortalConfig.getInstance().getStringArray(PortalConfig.PORTAL_UVP_MAP_REQUESTED_FIELDS_BLP_MARKER);
    public static final String[] REQUESTED_FIELDS_UVP_BLP_MARKER_DETAIL    = PortalConfig.getInstance().getStringArray(PortalConfig.PORTAL_UVP_MAP_REQUESTED_FIELDS_BLP_MARKER_DETAIL);
    public static final int REQUESTED_FIELDS_UVP_MARKER_NUM                = PortalConfig.getInstance().getInt(PortalConfig.PORTAL_UVP_MAP_REQUESTED_FIELDS_MARKER_NUM);

    public static final String[] REQUESTED_FIELDS_BAW_DMQS_MARKER = new String[] { "bwstr-center-lon", "bwstr-center-lat", "t01_object.obj_id", "bawAuftragsnummer", "simProcess", "simModelType" };
    public static final String[] REQUESTED_FIELDS_BAW_DMQS_BBOX = new String[] { "x1", "x2", "y1", "y2", "t01_object.obj_id" };

    public static void getHttpUrlDatatype(String paramURL, String login, String password, ResourceResponse response) throws IOException {
        String extension = "";

        if(paramURL != null) {
            if(paramURL.toLowerCase().indexOf("service=csw") > -1) {
                extension = "csw";
            } else if(paramURL.toLowerCase().indexOf("service=wms") > -1) {
                extension = "wms";
            } else if(paramURL.toLowerCase().indexOf("service=wfs") > -1) {
                extension = "wfs";
            } else if(paramURL.toLowerCase().indexOf("service=wmts") > -1) {
                extension = "wmts";
            }
            if(extension.isEmpty()) {
                try {
                    Map<String, Object> requestHeader = UtilsHttpConnection.urlConnectionHead (paramURL, login, password);
                    if(!requestHeader.isEmpty()) {
                        String contentType = (String) requestHeader.get(UtilsHttpConnection.HEADER_CONTENT_TYPE);
                        if(contentType != null) {
                            extension = UtilsMimeType.getFileExtensionOfMimeType(contentType.split(";")[0]);
                        }
                    }
                } catch (Exception e) {
                    UtilsLog.logError("Error get datatype from: " + paramURL, e, log);
                }
            }
        }
        response.setContentType( "text/plain" );
        response.getWriter().write( extension );
    }

    public static void getHttpUrlLength(String paramURL, String login, String password, ResourceResponse response ) throws IOException {
        try {
            Map<String, Object> requestHeader = UtilsHttpConnection.urlConnectionHead (paramURL, login, password);
            StringBuilder s = new StringBuilder();
            response.setContentType( "application/javascript" );
            response.getWriter().write( "{" );
            if(!requestHeader.isEmpty()) {
                int length = (int) requestHeader.get(UtilsHttpConnection.HEADER_CONTENT_LENGTH);
                if(length != -1) {
                    s.append( "\"contentLength\":");
                    s.append( "\"" + requestHeader.get(UtilsHttpConnection.HEADER_CONTENT_LENGTH) + "\"" );
                    response.getWriter().write( s.toString() );
                }
            }
            response.getWriter().write( "}" );
        } catch (Exception e) {
            UtilsLog.logError("Error get contentLength from: " + paramURL, e, log);
            response.setContentType( "application/javascript" );
            response.getWriter().write( "{}" );
        }
    }

    public static void getHttpUrlImage(String paramURL, ResourceResponse response, String resourceID) throws IOException {
        try {
            getURLResponse(paramURL, response);
        } catch (Exception e) {
            UtilsLog.logError("Error creating HTTP resource for resource ID: " + resourceID, e, log);
            String httpsUrl = paramURL.replace("http", "https").replace(":80/", "/");
            log.error( "Try https URL: " + httpsUrl);
            try {
                getURLResponse(httpsUrl, response);
            } catch (Exception e1) {
                UtilsLog.logError("Error creating HTTPS resource for resource ID: " + resourceID, e, log);
                response.getWriter().write(paramURL);
            }
        }
    }

    public static void getHttpMarkerUVP(ResourceResponse response, String[] requestedFields, ResourceRequest request, String queryString, IngridResourceBundle messages, IngridSysCodeList sysCodeList, List<IngridFacet> config) throws ParseException, IOException {
        String portalQueryString = UtilsSearch.updateQueryString("", request);
        IngridQuery query = QueryStringParser.parse( portalQueryString );

        if(queryString != null && queryString.trim().length() > 0){
            // Change query to clause query (with phrase search)
            UtilsSearch.changeQueryToClauseQuery(query);

            String addToQuery = queryString;
            if(addToQuery != null && addToQuery.length() > 0){
                IngridQuery addQuery = QueryStringParser.parse(addToQuery);
                ClauseQuery cp = UtilsSearch.createClauseQuery(addQuery, true, false);
                query.addClause(cp);
            }
        }
        if(config != null) {
            query = UtilsFacete.getQueryFacets(request, config, query);
        }
        int startPage = 0;
        if(request.getParameter("startPage") != null) {
            startPage = Integer.parseInt(request.getParameter("startPage"));
        }
        String marker = request.getParameter("marker");
        String markerColor = request.getParameter("markerColor");
        IBusQueryResultIterator it = new IBusQueryResultIterator( query, requestedFields, IBUSInterfaceImpl.getInstance()
                .getIBus(), REQUESTED_FIELDS_UVP_MARKER_NUM, startPage, REQUESTED_FIELDS_UVP_MARKER_NUM);
        response.setContentType( "application/javascript" );
        if(it != null){
            while (it.hasNext()) {
                try {
                    IngridHit hit = it.next();
                    IngridHitDetail detail = hit.getHitDetail();
                    JSONArray jsonDataEntry = new JSONArray();
                    jsonDataEntry.put(Double.parseDouble(UtilsSearch.getDetailValue( detail, "lat_center", 1)));
                    jsonDataEntry.put(Double.parseDouble(UtilsSearch.getDetailValue( detail, "lon_center", 1)));
                    jsonDataEntry.put(UtilsSearch.getDetailValue( detail, "title" ));
                    jsonDataEntry.put(UtilsSearch.getDetailValue( detail, "t01_object.obj_id" ));
                    jsonDataEntry.put(UtilsSearch.getDetailValue( detail, "t01_object.obj_class" ));
                    jsonDataEntry.put(sysCodeList.getName( "8001", UtilsSearch.getDetailValue( detail, "t01_object.obj_class" )));
                    ArrayList<String> categories = getIndexValue(detail.get( "uvp_category" ));
                    JSONArray jsonCategories = new JSONArray();
                    if(categories != null && !categories.isEmpty()){
                        for (String category : categories) {
                            JSONObject obj = new JSONObject();
                            obj.put( "id", category.trim());
                            obj.put( "name", messages.getString("searchResult.categories.uvp." + category.trim()));
                            jsonCategories.put(obj);
                        }
                    }
                    jsonDataEntry.put(jsonCategories);
                    ArrayList<String> steps = getIndexValue(detail.get( "uvp_steps" ));
                    JSONArray jsonSteps = new JSONArray();
                    if(steps != null && !steps.isEmpty()){
                        for (String step : steps) {
                            jsonSteps.put( messages.getString( "common.steps.uvp." + step.trim()));
                        }
                    }
                    jsonDataEntry.put(jsonSteps);
                    response.getWriter().write("createMarker(" + marker + ", " + jsonDataEntry.toString() + ", '" + markerColor + "');");
                } catch (Exception e) {
                    UtilsLog.logError("Error write response.", e, log);
                }
            }
        }
    }

    public static void getHttpMarkerUVPWithNumber(ResourceRequest request, ResourceResponse response, String[] requestedFields, String queryString, IngridResourceBundle messages, IngridSysCodeList sysCodeList, int pageSize) throws ParseException, IOException {
        getHttpMarkerUVPWithNumber(request, response, requestedFields, queryString, messages, sysCodeList, pageSize, null, null, null); 
    }

    public static void getHttpMarkerUVPWithNumber(ResourceRequest request, ResourceResponse response, String[] requestedFields, String queryString, IngridResourceBundle messages, IngridSysCodeList sysCodeList, int pageSize, String from, String to, String additionalQuery) throws ParseException, IOException {
        if(from != null && to != null && additionalQuery != null) {
            queryString = getTimeQuery(queryString, from, to, additionalQuery);
        }
        int startPage = 0;
        if(request.getParameter("startPage") != null) {
            startPage = Integer.parseInt(request.getParameter("startPage"));
        }
        String marker = request.getParameter("marker");
        String markerColor = request.getParameter("markerColor");
        IBusQueryResultIterator it = new IBusQueryResultIterator( QueryStringParser.parse( queryString ), requestedFields, IBUSInterfaceImpl.getInstance()
                .getIBus(), REQUESTED_FIELDS_UVP_MARKER_NUM, startPage, REQUESTED_FIELDS_UVP_MARKER_NUM);
        response.setContentType( "application/javascript" );
        if(it != null){
            while (it.hasNext()) {
                IngridHit hit = it.next();
                IngridHitDetail detail = hit.getHitDetail();
                try {
                    String lat = UtilsSearch.getDetailValue( detail, "lat_center", 1);
                    String lng = UtilsSearch.getDetailValue( detail, "lon_center", 1);
                    String title = UtilsSearch.getDetailValue( detail, "title" );
                    String uuid = UtilsSearch.getDetailValue( detail, "t01_object.obj_id" );
                    String iplug = hit.getPlugId();
                    if(!lat.isEmpty() && !lng.isEmpty()) {
                        JSONArray jsonDataEntry = new JSONArray();
                        jsonDataEntry.put(Double.parseDouble(lat));
                        jsonDataEntry.put(Double.parseDouble(lng));
                        jsonDataEntry.put(title);
                        jsonDataEntry.put(uuid);
                        jsonDataEntry.put(iplug);
                        response.getWriter().write("createMarker(" + marker + ", " + jsonDataEntry.toString() + ", '" + markerColor + "');");
                    } else {
                        log.error("Metadata '" + title + "' with UUID '" + uuid + "' has no location!");
                    }
                } catch (Exception e) {
                    UtilsLog.logError("Error write response.", e, log);
                }
            }
        }
    }

    public static void getHttpMarkerUVPDetail(ResourceResponse response, String[] requestedFields, String queryString, IngridResourceBundle messages, IngridSysCodeList sysCodeList) throws ParseException, IOException {
        IBusQueryResultIterator it = new IBusQueryResultIterator( QueryStringParser.parse( queryString ), requestedFields, IBUSInterfaceImpl.getInstance()
                .getIBus(), 1, 0, 1);
        JSONArray jsonData = new JSONArray();
        if(it != null){
            while (it.hasNext()) {
                IngridHit hit = it.next();
                IngridHitDetail detail = hit.getHitDetail();
                try {
                    jsonData.put(sysCodeList.getName( "8001", UtilsSearch.getDetailValue( detail, "t01_object.obj_class" )));
                    ArrayList<String> categories = getIndexValue(detail.get( "uvp_category" ));
                    JSONArray jsonCategories = new JSONArray();
                    if(categories != null && !categories.isEmpty()){
                        for (String category : categories) {
                            JSONObject obj = new JSONObject();
                            obj.put( "id", category.trim());
                            obj.put( "name", messages.getString("searchResult.categories.uvp." + category.trim()));
                           jsonCategories.put(obj);
                        }
                    }
                    jsonData.put(jsonCategories);
                    ArrayList<String> steps = getIndexValue(detail.get( "uvp_steps" ));
                    JSONArray jsonSteps = new JSONArray();
                    if(steps != null && !steps.isEmpty()){
                        for (String step : steps) {
                            jsonSteps.put( messages.getString( "common.steps.uvp." + step.trim()));
                        }
                    }
                    jsonData.put(jsonSteps);
                } catch (Exception e) {
                    UtilsLog.logError("Error write response.", e, log);
                }
            }
        }
        response.setContentType( "application/json" );
        response.getWriter().write(jsonData.toString());
    }

    public static void getHttpMarkerUVPBoundingBox (ResourceResponse response, String queryString) throws IOException, NumberFormatException, JSONException, ParseException {
        JSONArray bbox = new JSONArray();
        IBusQueryResultIterator it = new IBusQueryResultIterator( QueryStringParser.parse(queryString), REQUESTED_FIELDS_UVP_BBOX,
                IBUSInterfaceImpl.getInstance().getIBus(), 1, 0, 1 );
        if(it.hasNext()){
            IngridHit hit = it.next();
            IngridHitDetail detail = hit.getHitDetail();
            JSONArray bboxItems = new JSONArray();
            bboxItems.put(Double.parseDouble(UtilsSearch.getDetailValue( detail, "y1", 1)));
            bboxItems.put(Double.parseDouble(UtilsSearch.getDetailValue( detail, "x1", 1)));
            bbox.put(bboxItems);
            bboxItems = new JSONArray();
            bboxItems.put(Double.parseDouble(UtilsSearch.getDetailValue( detail, "y2", 1)));
            bboxItems.put(Double.parseDouble(UtilsSearch.getDetailValue( detail, "x2", 1)));
            bbox.put(bboxItems);
        }
        response.setContentType( "application/javascript" );
        response.getWriter().write( bbox.toString() );
    }

    public static void getHttpMarkerUVPMarkerBlp (ResourceRequest request, ResourceResponse response, String queryString, List<IngridFacet> config) throws IOException, NumberFormatException, JSONException, ParseException {
        IngridQuery query = null;
        if(config != null) {
            String portalQueryString = UtilsSearch.updateQueryString("", request);
            query = QueryStringParser.parse( portalQueryString );

            if(queryString != null && queryString.trim().length() > 0){
                // Change query to clause query (with phrase search)
                UtilsSearch.changeQueryToClauseQuery(query);
    
                String addToQuery = queryString;
                if(addToQuery != null && addToQuery.length() > 0){
                    IngridQuery addQuery = QueryStringParser.parse(addToQuery);
                    ClauseQuery cp = UtilsSearch.createClauseQuery(addQuery, true, false);
                    query.addClause(cp);
                }
            }
        } else {
            query = QueryStringParser.parse( queryString );
        }
        if(config != null) {
            query = UtilsFacete.getQueryFacets(request, config, query);
        }
        int startPage = 0;
        if(request.getParameter("startPage") != null) {
            startPage = Integer.parseInt(request.getParameter("startPage"));
        }
        String marker = request.getParameter("marker");
        String markerColor = request.getParameter("markerColor");
        IBusQueryResultIterator it = new IBusQueryResultIterator( query , REQUESTED_FIELDS_UVP_BLP_MARKER, IBUSInterfaceImpl.getInstance()
                .getIBus(), REQUESTED_FIELDS_UVP_MARKER_NUM, startPage, REQUESTED_FIELDS_UVP_MARKER_NUM);
        int cnt = 1;
        response.setContentType( "application/javascript" );
        while (it.hasNext()) {
            try {
                IngridHit hit = it.next();
                IngridHitDetail detail = hit.getHitDetail();
                if (log.isDebugEnabled()) {
                    log.debug("Got BLP result: " + detail.toString());
                }
                String latCenter = UtilsSearch.getDetailValue( detail, "y1", 1);
                String lonCenter = UtilsSearch.getDetailValue( detail, "x1", 1);
                String blpName = UtilsSearch.getDetailValue( detail, "blp_name" );
                if(!latCenter.isEmpty() && !lonCenter.isEmpty()) {
                    JSONObject jsonDataEntry = new JSONObject();
                    jsonDataEntry.put("id", cnt);
                    jsonDataEntry.put("name", blpName);
                    jsonDataEntry.put("lat", Double.parseDouble( latCenter.trim() ));
                    jsonDataEntry.put("lon", Double.parseDouble( lonCenter.trim() ));
                    jsonDataEntry.put("iplug", hit.getPlugId());
                    response.getWriter().write("createMarkerBLP(" + marker + ", " + jsonDataEntry.toString() + ", '" + markerColor + "');");
                    cnt++;
                } else {
                    log.error("Metadata '" + blpName + "' has no location!");
                }
            } catch (Exception e) {
                UtilsLog.logError("Error get json object.", e, log);
            }
        }
    }

    public static void getHttpMarkerUVPMarkerBlpDetail (ResourceResponse response, String queryString) throws IOException, NumberFormatException, JSONException, ParseException {
        IBusQueryResultIterator it = new IBusQueryResultIterator( QueryStringParser.parse(queryString) , REQUESTED_FIELDS_UVP_BLP_MARKER_DETAIL, IBUSInterfaceImpl.getInstance()
                .getIBus(), 1, 0, 1 );
        int cnt = 1;
        JSONObject jsonData = new JSONObject();
        while (it.hasNext()) {
            try {
                IngridHit hit = it.next();
                IngridHitDetail detail = hit.getHitDetail();
                if (log.isDebugEnabled()) {
                    log.debug("Got BLP result: " + detail.toString());
                }
                String blpDescription = UtilsSearch.getDetailValue( detail, "blp_description" );
                // General "Bauleitplanung"
                String urlFinished = UtilsSearch.getDetailValue( detail, "blp_url_finished" );
                String urlInProgress = UtilsSearch.getDetailValue( detail, "blp_url_in_progress" );
                // Flächennutzungsplan
                String urlFnpFinished = UtilsSearch.getDetailValue( detail, "fnp_url_finished" );
                String urlFnpInProgress = UtilsSearch.getDetailValue( detail, "fnp_url_in_progress" );
                // Bebauungsplan
                String urlBpFinished = UtilsSearch.getDetailValue( detail, "bp_url_finished" );
                String urlBpInProgress = UtilsSearch.getDetailValue( detail, "bp_url_in_progress" );
                jsonData.put("id", cnt);
                JSONArray bpInfos = new JSONArray();
                // Bauleitplaung
                if (urlInProgress != null && !urlInProgress.isEmpty()) {
                    bpInfos.put( new JSONObject().put( "url", urlInProgress ).put( "tags", "p" ) );
                }
                if (urlFinished != null && !urlFinished.isEmpty()) {
                    bpInfos.put( new JSONObject().put( "url", urlFinished ).put( "tags", "v" ) );
                }
                // Flächennutzungsplanung
                if (urlFnpInProgress != null && !urlFnpInProgress.isEmpty()) {
                    bpInfos.put( new JSONObject().put( "url", urlFnpInProgress ).put( "tags", "p_fnp" ) );
                }
                if (urlFnpFinished != null && !urlFnpFinished.isEmpty()) {
                    bpInfos.put( new JSONObject().put( "url", urlFnpFinished ).put( "tags", "v_fnp" ) );
                }
                // Bebauungsplanung
                if (urlBpInProgress != null && !urlBpInProgress.isEmpty()) {
                    bpInfos.put( new JSONObject().put( "url", urlBpInProgress ).put( "tags", "p_bp" ) );
                }
                if (urlBpFinished != null && !urlBpFinished.isEmpty()) {
                    bpInfos.put( new JSONObject().put( "url", urlBpFinished ).put( "tags", "v_bp" ) );
                }
                jsonData.put("bpinfos", bpInfos);
                if (blpDescription != null && !blpDescription.isEmpty()) {
                    jsonData.put( "descr", blpDescription);
                }
                cnt++;
            } catch (Exception e) {
                UtilsLog.logError("Error get json object.", e, log);
            }
        }
        response.setContentType( "application/json" );
        response.getWriter().write( jsonData.toString());
    }

    public static void getHttpMarkerUVPLegendCounter (ResourceRequest request, ResourceResponse response, String queryString, List<IngridFacet> config) throws IOException, NumberFormatException, JSONException, ParseException {
        IngridQuery query = null;
        if(config != null) {
            String portalQueryString = UtilsSearch.updateQueryString("", request);
            query = QueryStringParser.parse( portalQueryString );

            if(queryString != null && queryString.trim().length() > 0){
                // Change query to clause query (with phrase search)
                UtilsSearch.changeQueryToClauseQuery(query);
    
                String addToQuery = queryString;
                if(addToQuery != null && addToQuery.length() > 0){
                    IngridQuery addQuery = QueryStringParser.parse(addToQuery);
                    ClauseQuery cp = UtilsSearch.createClauseQuery(addQuery, true, false);
                    query.addClause(cp);
                }
            }
        } else {
            query = QueryStringParser.parse( queryString );
        }
        if(config != null) {
            query = UtilsFacete.getQueryFacets(request, config, query);
        }
        String stateRanking = (String) SearchState.getSearchStateObject(request, Settings.PARAM_RANKING);
        if(stateRanking == null) {
            if(config != null) {
                stateRanking = "date";
            } else {
                stateRanking = "score";
            }
        }
        if (query.get( "FACETS" ) == null) {
            ArrayList<IngridDocument> facetQueries = new ArrayList<>();
            ArrayList<HashMap<String, String>> facetList = new ArrayList<>();
            String tmpQuery = PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_QUERY, "");
            if (!tmpQuery.isEmpty()) {
                HashMap<String, String> facetEntry = new HashMap<>();
                facetEntry.put("id", "countMarker1");
                facetEntry.put("query", tmpQuery);
                facetList.add(facetEntry);
            }
            tmpQuery = PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_QUERY_2, "");
            if (!tmpQuery.isEmpty()) {
                HashMap<String, String> facetEntry = new HashMap<>();
                facetEntry.put("id", "countMarker2");
                facetEntry.put("query", tmpQuery);
                facetList.add(facetEntry);
            }
            tmpQuery = PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_QUERY_3, "");
            if (!tmpQuery.isEmpty()) {
                HashMap<String, String> facetEntry = new HashMap<>();
                facetEntry.put("id", "countMarker3");
                facetEntry.put("query", tmpQuery);
                facetList.add(facetEntry);
            }
            tmpQuery = PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_QUERY_4, "");
            if (!tmpQuery.isEmpty()) {
                HashMap<String, String> facetEntry = new HashMap<>();
                facetEntry.put("id", "countMarker4");
                facetEntry.put("query", tmpQuery);
                facetList.add(facetEntry);
            }
            tmpQuery = PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_QUERY_5, "");
            if (!tmpQuery.isEmpty()) {
                HashMap<String, String> facetEntry = new HashMap<>();
                facetEntry.put("id", "countMarker5");
                facetEntry.put("query", tmpQuery);
                facetList.add(facetEntry);
            }
            tmpQuery = PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_UVP_CATEGORY_DEV_PLAN, "");
            if (!tmpQuery.isEmpty() && !stateRanking.equals("date")) {
                HashMap<String, String> facetEntry = new HashMap<>();
                facetEntry.put("id", "countMarkerDevPlan");
                facetEntry.put("query", tmpQuery);
                facetList.add(facetEntry);
            }
            if (!facetList.isEmpty()) {
                IngridDocument facet = new IngridDocument();
                facet.put("id", "legend_counter");
                facet.put("classes", facetList);
                facetQueries.add(facet);
            }
            if (!facetQueries.isEmpty()) {
                query.put( "FACETS", facetQueries );
            }
        }
        IngridHits hits;
        try {
            IBUSInterface ibus = IBUSInterfaceImpl.getInstance();
            hits = ibus.search( query, Settings.SEARCH_RANKED_HITS_PER_PAGE, 1, 0, PortalConfig.getInstance().getInt( PortalConfig.QUERY_TIMEOUT_RANKED, 5000 ) );
    
            if (hits == null) {
                if (log.isErrorEnabled()) {
                    log.error( "Problems fetching details to hit list !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
                }
            } else {
                if(hits.length() > 0) {
                    Map<String, Object> facets = (Map<String, Object>) hits.get("FACETS");
                    JSONObject obj = new JSONObject();
                    if(facets != null){
                        for (Iterator<String> iterator = facets.keySet().iterator(); iterator.hasNext();) {
                            String key = iterator.next();
                            Long value = (Long) facets.get(key);
                            obj.put(key.split(":")[1], value);
                        }
                    }
                    response.getWriter().write( "var legendCounter = " + obj.toString() +";");
                } else {
                    response.getWriter().write( "var legendCounter = {};");
                }
            }
        } catch (Exception e) {
            UtilsLog.logError("Problems performing Search !", e, log);
        }
    }

    public static void getHttpMarkerBawDmqsMarker (ResourceResponse response, String queryString) throws IOException, NumberFormatException, JSONException, ParseException {
        response.setContentType( "application/javascript" );
        response.getWriter().write( "var markers = [" );
        IBusQueryResultIterator it = new IBusQueryResultIterator( QueryStringParser.parse( queryString ), REQUESTED_FIELDS_BAW_DMQS_MARKER, IBUSInterfaceImpl.getInstance()
                .getIBus() );
        while (it.hasNext()) {
            StringBuilder s = new StringBuilder();
            IngridHit hit = it.next();
            IngridHitDetail detail = hit.getHitDetail();
            if (detail.containsKey( "bwstr-center-lat" ) && detail.containsKey( "bwstr-center-lon" )) {
                String lat = UtilsSearch.getDetailValue(detail, "bwstr-center-lat");
                String lon = UtilsSearch.getDetailValue(detail, "bwstr-center-lon");
                if (!"NaN".equals(lat) && !"NaN".equals(lon)) {
                    s.append("[")
                            //.append( detail.get( "bwstr-center-lat" ).toString() )
                            .append(lat)
                            .append(",")
                            //.append( detail.get( "bwstr-center-lon" ).toString() )
                            .append(lon)
                            .append(",'")
                            .append(detail.get("title").toString())
                            .append("','")
                            .append(UtilsSearch.getDetailValue(detail, "t01_object.obj_id"));
    
                    String bawAuftragsnummer = UtilsSearch.getDetailValue(detail, "bawAuftragsnummer");
                    s.append("','");
                    if (bawAuftragsnummer != null) {
                        s.append(bawAuftragsnummer);
                    } else {
                        s.append("");
                    }
                    String simProcess = UtilsSearch.getDetailValue(detail, "simProcess");
                    s.append("','");
                    if (simProcess != null) {
                        s.append(simProcess);
                    } else {
                        s.append("");
                    }
                    String simModelType = UtilsSearch.getDetailValue(detail, "simModelType");
                    s.append("','");
                    if (simModelType != null) {
                        s.append(simModelType);
                    } else {
                        s.append("");
                    }
                    s.append("']");
                    if (it.hasNext()) {
                        s.append(",");
                    }
                    response.getWriter().write(s.toString());
                }
            }
        }
        response.getWriter().write( "];" );
    }

    public static void getHttpMarkerBawDmqsBoundingBox (ResourceResponse response, String queryString) throws IOException, NumberFormatException, JSONException, ParseException {
        response.setContentType( "application/javascript" );
        response.getWriter().write( "[" );
        IBusQueryResultIterator it = new IBusQueryResultIterator( QueryStringParser.parse(queryString), REQUESTED_FIELDS_BAW_DMQS_BBOX,
                IBUSInterfaceImpl.getInstance().getIBus() );
        if (it.hasNext()) {
            StringBuilder s = new StringBuilder();
            IngridHit hit = it.next();
            IngridHitDetail detail = hit.getHitDetail();
            if (detail.containsKey( "y1" ) && detail.containsKey( "x2" )) {
                s.append("[").append( detail.get( "y1" ).toString() ).append( "," ).append( detail.get( "x1" ).toString() ).append( "],[" ).append( detail.get( "y2" ).toString() )
                        .append( "," ).append( detail.get( "x2" ).toString() ).append("]");
                response.getWriter().write( s.toString() );
            }
        }
        response.getWriter().write( "]" );
    }

    public static void getHttpMarkerBawDmqsBwaStr (ResourceResponse response, String id, String von, String bis, String epsg) throws IOException, NumberFormatException, JSONException, ParseException {
        if((von == null || von.isEmpty()) && (bis == null || bis.isEmpty())) {
            URL url = new URL(PortalConfig.getInstance().getString(PortalConfig.PORTAL_BWASTR_LOCATOR_INFO) + id);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            InputStream in = con.getInputStream();
            String encoding = con.getContentEncoding();
            encoding = encoding == null ? "UTF-8" : encoding;

            String tmpJson = IOUtils.toString( in, encoding );
            JSONObject questJson = new JSONObject( tmpJson );
            if(questJson.has("result")) {
                JSONArray questJsonArray = questJson.getJSONArray( "result" );
                for (int j = 0; j < questJsonArray.length(); j++) {
                    JSONObject questJsonEntry = questJsonArray.getJSONObject( j );
                    if(questJsonEntry.has("km_von") && questJsonEntry.has("km_bis")) {
                        von = questJsonEntry.getString( "km_von" );
                        bis = questJsonEntry.getString( "km_bis" );
                        break;
                    }
                }
            }
            
        }
        String responseString = "{}";
        if((von != null && !von.isEmpty()) && (bis != null && !bis.isEmpty())) {
            URL url = new URL(PortalConfig.getInstance().getString(PortalConfig.PORTAL_BWASTR_LOCATOR_GEOK));
            String content = "{" +
                "\"limit\":200," +
                "\"queries\":[" +
                "{" +
                "\"qid\":1," +
                "\"bwastrid\":\"" + id + "\"," +
                "\"stationierung\":{" + 
                "\"km_von\":" + von +
                "," +
                "\"km_bis\":" + bis +
                "}," +
                "\"spatialReference\":{" +
                "\"wkid\": " + epsg +
                "}" +
                "}" +
                "]" +
            "}";
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Content-Length", String.valueOf(content.length()));
            
            OutputStreamWriter writer = new OutputStreamWriter( con.getOutputStream() );
            writer.write(content);
            writer.flush();
            
            InputStream in = con.getInputStream();
            String encoding = con.getContentEncoding();
            encoding = encoding == null ? "UTF-8" : encoding;
            
            String json = IOUtils.toString(in, encoding);
            JSONObject jsonObj = new JSONObject(json);
            if(jsonObj.has("result")) {
                JSONArray questJsonArray = jsonObj.getJSONArray( "result" );
                if(questJsonArray != null && questJsonArray.length() > 0) {
                    responseString = questJsonArray.get(0).toString();
                }
            }
        }
        response.setContentType( "application/json" );
        response.getWriter().write( responseString );
    }

    private static void getURLResponse (String paramURL, ResourceResponse response) throws IOException, URISyntaxException {
        getURLResponse(paramURL, response, false);
    }

    private static void getURLResponse (String paramURL, ResourceResponse response, boolean hasRedirect) throws IOException, URISyntaxException {
        UrlValidator urlValidator = new UrlValidator();
        if(urlValidator.isValid(paramURL)) {
            URL url = new URL(paramURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            if(con.getContentType().indexOf("image/") > -1) {
                InputStream inStreamConvert = con.getInputStream();
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                if (null != con.getContentType()) {
                    byte[] chunk = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = inStreamConvert.read(chunk)) > 0) {
                        os.write(chunk, 0, bytesRead);
                    }
                    os.flush();
                    URI dataUri = new URI("data:" + con.getContentType() + ";base64," +
                            Base64.getEncoder().encodeToString(os.toByteArray()));
                    response.getWriter().write(dataUri.toString());
                }
            } else if (con.getHeaderField("Location") != null && !hasRedirect){
                String locationUrl = con.getHeaderField("Location");
                if(urlValidator.isValid(locationUrl)) {
                    getURLResponse(locationUrl, response, true);
                }
            }
        }
    }

    private static ArrayList<String> getIndexValue(Object obj){
        ArrayList<String> array = new ArrayList<>();
        if(obj != null) {
            if(obj instanceof String[]){
                String [] tmp = (String[]) obj;
                for (String s : tmp) {
                    array.add( s );
                }
            }else if(obj instanceof ArrayList){
                array = (ArrayList) obj;
            }else {
                array.add( obj.toString() );
            }
        }
        return array;
    }
    
    
    private static String getTimeQuery(String queryString, String from, String to, String additionalQuery) {
        SimpleDateFormat df = new SimpleDateFormat( "yyyyMMdd" );
        Calendar cal = new GregorianCalendar();
        TM_PeriodDuration pdTo = TM_PeriodDuration.parse( to );
        if(pdTo != null) {
            if (pdTo.getValue( Interval.DAYS ) != null && pdTo.getValue( Interval.DAYS ).length() > 0) {
                cal.add( Calendar.DAY_OF_MONTH, Integer.parseInt( pdTo.getValue( Interval.DAYS ) ) * (-1) );
            }
            
            if (pdTo.getValue( Interval.MONTHS ) != null && pdTo.getValue( Interval.MONTHS ).length() > 0) {
                cal.add( Calendar.MONTH, Integer.parseInt( pdTo.getValue( Interval.MONTHS ) ) * (-1) );
            }
            
            if (pdTo.getValue( Interval.YEARS ) != null && pdTo.getValue( Interval.YEARS ).length() > 0) {
                cal.add( Calendar.YEAR, Integer.parseInt( pdTo.getValue( Interval.YEARS ) ) * (-1) );
            }
        }
        to = df.format( cal.getTime() );
        TM_PeriodDuration pdFrom = TM_PeriodDuration.parse( from );
        if(pdFrom != null) {
            if (pdFrom.getValue( Interval.DAYS ) != null && pdFrom.getValue( Interval.DAYS ).length() > 0) {
                cal.add( Calendar.DAY_OF_MONTH, Integer.parseInt( pdFrom.getValue( Interval.DAYS ) ) * (-1) );
            }
            
            if (pdFrom.getValue( Interval.MONTHS ) != null && pdFrom.getValue( Interval.MONTHS ).length() > 0) {
                cal.add( Calendar.MONTH, Integer.parseInt( pdFrom.getValue( Interval.MONTHS ) ) * (-1) );
            }
            
            if (pdFrom.getValue( Interval.YEARS ) != null && pdFrom.getValue( Interval.YEARS ).length() > 0) {
                cal.add( Calendar.YEAR, Integer.parseInt( pdFrom.getValue( Interval.YEARS ) ) * (-1) );
            }
        }
        from = df.format( cal.getTime() );
        if(!to.isEmpty() && !from.isEmpty() && additionalQuery != null) {
            queryString += " " + additionalQuery.replace("{TO}", to).replace("{FROM}", from);
        }
        return queryString;
    }

    public static void getHttpFacetValue(ResourceRequest request, ResourceResponse response, ArrayList<IngridFacet> config, String facetId) throws ParseException {
        IngridFacet facet = UtilsFacete.getFacetById(config, facetId);
        String stateRanking = (String) SearchState.getSearchStateObject(request, Settings.PARAM_RANKING);
        if(stateRanking == null) {
            stateRanking = "date";
        }
        if(facetId.equals("procedure_dev_plan") && stateRanking.equals("date")) {
            try {
                response.getWriter().write("0");
            } catch (IOException e) {
                UtilsLog.logError("Error write response!", e, log);
            }
        } else {
            if(facet != null) {
                String portalQueryString = UtilsSearch.updateQueryString("", request);
                IngridQuery query = QueryStringParser.parse( portalQueryString );

                String queryString = facet.getQuery();
                if(facet.getToggle() != null && facet.getToggle().isSelect()) {
                    queryString = facet.getToggle().getQuery();
                }

                if(queryString != null && queryString.trim().length() > 0){
                    // Change query to clause query (with phrase search)
                    UtilsSearch.changeQueryToClauseQuery(query);
        
                    String addToQuery = queryString;
                    if(addToQuery != null && addToQuery.length() > 0){
                        IngridQuery addQuery = QueryStringParser.parse(addToQuery);
                        ClauseQuery cp = UtilsSearch.createClauseQuery(addQuery, true, false);
                        query.addClause(cp);
                    }
                }
                if(config != null) {
                    query = UtilsFacete.getQueryFacets(request, config, query, facet.getParent().getId());
                }
                if (query.get( "FACETS" ) == null) {
                    ArrayList<IngridDocument> facetQueries = new ArrayList<>();
                    ArrayList<HashMap<String, String>> facetList = new ArrayList<>();
                    String tmpQuery = facet.getQuery();
                    if (!tmpQuery.isEmpty()) {
                        HashMap<String, String> facetEntry = new HashMap<>();
                        facetEntry.put("id", "value");
                        facetEntry.put("query", tmpQuery);
                        facetList.add(facetEntry);
                    }
                    if (!facetList.isEmpty()) {
                        IngridDocument facets = new IngridDocument();
                        facets.put("id", "value");
                        facets.put("classes", facetList);
                        facetQueries.add(facets);
                    }
                    if (!facetQueries.isEmpty()) {
                        query.put( "FACETS", facetQueries );
                    }
                }
                IngridHits hits;
                try {
                    IBUSInterface ibus = IBUSInterfaceImpl.getInstance();
                    hits = ibus.search( query, Settings.SEARCH_RANKED_HITS_PER_PAGE, 1, 0, PortalConfig.getInstance().getInt( PortalConfig.QUERY_TIMEOUT_RANKED, 5000 ) );

                    if (hits == null) {
                        if (log.isErrorEnabled()) {
                            log.error( "Problems fetching details to hit list !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
                        }
                    } else {
                        if(hits.length() > 0) {
                            Map<String, Object> facets = (Map<String, Object>) hits.get("FACETS");
                            JSONObject obj = new JSONObject();
                            if(facets != null){
                                for (Iterator<String> iterator = facets.keySet().iterator(); iterator.hasNext();) {
                                    String key = iterator.next();
                                    Long value = (Long) facets.get(key);
                                    response.getWriter().write(value+"");
                                    break;
                                }
                            }
                        } else {
                            response.getWriter().write("0");
                        }
                    }
                } catch (Exception e) {
                    UtilsLog.logError("Problems performing Search !", e, log);
                }
            }
        }
    }
}
