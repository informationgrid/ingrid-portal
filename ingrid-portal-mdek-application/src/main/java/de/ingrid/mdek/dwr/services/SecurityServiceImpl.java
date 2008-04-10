package de.ingrid.mdek.dwr.services;

import java.util.List;

import org.apache.log4j.Logger;

import de.ingrid.mdek.beans.security.Group;
import de.ingrid.mdek.handler.SecurityRequestHandler;

public class SecurityServiceImpl {

	private final static Logger log = Logger.getLogger(SecurityServiceImpl.class);	

	// Injected by Spring
	private SecurityRequestHandler securityRequestHandler;


	public List<Group> getGroups() {
		return securityRequestHandler.getGroups();
	}

	public Group getGroupDetails(String name) {
		return securityRequestHandler.getGroupDetails(name);
	}

	public Group createGroup(Group group, boolean refetch) {
		return securityRequestHandler.createGroup(group, true);
	}

	public Group storeGroup(Group group, boolean refetch) {
		return securityRequestHandler.storeGroup(group, true);
	}

	public SecurityRequestHandler getSecurityRequestHandler() {
		return securityRequestHandler;
	}

	public void setSecurityRequestHandler(
			SecurityRequestHandler securityRequestHandler) {
		this.securityRequestHandler = securityRequestHandler;
	}
}
