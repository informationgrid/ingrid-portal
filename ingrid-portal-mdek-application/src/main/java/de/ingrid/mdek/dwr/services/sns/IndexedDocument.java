package de.ingrid.mdek.dwr.services.sns;

import java.util.Date;

import com.slb.taxi.webservice.xtm.stubs.TopicMapFragmentIndexedDocument;

public class IndexedDocument {
	String title;
	String description;
	String uri;
	String lang;
	Date timeStamp;
	String at;
	String from;
	String to;


	public IndexedDocument(TopicMapFragmentIndexedDocument doc) {
		this.title = doc.getTitle();
		this.description = doc.get_abstract();
		this.uri = doc.getUri();
		this.lang = doc.getLang();
		this.timeStamp = doc.getTimestamp();
		this.at = doc.getAt();
		this.from = doc.getFrom();
		this.to = doc.getTo();
	}
	
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public String getLang() {
		return lang;
	}
	public void setLang(String lang) {
		this.lang = lang;
	}
	public String getAt() {
		return at;
	}
	public void setAt(String at) {
		this.at = at;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}


	public Date getTimeStamp() {
		return timeStamp;
	}


	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}
}
