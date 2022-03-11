/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2022 wemove digital solutions GmbH
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
import de.ingrid.mdek.beans.address.MdekAddressBean;
import de.ingrid.mdek.persistence.db.model.UserData;

public class User {

	// General parameters
	private Long id;
	private String addressUuid;
	private List<Long> groupIds;
	private Integer role;
	private String roleName;
	private Long parentUserId;
	private Boolean hasChildren;

	private MdekAddressBean address;
	private UserData userData;

	// Optional list with permissions on a specific object/address. This field is set depending on the backend query.
	private List<IdcPermission> permissions;

	// Group Details
	private Date creationTime;
	private Date modificationTime;
	private String lastEditor;


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
	public String getAddressUuid() {
		return addressUuid;
	}
	public void setAddressUuid(String addressUuid) {
		this.addressUuid = addressUuid;
	}
	public List<Long> getGroupIds() {
		return groupIds;
	}
	public void setGroupIds(List<Long> groupIds) {
		this.groupIds = groupIds;
	}
	public Integer getRole() {
		return role;
	}
	public void setRole(Integer role) {
		this.role = role;
	}
	public Long getParentUserId() {
		return parentUserId;
	}
	public void setParentUserId(Long parentUserId) {
		this.parentUserId = parentUserId;
	}
	public MdekAddressBean getAddress() {
		return address;
	}
	public void setAddress(MdekAddressBean address) {
		this.address = address;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public Boolean getHasChildren() {
		return hasChildren;
	}
	public void setHasChildren(Boolean hasChildren) {
		this.hasChildren = hasChildren;
	}
	public UserData getUserData() {
		return userData;
	}
	public void setUserData(UserData userData) {
		this.userData = userData;
	}
	public List<IdcPermission> getPermissions() {
		return permissions;
	}
	public void setPermissions(List<IdcPermission> permissions) {
		this.permissions = permissions;
	}
}
