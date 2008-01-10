package de.ingrid.mdek.dwr.cts;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

public class CoordinateTransformationServiceImpl implements CoordinateTransformationService {

	private final static Logger log = Logger.getLogger(CoordinateTransformationServiceImpl.class);

	private ResourceBundle resourceBundle; 
	private URL serviceURL;
	
	// Init Method is called by the Spring Framework on initialization
	public void init() throws Exception {
		resourceBundle = ResourceBundle.getBundle("cts");
		serviceURL = new URL(resourceBundle.getString("cts.serviceURL"));
	}


	public String getCoordinates(
			SpatialReferenceSystem fromSRS,
			SpatialReferenceSystem toSRS,
			String coords) {
		// TODO Auto-generated method stub
		return null;
	}

}
