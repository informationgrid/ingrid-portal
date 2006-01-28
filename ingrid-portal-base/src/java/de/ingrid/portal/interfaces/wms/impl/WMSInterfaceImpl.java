/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.interfaces.wms.impl;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import de.ingrid.portal.interfaces.wms.WMSInterface;
import de.ingrid.portal.interfaces.wms.om.WMSSearchDescriptor;
import de.ingrid.portal.interfaces.wms.om.WMSServiceDescriptor;

/**
 * Implements the WMS interface. It allows to communicate with the mapbender
 * WMS server that has been extended for the ingrid project with some
 * interface methods.
 * 
 * @author joachim@wemove.com
 */
public class WMSInterfaceImpl implements WMSInterface {

    private final static Log log = LogFactory.getLog(WMSInterfaceImpl.class);
    
    Configuration config;

    /**
     * @param url
     * @param port
     */
    public WMSInterfaceImpl(Configuration config) {
        super();
        this.config = config;
    }

    /**
     * @see de.ingrid.portal.interfaces.wms.WMSInterface#getWMSServices(java.lang.String)
     */
    public Collection getWMSServices(String sessionID) {
        URL url;
        SAXReader reader = new SAXReader(false);
        ArrayList result = new ArrayList();

        try {

            // workaround for wrong dtd location

            EntityResolver resolver = new EntityResolver() {
                public InputSource resolveEntity(String publicId, String systemId) {
                    if (systemId.equals("http://torwald.gistec-online.de/mapbender/php/dtd/portalCommunication.dtd")) {

                        InputStream in = getClass().getResourceAsStream("wms_interface.dtd");

                        return new InputSource(in);
                    }
                    return null;
                }
            };
            reader.setEntityResolver(resolver);

            // END workaround for wrong dtd location

            url = new URL(config.getString("interface_url", "http://localhost/mapbender/php/mod_portalCommunication_gt.php")
                    .concat("?PREQUEST=getWMSServices").concat("&PHPSESSID=" + sessionID));

            reader.setValidation(false);
            Document document = reader.read(url);

            // check for valid server response
            if (document.selectSingleNode("//portal_communication") == null) {
                throw new Exception("WMS Server Response is not valid!");
            }
            // check for valid server response
            String error = document.valueOf("//portal_communication/error");
            if (error != null && error.length() > 0) {
                throw new Exception("WMS Server Error: " + error);
            }
            // get the wms services
            List nodes = document.selectNodes("//portal_communication/wms_services/wms");
            for (Iterator i = nodes.iterator(); i.hasNext();) {
                Node node = (Node) i.next();
                WMSServiceDescriptor wmsServiceDescriptor = new WMSServiceDescriptor(node.valueOf("//name"), node
                        .valueOf("//url"));
                result.add(wmsServiceDescriptor);
            }

            return result;

        } catch (Exception e) {
            log.error(e);
        }

        return null;
    }

    /**
     * @see de.ingrid.portal.interfaces.wms.WMSInterface#getWMSSearchParameter(java.lang.String)
     */
    public WMSSearchDescriptor getWMSSearchParameter(String sessionID) {
        URL url;
        SAXReader reader = new SAXReader(false);

        try {

            // workaround for wrong dtd location

            EntityResolver resolver = new EntityResolver() {
                public InputSource resolveEntity(String publicId, String systemId) {
                    if (systemId.equals("http://torwald.gistec-online.de/mapbender/php/dtd/portalCommunication.dtd")) {

                        InputStream in = getClass().getResourceAsStream("wms_interface.dtd");

                        return new InputSource(in);
                    }
                    return null;
                }
            };
            reader.setEntityResolver(resolver);

            // END workaround for wrong dtd location

            url = new URL(config.getString("interface_url", "http://localhost/mapbender/php/mod_portalCommunication_gt.php")
                    .concat("?PREQUEST=getWMSSearch").concat("&PHPSESSID=" + sessionID));

            reader.setValidation(false);
            Document document = reader.read(url);

            // check for valid server response
            if (document.selectSingleNode("//portal_communication") == null) {
                throw new Exception("WMS Server Response is not valid!");
            }
            // check for valid server response
            String error = document.valueOf("//portal_communication/error");
            if (error != null && error.length() > 0) {
                throw new Exception("WMS Server Error: " + error);
            }

            // get search type
            String searchType = document.valueOf("//portal_communication/search/@type");
            if (searchType == null || (!searchType.equalsIgnoreCase("bbox") && searchType.equalsIgnoreCase("gkz"))) {
                throw new Exception("WMS Interface: unsupported search type (" + searchType + ")");
            }

            WMSSearchDescriptor wmsSearchDescriptor = new WMSSearchDescriptor();
            if (searchType.equalsIgnoreCase("bbox")) {
                wmsSearchDescriptor.setType(WMSSearchDescriptor.WMS_SEARCH_BBOX);
                wmsSearchDescriptor.setTypeOfCoordinates(document
                        .valueOf("//portal_communication/search/bbox/@cootype"));
                wmsSearchDescriptor.setMinX(Double.parseDouble(document
                        .valueOf("//portal_communication/search/bbox/minx")));
                wmsSearchDescriptor.setMinY(Double.parseDouble(document
                        .valueOf("//portal_communication/search/bbox/miny")));
                wmsSearchDescriptor.setMaxX(Double.parseDouble(document
                        .valueOf("//portal_communication/search/bbox/maxx")));
                wmsSearchDescriptor.setMaxY(Double.parseDouble(document
                        .valueOf("//portal_communication/search/bbox/maxy")));
            } else if (searchType.equalsIgnoreCase("gkz")) {
                wmsSearchDescriptor.setType(WMSSearchDescriptor.WMS_SEARCH_COMMUNITY_CODE);
                wmsSearchDescriptor.setCommunityCode(document.valueOf("//portal_communication/search/gkz"));
            }

            return wmsSearchDescriptor;

        } catch (Exception e) {
            log.error(e);
        }

        return null;
    }

    /**
     * @see de.ingrid.portal.interfaces.wms.WMSInterface#getWMSURL(java.lang.String)
     */
    public String getWMSURL(String sessionId) {
        return config.getString("display_url", "http://localhost/mapbender/frames/WMS_Search.php")
        .concat("?PHPSESSID=" + sessionId);
    }

    /**
     * @see de.ingrid.portal.interfaces.wms.WMSInterface#getWMSAddedServiceURL(de.ingrid.portal.interfaces.wms.om.WMSServiceDescriptor, java.lang.String)
     */
    public String getWMSAddedServiceURL(WMSServiceDescriptor service, String sessionId) {
        ArrayList l = new ArrayList();
        l.add(service);
        return getWMSAddedServiceURL(l, sessionId);
    }

    /**
     * @throws UnsupportedEncodingException 
     * @see de.ingrid.portal.interfaces.wms.WMSInterface#getWMSAddedServiceURL(java.util.ArrayList, java.lang.String)
     */
    public String getWMSAddedServiceURL(ArrayList services, String sessionId) {
        WMSServiceDescriptor service;
        String serviceURL;
        String serviceName;
        StringBuffer resultB = new StringBuffer(getWMSURL(sessionId));
        boolean prequestAdded = false;
        
        // check for invalid service parameter
        if (services == null || services.size() == 0) {
            return resultB.toString();
        }

        try {
            // add services
            for(int i=0; i<services.size();i++) {
                service = (WMSServiceDescriptor) services.get(i);
                serviceURL = service.getUrl();
                serviceName = service.getName();
                if (serviceURL != null && serviceURL.length()>0) {
                    if (!prequestAdded) {
                        resultB.append("&PREQUEST=setServices");
                        prequestAdded = true;
                    }
                    if (serviceName != null && serviceName.length() > 0) {
                        resultB.append("&wmsName" + (i+1) + "=" + URLEncoder.encode(serviceName, "UTF-8"));
                    }
                    resultB.append("&wms" + (i+1) + "=" + URLEncoder.encode(serviceURL.replace('&', ','), "UTF-8"));
                }
            }
        } catch (UnsupportedEncodingException e) {
            log.error(e);
        }
        
        return resultB.toString();
    }

}
