/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
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
