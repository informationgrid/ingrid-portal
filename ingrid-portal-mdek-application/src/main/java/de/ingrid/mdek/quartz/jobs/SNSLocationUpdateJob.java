package de.ingrid.mdek.quartz.jobs;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.InterruptableJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.UnableToInterruptJobException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import de.ingrid.mdek.MdekKeys;
import de.ingrid.mdek.beans.JobInfoBean;
import de.ingrid.mdek.caller.IMdekCallerQuery;
import de.ingrid.mdek.dwr.services.sns.SNSService;
import de.ingrid.mdek.dwr.services.sns.SNSTopic;
import de.ingrid.mdek.dwr.services.sns.SNSTopic.Source;
import de.ingrid.mdek.dwr.services.sns.SNSTopic.Type;
import de.ingrid.mdek.handler.ConnectionFacade;
import de.ingrid.mdek.util.MdekUtils;
import de.ingrid.utils.IngridDocument;

public class SNSLocationUpdateJob extends QuartzJobBean implements MdekJob, InterruptableJob {

	private final static Logger log = Logger.getLogger(SNSLocationUpdateJob.class);	

	private static final String JOB_BASE_NAME = "snsLocationUpdateJob_";
	private final String plugId;
	private final String jobName; 
	private final JobDetail jobDetail;
	private Scheduler scheduler;

	// Flag that signals if the job execution should be interrupted
	private boolean cancelJob;


	// No args constructor is required for the job to be scheduled by quartz
	public SNSLocationUpdateJob() {
		this.plugId = null;
		this.jobName = null;
		this.jobDetail = null;
		this.cancelJob = false;
	}

	public SNSLocationUpdateJob(ConnectionFacade connectionFacade, SNSService snsService, String[] changedTopics, String[] newTopics, String[] expiredTopics) {
		this.plugId = connectionFacade.getCurrentPlugId();
		jobName = createJobName(plugId);
		jobDetail = createJobDetail(connectionFacade, snsService, changedTopics, newTopics, expiredTopics);
	}

	public static String createJobName(String plugId) {
		return (JOB_BASE_NAME + plugId);
	}

	private JobDetail createJobDetail(ConnectionFacade connectionFacade, SNSService snsService, String[] changedTopics, String[] newTopics, String[] expiredTopics) {
		JobDetail jobDetail = new JobDetail(jobName, Scheduler.DEFAULT_GROUP, SNSLocationUpdateJob.class);
		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put("CHANGED_TOPICS", changedTopics);
		jobDataMap.put("NEW_TOPICS", newTopics);
		jobDataMap.put("EXPIRED_TOPICS", expiredTopics);
		jobDataMap.put("SNS_SERVICE", snsService);
		jobDataMap.put("CONNECTION_FACADE", connectionFacade);
		jobDataMap.put("PLUG_ID", connectionFacade.getCurrentPlugId());
		jobDetail.setJobDataMap(jobDataMap);

		return jobDetail;
	}

	public boolean start(Scheduler scheduler) throws SchedulerException {
		this.scheduler = scheduler;

		if (!isRunning()) {
			scheduler.addJob(jobDetail, true);
			scheduler.triggerJobWithVolatileTrigger(jobName, Scheduler.DEFAULT_GROUP);
			return true;

		} else {
			return false;
		}
	}

	@Override
	protected void executeInternal(JobExecutionContext jobExecutionContext)
			throws JobExecutionException {
		
		JobDataMap mergedJobDataMap = jobExecutionContext.getMergedJobDataMap();
		String[] changedTopics = (String[]) mergedJobDataMap.get("CHANGED_TOPICS");
		String[] newTopics = (String[]) mergedJobDataMap.get("NEW_TOPICS");
		String[] expiredTopics = (String[]) mergedJobDataMap.get("EXPIRED_TOPICS");
		SNSService snsService = (SNSService) mergedJobDataMap.get("SNS_SERVICE");
		ConnectionFacade connectionFacade = (ConnectionFacade) mergedJobDataMap.get("CONNECTION_FACADE");
		String plugId = mergedJobDataMap.getString("PLUG_ID");

		log.debug("Starting sns location update...");
		long startTime = System.currentTimeMillis();

		long endTime = System.currentTimeMillis();
		log.debug("SNS Location Update took "+(endTime - startTime)+" ms.");

		if (!cancelJob) {
//			SNSLocationUpdateJobInfoBean jobInfo = createJobResult(snsTopicsToChange, snsTopicsResult, snsTopicsToExpire, freeTerms, freeTermsResult);
			// TODO Send result to backend
//			jobExecutionContext.setResult(jobInfo);
		}
	}

	private List<SNSTopic> fetchSNSLocationTopics(IMdekCallerQuery mdekCallerQuery, String plugId) {
		// TODO Move method to backend and get ALL sns topics!
		// This method fetches sns topics for objs only!

		List<SNSTopic> snsTopics = new ArrayList<SNSTopic>();

		String qString = "select distinct stv.term, stsns.snsId, stsns.gemetId " +
			"from ObjectNode oNode " +
			"join oNode.t01ObjectPublished obj " +
			"join obj.searchtermObjs st " +
			"join st.searchtermValue stv " +
			"join stv.searchtermSns stsns";

		IngridDocument response = mdekCallerQuery.queryHQLToMap(plugId, qString, null, "");
		IngridDocument result = MdekUtils.getResultFromResponse(response);

		if (result != null) {
			List<IngridDocument> objs = (List<IngridDocument>) result.get(MdekKeys.OBJ_ENTITIES);
			if (objs != null) {
				for (IngridDocument objEntity : objs) {
					
					Source source = (objEntity.get("stsns.gemetId") != null) ? Source.GEMET : Source.UMTHES;
					String id = objEntity.getString("stsns.snsId");
					String title = objEntity.getString("stv.term");
					String gemetId = objEntity.getString("stsns.gemetId");

					SNSTopic snsTopic = new SNSTopic(Type.DESCRIPTOR, source, id, title, gemetId);
					snsTopics.add(snsTopic);
				}
			}
		}
		return snsTopics;
	}

	// Retrieves the current JobExecutionContext
	// If the job is not running, null is returned
	private JobExecutionContext getJobExecutionContext() {
		try {
			if (scheduler != null) {
				List<JobExecutionContext> executionContextList = scheduler.getCurrentlyExecutingJobs();
				for (JobExecutionContext executionContext : executionContextList) {
					if (jobName.equals(executionContext.getJobDetail().getName())) {
						return executionContext;
					}
				}
			}
		} catch (SchedulerException ex) {			
			log.debug("Error while fetching job execution context.", ex);
		}
		return null;
	}

	public JobInfoBean getRunningJobInfo() {
		JobExecutionContext executionContext = getJobExecutionContext();

		if (executionContext != null) {
			JobInfoBean jobInfoResult = new JobInfoBean();
			jobInfoResult.setDescription(jobName);
			jobInfoResult.setStartTime(executionContext.getFireTime());
			jobInfoResult.setNumProcessedEntities((Integer) executionContext.get("NUM_PROCESSED"));
			jobInfoResult.setNumEntities((Integer) executionContext.get("NUM_TOTAL"));
			return jobInfoResult;

		} else {
			return null;
		}
	}


	public boolean isRunning() {
		if (scheduler != null) {
			return getJobExecutionContext() != null;

		} else {
			return false;
		}
	}

	public void stop() {
		try {
			log.debug("trying to interrupt job via scheduler...");
			scheduler.interrupt(jobName, Scheduler.DEFAULT_GROUP);
			scheduler.deleteJob(jobName, Scheduler.DEFAULT_GROUP);

		} catch (UnableToInterruptJobException ex) {
			log.error("Error interrupting SNS Update job.", ex);
		
		} catch (SchedulerException ex) {
			log.error("Error interrupting SNS Update job.", ex);
		}
 
	}

	public String getName() {
		return jobName;
	}

	public void interrupt() throws UnableToInterruptJobException {
		cancelJob = true;
	}
}