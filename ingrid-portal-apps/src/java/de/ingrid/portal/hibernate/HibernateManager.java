/*
 * Copyright (c) 1997-2005 by media style GmbH
 */

package de.ingrid.portal.hibernate;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Criterion;
import org.hibernate.metadata.ClassMetadata;

/**
 * Abstracts the Hibernate OR Framework related logic. It provides a set of methods to load, save, update and delete
 * objects in the persitence layer. Further more it manage the transaction related issues with Hibernate Sessiosn.
 * 
 * @author mb
 * @author $Author: mb $ (last edit)
 */
public class HibernateManager {

    /**
     * Singleton instance of the Hibernate Manager. Initialized on demand.
     */
    private static HibernateManager fSystem = null;

    private Configuration fConfig;

    private Session fSession;

    private static SessionFactory fFactory;

    private ThreadLocal fThreadLocal = new ThreadLocal();

    /**
     * @return Singleton instance of the HibernateManager.
     * @throws HibernateException
     */
    public static synchronized HibernateManager getInstance() throws HibernateException {
        if (fSystem == null) {
            fSystem = new HibernateManager();
        }
        
        return fSystem;
    }

    /**
     * Constructor (private because object is a singleton and may not be instanciated from outside this class).
     * 
     * @throws HibernateException
     */
    private HibernateManager() throws HibernateException {
        super();
        intial();
    }

    /**
     * Initializes Hibernate. We need to tell Hibernate which Classes should be persistent managed by Hibernate. Further
     * more Hibernate needs to be configured.
     * 
     * @throws HibernateException
     */
    private void intial() throws HibernateException {
        this.fConfig = new org.hibernate.cfg.Configuration();
        this.fConfig.configure();
        this.fConfig.setProperty("hibernate.cache.provider_class", "org.hibernate.cache.HashtableCacheProvider");
        fFactory = this.fConfig.buildSessionFactory();

    }

    /**
     * Saves object to the database.
     * 
     * @param object
     *            Object to save to database. Must be an Hibernate managed object.
     * @throws HibernateException
     */
    public synchronized void save(Object object) throws HibernateException {
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        session.save(object);
        transaction.commit();
        session.flush();
    }

    /**
     * Opens an object identified by a key. The unique key field is setted up in the hibernate mapping xml (*.hbm.xml)
     * file of that object.
     * 
     * @param clazz
     *            Class of object that should be loaded.
     * @param key
     *            Primary key to identify object.
     * @return Newly created object.
     * 
     */
    public Object readObject(Class clazz, Serializable key) {
        Session session = getSession();
        Object object = session.get(clazz, key);
        return object;
    }

    /**
     * @param clazz
     * @param restrictions
     * @return object specifies by the parameters
     */
    public List findObject(Class clazz, Criterion restrictions) {
        Session session = getSession();
        Criteria crit = session.createCriteria(clazz);
        crit.add(restrictions);
        synchronized (this) {
            List list = crit.list();
            return list;
        }
    }

    /**
     * Returns a Hibernate session object. A Hibernate Session is a fine encapsulated process part that needs to be
     * sucessfully finished. Actions like save are mostly just executed when the session is closed. For lazy loading
     * Hibernate provides some more functionality, see the Hibernate documentation.
     * 
     * @return Session (only one per thread).
     * @throws HibernateException
     */
    public Session getSession() throws HibernateException {
        if (this.fSession == null) {
            this.fSession = fFactory.openSession();
        }
        return this.fSession;
    }

    /**
     * Closes the current Hibernate session.
     * 
     * @throws HibernateException
     */
    public void closeSession() throws HibernateException {
        Session s = (Session) this.fThreadLocal.get();
        this.fThreadLocal.set(null);
        if (s != null) {
            s.flush();
            s.close();
        }
    }

    /**
     * Removes an object from the database.
     * 
     * @param object
     * @throws HibernateException
     */
    public synchronized void delete(Object object) throws HibernateException {
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        session.delete(object);
        transaction.commit();
        session.flush();
    }

    /**
     * Updates an object in the database.accessable
     * 
     * @param object
     *            Hibernate managed object that needs to be updated.
     * @throws HibernateException
     */
    public synchronized void update(Object object) throws HibernateException {
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        session.update(object);
        transaction.commit();
        session.flush();
    }

    /**
     * Returns an string that represents a field in the Serializable Java Bean. Since we only work with java beans (as
     * Hibernate require) the field is accessable via a getter Method. There is a ReflectionUtil in hmc.model that can
     * support you to query field values.
     * 
     * @param object
     * @return String
     * @throws HibernateException
     */
    public String getIdentifier(Object object) throws HibernateException {
        ClassMetadata metadata = fFactory.getClassMetadata(object.getClass());
        return metadata.getIdentifierPropertyName();
    }

    /**
     * Returns a list of all data object that exist in the database.
     * 
     * @param clazz
     *            Class of object that should be loaded.
     * @param maxResults
     *            Maximum number of objects that should be loaded.
     * @return List of all objects.
     * @throws HibernateException
     */
    public List loadAllData(Class clazz, int maxResults) throws HibernateException {
        Criteria criteria = getSession().createCriteria(clazz);
        if (maxResults != 0) {
            criteria.setMaxResults(maxResults);
        }
        List list = criteria.list();
        return list;
    }

    protected void finalize() throws Throwable {
        closeSession();
        super.finalize();
    }

    /**
     * Deletes a list of objects.
     * 
     * @param list
     * @throws HibernateException
     */
    public void deleteList(List list) throws HibernateException {
        int count = list.size();
        for (int i = 0; i < count; i++) {
            delete(list.get(i));
        }
    }
}
