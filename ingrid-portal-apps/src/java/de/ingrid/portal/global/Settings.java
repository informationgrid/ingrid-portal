/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.global;

/**
 * Common resources used by our Portlet Application
 *
 * @author Martin Maidhof
 */
public class Settings {

    // ************************************
    // Search
    // ************************************

    /** Search parameters 
     * number of ranked hits per page */
    public final static int RANKED_HITS_PER_PAGE = 10;

    /** number of unranked hits per page */
    public final static int UNRANKED_HITS_PER_PAGE = 10;

    /** number of ranked pages displayed for selection ("selector pages") */
    public final static int RANKED_NUM_PAGES_TO_SELECT = 5;

    /** number of unranked pages displayed for selection ("selector pages") */
    public final static int UNRANKED_NUM_PAGES_TO_SELECT = 3;

    // ************************************
    // Portlet Messaging
    // ************************************
    /** dummy value for a message just to set a value for a message */
    public static final String MSG_VALUE_TRUE = "";

    /** Message Topics: define the message "scope", will be prefix in message key -> "topic:message" 
     * set "scope" service page */
    public final static String MSG_TOPIC_SERVICE = "service";

    /** Messages: define the message itself, will be suffix in message key -> "topic:message" 
     * this message contains the ingrid query */
    public static final String MSG_QUERY = "query";

    /** this message indicates that a new query was performed to ignore former render parameters */
    public static final String MSG_NEW_QUERY = "new_query";
}
