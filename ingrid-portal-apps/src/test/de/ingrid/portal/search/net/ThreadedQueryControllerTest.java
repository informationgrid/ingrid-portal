package de.ingrid.portal.search.net;

import java.util.HashMap;

import junit.framework.TestCase;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.ParseException;
import de.ingrid.utils.queryparser.QueryStringParser;
import de.ingrid.portal.search.net.ThreadedQueryController;

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
            controller.setTimeout(10000);
            controller.addQuery("2", qd);
            
            HashMap map = controller.search();
            assertEquals("expected hashmap size: 2, actual size: " + map.size(), 2, map.size());
            assertNotNull("map entry is null for map entry 1", map.get("1"));
            assertNotNull("map entry is null for map entry 2", map.get("2"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

}
