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

    // ===========================================
    // Search general settings
    // ===========================================

    /** number of ranked hits per page */
    public final static int SEARCH_RANKED_HITS_PER_PAGE = 10;

    /** number of unranked hits per page */
    public final static int SEARCH_UNRANKED_HITS_PER_PAGE = 10;

    /** number of ranked pages displayed for selection ("selector pages") */
    public final static int SEARCH_RANKED_NUM_PAGES_TO_SELECT = 5;

    /** number of unranked pages displayed for selection ("selector pages") */
    public final static int SEARCH_UNRANKED_NUM_PAGES_TO_SELECT = 3;

    public final static String SEARCH_INITIAL_DATASOURCE = Settings.PARAMV_DATASOURCE_ENVINFO;

    public final static int SEARCH_DEFAULT_TIMEOUT = 5000;

    // ===========================================
    // PSML pages
    // ===========================================

    /** page for displaying results */
    public final static String PAGE_SEARCH_RESULT = "/ingrid-portal/portal/main-search.psml";

    /** service catalogue page */
    public final static String PAGE_SERVICE = "/ingrid-portal/portal/main-service.psml";

    /** measures catalogue page */
    public final static String PAGE_MEASURES = "/ingrid-portal/portal/main-measures.psml";

    // ===========================================
    // IngridQuery
    // ===========================================

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

    // ===========================================
    // Result data (used in templates for rendering results!)
    // IF POSSIBLE, USE THESE KEYS ALSO TO ACCESS HIT DETAIL DATA !!! 
    // ===========================================

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

    // ===========================================
    // Hit Detail data (get data from hit details)
    // NOTICE: Define here only special stuff, WHEN YOU CAN'T USE RESULT_KEY_... from above
    // ===========================================

    public final static String HIT_KEY_WMS_URL = "T011_obj_serv_op_connpoint.connect_point";

    public final static String HIT_KEY_UDK_CLASS = "T01_object.obj_class";

    public final static String HIT_KEY_ADDRESS_CLASS = "T02_address.typ";

    // ===========================================
    // PORTLET MESSAGING
    // NOTICE: Request Parameter (see next section) ARE USED AS MESSAGE KEYS AND VALUES whenever possible !
    // These messages here are additional messages for logic, caching etc.
    // topics = MSG_TOPIC_...
    // keys = MSG_...
    // values = MSGV_...
    // ===========================================

    // ------------- MESSAGE TOPICS (MSG_TOPIC_...) -----------------------------
    // define the message "scope", will be "prefix" in message key -> "topic:message" 

    /** set message "scope" service page */
    public final static String MSG_TOPIC_SERVICE = "service";

    /** set message "scope" measures page */
    public final static String MSG_TOPIC_MEASURES = "measures";

    /** set message "scope" measures page */
    public final static String MSG_TOPIC_ENVIRONMENT = "environment";

    /** message "scope" for search pages (start page, simple search, extended search ...) */
    public final static String MSG_TOPIC_SEARCH = "search";

    // ------------- DATA MESSAGES: KEYS (MSG_...) -----------------------------
    // define the message itself, will be "suffix" in message key -> "topic:message"

    /** this message contains the central IngridQuery object */
    public static final String MSG_QUERY = "query";

    /** cache for ranked search results */
    public static final String MSG_SEARCH_RESULT_RANKED = "search_result_ranked";

    /** cache for unranked search results */
    public static final String MSG_SEARCH_RESULT_UNRANKED = "search_result_unranked";

    // ------------- INFORMATION MESSAGES: KEYS (MSG_...) AND VALUES ("MSGV_...") -----------------------------

    /** this message indicates what kind of query has to be performed, e.g.
     * - a new query (form submitted),
     * - no query (click in similiar terms),
     * - ranked query (left page navigation clicked),
     * - unranked query (right page navigation clicked)  
     */
    public static final String MSG_QUERY_EXECUTION_TYPE = "query_state";

    public static final String MSGV_NO_QUERY = "no";

    public static final String MSGV_NEW_QUERY = "new";

    public static final String MSGV_RANKED_QUERY = "ranked";

    public static final String MSGV_UNRANKED_QUERY = "unranked";

    // ------------- GENERIC MESSAGE VALUES ("MSGV_...") -----------------------------

    /** dummy value for a message just to set a value for a message */
    public static final String MSGV_TRUE = "1";

    // ===========================================
    // REQUEST PARAMETERS
    // NOTICE: Also used as message keys and values in SearchState !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! 
    // names = PARAM_
    // values = PARAMV_
    // ===========================================

    // ------------- PARAMS also used for messaging (SearchState) -----------------------------

    public final static String PARAM_QUERY_STRING = "q";

    public final static String PARAM_STARTHIT_RANKED = "rstart";

    public final static String PARAM_STARTHIT_UNRANKED = "nrstart";

    /** Datasource parameter name */
    public final static String PARAM_DATASOURCE = "ds";

    public final static String PARAMV_DATASOURCE_ENVINFO = "1";

    public final static String PARAMV_DATASOURCE_ADDRESS = "2";

    public final static String PARAMV_DATASOURCE_RESEARCH = "3";

    // ------------- PARAMS NOT used for messaging, just for access in Request -----------------------------

    public final static String PARAM_RUBRIC = "rubric";

    public final static String PARAM_PARTNER = "partner";

    public final static String PARAM_GROUPING = "grouping";

    /** Action parameter name */
    public final static String PARAM_ACTION = "action";

    public final static String PARAMV_ACTION_NEW_SEARCH = "doSearch";

    public final static String PARAMV_ACTION_NEW_DATASOURCE = "doChangeDS";

    public final static String PARAMV_ACTION_FROM_TEASER = "doTeaser";

}
