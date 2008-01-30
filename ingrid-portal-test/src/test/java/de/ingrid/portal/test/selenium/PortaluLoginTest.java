package de.ingrid.portal.test.selenium;

public class PortaluLoginTest extends PortalUSeleniumTestCase {

	public void testCreateUserByAdmin() throws Exception {
		switchToGermanHome();
		loginAs("admin", "admin");

		selenium.click("link=Administration");
		selenium.waitForPageToLoad("30000");
		selenium.click("link=Benutzer");
		selenium.waitForPageToLoad("30000");
		selenium.click("doNew");
		selenium.waitForPageToLoad("30000");
		selenium.select("salutation", "label=Herr");
		selenium.type("firstname", "Test Dude");
		selenium.type("lastname", "Tester");
		selenium.type("email", "joachim@wemove.com");
		selenium.type("id", "test");
		selenium.type("password_new", "test");
		selenium.type("password_new_confirm", "test");
		selenium.type("id", "tester");
		selenium.click("doSave");
		selenium.waitForPageToLoad("30000");
		assertTrue(selenium.isTextPresent("Account erfolgreich angelegt") || selenium.isTextPresent("Die Bestätigungs E-Mail konnte nicht erfolgreich versendet werden!"));
		// check for error msg existing user
		selenium.click("doSave");
		selenium.waitForPageToLoad("30000");
		assertTrue(selenium.isTextPresent("Der Benutzername existiert bereits."));
		
		logout();
		
		loginAs("tester", "test");
		logout();
		
		// delete test user
		loginAs("admin", "admin");
		deleteUser("tester");
		logout();
	}
	
	public void testCreateUserByAnonymous() throws Exception {
		switchToGermanHome();
		selenium.click("link=Mein PortalU");
		selenium.click("link=Neuer Benutzer");
		selenium.waitForPageToLoad("30000");
		selenium.select("salutation", "label=Herr");
		selenium.type("firstname", "Test Dude");
		selenium.type("lastname", "Tester");
		selenium.type("email", "joachim@wemove.com");
		selenium.type("login", "tester");
		selenium.type("password", "test");
		selenium.type("password_confirm", "test");
		selenium.click("//input[@value='Konto erstellen']");
		selenium.waitForPageToLoad("30000");
		selenium.click("link=Mein PortalU");
		selenium.waitForPageToLoad("30000");
		selenium.click("login");
		selenium.type("login", "tester");
		selenium.type("passwd", "test");
		selenium.click("//input[@value='Anmelden']");
		selenium.waitForPageToLoad("30000");
		assertTrue(selenium.isTextPresent("Der Benutzer ist noch nicht frei geschaltet"));
		
		// delete test user
		loginAs("admin", "admin");
		deleteUser("tester");
		logout();
	}
	
	
	private void switchToGermanHome() {
		selenium.open("/ingrid-portal/portal/default-page.psml");
		if (selenium.isTextPresent("German")) {
			selenium.click("link=German");
			selenium.waitForPageToLoad("30000");
		}
		assertTrue(selenium.isTextPresent("PortalU INFORMIERT"));
	}
	
	private void loginAs(String user, String passwd) throws InterruptedException {
		selenium.open("/ingrid-portal/portal/default-page.psml");
		selenium.click("link=Mein PortalU");
		selenium.waitForPageToLoad("30000");
		selenium.click("login");
		selenium.type("login", user);
		selenium.type("passwd", passwd);
		selenium.click("//input[@value='Anmelden']");
		selenium.waitForPageToLoad("30000");
		// sleep for complete client side redirect
		Thread.sleep(1000);
		assertTrue(selenium.isTextPresent("Willkommen"));
	}
	
	private void logout() {
		selenium.click("link=LOGOUT");
		selenium.waitForPageToLoad("30000");
		assertTrue(selenium.isTextPresent("PortalU INFORMIERT"));
	}
	
	private void deleteUser(String user) throws Exception {
		selenium.click("link=Administration");
		selenium.waitForPageToLoad("30000");
		selenium.click("link=Benutzer");
		selenium.waitForPageToLoad("30000");
		selenium.click("//input[@value='tester']");
		selenium.click("doDelete");
		selenium.waitForPageToLoad("30000");
		assertTrue(selenium.getConfirmation().matches("^Sollen die markierten Einträge wirklich gelöscht werden [\\s\\S]$"));
		// check if the test user has been removed
		assertFalse(selenium.isTextPresent(user));
	}
	
}
