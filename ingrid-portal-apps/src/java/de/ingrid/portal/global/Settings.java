/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2019 wemove digital solutions GmbH
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
    public static final int SEARCH_RANKED_HITS_PER_PAGE = 10;

    /** number of ranked pages displayed for selection ("selector pages") */
    public static final int SEARCH_RANKED_NUM_PAGES_TO_SELECT = 5;

    public static final String SEARCH_INITIAL_DATASOURCE = Settings.PARAMV_DATASOURCE_ENVINFO;

    /** maximum length of a row without white space in the left columnn, KEEP 80 ! depends on Characters, Uppercase takes 50 ! */
    public static final int SEARCH_RANKED_MAX_ROW_LENGTH = 50;
    
    // ===========================================
    // PSML pages
    // ===========================================

    /** main-search page -> displays and handles simple search, also with results */
    public static final String PAGE_SEARCH_RESULT = "/portal/main-search.psml";

    /** main-service page -> service catalogue, also displays results */
    public static final String PAGE_SERVICE = "/portal/main-service.psml";

    /** main-measures page -> measures catalogue, also displays results */
    public static final String PAGE_MEASURES = "/portal/main-measures.psml";

    /** main-environment page -> environment catalogue, also displays results */
    public static final String PAGE_ENVIRONMENT = "/portal/main-environment.psml";

    /** main-chronicle page -> environment chronicle, also displays results */
    public static final String PAGE_CHRONICLE = "/portal/main-chronicle.psml";

    /** contact page */
    public static final String PAGE_CONTACT = "/portal/service-contact.psml";

    // ===========================================
    // IngridQuery
    // ===========================================

    // ------------- query field names -----------------------------

    // grouped Field
    public static final String QFIELD_GROUPED = "grouped";

    public static final String QFIELD_PARTNER = "partner";
    public static final String QFIELD_PROVIDER = "provider";

    public static final String QFIELD_WWW_DOMAIN = "site";
    public static final String QFIELD_PLUG_ID = "plugid";
    public static final String QFIELD_DATATYPE = IngridQuery.DATA_TYPE;
    public static final String QFIELD_METACLASS = "metaclass";

    // "service", "measures"; rubric
    public static final String QFIELD_RUBRIC = "topic";

    // "environment topics": first category
    public static final String QFIELD_TOPIC = "topic";

    // "environment chronicle": event type
    public static final String QFIELD_EVENT_TYPE = "eventtype";
    // "environment chronicle", ...: at date
    public static final String QFIELD_DATE_AT = "t0";
    // "environment chronicle", ...: from date
    public static final String QFIELD_DATE_FROM = "t1";
    // "environment chronicle", ...: to date
    public static final String QFIELD_DATE_TO = "t2";

    // search settings: search sns metadata as well
    public static final String QFIELD_INCL_META = "incl_meta";

    public static final String QFIELD_LANG = "lang";


    // ------------- query field values -----------------------------

    // datatype: area types
    // --------------------
    public static final String QVALUE_DATATYPE_AREA_SERVICE = "service";
    public static final String QVALUE_DATATYPE_AREA_MEASURES = "measure";
    public static final String QVALUE_DATATYPE_AREA_ENVTOPICS = "topics";
    public static final String QVALUE_DATATYPE_AREA_ENVINFO = "default";
    public static final String QVALUE_DATATYPE_AREA_ADDRESS = "address";
    public static final String QVALUE_DATATYPE_AREA_RESEARCH = "research";
    public static final String QVALUE_DATATYPE_AREA_LAW = "law";
    public static final String QVALUE_DATATYPE_AREA_CATALOG = "metadata";
    private static final String[] QVALUES_DATATYPE_AREAS_BASIC = new String[] {
    	QVALUE_DATATYPE_AREA_ENVINFO,
    	QVALUE_DATATYPE_AREA_ADDRESS,
    	QVALUE_DATATYPE_AREA_RESEARCH,
    	QVALUE_DATATYPE_AREA_LAW
    };
    
    public static final String[] getQValuesDatatypesAreaBasic() {
        return QVALUES_DATATYPE_AREAS_BASIC.clone();
    }

    // datatype: source types
    // ----------------------
    public static final String QVALUE_DATATYPE_SOURCE_WWW = "www";
    public static final String QVALUE_DATATYPE_SOURCE_METADATA = "metadata";
    public static final String QVALUE_DATATYPE_SOURCE_FIS = "fis";
    
    // datatype: IPLUG types
    // ---------------------
    public static final String QVALUE_DATATYPE_IPLUG_DSC_ECS = "dsc_ecs";
    public static final String QVALUE_DATATYPE_IPLUG_DSC_ECS_ADDRESS = "dsc_ecs_address";
    public static final String QVALUE_DATATYPE_IPLUG_TAMINO = "tamino";
    public static final String QVALUE_DATATYPE_IPLUG_ECS = "ecs";
    public static final String QVALUE_DATATYPE_IPLUG_CSW = "csw";
    public static final String QVALUE_DATATYPE_IPLUG_DSC_CSW = "dsc_csw";
    public static final String QVALUE_DATATYPE_IPLUG_DSC_OTHER = "dsc_other";
    
    // all sub datatypes indicating address search (switch to address rendering of results) 
    private static final String[] QVALUES_DATATYPES_ADDRESS = new String[] {
    	QVALUE_DATATYPE_AREA_ADDRESS,
    	QVALUE_DATATYPE_IPLUG_DSC_ECS_ADDRESS,
    	// temporary datatype:training-address ! these are the new training "idc iplugs" (MDEK), should be rendered in live Portal !
    	"training-address"
    };
    
    public static final String[] getQValuesDatatypesAddress() {
        return QVALUES_DATATYPES_ADDRESS.clone();
    }

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

    public static final String RESULT_KEY_TITLE = "title";
    public static final String RESULT_KEY_ABSTRACT = "abstract";

    public static final String RESULT_KEY_ADDITIONAL_HTML_1 = "additional_html_1";

    public static final String RESULT_KEY_RUBRIC = "topic";
    public static final String RESULT_KEY_TOPIC = "topic";

    public static final String RESULT_KEY_URL = "url";
    public static final String RESULT_KEY_URL_STR = "url_str";
    public static final String RESULT_KEY_URL_DOMAIN = "url_domain";
    public static final String RESULT_KEY_URL_TYPE = "url_type";

    public static final String RESULT_KEY_PARTNER = "partner";
    public static final String RESULT_KEY_PROVIDER = "provider";

    public static final String RESULT_KEY_SOURCE = "source";
    public static final String RESULT_KEY_TYPE = "type";

    public static final String RESULT_KEY_PLUG_ID = "plugid";
    public static final String RESULT_KEY_DOC_ID = "docid";
    public static final String RESULT_KEY_DOC_UUID = "docuuid";

    public static final String RESULT_KEY_WMS_URL = "wms_url";
    public static final String RESULT_KEY_WMS_COORD = "wms_coord";
    public static final String RESULT_KEY_WMS_COORD_CHECK = "wms_check";
    public static final String RESULT_KEY_WMS_TMP_COORD_X = "coord_x";
    public static final String RESULT_KEY_WMS_TMP_COORD_Y = "coord_y";
    public static final String RESULT_KEY_WMS_TMP_COORD_CLASS = "coord_class";
    public static final String RESULT_KEY_WMS_TMP_COORD_TITLE = "coord_title";
    public static final String RESULT_KEY_WMS_TMP_COORD_DESCR = "coord_descr";

    public static final Object RESULT_KEY_IS_BLP = "is_blp";
    
    public static final String RESULT_KEY_UDK_IS_ADDRESS = "is_address";
    public static final String RESULT_KEY_UDK_CLASS = "udk_class";
    public static final String RESULT_KEY_UDK_ADDRESS_FIRSTNAME = "address_firstname";
    public static final String RESULT_KEY_UDK_ADDRESS_LASTNAME = "address_lastname";
    public static final String RESULT_KEY_UDK_ADDRESS_TITLE = "address_title";
    public static final String RESULT_KEY_UDK_ADDRESS_SALUTATION = "address_salutation";
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

    public static final String HIT_KEY_WMS_URL = "T011_obj_serv_op_connpoint.connect_point";
    
    // needed to determine the display of the "show in map" link 
    public static final String HIT_KEY_OBJ_SERV_HAS_ACCESS_CONSTRAINT = "t011_obj_serv.has_access_constraint";
    
    // this key represents the index key for the service type.
    public static final String HIT_KEY_OBJ_SERV_TYPE = "t011_obj_serv.type";
    public static final String HIT_KEY_OBJ_SERV_TYPE_KEY = "t011_obj_serv.type_key";

    
    public static final String HIT_KEY_OBJ_ID = "T01_object.obj_id";
    public static final String HIT_KEY_ORG_OBJ_ID = "T01_object.org_obj_id";

    // mapped ! NO, NOT MAPPED ANYMORE in Plug Description, instead in iBus when querying.
    // ECS iPlug has to be adapted (maybe !)
    public static final String HIT_KEY_UDK_CLASS = "T01_object.obj_class";

    public static final String HIT_KEY_ADDRESS_CLASS = "T02_address.typ";
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
    public static final String MSG_TOPIC_SEARCH = "search";

    /** message "scope" for the Thesaurus search page */
    public static final String MSG_TOPIC_SEARCH_THESAURUS = "searchThes";

    /** set message "scope" service page */
    public static final String MSG_TOPIC_SERVICE = "service";

    /** set message "scope" measures page */
    public static final String MSG_TOPIC_MEASURES = "measures";

    /** set message "scope" measures page */
    public static final String MSG_TOPIC_ENVIRONMENT = "environment";

    /** set message "scope" chronicle page */
    public static final String MSG_TOPIC_CHRONICLE = "chronicle";

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

    public static final String PARAMV_ALL = "all";

    // ACTION VALUES used by multiple portlets
    public static final String PARAMV_ACTION_SUBMIT = "doSubmit";
    public static final String PARAMV_ACTION_ORIGINAL_SETTINGS = "doOriginalSettings";
    public static final String PARAMV_ACTION_FROM_TEASER = "doTeaser";
    public static final String PARAMV_ACTION_NEW_SEARCH = "doSearch";
    public static final String PARAMV_ACTION_CHANGE_TAB = "doChangeTab";

    // DATASOURCE VALUES
    public static final String PARAMV_DATASOURCE_ENVINFO = "1";
    public static final String PARAMV_DATASOURCE_ADDRESS = "2";
    public static final String PARAMV_DATASOURCE_RESEARCH = "3";
	public static final String PARAMV_DATASOURCE_LAW = "4";
	public static final String PARAMV_DATASOURCE_CATALOG = "5";

    // GROUPING VALUES
    public static final String PARAMV_GROUPING_OFF = "none";
    public static final String PARAMV_GROUPING_PARTNER = "partner";
    public static final String PARAMV_GROUPING_PROVIDER = "provider";
    public static final String PARAMV_GROUPING_DOMAIN = "domain";
    public static final String PARAMV_GROUPING_PLUG_ID = "plugid";
    
    // ------------- PARAM Keys, also used as message keys (SearchState) -----------------------------

    public static final String PARAM_QUERY_STRING = "q";
    public static final String PARAM_QUERY_STRING_EXT = "q_ext";
    
    public static final String PARAM_STARTHIT_RANKED = "rstart";

    public static final String PARAM_DATASOURCE = "ds";

    public static final String PARAM_RANKING = "rank";
    
    // history of starthits of grouped pages
    public static final String PARAM_GROUPING_STARTHITS = "grouping_starthits";

    /** this parameter holds the type of filter for search result display (usually same as grouping: partner | provider) **/
    public static final String PARAM_FILTER = "filter";
    
    // ------------- PARAM Keys, NOT used for messaging, just for access in Request -----------------------------

    public static final String PARAM_RUBRIC = "rubric";
    public static final String PARAM_TOPIC_ID = "topId";

    public static final String PARAM_PARTNER = "partner";
    public static final String PARAM_PROVIDER = "provider";

    public static final String PARAM_SUBJECT = "subject";
    
    public static final String PARAM_GROUPING = "grouping";

    public static final String PARAM_ACTION = "action";

    public static final String PARAM_TAB = "tab";

    public static final String PARAM_CURRENT_SELECTOR_PAGE = "currentSelectorPage";
    
	public static final String PARAM_LOGIN_REDIRECT = "r";


    // ------------- Session keys for direct HTTP session manipulation -------------------------
    
    public static final String SESSION_LOGIN_STARTED = "session_started";

    // can contain external user authentication info even if user does not exist in portal
    public static final String USER_AUTH_INFO = "de.ingrid.user.auth.info";
    public static final String USER_AUTH_INFO_IS_ADMIN = "de.ingrid.user.auth.isAdminPartner";

}
