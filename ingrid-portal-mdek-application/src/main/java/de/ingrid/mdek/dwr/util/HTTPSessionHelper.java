package de.ingrid.mdek.dwr.util;

import de.ingrid.mdek.util.MdekSecurityUtils;

public class HTTPSessionHelper {
	
	public static String getCurrentUserUuid() {
		return MdekSecurityUtils.getCurrentPortalUserData().getAddressUuid();
	}
}
