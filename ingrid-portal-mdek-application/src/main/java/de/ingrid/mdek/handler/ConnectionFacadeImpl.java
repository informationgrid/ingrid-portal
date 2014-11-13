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
package de.ingrid.mdek.handler;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import de.ingrid.mdek.caller.IMdekCaller;
import de.ingrid.mdek.caller.IMdekCallerAddress;
import de.ingrid.mdek.caller.IMdekCallerCatalog;
import de.ingrid.mdek.caller.IMdekCallerObject;
import de.ingrid.mdek.caller.IMdekCallerQuery;
import de.ingrid.mdek.caller.IMdekCallerSecurity;
import de.ingrid.mdek.caller.IMdekClientCaller;
import de.ingrid.mdek.caller.MdekCaller;
import de.ingrid.mdek.caller.MdekCallerAddress;
import de.ingrid.mdek.caller.MdekCallerCatalog;
import de.ingrid.mdek.caller.MdekCallerObject;
import de.ingrid.mdek.caller.MdekCallerQuery;
import de.ingrid.mdek.caller.MdekCallerSecurity;
import de.ingrid.mdek.caller.MdekClientCaller;
import de.ingrid.mdek.util.MdekSecurityUtils;

public class ConnectionFacadeImpl implements ConnectionFacade {

	private final static Logger log = Logger.getLogger(ConnectionFacadeImpl.class);

	private IMdekClientCaller mdekClientCaller;
	private IMdekCallerObject mdekCallerObject;
	private IMdekCallerAddress mdekCallerAddress;
	private IMdekCallerQuery mdekCallerQuery;
	private IMdekCallerCatalog mdekCallerCatalog;
	private IMdekCallerSecurity mdekCallerSecurity;

	public ConnectionFacadeImpl(File communicationProperties) {
		if (communicationProperties == null || !(communicationProperties instanceof File)) {
			throw new IllegalStateException(
					"Please specify the location of the communication.properties file via the Property 'mdekClientCaller.properties' in /src/resources/mdek.properties");
		}
		log.debug("Initializing MdekCaller...");
		MdekClientCaller.initialize(communicationProperties);
		log.debug("MdekCaller initialized.");
		mdekClientCaller = MdekClientCaller.getInstance();

		MdekCallerObject.initialize(mdekClientCaller);
		MdekCallerAddress.initialize(mdekClientCaller);
		MdekCallerQuery.initialize(mdekClientCaller);
		MdekCallerCatalog.initialize(mdekClientCaller);
		MdekCallerSecurity.initialize(mdekClientCaller);
		
		mdekCallerObject = MdekCallerObject.getInstance();
		mdekCallerAddress = MdekCallerAddress.getInstance();
		mdekCallerQuery = MdekCallerQuery.getInstance();
		mdekCallerCatalog = MdekCallerCatalog.getInstance();
		mdekCallerSecurity = MdekCallerSecurity.getInstance();
	}

	// Shutdown Method is called by the Spring Framework on shutdown
	public void destroy() {
		log.debug("Shutting down MdekCaller...");
		MdekCaller.shutdown();
		log.debug("MdekCaller shut down.");
		mdekClientCaller = null;
	}


	public String getCurrentPlugId() {
/*
		List<String> iPlugs = mdekClientCaller.getRegisteredIPlugs();
		if (iPlugs.size() > 0) {
			return iPlugs.get(0);
		} else {
			return null;
		}
*/
		return MdekSecurityUtils.getCurrentPortalUserData().getPlugId();
	}
	
	public String getCurrentPlugId(HttpServletRequest req) {
	    return MdekSecurityUtils.getCurrentPortalUserData(req).getPlugId();
	}

	public IMdekClientCaller getMdekClientCaller() {
		return mdekClientCaller;
	}

	public IMdekCallerObject getMdekCallerObject() {
		return mdekCallerObject;
	}

	public IMdekCallerAddress getMdekCallerAddress() {
		return mdekCallerAddress;
	}

	public IMdekCallerQuery getMdekCallerQuery() {
		return mdekCallerQuery;
	}

	public IMdekCallerCatalog getMdekCallerCatalog() {
		return mdekCallerCatalog;
	}

	public IMdekCallerSecurity getMdekCallerSecurity() {
		return mdekCallerSecurity;
	}
}
