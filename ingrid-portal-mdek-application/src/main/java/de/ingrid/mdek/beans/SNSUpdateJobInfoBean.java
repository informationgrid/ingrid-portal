package de.ingrid.mdek.beans;

import java.util.ArrayList;
import java.util.List;


public class SNSUpdateJobInfoBean extends JobInfoBean {

	private List<SNSTopicUpdateResult> snsUpdateResults;


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

	public List<SNSTopicUpdateResult> getSnsUpdateResults() {
		return snsUpdateResults;
	}

	public void setSnsUpdateResults(List<SNSTopicUpdateResult> snsUpdateResults) {
		this.snsUpdateResults = snsUpdateResults;
	}

	// Retrieve the entries from snsUpdateResults as list of string arrays (for CSV Export)
	public List<String[]> getEntries() {
		List<String[]> entries = new ArrayList<String[]>();

		entries.add(new String[] { "Term", "Typ", "Aktion", "Objekte", "Adressen" });

		if (snsUpdateResults != null) {
			for (SNSTopicUpdateResult snsResult : snsUpdateResults) {
				entries.add(snsResult.toStringArray());
			}
		}
		return entries;
	}
}
