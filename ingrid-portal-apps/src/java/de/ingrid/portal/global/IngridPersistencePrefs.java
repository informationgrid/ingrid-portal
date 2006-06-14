/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.global;

import java.util.Date;
import java.util.List;

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

    /**
     * @param principalId
     *            The principal's id.
     * @param prefName
     *            The preference name.
     * @return The preference object.
     * @throws Exception
     */
    public static Object getPref(Long principalId, String prefName) throws Exception {

        XStream xstream = new XStream();

        // get xml from db
        Session session = HibernateUtil.currentSession();
        Transaction tx = null;
        String serializedObject = null;
        List prefs = null;
        try {
            tx = session.beginTransaction();
            prefs = session.createCriteria(IngridPrincipalPreference.class).add(
                    Restrictions.eq("principalId", principalId)).add(Restrictions.eq("prefName", prefName))
                    .setMaxResults(1).list();
            tx.commit();
            if (prefs.size() > 0) {
                serializedObject = ((IngridPrincipalPreference) prefs.get(0)).getPrefValue();
            }
        } catch (Throwable t) {
            if (tx != null) {
                tx.rollback();
            }
            throw new Exception(t.getMessage());
        } finally {
            HibernateUtil.closeSession();
        }
        return xstream.fromXML(serializedObject);
    }

    /**
     * @param principalId
     *            The principal id.
     * @param prefName
     *            The preference name.
     * @param prefValue
     *            The preference value.
     * @throws Exception
     */
    public static void setPref(Long principalId, String prefName, Object prefValue) throws Exception {
        XStream xstream = new XStream();
        String serializedObject = xstream.toXML(prefValue);

        Session session = HibernateUtil.currentSession();
        Transaction tx = null;
        List prefs = null;
        IngridPrincipalPreference pref = null;
        try {
            tx = session.beginTransaction();
            prefs = session.createCriteria(IngridPrincipalPreference.class).add(
                    Restrictions.eq("principalId", principalId)).add(Restrictions.eq("prefName", prefName)).list();
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
                pref.setPrincipalId(principalId);
                pref.setPrefValue(serializedObject);
                pref.setModifiedDate(new Date());
                session.save(pref);
            }
            tx.commit();
        } catch (Throwable t) {
            if (tx != null) {
                tx.rollback();
            }
            throw new Exception(t.getMessage());
        } finally {
            HibernateUtil.closeSession();
        }
    }
}
