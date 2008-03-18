package de.ingrid.mdek.beans;

public class KeyValuePair {
	public Integer key;
	public String value;

	public KeyValuePair() {
//		this.key = -1;
//		this.value = null;
	}

	public KeyValuePair(Integer key, String value) {
		this.key = key;
		this.value = value;
	}
	
	public Integer getKey() {
		return key;
	}
	public void setKey(Integer key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
