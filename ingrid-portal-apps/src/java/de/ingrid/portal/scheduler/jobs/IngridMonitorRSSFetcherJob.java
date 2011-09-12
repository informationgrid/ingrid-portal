/*
 * Copyright (c) 2008 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.scheduler.jobs;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.ingrid.portal.hibernate.HibernateUtil;
import de.ingrid.portal.om.IngridRSSStore;
import de.ingrid.portal.scheduler.IngridMonitorFacade;


/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class IngridMonitorRSSFetcherJob extends IngridMonitorAbstractJob {

	public static final String COMPONENT_TYPE = "component.monitor.general.type.rssfetcher";
	
	public static final String PARAM_PREVIOUS_NUM = "component.monitor.rsschecker.num";
	
	public static final String PARAM_PREVIOUS_DATE = "component.monitor.rsschecker.date";
	
	public static final String JOB_ID = "RSS-Fetcher-Monitor";

	private final static Logger log = LoggerFactory.getLogger(IngridMonitorRSSFetcherJob.class);

	/**
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	public void execute(JobExecutionContext context) throws JobExecutionException {
		long startTime = 0;
		
		if (log.isDebugEnabled()) {
			startTime = System.currentTimeMillis();
			log.debug("Job (" + context.getJobDetail().getName() + ") is executed...");
		}
		
		JobDetail jobDetail = context.getJobDetail();
		JobDataMap dataMap = jobDetail.getJobDataMap();
		String url 			= dataMap.getString(PARAM_SERVICE_URL);
		
		int status = 0;
		String statusCode = null;

        try {
          	startTimer();
              
            status = STATUS_OK;
			statusCode = STATUS_CODE_NO_ERROR;
            
			int previousNum = -1;
			Date previousFireTime = null;
			Date nextFireTime = null;
			
			if (dataMap.containsKey(PARAM_PREVIOUS_NUM)) previousNum = dataMap.getInt(PARAM_PREVIOUS_NUM);
			if (dataMap.containsKey(PARAM_PREVIOUS_DATE)) previousFireTime = (Date)dataMap.get(PARAM_PREVIOUS_DATE);

			int num = context.getScheduler().getJobDetail("RSSFetcherJob", "DEFAULT").getJobDataMap().getInt(PARAM_TIMER_NUM);
			
			// return if there is 
			if (context.getScheduler().getTriggersOfJob("RSSFetcherJob", "DEFAULT") != null ) 
				nextFireTime = context.getScheduler().getTriggersOfJob("RSSFetcherJob", "DEFAULT")[0].getNextFireTime();
			else
				return;
            
			// TODO AW: is the job executing right now?
			IngridMonitorFacade monitor = IngridMonitorFacade.instance();
			if (!monitor.isExecuting("RSSFetcherJob", "DEFAULT", context)) {
				// if it's the first start
	            if (previousFireTime == null)
	            	previousFireTime = nextFireTime;
				
	            // num must have changed after last time
	            if (nextFireTime.before(new Date())) {
	            	status = STATUS_ERROR;
	    			statusCode = "NextFireTime is in the past!";
	            }
	            
	            // what if restarting tomcat? num will be back to 1
	            if (num == previousNum && nextFireTime.compareTo(previousFireTime)!=0) {
	            	status = STATUS_ERROR;
	    			statusCode = "Number of executions is not increasing!";
	    			dataMap.put(PARAM_PREVIOUS_DATE, nextFireTime);
	        	}
	            
	            if (status == STATUS_OK && nextFireTime.compareTo(previousFireTime)!=0) {
	               dataMap.put(PARAM_PREVIOUS_DATE, nextFireTime);
	               dataMap.put(PARAM_PREVIOUS_NUM, num);
	            }
	            
	            // check database for last change
	            Session session = HibernateUtil.currentSession();
	            Transaction tx = null;

	            // RSS feeds must not be older than 2 days
	            // exception is after a weekend so that it might be up to 4 days
	            int days = 2;
	            Calendar cal = Calendar.getInstance();
	            cal.setTime(new Date());
	            if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) days = 3;
	            else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) days = 4;
	            
	            tx = session.beginTransaction();
	            IngridRSSStore rssStore = null;
	            
	            // get the most recent RSS News
	    		List rssStores = session.createCriteria(IngridRSSStore.class).addOrder(Order.desc("publishedDate")).setMaxResults(1).list();
	    		rssStore = (IngridRSSStore)rssStores.get(0);
	    		
	    		cal.setTime(rssStore.getPublishedDate());
	    		cal.add(Calendar.HOUR, days*24);
	    		if (cal.getTime().before(new Date())) {
	    			status = STATUS_ERROR;
	    			statusCode = "No new RSS Feeds for more than " + days + " days!";
	    		}
		        tx.commit();
			} else {
				if (log.isInfoEnabled()) {
	                log.info("RSS FetcherJob is executing right now!");
	            }
			}
            
        } catch (Exception e) {
            if (log.isInfoEnabled()) {
                log.info("Error building RSS feed (" + url + "). [" + e.getMessage() + "]");
            }
            if (log.isDebugEnabled()) {
                log.debug("Error building RSS feed (" + url + ").", e);
            }
            status = STATUS_ERROR;
			statusCode = e.getMessage();
        } finally {
        	computeTime(jobDetail.getJobDataMap(), stopTimer());
        }
        
        updateJobData(context, status, statusCode);
		sendAlertMail(context);
		updateJob(context);
		
		if (log.isDebugEnabled()) {
			log.debug("Job (" + context.getJobDetail().getName() + ") finished in "
					+ (System.currentTimeMillis() - startTime) + " ms.");
		}
	}
}
