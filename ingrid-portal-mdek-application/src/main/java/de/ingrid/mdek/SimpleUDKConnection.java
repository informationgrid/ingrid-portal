package de.ingrid.mdek;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;

import de.ingrid.mdek.IMdekCallerCommon.Quantity;
import de.ingrid.mdek.IMdekErrors.MdekError;
import de.ingrid.mdek.dwr.CatalogBean;
import de.ingrid.mdek.dwr.JobInfoBean;
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

	public MdekDataBean getObjectDetail(String uuid) {
		IngridDocument response = mdekCaller.fetchObject(uuid, Quantity.DETAIL_ENTITY, getCurrentSessionId());
		return extractSingleObjectFromResponse(response);
	}

	public ArrayList<HashMap<String, Object>> getRootAddresses() {
		IngridDocument response = mdekCaller.fetchTopAddresses(getCurrentSessionId(), false);
		return extractAddressesFromResponse(response);
	}

	public ArrayList<HashMap<String, Object>> getRootObjects() {
		IngridDocument response = mdekCaller.fetchTopObjects(getCurrentSessionId());
		return extractObjectsFromResponse(response);
	}

	public ArrayList<HashMap<String, Object>> getSubObjects(String uuid, int depth) {
		IngridDocument response = mdekCaller.fetchSubObjects(uuid, getCurrentSessionId());
		return extractObjectsFromResponse(response);
	}

	public ArrayList<HashMap<String, Object>> getSubAddresses(String uuid, int depth) {
		IngridDocument response = mdekCaller.fetchSubAddresses(uuid, getCurrentSessionId());
		return extractAddressesFromResponse(response);
	}

	public MdekDataBean getInitialObject(String parentUuid) {
		IngridDocument obj = new IngridDocument();
		obj.put(MdekKeys.PARENT_UUID, parentUuid);

		IngridDocument response = mdekCaller.getInitialObject(obj, getCurrentSessionId());
		return extractSingleObjectFromResponse(response);
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

		IngridDocument response = mdekCaller.storeObject(obj, true, getCurrentSessionId());
		return extractSingleObjectFromResponse(response);
	}

	public MdekDataBean publishObject(MdekDataBean data, boolean forcePublicationCondition) {
	IngridDocument obj = (IngridDocument) dataMapper.convertFromObjectRepresentation(data);

	if (data.getUuid().equalsIgnoreCase("newNode")) {
		obj.remove(MdekKeys.UUID);
		obj.remove(MdekKeys.ID);
	}

	log.debug("Sending the following object for publishing:");
	log.debug(obj);

	IngridDocument response = mdekCaller.publishObject(obj, true, forcePublicationCondition, getCurrentSessionId());
	return extractSingleObjectFromResponse(response);
}

	
	public void deleteObject(String uuid) {
		mdekCaller.deleteObject(uuid, getCurrentSessionId());
	}

	public boolean deleteObjectWorkingCopy(String uuid) {
		IngridDocument response = mdekCaller.deleteObjectWorkingCopy(uuid, getCurrentSessionId());

		IngridDocument result = extractAdditionalInformationFromResponse(response);
		return (Boolean) result.get(MdekKeys.RESULTINFO_WAS_FULLY_DELETED);
	}

	public boolean canCutObject(String uuid) {
		IngridDocument response = mdekCaller.checkObjectSubTree(uuid, getCurrentSessionId());
		if (mdekCaller.getResultFromResponse(response) == null) {
			handleError(response);
		} else {
			IngridDocument result = mdekCaller.getResultFromResponse(response);
			boolean hasWorkingCopy = result.getBoolean(MdekKeys.RESULTINFO_HAS_WORKING_COPY);
			if (hasWorkingCopy) {
				// Throw an error. A node that is about to be moved must not have working copies as children
				throw new MdekException(MdekError.SUBTREE_HAS_WORKING_COPIES);
			}
		}
		return true;
	}

	public boolean canCopyObject(String uuid) {
		// Copy is always allowed. Placeholder for future changes
		return true;
/*
		IngridDocument response = mdekCaller.checkObjectSubTree(uuid, getCurrentSessionId());
		if (mdekCaller.getResultFromResponse(response) == null) {
			handleError(response);
		}
*/
	}

	public List<String> getPathToObject(String uuid) {
		IngridDocument response = mdekCaller.getObjectPath(uuid, getCurrentSessionId());
		return extractPathFromResponse(response);
	}

	public Map<String, Object> copyObject(String fromUuid, String toUuid, boolean copySubTree) {
		IngridDocument response = mdekCaller.copyObject(fromUuid, toUuid, copySubTree, getCurrentSessionId());
		return extractSingleSimpleObjectFromResponse(response);
	}

	public void moveObjectSubTree(String fromUuid, String toUuid, boolean forcePublicationCondition) {
		IngridDocument response = mdekCaller.moveObject(fromUuid, toUuid, true, forcePublicationCondition, getCurrentSessionId());
		if (mdekCaller.getResultFromResponse(response) == null) {
			handleError(response);
		}
	}

	public Map<Integer, List<String[]>> getSysLists(Integer[] listIds, Integer languageCode) {
		IngridDocument response = mdekCaller.getSysLists(listIds, languageCode, getCurrentSessionId());
		return extractSysListFromResponse(response);
	}

	public CatalogBean getCatalogData() {
		IngridDocument response = mdekCaller.fetchCatalog(getCurrentSessionId());
		return extractCatalogFromResponse(response);
	}

	public JobInfoBean getRunningJobInfo() {
		IngridDocument response = mdekCaller.getRunningJobInfo(getCurrentSessionId());
		return extractJobInfoFromResponse(response);
	}

	public JobInfoBean cancelRunningJob() {
		IngridDocument response = mdekCaller.cancelRunningJob(getCurrentSessionId());
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

	private MdekDataBean extractSingleObjectFromResponse(IngridDocument response) {
		IngridDocument result = mdekCaller.getResultFromResponse(response);

		if (result != null) {
			return dataMapper.getDetailedObjectRepresentation(result);
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

	public Map<Integer, List<String[]>> extractSysListFromResponse(IngridDocument response) {
		IngridDocument result = mdekCaller.getResultFromResponse(response);
		if (result != null) {
			Map<Integer, List<String[]>> resultMap = new HashMap<Integer, List<String[]>>();
			Set<String> listKeys = (Set<String>) result.keySet();
			for (String listKey : listKeys) {
				IngridDocument listDocument = (IngridDocument) result.get(listKey);
				ArrayList<String[]> resultList = new ArrayList<String[]>();
				Integer listId = (Integer) listDocument.get(MdekKeys.LST_ID);

				List<IngridDocument> entries = (List<IngridDocument>) listDocument.get(MdekKeys.LST_ENTRY_LIST);
				resultList.add( new String[] {"", ""} );
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

	public CatalogBean extractCatalogFromResponse(IngridDocument response) {
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

	public JobInfoBean extractJobInfoFromResponse(IngridDocument response) {
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
		log.error(errorMessage);
		List<MdekError> err = mdekCaller.getErrorsFromResponse(response);
		if (err != null)
			throw new MdekException(err);
		else
			throw new RuntimeException(errorMessage);
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

	// Helper method to get the current sesison Id. Will be replaced by the user id somday
	private String getCurrentSessionId() {
		WebContext wctx = WebContextFactory.get();
		HttpSession session = wctx.getSession();
		return session.getId();
	}

}