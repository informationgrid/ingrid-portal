/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.interfaces.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.iplug.sns.utils.DetailedTopic;
import de.ingrid.iplug.sns.utils.Topic;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.interfaces.IBUSInterface;
import de.ingrid.portal.interfaces.SNSInterface;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.query.FieldQuery;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.IDataTypes;
import de.ingrid.utils.queryparser.QueryStringParser;

/**
 * TODO Describe your created type (class, etc.) here.
 *
 * @author joachim@wemove.com
 */
public class SNSInterfaceImpl implements SNSInterface {

    private final static Log log = LogFactory.getLog(SNSInterfaceImpl.class);

    private static SNSInterface instance = null;
    
    public static synchronized SNSInterface getInstance() {
        if (instance == null) {
            try {
                instance = new SNSInterfaceImpl();
            } catch (Exception e) {
                log.fatal("Error initiating the WMS interface.");
                e.printStackTrace();
            }
        }
        
        return instance;
    }
    
    private SNSInterfaceImpl() throws Exception {
        super();
    }    
    
    /**
     * @see de.ingrid.portal.interfaces.SNSInterface#getAnniversary(java.sql.Date)
     */
    public DetailedTopic getAnniversary(Date d) {
        
        SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd" );
        String dateStr = df.format( d );
        
        // ask ibus for anniversary
        IngridQuery query;
        try {
            query = QueryStringParser.parse(dateStr);
            query.addField(new FieldQuery(true, false, Settings.QFIELD_DATATYPE, IDataTypes.SNS));
            query.putInt(Topic.REQUEST_TYPE, Topic.ANNIVERSARY_FROM_TOPIC);
            
            IBUSInterface iBus = IBUSInterfaceImpl.getInstance();

            IngridHits hits = iBus.search(query, 10, 1, 10, 10000);
            IngridHit[] hitsArray = hits.getHits();
            if (hitsArray.length > 0) {
                int entry = (int)(Math.random() * hitsArray.length);
                return (DetailedTopic)iBus.getDetails(hitsArray[entry], query);
            }
            // TODO implement fallback
        } catch (Exception e) {
            log.error("Exception while querying sns for anniversary.", e);
            return null;
        }
        
        return null;
    }
}
