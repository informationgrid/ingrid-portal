/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2019 wemove digital solutions GmbH
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
package de.ingrid.portal.global;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public static final String SEARCH_SETTINGS = "searchSettings";

    public static final String SEARCH_SOURCES = "searchSources";

    public static final String SEARCH_PARTNER = "searchPartner";

    public static final String SEARCH_SAVE_ENTRIES = "searchSaveEntries";

    public static final String WMS_SERVICES = "wmsServices";

    public static final String WMC_DOCUMENT = "wmcDocument";
    
    private final static Logger log = LoggerFactory.getLogger(IngridPersistencePrefs.class);

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
     * Get all principal preferences for a given principal
     * 
     * @param principalName
     *            The name of the principal.
     * @return an HashMap containing the deserialized preference objects.
     */
    public static HashMap<String, String> getPrefs(String principalName) {
        // get xml from db
        Session session = HibernateUtil.currentSession();
        Transaction tx = null;
        List prefs = null;
        HashMap<String, String> result = new HashMap<String, String>();
        try {
            tx = session.beginTransaction();
            prefs = session.createCriteria(IngridPrincipalPreference.class).add(
                    Restrictions.eq("principalName", principalName)).list();
            for (int i = 0; i < prefs.size(); i++) {
                IngridPrincipalPreference pref = (IngridPrincipalPreference) prefs.get(i);
                result.put(pref.getPrefName(), pref.getPrefValue());
            }
            tx.commit();
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
        return result;
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
    public static void setPref(String principalName, String prefName, Object prefValue) {
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
     * @param principalName
     *            The principal's name.
     * @param prefName
     *            The preference name.
     */
    public static void removePref(String principalName, String prefName) {
        Session session = HibernateUtil.currentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session
                    .createQuery(
                            "delete IngridPrincipalPreference p where p.principalName = :principalName and p.prefName = :prefName")
                    .setString("principalName", principalName).setString("prefName", prefName).executeUpdate();
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
