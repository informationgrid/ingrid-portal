/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
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
 * Copyright (c) 1997-2006 by wemove GmbH
 */
package de.ingrid.portal.interfaces.wms;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import junit.framework.TestCase;

import org.apache.commons.configuration.Configuration;

import de.ingrid.portal.interfaces.WMSInterface;
import de.ingrid.portal.interfaces.impl.WMSInterfaceImpl;
import de.ingrid.portal.interfaces.om.WMSServiceDescriptor;

public class WMSInterfaceTest extends TestCase {

    private WMSInterface wmsInterface;

    private Configuration config;

    protected void setUp() throws Exception {
        super.setUp();

        wmsInterface = WMSInterfaceImpl.getInstance();
        config = wmsInterface.getConfig();

    }

    /*
     * Test method for
     * 'de.ingrid.portal.interfaces.wms.impl.WMSInterfaceImpl.getWMSViewerURL(String)'
     */
    public void testGetWMSViewerURL() {
        String myUrl = wmsInterface.getWMSViewerURL("session0815", true, new Locale("de"));
        if (config.getProperty("display_viewer_url").toString().indexOf("?") > 0) {
        	assertEquals(config.getProperty("display_viewer_url") + "&PHPSESSID=session0815&lang=de", myUrl);
        } else {
        	assertEquals(config.getProperty("display_viewer_url") + "?PHPSESSID=session0815&lang=de", myUrl);
        }
    }

    /*
     * Test method for
     * 'de.ingrid.portal.interfaces.wms.impl.WMSInterfaceImpl.getWMSSearchURL(String)'
     */
    public void testGetWMSSearchURL() {
        String myUrl = wmsInterface.getWMSSearchURL("session0815", true, new Locale("de"));
        if (config.getProperty("display_search_url").toString().indexOf("?") > 0) {
        	assertEquals(config.getProperty("display_search_url") + "&PHPSESSID=session0815&lang=de", myUrl);
        } else {
            assertEquals(config.getProperty("display_search_url") + "?PHPSESSID=session0815&lang=de", myUrl);
        }
    }

    /*
     * Test method for
     * 'de.ingrid.portal.interfaces.wms.impl.WMSInterfaceImpl.getWMSAddedServiceURL(WMSServiceDescriptor,
     * String)'
     */
    public void testGetWMSAddedServiceURLWMSServiceDescriptorString() throws UnsupportedEncodingException {
        WMSServiceDescriptor descr = new WMSServiceDescriptor(
                "My WMS Service Name",
                "http://wms1.ccgis.de/cgi-bin/mapserv?map=/data/umn/world/world_map.map&REQUEST=GetCapabilities&SERVICE=wms&VERSION=1.1.0");
        String myUrl = wmsInterface.getWMSAddedServiceURL(descr, "session0815", true, new Locale("de"), true);
        String request = "COMMAND=addWMS";
        String transmittedWmsUrl = "http://wms1.ccgis.de/cgi-bin/mapserv?map=/data/umn/world/world_map.map&REQUEST=GetCapabilities&SERVICE=wms&VERSION=1.1.0";
        if (wmsInterface.getMapbenderVersion().equals(wmsInterface.MAPBENDER_VERSION_2_1)) {
        	request = "PREQUEST=setServices";
            transmittedWmsUrl = "http://wms1.ccgis.de/cgi-bin/mapserv?map=/data/umn/world/world_map.map,REQUEST=GetCapabilities,SERVICE=wms,VERSION=1.1.0";
        }
        if (config.getProperty("display_viewer_url").toString().indexOf("?") > 0) {
	        assertEquals(
	                config.getProperty("display_viewer_url")
	                        + "&PHPSESSID=session0815&lang=de&" + request + "&wmsName1="
	                        + URLEncoder.encode("My WMS Service Name", "UTF-8")
	                        + "&wms1="
	                        + URLEncoder
	                                .encode(
	                                		transmittedWmsUrl,
	                                        "UTF-8"), myUrl);
        } else {
	        assertEquals(
	                config.getProperty("display_viewer_url")
	                        + "?PHPSESSID=session0815&lang=de&" + request + "&wmsName1="
	                        + URLEncoder.encode("My WMS Service Name", "UTF-8")
	                        + "&wms1="
	                        + URLEncoder
	                                .encode(
	                                		transmittedWmsUrl,
	                                        "UTF-8"), myUrl);
        }
        
    }

    /*
     * Test method for
     * 'de.ingrid.portal.interfaces.wms.impl.WMSInterfaceImpl.getWMSAddedServiceURL(ArrayList,
     * String)'
     */
    public void testGetWMSAddedServiceURLArrayListString() throws UnsupportedEncodingException {
        ArrayList a = new ArrayList();
        WMSServiceDescriptor descr = new WMSServiceDescriptor(
                "My WMS Service Name",
                "http://wms1.ccgis.de/cgi-bin/mapserv?map=/data/umn/world/world_map.map&REQUEST=GetCapabilities&SERVICE=wms&VERSION=1.1.0");
        a.add(descr);
        descr = new WMSServiceDescriptor(
                "My WMS Service Name 2",
                "http://wms1.ccgis.de/cgi-bin/mapserv?map=/data/umn/world/world_map.map&REQUEST=GetCapabilities&SERVICE=wms&VERSION=2.2.2");
        a.add(descr);

        String myUrl = wmsInterface.getWMSAddedServiceURL(a, "session0815", true, new Locale("de"), true);
        String request = "COMMAND=addWMS";
        String transmittedWmsUrl1 = "http://wms1.ccgis.de/cgi-bin/mapserv?map=/data/umn/world/world_map.map&REQUEST=GetCapabilities&SERVICE=wms&VERSION=1.1.0";
        String transmittedWmsUrl2 = "http://wms1.ccgis.de/cgi-bin/mapserv?map=/data/umn/world/world_map.map&REQUEST=GetCapabilities&SERVICE=wms&VERSION=2.2.2";
        if (wmsInterface.getMapbenderVersion().equals(wmsInterface.MAPBENDER_VERSION_2_1)) {
        	request = "PREQUEST=setServices";
            transmittedWmsUrl1 = "http://wms1.ccgis.de/cgi-bin/mapserv?map=/data/umn/world/world_map.map,REQUEST=GetCapabilities,SERVICE=wms,VERSION=1.1.0";
            transmittedWmsUrl2 = "http://wms1.ccgis.de/cgi-bin/mapserv?map=/data/umn/world/world_map.map,REQUEST=GetCapabilities,SERVICE=wms,VERSION=2.2.2";
        }

        if (config.getProperty("display_viewer_url").toString().indexOf("?") > 0) {
	        assertEquals(
	                config.getProperty("display_viewer_url")
	                        + "&PHPSESSID=session0815&lang=de&" + request + "&wmsName1="
	                        + URLEncoder.encode("My WMS Service Name", "UTF-8")
	                        + "&wms1="
	                        + URLEncoder
	                                .encode(
	                                		transmittedWmsUrl1,
	                                        "UTF-8")
	                        + "&wmsName2="
	                        + URLEncoder.encode("My WMS Service Name 2", "UTF-8")
	                        + "&wms2="
	                        + URLEncoder
	                                .encode(
	                                		transmittedWmsUrl2,
	                                        "UTF-8"), myUrl);
        } else {
	        assertEquals(
	                config.getProperty("display_viewer_url")
	                        + "?PHPSESSID=session0815&lang=de&" + request + "&wmsName1="
	                        + URLEncoder.encode("My WMS Service Name", "UTF-8")
	                        + "&wms1="
	                        + URLEncoder
	                                .encode(
	                                		transmittedWmsUrl1,
	                                        "UTF-8")
	                        + "&wmsName2="
	                        + URLEncoder.encode("My WMS Service Name 2", "UTF-8")
	                        + "&wms2="
	                        + URLEncoder
	                                .encode(
	                                		transmittedWmsUrl2,
	                                        "UTF-8"), myUrl);
        }

    }

    public void testGetWMSServices() {
        WMSServiceDescriptor descr = new WMSServiceDescriptor("WMS Test-Service (con terra GmbH)",
                "http://www.conterra.de/wmsconnector/gdi/brd?REQUEST=GetCapabilities&SERVICE=wms&VERSION=1.1.0");
        String sessionId = Integer.toString((int) (Math.random() * 10000000));
        String myUrl = wmsInterface.getWMSAddedServiceURL(descr, sessionId, true, new Locale("de"), true);
        URL url;
        try {
            url = new URL(myUrl);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String line = new String();
            StringBuffer page = new StringBuffer();
            while ((line = in.readLine()) != null) {
                page.append(line);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Collection services = wmsInterface.getWMSServices(sessionId);

        // check for failed communication with the WMS Server
        if (services != null) {
            Iterator it = services.iterator();
            if (it.hasNext()) {
                WMSServiceDescriptor service = (WMSServiceDescriptor) it.next();
                // name is not set correctly when only the frame definition page
                // is loaded
                // assertEquals(descr.getName(), service.getName() );
                assertEquals(descr.getUrl(), service.getUrl());
            } else {
                assertEquals(true, false);
            }
        } else {
            System.out.println("Failed to contact the WMS Server!");
        }
    }

    public void testGetWMSSearchParameter() {
        /*
         * String sessionId = Integer.toString((int)(Math.random() * 10000000));
         * WMSServiceDescriptor descr = new WMSServiceDescriptor("WMS
         * Test-Service (con terra GmbH)",
         * "http://www.conterra.de/wmsconnector/gdi/brd?REQUEST=GetCapabilities&SERVICE=wms&VERSION=1.1.0");
         * String myUrl = wmsInterface.getWMSAddedServiceURL(descr, sessionId);
         * URL url; try { url = new URL(myUrl); BufferedReader in = new
         * BufferedReader(new InputStreamReader(url.openStream())); String line =
         * new String(); StringBuffer page = new StringBuffer(); while ((line =
         * in.readLine()) != null) { page.append(line); } in.close(); } catch
         * (Exception e) { e.printStackTrace(); } WMSSearchDescriptor sdescr =
         * wmsInterface.getWMSSearchParameter(sessionId);
         */
    }

}
