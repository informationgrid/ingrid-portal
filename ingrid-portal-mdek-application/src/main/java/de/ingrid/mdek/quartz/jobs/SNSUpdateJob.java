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
import de.ingrid.mdek.beans.JobInfoBean;
import de.ingrid.mdek.beans.SNSUpdateJobInfoBean;
import de.ingrid.mdek.caller.IMdekCallerQuery;
import de.ingrid.mdek.dwr.services.sns.SNSService;
import de.ingrid.mdek.dwr.services.sns.SNSTopic;
import de.ingrid.mdek.dwr.services.sns.SNSTopic.Source;
import de.ingrid.mdek.dwr.services.sns.SNSTopic.Type;
import de.ingrid.mdek.handler.ConnectionFacade;
import de.ingrid.mdek.util.MdekUtils;
import de.ingrid.utils.IngridDocument;

public class SNSUpdateJob extends QuartzJobBean implements MdekJob, InterruptableJob {

	private final static Logger log = Logger.getLogger(SNSUpdateJob.class);	

	private static final String JOB_BASE_NAME = "snsUpdateJob_";
	private final String plugId;
	private final String jobName; 
	private final JobDetail jobDetail;
	private Scheduler scheduler;

	// Flag that signals if the job execution should be interrupted
	private boolean cancelJob;


	// No args constructor is required for the job to be scheduled by quartz
	public SNSUpdateJob() {
		this.plugId = null;
		this.jobName = null;
		this.jobDetail = null;
		this.cancelJob = false;
	}

	public SNSUpdateJob(ConnectionFacade connectionFacade, SNSService snsService, String[] changedTopics, String[] newTopics, String[] expiredTopics) {
		this.plugId = connectionFacade.getCurrentPlugId();
		jobName = createJobName(plugId);
		jobDetail = createJobDetail(connectionFacade, snsService, changedTopics, newTopics, expiredTopics);
	}

	public static String createJobName(String plugId) {
		return (JOB_BASE_NAME + plugId);
	}

	private JobDetail createJobDetail(ConnectionFacade connectionFacade, SNSService snsService, String[] changedTopics, String[] newTopics, String[] expiredTopics) {
		JobDetail jobDetail = new JobDetail(jobName, Scheduler.DEFAULT_GROUP, SNSUpdateJob.class);
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
		List<String> freeTerms = fetchFreeTerms(connectionFacade.getMdekCallerQuery(), plugId);

		log.debug("Starting sns update...");
		long startTime = System.currentTimeMillis();

		// Get the list of SNS Topics from the backend
		// Filter the list of sns topics according to the changed/expired lists
		// Update all topics that were found.
		// Changed topics are updated, expired topics are removed and added as free terms
		List<SNSTopic> snsTopics = fetchSNSTopics(connectionFacade.getMdekCallerQuery(), plugId);
		List<SNSTopic> snsTopicsToChange = filter(snsTopics, changedTopics);

		jobExecutionContext.put("NUM_PROCESSED", new Integer(0));
		jobExecutionContext.put("NUM_TOTAL", snsTopicsToChange.size() + freeTerms.size());


		// TODO remove. Inserted for testing purpose
		if (snsTopicsToChange != null && snsTopicsToChange.size() > 10)
			snsTopicsToChange = snsTopicsToChange.subList(0, 10);


		if (changedTopics != null) {
			log.debug("total changed topics: " + changedTopics.length);
		}
		log.debug("changed topic matches: " + snsTopicsToChange.size());
		List<SNSTopic> snsTopicsResult = updateChangedTopics(snsService, snsTopicsToChange, jobExecutionContext);

		List<SNSTopic> snsTopicsToExpire = filter(snsTopics, expiredTopics);
		if (expiredTopics != null) {
			log.debug("total expired topics: " + expiredTopics.length);
		}
		log.debug("expired topic matches: " + snsTopicsToExpire.size());
		removeExpiredTopics(snsTopicsToExpire);


		log.debug("total free terms: " + freeTerms.size());
		// TODO remove. Inserted for testing purpose
		if (freeTerms != null && freeTerms.size() > 10)
			freeTerms = freeTerms.subList(0, 10);


		List<SNSTopic> freeTermsResult = updateFreeTerms(snsService, freeTerms, jobExecutionContext);

		long endTime = System.currentTimeMillis();
		log.debug("SNS Update took "+(endTime - startTime)+" ms.");

		if (!cancelJob) {
			SNSUpdateJobInfoBean jobInfo = createJobResult(snsTopicsToChange, snsTopicsResult, snsTopicsToExpire, freeTerms, freeTermsResult);
			// TODO Send result to backend
			jobExecutionContext.setResult(jobInfo);
		}
	}

	private static SNSUpdateJobInfoBean createJobResult(List<SNSTopic> oldTopics, List<SNSTopic> newTopics,
			List<SNSTopic> expiredTopics, List<String> freeTerms, List<SNSTopic> descriptorsForFreeTerms) {

		SNSUpdateJobInfoBean jobInfo = new SNSUpdateJobInfoBean();
		// Check if the newly found topics differ from the old ones
		removeIdenticalTopics(oldTopics, newTopics);
		log.debug("number of topics to update: " + oldTopics.size());
		jobInfo.setOldSNSTopics(oldTopics);
		jobInfo.setNewSNSTopics(newTopics);
		log.debug("number of topics to expire: " + expiredTopics.size());
		jobInfo.setExpiredTopics(expiredTopics);
		removeUnknownTerms(freeTerms, descriptorsForFreeTerms);
		log.debug("number of free terms to add as topics: " + freeTerms.size());
		jobInfo.setFreeTerms(freeTerms);
		jobInfo.setTopicsForFreeTerms(descriptorsForFreeTerms);

		return jobInfo;
	}

	private static void removeIdenticalTopics(List<SNSTopic> oldTopics, List<SNSTopic> newTopics) {
		Iterator<SNSTopic> oldTopicsIt = oldTopics.iterator();
		Iterator<SNSTopic> newTopicsIt = newTopics.iterator();

		while (oldTopicsIt.hasNext()) {
			SNSTopic oldTopic = oldTopicsIt.next();
			SNSTopic newTopic = newTopicsIt.next();

			if (newTopic == null) {
				// Keep topics where new topics could not be found (will be changed to free terms)
				continue;
			}

			// Comare: type, source, topicId, title, gemetId
			if (oldTopic.getType() == newTopic.getType() &&
					oldTopic.getSource() == newTopic.getSource() &&
					oldTopic.getTopicId().equals(newTopic.getTopicId()) &&
					oldTopic.getTitle().equals(newTopic.getTitle()) &&
					(oldTopic.getGemetId() == null || oldTopic.getGemetId().equals(newTopic.getGemetId()))) {
				oldTopicsIt.remove();
				newTopicsIt.remove();
			}
		}
	}

	private static void removeUnknownTerms(List<String> freeTerms, List<SNSTopic> topics) {
		Iterator<String> freeTermsIt = freeTerms.iterator();
		Iterator<SNSTopic> topicsIt = topics.iterator();

		while (freeTermsIt.hasNext()) {
			String freeTerm = freeTermsIt.next();
			SNSTopic topic = topicsIt.next();
			if (topic == null) {
				freeTermsIt.remove();
				topicsIt.remove();
			}
		}
	}


	private List<SNSTopic> fetchSNSTopics(IMdekCallerQuery mdekCallerQuery, String plugId) {
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

	private List<String> fetchFreeTerms(IMdekCallerQuery mdekCallerQuery, String plugId) {
		// TODO Move method to backend and get ALL free terms!
		// This method fetches free terms for objs only!

		List<String> freeTerms = new ArrayList<String>();

		String qString = "select distinct stv.term " +
			"from ObjectNode oNode " +
			"join oNode.t01ObjectPublished obj " +
			"join obj.searchtermObjs st " +
			"join st.searchtermValue stv " +
			"where stv.searchtermSnsId is null";

		IngridDocument response = mdekCallerQuery.queryHQLToMap(plugId, qString, null, "");
		IngridDocument result = MdekUtils.getResultFromResponse(response);

		if (result != null) {
			List<IngridDocument> objs = (List<IngridDocument>) result.get(MdekKeys.OBJ_ENTITIES);
			if (objs != null) {
				freeTerms = new ArrayList<String>(objs.size());
				for (IngridDocument objEntity : objs) {
					freeTerms.add(objEntity.getString("stv.term"));
				}
			}
		}
		return freeTerms;
	}

	
	private List<SNSTopic> filter(List<SNSTopic> snsTopics, String[] topicIds) {
		if (snsTopics != null && topicIds != null) {
			List<SNSTopic> resultList = new ArrayList<SNSTopic>();
			List<String> topicIdList = Arrays.asList(topicIds);
			for (SNSTopic topic : snsTopics) {
				if (topicIdList.contains(topic.getTopicId())) {
					resultList.add(topic);
				}
			}
			return resultList;

		} else {
			return snsTopics;
		}
	}

	private List<SNSTopic> updateChangedTopics(SNSService snsService, List<SNSTopic> snsTopics, JobExecutionContext jobExecutionContext) {
		List<SNSTopic> newTopics = new ArrayList<SNSTopic>(snsTopics.size());
		for (SNSTopic oldTopic : snsTopics) {
			if (cancelJob) {
				break;
			}

			SNSTopic newTopic = snsService.getPSI(oldTopic.getTopicId());
			log.debug("old topic: " + oldTopic);
			log.debug("new topic: " + newTopic);
			newTopics.add(newTopic);
			jobExecutionContext.put("NUM_PROCESSED", (Integer) jobExecutionContext.get("NUM_PROCESSED") + 1);
		}
		return newTopics;
	}

	private void removeExpiredTopics(List<SNSTopic> snsTopics) {
		for (SNSTopic topic : snsTopics) {
			log.debug("expired topic: " + topic);
		}
	}

	private List<SNSTopic> updateFreeTerms(SNSService snsService, List<String> freeTerms, JobExecutionContext jobExecutionContext) {
		List<SNSTopic> foundTopics = new ArrayList<SNSTopic>();
		for (String freeTerm : freeTerms) {
			if (cancelJob) {
				break;
			}

			log.debug("Find Topic for '" + freeTerm + "'...");
			List<SNSTopic> snsTopics = snsService.findTopics(freeTerm);
			boolean found = false; 
			for (SNSTopic topic : snsTopics) {
				if (topic.getTitle().equals(freeTerm)) {
					if (topic.getType().equals(Type.DESCRIPTOR)) {
						log.debug("Found descriptor for free term: " + topic);
						foundTopics.add(topic);
						found = true;
						break;

					} else if (topic.getType().equals(Type.NON_DESCRIPTOR)) {
						log.debug("Found synonym: " + topic);
						SNSTopic descriptorForSynonym = snsService.getTopicsForTopic(topic.getTopicId());
						log.debug("Descriptor for synonym: " + descriptorForSynonym);
						foundTopics.add(descriptorForSynonym);
						found = true;
						break;
					}
				}
			}

			if (!found) {
				foundTopics.add(null);
			}
			jobExecutionContext.put("NUM_PROCESSED", (Integer) jobExecutionContext.get("NUM_PROCESSED") + 1);
		}
		return foundTopics;
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