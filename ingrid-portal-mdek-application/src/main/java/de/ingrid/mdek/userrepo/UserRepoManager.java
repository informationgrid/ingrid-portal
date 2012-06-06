package de.ingrid.mdek.userrepo;

import java.util.List;
import java.util.Map;

import de.ingrid.mdek.persistence.db.model.RepoUser;

public interface UserRepoManager {
    
    public List<Map<String,String>> getAllUsers();
    
    public List<String> getAllAvailableUsers();

    public void addUser(String username, String password, String firstName, String surname, String email);
    
    public void addUser(RepoUser userData);
    
    public void removeUser(String username);
    
    public boolean hasWriteAccess();

    public RepoUser getUser(String username);

    void updateUser(String login, RepoUser userData);
    
}
