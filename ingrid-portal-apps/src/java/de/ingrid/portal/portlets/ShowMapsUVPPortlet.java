/*
 * **************************************************-
 * InGrid Portal Apps
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

    private final static Logger log = LoggerFactory.getLogger(ShowMapsUVPPortlet.class);

    private static final String[] REQUESTED_FIELDS_MARKER       = new String[] { "title", "lon_center", "lat_center", "t01_object.obj_id", "uvp_category", "uvp_number", "t01_object.obj_class" };
    private static final String[] REQUESTED_FIELDS_BBOX         = new String[] { "x1", "x2", "y1", "y2", "t01_object.obj_id" };
    private static final String[] REQUESTED_FIELDS_BLP_MARKER   = new String[] { "title", "x1", "x2", "y1", "y2", "blp_name", "blp_description", "blp_url_finished", "blp_url_in_progress" };

    @Override
    public void serveResource(ResourceRequest request, ResourceResponse response) throws IOException {
        String resourceID = request.getResourceID();
        
        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()), request.getLocale());
        
        IngridSysCodeList sysCodeList = new IngridSysCodeList(request.getLocale());
        try {
            if (resourceID.equals( "marker" )) {
                response.setContentType( "application/javascript" );
                response.getWriter().write( "var markers = [" );
                String query = PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_QUERY, "");
                if(query != "") {
                    writeResponse(request, response, query, messages, sysCodeList);
                }
                response.getWriter().write( "];" );
            }
            if (resourceID.equals( "marker2" )) {
                response.setContentType( "application/javascript" );
                response.getWriter().write( "var markers = [" );
                String query = PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_QUERY_2, "");
                if(query != "") {
                    writeResponse(request, response, query, messages, sysCodeList);
                }
                response.getWriter().write( "];" );
            }
            if (resourceID.equals( "marker3" )) {
                response.setContentType( "application/javascript" );
                response.getWriter().write( "var markers = [" );
                String query = PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_QUERY_3, "");
                if(query != "") {
                    writeResponse(request, response, query, messages, sysCodeList);
                }
                response.getWriter().write( "];" );
            }
            if (resourceID.equals( "marker4" )) {
                response.setContentType( "application/javascript" );
                response.getWriter().write( "var markers = [" );
                String query = PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_QUERY_4, "");
                if(query != "") {
                    writeResponse(request, response, query, messages, sysCodeList);
                }
                response.getWriter().write( "];" );
            }
            if (resourceID.equals( "bbox" )) {
                String uuid = request.getParameter( "uuid" );
                response.setContentType( "application/javascript" );
                response.getWriter().write( "[" );
                IBusQueryResultIterator it = new IBusQueryResultIterator( QueryStringParser.parse( "t01_object.obj_id:" + uuid + " ranking:score" ), REQUESTED_FIELDS_BBOX,
                        IBUSInterfaceImpl.getInstance().getIBus() );
                if(it!= null){
                    if (it.hasNext()) {
                        StringBuilder s = new StringBuilder();
                        IngridHit hit = it.next();
                        IngridHitDetail detail = hit.getHitDetail();
                        Object[] x1 = (Object[]) detail.get( "x1" );
                        Object[] y1 = (Object[]) detail.get( "y1" );
                        Object[] x2 = (Object[]) detail.get( "x2" );
                        Object[] y2 = (Object[]) detail.get( "y2" );
                        
                        if (y1 != null && y2 != null && x1 != null && x2 != null) {
                            String y1Value = y1[0].toString().trim();
                            String x1Value = x1[0].toString().trim();
                            String y2Value = y2[0].toString().trim();
                            String x2Value = x2[0].toString().trim();
                            if(x1Value.length() > 0 && x1Value.toLowerCase().indexOf( "nan" ) == -1 &&
                                x2Value.length() > 0 && x2Value.toLowerCase().indexOf( "nan" ) == -1 &&
                                y1Value.length() > 0 && y1Value.toLowerCase().indexOf( "nan" ) == -1 &&
                                y2Value.length() > 0 && y2Value.toLowerCase().indexOf( "nan" ) == -1) {
                                s.append("[").append( y1Value ).append( "," ).append( x1Value ).append( "],[" ).append( y2Value )
                                        .append( "," ).append( x2Value ).append("]");
                                response.getWriter().write( s.toString() );
                            }
                        }
                    }
                }
                response.getWriter().write( "]" );
            }
            if(resourceID.equals( "devPlanMarker" )){
                String queryString = PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_UVP_CATEGORY_DEV_PLAN, "");
                
                IBusQueryResultIterator it = new IBusQueryResultIterator( QueryStringParser.parse(queryString) , REQUESTED_FIELDS_BLP_MARKER, IBUSInterfaceImpl.getInstance()
                        .getIBus() );
                if(it != null){
                    int cnt = 1;
                    JSONArray jsonData = new JSONArray();
                    while (it.hasNext()) {
                        try {
                            StringBuilder s = new StringBuilder();
                            IngridHit hit = it.next();
                            IngridHitDetail detail = hit.getHitDetail();
                            String lat_center = UtilsSearch.getDetailValue( detail, "y1", 1);
                            String lon_center = UtilsSearch.getDetailValue( detail, "x1", 1);
                            String blpName = UtilsSearch.getDetailValue( detail, "blp_name" );
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
                            JSONObject jsonDataEntry = new JSONObject();
                            jsonDataEntry.put("id", cnt);
                            jsonDataEntry.put("name", blpName);
                            jsonDataEntry.put("latlon", new JSONArray().put( Double.parseDouble( lat_center.trim()) ).put(Double.parseDouble(lon_center.trim())));
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
                            jsonDataEntry.put("bpinfos", bpInfos);
                            if (blpDescription != null && !blpDescription.isEmpty()) {
                                jsonDataEntry.put( "descr", blpDescription);
                            }
                            jsonData.put( jsonDataEntry );
                            cnt++;
                        } catch (Exception e) {
                            log.error("Error get json object:" + e);
                        }
                    }
                        
                    response.setContentType( "application/javascript" );
                    response.getWriter().write( "var markersDevPlan = "+ jsonData.toString() + ";");
                }
            }
            if(resourceID.equals( "legendCounter" )){
                String queryString = PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_UVP_QUERY_LEGEND, "datatype:www OR datatype:metadata");
                IngridQuery query = QueryStringParser.parse( queryString );
                query.put( IngridQuery.RANKED, "score" );
                if (query.get( "FACETS" ) == null) {
                    ArrayList<IngridDocument> facetQueries = new ArrayList<IngridDocument>();
                    ArrayList<HashMap<String, String>> facetList = new ArrayList<HashMap<String, String>> ();
                    
                    String tmpQuery = PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_QUERY, "");
                    if (tmpQuery != "") {
                        HashMap<String, String> facetEntry = new HashMap<String, String>();
                        facetEntry.put("id", "countMarker1");
                        facetEntry.put("query", tmpQuery);
                        facetList.add(facetEntry);
                    }
                    tmpQuery = PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_QUERY_2, "");
                    if (tmpQuery != "") {
                        HashMap<String, String> facetEntry = new HashMap<String, String>();
                        facetEntry.put("id", "countMarker2");
                        facetEntry.put("query", tmpQuery);
                        facetList.add(facetEntry);
                    }
                    tmpQuery = PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_QUERY_3, "");
                    if (tmpQuery != "") {
                        HashMap<String, String> facetEntry = new HashMap<String, String>();
                        facetEntry.put("id", "countMarker3");
                        facetEntry.put("query", tmpQuery);
                        facetList.add(facetEntry);
                    }
                    tmpQuery = PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_QUERY_4, "");
                    if (tmpQuery != "") {
                        HashMap<String, String> facetEntry = new HashMap<String, String>();
                        facetEntry.put("id", "countMarker4");
                        facetEntry.put("query", tmpQuery);
                        facetList.add(facetEntry);
                    }
                    tmpQuery = PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_UVP_CATEGORY_DEV_PLAN, "");
                    if (tmpQuery != "") {
                        HashMap<String, String> facetEntry = new HashMap<String, String>();
                        facetEntry.put("id", "countMarkerDevPlan");
                        facetEntry.put("query", tmpQuery);
                        facetList.add(facetEntry);
                    }
                    if (facetList.size() > 0) {
                        IngridDocument facet = new IngridDocument();
                        facet.put("id", "legend_counter");
                        facet.put("classes", facetList);
                        facetQueries.add(facet);
                    }
                    if (facetQueries.size() > 0) {
                        query.put( "FACETS", facetQueries );
                    }
                }
                IngridHits hits = null;
                try {
                    IBUSInterface ibus = IBUSInterfaceImpl.getInstance();
                    hits = ibus.search( query, Settings.SEARCH_RANKED_HITS_PER_PAGE, 1, 0, PortalConfig.getInstance().getInt( PortalConfig.QUERY_TIMEOUT_RANKED, 5000 ) );

                    if (hits == null) {
                        if (log.isErrorEnabled()) {
                            log.error( "Problems fetching details to hit list !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
                        }
                    } else {
                        response.setContentType( "application/javascript" );
                        if(hits.length() > 0) {
                            Map<String, Object> facets = (Map<String, Object>) hits.get("FACETS");
                            String facetsJSON = "{";
                            if(facets != null){
                                for (Iterator<String> iterator = facets.keySet().iterator(); iterator.hasNext();) {
                                    String key = iterator.next();
                                    Long value = (Long) facets.get(key);
                                    facetsJSON += "\"" + key.split(":")[1] + "\":" + value;
                                    if(iterator.hasNext()) {
                                        facetsJSON += ",";
                                    }
                                }
                            }
                            facetsJSON += "}";
                            response.getWriter().write( "var legendCounter = " + facetsJSON +";");
                        } else {
                            response.getWriter().write( "var legendCounter = {};");
                        }
                    }
                } catch (Throwable t) {
                    if (log.isErrorEnabled()) {
                        log.error( "Problems performing Search !", t );
                    }
                }
            }
        } catch (Exception e) {
            log.error( "Error creating resource for resource ID: " + resourceID, e );
        }
    }
    
    private void writeResponse(ResourceRequest request, ResourceResponse response, String queryString, IngridResourceBundle messages, IngridSysCodeList sysCodeList) throws ParseException, IOException {
        IBusQueryResultIterator it = new IBusQueryResultIterator( QueryStringParser.parse( queryString ), REQUESTED_FIELDS_MARKER, IBUSInterfaceImpl.getInstance()
                .getIBus() );
        if(it != null){
            while (it.hasNext()) {
                StringBuilder s = new StringBuilder();
                IngridHit hit = it.next();
                IngridHitDetail detail = hit.getHitDetail();
                Object[] lat_center = (Object[]) detail.get( "lat_center" );
                Object[] lon_center = (Object[]) detail.get( "lon_center" );
                if (lat_center != null && lon_center != null) {
                    String latCenterValue = lat_center[0].toString().trim();
                    String lonCenterValue = lon_center[0].toString().trim();
                    if(latCenterValue.length() > 0 && latCenterValue.toLowerCase().indexOf( "nan" ) == -1 &&
                        lonCenterValue.length() > 0 && lonCenterValue.toLowerCase().indexOf( "nan" ) == -1 ){
                        s.append( "[" )
                            .append( latCenterValue ).append( "," )
                            .append( lonCenterValue ).append( ",'" )
                            .append( detail.get( "title" ).toString() ).append( "','" )
                            .append( UtilsSearch.getDetailValue( detail, "t01_object.obj_id" ) ).append( "','" )
                            .append( UtilsSearch.getDetailValue( detail, "t01_object.obj_class" ) ).append( "','" )
                            .append( sysCodeList.getName( "8001", UtilsSearch.getDetailValue( detail, "t01_object.obj_class" )) ).append( "'");
                        
                        if(detail.get( "uvp_category" ) != null){
                            ArrayList<String> categories = getIndexValue(detail.get( "uvp_category" ));
                            s.append( "," ).append( "[" );
                            if(categories != null && categories.size() > 0){
                                int index = 0;
                                for (String category : categories) {
                                    s.append( "{" );
                                    s.append( "'id':'" + category.trim() + "'" );
                                    s.append( ",");
                                    s.append( "'name':'" + messages.getString( "searchResult.categories.uvp." + category.trim() ) + "'" );
                                    s.append( "}" );
                                    if(index < categories.size() - 1){
                                        s.append( "," );
                                    }
                                    index ++;
                                }
                            }
                            s.append( "]" );
                        }else{
                            s.append( "," ).append( "[" );
                            s.append( "]" );
                        }
                        
                        if(detail.get( "uvp_steps" ) != null){
                            ArrayList<String> steps = getIndexValue(detail.get( "uvp_steps" ));
                            s.append( "," ).append( "[" );
                            if(steps != null && steps.size() > 0){
                                int index = 0;
                                s.append( "'" );
                                for (String step : steps) {
                                    s.append( messages.getString( "common.steps.uvp." + step.trim() ) );
                                    if(index < steps.size() - 1){
                                        s.append( "','" );
                                    }else{
                                        s.append( "'" );
                                    }
                                    index ++;
                                }
                            }
                            s.append( "]" );
                        }else{
                            s.append( "," ).append( "[" );
                            s.append( "]" );
                        }
                        
                        s.append( "]" );
                        if (it.hasNext()) {
                            s.append( "," );
                        }
                        response.getWriter().write( s.toString() );
                    }
                }
            }
        }
    }

    public void doView(RenderRequest request, RenderResponse response)
            throws PortletException, IOException {
        // define an REST URL to get the map data dynamically
        ResourceURL restUrl = response.createResourceURL();
        String mapclientQuery = PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_QUERY, "");
        if(mapclientQuery != ""){
            restUrl.setResourceID( "marker" );
            request.setAttribute( "restUrlMarker", restUrl.toString() );
        }
        String mapclientQuery2 = PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_QUERY_2, "");
        if(mapclientQuery2 != ""){
            restUrl.setResourceID( "marker2" );
            request.setAttribute( "restUrlMarker2", restUrl.toString() );
        }
        String mapclientQuery3 = PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_QUERY_3, "");
        if(mapclientQuery3 != ""){
            restUrl.setResourceID( "marker3" );
            request.setAttribute( "restUrlMarker3", restUrl.toString() );
        }
        String mapclientQuery4 = PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_QUERY_4, "");
        if(mapclientQuery4 != ""){
            restUrl.setResourceID( "marker4" );
            request.setAttribute( "restUrlMarker4", restUrl.toString() );
        }
        restUrl.setResourceID( "bbox" );
        request.setAttribute( "restUrlBBOX", restUrl.toString() );

        String mapclientUVPDevPlanURL = PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_UVP_CATEGORY_DEV_PLAN, "");
        if(mapclientUVPDevPlanURL != ""){
            restUrl.setResourceID( "devPlanMarker" );
            request.setAttribute( "restUrlUVPDevPlan", restUrl.toString() );
        }

        if(mapclientQuery != "" || mapclientQuery2 != "" || mapclientQuery3 != "" || mapclientQuery4 != "" || mapclientUVPDevPlanURL != ""){
            restUrl.setResourceID( "legendCounter" );
            request.setAttribute( "restUrlLegendCounter", restUrl.toString() );
        }
        super.doView(request, response);
    }

    private ArrayList<String> getIndexValue(Object obj){
        ArrayList<String> array = new ArrayList<String>();
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
        return array;
    }
}
