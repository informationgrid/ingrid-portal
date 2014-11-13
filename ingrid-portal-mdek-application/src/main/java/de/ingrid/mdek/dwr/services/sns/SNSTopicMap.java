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
package de.ingrid.mdek.dwr.services.sns;

import java.util.List;

public class SNSTopicMap {

	private IndexedDocument indexedDocument;
	private List<SNSTopic> thesaTopics;
	private List<SNSLocationTopic> locationTopics;
	private List<SNSEventTopic> eventTopics;

	public IndexedDocument getIndexedDocument() {
		return indexedDocument;
	}

	public void setIndexedDocument(IndexedDocument indexedDocument) {
		this.indexedDocument = indexedDocument;
	}

	public List<SNSTopic> getThesaTopics() {
		return thesaTopics;
	}

	public void setThesaTopics(List<SNSTopic> thesaTopics) {
		this.thesaTopics = thesaTopics;
	}

	public List<SNSLocationTopic> getLocationTopics() {
		return locationTopics;
	}

	public void setLocationTopics(List<SNSLocationTopic> locationTopics) {
		this.locationTopics = locationTopics;
	}

	public List<SNSEventTopic> getEventTopics() {
		return eventTopics;
	}

	public void setEventTopics(List<SNSEventTopic> eventTopics) {
		this.eventTopics = eventTopics;
	}

}
