package de.ingrid.mdek.beans.security;

import java.util.Date;

import de.ingrid.mdek.beans.address.MdekAddressBean;

public class User {

	// General parameters
	private Long id;
	private String addressUuid;
	private Long groupId;
	private Integer role;
	private Integer parentUserId;

	private MdekAddressBean address;
	
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
	public Long getGroupId() {
		return groupId;
	}
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
	public Integer getRole() {
		return role;
	}
	public void setRole(Integer role) {
		this.role = role;
	}
	public Integer getParentUserId() {
		return parentUserId;
	}
	public void setParentUserId(Integer parentUserId) {
		this.parentUserId = parentUserId;
	}
	public MdekAddressBean getAddress() {
		return address;
	}
	public void setAddress(MdekAddressBean address) {
		this.address = address;
	}
}
