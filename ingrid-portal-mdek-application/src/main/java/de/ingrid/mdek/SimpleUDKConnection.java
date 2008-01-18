package de.ingrid.mdek;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.ingrid.mdek.IMdekCaller.Quantity;
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
		IngridDocument response = mdekCaller.moveObject(fromUuid, toUuid);
		return (mdekCaller.getResultFromResponse(response) != null);
	}

	public Map<String, List<String>> getUiListValues() {
		IngridDocument response = mdekCaller.getUiListValues();
		return extractUIListFromResponse(response);
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

	public Map<String, List<String>> extractUIListFromResponse(IngridDocument response) {
		IngridDocument result = mdekCaller.getResultFromResponse(response);
		Map resultMap = new HashMap<String, List<String>>();

		// TODO Create own keys
		resultMap.put(MdekKeys.UI_FREE_SPATIAL_REFERENCES, result.get(MdekKeys.UI_FREE_SPATIAL_REFERENCES));
		return resultMap;
	}

}
