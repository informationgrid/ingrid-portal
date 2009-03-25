package de.ingrid.mdek.quartz.jobs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
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
import de.ingrid.mdek.MdekUtils.SpatialReferenceType;
import de.ingrid.mdek.beans.JobInfoBean;
import de.ingrid.mdek.beans.SNSLocationUpdateJobInfoBean;
import de.ingrid.mdek.caller.IMdekCallerCatalog;
import de.ingrid.mdek.dwr.services.sns.SNSLocationTopic;
import de.ingrid.mdek.dwr.services.sns.SNSService;
import de.ingrid.mdek.handler.ConnectionFacade;
import de.ingrid.mdek.util.MdekEmailUtils;
import de.ingrid.mdek.util.MdekErrorUtils;
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
		List<SNSLocationTopic> snsTopics = fetchSNSLocationTopics(connectionFacade.getMdekCallerCatalog(), plugId);
		List<SNSLocationTopic> topicsToCheck = filter(snsTopics, changedTopics);

		jobExecutionContext.put("NUM_PROCESSED", new Integer(0));
		jobExecutionContext.put("NUM_TOTAL", topicsToCheck.size());


		if (changedTopics != null) {
			log.debug("total changed topics: " + changedTopics.length);
		}
		log.debug("changed topic matches: " + topicsToCheck.size());
		List<SNSLocationTopic> snsTopicsResult = updateChangedTopics(snsService, topicsToCheck, jobExecutionContext);


		List<SNSLocationTopic> snsTopicsToExpire = filter(snsTopics, expiredTopics);
		if (expiredTopics != null) {
			log.debug("total expired topics: " + expiredTopics.length);
		}
		log.debug("expired topic matches: " + snsTopicsToExpire.size());

		long endTime = System.currentTimeMillis();
		log.debug("SNS Location Update took "+(endTime - startTime)+" ms.");

		if (!cancelJob) {
			SNSLocationUpdateJobInfoBean jobInfo = createJobResult(topicsToCheck, snsTopicsResult, snsTopicsToExpire);
			// TODO Send result to backend
			jobExecutionContext.setResult(jobInfo);

			// TODO Send email to responsible users
			sendExpiryMails(jobInfo.getOldSNSTopics(), jobInfo.getNewSNSTopics());
		}
	}

	private static void sendExpiryMails(List<SNSLocationTopic> oldTopics, List<SNSLocationTopic> newTopics) {
		List<SNSLocationTopic> expiredTopics = new ArrayList<SNSLocationTopic>();
		for (int index = 0; index < oldTopics.size(); ++index) {
			if (newTopics.get(index) == null) {
				expiredTopics.add(oldTopics.get(index));
			}
		}

		if (expiredTopics.size() > 0) {
			MdekEmailUtils.sendSpatialReferencesExpiredMails(expiredTopics);
		}
	}

	private List<SNSLocationTopic> fetchSNSLocationTopics(IMdekCallerCatalog mdekCallerCatalog, String plugId) {
		IngridDocument response = mdekCallerCatalog.getSpatialReferences(
				plugId,
				new SpatialReferenceType[] { SpatialReferenceType.GEO_THESAURUS },
				"");
		IngridDocument result = MdekUtils.getResultFromResponse(response);

		if (result != null) {
			return mapToSNSLocationTopics((List<IngridDocument>) result.get(MdekKeys.LOCATIONS));

		} else {
			MdekErrorUtils.handleError(response);
			return null;
		}
	}

	private static List<SNSLocationTopic> mapToSNSLocationTopics(List<IngridDocument> topics) {
		List<SNSLocationTopic> resultList = new ArrayList<SNSLocationTopic>();
		if (resultList != null) {
			for (IngridDocument topic : topics) {
				SNSLocationTopic t = new SNSLocationTopic();
				t.setTopicId((String) topic.get(MdekKeys.LOCATION_SNS_ID));
				t.setName((String) topic.get(MdekKeys.LOCATION_NAME));
				t.setNativeKey((String) topic.get(MdekKeys.LOCATION_CODE));
				t.setTypeId((String) topic.get(MdekKeys.SNS_TOPIC_TYPE));
	
				if (topic.get(MdekKeys.WEST_BOUNDING_COORDINATE) != null &&
						topic.get(MdekKeys.SOUTH_BOUNDING_COORDINATE) != null &&
						topic.get(MdekKeys.EAST_BOUNDING_COORDINATE) != null &&
						topic.get(MdekKeys.NORTH_BOUNDING_COORDINATE) != null) {
					t.setBoundingBox(
							((Double) topic.get(MdekKeys.WEST_BOUNDING_COORDINATE)).floatValue(),
							((Double) topic.get(MdekKeys.SOUTH_BOUNDING_COORDINATE)).floatValue(),
							((Double) topic.get(MdekKeys.EAST_BOUNDING_COORDINATE)).floatValue(),
							((Double) topic.get(MdekKeys.NORTH_BOUNDING_COORDINATE)).floatValue());
				}
				resultList.add(t);
			}
		}

		return resultList;
	}

	private List<SNSLocationTopic> filter(List<SNSLocationTopic> snsTopics, String[] topicIds) {
		if (snsTopics != null && topicIds != null) {
			List<SNSLocationTopic> resultList = new ArrayList<SNSLocationTopic>();
			List<String> topicIdList = Arrays.asList(topicIds);
			for (SNSLocationTopic topic : snsTopics) {
				if (topicIdList.contains(topic.getTopicId())) {
					resultList.add(topic);
				}
			}
			return resultList;

		} else {
			return snsTopics;
		}
	}

	private List<SNSLocationTopic> updateChangedTopics(SNSService snsService, List<SNSLocationTopic> snsTopics, JobExecutionContext jobExecutionContext) {
		List<SNSLocationTopic> newTopics = new ArrayList<SNSLocationTopic>(snsTopics.size());
		for (SNSLocationTopic oldTopic : snsTopics) {
			if (cancelJob) {
				break;
			}

			SNSLocationTopic newTopic;
			try {
				newTopic = snsService.getLocationPSI(oldTopic.getTopicId());

			} catch (Exception e) {
				// If something went wrong, log the exception and set the 'newTopic' as 'oldTopic'
				// so the topic won't be modified/deleted later on
				log.error("Error querying the snsService for topic " + oldTopic + ".", e);
				newTopic = oldTopic;
			}
			log.debug("old topic: " + oldTopic);
			log.debug("new topic: " + newTopic);
			newTopics.add(newTopic);
			jobExecutionContext.put("NUM_PROCESSED", (Integer) jobExecutionContext.get("NUM_PROCESSED") + 1);
		}
		return newTopics;
	}

	private static SNSLocationUpdateJobInfoBean createJobResult(List<SNSLocationTopic> modTopics, List<SNSLocationTopic> modResultTopics,
			List<SNSLocationTopic> expiredTopics) {

		List<SNSLocationTopic> oldTopics = new ArrayList<SNSLocationTopic>();
		List<SNSLocationTopic> newTopics = new ArrayList<SNSLocationTopic>();
		SNSLocationUpdateJobInfoBean jobInfo = new SNSLocationUpdateJobInfoBean();

		// Check if the newly found topics differ from the old ones
		removeIdenticalTopics(modTopics, modResultTopics);
		log.debug("number of topics to update: " + modTopics.size());
		log.debug("number of topics to expire: " + expiredTopics.size());
		oldTopics.addAll(modTopics);
		newTopics.addAll(modResultTopics);

		// Add expired topics
		for (SNSLocationTopic topic : expiredTopics) {
			if (!oldTopics.contains(topic)) {
				oldTopics.add(topic);
				newTopics.add(null);
			}
		}

		jobInfo.setOldSNSTopics(oldTopics);
		jobInfo.setNewSNSTopics(newTopics);

		return jobInfo;
	}

	private static void removeIdenticalTopics(List<SNSLocationTopic> oldTopics, List<SNSLocationTopic> newTopics) {
		Iterator<SNSLocationTopic> oldTopicsIt = oldTopics.iterator();
		Iterator<SNSLocationTopic> newTopicsIt = newTopics.iterator();

		while (oldTopicsIt.hasNext()) {
			SNSLocationTopic oldTopic = oldTopicsIt.next();
			SNSLocationTopic newTopic = newTopicsIt.next();

			if (newTopic == null) {
				// Keep topics where new topics could not be found (will be changed to free terms)
				continue;
			}

			// Comare: topic id, type id, name, native key, bounding box
			if (isEqual(oldTopic, newTopic)) {
				oldTopicsIt.remove();
				newTopicsIt.remove();
			}
		}
	}

	private static boolean isEqual(SNSLocationTopic t1, SNSLocationTopic t2) {
		return (isEqual(t1.getTopicId(), t2.getTopicId()) &&
				isEqual(t1.getTypeId(), t2.getTypeId()) &&
				isEqual(t1.getName(), t2.getName()) &&
				isEqual(t1.getNativeKey(), t2.getNativeKey()) &&
				isEqual(t1.getBoundingBox(), t2.getBoundingBox()));
	}

	private static boolean isEqual(String s1, String s2) {
		return s1 != null ? s1.equals(s2) : s1 == s2;
	}

	private static boolean isEqual(float[] bb1, float[] bb2) {
		if (bb1 != null && bb2 != null) {
			if (bb1.length == 4 && bb2.length == 4) {
				return (bb1[0] == bb2[0] &&
						bb1[1] == bb2[1] &&
						bb1[2] == bb2[2] &&
						bb1[3] == bb2[3]);

			} else {
				return false;
			}

		} else {
			return bb1 == bb2;
		}
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
			log.error("Error interrupting SNS Location Update job.", ex);
		
		} catch (SchedulerException ex) {
			log.error("Error interrupting SNS Location Update job.", ex);
		}
 
	}

	public String getName() {
		return jobName;
	}

	public void interrupt() throws UnableToInterruptJobException {
		cancelJob = true;
	}
}