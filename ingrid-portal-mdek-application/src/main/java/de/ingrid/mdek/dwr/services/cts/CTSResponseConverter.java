/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2021 wemove digital solutions GmbH
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

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import de.ingrid.mdek.dwr.services.cts.CoordinateTransformationService.SpatialReferenceSystem;

public class CTSResponseConverter implements Converter {

	public boolean canConvert(Class clazz) {
		return clazz.equals(CTSResponse.class);
	}

	public void marshal(Object value, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		// We don't need to marshal response objects.
	}

	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {
		CTSResponse response = new CTSResponse();
		while (reader.hasMoreChildren()) {
			reader.moveDown();
			if ("SRS".equals(reader.getNodeName())) {
				String srsName = reader.getAttribute("name");
				try {
					response.setSpatialReferenceSystem(SpatialReferenceSystem.valueOf(srsName));
				} catch (Exception ex) {
					response.setErrorMsg(ex.getMessage());
				}
			} else if ("Coords".equals(reader.getNodeName())) {
				response.setCoordinate(new Coordinate(reader.getAttribute("values").split(" ")));				
			} else if ("ERROR".equals(reader.getNodeName())) {
				response.setErrorMsg(reader.getAttribute("message"));				
			}
			reader.moveUp();
		}
		
		return response;
	}

}
