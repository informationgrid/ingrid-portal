package de.ingrid.mdek.dwr;

import java.util.HashMap;

import de.ingrid.utils.IngridDocument;

/**
 * @author mbenz
 * 
 */
public class MdekObjectData {

	// Identification 
	private final static String MDEK_OBJECT_NAME = "objectName";
	private final static String MDEK_OBJECT_CLASS = "objectClass";
	private final static String MDEK_OBJECT_OWNER = "objectOwner";
	private final static String MDEK_OBJECT_LAST_EDITOR = "last_editor";

	// General
	private final static String MDEK_GENERAL_SHORT_DESC = "generalShortDesc";
	private final static String MDEK_GENERAL_DESC = "generalDesc";
	private final static String MDEK_GENERAL_ADDRESS = "generalAddress";

	// Object class 1 (Geoinformation/Karte)
	private final static String MDEK_OBJ_CLASS1_DATASET = "ref1DataSet";
	private final static String MDEK_OBJ_CLASS1_COVERAGE = "ref1Coverage";
	private final static String MDEK_OBJ_CLASS1_REPRESENTATION = "ref1Representation";	
	private final static String MDEK_OBJ_CLASS1_VEC_TOPOLOGY = "ref1VFormatTopology";
	private final static String MDEK_OBJ_CLASS1_VEC_DETAILS = "ref1VFormatDetails";
	private final static String MDEK_OBJ_CLASS1_SPATIAL_SYSTEM = "ref1SpatialSystem";
	private final static String MDEK_OBJ_CLASS1_SCALE = "ref1Scale";
	private final static String MDEK_OBJ_CLASS1_ALT_ACC = "ref1AltAccuracy";
	private final static String MDEK_OBJ_CLASS1_POS_ACC = "ref1PosAccuracy";
	private final static String MDEK_OBJ_CLASS1_SYMBOL_CAT = "ref1SymbolsText";
	private final static String MDEK_OBJ_CLASS1_KEYS_CAT = "ref1KeysText";
	private final static String MDEK_OBJ_CLASS1_SERVICE_LINK = "ref1ServiceLink";
	private final static String MDEK_OBJ_CLASS1_BASIS_LINK = "ref1BasisLink";
	private final static String MDEK_OBJ_CLASS1_DATA_BASIS_LINK = "ref1DataBasisLink";
	private final static String MDEK_OBJ_CLASS1_DATA = "ref1Data";
	private final static String MDEK_OBJ_CLASS1_PROCESS_LINK = "ref1ProcessLink";
	
	// Object class 2 (Dokument/Bericht/Literatur)
	private final static String MDEK_OBJ_CLASS2_AUTHOR = "ref2Author";
	private final static String MDEK_OBJ_CLASS2_EDITOR = "ref2Publisher";
	private final static String MDEK_OBJ_CLASS2_PUBLISH_IN = "ref2PublishedIn";
	private final static String MDEK_OBJ_CLASS2_PUBLISH_LOC = "ref2PublishLocation";
	private final static String MDEK_OBJ_CLASS2_PUBLISH_ISSUE = "ref2PublishedInIssue";
	private final static String MDEK_OBJ_CLASS2_PUBLISH_PAGES = "ref2PublishedInPages";
	private final static String MDEK_OBJ_CLASS2_PUBLISH_YEAR = "ref2PublishedInYear";
	private final static String MDEK_OBJ_CLASS2_PUBLISH_ISBN = "ref2PublishedISBN";
	private final static String MDEK_OBJ_CLASS2_PUBLISHER = "ref2PublishedPublisher";
	private final static String MDEK_OBJ_CLASS2_LOCATION = "ref2LocationLink";
	private final static String MDEK_OBJ_CLASS2_TYPE = "ref2DocumentType";
	private final static String MDEK_OBJ_CLASS2_BASE = "ref2BaseDataLink";
	private final static String MDEK_OBJ_CLASS2_INFO = "ref2BibDataIn";
	
	// Object class 3 (Dienst/Anwendung/Informationssystem)
	private final static String MDEK_OBJ_CLASS3_SERVICE_TYPE = "ref3ServiceType";
	private final static String MDEK_OBJ_CLASS3_SERVICE_VERSION = "ref3ServiceVersion";
	private final static String MDEK_OBJ_CLASS3_SYSTEM_ENV = "ref3SystemEnv";
	private final static String MDEK_OBJ_CLASS3_HISTORY = "ref3History";
	private final static String MDEK_OBJ_CLASS3_BASE_DATA = "ref3BaseDataLink";
	private final static String MDEK_OBJ_CLASS3_DESCRIPTION = "ref3Explanation";
	private final static String MDEK_OBJ_CLASS3_OPERATION = "ref3Operation";
	
	// Object class 4 (Vorhaben/Projekt/Programm)
	private final static String MDEK_OBJ_CLASS4_MEMBER = "ref4ParticipantsLink";
	private final static String MDEK_OBJ_CLASS4_LEADER = "ref4PMLink";
	private final static String MDEK_OBJ_CLASS4_DESCRIPTION = "ref4Explanations";
	
	// Object class 5 (Vorhaben/Projekt/Programm)
	private final static String MDEK_OBJ_CLASS5_CONTENT = "ref5Scale";
	private final static String MDEK_OBJ_CLASS5_DATA = "ref5MethodLink";
	private final static String MDEK_OBJ_CLASS5_DESCRIPTION = "ref5Explanation";

	// Spatial
	private final static String MDEK_SPATIAL_ADMIN = "spatialRefAdminUnit";
	private final static String MDEK_SPATIAL_ADMIN_COORDS = "spatialRefCoordsAdminUnit";
	private final static String MDEK_SPATIAL_LOC = "spatialRefLocation";
	private final static String MDEK_SPATIAL_LOC_COORDS = "spatialRefCoordsLocation";
	private final static String MDEK_SPATIAL_ALT_MIN = "spatialRefAltMin";
	private final static String MDEK_SPATIAL_ALT_MAX = "spatialRefAltMax";
	private final static String MDEK_SPATIAL_ALT_MEASURE = "spatialRefAltMeasure";
	private final static String MDEK_SPATIAL_ALT_VDATE = "spatialRefAltVDate";
	private final static String MDEK_SPATIAL_ALT_DESCR = "spatialRefExplanation";

	// Time
	private final static String MDEK_TIME_TYPE = "timeRefType";
	private final static String MDEK_TIME_FROM = "timeRefDate1";
	private final static String MDEK_TIME_TO = "timeRefDate2";
	private final static String MDEK_TIME_STATUS = "timeRefStatus";
	private final static String MDEK_TIME_PERIOD = "timeRefPeriodicity";
	private final static String MDEK_TIME_INTERVAL = "timeRefIntervalNum";
	private final static String MDEK_TIME_UNIT = "timeRefIntervalUnit";
	private final static String MDEK_TIME_REF = "timeRef2";
	private final static String MDEK_TIME_DESCR = "timeRefExplanation";

	// ExtraInfo
	private final static String MDEK_INFO_LANG_METADATA = "extraInfoLangMetaData";
	private final static String MDEK_INFO_LANG_DATA = "extraInfoLangData";
	private final static String MDEK_INFO_PUBLISH = "extraInfoPublishArea";
	private final static String MDEK_INFO_XML_EXPORT = "extraInfoXMLExport";
	private final static String MDEK_INFO_LEGAL = "extraInfoLegalBasics";
	private final static String MDEK_INFO_PURPOSE = "extraInfoPurpose";
	private final static String MDEK_INFO_USAGE = "extraInfoUse";
	
	// Availability
	private final static String MDEK_AVAIL_FORMAT = "availabilityDataFormat";
	private final static String MDEK_AVAIL_MEDIA_OPTION = "availabilityMediaOptions";
	private final static String MDEK_AVAIL_ORDER_INFO = "availabilityOrderInfo";
	private final static String MDEK_AVAIL_NOTE = "availabilityNoteUse";
	private final static String MDEK_AVAIL_FEES = "availabilityCosts";
	
	// Thesaurus
	private final static String MDEK_THES_TERMS = "thesaurusTerms";
	private final static String MDEK_THES_TOPICS = "thesaurusTopics";
	private final static String MDEK_THES_FREE_TERMS = "thesaurusFreeTermsList";
	private final static String MDEK_THES_ENV_EXT_RES = "thesaurusEnvExtRes";
	private final static String MDEK_THES_ENV_TOPICS = "thesaurusEnvTopics";
	private final static String MDEK_THES_ENV_CATS = "thesaurusEnvCats";

	// Links
	private final static String MDEK_LINKS_TO = "linksTo";
	private final static String MDEK_LINKS_FROM = "linksFrom";


	public MdekObjectData(HashMap<String, Object> objData) {
		
	}


	public HashMap getData() {
		// TODO Convert this object to a HashMap for the Mdek gui
		return null;
	}
}
