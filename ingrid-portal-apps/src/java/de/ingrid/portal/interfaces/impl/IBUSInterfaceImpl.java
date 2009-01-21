/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.interfaces.impl;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.cfg.Environment;

import de.ingrid.ibus.client.BusClient;
import de.ingrid.portal.global.UtilsString;
import de.ingrid.portal.interfaces.IBUSInterface;
import de.ingrid.portal.search.QueryPreProcessor;
import de.ingrid.portal.search.UtilsSearch;
import de.ingrid.utils.IBus;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.IngridQueryTools;
import de.ingrid.utils.PlugDescription;
import de.ingrid.utils.dsc.Record;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.query.TermQuery;

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
    
    private static CacheManager cacheManager = null;
    
    private static final String CACHE_IPLUGS = "de.ingrid.iplug.plugdescription";
    
    private static final String CACHE_SEARCH = "de.ingrid.search.query";
    

    public static synchronized IBUSInterface getInstance() {
        if (instance == null) {
            try {
                instance = new IBUSInterfaceImpl();
            } catch (Exception e) {
                log.fatal("Error initiating the iBus interface.", e);
            }
        }
        
        if (cacheManager == null) {
	        try {
				cacheManager = CacheManager.create();
			} catch (CacheException e) {
				log.error("CacheManager could not be created!");
				e.printStackTrace();
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

            client = BusClient.instance();
            bus = client.getBus();
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
        try {
			String configFilename = getResourceAsStream(client.getJxtaConfigurationPath());
			return new PropertiesConfiguration(configFilename);
		} catch (Exception e) {
			log.error("Error retrieving configuration.", e);
			return null;
		}
    }

    /**
     * @throws Exception 
     * @see de.ingrid.portal.interfaces.IBUSInterface#search(de.ingrid.utils.query.IngridQuery, int, int, int, int)
     */
    public IngridHits search(IngridQuery query, int hitsPerPage, int currentPage, int startHit, int timeout)
            throws Exception {
        IngridHits hits = null;
        String postfix = ", hitsPerPage=" + hitsPerPage + ", startHit=" + startHit;
        
        // create a cache key from the query
        QueryPreProcessor preQuery = new QueryPreProcessor();
        int cacheKey = preQuery.getQueryCacheKey(query, postfix);
        
        // it was noticed that if we could get hits out from the cache, then we
        // also should cache getDetail(s), otherwise the search will be even
        // slower (assume that Lucene is doing some caching to find details
        // after a search faster)
        hits = (IngridHits) getFromCache(CACHE_SEARCH, cacheKey);
        
        if (hits == null) {
	        try {
	            if (log.isDebugEnabled()) {
	                log
	                        .debug("iBus.search: IngridQuery = " + UtilsSearch.queryToString(query) + " / timeout="
	                                + timeout + ", hitsPerPage=" + hitsPerPage + ", currentPage=" + currentPage
	                                + ", startHit=" + startHit);
	            }
	            hits = bus.search(query, hitsPerPage, currentPage, startHit, timeout);
	            
	            // put result into cache	            
	            putInCache(CACHE_SEARCH, cacheKey, hits);            
	            
	            if (log.isDebugEnabled()) {
	                log.debug("iBus.search: finished !");
	            }
	        } catch (java.io.IOException e) {
	            if (log.isDebugEnabled()) {
	                log.debug("Problems doing iBus search, query=" + UtilsSearch.queryToString(query) + " / timeout="
	                        + timeout + ", hitsPerPage=" + hitsPerPage + ", currentPage=" + currentPage + ", startHit="
	                        + startHit, e);
	            } else if (log.isInfoEnabled()) {
	                log.info("Problems doing iBus search, query=" + UtilsSearch.queryToString(query) + " / timeout="
	                        + timeout + ", hitsPerPage=" + hitsPerPage + ", currentPage=" + currentPage + ", startHit="
	                        + startHit + "[cause:" + e.getMessage() + "]");
	            } else {
	                log.warn("Problems doing iBus search, query=" + UtilsSearch.queryToString(query) + " / timeout="
	                        + timeout + ", hitsPerPage=" + hitsPerPage + ", currentPage=" + currentPage + ", startHit="
	                        + startHit + "[cause:" + e.getCause().getMessage() + "]", e);
	            }
	        } catch (Throwable t) {
	            if (log.isErrorEnabled()) {
	                log.error("Problems doing iBus search, query=" + UtilsSearch.queryToString(query) + " / timeout="
	                        + timeout + ", hitsPerPage=" + hitsPerPage + ", currentPage=" + currentPage + ", startHit="
	                        + startHit, t);
	            }
	            throw new Exception(t);
	        }
        } else {
        	if (log.isDebugEnabled()) {
        		log.debug("Got search hits from cache :-)");
        	}
        }
        
        return hits;
    }

    /**
     * @see de.ingrid.portal.interfaces.IBUSInterface#getDetail(de.ingrid.utils.IngridHit, de.ingrid.utils.query.IngridQuery, java.lang.String[])
     */
    public IngridHitDetail getDetail(IngridHit result, IngridQuery query, String[] requestedFields) {
        IngridHitDetail detail = null;
        
        String postfix = "[";
        
        QueryPreProcessor preQuery = new QueryPreProcessor();
        postfix = "[" + UtilsString.concatStringsIfNotNull(requestedFields, ",") + "]";
        postfix += result.getPlugId() + "," + String.valueOf(result.getDocumentId());
        int cacheKey = preQuery.getQueryCacheKey(query, postfix);
        
        detail = (IngridHitDetail) getFromCache(CACHE_SEARCH, cacheKey);
        
        if (detail == null) {
	        try {
	            if (log.isDebugEnabled()) {
	                log.debug("iBus.getDetail: hit = " + result + ", requestedFields = " + requestedFields);
	            }
	            detail = bus.getDetail(result, query, requestedFields);
	            
	            // put result into cache	            
	            putInCache(CACHE_SEARCH, cacheKey, detail); 
	            
	            if (log.isDebugEnabled()) {
	                log.debug("iBus.getDetail: finished !");
	            }
	        } catch (Throwable t) {
	            if (log.isDebugEnabled()) {
	                log.debug("Problems fetching Detail of results: " + result + "[cause:" + t.getCause().getMessage() + "]", t);
	            } else if (log.isInfoEnabled()) {
	                log.info("Problems fetching Detail of results: " + result + "[cause:" + t.getCause().getMessage() + "]");
	            } else {
	                log.warn("Problems fetching Detail of results: " + result + "[cause:" + t.getCause().getMessage() + "]", t);
	            }
	        }
        } else {
        	if (log.isDebugEnabled()) {
        		log.debug("Got detail from cache :-)");
        	}
        }

        return detail;
    }

    /**
     * @see de.ingrid.portal.interfaces.IBUSInterface#getDetails(de.ingrid.utils.IngridHit[], de.ingrid.utils.query.IngridQuery, java.lang.String[])
     */
    public IngridHitDetail[] getDetails(IngridHit[] results, IngridQuery query, String[] requestedFields) {
        IngridHitDetail[] details = null;
        
        // make a string out of the requestedFields array
        String postfix = "";        
        String docIDs  = "";
        
        for (IngridHit iH : results) {
        	docIDs += iH.getPlugId() + "," + String.valueOf(iH.getDocumentId());
        }
        
        QueryPreProcessor preQuery = new QueryPreProcessor();
        postfix = "[" + UtilsString.concatStringsIfNotNull(requestedFields, ",") + "]" + docIDs;
        int cacheKey = preQuery.getQueryCacheKey(query, postfix);
        
        details = (IngridHitDetail[]) getFromCache(CACHE_SEARCH, cacheKey);
        
        if (details == null) {
	        try {
	            if (log.isDebugEnabled()) {
	                log.debug("iBus.getDetails: hits = " + results + ", requestedFields = " + requestedFields);
	            }
	            details = bus.getDetails(results, query, requestedFields);
	            
	            // put result into cache
	            putInCache(CACHE_SEARCH, cacheKey, details); 
	            
	            if (log.isDebugEnabled()) {
	                log.debug("iBus.getDetails: finished !");
	            }
	        } catch (Throwable t) {
	            if (log.isDebugEnabled()) {
	                log.debug("Problems fetching Details of results: " + results + "[cause:" + t.getMessage() + "]", t);
	            } else if (log.isInfoEnabled()) {
	                log.info("Problems fetching Details of results: " + results + "[cause:" + t.getMessage() + "]");
	            } else {
	                log.warn("Problems fetching Details of results: " + results + "[cause:" + t.getMessage() + "]", t);
	            }
	        }
        } else {
        	if (log.isDebugEnabled()) {
	    		log.debug("Got details from cache :-)");
	    	}
	    }

        return details;
    }

    /**
     * @see de.ingrid.portal.interfaces.IBUSInterface#getRecord(de.ingrid.utils.IngridHit)
     */
    public Record getRecord(IngridHit result) {
        Record rec = null;
        try {
            rec = bus.getRecord(result);
        } catch (Throwable t) {
            if (log.isDebugEnabled()) {
                log.debug("Problems fetching Record of result: " + result + "[cause:" + t.getCause() + "]", t);
            } else if (log.isInfoEnabled()) {
                log.info("Problems fetching Record of result: " + result + "[cause:" + t.getCause() + "]");
            } else {
                log.warn("Problems fetching Record of result: " + result + "[cause:" + t.getCause() + "]", t);
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
        PlugDescription pd = null;
        
        // check first the cache
        pd = (PlugDescription) getFromCache(CACHE_IPLUGS, plugId);
        
        // else ask the bus and store the result inside the cache
        if (pd == null) {
	        try {
	        	pd = bus.getIPlug(plugId);
	        	
	        	putInCache(CACHE_IPLUGS, plugId, pd);
		    } catch (Throwable t) {
		        if (log.isWarnEnabled()) {
		            log.warn("Problems fetching iPlug from iBus !", t);
		        }            
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
    
    private Serializable getFromCache(String cache, Serializable key) {
    	Serializable value = null;
    	Cache aCache = cacheManager.getCache(cache);
	        
	    try {
			Element element = aCache.get(key);
			
			if (element != null) {
				value = element.getValue();
			}
		} catch (IllegalStateException e) {
			if (log.isWarnEnabled()) {
                log.warn("Problems fetching iPlugs from cache (Illegal state)!", e);
            } 
		} catch (CacheException e) {
			if (log.isWarnEnabled()) {
                log.warn("Problems fetching iPlugs from cache (CacheException)!", e);
            } 
		}
		return value;
    }
    
    private void putInCache(String cache, Serializable key, Serializable value) {
    	Cache aCache = cacheManager.getCache(cache);
    	aCache.put(new Element(key, value));
    }
}
