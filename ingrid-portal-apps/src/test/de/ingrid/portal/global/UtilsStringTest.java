package de.ingrid.portal.global;

import junit.framework.TestCase;

public class UtilsStringTest extends TestCase {

    /*
     * Test method for 'de.ingrid.portal.global.UtilsString.getShortURLStr(String, int)'
     */
    public void testGetShortURLStr() {
        assertTrue(77 >= UtilsString.getShortURLStr("http://www.lubw.baden-wuerttemberg.de/servlet/is/13512/abbruch_von_gebaeuden.pdf?command=downloadContent&filename=abbruch_von_gebaeuden.pdf", 77).length());
        assertTrue(81 == UtilsString.getShortURLStr("http://www.lubw.baden-wuerttemberg.de/servlet/is/13512/abbruch_von_gebaeuden.pdf?", 81).length());
        assertTrue(81 == UtilsString.getShortURLStr("http://www.lubw.baden-wuerttemberg.de/servlet/is/13512/abbruch_von_gebaeuden.pdf?", 82).length());
        assertTrue(80 == UtilsString.getShortURLStr("http://www.lubw.baden-wuerttemberg.de/servlet/is/13512/abbruch_von_gebaeuden.pdf", 80).length());
        // results to http://www.lubw.baden-wuerttemberg.de/servlet/is/13512/abbruch_von_gebaeuden.pdf, because no query is appended 
        assertTrue(80 == UtilsString.getShortURLStr("http://www.lubw.baden-wuerttemberg.de/servlet/is/13512/abbruch_von_gebaeuden.pdf?", 80).length());
        assertTrue(79 >= UtilsString.getShortURLStr("http://www.lubw.baden-wuerttemberg.de/servlet/is/13512/abbruch_von_gebaeuden.pdf?", 79).length());
    }
    
    public void testStripTags() {
        assertEquals("hallo portal .", UtilsString.stripHTMLTags("hallo <b>portal </u>."));
        assertEquals("hallo portal .", UtilsString.stripHTMLTags("hallo <b>portal </u\n\n>."));
    }
    
    public void testStripHTMLTagsAndHTMLEncode() {
        assertEquals("hallo &amp; portal &reg;&euro;&copy;&reg;.", UtilsString.stripHTMLTagsAndHTMLEncode("hallo & <b>portal &reg;€©®</u>."));
    }

}
