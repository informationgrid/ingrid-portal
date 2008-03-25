package de.ingrid.mdek;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.directory.SearchResult;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;

import de.ingrid.mdek.IMdekCallerAbstract.Quantity;
import de.ingrid.mdek.MdekError.MdekErrorType;
import de.ingrid.mdek.beans.AddressSearchResultBean;
import de.ingrid.mdek.beans.CSVSearchResultBean;
import de.ingrid.mdek.beans.CatalogBean;
import de.ingrid.mdek.beans.JobInfoBean;
import de.ingrid.mdek.beans.MdekAddressBean;
import de.ingrid.mdek.beans.MdekDataBean;
import de.ingrid.mdek.beans.ObjectSearchResultBean;
import de.ingrid.mdek.beans.SearchResultBean;
import de.ingrid.mdek.beans.VersionInformation;
import de.ingrid.mdek.exception.EntityReferencedException;
import de.ingrid.mdek.job.MdekException;
import de.ingrid.utils.IngridDocument;

public class MdekConnection implements DataConnectionInterface {

	private final static Logger log = Logger.getLogger(MdekConnection.class);

	private IMdekCaller mdekCaller;
	private IMdekCallerObject mdekCallerObject;
	private IMdekCallerAddress mdekCallerAddress;
	private IMdekCallerQuery mdekCallerQuery;
	private IMdekCallerCatalog mdekCallerCatalog;
	private DataMapperInterface dataMapper;

	public MdekConnection(File communicationProperties) {
		if (communicationProperties == null || !(communicationProperties instanceof File)) {
			throw new IllegalStateException(
					"Please specify the location of the communication.properties file via the Property 'mdekCaller.properties' in /src/resources/mdek.properties");
		}
		log.debug("Initializing MdekCaller...");
		MdekCaller.initialize(communicationProperties);
		log.debug("MdekCaller initialized.");
		mdekCaller = MdekCaller.getInstance();

		MdekCallerObject.initialize(mdekCaller);
		MdekCallerAddress.initialize(mdekCaller);
		MdekCallerQuery.initialize(mdekCaller);
		MdekCallerCatalog.initialize(mdekCaller);
		
		mdekCallerObject = MdekCallerObject.getInstance();
		mdekCallerAddress = MdekCallerAddress.getInstance();
		mdekCallerQuery = MdekCallerQuery.getInstance();
		mdekCallerCatalog = MdekCallerCatalog.getInstance();
	}

	// Shutdown Method is called by the Spring Framework on shutdown
	public void destroy() {
		log.debug("Shutting down MdekCaller...");
		MdekCaller.shutdown();
		log.debug("MdekCaller shut down.");
		mdekCaller = null;
		dataMapper = null;
	}

	public DataMapperInterface getDataMapper() {
		return dataMapper;
	}

	public void setDataMapper(DataMapperInterface dataMapper) {
		this.dataMapper = dataMapper;
	}

	public MdekDataBean getObjectDetail(String uuid) {
		IngridDocument response = mdekCallerObject.fetchObject(getCurrentIPlug(), uuid, Quantity.DETAIL_ENTITY, getCurrentSessionId());
		return extractSingleObjectFromResponse(response);
	}

	public MdekAddressBean getAddressDetail(String uuid) {
		IngridDocument response = mdekCallerAddress.fetchAddress(getCurrentIPlug(), uuid, Quantity.DETAIL_ENTITY, getCurrentSessionId());
		return extractSingleAddressFromResponse(response);	
	}
	
	
	public ArrayList<HashMap<String, Object>> getRootAddresses(boolean freeAddressesOnly) {
		IngridDocument response = mdekCallerAddress.fetchTopAddresses(getCurrentIPlug(), getCurrentSessionId(), freeAddressesOnly);
		return extractAddressesFromResponse(response);
	}

	public ArrayList<HashMap<String, Object>> getRootObjects() {
		IngridDocument response = mdekCallerObject.fetchTopObjects(getCurrentIPlug(), getCurrentSessionId());
		return extractObjectsFromResponse(response);
	}

	public ArrayList<HashMap<String, Object>> getSubObjects(String uuid, int depth) {
		IngridDocument response = mdekCallerObject.fetchSubObjects(getCurrentIPlug(), uuid, getCurrentSessionId());
		return extractObjectsFromResponse(response);
	}

	public ArrayList<HashMap<String, Object>> getSubAddresses(String uuid, int depth) {
		IngridDocument response = mdekCallerAddress.fetchSubAddresses(getCurrentIPlug(), uuid, getCurrentSessionId());
		return extractAddressesFromResponse(response);
	}

	public MdekDataBean getInitialObject(String parentUuid) {
		IngridDocument obj = new IngridDocument();
		obj.put(MdekKeys.PARENT_UUID, parentUuid);

		IngridDocument response = mdekCallerObject.getInitialObject(getCurrentIPlug(), obj, getCurrentSessionId());
		return extractSingleObjectFromResponse(response);
	}
	
	public MdekAddressBean getInitialAddress(String parentUuid) {
		IngridDocument adr = new IngridDocument();
		adr.put(MdekKeys.PARENT_UUID, parentUuid);

		IngridDocument response = mdekCallerAddress.getInitialAddress(getCurrentIPlug(), adr, getCurrentSessionId());
		return extractSingleAddressFromResponse(response);
	}

	
	public MdekDataBean saveObject(MdekDataBean data) {
		IngridDocument obj = (IngridDocument) dataMapper.convertFromObjectRepresentation(data);

		// Handle store of a new node. Should this be handled by the
		// EntryService?
		if (data.getUuid().equalsIgnoreCase("newNode")) {
			obj.remove(MdekKeys.UUID);
			obj.remove(MdekKeys.ID);
		}

		log.debug("Sending the following object for storage:");
		log.debug(obj);

		IngridDocument response = mdekCallerObject.storeObject(getCurrentIPlug(), obj, true, getCurrentSessionId());
		return extractSingleObjectFromResponse(response);
	}

	public MdekAddressBean saveAddress(MdekAddressBean data) {
		IngridDocument adr = (IngridDocument) dataMapper.convertFromAddressRepresentation(data);
//		log.debug("saveAddress() not implemented yet.");

		// Handle store of a new address. Should this be handled by the
		// EntryService?
		if (data.getUuid().equalsIgnoreCase("newNode")) {
			adr.remove(MdekKeys.UUID);
			adr.remove(MdekKeys.ID);
		}

		log.debug("Sending the following address for storage:");
		log.debug(adr);

		IngridDocument response = mdekCallerAddress.storeAddress(getCurrentIPlug(), adr, true, getCurrentSessionId());
		return extractSingleAddressFromResponse(response);
	}

	
	public MdekDataBean publishObject(MdekDataBean data, boolean forcePublicationCondition) {
	IngridDocument obj = (IngridDocument) dataMapper.convertFromObjectRepresentation(data);

	if (data.getUuid().equalsIgnoreCase("newNode")) {
		obj.remove(MdekKeys.UUID);
		obj.remove(MdekKeys.ID);
	}

	log.debug("Sending the following object for publishing:");
	log.debug(obj);

	IngridDocument response = mdekCallerObject.publishObject(getCurrentIPlug(), obj, true, forcePublicationCondition, getCurrentSessionId());
	return extractSingleObjectFromResponse(response);
}


	public MdekAddressBean publishAddress(MdekAddressBean data) {
		IngridDocument adr = (IngridDocument) dataMapper.convertFromAddressRepresentation(data);

		if (data.getUuid().equalsIgnoreCase("newNode")) {
			adr.remove(MdekKeys.UUID);
			adr.remove(MdekKeys.ID);
		}

		log.debug("Sending the following address for publishing:");
		log.debug(adr);

		IngridDocument response = mdekCallerAddress.publishAddress(getCurrentIPlug(), adr, true, getCurrentSessionId());
		return extractSingleAddressFromResponse(response);
	}

	
	public void deleteObject(String uuid, boolean forceDeleteReferences) {
		IngridDocument response = mdekCallerObject.deleteObject(getCurrentIPlug(), uuid, forceDeleteReferences, getCurrentSessionId());
		IngridDocument result = extractAdditionalInformationFromResponse(response);
	}

	public void deleteAddress(String uuid, boolean forceDeleteReferences) {
		IngridDocument response = mdekCallerAddress.deleteAddress(getCurrentIPlug(), uuid, forceDeleteReferences, getCurrentSessionId());
		IngridDocument result = extractAdditionalInformationFromResponse(response);
	}

	public boolean deleteObjectWorkingCopy(String uuid, boolean forceDeleteReferences) {
		IngridDocument response = mdekCallerObject.deleteObjectWorkingCopy(getCurrentIPlug(), uuid, forceDeleteReferences, getCurrentSessionId());

		IngridDocument result = extractAdditionalInformationFromResponse(response);
		return (Boolean) result.get(MdekKeys.RESULTINFO_WAS_FULLY_DELETED);
	}

	public boolean deleteAddressWorkingCopy(String uuid, boolean forceDeleteReferences) {
		IngridDocument response = mdekCallerAddress.deleteAddressWorkingCopy(getCurrentIPlug(), uuid, forceDeleteReferences, getCurrentSessionId());

		IngridDocument result = extractAdditionalInformationFromResponse(response);
		return (Boolean) result.get(MdekKeys.RESULTINFO_WAS_FULLY_DELETED);
	}

	public boolean canCutObject(String uuid) {
		IngridDocument response = mdekCallerObject.checkObjectSubTree(getCurrentIPlug(), uuid, getCurrentSessionId());
		if (mdekCaller.getResultFromResponse(response) == null) {
			handleError(response);
		} else {
			IngridDocument result = mdekCaller.getResultFromResponse(response);
			boolean hasWorkingCopy = result.getBoolean(MdekKeys.RESULTINFO_HAS_WORKING_COPY);
			if (hasWorkingCopy) {
				// Throw an error. A node that is about to be moved must not have working copies as children
				throw new MdekException(new MdekError(MdekErrorType.SUBTREE_HAS_WORKING_COPIES));
			}
		}
		return true;
	}

	public boolean canCutAddress(String uuid) {
		IngridDocument response = mdekCallerAddress.checkAddressSubTree(getCurrentIPlug(), uuid, getCurrentSessionId());
		if (mdekCaller.getResultFromResponse(response) == null) {
			handleError(response);
		} else {
			IngridDocument result = mdekCaller.getResultFromResponse(response);
			boolean hasWorkingCopy = result.getBoolean(MdekKeys.RESULTINFO_HAS_WORKING_COPY);
			if (hasWorkingCopy) {
				// Throw an error. An address that is about to be moved must not have working copies as children
				throw new MdekException(new MdekError(MdekErrorType.SUBTREE_HAS_WORKING_COPIES));
			}
		}
		return true;
	}

	public boolean canCopyObject(String uuid) {
		// Copy is always allowed. Placeholder for future changes
		return true;
	}

	public boolean canCopyAddress(String uuid) {
		// Copy is always allowed. Placeholder for future changes
		return true;
	}

	
	public List<String> getPathToObject(String uuid) {
		IngridDocument response = mdekCallerObject.getObjectPath(getCurrentIPlug(), uuid, getCurrentSessionId());
		return extractPathFromResponse(response);
	}

	public List<String> getPathToAddress(String uuid) {
		IngridDocument response = mdekCallerAddress.getAddressPath(getCurrentIPlug(), uuid, getCurrentSessionId());
		return extractPathFromResponse(response);
	}

	public Map<String, Object> copyObject(String fromUuid, String toUuid, boolean copySubTree) {
		IngridDocument response = mdekCallerObject.copyObject(getCurrentIPlug(), fromUuid, toUuid, copySubTree, getCurrentSessionId());
		return extractSingleSimpleObjectFromResponse(response);
	}

	public Map<String, Object> copyAddress(String fromUuid, String toUuid, boolean copySubTree, boolean copyToFreeAddress) {
		IngridDocument response = mdekCallerAddress.copyAddress(getCurrentIPlug(), fromUuid, toUuid, copySubTree, copyToFreeAddress, getCurrentSessionId());
		return extractSingleSimpleAddressFromResponse(response);
	}

	public void moveObjectSubTree(String fromUuid, String toUuid, boolean forcePublicationCondition) {
		IngridDocument response = mdekCallerObject.moveObject(getCurrentIPlug(), fromUuid, toUuid, true, forcePublicationCondition, getCurrentSessionId());
		if (mdekCaller.getResultFromResponse(response) == null) {
			handleError(response);
		}
	}

	public void moveAddressSubTree(String fromUuid, String toUuid, boolean moveToFreeAddress) {
//		log.debug("moveAddressSubTree(String, String) not implemented yet.");
		IngridDocument response = mdekCallerAddress.moveAddress(getCurrentIPlug(), fromUuid, toUuid, true, moveToFreeAddress, getCurrentSessionId());
		if (mdekCaller.getResultFromResponse(response) == null) {
			handleError(response);
		}
	}
	
	public AddressSearchResultBean searchAddresses(MdekAddressBean adr, int startHit, int numHits) {
		IngridDocument adrDoc = (IngridDocument) dataMapper.convertFromAddressRepresentation(adr);

		log.debug("Sending the following address search:");
		log.debug(adrDoc);

		IngridDocument response = mdekCallerAddress.searchAddresses(getCurrentIPlug(), adrDoc, startHit, numHits, getCurrentSessionId());
		
		// TODO Convert the response
		return extractAddressSearchResultsFromResponse(response);
	}

	public AddressSearchResultBean queryExtAddresses(Map<String, String> params, int startHit, int numHits) {
		IngridDocument query = new IngridDocument();
		query.putAll(params);

		//TODO Implement
//		IngridDocument response = mdekCallerQuery.queryExtAddresses(getCurrentIPlug(), query, startHit, numHits, getCurrentSessionId());
//		return extractAddressSearchResultsFromResponse(response);
		return new AddressSearchResultBean();
	}


	public ObjectSearchResultBean queryExtObjects(Map<String, String> params, int startHit, int numHits) {
		IngridDocument query = new IngridDocument();
		query.putAll(params);

		//TODO Implement
//		IngridDocument response = mdekCallerQuery.queryExtAddresses(getCurrentIPlug(), query, startHit, numHits, getCurrentSessionId());
//		return extractAddressSearchResultsFromResponse(response);
		return new ObjectSearchResultBean();
	}

	
	public AddressSearchResultBean queryAddressesThesaurusTerm(String topicId, int startHit, int numHits) {
		log.debug("Searching for addresses with topicId: "+topicId);

		IngridDocument response = mdekCallerQuery.queryAddressesThesaurusTerm(getCurrentIPlug(), topicId, startHit, numHits, getCurrentSessionId());
		
		return extractAddressSearchResultsFromResponse(response);
	}
	
	
	public ObjectSearchResultBean queryObjectsThesaurusTerm(String topicId, int startHit, int numHits) {
		log.debug("Searching for objects with topicId: "+topicId);

		IngridDocument response = mdekCallerQuery.queryObjectsThesaurusTerm(getCurrentIPlug(), topicId, startHit, numHits, getCurrentSessionId());
		
		return extractObjectSearchResultsFromResponse(response);
	}

	public SearchResultBean queryHQL(String hqlQuery, int startHit, int numHits) {
		log.debug("Searching via HQL query: "+hqlQuery);

		IngridDocument response = mdekCallerQuery.queryHQL(getCurrentIPlug(), hqlQuery, startHit, numHits, getCurrentSessionId());

		return extractSearchResultsFromResponse(response);
	}

	public SearchResultBean queryHQLToCSV(String hqlQuery) {
		log.debug("Searching via HQL to csv query: "+hqlQuery);

		IngridDocument response = mdekCallerQuery.queryHQLToCsv(getCurrentIPlug(), hqlQuery, getCurrentSessionId());

		return extractSearchResultsFromResponse(response);
	}

	
	public List<VersionInformation> getVersion() {
		IngridDocument response = mdekCaller.getVersion(getCurrentIPlug());
		return extractVersionInformationFromResponse(response);
	}
	
	public Map<Integer, List<String[]>> getSysLists(Integer[] listIds, Integer languageCode) {
		IngridDocument response = mdekCallerCatalog.getSysLists(getCurrentIPlug(), listIds, languageCode, getCurrentSessionId());
		return extractSysListFromResponse(response);
	}

	public CatalogBean getCatalogData() {
		IngridDocument response = mdekCallerCatalog.fetchCatalog(getCurrentIPlug(), getCurrentSessionId());
		return extractCatalogFromResponse(response);
	}

	public JobInfoBean getRunningJobInfo() {
		IngridDocument response = mdekCaller.getRunningJobInfo(getCurrentIPlug(), getCurrentSessionId());
		return extractJobInfoFromResponse(response);
	}

	public JobInfoBean cancelRunningJob() {
		IngridDocument response = mdekCaller.cancelRunningJob(getCurrentIPlug(), getCurrentSessionId());
		return extractJobInfoFromResponse(response);	
	}

	
	// ------------------------ Helper Methods ---------------------------------------

	private ArrayList<HashMap<String, Object>> extractObjectsFromResponse(IngridDocument response) {
		IngridDocument result = mdekCaller.getResultFromResponse(response);

		ArrayList<HashMap<String, Object>> nodeList = null;

		if (result != null) {
			nodeList = new ArrayList<HashMap<String, Object>>();
			List<IngridDocument> objs = (List<IngridDocument>) result.get(MdekKeys.OBJ_ENTITIES);
			for (IngridDocument objEntity : objs) {
				nodeList.add(dataMapper.getSimpleObjectRepresentation(objEntity));
			}
		} else {
			handleError(response);
		}
		return nodeList;
	}

	private ArrayList<HashMap<String, Object>> extractAddressesFromResponse(IngridDocument response) {
		IngridDocument result = mdekCaller.getResultFromResponse(response);

		ArrayList<HashMap<String, Object>> nodeList = null;

		if (result != null) {
			nodeList = new ArrayList<HashMap<String, Object>>();
			List<IngridDocument> adrs = (List<IngridDocument>) result.get(MdekKeys.ADR_ENTITIES);
			for (IngridDocument adrEntity : adrs) {
				nodeList.add(dataMapper.getSimpleAddressRepresentation(adrEntity));
			}
		} else {
			handleError(response);
		}
		return nodeList;
	}

	private HashMap<String, Object> extractSingleSimpleObjectFromResponse(IngridDocument response) {
		IngridDocument result = mdekCaller.getResultFromResponse(response);

		if (result != null) {
			return dataMapper.getSimpleObjectRepresentation(result);
		} else {
			handleError(response);
			return null;
		}
	}

	private HashMap<String, Object> extractSingleSimpleAddressFromResponse(IngridDocument response) {
		IngridDocument result = mdekCaller.getResultFromResponse(response);

		if (result != null) {
			return dataMapper.getSimpleAddressRepresentation(result);
		} else {
			handleError(response);
			return null;
		}
	}

	
	private MdekDataBean extractSingleObjectFromResponse(IngridDocument response) {
		IngridDocument result = mdekCaller.getResultFromResponse(response);

		if (result != null) {
			return dataMapper.getDetailedObjectRepresentation(result);
		} else {
			handleError(response);
			return null;
		}
	}

	private MdekAddressBean extractSingleAddressFromResponse(IngridDocument response) {
		IngridDocument result = mdekCaller.getResultFromResponse(response);

		if (result != null) {
			return dataMapper.getDetailedAddressRepresentation(result);
		} else {
			handleError(response);
			return null;
		}
	}

	private IngridDocument extractAdditionalInformationFromResponse(IngridDocument response) {
		IngridDocument result = mdekCaller.getResultFromResponse(response);
		if (result != null) {
			return result;
		} else {
			handleError(response);
			return null;
		}

	}

	private List<String> extractPathFromResponse(IngridDocument response) {
		IngridDocument result = mdekCaller.getResultFromResponse(response);
		if (result != null) {
			List<String> uuidList = (List<String>) result.get(MdekKeys.PATH);
			return uuidList;
		} else {
			handleError(response);
			return null;
		}
	}

	private IngridDocument wrapObject(IngridDocument obj) {
		ArrayList<IngridDocument> list = new ArrayList();
		IngridDocument doc = new IngridDocument();

		list.add(obj);
		doc.put(MdekKeys.OBJ_ENTITIES, list);
		return doc;
	}

	private CSVSearchResultBean extractCSVSearchResultsFromResponse(IngridDocument response) {
		IngridDocument result = mdekCaller.getResultFromResponse(response);

		CSVSearchResultBean searchResult = new CSVSearchResultBean();

		if (result != null) {
//			searchResult.setNumHits(((Long) result.get(MdekKeys.SEARCH_TOTAL_NUM_HITS)));
			searchResult.setTotalNumHits((Long) result.get(MdekKeys.SEARCH_TOTAL_NUM_HITS));
			searchResult.setData((String) result.get(MdekKeys.CSV_RESULT));
		} else {
			handleError(response);
		}

		return searchResult;
	}

	private AddressSearchResultBean extractAddressSearchResultsFromResponse(IngridDocument response) {
		IngridDocument result = mdekCaller.getResultFromResponse(response);

		AddressSearchResultBean searchResult = new AddressSearchResultBean();

		if (result != null) {
			List<IngridDocument> adrs = (List<IngridDocument>) result.get(MdekKeys.ADR_ENTITIES);
			ArrayList<MdekAddressBean> nodeList = new ArrayList<MdekAddressBean>();

			if (adrs == null) {
				return null;
			}

			for (IngridDocument adrEntity : adrs) {
				nodeList.add(dataMapper.getDetailedAddressRepresentation(adrEntity));
			}

			searchResult.setNumHits((Integer) result.get(MdekKeys.SEARCH_NUM_HITS));
			searchResult.setTotalNumHits((Long) result.get(MdekKeys.SEARCH_TOTAL_NUM_HITS));
			searchResult.setResultList(nodeList);
		} else {
			handleError(response);
		}
		return searchResult;
	}
	
	private ObjectSearchResultBean extractObjectSearchResultsFromResponse(IngridDocument response) {
		IngridDocument result = mdekCaller.getResultFromResponse(response);

		ObjectSearchResultBean searchResult = new ObjectSearchResultBean();

		if (result != null) {
			List<IngridDocument> objs = (List<IngridDocument>) result.get(MdekKeys.OBJ_ENTITIES);
			ArrayList<MdekDataBean> nodeList = new ArrayList<MdekDataBean>();
			if (objs == null) {
				return null;
			}

			for (IngridDocument objEntity : objs) {
				nodeList.add(dataMapper.getDetailedObjectRepresentation(objEntity));
			}

			searchResult.setNumHits((Integer) result.get(MdekKeys.SEARCH_NUM_HITS));
			searchResult.setTotalNumHits((Long) result.get(MdekKeys.SEARCH_TOTAL_NUM_HITS));
			searchResult.setResultList(nodeList);
		} else {
			handleError(response);
		}
		return searchResult;
	}

	private SearchResultBean extractSearchResultsFromResponse(IngridDocument response) {
		ObjectSearchResultBean objResult = extractObjectSearchResultsFromResponse(response);
		AddressSearchResultBean adrResult = extractAddressSearchResultsFromResponse(response);
		CSVSearchResultBean csvResult = extractCSVSearchResultsFromResponse(response);

		SearchResultBean searchResult = new SearchResultBean();
		searchResult.setObjectSearchResult(objResult);
		searchResult.setAddressSearchResult(adrResult);
		searchResult.setCsvSearchResult(csvResult);
		
		return searchResult;
	}
	
	private ArrayList<MdekDataBean> extractDetailedObjects(IngridDocument doc) {
		ArrayList<MdekDataBean> results = new ArrayList<MdekDataBean>();

		if (doc != null) {
			List<IngridDocument> objs = (List<IngridDocument>) doc.get(MdekKeys.OBJ_ENTITIES);
			if (objs != null) {
				for (IngridDocument objEntity : objs) {
					results.add(dataMapper.getDetailedObjectRepresentation(objEntity));
				}
			}
		}
		return results;
	}

	private ArrayList<MdekAddressBean> extractDetailedAddresses(IngridDocument doc) {
		ArrayList<MdekAddressBean> results = new ArrayList<MdekAddressBean>();

		if (doc != null) {
			List<IngridDocument> adrs = (List<IngridDocument>) doc.get(MdekKeys.ADR_ENTITIES);
			if (adrs != null) {
				for (IngridDocument adrEntity : adrs) {
					results.add(dataMapper.getDetailedAddressRepresentation(adrEntity));
				}
			}
		}
		return results;
	}

	
	private List<VersionInformation> extractVersionInformationFromResponse(IngridDocument response) {
		IngridDocument result = mdekCaller.getResultFromResponse(response);

		ArrayList<VersionInformation> verList = new ArrayList<VersionInformation>();

		if (result != null) {
			// API Version
			VersionInformation v = new VersionInformation();
			v.setName(result.getString(MdekKeys.API_BUILD_NAME));
			v.setVersion(result.getString(MdekKeys.API_BUILD_VERSION));
			v.setBuildNumber(result.getString(MdekKeys.API_BUILD_NUMBER));
			try {
				v.setTimeStamp(new Date(Long.valueOf(result.getString(MdekKeys.API_BUILD_TIMESTAMP))));
			} catch (NumberFormatException e) {
				v.setTimeStamp(new Date());
			}
			verList.add(v);
	
			// Server Version
			v = new VersionInformation();
			v.setName(result.getString(MdekKeys.SERVER_BUILD_NAME));
			v.setVersion(result.getString(MdekKeys.SERVER_BUILD_VERSION));
			v.setBuildNumber(result.getString(MdekKeys.SERVER_BUILD_NUMBER));
			try {
				v.setTimeStamp(new Date(Long.valueOf(result.getString(MdekKeys.SERVER_BUILD_TIMESTAMP))));
			} catch (NumberFormatException e) {
				v.setTimeStamp(new Date());
			}
			verList.add(v);

		} else {
			handleError(response);
			return null;
		}
		return verList;		
	}
	
	private Map<Integer, List<String[]>> extractSysListFromResponse(IngridDocument response) {
		IngridDocument result = mdekCaller.getResultFromResponse(response);
		if (result != null) {
			Map<Integer, List<String[]>> resultMap = new HashMap<Integer, List<String[]>>();
			Set<String> listKeys = (Set<String>) result.keySet();
			for (String listKey : listKeys) {
				IngridDocument listDocument = (IngridDocument) result.get(listKey);
				ArrayList<String[]> resultList = new ArrayList<String[]>();
				Integer listId = (Integer) listDocument.get(MdekKeys.LST_ID);

				List<IngridDocument> entries = (List<IngridDocument>) listDocument.get(MdekKeys.LST_ENTRY_LIST);
//				resultList.add( new String[] {"", ""} );
				for (IngridDocument entry : entries) {
//					resultList.add( new String[] {StringEscapeUtils.escapeJavaScript(entry.getString(MdekKeys.ENTRY_NAME)), ((Integer) entry.get(MdekKeys.ENTRY_ID)).toString()} );
					resultList.add( new String[] {entry.getString(MdekKeys.ENTRY_NAME), ((Integer) entry.get(MdekKeys.ENTRY_ID)).toString()} );
				}
				resultMap.put(listId, resultList);
			}
			return resultMap;

		} else {
			handleError(response);
			return null;
		}
	}

	private CatalogBean extractCatalogFromResponse(IngridDocument response) {
		IngridDocument result = mdekCaller.getResultFromResponse(response);
		
		if (result != null) {
			CatalogBean resultCat = new CatalogBean();
	
			resultCat.setUuid(result.getString(MdekKeys.UUID));
			resultCat.setCatalogName(result.getString(MdekKeys.CATALOG_NAME));
			resultCat.setCountry(result.getString(MdekKeys.COUNTRY));
			resultCat.setWorkflowControl(result.getString(MdekKeys.WORKFLOW_CONTROL));
			resultCat.setExpiryDuration((Integer) result.get(MdekKeys.EXPIRY_DURATION));
			resultCat.setDateOfCreation(convertTimestampToDate((String) result.get(MdekKeys.DATE_OF_CREATION)));
			resultCat.setDateOfLastModification(convertTimestampToDate((String) result.get(MdekKeys.DATE_OF_LAST_MODIFICATION)));
			resultCat.setModUuid(result.getString(MdekKeys.MOD_UUID));
			return resultCat;
		} else {
			handleError(response);
			return null;
		}
	}

	private JobInfoBean extractJobInfoFromResponse(IngridDocument response) {
		IngridDocument result = mdekCaller.getResultFromResponse(response);

		if (result != null) {
			JobInfoBean job = new JobInfoBean();
			job.setDescription(result.getString(MdekKeys.RUNNINGJOB_DESCRIPTION));
			job.setNumEntities((Integer) result.get(MdekKeys.RUNNINGJOB_NUMBER_TOTAL_ENTITIES));
			job.setNumProcessedEntities((Integer) result.get(MdekKeys.RUNNINGJOB_NUMBER_PROCESSED_ENTITIES));
			return job;
		} else {
			handleError(response);
			return null;
		}		
	}
	
	private void handleError(IngridDocument response) throws RuntimeException {
		String errorMessage = mdekCaller.getErrorMsgFromResponse(response);
		log.debug(errorMessage);
		List<MdekError> err = mdekCaller.getErrorsFromResponse(response);
		if (err != null) {
			if (containsErrorType(err, MdekErrorType.ENTITY_REFERENCED_BY_OBJ)) {
				handleEntityReferencedByObjectError(err);
			} else {
				throw new MdekException(err);
			}
		} else {
			throw new RuntimeException(errorMessage);
		}
	}

	private void handleEntityReferencedByObjectError(List<MdekError> errorList) {
		MdekAddressBean targetAddress = null;
		MdekDataBean targetObject = null;
		ArrayList<MdekAddressBean> sourceAddresses = new ArrayList<MdekAddressBean>();
		ArrayList<MdekDataBean> sourceObjects = new ArrayList<MdekDataBean>();

		for (MdekError mdekError : errorList) {
			if (mdekError.getErrorType().equals(MdekErrorType.ENTITY_REFERENCED_BY_OBJ)) {
				// In the case of this exception, we have to build an MdekAppException containing additional data
				IngridDocument errorInfo = mdekError.getErrorInfo();
				ArrayList<MdekDataBean> objs = extractDetailedObjects(errorInfo);
				ArrayList<MdekAddressBean> adrs = extractDetailedAddresses(errorInfo);
				sourceObjects.addAll(objs);
				sourceAddresses.addAll(adrs);

				targetAddress = dataMapper.getDetailedAddressRepresentation(errorInfo);
			}
		}

		EntityReferencedException e = new EntityReferencedException(MdekErrorType.ENTITY_REFERENCED_BY_OBJ.toString());
		e.setTargetAddress(targetAddress);
		e.setTargetObject(targetObject);
		e.setSourceAddresses(sourceAddresses);
		e.setSourceObjects(sourceObjects);
		throw e;
	}
	
	
	private static boolean containsErrorType(List<MdekError> errorList, MdekErrorType errorType) {
		for (MdekError mdekError : errorList) {
			if (mdekError != null && mdekError.getErrorType().equals(errorType))
				return true;
		}
		return false;
	}
	
	
	private final static SimpleDateFormat timestampFormatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");

	private static Date convertTimestampToDate(String timeStamp) {
		if (timeStamp != null && timeStamp.length() != 0) {
			try {
				Date date = timestampFormatter.parse(timeStamp);
				return date;
			} catch (Exception ex){
				log.debug("Problems parsing timestamp from database: " + timeStamp, ex);
				return null;
			}
		} else {
			return null;
		}
	}

	// Helper method to get the current session Id. Will be replaced by the user id someday
	private String getCurrentSessionId() {
		try {
			WebContext wctx = WebContextFactory.get();
			HttpSession session = wctx.getSession();
			return session.getId();
		} catch (Exception e) {
			return "";
		}
	}

	// Helper method to get the current plug Id. Will be replaced by the users plug id someday
	private String getCurrentIPlug() {
		List<String> iPlugs = mdekCaller.getRegisteredIPlugs();
		if (iPlugs.size() > 0) {
			return iPlugs.get(0);
		} else {
			return null;
		}
	}
}