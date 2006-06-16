/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.global;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import com.thoughtworks.xstream.XStream;

import de.ingrid.portal.hibernate.HibernateUtil;
import de.ingrid.portal.om.IngridPrincipalPreference;

/**
 * This class provides basic functionality for storing and retrieving user
 * specific objects to and from the db.
 * 
 * It uses xml serialization (xstream).
 * 
 * @author joachim@wemove.com
 */
public class IngridPersistencePrefs {

    private final static Log log = LogFactory.getLog(IngridPersistencePrefs.class);

    private static final XStream xstream;

    static {
        try {
            // Create the SessionFactory
            xstream = new XStream();
        } catch (Throwable ex) {
            log.error("Initial Xstream creation failed.", ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
    
    
    /**
     * get an object from database storage.
     * 
     * @param principalName
     *            The principal's name.
     * @param prefName
     *            The preference name.
     * @return The preference object.
     */
    public static Object getPref(String principalName, String prefName) {

        // get xml from db
        Session session = HibernateUtil.currentSession();
        Transaction tx = null;
        String serializedObject = null;
        List prefs = null;
        try {
            tx = session.beginTransaction();
            prefs = session.createCriteria(IngridPrincipalPreference.class).add(
                    Restrictions.eq("principalName", principalName)).add(Restrictions.eq("prefName", prefName))
                    .setMaxResults(1).list();
            tx.commit();
            if (prefs.size() > 0) {
                serializedObject = ((IngridPrincipalPreference) prefs.get(0)).getPrefValue();
            }
        } catch (Throwable t) {
            if (tx != null) {
                tx.rollback();
            }
            if (log.isErrorEnabled()) {
                log.error("Error retrieving persistent preference.", t);
            }
        } finally {
            HibernateUtil.closeSession();
        }
        if (serializedObject == null) {
            return null;
        } else {
            return xstream.fromXML(serializedObject);
        }
    }

    /**
     * Set an object in database storage.
     * 
     * @param principalName
     *            The principal's name.
     * @param prefName
     *            The preference name.
     * @param prefValue
     *            The preference value.
     */
    public static void setPref(String principalName, String prefName, Object prefValue) throws Exception {
        if (prefValue == null) {
            return; 
        }
        String serializedObject = xstream.toXML(prefValue);

        Session session = HibernateUtil.currentSession();
        Transaction tx = null;
        List prefs = null;
        IngridPrincipalPreference pref = null;
        try {
            tx = session.beginTransaction();
            prefs = session.createCriteria(IngridPrincipalPreference.class).add(
                    Restrictions.eq("principalName", principalName)).add(Restrictions.eq("prefName", prefName)).list();
            if (prefs.size() > 0) {
                pref = (IngridPrincipalPreference) prefs.get(0);
            }
            if (pref != null) {
                pref.setPrefValue(serializedObject);
                pref.setModifiedDate(new Date());
                session.update(pref);
            } else {
                pref = new IngridPrincipalPreference();
                pref.setPrefName(prefName);
                pref.setPrincipalName(principalName);
                pref.setPrefValue(serializedObject);
                pref.setModifiedDate(new Date());
                session.save(pref);
            }
            tx.commit();
        } catch (Throwable t) {
            if (tx != null) {
                tx.rollback();
            }
            if (log.isErrorEnabled()) {
                log.error("Error storing persistent preference.", t);
            }
        } finally {
            HibernateUtil.closeSession();
        }
    }
    
    /**
     * Remove an entry from database storage.
     * 
     * @param principalName The principal's name.
     * @param prefName The preference name.
     */
    public static void removePref(String principalName, String prefName) {
        Session session = HibernateUtil.currentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.createQuery( "delete IngridPrincipalPreference p where p.principalName = :principalName and p.prefName = :prefName" )
                 .setString("principalName", principalName)
                 .setString("prefName", prefName)
                 .executeUpdate();
            tx.commit();
        } catch (Throwable t) {
            if (tx != null) {
                tx.rollback();
            }
            if (log.isErrorEnabled()) {
                log.error("Error removing persistent preference.", t);
            }
        } finally {
            HibernateUtil.closeSession();
        }
    }
    
}
