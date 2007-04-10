/*
 * Copyright (c) 2007 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.scheduler.jobs;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.Date;

import org.apache.axis.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.ingrid.iplug.ecs.UDKResultAnalyzer;
import de.ingrid.iplug.ecs.builder.request.UDKQueryBuilder;
import de.ingrid.iplug.ecs.tools.AxisQuerySender;
import de.ingrid.iplug.ecs.tools.AxisTools;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.PlugDescription;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.ParseException;
import de.ingrid.utils.queryparser.QueryStringParser;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class IngridMonitorECSJob extends IngridMonitorAbstractJob {

	public static final String STATUS_CODE_ERROR_QUERY_PARSE_EXCEPTION = "component.monitor.iplug.error.query.parse.exception";

	public static final String STATUS_CODE_ERROR_INVALID_SERVICE_URL = "component.monitor.general.error.invalid.service.url";

	public static final String STATUS_CODE_ERROR_INVALID_HTTP_REQUEST = "component.monitor.general.g2k.invalid.http.request";

	public static final String COMPONENT_TYPE = "component.monitor.general.type.ecs";

	private final static Log log = LogFactory.getLog(IngridMonitorECSJob.class);

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
		String serviceUrl = dataMap.getString(IngridMonitorAbstractJob.PARAM_SERVICE_URL);

		dataMap.put(PARAM_LAST_CHECK, new Date());

		int status = 0;
		String statusCode = null;
		try {

			UDKQueryBuilder fQueryBuilder = new UDKQueryBuilder();
			AxisQuerySender fQuerySender = new AxisQuerySender(serviceUrl);
			PlugDescription plugDescription = new PlugDescription();
			plugDescription.setProxyServiceURL("test_id");
			plugDescription.setDataSourceName("monitor_component");
			UDKResultAnalyzer fResultAnalyser = new UDKResultAnalyzer(plugDescription);

			IngridQuery q = QueryStringParser.parse(query);

			Message mRequest = AxisTools.createSOAPMessage(fQueryBuilder.createUDKQuery(q), 11);
			Message mResponse = fQuerySender.sendSOAPMessage(mRequest, timeout);
			// Analyse Result and build IngridHits
			IngridHits hits = fResultAnalyser.analyzeResult(mResponse, 0, 1);

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
		} catch (MalformedURLException e) {
			status = STATUS_ERROR;
			statusCode = STATUS_CODE_ERROR_INVALID_SERVICE_URL;
		} catch (SocketTimeoutException e) {
			status = STATUS_ERROR;
			statusCode = STATUS_CODE_ERROR_TIMEOUT;
		} catch (IOException e) {
			status = STATUS_ERROR;
			statusCode = STATUS_CODE_ERROR_TIMEOUT;
		} catch (Throwable e) {
			status = STATUS_ERROR;
			statusCode = STATUS_CODE_ERROR_UNSPECIFIC;
		}

		int eventOccurences;
		try {
			eventOccurences = dataMap.getInt(PARAM_EVENT_OCCURENCES);
		} catch (Exception e) {
			eventOccurences = 0;
		}

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

		sendAlertMail(context.getJobDetail());

		if (log.isDebugEnabled()) {
			log.debug("Job (" + context.getJobDetail().getName() + ") finished in "
					+ (System.currentTimeMillis() - startTime) + " ms.");
		}
	}
}
