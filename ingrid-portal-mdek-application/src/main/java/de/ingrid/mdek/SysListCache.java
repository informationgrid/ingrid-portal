package de.ingrid.mdek;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import de.ingrid.mdek.caller.IMdekCallerCatalog;
import de.ingrid.mdek.dwr.util.HTTPSessionHelper;
import de.ingrid.mdek.handler.CatalogRequestHandler;
import de.ingrid.mdek.handler.ConnectionFacade;
import de.ingrid.mdek.util.MdekCatalogUtils;
import de.ingrid.utils.IngridDocument;

public class SysListCache {

	private final static Logger log = Logger.getLogger(SysListCache.class);
	
	// Injected by Spring
	private ConnectionFacade connectionFacade;

	// Initialized by spring through the init method
	private IMdekCallerCatalog mdekCallerCatalog;

	
	private Map<Integer, List<String[]>> listCache;
	private Map<String, Integer> keyCache;

	private final String SYSLIST_MAPPING_PREFIX = "sysList.map.";

	private String languageCode;

	public void init() {
		mdekCallerCatalog = connectionFacade.getMdekCallerCatalog();
	}
		
	public void loadInitialLists() {
		ResourceBundle resourceBundle = ResourceBundle.getBundle("sysList");
		ArrayList<Integer> initialListIds = new ArrayList<Integer>();
		keyCache = new HashMap<String, Integer>();
		
		languageCode = resourceBundle.getString("sysList.languageCode");

		Enumeration<String> keys = resourceBundle.getKeys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			if (key.startsWith(SYSLIST_MAPPING_PREFIX)) {
				Integer listId = Integer.valueOf(resourceBundle.getString(key));
				keyCache.put(key.substring(SYSLIST_MAPPING_PREFIX.length()), listId);
				initialListIds.add(listId);
			}
		}

		try {
			listCache = getSysLists(initialListIds.toArray(new Integer[]{}), languageCode);
		} catch (Exception e) {
			// Could not get the lists from the backend
			// Possibly the connection was not established yet
//			log.debug("Could not get sysLists.", e);
			listCache = null;
		}
	}

	public String getValue(String key, Integer entryId) {
		if (listCache == null) {
			this.loadInitialLists();
		}

		List<String[]> sysList = listCache.get(keyCache.get(key));

		for (String[] entry : sysList) {
			if (entry[1].equals(entryId.toString())) {
				return entry[0];
			}
		}
		
		log.debug("Could not find sysList value for: ["+key+", "+entryId+"]");
		return "";
	}

	public String getInitialValue(String key) {
		if (listCache == null) {
			this.loadInitialLists();
		}

		List<String[]> sysList = listCache.get(keyCache.get(key));

		// The third entry in the string marks the default entry.
		for (String[] entry : sysList) {
			if (entry.length < 3) { continue; }
			if (entry[2].equalsIgnoreCase("Y")) {
				return entry[0];
			}
		}

		log.debug("Could not find default syslist entry for: ["+key+"]");
		return null;
	}

	public String getValueFromListId(Integer listId, Integer entryId) {
		if (listCache == null) {
			this.loadInitialLists();
		}

		List<String[]> sysList = listCache.get(listId);
		if (sysList == null) {
			sysList = addSysListToCache(listId);
		}

		for (String[] entry : sysList) {
			if (entry[1].equals(entryId.toString())) {
				return entry[0];
			}
		}
		
		log.debug("Could not find sysList/entryId for: ["+listId+", "+entryId+"]");
		return "";
	}

	public String getInitialValueFromListId(Integer listId) {
		if (listCache == null) {
			this.loadInitialLists();
		}

		List<String[]> sysList = listCache.get(listId);
		if (sysList == null) {
			sysList = addSysListToCache(listId);
		}

		// The third entry in the string marks the default entry.
		for (String[] entry : sysList) {
			if (entry.length < 3) { continue; }
			if (entry[2].equalsIgnoreCase("Y")) {
				return entry[0];
			}
		}

		log.debug("Could not find default syslist entry for list: ["+listId+"]");
		return null;
	}

	public Integer getKey(String key, String entryVal) {
		if (listCache == null) {
			this.loadInitialLists();
		}
		if (entryVal == null) {
			return null;
		}

		List<String[]> sysList = listCache.get(keyCache.get(key));
		if (sysList == null) {
			log.debug("Could not find sysList: ["+key+", "+entryVal+"]");
			return null;
		}
		
		for (String[] entry : sysList) {
			if (entry[0].trim().equalsIgnoreCase(entryVal.trim())) {
				return Integer.valueOf(entry[1]);
			}
		}

		log.debug("Could not find sysList key for: ["+key+", "+entryVal+"]");
		return null;
	}

	
	public Integer getKeyFromListId(Integer listId, String entryVal) {
		if (listCache == null) {
			this.loadInitialLists();
		}

		List<String[]> sysList = listCache.get(listId);
		if (sysList == null) {
			sysList = addSysListToCache(listId);
		}

		for (String[] entry : sysList) {
			if (entry[0].trim().equalsIgnoreCase(entryVal.trim())) {
				return Integer.valueOf(entry[1]);
			}
		}
		
		log.debug("Could not find sysList/entryId for: ["+listId+", "+entryVal+"]");
		return null;
	}

	public Integer getInitialKeyFromListId(Integer listId) {
		if (listCache == null) {
			this.loadInitialLists();
		}

		List<String[]> sysList = listCache.get(listId);
		if (sysList == null) {
			sysList = addSysListToCache(listId);
		}

		// The third entry in the string marks the default entry.
		for (String[] entry : sysList) {
			if (entry.length < 3) { continue; }
			if (entry[2].equalsIgnoreCase("Y")) {
				return Integer.valueOf(entry[1]);
			}
		}

		log.debug("Could not find default syslist entry for list: ["+listId+"]");
		return null;
	}


	public List<String[]> addSysListToCache(Integer listId) {
		Integer[] listIds = {listId};
		listCache.put(listId, getSysLists(listIds, languageCode).get(listId));
		return listCache.get(listId);
	}


	private Map<Integer, List<String[]>> getSysLists(Integer[] listIds, String languageCode) {
		IngridDocument response = mdekCallerCatalog.getSysLists(connectionFacade.getCurrentPlugId(), listIds, languageCode, HTTPSessionHelper.getCurrentSessionId());
		return MdekCatalogUtils.extractSysListFromResponse(response);
	}

	public ConnectionFacade getConnectionFacade() {
		return connectionFacade;
	}

	public void setConnectionFacade(ConnectionFacade connectionFacade) {
		this.connectionFacade = connectionFacade;
	}	
}
