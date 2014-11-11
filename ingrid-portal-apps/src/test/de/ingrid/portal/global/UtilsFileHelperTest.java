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
package de.ingrid.portal.global;

import java.io.File;
import java.util.ArrayList;

import junit.framework.TestCase;

public class UtilsFileHelperTest extends TestCase {

	File file = new File("src/webapp/images/index_themen.jpg");
	
	static int HITS_PER_PAGE = 10;

    static int CURRENT_PAGE = 1;

    static int TIMEOUT = 10000;
    
	
	
	public void testGenerateFilename() throws Exception{
		String test;
		
		test = UtilsFileHelper.generateFilename("image", "Logo", new String(UtilsFileHelper.getBytesFromFile(file)).hashCode(), "jpeg");
		assertEquals(test, "image_Logo_23976954.jpeg");
		
		test = UtilsFileHelper.generateFilename("image", "Logo",  new String (UtilsFileHelper.getBytesFromFile(file) + "1").hashCode(), "jpeg");
		assertFalse(test.equals(file.getName()));
    }
	
	public void testSortFileByAge() {
		ArrayList<Long> test = new ArrayList<Long>();
		test.add((long)1);
		test.add((long)3);
		test.add((long)53);
		test.add((long)2);
		test.add((long)7);
		test.add((long)0);
		
		UtilsFileHelper.sortFileByDate(test);
		assertEquals(test.get(4), Long.valueOf("7"));
	}
}
