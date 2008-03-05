package de.ingrid.mdek;

import java.util.ResourceBundle;

public class VersionInformation {

	String name;
	String version;
	String buildNumber;
	String timeStamp;
	
	// Init Method is called by the Spring Framework on initialization
    public void init() throws Exception {
		ResourceBundle resourceBundle = ResourceBundle.getBundle("general");

		this.name = resourceBundle.getString("build.name");
		this.version = resourceBundle.getString("build.version");
		this.buildNumber = resourceBundle.getString("build.number");
		this.timeStamp = resourceBundle.getString("build.timestamp");
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

	public String getTimeStamp() {
		return timeStamp;
	}
}
