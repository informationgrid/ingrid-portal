package de.ingrid.mdek.quartz.jobs.util;

public class URLObjectReference {
	private String objectUuid;
	private String objectName;
	private Integer objectClass;
	private String urlReferenceDescription;
	private URLState urlState;

	public String getObjectUuid() {
		return objectUuid;
	}
	public void setObjectUuid(String objectUuid) {
		this.objectUuid = objectUuid;
	}
	public String getObjectName() {
		return objectName;
	}
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}
	public Integer getObjectClass() {
		return objectClass;
	}
	public void setObjectClass(Integer objectClass) {
		this.objectClass = objectClass;
	}
	public String getUrlReferenceDescription() {
		return urlReferenceDescription;
	}
	public void setUrlReferenceDescription(String urlReferenceDescription) {
		this.urlReferenceDescription = urlReferenceDescription;
	}
	public URLState getUrlState() {
		return urlState;
	}
	public void setUrlState(URLState urlState) {
		this.urlState = urlState;
	}
}
