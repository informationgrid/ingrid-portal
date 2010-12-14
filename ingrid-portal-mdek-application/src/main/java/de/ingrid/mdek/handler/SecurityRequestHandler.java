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
     * Get all users with tree permission (write-tree, write-subtree) on a given address.
     * 
     * @param addressUuid
     * @param checkWorkflow If true take workflow conditions into account (i.e. if the the object is in QA, the user might only have write-subtree) right.
     * @param detailedPermissions If true return detailed permission on each user.
     * @return
     */
    public List<User> getUsersWithTreeOrSubTreePermissionForAddress(String addressUuid, boolean checkWorkflow, boolean detailedPermissions);

    /**
     * Get all users with tree permission (write-tree, write-subtree) on a given address.
     * 
     * @param objectUuid
     * @param checkWorkflow If true take workflow conditions into account (i.e. if the the object is in QA, the user might only have write-subtree) right.
     * @param detailedPermissions If true return detailed permission on each user.
     * @return
     */
    public List<User> getUsersWithTreeOrSubTreePermissionForObject(String objectUuid, boolean checkWorkflow, boolean detailedPermissions);

    
    /**
     * Get all users with any permission on a given address.
     * 
     * @param addressUuid
     * @param checkWorkflow If true take workflow conditions into account (i.e. if the the object is in QA, the user might only have write-subtree) right.
     * @param detailedPermissions If true return detailed permission on each user.
     * @return
     */
    public List<User> getUsersWithPermissionForAddress(String addressUuid, boolean checkWorkflow, boolean detailedPermissions);

    /**
     * Get all users with any permission on a given address.
     * 
     * @param objectUuid
     * @param checkWorkflow If true take workflow conditions into account (i.e. if the the object is in QA, the user might only have write-subtree) right.
     * @param detailedPermissions If true return detailed permission on each user.
     * @return
     */
    public List<User> getUsersWithPermissionForObject(String objectUuid, boolean checkWorkflow, boolean detailedPermissions);
    
	public List<User> getUsersOfGroup(String groupName);
}