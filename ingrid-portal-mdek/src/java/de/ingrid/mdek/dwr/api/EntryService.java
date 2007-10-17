/**
 * 
 */
package de.ingrid.mdek.dwr.api;

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
	 * Returns a tree (sub) structure encapsulated in Lists of HashMaps.
	 * HashMaps contain the following keys:
	 * 
	 * <ul>
	 * <li><b>uuid</b> - the uuid of the node</li>
	 * <li><b>type</b> - the type of the node ('o' or 'a')</li>
	 * <li><b>children</b> - the list of children of the node</li>
	 * </ul>
	 * 
	 * The Method returns all children from a node (object or address) depending
	 * in its uuid and its type.
	 * 
	 * If uuid is null all root nodes of the given type are returned up until a
	 * defined depth. If type is also null, all root node types (objects and
	 * addresses) are returned.
	 * 
	 * The parameter type must be set together with the uuid to classify the
	 * type of the node (object or address).
	 * 
	 * @param uuid
	 *            The uuid of the node (object or address). If null all root
	 *            nodes (objects and addresses) will be returned
	 * @param type
	 *            The type of the nodes returned (must be set together with
	 *            uuid). 'o' stands for objects, 'a' for addresses.
	 * @param depth
	 *            The depth of the returned tree structure. Must be > 0.
	 * @return
	 */
	public List getSubTree(String uuid, String type, int depth);

}
