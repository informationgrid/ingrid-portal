package de.ingrid.mdek.dwr.cts;

import java.io.IOException;

public interface CoordinateTransformationService {
	
	public Coordinate getCoordinates (
			SpatialReferenceSystem fromSRS,
			SpatialReferenceSystem toSRS,
			Coordinate coord) throws IOException;
}
