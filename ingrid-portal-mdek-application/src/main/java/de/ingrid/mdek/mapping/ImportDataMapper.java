package de.ingrid.mdek.mapping;

import java.io.InputStream;


public interface ImportDataMapper {
	public InputStream convert(InputStream in);
	
	public void setDataProvider(ImportDataProvider provider);
}
