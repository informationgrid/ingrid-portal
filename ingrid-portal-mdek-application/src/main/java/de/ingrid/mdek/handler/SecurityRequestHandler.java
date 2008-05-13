package de.ingrid.mdek.handler;

import java.util.List;

import de.ingrid.mdek.beans.address.MdekAddressBean;
import de.ingrid.mdek.beans.security.Group;
import de.ingrid.mdek.beans.security.User;

public interface SecurityRequestHandler {

	public List<Group> getGroups();
	public Group getGroupDetails(String name);
	public Group createGroup(Group group, boolean refetch);
	public Group storeGroup(Group group, boolean refetch);
	public void deleteGroup(Long groupId);

	public List<User> getSubUsers(Long userId);
	public User getUserDetails(String userId);
	public User createUser(User user, boolean refetch);
	public User storeUser(User user, boolean refetch);
	public void deleteUser(Long userId);

	public User getCatalogAdmin();

	public List<User> getUsersWithWritePermissionForAddress(String addressUuid);
	public List<User> getUsersWithWritePermissionForObject(String objectUuid);
}