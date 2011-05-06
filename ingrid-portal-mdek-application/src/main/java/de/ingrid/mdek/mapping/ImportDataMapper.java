package de.ingrid.mdek.mapping;

import java.io.InputStream;

import de.ingrid.mdek.handler.ProtocolHandler;
import de.ingrid.mdek.job.MdekException;


public interface ImportDataMapper {
	public InputStream convert(InputStream in, ProtocolHandler protocolHandler) throws MdekException;
	
	public void setDataProvider(ImportDataProvider provider);
}
