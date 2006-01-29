/*
 * Copyright (c) 1997-2006 by wemove GmbH
 */
package de.ingrid.portal.interfaces.wms;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;

import de.ingrid.portal.interfaces.wms.impl.WMSInterfaceImpl;
import de.ingrid.portal.interfaces.wms.om.WMSSearchDescriptor;
import de.ingrid.portal.interfaces.wms.om.WMSServiceDescriptor;

import junit.framework.TestCase;

public class WMSInterfaceTest extends TestCase {

    private WMSInterface wmsInterface;
    private Configuration config;
    
    protected void setUp() throws Exception {
        super.setUp();
        
        config = new BaseConfiguration();
        config.addProperty("display_url", "http://torwald.gistec-online.de/mapbender/frames/WMS_Search.php");
        config.addProperty("interface_url", "http://torwald.gistec-online.de/mapbender/php/mod_portalCommunication_gt.php");
        
        wmsInterface = new WMSInterfaceImpl(config);
        
    }

    /*
     * Test method for 'de.ingrid.portal.interfaces.wms.impl.WMSInterfaceImpl.getWMSURL(String)'
     */
    public void testGetWMSURL() {
        String myUrl = wmsInterface.getWMSURL("session0815");
        assertEquals(config.getProperty("display_url") + "?PHPSESSID=session0815" , myUrl);
    }

    /*
     * Test method for 'de.ingrid.portal.interfaces.wms.impl.WMSInterfaceImpl.getWMSAddedServiceURL(WMSServiceDescriptor, String)'
     */
    public void testGetWMSAddedServiceURLWMSServiceDescriptorString() throws UnsupportedEncodingException {
        WMSServiceDescriptor descr = new WMSServiceDescriptor("My WMS Service Name", "http://wms1.ccgis.de/cgi-bin/mapserv?map=/data/umn/world/world_map.map&REQUEST=GetCapabilities&SERVICE=wms&VERSION=1.1.0");
        String myUrl = wmsInterface.getWMSAddedServiceURL(descr, "session0815");
        assertEquals(config.getProperty("display_url") + "?PHPSESSID=session0815&PREQUEST=setServices&wmsName1=" + URLEncoder.encode("My WMS Service Name", "UTF-8") + "&wms1=" + URLEncoder.encode("http://wms1.ccgis.de/cgi-bin/mapserv?map=/data/umn/world/world_map.map,REQUEST=GetCapabilities,SERVICE=wms,VERSION=1.1.0", "UTF-8") , myUrl);
    }

    /*
     * Test method for 'de.ingrid.portal.interfaces.wms.impl.WMSInterfaceImpl.getWMSAddedServiceURL(ArrayList, String)'
     */
    public void testGetWMSAddedServiceURLArrayListString() throws UnsupportedEncodingException {
        ArrayList a = new ArrayList();
        WMSServiceDescriptor descr = new WMSServiceDescriptor("My WMS Service Name", "http://wms1.ccgis.de/cgi-bin/mapserv?map=/data/umn/world/world_map.map&REQUEST=GetCapabilities&SERVICE=wms&VERSION=1.1.0");
        a.add(descr);
        descr = new WMSServiceDescriptor("My WMS Service Name 2", "http://wms1.ccgis.de/cgi-bin/mapserv?map=/data/umn/world/world_map.map&REQUEST=GetCapabilities&SERVICE=wms&VERSION=2.2.2");
        a.add(descr);
        
        String myUrl = wmsInterface.getWMSAddedServiceURL(a, "session0815");
        assertEquals(config.getProperty("display_url") 
                + "?PHPSESSID=session0815&PREQUEST=setServices&wmsName1=" 
                + URLEncoder.encode("My WMS Service Name", "UTF-8") 
                + "&wms1=" 
                + URLEncoder.encode("http://wms1.ccgis.de/cgi-bin/mapserv?map=/data/umn/world/world_map.map,REQUEST=GetCapabilities,SERVICE=wms,VERSION=1.1.0", "UTF-8") 
                + "&wmsName2=" 
                + URLEncoder.encode("My WMS Service Name 2", "UTF-8") 
                + "&wms2=" 
                + URLEncoder.encode("http://wms1.ccgis.de/cgi-bin/mapserv?map=/data/umn/world/world_map.map,REQUEST=GetCapabilities,SERVICE=wms,VERSION=2.2.2", "UTF-8") , myUrl);

    }
    
    public void testGetWMSServices() {
        WMSServiceDescriptor descr = new WMSServiceDescriptor("WMS Test-Service (con terra GmbH)", "http://www.conterra.de/wmsconnector/gdi/brd?REQUEST=GetCapabilities&SERVICE=wms&VERSION=1.1.0");
        String sessionId = Integer.toString((int)(Math.random() * 10000000));
        String myUrl = wmsInterface.getWMSAddedServiceURL(descr, sessionId);
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
        Iterator it = services.iterator();
        if (it.hasNext()) {
            WMSServiceDescriptor service = (WMSServiceDescriptor) it.next();
            // name is not set correctly when only the frame definition page is loaded
            //assertEquals(descr.getName(), service.getName() );
            assertEquals(descr.getUrl(), service.getUrl());
        } else {
            assertEquals(true, false);
        }
        
    }
    
    public void testGetWMSSearchParameter() {
/*
        String sessionId = Integer.toString((int)(Math.random() * 10000000));
        WMSServiceDescriptor descr = new WMSServiceDescriptor("WMS Test-Service (con terra GmbH)", "http://www.conterra.de/wmsconnector/gdi/brd?REQUEST=GetCapabilities&SERVICE=wms&VERSION=1.1.0");
        String myUrl = wmsInterface.getWMSAddedServiceURL(descr, sessionId);
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
        WMSSearchDescriptor sdescr = wmsInterface.getWMSSearchParameter(sessionId);
*/        
    }
    

}
