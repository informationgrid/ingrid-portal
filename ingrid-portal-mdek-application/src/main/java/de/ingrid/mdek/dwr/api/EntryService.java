/**
 * 
 */
package de.ingrid.mdek.dwr.api;

import java.util.List;
import java.util.Map;

import de.ingrid.mdek.dwr.CatalogBean;
import de.ingrid.mdek.dwr.JobInfoBean;
import de.ingrid.mdek.dwr.MdekAddressBean;
import de.ingrid.mdek.dwr.MdekDataBean;

/**
 * @author joachim
 * 
 * This service describes methods that are used by the AJAX client to
 * communicate with the server application.
 * 
 * Responsibility: Object/Address Entry
 * 
 */
public interface EntryService {

	/**
	 * Returns a tree (sub) structure encapsulated in Lists of HashMaps. The
	 * data is based on the working copy of the nodes if one exists. Each node
	 * is represented by a HashMap which contains the following keys:
	 * 
	 * <ul>
	 * <li><b>(String) id</b> - uuid of the node</li>
	 * <li><b>(String) nodeAppType</b> - type of the node ('O' for Object or 'A' for Address or 'C' for catalog)</li>
	 * <li><b>(String) nodeDocType</b> possible values: "Class1...Class6" for UDK objects,
	 * "Institution|InstitutionUnit|InstitutionPerson|InstitutionUnit_B|InstitutionPerson_Q|PersonAddress_R" for UDK Adresses, 
	 * "Objects" for object root node, "Adresses" for address root node </li>
	 * <li><b>(String) title</b> - title of the node</li>
	 * <li><b>(String) isFolder</b> - "true" if node has children, null if not</li>
	 * <li><b>(String) contextMenu</b> - type of context menu to be displayed
	 * on the node: contextMenu1 for all data nodes, contextMenu2 for the two root nodes</li>
	 * <li><b>children</b> - list of children of the node</li>
	 * </ul>
	 * 
	 * The Method returns all children from a node (object or address) depending
	 * on its nodeUuid and its nodeType.
	 * 
	 * If nodeUuid is null all root nodes of the given nodeType are returned up
	 * until a defined depth. If nodeType is also null, all root node types
	 * (objects and addresses) are returned.
	 * 
	 * The parameter nodeType must be set together with nodeUuid to classify the
	 * type of the node (object or address).
	 * 
	 * @param nodeUuid
	 *            The uuid of the node (object or address). If null all root
	 *            nodes (objects and addresses) will be returned
	 * @param nodeType
	 *            The type of the nodes returned (must be set together with
	 *            uuid). 'o' stands for objects, 'a' for addresses.
	 * @param depth
	 *            The depth of the returned tree structure. Must be > 0.
	 * @return A List of HashMaps representing the tree.
	 */
	public List getSubTree(String nodeUuid, String nodeType, int depth)
			throws Exception;

	/**
	 * Returns partly opened tree structure encapsulated in Lists of HashMaps.
	 * This method is mainly used to 'jump' into the entry page, open a single
	 * node into the tree. The data is based on the working copy of the nodes if
	 * one exists. Each node is represented by a HashMap which contains the
	 * following keys:
	 * 
	 * <ul>
	 * <li><b>(String) id</b> - uuid of the node</li>
	 * <li><b>(String) nodeAppType</b> - type of the node ('O' for Object or 'A' for Address or 'C' for catalog)</li>
	 * <li><b>(String) nodeDocType</b> possible values: "Class1...Class6" for UDK objects,
	 * "Institution|InstitutionUnit|InstitutionPerson|InstitutionUnit_B|InstitutionPerson_Q|PersonAddress_R" for UDK Adresses, 
	 * "Objects" for object root node, "Adresses" for address root node </li>
	 * <li><b>(String) title</b> - title of the node</li>
	 * <li><b>(String) isFolder</b> - "true" if node has children, null if not</li>
	 * <li><b>(String) contextMenu</b> - type of context menu to be displayed
	 * on the node: contextMenu1 for all data nodes, contextMenu2 for the two root nodes</li>
	 * <li><b>children</b> - list of children of the node</li>
	 * </ul>
	 * 
	 * The Method returns a tree structure with a branch opened to a node
	 * specified by nodeUuid. For all parent nodes based on the node with nodeUuid
	 * all direct children are returned as well. The tree structure must be
	 * ready for display.
	 * 
	 * The parameter allRootTypes specifies if all root types are returned. If
	 * it is set to false only the root nodes of type nodeType are returned. Set
	 * to true all root nodes (type object and address) are returned.
	 * 
	 * 
	 * @param nodeUuid
	 *            The nodeUuid of the node to be opened. Must not be null.
	 * @param nodeType
	 *            The type of the node to open ('o' for object, 'a' for
	 *            address).
	 * @param allRootTypes
	 *            True to return all root nodes, regardless of their type,
	 *            False to return only root nodes of type nodeType.
	 * @return
	 * @throws Exception 
	 */
	public List getOpenTree(String nodeUuid, String nodeType,
			Boolean allRootTypes) throws Exception;

	/**
	 * Retrieves all relevant data of a node. Data is encapsulated in a MdekDataBean.
	 * 
	 * If useWorkingCopy is true, tries to get the working copy of the data. In
	 * case no working copy exists a new working copy will be created.
	 * 
	 * If useWorkingCopy is false, an existing workingCopy will be synchronized
	 * with the original data. In case no working copy exists, a new working
	 * copy will be created.
	 * 
	 * @param nodeUuid
	 *            The uuid of the node.
	 * @param nodeType
	 *            The type of the node ('o' for object, 'a' for address).
	 * @param useWorkingCopy
	 *            True to get the data based on the working copy, false to
	 *            synchronize the working copy with the original data.
	 * @return
	 */
	public MdekDataBean getNodeData(String nodeUuid, String nodeType,
			Boolean useWorkingCopy);

	/**
	 * Retrieves all relevant data of an address. Data is encapsulated in a MdekAddressBean.
	 * 
	 * If useWorkingCopy is true, tries to get the working copy of the data. In
	 * case no working copy exists a new working copy will be created.
	 * 
	 * If useWorkingCopy is false, an existing workingCopy will be synchronized
	 * with the original data. In case no working copy exists, a new working
	 * copy will be created.
	 * 
	 * @param nodeUuid
	 *            The uuid of the node.
	 * @param useWorkingCopy
	 *            True to get the data based on the working copy, false to
	 *            synchronize the working copy with the original data.
	 * @return
	 */
	public MdekAddressBean getAddressData(String nodeUuid, Boolean useWorkingCopy);

	/**
	 * Saves or updates the Object.
	 * 
	 * @param data
	 *            the nodes data.
	 * @param useWorkingCopy
	 *            If true saves data only to the working copy, if false save it
	 *            over the original data and remove the working copy.
	 * @return The stored node is returned with updated values (id, work state, etc.)
	 */
	public MdekDataBean saveNodeData(MdekDataBean data, Boolean useWorkingCopy, Boolean forcePublicationCondition);

	/**
	 * Copy a node. The parameter includeChildren specifies if the children of
	 * the specified node should be copied as well. The method copies the
	 * original data and the working copy.
	 * 
	 * @param nodeUuid
	 *            The uuid of the node to copy.
	 * @param dstNodeUuid
	 *            The destination nodes uuid (this will become the parent of the
	 *            copied node).
	 * @param includeChildren
	 *            true to copy all children of the specified node, false to copy
	 *            only the node.
	 * @return Basic information about the copy is returned or null on error
	 */
	public Map<String, Object> copyNode(String nodeUuid, String dstNodeUuid,
			Boolean includeChildren);

	/**
	 * Move a node. All children of the specified node are moved as well. This
	 * method moves the original data and the working copy.
	 * 
	 * @param nodeUuid
	 *            The uuid of the node to move.
	 * @param dstNodeUuid
	 *            The destination nodes uuid (this will become the parent of the
	 *            copied node).
	 * @param forcePublicationCondition
	 *            Tells the backend to force a move operation and modify child nodes
     *
	 */
	public void moveNode(String nodeUuid, String dstNodeUuid, boolean forcePublicationCondition);

	/**
	 * Delete a node. Deletion is only successful, if the node has no children.
	 * This method deletes the original data and the working copy. Marks the
	 * working copy as deleted if markOnly is set true.
	 * 
	 * @param nodeUuid
	 *            The uuid of the node to delete.
	 * @param markOnly
	 *            If set true the working copy of the node will only be marked
	 *            for deletion.
	 * @return 'success' or error message.
	 */
	public String deleteNode(String nodeUuid, Boolean markOnly);
	public MdekDataBean deleteObjectWorkingCopy(String nodeUuid, Boolean markOnly);

	public MdekDataBean createNewNode(String parentUuid);
	
	// Returns a list containing all the node ids from the root object to the node with id 'targetUuid'
	public List<String> getPathToObject(String targetUuid);

	// Queries if a node can be cut
	public boolean canCutObject(String parentUuid);

	// Queries if a node can be copied
	public boolean canCopyObject(String parentUuid);

	// Fetch sys lists from the backend
	public Map<Integer, List<String[]>> getSysLists(Integer[] listIds, Integer languageCode);

	// Fetch Catalog Data
	public CatalogBean getCatalogData();

	// Fetch info about a running job
	public JobInfoBean getRunningJobInfo();

	// Cancel a running job
	public JobInfoBean cancelRunningJob();
}