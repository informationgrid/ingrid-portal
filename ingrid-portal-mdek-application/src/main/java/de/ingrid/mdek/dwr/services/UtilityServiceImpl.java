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
package de.ingrid.mdek.dwr.services;

import java.util.Date;
import java.util.UUID;

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

	/** returns java generated UUID via UUID.randomUUID() */
	public String getRandomUUID() {
		return UUID.randomUUID().toString();
    }
}
