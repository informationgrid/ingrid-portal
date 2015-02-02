/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2015 wemove digital solutions GmbH
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.ingrid.mdek.SysListCache;
import de.ingrid.mdek.util.MdekSecurityUtils;

@Service
public class ImportDataProviderImpl implements ImportDataProvider {

	// Injected by Spring
    @Autowired
	private SysListCache sysListMapper;
	
	public void setSysListMapper(SysListCache sysListMapper) {
		this.sysListMapper = sysListMapper;
	}
	
	public Integer getInitialKeyFromListId(int id) {
		return sysListMapper.getInitialKeyFromListId(id);
	}

	public String getInitialValueFromListId(int id) {
		return sysListMapper.getInitialValueFromListId(id);
	}

	public String getCurrentUserUuid() {
		return MdekSecurityUtils.getCurrentUserUuid();
	}

}
