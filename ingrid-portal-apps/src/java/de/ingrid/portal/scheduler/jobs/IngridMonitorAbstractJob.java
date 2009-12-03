/*
 * Copyright (c) 2007 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.scheduler.jobs;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Utils;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public abstract class IngridMonitorAbstractJob extends IngridAbstractStateJob {

	private final static Log log = LogFactory.getLog(IngridMonitorAbstractJob.class);

	protected void updateJobData(JobExecutionContext context, int status, String statusCode) {
		JobDataMap dataMap = context.getJobDetail().getJobDataMap();

		// verify JobDataMap
		checkJobDataMap(dataMap);
		
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
		if (log.isDebugEnabled()) {
			log.debug("Previous status code:" + previousStatusCode);
			log.debug("New status code:" + statusCode);
		}
		if (status == previousStatus && previousStatusCode.equals(statusCode)) {
			eventOccurences++;
		} else {
			eventOccurences = 1;
		}
		
		if (status == STATUS_OK) {
		    dataMap.put(PARAM_LAST_ERRORFREE_RUN, DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd H:mm:ss"));
		}

		dataMap.put(PARAM_STATUS, status);
		dataMap.put(PARAM_STATUS_CODE, statusCode);
		dataMap.put(PARAM_EVENT_OCCURENCES, eventOccurences);
	}
	
	/**
	 * Checks if certain parameters inside the jobmap are set. If not then those 
	 * values will be set to default values.
	 * This is necessary for DefaultJobs where some parameters are not initially set!
	 * 
	 * @param map is the JobDataMap of the running job
	 */
	private void checkJobDataMap(JobDataMap map) {
		if (!map.containsKey(PARAM_STATUS))
			map.put(PARAM_STATUS, STATUS_OK);
		if (!map.containsKey(PARAM_STATUS_CODE))
			map.put(PARAM_STATUS_CODE, STATUS_CODE_NO_ERROR);
	}

	protected void sendAlertMail(JobExecutionContext context) {

		JobDetail job = context.getJobDetail();
		JobDataMap dataMap = job.getJobDataMap();
		int eventOccurences = dataMap.getInt(PARAM_EVENT_OCCURENCES);
		String currentStatusCode = dataMap.getString(PARAM_STATUS_CODE);
		List contacts = (List) dataMap.get(PARAM_CONTACTS);
		if (contacts != null) {
			for (Iterator it = contacts.iterator(); it.hasNext();) {
				HashMap contact = (HashMap) it.next();
				if (contact != null) {
					String lastAlertedEvent = (String) contact.get(PARAM_CONTACT_LAST_ALERTED_EVENT);
					if (lastAlertedEvent == null) {
						lastAlertedEvent = "";
					}
					int contactEventOccurencesBeforeAlert = ((Integer) contact
							.get(PARAM_CONTACT_EVENT_OCCURENCE_BEFORE_ALERT)).intValue();
					String contactEmails = (String) contact.get(PARAM_CONTACT_EMAIL);
					// if the previous number of occurrences for this status hits
					// the threshold for the alert
					if (log.isDebugEnabled()) {
						log.debug("Check sending alert email to " + contactEmails + ". (Event " + currentStatusCode + " occured "
								+ eventOccurences + " times, contact threshold is " + contactEventOccurencesBeforeAlert
								+ ", contact last event is " + lastAlertedEvent + ")");
					}
					if (contactEventOccurencesBeforeAlert == eventOccurences
							&& !lastAlertedEvent.equals(currentStatusCode)) {
						String[] emails = contactEmails.split(",");
						for (int i = 0; i < emails.length; i++) {
							String email = emails[i].trim();
							sendEmail(job, email);
						}
						// set last alerted event in contact
						contact.put(PARAM_CONTACT_LAST_ALERTED_EVENT, currentStatusCode);
					}
				}
			}
		}
		
		// always send to a default email address
		if (eventOccurences == 1) {
			String email = PortalConfig.getInstance().getString(PortalConfig.COMPONENT_MONITOR_DEFAULT_EMAIL, "");
			if (email.length() > 0) {
				sendEmail(job, email);
			}
		}
	}

	private void sendEmail(JobDetail job, String email) {
		if (log.isDebugEnabled()) {
			log.debug("Try to sent alert email to " + email);
		}
		HashMap mailData = new HashMap();
		mailData.put("JOB", job);
		ResourceBundle resources = ResourceBundle.getBundle("de.ingrid.portal.resources.AdminPortalResources",
				Locale.GERMAN);
		mailData.put("MESSAGES", new IngridResourceBundle(resources));

		URL url = Thread.currentThread().getContextClassLoader().getResource(
				"../templates/administration/monitor_alert_email.vm");
		String templatePath = url.getPath();
		String emailSubject = PortalConfig.getInstance().getString(PortalConfig.COMPONENT_MONITOR_ALERT_EMAIL_SUBJECT,
				"ingrid component monitor alert");
		emailSubject = emailSubject.concat(" [").concat(job.getJobDataMap().getString(PARAM_COMPONENT_TITLE)).concat(
				"]");
		if (job.getJobDataMap().getInt(PARAM_STATUS) == STATUS_OK) {
			emailSubject = emailSubject.concat("[OK]");
		} else {
			emailSubject = emailSubject.concat("[FAILED]");
		}

		String from = PortalConfig.getInstance().getString(PortalConfig.COMPONENT_MONITOR_ALERT_EMAIL_SENDER,
				"foo@bar.com");
		String to = email;
		String text = Utils.mergeTemplate(templatePath, mailData, "map");
		Utils.sendEmail(from, emailSubject, new String[] { to }, text, null);
		if (log.isDebugEnabled()) {
			log.debug("Sent alert email to " + to);
		}
	}

}
