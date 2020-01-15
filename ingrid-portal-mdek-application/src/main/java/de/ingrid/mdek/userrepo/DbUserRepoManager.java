/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2020 wemove digital solutions GmbH
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

import java.util.*;

import de.ingrid.mdek.persistence.db.model.RepoUser;
import de.ingrid.mdek.services.persistence.db.IDaoFactory;
import de.ingrid.mdek.services.persistence.db.IEntity;
import de.ingrid.mdek.services.persistence.db.IGenericDao;
import de.ingrid.mdek.util.MdekSecurityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.directwebremoting.WebContextFactory;

public class DbUserRepoManager implements UserRepoManager {

    private static final Logger log = LogManager.getLogger(DbUserRepoManager.class);

    private IDaoFactory daoFactory;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public List<Map<String,Object>> getAllUsers() {
        IGenericDao<IEntity> dao = daoFactory.getDao(RepoUser.class);

        dao.beginTransaction();
        List<RepoUser> userList = (List) dao.findAll();  // Can't cast to List<RepoUser>
        dao.commitTransaction();
        
        List<Map<String,Object>> users = new ArrayList<>();
        for (RepoUser repoUser : userList) {
            Map info = new HashMap<String, String>();
            info.put("surname", repoUser.getSurname());
            info.put("firstName", repoUser.getFirstName());
            info.put("email", repoUser.getEmail());
            info.put("login", repoUser.getLogin());
            info.put("passwordChangeId", repoUser.getPasswordChangeId());
            info.put("passwordChangeDate", repoUser.getPasswordChangeDate());
            users.add(info);
        }

        return users;
    }

    @Override
    public List<String> getAllAvailableUsers() {
        IGenericDao<IEntity> dao = daoFactory.getDao(RepoUser.class);
        RepoUser sample = new RepoUser();
        
        dao.beginTransaction();
        List<RepoUser> userList = (List) dao.findByExample(sample);  // Can't cast to List<RepoUser>
        dao.commitTransaction();
        
        List<String> users = new ArrayList<>();
        for (RepoUser repoUser : userList) {
            users.add(repoUser.getLogin());
        }

        return users;
    }
    
    @Override
    public RepoUser getUser(String username) {
        IGenericDao<IEntity> dao = daoFactory.getDao(RepoUser.class);

        dao.beginTransaction();
        RepoUser user = (RepoUser) dao.getById(username); 
        dao.commitTransaction();

        return user;
    }

    @Override
    public void addUser(String username, String password, String firstName, String surname, String email) {

        String currentUser = (String) WebContextFactory.get().getHttpServletRequest().getSession().getAttribute("userName");

        if ("admin".equals(currentUser)) {
        
            RepoUser userData = new RepoUser();
            userData.setLogin(username);
            userData.setPassword(MdekSecurityUtils.getHash(password));

            userData.setFirstName(firstName);
            userData.setSurname(surname);
            userData.setEmail(email);

            addUser(userData);

        } else {
            log.warn("Somebody else then admin tried to add a new user");
        }
    }
    
    @Override
    public void addUser(RepoUser userData) {

        String currentUser = (String) WebContextFactory.get().getHttpServletRequest().getSession().getAttribute("userName");

        if ("admin".equals(currentUser)) {


            IGenericDao<IEntity> dao = daoFactory.getDao(RepoUser.class);
            // encrypt password
            userData.setPassword(MdekSecurityUtils.getHash(userData.getPassword()));

            dao.beginTransaction();
            dao.makePersistent(userData);
            dao.commitTransaction();
        } else {
            log.warn("Somebody else then admin tried to add a new user");
        }

    }

    @Override
    public void updateUser(String login, RepoUser userData) {

        String currentUser = (String) WebContextFactory.get().getHttpServletRequest().getSession().getAttribute("userName");

        // only the logged in user can update its own data
        // and user admin can update all data
        if ("admin".equals(currentUser) || (login.equals(currentUser) && login.equals(userData.getLogin()))) {

            IGenericDao<IEntity> dao = daoFactory.getDao(RepoUser.class);
            dao.beginTransaction();
            RepoUser user = (RepoUser) dao.getById(login);
            user.setLogin(userData.getLogin());
            if (!userData.getPassword().isEmpty()) {
                user.setPassword(MdekSecurityUtils.getHash(userData.getPassword()));
            }
            user.setFirstName(userData.getFirstName());
            user.setSurname(userData.getSurname());
            user.setEmail(userData.getEmail());
            user.setPasswordChangeId(userData.getPasswordChangeId());

            dao.makePersistent(user);
            dao.commitTransaction();

        } else {
            log.warn("The user " + currentUser + " tried to change user data for another one");
        }
    }

    @Override
    public void setPasswordRecoveryId(String login, String passwordChangeId, String newPassword) {

        // this method must not have a user check, since recovery for a password must work for not logged in users

        IGenericDao<IEntity> dao = daoFactory.getDao(RepoUser.class);
        dao.beginTransaction();
        RepoUser user = (RepoUser) dao.getById(login);

        user.setPasswordChangeId(passwordChangeId);
        user.setPasswordChangeDate(new Date());

        if (newPassword != null && !newPassword.isEmpty()) {
            user.setPassword(MdekSecurityUtils.getHash(newPassword));
        }

        dao.makePersistent(user);
        dao.commitTransaction();
    }

    @Override
    public void removeUser(String username) {

        String currentUser = (String) WebContextFactory.get().getHttpServletRequest().getSession().getAttribute("userName");

        if ("admin".equals(currentUser)) {
            IGenericDao<IEntity> dao = daoFactory.getDao(RepoUser.class);
            dao.beginTransaction();
            RepoUser user = (RepoUser) dao.getById(username);
            dao.makeTransient(user);
            dao.commitTransaction();
        } else {
            log.warn("Somebody else then admin tried to remove a user");
        }

    }

    @Override
    public boolean hasWriteAccess() {
        return true;
    }
    
    public void setDaoFactory(IDaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    
}
