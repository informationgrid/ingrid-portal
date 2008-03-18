package de.ingrid.mdek.beans;

public class ScaleBean {
	public Integer scale;
	public Double groundResolution;
	public Double scanResolution;


	public Double getGroundResolution() {
		return groundResolution;
	}
	public void setGroundResolution(Double groundResolution) {
		this.groundResolution = groundResolution;
	}
	public Double getScanResolution() {
		return scanResolution;
	}
	public void setScanResolution(Double scanResolution) {
		this.scanResolution = scanResolution;
	}
	public Integer getScale() {
		return scale;
	}
	public void setScale(Integer scale) {
		this.scale = scale;
	}
}
