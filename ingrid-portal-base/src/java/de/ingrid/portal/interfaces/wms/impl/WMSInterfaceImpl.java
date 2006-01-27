/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.interfaces.wms.impl;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import de.ingrid.portal.interfaces.wms.WMSInterface;
import de.ingrid.portal.interfaces.wms.om.WMSSearchDescriptor;
import de.ingrid.portal.interfaces.wms.om.WMSServiceDescriptor;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class WMSInterfaceImpl implements WMSInterface {

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
     * @see de.ingrid.portal.interfaces.wms.WMSInterface#getWMSServices()
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

            url = new URL(config.getString("url", "http://localhost/mapbender/php/mod_portalCommunication_gt.php")
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

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

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

            url = new URL(config.getString("url", "http://localhost/mapbender/php/mod_portalCommunication_gt.php")
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

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

}
