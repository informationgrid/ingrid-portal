/*
 * **************************************************-
 * Ingrid Portal Apps
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
package de.ingrid.portal.portlets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.portlet.PortletException;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.ResourceURL;

import org.apache.commons.io.IOUtils;
import org.apache.velocity.context.Context;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;
import de.ingrid.portal.search.SearchState;
import de.ingrid.portal.search.UtilsSearch;
import de.ingrid.portal.search.net.IBusQueryResultIterator;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.queryparser.QueryStringParser;

/**
 * This portlet handles the "Result Display" fragment of the result page
 * 
 * @author martin@wemove.com
 */
public class SearchResultBawDmqsPortlet extends SearchResultPortlet {

    private static final Logger log = LoggerFactory.getLogger(SearchResultBawDmqsPortlet.class);

    private static final String[] REQUESTED_FIELDS_MARKER = new String[] { "bwstr-center-lon", "bwstr-center-lat", "t01_object.obj_id" };
    private static final String[] REQUESTED_FIELDS_BBOX = new String[] { "x1", "x2", "y1", "y2", "t01_object.obj_id" };
    
    @Override
    public void serveResource(ResourceRequest request, ResourceResponse response) throws IOException {
        String resourceID = request.getResourceID();
        try {
            if (resourceID.equals( "marker" )) {
                response.setContentType( "application/javascript" );
                response.getWriter().write( "var markers = [" );
                
                String queryString = SearchState.getSearchStateObjectAsString(request, Settings.PARAM_QUERY_STRING);
                if(queryString == null || queryString.length() == 0){
                    queryString = PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_QUERY, "datatype:metadata ranking:score") ;
                }else{
                    queryString += " ranking:score";
                }
                
                IBusQueryResultIterator it = new IBusQueryResultIterator( QueryStringParser.parse( queryString ), REQUESTED_FIELDS_MARKER, IBUSInterfaceImpl.getInstance()
                        .getIBus() );
                while (it.hasNext()) {
                    StringBuilder s = new StringBuilder();
                    IngridHit hit = it.next();
                    IngridHitDetail detail = hit.getHitDetail();
                    if (detail.containsKey( "bwstr-center-lat" ) && detail.containsKey( "bwstr-center-lon" )) {
                        s.append( "[" ).append( detail.get( "bwstr-center-lat" ).toString() ).append( "," ).append( detail.get( "bwstr-center-lon" ).toString() ).append( ",'" )
                                .append( detail.get( "title" ).toString() ).append( "','" ).append( UtilsSearch.getDetailValue( detail, "t01_object.obj_id" ) ).append( "']" );
                        if (it.hasNext()) {
                            s.append( "," );
                        }
                        response.getWriter().write( s.toString() );
                    }
                }
                response.getWriter().write( "];" );
            }
            if (resourceID.equals( "bbox" )) {
                String uuid = request.getParameter( "uuid" );
                response.setContentType( "application/javascript" );
                response.getWriter().write( "[" );
                IBusQueryResultIterator it = new IBusQueryResultIterator( QueryStringParser.parse( "t01_object.obj_id:" + uuid + " ranking:score" ), REQUESTED_FIELDS_BBOX,
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
            if (resourceID.equals( "bwastr" )) {
                String id = request.getParameter( "id" );
                String von = request.getParameter( "von" );
                String bis = request.getParameter( "bis" );
                String epsg = PortalConfig.getInstance().getString(PortalConfig.PORTAL_BWASTR_LOCATOR_EPSG);
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

        } catch (Exception e) {
            log.error( "Error creating resource for resource ID: " + resourceID, e );
        }
        super.serveResource(request, response);
    }
    
    @Override
    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        // define an REST URL to get the map data dynamically
        ResourceURL restUrl = response.createResourceURL();
        restUrl.setResourceID( "marker" );
        request.setAttribute( "restUrlMarker", restUrl.toString() );
        restUrl.setResourceID( "bbox" );
        request.setAttribute( "restUrlBBOX", restUrl.toString() );
        restUrl.setResourceID( "bwastr" );
        request.setAttribute( "restUrlBWaStr", restUrl.toString() );

        Context context = getContext(request);
        context.put("bwastrLocatorGetDataLowers", PortalConfig.getInstance().getString(PortalConfig.PORTAL_BWASTR_LOCATOR_GET_DATA_LOWER)); 

        super.doView(request, response);
    }

}
