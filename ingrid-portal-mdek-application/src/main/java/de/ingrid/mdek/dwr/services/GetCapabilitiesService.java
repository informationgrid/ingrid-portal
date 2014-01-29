package de.ingrid.mdek.dwr.services;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import de.ingrid.mdek.SysListCache;
import de.ingrid.mdek.beans.CapabilitiesBean;
import de.ingrid.mdek.dwr.services.capabilities.CapabilitiesParserFactory;
import de.ingrid.mdek.dwr.services.capabilities.ICapabilitiesParser;


public class GetCapabilitiesService {

	private final static Logger log = Logger.getLogger(GetCapabilitiesService.class);	

    private static String ERROR_GETCAP_INVALID_URL = "ERROR_GETCAP_INVALID_URL";
    private static String ERROR_GETCAP = "ERROR_GETCAP_ERROR";

	@Autowired
	private SysListCache sysListMapper;
	
    // Init Method is called by the Spring Framework on initialization
    public void init() throws Exception {
    }

    public CapabilitiesBean getCapabilities(String urlStr) {
        
    	try {
    		URL url = new URL(urlStr);
    		// get the content in UTF-8 format, to avoid "MalformedByteSequenceException: Invalid byte 1 of 1-byte UTF-8 sequence"
    		Reader reader = new InputStreamReader(url.openStream(), "UTF-8");
    		InputSource inputSource = new InputSource(reader);

        	// Build a document from the xml response
        	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        	// nameSpaceAware is false by default. Otherwise we would have to query for the correct namespace for every evaluation
        	factory.setNamespaceAware(true);
        	DocumentBuilder builder = factory.newDocumentBuilder();
        	Document doc = builder.parse(inputSource);

        	return getCapabilitiesData(doc);

    	} catch (MalformedURLException e) {
    		log.debug("URL is malformed: " + urlStr, e);
    		throw new RuntimeException(ERROR_GETCAP_INVALID_URL, e);

    	} catch (IOException e) {
    		log.debug("IO-Exception occured with url: " + urlStr, e);
    		throw new RuntimeException(ERROR_GETCAP, e);

    	} catch (Exception e) {
    		log.debug("A general exception occured with url: " + urlStr, e);
    		throw new RuntimeException(ERROR_GETCAP, e);
    	}    
    }

    public CapabilitiesBean getCapabilitiesData(Document doc) throws XPathExpressionException {
        ICapabilitiesParser capDoc = CapabilitiesParserFactory.getDocument(doc, sysListMapper);
        return capDoc.getCapabilitiesData(doc);
    }
    
    public SysListCache getSysListMapper() {
        return sysListMapper;
    }

    public void setSysListMapper(SysListCache syslistCache) {
        this.sysListMapper = syslistCache;
    }

}