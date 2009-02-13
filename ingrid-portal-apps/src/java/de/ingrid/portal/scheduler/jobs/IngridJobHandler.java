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
	private static final String[] component_types = {
		"component.monitor.general.type.iplug",
		"component.monitor.general.type.g2k",
		"component.monitor.general.type.csw",
		"component.monitor.general.type.ecs",
		"component.monitor.general.type.sns",
		"component.monitor.general.type.ibus",
		"component.monitor.general.type.rss",
		"component.monitor.general.type.rssfetcher",
		"component.monitor.general.type.provider.check"
	};
	
	private final static Log log = LogFactory.getLog(AdminComponentMonitorPortlet.class);
	
	IngridMonitorFacade monitor = IngridMonitorFacade.instance();
	
	
	/**
	 * Return the JobDataMap, which is used for storing job relevant data .
	 * @param id, is the job id that contains the data map
	 * @return the JobDataMap object
	 */
	public JobDataMap getJobDataMap(String id) {
		return getJobDetail(id).getJobDataMap();
	}
	
	public JobDetail getJobDetail( String id ) {
		JobDetail jobDetail = null;
		try {
			jobDetail = monitor.getScheduler().getJobDetail(id, IngridMonitorFacade.SCHEDULER_GROUP_NAME);
			if (jobDetail == null) {
				jobDetail = monitor.getScheduler().getJobDetail(id, "DEFAULT");
			}
		} catch (SchedulerException e) {
			log.error("Error getting job (" + id + ")", e);
		}
		return jobDetail;
	}
	
	/**
	 * Get the name of a job.
	 * @param id, is the job id that contains the name
	 * @return the string of the job name
	 */
	public String getJobName( String id ) {
		return getJobDetail(id).getName();
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
	public boolean updateJob(String id, AdminComponentMonitorForm cf) {
		try {
			JobDetail jobDetail = monitor.getScheduler().getJobDetail(id,
					IngridMonitorFacade.SCHEDULER_GROUP_NAME);
						
			//updateDataFromForm(cf, jobDetail);
			String componentType = cf.getInput(AdminComponentMonitorForm.FIELD_TYPE);
			
			// get the correct class depending on the monitor type
			Class jobClass = getClassFromMonitor(cf);
			
			// happens if a singleton job wanted to be created a second time 
			if (cf.hasErrors()) {
				//return false;
				// cannot happen when updating a job
				cf.clearErrors();
			}

			if (jobClass != null) {
				jobDetail.setJobClass(jobClass);
				JobDataMap dataMap = jobDetail.getJobDataMap();
				int active = cf.getInputAsInteger(AdminComponentMonitorForm.FIELD_ACTIVE, new Integer(0));
		
				dataMap.put(IngridMonitorIPlugJob.PARAM_ACTIVE, active);
				dataMap.put(IngridMonitorIPlugJob.PARAM_CHECK_INTERVAL, cf.getInputAsInteger(
						AdminComponentMonitorForm.FIELD_INTERVAL, new Integer(30)));
				dataMap.put(IngridMonitorIPlugJob.PARAM_TIMEOUT, cf.getInputAsInteger(
						AdminComponentMonitorForm.FIELD_TIMEOUT, new Integer(500)));
				dataMap.put(IngridMonitorIPlugJob.PARAM_COMPONENT_TITLE, cf
						.getInput(AdminComponentMonitorForm.FIELD_TITLE));
				
				dataMap.put(IngridMonitorIPlugJob.PARAM_COMPONENT_TYPE, componentType);
				
				dataMap.put(IngridMonitorIPlugJob.PARAM_SERVICE_URL, cf
						.getInput(AdminComponentMonitorForm.FIELD_SERVICE_URL));
				dataMap.put(IngridMonitorIPlugJob.PARAM_QUERY, cf
						.getInput(AdminComponentMonitorForm.FIELD_QUERY));
				dataMap.put(IngridMonitorIPlugJob.PARAM_EXCLUDED_PROVIDER, cf
						.getInput(AdminComponentMonitorForm.FIELD_EXCLUDED_PROVIDER));
				
				// event occurences back to zero after job was updated!
				dataMap.put(IngridMonitorIPlugJob.PARAM_EVENT_OCCURENCES, 0);
				
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
			} else {
				//throw SchedulerException;
				//return false;
			}
			
			//===============================
			monitor.getScheduler().addJob(jobDetail, true);

			jobDetail.setRequestsRecovery(false);
			
			int active = jobDetail.getJobDataMap().getInt(IngridMonitorIPlugJob.PARAM_ACTIVE);
			
			if (active==1) {
				int interval = cf.getInputAsInteger(AdminComponentMonitorForm.FIELD_INTERVAL, new Integer(30)).intValue();
				createTrigger(id, interval);
			} else {
				monitor.getScheduler().pauseTrigger(id,IngridMonitorFacade.SCHEDULER_GROUP_NAME);
			}
		} catch (SchedulerException e) {
			log.error("Error updating job (" + id + ").", e);
		}
		return true;
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
			// get the correct class depending on the monitor type
			Class jobClass = getClassFromMonitor(cf);
			
			// happens if a singleton job wanted to be created a second time 
			if (cf.hasErrors()) {
				return false;
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
				dataMap.put(IngridMonitorAbstractJob.PARAM_SERVICE_URL, cf
						.getInput(AdminComponentMonitorForm.FIELD_SERVICE_URL));
				dataMap.put(IngridMonitorAbstractJob.PARAM_QUERY, cf
						.getInput(AdminComponentMonitorForm.FIELD_QUERY));
				dataMap.put(IngridMonitorAbstractJob.PARAM_EXCLUDED_PROVIDER, cf
						.getInput(AdminComponentMonitorForm.FIELD_EXCLUDED_PROVIDER));
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
					int interval = cf.getInputAsInteger(AdminComponentMonitorForm.FIELD_INTERVAL, new Integer(1800)).intValue();
					createTrigger(id, interval);
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
	 * @param id
	 * @return
	 * @throws SchedulerException
	 */
	public boolean jobExists(String id) {
		JobDetail jobDetail = null;
		try {
			jobDetail = monitor.getScheduler().getJobDetail(id, IngridMonitorFacade.SCHEDULER_GROUP_NAME);
		} catch (SchedulerException e) {
			// TODO AW: Auto-generated catch block
			e.printStackTrace();
		}
		if (jobDetail != null) {
			return true;
		}
		return false;
	}

	private Class getClassFromMonitor(AdminComponentMonitorForm cf) {
		Class jobClass = null;
		String type = cf.getInput(AdminComponentMonitorForm.FIELD_TYPE);
		if (type.equals(IngridMonitorIPlugJob.COMPONENT_TYPE)) {
			jobClass = IngridMonitorIPlugJob.class;
		} else if (type.equals(IngridMonitorG2KJob.COMPONENT_TYPE)) {
			jobClass = IngridMonitorG2KJob.class;
		} else if (type.equals(IngridMonitorECSJob.COMPONENT_TYPE)) {
			jobClass = IngridMonitorECSJob.class;
		} else if (type.equals(IngridMonitorCSWJob.COMPONENT_TYPE)) {
			jobClass = IngridMonitorCSWJob.class;
		} else	if (type.equals(IngridMonitorSNSJob.COMPONENT_TYPE)) {
			jobClass = IngridMonitorSNSJob.class;
		} else	if (type.equals(IngridMonitorIBusJob.COMPONENT_TYPE)) {
			jobClass = IngridMonitorIBusJob.class;
			
			// IBus shall exist only once
			if (jobClassExists(IngridMonitorIBusJob.class)) {
				cf.setError(AdminComponentMonitorForm.FIELD_TYPE, "component.monitor.form.error.single.job");
				//return null;
			}
		} else	if (type.equals(IngridMonitorRSSCheckerJob.COMPONENT_TYPE)) {
			jobClass = IngridMonitorRSSCheckerJob.class;
		} else	if (type.equals(IngridMonitorRSSFetcherJob.COMPONENT_TYPE)) {
			jobClass = IngridMonitorRSSFetcherJob.class;
			
			// RSSFetcherJob shall exist only once
			if (jobClassExists(IngridMonitorRSSFetcherJob.class)) {
				cf.setError(AdminComponentMonitorForm.FIELD_TYPE, "component.monitor.form.error.single.job");
				//return null;
			}
		} else	if (type.equals(IngridMonitorProviderCheckJob.COMPONENT_TYPE)) {
			jobClass = IngridMonitorProviderCheckJob.class;
			
			// RSSProviderCheck shall exist only once
			if (jobClassExists(IngridMonitorProviderCheckJob.class)) {
				cf.setError(AdminComponentMonitorForm.FIELD_TYPE, "component.monitor.form.error.single.job");
				//return null;
			}
		}
		return jobClass;
	}
	
	public boolean jobClassExists( Class aClass ) {
		return monitor.jobClassExists( aClass );
	}
	
	public boolean jobTypeOnlyOnce( String type ) {
		boolean result = false;
		if (type.equals(IngridMonitorProviderCheckJob.COMPONENT_TYPE)) {
			result = monitor.jobClassExists( IngridMonitorProviderCheckJob.class );
		} else	if (type.equals(IngridMonitorRSSFetcherJob.COMPONENT_TYPE)) {
			result = monitor.jobClassExists( IngridMonitorRSSFetcherJob.class );
		} else	if (type.equals(IngridMonitorIBusJob.COMPONENT_TYPE)) {
			result = monitor.jobClassExists( IngridMonitorIBusJob.class );
		}
		return result;
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
		ArrayList<String> jobIds = new ArrayList<String>();;
		
		// only fill array with present jobs if you don't want to overwrite them
		if (request.getParameter("overwrite") == null) {
			// get all present iPlugs
			List<JobDetail> existingJobs = monitor.getJobs(null, true);
			
			for (int i=0; i < existingJobs.size(); i++) {
				jobIds.add(((JobDetail)existingJobs.get(i)).getName());
			}
		} else {
			// remove all jobs
			monitor.deleteAllJobs(IngridMonitorIPlugJob.COMPONENT_TYPE);
			monitor.deleteAllJobs(IngridMonitorIBusJob.COMPONENT_TYPE);
			monitor.deleteAllJobs(IngridMonitorRSSCheckerJob.COMPONENT_TYPE);
			monitor.deleteAllJobs(IngridMonitorRSSFetcherJob.COMPONENT_TYPE);
			monitor.deleteAllJobs(IngridMonitorProviderCheckJob.COMPONENT_TYPE);
		}
		
		// *****************************************
		// add IBusJob
		// *****************************************
		addIBusJob(activate, stdEmail);
		
		// *****************************************
		// add RSSFetcherJob (Monitor!)
		// *****************************************
		addMonitorRSSFetcherJob(activate, stdEmail);
		
		// *****************************************
		// add ProviderCheckJob
		// *****************************************
		addProviderCheckJob(activate, stdEmail);
		
		// *****************************************
		// add RSSCheckerJobs
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
					"wasser iplugs:\"" + iPlug.getProxyServiceURL() + "\" ranking:any cache:off");
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
				createTrigger(iPlug.getPlugId(), 1800);
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
		String jobId = IngridMonitorIBusJob.JOB_ID;
		
		// does the iBus component already exists?
		try {
			jobDetail = monitor.getScheduler().getJobDetail(jobId, IngridMonitorFacade.SCHEDULER_GROUP_NAME);
		} catch (SchedulerException e) {
			jobDetail = null;
		}
		
		if (jobDetail == null) {
			jobDetail = new JobDetail(jobId, IngridMonitorFacade.SCHEDULER_GROUP_NAME,
				IngridMonitorIBusJob.class);
			
			// here the ID of the job is also used as the name
			fillJobDataMap(jobDetail.getJobDataMap(), activate, jobId, stdEmail);
			
			// add specific data to the dataMap
			jobDetail.getJobDataMap().put(IngridMonitorIPlugJob.PARAM_COMPONENT_TYPE,
					IngridMonitorIBusJob.COMPONENT_TYPE);
			
			jobDetail.setRequestsRecovery(false);
			
			// add the job to the scheduler without a trigger
			try {
				monitor.getScheduler().addJob(jobDetail, true);
			} catch (SchedulerException e) {
				log.error("Error creating bus job (" + jobId + ").", e);
			}
			
			if (activate) {
				createTrigger(jobId, 1800);
			}
		}
	}
	
	/**
	 * Add the iBus component as a monitoring job.
	 * @param activate, if true the job will be immediately activated
	 * @param stdEmail, the email for reports, which is added to the job
	 */
	private void addProviderCheckJob( boolean activate, String stdEmail ) {
		JobDetail jobDetail;
		String jobId = IngridMonitorProviderCheckJob.JOB_ID;
		
		// does the iBus component already exists?
		try {
			jobDetail = monitor.getScheduler().getJobDetail(jobId, IngridMonitorFacade.SCHEDULER_GROUP_NAME);
		} catch (SchedulerException e) {
			jobDetail = null;
		}
		
		if (jobDetail == null) {
			jobDetail = new JobDetail(jobId, IngridMonitorFacade.SCHEDULER_GROUP_NAME,
				IngridMonitorProviderCheckJob.class);
			
			// here the ID of the job is also used as the name
			fillJobDataMap(jobDetail.getJobDataMap(), activate, jobId, stdEmail);
			
			// add specific data to the dataMap
			jobDetail.getJobDataMap().put(IngridMonitorIPlugJob.PARAM_COMPONENT_TYPE,
					IngridMonitorProviderCheckJob.COMPONENT_TYPE);
			
			jobDetail.setRequestsRecovery(false);
			
			// add the job to the scheduler without a trigger
			try {
				monitor.getScheduler().addJob(jobDetail, true);
			} catch (SchedulerException e) {
				log.error("Error creating bus job (" + jobId + ").", e);
			}
			
			if (activate) {
				createTrigger(jobId, 1800);
			}
		}
	}

	/**
	 * 
	 * @param activate
	 * @param stdEmail
	 */
	private void addMonitorRSSFetcherJob( boolean activate, String stdEmail ) {
		JobDetail jobDetail;
		String jobId = IngridMonitorRSSFetcherJob.JOB_ID;
		
		// does the component already exists?
		try {
			jobDetail = monitor.getScheduler().getJobDetail(jobId, IngridMonitorFacade.SCHEDULER_GROUP_NAME);
		} catch (SchedulerException e) {
			jobDetail = null;
		}
		
		if (jobDetail == null) {
			jobDetail = new JobDetail(jobId, IngridMonitorFacade.SCHEDULER_GROUP_NAME,
					IngridMonitorRSSFetcherJob.class);
			
			// here the ID of the job is also used as the name
			fillJobDataMap(jobDetail.getJobDataMap(), activate, jobId, stdEmail);
			
			// add specific data to the dataMap
			jobDetail.getJobDataMap().put(IngridMonitorIPlugJob.PARAM_COMPONENT_TYPE,
					IngridMonitorRSSFetcherJob.COMPONENT_TYPE);
			
			jobDetail.setRequestsRecovery(false);
			
			// add the job to the scheduler without a trigger
			try {
				monitor.getScheduler().addJob(jobDetail, true);
			} catch (SchedulerException e) {
				log.error("Error creating monitor for RSSFetcher-job (" + jobId + ").", e);
			}
			
			if (activate) {
				createTrigger(jobId, 1800);
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
		String jobId = rssSource.getUrl();
		
		// does the RSS component already exists?
		try {
			jobDetail = monitor.getScheduler().getJobDetail(jobId, IngridMonitorFacade.SCHEDULER_GROUP_NAME);
		} catch (SchedulerException e) {
			jobDetail = null;
		}
		
		if (jobDetail == null) {
			jobDetail = new JobDetail(jobId, IngridMonitorFacade.SCHEDULER_GROUP_NAME,
					IngridMonitorRSSCheckerJob.class);
			
			// here the ID of the job is also used as the name
			fillJobDataMap(jobDetail.getJobDataMap(), activate, rssSource.getDescription(), stdEmail);
			
			// add specific data to the dataMap
			jobDetail.getJobDataMap().put(IngridMonitorRSSCheckerJob.PARAM_COMPONENT_TYPE,
					IngridMonitorRSSCheckerJob.COMPONENT_TYPE);
			jobDetail.getJobDataMap().put(IngridMonitorRSSCheckerJob.PARAM_SERVICE_URL, jobId);
			
			jobDetail.setRequestsRecovery(false);
			
			// add the job to the scheduler without a trigger
			try {
				monitor.getScheduler().addJob(jobDetail, true);
			} catch (SchedulerException e) {
				log.error("Error creating rss job (" + jobId + ").", e);
			}
			
			if (activate) {
				createTrigger(jobId, 1800);
			}
		}
	}
	
	/**
	 * 
	 * @param jobId
	 */
	private void createTrigger(String jobId, int interval) {
		Trigger trigger = TriggerUtils.makeSecondlyTrigger(interval);
		trigger.setStartTime(new Date());
		trigger.setName(jobId);
		trigger.setGroup(IngridMonitorFacade.SCHEDULER_GROUP_NAME);
		trigger.setJobName(jobId);
		trigger.setJobGroup(IngridMonitorFacade.SCHEDULER_GROUP_NAME);
		
		try {
			if (triggerExists(jobId)) {
				monitor.getScheduler().rescheduleJob(jobId, IngridMonitorFacade.SCHEDULER_GROUP_NAME, trigger);
			} else {
				monitor.getScheduler().scheduleJob(trigger);
			}
		} catch (SchedulerException e) {
			log.error("Error while scheduling the job (" + jobId + ").", e);
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
			JobDetail detail = getJobDetail(id);
			detail.getJobDataMap().remove(IngridMonitorIPlugJob.PARAM_TIMER_AVERAGE);
			detail.getJobDataMap().put(IngridMonitorIPlugJob.PARAM_TIMER_NUM, 0);
			// replace existing job with changed values
			monitor.getScheduler().addJob(detail, true);
		} catch (SchedulerException e) {
			log.error("Error adding job (" + id + ") to scheduler.", e);
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
	
	/**
	 * 
	 * @param jobName
	 * @param jobGroup
	 * @return
	 */
	public Trigger getTrigger( String jobName, String jobGroup ) {
		return monitor.getTrigger(jobName, jobGroup);
	}
	
	/**
	 * 
	 * @param jobDetail
	 * @return
	 */
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
		String componentType = cf.getInput(AdminComponentMonitorForm.FIELD_TYPE);
		
		// get the correct class depending on the monitor type
		Class jobClass = getClassFromMonitor(cf);
		
		// happens if a singleton job wanted to be created a second time 
		//if (cf.hasErrors()) {
		//	return false;
		//}

		if (jobClass != null) {
			jobDetail.setJobClass(jobClass);
			JobDataMap dataMap = jobDetail.getJobDataMap();
			int active = cf.getInputAsInteger(AdminComponentMonitorForm.FIELD_ACTIVE, new Integer(0));
	
			dataMap.put(IngridMonitorIPlugJob.PARAM_ACTIVE, active);
			dataMap.put(IngridMonitorIPlugJob.PARAM_CHECK_INTERVAL, cf.getInputAsInteger(
					AdminComponentMonitorForm.FIELD_INTERVAL, new Integer(30)));
			dataMap.put(IngridMonitorIPlugJob.PARAM_TIMEOUT, cf.getInputAsInteger(
					AdminComponentMonitorForm.FIELD_TIMEOUT, new Integer(500)));
			dataMap.put(IngridMonitorIPlugJob.PARAM_COMPONENT_TITLE, cf
					.getInput(AdminComponentMonitorForm.FIELD_TITLE));
			
			dataMap.put(IngridMonitorIPlugJob.PARAM_COMPONENT_TYPE, componentType);
			
			dataMap.put(IngridMonitorIPlugJob.PARAM_QUERY, cf
					.getInput(AdminComponentMonitorForm.FIELD_QUERY));
			dataMap.put(IngridMonitorIPlugJob.PARAM_SERVICE_URL, cf
					.getInput(AdminComponentMonitorForm.FIELD_SERVICE_URL));
			dataMap.put(IngridMonitorIPlugJob.PARAM_EXCLUDED_PROVIDER, cf
					.getInput(AdminComponentMonitorForm.FIELD_EXCLUDED_PROVIDER));
	
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
		} else {
			//throw SchedulerException;
		}
	}
	
	/**
	 * Check if a job is from group-type default.
	 * @param id
	 * @return
	 */	
	public boolean isDefaultJob(String id) {
		JobDetail jobDetail = null;
		try {
			jobDetail = monitor.getScheduler().getJobDetail(id, "DEFAULT");
		} catch (SchedulerException e) {
			//
		}
		if (jobDetail != null)
			return true;
		return false;
	}
}
