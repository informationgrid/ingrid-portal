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

}
