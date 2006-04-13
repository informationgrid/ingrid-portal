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
     * @return Array of similar detailed topics for term.
     */
    IngridHitDetail[] getSimilarDetailedTerms(String term, IngridHit[] hits);

}
