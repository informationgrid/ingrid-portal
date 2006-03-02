/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.interfaces.impl;

import java.util.ArrayList;

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
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.global.UtilsDB;
import de.ingrid.portal.interfaces.IBUSInterface;
import de.ingrid.portal.search.UtilsSearch;
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

    private static Bus bus = null;

    private static SocketCommunication communication = null;

    private static ProxyService proxy = null;

    private Configuration config;

    public static synchronized IBUSInterface getInstance() {
        if (instance == null) {
            try {
                instance = new IBUSInterfaceImpl();
            } catch (Exception e) {
                if (log.isFatalEnabled()) {
                    log.fatal("Error initiating the iBus interface.", e);
                }
            }
        }

        return instance;
    }

    public static synchronized void resetBus() {
        try {
            if (log.isInfoEnabled()) {
                log
                        .info("WE RESET IBUSInterface Singleton, so new Instance is created next time !");
            }
            if (proxy != null) {
                proxy.shutdown();
                proxy = null;
            }
            if (communication != null) {
                communication.shutdown();
                communication = null;
            }
            if (instance != null) {
                instance = null;
            }
        } catch (Throwable t) {
            if (log.isErrorEnabled()) {
                log.error("Problems RESETTING IBUSInterfaceImpl Singleton", t);
            }
        }
    }

    private IBUSInterfaceImpl() throws Exception {
        super();
        String configFilename = getResourceAsStream("/ibus_interface.properties");
        config = new PropertiesConfiguration(configFilename);

        try {
            communication = new SocketCommunication();

            communication.setMulticastPort(Integer.parseInt(config.getString("multicast_port", "11114")));
            communication.setUnicastPort(Integer.parseInt(config.getString("unicast_port", "50000")));

            communication.startup();

            // start the proxy server
            proxy = new ProxyService();

            proxy.setCommunication(communication);
            proxy.startup();

            String iBusUrl = AddressUtil.getWetagURL(config.getString("ibus_server", "localhost"), Integer
                    .parseInt(config.getString("ibus_port", "11112")));

            if (log.isInfoEnabled()) {
                log.info("!!!!!!!!!! Connecting with iBus URL: "+iBusUrl);
            }

            RemoteInvocationController ric = proxy.createRemoteInvocationController(iBusUrl);
            bus = (Bus) ric.invoke(Bus.class, Bus.class.getMethod("getInstance", null), null);

            if (bus == null) {
                throw new Exception("NO iBUS available, RemoteInvocationController.invoke returns  NULL");
            }
        } catch (Throwable t) {
            if (log.isFatalEnabled()) {
                log.fatal("Problems Constructor IBUSInterfaceImpl Singleton", t);
            }
            resetBus();
            throw new Exception("Error Constructor IBUSInterfaceImpl", t);
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
            hits = bus.search(query, hitsPerPage, currentPage, requestedHits, timeout);
        } catch (Throwable t) {
            if (log.isErrorEnabled()) {
                log.error("Problems doing iBus search, query=" + query + ", hitsPerPage=" + hitsPerPage
                        + ", currentPage=" + currentPage + ", WE RESET IBUS", t);
            }
            // !!! we reset Singleton, so new Instance is created next time !!!
            resetBus();
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
            detail = bus.getDetail(result, query, requestedFields);
        } catch (Throwable t) {
            if (log.isErrorEnabled()) {
                log.error("Problems fetching Detail of result: " + result, t);
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
            details = bus.getDetails(results, query, requestedFields);
        } catch (Throwable t) {
            if (log.isErrorEnabled()) {
                log.error("Problems fetching Details of results: " + results, t);
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
            if (log.isErrorEnabled()) {
                log.error("Problems fetching Record of result: " + result, t);
            }
        }

        return rec;
    }

    private static String getResourceAsStream(String resource) throws Exception {
        String stripped = resource.startsWith("/") ? resource.substring(1) : resource;

        String stream = null;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader != null) {
            stream = classLoader.getResource(stripped).toString();
        }
        if (stream == null) {
            Environment.class.getResourceAsStream(resource);
        }
        if (stream == null) {
            stream = Environment.class.getClassLoader().getResource(stripped).toString();
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

    /*
    public PlugDescription getIPlug(IngridHit result) {
        PlugDescription plug = null;
        try {
            plug = getIPlug(result.getPlugId());
        } catch (Throwable t) {
            if (log.isErrorEnabled()) {
                log.error("Problems fetching iPlug of result: " + result, t);
            }
        }

        return plug;
    }
*/
    public void transferHitDetails(IngridHit result, IngridHitDetail detail) {
        try {
            result.put(Settings.RESULT_KEY_TITLE, detail.getTitle());
            result.put(Settings.RESULT_KEY_ABSTRACT, detail.getSummary());
            result.put(Settings.RESULT_KEY_DOC_ID, new Integer(result.getDocumentId()));
            result.put(Settings.RESULT_KEY_PROVIDER, detail.getOrganisation());
            result.put(Settings.RESULT_KEY_SOURCE, detail.getDataSourceName());
            result.put(Settings.RESULT_KEY_PLUG_ID, detail.getPlugId());

            if (detail.get(Settings.RESULT_KEY_URL) != null) {
                result.put(Settings.RESULT_KEY_URL, detail.get(Settings.RESULT_KEY_URL));
                result.put(Settings.RESULT_KEY_URL_STR, Utils.getShortURLStr((String) detail
                        .get(Settings.RESULT_KEY_URL), 80));
            }
            // Partner
            Object values = UtilsSearch.getDetailMultipleValues(detail, Settings.RESULT_KEY_PARTNER); 
            if (values != null) {
                result.put(Settings.RESULT_KEY_PARTNER, UtilsDB.getPartnerFromKey(values.toString()));                
            }
/*            
            // detail values as ArrayLists !
            // Hit URL
            Object values = Utils.getDetailMultipleValues(detail, Settings.RESULT_KEY_URL); 
            if (values != null) {
                result.put(Settings.RESULT_KEY_URL, values);
                result.put(Settings.RESULT_KEY_URL_STR, Utils.getShortURLStr((String)values, 80));
            }
*/            
        } catch (Throwable t) {
            if (log.isErrorEnabled()) {
                log.error("Problems taking over Hit Details into result:" + result, t);
            }
        }
    }
/*
    public Column getColumn(Record record, String columnName) {
        Column col = null;
        try {
            // serch for column
            Column[] columns = record.getColumns();
            for (int i = 0; i < columns.length; i++) {
                if (columns[i].getTargetName().equalsIgnoreCase(columnName)) {
                    return columns[i];
                }
            }
            // search sub records
            Record[] subRecords = record.getSubRecords();
            Column c = null;
            for (int i = 0; i < subRecords.length; i++) {
                c = getColumn(subRecords[i], columnName);
                if (c != null) {
                    return c;
                }
            }
        } catch (Throwable t) {
            if (log.isErrorEnabled()) {
                log.error("Problems fetching Column from record: " + record, t);
            }
        }

        return col;
    }
*/
}
