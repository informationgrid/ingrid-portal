package de.ingrid.mdek.dwr.services.sns;

import java.util.ArrayList;

public class SNSTopic {

	public enum Type { TOP_TERM, NODE_LABEL, DESCRIPTOR, NON_DESCRIPTOR }
	public enum Source { FREE, UMTHES, GEMET }

	private Type type; 
	private Source source;
	private String topicId;
	private String title;
	private String alternateTitle;
	private String gemetId;
	private ArrayList<SNSTopic> children;
	private ArrayList<SNSTopic> parents;
	private ArrayList<SNSTopic> synonyms;
	
	private ArrayList<String> inspireList;


	public SNSTopic() {
		// Default to topterm and umthes
		type = Type.TOP_TERM;
		source = Source.UMTHES;
		this.topicId = null;
		this.gemetId = null;
		this.title = null;
		this.alternateTitle = null;
		this.children = null;
		this.parents = null;
		this.synonyms = null;
		this.inspireList = new ArrayList<String>();
	}

	public SNSTopic(Type typ, Source source, String id, String title, String alternateTitle, String gemetId) {
		this.source = source;
		this.type = typ;
		this.topicId = id;
		this.title = title;
		this.alternateTitle = alternateTitle;
		this.gemetId = gemetId;
		this.children = new ArrayList<SNSTopic>();
		this.parents = new ArrayList<SNSTopic>();
		this.synonyms = new ArrayList<SNSTopic>();
		this.inspireList = new ArrayList<String>();
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

	public ArrayList<SNSTopic> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<SNSTopic> children) {
		this.children = children;
	}

	public ArrayList<SNSTopic> getParents() {
		return parents;
	}

	public void setParents(ArrayList<SNSTopic> parents) {
		this.parents = parents;
	}

	public ArrayList<SNSTopic> getSynonyms() {
		return synonyms;
	}

	public void setSynonyms(ArrayList<SNSTopic> synonyms) {
		this.synonyms = synonyms;
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
		return "[" + type + ", " + source + ", " + title + ", " + alternateTitle + ", " + topicId + ", " + gemetId + "]";
	}

	public void setInspireList(ArrayList<String> inspireList) {
		this.inspireList = inspireList;
	}

	public ArrayList<String> getInspireList() {
		return inspireList;
	}

	public String getAlternateTitle() {
		return alternateTitle;
	}

	public void setAlternateTitle(String alternateTitle) {
		this.alternateTitle = alternateTitle;
	}
}
