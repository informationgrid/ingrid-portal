/**
 * 
 */
package de.ingrid.portal.scheduler.jobs;

import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

import junit.framework.TestCase;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;

import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Utils;

/**
 * @author joachim
 * 
 */
public class IngridMonitorAbstractJobTest extends TestCase {

	/**
	 * Test method for
	 * {@link de.ingrid.portal.scheduler.jobs.IngridMonitorAbstractJob#sendAlertMail(org.quartz.JobDataMap)}.
	 */
	public void testMergeAlertText() {
		JobDetail job = new JobDetail();
		job.setName("\\kog-group:test-alert-iplug");

		JobDataMap dataMap = new JobDataMap();
		dataMap.put(IngridMonitorAbstractJob.PARAM_EVENT_OCCURENCES, 1);
		dataMap.put(IngridMonitorAbstractJob.PARAM_STATUS, 1);
		dataMap.put(IngridMonitorAbstractJob.PARAM_COMPONENT_TYPE, IngridMonitorIPlugJob.COMPONENT_TYPE);
		dataMap.put(IngridMonitorAbstractJob.PARAM_QUERY, "wasser iplugs:\"\\kog-group:test-alert-iplug\" ranking:any");
		dataMap.put(IngridMonitorAbstractJob.PARAM_STATUS_CODE, IngridMonitorAbstractJob.STATUS_CODE_ERROR_TIMEOUT);
		dataMap.put(IngridMonitorAbstractJob.PARAM_COMPONENT_TITLE, "Test Komponente");
		dataMap.put(IngridMonitorAbstractJob.PARAM_TIMEOUT, 12345678);
		job.setJobDataMap(dataMap);

		URL url = Thread.currentThread().getContextClassLoader().getResource("test_component_monitor_alert.vm");
		String templatePath = url.getPath();

		HashMap mailData = new HashMap();
		mailData.put("JOB", job);
		ResourceBundle resources = ResourceBundle.getBundle(
				"de.ingrid.portal.resources.AdminPortalResources", Locale.GERMAN);
		mailData.put("MESSAGES", new IngridResourceBundle(resources));

		String text = Utils.mergeTemplate(templatePath, mailData, "map");

		assertTrue(text.indexOf("IPLUG") > -1);
		assertTrue(text.indexOf("Test Komponente") > -1);
	}

}
