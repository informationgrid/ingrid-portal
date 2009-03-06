package de.ingrid.mdek.beans;


public class CodeListJobInfoBean extends JobInfoBean {
	private String uuid  = null;
	private String title = null;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setUuid(String uuid) {
		this.uuid  = uuid;
	}

	public String getUuid() {
		return uuid;
	}
	
}
