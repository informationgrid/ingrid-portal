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
import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.UtilsMapServiceManager;

public class ShowMapsPortlet extends GenericVelocityPortlet {

    private final static Logger log = LoggerFactory.getLogger(ShowMapsPortlet.class);

    public void init(PortletConfig config) throws PortletException {
        super.init(config);
    }

    public void doView(RenderRequest request, RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);
        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()), request.getLocale());
        context.put("MESSAGES", messages);
        context.put("lang", request.getLocale().getLanguage());
        context.put("mapUrl", PortalConfig.getInstance().getString( PortalConfig.PORTAL_MAPCLIENT_URL, "/ingrid-webmap-client/frontend/prd/" ));
        context.put("checkedCategoryDevPlan", PortalConfig.getInstance().getBoolean( PortalConfig.PORTAL_MAPCLIENT_UVP_CATEGORY_DEV_PLAN_CHECKED, false ));
        context.put("checkedCategory10", PortalConfig.getInstance().getBoolean( PortalConfig.PORTAL_MAPCLIENT_UVP_CATEGORY_10_CHECKED, false ));
        context.put("checkedCategory11", PortalConfig.getInstance().getBoolean( PortalConfig.PORTAL_MAPCLIENT_UVP_CATEGORY_11_CHECKED, false ));
        context.put("checkedCategory12", PortalConfig.getInstance().getBoolean( PortalConfig.PORTAL_MAPCLIENT_UVP_CATEGORY_12_CHECKED, false ));
        context.put("checkedCategory1314", PortalConfig.getInstance().getBoolean( PortalConfig.PORTAL_MAPCLIENT_UVP_CATEGORY_1314_CHECKED, false ));

        String[] mapPosition = PortalConfig.getInstance().getStringArray( PortalConfig.PORTAL_MAPCLIENT_LEAFLET_POSITION);
        if(mapPosition != null && mapPosition.length == 3) {
            context.put("mapPosition", mapPosition);
            context.put("mapExtent", "");
        } else if(mapPosition != null && mapPosition.length == 4) {
            context.put("mapExtent", mapPosition);
            context.put("mapPosition", "");
        }
        context.put("mapParamE", request.getParameter("E") != null ? request.getParameter("E"): "");
        context.put("mapParamN", request.getParameter("N") != null ? request.getParameter("N"): "");
        context.put("mapParamZoom", request.getParameter("zoom") != null ? request.getParameter("zoom"): "");
        context.put("mapParamExtent", request.getParameter("extent") != null ? request.getParameter("extent").split(","): "");
        context.put("mapParamLayer", request.getParameter("layer") != null ? request.getParameter("layer"): "");
        
        // read preferences
        PortletPreferences prefs = request.getPreferences();
        String hKey = prefs.getValue("helpKey", null);
        if (hKey != null && hKey.length() > 0) {
            context.put("hKey", hKey);
        }

        if (request.getParameter("action") != null) {
            if (request.getParameter("action").equals("doTmpService")) {
                Map<String, Object> kml = new HashMap<String, Object>();

                try {
                    kml = UtilsMapServiceManager.createKmlFromIDF(request.getParameter(Settings.RESULT_KEY_PLUG_ID), request.getParameter(Settings.RESULT_KEY_DOC_ID));

                    context.put("kmlUrl", kml.get("kml_url"));
                    context.put("kmlTitle", kml.get("coord_class"));

                } catch (NumberFormatException e) {
                    log.error("NumberFormatException: " + e);
                } catch (ParserConfigurationException e) {
                    log.error("ParserConfigurationException: " + e);
                } catch (SAXException e) {
                    log.error("SAXException: " + e);
                } catch (Exception e) {
                    log.error("Exception: " + e);
                }
            }
        }
        super.doView(request, response);
    }

    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {

    }
}
