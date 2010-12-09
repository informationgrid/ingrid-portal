package de.ingrid.mdek.handler;

import java.util.List;

import org.apache.log4j.Logger;

import de.ingrid.mdek.beans.security.Group;
import de.ingrid.mdek.beans.security.Permission;
import de.ingrid.mdek.beans.security.User;
import de.ingrid.mdek.caller.IMdekCallerSecurity;
import de.ingrid.mdek.util.MdekSecurityUtils;
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
		IngridDocument response = mdekCallerSecurity.getGroups(connectionFacade.getCurrentPlugId(), MdekSecurityUtils.getCurrentUserUuid(), includeCatAdminGroup);
		return MdekUtils.extractSecurityGroupsFromResponse(response);
	}

	public Group getGroupDetails(String name) {
		IngridDocument response = mdekCallerSecurity.getGroupDetails(connectionFacade.getCurrentPlugId(), name, MdekSecurityUtils.getCurrentUserUuid());
		return MdekUtils.extractSecurityGroupFromResponse(response);
	}

	public Group createGroup(Group group, boolean refetch) {
		IngridDocument g = MdekUtils.convertSecurityGroupToIngridDoc(group);
		IngridDocument response = mdekCallerSecurity.createGroup(connectionFacade.getCurrentPlugId(), g, refetch, MdekSecurityUtils.getCurrentUserUuid());
		return MdekUtils.extractSecurityGroupFromResponse(response);
	}

	public Group storeGroup(Group group, boolean refetch) {
		IngridDocument g = MdekUtils.convertSecurityGroupToIngridDoc(group);
		IngridDocument response = mdekCallerSecurity.storeGroup(connectionFacade.getCurrentPlugId(), g, refetch, MdekSecurityUtils.getCurrentUserUuid());
		return MdekUtils.extractSecurityGroupFromResponse(response);
	}

	public void deleteGroup(Long groupId) {
		// TODO: Implement forceDelete param
		boolean forceDelete = false;
		IngridDocument response = mdekCallerSecurity.deleteGroup(connectionFacade.getCurrentPlugId(), groupId, forceDelete, MdekSecurityUtils.getCurrentUserUuid());
		MdekUtils.checkForErrors(response);
		return;
	}

	public List<User> getSubUsers(Long userId) {
		IngridDocument response = mdekCallerSecurity.getSubUsers(connectionFacade.getCurrentPlugId(), userId, MdekSecurityUtils.getCurrentUserUuid());
		return MdekUtils.extractSecurityUsersFromResponse(response);		
	}
	
	public User getUserDetails(String userId) {
		IngridDocument response = mdekCallerSecurity.getUserDetails(connectionFacade.getCurrentPlugId(), userId, MdekSecurityUtils.getCurrentUserUuid());
		return MdekUtils.extractSecurityUserFromResponse(response);		
	}
	
    @Override
    public List<Permission> getUserPermissions(String userId) {
        IngridDocument response = mdekCallerSecurity.getUserPermissions(connectionFacade.getCurrentPlugId(), userId, MdekSecurityUtils.getCurrentUserUuid());
        return MdekUtils.extractUserPermissionsFromResponse(response);     
    }
	
	public User createUser(User user, boolean refetch) {
		IngridDocument u = MdekUtils.convertSecurityUserToIngridDoc(user);
		IngridDocument response = mdekCallerSecurity.createUser(connectionFacade.getCurrentPlugId(), u, refetch, MdekSecurityUtils.getCurrentUserUuid());
		return MdekUtils.extractSecurityUserFromResponse(response);
	}

	public User storeUser(User user, boolean refetch) {
		IngridDocument u = MdekUtils.convertSecurityUserToIngridDoc(user);
		IngridDocument response = mdekCallerSecurity.storeUser(connectionFacade.getCurrentPlugId(), u, refetch, MdekSecurityUtils.getCurrentUserUuid());
		return MdekUtils.extractSecurityUserFromResponse(response);
	}

	public void deleteUser(Long userId) {
		IngridDocument response = mdekCallerSecurity.deleteUser(connectionFacade.getCurrentPlugId(), userId, MdekSecurityUtils.getCurrentUserUuid());
		MdekUtils.extractSecurityUserFromResponse(response);
		return;
	}

	public User getCatalogAdmin() {
		IngridDocument response = mdekCallerSecurity.getCatalogAdmin(connectionFacade.getCurrentPlugId(), MdekSecurityUtils.getCurrentUserUuid());
		return MdekUtils.extractSecurityUserFromResponse(response);		
	}

	public List<User> getUsersWithWritePermissionForObject(String objectUuid, boolean checkWorkflow, boolean detailedPermissions) {
		IngridDocument response = mdekCallerSecurity.getUsersWithWritePermissionForObject(connectionFacade.getCurrentPlugId(), objectUuid, MdekSecurityUtils.getCurrentUserUuid(), checkWorkflow, detailedPermissions);
		return MdekUtils.extractSecurityUsersFromResponse(response);
	}
	
	public List<User> getUsersWithWritePermissionForAddress(String addressUuid, boolean checkWorkflow, boolean detailedPermissions) {
		IngridDocument response = mdekCallerSecurity.getUsersWithWritePermissionForAddress(connectionFacade.getCurrentPlugId(), addressUuid, MdekSecurityUtils.getCurrentUserUuid(), checkWorkflow, detailedPermissions);
		return MdekUtils.extractSecurityUsersFromResponse(response);
	}

    public List<User> getUsersWithTreePermissionForObject(String objectUuid, boolean checkWorkflow, boolean detailedPermissions) {
        IngridDocument response = mdekCallerSecurity.getUsersWithTreePermissionForObject(connectionFacade.getCurrentPlugId(), objectUuid, MdekSecurityUtils.getCurrentUserUuid(), checkWorkflow, detailedPermissions);
        return MdekUtils.extractSecurityUsersFromResponse(response);
    }
    
    public List<User> getUsersWithTreePermissionForAddress(String addressUuid, boolean checkWorkflow, boolean detailedPermissions) {
        IngridDocument response = mdekCallerSecurity.getUsersWithTreePermissionForAddress(connectionFacade.getCurrentPlugId(), addressUuid, MdekSecurityUtils.getCurrentUserUuid(), checkWorkflow, detailedPermissions);
        return MdekUtils.extractSecurityUsersFromResponse(response);
    }

    public List<User> getUsersWithPermissionForObject(String objectUuid, boolean checkWorkflow, boolean detailedPermissions) {
        IngridDocument response = mdekCallerSecurity.getUsersWithPermissionForObject(connectionFacade.getCurrentPlugId(), objectUuid, MdekSecurityUtils.getCurrentUserUuid(), checkWorkflow, detailedPermissions);
        return MdekUtils.extractSecurityUsersFromResponse(response);
    }
    
    public List<User> getUsersWithPermissionForAddress(String addressUuid, boolean checkWorkflow, boolean detailedPermissions) {
        IngridDocument response = mdekCallerSecurity.getUsersWithPermissionForAddress(connectionFacade.getCurrentPlugId(), addressUuid, MdekSecurityUtils.getCurrentUserUuid(), checkWorkflow, detailedPermissions);
        return MdekUtils.extractSecurityUsersFromResponse(response);
    }
    
	public List<User> getUsersOfGroup(String groupName) {
		IngridDocument response = mdekCallerSecurity.getUsersOfGroup(connectionFacade.getCurrentPlugId(), groupName, MdekSecurityUtils.getCurrentUserUuid());
		return MdekUtils.extractSecurityUsersFromResponse(response);
	}

	public ConnectionFacade getConnectionFacade() {
		return connectionFacade;
	}

	public void setConnectionFacade(ConnectionFacade connectionFacade) {
		this.connectionFacade = connectionFacade;
	}

}
