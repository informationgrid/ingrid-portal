package de.ingrid.portal.test.selenium;

public class PortaluTest extends PortalUSeleniumTestCase {

	public void testQuery() throws Exception {
		selenium.open("/");
		if (selenium.isTextPresent("German")) {
			selenium.click("link=German");
			selenium.waitForPageToLoad("30000");
		}
		selenium.type("qrystr", "wasser");
		selenium.click("//input[@value='PortalU Suche']");
		selenium.waitForPageToLoad("30000");
		assertTrue(selenium.isTextPresent("bewertete Treffer"));
		assertTrue(selenium.isTextPresent("weitere Datenquellen"));
	}
}
