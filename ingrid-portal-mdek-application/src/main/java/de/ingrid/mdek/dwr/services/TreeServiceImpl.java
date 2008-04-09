package de.ingrid.mdek.dwr.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.ingrid.mdek.handler.AddressRequestHandler;
import de.ingrid.mdek.handler.ObjectRequestHandler;

public class TreeServiceImpl {

	private final static Logger log = Logger.getLogger(TreeServiceImpl.class);	

	// Injected by Spring
	private ObjectRequestHandler objectRequestHandler;
	private AddressRequestHandler addressRequestHandler;


	// OBJECT_ROOT specifies the uuid for the object root node. 
	private final static String OBJECT_ROOT = "objectRoot"; 
	// TODO Load from a cfg file -> localization 
	private final static String OBJECT_ROOT_NAME = "Objekte";

	private final static String OBJECT_ROOT_DOCTYPE = "Objects";
	private final static String ROOT_MENU_ID = "contextMenu2";
	private final static String NODE_MENU_ID = "contextMenu1";
	private final static String NODE_DOJO_TYPE = "ingrid:TreeNode";
	private final static String OBJECT_APPTYPE = "O";


	private final static String ADDRESS_ROOT = "addressRoot"; 
	private final static String ADDRESS_FREE_ROOT = "addressFreeRoot"; 
	private final static String ADDRESS_ROOT_NAME = "Adressen";
	private final static String ADDRESS_FREE_ROOT_NAME = "Freie Adressen";
	private final static String ADDRESS_ROOT_DOCTYPE = "Addresses";
	private final static String ADDRESS_APPTYPE = "A";

	
	public ArrayList<HashMap<String, Object>> getSubTree(String nodeUuid, String nodeType, int depth) {
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
				subObjects = objectRequestHandler.getRootObjects();
			else
				subObjects = objectRequestHandler.getSubObjects(nodeUuid, depth);
	
			for (HashMap<String, Object> node : subObjects) {
				addTreeNodeObjectInfo(node);
			}
			return subObjects;

		} else if (nodeType.equals(ADDRESS_APPTYPE)) {
			ArrayList<HashMap<String, Object>> subAddresses = null; 
			if (nodeUuid.equalsIgnoreCase(ADDRESS_ROOT)) {
				subAddresses = addressRequestHandler.getRootAddresses(false);
				for (HashMap<String, Object> node : subAddresses) {
					addTreeNodeAddressInfo(node);
				}
				subAddresses.add(0, createFreeAddressRoot());

			} else if (nodeUuid.equalsIgnoreCase(ADDRESS_FREE_ROOT)) {
				subAddresses = addressRequestHandler.getRootAddresses(true);				
				for (HashMap<String, Object> node : subAddresses) {
					addTreeNodeAddressInfo(node);
				}

			} else {
				subAddresses = addressRequestHandler.getSubAddresses(nodeUuid, depth);

				for (HashMap<String, Object> node : subAddresses) {
					addTreeNodeAddressInfo(node);
				}
			}

			return subAddresses;

		} else {
			throw new IllegalArgumentException("Unknown node type: "+nodeType); 
		}
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

	public static Map<String, Object> addTreeNodeObjectInfo(Map<String, Object> node)
	{
		// TODO Do this recursive for all children!
		node.put("contextMenu", NODE_MENU_ID);
		node.put("dojoType", NODE_DOJO_TYPE);
		node.put("nodeAppType", OBJECT_APPTYPE);		
		return node;
	}

	public static Map<String, Object> addTreeNodeAddressInfo(Map<String, Object> node)
	{
		node.put("contextMenu", NODE_MENU_ID);
		node.put("dojoType", NODE_DOJO_TYPE);
		node.put("nodeAppType", ADDRESS_APPTYPE);		
		return node;
	}

	private static HashMap<String, Object> createFreeAddressRoot()
	{
		HashMap<String, Object> freeAddressRoot = new HashMap<String, Object>(); 

		freeAddressRoot.put("contextMenu", ROOT_MENU_ID);
		freeAddressRoot.put("isFolder", true);
		freeAddressRoot.put("nodeDocType", ADDRESS_ROOT_DOCTYPE);
		freeAddressRoot.put("title", ADDRESS_FREE_ROOT_NAME);
		freeAddressRoot.put("dojoType", NODE_DOJO_TYPE);
		freeAddressRoot.put("nodeAppType", ADDRESS_APPTYPE);
		freeAddressRoot.put("id", ADDRESS_FREE_ROOT);

		return freeAddressRoot;
	}

	public ObjectRequestHandler getObjectRequestHandler() {
		return objectRequestHandler;
	}


	public void setObjectRequestHandler(ObjectRequestHandler objectRequestHandler) {
		this.objectRequestHandler = objectRequestHandler;
	}


	public AddressRequestHandler getAddressRequestHandler() {
		return addressRequestHandler;
	}


	public void setAddressRequestHandler(AddressRequestHandler addressRequestHandler) {
		this.addressRequestHandler = addressRequestHandler;
	}
}
