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
