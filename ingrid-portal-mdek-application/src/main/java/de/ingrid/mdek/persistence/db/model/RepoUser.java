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
    
    private String login;
    
    private String firstName;
    
    private String surname;
    
    private String email;
    
    private String password;
    
    public RepoUser() {}
    
    public void setLogin(String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
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
        return getLogin();
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getSurname() {
        return surname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
    
}
