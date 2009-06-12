package de.ingrid.mdek.mapping;

import java.util.HashMap;

public class DataMapperFactory {
	
	// Injected by Spring
	private HashMap<String, ImportDataMapper> mapperClasses;
	


	public ImportDataMapper getMapper(String key) {
		if (mapperClasses.get(key) == null) {
			
			return null;
		}
		
		return mapperClasses.get(key);
	}

	public void setMapperClasses(HashMap<String, ImportDataMapper> mapper) {
		this.mapperClasses = mapper;
	}
	
}
