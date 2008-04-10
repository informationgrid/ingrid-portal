package de.ingrid.mdek.handler;

import java.util.List;

import org.apache.log4j.Logger;

import de.ingrid.mdek.beans.security.Group;
import de.ingrid.mdek.caller.IMdekCallerSecurity;
import de.ingrid.mdek.dwr.util.HTTPSessionHelper;
import de.ingrid.mdek.util.MdekUtils;
import de.ingrid.utils.IngridDocument;

public class SecurityRequestHandlerImpl implements SecurityRequestHandler {

	private final static Logger log = Logger.getLogger(SecurityRequestHandlerImpl.class);

	// Injected by Spring
	private ConnectionFacade connectionFacade;

	// Initialized by spring through the init method
	private IMdekCallerSecurity mdekCallerSecurity;

	public void init() {
		this.mdekCallerSecurity = connectionFacade.getMdekCallerSecurity();
	}
	
	public List<Group> getGroups() {
		IngridDocument response = mdekCallerSecurity.getGroups(connectionFacade.getCurrentPlugId(), HTTPSessionHelper.getCurrentSessionId());
		return MdekUtils.extractSecurityGroupsFromResponse(response);
	}

	public Group getGroupDetails(String name) {
		IngridDocument response = mdekCallerSecurity.getGroupDetails(connectionFacade.getCurrentPlugId(), name, HTTPSessionHelper.getCurrentSessionId());
		return MdekUtils.extractSecurityGroupFromResponse(response);
	}

	public Group createGroup(Group group, boolean refetch) {
		IngridDocument g = MdekUtils.convertSecurityGroupToIngridDoc(group);
		IngridDocument response = mdekCallerSecurity.createGroup(connectionFacade.getCurrentPlugId(), g, refetch, HTTPSessionHelper.getCurrentSessionId());
		return MdekUtils.extractSecurityGroupFromResponse(response);
	}

	public Group storeGroup(Group group, boolean refetch) {
		IngridDocument g = MdekUtils.convertSecurityGroupToIngridDoc(group);
		IngridDocument response = mdekCallerSecurity.storeGroup(connectionFacade.getCurrentPlugId(), g, refetch, HTTPSessionHelper.getCurrentSessionId());
		return MdekUtils.extractSecurityGroupFromResponse(response);
	}


	public ConnectionFacade getConnectionFacade() {
		return connectionFacade;
	}

	public void setConnectionFacade(ConnectionFacade connectionFacade) {
		this.connectionFacade = connectionFacade;
	}
}
