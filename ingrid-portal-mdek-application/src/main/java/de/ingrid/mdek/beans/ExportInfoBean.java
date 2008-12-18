package de.ingrid.mdek.beans;

import java.util.Date;


public class ExportInfoBean {
	String description;
	Integer numProcessed;
	Integer numTotal;
	Date startTime;
	Date endTime;
	byte[] result;

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Integer getNumProcessed() {
		return numProcessed;
	}
	public void setNumProcessed(Integer numProcessed) {
		this.numProcessed = numProcessed;
	}
	public Integer getNumTotal() {
		return numTotal;
	}
	public void setNumTotal(Integer numTotal) {
		this.numTotal = numTotal;
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
	public byte[] getResult() {
		return result;
	}
	public void setResult(byte[] result) {
		this.result = result;
	}

}
