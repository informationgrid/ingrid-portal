package de.ingrid.mdek.quartz;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import de.ingrid.mdek.beans.JobInfoBean;
import de.ingrid.mdek.handler.ConnectionFacade;
import de.ingrid.mdek.quartz.jobs.MdekJob;
import de.ingrid.mdek.quartz.jobs.URLValidatorJob;

public class MdekJobHandler {

	private final static Logger log = Logger.getLogger(MdekJobHandler.class);

	private Scheduler scheduler;
	private ConnectionFacade connectionFacade;
	private Map<String, MdekJob> jobMap;

	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
		this.jobMap = new HashMap<String, MdekJob>();
	}

	public void setConnectionFacade(ConnectionFacade connectionFacade) {
		this.connectionFacade = connectionFacade;
	}

	public void startUrlValidatorJob() {
		MdekJob job = new URLValidatorJob(connectionFacade.getMdekCallerQuery(), connectionFacade.getCurrentPlugId());

		try {
			boolean jobStarted = job.start(scheduler);
			if (jobStarted) {
				jobMap.put(job.getName(), job);

			} else {
				log.debug("Could not start URL Validator Job.");
			}

		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public JobInfoBean getUrlValidatorJobInfo() {
		MdekJob job = getUrlValidatorJob();
		if (job != null) {
			return job.getRunningJobInfo();

		} else {
			return new JobInfoBean();
		}
	}

	private URLValidatorJob getUrlValidatorJob() {
		String jobName = URLValidatorJob.createJobName(connectionFacade.getCurrentPlugId());
		return (URLValidatorJob) jobMap.get(jobName);
	}

	private void removeUrlValidatorJob() {
		String jobName = URLValidatorJob.createJobName(connectionFacade.getCurrentPlugId());
		jobMap.remove(jobName);
	}

	public void stopUrlValidatorJob() {
		MdekJob job = getUrlValidatorJob();
		if (job != null) {
			job.stop();
			removeUrlValidatorJob();

		} else {
			log.debug("Could not stop URL Validation job. It probably is not running.");
		}
	}
}
