/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2018 wemove digital solutions GmbH
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

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.hibernate.HibernateUtil;
import de.ingrid.portal.om.IngridChronicleEventType;
import de.ingrid.portal.om.IngridEnvTopic;
import de.ingrid.portal.om.IngridFormToQuery;
import de.ingrid.portal.om.IngridMeasuresRubric;
import de.ingrid.portal.om.IngridPartner;
import de.ingrid.portal.om.IngridProvider;
import de.ingrid.portal.om.IngridServiceRubric;

/**
 * Global STATIC data and utility methods for Database.
 * 
 * @author Martin Maidhof
 */
public class UtilsDB {

    private final static Logger log = LoggerFactory.getLogger(UtilsDB.class);

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
            	try {
            		if(session != null){
            			if(session.connection() != null){
            				if(!session.connection().isClosed()){
        						HibernateUtil.closeSession();	
        					}
            			}
            		}
				} catch (HibernateException e) {
					log.error("HibernateException: "+ e);
				} catch (SQLException e) {
					log.error("SQLException: While trying to close hibernate session.");
				}
            	
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
        return getValuesFromDB(session.createCriteria(IngridProvider.class).addOrder(Order.asc("name")), session,
                providers, true);
    }

    /**
     * Get alle providers that corresponded with a partner key. NOTICE:
     * comparison is case insensitive !
     * 
     * @param key
     *            The partner key (use 'bund')
     * @return The providers of the partner.
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
     * Get all the environment topics sorted alphabetically by the localized query value.
     * The query value of the IngridEnvTopic instances are overwritten with the localized values.
     * If resources is NULL, nor localization/sorting will occur.
     * 
     * @param resources
     * @return
     */
    public static List getEnvTopics(IngridResourceBundle resources) {
        // NOTICE: assign list to our static variable, passed static variable
        // may be null,
        // so there's no call by reference !
        envTopics = getValuesFromDB(IngridEnvTopic.class, envTopics);
        if (resources == null) {
        	return envTopics;
        }
        for (int i=0; i<envTopics.size(); i++) {
        	IngridEnvTopic topic = (IngridEnvTopic)envTopics.get(i);
        	topic.setQueryValue(resources.getString(topic.getQueryValue()));
        }
        Collections.sort(envTopics, new IngridEnvTopicComparator());
        return envTopics;
    }

    private static class IngridEnvTopicComparator implements Comparator {
        /**
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public final int compare(Object a, Object b) {
            String sa = ((IngridEnvTopic)a).getQueryValue();
            String sb = ((IngridEnvTopic)b).getQueryValue();
 
            return sa.compareTo(sb);
        }
    }
    
    
    /**
     * Get the query value of a topic from the form value (key).
     * 
     * @param key
     * @return
     */
    public static String getTopicFromKey(String key) {
        List envTopics = getEnvTopics(null);
        return getQueryValueFromFormValue(envTopics, key);
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
     * Get Partner/Provider Map.
     * 
     * The partners can be filtered by the partner identification.
     * 
     * The method builds a structure as follows:
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
     * @param partnerFilter
     *            List of partner identifications. If filter is null, alle
     *            partners are returned.
     * @return
     */
    public static LinkedHashMap getPartnerProviderMap(ArrayList partnerFilter) {
        LinkedHashMap partnerMap = new LinkedHashMap();

        List partnerList = UtilsDB.getPartners();
        List providerList = UtilsDB.getProviders();

        Iterator it = partnerList.iterator();
        while (it.hasNext()) {
            IngridPartner partner = (IngridPartner) it.next();
            if (partnerFilter == null || partnerFilter.contains(partner.getIdent())) {
                LinkedHashMap partnerHash = new LinkedHashMap();
                partnerHash.put("partner", partner);
                partnerMap.put(partner.getIdent(), partnerHash);
                Iterator providerIterator = providerList.iterator();
                while (providerIterator.hasNext()) {
                    IngridProvider provider = (IngridProvider) providerIterator.next();
                    String providerIdent = provider.getIdent();
                    String partnerIdent = "";
                    int sepPos = providerIdent.indexOf("_");
                    if (providerIdent != null && providerIdent.length() > 0 && sepPos != -1) {
                        partnerIdent = providerIdent.substring(0, sepPos);
                        // hack: "bund" is coded as "bu" in provider idents
                        partnerIdent = Utils.normalizePartnerKey(partnerIdent, false);
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
        }

        return partnerMap;
    }

    /**
     * Execute RAW UPDATE-SQL. Make sure the SQL Statement is valid.
     * 
     * @param sqlStr
     *            The SQL String.
     */
    public static void executeRawUpdateSQL(String sqlStr) {
        Transaction tx = null;
        try {
            // delete it
            Session session = HibernateUtil.currentSession();
            tx = session.beginTransaction();
            Statement st = session.connection().createStatement();
            if (log.isDebugEnabled()) {
                log.debug("Execute SQL: " + sqlStr);
            }
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

    /** Do we use a Oracle database ? */
    public static boolean isOracle() {
		return HibernateUtil.isOracle();
	}

    /** Do we use a Postgres database ? */
    public static boolean isPostgres() {
        return HibernateUtil.isPostgres();
    }
}
