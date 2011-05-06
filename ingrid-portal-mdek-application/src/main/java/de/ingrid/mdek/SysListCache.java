package de.ingrid.mdek;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.log4j.Logger;

import de.ingrid.mdek.beans.CatalogBean;
import de.ingrid.mdek.caller.IMdekCallerCatalog;
import de.ingrid.mdek.handler.ConnectionFacade;
import de.ingrid.mdek.util.MdekCatalogUtils;
import de.ingrid.mdek.util.MdekSecurityUtils;
import de.ingrid.utils.IngridDocument;

public class SysListCache {

	private final static Logger log = Logger.getLogger(SysListCache.class);

	// Injected by Spring
	private ConnectionFacade connectionFacade;

	// Initialized by spring through the init method
	private IMdekCallerCatalog mdekCallerCatalog;

	// Keycache used to identify lists via keys defined in sysList.properties
	private Map<String, Integer> keyCache;
	// The key prefix used in sysList.properties
	private final String SYSLIST_MAPPING_PREFIX = "sysList.map.";
	// sysList language
	private String languageCode;

	// EHCache manager
	private CacheManager cacheManager;
	private Cache sysListCache;

	public void init() {
		mdekCallerCatalog = connectionFacade.getMdekCallerCatalog();

		// Initialize EHCache
		cacheManager = new CacheManager();
		Cache cache = new Cache("sysListCache", 10000, true, false, 120, 120);
		cacheManager.addCache(cache);
	}

	public void destroy() {
		cacheManager.shutdown();
	}
	
	// Load the initial lists from the backend for all lists defined in sysList.properties
	public void loadInitialLists() {
		ResourceBundle resourceBundle = ResourceBundle.getBundle("sysList");
		List<Integer> initialListIds = new ArrayList<Integer>();
		keyCache = new HashMap<String, Integer>();

		// Load the catalog data from the backend to extract the catalog language
		try {
			IngridDocument response = mdekCallerCatalog.fetchCatalog(connectionFacade.getCurrentPlugId(), MdekSecurityUtils.getCurrentUserUuid());
			CatalogBean b = MdekCatalogUtils.extractCatalogFromResponse(response);
			languageCode = b.getLanguageShort();

		} catch (Exception e) {
			// Could not get the lists from the backend
			// Possibly the connection was not established yet
//			log.debug("SysListCache: Could not get catalog language.", e);			

			// Fallback to the language defined in the properties file?
			// languageCode = resourceBundle.getString("sysList.languageCode");
			return;
		}

// Don't load the language from the properties file. It is fetched from the catalog instead
//		languageCode = resourceBundle.getString("sysList.languageCode");

		// For all keys...
		Enumeration<String> keys = resourceBundle.getKeys();
		while (keys.hasMoreElements()) {
			// Add the key-listId pair to the keyCache
			String key = keys.nextElement();
			if (key.startsWith(SYSLIST_MAPPING_PREFIX)) {
				Integer listId = Integer.valueOf(resourceBundle.getString(key));
				keyCache.put(key.substring(SYSLIST_MAPPING_PREFIX.length()), listId);
				initialListIds.add(listId);
			}
		}

		// get the sysLists for all initialIDs and store them in the list cache
		try {
			Map<Integer, List<String[]>> sysLists = getSysLists(initialListIds.toArray(new Integer[]{}), languageCode);
			sysListCache = cacheManager.getCache("sysListCache");
			
			if (sysListCache == null) {
				log.debug("Could not get ehcache - sysListCache. Please check if it's defined in ehcache.xml!");
				return;
			}

			if (sysLists != null) {
				Iterator<Map.Entry<Integer, List<String[]>>> it = sysLists.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry<Integer, List<String[]>> entry = it.next();
					Integer listId = entry.getKey();
					List<String[]> list = entry.getValue();

					String key = createCacheKey(listId);
					Element e = new Element(key, list);
					sysListCache.put(e);
				}
			}

		} catch (Exception e) {
			// Could not get the lists from the backend
			// Possibly the connection was not established yet
//			log.debug("Could not get sysLists.", e);
			sysListCache = null;			
			return;
		}
	}


	public String getValue(String key, Integer entryId) {
		if (sysListCache == null) {
			this.loadInitialLists();
		}

		List<String[]> sysList = getSysListForListId(keyCache.get(key));
		if (sysList == null) {
			sysList = addSysListToCache(keyCache.get(key));
		}

		for (String[] entry : sysList) {
			if (entry[1].equals(entryId.toString())) {
				return entry[0];
			}
		}
		
		log.debug("Could not find sysList value for: ["+key+", "+entryId+"]");
		return "";
	}

	public String getValueFromListId(Integer listId, Integer entryId) {
		if (sysListCache == null) {
			this.loadInitialLists();
		}

		List<String[]> sysList = getSysListForListId(listId);
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
		if (sysListCache == null) {
			this.loadInitialLists();
		}

		List<String[]> sysList = getSysListForListId(listId);
		if (sysList == null) {
			sysList = addSysListToCache(listId);
		}

		// The third entry in the string marks the default entry.
		for (String[] entry : sysList) {
			if (entry.length < 3) { continue; }
			if (entry[2].equalsIgnoreCase(MdekUtils.YES)) {
				return entry[0];
			}
		}

		log.debug("Could not find default syslist entry for list: ["+listId+"]");
		return null;
	}

	public Integer getKey(String key, String entryVal) {
		if (sysListCache == null) {
			this.loadInitialLists();
		}
		if (entryVal == null) {
			return null;
		}

		List<String[]> sysList = getSysListForListId(keyCache.get(key));
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
		if (sysListCache == null) {
			this.loadInitialLists();
		}

		List<String[]> sysList = getSysListForListId(listId);
		if (sysList == null) {
			sysList = addSysListToCache(listId);
		}
		
		if (entryVal != null) {
    		for (String[] entry : sysList) {
    			if (entry[0].trim().equalsIgnoreCase(entryVal.trim())) {
    				return Integer.valueOf(entry[1]);
    			}
    		}
		}
		
		log.debug("Could not find sysList/entryId for: ["+listId+", "+entryVal+"]");
		return null;
	}

	public Integer getInitialKeyFromListId(Integer listId) {
		if (sysListCache == null) {
			this.loadInitialLists();
		}

		List<String[]> sysList = getSysListForListId(listId);
		if (sysList == null) {
			sysList = addSysListToCache(listId);
		}

		// The third entry in the string marks the default entry.
		for (String[] entry : sysList) {
			if (entry.length < 3) { continue; }
			if (entry[2].equalsIgnoreCase(MdekUtils.YES)) {
				return Integer.valueOf(entry[1]);
			}
		}

		log.debug("Could not find default syslist entry for list: ["+listId+"]");
		return null;
	}


	public List<String[]> addSysListToCache(Integer listId) {
		Integer[] listIds = {listId};
		List<String[]> sysList = getSysLists(listIds, languageCode).get(listId);
		Element e = new Element(createCacheKey(listId), sysList);
		sysListCache.put(e);	
		return sysList;
	}

	private List<String[]> getSysListForListId(Integer listId) {
		Element e = sysListCache.get(createCacheKey(listId));

		if (e == null) {
			return addSysListToCache(listId);

		} else {
			List<String[]> sysList = (List<String[]>) e.getValue();
			if (sysList == null) {
				// Reload the list since it is no longer valid
				return addSysListToCache(listId);

			} else {
				return sysList;
			}
		}
	}

	private String createCacheKey(Integer listId) {
		return connectionFacade.getCurrentPlugId()+listId;
	}
	
	private Map<Integer, List<String[]>> getSysLists(Integer[] listIds, String languageCode) {
		IngridDocument response = mdekCallerCatalog.getSysLists(connectionFacade.getCurrentPlugId(), listIds, languageCode, MdekSecurityUtils.getCurrentUserUuid());
		return MdekCatalogUtils.extractSysListFromResponse(response);
	}

	public ConnectionFacade getConnectionFacade() {
		return connectionFacade;
	}

	public void setConnectionFacade(ConnectionFacade connectionFacade) {
		this.connectionFacade = connectionFacade;
	}	
}
