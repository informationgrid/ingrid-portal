package de.ingrid.mdek.handler;

import java.util.List;

import org.apache.log4j.Logger;

import de.ingrid.mdek.beans.JobInfoBean;
import de.ingrid.mdek.beans.VersionInformation;
import de.ingrid.mdek.caller.IMdekClientCaller;
import de.ingrid.mdek.util.MdekSecurityUtils;
import de.ingrid.mdek.util.MdekUtils;
import de.ingrid.utils.IngridDocument;

public class GeneralRequestHandlerImpl implements GeneralRequestHandler {

	private final static Logger log = Logger.getLogger(GeneralRequestHandlerImpl.class);

	// Injected by Spring
	private ConnectionFacade connectionFacade;

	// Initialized by spring through the init method
	private IMdekClientCaller mdekClientCaller;

	public void init() {
		mdekClientCaller = connectionFacade.getMdekClientCaller();
	}
	
	public List<VersionInformation> getVersion() {
		IngridDocument response = mdekClientCaller.getVersion(connectionFacade.getCurrentPlugId());
		return MdekUtils.extractVersionInformationFromResponse(response);
	}
	
	public JobInfoBean getRunningJobInfo() {
		IngridDocument response = mdekClientCaller.getRunningJobInfo(connectionFacade.getCurrentPlugId(), MdekSecurityUtils.getCurrentUserUuid());
		return MdekUtils.extractJobInfoFromResponse(response);
	}

	public JobInfoBean cancelRunningJob() {
		IngridDocument response = mdekClientCaller.cancelRunningJob(connectionFacade.getCurrentPlugId(), MdekSecurityUtils.getCurrentUserUuid());
		return MdekUtils.extractJobInfoFromResponse(response);	
	}

	public ConnectionFacade getConnectionFacade() {
		return connectionFacade;
	}

	public void setConnectionFacade(ConnectionFacade connectionFacade) {
		this.connectionFacade = connectionFacade;
	}
}
