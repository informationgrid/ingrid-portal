package de.ingrid.mdek;

import java.util.Date;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;


public class VersionInformation {

	private final static Logger log = Logger.getLogger(VersionInformation.class);	

	String name;
	String version;
	String buildNumber;
	Date timeStamp;
	
	// Init Method is called by the Spring Framework on initialization
    public void init() throws Exception {
		ResourceBundle resourceBundle = ResourceBundle.getBundle("general");

		this.name = resourceBundle.getString("build.name");
		this.version = resourceBundle.getString("build.version");
		this.buildNumber = resourceBundle.getString("build.number");
		try {
			this.timeStamp = new Date(Long.valueOf(resourceBundle.getString("build.timestamp")));
		} catch (NumberFormatException e) {
			this.timeStamp = new Date();
		}
    }

    // getter for dwr
    public VersionInformation get() {
    	return this;
    }

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	public String getBuildNumber() {
		return buildNumber;
	}

	public Date getTimeStamp() {
		return timeStamp;
	}
}
