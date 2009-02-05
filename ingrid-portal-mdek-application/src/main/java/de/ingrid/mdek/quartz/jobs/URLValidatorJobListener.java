package de.ingrid.mdek.quartz.jobs;

import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

import de.ingrid.mdek.MdekKeys;
import de.ingrid.mdek.MdekUtils;
import de.ingrid.mdek.caller.IMdekCallerCatalog;
import de.ingrid.mdek.quartz.jobs.util.URLObjectReference;
import de.ingrid.mdek.util.MdekCatalogUtils;
import de.ingrid.mdek.util.MdekSecurityUtils;
import de.ingrid.utils.IngridDocument;


public class URLValidatorJobListener implements JobListener {

	private final static Logger log = Logger.getLogger(URLValidatorJobListener.class);	

	private final String listenerName;
	private final String plugId;
	private final IMdekCallerCatalog mdekCallerCatalog;

	public URLValidatorJobListener(String listenerName, IMdekCallerCatalog mdekCallerCatalog, String plugId) {
		this.listenerName = listenerName;
		this.mdekCallerCatalog = mdekCallerCatalog;
		this.plugId = plugId;
	}

	public String getName() {
		return listenerName;
	}


	public void jobWasExecuted(JobExecutionContext jobExecutionContext,
			JobExecutionException jobExecutionException) {

		List<URLObjectReference> urlObjectReferences = (List<URLObjectReference>) jobExecutionContext.getResult();

		IngridDocument jobInfo = new IngridDocument();
		jobInfo.put(MdekKeys.URL_RESULT, MdekCatalogUtils.convertFromUrlJobResult(urlObjectReferences));
		jobInfo.put(MdekKeys.JOBINFO_START_TIME, MdekUtils.dateToTimestamp(jobExecutionContext.getFireTime()));
		mdekCallerCatalog.setURLInfo(plugId, jobInfo, MdekSecurityUtils.getCurrentUserUuid());
	}

	// Do nothing for the following two methods
	public void jobExecutionVetoed(JobExecutionContext arg0) {}
	public void jobToBeExecuted(JobExecutionContext arg0) {}
}