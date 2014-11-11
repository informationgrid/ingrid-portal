/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
/*
 * Copyright (c) 2007 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.scheduler.jobs;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Resource;

import de.ingrid.external.FullClassifyService.FilterType;
import de.ingrid.external.sns.RDFUtils;
import de.ingrid.external.sns.SNSClient;
import de.ingrid.portal.config.PortalConfig;
import de.ingrid.utils.queryparser.ParseException;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class IngridMonitorSNSJob extends IngridMonitorAbstractJob {

	public static final String STATUS_CODE_ERROR_QUERY_PARSE_EXCEPTION = "component.monitor.iplug.error.query.parse.exception";

	public static final String STATUS_CODE_ERROR_INVALID_SERVICE_URL = "component.monitor.general.error.invalid.service.url";

	public static final String STATUS_CODE_ERROR_INVALID_HTTP_REQUEST = "component.monitor.general.g2k.invalid.http.request";

	public static final String COMPONENT_TYPE = "component.monitor.general.type.sns";

	private final static Logger log = LoggerFactory.getLogger(IngridMonitorSNSJob.class);

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
			SNSClient snsClient;
			if (serviceUrl == null || serviceUrl.length() == 0) {
				snsClient = new SNSClient(PortalConfig.getInstance().getString(
						PortalConfig.COMPONENT_MONITOR_SNS_LOGIN, "ms"), PortalConfig.getInstance().getString(
						PortalConfig.COMPONENT_MONITOR_SNS_PASSWORD, "m3d1asyl3"), "de");
			} else {
				snsClient = new SNSClient("ms", "m3d1asyl3", "de", new URL(serviceUrl), null, null);
			}
			snsClient.setTimeout(timeout);
			Resource mapFragment = snsClient.findTopics(query, FilterType.ONLY_TERMS, "exact",
					null, 0, 20, "de", false);
			NodeIterator topics = null;
			if (null != mapFragment) {
			    topics = RDFUtils.getResults( mapFragment );
			}
			if (!topics.hasNext()) {
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
			if (log.isDebugEnabled()) {
				log.debug("Error checking SNS Interface.", e);
			}
		} catch (SocketTimeoutException e) {
			status = STATUS_ERROR;
			statusCode = STATUS_CODE_ERROR_TIMEOUT;
			if (log.isDebugEnabled()) {
				log.debug("Error checking SNS Interface.", e);
			}
		} catch (IOException e) {
			status = STATUS_ERROR;
			statusCode = STATUS_CODE_ERROR_TIMEOUT;
			if (log.isDebugEnabled()) {
				log.debug("Error checking SNS Interface.", e);
			}
		} catch (Throwable e) {
			status = STATUS_ERROR;
			statusCode = STATUS_CODE_ERROR_UNSPECIFIC;
			log.error("Error checking SNS Interface.", e);
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
}
