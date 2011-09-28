package de.ingrid.mdek.dwr.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import de.ingrid.mdek.beans.TreeNodeBean;
import de.ingrid.mdek.beans.object.MdekDataBean;
import de.ingrid.mdek.handler.AddressRequestHandler;
import de.ingrid.mdek.handler.ObjectRequestHandler;
import de.ingrid.mdek.util.MdekObjectUtils;
import de.ingrid.utils.udk.UtilsLanguageCodelist;

public class TreeServiceImpl {

	private final static Logger log = Logger.getLogger(TreeServiceImpl.class);	

	// Injected by Spring
	private ObjectRequestHandler objectRequestHandler;
	private AddressRequestHandler addressRequestHandler;

	// OBJECT_ROOT specifies the uuid for the object root node. 
	private final static String OBJECT_ROOT = "objectRoot"; 
	// TODO Load from a cfg file -> localization 
	private final static String OBJECT_ROOT_NAME = "general.objects";

	private final static String OBJECT_ROOT_DOCTYPE = "Objects";
	private final static String ROOT_MENU_ID = "contextMenu2";
	private final static String NODE_MENU_ID = "contextMenu1";
	private final static String NODE_DOJO_TYPE = "ingrid:TreeNode";
	private final static String OBJECT_APPTYPE = "O";


	private final static String ADDRESS_ROOT = "addressRoot"; 
	private final static String ADDRESS_FREE_ROOT = "addressFreeRoot"; 
	private final static String ADDRESS_ROOT_NAME = "general.addresses";
	private final static String ADDRESS_FREE_ROOT_NAME = "general.addresses.free";
	private final static String ADDRESS_ROOT_DOCTYPE = "Addresses";
	private final static String ADDRESS_APPTYPE = "A";

	private static Locale loc;
	
	public List<TreeNodeBean> getSubTree(String nodeUuid, String nodeType) {
		if (nodeUuid != null && nodeType == null) {
			throw new IllegalArgumentException("Wrong arguments on method getSubTree(): nodeType must be set if nodeUuid is set!");
		}
		
		if(loc == null){
			loc = getCatalogLocale();
		}

		if (nodeUuid == null) {
			return createTree();
		}


		if (nodeType.equals(OBJECT_APPTYPE)) {
			List<TreeNodeBean> subObjects = null; 
			
			if (nodeUuid.equalsIgnoreCase(OBJECT_ROOT))
				subObjects = objectRequestHandler.getRootObjects();
			else
				subObjects = objectRequestHandler.getSubObjects(nodeUuid);
	
			for (TreeNodeBean node : subObjects) {
				addTreeNodeObjectInfo(node);
			}
			return subObjects;

		} else if (nodeType.equals(ADDRESS_APPTYPE)) {
			List<TreeNodeBean> subAddresses = null; 
			if (nodeUuid.equalsIgnoreCase(ADDRESS_ROOT)) {
				subAddresses = addressRequestHandler.getRootAddresses(false);
				for (TreeNodeBean node : subAddresses) {
					addTreeNodeAddressInfo(node);
				}
				subAddresses.add(0, createFreeAddressRoot());

			} else if (nodeUuid.equalsIgnoreCase(ADDRESS_FREE_ROOT)) {
				subAddresses = addressRequestHandler.getRootAddresses(true);				
				for (TreeNodeBean node : subAddresses) {
					addTreeNodeAddressInfo(node);
				}

			} else {
				subAddresses = addressRequestHandler.getSubAddresses(nodeUuid);

				for (TreeNodeBean node : subAddresses) {
					addTreeNodeAddressInfo(node);
				}
			}

			return subAddresses;

		} else {
			throw new IllegalArgumentException("Unknown node type: "+nodeType); 
		}
	}
	
	public List<TreeNodeBean> getAllSubTreeChildren(String nodeUuid, String nodeType) {
	    List<TreeNodeBean> allChildren = new ArrayList<TreeNodeBean>();
	    List<TreeNodeBean> children = getSubTree(nodeUuid, nodeType);
	    for (TreeNodeBean child : children) {
	        allChildren.addAll(getAllSubTreeChildren(child.getId(), child.getNodeAppType()));
        }
	    allChildren.addAll(children);
	    return allChildren;
	}

	
	private static List<TreeNodeBean> createTree()
	{
		ResourceBundle res = ResourceBundle.getBundle("messages", loc);
		List<TreeNodeBean> treeRoot = new ArrayList<TreeNodeBean>(); 

		TreeNodeBean objectRoot = new TreeNodeBean();
		objectRoot.setContextMenu(ROOT_MENU_ID);
		objectRoot.setIsFolder(true);
		objectRoot.setNodeDocType(OBJECT_ROOT_DOCTYPE);
		objectRoot.setTitle(res.getString(OBJECT_ROOT_NAME));
		objectRoot.setDojoType(NODE_DOJO_TYPE);
		objectRoot.setNodeAppType(OBJECT_APPTYPE);
		objectRoot.setId(OBJECT_ROOT);

		TreeNodeBean addressRoot = new TreeNodeBean();
		addressRoot.setContextMenu(ROOT_MENU_ID);
		addressRoot.setIsFolder(true);
		addressRoot.setNodeDocType(ADDRESS_ROOT_DOCTYPE);
		addressRoot.setTitle(res.getString(ADDRESS_ROOT_NAME));
		addressRoot.setDojoType(NODE_DOJO_TYPE);
		addressRoot.setNodeAppType(ADDRESS_APPTYPE);
		addressRoot.setId(ADDRESS_ROOT);

		treeRoot.add(objectRoot);
		treeRoot.add(addressRoot);
		return treeRoot;
	}

	public static void addTreeNodeObjectInfo(TreeNodeBean node) {
		node.setContextMenu(NODE_MENU_ID);
		node.setDojoType(NODE_DOJO_TYPE);
		node.setNodeAppType(OBJECT_APPTYPE);		
	}

	public static void addTreeNodeAddressInfo(TreeNodeBean node) {
		node.setContextMenu(NODE_MENU_ID);
		node.setDojoType(NODE_DOJO_TYPE);
		node.setNodeAppType(ADDRESS_APPTYPE);		
	}

	private static TreeNodeBean createFreeAddressRoot()
	{
		ResourceBundle res = ResourceBundle.getBundle("messages", loc);
		TreeNodeBean freeAddressRoot = new TreeNodeBean(); 

		freeAddressRoot.setContextMenu(ROOT_MENU_ID);
		freeAddressRoot.setIsFolder(true);
		freeAddressRoot.setNodeDocType(ADDRESS_ROOT_DOCTYPE);
		freeAddressRoot.setTitle(res.getString(ADDRESS_FREE_ROOT_NAME));
		freeAddressRoot.setDojoType(NODE_DOJO_TYPE);
		freeAddressRoot.setNodeAppType(ADDRESS_APPTYPE);
		freeAddressRoot.setId(ADDRESS_FREE_ROOT);

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
	
	
	/** Fetches locale NOT from catalog, instead from default language in language syslist !
	 * If NO default language set, set "en" ! */
	private Locale getCatalogLocale(){
		MdekDataBean data = null; 		
		try {
			data = objectRequestHandler.getInitialObject(null);
		} catch (RuntimeException e) {
			log.debug("Error while getting node data.", e);
			throw e;
		}
		MdekObjectUtils.setInitialValues(data);
		Integer languageCode = data.getExtraInfoLangDataCode();
		
		if (languageCode != null) {
			if (languageCode.compareTo(UtilsLanguageCodelist.getCodeFromShortcut("en")) == 0)
				return new Locale("en");
			else if (languageCode.compareTo(UtilsLanguageCodelist.getCodeFromShortcut("de")) == 0)
				return new Locale("de");
		}
		
		log.warn("Language ("+languageCode+") not supported! Using 'en' as default!");
		return new Locale("en");
	}
}
