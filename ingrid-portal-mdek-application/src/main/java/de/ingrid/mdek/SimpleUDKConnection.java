package de.ingrid.mdek;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.ingrid.utils.IngridDocument;

public class SimpleUDKConnection implements DataConnectionInterface {

	private IMdekCaller mdekCaller; 

	// TODO Load the file dynamically from a cfg file 	
	private final static String ENTRY_SERVICE_CFG_FILE = "C:\\_projekte\\ingrid\\ingrid-SVN\\ingrid-mdek\\trunk\\ingrid-mdek-api\\src\\main\\resources\\communication.properties";
	// TODO The OBJECT_ROOT_UUID is used as an entry point for the db. Should be replaced with another method call 'getRootObjects'
	private final static String OBJECT_ROOT_UUID = "9A347535-4754-11D4-BB4F-00105A378751";


	public SimpleUDKConnection() {
		File file = new File(ENTRY_SERVICE_CFG_FILE);
		MdekCaller.initialize(file);
		mdekCaller = MdekCaller.getInstance();
	}


	protected void finalize() {
		MdekCaller.shutdown();
		mdekCaller = null;
	}

	public HashMap<String, Object> getNodeDetail(String uuid) {
		System.out.println(uuid);
		// TODO Auto-generated method stub
		return null;
	}

	public ArrayList<HashMap<String, Object>> getRootAddresses() {
		// TODO Auto-generated method stub
		return null;
	}

	public ArrayList<HashMap<String, Object>> getRootObjects() {
		// TODO Replace with fetchRootObjects
		IngridDocument response = mdekCaller.fetchSubObjects(OBJECT_ROOT_UUID);
		IngridDocument result = MdekCaller.getResult(response);

		ArrayList<HashMap<String, Object>> nodeList = null;
		
		if (result != null) {
			nodeList = new ArrayList<HashMap<String, Object>>();
			List<IngridDocument> objs = (List<IngridDocument>) result.get(MdekKeys.OBJ_ENTITIES);
			for (IngridDocument objEntity : objs) {

				String entityUUID = (String) objEntity.get(MdekKeys.UUID);
				String entityName = (String) objEntity.get(MdekKeys.TITLE);
				Integer entityClass = (Integer) objEntity.get(MdekKeys.CLASS); 

				HashMap<String, Object> node = new HashMap<String, Object>();
				node.put("id", entityUUID);
				node.put("title", entityName);
				node.put("nodeDocType", "Class"+entityClass.toString());
				node.put("isFolder", hasChildren(entityUUID));

				nodeList.add(node);
			}
		} else {
			System.out.println(MdekCaller.getErrorMsg(response));			
		}

		return nodeList;
	}


	public ArrayList<HashMap<String, Object>> getSubObjects(String uuid, int depth) {
		IngridDocument response = mdekCaller.fetchSubObjects(uuid);
		IngridDocument result = MdekCaller.getResult(response);

		ArrayList<HashMap<String, Object>> nodeList = null;
		
		if (result != null) {
			nodeList = new ArrayList<HashMap<String, Object>>();
			List<IngridDocument> objs = (List<IngridDocument>) result.get(MdekKeys.OBJ_ENTITIES);
			for (IngridDocument objEntity : objs) {

				String entityUUID = (String) objEntity.get(MdekKeys.UUID);
				String entityName = (String) objEntity.get(MdekKeys.TITLE);
				Integer entityClass = (Integer) objEntity.get(MdekKeys.CLASS); 

				HashMap<String, Object> node = new HashMap<String, Object>();
				node.put("id", entityUUID);
				node.put("title", entityName);
				node.put("nodeDocType", "Class"+entityClass.toString());
				node.put("isFolder", hasChildren(entityUUID));

				nodeList.add(node);
			}
		} else {
			System.out.println(MdekCaller.getErrorMsg(response));			
		}

		return nodeList;
	}

	
	// ------------------------ Helper Methods ---------------------------------------	

	// TODO Temporary method. This information should come directly from the db query
	private boolean hasChildren(String uuid)
	{
		IngridDocument response = mdekCaller.fetchSubObjects(uuid);
		IngridDocument result = MdekCaller.getResult(response);

		if (result == null) {
			System.out.println(MdekCaller.getErrorMsg(response));			
			return false;
		}
		List<IngridDocument> objectEntities = (List<IngridDocument>) result.get(MdekKeys.OBJ_ENTITIES);

		return (objectEntities.size() > 0);
	}

}
