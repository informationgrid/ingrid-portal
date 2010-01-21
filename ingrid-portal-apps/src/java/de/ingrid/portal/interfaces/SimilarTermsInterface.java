/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.interfaces;

import java.util.Locale;

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
     * @param language in which language, pass null if default language
     * @return Array of similar topics for term.
     */
    IngridHit[] getSimilarTerms(String term, Locale language);

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
     * @param language in which language, pass null if default language
     * @return Array of topics found for term.
     */
    IngridHit[] getTopicsFromText(String term, String filter, Locale language);

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
     * @return Array of similar topics for topic.
     */
    IngridHit[] getTopicSimilarLocationsFromTopic(String topicId);

    /**
     * Get associated topics from a given topic id.
     * 
     * @param term
     *            The given topic id to search similar topics for.
     * @param language in which language, pass null if default language
     * @return Array of associated topics for topic.
     */
    IngridHit[] getTopicsFromTopic(String topicId, Locale language);

    /**
     * Get detail from hit.
     * 
     * @param hit The hit to get the detail from.
     * @param filter The filter to apply to the query (i.e. "/location" or "/thesa")
     * @param language in which language, pass null if default language
     * @return The detail of the hit.
     */
    IngridHitDetail getDetailsTopic(IngridHit hit, String filter, Locale language);

    
    /**
     * Get a hierarchy of topics from a given topic id.
     * 
     * @param topicId
     *            The given topic id that serves as a root topic. "toplevel" serves as the root
     * @param includeSiblings
     *            "true" or "false", if siblings should be included in the hierarchy
     * @param association
     *            "narrowerTermAssoc" is the only supported association at the moment
     * @param depth
     *            Search depth.
     * @param direction
     *            "up" or "down", search direction.
     * @param language in which language, pass null if default language
     * @return Array representing the resulting hierarchy.
     */
    IngridHit[] getHierarchy(String topicId, String includeSiblings, String association,
    		String depth, String direction, Locale language);
}
