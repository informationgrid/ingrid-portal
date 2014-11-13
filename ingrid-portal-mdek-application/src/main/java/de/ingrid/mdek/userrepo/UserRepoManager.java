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
