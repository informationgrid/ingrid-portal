package de.ingrid.mdek.beans;

import java.util.List;

import de.ingrid.mdek.quartz.jobs.util.URLObjectReference;

public class URLJobInfoBean extends JobInfoBean {

	List<URLObjectReference> urlObjectReferences;

	public List<URLObjectReference> getUrlObjectReferences() {
		return urlObjectReferences;
	}

	public void setUrlObjectReferences(List<URLObjectReference> urlObjectReferences) {
		this.urlObjectReferences = urlObjectReferences;
	}
}
