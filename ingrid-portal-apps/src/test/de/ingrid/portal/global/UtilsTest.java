/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
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
