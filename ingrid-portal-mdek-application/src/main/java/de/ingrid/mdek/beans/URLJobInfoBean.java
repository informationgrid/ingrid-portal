package de.ingrid.mdek.beans;

import java.util.List;

import de.ingrid.mdek.quartz.jobs.util.URLObjectReference;

public class URLJobInfoBean extends JobInfoBean {

	private List<URLObjectReference> urlObjectReferences;
	private List<URLObjectReference> capabilitiesReferences;

	public URLJobInfoBean() {}

	// Convenience method to copy all data contained in a JobInfoBean into a new URL Info obj
	public URLJobInfoBean(JobInfoBean jobInfo) {
		this.setDescription(jobInfo.getDescription());
		this.setEndTime(jobInfo.getEndTime());
		this.setEntityType(jobInfo.getEntityType());
		this.setException(jobInfo.getException());
		this.setNumEntities(jobInfo.getNumEntities());
		this.setNumProcessedEntities(jobInfo.getNumProcessedEntities());
		this.setStartTime(jobInfo.getStartTime());
	}

	public List<URLObjectReference> getUrlObjectReferences() {
		return urlObjectReferences;
	}

	public void setUrlObjectReferences(List<URLObjectReference> urlObjectReferences) {
		this.urlObjectReferences = urlObjectReferences;
	}

    public List<URLObjectReference> getCapabilitiesReferences() {
        return capabilitiesReferences;
    }

    public void setCapabilitiesReferences(List<URLObjectReference> capabilitiesReferences) {
        this.capabilitiesReferences = capabilitiesReferences;
    }
}
