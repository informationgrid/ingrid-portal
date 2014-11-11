/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or â€“ as soon they will be
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
