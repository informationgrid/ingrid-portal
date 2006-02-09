/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.interfaces;

import java.util.HashMap;

import de.ingrid.iplug.sns.utils.DetailedTopic;
import de.ingrid.iplug.sns.utils.Topic;
import de.ingrid.utils.IngridHitDetail;

/**
 * TODO Describe your created type (class, etc.) here.
 *
 * @author joachim@wemove.com
 */
public interface SNSInterface {

    
    /**
     * Returns the Anniversary of a given Date. This method uses a fallback strategy 
     * of no aniversary was found for a particular day, month. It enlarges the search
     * in two steps:
     * 
     *  1.) +- 7 days
     *  2.) +- 30 days
     * 
     * @param date The date to search an anniverary for.
     * @return The DetailedTopic representing the anniversary or null if no anniversary was found.
     */
    DetailedTopic getAnniversary(java.util.Date date);
    
}
