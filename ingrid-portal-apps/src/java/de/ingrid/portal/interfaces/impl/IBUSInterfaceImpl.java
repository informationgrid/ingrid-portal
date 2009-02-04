/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.interfaces.impl;

import java.net.URL;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.cfg.Environment;

import de.ingrid.ibus.client.BusClient;
import de.ingrid.ibus.client.BusClientFactory;
import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.interfaces.IBUSInterface;
import de.ingrid.portal.search.UtilsSearch;
import de.ingrid.utils.IBus;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.PlugDescription;
import de.ingrid.utils.dsc.Record;
import de.ingrid.utils.query.IngridQuery;

/**
 * This class abstracts the real iBus.
 *
 * @author joachim@wemove.com
 */
public class IBUSInterfaceImpl implements IBUSInterface {

    private final static Log log = LogFactory.getLog(IBUSInterfaceImpl.class);

    private static IBUSInterfaceImpl instance = null;

    private static IBus bus = null;

    static BusClient client = null;
    
    public static synchronized IBUSInterface getInstance() {
        if (instance == null) {
            try {
                instance = new IBUSInterfaceImpl();
            } catch (Exception e) {
                log.fatal("Error initiating the iBus interface.", e);
            }
        }
        
        return instance;
    }

    public static synchronized void shutdown() {
        try {
            if (log.isInfoEnabled()) {
                log.info("SHUT DOWN IBUSInterface!");
            }
            client.close();
            
        } catch (Throwable t) {
            if (log.isErrorEnabled()) {
                log.error("Problems SHUTTING DOWN IBUSInterface", t);
            }
        } finally {
            if (instance != null) {
                instance = null;
            }
        }
    }

    private IBUSInterfaceImpl() throws Exception {
        super();
        try {
            client = BusClientFactory.createBusClient();
            
            // check caching properties
            // associate a bus with and without cache form the client
            PortalConfig config = PortalConfig.getInstance();
            if (config.getBoolean("portal.enable.caching", true) == true) {
            	bus = client.getCacheableIBus();
            } else {
            	bus = client.getNonCacheableIBus();
            }
            
            if (bus == null) {
                throw new Exception("FATAL ERROR! iBus == null, FAILED to create bus instance.");
            }

        } catch (Throwable t) {
            if (log.isFatalEnabled()) {
                log.fatal("Problems Constructor IBUSInterfaceImpl Singleton", t);
            }
            shutdown();
            throw new Exception("Error Constructor IBUSInterfaceImpl", t);
        }
    }

    /**
     * @see de.ingrid.portal.interfaces.IBUSInterface#getConfig()
     */
    public Configuration getConfig() {
    	return null;
    }
    
    /**
     * @throws Exception 
     * @see de.ingrid.portal.interfaces.IBUSInterface#search(de.ingrid.utils.query.IngridQuery, int, int, int, int)
     */
    public IngridHits search(IngridQuery query, int hitsPerPage, int currentPage, int startHit, int timeout)
            throws Exception {
        IngridHits hits = null;

        // FIXME AW: add cache option to query
        
        
        try {
            if (log.isDebugEnabled()) {
                log.debug("iBus.search: IngridQuery = " + UtilsSearch.queryToString(query)
                		+ " / timeout=" + timeout + ", hitsPerPage=" + hitsPerPage 
                		+ ", currentPage=" + currentPage + ", startHit=" + startHit);
            }
            long start = System.currentTimeMillis();
            hits = bus.search(query, hitsPerPage, currentPage, startHit, timeout);
            long duration = System.currentTimeMillis() - start;
            
            if (log.isDebugEnabled()) {
                log.debug("iBus.search: finished !");
                log.debug("in " + duration + "ms");
            }
        } catch (java.io.IOException e) {
            if (log.isDebugEnabled()) {
                log.debug("Problems doing iBus search, query=" + UtilsSearch.queryToString(query) + " / timeout="
                        + timeout + ", hitsPerPage=" + hitsPerPage + ", currentPage="
                        + currentPage + ", startHit=" + startHit, e);
            } else if (log.isInfoEnabled()) {
                log.info("Problems doing iBus search, query=" + UtilsSearch.queryToString(query) + " / timeout="
                        + timeout + ", hitsPerPage=" + hitsPerPage + ", currentPage="
                        + currentPage + ", startHit=" + startHit + "[cause:" + e.getMessage() + "]");
            } else {
                log.warn("Problems doing iBus search, query=" + UtilsSearch.queryToString(query) + " / timeout="
                        + timeout + ", hitsPerPage=" + hitsPerPage + ", currentPage="
                        + currentPage + ", startHit=" + startHit + "[cause:" + e.getCause().getMessage() + "]", e);
            }
        } catch (Throwable t) {
            if (log.isErrorEnabled()) {
                log.error("Problems doing iBus search, query=" + UtilsSearch.queryToString(query) + " / timeout="
                        + timeout + ", hitsPerPage=" + hitsPerPage + ", currentPage="
                        + currentPage + ", startHit=" + startHit, t);
            }
            throw new Exception(t);
        }

        
        return hits;
    }

    /**
     * @see de.ingrid.portal.interfaces.IBUSInterface#getDetail(de.ingrid.utils.IngridHit,
     * de.ingrid.utils.query.IngridQuery, java.lang.String[])
     */
    public IngridHitDetail getDetail(IngridHit result, IngridQuery query, String[] requestedFields) {
        IngridHitDetail detail 	= null;
        
        try {
            if (log.isDebugEnabled()) {
                log.debug("iBus.getDetail: hit = " + result + ", requestedFields = " 
                		+ requestedFields);
            }
            long start = System.currentTimeMillis();
            detail = bus.getDetail(result, query, requestedFields);
            long duration = System.currentTimeMillis() - start;
            
            if (log.isDebugEnabled()) {
                log.debug("iBus.getDetail: finished !");
                log.debug("in " + duration + "ms");
                if (detail==null)
                	log.debug("DETAIL IS NULL !!!!!!!!!!!");
            }
        } catch (Throwable t) {
            if (log.isDebugEnabled()) {
                log.debug("Problems fetching Detail of results: " + result 
                		+ "[cause:" + t.getCause().getMessage() + "]", t);
            } else if (log.isInfoEnabled()) {
                log.info("Problems fetching Detail of results: " + result
                		+ "[cause:" + t.getCause().getMessage() + "]");
            } else {
                log.warn("Problems fetching Detail of results: " + result 
                		+ "[cause:" + t.getCause().getMessage() + "]", t);
            }
        }

        return detail;
    }

    /**
     * @see de.ingrid.portal.interfaces.IBUSInterface#getDetails(de.ingrid.utils.IngridHit[], de.ingrid.utils.query.IngridQuery, java.lang.String[])
     */
    public IngridHitDetail[] getDetails(IngridHit[] results, IngridQuery query, String[] requestedFields) {
        IngridHitDetail[] details 	= null;
        
        try {
            if (log.isDebugEnabled()) {
                log.debug("iBus.getDetails: hits = " + results + ", requestedFields = " + requestedFields);
            }
            long start = System.currentTimeMillis();
            details = bus.getDetails(results, query, requestedFields);
            long duration = System.currentTimeMillis() - start;
            
            if (log.isDebugEnabled()) {
                log.debug("iBus.getDetails: finished !");
                log.debug("in " + duration + "ms");
            }
        } catch (Throwable t) {
            if (log.isDebugEnabled()) {
                log.debug("Problems fetching Details of results: " + results
                		+ "[cause:" + t.getMessage() + "]", t);
            } else if (log.isInfoEnabled()) {
                log.info("Problems fetching Details of results: " + results
                		+ "[cause:" + t.getMessage() + "]");
            } else {
                log.warn("Problems fetching Details of results: " + results
                		+ "[cause:" + t.getMessage() + "]", t);
            }
        }

        return details;
    }

    /**
     * @see de.ingrid.portal.interfaces.IBUSInterface#getRecord(de.ingrid.utils.IngridHit)
     */
    public Record getRecord(IngridHit result) {
        Record rec 		= null;
        
        if (rec == null) {
	        try {
	            rec = bus.getRecord(result);
	        } catch (Throwable t) {
	            if (log.isDebugEnabled()) {
	                log.debug("Problems fetching Record of result: " + result
	                		+ "[cause:" + t.getCause() + "]", t);
	            } else if (log.isInfoEnabled()) {
	                log.info("Problems fetching Record of result: " + result
	                		+ "[cause:" + t.getCause() + "]");
	            } else {
	                log.warn("Problems fetching Record of result: " + result
	                		+ "[cause:" + t.getCause() + "]", t);
	            }
	        }
        } else {
        	if (log.isDebugEnabled()) {
        		log.debug("Got record from cache :)");
        	}
        }

        return rec;
    }

    private static String getResourceAsStream(String resource) throws Exception {
        String stripped = resource.startsWith("/") ? resource.substring(1) : resource;

        String stream = null;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader != null) {
            URL url = classLoader.getResource(stripped);
            if (url != null) {
                stream = url.toString();
            }
        }
        if (stream == null) {
            Environment.class.getResourceAsStream(resource);
        }
        if (stream == null) {
            URL url = Environment.class.getClassLoader().getResource(stripped);
            if (url != null) {
                stream = url.toString();
            }
        }
        if (stream == null) {
            throw new Exception(resource + " not found");
        }
        return stream;
    }

     /**
     * @see de.ingrid.portal.interfaces.IBUSInterface#getIPlug(java.lang.String)
     */
    public PlugDescription getIPlug(String plugId) {
        PlugDescription pd 	= null;
        
        // else ask the bus and store the result inside the cache
        try {
        	log.debug("get iPlugDescription: start");
        	long start = System.currentTimeMillis();
        	pd = bus.getIPlug(plugId);
        	long duration = System.currentTimeMillis() - start;
        	log.debug("finished in " + duration + "ms");
	    } catch (Throwable t) {
	        if (log.isWarnEnabled()) {
	            log.warn("Problems fetching iPlug from iBus !", t);
	        }            
	    }
        
	    return pd;
    }

    /**
     * @see de.ingrid.portal.interfaces.IBUSInterface#getAllIPlugs()
     */
    public PlugDescription[] getAllIPlugs() {
        PlugDescription[] plugs = new PlugDescription[0];
        
        // FIXME AW: caching true here?
        //IBus bus 				= bus_cached;
        
        try {
            plugs = bus.getAllIPlugs();
        } catch (Throwable t) {
            if (log.isWarnEnabled()) {
                log.warn("Problems fetching iPlugs from iBus !", t);
            }            
        }
        return plugs;
    }

    public PlugDescription[] getAllIPlugsWithoutTimeLimitation() {
        PlugDescription[] plugs = new PlugDescription[0];
        
        // FIXME AW: caching true here?
        //IBus bus 				= bus_cached;
        
        try {
            plugs = bus.getAllIPlugsWithoutTimeLimitation();
        } catch (Throwable t) {
            if (log.isWarnEnabled()) {
                log.warn("Problems fetching iPlugs from iBus !", t);
            }            
        }
        return plugs;
    }
}
