package de.ingrid.mdek.beans;

import java.util.Date;

import de.ingrid.mdek.beans.object.LocationBean;

public class CatalogBean {
	private String uuid;
	private String catalogName;
	private String partnerName;
	private String providerName;
	private String country;
	private String language;
	private LocationBean location;
	private String workflowControl;
	private Integer expiryDuration;
	private Date dateOfCreation;
	private Date dateOfLastModification;
	private String modUuid;

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

	public LocationBean getLocation() {
		return location;
	}

	public void setLocation(LocationBean location) {
		this.location = location;
	}

	public String getPartnerName() {
		return partnerName;
	}

	public void setPartnerName(String partnerName) {
		this.partnerName = partnerName;
	}

	public String getProviderName() {
		return providerName;
	}

	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getWorkflowControl() {
		return workflowControl;
	}

	public void setWorkflowControl(String workflowControl) {
		this.workflowControl = workflowControl;
	}
}
