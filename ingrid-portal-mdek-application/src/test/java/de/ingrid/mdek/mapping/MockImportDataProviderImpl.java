/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
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
