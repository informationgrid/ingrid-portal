package de.ingrid.portal.search.net;

import java.util.HashMap;

import junit.framework.TestCase;
import de.ingrid.utils.query.FieldQuery;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.IDataTypes;
import de.ingrid.utils.queryparser.ParseException;
import de.ingrid.utils.queryparser.QueryStringParser;
import de.ingrid.iplug.sns.utils.Topic;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.search.net.ThreadedQueryController;

public class ThreadedQueryControllerCase extends TestCase {

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
//            IngridQuery query = QueryStringParser.parse("datatype:sns sns_request_type:3 2006-03-20");
            IngridQuery query = QueryStringParser.parse("2006-03-20");
            query.addField(new FieldQuery(true, false, Settings.QFIELD_DATATYPE, IDataTypes.SNS));
            query.putInt(Topic.REQUEST_TYPE, Topic.ANNIVERSARY_FROM_TOPIC);
            
            QueryDescriptor qd = new QueryDescriptor(query, 10, 1, 10, 10000, true, null);
            controller.addQuery("1", qd);
            query = QueryStringParser.parse("wasser");
            qd = new QueryDescriptor(query, 10, 1, 10, 10000, true, null);
            controller.setTimeout(1000000);
//            controller.addQuery("2", qd);
            
            HashMap map = controller.search();
            assertEquals("expected hashmap size: 2, actual size: " + map.size(), 2, map.size());
            assertNotNull("map entry is null for map entry 1", map.get("1"));
            assertNotNull("map entry is null for map entry 2", map.get("2"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

}
