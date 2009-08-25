package de.ingrid.portal.om;

import java.util.Date;

public class IngridTinyUrlSource {

	private Long id;
	private String tinyUserRef;
	private String tinyKey;
	private String tinyName;
	private String tinyConfig;
	private Date tinyDate;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTinyUserRef() {
		return tinyUserRef;
	}
	public void setTinyUserRef(String tinyUserRef) {
		this.tinyUserRef = tinyUserRef;
	}
	public String getTinyKey() {
		return tinyKey;
	}
	public void setTinyKey(String tinyKey) {
		this.tinyKey = tinyKey;
	}
	public String getTinyName() {
		return tinyName;
	}
	public void setTinyName(String tinyName) {
		this.tinyName = tinyName;
	}
	public String getTinyConfig() {
		return tinyConfig;
	}
	public void setTinyConfig(String tinyConfig) {
		this.tinyConfig = tinyConfig;
	}
	public Date getTinyDate() {
		return tinyDate;
	}
	public void setTinyDate(Date tinyDate) {
		this.tinyDate = tinyDate;
	}
	
	
}