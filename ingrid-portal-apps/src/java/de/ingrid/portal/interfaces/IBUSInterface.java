/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.portal.interfaces;

import de.ingrid.utils.*;
import de.ingrid.utils.dsc.Record;
import de.ingrid.utils.query.IngridQuery;
import org.apache.commons.configuration.Configuration;

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
     * @param startHit The startHit for a new grouped(!) query.
     * @param timeout The timeout for this query.
     * @return The IngridHits containing the hits of this query.
     * @throws Exception 
     */
    IngridHits search(IngridQuery query, int hitsPerPage, int currentPage, int startHit, int timeout)
            throws Exception;

    /**
     * Performs a search on the iBus. Returns hits with basic properties (not quite usable 
     * for rendering). Get more details with getDetails() and getRecord(). 
     * 
     * @param query The IngridQuery to search for.
     * @param hitsPerPage The number of hits per page.
     * @param currentPage The current page requested.
     * @param startHit The startHit for a new grouped(!) query.
     * @param timeout The timeout for this query.
     * @param requestedFields are the requested fields
     * @return The IngridHits containing the details of this query in each IngridHit.
     * @throws Exception 
     */
    IngridHits searchAndDetail(IngridQuery query, int hitsPerPage, int currentPage, int startHit, int timeout, String[] reqParameter)
    	            throws Exception;
    
    /**
     * Returns details for a single IngridHit. Details are mainly used
     * to render the search result entries (title, summary, etc.).
     * 
     * @param hit The IngridHit to get the details from.
     * @param query The IngridQuery the hit was found with.
     * @return The IngridHitDetail with details about the hit OR NULL
     */
    IngridHitDetail getDetail(IngridHit hit, IngridQuery query, String[] requestedFields);

    /**
     * Return details for an array of IngridHits.
     * @param hits
     * @param query
     * @param requestedFields
     * @return
     */
    IngridHitDetail[] getDetails(IngridHit[] hits, IngridQuery query, String[] requestedFields);

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
     * Returns the PlugDescriptions of all iPlugs.
     * 
     * @return
     */
    PlugDescription[] getAllIPlugs();

    /**
     * Returns the PlugDescriptions of all active iPlugs.
     * 
     * @return
     */
    PlugDescription[] getAllActiveIPlugs();
    
    /**
     * Returns the PlugDescriptions of all iPlugs without any time limitation.
     * Also inactive iplugs, that failed the heartbeat will be returned.
     * 
     * @return
     */
    PlugDescription[] getAllIPlugsWithoutTimeLimitation();
    
    
    /**
     * Get the current iBus.
     * 
     * @return
     */
    IBus getIBus();
    
}
