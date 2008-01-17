package de.ingrid.portal.test.selenium;

import com.thoughtworks.selenium.DefaultSelenium;

public class MdekApplicationSeleniumTestCase extends AbstractSeleniumTestCase {

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.ingrid.portal.test.selenium.AbstractSeleniumTestCase#init(com.thoughtworks.selenium.Selenium)
	 */
	@Override
	public void init() {
		selenium = new DefaultSelenium("localhost", 4444, "*firefox", "http://localhost:8080");
		selenium.start();
	}
}
