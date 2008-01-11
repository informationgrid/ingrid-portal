package de.ingrid.mdek.dwr.cts;

public class Coordinate {
	public float longitude1;
	public float latitude1;
	public float longitude2;
	public float latitude2;

	public Coordinate() {};

	public Coordinate(String[] coords) {
		this.longitude1 = Float.valueOf(coords[0]);
		this.latitude1 = Float.valueOf(coords[1]);
		this.longitude2 = Float.valueOf(coords[2]);
		this.latitude2 = Float.valueOf(coords[3]);
	}


	public String toString() {
		return ""+longitude1+" "+latitude1+" "+longitude2+" "+latitude2;
	}

	public float getLongitude1() {
		return longitude1;
	}
	public void setLongitude1(float longitude1) {
		this.longitude1 = longitude1;
	}
	public float getLatitude1() {
		return latitude1;
	}
	public void setLatitude1(float latitude1) {
		this.latitude1 = latitude1;
	}
	public float getLongitude2() {
		return longitude2;
	}
	public void setLongitude2(float longitude2) {
		this.longitude2 = longitude2;
	}
	public float getLatitude2() {
		return latitude2;
	}
	public void setLatitude2(float latitude2) {
		this.latitude2 = latitude2;
	}
}
