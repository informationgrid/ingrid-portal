package de.ingrid.mdek.quartz.jobs;

import java.util.Map;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

import de.ingrid.mdek.quartz.jobs.util.URLState;
import de.ingrid.mdek.quartz.jobs.util.URLState.State;


public class URLValidatorJobListener implements JobListener {

	private final static Logger log = Logger.getLogger(URLValidatorJobListener.class);	

	private final String listenerName;

	public URLValidatorJobListener(String listenerName) {
		this.listenerName = listenerName;
	}

	public String getName() {
		return listenerName;
	}


	public void jobWasExecuted(JobExecutionContext jobExecutionContext,
			JobExecutionException jobExecutionException) {
		// TODO Store result in the backend

		log.debug("job executed successfully. Result:");
		Map<String, URLState> urlMap = (Map<String, URLState>) jobExecutionContext.getResult();
		for (Map.Entry<String, URLState> entry : urlMap.entrySet()) {
			URLState urlState = entry.getValue();
			log.debug("URL: "+urlState.getUrl());
			if (urlState.getState() == State.HTTP_ERROR || urlState.getState() == State.VALID) {
				log.debug("response: "+urlState.getState()+" ("+urlState.getResponseCode()+")");

			} else {
				log.debug("response: "+urlState.getState());
			}
		}
	}

	// Do nothing for the following two methods
	public void jobExecutionVetoed(JobExecutionContext arg0) {}
	public void jobToBeExecuted(JobExecutionContext arg0) {}
}