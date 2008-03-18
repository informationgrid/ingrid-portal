package de.ingrid.mdek.beans;


public class JobInfoBean {
	String description;
	Integer numEntities;
	Integer numProcessedEntities;
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Integer getNumEntities() {
		return numEntities;
	}
	public void setNumEntities(Integer numEntities) {
		this.numEntities = numEntities;
	}
	public Integer getNumProcessedEntities() {
		return numProcessedEntities;
	}
	public void setNumProcessedEntities(Integer numProcessedEntities) {
		this.numProcessedEntities = numProcessedEntities;
	}
}
