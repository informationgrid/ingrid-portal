package de.ingrid.mdek.mapping;

import de.ingrid.mdek.SysListCache;
import de.ingrid.mdek.util.MdekSecurityUtils;

public class ImportDataProviderImpl implements ImportDataProvider {

	// Injected by Spring
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
