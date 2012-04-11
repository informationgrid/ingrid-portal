package de.ingrid.mdek.userrepo;

import java.util.ArrayList;
import java.util.List;

import de.ingrid.mdek.persistence.db.model.RepoUser;
import de.ingrid.mdek.services.persistence.db.IDaoFactory;
import de.ingrid.mdek.services.persistence.db.IEntity;
import de.ingrid.mdek.services.persistence.db.IGenericDao;
import de.ingrid.mdek.util.MdekSecurityUtils;

public class DbUserRepoManager implements UserRepoManager {

    private IDaoFactory daoFactory;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public List<String> getAllUsers() {
        IGenericDao<IEntity> dao = daoFactory.getDao(RepoUser.class);

        dao.beginTransaction();
        List<RepoUser> userList = (List) dao.findAll();  // Can't cast to List<RepoUser>
        dao.commitTransaction();
        
        List<String> users = new ArrayList<String>();
        for (RepoUser repoUser : userList) {
            users.add(repoUser.getUsername());
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
        
        List<String> users = new ArrayList<String>();
        for (RepoUser repoUser : userList) {
            users.add(repoUser.getUsername());
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
    public void addUser(String username, String password) {
        // TODO: check for admin role first!
        
        IGenericDao<IEntity> dao = daoFactory.getDao(RepoUser.class);
        RepoUser userData = new RepoUser();
        userData.setUsername(username);
        userData.setPassword(MdekSecurityUtils.getHash(password));
        
        dao.beginTransaction();
        dao.makePersistent(userData);
        dao.commitTransaction();

    }

    @Override
    public void removeUser(String username) {
        // TODO: check for admin role first!
        IGenericDao<IEntity> dao = daoFactory.getDao(RepoUser.class);
        dao.beginTransaction();
        RepoUser user = (RepoUser) dao.getById(username);
        dao.makeTransient(user);
        dao.commitTransaction();
    }

    @Override
    public boolean hasWriteAccess() {
        return true;
    }
    
    public void setDaoFactory(IDaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    
}
