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
package de.ingrid.mdek.beans.security;

import java.util.Date;
import java.util.List;

import de.ingrid.mdek.MdekUtilsSecurity.IdcPermission;

public class Group {

	// General parameters
	private String name;
	private Long id;

	// Group Details
	private Date creationTime;
	private Date modificationTime;
	private String lastEditor;

	// Permissions
	private List<Permission> objectPermissions;
	private List<Permission> addressPermissions;
	private List<IdcPermission> groupPermissions;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}

	public Date getModificationTime() {
		return modificationTime;
	}

	public void setModificationTime(Date modificationTime) {
		this.modificationTime = modificationTime;
	}

	public String getLastEditor() {
		return lastEditor;
	}

	public void setLastEditor(String lastEditor) {
		this.lastEditor = lastEditor;
	}

	public List<Permission> getObjectPermissions() {
		return objectPermissions;
	}

	public void setObjectPermissions(List<Permission> objectPermissions) {
		this.objectPermissions = objectPermissions;
	}

	public List<Permission> getAddressPermissions() {
		return addressPermissions;
	}

	public void setAddressPermissions(List<Permission> addressPermissions) {
		this.addressPermissions = addressPermissions;
	}

	public List<IdcPermission> getGroupPermissions() {
		return groupPermissions;
	}

	public void setGroupPermissions(List<IdcPermission> groupPermissions) {
		this.groupPermissions = groupPermissions;
	}
}
