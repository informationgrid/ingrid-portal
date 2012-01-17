package de.ingrid.mdek.dwr.services.cts;

import de.ingrid.mdek.dwr.services.cts.CoordinateTransformationService.SpatialReferenceSystem;

public class CTSResponse {
	private SpatialReferenceSystem spatialReferenceSystem;
	private Coordinate coordinate;
	private String errorMsg = null;

	public String toString() {
		return "[" + spatialReferenceSystem + ", " + coordinate + ", " + errorMsg + "]";
	}

	public SpatialReferenceSystem getSpatialReferenceSystem() {
		return spatialReferenceSystem;
	}

	public void setSpatialReferenceSystem(
			SpatialReferenceSystem spatialReferenceSystem) {
		this.spatialReferenceSystem = spatialReferenceSystem;
	}

	public Coordinate getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(Coordinate coordinate) {
		this.coordinate = coordinate;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
}
