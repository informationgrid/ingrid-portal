/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.interfaces;

import org.apache.commons.configuration.Configuration;

import de.ingrid.iplug.PlugDescription;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.dsc.Column;
import de.ingrid.utils.dsc.Record;
import de.ingrid.utils.query.IngridQuery;

/**
 * Defines the interface to be implemented to communicate with the 
 * iBus.
 *
 * @author joachim@wemove.com
 */
public interface IBUSInterface {

    /**
     * Returns the Configuration of the ibus.
     * 
     * @return The Configuration of the ibus.
     */
    Configuration getConfig();

    /**
     * Performs a search on the iBus. Returns hits with basic properties (not quite usable 
     * for rendering). Get more details with getDetails() and getRecord(). 
     * 
     * @param query The IngridQuery to search for.
     * @param hitsPerPage The number of hits per page.
     * @param currentPage The current page requested.
     * @param requestedHits The number of requested hits.
     * @param timeout The timeout for this query.
     * @return The IngridHits containing the hits of this query.
     * @throws Exception 
     */
    IngridHits search(IngridQuery query, int hitsPerPage, int currentPage, int requestedHits, int timeout)
            throws Exception;

    /**
     * Returns details for a single IngridHit. Details are mainly used
     * to render the search result entries (title, summary, etc.).
     * 
     * @param hit The IngridHit to get the details from.
     * @param query The IngridQuery the hit was found with.
     * @return The IngridHitDetail with details about the hit OR NULL
     */
    IngridHitDetail getDetails(IngridHit hit, IngridQuery query);

    /**
     * Returns more information than getDetails about the hit. This is mainly used
     * to render the detailed information of a DSC hit like UDK details.
     * 
     * @param hit The IngridHit to get the record from.
     * @return The Record with detailed data of the hit OR NULL
     */
    Record getRecord(IngridHit hit);

    /**
     * Returns the PlugDescription of the iPlug with the id plugId. The 
     * PlugDescription contains detailed information about the iPlug.
     * 
     * @param plugId The id of the iPlug.
     * @return The PlugDescription. 
     */
    PlugDescription getIPlug(String plugId);

    /**
     * Get Plug Description of a result.
     * @param hit
     * @return The Plug Description OR NULL
     */
    PlugDescription getIPlug(IngridHit hit);

    /**
     * Transfer commonly used detail parameters from detail object to hitobject.
     * @param hit
     * @param detail
     */
    void transferHitDetails(IngridHit hit, IngridHitDetail detail);

    /**
     * Transfer commonly used plug parameters from plug description to hitobject.
     * @param hit
     * @param plug
     */
    void transferPlugDetails(IngridHit hit, PlugDescription plug);

    /**
     * Fetch column from Record.
     * @param record
     * @param columnName
     * @return The Column OR NULL
     */
    Column getColumn(Record record, String columnName);
}
