/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.global;

import de.ingrid.portal.forms.SearchSimpleForm;

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
    // Portlet Messaging
    // ===========================================

    // ------------- MESSAGE TOPICS (MSG_TOPIC_...) -----------------------------
    /** Message Topics: define the message "scope", will be "prefix" in message key -> "topic:message" */

    /** set message "scope" service page */
    public final static String MSG_TOPIC_SERVICE = "service";

    /** set message "scope" measures page */
    public final static String MSG_TOPIC_MEASURES = "measures";

    /** set message "scope" measures page */
    public final static String MSG_TOPIC_ENVIRONMENT = "environment";

    /** message "scope" for search pages (start page, simple search, extended search ...) */
    public final static String MSG_TOPIC_SEARCH = "search";

    // ------------- DATA MESSAGES: KEYS (MSG_...) -----------------------------
    /** Messages: define the message itself, will be "suffix" in message key -> "topic:message" */

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

    // ------------- INFORMATION MESSAGES: KEYS (MSG_...) AND VALUES ("MSGV_...") -----------------------------

    /** this message contains the "state" of the query, indicating the current state
     * and what kind of query has to be performed, e.g. a new query (form submitted),
     * no query (click in similiar terms), ranked query (left page navigation clicked),
     * unranked query (right page navigation clicked)  
     */
    public static final String MSG_QUERY_STATE = "query_state";

    public static final String MSGV_NO_QUERY = "no";

    public static final String MSGV_NEW_QUERY = "new";

    public static final String MSGV_RANKED_QUERY = "ranked";

    public static final String MSGV_UNRANKED_QUERY = "unranked";

    // ------------- GENERIC MESSAGE VALUES ("MSGV_...") -----------------------------

    /** dummy value for a message just to set a value for a message */
    public static final String MSGV_TRUE = "1";

    // ===========================================
    // Request Parameters
    // names = PARAM_
    // values = PARAMV_
    // ===========================================

    public final static String PARAM_QUERY = SearchSimpleForm.FIELD_QUERY;

    public final static String PARAM_STARTHIT_RANKED = "rstart";

    public final static String PARAM_STARTHIT_UNRANKED = "nrstart";

    /** Action parameter name and values */
    public final static String PARAM_ACTION = "action";

    public final static String PARAMV_ACTION_NEW_SEARCH = "doSearch";

    public final static String PARAMV_ACTION_NEW_DATASOURCE = "doChangeDS";

    /** Datasource parameter name and values */
    public final static String PARAM_DATASOURCE = "ds";

    public final static String PARAMV_DATASOURCE_ENVINFO = "1";

    public final static String PARAMV_DATASOURCE_ADDRESS = "2";

    public final static String PARAMV_DATASOURCE_RESEARCH = "3";

}
