/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.interfaces.ibus.impl;

import net.weta.components.communication_sockets.SocketCommunication;
import net.weta.components.communication_sockets.util.AddressUtil;
import net.weta.components.proxies.ProxyService;
import net.weta.components.proxies.remote.RemoteInvocationController;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.cfg.Environment;

import de.ingrid.ibus.Bus;
import de.ingrid.portal.interfaces.ibus.IBUSInterface;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.dsc.Record;
import de.ingrid.utils.query.IngridQuery;

/**
 * TODO Describe your created type (class, etc.) here.
 *
 * @author joachim@wemove.com
 */
public class IBUSInterfaceImpl implements IBUSInterface {

    private final static Log log = LogFactory.getLog(IBUSInterfaceImpl.class);
    
    private static IBUSInterfaceImpl instance = null;
    
    private static Bus bus = null;
    
    private Configuration config;

    public static synchronized IBUSInterface getInstance() {
        if (instance == null) {
            try {
                instance = new IBUSInterfaceImpl();
            } catch (Exception e) {
                log.fatal("Error initiating the WMS interface.");
                e.printStackTrace();
            }
        }
        
        return instance;
    }
    
    private IBUSInterfaceImpl() throws Exception {
        super();
        String configFilename = getResourceAsStream("/ibus_interface.properties");
        config = new PropertiesConfiguration(configFilename);

        SocketCommunication communication = null;
        ProxyService proxy = null;
        try{
            communication = new SocketCommunication();
            
            communication.setMulticastPort(Integer.parseInt(config.getString("multicast_port", "11114")));
            communication.setUnicastPort(Integer.parseInt(config.getString("unicast_port", "50000")));    
            
            communication.startup();
                
            // start the proxy server
             proxy = new ProxyService();
            
            proxy.setCommunication(communication);
            proxy.startup();
            

            String iBusUrl = AddressUtil.getWetagURL(config.getString("ibus_server", "localhost"), Integer.parseInt(config.getString("ibus_port", "11112")));
            RemoteInvocationController ric = proxy.createRemoteInvocationController(iBusUrl);
            bus = (Bus) ric.invoke(Bus.class, Bus.class.getMethod("getInstance", null), null);
        } catch (Throwable t){
            proxy.shutdown();
            communication.shutdown();
            throw new Exception(t);
        }
    }
    
    /**
     * @see de.ingrid.portal.interfaces.ibus.IBUSInterface#getConfig()
     */
    public Configuration getConfig() {
        return config;
    }

    /**
     * @throws Exception 
     * @see de.ingrid.portal.interfaces.ibus.IBUSInterface#search(de.ingrid.utils.query.IngridQuery, int, int, int, int)
     */
    public IngridHits search(IngridQuery query, int hitsPerPage, int currentPage, int requestedHits, int timeout) throws Exception {
        return bus.search(query, hitsPerPage, currentPage, requestedHits, timeout);
    }

    /**
     * @throws Exception 
     * @see de.ingrid.portal.interfaces.ibus.IBUSInterface#getDetails(de.ingrid.utils.IngridHit, de.ingrid.utils.query.IngridQuery)
     */
    public IngridHitDetail getDetails(IngridHit result, IngridQuery query) throws Exception {
        return bus.getDetails(result, query);
    }

    /**
     * @throws Exception 
     * @see de.ingrid.portal.interfaces.ibus.IBUSInterface#getRecord(de.ingrid.utils.IngridHit)
     */
    public Record getRecord(IngridHit hit) throws Exception {
        return bus.getRecord(hit);
    }

    private static String getResourceAsStream(String resource) throws Exception {
        String stripped = resource.startsWith("/") ? 
                resource.substring(1) : resource;
    
        String stream = null; 
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader!=null) {
            stream = classLoader.getResource( stripped ).toString();
        }
        if ( stream == null ) {
            Environment.class.getResourceAsStream( resource );
        }
        if ( stream == null ) {
            stream = Environment.class.getClassLoader().getResource( stripped ).toString();
        }
        if ( stream == null ) {
            throw new Exception( resource + " not found" );
        }
        return stream;
    }
    
    
}
