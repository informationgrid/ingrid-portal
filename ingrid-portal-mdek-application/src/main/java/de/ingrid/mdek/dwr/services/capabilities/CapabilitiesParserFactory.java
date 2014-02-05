/*
 * Copyright (c) 2013 wemove digital solutions. All rights reserved.
 */
package de.ingrid.mdek.dwr.services.capabilities;

import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import de.ingrid.mdek.SysListCache;
import de.ingrid.utils.xml.ConfigurableNamespaceContext;
import de.ingrid.utils.xml.Csw202NamespaceContext;
import de.ingrid.utils.xml.Wcs11NamespaceContext;
import de.ingrid.utils.xml.WcsNamespaceContext;
import de.ingrid.utils.xml.WctsNamespaceContext;
import de.ingrid.utils.xml.Wfs110NamespaceContext;
import de.ingrid.utils.xml.Wfs200NamespaceContext;
import de.ingrid.utils.xml.Wms130NamespaceContext;
import de.ingrid.utils.xpath.XPathUtils;

/**
 * @author André Wallat
 *
 */
public class CapabilitiesParserFactory {
    
    private final static Logger log = Logger.getLogger(CapabilitiesParserFactory.class);
    
    // Definition of all supported types
    private enum ServiceType { WMS, WMS111, WFS110, WFS200, WCS, WCS11, CSW, WCTS }
    
    // identifier for each service type
    private final static String SERVICE_TYPE_WMS = "WMS";
    private final static String SERVICE_TYPE_WFS = "WFS";
    private final static String SERVICE_TYPE_WCS = "WCS";
    private final static String SERVICE_TYPE_CSW = "CSW";
    private final static String SERVICE_TYPE_WCTS = "WCTS";
    
    private static XPathUtils xPathUtils = null;
    
    private static String ERROR_GETCAP_XPATH = "ERROR_GETCAP_XPATH";

    public static ICapabilitiesParser getDocument(Document doc, SysListCache syslistCache) {
        if (xPathUtils == null) {
            ConfigurableNamespaceContext ns = new ConfigurableNamespaceContext();
            ns.addNamespaceContext(new Csw202NamespaceContext());
            ns.addNamespaceContext(new Wms130NamespaceContext());
            ns.addNamespaceContext(new Wfs110NamespaceContext());
            ns.addNamespaceContext(new Wfs200NamespaceContext());
            ns.addNamespaceContext(new WcsNamespaceContext());
            ns.addNamespaceContext(new Wcs11NamespaceContext());
            ns.addNamespaceContext(new WctsNamespaceContext());
            xPathUtils = new XPathUtils(ns);
        }
        
        try {
            ServiceType serviceType = getServiceType(doc);
            
            switch (serviceType) {
            case WMS: return new Wms130CapabilitiesParser(syslistCache);
            case WMS111: return new Wms111CapabilitiesParser(syslistCache);            
            case WFS110: return new Wfs110CapabilitiesParser(syslistCache);
            case WFS200: return new Wfs200CapabilitiesParser(syslistCache);
            case WCS: return new WcsCapabilitiesParser(syslistCache);
            case WCS11: return new Wcs11CapabilitiesParser(syslistCache);
            case CSW: return new CswCapabilitiesParser(syslistCache);
            case WCTS: return new WctsCapabilitiesParser(syslistCache);
            default:
                throw new RuntimeException("Unknown Service Type.");
        }
            
        } catch (XPathExpressionException e) {
            log.debug("", e);
            throw new RuntimeException(ERROR_GETCAP_XPATH, e);
        }
        
    }
    
    private static ServiceType getServiceType(Document doc) throws XPathExpressionException {
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
        serviceType = xPathUtils.getString(doc, "/wfs:WFS_Capabilities/ows11:ServiceIdentification/ows11:ServiceType[1]");
        if (serviceType != null && serviceType.length() != 0) {
            return ServiceType.WFS200;
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
