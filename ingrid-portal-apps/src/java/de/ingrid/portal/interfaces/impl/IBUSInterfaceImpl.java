/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.interfaces.impl;

import java.lang.reflect.Method;
import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;

import net.weta.components.communication.ICommunication;
import net.weta.components.communication_sockets.SocketCommunication;
import net.weta.components.communication_sockets.util.AddressUtil;
import net.weta.components.proxies.ProxyService;
import net.weta.components.proxies.remote.RemoteInvocationController;
import net.weta.components.peer.PeerService;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationUtils;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.cfg.Environment;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

import de.ingrid.ibus.Bus;
import de.ingrid.ibus.client.BusClient;
import de.ingrid.portal.interfaces.IBUSInterface;
import de.ingrid.portal.search.UtilsSearch;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.PlugDescription;
import de.ingrid.utils.dsc.Record;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.ParseException;

/**
 * TODO Describe your created type (class, etc.) here.
 *
 * @author joachim@wemove.com
 */
public class IBUSInterfaceImpl implements IBUSInterface {

    private final static Log log = LogFactory.getLog(IBUSInterfaceImpl.class);

    private static IBUSInterfaceImpl instance = null;

    private static Bus bus = null;

    private static Object communication = null;
    
    private static RemoteInvocationController ric = null;

    private static ProxyService proxy = null;
    
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

    public static synchronized void resetBus() {
        try {
            if (log.isInfoEnabled()) {
                log.info("WE RESET IBUSInterface Singleton, so new Instance is created next time !");
            }
            
            if (ric != null) {
                ric.setCommunication(null);
            }

            if (proxy != null) {
                proxy.setCommunication(null);
                proxy.shutdown();
                proxy = null;
            }
            if (enJXTACommunication ) {
                client.shutdown();
            } else if (communication != null) {
                try {
                    ((SocketCommunication)communication).shutdown();
                } catch (RuntimeException e) {
                    log.error("error shutting down socket communication.", e);
                }
            }
            communication = null;
            
        } catch (Throwable t) {
            if (log.isErrorEnabled()) {
                log.error("Problems RESETTING IBUSInterfaceImpl Singleton", t);
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
            
                String iBusUrl = config.getString("ibus_wetag_url", "wetag:///torwald-ibus:ibus-torwald");
                
                client = BusClient.instance();
                String jxtaConf = "/jxta.conf.xml";
                
                client.setBusUrl(iBusUrl);
                client.setJxtaConfigurationPath(jxtaConf);

                bus = client.getBus();
                if (bus == null) {
                    throw new Exception("NO iBUS available, RemoteInvocationController.invoke returns  NULL");
                }
            } else {    
            
                communication = new SocketCommunication();
    
                ((SocketCommunication)communication).setMulticastPort(Integer.parseInt(config.getString("multicast_port", "11114")));
                ((SocketCommunication)communication).setUnicastPort(Integer.parseInt(config.getString("unicast_port", "50000")));
    
                ((SocketCommunication)communication).startup();
    
                // start the proxy server
                proxy = new ProxyService();
    
                proxy.setCommunication(((SocketCommunication)communication));
                proxy.startup();
    
                String iBusUrl = AddressUtil.getWetagURL(config.getString("ibus_server", "localhost"), Integer
                        .parseInt(config.getString("ibus_port", "11112")));
    
                if (log.isInfoEnabled()) {
                    log.info("!!!!!!!!!! Connecting with iBus URL: " + iBusUrl);
                }
    
                ric = proxy.createRemoteInvocationController(iBusUrl);
                bus = (Bus) ric.invoke(Bus.class, Bus.class.getMethod("getInstance", null), null);
                if (bus == null) {
                    throw new Exception("NO iBUS available, RemoteInvocationController.invoke returns  NULL");
                }
            }

        } catch (Throwable t) {
            if (log.isFatalEnabled()) {
                log.fatal("Problems Constructor IBUSInterfaceImpl Singleton", t);
            }
            resetBus();
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
            if (log.isDebugEnabled()) {
                log.debug("doSearch: IngridQuery = " + UtilsSearch.queryToString(query));
            }
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

    private ICommunication startJxtaCommunication(String fileName) throws Exception {
        PeerService communication = new PeerService();

        URL url = ConfigurationUtils.locate(fileName);
        
        InputSource inputSource = new InputSource(url.openStream());
        DocumentBuilderFactory buildFactory = DocumentBuilderFactory.newInstance();
        Document descriptionsDocument = buildFactory.newDocumentBuilder().parse(inputSource);
        Element descriptionElement = descriptionsDocument.getDocumentElement();
        NodeList callNodes = descriptionElement.getElementsByTagName("call");

        if (callNodes.getLength() < 1) {
            throw new ParseException("No call tags in the descriptor.xml file.");
        }

        for (int i = 0; i < callNodes.getLength(); i++) {
            Element element = (Element) callNodes.item(i);
            final String methodName = "set" + element.getAttribute("attribute");

            NodeList argNodes = element.getElementsByTagName("arg");
            if (argNodes.getLength() < 1) {
                throw new ParseException("No arg tags under the a call tag in the descriptor.xml file.");
            }

            Element argElement = (Element) argNodes.item(0);
            final String type = argElement.getAttribute("type");
            final String value = ((Text) argElement.getChildNodes().item(0)).getNodeValue();

            Class argType = null;
            Object argValue = null;
            if (type.endsWith("String")) {
                argType = String.class;
                argValue = value;
            } else if (type.endsWith("boolean")) {
                argType = boolean.class;
                argValue = new Boolean(value);
            } else if (type.endsWith("int")) {
                argType = int.class;
                argValue = new Integer(value);
            } else {
                throw new ParseException("Unknown argument type: " + type);
            }

            Method method = communication.getClass().getMethod(methodName, new Class[] { argType });
            method.invoke(communication, new Object[] { argValue });
        }

        communication.boot();

        return communication;
    }

    /**
     * @see de.ingrid.portal.interfaces.IBUSInterface#getIBus()
     */
    public Bus getIBus() {
        return bus;
    }
}
