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

import javax.portlet.PortletConfig;
import javax.portlet.PortletException;

import org.apache.portals.bridges.velocity.AbstractVelocityMessagingPortlet;
import org.apache.velocity.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.UtilsString;

public class MeasuresSearchPortlet extends AbstractVelocityMessagingPortlet {

    private static final Logger log = LoggerFactory.getLogger(MeasuresSearchPortlet.class);

    @Override
    public void init(PortletConfig config) throws PortletException {
        super.init(config);
    }

    @Override
    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);
        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()), request.getLocale());
        context.put("MESSAGES", messages);

        context.put("UtilsString", new UtilsString());

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
        context.put( "measureUrl", PortalConfig.getInstance().getString(PortalConfig.PORTAL_MEASURE_URL));
        String[] measureNetworksExclude = PortalConfig.getInstance().getStringArray(PortalConfig.PORTAL_MEASURE_NETWORKS_EXCLUDE);
        String excludeNetworks = "";
        for (String network : measureNetworksExclude) {
            if(!excludeNetworks.isEmpty()) {
                excludeNetworks += ",";
            }
            excludeNetworks += network; 
        }
        context.put( "measureNetworksExclude", excludeNetworks);
        String[] measureNetworksInit = PortalConfig.getInstance().getStringArray(PortalConfig.PORTAL_MEASURE_NETWORKS_INITIAL);
        String initNetworks = "";
        for (String network : measureNetworksInit) {
            if(!initNetworks.isEmpty()) {
                initNetworks += ",";
            }
            initNetworks += network; 
        }
        context.put( "measureNetworksInitial", initNetworks);
        super.doView(request, response);
    }
}
