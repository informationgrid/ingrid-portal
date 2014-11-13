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
