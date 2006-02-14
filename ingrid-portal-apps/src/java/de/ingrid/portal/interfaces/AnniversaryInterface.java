/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.interfaces;

import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;

/**
 * TODO Describe your created type (class, etc.) here.
 *
 * @author joachim@wemove.com
 */
public interface AnniversaryInterface {

    
    /**
     * Returns all found Anniversary of a given Date. This method uses a fallback strategy 
     * of no aniversary was found for a particular day, month. It enlarges the search
     * with the following fallbacks:
     * 
     *  1.) today
     *  2.) this month
     *  3.) year
     * 
     * @param date  The date to search anniveraries for.
     * @return The DetailedTopic Array representing the anniversaries.
     */
    IngridHitDetail[] getAnniversaries(java.util.Date date);
    
}
