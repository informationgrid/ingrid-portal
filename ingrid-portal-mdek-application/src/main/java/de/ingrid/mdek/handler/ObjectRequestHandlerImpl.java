package de.ingrid.mdek.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.ingrid.mdek.MdekError;
import de.ingrid.mdek.MdekKeys;
import de.ingrid.mdek.MdekError.MdekErrorType;
import de.ingrid.mdek.beans.MdekDataBean;
import de.ingrid.mdek.caller.IMdekCaller;
import de.ingrid.mdek.caller.IMdekCallerObject;
import de.ingrid.mdek.caller.IMdekCallerAbstract.Quantity;
import de.ingrid.mdek.dwr.util.HTTPSessionHelper;
import de.ingrid.mdek.job.MdekException;
import de.ingrid.mdek.util.MdekErrorUtils;
import de.ingrid.mdek.util.MdekObjectUtils;
import de.ingrid.mdek.util.MdekUtils;
import de.ingrid.utils.IngridDocument;

public class ObjectRequestHandlerImpl implements ObjectRequestHandler {

	private final static Logger log = Logger.getLogger(ObjectRequestHandlerImpl.class);

	// Injected by Spring
	private ConnectionFacade connectionFacade;


	// Initialized by spring through the init method
	private IMdekCaller mdekCaller;
	private IMdekCallerObject mdekCallerObject;

	public void init() {
		mdekCaller = connectionFacade.getMdekCaller();
		mdekCallerObject = connectionFacade.getMdekCallerObject();
	}

	public MdekDataBean getObjectDetail(String uuid) {
		IngridDocument response = mdekCallerObject.fetchObject(connectionFacade.getCurrentPlugId(), uuid, Quantity.DETAIL_ENTITY, HTTPSessionHelper.getCurrentSessionId());
		return MdekObjectUtils.extractSingleObjectFromResponse(response);
	}

	public ArrayList<HashMap<String, Object>> getRootObjects() {
		IngridDocument response = mdekCallerObject.fetchTopObjects(connectionFacade.getCurrentPlugId(), HTTPSessionHelper.getCurrentSessionId());
		return MdekObjectUtils.extractObjectsFromResponse(response);
	}

	public boolean canCopyObject(String uuid) {
		// Copy is always allowed. Placeholder for future changes
		return true;
	}

	public boolean canCutObject(String uuid) {
		IngridDocument response = mdekCallerObject.checkObjectSubTree(connectionFacade.getCurrentPlugId(), uuid, HTTPSessionHelper.getCurrentSessionId());
		if (mdekCaller.getResultFromResponse(response) == null) {
			MdekErrorUtils.handleError(response);
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

	public Map<String, Object> copyObject(String fromUuid, String toUuid, boolean copySubTree) {
		IngridDocument response = mdekCallerObject.copyObject(connectionFacade.getCurrentPlugId(), fromUuid, toUuid, copySubTree, HTTPSessionHelper.getCurrentSessionId());
		return MdekObjectUtils.extractSingleSimpleObjectFromResponse(response);
	}

	public void deleteObject(String uuid, boolean forceDeleteReferences) {
		IngridDocument response = mdekCallerObject.deleteObject(connectionFacade.getCurrentPlugId(), uuid, forceDeleteReferences, HTTPSessionHelper.getCurrentSessionId());
		IngridDocument result = MdekUtils.extractAdditionalInformationFromResponse(response);
	}

	public boolean deleteObjectWorkingCopy(String uuid, boolean forceDeleteReferences) {
		IngridDocument response = mdekCallerObject.deleteObjectWorkingCopy(connectionFacade.getCurrentPlugId(), uuid, forceDeleteReferences, HTTPSessionHelper.getCurrentSessionId());

		IngridDocument result = MdekUtils.extractAdditionalInformationFromResponse(response);
		return (Boolean) result.get(MdekKeys.RESULTINFO_WAS_FULLY_DELETED);
	}

	public MdekDataBean getInitialObject(String parentUuid) {
		IngridDocument obj = new IngridDocument();
		obj.put(MdekKeys.PARENT_UUID, parentUuid);

		IngridDocument response = mdekCallerObject.getInitialObject(connectionFacade.getCurrentPlugId(), obj, HTTPSessionHelper.getCurrentSessionId());
		return MdekObjectUtils.extractSingleObjectFromResponse(response);
	}

	public List<String> getPathToObject(String uuid) {
		IngridDocument response = mdekCallerObject.getObjectPath(connectionFacade.getCurrentPlugId(), uuid, HTTPSessionHelper.getCurrentSessionId());
		return MdekUtils.extractPathFromResponse(response);
	}
	
	public ArrayList<HashMap<String, Object>> getSubObjects(String uuid, int depth) {
		IngridDocument response = mdekCallerObject.fetchSubObjects(connectionFacade.getCurrentPlugId(), uuid, HTTPSessionHelper.getCurrentSessionId());
		return MdekObjectUtils.extractObjectsFromResponse(response);
	}

	public void moveObjectSubTree(String fromUuid, String toUuid, boolean forcePublicationCondition) {
		IngridDocument response = mdekCallerObject.moveObject(connectionFacade.getCurrentPlugId(), fromUuid, toUuid, forcePublicationCondition, HTTPSessionHelper.getCurrentSessionId());
		if (mdekCaller.getResultFromResponse(response) == null) {
			MdekErrorUtils.handleError(response);
		}
	}

	public MdekDataBean publishObject(MdekDataBean data, boolean forcePublicationCondition) {
		IngridDocument obj = (IngridDocument) MdekObjectUtils.convertFromObjectRepresentation(data);

		if (data.getUuid().equalsIgnoreCase("newNode")) {
			obj.remove(MdekKeys.UUID);
			obj.remove(MdekKeys.ID);
		}

		log.debug("Sending the following object for publishing:");
		log.debug(obj);

		IngridDocument response = mdekCallerObject.publishObject(connectionFacade.getCurrentPlugId(), obj, true, forcePublicationCondition, HTTPSessionHelper.getCurrentSessionId());
		return MdekObjectUtils.extractSingleObjectFromResponse(response);
	}

	public MdekDataBean saveObject(MdekDataBean data) {
		IngridDocument obj = (IngridDocument) MdekObjectUtils.convertFromObjectRepresentation(data);

		if (data.getUuid().equalsIgnoreCase("newNode")) {
			obj.remove(MdekKeys.UUID);
			obj.remove(MdekKeys.ID);
		}

		log.debug("Sending the following object for storage:");
		log.debug(obj);

		IngridDocument response = mdekCallerObject.storeObject(connectionFacade.getCurrentPlugId(), obj, true, HTTPSessionHelper.getCurrentSessionId());
		return MdekObjectUtils.extractSingleObjectFromResponse(response);
	}


	public ConnectionFacade getConnectionFacade() {
		return connectionFacade;
	}


	public void setConnectionFacade(ConnectionFacade connectionFacade) {
		this.connectionFacade = connectionFacade;
	}
}
