/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
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
package de.ingrid.mdek.dwr.services.cts;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

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
			ctsConnection = serviceURL.openConnection();

			// Set general parameters for the connection
			ctsConnection.setAllowUserInteraction(false);
			ctsConnection.setConnectTimeout(CONNECTION_TIMEOUT);
			ctsConnection.setReadTimeout(REQUEST_TIMEOUT);
			ctsConnection.setDoInput(true);
			ctsConnection.setDoOutput(false);

			if (log.isDebugEnabled()) {
				log.debug("CTS Request URL: " + ctsConnection.getURL());				
			}
	
			// Start the request
			ctsConnection.connect();
			CTSResponse ctsResp = (CTSResponse) xstream.fromXML(ctsConnection.getInputStream());

			if (log.isDebugEnabled()) {
				log.debug("CTS Response: " + ctsResp);				
			}
	
			return ctsResp;

		} catch (IOException e) {
			log.error("Error while communicating with the transformation service", e);
			throw e;
		}
	}
}
