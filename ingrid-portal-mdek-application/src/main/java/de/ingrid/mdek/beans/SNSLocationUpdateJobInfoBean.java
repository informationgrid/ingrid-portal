package de.ingrid.mdek.beans;

import java.util.ArrayList;
import java.util.List;


public class SNSLocationUpdateJobInfoBean extends JobInfoBean {

	private List<SNSLocationUpdateResult> snsUpdateResults;


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

	public List<SNSLocationUpdateResult> getSnsUpdateResults() {
		return snsUpdateResults;
	}

	public void setSnsUpdateResults(List<SNSLocationUpdateResult> snsUpdateResults) {
		this.snsUpdateResults = snsUpdateResults;
	}

	// Retrieve the entries from snsUpdateResults as list of string arrays (for CSV Export)
	public List<String[]> getEntries() {
		List<String[]> entries = new ArrayList<String[]>();

		entries.add(new String[] { "Titel", "Code", "Aktion", "Objekte" });

		if (snsUpdateResults != null) {
			for (SNSLocationUpdateResult snsResult : snsUpdateResults) {
				entries.add(snsResult.toStringArray());
			}
		}
		return entries;
	}
}
