/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2023 wemove digital solutions GmbH
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

import com.thoughtworks.xstream.security.AnyTypePermission;
import org.apache.log4j.Logger;

import com.thoughtworks.xstream.XStream;

public class CoordinateTransformationServiceImpl implements CoordinateTransformationService {

	private static final Logger log = Logger.getLogger(CoordinateTransformationServiceImpl.class);
	
	private static final String REQUEST_KEY = "REQUEST";
	private static final String GET_COORDINATES_REQUEST = "GetCoordinates";
	private static final String SRS_SRC_KEY = "FROMSRS";
	private static final String SRS_DST_KEY = "TOSRS";
	private static final String COORDINATES_KEY = "COORDS";


	private String serviceURLStr;
	private XStream xstream;
	
	// standard values, can be overwritten in the properties file
	private int connectionTimeout = 5000;
	private int requestTimeout = 5000;


	// Init Method is called by the Spring Framework on initialization
	public void init() {
		String resource;
		ResourceBundle resourceBundle = ResourceBundle.getBundle("cts");
		serviceURLStr = resourceBundle.getString("cts.serviceURL");
		
		// XStream initialization
		xstream = new XStream();
		xstream.addPermission(AnyTypePermission.ANY);
	    xstream.alias("CTS_Response", CTSResponse.class);
	    xstream.registerConverter(new CTSResponseConverter());

	    // Optional parameters
		try {
			resource = resourceBundle.getString("cts.connectionTimeout");
			connectionTimeout = Integer.valueOf(resource);
		} catch (MissingResourceException e) {
		    log.error("MissingResourceException.", e);
		}
		try {
			resource = resourceBundle.getString("cts.requestTimeout");
			requestTimeout = Integer.valueOf(resource);
		} catch (MissingResourceException e) {
		    log.error("MissingResourceException.", e);
		}
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
			ctsConnection.setConnectTimeout(connectionTimeout);
			ctsConnection.setReadTimeout(requestTimeout);
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
