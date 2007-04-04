/*
 * Copyright (c) 2007 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.scheduler.jobs;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.quartz.JobDataMap;
import org.quartz.StatefulJob;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.global.Utils;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public abstract class IngridMonitorAbstractJob implements StatefulJob {

	public static int STATUS_OK = 0;

	public static int STATUS_ERROR = 1;

	public static int ACTIVE_ON = 1;

	public static int ACTIVE_OFF = 0;
	
	public static String STATUS_CODE_ERROR_NO_HITS = "component.monitor.general.error.no.hits";

	public static String STATUS_CODE_ERROR_UNSPECIFIC = "component.monitor.general.error.unspecific";

	public static String STATUS_CODE_ERROR_TIMEOUT = "component.monitor.general.error.timout";

	public static String STATUS_CODE_NO_ERROR = "component.monitor.general.error.none";

	public static String TYPE_CSW = "component.monitor.general.type.csw";

	public static String TYPE_ECS = "component.monitor.general.type.ecs";

	public static String TYPE_FPN = "component.monitor.general.type.fpn";

	public static String TYPE_SNS = "component.monitor.general.type.sns";

	public static String TYPE_G2K = "component.monitor.general.type.g2k";

	public static String PARAM_STATUS = "component.monitor.general.status";

	public static String PARAM_STATUS_CODE = "component.monitor.general.status.code";

	public static String PARAM_EVENT_OCCURENCES = "component.monitor.general.event.occurences";

	public static String PARAM_TIMEOUT = "component.monitor.general.timeout";

	public static String PARAM_LAST_CHECK = "component.monitor.general.last.check";

	public static String PARAM_CHECK_INTERVAL = "component.monitor.general.check.interval";

	public static String PARAM_ACTIVE = "component.monitor.general.active";
	
	public static String PARAM_QUERY = "component.monitor.general.query";

	public static String PARAM_CONTACTS = "component.monitor.general.contacts";

	public static String PARAM_CONTACT_EMAIL = "component.monitor.general.contact.email";

	public static String PARAM_CONTACT_EVENT_OCCURENCE_BEFORE_ALERT = "component.monitor.general.contact.event.occurence.before.alert";

	public static String PARAM_COMPONENT_TITLE = "component.monitor.general.title";

	public static String PARAM_COMPONENT_TYPE = "component.monitor.general.type";
	
	protected void sendAlertMail(JobDataMap dataMap) {

		int eventOccurences = dataMap.getInt(PARAM_EVENT_OCCURENCES);
		int status = dataMap.getInt(PARAM_STATUS);
		String statusCode = dataMap.getString(PARAM_STATUS_CODE);
		String componentTitle = dataMap.getString(PARAM_COMPONENT_TITLE);
		
		List alerts = (List) dataMap.get(PARAM_CONTACTS);

		for (Iterator it = alerts.iterator(); it.hasNext();) {
			Map contacts = (Map) it.next();
			if (contacts != null) {
				int contactEventOccurencesBeforeAlert = ((Integer) contacts
						.get(PARAM_CONTACT_EVENT_OCCURENCE_BEFORE_ALERT)).intValue();
				String contactEmails = (String) contacts.get(PARAM_CONTACT_EMAIL);
				// if the previous number of occurences for this status hits the
				// threshhold for the alert
				if (contactEventOccurencesBeforeAlert == eventOccurences) {
					String[] emails = contactEmails.split(",");
					for (int i = 0; i < emails.length; i++) {
						String email = emails[i].trim();

						HashMap mailData = new HashMap();
						mailData.put(PARAM_STATUS, String.valueOf(status));
						mailData.put(PARAM_STATUS_CODE, statusCode);
						mailData.put(PARAM_COMPONENT_TITLE, componentTitle);

						URL url = Thread.currentThread().getContextClassLoader().getResource("../templates/administration/monitor_alert_email.vm");
						String templatePath = url.getPath();
						String emailSubject = PortalConfig.getInstance().getString(
								PortalConfig.COMPONENT_MONITOR_ALERT_EMAIL_SUBJECT, "ingrid component monitor alert");
						emailSubject = emailSubject.concat(" [").concat(componentTitle).concat("]");
						
						String from = PortalConfig.getInstance().getString(
								PortalConfig.COMPONENT_MONITOR_ALERT_EMAIL_SENDER, "foo@bar.com");
						String to = email;
						String text = Utils.mergeTemplate(templatePath, mailData, "map");
						Utils.sendEmail(from, emailSubject, new String[] { to }, text, null);
					}
				}
			}
		}

	}

}
