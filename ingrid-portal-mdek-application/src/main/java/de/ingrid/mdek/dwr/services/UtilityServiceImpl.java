/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.mdek.dwr.services;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import de.ingrid.mdek.SpringConfiguration;
import org.apache.log4j.Logger;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import de.ingrid.mdek.Config;
import org.springframework.beans.factory.annotation.Autowired;

public class UtilityServiceImpl {

	@Autowired
	private SpringConfiguration springConfig;

	private static final Logger log = Logger.getLogger(UtilityServiceImpl.class);

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
        
        return !ses.isNew();
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

    /** get globalconfig field (defined in mdek.properties) from backend to frontend */
    public Object getApplicationConfigEntry(String key) throws NoSuchFieldException, IllegalAccessException {
    	Config c = springConfig.globalConfig();
		return c.getClass().getField( key ).get( c );
    }
}
