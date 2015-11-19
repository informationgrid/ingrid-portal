/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2015 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.mdek.quartz.jobs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

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

    private Locale locale;

    //private String urlThesaurus;


	// No args constructor is required for the job to be scheduled by quartz
	public SNSUpdateJob() {
		this.plugId = null;
		this.jobName = null;
		this.jobDetail = null;
		this.cancelJob = false;
		//this.urlThesaurus = ResourceBundle.getBundle("sns").getString("sns.serviceURL.thesaurus");
	}

	public SNSUpdateJob(ConnectionFacade connectionFacade, SNSService snsService, String lang) {
		this.plugId = connectionFacade.getCurrentPlugId();
		jobName = createJobName(plugId);
		jobDetail = createJobDetail(connectionFacade, snsService, lang);
	}

	public static String createJobName(String plugId) {
		return (JOB_BASE_NAME + plugId);
	}

	private JobDetail createJobDetail(ConnectionFacade connectionFacade, SNSService snsService, String lang) {
		JobDetail jobDetail = new JobDetail(jobName, Scheduler.DEFAULT_GROUP, SNSUpdateJob.class);
		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put("SNS_SERVICE", snsService);
		jobDataMap.put("CONNECTION_FACADE", connectionFacade);
		jobDataMap.put("PLUG_ID", connectionFacade.getCurrentPlugId());
		jobDataMap.put("USER_ID", MdekSecurityUtils.getCurrentUserUuid());
		jobDataMap.put("LOCALE", new Locale( lang ));
		//jobDataMap.put("URL_THESAURUS", ResourceBundle.getBundle("sns").getString("sns.serviceURL.thesaurus"));
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

		try {
			JobDataMap mergedJobDataMap = jobExecutionContext.getMergedJobDataMap();
			SNSService snsService = (SNSService) mergedJobDataMap.get("SNS_SERVICE");
			ConnectionFacade connectionFacade = (ConnectionFacade) mergedJobDataMap.get("CONNECTION_FACADE");
			String plugId = mergedJobDataMap.getString("PLUG_ID");
			String userId = mergedJobDataMap.getString("USER_ID");
			locale = (Locale) mergedJobDataMap.get("LOCALE");
			//urlThesaurus = mergedJobDataMap.getString("URL_THESAURUS");
			List<String> freeTerms = fetchFreeTopics(connectionFacade.getMdekCallerCatalog(), plugId, userId);
	
			log.debug("Starting sns update...");
			long startTime = System.currentTimeMillis();
	
			// Get the list of SNS Topics from the backend
			// Filter the list of sns topics according to the changed/expired lists
			// Update all topics that were found.
			// Changed topics are updated, expired topics are removed and added as free terms
			List<SNSTopic> snsTopics = fetchSNSTopics(connectionFacade.getMdekCallerCatalog(), plugId, userId);
	
			jobExecutionContext.put("NUM_PROCESSED", new Integer(0));
			jobExecutionContext.put("NUM_TOTAL", snsTopics.size() + freeTerms.size());

			log.debug("changed topic matches: " + snsTopics.size());
			List<SNSTopic> snsTopicsResult = updateChangedTopics(snsService, snsTopics, jobExecutionContext);
	
			List<SNSTopic> snsTopicsToExpire = filterExpired(snsTopicsResult);
			
			log.debug("expired topic matches: " + snsTopicsToExpire.size());
			log.debug("total free terms: " + freeTerms.size());

			List<SNSTopic> freeTermsResult = updateFreeTerms(snsService, freeTerms, jobExecutionContext);
	
			long endTime = System.currentTimeMillis();
			log.debug("SNS Update took "+(endTime - startTime)+" ms.");
	
			if (!cancelJob) {
				JobResult jobResult = createJobResult(snsTopics, snsTopicsResult, snsTopicsToExpire, freeTerms, freeTermsResult);
				jobExecutionContext.setResult(jobResult);
	
				connectionFacade.getMdekCallerCatalog().updateSearchTerms(
						plugId,
						MdekMapper.mapFromThesTermTable(jobResult.getOldTopics()),
						MdekMapper.mapFromThesTermTable(jobResult.getNewTopics()),
						userId);
			}
		} catch (Exception e) {
			log.error("Error during SNS Update.", e);
			throw new JobExecutionException(e);
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
		for (SNSTopic expiredTopic : expiredTopics) {
			if (!oldTopics.contains(expiredTopic)) {
				oldTopics.add(expiredTopic);
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

			if (isEqual(oldTopic, newTopic)) {
				oldTopicsIt.remove();
				newTopicsIt.remove();
			}
		}
	}

	// Comare: type, source, topicId, title, alternate title, gemetId
	private static boolean isEqual(SNSTopic topicA, SNSTopic topicB) {
		return (topicA.getType() == topicB.getType() &&
				topicA.getSource() == topicB.getSource() &&
				isEqual(topicA.getTopicId(), topicB.getTopicId()) &&
				isEqual(topicA.getTitle(), topicB.getTitle()) &&
				isEqual(topicA.getAlternateTitle(), topicB.getAlternateTitle()) &&
				isEqual(topicA.getGemetId(), topicB.getGemetId()));
	}

	private static boolean isEqual(String s1, String s2) {
		return s1 != null ? s1.equals(s2) : s1 == s2;
	}

	private static void removeUnknownTerms(List<String> freeTerms, List<SNSTopic> topics) {
		Iterator<String> freeTermsIt = freeTerms.iterator();
		Iterator<SNSTopic> topicsIt = topics.iterator();

		while (freeTermsIt.hasNext()) {
			@SuppressWarnings("unused")
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

	@SuppressWarnings("unchecked")
    private List<SNSTopic> fetchTopics(IMdekCallerCatalog mdekCallerCatalog, String plugId, String userId, SearchtermType[] termTypes) {
		IngridDocument response = mdekCallerCatalog.getSearchTerms(
				plugId,
				termTypes,
				userId);
		IngridDocument result = MdekUtils.getResultFromResponse(response);

		if (result != null) {
			return MdekMapper.mapToThesTermsTable((List<IngridDocument>) result.get(MdekKeys.SUBJECT_TERMS));

		} else {
			MdekErrorUtils.handleError(response);
			return null;
		}
	}

	private List<SNSTopic> filterExpired(List<SNSTopic> snsTopics) {
		if (snsTopics != null) {
			List<SNSTopic> resultList = new ArrayList<SNSTopic>();
			for (SNSTopic topic : snsTopics) {
				if (topic != null && topic.isExpired()) {
					resultList.add(topic);
				}
			}
			return resultList;

		} 
		return null;
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
			    String topicId = oldTopic.getTopicId();
//			    if (!topicId.startsWith( "http" )) {
//			        topicId = urlThesaurus + "_000" + topicId.substring( topicId.lastIndexOf( "_" ) + 1 );
//			    }
				newTopic = getSNSTopicForTopicId(snsService, topicId);
	
				if (newTopic == null) {
					log.debug("topic id not found. Querying as free term...");
					String title = oldTopic.getTitle();
					if (title == null) {
					    newTopics.add( null );
					    continue;
					}
					newTopic = getSNSTopicForFreeTerm(snsService, title);
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
	private SNSTopic getSNSTopicForTopicId(SNSService snsService, String topicId) throws Exception {
		SNSTopic snsTopic = snsService.getPSI(topicId, locale);
		if (snsTopic != null && snsTopic.getType().equals(Type.DESCRIPTOR)) {
			return snsTopic;

		} else {
			return null;
		}
	}

	// Query the SNS for a given term
	// Returns either a topic with name 'term' or a corresponding descriptor if 'term' is a synonym
	// Returns null if no descriptor or synonym
	private SNSTopic getSNSTopicForFreeTerm(SNSService snsService, String term) {
		List<SNSTopic> snsTopics = snsService.findTopics(term, locale);
		for (SNSTopic topic : snsTopics) {
			if (topic.getTitle().equalsIgnoreCase(term)) {
				if (topic.getType().equals(Type.DESCRIPTOR)) {
					log.debug("Found descriptor for free term: " + topic);
					return topic;

				} else if (topic.getType().equals(Type.NON_DESCRIPTOR)) {
					log.debug("Found synonym: " + topic);
					SNSTopic descriptorForSynonym = snsService.getTopicsForTopic(topic.getTopicId(), locale);
					log.debug("Descriptor for synonym: " + descriptorForSynonym);
					descriptorForSynonym.setTitle( topic.getTitle() );
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
			if (freeTerm == null) {
			    foundTopics.add(null);
			    continue;
			}
			SNSTopic newTopic = getSNSTopicForFreeTerm(snsService, freeTerm);
			foundTopics.add(newTopic);

			jobExecutionContext.put("NUM_PROCESSED", (Integer) jobExecutionContext.get("NUM_PROCESSED") + 1);
		}
		return foundTopics;
	}

	// Retrieves the current JobExecutionContext
	// If the job is not running, null is returned
	@SuppressWarnings("unchecked")
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
