package de.ingrid.mdek.dwr.services;

import java.util.List;

import org.apache.log4j.Logger;

import de.ingrid.mdek.MdekUtils.IdcEntityOrderBy;
import de.ingrid.mdek.MdekUtils.IdcQAEntitiesSelectionType;
import de.ingrid.mdek.MdekUtils.IdcWorkEntitiesSelectionType;
import de.ingrid.mdek.MdekUtils.WorkState;
import de.ingrid.mdek.beans.TreeNodeBean;
import de.ingrid.mdek.beans.object.MdekDataBean;
import de.ingrid.mdek.beans.query.ObjectSearchResultBean;
import de.ingrid.mdek.beans.query.ObjectStatisticsResultBean;
import de.ingrid.mdek.beans.query.ThesaurusStatisticsResultBean;
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

	public TreeNodeBean copyNode(String nodeUuid, String dstNodeUuid,
			Boolean includeChildren) {
		log.debug("Copying node with ID: "+nodeUuid+" to ID: "+dstNodeUuid);

		try {
			TreeNodeBean copyResult = objectRequestHandler.copyObject(nodeUuid, dstNodeUuid, includeChildren);
			if (copyResult != null) {
				TreeServiceImpl.addTreeNodeObjectInfo(copyResult);
			}
			return copyResult;
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

	public MdekDataBean getPublishedNodeData(String nodeUuid) {
		MdekDataBean data = null; 

		try {
			data = objectRequestHandler.getPublishedObjectDetail(nodeUuid);
		} catch (RuntimeException e) {
			log.debug("Error while getting published node data.", e);
			throw e;
		}

		// Return a newly created node
		if (data == null) {;}

		return data;
	}

	
	public List<String> getPathToObject(String uuid) {
		return objectRequestHandler.getPathToObject(uuid);
	}

	public void moveNode(String nodeUuid, String oldParentUuid, String newParentUuid, boolean forcePublicationCondition) {
		log.debug("Moving node with ID: "+nodeUuid+" and parent: "+oldParentUuid+" to ID: "+newParentUuid);

		try {
			objectRequestHandler.moveObjectSubTree(nodeUuid, oldParentUuid, newParentUuid, forcePublicationCondition);
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

	public void updateObjectTitle(String uuid, String newTitle) {
		try {
			objectRequestHandler.updateObjectTitle(uuid, newTitle);

		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while updating object title.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		}
	}

	public MdekDataBean assignObjectToQA(MdekDataBean data) {
		log.debug("Assigning node with ID to QA: "+data.getUuid());

		try { return objectRequestHandler.assignObjectToQA(data); }
		catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while assigning node to QA.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		}
		catch (RuntimeException e) {
			log.debug("Error while assigning node to QA", e);
			throw e;
		}
	}		

	public MdekDataBean reassignObjectToAuthor(MdekDataBean data) {
		log.debug("Reassigning node with ID to author: "+data.getUuid());

		try { return objectRequestHandler.reassignObjectToAuthor(data); }
		catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while reassigning node to Author.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		}
		catch (RuntimeException e) {
			log.debug("Error while reassigning node to Author", e);
			throw e;
		}
	}		
	
	public ObjectSearchResultBean getWorkObjects(IdcWorkEntitiesSelectionType selectionType, IdcEntityOrderBy orderBy, boolean orderAsc, Integer startHit, Integer numHits) {
		try {
			return objectRequestHandler.getWorkObjects(selectionType, orderBy, orderAsc, startHit, numHits);

		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while fetching Work objects.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
	
		} catch (RuntimeException e) {
			log.debug("Error while fetching Work objects", e);
			throw e;
		}
	}

	public ObjectSearchResultBean getQAObjects(WorkState workState, IdcQAEntitiesSelectionType selectionType, IdcEntityOrderBy orderBy, boolean orderAsc, Integer startHit, Integer numHits) {
		try {
			return objectRequestHandler.getQAObjects(workState, selectionType, orderBy, orderAsc, startHit, numHits);

		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while fetching QA objects.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));

		} catch (RuntimeException e) {
			log.debug("Error while fetching QA objects", e);
			throw e;
		}
	}

	public ObjectStatisticsResultBean getObjectStatistics(String objUuid) {
		try {
			return objectRequestHandler.getObjectStatistics(objUuid);

		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while fetching objects for statistics.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));

		} catch (RuntimeException e) {
			log.debug("Error while fetching objects for statistics", e);
			throw e;
		}		
	}

	public ThesaurusStatisticsResultBean getObjectThesaurusStatistics(String objUuid, boolean thesaurusTerms, int startHit, int numHits) {
	try {
		return objectRequestHandler.getObjectThesaurusStatistics(objUuid, thesaurusTerms, startHit, numHits);

	} catch (MdekException e) {
		// Wrap the MdekException in a RuntimeException so dwr can convert it
		log.debug("MdekException while fetching objects for thesaurus statistics.", e);
		throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));

	} catch (RuntimeException e) {
		log.debug("Error while fetching objects for thesaurus statistics", e);
		throw e;
	}		
}

	public ObjectRequestHandler getObjectRequestHandler() {
		return objectRequestHandler;
	}


	public void setObjectRequestHandler(ObjectRequestHandler objectRequestHandler) {
		this.objectRequestHandler = objectRequestHandler;
	}
}
