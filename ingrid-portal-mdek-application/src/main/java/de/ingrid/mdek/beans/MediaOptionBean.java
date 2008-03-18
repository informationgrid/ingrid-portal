package de.ingrid.mdek.beans;

public class MediaOptionBean {
	public Integer name;
	public String location;
	public Double transferSize;

	public Integer getName() {
		return name;
	}
	public void setName(Integer name) {
		this.name = name;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public Double getTransferSize() {
		return transferSize;
	}
	public void setTransferSize(Double transferSize) {
		this.transferSize = transferSize;
	}
}
