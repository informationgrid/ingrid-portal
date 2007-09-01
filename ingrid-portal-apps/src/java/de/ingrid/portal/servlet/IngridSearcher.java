/*
 * Copyright (c) 2007 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.servlet;

import java.util.HashMap;

import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.QueryStringParser;

/**
 * Test class for easy searching.
 *
 * @author joachim@wemove.com
 */
public class IngridSearcher {

    public HashMap search(String query, int noHits, int timeout) throws Exception {
        IngridQuery q = QueryStringParser.parse(query);
        
        IngridHits hits = IBUSInterfaceImpl.getInstance().search(q, noHits, 1, 0, timeout);
        IngridHit[] hitArray = hits.getHits();
        IngridHitDetail[] details =  IBUSInterfaceImpl.getInstance().getDetails(hitArray, q, null);
        for (int i = 0; i < hitArray.length; i++) {
            hitArray[i].put("detail", details[i]);
        }
        
        return hits;
    }
    
    public String getClassName() {
        return this.getClass().getName();
    }
    
}
