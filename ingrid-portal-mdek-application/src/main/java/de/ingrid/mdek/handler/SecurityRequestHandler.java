package de.ingrid.mdek.handler;

import java.util.List;

import de.ingrid.mdek.beans.security.Group;
import de.ingrid.mdek.beans.security.Permission;
import de.ingrid.mdek.beans.security.User;

public interface SecurityRequestHandler {

	public List<Group> getGroups(boolean includeCatAdminGroup);
	public Group getGroupDetails(String name);
	public Group createGroup(Group group, boolean refetch);
	public Group storeGroup(Group group, boolean refetch);
	public void deleteGroup(Long groupId);

	public List<User> getSubUsers(Long userId);
	public User getUserDetails(String userId);
    public List<Permission> getUserPermissions(String userId);
	public User createUser(User user, boolean refetch);
	public User storeUser(User user, boolean refetch);
	public void deleteUser(Long userId);

	public User getCatalogAdmin();

	public List<User> getUsersWithWritePermissionForAddress(String addressUuid, boolean checkWorkflow, boolean detailedPermissions);
	public List<User> getUsersWithWritePermissionForObject(String objectUuid, boolean checkWorkflow, boolean detailedPermissions);

    /**
     * Get all users who can be responsible users for given address.
     * 
     * @param addressUuid
     * @param checkWorkflow If true take workflow conditions into account
     * @param detailedPermissions If true return detailed permission on each user.
     * @return
     */
    public List<User> getResponsibleUsersForNewAddress(String addressUuid, boolean checkWorkflow, boolean detailedPermissions);

    /**
     * Get all users who can be responsible users for given object.
     * 
     * @param objectUuid
     * @param checkWorkflow If true take workflow conditions into account
     * @param detailedPermissions If true return detailed permission on each user.
     * @return
     */
    public List<User> getResponsibleUsersForNewObject(String objectUuid, boolean checkWorkflow, boolean detailedPermissions);

    
    /**
     * Get all users with any permission on a given address.
     * 
     * @param addressUuid
     * @param checkWorkflow If true take workflow conditions into account
     * @param detailedPermissions If true return detailed permission on each user.
     * @return
     */
    public List<User> getUsersWithPermissionForAddress(String addressUuid, boolean checkWorkflow, boolean detailedPermissions);

    /**
     * Get all users with any permission on a given address.
     * 
     * @param objectUuid
     * @param checkWorkflow If true take workflow conditions into account
     * @param detailedPermissions If true return detailed permission on each user.
     * @return
     */
    public List<User> getUsersWithPermissionForObject(String objectUuid, boolean checkWorkflow, boolean detailedPermissions);
    
	public List<User> getUsersOfGroup(String groupName);
}