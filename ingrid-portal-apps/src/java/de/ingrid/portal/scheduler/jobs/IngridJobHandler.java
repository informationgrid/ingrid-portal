/*
 * Copyright (c) 1997-2008 by wemove GmbH
 */

package de.ingrid.portal.scheduler.jobs;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.portlet.ActionRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerUtils;

import de.ingrid.portal.forms.AdminComponentMonitorForm;
import de.ingrid.portal.hibernate.HibernateUtil;
import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;
import de.ingrid.portal.om.IngridRSSSource;
import de.ingrid.portal.portlets.admin.AdminComponentMonitorPortlet;
import de.ingrid.portal.scheduler.IngridMonitorFacade;
import de.ingrid.utils.PlugDescription;

/**
 * This class takes care of all job relevant things as creating, updating,
 * importing and setting the scheduler correctly
 * @author Andre
 *
 */
public class IngridJobHandler {
	private static final String[] component_types = { "component.monitor.general.type.iplug", "component.monitor.general.type.g2k", "component.monitor.general.type.csw", "component.monitor.general.type.ecs", "component.monitor.general.type.sns", "component.monitor.general.type.ibus", "component.monitor.general.type.rss" };
	
	private final static Log log = LogFactory.getLog(AdminComponentMonitorPortlet.class);
	
	IngridMonitorFacade monitor = IngridMonitorFacade.instance();
	
	
	/**
	 * Return the JobDataMap, which is used for storing job relevant data .
	 * @param id, is the job id that contains the data map
	 * @return the JobDataMap object
	 */
	public JobDataMap getJobDataMap(String id) {
		JobDataMap jobDetailMap = null;
		try {
			JobDetail jobDetail = monitor.getScheduler().getJobDetail(id, IngridMonitorFacade.SCHEDULER_GROUP_NAME);
			jobDetailMap = jobDetail.getJobDataMap();
		} catch (SchedulerException e) {
			log.error("Error getting job (" + id + ")", e);
		}
		return jobDetailMap;
	}
	
	public JobDetail getJobDetail( String id ) {
		try {
			return monitor.getScheduler().getJobDetail(id, IngridMonitorFacade.SCHEDULER_GROUP_NAME);
		} catch (SchedulerException e) {
			log.error("Error getting job (" + id + ")", e);
		}
		return null;
	}
	
	/**
	 * Get the name of a job.
	 * @param id, is the job id that contains the name
	 * @return the string of the job name
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
	 * Remove one or more jobs from the scheduler
	 * @param plugIds, is an array of strings containing all to be removed jobs
	 */
	public void removeJob(String[] jobIds) {
		for (int i = 0; i < jobIds.length; i++) {
			String jobId = jobIds[i];
			monitor.deleteJob(jobId);
		}
	}
	

	/**
	 * Update the modified content of a job.
	 * @param id, is the job id
	 * @param cf, is the action form containing all the (modified) values
	 */
	public void updateJob(String id, AdminComponentMonitorForm cf) {
		try {
			JobDetail jobDetail = monitor.getScheduler().getJobDetail(id,
					IngridMonitorFacade.SCHEDULER_GROUP_NAME);
						
			updateDataFromForm(cf, jobDetail);
			monitor.getScheduler().addJob(jobDetail, true);

			jobDetail.setRequestsRecovery(false);
			
			int active = jobDetail.getJobDataMap().getInt(IngridMonitorIPlugJob.PARAM_ACTIVE);
			
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
				monitor.getScheduler().pauseTrigger(id,IngridMonitorFacade.SCHEDULER_GROUP_NAME);
			}
		} catch (SchedulerException e) {
			log.error("Error updating job (" + id + ").", e);
		}
	}
	
	
	/**
	 * Create a new job that must have an unique id. If it was set to active a trigger will
	 * be initialized, that starts the job to a specified time.
	 * @param cf, the action form that contains all the job's information
	 * @return true, if job could be created
	 * 		   false, if id already existed 
	 */
	public boolean newJob(AdminComponentMonitorForm cf) {
		String id = cf.getInput(AdminComponentMonitorForm.FIELD_ID);
		
		try {
			// check for existing job id
			JobDetail jobDetail = monitor.getScheduler().getJobDetail(id, IngridMonitorFacade.SCHEDULER_GROUP_NAME);
			if (jobDetail != null) {
				cf.setError(AdminComponentMonitorForm.FIELD_ID, "component.monitor.form.error.duplicate.id");
				return false;
			}

			updateDataFromForm(cf, jobDetail);
			jobDetail.setRequestsRecovery(false);
			
			int active = jobDetail.getJobDataMap().getInt(IngridMonitorIPlugJob.PARAM_ACTIVE);
			
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
					log.warn("added a new job with an existing trigger!!!");
				} else {
					monitor.getScheduler().scheduleJob(trigger);
				}
			} else {
				// remove the trigger id from the job that is called id ... does not work
				monitor.getScheduler().removeTriggerListener(id);
			}
			
		} catch (SchedulerException e) {
			log.error("Error creating job (" + id + ").", e);
		}
		return true;
	}
	
	
	/**
	 * Import all connected iPlugs, available RSS feeds and the iBus as jobs/monitors.
	 * If "activate" was checked, all new imported jobs will be attached a trigger.
	 * If "overwrite" was checked, the list will be cleared and all settings of the
	 * jobs will be removed. If not checked, only new jobs will be imported. In case
	 * a standard email was given, it is automatically add to the job.
	 * @param request, contains the parameter from the form
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
					log.error("Error creating job (" + iPlug.getPlugId() + ").", e);
				}
								
			}
		}
	}
	
	
	/**
	 * Add the iBus component as a monitoring job.
	 * @param activate, if true the job will be immediately activated
	 * @param stdEmail, the email for reports, which is added to the job
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
					log.error("Error creating iBus job (" + IngridMonitorIBusJob.JOB_ID + ").", e);
				}
			}
		}
	}
	
	
	/**
	 * Add an RSS checker component as a monitoring job.
	 * @param rssSource, contains information about the rss feed
	 * @param activate, if true the job will be immediately activated
	 * @param stdEmail, the email for reports, which is added to the job
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
				} catch (SchedulerException e) {
					log.error("Error creating rss job (" + rssSource.getUrl() + ").", e);
				}
				
			}
		}
	}
	
	
	/**
	 * Fill the JobDataMap of a job with standard values.
	 * @param dataMap, the JobDataMap to be filled
	 * @param activate, if true the job will be immediately activated
	 * @param title, the title of the component
	 * @param stdEmailthe email for reports, which is added to the job
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
	 * Return all the types of the components.
	 * @return the compontent types
	 */
	public String[] getComponentTypes() {
		return component_types;
	}
	
	
	/**
	 * Return all jobs in a sorted list. 
	 * @param sortColumn, the property/column the jobs are sorted
	 * @param ascending, if true the list is sorted ascending
	 * @return the list of all jobs
	 */
	public List<JobDetail> getJobs(String sortColumn, boolean ascending) {
		return monitor.getJobs(sortColumn, ascending);
	}
	
	/**
	 * Return all jobs in a sorted list. 
	 * @param sortColumn, the property/column the jobs are sorted
	 * @param ascending, if true the list is sorted ascending
	 * @return the list of all jobs
	 */
	public List<JobDetail> getFilteredJobs(String sortColumn, boolean ascending, HashMap filter, boolean inverse) {
		List<JobDetail> allJobs = monitor.getJobs(sortColumn, ascending);
		List<JobDetail> filteredJobs = new ArrayList();
		Set<String> filterSet = filter.keySet();
		for (int i=0; i<allJobs.size(); i++) {
			if (filter.isEmpty()) {
				filteredJobs.add(allJobs.get(i));
			} else {
				Iterator<String> filtIter = filterSet.iterator();
				while (filtIter.hasNext()) {
					String key = (String)filtIter.next();
					if (allJobs.get(i).getJobDataMap().containsKey(key)) {
						if (allJobs.get(i).getJobDataMap().get(key).equals(filter.get(key)) && !inverse) {
							filteredJobs.add(allJobs.get(i));
						} else if (!(allJobs.get(i).getJobDataMap().get(key).equals(filter.get(key))) && inverse) {
							filteredJobs.add(allJobs.get(i));
						}
					} else if (inverse) {
						filteredJobs.add(allJobs.get(i));
					}
				}
			}
		}
		return filteredJobs;
	}
	
	/**
	 * Reset the average execution time.
	 * @param id, the job id whose time shall be reseted
	 */
	public void resetTime(String id) {
		try {
			JobDetail detail = monitor.getScheduler().getJobDetail(id, IngridMonitorFacade.SCHEDULER_GROUP_NAME);
			detail.getJobDataMap().remove(IngridMonitorIPlugJob.PARAM_TIMER_AVERAGE);
			detail.getJobDataMap().put(IngridMonitorIPlugJob.PARAM_TIMER_NUM, 0);
			// replace existing job with changed values
			monitor.getScheduler().addJob(detail, true);
		} catch (SchedulerException e) {
			log.error("Error manipulating job (" + id + ").", e);
		}
	}
	
	
	/**
	 * Check if a trigger to a job already exists.
	 * @param id, the id of the job
	 * @return true if a trigger exist, otherwise false
	 */
	private boolean triggerExists( String id ) {
		boolean result = false;
		try {
			result = (monitor.getScheduler().getTrigger(id,IngridMonitorFacade.SCHEDULER_GROUP_NAME) != null);
		} catch (SchedulerException e) {
			log.error("Could not get trigger from Scheduler", e);
		}
		return result;
	}
	
	public Trigger getTrigger( String jobName, String jobGroup ) {
		return monitor.getTrigger(jobName, jobGroup);
	}
	
	public long getInterval(JobDetail jobDetail) {
		return monitor.getInterval(jobDetail);
	}
	
	
	/**
	 * Update the JobDataMap of a job with the values of a form.
	 * @param cf, is the action form that contains all the values
	 * @param jobDetail, is the map that contains the job's information
	 */
	private void updateDataFromForm(AdminComponentMonitorForm cf,
			JobDetail jobDetail) {
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
	}
}
