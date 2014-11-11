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
package de.ingrid.mdek.quartz.jobs.util;

public class URLObjectReference {
	private String objectUuid;
	private String objectName;
	private Integer objectClass;
	private String urlReferenceDescription;
	private URLState urlState;

	public String getObjectUuid() {
		return objectUuid;
	}
	public void setObjectUuid(String objectUuid) {
		this.objectUuid = objectUuid;
	}
	public String getObjectName() {
		return objectName;
	}
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}
	public Integer getObjectClass() {
		return objectClass;
	}
	public void setObjectClass(Integer objectClass) {
		this.objectClass = objectClass;
	}
	public String getUrlReferenceDescription() {
		return urlReferenceDescription;
	}
	public void setUrlReferenceDescription(String urlReferenceDescription) {
		this.urlReferenceDescription = urlReferenceDescription;
	}
	public URLState getUrlState() {
		return urlState;
	}
	public void setUrlState(URLState urlState) {
		this.urlState = urlState;
	}
}
