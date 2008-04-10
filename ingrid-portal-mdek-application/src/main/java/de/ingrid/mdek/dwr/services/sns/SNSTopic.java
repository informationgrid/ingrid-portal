package de.ingrid.mdek.dwr.services.sns;

import java.util.ArrayList;

public class SNSTopic {

	public Type type; 
	public String topicId;
	public String title;
	public ArrayList<SNSTopic> children;
	public ArrayList<SNSTopic> parents;
	public ArrayList<SNSTopic> synonyms;


	public SNSTopic() {
		type = Type.TOP_TERM;
		this.topicId = null;
		this.title = null;
		this.children = null;
		this.parents = null;
		this.synonyms = null;
	}

	public SNSTopic(Type typ, String id, String title)
	{
		this.type = typ;
		this.topicId = id;
		this.title = title;
		this.children = new ArrayList<SNSTopic>();
		this.parents = new ArrayList<SNSTopic>();
		this.synonyms = new ArrayList<SNSTopic>();
	}
	
	public void setTyp(Type type) {
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
}
