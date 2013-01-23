package de.ingrid.mdek.security;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * This interface is used for implementations of user authentification used when
 * a user needs to login or has to authenticate when doing an operation on the
 * backend.
 * @author Andre
 *
 */
public interface AuthenticationProvider {

    /**
     * Check if the username is registered with the password.
     */
    public boolean authenticate(String username, String password);
    
    /**
     * Get all available user ids that can be imported to be an mdek user.
     * All users that aleady are IGE user should not be listed here.
     */
    public List<String> getAllUserIds();

    /**
     * If there's a login as a different user than the one logged in, then this one
     * can be received here. For example we log into the portal as 'admin' where we
     * can start the IGE as a different user then this function needs to be considered.
     * The user in the session is ignored then. This functionality is for maintainance
     * and shall not be used normally!
     * 
     * @return null if the logged in user wants to access the IGE
     */
    public String getForcedUser(HttpServletRequest req);
    
    /**
     * Mark a user as IGE user so that it won't be listed as available user.
     * @param username
     */
    public void setIgeUser(String username);
    
    /**
     * Remove a user from the IGE users list, so that it is again available for
     * a new user to choose.
     * @param username
     */
    public void removeIgeUser(String username);

}
