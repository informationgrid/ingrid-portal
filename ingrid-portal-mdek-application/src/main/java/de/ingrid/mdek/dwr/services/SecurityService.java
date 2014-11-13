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

    public List<String> getAvailableUsers();

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

    public int getUserNumForPlugId(String plugId);
    
    public boolean authenticate(String username, String password);
}