/**
 * 
 */
package de.ingrid.portal.global;

import java.net.URL;
import java.util.HashMap;

import junit.framework.TestCase;

/**
 * @author joachim
 *
 */
public class UtilsTest extends TestCase {

	/**
	 * Test method for {@link de.ingrid.portal.global.Utils#mergeTemplate(java.lang.String, java.util.Map, java.lang.String)}.
	 */
	public void testMergeTemplateStringMapString() {
		URL url = Thread.currentThread().getContextClassLoader().getResource("test.vm");
		String templatePath = url.getPath();

		HashMap mailData = new HashMap();
		mailData.put("name", "Joachim");
		
		String s = Utils.mergeTemplate(templatePath, mailData, "map");
		
		//assertEquals("Hallo Joachim!", s);
	}

}
