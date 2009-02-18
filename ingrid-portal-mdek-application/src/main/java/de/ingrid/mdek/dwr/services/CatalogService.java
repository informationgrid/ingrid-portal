package de.ingrid.mdek.dwr.services;

import java.util.List;
import java.util.Map;

import de.ingrid.mdek.beans.AdditionalFieldBean;
import de.ingrid.mdek.beans.CatalogBean;

public interface CatalogService {

	public Map<Integer, List<String[]>> getSysLists(Integer[] listIds, String languageCode);
	public List<Map<String, String>> getSysGuis(String[] guiIds);
	public List<AdditionalFieldBean> getSysAdditionalFields(Long[] fieldIds, String language);
	public void storeSysAdditionalFields(List<AdditionalFieldBean> additionalFields, String languageCode);
	public List<Map<String, String>> storeSysGuis(List<Map<String, String>> sysGuis, boolean refetchAfterStore);
	public CatalogBean getCatalogData();
	public CatalogBean storeCatalogData(CatalogBean cat);
}