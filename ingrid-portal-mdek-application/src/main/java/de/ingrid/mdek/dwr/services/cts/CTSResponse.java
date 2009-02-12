package de.ingrid.mdek.dwr.services.cts;

import de.ingrid.mdek.dwr.services.cts.CoordinateTransformationService.SpatialReferenceSystem;

public class CTSResponse {
	SpatialReferenceSystem spatialReferenceSystem;
	Coordinate coordinate;

	public String toString() {
		return "["+spatialReferenceSystem+", "+coordinate+"]";
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
}
