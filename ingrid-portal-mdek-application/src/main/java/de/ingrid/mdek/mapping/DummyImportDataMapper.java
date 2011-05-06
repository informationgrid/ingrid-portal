package de.ingrid.mdek.mapping;

import java.io.InputStream;

import de.ingrid.mdek.handler.ProtocolHandler;

public class DummyImportDataMapper implements ImportDataMapper {

	public InputStream convert(InputStream in, ProtocolHandler protocolHandler) {
		// no mapping needed here
		return in;
	}

	public void setDataProvider(ImportDataProvider provider) {
		// nothing to do here		
	}

}
