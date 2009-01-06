/*
 * Copyright (c) 2007 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.scheduler.jobs;

import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;
import org.quartz.StatefulJob;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Utils;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public abstract class IngridAbstractStateJob implements StatefulJob {

	private final static Log log = LogFactory.getLog(IngridAbstractStateJob.class);

	public static final int STATUS_OK = 0;

	public static final int STATUS_ERROR = 1;

	public static final int ACTIVE_ON = 1;

	public static final int ACTIVE_OFF = 0;

	public static final String STATUS_CODE_ERROR_NO_HITS = "component.monitor.general.error.no.hits";

	public static final String STATUS_CODE_ERROR_UNSPECIFIC = "component.monitor.general.error.unspecific";

	public static final String STATUS_CODE_ERROR_TIMEOUT = "component.monitor.general.error.timout";

	public static final String STATUS_CODE_ERROR_NO_IPLUGS = "component.monitor.general.error.no.iplugs";
	
	public static final String STATUS_CODE_ERROR_XML_PARSE = "component.monitor.general.error.xml.parse";
	
	public static final String STATUS_CODE_NO_ERROR = "component.monitor.general.error.none";
	
	public static final String PARAM_STATUS = "component.monitor.general.status";

	public static final String PARAM_STATUS_CODE = "component.monitor.general.status.code";

	public static final String PARAM_EVENT_OCCURENCES = "component.monitor.general.event.occurences";

	public static final String PARAM_TIMEOUT = "component.monitor.general.timeout";

	public static final String PARAM_LAST_CHECK = "component.monitor.general.last.check";
	
	public static final String PARAM_NEXT_CHECK = "component.monitor.general.next.check";

	public static final String PARAM_CHECK_INTERVAL = "component.monitor.general.check.interval";

	public static final String PARAM_ACTIVE = "component.monitor.general.active";

	public static final String PARAM_QUERY = "component.monitor.general.query";

	public static final String PARAM_CONTACTS = "component.monitor.general.contacts";

	public static final String PARAM_CONTACT_EMAIL = "component.monitor.general.contact.email";

	public static final String PARAM_CONTACT_LAST_ALERTED_EVENT = "component.monitor.general.contact.last.alerted.event";

	public static final String PARAM_CONTACT_EVENT_OCCURENCE_BEFORE_ALERT = "component.monitor.general.contact.event.occurence.before.alert";

	public static final String PARAM_COMPONENT_TITLE = "component.monitor.general.title";

	public static final String PARAM_COMPONENT_TYPE = "component.monitor.general.type";

	public static final String PARAM_SERVICE_URL = "component.monitor.general.service.url";
	
	public static final String PARAM_TIMER_AVERAGE = "component.monitor.general.timer.average";

	public static final String PARAM_TIMER_NUM = "component.monitor.general.timer.num";
	
	private long startTime;
	
	public void startTimer() {
		startTime = System.currentTimeMillis();
	}
	
	public long stopTimer() {
		return System.currentTimeMillis() - startTime;  
	}
	
	/**
	 * 
	 * @param dataMap
	 * @param newTime
	 */
	public static void computeTime(JobDataMap dataMap, long newTime) {
		long average = 0;
		int  num 	 = 0;
		
		if (dataMap.containsKey(PARAM_TIMER_AVERAGE)) {
			average = dataMap.getLong(PARAM_TIMER_AVERAGE);
		}
		
		if (dataMap.containsKey(PARAM_TIMER_NUM)) {
			num = dataMap.getInt(PARAM_TIMER_NUM);
		}
		
		long newAverage = (num*average + newTime) / (num+1);
		
		dataMap.put(PARAM_TIMER_AVERAGE, newAverage);
		dataMap.put(PARAM_TIMER_NUM, num+1);
	}

	
	protected void updateJob(JobExecutionContext context) {
		try {
			context.getScheduler().addJob(context.getJobDetail(), true);
		} catch (SchedulerException e) {
			log.error("Error updating job " + context.getJobDetail().getName());
		}
	}
	
}
