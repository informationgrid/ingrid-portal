package de.ingrid.portal.search;

import de.ingrid.utils.query.ClauseQuery;
import de.ingrid.utils.query.FieldQuery;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.query.TermQuery;
import de.ingrid.utils.queryparser.ParseException;
import de.ingrid.utils.queryparser.QueryStringParser;
import junit.framework.TestCase;

public class IngridQueryUtilsTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }
    
    public void testRenderQuery() throws ParseException {
        
        IngridQuery q = QueryStringParser.parse("(gemüse OR garten T02:test) wemove T01:alles datatype:default ranking:score");
        String qStr = UtilsSearch.queryToString(q);
        assertEquals("wemove (gemüse OR garten t02:test) t01:alles datatype:default ranking:score", qStr);
        
    }

}
