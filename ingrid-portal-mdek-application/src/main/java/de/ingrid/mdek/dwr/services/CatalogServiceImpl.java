package de.ingrid.mdek.dwr.services;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.ingrid.mdek.beans.AdditionalFieldBean;
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

	public List<Map<String, String>> getSysGuis(String[] guiIds) {
		return catalogRequestHandler.getSysGuis(guiIds);
	}

	public List<AdditionalFieldBean> getSysAdditionalFields(Long[] fieldIds, String languageCode) {
		return catalogRequestHandler.getSysAdditionalFields(fieldIds, languageCode);
	}

	public void storeSysAdditionalFields(List<AdditionalFieldBean> additionalFields, String languageCode) {
		// TODO store additionalFields in db
		for (AdditionalFieldBean additionalFieldBean : additionalFields) {
			log.debug("language: "+additionalFieldBean.getListLanguage());
			log.debug("id: "+additionalFieldBean.getId());
			log.debug("name: "+additionalFieldBean.getName());
			log.debug("type: "+additionalFieldBean.getType());
			log.debug("size: "+ additionalFieldBean.getSize());
			log.debug("list entries: "+additionalFieldBean.getListEntries());
		}
	}

	public List<Map<String, String>> storeSysGuis(List<Map<String, String>> sysGuis, boolean refetchAfterStore) {
		try {
			return catalogRequestHandler.storeSysGuis(sysGuis, refetchAfterStore);

		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while storing sysGui data.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		}
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
