package de.ingrid.mdek.quartz.jobs;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

import de.ingrid.mdek.MdekKeys;
import de.ingrid.mdek.MdekUtils;
import de.ingrid.mdek.caller.IMdekCallerCatalog;
import de.ingrid.mdek.quartz.jobs.util.URLObjectReference;
import de.ingrid.mdek.util.MdekCatalogUtils;
import de.ingrid.utils.IngridDocument;


public class URLValidatorJobListener implements JobListener {

	private final static Logger log = Logger.getLogger(URLValidatorJobListener.class);	

	private final String listenerName;
	private final String plugId;
	// We need to store the userUuid since the method 'jobWasExecuted' is invoked by quartz in another thread.
	// Therefore we can't get the username via MdekSecurityUtils.getCurrentUserUuid().
	private final String userUuid;
	private final IMdekCallerCatalog mdekCallerCatalog;

	public URLValidatorJobListener(String listenerName, IMdekCallerCatalog mdekCallerCatalog, String userUuid, String plugId) {
		this.listenerName = listenerName;
		this.mdekCallerCatalog = mdekCallerCatalog;
		this.userUuid = userUuid;
		this.plugId = plugId;
	}

	public String getName() {
		return listenerName;
	}


	public void jobWasExecuted(JobExecutionContext jobExecutionContext,
			JobExecutionException jobExecutionException) {
	    Map<String, List<URLObjectReference>> results = (Map<String, List<URLObjectReference>>) jobExecutionContext.getResult();
		List<URLObjectReference> urlObjectReferences = results.get(MdekKeys.URL_RESULT);
		List<URLObjectReference> capabilitiesReferences = results.get(MdekKeys.CAP_RESULT);

		if (urlObjectReferences != null) {
			log.debug("sending URL Job result to the backend.");
			IngridDocument jobInfo = new IngridDocument();
			jobInfo.put(MdekKeys.URL_RESULT, MdekCatalogUtils.convertFromUrlJobResult(urlObjectReferences));
			jobInfo.put(MdekKeys.CAP_RESULT, MdekCatalogUtils.convertFromUrlJobResult(capabilitiesReferences));
			jobInfo.put(MdekKeys.JOBINFO_START_TIME, MdekUtils.dateToTimestamp(jobExecutionContext.getFireTime()));
			mdekCallerCatalog.setURLInfo(plugId, jobInfo, userUuid);
			log.debug("URL Validator Job result has been stored in the DB.");

		} else {
			log.debug("URL Validator Job result was null. Job has probably been canceled. Result will not be stored in the DB!");
		}
	}

	// Do nothing for the following two methods
	public void jobExecutionVetoed(JobExecutionContext arg0) {}
	public void jobToBeExecuted(JobExecutionContext arg0) {}
}