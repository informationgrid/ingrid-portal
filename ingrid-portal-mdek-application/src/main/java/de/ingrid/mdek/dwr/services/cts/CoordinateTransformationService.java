package de.ingrid.mdek.dwr.services.cts;

import java.io.IOException;

public interface CoordinateTransformationService {
	
	public enum SpatialReferenceSystem {
		// All uppercase since the CTService seems to return arbitrary values
		GEO84, GEO_BESSEL_POTSDAM, GK2, GK3, GK4, GK5, UTM32W, UTM33W, UTM32S, UTM33S, LAMGW;
	}

	public CTSResponse getCoordinates (
			SpatialReferenceSystem fromSRS,
			SpatialReferenceSystem toSRS,
			Coordinate coord) throws IOException;
}
