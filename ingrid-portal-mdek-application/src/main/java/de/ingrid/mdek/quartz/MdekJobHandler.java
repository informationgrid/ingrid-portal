package de.ingrid.mdek.quartz;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import de.ingrid.mdek.beans.JobInfoBean;
import de.ingrid.mdek.beans.URLJobInfoBean;
import de.ingrid.mdek.caller.IMdekCallerCatalog;
import de.ingrid.mdek.dwr.services.sns.SNSService;
import de.ingrid.mdek.handler.ConnectionFacade;
import de.ingrid.mdek.job.IJob.JobType;
import de.ingrid.mdek.quartz.jobs.MdekJob;
import de.ingrid.mdek.quartz.jobs.SNSUpdateJob;
import de.ingrid.mdek.quartz.jobs.URLValidatorJob;
import de.ingrid.mdek.util.MdekCatalogUtils;
import de.ingrid.mdek.util.MdekSecurityUtils;
import de.ingrid.utils.IngridDocument;

public class MdekJobHandler implements BeanFactoryAware {

	private final static Logger log = Logger.getLogger(MdekJobHandler.class);

	// Spring Bean Factory for lookup of job specific dependencies
	private BeanFactory beanFactory;

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
		MdekJob job = new URLValidatorJob(connectionFacade);

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
		IngridDocument response = mdekCallerCatalog.getJobInfo(
				connectionFacade.getCurrentPlugId(),
				JobType.URL,
				MdekSecurityUtils.getCurrentUserUuid());

		return MdekCatalogUtils.extractUrlJobInfoFromResponse(response);
	}


	public void startSNSUpdateJob(String[] changedTopics, String[] newTopics, String[] expiredTopics) {
		MdekJob job = new SNSUpdateJob(
				connectionFacade,
				(SNSService) beanFactory.getBean("snsService"),
				changedTopics, newTopics, expiredTopics);

		try {
			boolean jobStarted = job.start(scheduler);
			if (jobStarted) {
				jobMap.put(job.getName(), job);

			} else {
				log.debug("Could not start SNS Update Job.");
			}

		} catch (SchedulerException ex) {
			log.debug("Error starting SNS Update Job.", ex);
		}
	}

	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}
}
