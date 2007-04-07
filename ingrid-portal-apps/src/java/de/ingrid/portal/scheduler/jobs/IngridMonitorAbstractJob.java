/*
 * Copyright (c) 2007 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.scheduler.jobs;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.StatefulJob;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Utils;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public abstract class IngridMonitorAbstractJob implements StatefulJob {

	public static final int STATUS_OK = 0;

	public static final int STATUS_ERROR = 1;

	public static final int ACTIVE_ON = 1;

	public static final int ACTIVE_OFF = 0;

	public static final String STATUS_CODE_ERROR_NO_HITS = "component.monitor.general.error.no.hits";

	public static final String STATUS_CODE_ERROR_UNSPECIFIC = "component.monitor.general.error.unspecific";

	public static final String STATUS_CODE_ERROR_TIMEOUT = "component.monitor.general.error.timout";

	public static final String STATUS_CODE_NO_ERROR = "component.monitor.general.error.none";

	public static final String PARAM_STATUS = "component.monitor.general.status";

	public static final String PARAM_STATUS_CODE = "component.monitor.general.status.code";

	public static final String PARAM_EVENT_OCCURENCES = "component.monitor.general.event.occurences";

	public static final String PARAM_TIMEOUT = "component.monitor.general.timeout";

	public static final String PARAM_LAST_CHECK = "component.monitor.general.last.check";

	public static final String PARAM_CHECK_INTERVAL = "component.monitor.general.check.interval";

	public static final String PARAM_ACTIVE = "component.monitor.general.active";

	public static final String PARAM_QUERY = "component.monitor.general.query";

	public static final String PARAM_CONTACTS = "component.monitor.general.contacts";

	public static final String PARAM_CONTACT_EMAIL = "component.monitor.general.contact.email";

	public static final String PARAM_CONTACT_EVENT_OCCURENCE_BEFORE_ALERT = "component.monitor.general.contact.event.occurence.before.alert";

	public static final String PARAM_COMPONENT_TITLE = "component.monitor.general.title";

	public static final String PARAM_COMPONENT_TYPE = "component.monitor.general.type";

	public static final String PARAM_SERVICE_URL = "component.monitor.general.service.url";

	protected void sendAlertMail(JobDetail job) {

		JobDataMap dataMap = job.getJobDataMap();
		int eventOccurences = dataMap.getInt(PARAM_EVENT_OCCURENCES);
		List contacts = (List) dataMap.get(PARAM_CONTACTS);
		if (contacts != null) {
			for (Iterator it = contacts.iterator(); it.hasNext();) {
				Map contact = (Map) it.next();
				if (contact != null) {
					int contactEventOccurencesBeforeAlert = ((Integer) contact
							.get(PARAM_CONTACT_EVENT_OCCURENCE_BEFORE_ALERT)).intValue();
					String contactEmails = (String) contact.get(PARAM_CONTACT_EMAIL);
					// if the previous number of occurences for this status hits the
					// threshhold for the alert
					if (contactEventOccurencesBeforeAlert == eventOccurences) {
						String[] emails = contactEmails.split(",");
						for (int i = 0; i < emails.length; i++) {
							String email = emails[i].trim();
							sendEmail(job, email);
						}
					}
				}
			}
		}
		if (eventOccurences == 1) {
			String email = PortalConfig.getInstance().getString(PortalConfig.COMPONENT_MONITOR_DEFAULT_EMAIL, "");
			if (email.length() > 0) {
				sendEmail(job, email);
			}
		}
	}

	private void sendEmail(JobDetail job, String email) {
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

		String from = PortalConfig.getInstance().getString(PortalConfig.COMPONENT_MONITOR_ALERT_EMAIL_SENDER,
				"foo@bar.com");
		String to = email;
		String text = Utils.mergeTemplate(templatePath, mailData, "map");
		Utils.sendEmail(from, emailSubject, new String[] { to }, text, null);
	}

}
