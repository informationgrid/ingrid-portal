/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2015 wemove digital solutions GmbH
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
package de.ingrid.mdek.beans.address;

public class MdekAddressSimpleBean {
	
	private String uuid;
	private String nodeDocType;
	private Boolean writePermission;
	
	private String modificationTime;
	private String workState;
	private Integer addressClass;
	private String organisation;
    private String name;
    private String givenName;
	
	
	public MdekAddressSimpleBean() {
	}


    public String getUuid() {
        return uuid;
    }


    public void setUuid(String uuid) {
        this.uuid = uuid;
    }


    public String getNodeDocType() {
        return nodeDocType;
    }


    public void setNodeDocType(String nodeDocType) {
        this.nodeDocType = nodeDocType;
    }


    public Boolean getWritePermission() {
        return writePermission;
    }


    public void setWritePermission(Boolean writePermission) {
        this.writePermission = writePermission;
    }


    public String getModificationTime() {
        return modificationTime;
    }


    public void setModificationTime(String modificationTime) {
        this.modificationTime = modificationTime;
    }


    public String getWorkState() {
        return workState;
    }


    public void setWorkState(String workState) {
        this.workState = workState;
    }


    public Integer getAddressClass() {
        return addressClass;
    }


    public void setAddressClass(Integer addressClass) {
        this.addressClass = addressClass;
    }


    public String getOrganisation() {
        return organisation;
    }


    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getGivenName() {
        return givenName;
    }


    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }
	
	
}
