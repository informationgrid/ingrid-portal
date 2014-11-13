/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.mdek.dwr.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import de.ingrid.mdek.beans.TreeNodeBean;
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
	private final static String OBJECT_ROOT_NAME = "general.objects";

	private final static String OBJECT_ROOT_DOCTYPE = "Objects";
	private final static String OBJECT_APPTYPE = "O";


	private final static String ADDRESS_ROOT = "addressRoot"; 
	private final static String ADDRESS_FREE_ROOT = "addressFreeRoot"; 
	private final static String ADDRESS_ROOT_NAME = "general.addresses";
	private final static String ADDRESS_FREE_ROOT_NAME = "general.addresses.free";
	private final static String ADDRESS_ROOT_DOCTYPE = "Addresses";
	private final static String ADDRESS_APPTYPE = "A";

	public List<TreeNodeBean> getSubTree(String nodeUuid, String nodeType, String language) {
		if (nodeUuid != null && nodeType == null) {
			throw new IllegalArgumentException("Wrong arguments on method getSubTree(): nodeType must be set if nodeUuid is set!");
		}
		
		if (nodeUuid == null) {
			return createTree(language);
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
				subAddresses.add(0, createFreeAddressRoot(language));

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
	
	public List<TreeNodeBean> getAllSubTreeChildren(String nodeUuid, String nodeType, String language) {
	    List<TreeNodeBean> allChildren = new ArrayList<TreeNodeBean>();
	    List<TreeNodeBean> children = getSubTree(nodeUuid, nodeType, language);
	    for (TreeNodeBean child : children) {
	        allChildren.addAll(getAllSubTreeChildren(child.getId(), child.getNodeAppType(), language));
        }
	    allChildren.addAll(children);
	    return allChildren;
	}

	
	private static List<TreeNodeBean> createTree(String language)
	{
		ResourceBundle res = ResourceBundle.getBundle("messages", new Locale(language));
		List<TreeNodeBean> treeRoot = new ArrayList<TreeNodeBean>(); 

		TreeNodeBean objectRoot = new TreeNodeBean();
		objectRoot.setIsFolder(true);
		objectRoot.setNodeDocType(OBJECT_ROOT_DOCTYPE);
		objectRoot.setTitle(res.getString(OBJECT_ROOT_NAME));
		objectRoot.setNodeAppType(OBJECT_APPTYPE);
		objectRoot.setId(OBJECT_ROOT);

		TreeNodeBean addressRoot = new TreeNodeBean();
		addressRoot.setIsFolder(true);
		addressRoot.setNodeDocType(ADDRESS_ROOT_DOCTYPE);
		addressRoot.setTitle(res.getString(ADDRESS_ROOT_NAME));
		addressRoot.setNodeAppType(ADDRESS_APPTYPE);
		addressRoot.setId(ADDRESS_ROOT);

		treeRoot.add(objectRoot);
		treeRoot.add(addressRoot);
		return treeRoot;
	}

	public static void addTreeNodeObjectInfo(TreeNodeBean node) {
		node.setNodeAppType(OBJECT_APPTYPE);		
	}

	public static void addTreeNodeAddressInfo(TreeNodeBean node) {
		node.setNodeAppType(ADDRESS_APPTYPE);		
	}

	private static TreeNodeBean createFreeAddressRoot(String language)
	{
		ResourceBundle res = ResourceBundle.getBundle("messages", new Locale(language));
		TreeNodeBean freeAddressRoot = new TreeNodeBean(); 

		freeAddressRoot.setIsFolder(true);
		freeAddressRoot.setNodeDocType(ADDRESS_ROOT_DOCTYPE);
		freeAddressRoot.setTitle(res.getString(ADDRESS_FREE_ROOT_NAME));
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
}
