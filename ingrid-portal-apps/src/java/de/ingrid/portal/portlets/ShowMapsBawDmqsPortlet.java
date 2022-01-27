/*
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
package de.ingrid.portal.portlets;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.ResourceURL;

import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.UtilsPortletServeResources;

public class ShowMapsBawDmqsPortlet extends GenericVelocityPortlet {

    private static final Logger log = LoggerFactory.getLogger( ShowMapsBawDmqsPortlet.class );

    @Override
    public void init(PortletConfig config) throws PortletException {
        super.init( config );
    }

    @Override
    public void serveResource(ResourceRequest request, ResourceResponse response) throws IOException {
        String resourceID = request.getResourceID();
        try {
            if (resourceID.equals( "marker" )) {
                String queryString = PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_QUERY, "datatype:metadata ranking:score");
                UtilsPortletServeResources.getHttpMarkerBawDmqsMarker(response, queryString);
            }
            if (resourceID.equals( "bbox" )) {
                String uuid = request.getParameter( "uuid" );
                if(uuid != null) {
                    String queryString = "t01_object.obj_id:" + uuid + " ranking:score";
                    UtilsPortletServeResources.getHttpMarkerBawDmqsBoundingBox(response, queryString);
                }
            }
        } catch (Exception e) {
            log.error( "Error creating resource for resource ID: " + resourceID, e );
        }

    }

    @Override
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        Context context = getContext( request );
        IngridResourceBundle messages = new IngridResourceBundle( getPortletConfig().getResourceBundle( request.getLocale() ), request.getLocale() );
        context.put( "MESSAGES", messages );
        context.put( "lang", request.getLocale().getLanguage() );
        // read preferences
        PortletPreferences prefs = request.getPreferences();
        String hKey = prefs.getValue( "helpKey", null );
        if (hKey != null && hKey.length() > 0) {
            context.put( "hKey", hKey );
        }

        // define an REST URL to get the map data dynamically
        ResourceURL restUrl = response.createResourceURL();
        restUrl.setResourceID( "marker" );
        request.setAttribute( "restUrlMarker", restUrl.toString() );
        restUrl.setResourceID( "bbox" );
        request.setAttribute( "restUrlBBOX", restUrl.toString() );

        context.put( "leafletBgLayerWMTS", PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_LEAFLET_BG_LAYER_WMTS));
        context.put( "leafletBgLayerAttribution", PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_LEAFLET_BG_LAYER_ATTRIBUTION));
        context.put( "leafletBgLayerOpacity", PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_LEAFLET_BG_LAYER_OPACITY));

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
        super.doView( request, response );
    }

    @Override
    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException, IOException {

    }
}
