/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.mdek.quartz;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.ingrid.mdek.MdekKeys;
import de.ingrid.mdek.MdekUtils;
import de.ingrid.mdek.beans.JobInfoBean;
import de.ingrid.mdek.beans.SNSLocationUpdateJobInfoBean;
import de.ingrid.mdek.beans.SNSUpdateJobInfoBean;
import de.ingrid.mdek.beans.URLJobInfoBean;
import de.ingrid.mdek.caller.IMdekCallerCatalog;
import de.ingrid.mdek.dwr.services.sns.SNSService;
import de.ingrid.mdek.handler.ConnectionFacade;
import de.ingrid.mdek.quartz.jobs.MdekJob;
import de.ingrid.mdek.quartz.jobs.SNSLocationUpdateJob;
import de.ingrid.mdek.quartz.jobs.SNSUpdateJob;
import de.ingrid.mdek.quartz.jobs.URLValidatorJob;
import de.ingrid.mdek.util.MdekCatalogUtils;
import de.ingrid.mdek.util.MdekSecurityUtils;
import de.ingrid.utils.IngridDocument;

@Service
public class MdekJobHandler implements BeanFactoryAware {

	private static final Logger log = Logger.getLogger(MdekJobHandler.class);

	// Spring Bean Factory for lookup of job specific dependencies
	private BeanFactory beanFactory;

	// Spring injected
	private Scheduler scheduler;
	
	@Autowired
	private ConnectionFacade connectionFacade;
	
	private Map<String, MdekJob> jobMap;

	public enum JobType { URL_VALIDATOR, SNS_UPDATE, SNS_LOCATION_UPDATE }

	@Autowired
	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
		this.jobMap = new HashMap<>();
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
				// initialize the job data for this job
				// since this is a long running job with segmented data for the backend to be stored,
				// we have to make sure at the beginning that it's marked running, otherwise the frontend
				// might display the wrong/old data 
				connectionFacade.getMdekCallerCatalog().setURLInfo( connectionFacade.getCurrentPlugId(), getInitialJobInfo( new Date() ), MdekSecurityUtils.getCurrentUserUuid() );

			} else {
				log.debug("Could not start URL Validator Job.");
			}

		} catch (SchedulerException ex) {
			log.debug("Error starting URL Validator Job.", ex);
		}
	}
	
	private IngridDocument getInitialJobInfo(Date startTime) {
      IngridDocument jobInfo = new IngridDocument();
        jobInfo.put(MdekKeys.URL_RESULT, new ArrayList<Map<String, Object>>());
        jobInfo.put(MdekKeys.CAP_RESULT, new ArrayList<Map<String, Object>>());
        jobInfo.put(MdekKeys.JOBINFO_START_TIME, MdekUtils.dateToTimestamp(startTime));
        jobInfo.putBoolean( MdekKeys.JOBINFO_IS_UPDATE, false );
        jobInfo.putBoolean( MdekKeys.JOBINFO_IS_FINISHED, false );
        return jobInfo;
    }

	public JobInfoBean getJobInfo(JobType jobType) {
		MdekJob job = getJob(jobType);
		if (job != null && job.getRunningJobInfo() != null) {
			JobInfoBean jobInfo = job.getRunningJobInfo();
			return createJobInfo(jobInfo, jobType);

		} else {
			return getJobResult(jobType);
		}
	}

	private MdekJob getJob(JobType jobType) {
		return jobMap.get(getJobName(jobType));
	}

	private JobInfoBean createJobInfo(JobInfoBean jobInfo, JobType jobType) {
		switch (jobType) {
		case URL_VALIDATOR:
			return new URLJobInfoBean(jobInfo);

		case SNS_UPDATE:
			return new SNSUpdateJobInfoBean(jobInfo);

		case SNS_LOCATION_UPDATE:
			return new SNSLocationUpdateJobInfoBean(jobInfo);

		default:
			return new JobInfoBean();
		}
	}

	private JobInfoBean getJobResult(JobType jobType) {
		switch (jobType) {
		case URL_VALIDATOR:
			return getUrlValidatorJobResult();

		case SNS_UPDATE:
			return getSNSUpdateJobResult();

		case SNS_LOCATION_UPDATE:
			return getSNSLocationUpdateJobResult();

		default:
			return new JobInfoBean();
		}
	}


	private String getJobName(JobType jobType) {
		switch (jobType) {
		case URL_VALIDATOR:
			return URLValidatorJob.createJobName(connectionFacade.getCurrentPlugId());

		case SNS_UPDATE:
			return SNSUpdateJob.createJobName(connectionFacade.getCurrentPlugId());

		case SNS_LOCATION_UPDATE:
			return SNSLocationUpdateJob.createJobName(connectionFacade.getCurrentPlugId());

		default:
			return null;
		}
	}

	private void removeJob(JobType jobType) {
		jobMap.remove(getJobName(jobType));
	}

	public void stopJob(JobType jobType) {
		MdekJob job = getJob(jobType);
		if (job != null) {
			job.stop();
			removeJob(jobType);

		} else {
			log.debug("Could not stop job of type '" + jobType + "'. It probably is not running.");
		}
	}

	private URLJobInfoBean getUrlValidatorJobResult() {
		IMdekCallerCatalog mdekCallerCatalog = connectionFacade.getMdekCallerCatalog();
		IngridDocument response = mdekCallerCatalog.getJobInfo(
				connectionFacade.getCurrentPlugId(),
				de.ingrid.mdek.job.IJob.JobType.URL,
				MdekSecurityUtils.getCurrentUserUuid());

		return MdekCatalogUtils.extractUrlJobInfoFromResponse(response);
	}


	public void startSNSUpdateJob(String lang) {
		MdekJob job = new SNSUpdateJob(
				connectionFacade,
				(SNSService) beanFactory.getBean("snsService"),
				lang);

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

	private SNSUpdateJobInfoBean getSNSUpdateJobResult() {
		IMdekCallerCatalog mdekCallerCatalog = connectionFacade.getMdekCallerCatalog();
		IngridDocument response = mdekCallerCatalog.getJobInfo(
				connectionFacade.getCurrentPlugId(),
				de.ingrid.mdek.job.IJob.JobType.UPDATE_SEARCHTERMS,
				MdekSecurityUtils.getCurrentUserUuid());

		return MdekCatalogUtils.extractSNSUpdateJobInfoFromResponse(response);
	}


	public void startSNSLocationUpdateJob(String lang) {
		MdekJob job = new SNSLocationUpdateJob(
				connectionFacade,
				(SNSService) beanFactory.getBean("snsService"),
				lang);

		try {
			boolean jobStarted = job.start(scheduler);
			if (jobStarted) {
				jobMap.put(job.getName(), job);

			} else {
				log.debug("Could not start SNS Location Update Job.");
			}

		} catch (SchedulerException ex) {
			log.debug("Error starting SNS Location Update Job.", ex);
		}
	}

	private SNSLocationUpdateJobInfoBean getSNSLocationUpdateJobResult() {
		IMdekCallerCatalog mdekCallerCatalog = connectionFacade.getMdekCallerCatalog();
		IngridDocument response = mdekCallerCatalog.getJobInfo(
				connectionFacade.getCurrentPlugId(),
				de.ingrid.mdek.job.IJob.JobType.UPDATE_SPATIAL_REFERENCES,
				MdekSecurityUtils.getCurrentUserUuid());

		return MdekCatalogUtils.extractSNSLocationUpdateJobInfoFromResponse(response, false);
	}

	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
} 
