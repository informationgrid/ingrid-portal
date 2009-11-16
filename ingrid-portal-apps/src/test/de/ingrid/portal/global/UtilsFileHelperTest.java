package de.ingrid.portal.global;

import java.io.File;

import junit.framework.TestCase;

public class UtilsFileHelperTest extends TestCase {

	File file = new File("src/webapp/images/index_themen.jpg");
	
	static int HITS_PER_PAGE = 10;

    static int CURRENT_PAGE = 1;

    static int TIMEOUT = 10000;
    
	
	
	public void testGenerateFilename() throws Exception{
		String test;
		
		test = UtilsFileHelper.generateFilename("image", "Logo", new String(UtilsFileHelper.getBytesFromFile(file)).hashCode(), "jpeg");
		assertEquals(test, "image_Logo_1243910217.jpeg");
		System.out.println(test +" == image_Logo_1243910217.jpeg");
		
		test = UtilsFileHelper.generateFilename("image", "Logo",  new String (UtilsFileHelper.getBytesFromFile(file) + "1").hashCode(), "jpeg");
		assertFalse(test.equals(file.getName()));
		System.out.println(test +" != image_Logo_1243910217.jpeg");        
    }
	
}
