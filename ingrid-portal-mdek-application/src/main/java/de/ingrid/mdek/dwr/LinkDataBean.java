package de.ingrid.mdek.dwr;

import java.util.Date;


public class LinkDataBean {
	public Integer title;
	public Date date;
	public String version;

	public Integer getTitle() {
		return title;
	}
	public void setTitle(Integer title) {
		this.title = title;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
}
