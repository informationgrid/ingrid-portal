package de.ingrid.portal.scheduler.jobs;

import org.apache.jetspeed.scheduler.JobEntry;
import org.apache.jetspeed.scheduler.ScheduledJob;

public class DummyJob extends ScheduledJob {

    public void run(JobEntry arg0) throws Exception {
        System.out.println("executing the dummy job");
    }
}
