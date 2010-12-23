package de.ingrid.mdek.dwr.services;

import java.util.List;

import de.ingrid.mdek.beans.security.Group;
import de.ingrid.mdek.beans.security.Permission;
import de.ingrid.mdek.beans.security.User;
import de.ingrid.mdek.handler.SecurityRequestHandler;
import de.ingrid.mdek.persistence.db.model.UserData;

public interface SecurityService {

    public List<Group> getGroups(boolean includeCatAdminGroup);

    public Group getGroupDetails(String name);

    public Group createGroup(Group group, boolean refetch);

    public Group storeGroup(Group group, boolean refetch);

    public void deleteGroup(Long groupId);

    public UserData getUserDataForAddress(String addressUuid);

    public List<User> getSubUsers(Long userId);

    public User getCurrentUser();

    public User getUserDetails(String userId);

    public List<Permission> getUserPermissions(String userId);
    
    public User createUser(User user, String portalLogin, boolean refetch);

    public User storeUser(String oldUserLogin, User user, String portalLogin,
            boolean refetch);

    public void deleteUser(Long userId, String addressUuid);

    public User getCatalogAdmin();

    public List<String> getPortalUsers();

    public List<User> getUsersWithWritePermissionForObject(String objectUuid,
            boolean checkWorkflow, boolean includePermissions);

    public List<User> getUsersWithWritePermissionForAddress(String objectUuid,
            boolean checkWorkflow, boolean includePermissions);
    
    public List<User> getResponsibleUsersForNewObject(String objectUuid,
            boolean checkWorkflow, boolean includePermissions);
    
    public List<User> getResponsibleUsersForNewAddress(String addressUuid,
            boolean checkWorkflow, boolean includePermissions);

    public List<User> getUsersWithPermissionForObject(String objectUuid,
            boolean checkWorkflow, boolean includePermissions);
    
    public List<User> getUsersWithPermissionForAddress(String addressUuid,
            boolean checkWorkflow, boolean includePermissions);
    
    public List<User> getUsersOfGroup(String groupName);

    public SecurityRequestHandler getSecurityRequestHandler();

    public void setSecurityRequestHandler(
            SecurityRequestHandler securityRequestHandler);

}