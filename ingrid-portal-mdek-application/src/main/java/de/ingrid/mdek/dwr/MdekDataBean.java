package de.ingrid.mdek.dwr;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author mbenz
 * 
 */
public class MdekDataBean {

	public String id;
	public Boolean hasChildren;

	public String title;
	public String nodeDocType;
	public String nodeAppType;
	
	// Identification 
	public String objectName;
	public Integer objectClass;
	public String objectOwner;
	public String lastEditor;

	// General
	public String generalShortDescription;
	public String generalDescription;
	public ArrayList<HashMap<String, String>> generalAddressTable;

	/*
	// Spatial
	public String spatialRefAdminUnit;
	public String spatialRefCoordsAdminUnit;
	public String spatialRefLocation;
	public String spatialRefCoordsLocation;
	public String spatialRefAltMin;
	public String spatialRefAltMax;
	public String spatialRefAltMeasure;
	public String spatialRefAltVDate;
	public String spatialRefExplanation;

	// Time
	public String timeRefType;
	public String timeRefDate1;
	public String timeRefDate2;
	public String timeRefStatus;
	public String timeRefPeriodicity;
	public String timeRefIntervalNum;
	public String timeRefIntervalUnit;
	public String timeRef2;
	public String timeRefExplanation;

	// ExtraInfo
	public String extraInfoLangMetaData;
	public String extraInfoLangData;
	public String extraInfoPublishArea;
	public String extraInfoXMLExport;
	public String extraInfoLegalBasics;
	public String extraInfoPurpose;
	public String extraInfoUse;
	
	// Availability
	public String availabilityDataFormat;
	public String availabilityMediaOptions;
	public String availabilityOrderInfo;
	public String availabilityNoteUse;
	public String availabilityCosts;
	
	// Thesaurus
	public String thesaurusTerms;
	public String thesaurusTopics;
	public String thesaurusFreeTermsList;
	public String thesaurusEnvExtRes;
	public String thesaurusEnvTopics;
	public String thesaurusEnvCats;

	// Links
	public String linksTo;
	public String linksFrom;


	// TODO Subclass this?
	// Object class 1 (Geoinformation/Karte)
	public String ref1DataSet;
	public String ref1Coverage;
	public String ref1Representation;	
	public String ref1VFormatTopology;
	public String ref1VFormatDetails;
	public String ref1SpatialSystem;
	public String ref1Scale;
	public String ref1AltAccuracy;
	public String ref1PosAccuracy;
	public String ref1SymbolsText;
	public String ref1KeysText;
	public String ref1ServiceLink;
	public String ref1BasisLink;
	public String ref1DataBasisLink;
	public String ref1Data;
	public String ref1ProcessLink;
	
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
	public final static String MDEK_OBJ_CLASS4_DESCRIPTION = "ref4Explanations";
	
	// Object class 5 (Vorhaben/Projekt/Programm)
	public final static String MDEK_OBJ_CLASS5_CONTENT = "ref5Scale";
	public final static String MDEK_OBJ_CLASS5_DATA = "ref5MethodLink";
	public final static String MDEK_OBJ_CLASS5_DESCRIPTION = "ref5Explanation";
	 */
	
	
	
	public MdekDataBean(){}

	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ArrayList<HashMap<String, String>> getGeneralAddressTable() {
		return generalAddressTable;
	}

	public void setGeneralAddressTable(ArrayList<HashMap<String, String>> generalAddress) {
		this.generalAddressTable = generalAddress;
	}

	public Boolean getHasChildren() {
		return hasChildren;
	}

	public void setHasChildren(Boolean hasChildren) {
		this.hasChildren = hasChildren;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getNodeDocType() {
		return nodeDocType;
	}

	public void setNodeDocType(String nodeDocType) {
		this.nodeDocType = nodeDocType;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public Integer getObjectClass() {
		return objectClass;
	}

	public void setObjectClass(Integer objectClass) {
		this.objectClass = objectClass;
	}

	public String getObjectOwner() {
		return objectOwner;
	}

	public void setObjectOwner(String objectOwner) {
		this.objectOwner = objectOwner;
	}

	public String getLastEditor() {
		return lastEditor;
	}

	public void setLastEditor(String lastEditor) {
		this.lastEditor = lastEditor;
	}

	public String getGeneralShortDescription() {
		return generalShortDescription;
	}

	public void setGeneralShortDescription(String generalShortDescription) {
		this.generalShortDescription = generalShortDescription;
	}

	public String getGeneralDescription() {
		return generalDescription;
	}

	public void setGeneralDescription(String generalDescription) {
		this.generalDescription = generalDescription;
	}

	public String getNodeAppType() {
		return nodeAppType;
	}

	public void setNodeAppType(String nodeAppType) {
		this.nodeAppType = nodeAppType;
	};
}
