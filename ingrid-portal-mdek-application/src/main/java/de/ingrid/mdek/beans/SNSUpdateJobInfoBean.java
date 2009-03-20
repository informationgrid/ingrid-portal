package de.ingrid.mdek.beans;

import java.util.List;

import de.ingrid.mdek.dwr.services.sns.SNSTopic;


public class SNSUpdateJobInfoBean extends JobInfoBean {

	private List<SNSTopic> oldSNSTopics;
	private List<SNSTopic> newSNSTopics;

	private List<SNSTopic> expiredTopics;

	private List<String> freeTerms;
	private List<SNSTopic> topicsForFreeTerms;

	public SNSUpdateJobInfoBean() {}

	// Convenience method to copy all data contained in a JobInfoBean into a new URL Info obj
	public SNSUpdateJobInfoBean(JobInfoBean jobInfo) {
		this.setDescription(jobInfo.getDescription());
		this.setEndTime(jobInfo.getEndTime());
		this.setEntityType(jobInfo.getEntityType());
		this.setException(jobInfo.getException());
		this.setNumEntities(jobInfo.getNumEntities());
		this.setNumProcessedEntities(jobInfo.getNumProcessedEntities());
		this.setStartTime(jobInfo.getStartTime());
	}


	public List<SNSTopic> getOldSNSTopics() {
		return oldSNSTopics;
	}


	public void setOldSNSTopics(List<SNSTopic> oldSNSTopics) {
		this.oldSNSTopics = oldSNSTopics;
	}


	public List<SNSTopic> getNewSNSTopics() {
		return newSNSTopics;
	}


	public void setNewSNSTopics(List<SNSTopic> newSNSTopics) {
		this.newSNSTopics = newSNSTopics;
	}


	public List<SNSTopic> getExpiredTopics() {
		return expiredTopics;
	}


	public void setExpiredTopics(List<SNSTopic> expiredTopics) {
		this.expiredTopics = expiredTopics;
	}


	public List<String> getFreeTerms() {
		return freeTerms;
	}


	public void setFreeTerms(List<String> freeTerms) {
		this.freeTerms = freeTerms;
	}


	public List<SNSTopic> getTopicsForFreeTerms() {
		return topicsForFreeTerms;
	}


	public void setTopicsForFreeTerms(List<SNSTopic> topicsForFreeTerms) {
		this.topicsForFreeTerms = topicsForFreeTerms;
	}
}
