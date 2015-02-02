/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2015 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
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
     * @param term The given topic id to search similar topics for.
     * @param language in which language, pass null if default language
     * @return Array of similar topics for topic.
     */
    IngridHit[] getTopicSimilarLocationsFromTopic(String topicId, Locale language);

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
    
    /**
     * Get a topic name by given topic id and language.
     * 
     * @param topicId
     * 			The given topic id that serves as a root topic
     * @param language
     * 			in which language, pass null if default language
     * @return
     */
    IngridHit[] getTopicFromID(String topicId, Locale language);
}
