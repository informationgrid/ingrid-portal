/**
 * 
 */
package de.ingrid.mdek.dwr;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;

import com.thoughtworks.xstream.XStream;

import de.ingrid.mdek.dwr.api.EntryService;


/**
 * @author joachim
 * 
 */
public class EntryServiceMockupImpl implements EntryService {

	private XStream xstream;
	private List dummyDataList;

	public EntryServiceMockupImpl() {
		xstream = new XStream();
		try {
			// Create the SessionFactory
			InputStream resourceAsStream = EntryServiceMockupImpl.class
					.getResourceAsStream("tree_dummy_data.xml");
			if (resourceAsStream == null) {
				resourceAsStream = EntryServiceMockupImpl.class
						.getClassLoader().getResourceAsStream(
								"tree_dummy_data.xml");
			}
			dummyDataList = (List) xstream.fromXML(resourceAsStream);
		} catch (Throwable ex) {
			throw new ExceptionInInitializerError(ex);
		}
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
	public HashMap getNodeData(String nodeUuid, String nodeType,
			Boolean useWorkingCopy) {

		HashMap<String, String> map = new HashMap<String, String>();
		HashMap node = findNodeInTree(dummyDataList, nodeUuid);
		if (node == null)
		{
			map.put("id", nodeUuid);
			map.put("nodeAppType", nodeType);
			return map;
		}

		for (Map.Entry<String, Object> element : (Set<Map.Entry<String, Object>>) node.entrySet()) {
			String key = element.getKey();
			Object val = element.getValue();

//			System.out.println("Value: "+element.getValue());
//			System.out.println("Key: "+element.getKey());				
			
			if (val instanceof String)
			{
				map.put(key, (String) val);
			}
		}

		return map;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.ingrid.mdek.dwr.api.EntryService#getOpenTree(java.lang.String,
	 *      java.lang.String, java.lang.Boolean)
	 */
	public List getOpenTree(String nodeUuid, String nodeType,
			Boolean allRootTypes) throws Exception {
		HashMap node = findNodeInTree(dummyDataList, nodeUuid);
		if (node == null) {
			throw new Exception("Node (" + nodeUuid + ") not found!");
		}
		
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

		if (nodeUuid!=null && nodeType == null) {
			throw new IllegalArgumentException("Wrong arguments on method getSubTree(): nodeType must be set if nodeUuid is set!");
		}

		List list = getChildren( nodeUuid, nodeType, depth, 0);
		if (list != null) {
			return list;
		} else {
			throw new IllegalArgumentException("invalid parameters");
		}
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
	public String saveNodeData(HashMap data, Boolean useWorkingCopy) {
		// TODO Auto-generated method stub
		return null;
	}

	private List getChildren(String id, String type, int depth, int level) {
		int internalLevel = level + 1;
		List srcList = null;
		if (id == null && type == null) {
			srcList = dummyDataList;
		} else {
			HashMap srcNode = findNodeInTree(dummyDataList, id);
			srcList = (List)srcNode.get("children");
		}
		if (srcList == null) {
			return null;
		}
		List dstList = new ArrayList();
		for (int i = 0; i < srcList.size(); i++) {
			HashMap hash = new HashMap();
			hash.putAll((HashMap) srcList.get(i));
			if (hash.get("children") != null) {
				hash.put("isFolder", "true");
				hash.remove("children");
			}
			hash.put("title", StringEscapeUtils.escapeHtml(((String)hash.get("title")).trim()));
			if (internalLevel < depth) {
				hash.put("children", getChildren((String)hash.get("id"), type, depth, internalLevel));
			}
			dstList.add(hash);
		}
		return dstList;
	}

	private HashMap findNodeInTree(List list, String id) {
		HashMap node;
		for (int i = 0; i < list.size(); i++) {
			node = (HashMap) list.get(i);
			if (node.get("id").equals(id)) {
				return node;
			} else if (node.get("children") != null) {
				node = findNodeInTree((List) node.get("children"), id);
				if (node != null) {
					return node;
				}
			}
		}
		return null;
	}
}
