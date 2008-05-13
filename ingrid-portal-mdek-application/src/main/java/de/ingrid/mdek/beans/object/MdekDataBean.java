package de.ingrid.mdek.beans.object;

import java.util.ArrayList;
import java.util.Date;

import de.ingrid.mdek.beans.CommentBean;
import de.ingrid.mdek.beans.address.MdekAddressBean;
import de.ingrid.mdek.dwr.services.sns.SNSTopic;


/**
 * @author mbenz
 * 
 */
public class MdekDataBean {

	public String uuid;
	public String parentUuid;
	public Boolean hasChildren;
	public String workState;
	public Integer parentPublicationCondition;
	
	public String title;
	public String nodeDocType;
	public String nodeAppType;
	
	// Identification 
	public String objectName;
	public Integer objectClass;
	public String objectOwner;
	public String creationTime;
	public String modificationTime;
	public MdekAddressBean lastEditor;

	// General
	public String generalShortDescription;
	public String generalDescription;
	public ArrayList<MdekAddressBean> generalAddressTable;
	public ArrayList<CommentBean> commentTable;

	// Spatial
	public ArrayList<LocationBean> spatialRefAdminUnitTable;
	public ArrayList<LocationBean> spatialRefLocationTable;
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
	public ArrayList<TimeReferenceBean> timeRefTable;
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
	public ArrayList<DataFormatBean> availabilityDataFormatTable;
	public ArrayList<MediaOptionBean> availabilityMediaOptionsTable;
	public String availabilityOrderInfo;
	public String availabilityNoteUse;
	public String availabilityCosts;
	
	// Thesaurus
	public ArrayList<SNSTopic> thesaurusTermsTable;
	public ArrayList<String> thesaurusFreeTermsTable;
	public ArrayList<Integer> thesaurusTopicsList;
	public Boolean thesaurusEnvExtRes;
	public ArrayList<Integer> thesaurusEnvTopicsList;
	public ArrayList<Integer> thesaurusEnvCatsList;

	// Links
	public ArrayList<MdekDataBean> linksToObjectTable;
	public ArrayList<MdekDataBean> linksFromObjectTable;
	public ArrayList<MdekDataBean> linksFromPublishedObjectTable;
	public ArrayList<UrlBean> linksToUrlTable;
	public Integer relationType;
	public String relationTypeName;
	public String relationDescription;
	
	// TODO Subclass this?
	// Object class 1 (Geoinformation/Karte)
	public Integer ref1DataSet;
	public Double ref1Coverage;
	public ArrayList<Integer> ref1Representation;	
	public Integer ref1VFormatTopology;
	public ArrayList<VectorFormatDetailsBean> ref1VFormatDetails;
	public String ref1SpatialSystem;
	public ArrayList<ScaleBean> ref1Scale;
	public Double ref1AltAccuracy;
	public Double ref1PosAccuracy;
	public ArrayList<LinkDataBean> ref1SymbolsText;
	public ArrayList<LinkDataBean> ref1KeysText;
	public String ref1BasisText;
	public String ref1DataBasisText;
	public String ref1ProcessText;
	public ArrayList<String> ref1Data;


	// Object class 2 (Dokument/Bericht/Literatur)
	public String ref2Author;
	public String ref2Publisher;
	public String ref2PublishedIn;
	public String ref2PublishLocation;
	public String ref2PublishedInIssue;
	public String ref2PublishedInPages;
	public String ref2PublishedInYear;
	public String ref2PublishedISBN;
	public String ref2PublishedPublisher;
	public String ref2LocationText;
	public String ref2DocumentType;
	public String ref2BaseDataText;
	public String ref2BibData;
	public String ref2Explanation;

	// Object class 3 (Dienst/Anwendung/Informationssystem)
	public String ref3ServiceType;
	public ArrayList<String> ref3ServiceVersion;
	public String ref3SystemEnv;
	public String ref3History;
	public String ref3BaseDataText;
	public String ref3Explanation;
	public ArrayList<OperationBean> ref3Operation;
	
	// Object class 4 (Vorhaben/Projekt/Programm)
	public String ref4ParticipantsText;
	public String ref4PMText;
	public String ref4Explanation;

	// Object class 5 (Vorhaben/Projekt/Programm)
	public ArrayList<DBContentBean> ref5dbContent;
	public String ref5MethodText;
	public String ref5Explanation;

	

	public ArrayList<DBContentBean> getRef5dbContent() {
		return ref5dbContent;
	}




	public void setRef5dbContent(ArrayList<DBContentBean> ref5dbContent) {
		this.ref5dbContent = ref5dbContent;
	}




	public String getRef5MethodText() {
		return ref5MethodText;
	}




	public void setRef5MethodText(String ref5MethodText) {
		this.ref5MethodText = ref5MethodText;
	}




	public String getRef5Explanation() {
		return ref5Explanation;
	}




	public void setRef5Explanation(String ref5Explanation) {
		this.ref5Explanation = ref5Explanation;
	}




	public String getRef4ParticipantsText() {
		return ref4ParticipantsText;
	}




	public void setRef4ParticipantsText(String ref4ParticipantsText) {
		this.ref4ParticipantsText = ref4ParticipantsText;
	}




	public String getRef4PMText() {
		return ref4PMText;
	}




	public void setRef4PMText(String ref4PMText) {
		this.ref4PMText = ref4PMText;
	}




	public String getRef4Explanation() {
		return ref4Explanation;
	}




	public void setRef4Explanation(String ref4Explanation) {
		this.ref4Explanation = ref4Explanation;
	}



	public ArrayList<String> getRef3ServiceVersion() {
		return ref3ServiceVersion;
	}




	public void setRef3ServiceVersion(ArrayList<String> ref3ServiceVersion) {
		this.ref3ServiceVersion = ref3ServiceVersion;
	}




	public String getRef3SystemEnv() {
		return ref3SystemEnv;
	}




	public void setRef3SystemEnv(String ref3SystemEnv) {
		this.ref3SystemEnv = ref3SystemEnv;
	}




	public String getRef3History() {
		return ref3History;
	}




	public void setRef3History(String ref3History) {
		this.ref3History = ref3History;
	}



	public String getRef3Explanation() {
		return ref3Explanation;
	}




	public void setRef3Explanation(String ref3Explanation) {
		this.ref3Explanation = ref3Explanation;
	}




	public ArrayList<OperationBean> getRef3Operation() {
		return ref3Operation;
	}




	public void setRef3Operation(ArrayList<OperationBean> ref3Operation) {
		this.ref3Operation = ref3Operation;
	}




	public String getRef2Author() {
		return ref2Author;
	}




	public void setRef2Author(String ref2Author) {
		this.ref2Author = ref2Author;
	}




	public String getRef2Publisher() {
		return ref2Publisher;
	}




	public void setRef2Publisher(String ref2Publisher) {
		this.ref2Publisher = ref2Publisher;
	}




	public String getRef2PublishedIn() {
		return ref2PublishedIn;
	}




	public void setRef2PublishedIn(String ref2PublishedIn) {
		this.ref2PublishedIn = ref2PublishedIn;
	}




	public String getRef2PublishLocation() {
		return ref2PublishLocation;
	}




	public void setRef2PublishLocation(String ref2PublishLocation) {
		this.ref2PublishLocation = ref2PublishLocation;
	}




	public String getRef2PublishedInIssue() {
		return ref2PublishedInIssue;
	}




	public void setRef2PublishedInIssue(String ref2PublishedInIssue) {
		this.ref2PublishedInIssue = ref2PublishedInIssue;
	}




	public String getRef2PublishedInPages() {
		return ref2PublishedInPages;
	}




	public void setRef2PublishedInPages(String ref2PublishedInPages) {
		this.ref2PublishedInPages = ref2PublishedInPages;
	}




	public String getRef2PublishedInYear() {
		return ref2PublishedInYear;
	}




	public void setRef2PublishedInYear(String ref2PublishedInYear) {
		this.ref2PublishedInYear = ref2PublishedInYear;
	}




	public String getRef2PublishedISBN() {
		return ref2PublishedISBN;
	}




	public void setRef2PublishedISBN(String ref2PublishedISBN) {
		this.ref2PublishedISBN = ref2PublishedISBN;
	}




	public String getRef2PublishedPublisher() {
		return ref2PublishedPublisher;
	}




	public void setRef2PublishedPublisher(String ref2PublishedPublisher) {
		this.ref2PublishedPublisher = ref2PublishedPublisher;
	}




	public String getRef2LocationText() {
		return ref2LocationText;
	}




	public void setRef2LocationText(String ref2LocationText) {
		this.ref2LocationText = ref2LocationText;
	}



	public String getRef2BaseDataText() {
		return ref2BaseDataText;
	}




	public void setRef2BaseDataText(String ref2BaseDataText) {
		this.ref2BaseDataText = ref2BaseDataText;
	}




	public String getRef2BibData() {
		return ref2BibData;
	}




	public void setRef2BibData(String ref2BibData) {
		this.ref2BibData = ref2BibData;
	}




	public String getRef2Explanation() {
		return ref2Explanation;
	}




	public void setRef2Explanation(String ref2Explanation) {
		this.ref2Explanation = ref2Explanation;
	}




	public MdekDataBean(){
		this.setGeneralAddressTable(new ArrayList<MdekAddressBean>());
		this.setCommentTable(new ArrayList<CommentBean>());
		this.setSpatialRefAdminUnitTable(new ArrayList<LocationBean>());
		this.setSpatialRefLocationTable(new ArrayList<LocationBean>());
		this.setTimeRefTable(new ArrayList<TimeReferenceBean>());
		this.setExtraInfoXMLExportTable(new ArrayList<String>());
		this.setExtraInfoLegalBasicsTable(new ArrayList<String>());
		this.setAvailabilityDataFormatTable(new ArrayList<DataFormatBean>());
		this.setAvailabilityMediaOptionsTable(new ArrayList<MediaOptionBean>());
		this.setThesaurusTermsTable(new ArrayList<SNSTopic>());
		this.setThesaurusFreeTermsTable(new ArrayList<String>());
		this.setThesaurusTopicsList(new ArrayList<Integer>());
		this.setThesaurusEnvTopicsList(new ArrayList<Integer>());
		this.setThesaurusEnvCatsList(new ArrayList<Integer>());
		this.setLinksToObjectTable(new ArrayList<MdekDataBean>());
		this.setLinksFromObjectTable(new ArrayList<MdekDataBean>());
		this.setLinksFromPublishedObjectTable(new ArrayList<MdekDataBean>());
		this.setLinksToUrlTable(new ArrayList<UrlBean>());
		this.setRef1Representation(new ArrayList<Integer>());
		this.setRef1VFormatDetails(new ArrayList<VectorFormatDetailsBean>());
		this.setRef1Scale(new ArrayList<ScaleBean>());
		this.setRef1SymbolsText(new ArrayList<LinkDataBean>());
		this.setRef1KeysText(new ArrayList<LinkDataBean>());
		this.setRef1Data(new ArrayList<String>());
		this.setRef3ServiceVersion(new ArrayList<String>());
		this.setRef3Operation(new ArrayList<OperationBean>());
		this.setRef5dbContent(new ArrayList<DBContentBean>());
	}
	
	

	
	public String getRelationTypeName() {
		return relationTypeName;
	}


	public void setRelationTypeName(String relationTypeName) {
		this.relationTypeName = relationTypeName;
	}


	public Boolean getThesaurusEnvExtRes() {
		return thesaurusEnvExtRes;
	}



	public void setThesaurusEnvExtRes(Boolean thesaurusEnvExtRes) {
		this.thesaurusEnvExtRes = thesaurusEnvExtRes;
	}



	public ArrayList<Integer> getThesaurusEnvTopicsList() {
		return thesaurusEnvTopicsList;
	}



	public void setThesaurusEnvTopicsList(ArrayList<Integer> thesaurusEnvTopicsList) {
		this.thesaurusEnvTopicsList = thesaurusEnvTopicsList;
	}



	public ArrayList<Integer> getThesaurusEnvCatsList() {
		return thesaurusEnvCatsList;
	}



	public void setThesaurusEnvCatsList(ArrayList<Integer> thesaurusEnvCatsList) {
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

	public MdekAddressBean getLastEditor() {
		return lastEditor;
	}

	public void setLastEditor(MdekAddressBean lastEditor) {
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



	public ArrayList<LocationBean> getSpatialRefAdminUnitTable() {
		return spatialRefAdminUnitTable;
	}



	public void setSpatialRefAdminUnitTable(
			ArrayList<LocationBean> spatialRefAdminUnitTable) {
		this.spatialRefAdminUnitTable = spatialRefAdminUnitTable;
	}



	public ArrayList<LocationBean> getSpatialRefLocationTable() {
		return spatialRefLocationTable;
	}



	public void setSpatialRefLocationTable(
			ArrayList<LocationBean> spatialRefLocationTable) {
		this.spatialRefLocationTable = spatialRefLocationTable;
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
	}




	public ArrayList<UrlBean> getLinksToUrlTable() {
		return linksToUrlTable;
	}




	public void setLinksToUrlTable(ArrayList<UrlBean> linksToUrlTable) {
		this.linksToUrlTable = linksToUrlTable;
	}




	public ArrayList<TimeReferenceBean> getTimeRefTable() {
		return timeRefTable;
	}




	public void setTimeRefTable(ArrayList<TimeReferenceBean> timeRefTable) {
		this.timeRefTable = timeRefTable;
	}




	public ArrayList<DataFormatBean> getAvailabilityDataFormatTable() {
		return availabilityDataFormatTable;
	}




	public void setAvailabilityDataFormatTable(
			ArrayList<DataFormatBean> availabilityDataFormatTable) {
		this.availabilityDataFormatTable = availabilityDataFormatTable;
	}




	public ArrayList<String> getThesaurusFreeTermsTable() {
		return thesaurusFreeTermsTable;
	}




	public void setThesaurusFreeTermsTable(ArrayList<String> thesaurusFreeTermsTable) {
		this.thesaurusFreeTermsTable = thesaurusFreeTermsTable;
	}



	public Integer getRef1DataSet() {
		return ref1DataSet;
	}




	public void setRef1DataSet(Integer ref1DataSet) {
		this.ref1DataSet = ref1DataSet;
	}




	public Double getRef1Coverage() {
		return ref1Coverage;
	}




	public void setRef1Coverage(Double ref1Coverage) {
		this.ref1Coverage = ref1Coverage;
	}




	public ArrayList<Integer> getRef1Representation() {
		return ref1Representation;
	}




	public void setRef1Representation(ArrayList<Integer> ref1Representation) {
		this.ref1Representation = ref1Representation;
	}




	public Integer getRef1VFormatTopology() {
		return ref1VFormatTopology;
	}




	public void setRef1VFormatTopology(Integer ref1VFormatTopology) {
		this.ref1VFormatTopology = ref1VFormatTopology;
	}




	public ArrayList<VectorFormatDetailsBean> getRef1VFormatDetails() {
		return ref1VFormatDetails;
	}




	public void setRef1VFormatDetails(
			ArrayList<VectorFormatDetailsBean> ref1VFormatDetails) {
		this.ref1VFormatDetails = ref1VFormatDetails;
	}





	public ArrayList<ScaleBean> getRef1Scale() {
		return ref1Scale;
	}




	public void setRef1Scale(ArrayList<ScaleBean> ref1Scale) {
		this.ref1Scale = ref1Scale;
	}




	public Double getRef1AltAccuracy() {
		return ref1AltAccuracy;
	}




	public void setRef1AltAccuracy(Double ref1AltAccuracy) {
		this.ref1AltAccuracy = ref1AltAccuracy;
	}




	public Double getRef1PosAccuracy() {
		return ref1PosAccuracy;
	}




	public void setRef1PosAccuracy(Double ref1PosAccuracy) {
		this.ref1PosAccuracy = ref1PosAccuracy;
	}




	public ArrayList<LinkDataBean> getRef1SymbolsText() {
		return ref1SymbolsText;
	}




	public void setRef1SymbolsText(ArrayList<LinkDataBean> ref1SymbolsText) {
		this.ref1SymbolsText = ref1SymbolsText;
	}




	public ArrayList<LinkDataBean> getRef1KeysText() {
		return ref1KeysText;
	}




	public void setRef1KeysText(ArrayList<LinkDataBean> ref1KeysText) {
		this.ref1KeysText = ref1KeysText;
	}




	public String getRef1BasisText() {
		return ref1BasisText;
	}




	public void setRef1BasisText(String ref1BasisText) {
		this.ref1BasisText = ref1BasisText;
	}




	public String getRef1DataBasisText() {
		return ref1DataBasisText;
	}




	public void setRef1DataBasisText(String ref1DataBasisText) {
		this.ref1DataBasisText = ref1DataBasisText;
	}




	public ArrayList<String> getRef1Data() {
		return ref1Data;
	}




	public void setRef1Data(ArrayList<String> ref1Data) {
		this.ref1Data = ref1Data;
	}




	public String getRef3BaseDataText() {
		return ref3BaseDataText;
	}




	public void setRef3BaseDataText(String ref3BaseDataText) {
		this.ref3BaseDataText = ref3BaseDataText;
	}




	public ArrayList<CommentBean> getCommentTable() {
		return commentTable;
	}




	public void setCommentTable(ArrayList<CommentBean> commentTable) {
		this.commentTable = commentTable;
	}




	public ArrayList<MediaOptionBean> getAvailabilityMediaOptionsTable() {
		return availabilityMediaOptionsTable;
	}




	public void setAvailabilityMediaOptionsTable(
			ArrayList<MediaOptionBean> availabilityMediaOptionsTable) {
		this.availabilityMediaOptionsTable = availabilityMediaOptionsTable;
	}




	public ArrayList<SNSTopic> getThesaurusTermsTable() {
		return thesaurusTermsTable;
	}




	public void setThesaurusTermsTable(ArrayList<SNSTopic> thesaurusTermsTable) {
		this.thesaurusTermsTable = thesaurusTermsTable;
	}




	public ArrayList<Integer> getThesaurusTopicsList() {
		return thesaurusTopicsList;
	}




	public void setThesaurusTopicsList(ArrayList<Integer> thesaurusTopicsList) {
		this.thesaurusTopicsList = thesaurusTopicsList;
	}




	public String getRef1ProcessText() {
		return ref1ProcessText;
	}




	public void setRef1ProcessText(String ref1ProcessText) {
		this.ref1ProcessText = ref1ProcessText;
	}


	public String getRef2DocumentType() {
		return ref2DocumentType;
	}




	public void setRef2DocumentType(String ref2DocumentType) {
		this.ref2DocumentType = ref2DocumentType;
	}




	public String getRef3ServiceType() {
		return ref3ServiceType;
	}




	public void setRef3ServiceType(String ref3ServiceType) {
		this.ref3ServiceType = ref3ServiceType;
	}

	public Integer getParentPublicationCondition() {
		return parentPublicationCondition;
	}




	public void setParentPublicationCondition(Integer parentPublicationCondition) {
		this.parentPublicationCondition = parentPublicationCondition;
	}




	public Integer getRelationType() {
		return relationType;
	}




	public void setRelationType(Integer relationType) {
		this.relationType = relationType;
	}




	public String getRef1SpatialSystem() {
		return ref1SpatialSystem;
	}




	public void setRef1SpatialSystem(String ref1SpatialSystem) {
		this.ref1SpatialSystem = ref1SpatialSystem;
	}




	public ArrayList<String> getExtraInfoXMLExportTable() {
		return extraInfoXMLExportTable;
	}




	public void setExtraInfoXMLExportTable(ArrayList<String> extraInfoXMLExportTable) {
		this.extraInfoXMLExportTable = extraInfoXMLExportTable;
	}




	public ArrayList<String> getExtraInfoLegalBasicsTable() {
		return extraInfoLegalBasicsTable;
	}




	public void setExtraInfoLegalBasicsTable(
			ArrayList<String> extraInfoLegalBasicsTable) {
		this.extraInfoLegalBasicsTable = extraInfoLegalBasicsTable;
	}




	public ArrayList<MdekDataBean> getLinksFromPublishedObjectTable() {
		return linksFromPublishedObjectTable;
	}




	public void setLinksFromPublishedObjectTable(
			ArrayList<MdekDataBean> linksFromPublishedObjectTable) {
		this.linksFromPublishedObjectTable = linksFromPublishedObjectTable;
	}

}
