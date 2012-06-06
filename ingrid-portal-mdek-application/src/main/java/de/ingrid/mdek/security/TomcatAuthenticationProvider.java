package de.ingrid.mdek.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.directwebremoting.WebContextFactory;

import de.ingrid.mdek.persistence.db.model.RepoUser;
import de.ingrid.mdek.userrepo.UserRepoManager;
import de.ingrid.mdek.util.MdekSecurityUtils;

public class TomcatAuthenticationProvider implements AuthenticationProvider {

    // injected by Spring
    private UserRepoManager repoManager;
    
    /**
     * Authenticate only those users who are registered in mdek database!  
     */
    @Override
    public boolean authenticate(String username, String password) {
        // first check if user is in user repository
        RepoUser user = repoManager.getUser(username);
        if (user == null || !user.getPassword().equals(MdekSecurityUtils.getHash(password))) {
            return false;
        }
        
        return true;
    }

    @Override
    public List<String> getAllUserIds() {
        List<String> userIds = new ArrayList<String>();
        for (Map info : repoManager.getAllUsers()) {
            userIds.add((String) info.get("login"));
        }
        return userIds;
    }

    /**
     * It's not possible to access as a different user as the one that is logged
     * in. This is only possible when connected to a portal and logged in as 
     * 'admin'.
     */
    @Override
    public String getForcedUser(HttpServletRequest req) {
        return null;
    }

    @Override
    public void setIgeUser(String username) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeIgeUser(String username) {
        // TODO Auto-generated method stub

    }
    
    private String getCurrentUserInSession() {
        HttpSession session = WebContextFactory.get().getHttpServletRequest().getSession();
        return (String) session.getAttribute("userName");
    }

    public void setRepoManager(UserRepoManager repoManager) {
        this.repoManager = repoManager;
    }
}
