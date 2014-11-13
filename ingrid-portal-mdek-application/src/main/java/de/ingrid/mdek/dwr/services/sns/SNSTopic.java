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

import java.util.ArrayList;
import java.util.List;

public class SNSTopic {

	public enum Type {
		TOP_TERM, NODE_LABEL, DESCRIPTOR, NON_DESCRIPTOR
	}

	public enum Source {
		FREE, UMTHES, GEMET
	}

	private Type type;
	private Source source;
	private String topicId;
	private String title;			// UMTHES, if no GEMET OR GEMET
	private String alternateTitle;  // UMTHES, if GEMET is different
	private String gemetId;
	private List<SNSTopic> children;
	private List<SNSTopic> parents;
	private List<SNSTopic> synonyms;
	private List<SNSTopic> descriptors;
	private boolean isExpired;

	private List<String> inspireList;

	public SNSTopic() {
		// Default to topterm and umthes
		type 				= Type.TOP_TERM;
		source 				= Source.UMTHES;
		this.topicId 		= null;
		this.gemetId 		= null;
		this.title 			= null;
		this.alternateTitle = null;
		this.children 		= null;
		this.parents 		= null;
		this.synonyms 		= null;
		this.descriptors	= null;
		this.inspireList 	= new ArrayList<String>();
		this.setExpired( false );
	}

	public SNSTopic(Type typ, Source source, String id, String title,
			String alternateTitle, String gemetId) {
		this.source 		= source;
		this.type 			= typ;
		this.topicId 		= id;
		this.title 			= title;
		this.alternateTitle = alternateTitle;
		this.gemetId 		= gemetId;
		this.children 		= new ArrayList<SNSTopic>();
		this.parents 		= new ArrayList<SNSTopic>();
		this.synonyms 		= new ArrayList<SNSTopic>();
		this.descriptors	= new ArrayList<SNSTopic>();
		this.inspireList 	= new ArrayList<String>();
		this.setExpired( false );
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Type getType() {
		return type;
	}

	public String getTopicId() {
		return topicId;
	}

	public void setTopicId(String id) {
		this.topicId = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<SNSTopic> getChildren() {
		return children;
	}

	public void setChildren(List<SNSTopic> children) {
		this.children = children;
	}

	public List<SNSTopic> getParents() {
		return parents;
	}

	public void setParents(List<SNSTopic> parents) {
		this.parents = parents;
	}

	public List<SNSTopic> getSynonyms() {
		return synonyms;
	}

	public void setSynonyms(List<SNSTopic> synonyms) {
		this.synonyms = synonyms;
	}

	public List<SNSTopic> getDescriptors() {
		return descriptors;
	}

	public void setDescriptors(List<SNSTopic> descriptors) {
		this.descriptors = descriptors;
	}

	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
	}

	public String getGemetId() {
		return gemetId;
	}

	public void setGemetId(String gemetId) {
		this.gemetId = gemetId;
	}

	public String toString() {
		return "[" + type + ", " + source + ", " + title + ", "
				+ alternateTitle + ", " + topicId + ", " + gemetId + "]";
	}

	public void setInspireList(List<String> inspireList) {
		this.inspireList = inspireList;
	}

	public List<String> getInspireList() {
		return inspireList;
	}

	public String getAlternateTitle() {
		return alternateTitle;
	}

	public void setAlternateTitle(String alternateTitle) {
		this.alternateTitle = alternateTitle;
	}

    public boolean isExpired() {
        return isExpired;
    }

    public void setExpired( boolean expired ) {
        this.isExpired = expired;
    }
}
