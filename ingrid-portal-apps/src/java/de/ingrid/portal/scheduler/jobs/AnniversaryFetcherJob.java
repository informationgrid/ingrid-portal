/*
 * Copyright (c) 1997-2006 by wemove GmbH
 */
package de.ingrid.portal.scheduler.jobs;

import java.util.Calendar;
import java.util.Date;
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
            cal.add(Calendar.DATE, 1);
    
            fHibernateManager = HibernateManager.getInstance();
            Session session = this.fHibernateManager.getSession();
            
            IngridHitDetail[] details = SNSAnniversaryInterfaceImpl.getInstance().getAnniversaries(cal.getTime());
            if (details.length > 0) {
                for (int i=0; i<details.length; i++) {
                    if ((details[i] instanceof DetailedTopic) &&  details[i].size() > 0) {
                        DetailedTopic detail = (DetailedTopic) details[i];
                        List anniversaryList = session.createCriteria(IngridAnniversary.class).add(
                                Restrictions.eq("id", detail.getTopicID())).list();
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
    
                            session.beginTransaction();
                            session.save(anni);
                            session.getTransaction().commit();
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error executing quartz job AnniversaryFetcherJob.", e);
//            throw new JobExecutionException("Error executing quartz job AnniversaryFetcherJob.", e, true);
        }
    }

}
