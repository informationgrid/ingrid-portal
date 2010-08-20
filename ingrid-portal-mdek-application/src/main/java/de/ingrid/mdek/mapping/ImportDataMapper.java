package de.ingrid.mdek.mapping;

import java.io.InputStream;

import de.ingrid.mdek.handler.ProtocolHandler;


public interface ImportDataMapper {
	public InputStream convert(InputStream in, ProtocolHandler protocolHandler);
	
	public void setDataProvider(ImportDataProvider provider);
}
