/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.global;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.hibernate.HibernateUtil;
import de.ingrid.portal.om.IngridChronicleEventType;
import de.ingrid.portal.om.IngridEnvFunctCategory;
import de.ingrid.portal.om.IngridEnvTopic;
import de.ingrid.portal.om.IngridFormToQuery;
import de.ingrid.portal.om.IngridMeasuresRubric;
import de.ingrid.portal.om.IngridPartner;
import de.ingrid.portal.om.IngridProvider;
import de.ingrid.portal.om.IngridServiceRubric;
import de.ingrid.portal.om.IngridSysCodelistDomain;
import de.ingrid.portal.om.IngridSysCodelistDomainId;

/**
 * Global STATIC data and utility methods for Database.
 * 
 * @author Martin Maidhof
 */
public class UtilsDB {

    private final static Log log = LogFactory.getLog(UtilsDB.class);

    /**
     * this flag controls whether Data is always fetched from Database or from
     * cache
     */
    // TODO keep possibilty to always reload data from DB ???? makes it
    // complicated ! maybe instable ?
    private static boolean alwaysReloadDBData = PortalConfig.getInstance().getBoolean(
            PortalConfig.ALWAYS_REREAD_DB_VALUES);

    // TODO: optimize ? use hash map instead of separate lists !

    /** cache for partners */
    private static List partners = null;

    /** cache for providers */
    private static List providers = null;

    /** cache for environment topics */
    private static List envTopics = null;

    /** cache for environment functional categories */
    private static List envFunctCategories = null;

    /** cache for service rubrics */
    private static List serviceRubrics = null;

    /** cache for measures rubrics */
    private static List measuresRubrics = null;

    /** cache for chronicle eventTypes */
    private static List chronicleEventTypes = null;

    public static boolean getAlwaysReloadDBData() {
        return alwaysReloadDBData;
    }

    public static synchronized void setAlwaysReloadDBData(boolean reload) {
        alwaysReloadDBData = reload;
    }

    /**
     * Convenient generic method for reading stuff from database. NOTICE: If
     * passed cacheList is null, we always reload data from database. If not
     * null we only reload if flag alwaysReloadDBData in our class is set to
     * true. If flag is set false we just return cacheList.
     * 
     * @param objClass
     * @param cacheList
     * @return
     */
    public static List getValuesFromDB(Class objClass, List cacheList) {
        Session session = HibernateUtil.currentSession();
        return getValuesFromDB(session.createCriteria(objClass), session, cacheList, true);
    }

    /**
     * Convenient generic method for reading stuff from database. NOTICE: If
     * passed cacheList is null, we always reload data from database. If not
     * null we only reload if flag alwaysReloadDBData in our class is set to
     * true. If flag is set false we just return cacheList.
     * 
     * @param Criteria
     * @param cacheList
     * @return
     */
    public static List getValuesFromDB(Criteria c, Session session, List cacheList, boolean closeSession) {
        // TODO: use hibernate caching
        Transaction tx = null;
        try {
            if (alwaysReloadDBData) {
                tx = session.beginTransaction();
                List ret = c.list();
                tx.commit();
                return ret;
            } else if (cacheList == null) {
                synchronized (UtilsDB.class) {
                    tx = session.beginTransaction();
                    cacheList = c.list();
                    tx.commit();
                }
            }
            return cacheList;
        } catch (Throwable t) {
            if (tx != null) {
                tx.rollback();
            }
            if (log.isErrorEnabled()) {
                log.error("Error reading from database.", t);
            }
            return null;
        } finally {
            if (closeSession) {
                HibernateUtil.closeSession();
            }
        }
    }

    /**
     * Convenient method for extracting a QueryValue from a List of
     * IngridFormToQuery objects, based on the formValue.
     * 
     * @param formToQueryList
     * @param formValue
     * @return
     */
    private static String getQueryValueFromFormValue(List formToQueryList, String formValue) {
        String ret = formValue;
        IngridFormToQuery formToQuery = null;
        for (int i = 0; i < formToQueryList.size(); i++) {
            formToQuery = (IngridFormToQuery) formToQueryList.get(i);
            if (formToQuery.getFormValue().equals(formValue)) {
                ret = formToQuery.getQueryValue();
                break;
            }
        }
        return ret;
    }

    /**
     * Convenient method for extracting a FormValue from a List of
     * IngridFormToQuery objects, based on the QueryValue.
     * 
     * @param formToQueryList
     * @param formValue
     * @return
     */
    public static String getFormValueFromQueryValue(List formToQueryList, String queryValue) {
        String ret = queryValue;
        IngridFormToQuery formToQuery = null;
        for (int i = 0; i < formToQueryList.size(); i++) {
            formToQuery = (IngridFormToQuery) formToQueryList.get(i);
            if (formToQuery.getQueryValue().equals(queryValue)) {
                ret = formToQuery.getFormValue();
                break;
            }
        }
        return ret;
    }

    /**
     * Get all the partners.
     * 
     * @return
     */
    public static List getPartners() {
        // NOTICE: assign list to our static variable, passed static variable
        // may be null,
        // so there's no call by reference !
        Session session = HibernateUtil.currentSession();
        return getValuesFromDB(session.createCriteria(IngridPartner.class).addOrder(Order.asc("sortkey")), session,
                partners, true);
    }

    /**
     * Get the name of the partner with the according ident. NOTICE: comparison
     * is case insensitive !
     * 
     * @param ident
     * @return the name of the partner OR the key, if no matching partner was
     *         found
     */
    public static String getPartnerFromKey(String ident) {
        List partners = getPartners();
        IngridPartner partner = null;
        for (int i = 0; i < partners.size(); i++) {
            partner = (IngridPartner) partners.get(i);
            if (partner.getIdent().toLowerCase().equals(ident.toLowerCase())) {
                return partner.getName();
            }
        }
        return ident;
    }

    /**
     * Get all the providers. Sort by sortkeyPartner, name !
     * 
     * @return
     */
    public static List getProviders() {
        // NOTICE: assign list to our static variable, passed static variable
        // may be null,
        // so there's no call by reference !
        Session session = HibernateUtil.currentSession();
        return getValuesFromDB(session.createCriteria(IngridProvider.class).addOrder(Order.asc("sortkeyPartner"))
                .addOrder(Order.asc("name")), session, providers, true);
    }

    /**
     * Get alle providers that corresponded with a partner key. NOTICE:
     * comparison is case insensitive !
     * 
     * @param key
     *            The partner key (use 'bund')
     * @return The provider of the partner.
     */
    public static List getProvidersFromPartnerKey(String key) {
        ArrayList result = new ArrayList();
        List provider = getProviders();
        String providerPartnerKey = Utils.normalizePartnerKey(key, true).concat("_");
        Iterator it = provider.iterator();
        while (it.hasNext()) {
            IngridProvider p = (IngridProvider) it.next();
            if (p.getIdent().toLowerCase().startsWith(providerPartnerKey.toLowerCase())) {
                result.add(p);
            }
        }
        return result;
    }

    /**
     * Get the name of the Provider with the according ident. NOTICE: comparison
     * is case insensitive !
     * 
     * @param ident
     * @return the name of the provider OR the key, if no matching provider was
     *         found
     */
    public static String getProviderFromKey(String ident) {
        List providers = getProviders();
        IngridProvider provider = null;
        for (int i = 0; i < providers.size(); i++) {
            provider = (IngridProvider) providers.get(i);
            if (provider.getIdent().toLowerCase().equals(ident.toLowerCase())) {
                return provider.getName();
            }
        }
        return ident;
    }

    /**
     * Get the Provider Object with the according ident. NOTICE: comparison is
     * case insensitive !
     * 
     * @param ident
     * @return The IngridPrivider OR null if no matching was found
     */
    public static IngridProvider getIngridProviderFromKey(String ident) {
        List providers = getProviders();
        IngridProvider provider = null;
        for (int i = 0; i < providers.size(); i++) {
            provider = (IngridProvider) providers.get(i);
            if (provider.getIdent().toLowerCase().equals(ident.toLowerCase())) {
                return provider;
            }
        }
        return null;
    }

    /**
     * Get all the environment topics.
     * 
     * @return
     */
    public static List getEnvTopics() {
        // NOTICE: assign list to our static variable, passed static variable
        // may be null,
        // so there's no call by reference !
        envTopics = getValuesFromDB(IngridEnvTopic.class, envTopics);
        return envTopics;
    }

    /**
     * Get the query value of a topic from the form value (key).
     * 
     * @param key
     * @return
     */
    public static String getTopicFromKey(String key) {
        List envTopics = getEnvTopics();
        return getQueryValueFromFormValue(envTopics, key);
    }

    /**
     * Get all the environment functional categories.
     * 
     * @return
     */
    public static List getEnvFunctCategories() {
        // NOTICE: assign list to our static variable, passed static variable
        // may be null,
        // so there's no call by reference !
        envFunctCategories = getValuesFromDB(IngridEnvFunctCategory.class, envFunctCategories);
        return envFunctCategories;
    }

    /**
     * Get the query value of a functional category from the form value (key).
     * 
     * @param key
     * @return
     */
    public static String getFunctCategoryFromKey(String key) {
        List envCategories = getEnvFunctCategories();
        return getQueryValueFromFormValue(envCategories, key);
    }

    /**
     * Get the service rubrics.
     * 
     * @return
     */
    public static List getServiceRubrics() {
        // NOTICE: assign list to our static variable, passed static variable
        // may be null,
        // so there's no call by reference !
        serviceRubrics = getValuesFromDB(IngridServiceRubric.class, serviceRubrics);
        return serviceRubrics;
    }

    /**
     * Get the query value of a service rubric from the form value (key).
     * 
     * @param key
     * @return
     */
    public static String getServiceRubricFromKey(String key) {
        List rubrics = getServiceRubrics();
        return getQueryValueFromFormValue(rubrics, key);
    }

    /**
     * Get the measures rubrics.
     * 
     * @return
     */
    public static List getMeasuresRubrics() {
        // NOTICE: assign list to our static variable, passed static variable
        // may be null,
        // so there's no call by reference !
        measuresRubrics = getValuesFromDB(IngridMeasuresRubric.class, measuresRubrics);
        return measuresRubrics;
    }

    /**
     * Get the query value of a measures rubric from the form value (key).
     * 
     * @param key
     * @return
     */
    public static String getMeasuresRubricFromKey(String key) {
        List rubrics = getMeasuresRubrics();
        return getQueryValueFromFormValue(rubrics, key);
    }

    /**
     * Get the event types for chronicle.
     * 
     * @return
     */
    public static List getChronicleEventTypes() {
        // NOTICE: assign list to our static variable, passed static variable
        // may be null,
        // so there's no call by reference !
        chronicleEventTypes = getValuesFromDB(IngridChronicleEventType.class, chronicleEventTypes);
        return chronicleEventTypes;
    }

    /**
     * Get the query value of an event type from the form value (key).
     * 
     * @param key
     * @return
     */
    public static String getChronicleEventTypeFromKey(String key) {
        List eventTypes = getChronicleEventTypes();
        return getQueryValueFromFormValue(eventTypes, key);
    }

    /**
     * Get a entry from the code list table
     * 
     * @param codeListId
     * @param domainId
     * @param langId
     * @return
     */
    public static String getCodeListEntryName(long codeListId, long domainId, long langId) {

        Session session = HibernateUtil.currentSession();
        Transaction tx = null;
        try {
            IngridSysCodelistDomainId id = new IngridSysCodelistDomainId();
            id.setCodelistId(new Long(codeListId));
            id.setDomainId(new Long(domainId));
            id.setLangId(new Long(langId));
            tx = session.beginTransaction();
            IngridSysCodelistDomain domainEntry = (IngridSysCodelistDomain) session.load(IngridSysCodelistDomain.class,
                    id);
            tx.commit();
            return domainEntry.getName();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            return String.valueOf(domainId);
        } finally {
            HibernateUtil.closeSession();
        }
    }

    /**
     * Update given mapped entity (hibernate) in database.
     * 
     * @param dbEntity
     */
    public static void updateDBObject(Object dbEntity) {
        updateDBObjects(new Object[] { dbEntity });
    }

    /**
     * Update multiple entities in ONE transaction. Rollback all updates if
     * something fails.
     * 
     * @param dbEntities
     */
    public static void updateDBObjects(Object[] dbEntities) {
        if (dbEntities == null) {
            return;
        }
        Transaction tx = null;
        try {
            // update it
            Session session = HibernateUtil.currentSession();
            tx = session.beginTransaction();
            for (int i = 0; i < dbEntities.length; i++) {
                session.update(dbEntities[i]);
            }
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            if (log.isErrorEnabled()) {
                log.error("Problems updating DB entity, mapped entities=" + dbEntities, ex);
            }
        } finally {
            HibernateUtil.closeSession();
        }
    }

    /**
     * Save NEW mapped entity (hibernate) to database.
     * 
     * @param dbEntity
     */
    public static void saveDBObject(Object dbEntity) {
        saveDBObjects(new Object[] { dbEntity });
    }

    /**
     * Save multiple entities in ONE transaction. Rollback all saves if
     * something fails.
     * 
     * @param dbEntities
     */
    public static void saveDBObjects(Object[] dbEntities) {
        if (dbEntities == null) {
            return;
        }
        Transaction tx = null;
        try {
            // save it
            Session session = HibernateUtil.currentSession();
            tx = session.beginTransaction();
            for (int i = 0; i < dbEntities.length; i++) {
                session.save(dbEntities[i]);
            }
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            if (log.isErrorEnabled()) {
                log.error("Problems saving DB entity, mapped entities=" + dbEntities, ex);
            }
        } finally {
            HibernateUtil.closeSession();
        }
    }

    /**
     * Delete given mapped entity (hibernate) in database.
     * 
     * @param dbEntity
     */
    public static void deleteDBObject(Object dbEntity) {
        deleteDBObjects(new Object[] { dbEntity });
    }

    /**
     * Delete multiple entities in ONE transaction. Rollback all deletions if
     * something fails.
     * 
     * @param dbEntities
     */
    public static void deleteDBObjects(Object[] dbEntities) {
        if (dbEntities == null) {
            return;
        }
        Transaction tx = null;
        try {
            // delete it
            Session session = HibernateUtil.currentSession();
            tx = session.beginTransaction();
            for (int i = 0; i < dbEntities.length; i++) {
                session.delete(dbEntities[i]);
            }
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            if (log.isErrorEnabled()) {
                log.error("Problems deleting DB entities, entities=" + dbEntities, ex);
            }
        } finally {
            HibernateUtil.closeSession();
        }
    }

    /**
     * Get Partner/Provider Map. The method builds a structure as follows:
     * 
     * partnerMap (partner Ident => partnerHashMap)
     * 
     * partnerHashMap ("partner" => IngridPartner) ("providers" =>
     * providersHashMap)
     * 
     * providersHashMap (provider Ident => providerHashMap)
     * 
     * providerHashMap ("provider" => IngridProvider)
     * 
     * @return
     */
    public static LinkedHashMap getPartnerProviderMap() {
        LinkedHashMap partnerMap = new LinkedHashMap();

        List partnerList = UtilsDB.getPartners();
        List providerList = UtilsDB.getProviders();

        Iterator it = partnerList.iterator();
        while (it.hasNext()) {
            IngridPartner partner = (IngridPartner) it.next();
            LinkedHashMap partnerHash = new LinkedHashMap();
            partnerHash.put("partner", partner);
            partnerMap.put(partner.getIdent(), partnerHash);
            Iterator providerIterator = providerList.iterator();
            while (providerIterator.hasNext()) {
                IngridProvider provider = (IngridProvider) providerIterator.next();
                String providerIdent = provider.getIdent();
                String partnerIdent = "";
                if (providerIdent != null && providerIdent.length() > 0) {
                    partnerIdent = providerIdent.substring(0, providerIdent.indexOf("_"));
                    // hack: "bund" is coded as "bu" in provider idents
                    if (partnerIdent.equals("bu")) {
                        partnerIdent = "bund";
                    }
                }
                if (partnerIdent.equals(partner.getIdent())) {
                    // check for providers
                    if (!partnerHash.containsKey("providers")) {
                        partnerHash.put("providers", new LinkedHashMap());
                    }
                    // get providers of the partner
                    HashMap providersHash = (LinkedHashMap) partnerHash.get("providers");
                    // LinkedHashMap for prvider with a provider id.
                    if (!providersHash.containsKey(providerIdent)) {
                        providersHash.put(providerIdent, new LinkedHashMap());
                    }
                    // get provider hash map
                    LinkedHashMap providerHash = (LinkedHashMap) providersHash.get(providerIdent);
                    // check for provider entry, create if not exists
                    // initialise with iplug, which contains all information
                    if (!providerHash.containsKey("provider")) {
                        providerHash.put("provider", provider);
                    }
                }
            }
        }

        return partnerMap;
    }

    public static void executeRawSQL(String sqlStr) {
        Transaction tx = null;
        try {
            // delete it
            Session session = HibernateUtil.currentSession();
            tx = session.beginTransaction();
            Statement st = session.connection().createStatement();
            st.executeUpdate(session.connection().nativeSQL(sqlStr));
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            log.error("Problems executing RAW-SQL:" + sqlStr, ex);
        } finally {
            HibernateUtil.closeSession();
        }
    }

}
