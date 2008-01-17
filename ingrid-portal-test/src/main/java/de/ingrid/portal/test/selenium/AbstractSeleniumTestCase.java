package de.ingrid.portal.test.selenium;

import junit.framework.TestCase;

import org.openqa.selenium.server.SeleniumServer;

import com.thoughtworks.selenium.Selenium;

public abstract class AbstractSeleniumTestCase extends TestCase {

	protected SeleniumServer seleniumServer;
	protected Selenium selenium = null;

	public void setUp() {
		try {
			seleniumServer = new SeleniumServer(4444);
			seleniumServer.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public abstract void init();

	public void tearDown() {
		selenium.stop();
		seleniumServer.stop();
	}

}
