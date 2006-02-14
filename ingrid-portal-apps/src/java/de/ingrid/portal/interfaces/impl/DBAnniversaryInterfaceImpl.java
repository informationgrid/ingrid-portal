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
import org.hibernate.criterion.Restrictions;

import de.ingrid.iplug.sns.utils.DetailedTopic;
import de.ingrid.portal.hibernate.HibernateManager;
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

    HibernateManager fHibernateManager = null;

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

        try {
            fHibernateManager = HibernateManager.getInstance();
            Session session = this.fHibernateManager.getSession();

            Calendar fromCal = Calendar.getInstance();
            Calendar toCal = Calendar.getInstance();

            fromCal.setTime(d);

            List anniversaryList = session.createCriteria(IngridAnniversary.class).add(
                    Restrictions.eq("dateFromDay", new Integer(fromCal.get(Calendar.DAY_OF_MONTH)))).add(
                    Restrictions.eq("dateFromMonth", new Integer(fromCal.get(Calendar.MONTH)))).list();
            if (anniversaryList.isEmpty()) {
                fromCal.setTime(d);
                fromCal.add(Calendar.MONTH, -1);
                toCal.setTime(d);
                toCal.add(Calendar.MONTH, 1);

                anniversaryList = session.createCriteria(IngridAnniversary.class).add(
                        Restrictions.between("dateFromDay", new Integer(fromCal.get(Calendar.DAY_OF_MONTH)),
                                new Integer(toCal.get(Calendar.DAY_OF_MONTH)))).add(
                        Restrictions.between("dateFromMonth", new Integer(fromCal.get(Calendar.MONTH)), new Integer(
                                toCal.get(Calendar.MONTH)))).list();

                if (anniversaryList.isEmpty()) {
                    fromCal.setTime(d);

                    anniversaryList = session.createCriteria(IngridAnniversary.class).list();
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
            }

            return rslt;

        } catch (Exception e) {
            log.error("Exception while querying sns for anniversary.", e);
            return new DetailedTopic[0];
        }
    }

}
