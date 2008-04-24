/**
 * 
 */
package de.ingrid.portal.test.selenium;

import com.thoughtworks.selenium.DefaultSelenium;

/**
 * @author Administrator
 *
 */
public class IngridSelenium extends DefaultSelenium {

	public IngridSelenium(String serverHost, int serverPort, String browserStartCommand, String browserURL) {
		super(serverHost, serverPort, browserStartCommand, browserURL);
	}
	
	public void doContextMenuAt(String locator, String position) {
//		this.setCursorPosition(locator, position);
		this.fireEvent(locator, "contextmenu");
	}

}
