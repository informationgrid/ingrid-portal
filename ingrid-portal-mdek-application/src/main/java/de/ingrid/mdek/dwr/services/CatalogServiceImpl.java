package de.ingrid.mdek.dwr.services;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.ingrid.mdek.beans.CatalogBean;
import de.ingrid.mdek.handler.CatalogRequestHandler;

public class CatalogServiceImpl implements CatalogService {

	private final static Logger log = Logger.getLogger(CatalogServiceImpl.class);	

	// Injected by Spring
	private CatalogRequestHandler catalogRequestHandler;

	
	public Map<Integer, List<String[]>> getSysLists(Integer[] listIds, String languageCode) {
		return catalogRequestHandler.getSysLists(listIds, languageCode);
	}

	public CatalogBean getCatalogData() {
		return catalogRequestHandler.getCatalogData();	
	}

	public CatalogRequestHandler getCatalogRequestHandler() {
		return catalogRequestHandler;
	}

	public void setCatalogRequestHandler(CatalogRequestHandler catalogRequestHandler) {
		this.catalogRequestHandler = catalogRequestHandler;
	}

}
