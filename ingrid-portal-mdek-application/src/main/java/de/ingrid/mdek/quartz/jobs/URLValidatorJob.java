package de.ingrid.mdek.quartz.jobs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import de.ingrid.mdek.quartz.jobs.util.URLState;
import de.ingrid.mdek.quartz.jobs.util.URLValidator;

public class URLValidatorJob extends QuartzJobBean {

	private final static Logger log = Logger.getLogger(URLValidatorJob.class);	

	public static final String URL_MAP = "urlMap";
	public static final String START_TIME = "startTime";
	public static final String END_TIME = "endTime";

	@Override
	protected void executeInternal(JobExecutionContext jobExecutionContext)
			throws JobExecutionException {

		ExecutorService executorService = Executors.newFixedThreadPool(20);
		JobDataMap mergedJobDataMap = jobExecutionContext.getMergedJobDataMap();
		Map<String, URLState> urlMap = (Map<String, URLState>) mergedJobDataMap.get(URL_MAP);
		List<URLValidator> validatorTasks = new ArrayList<URLValidator>(urlMap.size());
		for (URLState urlState : urlMap.values()) {
			validatorTasks.add(new URLValidator(urlState));
		}

		log.debug("Starting url validation...");
		long startTime = System.currentTimeMillis();
		List<Future<URLState>> resultFutureList = null; 
		try {
			resultFutureList = executorService.invokeAll(validatorTasks);

		} catch (InterruptedException ex) {
			log.debug("Exception while invoking URL Validation tasks.", ex);
		}
		long endTime = System.currentTimeMillis();
		log.debug("URL Validation took "+(endTime - startTime)+" ms.");

		for (Future<URLState> future : resultFutureList) {
			try {
				future.get();

			} catch (Exception ex) {
				log.debug("Exception while fetching result from future.", ex);
			}
		}

		executorService.shutdown();
		jobExecutionContext.setResult(urlMap);
	}
}