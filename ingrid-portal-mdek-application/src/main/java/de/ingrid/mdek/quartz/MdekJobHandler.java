package de.ingrid.mdek.quartz;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import de.ingrid.mdek.MdekKeys;
import de.ingrid.mdek.beans.JobInfoBean;
import de.ingrid.mdek.caller.IMdekCallerQuery;
import de.ingrid.mdek.handler.ConnectionFacade;
import de.ingrid.mdek.quartz.jobs.URLValidatorJob;
import de.ingrid.mdek.quartz.jobs.URLValidatorJobListener;
import de.ingrid.mdek.quartz.jobs.util.URLObjectReference;
import de.ingrid.mdek.quartz.jobs.util.URLState;
import de.ingrid.mdek.quartz.jobs.util.URLState.State;
import de.ingrid.mdek.util.MdekUtils;
import de.ingrid.utils.IngridDocument;

public class MdekJobHandler {

	private final static Logger log = Logger.getLogger(MdekJobHandler.class);

	private static final String URL_VALIDATOR_JOB_BASE_NAME = "urlValidatorJob_";
	private static final String URL_VALIDATOR_JOB_LISTENER_SUFFIX = "_listener";

	private Scheduler scheduler;
	private ConnectionFacade connectionFacade;

	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	public void setConnectionFacade(ConnectionFacade connectionFacade) {
		this.connectionFacade = connectionFacade;
	}

	public boolean startUrlValidatorJob() {
		JobDetail jobDetail = buildUrlValidatorJobDetail();
		try {
			scheduleUrlValidatorJob(jobDetail);

		} catch (SchedulerException ex) {
			log.debug("Could not start url validation job "+jobDetail.getName(), ex);
			return false;
		}
		return true;
	}

	private JobDetail buildUrlValidatorJobDetail() {
		final String jobName = getValidatorJobName();
		final String jobListenerName = getValidatorJobListenerName();

		Map<String, URLState> urlMap = createUrlMap();

		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put(URLValidatorJob.URL_MAP, urlMap);
		dataMap.put(URLValidatorJob.START_TIME, new Date());

		JobDetail jobDetail = new JobDetail(jobName, Scheduler.DEFAULT_GROUP, URLValidatorJob.class);
		JobDataMap jobDataMap = new JobDataMap(dataMap);
		jobDetail.setJobDataMap(jobDataMap);
		jobDetail.addJobListener(jobListenerName);

		return jobDetail;
	}

	private void scheduleUrlValidatorJob(JobDetail jobDetail) throws SchedulerException {
		scheduler.addJob(jobDetail, true);
		scheduler.addJobListener(new URLValidatorJobListener(jobDetail.getJobListenerNames()[0]));
		scheduler.triggerJobWithVolatileTrigger(jobDetail.getName(), Scheduler.DEFAULT_GROUP);
	}

	private String getValidatorJobName() {
		String plugId = connectionFacade.getCurrentPlugId();
		return (URL_VALIDATOR_JOB_BASE_NAME + plugId);
	}

	private String getValidatorJobListenerName() {
		return getValidatorJobName() + URL_VALIDATOR_JOB_LISTENER_SUFFIX;
	}

	// Retrieves the current JobExecutionContext for the running url validator job
	// If no validator job is currently running, null is returned
	public JobExecutionContext getCurrentValidatorJobExecutionContext() throws SchedulerException {
		String jobName = getValidatorJobName();
		List<JobExecutionContext> executionContextList = scheduler.getCurrentlyExecutingJobs();
		for (JobExecutionContext executionContext : executionContextList) {
			if (jobName.equals(executionContext.getJobDetail().getName())) {
				return executionContext;
			}
		}
		return null;
	}

	public boolean isUrlValidatorRunning() throws SchedulerException {
		return getCurrentValidatorJobExecutionContext() != null;
	}


	public JobInfoBean getUrlValidatorInfo() throws SchedulerException {
		JobInfoBean jobInfoResult = new JobInfoBean();
		JobExecutionContext executionContext = getCurrentValidatorJobExecutionContext();

		if (executionContext != null) {
			Map<String, URLState> urlMap = (Map<String, URLState>) executionContext.getMergedJobDataMap().get(URLValidatorJob.URL_MAP);
			int totalNumberOfUrls = urlMap.size();
			int numberOfProcessedUrls = 0;
			for (URLState urlState : urlMap.values()) {
				if (State.NOT_CHECKED != urlState.getState()) {
					++numberOfProcessedUrls;
				}
			}

			jobInfoResult.setDescription("URL_VALIDATOR");
			jobInfoResult.setNumEntities(totalNumberOfUrls);
			jobInfoResult.setNumProcessedEntities(numberOfProcessedUrls);
			jobInfoResult.setStartTime((Date) executionContext.getMergedJobDataMap().get(URLValidatorJob.START_TIME));
			jobInfoResult.setEndTime((Date) executionContext.getMergedJobDataMap().get(URLValidatorJob.END_TIME));
		} else {
			// No running job found for the current iplug...
		}

		return jobInfoResult;
	}

	private Map<String, URLState> createUrlMap() {
		String plugId = connectionFacade.getCurrentPlugId();
		Map<String, URLState> urlMap = new HashMap<String, URLState>();
		for (URLObjectReference ref : fetchUrls(plugId)) {
			urlMap.put(ref.getUrlState().getUrl(), ref.getUrlState());
		}
		return urlMap;
	}

	private List<URLObjectReference> fetchUrls(String plugId) {
		List<URLObjectReference> resultList = new ArrayList<URLObjectReference>();
		IMdekCallerQuery mdekCallerQuery = connectionFacade.getMdekCallerQuery();

		// Fetch all URLs for objects that are published and not modified (objIdPub = objId)
		String qString = "select obj.objUuid, obj.objName, obj.objClass, " +
				"urlRef.urlLink, urlRef.content " +
			"from ObjectNode oNode " +
				"inner join oNode.t01ObjectPublished obj " +
				"inner join obj.t017UrlRefs urlRef " +
			"where oNode.objIdPublished = oNode.objId " +
			"order by urlRef.urlLink";

		IngridDocument response = mdekCallerQuery.queryHQLToMap(plugId, qString, null, "");
		IngridDocument result = MdekUtils.getResultFromResponse(response);

		if (result != null) {
			List<IngridDocument> objs = (List<IngridDocument>) result.get(MdekKeys.OBJ_ENTITIES);
			if (objs != null) {
				Map<String, URLState> urlStateMap = new HashMap<String, URLState>(); 
				for (IngridDocument objEntity : objs) {
					URLObjectReference ref = new URLObjectReference();
					ref.setObjectClass(objEntity.getInt("obj.objClass"));
					ref.setObjectName(objEntity.getString("obj.objName"));
					ref.setObjectUuid(objEntity.getString("obj.objUuid"));
					ref.setUrlReferenceDescription(objEntity.getString("urlRef.content"));
					String url = objEntity.getString("urlRef.urlLink");
					URLState urlState = urlStateMap.get(url);
					if (urlState == null) {
						urlState = new URLState(url);
						urlStateMap.put(url, urlState);
					}
					ref.setUrlState(urlState);
					resultList.add(ref);
				}
			}
		}
		return resultList;
	}
}

