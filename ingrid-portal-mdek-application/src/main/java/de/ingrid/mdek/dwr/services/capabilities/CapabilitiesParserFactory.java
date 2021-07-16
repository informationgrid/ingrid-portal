/*
 * **************************************************-
 * Ingrid Portal MDEK Application
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
/*
 * Copyright (c) 2013 wemove digital solutions. All rights reserved.
 */
package de.ingrid.mdek.dwr.services.capabilities;

import de.ingrid.mdek.dwr.services.CatalogService;
import de.ingrid.utils.xml.*;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import de.ingrid.mdek.SysListCache;
import de.ingrid.utils.xpath.XPathUtils;

/**
 * @author André Wallat
 *
 */
public class CapabilitiesParserFactory {

    private static final Logger log = Logger.getLogger(CapabilitiesParserFactory.class);

    // Definition of all supported types
    private enum ServiceType { WMS, WMS111, WFS110, WFS200, WCS, WCS11, WCS201, CSW, WCTS, WMTS }

    // identifier for each service type
    private static final String SERVICE_TYPE_WMS = "WMS";
    private static final String SERVICE_TYPE_WFS = "WFS";
    private static final String SERVICE_TYPE_WCS = "WCS";
    private static final String SERVICE_TYPE_CSW = "CSW";
    private static final String SERVICE_TYPE_WCTS = "WCTS";
    private static final String SERVICE_TYPE_WMTS = "WMTS";

    private static XPathUtils xPathUtils = null;

    private static final String ERROR_GETCAP_XPATH = "ERROR_GETCAP_XPATH";

    public static ICapabilitiesParser getDocument(Document doc, SysListCache syslistCache, CatalogService catalogService) {
        if (xPathUtils == null) {
            ConfigurableNamespaceContext ns = new ConfigurableNamespaceContext();
            ns.addNamespaceContext(new Csw202NamespaceContext());
            ns.addNamespaceContext(new Wms130NamespaceContext());
            ns.addNamespaceContext(new Wfs110NamespaceContext());
            ns.addNamespaceContext(new Wfs200NamespaceContext());
            ns.addNamespaceContext(new WcsNamespaceContext());
            ns.addNamespaceContext(new Wcs11NamespaceContext());
            ns.addNamespaceContext(new Wcs201NamespaceContext());
            ns.addNamespaceContext(new WctsNamespaceContext());
            ns.addNamespaceContext(new WmtsNamespaceContext());
            xPathUtils = new XPathUtils(ns);
        }

        ServiceType serviceType = getServiceType(doc);

        switch (serviceType) {
            case WMS: return new Wms130CapabilitiesParser(syslistCache, catalogService);
            case WMS111: return new Wms111CapabilitiesParser(syslistCache, catalogService);
            case WFS110: return new Wfs110CapabilitiesParser(syslistCache);
            case WFS200: return new Wfs200CapabilitiesParser(syslistCache);
            case WCS: return new WcsCapabilitiesParser(syslistCache);
            case WCS11: return new Wcs11CapabilitiesParser(syslistCache);
            case WCS201: return new Wcs201CapabilitiesParser(syslistCache);
            case CSW: return new CswCapabilitiesParser(syslistCache);
            case WCTS: return new WctsCapabilitiesParser(syslistCache);
            case WMTS: return new WmtsCapabilitiesParser(syslistCache);
            default:
                throw new RuntimeException("Unknown Service Type.");
        }
    }

    private static ServiceType getServiceType(Document doc) {
        // WMS Version 1.3.0
        String serviceType = xPathUtils.getString(doc, "/wms:WMS_Capabilities/wms:Service/wms:Name[1]");
        if (serviceType != null && serviceType.length() != 0) {
            return ServiceType.WMS;
        }
        // WMS Version 1.1.1
        serviceType = xPathUtils.getString(doc, "/WMT_MS_Capabilities/Service/Name[1]");
        if (serviceType != null && serviceType.length() != 0) {
            return ServiceType.WMS111;
        }
        // WCS Version 1.0.0. Doesn't have a Service or ServiceType/Name Element. Just check if WCS_Capabilities exists
        serviceType = xPathUtils.getString(doc, "/wcs:WCS_Capabilities");
        if (serviceType != null && serviceType.length() != 0) {
            return ServiceType.WCS;
        }

        // WCS 1.1.0
        serviceType = xPathUtils.getString(doc, "/wcs11:Capabilities/ows11:ServiceIdentification/ows11:ServiceType[1]");
        if (serviceType != null && serviceType.contains(SERVICE_TYPE_WCS)) {
            return ServiceType.WCS11;
        }

        // WCS 2.0.1
        serviceType = xPathUtils.getString(doc, "/wcs201:Capabilities/ows20:ServiceIdentification/ows20:ServiceType[1]");
        if (serviceType != null && serviceType.toLowerCase().contains(SERVICE_TYPE_WCS.toLowerCase())) {
            return ServiceType.WCS201;
        }

        // WCTS
        serviceType = xPathUtils.getString(doc, "/wcts:Capabilities/owsgeo:ServiceIdentification/owsgeo:ServiceType[1]");
        if (serviceType != null && serviceType.contains(SERVICE_TYPE_WCTS)) {
            return ServiceType.WCTS;
        }

        // WFS 1.1.0
        serviceType = xPathUtils.getString(doc, "/wfs:WFS_Capabilities/ows:ServiceIdentification/ows:ServiceType[1]");
        if (serviceType != null && serviceType.length() != 0) {
            return ServiceType.WFS110;
        }

        // WFS 2.0
        serviceType = xPathUtils.getString(doc, "/wfs20:WFS_Capabilities/ows11:ServiceIdentification/ows11:ServiceType[1]");
        if (serviceType != null && serviceType.length() != 0) {
            return ServiceType.WFS200;
        }

        // WMTS
        serviceType = xPathUtils.getString(doc, "/wmts:Capabilities/ows11:ServiceIdentification/ows11:ServiceType");
        if (serviceType != null && serviceType.length() != 0) {
            return ServiceType.WMTS;
        }

        // All other services have can be evaluated via '/Capabilities/ServiceIdentification/ServiceType[1]'
        serviceType = xPathUtils.getString(doc, "/csw:Capabilities/ows:ServiceIdentification/ows:ServiceType[1]");
        if (serviceType != null && serviceType.length() != 0) {
            if (serviceType.contains(SERVICE_TYPE_WMS)) {
                return ServiceType.WMS;

            } else if (serviceType.contains(SERVICE_TYPE_WFS)) {
                return ServiceType.WFS200;

            } else if (serviceType.contains(SERVICE_TYPE_WCS)) {
                return ServiceType.WCS;

            } else if (serviceType.contains(SERVICE_TYPE_CSW)) {
                return ServiceType.CSW;

            } else if (serviceType.contains(SERVICE_TYPE_WCTS)) {
                return ServiceType.WCTS;

            } else {
                log.debug("Invalid service type: "+serviceType);
                throw new RuntimeException("Invalid service type: "+serviceType);
            }

        } else {
            // Could not determine ServiceType
            String error = "";
            if ("ExceptionReport".equals(doc.getFirstChild().getNodeName())) {
                error = doc.getFirstChild().getFirstChild().getFirstChild().getNodeValue();
            }
            log.debug("Could not evaluate service type. " + error);
            throw new RuntimeException("Could not evaluate service type: " + error);
        }
    }
}
