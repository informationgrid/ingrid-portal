package de.ingrid.mdek;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

public class SysListMapper {

	private final static Logger log = Logger.getLogger(SysListMapper.class);
	
	private DataConnectionInterface dataConnection;
	private Map<Integer, List<String[]>> listCache;
	private Map<String, Integer> keyCache;
	
	private final String SYSLIST_MAPPING_PREFIX = "sysList.map.";

	private Integer languageCode;

	public void init() {
		ResourceBundle resourceBundle = ResourceBundle.getBundle("sysList");
		ArrayList<Integer> initialListIds = new ArrayList<Integer>();
		keyCache = new HashMap<String, Integer>();
		
		languageCode = Integer.valueOf(resourceBundle.getString("sysList.languageCode"));

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
			listCache = dataConnection.getSysLists(initialListIds.toArray(new Integer[]{}), languageCode);
		} catch (Exception e) {
			// Could not get the lists from the backend
			// Possibly the connection was not established yet
			log.debug("Could not get sysLists.", e);
			listCache = null;
		}
	}

	public String getValue(String key, Integer entryId) {
		if (listCache == null) {
			this.init();
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

	public String getValueFromListId(Integer listId, Integer entryId) {
		if (listCache == null) {
			this.init();
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

	
	public Integer getKey(String key, String entryVal) {
		if (listCache == null) {
			this.init();
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
			this.init();
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

	
	public List<String[]> addSysListToCache(Integer listId) {
		Integer[] listIds = {listId};
		listCache.put(listId, dataConnection.getSysLists(listIds, languageCode).get(listId));
		return listCache.get(listId);
	}
	
	public DataConnectionInterface getDataConnection() {
		return dataConnection;
	}

	public void setDataConnection(DataConnectionInterface dataConnection) {
		this.dataConnection = dataConnection;
	}
}
