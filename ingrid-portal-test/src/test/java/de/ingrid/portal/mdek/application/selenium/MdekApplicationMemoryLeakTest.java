package de.ingrid.portal.mdek.application.selenium;

import org.openqa.selenium.server.SeleniumServer;

import de.ingrid.portal.test.selenium.MdekApplicationSeleniumTestCase;

public class MdekApplicationMemoryLeakTest extends MdekApplicationSeleniumTestCase {

	public void testQuery() throws Exception {
		selenium.open("/ingrid-portal-mdek-application/mdek_entry.jsp");
		selenium.waitForPageToLoad("30000");
		assertTrue(selenium.isTextPresent("Kein Objekt"));
		selenium.click("//div[5]/div/div[1]/div[1]");
		Thread.sleep(5000);
		assertTrue(selenium.isTextPresent("Kein Objekt"));
		selenium.click("//div[2]/div[3]/div[1]/div[1]");
		Thread.sleep(5000);
		assertTrue(selenium.isTextPresent("Kein Objekt"));
		for (int i=0; i< 1000; i++) {
			selenium.click("//div[3]/div[2]/div[1]/div[1]/div[3]/span");
			Thread.sleep(500);
			assertTrue(selenium.isTextPresent("Alfred Toepfer Akademie für Naturschutz NNA"));
		}
	}
}
