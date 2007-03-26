/**
 * 
 */
package de.ingrid.portal.scheduler;

import java.util.ArrayList;
import java.util.Date;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.jetspeed.components.datasource.DBCPDatasourceComponent;
import org.apache.jetspeed.components.datasource.DatasourceComponent;
import org.apache.jetspeed.components.util.DatasourceTestCase;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerUtils;

import de.ingrid.portal.scheduler.jobs.IngridMonitorDummyJob;

/**
 * @author joachim
 * 
 */
public class IngridMonitorFacadeTestLocal extends DatasourceTestCase {

	protected void setUp() throws Exception {
		System.setProperty("org.apache.jetspeed.database.url", "jdbc:mysql://localhost/ingrid-portal");
		System.setProperty("org.apache.jetspeed.database.driver", "com.mysql.jdbc.Driver");
		System.setProperty("org.apache.jetspeed.database.user", "root");
		System.setProperty("org.apache.jetspeed.database.password", "");
		super.setUp();
	}

	/**
	 * Test method for
	 * {@link de.ingrid.portal.scheduler.IngridMonitorFacade#instance()}.
	 * 
	 * @throws SchedulerException
	 * @throws NamingException
	 * @throws InterruptedException
	 * @throws SchedulerException
	 */
	public void testInstance() throws NamingException, InterruptedException, SchedulerException {
		assertTrue(DatasourceComponent.class.isAssignableFrom(DBCPDatasourceComponent.class));
		InitialContext context = new InitialContext();
		// look up from jndi
		assertNotNull(context.lookup("java:/jdbc/jetspeed"));
		IngridMonitorFacade monitor = IngridMonitorFacade.instance();
		assertNotNull(monitor);

		Scheduler scheduler = monitor.getScheduler();
		JobDetail jobDetail = new JobDetail("myJob", // job name
				Scheduler.DEFAULT_GROUP, // job group (you can also specify
				// 'null' to use the default group)
				IngridMonitorDummyJob.class); // the java class to execute
		jobDetail.getJobDataMap().put("jobSays", "Hello World!");
		jobDetail.getJobDataMap().put("myFloatValue", 3.141f);
		jobDetail.getJobDataMap().put("myStateData", new ArrayList());
		jobDetail.setRequestsRecovery(false);

		Trigger trigger = TriggerUtils.makeSecondlyTrigger();
		trigger.setStartTime(new Date());
		trigger.setName("myTrigger");
		try {
			if (scheduler.getJobDetail("myJob", null) == null) {
				scheduler.scheduleJob(jobDetail, trigger);
			}
			Thread.sleep(5000);

			jobDetail = scheduler.getJobDetail("myJob", null);
			assertEquals("Hello World!", jobDetail.getJobDataMap().getString("jobSays"));
			assertEquals(0, ((ArrayList) jobDetail.getJobDataMap().get("myStateData")).size());

			scheduler.deleteJob("myJob", null);

			jobDetail = scheduler.getJobDetail("myJob", null);

			assertNull(jobDetail);

		} finally {
			monitor.shutdownMonitor(true);
			context.close();
		}
	}

}
