/*
 * **************************************************-
 * InGrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2024 wemove digital solutions GmbH
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
import java.util.ArrayList;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.ResourceURL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.IngridSysCodeList;
import de.ingrid.portal.global.UtilsFacete;
import de.ingrid.portal.global.UtilsPortletServeResources;
import de.ingrid.portal.om.IngridFacet;

public class SearchResultUVPPortlet extends SearchResultPortlet {

    private static final Logger log = LoggerFactory.getLogger(SearchResultUVPPortlet.class);

    @Override
    public void serveResource(ResourceRequest request, ResourceResponse response) throws IOException {
        String resourceID = request.getResourceID();
        response.setContentType( "application/javascript" );

        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()), request.getLocale());
        
        IngridSysCodeList sysCodeList = new IngridSysCodeList(request.getLocale());
        ArrayList<IngridFacet> config = (ArrayList<IngridFacet>) UtilsFacete.getAttributeFromSession(request, UtilsFacete.FACET_CONFIG);
        try {
            // "Zulassungsverfahren"
            if (resourceID.equals( "marker" )) {
                String query = PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_QUERY, "");
                if(!query.isEmpty()) {
                    UtilsPortletServeResources.getHttpMarkerUVP(response, UtilsPortletServeResources.REQUESTED_FIELDS_UVP_MARKER, request, query, messages, sysCodeList, config);
                }
            }
            // "Raumordnungsverfahren"
            if (resourceID.equals( "marker2" )) {
                String query = PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_QUERY_2, "");
                if(!query.isEmpty()) {
                    UtilsPortletServeResources.getHttpMarkerUVP(response, UtilsPortletServeResources.REQUESTED_FIELDS_UVP_MARKER, request, query, messages, sysCodeList, config);
                }
            }
            // "Ausländische Verfahren"
            if (resourceID.equals( "marker3" )) {
                String query = PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_QUERY_3, "");
                if(!query.isEmpty()) {
                    UtilsPortletServeResources.getHttpMarkerUVP(response, UtilsPortletServeResources.REQUESTED_FIELDS_UVP_MARKER, request, query, messages, sysCodeList, config);
                }
            }
            // "Negative Vorprüfungen"
            if (resourceID.equals( "marker4" )) {
                String query = PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_QUERY_4, "");
                if(!query.isEmpty()) {
                    UtilsPortletServeResources.getHttpMarkerUVP(response, UtilsPortletServeResources.REQUESTED_FIELDS_UVP_MARKER, request, query, messages, sysCodeList, config);
                }
            }
            // "Linienbestimmungen"
            if (resourceID.equals( "marker5" )) {
                String query = PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_QUERY_5, "");
                if(!query.isEmpty()) {
                    UtilsPortletServeResources.getHttpMarkerUVP(response, UtilsPortletServeResources.REQUESTED_FIELDS_UVP_MARKER, request, query, messages, sysCodeList, config);
                }
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
                    UtilsPortletServeResources.getHttpMarkerUVPMarkerBlp(request, response, queryString, config, messages);
                }
            }
            if(resourceID.equals( "legendCounter" )){
                String queryString = PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_UVP_QUERY_LEGEND, "datatype:www OR datatype:metadata");
                if(!queryString.isEmpty()) {
                    UtilsPortletServeResources.getHttpMarkerUVPLegendCounter(request, response, queryString, config, messages);
                }
            }

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

        String mapclientQuery5 = PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_QUERY_5, "");
        if(!mapclientQuery5.isEmpty()){
            restUrl.setResourceID( "marker5" );
            request.setAttribute( "restUrlMarker5", restUrl.toString() );
        }

        restUrl.setResourceID( "bbox" );
        request.setAttribute( "restUrlBBOX", restUrl.toString() );

        String mapclientUVPDevPlanURL = PortalConfig.getInstance().getString(PortalConfig.PORTAL_MAPCLIENT_UVP_CATEGORY_DEV_PLAN, "");
        if(!mapclientUVPDevPlanURL.isEmpty()){
            restUrl.setResourceID( "devPlanMarker" );
            request.setAttribute( "restUrlUVPDevPlan", restUrl.toString() );
        }

        if(!mapclientQuery.isEmpty() || !mapclientQuery2.isEmpty() || !mapclientQuery3.isEmpty() || !mapclientQuery4.isEmpty() || !mapclientUVPDevPlanURL.isEmpty()){
            restUrl.setResourceID( "legendCounter" );
            request.setAttribute( "restUrlLegendCounter", restUrl.toString() );
        }

        super.doView(request, response);
    }
}
