package de.ingrid.mdek.quartz.jobs.util;

import java.util.Date;

public class ExpiredDataset {

	public enum Type { OBJECT, ADDRESS }

	private String title;
	private String uuid;
	private Type type;
	private Date lastModified;
	private String lastModifiedBy;
	private String responsibleUserEmail;

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public Date getLastModified() {
		return lastModified;
	}
	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}
	public String getLastModifiedBy() {
		return lastModifiedBy;
	}
	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}
	public String getResponsibleUserEmail() {
		return responsibleUserEmail;
	}
	public void setResponsibleUserEmail(String responsibleUserEmail) {
		this.responsibleUserEmail = responsibleUserEmail;
	}
}
