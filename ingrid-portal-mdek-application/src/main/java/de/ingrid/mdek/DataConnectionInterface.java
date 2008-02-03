package de.ingrid.mdek;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.ingrid.mdek.dwr.CatalogBean;
import de.ingrid.mdek.dwr.JobInfoBean;
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
	 * <li><b>(String) nodeDocType</b> possible values: "Class1...Class6"</li>
	 * <li><b>(String) title</b> - title of the node</li>
	 * <li><b>(String) hasChildren</b> - 'true' if node has children, 'false' if not</li>
	 * </ul>
	 * 
	 * @return A MdekDataBean representing the node identified by uuid.
	 */	
	public MdekDataBean getNodeDetail(String uuid);
	public MdekDataBean getInitialObject(String parentUuid);
	
	public MdekDataBean saveNode(MdekDataBean data, boolean forcePublicationCondition);
	public MdekDataBean publishNode(MdekDataBean data);
	
	public void deleteObject(String uuid);
	public boolean deleteObjectWorkingCopy(String uuid);

	public void canCutObject(String uuid);
	public void canCopyObject(String uuid);
	public List<String> getPathToObject(String uuid);
	public Map<String, Object> copyObject(String fromUuid, String toUuid, boolean copySubTree);
	public void moveObjectSubTree(String fromUuid, String toUuid);
	public Map<Integer, List<String[]>> getSysLists(Integer[] listIds, Integer languageCode);
	public CatalogBean getCatalogData();
	public JobInfoBean getRunningJobInfo();
	
	public ArrayList<HashMap<String, Object>> getSubAddresses(String uuid, int depth);
}
