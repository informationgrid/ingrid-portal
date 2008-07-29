package de.ingrid.mdek.dwr.services.sns;

import java.util.ArrayList;

public class SNSTopicMap {

	IndexedDocument indexedDocument;
	ArrayList<SNSTopic> thesaTopics;
	ArrayList<SNSLocationTopic> locationTopics;
	ArrayList<SNSEventTopic> eventTopics;

	public IndexedDocument getIndexedDocument() {
		return indexedDocument;
	}
	public void setIndexedDocument(IndexedDocument indexedDocument) {
		this.indexedDocument = indexedDocument;
	}
	public ArrayList<SNSTopic> getThesaTopics() {
		return thesaTopics;
	}
	public void setThesaTopics(ArrayList<SNSTopic> thesaTopics) {
		this.thesaTopics = thesaTopics;
	}
	public ArrayList<SNSLocationTopic> getLocationTopics() {
		return locationTopics;
	}
	public void setLocationTopics(ArrayList<SNSLocationTopic> locationTopics) {
		this.locationTopics = locationTopics;
	}
	public ArrayList<SNSEventTopic> getEventTopics() {
		return eventTopics;
	}
	public void setEventTopics(ArrayList<SNSEventTopic> eventTopics) {
		this.eventTopics = eventTopics;
	}

}
