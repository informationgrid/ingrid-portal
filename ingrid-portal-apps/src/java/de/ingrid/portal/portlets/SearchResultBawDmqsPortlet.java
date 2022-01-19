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

import javax.portlet.PortletException;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.ResourceURL;

import org.apache.velocity.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.UtilsPortletServeResources;
import de.ingrid.portal.search.SearchState;

/**
 * This portlet handles the "Result Display" fragment of the result page
 * 
 * @author martin@wemove.com
 */
public class SearchResultBawDmqsPortlet extends SearchResultPortlet {

    private static final Logger log = LoggerFactory.getLogger(SearchResultBawDmqsPortlet.class);

    @Override
    public void serveResource(ResourceRequest request, ResourceResponse response) throws IOException {
        String resourceID = request.getResourceID();
        try {
            if (resourceID.equals( "marker" )) {
                String queryString = SearchState.getSearchStateObjectAsString(request, Settings.PARAM_QUERY_STRING);
                if(queryString == null || queryString.length() == 0){
                    queryString = PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_QUERY, "datatype:metadata ranking:score") ;
                }else{
                    queryString += " ranking:score";
                }
                UtilsPortletServeResources.getHttpMarkerBawDmqsMarker(response, queryString);
            }
            if (resourceID.equals( "bbox" )) {
                String uuid = request.getParameter( "uuid" );
                if(uuid != null) {
                    String queryString = "t01_object.obj_id:" + uuid + " ranking:score";
                    UtilsPortletServeResources.getHttpMarkerBawDmqsBoundingBox(response, queryString);
                }
            }
            if (resourceID.equals( "bwastr" )) {
                String id = request.getParameter( "id" );
                String von = request.getParameter( "von" );
                String bis = request.getParameter( "bis" );
                String epsg = PortalConfig.getInstance().getString(PortalConfig.PORTAL_BWASTR_LOCATOR_EPSG);
                if(id != null && von != null && bis != null && epsg != null) {
                    UtilsPortletServeResources.getHttpMarkerBawDmqsBwaStr(response, id, von, bis, epsg);
                }
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
