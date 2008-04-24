package de.ingrid.portal.test.selenium;

import com.thoughtworks.selenium.DefaultSelenium;

public class PortalUSeleniumTestCase extends AbstractSeleniumTestCase {

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.ingrid.portal.test.selenium.AbstractSeleniumTestCase#init(com.thoughtworks.selenium.Selenium)
	 */
	@Override
	public void init() {
		selenium = new IngridSelenium("localhost", 4444, "*firefox", "http://harrison.its-technidata.de");
		selenium.start();
	}
}
