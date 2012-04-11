package de.ingrid.mdek.persistence.db.model;

import java.io.Serializable;

import de.ingrid.mdek.services.persistence.db.IEntity;


/**
 * Represents the user stored for authentication when accessing the IGE without
 * a connected Portal. 
 * @author Andre
 *
 */
public class RepoUser implements IEntity {
    
    private int version;
    
    private String username;
    
    private String password;
    
    public RepoUser() {}
    
    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getVersion() {
        return version;
    }

    @Override
    public Serializable getId() {
        return getUsername();
    }
    
}
