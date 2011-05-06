package de.ingrid.mdek.dwr.services;

import java.util.Date;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;

public class UtilityServiceImpl {

	private final static Logger log = Logger.getLogger(UtilityServiceImpl.class);

	public void refreshSession() {
		WebContext wctx = WebContextFactory.get();
		HttpSession ses = wctx.getSession();
		log.debug("refreshing session. Last access time: "+new Date(ses.getLastAccessedTime()));
	}
	
	public boolean sessionValid() {
        WebContext wctx = WebContextFactory.get();
        HttpSession ses = wctx.getSession(false);
        if (ses.getAttribute("userName") == null)
            log.info("userName was not in Session ... should return false now!");
        
        return ses != null && !ses.isNew();
    }
	
	public int getSessionTimoutInterval() {
        WebContext wctx = WebContextFactory.get();
        HttpSession ses = wctx.getSession(false);
        return ses.getMaxInactiveInterval();
	}
}
