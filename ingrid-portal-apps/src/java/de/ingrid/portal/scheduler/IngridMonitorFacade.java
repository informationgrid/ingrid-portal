/*
 * Copyright (c) 2007 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.scheduler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import de.ingrid.portal.scheduler.jobs.IngridAbstractStateJob;
import de.ingrid.portal.scheduler.jobs.IngridMonitorAbstractJob;
import de.ingrid.portal.servlet.IngridComponentMonitorStartListener;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class IngridMonitorFacade {

	private final static Log log = LogFactory.getLog(IngridComponentMonitorStartListener.class);

	private static IngridMonitorFacade instance = null;

	private Scheduler scheduler = null;

	public static final String SCHEDULER_GROUP_NAME = "monitor";

	private IngridMonitorFacade() throws SchedulerException {
		StdSchedulerFactory sf = new StdSchedulerFactory();
		sf.initialize("quartz.properties");
		scheduler = sf.getScheduler();
		scheduler.start();

		log.info("ingrid component monitor startet.");
	}

	public static synchronized IngridMonitorFacade instance() {
		if (instance == null) {
			try {
				instance = new IngridMonitorFacade();
			} catch (SchedulerException e) {
				log.error("ingrid component monitor could not be started.", e);
			}
		}
		return instance;
	}

	public synchronized void shutdownMonitor(boolean arg) throws SchedulerException {
		if (scheduler != null) {
			scheduler.shutdown(arg);
		}
		log.info("ingrid component monitor shut down.");
	}

	public Scheduler getScheduler() {
		return scheduler;
	}

	public List<JobDetail> getJobs(String sortBy, boolean ascending) {
		ArrayList<JobDetail> result = new ArrayList<JobDetail>();
		try {
			if (scheduler != null && !scheduler.isShutdown()) {
				String[] allJobGroupNames = scheduler.getJobGroupNames();
				for (int i=0; i<allJobGroupNames.length; i++) {
					String[] allJobsOfAGroup = scheduler.getJobNames(allJobGroupNames[i]);
					for (int j=0; j<allJobsOfAGroup.length; j++) {
						result.add(scheduler.getJobDetail(allJobsOfAGroup[j], allJobGroupNames[i]));
					}
				}
			}
		} catch (SchedulerException e) {
			log.error("Error getting jobs from scheduler.", e);
		}
		if (sortBy != null) {
			Collections.sort(result, new JobDetailComparator(sortBy, ascending));
		}
		return result;
	}
	
	
	public Trigger getTrigger( String jobName, String jobGroup ) {
		
		Trigger trigger = null;
		
		try {
			Trigger[] triggers = scheduler.getTriggersOfJob(jobName, jobGroup);
			if (triggers.length > 1) {
				log.error("The job" + jobName + " has more than one trigger!");
			} 
			if (triggers.length > 0) {
				trigger = triggers[0];
			}
		} catch (SchedulerException e) {
			log.error("Error while getting trigger of job " + jobName + "!");
			e.printStackTrace();			
		}
		
		return trigger;
	}
	
	public long getInterval(JobDetail jobDetail) {
		long interval = 0;
		SimpleTrigger t = (SimpleTrigger) getTrigger( jobDetail.getName(), jobDetail.getGroup() );
		if (t == null) {
			// use it from dataMap
			interval = jobDetail.getJobDataMap().getInt(IngridAbstractStateJob.PARAM_CHECK_INTERVAL);
		} else {
			interval = t.getRepeatInterval()/1000;
		}
		return interval;
		
	}
	
	public boolean deleteAllJobs(String jobType) {
		try {
			if (scheduler != null && !scheduler.isShutdown()) {
				String[] jobsInGroup = scheduler.getJobNames(IngridMonitorFacade.SCHEDULER_GROUP_NAME);
				
				for (int i = 0; i < jobsInGroup.length; i++) {
					JobDataMap data = scheduler.getJobDetail(jobsInGroup[i], IngridMonitorFacade.SCHEDULER_GROUP_NAME).getJobDataMap();
					if (jobType == null || data.getString(IngridMonitorAbstractJob.PARAM_COMPONENT_TYPE).equals(jobType)) {
						this.deleteJob(jobsInGroup[i]);
					}
				}
				return true;
			}
		} catch (SchedulerException e) {
			log.error("Error deleting all jobs from scheduler.", e);
		}
		return false;
	}
	
	public boolean deleteJob(String jobName) {
		try {
			if (scheduler != null && !scheduler.isShutdown()) {
				scheduler.deleteJob(jobName, IngridMonitorFacade.SCHEDULER_GROUP_NAME);
				return true;
			}
		} catch (SchedulerException e) {
			log.error("Error deleting job (" + jobName + ") from scheduler.", e);
		}
		return false;
	}
	
	public void scheduleJob(JobDetail job, Trigger trigger) {
		try {
			if (scheduler != null && !scheduler.isShutdown()) {
				scheduler.scheduleJob(job, trigger);
			}
		} catch (SchedulerException e) {
			log.error("Error scheduling job (" + job.getName() + ") with trigger (" + trigger.getName() + ").", e);
		}
	}
	
	public void triggerJob(String name, String group) throws SchedulerException {
		scheduler.triggerJob(name, group);
	}
	
	
	public boolean isExecuting(String name, String group, JobExecutionContext context) {
		Scheduler scheduler = context.getScheduler();
		try {
			List jobs = scheduler.getCurrentlyExecutingJobs();
			if (jobs.isEmpty()) {
				return false;
			} 
			for (int i = 0; i < jobs.size(); i++){ 
				JobExecutionContext jobExecutionContext = (JobExecutionContext) jobs.get(i);
				if (jobExecutionContext.getJobDetail().getName().equals(context.getJobDetail().getName())) {
					if (jobExecutionContext.getTrigger().getName().equals(name) ||
							jobExecutionContext.getTrigger().getGroup().equals(group)) {
						return true; 
					}
				}
			} 
		} catch (SchedulerException e) {
			e.printStackTrace(); 
		} 
		return false;
	} 
	
	/**
	 * 
	 * @param jobClass
	 * @return
	 */
	public boolean jobClassExists( Class jobClass ) {
		List jobs = getJobs(null, true);
		
		for (int i = 0; i < jobs.size(); i++) {
			if (((JobDetail)jobs.get(i)).getJobClass() ==  jobClass) {
				return true;
			}
		}
		return false;
	}

	private class JobDetailComparator implements Comparator {

		private String sortBy = null;
		private boolean ascending = true;

		public JobDetailComparator(String sortBy, boolean ascending) {
			this.sortBy = sortBy;
			this.ascending = ascending;
		}

		/**
		 * 
		 * @param jobDetail
		 * @return
		 */
		private Object getObject( JobDetail jobDetail ) {
			Object object = null;
			
			try {
				if (sortBy.equals(IngridAbstractStateJob.PARAM_COMPONENT_TITLE)) {
					if (jobDetail.getGroup().equals("monitor")) {
						object = jobDetail.getJobDataMap().get(sortBy);
					} else {
						object = jobDetail.getName();
					}				
				} else if (sortBy.equals(IngridAbstractStateJob.PARAM_COMPONENT_TYPE)) {
					object = jobDetail.getJobDataMap().get(sortBy);
				} else if (sortBy.equals(IngridAbstractStateJob.PARAM_STATUS)) {
					object = jobDetail.getJobDataMap().get(sortBy);
				} else if (sortBy.equals(IngridAbstractStateJob.PARAM_LAST_CHECK)) {
					Trigger trigger = getTrigger(jobDetail.getName(), jobDetail.getGroup());
					if (trigger != null) 
						object = trigger.getPreviousFireTime();
				} else if (sortBy.equals(IngridAbstractStateJob.PARAM_NEXT_CHECK)) {
					Trigger trigger = getTrigger(jobDetail.getName(), jobDetail.getGroup());
					if (trigger != null)
						object = trigger.getNextFireTime();
				} else if (sortBy.equals(IngridAbstractStateJob.PARAM_TIMER_AVERAGE)) {
					object = jobDetail.getJobDataMap().get(sortBy);				
				} else if (sortBy.equals(IngridAbstractStateJob.PARAM_CHECK_INTERVAL)) {
					object = getInterval(jobDetail);				
				}
			} catch (Exception e) {
				log.error("Couldn't return object when comparing " + jobDetail.getName());
				return null;
			}
			return object;
		}
		
		/**
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public final int compare(Object a, Object b) {

			try {
				JobDetail jobA = (JobDetail) a;
				JobDetail jobB = (JobDetail) b;

				Object aName = getObject(jobA);
				Object bName = getObject(jobB);
				
				if (ascending) {
					if (aName instanceof String) {
						return ((String)aName).toLowerCase().compareTo(((String)bName).toLowerCase());
					} else if (aName instanceof Integer) {
						return ((Integer)aName).compareTo(((Integer)bName));
					} else if (aName instanceof Long) {
						if (bName == null) {
							return -1;
						} else {
							return ((Long)aName).compareTo(((Long)bName)); 
						}
					} else if (aName instanceof Date) {
						if (bName == null) {
							return -1;
						}
						return ((Date)aName).compareTo(((Date)bName));
					} else if (aName == null) {
						return 1;
					}
				} else {
					if (aName instanceof String) {
						return ((String)aName).toLowerCase().compareTo(((String)bName).toLowerCase()) * -1;
					} else if (aName instanceof Integer) {
						return ((Integer)aName).compareTo(((Integer)bName)) * -1;
					} else if (aName instanceof Long) {
						if (bName == null) {
							return -1;
						} else {
							return ((Long)aName).compareTo(((Long)bName)) * -1;
						}
					} else if (aName instanceof Date) {
						if (bName == null) {
							return 1;
						}
						return ((Date)aName).compareTo(((Date)bName)) * -1;
					} else if (aName == null) {
						return 1;
					}
				}
				return 0;
			} catch (Exception e) {
				return 0;
			}
		}
	}

}
