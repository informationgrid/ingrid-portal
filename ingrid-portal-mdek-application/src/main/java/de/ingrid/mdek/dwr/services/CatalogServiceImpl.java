package de.ingrid.mdek.dwr.services;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.ingrid.mdek.beans.CatalogBean;
import de.ingrid.mdek.handler.CatalogRequestHandler;
import de.ingrid.mdek.job.MdekException;
import de.ingrid.mdek.util.MdekErrorUtils;

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

	public CatalogBean storeCatalogData(CatalogBean cat) {
		try {
			return catalogRequestHandler.storeCatalogData(cat);

		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while storing catalog data.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		}
	}

	public CatalogRequestHandler getCatalogRequestHandler() {
		return catalogRequestHandler;
	}

	public void setCatalogRequestHandler(CatalogRequestHandler catalogRequestHandler) {
		this.catalogRequestHandler = catalogRequestHandler;
	}

}
