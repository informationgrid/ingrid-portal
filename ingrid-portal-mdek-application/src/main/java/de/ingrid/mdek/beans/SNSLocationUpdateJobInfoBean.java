package de.ingrid.mdek.beans;

import java.util.List;

import de.ingrid.mdek.dwr.services.sns.SNSLocationTopic;


public class SNSLocationUpdateJobInfoBean extends JobInfoBean {

	private List<SNSLocationTopic> oldSNSTopics;
	private List<SNSLocationTopic> newSNSTopics;

	private List<SNSLocationTopic> expiredTopics;

	public List<SNSLocationTopic> getOldSNSTopics() {
		return oldSNSTopics;
	}

	public void setOldSNSTopics(List<SNSLocationTopic> oldSNSTopics) {
		this.oldSNSTopics = oldSNSTopics;
	}

	public List<SNSLocationTopic> getNewSNSTopics() {
		return newSNSTopics;
	}

	public void setNewSNSTopics(List<SNSLocationTopic> newSNSTopics) {
		this.newSNSTopics = newSNSTopics;
	}

	public List<SNSLocationTopic> getExpiredTopics() {
		return expiredTopics;
	}

	public void setExpiredTopics(List<SNSLocationTopic> expiredTopics) {
		this.expiredTopics = expiredTopics;
	}

	public SNSLocationUpdateJobInfoBean() {}

	// Convenience method to copy all data contained in a JobInfoBean into a new URL Info obj
	public SNSLocationUpdateJobInfoBean(JobInfoBean jobInfo) {
		this.setDescription(jobInfo.getDescription());
		this.setEndTime(jobInfo.getEndTime());
		this.setEntityType(jobInfo.getEntityType());
		this.setException(jobInfo.getException());
		this.setNumEntities(jobInfo.getNumEntities());
		this.setNumProcessedEntities(jobInfo.getNumProcessedEntities());
		this.setStartTime(jobInfo.getStartTime());
	}

}
