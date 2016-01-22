/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2016 wemove digital solutions GmbH
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
import de.ingrid.mdek.MdekUtils.SpatialReferenceType;
import de.ingrid.mdek.beans.JobInfoBean;
import de.ingrid.mdek.beans.SNSLocationUpdateJobInfoBean;
import de.ingrid.mdek.beans.SNSLocationUpdateResult;
import de.ingrid.mdek.caller.IMdekCallerCatalog;
import de.ingrid.mdek.dwr.services.sns.SNSLocationTopic;
import de.ingrid.mdek.dwr.services.sns.SNSService;
import de.ingrid.mdek.handler.ConnectionFacade;
import de.ingrid.mdek.util.MdekCatalogUtils;
import de.ingrid.mdek.util.MdekEmailUtils;
import de.ingrid.mdek.util.MdekErrorUtils;
import de.ingrid.mdek.util.MdekSecurityUtils;
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

	private Locale locale;

    //private String urlGazetteer;

	// No args constructor is required for the job to be scheduled by quartz
	public SNSLocationUpdateJob() {
		this.plugId = null;
		this.jobName = null;
		this.jobDetail = null;
		this.cancelJob = false;
	}

	public SNSLocationUpdateJob(ConnectionFacade connectionFacade, SNSService snsService, String lang) {
		this.plugId = connectionFacade.getCurrentPlugId();
		jobName = createJobName(plugId);
		jobDetail = createJobDetail(connectionFacade, snsService, lang);
	}

	public static String createJobName(String plugId) {
		return (JOB_BASE_NAME + plugId);
	}

	private JobDetail createJobDetail(ConnectionFacade connectionFacade, SNSService snsService, String lang) {
		JobDetail jobDetail = new JobDetail(jobName, Scheduler.DEFAULT_GROUP, SNSLocationUpdateJob.class);
		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put("SNS_SERVICE", snsService);
		jobDataMap.put("CONNECTION_FACADE", connectionFacade);
		jobDataMap.put("PLUG_ID", connectionFacade.getCurrentPlugId());
		jobDataMap.put("USER_ID", MdekSecurityUtils.getCurrentUserUuid());
		jobDataMap.put("LOCALE", new Locale( lang ));
		//jobDataMap.put("URL_GAZETTEER", ResourceBundle.getBundle("sns").getString("sns.serviceURL.gazetteer"));
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
		SNSService snsService = (SNSService) mergedJobDataMap.get("SNS_SERVICE");
		ConnectionFacade connectionFacade = (ConnectionFacade) mergedJobDataMap.get("CONNECTION_FACADE");
		String plugId = mergedJobDataMap.getString("PLUG_ID");
		String userId = mergedJobDataMap.getString("USER_ID");
		locale = (Locale) mergedJobDataMap.get("LOCALE");
		//urlGazetteer = mergedJobDataMap.getString("URL_GAZETTEER");

		log.debug("Starting sns location update...");
		long startTime = System.currentTimeMillis();
		List<SNSLocationTopic> snsTopics = fetchSNSLocationTopics(connectionFacade.getMdekCallerCatalog(), plugId, userId);

		jobExecutionContext.put("NUM_PROCESSED", new Integer(0));
		jobExecutionContext.put("NUM_TOTAL", snsTopics.size());

		log.debug("changed topic matches: " + snsTopics.size());
		List<SNSLocationTopic> snsTopicsResult = updateChangedTopics(snsService, snsTopics, jobExecutionContext);


		List<SNSLocationTopic> snsTopicsToExpire = filterExpired(snsTopics);
		log.debug("expired topic matches: " + snsTopicsToExpire.size());

		long endTime = System.currentTimeMillis();
		log.debug("SNS Location Update took "+(endTime - startTime)+" ms.");

		if (!cancelJob) {
			JobResult jobResult = createJobResult(snsTopics, snsTopicsResult, snsTopicsToExpire);
			jobExecutionContext.setResult(jobResult);

			// TODO Send result to backend
			IngridDocument response = connectionFacade.getMdekCallerCatalog().updateSpatialReferences(
					plugId,
					mapFromSNSLocationTopics(jobResult.getOldTopics()),
					mapFromSNSLocationTopics(jobResult.getNewTopics()),
					userId);

			SNSLocationUpdateJobInfoBean jobInfo = MdekCatalogUtils.extractSNSLocationUpdateJobInfoFromResponse(response, true);
			sendExpiryMails(jobInfo.getSnsUpdateResults(), plugId, userId);
		}
	}

    private static List<IngridDocument> mapFromSNSLocationTopics(List<SNSLocationTopic> topics) {
		List<IngridDocument> resultDocs = new ArrayList<IngridDocument>();

		if (topics != null) {
			for (SNSLocationTopic topic : topics) {
				IngridDocument topicDoc = null;
				if (topic != null) {
					topicDoc = new IngridDocument();
					topicDoc.put(MdekKeys.LOCATION_TYPE, "G");
					topicDoc.put(MdekKeys.LOCATION_NAME, topic.getName());
					topicDoc.put(MdekKeys.LOCATION_CODE, topic.getNativeKey());
					topicDoc.put(MdekKeys.LOCATION_SNS_ID, topic.getTopicId());
					topicDoc.put(MdekKeys.SNS_TOPIC_TYPE, topic.getTypeId());
					
					// only write expired date if location is expired!
					// the backend does not check if the date has already been passed
					if (topic.isExpired()) {
					    topicDoc.put(MdekKeys.LOCATION_EXPIRED_AT, topic.getExpiredDate());
					}

					topicDoc.put(MdekKeys.SUCCESSORS, mapFromSNSLocationTopics( topic.getSuccessors() ));

					float[] boundingBox = topic.getBoundingBox();
					if (boundingBox != null && boundingBox.length == 4) {
						topicDoc.put(MdekKeys.WEST_BOUNDING_COORDINATE, (double) boundingBox[0]);
						topicDoc.put(MdekKeys.SOUTH_BOUNDING_COORDINATE, (double) boundingBox[1]);
						topicDoc.put(MdekKeys.EAST_BOUNDING_COORDINATE, (double) boundingBox[2]);
						topicDoc.put(MdekKeys.NORTH_BOUNDING_COORDINATE, (double) boundingBox[3]);
					}
				}
				resultDocs.add(topicDoc);
			}
		}
		return resultDocs;
	}


	private static void sendExpiryMails(List<SNSLocationUpdateResult> results, String plugId, String userId) {
		if (results != null) {
			MdekEmailUtils.sendSpatialReferencesExpiredMails(results, plugId, userId);
		}
	}

	@SuppressWarnings("unchecked")
    private List<SNSLocationTopic> fetchSNSLocationTopics(IMdekCallerCatalog mdekCallerCatalog, String plugId, String userId) {
		IngridDocument response = mdekCallerCatalog.getSpatialReferences(
				plugId,
				new SpatialReferenceType[] { SpatialReferenceType.GEO_THESAURUS },
				userId);
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

	private List<SNSLocationTopic> filterExpired(List<SNSLocationTopic> snsTopics) {
		if (snsTopics != null) {
			List<SNSLocationTopic> resultList = new ArrayList<SNSLocationTopic>();
			for (SNSLocationTopic topic : snsTopics) {
				if (topic.isExpired()) {
					resultList.add(topic);
				}
			}
			return resultList;

		}
		return null;
	}

	private List<SNSLocationTopic> updateChangedTopics(SNSService snsService, List<SNSLocationTopic> snsTopics, JobExecutionContext jobExecutionContext) {
		List<SNSLocationTopic> newTopics = new ArrayList<SNSLocationTopic>(snsTopics.size());
		for (SNSLocationTopic oldTopic : snsTopics) {
			if (cancelJob) {
				break;
			}

			SNSLocationTopic newTopic;
			try {
			    String topicId = oldTopic.getTopicId();
//			    if (!topicId.startsWith( "http" )) {
//			        topicId = urlGazetteer + topicId;
//			    }
				newTopic = findTopicById(snsService, topicId);

				// If the topic was not found, query by name and find a 'best match'
				// 1. a topic with the same name, native key and type id
				// 2. a topic with the same name
				if (newTopic == null) {
					log.debug("no topic found for id '" + topicId + "'");
					List<SNSLocationTopic> topics = findTopicsByName(snsService, oldTopic.getName());

					// Find a 'best match' from the returned topics
					for (SNSLocationTopic topic : topics) {
						newTopic = topic;
						if (topic.getNativeKey() != null && topic.getNativeKey().equals(oldTopic.getNativeKey())) {// && topic.getTypeId() != null && topic.getTypeId().equals(oldTopic.getTypeId())) {
							log.debug("found topic with same name and native key");
							break;

						} else {
							log.debug("found topic with differing name, type id or native key");
						}
					}
				}

			} catch (Exception e) {
				// If something went wrong, log the exception and set the 'newTopic' as 'oldTopic'
				// so the topic won't be modified/deleted later on
				log.error("Error querying the snsService for topic " + oldTopic + ".", e);
				newTopic = oldTopic;
			}
			
			// flatten all successors, in case several references of expired topics occurred
			if (newTopic != null) {
    			List<SNSLocationTopic> validSuccessors = getNonExpiredSuccessors( newTopic );
    			newTopic.setSuccessors( validSuccessors );
			}
			
			log.debug("old topic: " + oldTopic);
			log.debug("new topic: " + newTopic);
			newTopics.add(newTopic);
			jobExecutionContext.put("NUM_PROCESSED", (Integer) jobExecutionContext.get("NUM_PROCESSED") + 1);
		}
		return newTopics;
	}

	private List<SNSLocationTopic> getNonExpiredSuccessors( SNSLocationTopic newTopic ) {
	    List<SNSLocationTopic> successors = newTopic.getSuccessors();
	    if (successors == null) return null;
	    
	    List<SNSLocationTopic> topics = new ArrayList<SNSLocationTopic>();
	    for (SNSLocationTopic successor : successors) {
            // only add successor that is not expired AND that is not a reference to itself!!!
	        if (!successor.isExpired() && !successor.getTopicId().equals( newTopic.getTopicId() )) {
                topics.add( successor );
            }
	        
	        // include successors of a successor
	        List<SNSLocationTopic> moreSuccessors = getNonExpiredSuccessors( successor );
	        if (moreSuccessors != null) {
	            topics.addAll( moreSuccessors );
	        }
        }
        
	    // do not add empty lists!
	    if (topics.isEmpty()) return null;
	    
        return topics;
    }

    // Find a topic by the given topicId
	// Return null iff no topic for the given id could be found
	// Throws an Exception if there was an error querying the SNS Service (connection timeout, etc.)
	private SNSLocationTopic findTopicById(SNSService snsService, String topicId) throws Exception {
		return snsService.getLocationPSI(topicId, locale, null);
	}

	// Find a topic for the given topicName
	// Return an empty list if no topics with the given name could be found
	private List<SNSLocationTopic> findTopicsByName(SNSService snsService, String topicName) {
		List<SNSLocationTopic> topics = snsService.getLocationTopics(topicName, null, null, locale);

		for (Iterator<SNSLocationTopic> iterator = topics.iterator(); iterator.hasNext();) {
			SNSLocationTopic topic = (SNSLocationTopic) iterator.next();
			if (topic.getName() == null || !topic.getName().equalsIgnoreCase((topicName))) {
				iterator.remove();
			}
		}

		return topics;
	}


	private static JobResult createJobResult(List<SNSLocationTopic> modTopics, List<SNSLocationTopic> modResultTopics,
			List<SNSLocationTopic> expiredTopics) {

		List<SNSLocationTopic> oldTopics = new ArrayList<SNSLocationTopic>();
		List<SNSLocationTopic> newTopics = new ArrayList<SNSLocationTopic>();

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

		return new JobResult(oldTopics, newTopics);
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

	private static class JobResult {
		private List<SNSLocationTopic> oldTopics;
		private List<SNSLocationTopic> newTopics;

		public JobResult(List<SNSLocationTopic> oldTopics, List<SNSLocationTopic> newTopics) {
			this.oldTopics = oldTopics;
			this.newTopics = newTopics;
		}
		public List<SNSLocationTopic> getOldTopics() {
			return oldTopics;
		}
		public List<SNSLocationTopic> getNewTopics() {
			return newTopics;
		}
	}
} 
