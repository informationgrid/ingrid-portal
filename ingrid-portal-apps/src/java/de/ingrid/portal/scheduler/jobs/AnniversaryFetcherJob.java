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
import org.hibernate.criterion.Restrictions;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.ingrid.iplug.sns.utils.DetailedTopic;
import de.ingrid.portal.global.UtilsDate;
import de.ingrid.portal.hibernate.HibernateManager;
import de.ingrid.portal.interfaces.impl.SNSAnniversaryInterfaceImpl;
import de.ingrid.portal.om.IngridAnniversary;
import de.ingrid.utils.IngridHitDetail;

/**
 * TODO Describe your created type (class, etc.) here.
 *
 * @author joachim@wemove.com
 */
public class AnniversaryFetcherJob implements Job {

    protected final static Log log = LogFactory.getLog(AnniversaryFetcherJob.class);

    HibernateManager fHibernateManager = null;

    /**
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     */
    public void execute(JobExecutionContext arg0) throws JobExecutionException {

        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            //            cal.set(Calendar.DATE, 15);
            //            cal.set(Calendar.MONTH, 2);
            cal.add(Calendar.DATE, 1);
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

            fHibernateManager = HibernateManager.getInstance();
            Session session = this.fHibernateManager.getSession();

            IngridHitDetail[] details = SNSAnniversaryInterfaceImpl.getInstance().getAnniversaries(queryDate);
            if (details.length > 0) {

                for (int i = 0; i < details.length; i++) {
                    if ((details[i] instanceof DetailedTopic) && details[i].size() > 0) {
                        DetailedTopic detail = (DetailedTopic) details[i];
                        // check if theis item already exists
                        List anniversaryList = session.createCriteria(IngridAnniversary.class).add(
                                Restrictions.eq("topicId", detail.getTopicID())).add(
                                Restrictions.between("fetchedFor", queryDateFrom.getTime(), queryDateTo.getTime()))
                                .list();
                        if (anniversaryList.isEmpty()) {
                            IngridAnniversary anni = new IngridAnniversary();
                            anni.setTopicId(detail.getTopicID());
                            anni.setTopicName(detail.getTopicName());
                            anni.setDateFrom(detail.getFrom());
                            if (detail.getFrom() != null) {
                                Date fromDate = UtilsDate.parseDateString(detail.getFrom());
                                cal.setTime(fromDate);
                                anni.setDateFromYear(new Integer(cal.get(Calendar.YEAR)));
                                anni.setDateFromMonth(new Integer(cal.get(Calendar.MONTH) + 1));
                                anni.setDateFromDay(new Integer(cal.get(Calendar.DAY_OF_MONTH)));
                            }
                            anni.setDateTo(detail.getTo());
                            if (detail.getTo() != null) {
                                Date toDate = UtilsDate.parseDateString(detail.getTo());
                                cal.setTime(toDate);
                                anni.setDateToYear(new Integer(cal.get(Calendar.YEAR)));
                                anni.setDateToMonth(new Integer(cal.get(Calendar.MONTH) + 1));
                                anni.setDateToDay(new Integer(cal.get(Calendar.DAY_OF_MONTH)));
                            }
                            anni.setAdministrativeId(detail.getAdministrativeID());
                            anni.setFetched(new Date());
                            anni.setFetchedFor(queryDate);

                            session.beginTransaction();
                            session.save(anni);
                            session.getTransaction().commit();
                        }
                    }
                }
            }
            // remove old entries
            cal.setTime(new Date());
            cal.add(Calendar.DATE, -1);
            List deleteEntries = session.createCriteria(IngridAnniversary.class).add(
                    Restrictions.lt("fetchedFor", cal.getTime())).list();
            Iterator it = deleteEntries.iterator();
            session.beginTransaction();
            while (it.hasNext()) {
                session.delete((IngridAnniversary) it.next());
            }
            session.getTransaction().commit();

        } catch (Exception e) {
            log.error("Error executing quartz job AnniversaryFetcherJob.", e);
            throw new JobExecutionException("Error executing quartz job AnniversaryFetcherJob.", e, false);
        }
    }

}
