/*
 * Copyright (c) 1997-2006 by wemove GmbH
 */
package de.ingrid.portal.interfaces.wms;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;

import de.ingrid.portal.interfaces.wms.impl.WMSInterfaceImpl;
import de.ingrid.portal.interfaces.wms.om.WMSServiceDescriptor;
import junit.framework.TestCase;

public class WMSInterfaceTest extends TestCase {

    private WMSInterface wmsInterface;
    private Configuration config;
    
    protected void setUp() throws Exception {
        super.setUp();
        
        config = new BaseConfiguration();
        config.addProperty("display_url", "http://localhost/mapbender/frames/WMS_Search.php");
        config.addProperty("base_url", "http://localhost/mapbender/php/mod_portalCommunication_gt.php");
        
        wmsInterface = new WMSInterfaceImpl(config);
        
    }

    /*
     * Test method for 'de.ingrid.portal.interfaces.wms.impl.WMSInterfaceImpl.getWMSURL(String)'
     */
    public void testGetWMSURL() {
        String myUrl = wmsInterface.getWMSURL("my_session_id");
        assertEquals(config.getProperty("display_url") + "?PHPSESSID=my_session_id" , myUrl);
    }

    /*
     * Test method for 'de.ingrid.portal.interfaces.wms.impl.WMSInterfaceImpl.getWMSAddedServiceURL(WMSServiceDescriptor, String)'
     */
    public void testGetWMSAddedServiceURLWMSServiceDescriptorString() throws UnsupportedEncodingException {
        WMSServiceDescriptor descr = new WMSServiceDescriptor("My WMS Service Name", "http://wms1.ccgis.de/cgi-bin/mapserv?map=/data/umn/world/world_map.map&REQUEST=GetCapabilities&SERVICE=wms&VERSION=1.1.0");
        String myUrl = wmsInterface.getWMSAddedServiceURL(descr, "my_session_id");
        assertEquals(config.getProperty("display_url") + "?PHPSESSID=my_session_id&PREQUEST=setServices&wmsName1=" + URLEncoder.encode("My WMS Service Name", "UTF-8") + "&wms1=" + URLEncoder.encode("http://wms1.ccgis.de/cgi-bin/mapserv?map=/data/umn/world/world_map.map,REQUEST=GetCapabilities,SERVICE=wms,VERSION=1.1.0", "UTF-8") , myUrl);
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
        
        String myUrl = wmsInterface.getWMSAddedServiceURL(a, "my_session_id");
        assertEquals(config.getProperty("display_url") 
                + "?PHPSESSID=my_session_id&PREQUEST=setServices&wmsName1=" 
                + URLEncoder.encode("My WMS Service Name", "UTF-8") 
                + "&wms1=" 
                + URLEncoder.encode("http://wms1.ccgis.de/cgi-bin/mapserv?map=/data/umn/world/world_map.map,REQUEST=GetCapabilities,SERVICE=wms,VERSION=1.1.0", "UTF-8") 
                + "&wmsName2=" 
                + URLEncoder.encode("My WMS Service Name 2", "UTF-8") 
                + "&wms2=" 
                + URLEncoder.encode("http://wms1.ccgis.de/cgi-bin/mapserv?map=/data/umn/world/world_map.map,REQUEST=GetCapabilities,SERVICE=wms,VERSION=2.2.2", "UTF-8") , myUrl);

    }

}
