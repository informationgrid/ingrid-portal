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

import junit.framework.TestCase;

public class QueryStringUtilTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    /*
     * Test method for 'de.ingrid.portal.global.UtilsQueryString.replaceTerm(String, String, String)'
     */
    public void testReplaceTerm() {
        String result;
        result = UtilsQueryString.replaceTerm("Wasser", "wasser", "(wasser OR wasserspiegel)");
        assertEquals("(wasser OR wasserspiegel)", result);
        result = UtilsQueryString.replaceTerm("Wasser AND \"Wasser Aufbereitung\"", "wasser", "(wasser OR wasserspiegel)");
        assertEquals("(wasser OR wasserspiegel) AND \"Wasser Aufbereitung\"", result);
        result = UtilsQueryString.replaceTerm("(Wasser OR Boden) AND \"Wasser Aufbereitung\"", "wasser", "(wasser OR wasserspiegel)");
        assertEquals("((wasser OR wasserspiegel) OR Boden) AND \"Wasser Aufbereitung\"", result);
        result = UtilsQueryString.replaceTerm("(\"Wasser\" OR Boden) AND \"Wasser Aufbereitung\"", "wasser", "(wasser OR wasserspiegel)");
        assertEquals("((wasser OR wasserspiegel) OR Boden) AND \"Wasser Aufbereitung\"", result);
    }
    
    public void testStripQueryWhitespace() {
        String result;
        result = UtilsQueryString.stripQueryWhitespace("(name)");
        assertEquals("name", result);
        result = UtilsQueryString.stripQueryWhitespace("((name))");
        assertEquals("name", result);
        result = UtilsQueryString.stripQueryWhitespace("((name des schl\u00fcssels))");
        assertEquals("(name des schl\u00fcssels)", result);
        result = UtilsQueryString.stripQueryWhitespace("((name des) OR schl\u00fcssels))");
        assertEquals("((name des) OR schl\u00fcssels))", result);
    }

    public void testEscapeQueryToken() {
        assertEquals("abcde-33445545", UtilsQueryString.normalizeUuid("{abcde-33445545}"));
        assertEquals("APA 2009", UtilsQueryString.normalizeUuid("APA 2009"));
    }
    
}
