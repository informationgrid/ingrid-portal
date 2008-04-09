package de.ingrid.mdek.handler;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;

import de.ingrid.mdek.caller.IMdekCaller;
import de.ingrid.mdek.caller.IMdekCallerAddress;
import de.ingrid.mdek.caller.IMdekCallerCatalog;
import de.ingrid.mdek.caller.IMdekCallerObject;
import de.ingrid.mdek.caller.IMdekCallerQuery;
import de.ingrid.mdek.caller.MdekCaller;
import de.ingrid.mdek.caller.MdekCallerAddress;
import de.ingrid.mdek.caller.MdekCallerCatalog;
import de.ingrid.mdek.caller.MdekCallerObject;
import de.ingrid.mdek.caller.MdekCallerQuery;

public class ConnectionFacadeImpl implements ConnectionFacade {

	private final static Logger log = Logger.getLogger(ConnectionFacadeImpl.class);

	private IMdekCaller mdekCaller;
	private IMdekCallerObject mdekCallerObject;
	private IMdekCallerAddress mdekCallerAddress;
	private IMdekCallerQuery mdekCallerQuery;
	private IMdekCallerCatalog mdekCallerCatalog;

	public ConnectionFacadeImpl(File communicationProperties) {
		if (communicationProperties == null || !(communicationProperties instanceof File)) {
			throw new IllegalStateException(
					"Please specify the location of the communication.properties file via the Property 'mdekCaller.properties' in /src/resources/mdek.properties");
		}
		log.debug("Initializing MdekCaller...");
		MdekCaller.initialize(communicationProperties);
		log.debug("MdekCaller initialized.");
		mdekCaller = MdekCaller.getInstance();

		MdekCallerObject.initialize(mdekCaller);
		MdekCallerAddress.initialize(mdekCaller);
		MdekCallerQuery.initialize(mdekCaller);
		MdekCallerCatalog.initialize(mdekCaller);
		
		mdekCallerObject = MdekCallerObject.getInstance();
		mdekCallerAddress = MdekCallerAddress.getInstance();
		mdekCallerQuery = MdekCallerQuery.getInstance();
		mdekCallerCatalog = MdekCallerCatalog.getInstance();
	}

	// Shutdown Method is called by the Spring Framework on shutdown
	public void destroy() {
		log.debug("Shutting down MdekCaller...");
		MdekCaller.shutdown();
		log.debug("MdekCaller shut down.");
		mdekCaller = null;
	}


	public String getCurrentPlugId() {
		List<String> iPlugs = mdekCaller.getRegisteredIPlugs();
		if (iPlugs.size() > 0) {
			return iPlugs.get(0);
		} else {
			return null;
		}
	}

	public IMdekCaller getMdekCaller() {
		return mdekCaller;
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
}
