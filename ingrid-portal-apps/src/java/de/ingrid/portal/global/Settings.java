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

    /** Search parameters */
    public final static int HITS_PER_PAGE = 10;
    public final static int NUM_SELECTOR_PAGES = 5;

    /** Message Topics: define the message "scope", will be prefix of message key -> "topic:message" */
    public final static String MSG_TOPIC_SERVICE = "service";

    /** Messages: define the message itself, will be suffix of message key -> "topic:message" */
    public static final String MSG_QUERY = "query";

}
