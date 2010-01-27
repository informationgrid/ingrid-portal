package de.ingrid.portal.scheduler.jobs;

import junit.framework.TestCase;

public class UpgradeClientJobTest extends TestCase {

    public final void testGetComponentsFromUpgrader() {
        UpgradeClientJob job = new UpgradeClientJob();
        //job.getComponentsFromUpgrader("http://harrison.its-technidata.de/update");
        job.getComponentsFromUpgrader("http://localhost/atom.rss");
    }

}
