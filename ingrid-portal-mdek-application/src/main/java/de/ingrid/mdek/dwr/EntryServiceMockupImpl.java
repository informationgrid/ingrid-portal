/**
 * 
 */
package de.ingrid.mdek.dwr;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
            InputStream resourceAsStream = EntryServiceMockupImpl.class.getResourceAsStream("tree_dummy_data.xml");
            if (resourceAsStream == null) {
                resourceAsStream = EntryServiceMockupImpl.class.getClassLoader().getResourceAsStream("tree_dummy_data.xml");
            }
            dummyDataList = (List)xstream.fromXML(resourceAsStream);
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
	}
	
	/* (non-Javadoc)
	 * @see de.ingrid.mdek.dwr.api.EntryService#copyNode(java.lang.String, java.lang.String, java.lang.Boolean)
	 */
	public String copyNode(String nodeUuid, String dstNodeUuid, Boolean includeChildren) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see de.ingrid.mdek.dwr.api.EntryService#deleteNode(java.lang.String, java.lang.Boolean)
	 */
	public String deleteNode(String nodeUuid, Boolean markOnly) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see de.ingrid.mdek.dwr.api.EntryService#getNodeData(java.lang.String, java.lang.String, java.lang.Boolean)
	 */
	public HashMap getNodeData(String nodeUuid, String nodeType, Boolean useWorkingCopy) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see de.ingrid.mdek.dwr.api.EntryService#getOpenTree(java.lang.String, java.lang.String, java.lang.Boolean)
	 */
	public List getOpenTree(String nodeUuid, String nodeType, Boolean allRootTypes) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see de.ingrid.mdek.dwr.api.EntryService#getSubTree(java.lang.String, java.lang.String, int)
	 */
	public List getSubTree(String nodeUuid, String nodeType, int depth) throws Exception {

		if (nodeUuid == null && nodeType == null) {
			ArrayList list = new ArrayList(2);
			HashMap o = new HashMap();
			o.put("id", "objectRoot");
			o.put("title", "Objekte");
			o.put("dojoType", "ingrid:TreeNode");
			o.put("contextMenu", "contextMenu1");
			o.put("nodeDocType", "Objects");
			o.put("nodeAppType", "Objekt");
			o.put("isFolder", "true");
			list.add(o);

			o = new HashMap();
			o.put("id", "addressRoot");
			o.put("title", "Adressen");
			o.put("dojoType", "ingrid:TreeNode");
			o.put("contextMenu", "contextMenu2");
			o.put("nodeDocType", "Addresses");
			o.put("nodeAppType", "Adresse");
			o.put("isFolder", "true");
			list.add(o);
			xstream.toXML(list);
			return list;
		} else if (nodeUuid.equals("objectRoot")) {
			ArrayList list = new ArrayList(2);
			HashMap o = new HashMap();

			o = new HashMap();
			o.put("id", "o1");
			o.put("title", "Test Objekt 1");
			o.put("dojoType", "ingrid:TreeNode");
			o.put("contextMenu", "contextMenu2");
			o.put("nodeDocType", "Class1");
			o.put("nodeAppType", "Objekt");
			list.add(o);
			o = new HashMap();
			o.put("id", "o11");
			o.put("title", "Test Objekt 1.1");
			o.put("dojoType", "ingrid:TreeNode");
			o.put("contextMenu", "contextMenu2");
			o.put("nodeDocType", "Class3");
			o.put("nodeAppType", "Objekt");
			list.add(o);
			
			return list;
		} else if (nodeUuid.equals("addressRoot")) {
			ArrayList list = new ArrayList(2);
			HashMap o = new HashMap();
			o = new HashMap();
			o.put("id", "a1");
			o.put("title", "Test Adresse 1");
			o.put("dojoType", "ingrid:TreeNode");
			o.put("contextMenu", "contextMenu2");
			o.put("nodeDocType", "Class3");
			o.put("nodeAppType", "Adresse");
			o.put("isFolder", "true");
			list.add(o);
			return list;
		} else {
			throw new IllegalArgumentException("invalid parameters");
		}

	}

	/* (non-Javadoc)
	 * @see de.ingrid.mdek.dwr.api.EntryService#moveNode(java.lang.String, java.lang.String)
	 */
	public String moveNode(String nodeUuid, String dstNodeUuid) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see de.ingrid.mdek.dwr.api.EntryService#saveNodeData(java.util.HashMap, java.lang.Boolean)
	 */
	public String saveNodeData(HashMap data, Boolean useWorkingCopy) {
		// TODO Auto-generated method stub
		return null;
	}

}
