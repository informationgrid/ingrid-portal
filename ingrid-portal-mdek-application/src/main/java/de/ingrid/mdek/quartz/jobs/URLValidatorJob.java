package de.ingrid.mdek.quartz.jobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpConnectionParams;
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
import de.ingrid.mdek.beans.JobInfoBean.EntityType;
import de.ingrid.mdek.caller.IMdekCallerQuery;
import de.ingrid.mdek.handler.ConnectionFacade;
import de.ingrid.mdek.quartz.jobs.util.URLObjectReference;
import de.ingrid.mdek.quartz.jobs.util.URLState;
import de.ingrid.mdek.quartz.jobs.util.URLValidator;
import de.ingrid.mdek.quartz.jobs.util.URLState.State;
import de.ingrid.mdek.util.MdekUtils;
import de.ingrid.utils.IngridDocument;

public class URLValidatorJob extends QuartzJobBean implements MdekJob, InterruptableJob {

	private final static Logger log = Logger.getLogger(URLValidatorJob.class);	

	private static final String JOB_BASE_NAME = "urlValidatorJob_";
	private static final String JOB_LISTENER_SUFFIX = "_listener";
	public static final String URL_MAP = "urlMap";
	public static final String URL_OBJECT_REFERENCES = "urlObjectReferences";
	public static final String END_TIME = "endTime";
	public static final int NUM_THREADS = 10;

	private final static int CONNECTION_TIMEOUT = 5000;
	private final static int SOCKET_TIMEOUT = 5000;

	private final String plugId;
	private final String jobName; 
	private final IMdekCallerQuery mdekCallerQuery;
	private final JobDetail jobDetail;
	private final URLValidatorJobListener jobListener;
	private Scheduler scheduler;

	// Flag that signals if the job execution should be interrupted
	private boolean cancelJob;


	// No args constructor is required for the job to be scheduled by quartz
	public URLValidatorJob() {
		this.plugId = null;
		this.jobName = null;
		this.mdekCallerQuery = null;
		this.jobDetail = null;
		this.jobListener = null;
		this.cancelJob = false;
	}

	public URLValidatorJob(ConnectionFacade connectionFacade, String plugId) {
		this.mdekCallerQuery = connectionFacade.getMdekCallerQuery();
		this.plugId = plugId;
		jobName = createJobName(plugId);
		String jobListenerName = createJobListenerName(plugId);
		jobListener = new URLValidatorJobListener(jobListenerName, connectionFacade.getMdekCallerCatalog(), plugId);
		jobDetail = createJobDetail();
	}

	public static String createJobName(String plugId) {
		return (JOB_BASE_NAME + plugId);
	}

	private static String createJobListenerName(String plugId) {
		return (JOB_BASE_NAME + plugId + JOB_LISTENER_SUFFIX);
	}

	private JobDetail createJobDetail() {
		List<URLObjectReference> urlObjectReferences = fetchUrls();
		Map<String, URLState> urlMap = createUrlMap(urlObjectReferences);

		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put(URLValidatorJob.URL_MAP, urlMap);
		dataMap.put(URLValidatorJob.URL_OBJECT_REFERENCES, urlObjectReferences);

		JobDetail jobDetail = new JobDetail(jobName, Scheduler.DEFAULT_GROUP, URLValidatorJob.class);
		JobDataMap jobDataMap = new JobDataMap(dataMap);
		jobDetail.setJobDataMap(jobDataMap);
		jobDetail.addJobListener(jobListener.getName());

		return jobDetail;
	}


	private Map<String, URLState> createUrlMap(List<URLObjectReference> urlObjectReferences) {
		Map<String, URLState> urlMap = new HashMap<String, URLState>();
		for (URLObjectReference ref : urlObjectReferences) {
			urlMap.put(ref.getUrlState().getUrl(), ref.getUrlState());
		}
		return urlMap;
	}


	private List<URLObjectReference> fetchUrls() {
		List<URLObjectReference> resultList = new ArrayList<URLObjectReference>();

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


	public boolean start(Scheduler scheduler) throws SchedulerException {
		this.scheduler = scheduler;

		if (!isRunning()) {
			scheduler.addJob(jobDetail, true);
			scheduler.addJobListener(jobListener);
			scheduler.triggerJobWithVolatileTrigger(jobName, Scheduler.DEFAULT_GROUP);
			return true;

		} else {
			return false;
		}
	}

	@Override
	protected void executeInternal(JobExecutionContext jobExecutionContext)
			throws JobExecutionException {

		ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);
		JobDataMap mergedJobDataMap = jobExecutionContext.getMergedJobDataMap();
		Map<String, URLState> urlMap = (Map<String, URLState>) mergedJobDataMap.get(URL_MAP);
		List<URLValidator> validatorTasks = new ArrayList<URLValidator>(urlMap.size());

		HttpClientParams httpClientParams = new HttpClientParams();
		httpClientParams.setConnectionManagerTimeout(0);
		httpClientParams.setSoTimeout(SOCKET_TIMEOUT);
		HttpConnectionParams httpParams = new HttpConnectionParams();
		httpParams.setConnectionTimeout(CONNECTION_TIMEOUT);
		httpClientParams.setDefaults(httpParams);
		HttpClient httpClient = new HttpClient(httpClientParams, new MultiThreadedHttpConnectionManager());
		for (URLState urlState : urlMap.values()) {
			validatorTasks.add(new URLValidator(httpClient, urlState));
		}

		log.debug("Starting url validation...");
		long startTime = System.currentTimeMillis();
		List<Future<URLState>> resultFutureList = new ArrayList<Future<URLState>>();
		for (URLValidator validator : validatorTasks) {
			resultFutureList.add(executorService.submit(validator));
		}

		for (Future<URLState> future : resultFutureList) {
			try {
				if (!cancelJob) {
					future.get();

				} else {
					log.debug("forcing shutdown of executor service...");
					executorService.shutdownNow();
					break;
				}

			} catch (Exception ex) {
				log.debug("Exception while fetching result from future.", ex);
			}
		}
		long endTime = System.currentTimeMillis();
		log.debug("URL Validation took "+(endTime - startTime)+" ms.");

		executorService.shutdown();
		jobExecutionContext.setResult(mergedJobDataMap.get(URL_OBJECT_REFERENCES));
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
			Map<String, URLState> urlMap = (Map<String, URLState>) executionContext.getMergedJobDataMap().get(URLValidatorJob.URL_MAP);
			int totalNumberOfUrls = urlMap.size();
			int numberOfProcessedUrls = 0;
			for (URLState urlState : urlMap.values()) {
				if (State.NOT_CHECKED != urlState.getState()) {
					++numberOfProcessedUrls;
				}
			}

			JobInfoBean jobInfoResult = new JobInfoBean();
			jobInfoResult.setDescription(jobName);
			jobInfoResult.setEntityType(EntityType.URL);
			jobInfoResult.setNumEntities(totalNumberOfUrls);
			jobInfoResult.setNumProcessedEntities(numberOfProcessedUrls);
			jobInfoResult.setStartTime(executionContext.getFireTime());
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
			log.error("Error interrupting URL Validation job.", ex);
		
		} catch (SchedulerException ex) {
			log.error("Error interrupting URL Validation job.", ex);
		}
 
	}

	public String getName() {
		return jobName;
	}

	public void interrupt() throws UnableToInterruptJobException {
		cancelJob = true;
	}
}