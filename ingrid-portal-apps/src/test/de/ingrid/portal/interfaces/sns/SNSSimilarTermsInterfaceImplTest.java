/*
 * Copyright (c) 1997-2006 by wemove GmbH
 */
package de.ingrid.portal.interfaces.sns;

import de.ingrid.iplug.sns.utils.DetailedTopic;
import de.ingrid.iplug.sns.utils.Topic;
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
import junit.framework.TestCase;

public class SNSSimilarTermsInterfaceImplTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    /*
     * Test method for 'de.ingrid.portal.interfaces.impl.SNSSimilarTermsInterfaceImpl.getTopicsFromText(String, String)'
     */
    public void testGetTopicsFromText() throws Exception {
        IngridQuery query = QueryStringParser.parse("frankfurt");
        query.addField(new FieldQuery(true, false, "datatype", IDataTypes.SNS));
        query.addField(new FieldQuery(true, false, "filter", "/event"));
        query.putInt(Topic.REQUEST_TYPE, Topic.TOPIC_FROM_TEXT);

        IBUSInterface iBus = IBUSInterfaceImpl.getInstance();
        IngridHits hits = iBus.search(query, 10, 1, 10, 30000);
        IngridHit[] hitsArray = hits.getHits();
        assertNotNull(hitsArray);
        showHits(hitsArray);
        IngridHitDetail[] details = iBus.getDetails(hitsArray, query, new String[0]);
        assertNotNull(details);
        showDetails(details);
        assertTrue(hitsArray.length > 0);
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
