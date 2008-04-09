package de.ingrid.mdek.dwr.util;

import javax.servlet.http.HttpSession;

import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;

public class HTTPSessionHelper {
	
	public static String getCurrentSessionId() {
		try {
			WebContext wctx = WebContextFactory.get();
			HttpSession session = wctx.getSession();
			return session.getId();
		} catch (Exception e) {
			return "";
		}
	}
}
