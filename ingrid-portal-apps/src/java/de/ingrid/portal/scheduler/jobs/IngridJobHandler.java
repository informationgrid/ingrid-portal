/*
 * Copyright (c) 1997-2008 by wemove GmbH
 */

package de.ingrid.portal.scheduler.jobs;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.portlet.ActionRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobListener;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerUtils;

import de.ingrid.portal.forms.AdminComponentMonitorForm;
import de.ingrid.portal.hibernate.HibernateUtil;
import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;
import de.ingrid.portal.om.IngridRSSSource;
import de.ingrid.portal.portlets.admin.AdminComponentMonitorPortlet;
import de.ingrid.portal.scheduler.IngridMonitorFacade;
import de.ingrid.utils.PlugDescription;

public class IngridJobHandler {
	private static final String[] component_types = { "component.monitor.general.type.iplug", "component.monitor.general.type.g2k", "component.monitor.general.type.csw", "component.monitor.general.type.ecs", "component.monitor.general.type.sns", "component.monitor.general.type.ibus", "component.monitor.general.type.rss" };
	
	private final static Log log = LogFactory.getLog(AdminComponentMonitorPortlet.class);
	
	IngridMonitorFacade monitor = IngridMonitorFacade.instance();
	
	/**
	 * Return the JobDataMap, which is used for storing job relevant data .
	 * @param id, 
	 * @return
	 */
	public JobDataMap getJobDataMap(String id) {
		JobDataMap jobDetailMap = null;
		try {
			//IngridMonitorFacade monitor = IngridMonitorFacade.instance();
			JobDetail jobDetail = monitor.getScheduler().getJobDetail(id, IngridMonitorFacade.SCHEDULER_GROUP_NAME);
			jobDetailMap = jobDetail.getJobDataMap();
		} catch (SchedulerException e) {
			log.error("Error getting job (" + id + ")", e);
		}
		return jobDetailMap;
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public String getJobName( String id ) {
		String jobName = null;
		try {
			JobDetail jobDetail = monitor.getScheduler().getJobDetail(id, IngridMonitorFacade.SCHEDULER_GROUP_NAME);
			jobName = jobDetail.getName();
		} catch (SchedulerException e) {
			log.error("Error getting job (" + id + ")", e);
		}
		return jobName;
	}
	
	/**
	 * 
	 * @param plugIds
	 */
	public void removeJob(String[] plugIds) {
		//String[] plugIds = plugIds.getParameterValues("id");
		for (int i = 0; i < plugIds.length; i++) {
			String plugId = plugIds[i];
			monitor.deleteJob(plugId);
		}
	}
	

	/**
	 * 
	 */
	public void updateJob(String id, AdminComponentMonitorForm cf) {
		
		try {
			JobDetail jobDetail = monitor.getScheduler().getJobDetail(id,
					IngridMonitorFacade.SCHEDULER_GROUP_NAME);
			JobDataMap dataMap = jobDetail.getJobDataMap();
			int active = cf.getInputAsInteger(AdminComponentMonitorForm.FIELD_ACTIVE, new Integer(0));

			dataMap.put(IngridMonitorIPlugJob.PARAM_ACTIVE, active);
			dataMap.put(IngridMonitorIPlugJob.PARAM_CHECK_INTERVAL, cf.getInputAsInteger(
					AdminComponentMonitorForm.FIELD_INTERVAL, new Integer(30)));
			dataMap.put(IngridMonitorIPlugJob.PARAM_TIMEOUT, cf.getInputAsInteger(
					AdminComponentMonitorForm.FIELD_TIMEOUT, new Integer(500)));
			dataMap.put(IngridMonitorIPlugJob.PARAM_COMPONENT_TITLE, cf
					.getInput(AdminComponentMonitorForm.FIELD_TITLE));
			String componentType = cf.getInput(AdminComponentMonitorForm.FIELD_TYPE);
			dataMap.put(IngridMonitorIPlugJob.PARAM_COMPONENT_TYPE, componentType);
			if (componentType.equals(IngridMonitorIPlugJob.COMPONENT_TYPE)) {
				jobDetail.setJobClass(IngridMonitorIPlugJob.class);
			} else if (componentType.equals(IngridMonitorG2KJob.COMPONENT_TYPE)) {
				jobDetail.setJobClass(IngridMonitorG2KJob.class);
			} else if (componentType.equals(IngridMonitorECSJob.COMPONENT_TYPE)) {
				jobDetail.setJobClass(IngridMonitorECSJob.class);
			} else if (componentType.equals(IngridMonitorCSWJob.COMPONENT_TYPE)) {
				jobDetail.setJobClass(IngridMonitorCSWJob.class);
			} else if (componentType.equals(IngridMonitorSNSJob.COMPONENT_TYPE)) {
				jobDetail.setJobClass(IngridMonitorSNSJob.class);
			}
			dataMap.put(IngridMonitorIPlugJob.PARAM_QUERY, cf
					.getInput(AdminComponentMonitorForm.FIELD_QUERY));
			dataMap.put(IngridMonitorIPlugJob.PARAM_SERVICE_URL, cf
					.getInput(AdminComponentMonitorForm.FIELD_SERVICE_URL));

			ArrayList contacts = new ArrayList();
			String[] emails = cf.getInputAsArray(AdminComponentMonitorForm.FIELD_CONTACT_EMAILS);
			String[] thresholds = cf.getInputAsArray(AdminComponentMonitorForm.FIELD_CONTACT_THRESHOLDS);
			for (int i = 0; i < emails.length; i++) {
				HashMap contact = new HashMap();
				contact.put(IngridMonitorIPlugJob.PARAM_CONTACT_EMAIL, emails[i]);
				contact.put(IngridMonitorIPlugJob.PARAM_CONTACT_EVENT_OCCURENCE_BEFORE_ALERT, Integer
						.valueOf(thresholds[i]));
				contacts.add(contact);
			}
			dataMap.put(IngridMonitorIPlugJob.PARAM_CONTACTS, contacts);
			monitor.getScheduler().addJob(jobDetail, true);

			jobDetail.setRequestsRecovery(false);
			
			// trigger removes job?
			//monitor.getScheduler().removeTriggerListener(id);
			
			//monitor.getScheduler().pauseTrigger(id,IngridMonitorFacade.SCHEDULER_GROUP_NAME);
			
			// this removes the job also when removing the trigger
			//monitor.getScheduler().unscheduleJob(id,IngridMonitorFacade.SCHEDULER_GROUP_NAME);
			
			if (active==1) {
				
				// add a trigger to fire the job every x seconds
				Trigger trigger = TriggerUtils.makeSecondlyTrigger(cf.getInputAsInteger(
						AdminComponentMonitorForm.FIELD_INTERVAL, new Integer(30)).intValue());
				
				trigger.setStartTime(new Date());
				trigger.setName(id);
				trigger.setGroup(IngridMonitorFacade.SCHEDULER_GROUP_NAME);
				trigger.setJobName(id);
				trigger.setJobGroup(IngridMonitorFacade.SCHEDULER_GROUP_NAME);
				
				if (triggerExists(id)) {
					monitor.getScheduler().rescheduleJob(id, IngridMonitorFacade.SCHEDULER_GROUP_NAME, trigger);
				} else {
					monitor.getScheduler().scheduleJob(trigger);
				}
			} else {
				// remove the trigger id from the job that is called id
				//Trigger trigger = monitor.getScheduler().getTrigger(id,IngridMonitorFacade.SCHEDULER_GROUP_NAME);
				/*String[] str = monitor.getScheduler().getTriggerNames(IngridMonitorFacade.SCHEDULER_GROUP_NAME);
				JobListener jl = monitor.getScheduler().getGlobalJobListener(id);
				String jlName = jl.getName();
				boolean b1 = monitor.getScheduler().removeGlobalTriggerListener(id);
				boolean b2 = monitor.getScheduler().removeTriggerListener(id);
				boolean b3 = monitor.getScheduler().removeJobListener(id);
				boolean b4 = monitor.getScheduler().removeTriggerListener(id);
				
				//boolean b5 = monitor.getScheduler().unscheduleJob(id, IngridMonitorFacade.SCHEDULER_GROUP_NAME);
				int i;*/
				monitor.getScheduler().pauseTrigger(id,IngridMonitorFacade.SCHEDULER_GROUP_NAME);
				
			}
		} catch (SchedulerException e) {
			log.error("Error updating job (" + id + ").", e);
		}
	}
	
	/**
	 * 
	 * @param cf
	 * @return
	 */
	public boolean newJob(AdminComponentMonitorForm cf) {
		String id = cf.getInput(AdminComponentMonitorForm.FIELD_ID);
		
		try {
			// check for existing job id
			JobDetail detail = monitor.getScheduler().getJobDetail(id, IngridMonitorFacade.SCHEDULER_GROUP_NAME);
			if (detail != null) {
				cf.setError(AdminComponentMonitorForm.FIELD_ID, "component.monitor.form.error.duplicate.id");
				return false;
			}

			Class jobClass = null;
			if (cf.getInput(AdminComponentMonitorForm.FIELD_TYPE).equals(IngridMonitorIPlugJob.COMPONENT_TYPE)) {
				jobClass = IngridMonitorIPlugJob.class;
			} else if (cf.getInput(AdminComponentMonitorForm.FIELD_TYPE).equals(IngridMonitorG2KJob.COMPONENT_TYPE)) {
				jobClass = IngridMonitorG2KJob.class;
			} else if (cf.getInput(AdminComponentMonitorForm.FIELD_TYPE).equals(IngridMonitorECSJob.COMPONENT_TYPE)) {
				jobClass = IngridMonitorECSJob.class;
			} else if (cf.getInput(AdminComponentMonitorForm.FIELD_TYPE).equals(IngridMonitorCSWJob.COMPONENT_TYPE)) {
				jobClass = IngridMonitorCSWJob.class;
			} else	if (cf.getInput(AdminComponentMonitorForm.FIELD_TYPE).equals(IngridMonitorSNSJob.COMPONENT_TYPE)) {
				jobClass = IngridMonitorSNSJob.class;
			} else	if (cf.getInput(AdminComponentMonitorForm.FIELD_TYPE).equals(IngridMonitorIBusJob.COMPONENT_TYPE)) {
				jobClass = IngridMonitorIBusJob.class;
			} else	if (cf.getInput(AdminComponentMonitorForm.FIELD_TYPE).equals(IngridMonitorRSSCheckerJob.COMPONENT_TYPE)) {
				jobClass = IngridMonitorRSSCheckerJob.class;
			}
			if (jobClass != null) {
				JobDetail jobDetail = new JobDetail(id,	IngridMonitorFacade.SCHEDULER_GROUP_NAME, jobClass);

				JobDataMap dataMap = jobDetail.getJobDataMap();
				int active = cf.getInputAsInteger(AdminComponentMonitorForm.FIELD_ACTIVE, new Integer(0));

				dataMap.put(IngridMonitorAbstractJob.PARAM_ACTIVE, active);
				dataMap.put(IngridMonitorAbstractJob.PARAM_CHECK_INTERVAL, cf.getInputAsInteger(
						AdminComponentMonitorForm.FIELD_INTERVAL, new Integer(30)));
				dataMap.put(IngridMonitorAbstractJob.PARAM_TIMEOUT, cf.getInputAsInteger(
						AdminComponentMonitorForm.FIELD_TIMEOUT, new Integer(500)));
				dataMap.put(IngridMonitorAbstractJob.PARAM_COMPONENT_TITLE, cf
						.getInput(AdminComponentMonitorForm.FIELD_TITLE));
				dataMap.put(IngridMonitorAbstractJob.PARAM_COMPONENT_TYPE, cf
						.getInput(AdminComponentMonitorForm.FIELD_TYPE));
				dataMap.put(IngridMonitorAbstractJob.PARAM_QUERY, cf
						.getInput(AdminComponentMonitorForm.FIELD_QUERY));
				dataMap.put(IngridMonitorAbstractJob.PARAM_SERVICE_URL, cf
						.getInput(AdminComponentMonitorForm.FIELD_SERVICE_URL));
				dataMap.put(IngridMonitorAbstractJob.PARAM_STATUS, IngridMonitorAbstractJob.STATUS_OK);
				dataMap
						.put(IngridMonitorAbstractJob.PARAM_STATUS_CODE,
								IngridMonitorAbstractJob.STATUS_CODE_NO_ERROR);
				dataMap.put(IngridMonitorAbstractJob.PARAM_EVENT_OCCURENCES, 1);

				ArrayList contacts = new ArrayList();
				String[] emails = cf.getInputAsArray(AdminComponentMonitorForm.FIELD_CONTACT_EMAILS);
				String[] thresholds = cf.getInputAsArray(AdminComponentMonitorForm.FIELD_CONTACT_THRESHOLDS);
				for (int i = 0; i < emails.length; i++) {
					HashMap contact = new HashMap();
					contact.put(IngridMonitorAbstractJob.PARAM_CONTACT_EMAIL, emails[i]);
					contact.put(IngridMonitorAbstractJob.PARAM_CONTACT_EVENT_OCCURENCE_BEFORE_ALERT, Integer
							.valueOf(thresholds[i]));
					contacts.add(contact);
				}
				dataMap.put(IngridMonitorAbstractJob.PARAM_CONTACTS, contacts);
				jobDetail.setRequestsRecovery(false);
				
				// add the job to the scheduler without a trigger
				try {
					monitor.getScheduler().addJob(jobDetail, true);
				} catch (SchedulerException e) {
					log.error("Error creating job (" + id + ").", e);
				}

				if (active==1) {
					Trigger trigger = TriggerUtils.makeSecondlyTrigger(cf.getInputAsInteger(
							AdminComponentMonitorForm.FIELD_INTERVAL, new Integer(1800)).intValue());
					trigger.setStartTime(new Date());
					trigger.setName(id);
					trigger.setGroup(IngridMonitorFacade.SCHEDULER_GROUP_NAME);
					trigger.setJobName(id);
					trigger.setJobGroup(IngridMonitorFacade.SCHEDULER_GROUP_NAME);
					if (triggerExists(id)) {
						monitor.getScheduler().rescheduleJob(id, IngridMonitorFacade.SCHEDULER_GROUP_NAME, trigger);
					} else {
						monitor.getScheduler().scheduleJob(trigger);
					}
				} else {
					// remove the trigger id from the job that is called id ... does not work
					monitor.getScheduler().removeTriggerListener(id);
				}
			}
		} catch (SchedulerException e) {
			log.error("Error creating job (" + id + ").", e);
		}
		return true;
	}
	
	
	/**
	 * 
	 * @param request
	 */
	public void importJobs(ActionRequest request) {
		boolean activate = (request.getParameter("activate") != null);
		String stdEmail  = request.getParameter("std_email");
		ArrayList jobIds = new ArrayList();;
		
		
		// only fill array with present jobs if you don't want to overwrite them
		if (request.getParameter("overwrite") == null) {
			// get all present iPlugs
			List existingJobs = monitor.getJobs(null, true);
			
			for (int i=0; i < existingJobs.size(); i++) {
				jobIds.add(((JobDetail)existingJobs.get(i)).getName());
			}
		} else {
			// remove all jobs
			monitor.deleteAllJobs(IngridMonitorIPlugJob.COMPONENT_TYPE);
			monitor.deleteAllJobs(IngridMonitorIBusJob.COMPONENT_TYPE);
			monitor.deleteAllJobs(IngridMonitorRSSCheckerJob.COMPONENT_TYPE);
		}
		
		// *****************************************
		// add IBusJob
		// *****************************************
		addIBusJob(activate, stdEmail);
		
		// *****************************************
		// add RSSCheckerJob
		// *****************************************
        Session session = HibernateUtil.currentSession();
        List rssSources = session.createCriteria(IngridRSSSource.class).list();
        Iterator it = rssSources.iterator();
        while (it.hasNext()) {
        	addRSSCheckerJob(((IngridRSSSource) it.next()), activate, stdEmail);
        }
		
		// *****************************************
		// add IPlugJobs
		// *****************************************
		PlugDescription[] iPlugs = IBUSInterfaceImpl.getInstance().getAllIPlugs();
		for (int i = 0; i < iPlugs.length; i++) {
			PlugDescription iPlug = iPlugs[i];

			if (jobIds.contains(iPlug.getPlugId())) {
				if (log.isDebugEnabled()) {
					log.debug("iPlug (" + iPlug.getPlugId() + ") already exists ... import skipped");
				}
				continue;
			}			
			
			JobDetail jobDetail = new JobDetail(iPlug.getPlugId(), IngridMonitorFacade.SCHEDULER_GROUP_NAME,
					IngridMonitorIPlugJob.class);
			
			// fill the data map with standard values
			fillJobDataMap(jobDetail.getJobDataMap(), activate, iPlug.getDataSourceName(), stdEmail);
			
			// add specific data to the dataMap
			jobDetail.getJobDataMap().put(IngridMonitorIPlugJob.PARAM_QUERY,
					"wasser iplugs:\"" + iPlug.getProxyServiceURL() + "\" ranking:any");
			jobDetail.getJobDataMap().put(IngridMonitorIPlugJob.PARAM_COMPONENT_TYPE,
					IngridMonitorIPlugJob.COMPONENT_TYPE);
			
			jobDetail.setRequestsRecovery(false);
			
			// add the job to the scheduler without a trigger
			try {
				monitor.getScheduler().addJob(jobDetail, true);
			} catch (SchedulerException e) {
				log.error("Error creating job (" + iPlug.getPlugId() + ").", e);
			}

			if (activate) {
				Trigger trigger = TriggerUtils.makeSecondlyTrigger(1800);
				trigger.setStartTime(new Date());
				trigger.setName(iPlug.getPlugId());
				trigger.setGroup(IngridMonitorFacade.SCHEDULER_GROUP_NAME);
				trigger.setJobName(iPlug.getPlugId());
				trigger.setJobGroup(IngridMonitorFacade.SCHEDULER_GROUP_NAME);
				
				try {
					if (triggerExists(iPlug.getPlugId())) {
						monitor.getScheduler().rescheduleJob(iPlug.getPlugId(), IngridMonitorFacade.SCHEDULER_GROUP_NAME, trigger);
					} else {
						monitor.getScheduler().scheduleJob(trigger);
					}
				} catch (SchedulerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
								
			}
		}
	}
	
	/**
	 * 
	 * @param activate
	 * @param stdEmail
	 */
	private void addIBusJob( boolean activate, String stdEmail ) {
		JobDetail jobDetail;
		
		// does the iBus component already exists?
		try {
			jobDetail = monitor.getScheduler().getJobDetail(IngridMonitorIBusJob.JOB_ID, IngridMonitorFacade.SCHEDULER_GROUP_NAME);
		} catch (SchedulerException e) {
			jobDetail = null;
		}
		
		if (jobDetail == null) {
			jobDetail = new JobDetail(IngridMonitorIBusJob.JOB_ID, IngridMonitorFacade.SCHEDULER_GROUP_NAME,
				IngridMonitorIBusJob.class);
			
			// here the ID of the job is also used as the name
			fillJobDataMap(jobDetail.getJobDataMap(), activate, IngridMonitorIBusJob.JOB_ID, stdEmail);
			
			// add specific data to the dataMap
			jobDetail.getJobDataMap().put(IngridMonitorIPlugJob.PARAM_COMPONENT_TYPE,
					IngridMonitorIBusJob.COMPONENT_TYPE);
			
			jobDetail.setRequestsRecovery(false);
			
			// add the job to the scheduler without a trigger
			try {
				monitor.getScheduler().addJob(jobDetail, true);
			} catch (SchedulerException e) {
				log.error("Error creating bus job (" + IngridMonitorIBusJob.JOB_ID + ").", e);
			}
			
			if (activate) {
				Trigger trigger = TriggerUtils.makeSecondlyTrigger(1800);
				trigger.setStartTime(new Date());
				trigger.setName(IngridMonitorIBusJob.JOB_ID);
				trigger.setGroup(IngridMonitorFacade.SCHEDULER_GROUP_NAME);
				trigger.setJobName(IngridMonitorIBusJob.JOB_ID);
				trigger.setJobGroup(IngridMonitorFacade.SCHEDULER_GROUP_NAME);
				
				try {
					if (triggerExists(IngridMonitorIBusJob.JOB_ID)) {
						monitor.getScheduler().rescheduleJob(IngridMonitorIBusJob.JOB_ID, IngridMonitorFacade.SCHEDULER_GROUP_NAME, trigger);
					} else {
						monitor.getScheduler().scheduleJob(trigger);
					}
				} catch (SchedulerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 
	 * @param activate
	 * @param stdEmail
	 */
	private void addRSSCheckerJob( IngridRSSSource rssSource, boolean activate, String stdEmail ) {
		JobDetail jobDetail;
		
		// does the iBus component already exists?
		try {
			jobDetail = monitor.getScheduler().getJobDetail(rssSource.getUrl(), IngridMonitorFacade.SCHEDULER_GROUP_NAME);
		} catch (SchedulerException e) {
			jobDetail = null;
		}
		
		if (jobDetail == null) {
			jobDetail = new JobDetail(rssSource.getUrl(), IngridMonitorFacade.SCHEDULER_GROUP_NAME,
					IngridMonitorRSSCheckerJob.class);
			
			// here the ID of the job is also used as the name
			fillJobDataMap(jobDetail.getJobDataMap(), activate, rssSource.getDescription(), stdEmail);
			
			// add specific data to the dataMap
			jobDetail.getJobDataMap().put(IngridMonitorRSSCheckerJob.PARAM_COMPONENT_TYPE,
					IngridMonitorRSSCheckerJob.COMPONENT_TYPE);
			jobDetail.getJobDataMap().put(IngridMonitorRSSCheckerJob.PARAM_SERVICE_URL, rssSource.getUrl());
			
			jobDetail.setRequestsRecovery(false);
			
			// add the job to the scheduler without a trigger
			try {
				monitor.getScheduler().addJob(jobDetail, true);
			} catch (SchedulerException e) {
				log.error("Error creating rss job (" + rssSource.getUrl() + ").", e);
			}
			
			if (activate) {
				Trigger trigger = TriggerUtils.makeSecondlyTrigger(1800);
				trigger.setStartTime(new Date());
				trigger.setName(rssSource.getUrl());
				trigger.setGroup(IngridMonitorFacade.SCHEDULER_GROUP_NAME);
				trigger.setJobName(rssSource.getUrl());
				trigger.setJobGroup(IngridMonitorFacade.SCHEDULER_GROUP_NAME);
				try {
					if (triggerExists(rssSource.getUrl())) {
						monitor.getScheduler().rescheduleJob(rssSource.getUrl(), IngridMonitorFacade.SCHEDULER_GROUP_NAME, trigger);
					} else {
						monitor.getScheduler().scheduleJob(trigger);
					}
					//monitor.getScheduler().scheduleJob(jobDetail, trigger);
					
				} catch (SchedulerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
	}
	
	/**
	 * 
	 * @param dataMap
	 * @param activate
	 * @param title
	 * @param stdEmail
	 */
	private void fillJobDataMap( JobDataMap dataMap, boolean activate, String title, String stdEmail ) {
		if (activate) {
			dataMap.put(IngridMonitorAbstractJob.PARAM_ACTIVE, IngridMonitorAbstractJob.ACTIVE_ON);
		} else {
			dataMap.put(IngridMonitorAbstractJob.PARAM_ACTIVE, IngridMonitorAbstractJob.ACTIVE_OFF);
		}
		dataMap.put(IngridMonitorAbstractJob.PARAM_CHECK_INTERVAL, 1800);
		dataMap.put(IngridMonitorAbstractJob.PARAM_TIMEOUT, 30000);
		dataMap.put(IngridMonitorAbstractJob.PARAM_COMPONENT_TITLE, title);
		dataMap.put(IngridMonitorAbstractJob.PARAM_STATUS, IngridMonitorAbstractJob.STATUS_OK);
		dataMap.put(IngridMonitorAbstractJob.PARAM_STATUS_CODE,
				IngridMonitorAbstractJob.STATUS_CODE_NO_ERROR);
		dataMap.put(IngridMonitorAbstractJob.PARAM_EVENT_OCCURENCES, 1);
		
		// add standard email
		ArrayList contacts = new ArrayList();
					
		HashMap contact = new HashMap();
		contact.put(IngridMonitorAbstractJob.PARAM_CONTACT_EMAIL, stdEmail);
		contact.put(IngridMonitorAbstractJob.PARAM_CONTACT_EVENT_OCCURENCE_BEFORE_ALERT, 1);
		contacts.add(contact);
		
		dataMap.put(IngridMonitorAbstractJob.PARAM_CONTACTS, contacts);
		
	}
	
	/**
	 * 
	 * @return
	 */
	public String[] getComponentTypes() {
		return component_types;
	}
	
	/**
	 * 
	 * @param sortColumn
	 * @param ascending
	 * @return
	 */
	public List getJobs(String sortColumn, boolean ascending) {
		return monitor.getJobs(sortColumn, ascending);
	}
	
	/**
	 * 
	 * @param id
	 */
	public void resetTime(String id) {
		try {
			JobDetail detail = monitor.getScheduler().getJobDetail(id, IngridMonitorFacade.SCHEDULER_GROUP_NAME);
			detail.getJobDataMap().put(IngridMonitorIPlugJob.PARAM_TIMER_AVERAGE, 0L);
			detail.getJobDataMap().put(IngridMonitorIPlugJob.PARAM_TIMER_NUM, 0);
			
			// replace existing job with changed values
			monitor.getScheduler().addJob(detail, true);
			
			// test call
			// getAllJobs();
		} catch (SchedulerException e) {
			log.error("Error manipulating job (" + id + ").", e);
		}
	}
	
	/**
	 * 
	 */
	public void getAllJobs() {
		try {
			String[] allJobGroupNames = monitor.getScheduler().getJobGroupNames();
			for (int i=0; i<allJobGroupNames.length; i++) {
				String[] allJobsOfAGroup = monitor.getScheduler().getJobNames(allJobGroupNames[i]);
				for (int j=0; j<allJobsOfAGroup.length; j++) {
					log.debug(allJobsOfAGroup[j]);
				}
			}
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private boolean triggerExists( String id ) {
		boolean result = false;
		try {
			result = (monitor.getScheduler().getTrigger(id,IngridMonitorFacade.SCHEDULER_GROUP_NAME) != null);
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
}
