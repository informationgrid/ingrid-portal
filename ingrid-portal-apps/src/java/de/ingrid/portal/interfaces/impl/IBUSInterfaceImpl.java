/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.interfaces.impl;

import java.net.URL;

import net.weta.components.communication.ICommunication;
import net.weta.components.communication_sockets.SocketCommunication;
import net.weta.components.communication_sockets.util.AddressUtil;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.cfg.Environment;

import de.ingrid.ibus.client.BusClient;
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
 * TODO Describe your created type (class, etc.) here.
 *
 * @author joachim@wemove.com
 */
public class IBUSInterfaceImpl implements IBUSInterface {

    private final static Log log = LogFactory.getLog(IBUSInterfaceImpl.class);

    private static IBUSInterfaceImpl instance = null;

    private static IBus bus = null;

    private static Object communication = null;

    static BusClient client = null;

    private static Configuration config;

    private static boolean enJXTACommunication = false;

    private static boolean initInProgress = false;

    public static synchronized IBUSInterface getInstance() {
        if (instance == null && !initInProgress) {
            try {
                instance = new IBUSInterfaceImpl();
            } catch (Exception e) {
                initInProgress = false;
                if (log.isFatalEnabled()) {
                    log.fatal("Error initiating the iBus interface.", e);
                }
            }
        }

        return instance;
    }

    public static synchronized void shutdown() {
        try {
            if (log.isInfoEnabled()) {
                log.info("SHUT DOWN IBUSInterface!");
            }

            if (client != null) {
                client.shutdown();

            } else if (communication != null) {
                try {
                    ((SocketCommunication) communication).shutdown();
                } catch (RuntimeException e) {
                    log.error("error shutting down socket communication.", e);
                }
            }
            communication = null;

        } catch (Throwable t) {
            if (log.isErrorEnabled()) {
                log.error("Problems SHUTTING DPWN IBUSInterface", t);
            }
        } finally {
            if (instance != null) {
                instance = null;
            }
            initInProgress = false;
        }
    }

    private IBUSInterfaceImpl() throws Exception {
        super();
        initInProgress = true;
        String configFilename = getResourceAsStream("/ibus_interface.properties");
        config = new PropertiesConfiguration(configFilename);

        try {

            enJXTACommunication = config.getString("enable_jxta", "0").equals("1");

            if (enJXTACommunication) {

                String iBusUrl = config.getString("ibus_wetag_url", "wetag:///kug-group:kug-ibus");

                client = BusClient.instance();
                String jxtaConf = config.getString("jxta_conf_filename", "/jxta.properties");

                client.setBusUrl(iBusUrl);
                client.setJxtaConfigurationPath(jxtaConf);

                bus = client.getBus();
            } else {

                communication = new SocketCommunication();

                ((SocketCommunication) communication).setMulticastPort(Integer.parseInt(config.getString(
                        "multicast_port", "11114")));
                ((SocketCommunication) communication).setUnicastPort(Integer.parseInt(config.getString("unicast_port",
                        "50000")));

                ((SocketCommunication) communication).startup();

                String iBusUrl = AddressUtil.getWetagURL(config.getString("ibus_server", "localhost"), Integer
                        .parseInt(config.getString("ibus_port", "11112")));
                if (log.isInfoEnabled()) {
                    log.info("!!!!!!!!!! Connecting with iBus URL: " + iBusUrl);
                }

                bus = (IBus) net.weta.components.communication.reflect.ProxyService.createProxy(
                        (ICommunication) communication, IBus.class, iBusUrl);
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
        } finally {
            initInProgress = false;
        }
    }

    /**
     * @see de.ingrid.portal.interfaces.IBUSInterface#getConfig()
     */
    public Configuration getConfig() {
        return config;
    }

    /**
     * @throws Exception 
     * @see de.ingrid.portal.interfaces.IBUSInterface#search(de.ingrid.utils.query.IngridQuery, int, int, int, int)
     */
    public IngridHits search(IngridQuery query, int hitsPerPage, int currentPage, int requestedHits, int timeout)
            throws Exception {
        IngridHits hits = null;
        try {
            if (log.isInfoEnabled()) {
                log
                        .info("iBus.search: IngridQuery = " + UtilsSearch.queryToString(query) + " / timeout="
                                + timeout + ", hitsPerPage=" + hitsPerPage + ", currentPage=" + currentPage
                                + ", length=" + requestedHits);
            }
            hits = bus.search(query, hitsPerPage, currentPage, requestedHits, timeout);
            if (log.isInfoEnabled()) {
                log.info("iBus.search: finished !");
            }
        } catch (java.io.IOException e) {
            if (log.isWarnEnabled()) {
                log.warn("Problems doing iBus search, query=" + UtilsSearch.queryToString(query) + " / timeout="
                        + timeout + ", hitsPerPage=" + hitsPerPage + ", currentPage=" + currentPage + ", length="
                        + requestedHits, e);
            }
        } catch (Throwable t) {
            if (log.isErrorEnabled()) {
                log.error("Problems doing iBus search, query=" + UtilsSearch.queryToString(query) + " / timeout="
                        + timeout + ", hitsPerPage=" + hitsPerPage + ", currentPage=" + currentPage + ", length="
                        + requestedHits, t);
            }
            // !!! we reset Singleton when socket communication, so new Instance is created next time !!!
            if (!enJXTACommunication) {
                shutdown();
            }
            throw new Exception(t);
        }

        return hits;
    }

    /**
     * @see de.ingrid.portal.interfaces.IBUSInterface#getDetail(de.ingrid.utils.IngridHit, de.ingrid.utils.query.IngridQuery, java.lang.String[])
     */
    public IngridHitDetail getDetail(IngridHit result, IngridQuery query, String[] requestedFields) {
        IngridHitDetail detail = null;
        try {
            if (log.isInfoEnabled()) {
                log.info("iBus.getDetail: hit = " + result + ", requestedFields = " + requestedFields);
            }
            detail = bus.getDetail(result, query, requestedFields);
            if (log.isInfoEnabled()) {
                log.info("iBus.getDetail: finished !");
            }
        } catch (Throwable t) {
            if (log.isWarnEnabled()) {
                log.warn("Problems fetching Detail of result: " + result, t);
            }
            // !!! we reset Singleton when socket communication, so new Instance is created next time !!!
            if (!enJXTACommunication) {
                shutdown();
            }
        }

        return detail;
    }

    /**
     * @see de.ingrid.portal.interfaces.IBUSInterface#getDetails(de.ingrid.utils.IngridHit[], de.ingrid.utils.query.IngridQuery, java.lang.String[])
     */
    public IngridHitDetail[] getDetails(IngridHit[] results, IngridQuery query, String[] requestedFields) {
        IngridHitDetail[] details = null;
        try {
            if (log.isInfoEnabled()) {
                log.info("iBus.getDetails: hits = " + results + ", requestedFields = " + requestedFields);
            }
            details = bus.getDetails(results, query, requestedFields);
            if (log.isInfoEnabled()) {
                log.info("iBus.getDetails: finished !");
            }
        } catch (Throwable t) {
            if (log.isWarnEnabled()) {
                log.warn("Problems fetching Details of results: " + results, t);
            }
            // !!! we reset Singleton when socket communication, so new Instance is created next time !!!
            if (!enJXTACommunication) {
                shutdown();
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
            if (log.isWarnEnabled()) {
                log.warn("Problems fetching Record of result: " + result, t);
            }
            // !!! we reset Singleton when socket communication, so new Instance is created next time !!!
            if (!enJXTACommunication) {
                shutdown();
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
        return bus.getIPlug(plugId);
    }

    /**
     * @see de.ingrid.portal.interfaces.IBUSInterface#getAllIPlugs()
     */
    public PlugDescription[] getAllIPlugs() {
        return bus.getAllIPlugs();
    }
}
