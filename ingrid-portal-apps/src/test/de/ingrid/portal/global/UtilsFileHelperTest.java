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
