package de.ingrid.mdek.beans;

import java.util.ArrayList;

import de.ingrid.mdek.beans.object.OperationBean;

public class CapabilitiesBean {

	public String title;
	public String description;
	public String serviceType;
	public ArrayList<String> versions;

	public ArrayList<OperationBean> operations;


	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public ArrayList<OperationBean> getOperations() {
		return operations;
	}

	public void setOperations(ArrayList<OperationBean> operations) {
		this.operations = operations;
	}

	public ArrayList<String> getVersions() {
		return versions;
	}

	public void setVersions(ArrayList<String> versions) {
		this.versions = versions;
	}

}