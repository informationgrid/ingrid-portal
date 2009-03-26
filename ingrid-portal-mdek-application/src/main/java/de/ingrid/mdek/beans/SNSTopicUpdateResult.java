package de.ingrid.mdek.beans;

public class SNSTopicUpdateResult {
	private String term;
	private String type;
	private String action;
	private int objects;
	private int addresses;

	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public int getObjects() {
		return objects;
	}
	public void setObjects(int objects) {
		this.objects = objects;
	}
	public int getAddresses() {
		return addresses;
	}
	public void setAddresses(int addresses) {
		this.addresses = addresses;
	}

	public String[] toStringArray() {
		return new String[] { term, type, action, String.valueOf(objects), String.valueOf(addresses) };
	}
}
