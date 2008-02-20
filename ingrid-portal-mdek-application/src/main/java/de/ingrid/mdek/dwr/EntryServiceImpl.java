/**
 * 
 */
package de.ingrid.mdek.dwr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.ingrid.mdek.DataConnectionInterface;
import de.ingrid.mdek.MdekException;
import de.ingrid.mdek.IMdekErrors.MdekError;
import de.ingrid.mdek.dwr.api.EntryService;


/**
 * @author mbenz
 * 
 */
public class EntryServiceImpl implements EntryService {

	private final static Logger log = Logger.getLogger(EntryServiceImpl.class);	
	
	private DataConnectionInterface dataConnection;

	// OBJECT_ROOT specifies the uuid for the object root node. 
	private final static String OBJECT_ROOT = "objectRoot"; 
	// TODO Load from a cfg file -> localization 
	private final static String OBJECT_ROOT_NAME = "Objekte";

	// TODO Move the following to EntryService.java?
	private final static String OBJECT_ROOT_DOCTYPE = "Objects";
	private final static String OBJECT_INITIAL_DOCTYPE = "Class0_B";
	private final static String ROOT_MENU_ID = "contextMenu2";
	private final static String NODE_MENU_ID = "contextMenu1";
	private final static String NODE_DOJO_TYPE = "ingrid:TreeNode";
	private final static String OBJECT_APPTYPE = "O";


	private final static String ADDRESS_ROOT = "addressRoot"; 
	private final static String ADDRESS_ROOT_NAME = "Adressen";
	private final static String ADDRESS_ROOT_DOCTYPE = "Addresses";
	private final static String ADDRESS_INITIAL_DOCTYPE = "Institution_B";
	private final static String ADDRESS_APPTYPE = "A";

	
	public DataConnectionInterface getDataConnection() {
		return dataConnection;
	}

	public void setDataConnection(DataConnectionInterface dataConnection) {
		this.dataConnection = dataConnection;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.ingrid.mdek.dwr.api.EntryService#copyNode(java.lang.String,
	 *      java.lang.String, java.lang.Boolean)
	 */
	public Map<String, Object> copyNode(String nodeUuid, String dstNodeUuid,
			Boolean includeChildren) {
		log.debug("Copying node with ID: "+nodeUuid+" to ID: "+dstNodeUuid);

		try {
			Map<String, Object> copyResult = dataConnection.copyObject(nodeUuid, dstNodeUuid, includeChildren);
			if (copyResult != null) {
				return addTreeNodeObjectInfo(copyResult);
			} else {
				return null;
			}
		}
		catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while copying node.", e);
			throw new RuntimeException(convertToRuntimeException(e));
		}
		catch (Exception e) {
			log.error("Error copying node.", e);
			throw new RuntimeException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.ingrid.mdek.dwr.api.EntryService#deleteNode(java.lang.String,
	 *      java.lang.Boolean)
	 */
	public String deleteNode(String uuid, Boolean markOnly) {
		dataConnection.deleteObject(uuid);
		return "success";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.ingrid.mdek.dwr.api.EntryService#deleteObjectWorkingCopy(java.lang.String,
	 *      java.lang.Boolean)
	 */
	public MdekDataBean deleteObjectWorkingCopy(String uuid, Boolean markOnly) {
		try {
			boolean wasFullyDeleted = dataConnection.deleteObjectWorkingCopy(uuid);
			if (!wasFullyDeleted) {
				return dataConnection.getObjectDetail(uuid);
			}
		} catch (Exception e) {
			log.error("Error deleting working Copy.", e);
		};
		return null;
	}

	public MdekAddressBean deleteAddressWorkingCopy(String uuid, Boolean markOnly) {
		try {
			boolean wasFullyDeleted = dataConnection.deleteAddressWorkingCopy(uuid);
			if (!wasFullyDeleted) {
				return dataConnection.getAddressDetail(uuid);
			}
		} catch (Exception e) {
			log.error("Error deleting address working Copy.", e);
		};
		return null;
	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.ingrid.mdek.dwr.api.EntryService#getPathToObject(java.lang.String)
	 */
	public List<String> getPathToObject(String uuid) {
		return dataConnection.getPathToObject(uuid);
	}
	public List<String> getPathToAddress(String uuid) {
		return dataConnection.getPathToAddress(uuid);
	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.ingrid.mdek.dwr.api.EntryService#getNodeData(java.lang.String,
	 *      java.lang.String, java.lang.Boolean)
	 */
	public MdekDataBean getNodeData(String nodeUuid, String nodeType,
			Boolean useWorkingCopy) {
		
		MdekDataBean data = null; 

		try {
			data = dataConnection.getObjectDetail(nodeUuid);
		} catch (Exception e) {
			log.error("Error while getting node data.", e);
		}

		// TODO check for errors and throw an exception?
		// Return a newly created node
		if (data == null) {;}

		return data;
	}

	public MdekAddressBean getAddressData(String nodeUuid, Boolean useWorkingCopy) {
		MdekAddressBean address = null; 

		try {
			address = dataConnection.getAddressDetail(nodeUuid);
		} catch (Exception e) {
			log.error("Error while getting address data.", e);
		}

		// TODO check for errors and throw an exception?
		// Return a newly created node
		if (address == null) {;}

		return address;
	}

	
	public MdekDataBean createNewNode(String parentUuid) {
		log.debug("creating new node with parent id = "+parentUuid);		
		MdekDataBean data = null;
		try {
			data = dataConnection.getInitialObject(parentUuid);
		} catch (Exception e) {
			log.error("Error while getting node data.", e);
		}

//		data.setObjectName("Neues Objekt");
		data.setNodeAppType(OBJECT_APPTYPE);
		data.setNodeDocType(OBJECT_INITIAL_DOCTYPE);
		data.setUuid("newNode");
		return data;
	}

	public MdekAddressBean createNewAddress(String parentUuid) {
		log.debug("creating new address with parent id = "+parentUuid);		
		MdekAddressBean data = null;
		try {
			data = dataConnection.getInitialAddress(parentUuid);
		} catch (Exception e) {
			log.error("Error while getting address data.", e);
		}

		data.setNodeAppType(ADDRESS_APPTYPE);
//		data.setNodeDocType(ADDRESS_INITIAL_DOCTYPE);
		data.setUuid("newNode");
		return data;
	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.ingrid.mdek.dwr.api.EntryService#getOpenTree(java.lang.String,
	 *      java.lang.String, java.lang.Boolean)
	 */
	public List getOpenTree(String nodeUuid, String nodeType,
			Boolean allRootTypes) throws Exception {
		// TODO: build tree top down until the node with nodeUuid is reached
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.ingrid.mdek.dwr.api.EntryService#getSubTree(java.lang.String,
	 *      java.lang.String, int)
	 */
	public List getSubTree(String nodeUuid, String nodeType, int depth) throws Exception {
		// TODO Cleanup
		// TODO The depth parameter is currently ignored

		if (nodeUuid != null && nodeType == null) {
			throw new IllegalArgumentException("Wrong arguments on method getSubTree(): nodeType must be set if nodeUuid is set!");
		}

		if (nodeUuid == null) {
			return createTree();
		}


		if (nodeType.equals(OBJECT_APPTYPE)) {
			ArrayList<HashMap<String, Object>> subObjects = null; 
			
			if (nodeUuid.equalsIgnoreCase(OBJECT_ROOT))
				subObjects = dataConnection.getRootObjects();
			else
				subObjects = dataConnection.getSubObjects(nodeUuid, depth);
	
			for (HashMap<String, Object> node : subObjects) {
				addTreeNodeObjectInfo(node);
			}
			return subObjects;

		} else if (nodeType.equals(ADDRESS_APPTYPE)) {
			ArrayList<HashMap<String, Object>> subAddresses = null; 
			if (nodeUuid.equalsIgnoreCase(ADDRESS_ROOT))
				subAddresses = dataConnection.getRootAddresses();
			else
				subAddresses = dataConnection.getSubAddresses(nodeUuid, depth);
	
			for (HashMap<String, Object> node : subAddresses) {
				addTreeNodeAddressInfo(node);
			}
			return subAddresses;

		} else {
			throw new IllegalArgumentException("Unknown node type: "+nodeType); 
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.ingrid.mdek.dwr.api.EntryService#moveNode(java.lang.String,
	 *      java.lang.String)
	 */
	public void moveNode(String nodeUuid, String dstNodeUuid, boolean forcePublicationCondition) {
		log.debug("Moving node with ID: "+nodeUuid+" to ID: "+dstNodeUuid);

		try {
			dataConnection.moveObjectSubTree(nodeUuid, dstNodeUuid, forcePublicationCondition);
		}
		catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while moving node.", e);
			throw new RuntimeException(convertToRuntimeException(e));
		}
		catch (Exception e) {
			log.error("Error moving node.", e);
			throw new RuntimeException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.ingrid.mdek.dwr.api.EntryService#saveNodeData(MdekDataBean,
	 *      java.lang.Boolean)
	 */
	public MdekDataBean saveNodeData(MdekDataBean data, Boolean useWorkingCopy, Boolean forcePublicationCondition) {
		log.debug("saveNodeData()");
		if (useWorkingCopy) {
			log.debug("Saving node with ID: "+data.getUuid());

			try { return dataConnection.saveObject(data); }
			catch (MdekException e) {
				// Wrap the MdekException in a RuntimeException so dwr can convert it
				log.debug("MdekException while saving node.", e);
				throw new RuntimeException(convertToRuntimeException(e));
			}
			catch (Exception e) {
				log.error("Error while saving node", e);
				throw new RuntimeException("Error while saving node.");
			}
		} else {
			log.debug("Publishing node with ID: "+data.getUuid());

			try { return dataConnection.publishObject(data, forcePublicationCondition); }
			catch (MdekException e) {
				// Wrap the MdekException in a RuntimeException so dwr can convert it
				log.debug("MdekException while publishing node.", e);
				throw new RuntimeException(convertToRuntimeException(e));
			}
			catch (Exception e) {
				log.error("Error while publishing node", e);
				throw new RuntimeException("Error while publishing node.");
			}
		}
	}


	public MdekAddressBean saveAddressData(MdekAddressBean data, Boolean useWorkingCopy) {
		log.debug("saveAddressData()");

		if (useWorkingCopy) {
			log.debug("Saving address with ID: "+data.getUuid());

			try { return dataConnection.saveAddress(data); }
			catch (MdekException e) {
				// Wrap the MdekException in a RuntimeException so dwr can convert it
				log.debug("MdekException while saving address.", e);
				throw new RuntimeException(convertToRuntimeException(e));
			}
			catch (Exception e) {
				log.error("Error while saving node", e);
				throw new RuntimeException("Error while saving address.");
			}
		} else {
			log.debug("Publishing address with ID: "+data.getUuid());

			try { return dataConnection.publishAddress(data); }
			catch (MdekException e) {
				// Wrap the MdekException in a RuntimeException so dwr can convert it
				log.debug("MdekException while publishing address.", e);
				throw new RuntimeException(convertToRuntimeException(e));
			}
			catch (Exception e) {
				log.error("Error while publishing address", e);
				throw new RuntimeException("Error while publishing address.");
			}
		}
	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.ingrid.mdek.dwr.api.EntryService#canCutObject(java.lang.String)
	 */
	public boolean canCutObject(String uuid) {
		log.debug("Query if node can be cut: "+uuid);

		try {
			  dataConnection.canCutObject(uuid);
		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while checking if node can be cut.", e);
			throw new RuntimeException(convertToRuntimeException(e));
		} catch (Exception e) {
			log.error("Error checking if node can be cut.", e);
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.ingrid.mdek.dwr.api.EntryService#canCutObject(java.lang.String)
	 */
	public boolean canCopyObject(String uuid) {
		log.debug("Query if node can be copied: "+uuid);

		try {
			  dataConnection.canCopyObject(uuid);
		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.debug("MdekException while checking if node can be cut.", e);
			throw new RuntimeException(convertToRuntimeException(e));
		} catch (Exception e) {
			log.error("Error checking if node can be copied.", e);
		}
		return true;
	}

	public Map<Integer, List<String[]>> getSysLists(Integer[] listIds, Integer languageCode) {
		return dataConnection.getSysLists(listIds, languageCode);
	}

	public CatalogBean getCatalogData() {
		return dataConnection.getCatalogData();	
	}

	public JobInfoBean getRunningJobInfo() {
		return dataConnection.getRunningJobInfo();
	}

	public JobInfoBean cancelRunningJob() {
		return dataConnection.cancelRunningJob();
	}
	
	
// ------------------------ Helper Methods ---------------------------------------	
	
	private static RuntimeException convertToRuntimeException(MdekException e) {
		String errorStr = "";
		List<MdekError> errorList = e.getMdekErrors();
		for (MdekError mdekError : errorList) {
			errorStr += mdekError.toString()+" ";
		}
		return new RuntimeException(errorStr.trim());
	}


	private static ArrayList<HashMap<String, Object>> createTree()
	{
		ArrayList<HashMap<String, Object>> treeRoot = new ArrayList<HashMap<String, Object>>(); 

		HashMap<String, Object> objectRoot = new HashMap<String, Object>();
		objectRoot.put("contextMenu", ROOT_MENU_ID);
		objectRoot.put("isFolder", true);
		objectRoot.put("nodeDocType", OBJECT_ROOT_DOCTYPE);
		objectRoot.put("title", OBJECT_ROOT_NAME);
		objectRoot.put("dojoType", NODE_DOJO_TYPE);
		objectRoot.put("nodeAppType", OBJECT_APPTYPE);
		objectRoot.put("id", OBJECT_ROOT);

		HashMap<String, Object> addressRoot = new HashMap<String, Object>();
		addressRoot.put("contextMenu", ROOT_MENU_ID);
		addressRoot.put("isFolder", true);
		addressRoot.put("nodeDocType", ADDRESS_ROOT_DOCTYPE);
		addressRoot.put("title", ADDRESS_ROOT_NAME);
		addressRoot.put("dojoType", NODE_DOJO_TYPE);
		addressRoot.put("nodeAppType", ADDRESS_APPTYPE);
		addressRoot.put("id", ADDRESS_ROOT);

		treeRoot.add(objectRoot);
		treeRoot.add(addressRoot);
		return treeRoot;
	}


	private static Map<String, Object> addTreeNodeObjectInfo(Map<String, Object> node)
	{
		// TODO Do this recursive for all children!
		node.put("contextMenu", NODE_MENU_ID);
		node.put("dojoType", NODE_DOJO_TYPE);
		node.put("nodeAppType", OBJECT_APPTYPE);
		
//		node.put("isFolder", hasChildren);
//		node.put("nodeDocType", "Class"+entityClass.toString());
//		node.put("title", name);
//		node.put("id", uuid);

		return node;
	}

	private static Map<String, Object> addTreeNodeAddressInfo(Map<String, Object> node)
	{
		node.put("contextMenu", NODE_MENU_ID);
		node.put("dojoType", NODE_DOJO_TYPE);
		node.put("nodeAppType", ADDRESS_APPTYPE);		
		return node;
	}
}
