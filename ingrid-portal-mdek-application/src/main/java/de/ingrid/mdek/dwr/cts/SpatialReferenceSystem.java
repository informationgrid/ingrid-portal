package de.ingrid.mdek.dwr.cts;

// TODO Move this to another package? Can't make it inline because inline enums don't work with DWR
public enum SpatialReferenceSystem {
//	GEO84, GEO_BESSEL_POTSDAM, GK2, GK3, GK4, GK5, UTM32w, UTM33w, UTM32s, UTM33s, LAMGw;

	// All uppercase since the CTService seems to return arbitrary values
	GEO84, GEO_BESSEL_POTSDAM, GK2, GK3, GK4, GK5, UTM32W, UTM33W, UTM32S, UTM33S, LAMGW;
}
