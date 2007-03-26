/*
 * Copyright (c) 2007 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.scheduler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

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
		sf.initialize("quartz.monitor.properties");
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

	public List getJobs(String sortBy, boolean ascending) {
		ArrayList result = new ArrayList();
		try {
			if (scheduler != null && !scheduler.isShutdown()) {
				String[] jobsInGroup = scheduler.getJobNames(IngridMonitorFacade.SCHEDULER_GROUP_NAME);
				for (int i = 0; i < jobsInGroup.length; i++) {
					result.add(scheduler.getJobDetail(jobsInGroup[i], IngridMonitorFacade.SCHEDULER_GROUP_NAME));
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

	private class JobDetailComparator implements Comparator {

		private String sortBy = null;
		private boolean ascending = true;

		public JobDetailComparator(String sortBy, boolean ascending) {
			this.sortBy = sortBy;
			this.ascending = ascending;
		}

		/**
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public final int compare(Object a, Object b) {

			try {
				JobDetail jobA = (JobDetail) a;
				JobDetail jobB = (JobDetail) b;

				String aName = jobA.getJobDataMap().getString(sortBy).toLowerCase();
				String bName = jobB.getJobDataMap().getString(sortBy).toLowerCase();

				if (ascending) {
					return aName.compareTo(bName);
				} else {
					return aName.compareTo(bName) * -1;
				}
			} catch (Exception e) {
				return 0;
			}
		}
	}

}
