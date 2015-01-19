/*
 * **************************************************-
 * Ingrid Portal Apps
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
package de.ingrid.portal.om;

import java.util.Date;

public class IngridTinyUrlSource {

	private Long id;
	private String tinyUserRef;
	private String tinyKey;
	private String tinyName;
	private String tinyConfig;
	private Date tinyDate;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTinyUserRef() {
		return tinyUserRef;
	}
	public void setTinyUserRef(String tinyUserRef) {
		this.tinyUserRef = tinyUserRef;
	}
	public String getTinyKey() {
		return tinyKey;
	}
	public void setTinyKey(String tinyKey) {
		this.tinyKey = tinyKey;
	}
	public String getTinyName() {
		return tinyName;
	}
	public void setTinyName(String tinyName) {
		this.tinyName = tinyName;
	}
	public String getTinyConfig() {
		return tinyConfig;
	}
	public void setTinyConfig(String tinyConfig) {
		this.tinyConfig = tinyConfig;
	}
	public Date getTinyDate() {
		return tinyDate;
	}
	public void setTinyDate(Date tinyDate) {
		this.tinyDate = tinyDate;
	}
	
	
}