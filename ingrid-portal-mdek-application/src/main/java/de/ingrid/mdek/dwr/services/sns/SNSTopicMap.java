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
