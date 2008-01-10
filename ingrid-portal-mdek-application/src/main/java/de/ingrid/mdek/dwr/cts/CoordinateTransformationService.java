package de.ingrid.mdek.dwr.cts;

public interface CoordinateTransformationService {
	public String getCoordinates(SpatialReferenceSystem fromSRS, SpatialReferenceSystem toSRS, String coords);
}
