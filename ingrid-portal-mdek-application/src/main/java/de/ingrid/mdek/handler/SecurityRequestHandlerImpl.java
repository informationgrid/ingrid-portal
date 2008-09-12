package de.ingrid.mdek.handler;

import java.util.List;

import org.apache.log4j.Logger;

import de.ingrid.mdek.beans.security.Group;
import de.ingrid.mdek.beans.security.User;
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
	
	public List<Group> getGroups(boolean includeCatAdminGroup) {
		IngridDocument response = mdekCallerSecurity.getGroups(connectionFacade.getCurrentPlugId(), HTTPSessionHelper.getCurrentSessionId(), includeCatAdminGroup);
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

	public void deleteGroup(Long groupId) {
		// TODO: Implement forceDelete param
		boolean forceDelete = false;
		IngridDocument response = mdekCallerSecurity.deleteGroup(connectionFacade.getCurrentPlugId(), groupId, forceDelete, HTTPSessionHelper.getCurrentSessionId());
		MdekUtils.checkForErrors(response);
		return;
	}

	public List<User> getSubUsers(Long userId) {
		IngridDocument response = mdekCallerSecurity.getSubUsers(connectionFacade.getCurrentPlugId(), userId, HTTPSessionHelper.getCurrentSessionId());
		return MdekUtils.extractSecurityUsersFromResponse(response);		
	}
	
	public User getUserDetails(String userId) {
		IngridDocument response = mdekCallerSecurity.getUserDetails(connectionFacade.getCurrentPlugId(), userId, HTTPSessionHelper.getCurrentSessionId());
		return MdekUtils.extractSecurityUserFromResponse(response);		
	}
	
	public User createUser(User user, boolean refetch) {
		IngridDocument u = MdekUtils.convertSecurityUserToIngridDoc(user);
		IngridDocument response = mdekCallerSecurity.createUser(connectionFacade.getCurrentPlugId(), u, refetch, HTTPSessionHelper.getCurrentSessionId());
		return MdekUtils.extractSecurityUserFromResponse(response);
	}

	public User storeUser(User user, boolean refetch) {
		IngridDocument u = MdekUtils.convertSecurityUserToIngridDoc(user);
		IngridDocument response = mdekCallerSecurity.storeUser(connectionFacade.getCurrentPlugId(), u, refetch, HTTPSessionHelper.getCurrentSessionId());
		return MdekUtils.extractSecurityUserFromResponse(response);
	}

	public void deleteUser(Long userId) {
		IngridDocument response = mdekCallerSecurity.deleteUser(connectionFacade.getCurrentPlugId(), userId, HTTPSessionHelper.getCurrentSessionId());
		MdekUtils.extractSecurityUserFromResponse(response);
		return;
	}

	public User getCatalogAdmin() {
		IngridDocument response = mdekCallerSecurity.getCatalogAdmin(connectionFacade.getCurrentPlugId(), HTTPSessionHelper.getCurrentSessionId());
		return MdekUtils.extractSecurityUserFromResponse(response);		
	}

	public List<User> getUsersWithWritePermissionForObject(String objectUuid, boolean checkWorkflow, boolean detailedPermissions) {
		IngridDocument response = mdekCallerSecurity.getUsersWithWritePermissionForObject(connectionFacade.getCurrentPlugId(), objectUuid, HTTPSessionHelper.getCurrentSessionId(), checkWorkflow, detailedPermissions);
		return MdekUtils.extractSecurityUsersFromResponse(response);
	}
	
	public List<User> getUsersWithWritePermissionForAddress(String addressUuid, boolean checkWorkflow, boolean detailedPermissions) {
		IngridDocument response = mdekCallerSecurity.getUsersWithWritePermissionForAddress(connectionFacade.getCurrentPlugId(), addressUuid, HTTPSessionHelper.getCurrentSessionId(), checkWorkflow, detailedPermissions);
		return MdekUtils.extractSecurityUsersFromResponse(response);
	}

	public List<User> getUsersOfGroup(String groupName) {
		IngridDocument response = mdekCallerSecurity.getUsersOfGroup(connectionFacade.getCurrentPlugId(), groupName, HTTPSessionHelper.getCurrentSessionId());
		return MdekUtils.extractSecurityUsersFromResponse(response);
	}

	public ConnectionFacade getConnectionFacade() {
		return connectionFacade;
	}

	public void setConnectionFacade(ConnectionFacade connectionFacade) {
		this.connectionFacade = connectionFacade;
	}
}
