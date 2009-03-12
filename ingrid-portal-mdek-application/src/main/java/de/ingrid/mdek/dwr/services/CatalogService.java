package de.ingrid.mdek.dwr.services;

import java.util.List;
import java.util.Map;

import de.ingrid.mdek.MdekUtils.MdekSysList;
import de.ingrid.mdek.beans.AdditionalFieldBean;
import de.ingrid.mdek.beans.CatalogBean;
import de.ingrid.mdek.beans.GenericValueBean;

public interface CatalogService {

	public Integer[] getAllSysListIds();
	public Map<Integer, List<String[]>> getSysLists(Integer[] listIds, String languageCode);
	public void storeSysList(Integer listId, boolean maintainable, Integer defaultEntryIndex, Integer[] entryIds,
			String[] entriesGerman, String[] entriesEnglish);
	public String[] getFreeListEntries(MdekSysList sysList);
	public void replaceFreeEntryWithSysListEntry(String freeEntry, MdekSysList sysList, Integer sysListEntryId, String sysListEntryName);

	public List<AdditionalFieldBean> getSysAdditionalFields(Long[] fieldIds, String language);
	public List<AdditionalFieldBean> storeAllSysAdditionalFields(List<AdditionalFieldBean> additionalFields);

	public List<Map<String, String>> getSysGuis(String[] guiIds);
	public List<Map<String, String>> storeSysGuis(List<Map<String, String>> sysGuis, boolean refetchAfterStore);

	public List<GenericValueBean> getSysGenericValues(String[] keyNames);
	public void storeSysGenericValues(List<GenericValueBean> genericValues);
	
	public CatalogBean getCatalogData();
	public CatalogBean storeCatalogData(CatalogBean cat);
}