package de.ingrid.mdek;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.ingrid.mdek.dwr.CatalogBean;
import de.ingrid.mdek.dwr.JobInfoBean;
import de.ingrid.mdek.dwr.MdekAddressBean;
import de.ingrid.mdek.dwr.MdekDataBean;
import de.ingrid.utils.IngridDocument;

/**
 * @author mbenz
 * 
 * The Data Connection Interface defines the methods needed to connect a data source to the
 * Mdek GUI. EntryService needs a DataConnectionInterface implementation.
 * 
 */
public interface DataConnectionInterface {

	/**
	 * Returns the Root Objects in a List. Each Root Object is represented by a HashMap
	 * with the following keys:
	 * 
	 * <ul>
	 * <li><b>(String) id</b> - uuid of the node</li>
	 * <li><b>(String) nodeDocType</b> possible values: "Class1...Class6"</li>
	 * <li><b>(String) title</b> - title of the node</li>
	 * <li><b>(String) hasChildren</b> - 'true' if node has children, 'false' if not</li>
	 * </ul>
	 * 
	 * @return A List of HashMaps representing the root objects.
	 */
	public ArrayList<HashMap<String, Object>> getRootObjects();

	
	/**
	 * Returns the root addresses in a List. Each root address is represented by a HashMap
	 * with the following keys:
	 * 
	 * <ul>
	 * <li><b>(String) id</b> - uuid of the node</li>
	 * <li><b>(String) nodeDocType</b> possible values: "Institution|InstitutionUnit|InstitutionPerson|InstitutionUnit_B|InstitutionPerson_Q|PersonAddress_R"</li>
	 * <li><b>(String) title</b> - title of the node</li>
	 * <li><b>(String) hasChildren</b> - 'true' if node has children, 'false' if not</li>
	 * </ul>
	 * 
	 * @return A List of HashMaps representing the root addresses.
	 */	
	public ArrayList<HashMap<String, Object>> getRootAddresses();

	/**
	 * Returns a tree Structure representing the sub objects of the object with uuid. Each node
	 * is represented by a Map with the following keys:
	 * 
	 * <ul>
	 * <li><b>(String) id</b> - uuid of the node</li>
	 * <li><b>(String) nodeDocType</b> possible values: "Class1...Class6"</li>
	 * <li><b>(String) title</b> - title of the node</li>
	 * <li><b>(String) hasChildren</b> - 'true' if node has children, 'false' if not</li>
	 * </ul>
	 * 
	 * @return A List of HashMaps representing the root addresses.
	 */	
	public ArrayList<HashMap<String, Object>> getSubObjects(String uuid, int depth);

	/**
	 * Returns a Map representing the object with uuid.
	 * 
	 * <ul>
	 * <li><b>(String) id</b> - uuid of the node</li>
	 * </ul>
	 * 
	 * @return A MdekDataBean representing the node identified by uuid.
	 */	
	public MdekDataBean getObjectDetail(String uuid);

	/**
	 * Returns a Map representing the address with uuid.
	 * 
	 * <ul>
	 * <li><b>(String) id</b> - uuid of the node</li>
	 * </ul>
	 * 
	 * @return A MdekAddressBean representing the node identified by uuid.
	 */	
	public MdekAddressBean getAddressDetail(String uuid);

	/**
	 * Returns an object with initial data.
	 * 
	 * <ul>
	 * <li><b>(String) parentUuid</b> - uuid of the parent node where the new node will be attached</li>
	 * </ul>
	 * 
	 * @return A MdekDataBean representing the initial object.
	 */		
	public MdekDataBean getInitialObject(String parentUuid);

	/**
	 * Returns an address with initial data.
	 * 
	 * <ul>
	 * <li><b>(String) parentUuid</b> - uuid of the parent node where the new node will be attached</li>
	 * </ul>
	 * 
	 * @return A MdekAddressBean representing the initial address.
	 */		
	public MdekAddressBean getInitialAddress(String parentUuid);

	/**
	 * Sends an object for storage.
	 * 
	 * <ul>
	 * <li><b>(MdekDataBean) data</b> - data representing the object that should be stored.</li>
	 * </ul>
	 * 
	 * @return The stored object is returned.
	 */		
	public MdekDataBean saveObject(MdekDataBean data);

	/**
	 * Sends an address for storage.
	 * 
	 * <ul>
	 * <li><b>(MdekAddressBean) data</b> - data representing the address that should be stored.</li>
	 * </ul>
	 * 
	 * @return The stored address is returned.
	 */		
	public MdekAddressBean saveAddress(MdekAddressBean data);

	/**
	 * Sends an object for publishing.
	 * 
	 * <ul>
	 * <li><b>(MdekDataBean) data</b> - data representing the object that should be published.</li>
	 * </ul>
	 * 
	 * @return The published object is returned.
	 */		
	public MdekDataBean publishObject(MdekDataBean data, boolean forcePublicationCondition);
	
	/**
	 * Sends an address for publishing.
	 * 
	 * <ul>
	 * <li><b>(MdekAddressBean) data</b> - data representing the address that should be published.</li>
	 * </ul>
	 * 
	 * @return The published address is returned.
	 */		
	public MdekAddressBean publishAddress(MdekAddressBean data);

	
	/**
	 * Sends a request to delete an object.
	 * 
	 * <ul>
	 * <li><b>(String) uuid</b> - uuid of the node that should be deleted.</li>
	 * </ul>
	 * 
	 */		
	public void deleteObject(String uuid);

	/**
	 * Sends a request to delete a working copy.
	 * 
	 * <ul>
	 * <li><b>(String) uuid</b> - uuid of the node whose working copy should be deleted.</li>
	 * </ul>
	 * 
	 * @return boolean if the object was fully deleted (no published version exists) or not (published ver exists)
	 */		
	public boolean deleteObjectWorkingCopy(String uuid);

	/**
	 * Sends a request to delete a working copy.
	 * 
	 * <ul>
	 * <li><b>(String) uuid</b> - uuid of the node whose working copy should be deleted.</li>
	 * </ul>
	 * 
	 * @return boolean if the address was fully deleted (no published version exists) or not (published ver exists)
	 */		
	public boolean deleteAddressWorkingCopy(String uuid);

	
	/**
	 * Query if an object can be cut.
	 * 
	 * <ul>
	 * <li><b>(String) uuid</b> - uuid of the node to check.</li>
	 * </ul>
	 * 
	 * @return boolean if the object can be cut
	 */		
	public boolean canCutObject(String uuid);

	/**
	 * Query if an object can be copied.
	 * 
	 * <ul>
	 * <li><b>(String) uuid</b> - uuid of the node to check.</li>
	 * </ul>
	 * 
	 * @return boolean if the object can be copied
	 */		
	public boolean canCopyObject(String uuid);

	/**
	 * Get a path from the root node to the specified node with uuid.
	 * 
	 * <ul>
	 * <li><b>(String) uuid</b> - uuid of the target node.</li>
	 * </ul>
	 * 
	 * @return List of uuids representing a path to the target node.
	 */		
	public List<String> getPathToObject(String uuid);

	/**
	 * Get a path from the root node to the specified address with uuid.
	 * 
	 * <ul>
	 * <li><b>(String) uuid</b> - uuid of the target node.</li>
	 * </ul>
	 * 
	 * @return List of uuids representing a path to the target node.
	 */		
	public List<String> getPathToAddress(String uuid);

	/**
	 * Copy a single object or a whole tree.
	 * 
	 * <ul>
	 * <li><b>(String) fromUuid</b> - uuid of the source node.</li>
	 * <li><b>(String) toUuid</b> - uuid of the target node.</li>
	 * <li><b>(boolean) copySubTree</b> - flag signaling if the whole subtree should be copied.</li>
	 * </ul>
	 * 
	 * @return Map with information about the copied node (top node if a whole tree is copied)
	 */	
	public Map<String, Object> copyObject(String fromUuid, String toUuid, boolean copySubTree);

	/**
	 * Move a single object or a whole tree.
	 * 
	 * <ul>
	 * <li><b>(String) fromUuid</b> - uuid of the source node.</li>
	 * <li><b>(String) toUuid</b> - uuid of the target node.</li>
	 * <li><b>(boolean) forcePublicationCondition</b> - flag signaling if the whole subtree should be moved.</li>
	 * </ul>
	 * 
	 */	
	public void moveObjectSubTree(String fromUuid, String toUuid, boolean forcePublicationCondition);

	/**
	 * Fetch Sys Lists from the server.
	 * 
	 * <ul>
	 * <li><b>(Integer[]) listIds</b> - List of ids for syslists that should be fetched.</li>
	 * <li><b>(Integer) languageCode</b> - Language Code.</li>
	 * </ul>
	 * 
	 * @return Map containing the requested syslists.
	 */	
	public Map<Integer, List<String[]>> getSysLists(Integer[] listIds, Integer languageCode);

	/**
	 * Fetch information about the catalog.
	 * 
	 * @return Catalog Data.
	 */	
	public CatalogBean getCatalogData();

	/**
	 * Fetch information about the current running job (for the current session id).
	 * 
	 * @return Job info.
	 */	
	public JobInfoBean getRunningJobInfo();

	/**
	 * Cancel a running job (for the current session id).
	 * 
	 * @return Job info.
	 */	
	public JobInfoBean cancelRunningJob();
	
	/**
	 * Returns a tree Structure representing the sub addresses of the address with uuid.
	 * 
	 * 
	 * @return A List of HashMaps representing the sub addresses.
	 */	
	public ArrayList<HashMap<String, Object>> getSubAddresses(String uuid, int depth);
}
