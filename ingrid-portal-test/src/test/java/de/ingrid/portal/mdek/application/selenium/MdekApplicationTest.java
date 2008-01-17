package de.ingrid.portal.mdek.application.selenium;

import de.ingrid.portal.test.selenium.MdekApplicationSeleniumTestCase;

public class MdekApplicationTest extends MdekApplicationSeleniumTestCase {

	public void testQuery() throws Exception {
		selenium.open("/ingrid-portal-mdek-application/mdek_entry.jsp");
		selenium.waitForPageToLoad("30000");
		assertTrue(selenium.isTextPresent("No Object/Adress selected"));
		selenium.click("//div[5]/div/div[1]/div[1]");
		Thread.sleep(5000);
		assertTrue(selenium.isTextPresent("No Object/Adress selected"));
		selenium.click("//div[2]/div[3]/div[1]/div[1]");
		Thread.sleep(5000);
		assertTrue(selenium.isTextPresent("No Object/Adress selected"));
		selenium.click("//div[3]/div[2]/div[1]/div[1]/div[3]/span");
		Thread.sleep(5000);
		assertTrue(selenium.isTextPresent("Alfred Toepfer Akademie für Naturschutz NNA"));
	}
}
