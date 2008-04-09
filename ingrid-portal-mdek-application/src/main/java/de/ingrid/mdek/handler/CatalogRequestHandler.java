package de.ingrid.mdek.handler;

import java.util.List;
import java.util.Map;

import de.ingrid.mdek.beans.CatalogBean;

public interface CatalogRequestHandler {

	public Map<Integer, List<String[]>> getSysLists(Integer[] listIds, String languageCode);
	public CatalogBean getCatalogData();
}
