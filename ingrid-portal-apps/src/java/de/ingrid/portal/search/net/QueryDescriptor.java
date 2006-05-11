/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.search.net;

import de.ingrid.utils.query.IngridQuery;

/**
 * Describes a query that should be performed over the ibus. At the 
 * moment only search queries (with optional detail fetching) are supported. 
 *
 * @author joachim@wemove.com
 */
public class QueryDescriptor {

    IngridQuery query;

    int hitsPerPage;

    int currentPage;

    int startHit;

    int timeout;

    boolean getDetails;

    boolean getPlugDescription;

    String[] requestedFields;

    /**
     * Contructor.
     * 
     * @param query
     * @param hitsPerPage
     * @param currentPage
     * @param startHit
     * @param timeout
     * @param getDetails
     * @param getPlugDescription
     * @param requestedFields
     */
    public QueryDescriptor(IngridQuery query, int hitsPerPage, int currentPage, int requestedHits, int timeout,
            boolean getDetails, boolean getPlugDescription, String[] requestedFields) {
        super();
        this.query = query;
        this.hitsPerPage = hitsPerPage;
        this.currentPage = currentPage;
        this.startHit = requestedHits;
        this.timeout = timeout;
        this.getDetails = getDetails;
        this.getPlugDescription = getPlugDescription;
        this.requestedFields = requestedFields;
    }

    /**
     * @return Returns the currentPage.
     */
    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * @param currentPage The currentPage to set.
     */
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    /**
     * @return Returns the getDetails.
     */
    public boolean isGetDetails() {
        return getDetails;
    }

    /**
     * @param getDetails The getDetails to set.
     */
    public void setGetDetails(boolean getDetails) {
        this.getDetails = getDetails;
    }

    /**
     * @return Returns the getPlugDescription.
     */
    public boolean isGetPlugDescription() {
        return getPlugDescription;
    }

    /**
     * @param getPlugDescription The getPlugDescription to set.
     */
    public void setGetPlugDescription(boolean getPlugDescription) {
        this.getPlugDescription = getPlugDescription;
    }

    /**
     * @return Returns the hitsPerPage.
     */
    public int getHitsPerPage() {
        return hitsPerPage;
    }

    /**
     * @param hitsPerPage The hitsPerPage to set.
     */
    public void setHitsPerPage(int hitsPerPage) {
        this.hitsPerPage = hitsPerPage;
    }

    /**
     * @return Returns the query.
     */
    public IngridQuery getQuery() {
        return query;
    }

    /**
     * @param query The query to set.
     */
    public void setQuery(IngridQuery query) {
        this.query = query;
    }

    /**
     * @return Returns the requestedFields for detail querying.
     */
    public String[] getRequestedFields() {
        return requestedFields;
    }

    /**
     * @param requestedFields The requestedFields to set for detail querying.
     */
    public void setRequestedFields(String[] requestedFields) {
        this.requestedFields = requestedFields;
    }

    /**
     * @return Returns the startHit.
     */
    public int getStartHit() {
        return startHit;
    }

    /**
     * @param startHit The startHit to set.
     */
    public void setStartHit(int startHit) {
        this.startHit = startHit;
    }

    /**
     * @return Returns the timeout.
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * @param timeout The timeout to set.
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

}
