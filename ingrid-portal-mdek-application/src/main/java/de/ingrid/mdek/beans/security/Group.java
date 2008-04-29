package de.ingrid.mdek.beans.security;

import java.util.ArrayList;
import java.util.Date;

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
	private ArrayList<Permission> objectPermissions;
	private ArrayList<Permission> addressPermissions;
	private ArrayList<IdcPermission> groupPermissions;

	
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
	public ArrayList<Permission> getObjectPermissions() {
		return objectPermissions;
	}
	public void setObjectPermissions(ArrayList<Permission> objectPermissions) {
		this.objectPermissions = objectPermissions;
	}
	public ArrayList<Permission> getAddressPermissions() {
		return addressPermissions;
	}
	public void setAddressPermissions(ArrayList<Permission> addressPermissions) {
		this.addressPermissions = addressPermissions;
	}
	public ArrayList<IdcPermission> getGroupPermissions() {
		return groupPermissions;
	}
	public void setGroupPermissions(ArrayList<IdcPermission> groupPermissions) {
		this.groupPermissions = groupPermissions;
	}
}
