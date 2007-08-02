/*
 * Copyright (c) 2007 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.scheduler.jobs;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.axis.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import de.ingrid.iplug.csw.CSWQueryBuilder;
import de.ingrid.iplug.csw.tools.AxisQuerySender;
import de.ingrid.iplug.csw.tools.AxisTools;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.ParseException;
import de.ingrid.utils.queryparser.QueryStringParser;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class IngridMonitorCSWJob extends IngridMonitorAbstractJob {

	public static final String STATUS_CODE_ERROR_QUERY_PARSE_EXCEPTION = "component.monitor.iplug.error.query.parse.exception";

	public static final String STATUS_CODE_ERROR_INVALID_SERVICE_URL = "component.monitor.general.error.invalid.service.url";

	public static final String STATUS_CODE_ERROR_INVALID_HTTP_REQUEST = "component.monitor.general.g2k.invalid.http.request";

	public static final String COMPONENT_TYPE = "component.monitor.general.type.csw";

	private final static Log log = LogFactory.getLog(IngridMonitorCSWJob.class);

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

			IngridQuery q = QueryStringParser.parse(query);
			CSWQueryBuilder qBuilder = new CSWQueryBuilder();
			AxisQuerySender qSender = new AxisQuerySender(serviceUrl);

			// Send and receive SOAP-Message
			String cswQuery = qBuilder.createCSWQuery(q, 0, 1);
			Message mRequest = AxisTools.createSOAPMessage(cswQuery, 11);

			Message mResponse = qSender.sendSOAPMessage(mRequest, timeout);
			// Analyse Result and build IngridHits
			org.w3c.dom.Document bodyDOM = getBodyAsDOM(mResponse);

			// get number of hits matched
			String numberOfMatchedHitsStr = null;
			int numberOfMatchedHits = 0;
			NodeList nl = bodyDOM.getElementsByTagName("SearchResults");
			if (nl != null && nl.getLength() >= 1) {
				Element e = (Element) nl.item(0);
				numberOfMatchedHitsStr = e.getAttribute("numberOfRecordsMatched");
				numberOfMatchedHits = Integer.parseInt(numberOfMatchedHitsStr);
			}

			if (numberOfMatchedHits == 0) {
				status = STATUS_ERROR;
				statusCode = STATUS_CODE_ERROR_NO_HITS;
			} else {
				status = STATUS_OK;
				statusCode = STATUS_CODE_NO_ERROR;
			}
		} catch (ParseException e) {
			status = STATUS_ERROR;
			statusCode = STATUS_CODE_ERROR_QUERY_PARSE_EXCEPTION;
			if (log.isDebugEnabled()) {
				log.debug("Error processing SOAP call.", e);
			}
		} catch (MalformedURLException e) {
			status = STATUS_ERROR;
			statusCode = STATUS_CODE_ERROR_INVALID_SERVICE_URL;
			if (log.isDebugEnabled()) {
				log.debug("Error processing SOAP call.", e);
			}
		} catch (SocketTimeoutException e) {
			status = STATUS_ERROR;
			statusCode = STATUS_CODE_ERROR_TIMEOUT;
			if (log.isDebugEnabled()) {
				log.debug("Error processing SOAP call.", e);
			}
		} catch (IOException e) {
			status = STATUS_ERROR;
			statusCode = STATUS_CODE_ERROR_TIMEOUT;
			if (log.isDebugEnabled()) {
				log.debug("Error processing SOAP call.", e);
			}
		} catch (Throwable e) {
			status = STATUS_ERROR;
			statusCode = STATUS_CODE_ERROR_UNSPECIFIC;
			log.error("Unspecific Error!", e);
		}

		updateJobData(context, status, statusCode);
		sendAlertMail(context);
		updateJob(context);

		if (log.isDebugEnabled()) {
			log.debug("Job (" + context.getJobDetail().getName() + ") finished in "
					+ (System.currentTimeMillis() - startTime) + " ms.");
		}
	}

	/**
	 * Extracting pure XML body out of the SOAP message. If the exracted SOAP
	 * body is surrounded by the "<soapenv:Body ...>" tag, this tag will be
	 * removed. In case of an parsing error an empty <code>String</code> will
	 * be returned.
	 * 
	 * @param msg
	 *            SOAP message
	 * @return extracted XML message
	 */
	private String getBodyAsXMLString(final Message msg) {
		try {
			String xml = msg.getSOAPEnvelope().getBody().toString();
			// remove surrounding SOAP body tag if exists:
			if (xml.startsWith("<soapenv:Body") || xml.startsWith("<ns1:Body")) {
				xml = xml.substring(xml.indexOf('>') + 1, xml.lastIndexOf('<'));
			}
			return xml;
		} catch (Exception e) {
			log.error("Can't extract XML message from SOAP body! Message was: " + e.getMessage());
			return "";
		}
	}

	/**
	 * Extracting pure XML body out of the SOAP message. If the exracted SOAP
	 * body is surrounded by the "<soapenv:Body ...>" tag, this tag will be
	 * removed. In case of an parsing error an empty <code>Document</code>
	 * will be returned.
	 * 
	 * @param msg
	 *            SOAP message
	 * @return extracted XML message
	 */
	private org.w3c.dom.Document getBodyAsDOM(final Message msg) throws Exception {
		log.debug("Removing SOAP envelope...");
		String xml = getBodyAsXMLString(msg);
		// log.debug(xml);

		// Create DOM document out of the AXIS message
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder.parse(new InputSource(new StringReader(xml)));
	}

}
