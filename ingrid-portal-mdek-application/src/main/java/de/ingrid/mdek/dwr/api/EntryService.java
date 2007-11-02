/**
 * 
 */
package de.ingrid.mdek.dwr.api;

import java.util.HashMap;
import java.util.List;

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
	 * <li><b>(String) uuid</b> - uuid of the node</li>
	 * <li><b>(String) type</b> - type of the node ('o' for Object or 'a' for
	 * Address)</li>
	 * <li><b>(String) class</b> - class of the object (UDK class for Objects,
	 * type for Adresses)</li>
	 * <li><b>(String) title</b> - title of the node</li>
	 * <li><b>(Boolean) hasChildren</b> - node has children</li>
	 * <li><b>children</b> - list of children of the node</li>
	 * </ul>
	 * 
	 * The Method returns all children from a node (object or address) depending
	 * in its uuid and its type.
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
	public List getSubTree(String nodeUuid, String nodeType, int depth) throws Exception;

	/**
	 * Returns partly opened tree structure encapsulated in Lists of HashMaps.
	 * This method is mainly used to 'jump' into the entry page, open a single
	 * node into the tree. The data is based on the working copy of the nodes if
	 * one exists. Each node is represented by a HashMap which contains the
	 * following keys:
	 * 
	 * <ul>
	 * <li><b>(String) uuid</b> - uuid of the node</li>
	 * <li><b>(String) type</b> - type of the node ('o' for Object or 'a' for
	 * Address)</li>
	 * <li><b>(String) class</b> - class of the object (UDK class for Objects,
	 * type for Adresses)</li>
	 * <li><b>(String) title</b> - title of the node</li>
	 * <li><b>(Boolean) isOpen</b> - node is open</li>
	 * <li><b>(Boolean) hasChildren</b> - node has children</li>
	 * <li><b>children</b> - list of children of the node</li>
	 * </ul>
	 * 
	 * The Method returns a tree structure with a branch opened to an node
	 * specified by uuid. For all parent nodes based on the node with nodeUuid
	 * all direct children are returned as well. The tree structure must be
	 * ready for display.
	 * 
	 * The parameter allRootTypes specifies if all root types are returned. If
	 * it is set to false only the root nodes of type nodeType are returned. Set
	 * to true all root nodes (type object and address) are returned.
	 * 
	 * 
	 * @param nodeUuid
	 *            The uuid of the node to be opened. Must not be null.
	 * @param nodeType
	 *            The type of the node to open ('o' for object, 'a' for
	 *            address).
	 * @param allRootTypes
	 *            True to return alle root nodes, regardless of their type,
	 *            False to return only root nodes of type nodeType.
	 * @return
	 */
	public List getOpenTree(String nodeUuid, String nodeType, Boolean allRootTypes);

	/**
	 * Retrieves all relevant data of a node. Data is encapsulated in a HashMap.
	 * Sub lists are encapsulated in ArrayLists inside the HashMap.
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
	public HashMap getNodeData(String nodeUuid, String nodeType, Boolean useWorkingCopy);

	/**
	 * Saves or updates the Object.
	 * 
	 * @param data
	 *            the nodes data.
	 * @param useWorkingCopy
	 *            If true saves data only to the working copy, if false save it
	 *            over the original data and remove the working copy.
	 * @return 'success' or error message.
	 */
	public String saveNodeData(HashMap data, Boolean useWorkingCopy);

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
	 * @return 'success' or error message.
	 */
	public String copyNode(String nodeUuid, String dstNodeUuid, Boolean includeChildren);

	/**
	 * Move a node. All children of the specified node are moved as well. This
	 * method moves the original data and the working copy.
	 * 
	 * @param nodeUuid
	 *            The uuid of the node to move.
	 * @param dstNodeUuid
	 *            The destination nodes uuid (this will become the parent of the
	 *            copied node).
	 * @return 'success' or error message.
	 */
	public String moveNode(String nodeUuid, String dstNodeUuid);

	/**
	 * Delete a node. Deletion is only successful, if the node has no children.
	 * This method deletes the original data and the working copy of markes the
	 * working copy as deleted if markOnly is set true.
	 * 
	 * @param nodeUuid
	 *            The uuid of the node to delete.
	 * @param markOnly
	 *            If set true the working copy of the node will only be marked for deletion.
	 * @return 'success' or error message.
	 */
	public String deleteNode(String nodeUuid, Boolean markOnly);

}
