/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.interfaces.ibus;

import org.apache.commons.configuration.Configuration;

import de.ingrid.iplug.PlugDescription;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.dsc.Record;
import de.ingrid.utils.query.IngridQuery;

/**
 * TODO Describe your created type (class, etc.) here.
 *
 * @author joachim@wemove.com
 */
public interface IBUSInterface {

    /**
     * Returns the Configuration of the service.
     * 
     * @return The Configuration of the Service
     */
    Configuration getConfig();
    
    
    /**
     * @param query
     * @param hitsPerPage
     * @param currentPage
     * @param requestedHits
     * @param timeout
     * @return
     * @throws Exception 
     */
    IngridHits search(IngridQuery query, int hitsPerPage, int currentPage, int requestedHits, int timeout) throws Exception;
    

    /**
     * @param result
     * @param query
     * @return
     * @throws Exception 
     */
    IngridHitDetail getDetails(IngridHit result, IngridQuery query) throws Exception;
    
    /**
     * @param hit
     * @return
     * @throws Exception 
     */
    Record getRecord(IngridHit hit) throws Exception;


    /**
     * @param plugId
     * @return
     */
    PlugDescription getIPlug(String plugId);
    
}
