package de.ingrid.mdek.dwr.cts;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

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
				response.setSpatialReferenceSystem(SpatialReferenceSystem.valueOf(reader.getAttribute("name")));				
			} else if ("COORDS".equals(reader.getNodeName())) {
				response.setCoordinate(new Coordinate(reader.getAttribute("values").split(" ")));				
			}
			reader.moveUp();
		}
		
		return response;
	}

}
