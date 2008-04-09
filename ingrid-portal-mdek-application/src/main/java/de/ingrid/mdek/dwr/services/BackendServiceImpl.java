package de.ingrid.mdek.dwr.services;

import java.util.List;

import org.apache.log4j.Logger;

import de.ingrid.mdek.beans.JobInfoBean;
import de.ingrid.mdek.beans.VersionInformation;
import de.ingrid.mdek.handler.GeneralRequestHandler;

public class BackendServiceImpl implements BackendService {

	private final static Logger log = Logger.getLogger(BackendServiceImpl.class);	

	// Injected by Spring
	private GeneralRequestHandler generalRequestHandler;

	
	public List<VersionInformation> getBackendVersion() {
		return generalRequestHandler.getVersion();
	}
	
	public JobInfoBean getRunningJobInfo() {
		return generalRequestHandler.getRunningJobInfo();
	}

	public JobInfoBean cancelRunningJob() {
		return generalRequestHandler.cancelRunningJob();
	}

	public GeneralRequestHandler getGeneralRequestHandler() {
		return generalRequestHandler;
	}

	public void setGeneralRequestHandler(GeneralRequestHandler generalRequestHandler) {
		this.generalRequestHandler = generalRequestHandler;
	}

}
