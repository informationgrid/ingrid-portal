/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.interfaces.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import de.ingrid.iplug.sns.utils.DetailedTopic;
import de.ingrid.portal.hibernate.HibernateUtil;
import de.ingrid.portal.interfaces.AnniversaryInterface;
import de.ingrid.portal.om.IngridAnniversary;
import de.ingrid.utils.IngridHitDetail;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class DBAnniversaryInterfaceImpl implements AnniversaryInterface {

    private final static Log log = LogFactory.getLog(DBAnniversaryInterfaceImpl.class);

    private static AnniversaryInterface instance = null;

    public static synchronized AnniversaryInterface getInstance() {
        if (instance == null) {
            try {
                instance = new DBAnniversaryInterfaceImpl();
            } catch (Exception e) {
                log.fatal("Error initiating the WMS interface.");
                e.printStackTrace();
            }
        }
        return instance;
    }

    private DBAnniversaryInterfaceImpl() throws Exception {
        super();
    }

    /**
     * @see de.ingrid.portal.interfaces.SimilarTermsInterface#getAnniversary(java.sql.Date)
     */
    public IngridHitDetail[] getAnniversaries(Date d) {

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
                    .add(Restrictions.eq("dateFromDay", new Integer(fromCal.get(Calendar.DAY_OF_MONTH))))
                    .add(Restrictions.eq("dateFromMonth", new Integer(fromCal.get(Calendar.MONTH) + 1)))
                    .add(Restrictions.between("fetchedFor", queryDateFrom.getTime(), queryDateTo.getTime()))
                    .list();
            tx.commit();
            
            if (anniversaryList.isEmpty()) {
                // fall back: get any event of this month
                fromCal.setTime(d);
                toCal.setTime(d);

                tx = session.beginTransaction();
                anniversaryList = session.createCriteria(IngridAnniversary.class)
                        .add(Restrictions.between("dateFromMonth", new Integer(fromCal.get(Calendar.MONTH) + 1), new Integer(toCal.get(Calendar.MONTH) + 1)))
                        .add(Restrictions.sqlRestriction("length({alias}.date_from) > 4"))
                        // FIX, DO WE NEED THIS ! IS WRONG ?
//                        .add(Restrictions.between("fetchedFor", queryDateFrom.getTime(), queryDateTo.getTime()))
                        .list();
                tx.commit();

                if (anniversaryList.isEmpty()) {
                    // fall back: get all events that only define a year
                    fromCal.setTime(d);

                    tx = session.beginTransaction();
                    anniversaryList = session.createCriteria(IngridAnniversary.class)
                            .add(Restrictions.between("fetchedFor", queryDateFrom.getTime(), queryDateTo.getTime()))
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
