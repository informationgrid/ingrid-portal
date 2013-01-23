package de.ingrid.mdek.dwr.services.cts;

import java.io.IOException;

public interface CoordinateTransformationService {
	
	public enum SpatialReferenceSystem {
		// All uppercase since the CTService seems to return arbitrary values
		GEO_WGS84, GEO_DHDN, GK2, GK3, GK4, GK5, UTM32e, UTM33e, UTM32s, UTM33s, LAMGe;
	}

	public CTSResponse getCoordinates (
			SpatialReferenceSystem fromSRS,
			SpatialReferenceSystem toSRS,
			Coordinate coord) throws IOException;
}
