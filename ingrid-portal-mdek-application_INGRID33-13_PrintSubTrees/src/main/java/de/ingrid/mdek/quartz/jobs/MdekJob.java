package de.ingrid.mdek.quartz.jobs;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import de.ingrid.mdek.beans.JobInfoBean;

public interface MdekJob {

	public boolean start(Scheduler scheduler) throws SchedulerException;
	public void stop();
	public boolean isRunning();
	public String getName();
	public JobInfoBean getRunningJobInfo();
}
