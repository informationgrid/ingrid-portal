/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.mdek.dwr.services.cts;

public class Coordinate {
	private float longitude1;
	private float latitude1;
	private float longitude2;
	private float latitude2;

	public Coordinate() {
	};

	public Coordinate(String[] coords) {
		this.longitude1 = Float.valueOf(coords[0]);
		this.latitude1 = Float.valueOf(coords[1]);
		this.longitude2 = Float.valueOf(coords[2]);
		this.latitude2 = Float.valueOf(coords[3]);
	}

	public String toString() {
		return "" + longitude1 + " " + latitude1 + " " + longitude2 + " "
				+ latitude2;
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
