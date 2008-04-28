package de.ingrid.mdek.dwr.util;

import de.ingrid.mdek.util.MdekSecurityUtils;

public class HTTPSessionHelper {
	
	public static String getCurrentSessionId() {
		return MdekSecurityUtils.getCurrentPortalUserData().getAddressUuid();
	/*
		try {
			WebContext wctx = WebContextFactory.get();
			HttpSession session = wctx.getSession();
			return session.getId();
		} catch (Exception e) {
			return "";
		}
	*/
	}
}
