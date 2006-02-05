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

    // ============= general settings ! ==============================

    /** Search parameters 
     * number of ranked hits per page */
    public final static int RANKED_HITS_PER_PAGE = 10;

    /** number of unranked hits per page */
    public final static int UNRANKED_HITS_PER_PAGE = 10;

    /** number of ranked pages displayed for selection ("selector pages") */
    public final static int RANKED_NUM_PAGES_TO_SELECT = 5;

    /** number of unranked pages displayed for selection ("selector pages") */
    public final static int UNRANKED_NUM_PAGES_TO_SELECT = 3;

    /** the initial datasource ! */
    public final static String INITIAL_DATASOURCE = "1";

    public final static int DEFAULT_SEARCH_TIMEOUT = 2000;

    // ============= definitions for IngridQuery ==============================

    // ------------- field names -----------------------------

    public final static String QFIELD_DATATYPE = "datatype";
    public final static String QFIELD_PARTNER = "partner";
    // first category in all catalogue pages
    public final static String QFIELD_TOPIC = "topic";
    // second category in environment topics catalogue page !
    public final static String QFIELD_FUNCT_CATEGORY = "funct_category";

    // ------------- field values -----------------------------

    // datatypes
    public final static String QVALUE_DATATYPE_WWW = "www";
    public final static String QVALUE_DATATYPE_SERVICE = "www_service";
    public final static String QVALUE_DATATYPE_MEASURES = "www_measures";
    public final static String QVALUE_DATATYPE_ENVTOPIC = "www_topic";
    
    // ============= definitions to access Result data (render data) ==============================

    public final static String RESULT_KEY_TITLE = "title";
    public final static String RESULT_KEY_ABSTRACT = "abstract";
    public final static String RESULT_KEY_TOPIC = "topic";
    public final static String RESULT_KEY_FUNCT_CATEGORY = "funct_category";
    public final static String RESULT_KEY_URL = "url";
    public final static String RESULT_KEY_URL_STR = "url_str";
    public final static String RESULT_KEY_PROVIDER = "provider";
    public final static String RESULT_KEY_SOURCE = "source";
    public final static String RESULT_KEY_TYPE = "type";
    public final static String RESULT_KEY_PLUG_ID = "plugid";
    public final static String RESULT_KEY_DOC_ID = "docid";
    public final static String RESULT_KEY_WMS_URL = "wms_url";
    public final static String RESULT_KEY_UDK_CLASS = "udk_class";

    // ============= definitions to access Hit data (get data from hits) ==============================
    // NOTICE: Define here only special stuff, WHEN YOU CAN'T USE RESULT_KEY_... from above

    public final static String HIT_KEY_WMS_URL = "T011_obj_serv_op_connpoint.connect_point";
    public final static String HIT_KEY_UDK_CLASS = "T01_object.obj_class";


    // ************************************
    // Portlet Messaging
    // ************************************
    /** dummy value for a message just to set a value for a message */
    public static final String MSG_VALUE_TRUE = "";

    // ------------- MESSAGE TOPICS (SCOPES) -----------------------------

    /** Message Topics: define the message "scope", will be "topic-prefix" in message key -> "topic:message" 
     * set message "scope" service page */
    public final static String MSG_TOPIC_SERVICE = "service";

    /** set message "scope" measures page */
    public final static String MSG_TOPIC_MEASURES = "measures";

    /** set message "scope" measures page */
    public final static String MSG_TOPIC_ENVIRONMENT = "environment";

    /** message "scope" for search pages (start page, simple search, extended search ...) */
    public final static String MSG_TOPIC_SEARCH = "search";

    // ------------- DATA MESSAGES -----------------------------

    /** Messages: define the message itself, will be "message-suffix" in message key -> "topic:message" 
     * this message contains the ingrid query (an IngridQuery object) */
    public static final String MSG_QUERY = "query";

    /** this message contains the ingrid query STRING*/
    public static final String MSG_QUERY_STRING = "query_string";

    /** this message contains the ingrid datasource */
    public static final String MSG_DATASOURCE = "datasource";

    // ------------- INFORMATION MESSAGES -----------------------------

    /** this message indicates that a new query was performed to ignore former render parameters
     * we use a separate message for every portlet, because the message is removed by every portlet
     * and we don't know processing order */
    public static final String MSG_NEW_QUERY = "new_query";

    /** new query message for "similar terms" portlet */
    public static final String MSG_NEW_QUERY_FOR_SIMILAR = "new_query_similar";

}
