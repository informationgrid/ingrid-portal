/**
 * 
 */
package de.ingrid.mdek.dwr;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

import de.ingrid.mdek.dwr.api.EntryService;

import de.ingrid.mdek.DataConnectionInterface;
import de.ingrid.mdek.DataMapperInterface;
import de.ingrid.mdek.IMdekCaller;
import de.ingrid.mdek.MdekCaller;
import de.ingrid.mdek.MdekKeys;
import de.ingrid.mdek.SimpleMdekMapper;
import de.ingrid.utils.IngridDocument;


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
	private final static String ROOT_MENU_ID = "contextMenu2";
	private final static String NODE_MENU_ID = "contextMenu1";
	private final static String NODE_DOJO_TYPE = "ingrid:TreeNode";
	private final static String OBJECT_APPTYPE = "O";


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
	public String copyNode(String nodeUuid, String dstNodeUuid,
			Boolean includeChildren) {
		// TODO Auto-generated method stub
		return null;
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
	 * @see de.ingrid.mdek.dwr.api.EntryService#getNodeData(java.lang.String,
	 *      java.lang.String, java.lang.Boolean)
	 */
	public MdekDataBean getNodeData(String nodeUuid, String nodeType,
			Boolean useWorkingCopy) {
		
		MdekDataBean data = null; 

		if (nodeUuid.equalsIgnoreCase("newNode")) {
			log.debug("--- New node ---");		
			data = new MdekDataBean();
			data.setNodeAppType(OBJECT_APPTYPE);
			data.setNodeDocType("Class0");
			data.setObjectClass(0);
			data.setUuid(nodeUuid); // "newNode"
		} else {
			try {
				data = dataConnection.getNodeDetail(nodeUuid);
			} catch (Exception e) {e.printStackTrace();}
	
			// TODO check for errors and throw an exception?
			// Return a newly created node
			if (data == null) {
			}
		}

		return data;
	}

	
	public MdekDataBean createNewNode(String parentUuid) {
		log.debug("--- New node ---");		
		MdekDataBean data = new MdekDataBean(); 
		data.setTitle("Neues Datenobjekt");
		data.setObjectName("Neues Datenobjekt");
		data.setNodeAppType(OBJECT_APPTYPE);
		data.setNodeDocType("Class0");
		data.setObjectClass(0);
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

		ArrayList<HashMap<String, Object>> subObjects = null; 
		
		if (nodeUuid.equalsIgnoreCase(OBJECT_ROOT))
			subObjects = dataConnection.getRootObjects();
		else
			subObjects = dataConnection.getSubObjects(nodeUuid, depth);

		for (HashMap<String, Object> node : subObjects) {
			addTreeNodeInfo(node);
		}

		return subObjects;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.ingrid.mdek.dwr.api.EntryService#moveNode(java.lang.String,
	 *      java.lang.String)
	 */
	public String moveNode(String nodeUuid, String dstNodeUuid) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.ingrid.mdek.dwr.api.EntryService#saveNodeData(java.util.HashMap,
	 *      java.lang.Boolean)
	 */
	public MdekDataBean saveNodeData(MdekDataBean data, Boolean useWorkingCopy) {
		log.debug("Saving node with ID: "+data.getUuid());

		try {
		  return dataConnection.saveNode(data);
		}
		catch (Exception e) {e.printStackTrace();};

		return null;
	}



	
	
// ------------------------ Helper Methods ---------------------------------------	
	
	
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

		treeRoot.add(objectRoot);
		return treeRoot;
	}


	private static HashMap<String, Object> addTreeNodeInfo(HashMap<String, Object> node)
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

}
