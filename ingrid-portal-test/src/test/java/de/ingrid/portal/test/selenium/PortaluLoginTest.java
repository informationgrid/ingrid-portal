package de.ingrid.portal.test.selenium;

public class PortaluLoginTest extends SeleniumTestCase {

	public void testQuery() throws Exception {
		selenium.open("/");
		if (selenium.isTextPresent("German")) {
			selenium.click("link=German");
			selenium.waitForPageToLoad("30000");
		}

		selenium.open("/ingrid-portal/portal/default-page.psml");
		selenium.click("link=Mein PortalU");
		selenium.waitForPageToLoad("30000");
		selenium.click("login");
		selenium.type("login", "jm");
		selenium.type("passwd", "jm");
		selenium.click("//input[@value='Anmelden']");
		selenium.waitForPageToLoad("30000");
		// sleep for complete client side redirect
		Thread.sleep(1000);
		assertTrue(selenium.isTextPresent("Willkommen Joachim"));
		selenium.click("link=Administration");
		selenium.waitForPageToLoad("30000");
		assertTrue(selenium.isTextPresent("Überwachung"));
		selenium.click("link=LOGOUT");
		selenium.waitForPageToLoad("30000");
		assertTrue(selenium.isTextPresent("PortalU INFORMIERT"));
	}
}
