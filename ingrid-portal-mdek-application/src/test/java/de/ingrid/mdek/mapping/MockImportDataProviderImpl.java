package de.ingrid.mdek.mapping;

import java.util.HashMap;
import java.util.Map;

public class MockImportDataProviderImpl implements ImportDataProvider {
	
	String userId = null;
	
	Map<Integer, Integer> initialKeys  = new HashMap<Integer,Integer>();
	
	Map<Integer, String> initialValues = new HashMap<Integer,String>();

	public String getCurrentUserUuid() {
		return userId;
	}

	public Integer getInitialKeyFromListId(int id) {
		return initialKeys.get(id);
	}

	public String getInitialValueFromListId(int id) {
		return initialValues.get(id);
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public void setInitialKeys(Map<Integer, Integer> map) {
		this.initialKeys = map;
	}
	
	public void setInitialValues(Map<Integer, String> map) {
		this.initialValues = map;
	}

}
