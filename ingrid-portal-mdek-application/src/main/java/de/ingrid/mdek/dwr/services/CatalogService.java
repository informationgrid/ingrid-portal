package de.ingrid.mdek.dwr.services;

import java.util.List;
import java.util.Map;

import de.ingrid.mdek.beans.CatalogBean;

public interface CatalogService {

	public Map<Integer, List<String[]>> getSysLists(Integer[] listIds, String languageCode);
	public CatalogBean getCatalogData();
	public CatalogBean storeCatalogData(CatalogBean cat);
}