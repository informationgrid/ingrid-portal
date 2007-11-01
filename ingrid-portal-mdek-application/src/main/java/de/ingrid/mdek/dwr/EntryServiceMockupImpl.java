/**
 * 
 */
package de.ingrid.mdek.dwr;

import java.util.HashMap;
import java.util.List;

import de.ingrid.mdek.dwr.api.EntryService;

/**
 * @author joachim
 *
 */
public class EntryServiceMockupImpl implements EntryService {

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
	public List getSubTree(String nodeUuid, String nodeType, int depth) {
		return null;
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
