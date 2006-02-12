package de.ingrid.portal.scheduler.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class DummyJob implements Job {

    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        System.out.println("executing the dummy job");
    }
}
