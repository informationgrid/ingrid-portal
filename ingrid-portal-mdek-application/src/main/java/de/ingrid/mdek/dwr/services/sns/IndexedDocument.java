package de.ingrid.mdek.dwr.services.sns;

import java.text.SimpleDateFormat;
import java.util.Date;

public class IndexedDocument {
	private String title;
	private String description;
	private String uri;
	private String lang;
	private Date timeStamp;
	private String at;
	private String from;
	private String to;

	/*public IndexedDocument(TopicMapFragmentIndexedDocument doc) {
		this.title = doc.getTitle();
		this.description = doc.get_abstract();
		this.uri = doc.getUri();
		this.lang = doc.getLang();
		this.timeStamp = doc.getTimestamp();
		this.at = doc.getAt();
		this.from = doc.getFrom();
		this.to = doc.getTo();
	}*/

	public IndexedDocument(de.ingrid.external.om.IndexedDocument doc) {
		this.title = doc.getTitle();
		this.description = doc.getDescription();
		this.uri = doc.getURL().toString();
		this.lang = doc.getLang().getLanguage();
		this.timeStamp = doc.getClassifyTimeStamp();

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		if (doc.getTimeAt() != null)
			this.at = df.format(doc.getTimeAt());			
		if (doc.getTimeFrom() != null)
			this.from = df.format(doc.getTimeFrom());
		if (doc.getTimeTo() != null)
			this.to = df.format(doc.getTimeTo());
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

	public String toString() {
		String result = "[";
		result += "timeStamp: " + this.timeStamp;
		result += ", lang: "+this.lang;
		result += ", title: "+this.title;
		result += ", description: "+this.description;
		result += ", at: "+this.at;
		result += ", from: "+this.from;
		result += ", to: "+this.to;
		result += ", uri: "+this.uri;
		result += "]";
		return result;
	}
}
