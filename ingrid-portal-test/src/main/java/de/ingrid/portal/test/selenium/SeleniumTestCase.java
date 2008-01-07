package de.ingrid.portal.test.selenium;

import org.openqa.selenium.server.SeleniumServer;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;

import junit.framework.TestCase;

public class SeleniumTestCase extends TestCase {

	protected SeleniumServer seleniumServer;
	protected Selenium selenium;

	public void setUp() {
		try {
			seleniumServer = new SeleniumServer(4444);
			seleniumServer.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		selenium = new DefaultSelenium("localhost", 4444, "*iexplore", "http://www.portalu.de");
		selenium.start();
	}

	public void tearDown() {
		selenium.stop();
		seleniumServer.stop();
	}
	
	
}
