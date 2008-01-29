package de.ingrid.mdek;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import de.ingrid.mdek.IMdekCaller.Quantity;
import de.ingrid.mdek.dwr.CatalogBean;
import de.ingrid.mdek.dwr.MdekDataBean;
import de.ingrid.utils.IngridDocument;

public class SimpleUDKConnection implements DataConnectionInterface {

	private final static Logger log = Logger.getLogger(SimpleUDKConnection.class);

	private IMdekCaller mdekCaller;

	private DataMapperInterface dataMapper;

	public SimpleUDKConnection(File communicationProperties) {
		if (communicationProperties == null || !(communicationProperties instanceof File)) {
			throw new IllegalStateException(
					"Please specify the location of the communication.properties file via the Property 'mdekCaller.properties' in /src/resources/mdek.properties");
		}
		MdekCaller.initialize(communicationProperties);
		mdekCaller = MdekCaller.getInstance();
	}

	// Shutdown Method is called by the Spring Framework on shutdown
	public void destroy() {
		MdekCaller.shutdown();
		mdekCaller = null;
		dataMapper = null;
	}

	public DataMapperInterface getDataMapper() {
		return dataMapper;
	}

	public void setDataMapper(DataMapperInterface dataMapper) {
		this.dataMapper = dataMapper;
	}

	public MdekDataBean getNodeDetail(String uuid) {
		IngridDocument response = mdekCaller.fetchObject(uuid, Quantity.DETAIL_ENTITY);
		return extractSingleObjectFromResponse(response);
	}

	public ArrayList<HashMap<String, Object>> getRootAddresses() {
		// IngridDocument response = mdekCaller.fetchTopAddresses();
		// return extractAddressesFromResponse(response);
		return null;
	}

	public ArrayList<HashMap<String, Object>> getRootObjects() {
		IngridDocument response = mdekCaller.fetchTopObjects();
		return extractObjectsFromResponse(response);
	}

	public ArrayList<HashMap<String, Object>> getSubObjects(String uuid, int depth) {
		IngridDocument response = mdekCaller.fetchSubObjects(uuid);
		return extractObjectsFromResponse(response);
	}

	public ArrayList<HashMap<String, Object>> getSubAddresses(String uuid, int depth) {
		// IngridDocument response = mdekCaller.fetchSubAddresses(uuid);
		// return extractObjectsFromResponse(response);
		return null;
	}

	public MdekDataBean getInitialObject(String parentUuid) {
		IngridDocument obj = new IngridDocument();
		obj.put(MdekKeys.PARENT_UUID, parentUuid);

		IngridDocument response = mdekCaller.getInitialObject(obj);
		return extractSingleObjectFromResponse(response);
	}
	
	public MdekDataBean saveNode(MdekDataBean data) {
		IngridDocument obj = (IngridDocument) dataMapper.convertFromMdekRepresentation(data);

		// Handle store of a new node. Should this be handled by the
		// EntryService?
		if (data.getUuid().equalsIgnoreCase("newNode")) {
			obj.remove(MdekKeys.UUID);
			obj.remove(MdekKeys.ID);
		}

		log.debug("Sending the following object for storage:");
		log.debug(obj);

		IngridDocument response = mdekCaller.storeObject(obj, true);
		return extractSingleObjectFromResponse(response);
	}

	public MdekDataBean publishNode(MdekDataBean data) {
	IngridDocument obj = (IngridDocument) dataMapper.convertFromMdekRepresentation(data);

	if (data.getUuid().equalsIgnoreCase("newNode")) {
		obj.remove(MdekKeys.UUID);
		obj.remove(MdekKeys.ID);
	}

	log.debug("Sending the following object for publishing:");
	log.debug(obj);

	IngridDocument response = mdekCaller.publishObject(obj, true);
	return extractSingleObjectFromResponse(response);
}

	
	public void deleteObject(String uuid) {
		mdekCaller.deleteObject(uuid);
	}

	public boolean deleteObjectWorkingCopy(String uuid) {
		IngridDocument response = mdekCaller.deleteObjectWorkingCopy(uuid);

		IngridDocument result = extractAdditionalInformationFromResponse(response);
		return (Boolean) result.get(MdekKeys.RESULTINFO_WAS_FULLY_DELETED);
	}

	public void canCutObject(String uuid) {
		mdekCaller.checkObjectSubTree(uuid);
	}

	public void canCopyObject(String uuid) {
		mdekCaller.checkObjectSubTree(uuid);
	}

	public List<String> getPathToObject(String uuid) {
		IngridDocument response = mdekCaller.getObjectPath(uuid);
		return extractPathFromResponse(response);
	}

	public Map<String, Object> copyObject(String fromUuid, String toUuid, boolean copySubTree) {
		IngridDocument response = mdekCaller.copyObject(fromUuid, toUuid, copySubTree);
		return extractSingleSimpleObjectFromResponse(response);
	}

	public boolean moveObjectSubTree(String fromUuid, String toUuid) {
		IngridDocument response = mdekCaller.moveObject(fromUuid, toUuid, true);
		return (mdekCaller.getResultFromResponse(response) != null);
	}

	public Map<Integer, List<String[]>> getSysLists(Integer[] listIds) {
		IngridDocument response = mdekCaller.getSysLists(listIds);
		return extractSysListFromResponse(response);
	}

	public CatalogBean getCatalogData() {
		IngridDocument response = mdekCaller.fetchCatalog();
		return extractCatalogFromResponse(response);
	}

	// ------------------------ Helper Methods
	// ---------------------------------------

	private ArrayList<HashMap<String, Object>> extractObjectsFromResponse(IngridDocument response) {
		IngridDocument result = mdekCaller.getResultFromResponse(response);

		ArrayList<HashMap<String, Object>> nodeList = null;

		if (result != null) {
			nodeList = new ArrayList<HashMap<String, Object>>();
			List<IngridDocument> objs = (List<IngridDocument>) result.get(MdekKeys.OBJ_ENTITIES);
			for (IngridDocument objEntity : objs) {
				nodeList.add(dataMapper.getSimpleMdekRepresentation(objEntity));
			}
		} else {
			log.error(mdekCaller.getErrorMsgFromResponse(response));
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
				nodeList.add(dataMapper.getSimpleMdekRepresentation(adrEntity));
			}
		} else {
			log.error(mdekCaller.getErrorMsgFromResponse(response));
		}
		return nodeList;
	}

	private HashMap<String, Object> extractSingleSimpleObjectFromResponse(IngridDocument response) {
		IngridDocument result = mdekCaller.getResultFromResponse(response);

		ArrayList<HashMap<String, Object>> nodeList = null;

		if (result != null) {
			return dataMapper.getSimpleMdekRepresentation(result);
		} else {
			log.error(mdekCaller.getErrorMsgFromResponse(response));
			return null;
		}
	}

	/*
	 * private MdekDataBean extractSingleObjectFromResponse(IngridDocument
	 * response) { IngridDocument result =
	 * mdekCaller.getResultFromResponse(response);
	 * 
	 * if (result != null) { List<IngridDocument> objs = (List<IngridDocument>)
	 * result.get(MdekKeys.OBJ_ENTITIES); if (objs == null) { log.error("Error
	 * in SimpleUDKConnection.extractSingleObjectFromResponse. No object
	 * entities returned."); } else if (objs.size() != 1) { log.error("Error in
	 * SimpleUDKConnection.extractSingleObjectFromResponse. Number of returned
	 * objects != 1."); } else { return
	 * dataMapper.getDetailedMdekRepresentation(objs.get(0)); } } else {
	 * log.error(mdekCaller.getErrorMsgFromResponse(response)); } return null; }
	 */
	private MdekDataBean extractSingleObjectFromResponse(IngridDocument response) {
		IngridDocument result = mdekCaller.getResultFromResponse(response);

		if (result != null) {
			return dataMapper.getDetailedMdekRepresentation(result);
		} else {
			log.error(mdekCaller.getErrorMsgFromResponse(response));
			return null;
		}
	}

	private IngridDocument extractAdditionalInformationFromResponse(IngridDocument response) {
		IngridDocument result = mdekCaller.getResultFromResponse(response);
		if (result != null) {
			return result;
		} else {
			log.error("ERROR: " + mdekCaller.getErrorMsgFromResponse(response));
			return null;
		}

	}

	private List<String> extractPathFromResponse(IngridDocument response) {
		IngridDocument result = mdekCaller.getResultFromResponse(response);
		if (result != null) {
			List<String> uuidList = (List<String>) result.get(MdekKeys.PATH);
			return uuidList;
		} else {
			log.error("ERROR: " + mdekCaller.getErrorMsgFromResponse(response));
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

	public Map<Integer, List<String[]>> extractSysListFromResponse(IngridDocument response) {
		IngridDocument result = mdekCaller.getResultFromResponse(response);
		if (result != null) {
			Map<Integer, List<String[]>> resultMap = new HashMap<Integer, List<String[]>>();
			Set<String> listKeys = (Set<String>) result.keySet();
			System.out.println("SUCCESS: " + listKeys.size() + " sys-lists");
			for (String listKey : listKeys) {
				IngridDocument listDocument = (IngridDocument) result.get(listKey);
				ArrayList<String[]> resultList = new ArrayList<String[]>();
				Integer listId = (Integer) listDocument.get(MdekKeys.LST_ID);

				List<IngridDocument> entries = (List<IngridDocument>) listDocument.get(MdekKeys.LST_ENTRY_LIST);
				for (IngridDocument entry : entries) {
					resultList.add( new String[] {entry.getString(MdekKeys.ENTRY_NAME), ((Integer) entry.get(MdekKeys.ENTRY_ID)).toString()} );
				}
				resultMap.put(listId, resultList);
			}
			return resultMap;

		} else {
			log.error("ERROR: " + mdekCaller.getErrorMsgFromResponse(response));
			return null;
		}
	}

	public CatalogBean extractCatalogFromResponse(IngridDocument response) {
		IngridDocument result = mdekCaller.getResultFromResponse(response);
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
	}


	private final static SimpleDateFormat timestampFormatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");

	private static Date convertTimestampToDate(String timeStamp) {
		if (timeStamp != null && timeStamp.length() != 0) {
			try {
				Date date = timestampFormatter.parse(timeStamp);
				return date;
			} catch (Exception ex){
				log.error("Problems parsing timestamp from database: " + timeStamp, ex);
				return null;
			}
		} else {
			return null;
		}
	}
}