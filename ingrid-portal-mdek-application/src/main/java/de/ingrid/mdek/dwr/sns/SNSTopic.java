package de.ingrid.mdek.dwr.sns;

import java.util.ArrayList;

public class SNSTopic {

	public Type type; 
	public String id;
	public String title;
	public ArrayList<SNSTopic> children;
	public ArrayList<SNSTopic> parents;

	
	public SNSTopic() {
		type = Type.TOP_TERM;
		this.id = null;
		this.title = null;
		this.children = null;
		this.parents = null;
	}

	public SNSTopic(Type typ, String id, String title)
	{
		this.type = typ;
		this.id = id;
		this.title = title;
		this.children = new ArrayList<SNSTopic>();
		this.parents = new ArrayList<SNSTopic>();
	}
	
	public void setTyp(Type type) {
		this.type = type;
	}

	public Type getType() {
		return type;
	}
/*
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
*/
	public String getTopicId() {
		return id;
	}

	public void setTopicId(String id) {
		this.id = id;
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
}
