/*
 * **************************************************-
 * Ingrid Portal Apps
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

public class SearchDetailBawDmqsPortlet extends SearchDetailPortlet {
    private static final Logger log = LoggerFactory.getLogger(SearchDetailBawDmqsPortlet.class);

    @Override
    public void serveResource(ResourceRequest request, ResourceResponse response) throws IOException {
        String resourceID = request.getResourceID();
        try {
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
                        for (int j = 0; j < questJson.length(); j++) {
                            if(!questJsonArray.isNull(j)) {
                                JSONObject questJsonEntry = questJsonArray.getJSONObject( j );
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
        restUrl.setResourceID( "bwastr" );
        request.setAttribute( "restUrlBWaStr", restUrl.toString() );

        Context context = getContext(request);
        context.put("bwastrLocatorGetDataLowers", PortalConfig.getInstance().getString(PortalConfig.PORTAL_BWASTR_LOCATOR_GET_DATA_LOWER)); 

        super.doView(request, response);
    }
}
