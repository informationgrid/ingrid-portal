/*
 * Copyright (c) 2007 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.scheduler.jobs;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.Date;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.ingrid.iplug.g2k.G2kQueryBuilder;
import de.ingrid.iplug.g2k.G2kResultAnalyzer;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.ParseException;
import de.ingrid.utils.queryparser.QueryStringParser;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class IngridMonitorG2KJob extends IngridMonitorAbstractJob {

	public static final String STATUS_CODE_ERROR_QUERY_PARSE_EXCEPTION = "component.monitor.iplug.error.query.parse.exception";

	public static final String STATUS_CODE_ERROR_INVALID_SERVICE_URL = "component.monitor.general.error.invalid.service.url";

	public static final String STATUS_CODE_ERROR_INVALID_HTTP_REQUEST = "component.monitor.general.g2k.invalid.http.request";

	public static final String COMPONENT_TYPE = "component.monitor.general.type.g2k";

	private final static Logger log = LoggerFactory.getLogger(IngridMonitorG2KJob.class);

	/**
	 * Coding
	 */
	public static final String CODING = "ISO-8859-1";

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

		int status = 0;
		String statusCode = null;
		try {
			startTimer();
			G2kQueryBuilder qBuilder = new G2kQueryBuilder();
			IngridQuery q = QueryStringParser.parse(query);
			final String answer = sendQuery(qBuilder.createSimpleSearchString(q, "de"), serviceUrl, timeout);

			G2kResultAnalyzer rAnalyser = new G2kResultAnalyzer("dummy_id");
			IngridHits hits = rAnalyser.analyzeResult(answer, 0, 1);

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
		} catch (HttpException e) {
			status = STATUS_ERROR;
			try {
				int httpError = Integer.parseInt(e.getMessage());
				statusCode = "HTTP ERROR: " + httpError;
			} catch (NumberFormatException e1) {
				statusCode = STATUS_CODE_ERROR_INVALID_HTTP_REQUEST;
			}
		} catch (SocketTimeoutException e) {
			status = STATUS_ERROR;
			statusCode = STATUS_CODE_ERROR_TIMEOUT;
		} catch (IOException e) {
			status = STATUS_ERROR;
			statusCode = STATUS_CODE_ERROR_TIMEOUT;
		} catch (IllegalArgumentException e) {
			status = STATUS_ERROR;
			statusCode = STATUS_CODE_ERROR_ILLEGAL_ARGUMENT;
		} catch (Throwable e) {
			status = STATUS_ERROR;
			statusCode = STATUS_CODE_ERROR_UNSPECIFIC;
			if (log.isDebugEnabled()) {
				e.printStackTrace();
			}	
		}
		computeTime(dataMap, stopTimer());
		updateJobData(context, status, statusCode);
		sendAlertMail(context);
		updateJob(context);

		if (log.isDebugEnabled()) {
			log.debug("Job (" + context.getJobDetail().getName() + ") finished in "
					+ (System.currentTimeMillis() - startTime) + " ms.");
		}
	}

	/**
	 * <code>query</code> will be sent to the specified <code>g2kUrl</code>.
	 * In case of an error <code>null</code> we be returned.
	 * 
	 * @param query
	 *            G2k XML query
	 * @param g2kUrl
	 *            Target URL of the G2k service
	 * @return The response as XML string
	 * @throws MalformedURLException
	 */
	private String sendQuery(String query, String g2kUrl, int timeout) throws MalformedURLException, HttpException,
			IOException {

		if (g2kUrl == null || g2kUrl == "") {
			log.error("Error: no g2k-URL specified. Returning NULL!");
			return null;
		}

		// Create an instance of HttpClient.
		HttpClient client = new HttpClient();
		client.getParams().setParameter(HttpClientParams.SO_TIMEOUT, new Integer(timeout));
		// Create a method instance.
		PostMethod method = new PostMethod(g2kUrl);
		// Provide custom retry handler is necessary
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(0, false));
		method.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, new Integer(timeout));
		method.setRequestEntity(new StringRequestEntity(query));

		try {
			// Execute the method.
			int statusCode = client.executeMethod(method);

			if (statusCode != HttpStatus.SC_OK) {
				if (log.isDebugEnabled()) {
					log.debug("Method failed:" + method.getStatusLine());
				}
				throw new HttpException(String.valueOf(statusCode));
			}

			// Read the response body.
			byte[] responseBody = method.getResponseBody();

			// Deal with the response.
			return new String(responseBody);
		} finally {
			// Release the connection.
			method.releaseConnection();
		}
	}
}
