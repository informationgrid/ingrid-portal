package de.ingrid.mdek.dwr.services;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.ingrid.mdek.beans.object.MdekDataBean;
import de.ingrid.mdek.handler.ObjectRequestHandler;
import de.ingrid.mdek.job.MdekException;
import de.ingrid.mdek.util.MdekErrorUtils;
import de.ingrid.mdek.util.MdekObjectUtils;

public class ObjectServiceImpl implements ObjectService {

	private final static Logger log = Logger.getLogger(ObjectServiceImpl.class);	

	// Injected by Spring
	private ObjectRequestHandler objectRequestHandler;

	private final static String OBJECT_APPTYPE = "O";
	private final static String OBJECT_INITIAL_DOCTYPE = "Class0_B";

	
	public boolean canCopyObject(String uuid) {
		log.debug("Query if node can be copied: "+uuid);

		try {
			objectRequestHandler.canCopyObject(uuid);
		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while checking if node can be cut.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		} catch (Exception e) {
			log.error("Error checking if node can be copied.", e);
		}
		return true;
	}

	
	public boolean canCutObject(String uuid) {
		log.debug("Query if node can be cut: "+uuid);

		try {
			objectRequestHandler.canCutObject(uuid);
		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while checking if node can be cut.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		} catch (Exception e) {
			log.error("Error checking if node can be cut.", e);
		}
		return true;
	}

	public Map<String, Object> copyNode(String nodeUuid, String dstNodeUuid,
			Boolean includeChildren) {
		log.debug("Copying node with ID: "+nodeUuid+" to ID: "+dstNodeUuid);

		try {
			Map<String, Object> copyResult = objectRequestHandler.copyObject(nodeUuid, dstNodeUuid, includeChildren);
			if (copyResult != null) {
				return TreeServiceImpl.addTreeNodeObjectInfo(copyResult);
			} else {
				return null;
			}
		}
		catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while copying node.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		}
		catch (Exception e) {
			log.debug("Error copying node.", e);
			throw new RuntimeException(e);
		}
	}

	
	public MdekDataBean createNewNode(String parentUuid) {
		log.debug("creating new node with parent id = "+parentUuid);		
		MdekDataBean data = null;
		try {
			data = objectRequestHandler.getInitialObject(parentUuid);
		} catch (Exception e) {
			log.error("Error while getting node data.", e);
		}

		MdekObjectUtils.setInitialValues(data);

		data.setNodeAppType(OBJECT_APPTYPE);
		data.setNodeDocType(OBJECT_INITIAL_DOCTYPE);
		data.setUuid("newNode");
		return data;
	}

	
	public void deleteNode(String uuid, Boolean forceDeleteReferences, Boolean markOnly) {
		try {
			objectRequestHandler.deleteObject(uuid, forceDeleteReferences);
		}
		catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("Error deleting object.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		}
		catch (Exception e) {
			log.debug("Error deleting object.", e);
			throw new RuntimeException(e);
		}
	}

	
	public MdekDataBean deleteObjectWorkingCopy(String uuid, Boolean forceDeleteReferences, Boolean markOnly) {
		try {
			boolean wasFullyDeleted = objectRequestHandler.deleteObjectWorkingCopy(uuid, forceDeleteReferences);
			if (!wasFullyDeleted) {
				return objectRequestHandler.getObjectDetail(uuid);
			}
		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("Error deleting working Copy.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		}
		catch (Exception e) {
			log.debug("Error deleting working Copy.", e);
			throw new RuntimeException(e);
		}

		return null;
	}

	public MdekDataBean getNodeData(String nodeUuid, String nodeType,
			Boolean useWorkingCopy) {
		
		MdekDataBean data = null; 

		try {
			data = objectRequestHandler.getObjectDetail(nodeUuid);
		} catch (RuntimeException e) {
			log.debug("Error while getting node data.", e);
			throw e;
		}

		// Return a newly created node
		if (data == null) {;}

		return data;
	}

	public List<String> getPathToObject(String uuid) {
		return objectRequestHandler.getPathToObject(uuid);
	}

	public void moveNode(String nodeUuid, String dstNodeUuid, boolean forcePublicationCondition) {
		log.debug("Moving node with ID: "+nodeUuid+" to ID: "+dstNodeUuid);

		try {
			objectRequestHandler.moveObjectSubTree(nodeUuid, dstNodeUuid, forcePublicationCondition);
		}
		catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while moving node.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		}
		catch (Exception e) {
			log.debug("Error moving node.", e);
			throw new RuntimeException(e);
		}
	}

	public MdekDataBean saveNodeData(MdekDataBean data, Boolean useWorkingCopy, Boolean forcePublicationCondition) {
		log.debug("saveNodeData()");
		if (useWorkingCopy) {
			log.debug("Saving node with ID: "+data.getUuid());

			try { return objectRequestHandler.saveObject(data); }
			catch (MdekException e) {
				// Wrap the MdekException in a RuntimeException so dwr can convert it
				log.debug("MdekException while saving node.", e);
				throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
			}
			catch (RuntimeException e) {
				log.debug("Error while saving node", e);
				throw e;
			}
		} else {
			log.debug("Publishing node with ID: "+data.getUuid());

			try { return objectRequestHandler.publishObject(data, forcePublicationCondition); }
			catch (MdekException e) {
				// Wrap the MdekException in a RuntimeException so dwr can convert it
				log.debug("MdekException while publishing node.", e);
				throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
			}
			catch (RuntimeException e) {
				log.debug("Error while publishing node", e);
				throw e;
			}
		}
	}
	
	public ObjectRequestHandler getObjectRequestHandler() {
		return objectRequestHandler;
	}


	public void setObjectRequestHandler(ObjectRequestHandler objectRequestHandler) {
		this.objectRequestHandler = objectRequestHandler;
	}
}
