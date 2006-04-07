/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.global;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.hibernate.HibernateManager;
import de.ingrid.portal.om.IngridChronicleEventType;
import de.ingrid.portal.om.IngridEnvFunctCategory;
import de.ingrid.portal.om.IngridEnvTopic;
import de.ingrid.portal.om.IngridFormToQuery;
import de.ingrid.portal.om.IngridMeasuresRubric;
import de.ingrid.portal.om.IngridPartner;
import de.ingrid.portal.om.IngridServiceRubric;

/**
 * Global STATIC data and utility methods for Database.
 * 
 * @author Martin Maidhof
 */
public class UtilsDB {

    /** this flag controls whether Data is always fetched from Database or from cache  */
    // TODO keep possibilty to always reload data from DB ???? makes it complicated ! maybe instable ?
    private static boolean alwaysReloadDBData = PortalConfig.getInstance().getBoolean(
            PortalConfig.ALWAYS_REREAD_DB_VALUES);

    // TODO: optimize ? use hash map instead of separate lists !

    /** cache for partners */
    private static List partners = null;

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
     * NOTICE: values are always reloaded from database if flag alwaysReloadDBData
     * in our class is set to true. Otherwise we load the values once and return
     * our cache !
     * @param objClass
     * @param cacheList
     * @return
     */
    private static List getValuesFromDB(Class objClass, List cacheList) {
        // TODO: use hibernate caching
        if (alwaysReloadDBData) {
            HibernateManager hibernateManager = HibernateManager.getInstance();
            return hibernateManager.loadAllData(objClass, 0);
        }
        synchronized (UtilsDB.class) {
            if (cacheList == null) {
                HibernateManager hibernateManager = HibernateManager.getInstance();
                cacheList = hibernateManager.loadAllData(objClass, 0);
            }
        }
        return cacheList;
    }

    private static List getValuesFromDB(Criteria c, List cacheList) {
        // TODO: use hibernate caching
        if (alwaysReloadDBData) {
            return c.list();
        }
        synchronized (UtilsDB.class) {
            if (cacheList == null) {
                cacheList = c.list();
            }
        }
        return cacheList;
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
     * Get all the partners.
     * @return
     */
    public static List getPartners() {
        // NOTICE: assign list to our static variable, passed static variable may be null,
        // so there's no call by reference !
        return getValuesFromDB(HibernateManager.getInstance().getSession().createCriteria(IngridPartner.class).addOrder(Order.asc("sortkey")), partners);
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
}
