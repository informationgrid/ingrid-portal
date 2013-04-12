/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.global;

import de.ingrid.utils.query.IngridQuery;

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
    public final static int SEARCH_UNRANKED_NUM_PAGES_TO_SELECT = 5;

    /* max. number of hits displayed for every iPlug (in unranked search) */
    // NOT NEEDED ANYMORE, WE ALWAYS DISPLAY ONE HIT PER GROUP !
//    public final static int SEARCH_NUM_HITS_PER_GROUP = 3;

    public final static String SEARCH_INITIAL_DATASOURCE = Settings.PARAMV_DATASOURCE_ENVINFO;

    /** maximum length of a row without white space in the left columnn, KEEP 80 ! depends on Characters, Uppercase takes 77 ! */
    public final static int SEARCH_RANKED_MAX_ROW_LENGTH = 77;
    
    // ===========================================
    // PSML pages
    // ===========================================

    /** main-search page -> displays and handles simple search, also with results */
    public final static String PAGE_SEARCH_RESULT = "/portal/main-search.psml";

    /** main-service page -> service catalogue, also displays results */
    public final static String PAGE_SERVICE = "/portal/main-service.psml";

    /** main-measures page -> measures catalogue, also displays results */
    public final static String PAGE_MEASURES = "/portal/main-measures.psml";

    /** main-environment page -> environment catalogue, also displays results */
    public final static String PAGE_ENVIRONMENT = "/portal/main-environment.psml";

    /** main-chronicle page -> environment chronicle, also displays results */
    public final static String PAGE_CHRONICLE = "/portal/main-chronicle.psml";

    /** contact page */
    public final static String PAGE_CONTACT = "/portal/service-contact.psml";

    // ===========================================
    // IngridQuery
    // ===========================================

    // ------------- query field names -----------------------------

    // grouped Field
    public final static String QFIELD_GROUPED = "grouped";

    public final static String QFIELD_PARTNER = "partner";
    public static final String QFIELD_PROVIDER = "provider";

    public static final String QFIELD_WWW_DOMAIN = "site";
    public static final String QFIELD_PLUG_ID = "plugid";
    public final static String QFIELD_DATATYPE = IngridQuery.DATA_TYPE;
    public static final String QFIELD_METACLASS = "metaclass";

    // "service", "measures"; rubric
    public final static String QFIELD_RUBRIC = "topic";

    // "environment topics": first category
    public final static String QFIELD_TOPIC = "topic";

    // "environment chronicle": event type
    public final static String QFIELD_EVENT_TYPE = "eventtype";
    // "environment chronicle", ...: at date
    public final static String QFIELD_DATE_AT = "t0";
    // "environment chronicle", ...: from date
    public final static String QFIELD_DATE_FROM = "t1";
    // "environment chronicle", ...: to date
    public final static String QFIELD_DATE_TO = "t2";

    // search settings: search sns metadata as well
    public static final String QFIELD_INCL_META = "incl_meta";

    public static final String QFIELD_LANG = "lang";


    // ------------- query field values -----------------------------

    // datatype: area types
    // --------------------
    public final static String QVALUE_DATATYPE_AREA_SERVICE = "service";
    public final static String QVALUE_DATATYPE_AREA_MEASURES = "measure";
    public final static String QVALUE_DATATYPE_AREA_ENVTOPICS = "topics";
    public final static String QVALUE_DATATYPE_AREA_ENVINFO = "default";
    public final static String QVALUE_DATATYPE_AREA_ADDRESS = "address";
    public final static String QVALUE_DATATYPE_AREA_RESEARCH = "research";
    public static final String QVALUE_DATATYPE_AREA_LAW = "law";
    public static final String QVALUE_DATATYPE_AREA_CATALOG = "metadata";
    public final static String[] QVALUES_DATATYPE_AREAS_BASIC = new String[] {
    	QVALUE_DATATYPE_AREA_ENVINFO,
    	QVALUE_DATATYPE_AREA_ADDRESS,
    	QVALUE_DATATYPE_AREA_RESEARCH,
    	QVALUE_DATATYPE_AREA_LAW
    };

    // datatype: source types
    // ----------------------
    public final static String QVALUE_DATATYPE_SOURCE_WWW = "www";
    public final static String QVALUE_DATATYPE_SOURCE_METADATA = "metadata";
    public final static String QVALUE_DATATYPE_SOURCE_FIS = "fis";
    
    // datatype: IPLUG types
    // ---------------------
    public final static String QVALUE_DATATYPE_IPLUG_DSC_ECS = "dsc_ecs";
    public final static String QVALUE_DATATYPE_IPLUG_DSC_ECS_ADDRESS = "dsc_ecs_address";
    public static final String QVALUE_DATATYPE_IPLUG_TAMINO = "tamino";
    public final static String QVALUE_DATATYPE_IPLUG_ECS = "ecs";
    public final static String QVALUE_DATATYPE_IPLUG_CSW = "csw";
    public final static String QVALUE_DATATYPE_IPLUG_DSC_CSW = "dsc_csw";
    public final static String QVALUE_DATATYPE_IPLUG_DSC_OTHER = "dsc_other";
    
    // all sub datatypes indicating address search (switch to address rendering of results) 
    public final static String[] QVALUES_DATATYPES_ADDRESS = new String[] {
    	QVALUE_DATATYPE_AREA_ADDRESS,
    	QVALUE_DATATYPE_IPLUG_DSC_ECS_ADDRESS,
    	// temporary datatype:training-address ! these are the new training "idc iplugs" (MDEK), should be rendered in live Portal !
    	"training-address"
    };

    public static final String QVALUE_LANG_DE = "de";
    
    // metaclass: types
    // ---------------------
    
    public static final String QVALUE_METACLASS_JOB = "job";
    public static final String QVALUE_METACLASS_MAP = "map";
    public static final String QVALUE_METACLASS_DOCUMENT = "document";
    public static final String QVALUE_METACLASS_GEOSERVICE = "geoservice";
    public static final String QVALUE_METACLASS_SERVICE = "service";
    public static final String QVALUE_METACLASS_PROJECT = "project";
    public static final String QVALUE_METACLASS_DATABASE = "database";
    
    // ===========================================
    // Result data (used in templates for rendering results!)
    // IF POSSIBLE, USE THESE KEYS ALSO TO ACCESS HIT DETAIL DATA !!! 
    // ===========================================

    public final static String RESULT_KEY_TITLE = "title";
    public final static String RESULT_KEY_ABSTRACT = "abstract";

    public final static String RESULT_KEY_ADDITIONAL_HTML_1 = "additional_html_1";

    public final static String RESULT_KEY_RUBRIC = "topic";
    public final static String RESULT_KEY_TOPIC = "topic";

    public final static String RESULT_KEY_URL = "url";
    public final static String RESULT_KEY_URL_STR = "url_str";
    public final static String RESULT_KEY_URL_DOMAIN = "url_domain";
    public final static String RESULT_KEY_URL_TYPE = "url_type";

    public final static String RESULT_KEY_PARTNER = "partner";
    public final static String RESULT_KEY_PROVIDER = "provider";

    public final static String RESULT_KEY_SOURCE = "source";
    public final static String RESULT_KEY_TYPE = "type";

    public final static String RESULT_KEY_PLUG_ID = "plugid";
    public final static String RESULT_KEY_DOC_ID = "docid";
    public final static String RESULT_KEY_DOC_UUID = "docuuid";

    public final static String RESULT_KEY_WMS_URL = "wms_url";
    public final static String RESULT_KEY_WMS_COORD = "wms_coord";
    public final static String RESULT_KEY_WMS_COORD_CHECK = "wms_check";
    public final static String RESULT_KEY_WMS_TMP_COORD_X = "coord_x";
    public final static String RESULT_KEY_WMS_TMP_COORD_Y = "coord_y";
    public final static String RESULT_KEY_WMS_TMP_COORD_CLASS = "coord_class";
    public final static String RESULT_KEY_WMS_TMP_COORD_TITLE = "coord_title";
    public final static String RESULT_KEY_WMS_TMP_COORD_DESCR = "coord_descr";

    public final static String RESULT_KEY_UDK_IS_ADDRESS = "is_address";
    public final static String RESULT_KEY_UDK_CLASS = "udk_class";
    public final static String RESULT_KEY_UDK_ADDRESS_FIRSTNAME = "address_firstname";
    public final static String RESULT_KEY_UDK_ADDRESS_LASTNAME = "address_lastname";
    public final static String RESULT_KEY_UDK_ADDRESS_TITLE = "address_title";
    public final static String RESULT_KEY_UDK_ADDRESS_SALUTATION = "address_salutation";
    public static final String RESULT_KEY_UDK_TITLE = "title";

    public static final String RESULT_KEY_SUB_HIT = "subhit";
    public static final String RESULT_KEY_DETAIL = "detail";
    public static final String RESULT_KEY_PLUG_DESCRIPTION = "plugDescr";

    public static final String RESULT_KEY_NO_OF_HITS = "no_of_hits";
    public static final String RESULT_KEY_DUMMY_HIT = "dummyHit";
    
    public static final String RESULT_KEY_CAPABILITIES_URL = "capabilities_url";
    public static final String RESULT_KEY_SERVICE_UUID     = "refering_service_uuid";
    public static final String RESULT_KEY_COUPLED_RESOURCE = "coupled_resource";
    
    public static final String RESULT_KEY_CSW_INTERFACE_URL = "csw_link";

    // ===========================================
    // Hit Detail data (get data from hit details)
    // NOTICE: Define here only special stuff, WHEN YOU CAN'T USE RESULT_KEY_... from above
    // ===========================================

    public final static String HIT_KEY_WMS_URL = "T011_obj_serv_op_connpoint.connect_point";
    
    // needed to determine the display of the "show in map" link 
    public final static String HIT_KEY_OBJ_SERV_HAS_ACCESS_CONSTRAINT = "t011_obj_serv.has_access_constraint";
    
    // this key represents the index key for the service type.
    public static final String HIT_KEY_OBJ_SERV_TYPE = "t011_obj_serv.type";
    public static final String HIT_KEY_OBJ_SERV_TYPE_KEY = "t011_obj_serv.type_key";

    
    public final static String HIT_KEY_OBJ_ID = "T01_object.obj_id";
    public final static String HIT_KEY_ORG_OBJ_ID = "T01_object.org_obj_id";

    // mapped ! NO, NOT MAPPED ANYMORE in Plug Description, instead in iBus when querying.
    // ECS iPlug has to be adapted (maybe !)
    public final static String HIT_KEY_UDK_CLASS = "T01_object.obj_class";
//    public final static String HIT_KEY_UDK_CLASS = "metaclass";

    public final static String HIT_KEY_ADDRESS_CLASS = "T02_address.typ";
	public static final String HIT_KEY_ADDRESS_CLASS2 = "T02_address2.typ";
	public static final String HIT_KEY_ADDRESS_CLASS3 = "T02_address3.typ";
    public static final String HIT_KEY_ADDRESS_FIRSTNAME = "T02_address.firstname";
    public static final String HIT_KEY_ADDRESS_LASTNAME = "T02_address.lastname";
    public static final String HIT_KEY_ADDRESS_TITLE = "T02_address.title";
	public static final String HIT_KEY_ADDRESS_INSTITUTION = "title";
	public static final String HIT_KEY_ADDRESS_INSTITUTION2 = "title2";
	public static final String HIT_KEY_ADDRESS_INSTITUTION3 = "title3";
    public static final String HIT_KEY_ADDRESS_ADDRESS = "T02_address.address";
    public static final String HIT_KEY_ADDRESS_ADDRID = "T02_address.adr_id";
	public static final String HIT_KEY_ADDRESS_ADDRID2 = "T02_address2.adr_id";
	public static final String HIT_KEY_ADDRESS_ADDRID3 = "T02_address3.adr_id";
	public static final String HIT_KEY_ADDRESS_ADDR_FROM_ID = "t022_adr_adr.adr_from_id";
	public static final String HIT_KEY_ADDRESS_ADDR_FROM_ID3 = "t022_adr_adr3.adr_from_id";

    // Object relations
    public static final String HIT_KEY_OBJ_OBJ_FROM = "T012_obj_obj.object_from_id";
    public static final String HIT_KEY_OBJ_OBJ_TYP = "T012_obj_obj.typ";
    public static final String HIT_KEY_OBJ_OBJ_TO = "T012_obj_obj.object_to_id";

    // Object address relations
    public static final String HIT_KEY_OBJ_ADDR_RELATION = "T012_obj_adr";

    // Address address relations
    public static final String HIT_KEY_ADDR_ADDR_RELATION = "T022_adr_adr";

    // UDK Objects topic in environment catalog (Umweltthemen = datatype:topics)
    public static final String HIT_KEY_OBJ_ENV_TOPIC_KEY = "t0114_env_topic.topic_key";

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

    /** message "scope" for search pages (start page, simple search, extended search ...) */
    public final static String MSG_TOPIC_SEARCH = "search";

    /** message "scope" for the Thesaurus search page */
    public final static String MSG_TOPIC_SEARCH_THESAURUS = "searchThes";

    /** set message "scope" service page */
    public final static String MSG_TOPIC_SERVICE = "service";

    /** set message "scope" measures page */
    public final static String MSG_TOPIC_MEASURES = "measures";

    /** set message "scope" measures page */
    public final static String MSG_TOPIC_ENVIRONMENT = "environment";

    /** set message "scope" chronicle page */
    public final static String MSG_TOPIC_CHRONICLE = "chronicle";

    // ------------- DATA MESSAGES: KEYS (MSG_...) -----------------------------
    // define the message itself, will be "suffix" in message key -> "topic:message"

    /** this message contains the central IngridQuery object */
    public static final String MSG_QUERY = "query";

    /** this message contains the currently selected partners, from whom results should be delivered */
    public static final String MSG_PARTNER = "partner";

    /** cache for ranked search results */
    public static final String MSG_SEARCH_RESULT_RANKED = "search_result_ranked";

    /** when set ranked search has finished */
    public static final String MSG_SEARCH_FINISHED_RANKED = "search_finished_ranked";

    /** cache for unranked search results */
    public static final String MSG_SEARCH_RESULT_UNRANKED = "search_result_unranked";

    /** when set ranked search has finished */
    public static final String MSG_SEARCH_FINISHED_UNRANKED = "search_finished_unranked";

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

    /** indicates whether JavaScript is active in Browser */
    public static final String MSG_HAS_JAVASCRIPT = "has_javascript";

    // ------------- GENERIC MESSAGE VALUES ("MSGV_...") -----------------------------

    /** dummy value for a message just to set a value for a message */
    public static final String MSGV_TRUE = "1";

    // ===========================================
    // REQUEST PARAMETERS
    // NOTICE: Also used as message keys and values in SearchState !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! 
    // names = PARAM_
    // values = PARAMV_
    // ===========================================

    // ------------- PARAM values -----------------------------

    public final static String PARAMV_ALL = "all";

    // ACTION VALUES used by multiple portlets
    public final static String PARAMV_ACTION_SUBMIT = "doSubmit";
    public final static String PARAMV_ACTION_ORIGINAL_SETTINGS = "doOriginalSettings";
    public final static String PARAMV_ACTION_FROM_TEASER = "doTeaser";
    public final static String PARAMV_ACTION_NEW_SEARCH = "doSearch";
    public final static String PARAMV_ACTION_CHANGE_TAB = "doChangeTab";

    // DATASOURCE VALUES
    public final static String PARAMV_DATASOURCE_ENVINFO = "1";
    public final static String PARAMV_DATASOURCE_ADDRESS = "2";
    public final static String PARAMV_DATASOURCE_RESEARCH = "3";
	public final static String PARAMV_DATASOURCE_LAW = "4";
	public final static String PARAMV_DATASOURCE_CATALOG = "5";

    // GROUPING VALUES
    public final static String PARAMV_GROUPING_OFF = "none";
    public final static String PARAMV_GROUPING_PARTNER = "partner";
    public final static String PARAMV_GROUPING_PROVIDER = "provider";
    public final static String PARAMV_GROUPING_DOMAIN = "domain";
    public final static String PARAMV_GROUPING_PLUG_ID = "plugid";
    
    // ------------- PARAM Keys, also used as message keys (SearchState) -----------------------------

    public final static String PARAM_QUERY_STRING = "q";
    public static final String PARAM_QUERY_STRING_EXT = "q_ext";
    
    public final static String PARAM_STARTHIT_RANKED = "rstart";
    public final static String PARAM_STARTHIT_UNRANKED = "nrstart";

    public final static String PARAM_DATASOURCE = "ds";

    public final static String PARAM_RANKING = "rank";
    
    // history of starthits of grouped pages
    public static final String PARAM_GROUPING_STARTHITS = "grouping_starthits";
    public static final String PARAM_GROUPING_STARTHITS_UNRANKED = "grouping_starthits_unranked";
    

    /** this parameter holds the type of filter for search result display (usually same as grouping: partner | provider) **/
    public static final String PARAM_FILTER = "filter";
    
    // ------------- PARAM Keys, NOT used for messaging, just for access in Request -----------------------------

    public final static String PARAM_RUBRIC = "rubric";
    public final static String PARAM_TOPIC_ID = "topId";

    public final static String PARAM_PARTNER = "partner";
    public static final String PARAM_PROVIDER = "provider";

    public static final String PARAM_SUBJECT = "subject";
    
    public final static String PARAM_GROUPING = "grouping";

    public final static String PARAM_ACTION = "action";

    public final static String PARAM_TAB = "tab";

    public static final String PARAM_CURRENT_SELECTOR_PAGE = "currentSelectorPage";
    public static final String PARAM_CURRENT_SELECTOR_PAGE_UNRANKED = "currentSelectorPageUnranked";
    
	public static final String PARAM_LOGIN_REDIRECT = "r";


    // ------------- Session keys for direct HTTP session manipulation -------------------------
    
    public static final String SESSION_LOGIN_STARTED = "session_started";

    // reg exp to check for forbidden login patterns
    public static final String FORBIDDEN_LOGINS_REGEXP_STR = ".*/.*";
    
    // can contain external user authentication info even if user does not exist in portal
    public static final String USER_AUTH_INFO = "de.ingrid.user.auth.info";
    public static final String USER_AUTH_INFO_IS_ADMIN = "de.ingrid.user.auth.isAdminPartner";

}
