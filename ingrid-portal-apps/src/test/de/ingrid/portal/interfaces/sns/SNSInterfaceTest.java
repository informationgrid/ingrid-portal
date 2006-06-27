package de.ingrid.portal.interfaces.sns;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;
import de.ingrid.iplug.sns.utils.DetailedTopic;
import de.ingrid.iplug.sns.utils.Topic;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.UtilsDB;
import de.ingrid.portal.interfaces.IBUSInterface;
import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.query.FieldQuery;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.IDataTypes;
import de.ingrid.utils.queryparser.QueryStringParser;

public class SNSInterfaceTest extends TestCase {

    static int HITS_PER_PAGE = 10;

    static int CURRENT_PAGE = 1;

    static int TIMEOUT = 10000;

    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testGetAnniversaries() throws Exception {
        System.out.println("########## testGetAnniversaries()");

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        //            cal.set(Calendar.DATE, 15);
        //            cal.set(Calendar.MONTH, 2);
        cal.add(Calendar.DATE, 1);
        Date queryDate = cal.getTime();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        //        String dateStr = df.format(queryDate);
        String dateStr = "2006-04-13";

        IngridQuery query = QueryStringParser.parse(dateStr);
        query.addField(new FieldQuery(true, false, Settings.QFIELD_DATATYPE, IDataTypes.SNS));
        query.putInt(Topic.REQUEST_TYPE, Topic.ANNIVERSARY_FROM_TOPIC);

        IBUSInterface iBus = IBUSInterfaceImpl.getInstance();

        IngridHits hits = iBus.search(query, HITS_PER_PAGE, CURRENT_PAGE, HITS_PER_PAGE, TIMEOUT);
        IngridHit[] hitsArray = hits.getHits();
        assertNotNull(hitsArray);
        showHits(hitsArray);
        IngridHitDetail[] details = iBus.getDetails(hitsArray, query, new String[0]);
        assertNotNull(details);
        showDetails(details);
    }

    public void testEVENTS_FROM_TERM() throws Exception {
        System.out.println("########## testEVENTS_FROM_TERM()");
        String term = "Tschernobyl";
        System.out.println("TERM = " + term);
        IngridQuery query = QueryStringParser.parse(term);
        query.addField(new FieldQuery(true, false, Settings.QFIELD_DATATYPE, IDataTypes.SNS));
        query.putInt(Topic.REQUEST_TYPE, Topic.EVENT_FROM_TOPIC);

        IBUSInterface ibus = IBUSInterfaceImpl.getInstance();
        IngridHits hits = ibus.search(query, HITS_PER_PAGE, CURRENT_PAGE, HITS_PER_PAGE, TIMEOUT);
        IngridHit[] hitsArray = hits.getHits();
        assertNotNull(hitsArray);
        showHits(hitsArray);
        IngridHitDetail[] details = ibus.getDetails(hitsArray, query, new String[0]);
        assertNotNull(details);
        showDetails(details);
    }

    public void testEVENTS_FROM_TYPE() throws Exception {
        System.out.println("########## testEVENTS_FROM_TYPE()");
        //                String term = "Tschernobyl";
        String term = "";
        IngridQuery query = QueryStringParser.parse(term);
        //        IngridQuery query = new IngridQuery();
        query.addField(new FieldQuery(true, false, "datatype", IDataTypes.SNS));
        query.putInt(Topic.REQUEST_TYPE, Topic.EVENT_FROM_TOPIC);

        String[] eventTypes = new String[]{"industrialAccident"};
        query.put("eventtype", eventTypes);

        // fix, if no term, date has to be set !!!!?????
//                query.put("t0", "3000-01-01");
//                query.put("t1", "1900-01-01");
        query.put("t2", "3000-01-01");

        IBUSInterface ibus = IBUSInterfaceImpl.getInstance();
        IngridHits hits = ibus.search(query, HITS_PER_PAGE, CURRENT_PAGE, HITS_PER_PAGE, TIMEOUT);
        IngridHit[] hitsArray = hits.getHits();
        assertNotNull(hitsArray);
        showHits(hitsArray);
        IngridHitDetail[] details = ibus.getDetails(hitsArray, query, new String[0]);
        assertNotNull(details);
        showDetails(details);
    }

    public void testEVENTS_FROM_TYPE_AND_TERM() throws Exception {
        System.out.println("########## testEVENTS_FROM_TYPE_AND_TERM()");
        String term = "Tschernobyl";
        String[] eventTypes = new String[]{"industrialAccident"};
        System.out.println("TERM = " + term);
        System.out.println("TYPE = " + eventTypes);
        IngridQuery query = QueryStringParser.parse(term);
        query.addField(new FieldQuery(true, false, Settings.QFIELD_DATATYPE, IDataTypes.SNS));
        query.putInt(Topic.REQUEST_TYPE, Topic.EVENT_FROM_TOPIC);
        query.put(Settings.QFIELD_EVENT_TYPE, eventTypes);

        IBUSInterface ibus = IBUSInterfaceImpl.getInstance();
        IngridHits hits = ibus.search(query, HITS_PER_PAGE, CURRENT_PAGE, HITS_PER_PAGE, TIMEOUT);
        IngridHit[] hitsArray = hits.getHits();
        assertNotNull(hitsArray);
        showHits(hitsArray);
        IngridHitDetail[] details = ibus.getDetails(hitsArray, query, new String[0]);
        assertNotNull(details);
        showDetails(details);
    }

    protected void showDetails(IngridHitDetail[] details) {
        if (details.length > 0) {
            for (int i = 0; i < details.length; i++) {
                assertTrue(details[i] instanceof DetailedTopic);
                DetailedTopic detail = (DetailedTopic) details[i];
                System.out.println("--------");
                System.out.println("topicName:" + detail.getTopicName());
                System.out.println("topicID:" + detail.getTopicID());
                System.out.println("from:" + detail.getFrom());
                System.out.println("to:" + detail.getTo());
                System.out.println("type:" + detail.getType());
                System.out.println("administrativeID:" + detail.getAdministrativeID());
                System.out.println("associatedTermsOcc (suchbegriffe):" + detail.get(DetailedTopic.ASSOCIATED_OCC));
                System.out.println("sampleOcc (Internetverweis):" + detail.get(DetailedTopic.SAMPLE_OCC));
                System.out.println("descriptionOcc:" + detail.get(DetailedTopic.DESCRIPTION_OCC));
                System.out.println("href:" + detail.getArrayList(DetailedTopic.INSTANCE_OF));
            }
        } else {
            System.out.println("!!!!!!!!!!!!!!!! NO DETAILS FOUND");
        }
    }

    protected void showHits(IngridHit[] hitsArray) {
        if (hitsArray.length == 0) {
            System.out.println("!!!!!!!!!!!!!!!! NO HITS FOUND");
        } else {
            for (int i = 0; i < hitsArray.length; i++) {
                Topic hit = (Topic) hitsArray[i];
                System.out.println(hit.getTopicName() + ":" + hit.getTopicID());
            }
        }
    }

}
