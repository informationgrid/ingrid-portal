package de.ingrid.mdek.dwr.services;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.directwebremoting.io.FileTransfer;

import de.ingrid.mdek.MdekUtils.MdekSysList;
import de.ingrid.mdek.beans.AdditionalFieldBean;
import de.ingrid.mdek.beans.CatalogBean;
import de.ingrid.mdek.beans.GenericValueBean;
import de.ingrid.mdek.handler.CatalogRequestHandler;
import de.ingrid.mdek.job.MdekException;
import de.ingrid.mdek.util.MdekErrorUtils;

public class CatalogServiceImpl implements CatalogService {

	private final static Logger log = Logger.getLogger(CatalogServiceImpl.class);	

	// Injected by Spring
	private CatalogRequestHandler catalogRequestHandler;

	
	public Integer[] getAllSysListIds() {
		return catalogRequestHandler.getAllSysListIds();
	}

	public Map<Integer, List<String[]>> getSysLists(Integer[] listIds, String languageCode) {
		return catalogRequestHandler.getSysLists(listIds, languageCode);
	}

	public String[] getFreeListEntries(MdekSysList sysList) {
		return catalogRequestHandler.getFreeListEntries(sysList);
	}

	public void storeSysList(Integer listId, boolean maintainable, Integer defaultEntryIndex, Integer[] entryIds,
			String[] entriesGerman, String[] entriesEnglish) {
		catalogRequestHandler.storeSysList(listId, maintainable, defaultEntryIndex, entryIds, entriesGerman, entriesEnglish);
	}

	public void replaceFreeEntryWithSysListEntry(String freeEntry, MdekSysList sysList, Integer sysListEntryId, String sysListEntryName) {
		catalogRequestHandler.replaceFreeEntryWithSysListEntry(freeEntry, sysList, sysListEntryId, sysListEntryName);
	}

	public FileTransfer exportSysLists(Integer[] listIds) throws UnsupportedEncodingException {
		String xmlDoc = catalogRequestHandler.exportSysLists(listIds);
		return new FileTransfer("sysList.xml", "text/xml", xmlDoc.getBytes("UTF-8"));
	}

	public void importSysLists(byte[] data) throws UnsupportedEncodingException {
		catalogRequestHandler.importSysLists(new String(data, "UTF-8"));
	}

	
	public List<Map<String, String>> getSysGuis(String[] guiIds) {
		return catalogRequestHandler.getSysGuis(guiIds);
	}

	public List<AdditionalFieldBean> getSysAdditionalFields(Long[] fieldIds, String languageCode) {
		return catalogRequestHandler.getSysAdditionalFields(fieldIds, languageCode);
	}

	public List<AdditionalFieldBean> storeAllSysAdditionalFields(List<AdditionalFieldBean> additionalFields) {
		return catalogRequestHandler.storeAllSysAdditionalFields(additionalFields);
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

	public List<GenericValueBean> getSysGenericValues(String[] keyNames) {
		try {
			return catalogRequestHandler.getSysGenericValues(keyNames);

		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while fetching sysGenericKeys data.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		}
	}

	public void storeSysGenericValues(List<GenericValueBean> genericValues) {
		try {
			catalogRequestHandler.storeSysGenericValues(genericValues);

		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while storing sysGenericKeys data.", e);
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
