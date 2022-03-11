/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2022 wemove digital solutions GmbH
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
package de.ingrid.portal.interfaces.impl;

import de.ingrid.iplug.sns.utils.DetailedTopic;
import de.ingrid.portal.hibernate.HibernateUtil;
import de.ingrid.portal.interfaces.AnniversaryInterface;
import de.ingrid.portal.om.IngridAnniversary;
import de.ingrid.utils.IngridHitDetail;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class DBAnniversaryInterfaceImpl implements AnniversaryInterface {

    private static final Logger log = LoggerFactory.getLogger(DBAnniversaryInterfaceImpl.class);

    private static AnniversaryInterface instance = null;

    public static synchronized AnniversaryInterface getInstance() {
        if (instance == null) {
            try {
                instance = new DBAnniversaryInterfaceImpl();
            } catch (Exception e) {
                log.error("Error initiating the WMS interface.", e);
            }
        }
        return instance;
    }

    private DBAnniversaryInterfaceImpl() {
        super();
    }

    /**
     * @see de.ingrid.portal.interfaces.SimilarTermsInterface#getAnniversary(java.sql.Date)
     */
    public IngridHitDetail[] getAnniversaries(Date d, String lang) {

        Session session = HibernateUtil.currentSession();
        Transaction tx = null;
        try {
            Calendar queryDateFrom = Calendar.getInstance();
            queryDateFrom.set(Calendar.HOUR_OF_DAY, 0);
            queryDateFrom.set(Calendar.MINUTE, 0);
            queryDateFrom.set(Calendar.SECOND, 0);
            queryDateFrom.set(Calendar.MILLISECOND, 0);
            Calendar queryDateTo = Calendar.getInstance();
            queryDateTo.set(Calendar.HOUR_OF_DAY, 23);
            queryDateTo.set(Calendar.MINUTE, 59);
            queryDateTo.set(Calendar.SECOND, 59);
            queryDateTo.set(Calendar.MILLISECOND, 0);

            Calendar fromCal = Calendar.getInstance();
            Calendar toCal = Calendar.getInstance();

            fromCal.setTime(d);
            
            tx = session.beginTransaction();
            List anniversaryList = session.createCriteria(IngridAnniversary.class)
                    .add(Restrictions.eq("dateFromDay", fromCal.get(Calendar.DAY_OF_MONTH)))
                    .add(Restrictions.eq("dateFromMonth", fromCal.get(Calendar.MONTH) + 1))
                    .add(Restrictions.eq("language", lang))
                    .list();
            tx.commit();
            
            if (anniversaryList.isEmpty()) {
                // fall back: get any event of this month
                fromCal.setTime(d);
                toCal.setTime(d);

                tx = session.beginTransaction();
                anniversaryList = session.createCriteria(IngridAnniversary.class)
                        .add(Restrictions.between("dateFromMonth", fromCal.get(Calendar.MONTH) + 1, toCal.get(Calendar.MONTH) + 1))
                        .add(Restrictions.sqlRestriction("length({alias}.date_from) > 4"))
                        .add(Restrictions.eq("language", lang))
                        .list();
                tx.commit();

                if (anniversaryList.isEmpty()) {
                    // fall back: get any event that has been fetched for today
                    fromCal.setTime(d);

                    tx = session.beginTransaction();
                    anniversaryList = session.createCriteria(IngridAnniversary.class)
                            .add(Restrictions.eq("language", lang))
                            .list();
                    tx.commit();
                }
            }
            
            IngridHitDetail[] rslt = new IngridHitDetail[anniversaryList.size()];
            for (int i = 0; i < rslt.length; i++) {
                IngridAnniversary anni = (IngridAnniversary) anniversaryList.get(i);
                rslt[i] = new DetailedTopic();
                rslt[i].put("topicId", anni.getTopicId());
                rslt[i].put("topicName", anni.getTopicName());
                rslt[i].put("from", anni.getDateFrom());
                rslt[i].put("until", anni.getDateTo());
                rslt[i].put("until", anni.getDateTo());
                session.evict(anni);
            }

            return rslt;

        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            if (log.isErrorEnabled()) {
                log.error("Exception while querying sns for anniversary.", e);
            }
            return new DetailedTopic[0];
        } finally {
            HibernateUtil.closeSession();                
        }
    }

}
