package de.ingrid.mdek.handler;

import java.util.List;

import de.ingrid.mdek.beans.JobInfoBean;
import de.ingrid.mdek.beans.VersionInformation;

public interface GeneralRequestHandler {

	public List<VersionInformation> getVersion();
	public JobInfoBean getRunningJobInfo();
	public JobInfoBean cancelRunningJob();
}
