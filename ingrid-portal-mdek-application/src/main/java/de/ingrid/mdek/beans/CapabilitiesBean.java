package de.ingrid.mdek.beans;

import java.util.List;

import de.ingrid.mdek.beans.object.OperationBean;

public class CapabilitiesBean {

	private String title;
	private String description;
	private String serviceType;
	private List<String> versions;

	private List<OperationBean> operations;

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

	public List<OperationBean> getOperations() {
		return operations;
	}

	public void setOperations(List<OperationBean> operations) {
		this.operations = operations;
	}

	public List<String> getVersions() {
		return versions;
	}

	public void setVersions(List<String> versions) {
		this.versions = versions;
	}

}