/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2015 wemove digital solutions GmbH
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
package de.ingrid.portal.interfaces.impl;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.cfg.Environment;

import de.ingrid.ibus.client.BusClient;
import de.ingrid.ibus.client.BusClientFactory;
import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.interfaces.IBUSInterface;
import de.ingrid.portal.search.UtilsSearch;
import de.ingrid.utils.DeepUtil;
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

    private final static Logger log = LoggerFactory.getLogger(IBUSInterfaceImpl.class);

    private static IBUSInterfaceImpl instance = null;

    private static IBus bus = null;

    static BusClient client = null;
    
    private static boolean cache = false;
    
    public static synchronized IBUSInterface getInstance() {
        if (instance == null) {
            try {
                instance = new IBUSInterfaceImpl();
            } catch (Exception e) {
                log.error("Error initiating the iBus interface.", e);
            }
        }
        
        return instance;
    }

    public static synchronized void shutdown() {
        try {
            if (log.isInfoEnabled()) {
                log.info("SHUT DOWN IBUSInterface!");
            }
            client.shutdown();
            
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
            	cache = true;
            } else {
            	bus = client.getNonCacheableIBus();
            }
            
            if (bus == null) {
                throw new Exception("FATAL ERROR! iBus == null, FAILED to create bus instance.");
            }

        } catch (Throwable t) {
            if (log.isErrorEnabled()) {
                log.error("Problems Constructor IBUSInterfaceImpl Singleton", t);
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

        injectCache(query);
        
        try {
            if (log.isDebugEnabled()) {
                log.debug("iBus.search: IngridQuery = " + UtilsSearch.queryToString(query)
                		+ " / timeout=" + timeout + ", hitsPerPage=" + hitsPerPage 
                		+ ", currentPage=" + currentPage + ", startHit=" + startHit);
            }
            long start = System.currentTimeMillis();
            
            hits = bus.search(query, hitsPerPage, currentPage, startHit, timeout);
            
            if (log.isDebugEnabled()) {
            	long duration = System.currentTimeMillis() - start;
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
     * Calling the searchAndDetail method at the iBus doing one call to the bus instead of two. Returned
     * are the IngridHitDetails.
     */
    public IngridHits searchAndDetail(IngridQuery query, int hitsPerPage, int currentPage, int startHit, int timeout, String[] reqParameter)
    throws Exception {
    	IngridHits hits = null;
    	try {
            if (log.isDebugEnabled()) {
                log.debug("iBus.search: IngridQuery = " + UtilsSearch.queryToString(query)
                		+ " / timeout=" + timeout + ", hitsPerPage=" + hitsPerPage 
                		+ ", currentPage=" + currentPage + ", startHit=" + startHit);
            }
            long start = System.currentTimeMillis();
            
            hits = bus.searchAndDetail(query, hitsPerPage, currentPage, startHit, timeout, reqParameter);
            
            if (log.isDebugEnabled()) {
            	long duration = System.currentTimeMillis() - start;
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
        
        injectCache(query);
        
        String s = DeepUtil.deepString(query, 1);
        
        try {
            if (log.isDebugEnabled()) {
                log.debug("iBus.getDetail: hit = " + result + ", requestedFields = " 
                		+ requestedFields);
            }
            long start = System.currentTimeMillis();
            detail = bus.getDetail(result, query, requestedFields);
            
            if (log.isDebugEnabled()) {
                long duration = System.currentTimeMillis() - start;
                log.debug("iBus.getDetail: finished !");
                log.debug("in " + duration + "ms");
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
        
        injectCache(query);
        
        String s = DeepUtil.deepString(query, 1);
        
        try {
            if (log.isDebugEnabled()) {
                log.debug("iBus.getDetails: IngridQuery = '" + UtilsSearch.queryToString(query)
                		+ "', hits = " + results + ", requestedFields = " + requestedFields);
            }
            long start = System.currentTimeMillis();
            details = bus.getDetails(results, query, requestedFields);
            
            if (log.isDebugEnabled()) {
            	long duration = System.currentTimeMillis() - start;
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
        
        injectCache(result);
        
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
        long start 			= 0;
        
        // else ask the bus and store the result inside the cache
        try {
        	if (log.isDebugEnabled()) {
        		log.debug("get iPlugDescription ("+plugId+"): start");
        		start = System.currentTimeMillis();
        	}
        	
        	pd = bus.getIPlug(plugId);
        	
        	if (log.isDebugEnabled()) {
        		long duration = System.currentTimeMillis() - start;
        		log.debug("finished in " + duration + "ms");
        	}
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
        
        try {
            plugs = bus.getAllIPlugs();
        } catch (Throwable t) {
            if (log.isWarnEnabled()) {
                log.warn("Problems fetching iPlugs from iBus !", t);
            }            
        }
        return plugs;
    }
    
    /**
     * Get all iPlugs that are active.
     */
    public PlugDescription[] getAllActiveIPlugs() {
        PlugDescription[] plugs = getAllIPlugs();
        ArrayList<PlugDescription> activePlugs = new ArrayList<PlugDescription>();
        
        for (PlugDescription plugDescription : plugs) {
			if (plugDescription.isActivate()) {
				activePlugs.add(plugDescription);
			}
		}
        
        return activePlugs.toArray(new PlugDescription[0]);
    }

    public PlugDescription[] getAllIPlugsWithoutTimeLimitation() {
        PlugDescription[] plugs = new PlugDescription[0];
        
        try {
            plugs = bus.getAllIPlugsWithoutTimeLimitation();
        } catch (Throwable t) {
            if (log.isWarnEnabled()) {
                log.warn("Problems fetching iPlugs from iBus !", t);
            }
        }
        return plugs;
    }
    
    private void injectCache(HashMap map) {
    	if (!map.containsKey("cache")) {
	    	if (cache) {
	    		map.put("cache", "on");
	        } else {
	        	map.put("cache", "off");
	        }
    	}
    }
}
