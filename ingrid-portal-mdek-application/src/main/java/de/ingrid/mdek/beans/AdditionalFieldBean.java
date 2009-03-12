package de.ingrid.mdek.beans;

import java.util.List;

public class AdditionalFieldBean {
	public enum Type { TEXT, LIST }

	private Long id;
	private String name;
	private Type type;
	private int size;
	// The following two fields only contain values if the additional field is of type list
	private String listLanguage;
	private List<String> listEntries;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public String getListLanguage() {
		return listLanguage;
	}
	public void setListLanguage(String listLanguage) {
		this.listLanguage = listLanguage;
	}
	public List<String> getListEntries() {
		return listEntries;
	}
	public void setListEntries(List<String> listEntries) {
		this.listEntries = listEntries;
	}
}
