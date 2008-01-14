package de.ingrid.mdek.dwr;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * @author mbenz
 * 
 */
public class MdekDataBean {

	public String uuid;
	public String parentUuid;	// Only set when storing new objects
	public Boolean hasChildren;
	public String workState;

	public String title;
	public String nodeDocType;
	public String nodeAppType;
	
	// Identification 
	public String objectName;
	public Integer objectClass;
	public String objectOwner;
	public String creationTime;
	public String modificationTime;
	public String lastEditor;

	// General
	public String generalShortDescription;
	public String generalDescription;
	public ArrayList<MdekAddressBean> generalAddressTable;

	// Spatial
	public ArrayList<HashMap<String, String>> spatialRefAdminUnitTable;
	public ArrayList<HashMap<String, String>> spatialRefCoordsAdminUnitTable;
	public ArrayList<HashMap<String, String>> spatialRefLocationTable;
	public ArrayList<HashMap<String, String>> spatialRefCoordsLocationTable;
	public Double spatialRefAltMin;
	public Double spatialRefAltMax;
	public Integer spatialRefAltMeasure;
	public Integer spatialRefAltVDate;
	public String spatialRefExplanation;

	// Time
	public String timeRefType;
	public Date timeRefDate1;
	public Date timeRefDate2;
	public Integer timeRefStatus;
	public Integer timeRefPeriodicity;
	public String timeRefIntervalNum;
	public String timeRefIntervalUnit;
	public ArrayList<HashMap<String, String>> timeRefTable;
	public String timeRefExplanation;

	// ExtraInfo
	public String extraInfoLangMetaData;
	public String extraInfoLangData;
	public Integer extraInfoPublishArea;
	public ArrayList<String> extraInfoXMLExportTable;
	public ArrayList<String> extraInfoLegalBasicsTable;
	public String extraInfoPurpose;
	public String extraInfoUse;
	

	// Availability
	public ArrayList<HashMap<String, String>> availabilityDataFormatTable;
	public ArrayList<HashMap<String, String>> availabilityMediaOptionsTable;
	public String availabilityOrderInfo;
	public String availabilityNoteUse;
	public String availabilityCosts;
	
	// Thesaurus
	public ArrayList<HashMap<String, String>> thesaurusTermsTable;
	public ArrayList<String> thesaurusTopicsList;
	public ArrayList<String> thesaurusFreeTermsList;
	public Boolean thesaurusEnvExtRes;
	public ArrayList<String> thesaurusEnvTopicsList;
	public ArrayList<String> thesaurusEnvCatsList;

	// Links
	public ArrayList<MdekDataBean> linksToObjectTable;
	public ArrayList<MdekDataBean> linksFromObjectTable;
	public ArrayList<HashMap<String, String>> linksToTable;
	public ArrayList<HashMap<String, String>> linksFromTable;
	public String relationTypeName;
	public String relationDescription;
	
/*
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
	public final static String MDEK_OBJ_CLASS4_DESCRIPTION = "ref4Explanation";
	
	// Object class 5 (Vorhaben/Projekt/Programm)
	public final static String MDEK_OBJ_CLASS5_CONTENT = "ref5Scale";
	public final static String MDEK_OBJ_CLASS5_DATA = "ref5MethodLink";
	public final static String MDEK_OBJ_CLASS5_DESCRIPTION = "ref5Explanation";
	 */

	public MdekDataBean(){
		this.setGeneralAddressTable(new ArrayList<MdekAddressBean>());
		this.setLinksToObjectTable(new ArrayList<MdekDataBean>());
		this.setLinksFromObjectTable(new ArrayList<MdekDataBean>());
	}
	
	

	
	public String getRelationTypeName() {
		return relationTypeName;
	}


	public void setRelationTypeName(String relationTypeName) {
		this.relationTypeName = relationTypeName;
	}


	public ArrayList<HashMap<String, String>> getLinksToTable() {
		return linksToTable;
	}



	public void setLinksToTable(ArrayList<HashMap<String, String>> linksToTable) {
		this.linksToTable = linksToTable;
	}



	public ArrayList<HashMap<String, String>> getLinksFromTable() {
		return linksFromTable;
	}



	public void setLinksFromTable(ArrayList<HashMap<String, String>> linksFromTable) {
		this.linksFromTable = linksFromTable;
	}



	public ArrayList<HashMap<String, String>> getThesaurusTermsTable() {
		return thesaurusTermsTable;
	}



	public void setThesaurusTermsTable(
			ArrayList<HashMap<String, String>> thesaurusTermsTable) {
		this.thesaurusTermsTable = thesaurusTermsTable;
	}



	public ArrayList<String> getThesaurusTopicsList() {
		return thesaurusTopicsList;
	}



	public void setThesaurusTopicsList(ArrayList<String> thesaurusTopicsList) {
		this.thesaurusTopicsList = thesaurusTopicsList;
	}



	public ArrayList<String> getThesaurusFreeTermsList() {
		return thesaurusFreeTermsList;
	}



	public void setThesaurusFreeTermsList(ArrayList<String> thesaurusFreeTermsList) {
		this.thesaurusFreeTermsList = thesaurusFreeTermsList;
	}



	public Boolean getThesaurusEnvExtRes() {
		return thesaurusEnvExtRes;
	}



	public void setThesaurusEnvExtRes(Boolean thesaurusEnvExtRes) {
		this.thesaurusEnvExtRes = thesaurusEnvExtRes;
	}



	public ArrayList<String> getThesaurusEnvTopicsList() {
		return thesaurusEnvTopicsList;
	}



	public void setThesaurusEnvTopicsList(ArrayList<String> thesaurusEnvTopicsList) {
		this.thesaurusEnvTopicsList = thesaurusEnvTopicsList;
	}



	public ArrayList<String> getThesaurusEnvCatsList() {
		return thesaurusEnvCatsList;
	}



	public void setThesaurusEnvCatsList(ArrayList<String> thesaurusEnvCatsList) {
		this.thesaurusEnvCatsList = thesaurusEnvCatsList;
	}

	public ArrayList<MdekAddressBean> getGeneralAddressTable() {
		return generalAddressTable;
	}

	public void setGeneralAddressTable(ArrayList<MdekAddressBean> generalAddress) {
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
	}



	public ArrayList<HashMap<String, String>> getSpatialRefAdminUnitTable() {
		return spatialRefAdminUnitTable;
	}



	public void setSpatialRefAdminUnitTable(
			ArrayList<HashMap<String, String>> spatialRefAdminUnitTable) {
		this.spatialRefAdminUnitTable = spatialRefAdminUnitTable;
	}



	public ArrayList<HashMap<String, String>> getSpatialRefCoordsAdminUnitTable() {
		return spatialRefCoordsAdminUnitTable;
	}



	public void setSpatialRefCoordsAdminUnitTable(
			ArrayList<HashMap<String, String>> spatialRefCoordsAdminUnitTable) {
		this.spatialRefCoordsAdminUnitTable = spatialRefCoordsAdminUnitTable;
	}



	public ArrayList<HashMap<String, String>> getSpatialRefLocationTable() {
		return spatialRefLocationTable;
	}



	public void setSpatialRefLocationTable(
			ArrayList<HashMap<String, String>> spatialRefLocationTable) {
		this.spatialRefLocationTable = spatialRefLocationTable;
	}



	public ArrayList<HashMap<String, String>> getSpatialRefCoordsLocationTable() {
		return spatialRefCoordsLocationTable;
	}



	public void setSpatialRefCoordsLocationTable(
			ArrayList<HashMap<String, String>> spatialRefCoordsLocationTable) {
		this.spatialRefCoordsLocationTable = spatialRefCoordsLocationTable;
	}



	public Double getSpatialRefAltMin() {
		return spatialRefAltMin;
	}



	public void setSpatialRefAltMin(Double spatialRefAltMin) {
		this.spatialRefAltMin = spatialRefAltMin;
	}



	public Double getSpatialRefAltMax() {
		return spatialRefAltMax;
	}



	public void setSpatialRefAltMax(Double spatialRefAltMax) {
		this.spatialRefAltMax = spatialRefAltMax;
	}



	public Integer getSpatialRefAltMeasure() {
		return spatialRefAltMeasure;
	}



	public void setSpatialRefAltMeasure(Integer spatialRefAltMeasure) {
		this.spatialRefAltMeasure = spatialRefAltMeasure;
	}



	public Integer getSpatialRefAltVDate() {
		return spatialRefAltVDate;
	}



	public void setSpatialRefAltVDate(Integer spatialRefAltVDate) {
		this.spatialRefAltVDate = spatialRefAltVDate;
	}



	public String getSpatialRefExplanation() {
		return spatialRefExplanation;
	}



	public void setSpatialRefExplanation(String spatialRefExplanation) {
		this.spatialRefExplanation = spatialRefExplanation;
	}



	public String getTimeRefType() {
		return timeRefType;
	}



	public void setTimeRefType(String timeRefType) {
		this.timeRefType = timeRefType;
	}



	public Date getTimeRefDate1() {
		return timeRefDate1;
	}



	public void setTimeRefDate1(Date timeRefDate1) {
		this.timeRefDate1 = timeRefDate1;
	}



	public Date getTimeRefDate2() {
		return timeRefDate2;
	}



	public void setTimeRefDate2(Date timeRefDate2) {
		this.timeRefDate2 = timeRefDate2;
	}



	public Integer getTimeRefStatus() {
		return timeRefStatus;
	}



	public void setTimeRefStatus(Integer timeRefStatus) {
		this.timeRefStatus = timeRefStatus;
	}



	public Integer getTimeRefPeriodicity() {
		return timeRefPeriodicity;
	}



	public void setTimeRefPeriodicity(Integer timeRefPeriodicity) {
		this.timeRefPeriodicity = timeRefPeriodicity;
	}



	public String getTimeRefIntervalNum() {
		return timeRefIntervalNum;
	}



	public void setTimeRefIntervalNum(String timeRefIntervalNum) {
		this.timeRefIntervalNum = timeRefIntervalNum;
	}



	public String getTimeRefIntervalUnit() {
		return timeRefIntervalUnit;
	}



	public void setTimeRefIntervalUnit(String timeRefIntervalUnit) {
		this.timeRefIntervalUnit = timeRefIntervalUnit;
	}



	public ArrayList<HashMap<String, String>> getTimeRefTable() {
		return timeRefTable;
	}



	public void setTimeRefTable(ArrayList<HashMap<String, String>> timeRefTable) {
		this.timeRefTable = timeRefTable;
	}



	public String getTimeRefExplanation() {
		return timeRefExplanation;
	}



	public void setTimeRefExplanation(String timeRefExplanation) {
		this.timeRefExplanation = timeRefExplanation;
	}



	public String getExtraInfoLangMetaData() {
		return extraInfoLangMetaData;
	}



	public void setExtraInfoLangMetaData(String extraInfoLangMetaData) {
		this.extraInfoLangMetaData = extraInfoLangMetaData;
	}



	public String getExtraInfoLangData() {
		return extraInfoLangData;
	}



	public void setExtraInfoLangData(String extraInfoLangData) {
		this.extraInfoLangData = extraInfoLangData;
	}



	public Integer getExtraInfoPublishArea() {
		return extraInfoPublishArea;
	}



	public void setExtraInfoPublishArea(Integer extraInfoPublishArea) {
		this.extraInfoPublishArea = extraInfoPublishArea;
	}



	public ArrayList<String> getExtraInfoXMLExportTable() {
		return extraInfoXMLExportTable;
	}



	public void setExtraInfoXMLExport(ArrayList<String> extraInfoXMLExportTable) {
		this.extraInfoXMLExportTable = extraInfoXMLExportTable;
	}



	public ArrayList<String> getExtraInfoLegalBasicsTable() {
		return extraInfoLegalBasicsTable;
	}



	public void setExtraInfoLegalBasicsTable(ArrayList<String> extraInfoLegalBasicsTable) {
		this.extraInfoLegalBasicsTable = extraInfoLegalBasicsTable;
	}



	public String getExtraInfoPurpose() {
		return extraInfoPurpose;
	}



	public void setExtraInfoPurpose(String extraInfoPurpose) {
		this.extraInfoPurpose = extraInfoPurpose;
	}



	public String getExtraInfoUse() {
		return extraInfoUse;
	}



	public void setExtraInfoUse(String extraInfoUse) {
		this.extraInfoUse = extraInfoUse;
	}



	public ArrayList<HashMap<String, String>> getAvailabilityDataFormatTable() {
		return availabilityDataFormatTable;
	}



	public void setAvailabilityDataFormatTable(
			ArrayList<HashMap<String, String>> availabilityDataFormatTable) {
		this.availabilityDataFormatTable = availabilityDataFormatTable;
	}



	public ArrayList<HashMap<String, String>> getAvailabilityMediaOptionsTable() {
		return availabilityMediaOptionsTable;
	}



	public void setAvailabilityMediaOptionsTable(
			ArrayList<HashMap<String, String>> availabilityMediaOptionsTable) {
		this.availabilityMediaOptionsTable = availabilityMediaOptionsTable;
	}



	public String getAvailabilityOrderInfo() {
		return availabilityOrderInfo;
	}



	public void setAvailabilityOrderInfo(String availabilityOrderInfo) {
		this.availabilityOrderInfo = availabilityOrderInfo;
	}



	public String getAvailabilityNoteUse() {
		return availabilityNoteUse;
	}



	public void setAvailabilityNoteUse(String availabilityNoteUse) {
		this.availabilityNoteUse = availabilityNoteUse;
	}



	public String getAvailabilityCosts() {
		return availabilityCosts;
	}



	public void setAvailabilityCosts(String availabilityCosts) {
		this.availabilityCosts = availabilityCosts;
	}



	public void setExtraInfoXMLExportTable(ArrayList<String> extraInfoXMLExportTable) {
		this.extraInfoXMLExportTable = extraInfoXMLExportTable;
	}



	public String getUuid() {
		return uuid;
	}



	public void setUuid(String uuid) {
		this.uuid = uuid;
	}



	public String getCreationTime() {
		return creationTime;
	}



	public void setCreationTime(String creationTime) {
		this.creationTime = creationTime;
	}



	public String getModificationTime() {
		return modificationTime;
	}



	public void setModificationTime(String modificationTime) {
		this.modificationTime = modificationTime;
	}


	public String getWorkState() {
		return workState;
	}


	public void setWorkState(String workState) {
		this.workState = workState;
	}


	public String getParentUuid() {
		return parentUuid;
	}


	public void setParentUuid(String parentUuid) {
		this.parentUuid = parentUuid;
	}


	public ArrayList<MdekDataBean> getLinksToObjectTable() {
		return linksToObjectTable;
	}


	public void setLinksToObjectTable(ArrayList<MdekDataBean> linksToObjectTable) {
		this.linksToObjectTable = linksToObjectTable;
	}


	public String getRelationDescription() {
		return relationDescription;
	}


	public void setRelationDescription(String relationDescription) {
		this.relationDescription = relationDescription;
	}




	public ArrayList<MdekDataBean> getLinksFromObjectTable() {
		return linksFromObjectTable;
	}




	public void setLinksFromObjectTable(ArrayList<MdekDataBean> linksFromObjectTable) {
		this.linksFromObjectTable = linksFromObjectTable;
	};
}
