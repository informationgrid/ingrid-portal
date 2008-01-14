package de.ingrid.mdek;

import java.util.HashMap;

import de.ingrid.mdek.dwr.MdekDataBean;

/**
 * @author mbenz
 * 
 * The Data Mapper Interface defines the methods needed to map external data to the internal
 * UDK data representation of the Mdek and the other way round.
 * 
 * The static Strings defined in this interface represent the Mdek view of an Udk object
 * 
 */
public interface DataMapperInterface {

	// TODO Change return type to MdekDataBean?
	public HashMap<String, Object> getSimpleMdekRepresentation(Object obj);
	public MdekDataBean getDetailedMdekRepresentation(Object obj);

	// We return an Object since we don't know all the possible target types in advance 
	public Object convertFromMdekRepresentation(MdekDataBean data);

	
	// Miscellaneous
	public final static String MDEK_OBJECT_ID = "id";
	public final static String MDEK_OBJECT_HAS_CHILDREN = "isFolder";
	// TODO: Some values are duplicates. Merge them!
	public final static String MDEK_OBJECT_TITLE = "title";
	public final static String MDEK_OBJECT_DOCTYPE = "nodeDocType";


	// Identification 
	public final static String MDEK_OBJECT_NAME = "objectName";
	public final static String MDEK_OBJECT_CLASS = "objectClass";
	public final static String MDEK_OBJECT_OWNER = "objectOwner";
	public final static String MDEK_OBJECT_LAST_EDITOR = "last_editor";

	// General
	public final static String MDEK_GENERAL_SHORT_DESC = "generalShortDesc";
	public final static String MDEK_GENERAL_DESC = "generalDesc";
	public final static String MDEK_GENERAL_ADDRESS_TABLE = "generalAddress";
	public final static String MDEK_GENERAL_ADDRESS_TABLE_ID = "Id";
	public final static String MDEK_GENERAL_ADDRESS_TABLE_INFO = "information";
	public final static String MDEK_GENERAL_ADDRESS_TABLE_CLASS = "icon";
	public final static String MDEK_GENERAL_ADDRESS_TABLE_NAME = "name";
	public final static String MDEK_GENERAL_ADDRESS_TABLE_ORGANISATION = "organisation";
	public final static String MDEK_GENERAL_ADDRESS_TABLE_GIVENNAME = "givenName";
	public final static String MDEK_GENERAL_ADDRESS_TABLE_NAME_FORM = "nameForm";
	public final static String MDEK_GENERAL_ADDRESS_TABLE_TITLEORFUNC = "titleOrFunction";
	public final static String MDEK_GENERAL_ADDRESS_TABLE_STREET = "street";
	public final static String MDEK_GENERAL_ADDRESS_TABLE_COUNTRY_CODE = "countryCode";
	public final static String MDEK_GENERAL_ADDRESS_TABLE_CITY = "city";
	public final static String MDEK_GENERAL_ADDRESS_TABLE_POBOX_POSTAL = "poboxPostalCode";
	public final static String MDEK_GENERAL_ADDRESS_TABLE_POBOX = "pobox";
	public final static String MDEK_GENERAL_ADDRESS_TABLE_FUNCTION = "function";
	public final static String MDEK_GENERAL_ADDRESS_TABLE_DESCRIPTION = "addressDescription";
	public final static String MDEK_GENERAL_ADDRESS_TABLE_COMM = "communication";
	public final static String MDEK_GENERAL_ADDRESS_TABLE_COMM_MEDIUM = "communicationMedium";
	public final static String MDEK_GENERAL_ADDRESS_TABLE_COMM_VALUE = "communicationValue";
	public final static String MDEK_GENERAL_ADDRESS_TABLE_COMM_DESCRIPTION = "communicationDescription";

    
    // Object class 1 (Geoinformation/Karte)
	public final static String MDEK_OBJ_CLASS1_DATASET = "ref1DataSet";
	public final static String MDEK_OBJ_CLASS1_COVERAGE = "ref1Coverage";
	public final static String MDEK_OBJ_CLASS1_REPRESENTATION = "ref1Representation";	
	public final static String MDEK_OBJ_CLASS1_VEC_TOPOLOGY = "ref1VFormatTopology";
	public final static String MDEK_OBJ_CLASS1_VEC_DETAILS = "ref1VFormatDetails";
	public final static String MDEK_OBJ_CLASS1_SPATIAL_SYSTEM = "ref1SpatialSystem";
	public final static String MDEK_OBJ_CLASS1_SCALE = "ref1Scale";
	public final static String MDEK_OBJ_CLASS1_ALT_ACC = "ref1AltAccuracy";
	public final static String MDEK_OBJ_CLASS1_POS_ACC = "ref1PosAccuracy";
	public final static String MDEK_OBJ_CLASS1_SYMBOL_CAT = "ref1SymbolsText";
	public final static String MDEK_OBJ_CLASS1_KEYS_CAT = "ref1KeysText";
	public final static String MDEK_OBJ_CLASS1_SERVICE_LINK = "ref1ServiceLink";
	public final static String MDEK_OBJ_CLASS1_BASIS_LINK = "ref1BasisLink";
	public final static String MDEK_OBJ_CLASS1_DATA_BASIS_LINK = "ref1DataBasisLink";
	public final static String MDEK_OBJ_CLASS1_DATA = "ref1Data";
	public final static String MDEK_OBJ_CLASS1_PROCESS_LINK = "ref1ProcessLink";
	
	// Object class 2 (Dokument/Bericht/Literatur)
	public final static String MDEK_OBJ_CLASS2_AUTHOR = "ref2Author";
	public final static String MDEK_OBJ_CLASS2_EDITOR = "ref2Publisher";
	public final static String MDEK_OBJ_CLASS2_PUBLISH_IN = "ref2PublishedIn";
	public final static String MDEK_OBJ_CLASS2_PUBLISH_LOC = "ref2PublishLocation";
	public final static String MDEK_OBJ_CLASS2_PUBLISH_ISSUE = "ref2PublishedInIssue";
	public final static String MDEK_OBJ_CLASS2_PUBLISH_PAGES = "ref2PublishedInPages";
	public final static String MDEK_OBJ_CLASS2_PUBLISH_YEAR = "ref2PublishedInYear";
	public final static String MDEK_OBJ_CLASS2_PUBLISH_ISBN = "ref2PublishedISBN";
	public final static String MDEK_OBJ_CLASS2_PUBLISHER = "ref2PublishedPublisher";
	public final static String MDEK_OBJ_CLASS2_LOCATION = "ref2LocationLink";
	public final static String MDEK_OBJ_CLASS2_TYPE = "ref2DocumentType";
	public final static String MDEK_OBJ_CLASS2_BASE = "ref2BaseDataLink";
	public final static String MDEK_OBJ_CLASS2_INFO = "ref2BibDataIn";
	
	// Object class 3 (Dienst/Anwendung/Informationssystem)
	public final static String MDEK_OBJ_CLASS3_SERVICE_TYPE = "ref3ServiceType";
	public final static String MDEK_OBJ_CLASS3_SERVICE_VERSION = "ref3ServiceVersion";
	public final static String MDEK_OBJ_CLASS3_SYSTEM_ENV = "ref3SystemEnv";
	public final static String MDEK_OBJ_CLASS3_HISTORY = "ref3History";
	public final static String MDEK_OBJ_CLASS3_BASE_DATA = "ref3BaseDataLink";
	public final static String MDEK_OBJ_CLASS3_DESCRIPTION = "ref3Explanation";
	public final static String MDEK_OBJ_CLASS3_OPERATION = "ref3Operation";
	
	// Object class 4 (Vorhaben/Projekt/Programm)
	public final static String MDEK_OBJ_CLASS4_MEMBER = "ref4ParticipantsLink";
	public final static String MDEK_OBJ_CLASS4_LEADER = "ref4PMLink";
	public final static String MDEK_OBJ_CLASS4_DESCRIPTION = "ref4Explanation";
	
	// Object class 5 (Vorhaben/Projekt/Programm)
	public final static String MDEK_OBJ_CLASS5_CONTENT = "ref5Scale";
	public final static String MDEK_OBJ_CLASS5_DATA = "ref5MethodLink";
	public final static String MDEK_OBJ_CLASS5_DESCRIPTION = "ref5Explanation";

	// Spatial
	public final static String MDEK_SPATIAL_ADMIN = "spatialRefAdminUnit";
	public final static String MDEK_SPATIAL_ADMIN_COORDS = "spatialRefCoordsAdminUnit";
	public final static String MDEK_SPATIAL_LOC = "spatialRefLocation";
	public final static String MDEK_SPATIAL_LOC_COORDS = "spatialRefCoordsLocation";
	public final static String MDEK_SPATIAL_ALT_MIN = "spatialRefAltMin";
	public final static String MDEK_SPATIAL_ALT_MAX = "spatialRefAltMax";
	public final static String MDEK_SPATIAL_ALT_MEASURE = "spatialRefAltMeasure";
	public final static String MDEK_SPATIAL_ALT_VDATE = "spatialRefAltVDate";
	public final static String MDEK_SPATIAL_ALT_DESCR = "spatialRefExplanation";

	// Time
	public final static String MDEK_TIME_TYPE = "timeRefType";
	public final static String MDEK_TIME_FROM = "timeRefDate1";
	public final static String MDEK_TIME_TO = "timeRefDate2";
	public final static String MDEK_TIME_STATUS = "timeRefStatus";
	public final static String MDEK_TIME_PERIOD = "timeRefPeriodicity";
	public final static String MDEK_TIME_INTERVAL = "timeRefIntervalNum";
	public final static String MDEK_TIME_UNIT = "timeRefIntervalUnit";
	public final static String MDEK_TIME_REF = "timeRefTable";
	public final static String MDEK_TIME_DESCR = "timeRefExplanation";

	// ExtraInfo
	public final static String MDEK_INFO_LANG_METADATA = "extraInfoLangMetaData";
	public final static String MDEK_INFO_LANG_DATA = "extraInfoLangData";
	public final static String MDEK_INFO_PUBLISH = "extraInfoPublishArea";
	public final static String MDEK_INFO_XML_EXPORT = "extraInfoXMLExport";
	public final static String MDEK_INFO_LEGAL = "extraInfoLegalBasics";
	public final static String MDEK_INFO_PURPOSE = "extraInfoPurpose";
	public final static String MDEK_INFO_USAGE = "extraInfoUse";
	
	// Availability
	public final static String MDEK_AVAIL_FORMAT = "availabilityDataFormat";
	public final static String MDEK_AVAIL_MEDIA_OPTION = "availabilityMediaOptions";
	public final static String MDEK_AVAIL_ORDER_INFO = "availabilityOrderInfo";
	public final static String MDEK_AVAIL_NOTE = "availabilityNoteUse";
	public final static String MDEK_AVAIL_FEES = "availabilityCosts";
	
	// Thesaurus
	public final static String MDEK_THES_TERMS = "thesaurusTerms";
	public final static String MDEK_THES_TOPICS = "thesaurusTopics";
	public final static String MDEK_THES_FREE_TERMS = "thesaurusFreeTermsList";
	public final static String MDEK_THES_ENV_EXT_RES = "thesaurusEnvExtRes";
	public final static String MDEK_THES_ENV_TOPICS = "thesaurusEnvTopics";
	public final static String MDEK_THES_ENV_CATS = "thesaurusEnvCats";

	// Links
	public final static String MDEK_LINKS_TO = "linksTo";
	public final static String MDEK_LINKS_FROM = "linksFrom";
}
