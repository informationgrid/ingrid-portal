package de.ingrid.mdek.beans;

import java.util.Date;


public class JobInfoBean {
	String description;
	Integer numEntities;
	Integer numProcessedEntities;
	Date startTime;
	Date endTime;
	Exception exception;

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
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public Exception getException() {
		return exception;
	}
	public void setException(Exception exception) {
		this.exception = exception;
	}
}
