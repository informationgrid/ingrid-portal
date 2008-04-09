package de.ingrid.mdek.handler;

import java.util.List;

import org.apache.log4j.Logger;

import de.ingrid.mdek.beans.JobInfoBean;
import de.ingrid.mdek.beans.VersionInformation;
import de.ingrid.mdek.caller.IMdekCaller;
import de.ingrid.mdek.dwr.util.HTTPSessionHelper;
import de.ingrid.mdek.util.MdekUtils;
import de.ingrid.utils.IngridDocument;

public class GeneralRequestHandlerImpl implements GeneralRequestHandler {

	private final static Logger log = Logger.getLogger(GeneralRequestHandlerImpl.class);

	// Injected by Spring
	private ConnectionFacade connectionFacade;

	// Initialized by spring through the init method
	private IMdekCaller mdekCaller;

	public void init() {
		mdekCaller = connectionFacade.getMdekCaller();
	}
	
	public List<VersionInformation> getVersion() {
		IngridDocument response = mdekCaller.getVersion(connectionFacade.getCurrentPlugId());
		return MdekUtils.extractVersionInformationFromResponse(response);
	}
	
	public JobInfoBean getRunningJobInfo() {
		IngridDocument response = mdekCaller.getRunningJobInfo(connectionFacade.getCurrentPlugId(), HTTPSessionHelper.getCurrentSessionId());
		return MdekUtils.extractJobInfoFromResponse(response);
	}

	public JobInfoBean cancelRunningJob() {
		IngridDocument response = mdekCaller.cancelRunningJob(connectionFacade.getCurrentPlugId(), HTTPSessionHelper.getCurrentSessionId());
		return MdekUtils.extractJobInfoFromResponse(response);	
	}

	public ConnectionFacade getConnectionFacade() {
		return connectionFacade;
	}

	public void setConnectionFacade(ConnectionFacade connectionFacade) {
		this.connectionFacade = connectionFacade;
	}
}
