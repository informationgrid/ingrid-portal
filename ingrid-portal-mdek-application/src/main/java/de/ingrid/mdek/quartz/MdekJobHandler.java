package de.ingrid.mdek.quartz;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import de.ingrid.mdek.beans.JobInfoBean;
import de.ingrid.mdek.beans.URLJobInfoBean;
import de.ingrid.mdek.caller.IMdekCallerCatalog;
import de.ingrid.mdek.handler.ConnectionFacade;
import de.ingrid.mdek.quartz.jobs.MdekJob;
import de.ingrid.mdek.quartz.jobs.URLValidatorJob;
import de.ingrid.mdek.quartz.jobs.util.URLObjectReference;
import de.ingrid.mdek.quartz.jobs.util.URLState.State;
import de.ingrid.mdek.util.MdekCatalogUtils;
import de.ingrid.mdek.util.MdekSecurityUtils;
import de.ingrid.utils.IngridDocument;

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
		MdekJob job = new URLValidatorJob(connectionFacade, connectionFacade.getCurrentPlugId());

		try {
			boolean jobStarted = job.start(scheduler);
			if (jobStarted) {
				jobMap.put(job.getName(), job);

			} else {
				log.debug("Could not start URL Validator Job.");
			}

		} catch (SchedulerException ex) {
			log.debug("Error starting URL Validator Job.", ex);
		}
	}

	public URLJobInfoBean getUrlValidatorJobInfo() {
		MdekJob job = getUrlValidatorJob();
		if (job != null && job.getRunningJobInfo() != null) {
			JobInfoBean jobInfo = job.getRunningJobInfo();
			return new URLJobInfoBean(jobInfo);

		} else {
			return getUrlValidatorJobResult();
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

	private URLJobInfoBean getUrlValidatorJobResult() {
		IMdekCallerCatalog mdekCallerCatalog = connectionFacade.getMdekCallerCatalog();
		IngridDocument response = mdekCallerCatalog.getURLInfo(connectionFacade.getCurrentPlugId(), MdekSecurityUtils.getCurrentUserUuid());
		return MdekCatalogUtils.extractUrlJobInfoFromResponse(response);
	}
}
