/*
 * Copyright (c) 2007 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.scheduler.jobs;

import java.io.IOException;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.ParseException;
import de.ingrid.utils.queryparser.QueryStringParser;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class IngridMonitorIPlugJob extends IngridMonitorAbstractJob {

	public static final String STATUS_CODE_ERROR_QUERY_PARSE_EXCEPTION = "component.monitor.iplug.error.query.parse.exception";

	public static final String COMPONENT_TYPE = "component.monitor.general.type.iplug";

	private final static Log log = LogFactory.getLog(IngridMonitorIPlugJob.class);

	/**
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	public void execute(JobExecutionContext context) throws JobExecutionException {

		JobDataMap dataMap = context.getJobDetail().getJobDataMap();
		long startTime = 0;

		boolean isActive = dataMap.getInt(PARAM_ACTIVE) == 1;
		if (!isActive) {
			if (log.isDebugEnabled()) {
				log.debug("Job (" + context.getJobDetail().getName() + ") is inactive. Exiting.");
			}
			return;
		} else {
			if (log.isDebugEnabled()) {
				startTime = System.currentTimeMillis();
				log.debug("Job (" + context.getJobDetail().getName() + ") is executed...");
			}
		}

		String query = dataMap.getString(PARAM_QUERY);
		int timeout;
		try {
			timeout = dataMap.getInt(PARAM_TIMEOUT);
		} catch (Exception e1) {
			timeout = 0;
		}
		if (timeout == 0) {
			timeout = 30000;
			dataMap.put(PARAM_TIMEOUT, timeout);
		}
		
		int status = 0;
		String statusCode = null;
		try {
			IngridQuery q = QueryStringParser.parse(query);
			//long beginTime = System.currentTimeMillis();
			
			startTimer();
			IngridHits hits = IBUSInterfaceImpl.getInstance().search(q, 10, 1, 0, timeout);
			computeTime(dataMap, stopTimer());
			
			//long duration = System.currentTimeMillis() - beginTime;
			//log.info("Call took " + duration + "ms");
			if (hits.length() == 0) {
				status = STATUS_ERROR;
				statusCode = STATUS_CODE_ERROR_NO_HITS;
			} else {
				status = STATUS_OK;
				statusCode = STATUS_CODE_NO_ERROR;
			}
		} catch (ParseException e) {
			status = STATUS_ERROR;
			statusCode = STATUS_CODE_ERROR_QUERY_PARSE_EXCEPTION;
		} catch (InterruptedException e) {
			status = STATUS_ERROR;
			statusCode = STATUS_CODE_ERROR_TIMEOUT;
		} catch (IOException e) {
			status = STATUS_ERROR;
			statusCode = STATUS_CODE_ERROR_TIMEOUT;
		} catch (Throwable e) {
			status = STATUS_ERROR;
			statusCode = STATUS_CODE_ERROR_UNSPECIFIC;
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
