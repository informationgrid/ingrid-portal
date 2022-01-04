/*
 * **************************************************-
 * InGrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2021 wemove digital solutions GmbH
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
package de.ingrid.portal.portlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.ResourceURL;

import org.apache.velocity.context.Context;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.IngridSysCodeList;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.interfaces.IBUSInterface;
import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;
import de.ingrid.portal.search.UtilsSearch;
import de.ingrid.portal.search.net.IBusQueryResultIterator;
import de.ingrid.utils.IngridDocument;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.ParseException;
import de.ingrid.utils.queryparser.QueryStringParser;

public class ShowMapsUVPPortlet extends ShowMapsPortlet {

    private static final Logger log = LoggerFactory.getLogger(ShowMapsUVPPortlet.class);

    private static final String[] REQUESTED_FIELDS_MARKER               = PortalConfig.getInstance().getStringArray(PortalConfig.PORTAL_UVP_MAP_REQUESTED_FIELDS_MARKER);
    private static final String[] REQUESTED_FIELDS_MARKER_DETAIL        = PortalConfig.getInstance().getStringArray(PortalConfig.PORTAL_UVP_MAP_REQUESTED_FIELDS_MARKER_DETAIL);
    private static final String[] REQUESTED_FIELDS_BBOX                 = PortalConfig.getInstance().getStringArray(PortalConfig.PORTAL_UVP_MAP_REQUESTED_FIELDS_MARKER_BBOX);
    private static final String[] REQUESTED_FIELDS_BLP_MARKER           = PortalConfig.getInstance().getStringArray(PortalConfig.PORTAL_UVP_MAP_REQUESTED_FIELDS_BLP_MARKER);
    private static final String[] REQUESTED_FIELDS_BLP_MARKER_DETAIL    = PortalConfig.getInstance().getStringArray(PortalConfig.PORTAL_UVP_MAP_REQUESTED_FIELDS_BLP_MARKER_DETAIL);
    private static final int REQUESTED_FIELDS_MARKER_NUM            = PortalConfig.getInstance().getInt(PortalConfig.PORTAL_UVP_MAP_REQUESTED_FIELDS_MARKER_NUM);

    @Override
    public void serveResource(ResourceRequest request, ResourceResponse response) throws IOException {
        String resourceID = request.getResourceID();
        response.setContentType( "application/javascript" );

        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()), request.getLocale());

        IngridSysCodeList sysCodeList = new IngridSysCodeList(request.getLocale());
        try {
            if (resourceID.equals( "marker" )) {
                String query = PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_QUERY, "");
                if(!query.isEmpty()) {
                    response.getWriter().write(writeResponse(query, messages, sysCodeList, REQUESTED_FIELDS_MARKER, REQUESTED_FIELDS_MARKER_NUM));
                }
            }
            if (resourceID.equals( "marker2" )) {
                String query = PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_QUERY_2, "");
                if(!query.isEmpty()) {
                    response.getWriter().write(writeResponse(query, messages, sysCodeList, REQUESTED_FIELDS_MARKER, REQUESTED_FIELDS_MARKER_NUM));
                }
            }
            if (resourceID.equals( "marker3" )) {
                String query = PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_QUERY_3, "");
                if(!query.isEmpty()) {
                    response.getWriter().write(writeResponse(query, messages, sysCodeList, REQUESTED_FIELDS_MARKER, REQUESTED_FIELDS_MARKER_NUM));
                }
            }
            if (resourceID.equals( "marker4" )) {
                String query = PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_QUERY_4, "");
                if(!query.isEmpty()) {
                    response.getWriter().write(writeResponse(query, messages, sysCodeList, REQUESTED_FIELDS_MARKER, REQUESTED_FIELDS_MARKER_NUM));
                }
            }
            if (resourceID.equals( "markerDetail" )) {
                response.setContentType( "application/json" );
                String uuid = request.getParameter( "uuid" );
                String iplug = request.getParameter( "iplug" );
                String query = "t01_object.obj_id:" + uuid + " iplugs:\"" + iplug + "\" ranking:score";
                response.getWriter().write(writeResponse(query, messages, sysCodeList, REQUESTED_FIELDS_MARKER_DETAIL));
            }
            if (resourceID.equals( "bbox" )) {
                String uuid = request.getParameter( "uuid" );
                IngridQuery queryString = QueryStringParser.parse("t01_object.obj_id:" + uuid + " ranking:score");
                JSONArray bbox = new JSONArray();
                IBusQueryResultIterator it = new IBusQueryResultIterator( queryString, REQUESTED_FIELDS_BBOX,
                        IBUSInterfaceImpl.getInstance().getIBus() );
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
                response.getWriter().write( bbox.toString() );
            }
            if(resourceID.equals( "devPlanMarker" )){
                String queryString = PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_UVP_CATEGORY_DEV_PLAN, "");

                IBusQueryResultIterator it = new IBusQueryResultIterator( QueryStringParser.parse(queryString) , REQUESTED_FIELDS_BLP_MARKER, IBUSInterfaceImpl.getInstance()
                        .getIBus(), REQUESTED_FIELDS_MARKER_NUM );
                int cnt = 1;
                JSONArray jsonData = new JSONArray();
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
                            jsonData.put( jsonDataEntry );
                            cnt++;
                        } else {
                            log.error("Metadata '" + blpName + "' has no location!");
                        }
                    } catch (Exception e) {
                        log.error("Error get json object.", e);
                    }
                }

                response.setContentType( "application/javascript" );
                response.getWriter().write( "var markersDevPlan = "+ jsonData.toString() + ";");
            }

            if(resourceID.equals( "devPlanMarkerDetail" )){
                response.setContentType( "application/json" );
                String title = request.getParameter( "title" );
                String x1 = request.getParameter( "x1" );
                String y1 = request.getParameter( "y1" );
                String iplug = request.getParameter( "iplug" );
                IngridQuery queryString = QueryStringParser.parse("\""+ title + "\" procedure:dev_plan y1:" + y1 + " x1:" + x1 + " iplugs:\"" + iplug + "\"");
                
                IBusQueryResultIterator it = new IBusQueryResultIterator( queryString , REQUESTED_FIELDS_BLP_MARKER_DETAIL, IBUSInterfaceImpl.getInstance()
                        .getIBus() );
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
                        log.error("Error get json object.", e);
                    }
                }

                response.getWriter().write( jsonData.toString());
            }

            if(resourceID.equals( "legendCounter" )){
                String queryString = PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_UVP_QUERY_LEGEND, "datatype:www OR datatype:metadata");
                IngridQuery query = QueryStringParser.parse( queryString );
                query.put( IngridQuery.RANKED, "score" );
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
                    tmpQuery = PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_UVP_CATEGORY_DEV_PLAN, "");
                    if (!tmpQuery.isEmpty()) {
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
                } catch (Exception t) {
                    if (log.isErrorEnabled()) {
                        log.error( "Problems performing Search !", t );
                    }
                }
            }
        } catch (Exception e) {
            log.error( "Error creating resource for resource ID: " + resourceID, e );
        }
    }

    private String writeResponse(String queryString, IngridResourceBundle messages, IngridSysCodeList sysCodeList, String[] requestedField) throws ParseException {
        return writeResponse(queryString, messages, sysCodeList, requestedField, 10);
    }

    private String writeResponse(String queryString, IngridResourceBundle messages, IngridSysCodeList sysCodeList, String[] requestedField, int pageSize) throws ParseException {
        IBusQueryResultIterator it = new IBusQueryResultIterator( QueryStringParser.parse( queryString ), requestedField, IBUSInterfaceImpl.getInstance()
                .getIBus(), pageSize);
        JSONArray jsonData = new JSONArray();
        if(it != null){
            while (it.hasNext()) {
                IngridHit hit = it.next();
                IngridHitDetail detail = hit.getHitDetail();
                try {
                   if(requestedField == REQUESTED_FIELDS_MARKER) {
                        JSONArray jsonDataEntry = new JSONArray();
                        String lat = UtilsSearch.getDetailValue( detail, "lat_center", 1);
                        String lng = UtilsSearch.getDetailValue( detail, "lon_center", 1);
                        String title = UtilsSearch.getDetailValue( detail, "title" );
                        String uuid = UtilsSearch.getDetailValue( detail, "t01_object.obj_id" );
                        String iplug = hit.getPlugId();
                        if(!lat.isEmpty() && !lng.isEmpty()) {
                            jsonDataEntry.put(Double.parseDouble(lat));
                            jsonDataEntry.put(Double.parseDouble(lng));
                            jsonDataEntry.put(title);
                            jsonDataEntry.put(uuid);
                            jsonDataEntry.put(iplug);
                            jsonData.put(jsonDataEntry);
                        } else {
                            log.error("Metadata '" + title + "' with UUID '" + uuid + "' has no location!");
                        }
                    } else if (requestedField == REQUESTED_FIELDS_MARKER_DETAIL) {
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
                    }
                    
                } catch (Exception e) {
                    log.error("Error write response.", e);
                }
            }
        }
        if(requestedField == REQUESTED_FIELDS_MARKER) {
            return "var markers = " + jsonData.toString() + ";";
        } else {
            return jsonData.toString();
        }
        
    }

    @Override
    public void doView(RenderRequest request, RenderResponse response)
            throws PortletException, IOException {
        // define an REST URL to get the map data dynamically
        ResourceURL restUrl = response.createResourceURL();
        String mapclientQuery = PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_QUERY, "");
        if(!mapclientQuery.isEmpty()){
            restUrl.setResourceID( "marker" );
            request.setAttribute( "restUrlMarker", restUrl.toString() );
        }
        String mapclientQuery2 = PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_QUERY_2, "");
        if(!mapclientQuery2.isEmpty()){
            restUrl.setResourceID( "marker2" );
            request.setAttribute( "restUrlMarker2", restUrl.toString() );
        }
        String mapclientQuery3 = PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_QUERY_3, "");
        if(!mapclientQuery3.isEmpty()){
            restUrl.setResourceID( "marker3" );
            request.setAttribute( "restUrlMarker3", restUrl.toString() );
        }
        String mapclientQuery4 = PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_QUERY_4, "");
        if(!mapclientQuery4.isEmpty()){
            restUrl.setResourceID( "marker4" );
            request.setAttribute( "restUrlMarker4", restUrl.toString() );
        }

        restUrl.setResourceID( "markerDetail" );
        request.setAttribute( "restUrlMarkerDetail", restUrl.toString() );

        restUrl.setResourceID( "bbox" );
        request.setAttribute( "restUrlBBOX", restUrl.toString() );

        String mapclientUVPDevPlanURL = PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_UVP_CATEGORY_DEV_PLAN, "");
        if(!mapclientUVPDevPlanURL.isEmpty()){
            restUrl.setResourceID( "devPlanMarker" );
            request.setAttribute( "restUrlUVPDevPlan", restUrl.toString() );

            restUrl.setResourceID( "devPlanMarkerDetail" );
            request.setAttribute( "restUrlUVPDevPlanDetail", restUrl.toString() );
        }

        if(!mapclientQuery.isEmpty() || !mapclientQuery2.isEmpty() || !mapclientQuery3.isEmpty() || !mapclientQuery4.isEmpty() || !mapclientUVPDevPlanURL.isEmpty()){
            restUrl.setResourceID( "legendCounter" );
            request.setAttribute( "restUrlLegendCounter", restUrl.toString() );
        }
        
        Context context = getContext(request);
        context.put( "leafletBgLayerWMTS", PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_LEAFLET_BG_LAYER_WMTS));
        context.put( "leafletBgLayerAttribution", PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_LEAFLET_BG_LAYER_ATTRIBUTION));
        
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
        super.doView(request, response);
    }

    private ArrayList<String> getIndexValue(Object obj){
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
}
