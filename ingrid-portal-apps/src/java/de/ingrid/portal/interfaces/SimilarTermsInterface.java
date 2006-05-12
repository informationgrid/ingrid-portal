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
public interface SimilarTermsInterface {

    /**
     * Get similar topics from a given term.
     * 
     * @param term
     *            The given term to search similar topics for.
     * @return Array of similar topics for term.
     */
    IngridHit[] getSimilarTerms(String term);
    
    
    /**
     * Get similar detailed topics from a given term.
     * 
     * @param term
     *            The given term to search similar topics for.
     * @param hits The hits to fetch details for.
     * @return Array of similar detailed topics for term.
     */
    IngridHitDetail[] getSimilarDetailedTerms(String term, IngridHit[] hits);

    
    /**
     * Get topics from a given term using the autoclassify method of SNS.
     * 
     * @param term The term to classify.
     * @param filter The filter to apply to the query (i.e. "/location" or "/event")
     * @return Array of topics found for term.
     */
    IngridHit[] getTopicsFromText(String term, String filter);
    
    /**
     * Get detailed topics from a given term, the hits and a querytype.
     * 
     * @param term The given term to search similar topics for.
     * @param hits The hits to fetch details for.
     * @param queryType The SNS query type the hits where fetched with.
     * @return Array of detailed topics for term.
     */
    IngridHitDetail[] getDetailedTopics(String term, IngridHit[] hits, int queryType);
    
    
    
    /**
     * Get similar location topics from a given topic id.
     * 
     * @param term
     *            The given topic id to search similar topics for.
     * @return Array of similar topics for term.
     */
    IngridHit[] getTopicSimilarLocationsFromTopic(String topicId);
    
}
