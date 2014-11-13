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


public class JobInfoBean {

	public enum EntityType { ADDRESS, OBJECT, URL }

	private String description;
	private String frontendMessages;
	private EntityType entityType;
	private Integer numEntities;
	private Integer numObjects;
	private Integer numAddresses;
	private Integer numProcessedEntities;
	private Integer numProcessedObjects;
	private Integer numProcessedAddresses;
	private Date startTime;
	private Date endTime;
	private Exception exception;

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Integer getNumEntities() {
		return numEntities;
	}
	public void setNumEntities(Integer numEntities) {
		this.numEntities = numEntities;
	}
	public Integer getNumObjects() {
		return numObjects;
	}
	public void setNumObjects(Integer numObj) {
		this.numObjects = numObj;
	}
	public Integer getNumAddresses() {
		return numAddresses;
	}
	public void setNumAddresses(Integer numAddresses) {
		this.numAddresses = numAddresses;
	}
	public Integer getNumProcessedEntities() {
		return numProcessedEntities;
	}
	public void setNumProcessedEntities(Integer numProcessedEntities) {
		this.numProcessedEntities = numProcessedEntities;
	}
	public Integer getNumProcessedObjects() {
		return numProcessedObjects;
	}
	public void setNumProcessedObjects(Integer numProcessedObjects) {
		this.numProcessedObjects = numProcessedObjects;
	}
	public Integer getNumProcessedAddresses() {
		return numProcessedAddresses;
	}
	public void setNumProcessedAddresses(Integer numProcessedAddresses) {
		this.numProcessedAddresses = numProcessedAddresses;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public Exception getException() {
		return exception;
	}
	public void setException(Exception exception) {
		this.exception = exception;
	}
	public EntityType getEntityType() {
		return entityType;
	}
	public void setEntityType(EntityType entityType) {
		this.entityType = entityType;
	}
    public String getFrontendMessages() {
        return frontendMessages;
    }
    public void setFrontendMessages(String frontendMessages) {
        this.frontendMessages = frontendMessages;
    }
}
