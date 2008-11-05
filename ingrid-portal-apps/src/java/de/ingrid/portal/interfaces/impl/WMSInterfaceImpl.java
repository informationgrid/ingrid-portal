/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.interfaces.impl;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.hibernate.cfg.Environment;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.interfaces.WMSInterface;
import de.ingrid.portal.interfaces.om.WMSSearchDescriptor;
import de.ingrid.portal.interfaces.om.WMSServiceDescriptor;

/**
 * Implements the WMS interface. It allows to communicate with the mapbender WMS
 * server that has been extended for the ingrid project with some interface
 * methods.
 * 
 * @author joachim@wemove.com
 */
public class WMSInterfaceImpl implements WMSInterface {

    private final static Log log = LogFactory.getLog(WMSInterfaceImpl.class);

    private final static String LANGUAGE_PARAM = "lang";

    private static WMSInterfaceImpl instance = null;
    
    private String mapBenderVersion = null;

    Configuration config;

    public static synchronized WMSInterfaceImpl getInstance() {
        if (instance == null) {
            try {
                instance = new WMSInterfaceImpl();
            } catch (Exception e) {
                log.fatal("Error initiating the WMS interface.");
                e.printStackTrace();
            }
        }

        return instance;
    }

    private WMSInterfaceImpl() throws Exception {
        super();
        String configFilename = getResourceAsStream("/wms_interface.properties");
        config = new PropertiesConfiguration(configFilename);
        mapBenderVersion = config.getString("mapbender_version", MAPBENDER_VERSION_2_1);
    }

    public Configuration getConfig() {
        return config;
    }

    /**
     * @see de.ingrid.portal.interfaces.WMSInterface#getWMSServices(java.lang.String)
     */
    public Collection getWMSServices(String sessionID) {
        URL url;
        SAXReader reader = new SAXReader(false);
        ArrayList result = new ArrayList();

        try {

            // workaround for wrong dtd location

            EntityResolver resolver = new EntityResolver() {
                public InputSource resolveEntity(String publicId, String systemId) {
                    if (systemId.indexOf("portalCommunication.dtd") > 0) {

                        InputStream in = getClass().getResourceAsStream("wms_interface.dtd");

                        return new InputSource(in);
                    }
                    return null;
                }
            };
            reader.setEntityResolver(resolver);

            // END workaround for wrong dtd location

            url = new URL(config.getString("interface_url",
                    "http://localhost/mapbender/php/mod_portalCommunication_gt.php").concat("?PREQUEST=getWMSServices")
                    .concat("&PHPSESSID=" + sessionID));

            reader.setValidation(false);
            Document document = reader.read(url);

            // check for valid server response
            if (document.selectSingleNode("//portal_communication") == null) {
                throw new Exception("WMS Server Response is not valid!");
            }
            // check for valid server response
            String error = document.valueOf("//portal_communication/error");
            if (error != null && error.length() > 0) {
                throw new Exception("WMS Server Error: " + error);
            }
            // get the wms services
            List nodes = document.selectNodes("//portal_communication/wms_services/wms");
            String serviceURL = null;
            for (Iterator i = nodes.iterator(); i.hasNext();) {
                Node node = (Node) i.next();
            	serviceURL = node.valueOf("url");
                if (mapBenderVersion.equals(MAPBENDER_VERSION_2_1)) {
                	serviceURL = serviceURL.replace(',', '&');
                }
                WMSServiceDescriptor wmsServiceDescriptor = new WMSServiceDescriptor(node.valueOf("name"), serviceURL);
                result.add(wmsServiceDescriptor);
            }

            return result;

        } catch (Exception e) {
            log.error(e);
        }

        return null;
    }

    /**
     * @see de.ingrid.portal.interfaces.WMSInterface#getWMSSearchParameter(java.lang.String)
     */
    public WMSSearchDescriptor getWMSSearchParameter(String sessionID) {
        URL url;
        SAXReader reader = new SAXReader(false);

        try {

            // workaround for wrong dtd location

            EntityResolver resolver = new EntityResolver() {
                public InputSource resolveEntity(String publicId, String systemId) {
                    if (systemId.indexOf("portalCommunication.dtd") > 0) {

                        InputStream in = getClass().getResourceAsStream("wms_interface.dtd");

                        return new InputSource(in);
                    }
                    return null;
                }
            };
            reader.setEntityResolver(resolver);

            // END workaround for wrong dtd location

            String urlStr = config.getString("interface_url", "http://localhost/mapbender/php/mod_portalCommunication_gt.php");
            
            if (urlStr.indexOf("?") > 0) {
            	urlStr = urlStr.concat("&PREQUEST=getWMSSearch").concat("&PHPSESSID=" + sessionID);
            } else {
            	urlStr = urlStr.concat("?PREQUEST=getWMSSearch").concat("&PHPSESSID=" + sessionID);
            }
            
            url = new URL(urlStr);

            reader.setValidation(false);
            Document document = reader.read(url);

            // check for valid server response
            if (document.selectSingleNode("//portal_communication") == null) {
                throw new Exception("WMS Server Response is not valid!");
            }
            // check for valid server response
            String error = document.valueOf("//portal_communication/error");
            if (error != null && error.length() > 0) {
                throw new Exception("WMS Server Error: " + error);
            }

            // get search type
            String searchType = document.valueOf("//portal_communication/search/@type");
            if (searchType == null || (!searchType.equalsIgnoreCase("bbox") && !searchType.equalsIgnoreCase("gkz"))) {
                throw new Exception("WMS Interface: unsupported search type (" + searchType + ")");
            }

            WMSSearchDescriptor wmsSearchDescriptor = new WMSSearchDescriptor();
            if (searchType.equalsIgnoreCase("bbox")) {
                wmsSearchDescriptor.setType(WMSSearchDescriptor.WMS_SEARCH_BBOX);
                wmsSearchDescriptor.setTypeOfCoordinates(document
                        .valueOf("//portal_communication/search/bbox/@cootype"));
                wmsSearchDescriptor.setMinX(Double.parseDouble(document
                        .valueOf("//portal_communication/search/bbox/minx")));
                wmsSearchDescriptor.setMinY(Double.parseDouble(document
                        .valueOf("//portal_communication/search/bbox/miny")));
                wmsSearchDescriptor.setMaxX(Double.parseDouble(document
                        .valueOf("//portal_communication/search/bbox/maxx")));
                wmsSearchDescriptor.setMaxY(Double.parseDouble(document
                        .valueOf("//portal_communication/search/bbox/maxy")));
            } else if (searchType.equalsIgnoreCase("gkz")) {
                wmsSearchDescriptor.setType(WMSSearchDescriptor.WMS_SEARCH_COMMUNITY_CODE);
                wmsSearchDescriptor.setCommunityCode(document.valueOf("//portal_communication/search/gkz"));
            }

            return wmsSearchDescriptor;

        } catch (Exception e) {
            log.error(e);
        }

        return null;
    }

    /**
     * Checks whether a WMS Viewer is configured !
     * 
     * @return
     */
    public boolean hasWMSViewer() {
        String viewerURL = config.getString("display_viewer_url", null);
        if (viewerURL != null && viewerURL.trim().length() > 0) {
            return true;
        }

        return false;
    }

    /**
     * @see de.ingrid.portal.interfaces.WMSInterface#getWMSURL(java.lang.String)
     */
    public String getWMSViewerURL(String sessionId, boolean jsEnabled, Locale language) {
        String viewerURL = "";
        if (jsEnabled) {
            viewerURL = config.getString("display_viewer_url", "http://localhost/mapbender/frames/WMS_Viewer.php");
        } else {
            viewerURL = config.getString("nojs_display_viewer_url",
                    "http://localhost/mapbender/frames/wms_viewer_nojs.php");
        }
        if (viewerURL.indexOf("?") > 0) {
            viewerURL = viewerURL.concat("&PHPSESSID=" + sessionId);
        } else {
            viewerURL = viewerURL.concat("?PHPSESSID=" + sessionId);
        }
        
        if (language != null) {
            viewerURL = viewerURL.concat("&" + LANGUAGE_PARAM + "=" + language.getLanguage());
        }

        return viewerURL;
    }

    /**
     * @see de.ingrid.portal.interfaces.WMSInterface#getWMSSearchURL(java.lang.String)
     */
    public String getWMSSearchURL(String sessionId, boolean jsEnabled, Locale language) {
        String searchURL = "";
        if (jsEnabled) {
            searchURL = config.getString("display_search_url", "http://localhost/mapbender/frames/WMS_Search.php");
        } else {
            searchURL = config.getString("nojs_display_search_url",
                    "http://localhost/mapbender/frames/wms_search_nojs.php");
        }
        if (searchURL.indexOf("?") > 0) {
        	searchURL = searchURL.concat("&PHPSESSID=" + sessionId);
        } else {
        	searchURL = searchURL.concat("?PHPSESSID=" + sessionId);
        }
        if (language != null) {
            searchURL = searchURL.concat("&" + LANGUAGE_PARAM + "=" + language.getLanguage());
        }

        return searchURL;
    }

    /**
     * @see de.ingrid.portal.interfaces.WMSInterface#getWMSAddedServiceURL(de.ingrid.portal.interfaces.om.WMSServiceDescriptor,
     *      java.lang.String)
     */
    public String getWMSAddedServiceURL(WMSServiceDescriptor service, String sessionId, boolean jsEnabled,
            Locale language, boolean isViewer) {
        ArrayList l = new ArrayList();
        l.add(service);
        return getWMSAddedServiceURL(l, sessionId, jsEnabled, language, isViewer);
    }

    /**
     * @throws UnsupportedEncodingException
     * @see de.ingrid.portal.interfaces.WMSInterface#getWMSAddedServiceURL(java.util.ArrayList,
     *      java.lang.String)
     */
    public String getWMSAddedServiceURL(List services, String sessionId, boolean jsEnabled, Locale language, boolean isViewer) {
        WMSServiceDescriptor service;
        String serviceURL;
        String serviceName;
        StringBuffer resultB;
        if (isViewer) {
            resultB = new StringBuffer(getWMSViewerURL(sessionId, jsEnabled, language));
        } else {
            resultB = new StringBuffer(this.getWMSSearchURL(sessionId, jsEnabled, language));
        }
        boolean prequestAdded = false;

        // check for invalid service parameter
        if (services == null || services.size() == 0) {
            return resultB.toString();
        }

        try {
            // add services
            for (int i = 0; i < services.size(); i++) {
                service = (WMSServiceDescriptor) services.get(i);
                serviceURL = service.getUrl();
                serviceName = service.getName();
                if (serviceURL != null && serviceURL.length() > 0) {
                    if (!prequestAdded) {
                        if (mapBenderVersion.equals(MAPBENDER_VERSION_2_1)) {
                        	resultB.append("&PREQUEST=setServices");
                        } else {
                        	resultB.append("&COMMAND=addWMS");
                        }
                        prequestAdded = true;
                    }
                    if (serviceName != null && serviceName.length() > 0) {
                        resultB.append("&wmsName" + (i + 1) + "=" + URLEncoder.encode(serviceName, "UTF-8"));
                    }
                    if (mapBenderVersion.equals(MAPBENDER_VERSION_2_1)) {
                    	serviceURL.replace('&', ',');
                    }
                    resultB.append("&wms" + (i + 1) + "=" + URLEncoder.encode(serviceURL, "UTF-8"));
                }
            }
        } catch (UnsupportedEncodingException e) {
            log.error(e);
        }

        return resultB.toString();
    }

    public static String getResourceAsStream(String resource) throws Exception {
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
     * @see de.ingrid.portal.interfaces.WMSInterface#getAdminInterfaceURLs()
     */
    public String[] getAdminInterfaceURLs() {
        String[] result = new String[2];
        result[0] = PortalConfig.getInstance().getString(PortalConfig.WMS_MAPBENDER_ADMIN_URL, "");
        result[1] = PortalConfig.getInstance().getString(PortalConfig.WMS_MAPLAB_ADMIN_URL, "");
        return result;
    }

	public String getWMCDocument(String sessionId) {
        String response = null;
        
        try {
            String urlStr = config.getString("interface_url", "http://localhost/mapbender/php/mod_portalCommunication_gt.php");
            if (urlStr.indexOf("?") > 0) {
            	urlStr = urlStr.concat("&PREQUEST=getWMC").concat("&PHPSESSID=" + sessionId);
            } else {
            	urlStr = urlStr.concat("?PREQUEST=getWMC").concat("&PHPSESSID=" + sessionId);
            }

            if (log.isDebugEnabled()) {
            	log.debug("MapBender Server Request: " + urlStr);
            }
            
//          Create an instance of HttpClient.
            HttpClient client = new HttpClient();

            // Create a method instance.
            GetMethod method = new GetMethod(urlStr);
            
            // Provide custom retry handler is necessary
            method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
            		new DefaultHttpMethodRetryHandler(2, false));

            try {
              // Execute the method.
              int statusCode = client.executeMethod(method);

              if (statusCode != HttpStatus.SC_OK) {
            	  log.error("Requesting WMC faild for URL: " + urlStr);
              }

              // Read the response body.
              byte[] responseBody = method.getResponseBody();

              response = new String(responseBody);

            } catch (HttpException e) {
          	  log.error("Fatal protocol violation: " + e.getMessage(), e);
            } catch (IOException e) {
           	  log.error("Fatal transport error: " + e.getMessage(), e);
            } finally {
              // Release the connection.
              method.releaseConnection();
            }            
            
            if (log.isDebugEnabled()) {
            	log.debug("MapBender Server Response: " + response);
            }
            
            Document document = DocumentHelper.parseText(response);
            // check for valid server response
            String error = document.valueOf("//portal_communication/error");
            if (error != null && error.length() > 0) {
                throw new Exception("MapBender Server Error: " + error);
            }
            
            String sessionMapBender = document.valueOf("//portal_communication/session");
            if (sessionMapBender != null && !sessionMapBender.equals(sessionId)) {
                throw new Exception("MapBender Server Error: session Id (" + sessionId + ") from request and session Id (" + sessionMapBender + ") from MapBender are not the same.");
            }
            
            String urlEncodedWmc = document.valueOf("//portal_communication/wmc");
        	return urlEncodedWmc;

        } catch (Exception e) {
            log.error(e);
        }
        return null;
    }

	public void setWMCDocument(String wmc, String sessionId) {
        String urlStr = config.getString("interface_url", "http://localhost/mapbender/php/mod_portalCommunication_gt.php");
        if (urlStr.indexOf("?") > 0) {
        	urlStr = urlStr.concat("&PREQUEST=setWMC").concat("&PHPSESSID=" + sessionId);
        } else {
        	urlStr = urlStr.concat("?PREQUEST=setWMC").concat("&PHPSESSID=" + sessionId);
        }
//      Create an instance of HttpClient.
        HttpClient client = new HttpClient();

        // Create a method instance.
        PostMethod method = new PostMethod(urlStr);
        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
        		new DefaultHttpMethodRetryHandler(2, false));
        
        NameValuePair[] data = {
                new NameValuePair("wmc", wmc)
              };
        method.setRequestBody(data);        
        
        try {
          // Execute the method.
          int statusCode = client.executeMethod(method);

          if (statusCode != HttpStatus.SC_OK) {
        	  log.error("Sending WMC faild for URL: " + urlStr);
          }

          // Read the response body.
          byte[] responseBody = method.getResponseBody();

          if (log.isDebugEnabled()) {
            	log.debug("MapBender Server Response: " + new String(responseBody));
          }
          
          // Deal with the response.
          // Use caution: ensure correct character encoding and is not binary data
          Document document = DocumentHelper.parseText(new String(responseBody));
          
          // check for valid server response
          String error = document.valueOf("//portal_communication/error");
          if (error != null && error.length() > 0) {
              throw new Exception("WMS Server Error: " + error);
          }
          
          
          String success = document.valueOf("//portal_communication/success");
          if (error == null || success.length() == 0) {
              throw new Exception("WMS Server Error: Cannot find success message from server. message was: " + new String(responseBody));
          }

        } catch (HttpException e) {
        	log.error("Fatal protocol violation: " + e.getMessage(), e);
        } catch (IOException e) {
            log.error("Fatal transport error: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Fatal error: " + e.getMessage(), e);
        } finally {
          // Release the connection.
          method.releaseConnection();
        }         
	}

	/* (non-Javadoc)
	 * @see de.ingrid.portal.interfaces.WMSInterface#getMapbenderVersion()
	 */
	public String getMapbenderVersion() {
		return mapBenderVersion;
	}

}
