/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.global;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.hibernate.HibernateUtil;
import de.ingrid.portal.interfaces.impl.DBAnniversaryInterfaceImpl;
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

    private final static Log log = LogFactory.getLog(DBAnniversaryInterfaceImpl.class);

    /** this flag controls whether Data is always fetched from Database or from cache  */
    // TODO keep possibilty to always reload data from DB ???? makes it complicated ! maybe instable ?
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
     * Convenient generic method for reading stuff from database.
     * NOTICE: If passed cacheList is null, we always reload data from database.
     * If not null we only reload if flag alwaysReloadDBData in our class
     * is set to true. If flag is set false we just return cacheList. 
     * @param objClass
     * @param cacheList
     * @return
     */
    public static List getValuesFromDB(Class objClass, List cacheList) {
        Session session = HibernateUtil.currentSession();
        return getValuesFromDB(session.createCriteria(objClass), session, cacheList, true);
    }

    /**
     * Convenient generic method for reading stuff from database.
     * NOTICE: If passed cacheList is null, we always reload data from database.
     * If not null we only reload if flag alwaysReloadDBData in our class
     * is set to true. If flag is set false we just return cacheList. 
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
     * Convenient method for extracting a QueryValue from a List of IngridFormToQuery
     * objects, based on the formValue.
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
     * Convenient method for extracting a FormValue from a List of IngridFormToQuery
     * objects, based on the QueryValue.
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
     * @return
     */
    public static List getPartners() {
        // NOTICE: assign list to our static variable, passed static variable may be null,
        // so there's no call by reference !
        Session session = HibernateUtil.currentSession();
        return getValuesFromDB(session.createCriteria(IngridPartner.class).addOrder(Order.asc("sortkey")), session,
                partners, true);
    }

    /**
     * Get the name of the partner with the according ident.
     * @param ident
     * @return the name of the partner OR the key, if no matching partner was found
     */
    public static String getPartnerFromKey(String ident) {
        List partners = getPartners();
        IngridPartner partner = null;
        for (int i = 0; i < partners.size(); i++) {
            partner = (IngridPartner) partners.get(i);
            if (partner.getIdent().equals(ident)) {
                return partner.getName();
            }
        }
        return ident;
    }

    /**
     * Get all the providers. Sort by sortkeyPartner, name !
     * @return
     */
    public static List getProviders() {
        // NOTICE: assign list to our static variable, passed static variable may be null,
        // so there's no call by reference !
        Session session = HibernateUtil.currentSession();
        return getValuesFromDB(session.createCriteria(IngridProvider.class).addOrder(Order.asc("sortkeyPartner"))
                .addOrder(Order.asc("name")), session, providers, true);
    }

    /**
     * Get the name of the Provider with the according ident.
     * @param ident
     * @return the name of the provider OR the key, if no matching provider was found
     */
    public static String getProviderFromKey(String ident) {
        List providers = getProviders();
        IngridProvider provider = null;
        for (int i = 0; i < providers.size(); i++) {
            provider = (IngridProvider) providers.get(i);
            if (provider.getIdent().equals(ident)) {
                return provider.getName();
            }
        }
        return ident;
    }

    /**
     * Get the Provider Object with the according ident.
     * @param ident
     * @return The IngridPrivider OR null if no matching was found
     */
    public static IngridProvider getIngridProviderFromKey(String ident) {
        List providers = getProviders();
        IngridProvider provider = null;
        for (int i = 0; i < providers.size(); i++) {
            provider = (IngridProvider) providers.get(i);
            if (provider.getIdent().equals(ident)) {
                return provider;
            }
        }
        return null;
    }

    /**
     * Get all the environment topics.
     * @return
     */
    public static List getEnvTopics() {
        // NOTICE: assign list to our static variable, passed static variable may be null,
        // so there's no call by reference !
        envTopics = getValuesFromDB(IngridEnvTopic.class, envTopics);
        return envTopics;
    }

    /**
     * Get the query value of a topic from the form value (key).
     * @param key
     * @return
     */
    public static String getTopicFromKey(String key) {
        List envTopics = getEnvTopics();
        return getQueryValueFromFormValue(envTopics, key);
    }

    /**
     * Get all the environment functional categories.
     * @return
     */
    public static List getEnvFunctCategories() {
        // NOTICE: assign list to our static variable, passed static variable may be null,
        // so there's no call by reference !
        envFunctCategories = getValuesFromDB(IngridEnvFunctCategory.class, envFunctCategories);
        return envFunctCategories;
    }

    /**
     * Get the query value of a functional category from the form value (key).
     * @param key
     * @return
     */
    public static String getFunctCategoryFromKey(String key) {
        List envCategories = getEnvFunctCategories();
        return getQueryValueFromFormValue(envCategories, key);
    }

    /**
     * Get the service rubrics.
     * @return
     */
    public static List getServiceRubrics() {
        // NOTICE: assign list to our static variable, passed static variable may be null,
        // so there's no call by reference !
        serviceRubrics = getValuesFromDB(IngridServiceRubric.class, serviceRubrics);
        return serviceRubrics;
    }

    /**
     * Get the query value of a service rubric from the form value (key).
     * @param key
     * @return
     */
    public static String getServiceRubricFromKey(String key) {
        List rubrics = getServiceRubrics();
        return getQueryValueFromFormValue(rubrics, key);
    }

    /**
     * Get the measures rubrics.
     * @return
     */
    public static List getMeasuresRubrics() {
        // NOTICE: assign list to our static variable, passed static variable may be null,
        // so there's no call by reference !
        measuresRubrics = getValuesFromDB(IngridMeasuresRubric.class, measuresRubrics);
        return measuresRubrics;
    }

    /**
     * Get the query value of a measures rubric from the form value (key).
     * @param key
     * @return
     */
    public static String getMeasuresRubricFromKey(String key) {
        List rubrics = getMeasuresRubrics();
        return getQueryValueFromFormValue(rubrics, key);
    }

    /**
     * Get the event types for chronicle.
     * @return
     */
    public static List getChronicleEventTypes() {
        // NOTICE: assign list to our static variable, passed static variable may be null,
        // so there's no call by reference !
        chronicleEventTypes = getValuesFromDB(IngridChronicleEventType.class, chronicleEventTypes);
        return chronicleEventTypes;
    }

    /**
     * Get the query value of an event type from the form value (key).
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

}
