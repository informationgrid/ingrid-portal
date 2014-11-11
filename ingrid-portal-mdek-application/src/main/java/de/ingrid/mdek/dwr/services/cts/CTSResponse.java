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
