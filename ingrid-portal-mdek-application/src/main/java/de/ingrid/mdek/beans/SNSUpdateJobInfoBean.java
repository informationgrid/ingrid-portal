/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.mdek.beans;

import java.util.ArrayList;
import java.util.List;

public class SNSUpdateJobInfoBean extends JobInfoBean {

	private List<SNSTopicUpdateResult> snsUpdateResults;

	public SNSUpdateJobInfoBean() {
	}

	// Convenience method to copy all data contained in a JobInfoBean into a new
	// URL Info obj
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

	// Retrieve the entries from snsUpdateResults as list of string arrays (for
	// CSV Export)
	public List<String[]> getEntries() {
		List<String[]> entries = new ArrayList<String[]>();

		entries.add(new String[] { "Titel", "Alternativer Titel", "Typ",
				"Aktion", "Objekte", "Adressen" });

		if (snsUpdateResults != null) {
			for (SNSTopicUpdateResult snsResult : snsUpdateResults) {
				entries.add(snsResult.toStringArray());
			}
		}
		return entries;
	}
}
