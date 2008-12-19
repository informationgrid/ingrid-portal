/*
 * Copyright (c) 2008 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.scheduler.jobs;

import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

import de.ingrid.portal.hibernate.HibernateUtil;
import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;
import de.ingrid.portal.om.IngridRSSSource;
import de.ingrid.utils.PlugDescription;


/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class IngridMonitorRSSCheckerJob extends IngridMonitorAbstractJob {

	//public static final String STATUS_CODE_ERROR_QUERY_PARSE_EXCEPTION = "component.monitor.iplug.error.query.parse.exception";

	public static final String COMPONENT_TYPE = "component.monitor.general.type.rss";
	
	public static final String JOB_ID = "RSS-Checker";

	private final static Log log = LogFactory.getLog(IngridMonitorRSSCheckerJob.class);

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
		String url 			= jobDetail.getJobDataMap().getString(PARAM_SERVICE_URL);
		
		updateDate(jobDetail.getJobDataMap());
		//jobDetail.getJobDataMap().put(PARAM_LAST_CHECK, new Date());
		
		int status = 0;
		String statusCode = null;
		SyndFeed feed = null;
		SyndFeedInput input = null;
		
        try {
            input = new SyndFeedInput();
        	
        	// check if XML behind URL can be parsed correctly
        	timer.start();
            feed = input.build(new XmlReader(new URL(url)));                    
            computeTime(jobDetail.getJobDataMap(), timer.stop());
            
            status = STATUS_OK;
			statusCode = STATUS_CODE_NO_ERROR;
            
        } catch (Exception e) {
            if (log.isInfoEnabled()) {
                log.info("Error building RSS feed (" + url + "). [" + e.getMessage() + "]");
            }
            if (log.isDebugEnabled()) {
                log.debug("Error building RSS feed (" + url + ").", e);
            }
            status = STATUS_ERROR;
			statusCode = e.getMessage(); //STATUS_CODE_ERROR_XML_PARSE;
            //feed = null;
        } finally {
            //session.evict(rssSource);
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
