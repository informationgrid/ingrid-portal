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
