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
import de.ingrid.mdek.MdekMapper;
import de.ingrid.mdek.MdekUtils.SearchtermType;
import de.ingrid.mdek.beans.JobInfoBean;
import de.ingrid.mdek.caller.IMdekCallerCatalog;
import de.ingrid.mdek.dwr.services.sns.SNSService;
import de.ingrid.mdek.dwr.services.sns.SNSTopic;
import de.ingrid.mdek.dwr.services.sns.SNSTopic.Source;
import de.ingrid.mdek.dwr.services.sns.SNSTopic.Type;
import de.ingrid.mdek.handler.ConnectionFacade;
import de.ingrid.mdek.util.MdekErrorUtils;
import de.ingrid.mdek.util.MdekSecurityUtils;
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
		jobDataMap.put("USER_ID", MdekSecurityUtils.getCurrentUserUuid());
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
		String userId = mergedJobDataMap.getString("USER_ID");
		List<String> freeTerms = fetchFreeTopics(connectionFacade.getMdekCallerCatalog(), plugId, userId);

		log.debug("Starting sns update...");
		long startTime = System.currentTimeMillis();

		// Get the list of SNS Topics from the backend
		// Filter the list of sns topics according to the changed/expired lists
		// Update all topics that were found.
		// Changed topics are updated, expired topics are removed and added as free terms
		List<SNSTopic> snsTopics = fetchSNSTopics(connectionFacade.getMdekCallerCatalog(), plugId, userId);
		List<SNSTopic> snsTopicsToChange = filter(snsTopics, changedTopics);

		jobExecutionContext.put("NUM_PROCESSED", new Integer(0));
		jobExecutionContext.put("NUM_TOTAL", snsTopicsToChange.size() + freeTerms.size());

/*
		// TODO remove. Inserted for testing purpose
		if (snsTopicsToChange != null && snsTopicsToChange.size() > 10)
			snsTopicsToChange = snsTopicsToChange.subList(0, 10);
*/

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

		log.debug("total free terms: " + freeTerms.size());
/*
		// TODO remove. Inserted for testing purpose
		if (freeTerms != null && freeTerms.size() > 10)
			freeTerms = freeTerms.subList(0, 10);
*/

		List<SNSTopic> freeTermsResult = updateFreeTerms(snsService, freeTerms, jobExecutionContext);

		long endTime = System.currentTimeMillis();
		log.debug("SNS Update took "+(endTime - startTime)+" ms.");

		if (!cancelJob) {
			JobResult jobResult = createJobResult(snsTopicsToChange, snsTopicsResult, snsTopicsToExpire, freeTerms, freeTermsResult);
			jobExecutionContext.setResult(jobResult);

			connectionFacade.getMdekCallerCatalog().updateSearchTerms(
					plugId,
					MdekMapper.mapFromThesTermTable(jobResult.getOldTopics()),
					MdekMapper.mapFromThesTermTable(jobResult.getNewTopics()),
					userId);
		}
	}

	private static JobResult createJobResult(List<SNSTopic> modTopics, List<SNSTopic> modResultTopics,
			List<SNSTopic> expiredTopics, List<String> freeTerms, List<SNSTopic> descriptorsForFreeTerms) {

		List<SNSTopic> oldTopics = new ArrayList<SNSTopic>();
		List<SNSTopic> newTopics = new ArrayList<SNSTopic>();

		// Check if the newly found topics differ from the old ones
		removeIdenticalTopics(modTopics, modResultTopics);
		log.debug("number of topics to update: " + modTopics.size());
		oldTopics.addAll(modTopics);
		newTopics.addAll(modResultTopics);

		// Add the topics to expire
		log.debug("number of topics to expire: " + expiredTopics.size());
		for (SNSTopic topic : expiredTopics) {
			if (!oldTopics.contains(topic)) {
				oldTopics.add(topic);
				newTopics.add(null);
			}
		}

		// Add the free terms
		removeUnknownTerms(freeTerms, descriptorsForFreeTerms);
		log.debug("number of free terms to add as topics: " + freeTerms.size());
		oldTopics.addAll(convertToSNSTopics(freeTerms));
		newTopics.addAll(descriptorsForFreeTerms);

		return new JobResult(oldTopics, newTopics);
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

	private static List<SNSTopic> convertToSNSTopics(List<String> terms) {
		List<SNSTopic> topics = new ArrayList<SNSTopic>();
		for (String term : terms) {
			SNSTopic topic = new SNSTopic();
			topic.setSource(Source.FREE);
			topic.setTitle(term);
			topic.setType(Type.DESCRIPTOR);
			topics.add(topic);
		}
		return topics;
	}

	private List<String> fetchFreeTopics(IMdekCallerCatalog mdekCallerCatalog, String plugId, String userId) {
		List<SNSTopic> snsTopics = fetchTopics(mdekCallerCatalog, plugId, userId, new SearchtermType[] { SearchtermType.FREI });

		List<String> topics = new ArrayList<String>();

		if (snsTopics != null) {
			for (SNSTopic topic : snsTopics) {
				topics.add(topic.getTitle());
			}
		}

		return topics;
	}

	private List<SNSTopic> fetchSNSTopics(IMdekCallerCatalog mdekCallerCatalog, String plugId, String userId) {
		return fetchTopics(mdekCallerCatalog, plugId, userId, new SearchtermType[] { SearchtermType.UMTHES, SearchtermType.GEMET });
	}

	private List<SNSTopic> fetchTopics(IMdekCallerCatalog mdekCallerCatalog, String plugId, String userId, SearchtermType[] termTypes) {
		IngridDocument response = mdekCallerCatalog.getSearchTerms(
				plugId,
				termTypes,
				userId);
		IngridDocument result = MdekUtils.getResultFromResponse(response);

		if (result != null) {
			return mapToSNSTopic((List<IngridDocument>) result.get(MdekKeys.SUBJECT_TERMS));

		} else {
			MdekErrorUtils.handleError(response);
			return null;
		}
	}

	private static List<SNSTopic> mapToSNSTopic(List<IngridDocument> subjectTerms) {
		List<SNSTopic> resultList = new ArrayList<SNSTopic>();
		if (subjectTerms == null)
			return resultList;

		for (IngridDocument term : subjectTerms) {
			SNSTopic t = new SNSTopic();
			t.setType(Type.DESCRIPTOR);
			String type = (String) term.get(MdekKeys.TERM_TYPE);
			if (type.equalsIgnoreCase(SearchtermType.GEMET.getDbValue())) {
				t.setSource(Source.GEMET);
				t.setTitle((String) term.get(MdekKeys.TERM_NAME));
				t.setTopicId((String) term.get(MdekKeys.TERM_SNS_ID));
				t.setGemetId((String) term.get(MdekKeys.TERM_GEMET_ID));

			} else if (type.equalsIgnoreCase(SearchtermType.UMTHES.getDbValue())) {
				t.setSource(Source.UMTHES);
				t.setTitle((String) term.get(MdekKeys.TERM_NAME));
				t.setTopicId((String) term.get(MdekKeys.TERM_SNS_ID));

			} else if (type.equalsIgnoreCase(SearchtermType.FREI.getDbValue())) {
				t.setSource(Source.FREE);
				t.setTitle((String) term.get(MdekKeys.TERM_NAME));
			}
			resultList.add(t);
		}
		return resultList;
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

			log.debug("old topic: " + oldTopic);
			SNSTopic newTopic;
			try {
				newTopic = getSNSTopicForTopicId(snsService, oldTopic.getTopicId());
	
				if (newTopic == null) {
					log.debug("topic id not found. Querying as free term...");
					newTopic = getSNSTopicForFreeTerm(snsService, oldTopic.getTitle());
				}

			} catch (Exception e) {
				// If something went wrong, log the exception and set the 'newTopic' as 'oldTopic'
				// so the topic won't be modified/deleted later on
				log.error("Error querying the snsService for topic " + oldTopic + ".", e);
				newTopic = oldTopic;
			}

			log.debug("new topic: " + newTopic);
			newTopics.add(newTopic);
			jobExecutionContext.put("NUM_PROCESSED", (Integer) jobExecutionContext.get("NUM_PROCESSED") + 1);
		}
		return newTopics;
	}

	// Query the SNS for a given topicId
	// Returns null if no DESCRIPTOR could be found
	// Throws an exception if something went wrong while communicating with the sns
	private static SNSTopic getSNSTopicForTopicId(SNSService snsService, String topicId) throws Exception {
		SNSTopic snsTopic = snsService.getPSI(topicId);
		if (snsTopic != null && snsTopic.getType().equals(Type.DESCRIPTOR)) {
			return snsTopic;

		} else {
			return null;
		}
	}

	// Query the SNS for a given term
	// Returns either a topic with name 'term' or a corresponding descriptor if 'term' is a synonym
	// Returns null if no descriptor or synonym
	private static SNSTopic getSNSTopicForFreeTerm(SNSService snsService, String term) {
		List<SNSTopic> snsTopics = snsService.findTopics(term);
		for (SNSTopic topic : snsTopics) {
			if (topic.getTitle().equalsIgnoreCase(term)) {
				if (topic.getType().equals(Type.DESCRIPTOR)) {
					log.debug("Found descriptor for free term: " + topic);
					return topic;

				} else if (topic.getType().equals(Type.NON_DESCRIPTOR)) {
					log.debug("Found synonym: " + topic);
					SNSTopic descriptorForSynonym = snsService.getTopicsForTopic(topic.getTopicId());
					log.debug("Descriptor for synonym: " + descriptorForSynonym);
					return descriptorForSynonym;
				}
			}
		}
		return null;
	}

	private List<SNSTopic> updateFreeTerms(SNSService snsService, List<String> freeTerms, JobExecutionContext jobExecutionContext) {
		List<SNSTopic> foundTopics = new ArrayList<SNSTopic>();
		for (String freeTerm : freeTerms) {
			if (cancelJob) {
				break;
			}

			log.debug("Find Topic for '" + freeTerm + "'...");
			SNSTopic newTopic = getSNSTopicForFreeTerm(snsService, freeTerm);
			foundTopics.add(newTopic);

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

		if (executionContext != null && executionContext.getResult() == null) {
			Integer numProcessed = (Integer) executionContext.get("NUM_PROCESSED");
			Integer numTotal = (Integer) executionContext.get("NUM_TOTAL");

			JobInfoBean jobInfoResult = new JobInfoBean();
			jobInfoResult.setDescription(jobName);
			jobInfoResult.setStartTime(executionContext.getFireTime());
			jobInfoResult.setNumProcessedEntities(numProcessed);
			jobInfoResult.setNumEntities(numTotal);
			return jobInfoResult;
		}

		return null;
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


	// Container class for the two result lists
	private static class JobResult {
		private List<SNSTopic> oldTopics;
		private List<SNSTopic> newTopics;

		public JobResult(List<SNSTopic> oldTopics, List<SNSTopic> newTopics) {
			this.oldTopics = oldTopics;
			this.newTopics = newTopics;
		}
		public List<SNSTopic> getOldTopics() {
			return oldTopics;
		}
		public List<SNSTopic> getNewTopics() {
			return newTopics;
		}
	}
}