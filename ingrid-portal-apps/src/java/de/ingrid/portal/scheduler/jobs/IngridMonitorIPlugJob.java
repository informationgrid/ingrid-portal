/*
 * Copyright (c) 2007 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.scheduler.jobs;

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
		int timeout = dataMap.getInt(PARAM_TIMEOUT);
		if (timeout == 0) {
			timeout = 30000;
			dataMap.put(PARAM_TIMEOUT, timeout);
		}
		dataMap.put(PARAM_LAST_CHECK, new Date());

		int status = 0;
		String statusCode = null;
		try {
			IngridQuery q = QueryStringParser.parse(query);
			IngridHits hits = IBUSInterfaceImpl.getInstance().search(q, 10, 1, 1, timeout);
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
		} catch (Throwable e) {
			status = STATUS_ERROR;
			statusCode = STATUS_CODE_ERROR_UNSPECIFIC;
		}

		int eventOccurences = dataMap.getInt(PARAM_EVENT_OCCURENCES);
		int previousStatus = dataMap.getInt(PARAM_STATUS);
		String previousStatusCode = dataMap.getString(PARAM_STATUS_CODE);

		// if we have exactly the same result like the previous check
		// increase event occurences
		if (status == previousStatus && previousStatusCode.equals(statusCode)) {
			eventOccurences++;
		} else {
			eventOccurences = 1;
		}

		dataMap.put(PARAM_STATUS, status);
		dataMap.put(PARAM_STATUS_CODE, statusCode);
		dataMap.put(PARAM_EVENT_OCCURENCES, eventOccurences);

		sendAlertMail(dataMap);

		if (log.isDebugEnabled()) {
			log.debug("Job (" + context.getJobDetail().getName() + ") finished in "
					+ (System.currentTimeMillis() - startTime) + " ms.");
		}
	}
}
