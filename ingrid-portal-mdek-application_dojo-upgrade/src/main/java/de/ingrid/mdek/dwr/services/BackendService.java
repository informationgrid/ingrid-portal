package de.ingrid.mdek.dwr.services;

import java.util.List;

import de.ingrid.mdek.beans.JobInfoBean;
import de.ingrid.mdek.beans.VersionInformation;

public interface BackendService {

	public List<VersionInformation> getBackendVersion();
	public JobInfoBean getRunningJobInfo();
	public JobInfoBean cancelRunningJob();
}