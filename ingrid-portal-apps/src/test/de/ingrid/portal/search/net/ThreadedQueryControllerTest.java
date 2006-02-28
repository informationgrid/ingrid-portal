package de.ingrid.portal.search.net;

import java.util.HashMap;

import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.ParseException;
import de.ingrid.utils.queryparser.QueryStringParser;
import junit.framework.TestCase;

public class ThreadedQueryControllerTest extends TestCase {

    ThreadedQueryController controller;
    
    protected void setUp() throws Exception {
        super.setUp();
        controller = new ThreadedQueryController();
    }

    /*
     * Test method for 'de.ingrid.portal.search.net.ThreadedQueryController.search()'
     */
    public void testSearch() {
        
        controller.clear();
        try {
            IngridQuery query = QueryStringParser.parse("wasser ranking:score");
            QueryDescriptor qd = new QueryDescriptor(query, 10, 1, 10, 5000, true, null);
            controller.addQuery("1", qd);
            query = QueryStringParser.parse("wasser ranking:off");
            qd = new QueryDescriptor(query, 10, 1, 10, 5000, true, null);
            controller.addQuery("2", qd);
            
            HashMap map = controller.search();
            assertEquals(2, map.size());
            assertNotNull(map.get("1"));
            assertNotNull(map.get("2"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

}
