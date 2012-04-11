package de.ingrid.mdek.userrepo;

import java.util.List;

import de.ingrid.mdek.persistence.db.model.RepoUser;

public interface UserRepoManager {
    
    public List<String> getAllUsers();
    
    public List<String> getAllAvailableUsers();

    public void addUser(String username, String password);
    
    public void removeUser(String username);
    
    public boolean hasWriteAccess();

    public RepoUser getUser(String username);
}
