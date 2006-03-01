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
    public final static int SEARCH_RANKED_HITS_PER_PAGE = 10;

    /** number of unranked hits per page */
    public final static int SEARCH_UNRANKED_HITS_PER_PAGE = 10;

    /** number of ranked pages displayed for selection ("selector pages") */
    public final static int SEARCH_RANKED_NUM_PAGES_TO_SELECT = 5;

    /** number of unranked pages displayed for selection ("selector pages") */
    public final static int SEARCH_UNRANKED_NUM_PAGES_TO_SELECT = 3;

    /** datasource values submitted from template (view) */
    public final static String SEARCH_DATASOURCE_ENVINFO = "1";

    public final static String SEARCH_DATASOURCE_ADDRESS = "2";

    public final static String SEARCH_DATASOURCE_RESEARCH = "3";

    public final static String SEARCH_INITIAL_DATASOURCE = SEARCH_DATASOURCE_ENVINFO;

    public final static int SEARCH_DEFAULT_TIMEOUT = 5000;

    // ============= definitions for IngridQuery ==============================

    // ------------- query field names -----------------------------

    public final static String QFIELD_DATATYPE = "datatype";

    public final static String QFIELD_PARTNER = "partner";

    // first category in all catalogue pages
    public final static String QFIELD_TOPIC = "topic";

    // second category in environment topics catalogue page !
    public final static String QFIELD_FUNCT_CATEGORY = "funct_category";

    // ------------- query field values -----------------------------

    // datatypes
    public final static String QVALUE_DATATYPE_SERVICE = "www_service";

    public final static String QVALUE_DATATYPE_MEASURES = "www_measures";

    public final static String QVALUE_DATATYPE_ENVTOPIC = "www_topic";

    public final static String QVALUE_DATATYPE_ENVINFO = "default";

    // TODO: doesn't work yet !!! we use explicit address iPlug as datytpe !
    //    public final static String QVALUE_DATATYPE_ADDRESS = "address";
    public final static String QVALUE_DATATYPE_ADDRESS = "dsc_ecs_address";

    public final static String QVALUE_DATATYPE_RESEARCH = "research";

    // TODO: at the moment we explicitly handle g2k in IngridQuery, should be handled via "search area" in future
    public final static String QVALUE_DATATYPE_G2K = "g2k";

    // TODO: helper datatype to display nothing, use search areas in future
    public final static String QVALUE_DATATYPE_NORESULTS = "nix";

    // ============= definitions to access Result data (render data) ==============================
    // IF POSSIBLE, USE THE KEYS TO ACCESS HIT DATA ALSO AS KEYS FOR RESULT DATA !!! 

    public final static String RESULT_KEY_TITLE = "title";

    public final static String RESULT_KEY_ABSTRACT = "abstract";

    public final static String RESULT_KEY_PARTNER = "partner";

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

    public final static String HIT_KEY_ADDRESS_CLASS = "T02_address.typ";

    // ************************************
    // Portlet Messaging
    // ************************************
    /** dummy value for a message just to set a value for a message */
    public static final String MSG_VALUE_TRUE = "1";

    // ------------- MESSAGE TOPICS (SCOPES) -----------------------------
    /** Message Topics: define the message "scope", will be "topic-prefix" in message key -> "topic:message" */

    /** set message "scope" service page */
    public final static String MSG_TOPIC_SERVICE = "service";

    /** set message "scope" measures page */
    public final static String MSG_TOPIC_MEASURES = "measures";

    /** set message "scope" measures page */
    public final static String MSG_TOPIC_ENVIRONMENT = "environment";

    /** message "scope" for search pages (start page, simple search, extended search ...) */
    public final static String MSG_TOPIC_SEARCH = "search";

    // ------------- DATA MESSAGES -----------------------------
    /** Messages: define the message itself, will be "message-suffix" in message key -> "topic:message" */

    /** this message contains the ingrid query (an IngridQuery object) */
    public static final String MSG_QUERY = "query";

    /** this message contains the ingrid query STRING */
    public static final String MSG_QUERY_STRING = "query_string";

    /** this message contains the ingrid datasource */
    public static final String MSG_DATASOURCE = "datasource";

    /** this message contains the start hit of the ranked search results (which result page) */
    public static final String MSG_STARTHIT_RANKED = "starthit_ranked";

    /** this message contains the start hit of the unranked search results (which result page) */
    public static final String MSG_STARTHIT_UNRANKED = "starthit_unranked";

    /** this message contains ranked search results */
    public static final String MSG_SEARCH_RESULT_RANKED = "search_result_ranked";

    /** this message contains unranked search results */
    public static final String MSG_SEARCH_RESULT_UNRANKED = "search_result_unranked";

    // ------------- INFORMATION MESSAGES -----------------------------

    /** this message indicates that a new query was explicitly triggered by the search form,
     * so query states should be ignored (e.g. position of result page) 
     * This message is reset when an action in the result portlet causes a new
     * query, which then is no initial query anymore (e.g. change result page).
     */
    public static final String MSG_NEW_QUERY = "new_query";

    /** this message indicates that a query should not be performed.
     * This message is set when an action on the search_result page is triggered that
     * does not require a requery of the ibus (e.g. similar terms)
     * This message is reset when the result page has been rendered.
     */
    public static final String MSG_NO_QUERY = "no_query";
}
