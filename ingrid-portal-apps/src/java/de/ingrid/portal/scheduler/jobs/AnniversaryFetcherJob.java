/*
 * Copyright (c) 1997-2006 by wemove GmbH
 */
package de.ingrid.portal.scheduler.jobs;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.ingrid.iplug.sns.utils.DetailedTopic;
import de.ingrid.portal.hibernate.HibernateUtil;
import de.ingrid.portal.interfaces.impl.SNSAnniversaryInterfaceImpl;
import de.ingrid.portal.om.IngridAnniversary;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.udk.UtilsDate;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class AnniversaryFetcherJob implements Job {

    protected final static Log log = LogFactory.getLog(AnniversaryFetcherJob.class);

    /**
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     */
    public void execute(JobExecutionContext arg0) throws JobExecutionException {

        insertIntoDB("de");
        insertIntoDB("en");

    }

    private void insertIntoDB(String lang) throws JobExecutionException {

        Session session = HibernateUtil.currentSession();
        Transaction tx = null;

        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            Date queryDate = cal.getTime();

            Calendar queryDateFrom = Calendar.getInstance();
            queryDateFrom.setTime(queryDate);
            queryDateFrom.set(Calendar.HOUR_OF_DAY, 0);
            queryDateFrom.set(Calendar.MINUTE, 0);
            queryDateFrom.set(Calendar.SECOND, 0);
            queryDateFrom.set(Calendar.MILLISECOND, 0);
            Calendar queryDateTo = Calendar.getInstance();
            queryDateTo.setTime(queryDate);
            queryDateTo.set(Calendar.HOUR_OF_DAY, 23);
            queryDateTo.set(Calendar.MINUTE, 59);
            queryDateTo.set(Calendar.SECOND, 59);
            queryDateTo.set(Calendar.MILLISECOND, 0);
            IngridHitDetail[] details = SNSAnniversaryInterfaceImpl.getInstance().getAnniversaries(queryDate, lang);
            if (details.length > 0) {

                for (int i = 0; i < details.length; i++) {
                    if ((details[i] instanceof DetailedTopic) && details[i].size() > 0) {
                        DetailedTopic detail = (DetailedTopic) details[i];
                        log.debug("Anniversary gefunden! (topic:'" + detail.getTopicID() + "', lang:" + lang + ")");
                        // check if theis item already exists
                        tx = session.beginTransaction();
                        List anniversaryList = session.createCriteria(IngridAnniversary.class).add(
                                Restrictions.eq("topicId", detail.getTopicID())).add(
                                Restrictions.eq("language", lang)).add(
                                Restrictions.between("fetchedFor", queryDateFrom.getTime(), queryDateTo.getTime()))
                                .list();
                        tx.commit();
                        if (anniversaryList.isEmpty()) {
                            IngridAnniversary anni = new IngridAnniversary();
                            anni.setTopicId(detail.getTopicID());
                            anni.setTopicName(detail.getTopicName());
                            String from = detail.getFrom();
                            anni.setDateFrom(from);
                            if (from != null) {
                                // !!! trim, sns date may have white spaces !!! :-(
                                anni.setDateFrom(from.trim());
                                Date fromDate = UtilsDate.parseDateString(from);
                                cal.setTime(fromDate);
                                anni.setDateFromYear(new Integer(cal.get(Calendar.YEAR)));
                                anni.setDateFromMonth(new Integer(cal.get(Calendar.MONTH) + 1));
                                anni.setDateFromDay(new Integer(cal.get(Calendar.DAY_OF_MONTH)));
                            }
                            String to = detail.getTo();
                            anni.setDateTo(to);
                            if (to != null) {
                                // !!! trim, sns date may have white spaces !!! :-(
                                anni.setDateTo(to.trim());
                                Date toDate = UtilsDate.parseDateString(to);
                                cal.setTime(toDate);
                                anni.setDateToYear(new Integer(cal.get(Calendar.YEAR)));
                                anni.setDateToMonth(new Integer(cal.get(Calendar.MONTH) + 1));
                                anni.setDateToDay(new Integer(cal.get(Calendar.DAY_OF_MONTH)));
                            }
                            anni.setAdministrativeId(detail.getAdministrativeID());
                            anni.setFetched(new Date());
                            anni.setFetchedFor(queryDate);
                            anni.setLanguage(lang);

                            tx = session.beginTransaction();
                            session.save(anni);
                            tx.commit();
                        }
                    }
                }
            } else {
                if (log.isWarnEnabled()) {
                    log.warn("!!! SNS query: NO Anniversaries found for date = " + queryDate + " !!!");
                }
            }
            // remove old entries
            cal.setTime(new Date());
            cal.add(Calendar.DATE, -1);
            tx = session.beginTransaction();
            List deleteEntries = session.createCriteria(IngridAnniversary.class).add(
                    Restrictions.lt("fetchedFor", cal.getTime())).list();
            tx.commit();
            Iterator it = deleteEntries.iterator();
            tx = session.beginTransaction();
            while (it.hasNext()) {
                session.delete((IngridAnniversary) it.next());
            }
            tx.commit();

        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            log.error("Error executing quartz job AnniversaryFetcherJob.", e);
            throw new JobExecutionException("Error executing quartz job AnniversaryFetcherJob.", e, false);
        } finally {
            HibernateUtil.closeSession();
        }
    }

}
