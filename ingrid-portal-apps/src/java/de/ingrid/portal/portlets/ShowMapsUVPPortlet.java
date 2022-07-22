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

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.ResourceURL;

import org.apache.velocity.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.IngridSysCodeList;
import de.ingrid.portal.global.UtilsPortletServeResources;

public class ShowMapsUVPPortlet extends ShowMapsPortlet {

    private static final Logger log = LoggerFactory.getLogger(ShowMapsUVPPortlet.class);

    @Override
    public void serveResource(ResourceRequest request, ResourceResponse response) throws IOException {
        String resourceID = request.getResourceID();
        response.setContentType( "application/javascript" );

        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()), request.getLocale());

        IngridSysCodeList sysCodeList = new IngridSysCodeList(request.getLocale());
        try {
            // "Zulassungsverfahren"
            if (resourceID.equals( "marker_time" )) {
                String query = PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_QUERY, "");
                if(!query.isEmpty()) {
                    UtilsPortletServeResources.getHttpMarkerUVPWithNumber(request, response, UtilsPortletServeResources.REQUESTED_FIELDS_UVP_MARKER,
                        query, messages, sysCodeList, UtilsPortletServeResources.REQUESTED_FIELDS_UVP_MARKER_NUM,
                        "P1YT", "PT", PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_QUERY_ADDITIONAL, ""));
                }
            }
            if (resourceID.equals( "marker" )) {
                String query = PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_QUERY, "");
                if(!query.isEmpty()) {
                    UtilsPortletServeResources.getHttpMarkerUVPWithNumber(request, response, UtilsPortletServeResources.REQUESTED_FIELDS_UVP_MARKER, query, messages, sysCodeList, UtilsPortletServeResources.REQUESTED_FIELDS_UVP_MARKER_NUM);
                }
            }
            // "Raumordnungsverfahren"
            if (resourceID.equals( "marker2_time" )) {
                String query = PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_QUERY_2, "");
                if(!query.isEmpty()) {
                    UtilsPortletServeResources.getHttpMarkerUVPWithNumber(request, response, UtilsPortletServeResources.REQUESTED_FIELDS_UVP_MARKER,
                        query, messages, sysCodeList, UtilsPortletServeResources.REQUESTED_FIELDS_UVP_MARKER_NUM,
                        "P1YT", "PT", PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_QUERY_2_ADDITIONAL, ""));
                }
            }
            if (resourceID.equals( "marker2" )) {
                String query = PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_QUERY_2, "");
                if(!query.isEmpty()) {
                    UtilsPortletServeResources.getHttpMarkerUVPWithNumber(request, response, UtilsPortletServeResources.REQUESTED_FIELDS_UVP_MARKER, query, messages, sysCodeList, UtilsPortletServeResources.REQUESTED_FIELDS_UVP_MARKER_NUM);
                }
            }
            // "Ausländische Verfahren"
            if (resourceID.equals( "marker3_time" )) {
                String query = PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_QUERY_3, "");
                if(!query.isEmpty()) {
                    UtilsPortletServeResources.getHttpMarkerUVPWithNumber(request, response, UtilsPortletServeResources.REQUESTED_FIELDS_UVP_MARKER,
                        query, messages, sysCodeList, UtilsPortletServeResources.REQUESTED_FIELDS_UVP_MARKER_NUM,
                        "P1YT", "PT", PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_QUERY_3_ADDITIONAL, ""));
                }
            }
            if (resourceID.equals( "marker3" )) {
                String query = PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_QUERY_3, "");
                if(!query.isEmpty()) {
                    UtilsPortletServeResources.getHttpMarkerUVPWithNumber(request, response, UtilsPortletServeResources.REQUESTED_FIELDS_UVP_MARKER, query, messages, sysCodeList, UtilsPortletServeResources.REQUESTED_FIELDS_UVP_MARKER_NUM);
                }
            }
            // "Negative Vorprüfungen"
            if (resourceID.equals( "marker4_time" )) {
                String query = PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_QUERY_4, "");
                if(!query.isEmpty()) {
                    UtilsPortletServeResources.getHttpMarkerUVPWithNumber(request, response, UtilsPortletServeResources.REQUESTED_FIELDS_UVP_MARKER,
                        query, messages, sysCodeList, UtilsPortletServeResources.REQUESTED_FIELDS_UVP_MARKER_NUM,
                        "P1YT", "PT", PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_QUERY_4_ADDITIONAL, ""));
                }
            }
            if (resourceID.equals( "marker4" )) {
                String query = PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_QUERY_4, "");
                if(!query.isEmpty()) {
                    UtilsPortletServeResources.getHttpMarkerUVPWithNumber(request, response, UtilsPortletServeResources.REQUESTED_FIELDS_UVP_MARKER, query, messages, sysCodeList, UtilsPortletServeResources.REQUESTED_FIELDS_UVP_MARKER_NUM);
                }
            }
            // "Linienbestimmungen"
            if (resourceID.equals( "marker5_time" )) {
                String query = PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_QUERY_5, "");
                if(!query.isEmpty()) {
                    UtilsPortletServeResources.getHttpMarkerUVPWithNumber(request, response, UtilsPortletServeResources.REQUESTED_FIELDS_UVP_MARKER,
                        query, messages, sysCodeList, UtilsPortletServeResources.REQUESTED_FIELDS_UVP_MARKER_NUM,
                        "P1YT", "PT", PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_QUERY_5_ADDITIONAL, ""));
                }
            }
            if (resourceID.equals( "marker5" )) {
                String query = PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_QUERY_5, "");
                if(!query.isEmpty()) {
                    UtilsPortletServeResources.getHttpMarkerUVPWithNumber(request, response, UtilsPortletServeResources.REQUESTED_FIELDS_UVP_MARKER, query, messages, sysCodeList, UtilsPortletServeResources.REQUESTED_FIELDS_UVP_MARKER_NUM);
                }
            }
            if (resourceID.equals( "markerDetail" )) {
                response.setContentType( "application/json" );
                String uuid = request.getParameter( "uuid" );
                String iplug = request.getParameter( "iplug" );
                String query = "t01_object.obj_id:" + uuid + " iplugs:\"" + iplug + "\" ranking:score";
                UtilsPortletServeResources.getHttpMarkerUVPDetail(response, UtilsPortletServeResources.REQUESTED_FIELDS_UVP_MARKER_DETAIL, query, messages, sysCodeList);
            }
            if (resourceID.equals( "bbox" )) {
                String uuid = request.getParameter( "uuid" );
                if(uuid != null) {
                    String queryString = "t01_object.obj_id:" + uuid + " ranking:score";
                    UtilsPortletServeResources.getHttpMarkerUVPBoundingBox(response, queryString);
                }
            }
            if(resourceID.equals( "devPlanMarker" )){
                String queryString = PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_UVP_CATEGORY_DEV_PLAN, "");
                if(!queryString.isEmpty()) {
                    UtilsPortletServeResources.getHttpMarkerUVPMarkerBlp(request, response, queryString, null);
                }
            }

            if(resourceID.equals( "devPlanMarkerDetail" )){
                String title = request.getParameter( "title" );
                String x1 = request.getParameter( "x1" );
                String y1 = request.getParameter( "y1" );
                String x2 = request.getParameter( "x2" );
                String y2 = request.getParameter( "y2" );
                if(x2 == null) {
                    x2 = x1;
                }
                if(y2 == null) {
                    y2 = y1;
                }
                String iplug = request.getParameter( "iplug" );
                String queryString = "\""+ title + "\" procedure:dev_plan y1:" + y1 + " x1:" + x1 + " y2:" + y2 + " x2:" + x2 + " iplugs:\"" + iplug + "\"";
                if(!queryString.isEmpty()) {
                    UtilsPortletServeResources.getHttpMarkerUVPMarkerBlpDetail(response, queryString);
                }
            }

            if(resourceID.equals( "legendCounter" )){
                String queryString = PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_UVP_QUERY_LEGEND, "datatype:www OR datatype:metadata");
                if(!queryString.isEmpty()) {
                    UtilsPortletServeResources.getHttpMarkerUVPLegendCounter(request, response, queryString, null);
                }
            }
        } catch (Exception e) {
            log.error( "Error creating resource for resource ID: " + resourceID, e );
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
            restUrl.setResourceID( "marker_time" );
            request.setAttribute( "restUrlMarker_time", restUrl.toString() );
        }
        String mapclientQuery2 = PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_QUERY_2, "");
        if(!mapclientQuery2.isEmpty()){
            restUrl.setResourceID( "marker2" );
            request.setAttribute( "restUrlMarker2", restUrl.toString() );
            restUrl.setResourceID( "marker2_time" );
            request.setAttribute( "restUrlMarker2_time", restUrl.toString() );
        }
        String mapclientQuery3 = PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_QUERY_3, "");
        if(!mapclientQuery3.isEmpty()){
            restUrl.setResourceID( "marker3" );
            request.setAttribute( "restUrlMarker3", restUrl.toString() );
            restUrl.setResourceID( "marker3_time" );
            request.setAttribute( "restUrlMarker3_time", restUrl.toString() );
        }
        String mapclientQuery4 = PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_QUERY_4, "");
        if(!mapclientQuery4.isEmpty()){
            restUrl.setResourceID( "marker4" );
            request.setAttribute( "restUrlMarker4", restUrl.toString() );
            restUrl.setResourceID( "marker4_time" );
            request.setAttribute( "restUrlMarker4_time", restUrl.toString() );
        }
        String mapclientQuery5 = PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_QUERY_5, "");
        if(!mapclientQuery5.isEmpty()){
            restUrl.setResourceID( "marker5" );
            request.setAttribute( "restUrlMarker5", restUrl.toString() );
            restUrl.setResourceID( "marker5_time" );
            request.setAttribute( "restUrlMarker5_time", restUrl.toString() );
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
        context.put( "leafletEpsg", PortalConfig.getInstance().getString( PortalConfig.PORTAL_MAPCLIENT_LEAFLET_EPSG, "3857"));
        context.put( "leafletBgLayerWMTS", PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_LEAFLET_BG_LAYER_WMTS));
        context.put( "leafletBgLayerAttribution", PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_LEAFLET_BG_LAYER_ATTRIBUTION));
        context.put( "leafletBgLayerOpacity", PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_LEAFLET_BG_LAYER_OPACITY));
        context.put( "leafletGeocoderServiceUrl", PortalConfig.getInstance().getString( PortalConfig.PORTAL_MAPCLIENT_UVP_GEOCODER_SERVICE_URL, "https://nominatim.openstreetmap.org/" ));

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
}
