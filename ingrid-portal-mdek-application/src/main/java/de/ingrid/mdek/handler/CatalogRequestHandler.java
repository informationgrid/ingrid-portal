package de.ingrid.mdek.handler;

import java.util.List;
import java.util.Map;

import de.ingrid.mdek.beans.CatalogBean;

public interface CatalogRequestHandler {

	public Map<Integer, List<String[]>> getSysLists(Integer[] listIds, String languageCode);
	public List<Map<String, String>> getSysGuis(String[] guiIds);
	public List<Map<String, String>> storeSysGuis(List<Map<String, String>> sysGuis, boolean refetchAfterStore);
	public CatalogBean getCatalogData();
	public CatalogBean storeCatalogData(CatalogBean cat);
}
