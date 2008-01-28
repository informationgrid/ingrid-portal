package de.ingrid.mdek.dwr;

import java.util.Date;

public class CatalogBean {
	String uuid;
	String catalogName;
	String country;
	String workflowControl;
	Integer expiryDuration;
	Date dateOfCreation;
	Date dateOfLastModification;
	String modUuid;

	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getCatalogName() {
		return catalogName;
	}
	public void setCatalogName(String catalogName) {
		this.catalogName = catalogName;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getWorkflowControl() {
		return workflowControl;
	}
	public void setWorkflowControl(String workflowControl) {
		this.workflowControl = workflowControl;
	}
	public Integer getExpiryDuration() {
		return expiryDuration;
	}
	public void setExpiryDuration(Integer expiryDuration) {
		this.expiryDuration = expiryDuration;
	}
	public Date getDateOfCreation() {
		return dateOfCreation;
	}
	public void setDateOfCreation(Date dateOfCreation) {
		this.dateOfCreation = dateOfCreation;
	}
	public Date getDateOfLastModification() {
		return dateOfLastModification;
	}
	public void setDateOfLastModification(Date dateOfLastModification) {
		this.dateOfLastModification = dateOfLastModification;
	}
	public String getModUuid() {
		return modUuid;
	}
	public void setModUuid(String modUuid) {
		this.modUuid = modUuid;
	}
}
