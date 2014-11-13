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
