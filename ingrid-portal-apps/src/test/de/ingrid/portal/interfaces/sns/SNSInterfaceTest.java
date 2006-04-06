package de.ingrid.portal.interfaces.sns;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;
import de.ingrid.iplug.sns.utils.DetailedTopic;
import de.ingrid.iplug.sns.utils.Topic;
import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.global.Settings;
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
        String dateStr = df.format(queryDate);

        IngridQuery query = QueryStringParser.parse(dateStr);
        query.addField(new FieldQuery(true, false, Settings.QFIELD_DATATYPE, IDataTypes.SNS));
        query.putInt(Topic.REQUEST_TYPE, Topic.ANNIVERSARY_FROM_TOPIC);

        IBUSInterface iBus = IBUSInterfaceImpl.getInstance();

        IngridHits hits = iBus.search(query, 10, 1, 10, 10000);
        IngridHit[] hitsArray = hits.getHits();
        assertNotNull(hitsArray);
        IngridHitDetail[] details = iBus.getDetails(hitsArray, query, new String[0]);
        assertNotNull(details);
        if (details.length > 0) {
            for (int i = 0; i < details.length; i++) {
                assertTrue(details[i] instanceof DetailedTopic);
                DetailedTopic detail = (DetailedTopic) details[i];
                System.out.println(detail.getTopicName() + ":" + detail.getTopicID() + ":" + detail.getFrom() + ":"
                        + detail.getTo() + ":" + detail.getAdministrativeID());
            }
        } else {
            System.out.println("!!!!!!!!!!!!!!!! NO ANNIVERSARY FOUND");
        }
    }

    public void testEVENTS_FROM_TERM() throws Exception {
        System.out.println("########## testEVENTS_FROM_TERM()");
        String term = "Tschernobyl";
        System.out.println("TERM = " + term);
        IngridQuery query = QueryStringParser.parse(term);
        query.addField(new FieldQuery(true, false, Settings.QFIELD_DATATYPE, IDataTypes.SNS));
        query.putInt(Topic.REQUEST_TYPE, Topic.EVENT_FROM_TOPIC);

        IBUSInterface ibus = IBUSInterfaceImpl.getInstance();
        IngridHits hits = ibus.search(query, HITS_PER_PAGE, CURRENT_PAGE, HITS_PER_PAGE, PortalConfig.getInstance()
                .getInt(PortalConfig.SNS_TIMEOUT_DEFAULT, 30000));
        IngridHit[] hitsArray = hits.getHits();
        assertNotNull(hitsArray);
        if (hitsArray.length == 0) {
            System.out.println("!!!!!!!!!!!!!!!! NO EVENT WITH TERM FOUND");
        } else {
            for (int i = 0; i < hitsArray.length; i++) {
                Topic hit = (Topic) hitsArray[i];
                System.out.println(hit.getTopicName() + ":" + hit.getTopicID());
            }
        }
    }

    public void testEVENTS_FROM_TYPE() throws Exception {
        System.out.println("########## testEVENTS_FROM_TYPE()");
        String term = "";
        String eventType = "industrialAccident";
        System.out.println("TYPE = " + eventType);
        IngridQuery query = QueryStringParser.parse(term);
        query.addField(new FieldQuery(true, false, Settings.QFIELD_DATATYPE, IDataTypes.SNS));
        query.putInt(Topic.REQUEST_TYPE, Topic.EVENT_FROM_TOPIC);
        query.put(Settings.QFIELD_EVENT_TYPE, eventType);

        IBUSInterface ibus = IBUSInterfaceImpl.getInstance();
        IngridHits hits = ibus.search(query, HITS_PER_PAGE, CURRENT_PAGE, HITS_PER_PAGE, PortalConfig.getInstance()
                .getInt(PortalConfig.SNS_TIMEOUT_DEFAULT, 30000));
        IngridHit[] hitsArray = hits.getHits();
        assertNotNull(hitsArray);
        if (hitsArray.length == 0) {
            System.out.println("!!!!!!!!!!!!!!!! NO EVENT OF TYPE FOUND");
        } else {
            for (int i = 0; i < hitsArray.length; i++) {
                Topic hit = (Topic) hitsArray[i];
                System.out.println(hit.getTopicName() + ":" + hit.getTopicID());
            }
        }
    }

    public void testEVENTS_FROM_TYPE_AND_TERM() throws Exception {
        System.out.println("########## testEVENTS_FROM_TYPE_AND_TERM()");
        String term = "Tschernobyl";
        String eventType = "industrialAccident";
        System.out.println("TERM = " + term);
        System.out.println("TYPE = " + eventType);
        IngridQuery query = QueryStringParser.parse(term);
        query.addField(new FieldQuery(true, false, Settings.QFIELD_DATATYPE, IDataTypes.SNS));
        query.putInt(Topic.REQUEST_TYPE, Topic.EVENT_FROM_TOPIC);
        query.put(Settings.QFIELD_EVENT_TYPE, eventType);

        IBUSInterface ibus = IBUSInterfaceImpl.getInstance();
        IngridHits hits = ibus.search(query, HITS_PER_PAGE, CURRENT_PAGE, HITS_PER_PAGE, PortalConfig.getInstance()
                .getInt(PortalConfig.SNS_TIMEOUT_DEFAULT, 30000));
        IngridHit[] hitsArray = hits.getHits();
        assertNotNull(hitsArray);
        if (hitsArray.length == 0) {
            System.out.println("!!!!!!!!!!!!!!!! NO EVENT OF TYPE AND TERM FOUND");
        } else {
            for (int i = 0; i < hitsArray.length; i++) {
                Topic hit = (Topic) hitsArray[i];
                System.out.println(hit.getTopicName() + ":" + hit.getTopicID());
            }
        }
    }

    public void testEVENT_DETAILS() throws Exception {
        System.out.println("########## testEVENT_DETAILS()");
        String term = "Tschernobyl";
        String eventType = "industrialAccident";
        System.out.println("TERM = " + term);
        System.out.println("TYPE = " + eventType);
        IngridQuery query = QueryStringParser.parse(term);
        query.addField(new FieldQuery(true, false, Settings.QFIELD_DATATYPE, IDataTypes.SNS));
        query.putInt(Topic.REQUEST_TYPE, Topic.EVENT_FROM_TOPIC);
        query.put(Settings.QFIELD_EVENT_TYPE, eventType);

        IBUSInterface ibus = IBUSInterfaceImpl.getInstance();
        IngridHits hits = ibus.search(query, HITS_PER_PAGE, CURRENT_PAGE, HITS_PER_PAGE, PortalConfig.getInstance()
                .getInt(PortalConfig.SNS_TIMEOUT_DEFAULT, 30000));
        IngridHit[] hitsArray = hits.getHits();
        assertNotNull(hitsArray);
        if (hitsArray.length == 0) {
            System.out.println("!!!!!!!!!!!!!!!! NO EVENT OF TYPE AND TERM FOUND");
        } else {
            Topic hit = (Topic) hitsArray[0];
            System.out.println(hit.getTopicName() + ":" + hit.getTopicID());

        }
    }
}
