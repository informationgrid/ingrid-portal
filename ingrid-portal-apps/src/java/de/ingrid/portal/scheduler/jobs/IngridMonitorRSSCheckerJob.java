/*
 * Copyright (c) 2008 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.scheduler.jobs;

import java.net.URL;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

import de.ingrid.portal.hibernate.HibernateUtil;
import de.ingrid.portal.om.IngridRSSSource;


/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class IngridMonitorRSSCheckerJob extends IngridMonitorAbstractJob {

	public static final String COMPONENT_TYPE = "component.monitor.general.type.rss";
	
	public static final String JOB_ID = "RSS-Checker";

	private final static Log log = LogFactory.getLog(IngridMonitorRSSCheckerJob.class);

	/**
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	public void execute(JobExecutionContext context) throws JobExecutionException {
		long startTime = 0;
		Session session = HibernateUtil.currentSession();
        Transaction tx = null;
        
		if (log.isDebugEnabled()) {
			startTime = System.currentTimeMillis();
			log.debug("Job (" + context.getJobDetail().getName() + ") is executed...");
		}
		
		JobDetail jobDetail = context.getJobDetail();
		String url 			= jobDetail.getJobDataMap().getString(PARAM_SERVICE_URL);
		
		int status = 0;
		String statusCode = null;
		SyndFeed feed = null;
		SyndFeedInput input = null;
		
		IngridRSSSource rssSource = null;
		List rssSources = session.createCriteria(IngridRSSSource.class).add(Restrictions.eq("url", url)).list();
		
		if (rssSources.size()==1) {
			rssSource = (IngridRSSSource)rssSources.get(0);
		} else {
			if (log.isInfoEnabled()) {
                log.info("RSS Source not found in list. List of database objects returned " + rssSources.size() + " objects.");
            }
		}
		
		
        try {
            input = new SyndFeedInput();
        	
        	// check if XML behind URL can be parsed correctly
        	startTimer();
            feed = input.build(new XmlReader(new URL(url)));
            
            status = STATUS_OK;
			statusCode = STATUS_CODE_NO_ERROR;
            
        } catch (Exception e) {
            if (log.isInfoEnabled()) {
                log.info("Error building RSS feed (" + url + "). [" + e.getClass().getName() + ":" + e.getMessage() + "]");
            }
            if (log.isDebugEnabled()) {
                log.debug("Error building RSS feed (" + url + ").", e);
            }
            status = STATUS_ERROR;
			statusCode = e.getClass().getName() + ":" + e.getMessage();
        } finally {
        	computeTime(jobDetail.getJobDataMap(), stopTimer());
        }
        
        if (rssSource != null) {
	        tx = session.beginTransaction();
	        rssSource.setError(statusCode);
	        session.save(rssSource);
	        tx.commit();
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
