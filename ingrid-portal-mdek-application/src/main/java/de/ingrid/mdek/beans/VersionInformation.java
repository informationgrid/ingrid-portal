package de.ingrid.mdek.beans;

import java.util.Date;
import java.util.ResourceBundle;

public class VersionInformation {

	private String name;
	private String version;
	private String buildNumber;
	private Date timeStamp;

	// Init Method is called by the Spring Framework on initialization
	public void init() throws Exception {
		ResourceBundle resourceBundle = ResourceBundle.getBundle("general");

		this.name = resourceBundle.getString("build.name");
		this.version = resourceBundle.getString("build.version");
		this.buildNumber = resourceBundle.getString("build.number");
		try {
			this.timeStamp = new Date(Long.valueOf(resourceBundle
					.getString("build.timestamp")));
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

	public void setName(String name) {
		this.name = name;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setBuildNumber(String buildNumber) {
		this.buildNumber = buildNumber;
	}

	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}
}
