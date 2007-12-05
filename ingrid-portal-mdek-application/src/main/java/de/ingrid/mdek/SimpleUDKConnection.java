package de.ingrid.mdek;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.ingrid.utils.IngridDocument;

public class SimpleUDKConnection implements DataConnectionInterface {

	private IMdekCaller mdekCaller; 
	private DataMapperInterface dataMapper;

	// TODO Load the file dynamically from a cfg file 	
	private final static String ENTRY_SERVICE_CFG_FILE = "C:\\_projekte\\ingrid\\ingrid-SVN\\ingrid-mdek\\trunk\\ingrid-mdek-api\\src\\main\\resources\\communication.properties";
	// TODO The OBJECT_ROOT_UUID is used as an entry point for the db. Should be replaced with another method call 'getRootObjects'
//	private final static String OBJECT_ROOT_UUID = "9A347535-4754-11D4-BB4F-00105A378751";
//	private final static String OBJECT_ROOT_UUID = "19654CB2-C510-11D3-BADE-0060971A0BF7";
//	private final static String OBJECT_ROOT_UUID = "1C549049-F243-11D2-9A86-080000507261";


	public SimpleUDKConnection() {
		File file = new File(ENTRY_SERVICE_CFG_FILE);
		MdekCaller.initialize(file);
		mdekCaller = MdekCaller.getInstance();
	}


	protected void finalize() {
		MdekCaller.shutdown();
		mdekCaller = null;
	}

	public DataMapperInterface getDataMapper() {
		return dataMapper;
	}


	public void setDataMapper(DataMapperInterface dataMapper) {
		this.dataMapper = dataMapper;
	}


	public HashMap<String, Object> getNodeDetail(String uuid) {
		// TODO Auto-generated method stub
		return null;
	}

	public ArrayList<HashMap<String, Object>> getRootAddresses() {
		IngridDocument response = mdekCaller.fetchTopAddresses();
		return extractAddressesFromResponse(response);
	}

	public ArrayList<HashMap<String, Object>> getRootObjects() {
		IngridDocument response = mdekCaller.fetchTopObjects();
		return extractObjectsFromResponse(response);
	}


	public ArrayList<HashMap<String, Object>> getSubObjects(String uuid, int depth) {
		IngridDocument response = mdekCaller.fetchSubObjects(uuid);
		return extractObjectsFromResponse(response);
	}

	public ArrayList<HashMap<String, Object>> getSubAddresses(String uuid, int depth) {
		IngridDocument response = mdekCaller.fetchSubAddresses(uuid);
		return extractObjectsFromResponse(response);
	}


	
	// ------------------------ Helper Methods ---------------------------------------	

	private ArrayList<HashMap<String, Object>> extractObjectsFromResponse(IngridDocument response)
	{
		IngridDocument result = mdekCaller.getResultFromResponse(response);
		

		ArrayList<HashMap<String, Object>> nodeList = null;
		
		if (result != null) {
			nodeList = new ArrayList<HashMap<String, Object>>();
			List<IngridDocument> objs = (List<IngridDocument>) result.get(MdekKeys.OBJ_ENTITIES);
			for (IngridDocument objEntity : objs) {
				nodeList.add(dataMapper.getSimpleMdekRepresentation(objEntity));
			}
		} else {
			System.out.println(mdekCaller.getErrorMsgFromResponse(response));			
		}
		return nodeList;
	}
	
	private ArrayList<HashMap<String, Object>> extractAddressesFromResponse(IngridDocument response)
	{
		IngridDocument result = mdekCaller.getResultFromResponse(response);

		ArrayList<HashMap<String, Object>> nodeList = null;
		
		if (result != null) {
			nodeList = new ArrayList<HashMap<String, Object>>();
			List<IngridDocument> adrs = (List<IngridDocument>) result.get(MdekKeys.ADR_ENTITIES);
			for (IngridDocument adrEntity : adrs) {
				nodeList.add(dataMapper.getSimpleMdekRepresentation(adrEntity));
			}
		} else {
			System.out.println(mdekCaller.getErrorMsgFromResponse(response));			
		}
		return nodeList;
	}

	
	// TODO Temporary method. This information should come directly from the db query
	@Deprecated
	private boolean hasChildren(String uuid)
	{
		IngridDocument response = mdekCaller.fetchSubObjects(uuid);
		IngridDocument result = mdekCaller.getResultFromResponse(response);

		if (result == null) {
			System.out.println(mdekCaller.getErrorMsgFromResponse(response));			
			return false;
		}
		List<IngridDocument> objectEntities = (List<IngridDocument>) result.get(MdekKeys.OBJ_ENTITIES);

		return (objectEntities.size() > 0);
	}

}
