package de.ingrid.mdek.dwr.cts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.log4j.Logger;

import com.thoughtworks.xstream.XStream;

public class CoordinateTransformationServiceImpl implements CoordinateTransformationService {

	private final static Logger log = Logger.getLogger(CoordinateTransformationServiceImpl.class);
	
	private final static String REQUEST_KEY = "REQUEST";
	private final static String GET_COORDINATES_REQUEST = "GetCoordinates";
	private final static String SRS_SRC_KEY = "FROMSRS";
	private final static String SRS_DST_KEY = "TOSRS";
	private final static String COORDINATES_KEY = "COORDS";


	private ResourceBundle resourceBundle; 
	private String serviceURLStr;
	private XStream xstream;
	
	// standard values, can be overwritten in the properties file
	private int CONNECTION_TIMEOUT = 5000;
	private int REQUEST_TIMEOUT = 5000;


	// Init Method is called by the Spring Framework on initialization
	public void init() {
		String resource;
		resourceBundle = ResourceBundle.getBundle("cts");
		serviceURLStr = resourceBundle.getString("cts.serviceURL");
		
		// XStream initialization
		xstream = new XStream();
	    xstream.alias("CTS_Response", CTSResponse.class);
	    xstream.registerConverter(new CTSResponseConverter());

	    // Optional parameters
		try {
			resource = resourceBundle.getString("cts.connectionTimeout");
			CONNECTION_TIMEOUT = Integer.valueOf(resource);
		} catch (MissingResourceException e) {}
		try {
			resource = resourceBundle.getString("cts.requestTimeout");
			REQUEST_TIMEOUT = Integer.valueOf(resource);
		} catch (MissingResourceException e) {}
	}


	public CTSResponse getCoordinates (
			SpatialReferenceSystem fromSRS,
			SpatialReferenceSystem toSRS,
			Coordinate coord) throws IOException {
		URLConnection ctsConnection;

		// Buid the URL from the given parameters
		URL serviceURL = null;
		try {
			serviceURL = new URL(
					serviceURLStr+
					"?"+REQUEST_KEY+"="+GET_COORDINATES_REQUEST+
					"&"+SRS_SRC_KEY+"="+fromSRS+
					"&"+SRS_DST_KEY+"="+toSRS+
					"&"+COORDINATES_KEY+"="+URLEncoder.encode(coord.toString(), "UTF-8"));
		} catch (MalformedURLException e) {
			log.error(e);
			throw new IOException("Error while creating the service URL");
		}

		try {
			ctsConnection = serviceURL.openConnection();	// Proxy parameter?

			// Set general parameters for the connection
			ctsConnection.setAllowUserInteraction(false);
			ctsConnection.setConnectTimeout(CONNECTION_TIMEOUT);
			ctsConnection.setReadTimeout(REQUEST_TIMEOUT);
			ctsConnection.setDoInput(true);
			ctsConnection.setDoOutput(false);
	
			log.debug("CTS Request URL: "+ctsConnection.getURL());
	
			// Start the request
			ctsConnection.connect();
	
	//		log.debug("Content type: "+ctsConnection.getContentType());
			return (CTSResponse) xstream.fromXML(ctsConnection.getInputStream());

/*
			String testResponse = 
		    	"<?xml version=\"1.0\"?>"+
		    	"<CTS_Response version=\"1.0\">"+
		    	"<SRS name=\"GK3\" />"+
		    	"<COORDS values=\"3571769.7220 5540886.8658 3640462.9333 5653551.3119 \" />"+
		    	"</CTS_Response>";		
	
			CTSResponse res = (CTSResponse) xstream.fromXML(testResponse);
			res.setSpatialReferenceSystem(toSRS);
			return res;
*/
		} catch (IOException e) {
			log.error("Error while communicating with the transformation service", e);
			throw e;
		}
	}
}
