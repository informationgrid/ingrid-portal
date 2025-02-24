/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.portal.scheduler.jobs;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class AnniversaryFetcherJob extends IngridMonitorAbstractJob {

    protected static final Logger log = LoggerFactory.getLogger(AnniversaryFetcherJob.class);

    /**
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     */
    public void execute(JobExecutionContext context) throws JobExecutionException {
    	JobDataMap dataMap = context.getJobDetail().getJobDataMap();
    	
    	startTimer();
    	
        if (log.isDebugEnabled()) {
        	log.debug("start job: AnniversaryFetcherJob.");
        }
        
        int status = 0;
		String statusCode = null;
        try {
        	insertIntoDB("de");
        	insertIntoDB("en");
        	status = STATUS_OK;
        	statusCode = STATUS_CODE_NO_ERROR;
        } catch (JobExecutionException e) {
        	status = STATUS_ERROR;
			statusCode = STATUS_CODE_ERROR_UNSPECIFIC;
        }
        if (log.isDebugEnabled()) {
        	log.debug("finish job: AnniversaryFetcherJob.");
        }
        computeTime(dataMap, stopTimer());
        updateJobData(context, status, statusCode);
    	updateJob(context);
    }
    
    private void insertIntoDB(String lang) throws JobExecutionException {

        Session session = HibernateUtil.currentSession();
        Transaction tx = null;

        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            int thisYear = cal.get(Calendar.YEAR);
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
                        if (log.isDebugEnabled()) {
                        	log.debug("Anniversary gefunden! (topic:'" + detail.getTopicID() + "', lang:" + lang + ")");
                        }
                        // check if theis item already exists
                        tx = session.beginTransaction();
                        List anniversaryList = session.createCriteria(IngridAnniversary.class).add(
                                Restrictions.eq("topicId", detail.getTopicID())).add(
                                Restrictions.eq("language", lang))
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
                                from = from.trim();
                                anni.setDateFrom(from);
                                Date fromDate = UtilsDate.parseDateString(from);
                                cal.setTime(fromDate);
                                anni.setDateFromYear(cal.get(Calendar.YEAR));
                                anni.setDateFromMonth(cal.get(Calendar.MONTH) + 1);
                                anni.setDateFromDay(cal.get(Calendar.DAY_OF_MONTH));
                            }
                            if (thisYear == cal.get(Calendar.YEAR)) {
                                if (log.isDebugEnabled()) {
                                    log.debug("Skip Anniversary for (topic:'" + detail.getTopicID() + "', lang:" + lang + ") because the year of the event equals the current year.");
                                }
                            }else{
                                String to = detail.getTo();
                                anni.setDateTo(to);
                                if (to != null) {
                                    // !!! trim, sns date may have white spaces !!! :-(
                                    to = to.trim();
                                    anni.setDateTo(to);
                                    Date toDate = UtilsDate.parseDateString(to);
                                    cal.setTime(toDate);
                                    anni.setDateToYear(cal.get(Calendar.YEAR));
                                    anni.setDateToMonth(cal.get(Calendar.MONTH) + 1);
                                    anni.setDateToDay(cal.get(Calendar.DAY_OF_MONTH));
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
                }
            } else {
                if (log.isWarnEnabled()) {
                    log.warn("!!! SNS query: NO Anniversaries found for date = " + queryDate + " !!!");
                }
            }
            // remove old entries
            cal.setTime(new Date());
            cal.add(Calendar.DATE, -7);
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
            throw new JobExecutionException("Error executing quartz job AnniversaryFetcherJob.", e, false);
        } finally {
            HibernateUtil.closeSession();
        }
    }

}
