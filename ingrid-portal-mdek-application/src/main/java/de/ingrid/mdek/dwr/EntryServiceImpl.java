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
	public String deleteNode(String nodeUuid, Boolean markOnly) {
		// TODO Auto-generated method stub
		return null;
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
			data = dataConnection.getNodeDetail(nodeUuid);
		} catch (Exception e) {e.printStackTrace();}

		// TODO check for errors and throw an exception?
		if (data == null) {
			data = new MdekDataBean();
		
//			data.setId(nodeUuid);
			data.setUuid(nodeUuid);
			data.setNodeDocType("Class1");
			data.setNodeAppType(nodeType);

			ArrayList<MdekAddressBean> addressTable = new ArrayList<MdekAddressBean>(); 
			MdekAddressBean entry1 = new MdekAddressBean();
			MdekAddressBean entry2 = new MdekAddressBean();
			entry1.setId(new Long(0));
			entry1.setUuid("0");
			entry1.setInformation("Auswertung");
			entry1.setIcon("<img src=\"img/UDK/addr_institution.gif\" width=\"16\" height=\"16\" alt=\"Institution\" />");
			entry1.setName("<a href=\"#\" title=\"Adresse &ouml;ffnen: Gerdes, G&uuml;nther\">Gerdes, G&uuml;nther</a>");
			addressTable.add(entry1);

			entry2.setId(new Long(1));
			entry1.setUuid("1");
			entry2.setInformation("Auskunft");
			entry2.setIcon("<img src=\"img/UDK/addr_institution.gif\" width=\"16\" height=\"16\" alt=\"Institution\" />");
			entry2.setName("<a href=\"#\" title=\"Adresse &ouml;ffnen: Altm&uuml;ller, Reinhard\">Altm&uuml;ller, Reinhard</a>");
			addressTable.add(entry2);

			data.setGeneralAddressTable(addressTable);
			return data;
		}

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
	public String saveNodeData(MdekDataBean data, Boolean useWorkingCopy) {
		log.debug("Saving node with ID: "+data.getId());
		try
		{
		  dataConnection.saveNode(data);
		}
		catch (Exception e) {e.printStackTrace(); return e.toString();};
		
		return "success";
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
