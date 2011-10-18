package de.ingrid.mdek.beans.object;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.ingrid.mdek.beans.CommentBean;
import de.ingrid.mdek.beans.address.MdekAddressBean;
import de.ingrid.mdek.dwr.services.sns.SNSTopic;


/**
 * @author mbenz
 * 
 */
public class MdekDataBean {

	private String uuid;
	private String parentUuid;
	private Boolean hasChildren;
	private String workState;
	private Integer parentPublicationCondition;
	private Boolean isPublished;
	private Boolean isMarkedDeleted;

	private String title;
	private String nodeDocType;
	private String nodeAppType;
	private Boolean writePermission;
    private Boolean movePermission;
	private Boolean writeSinglePermission;
	private Boolean writeTreePermission;
	private Boolean writeSubNodePermission;
	private Boolean writeSubTreePermission;

	// QA Fields
	private MdekAddressBean assignerUser;
	private Date assignTime;
	private String userOperation;

	// Identification 
	private String objectName;
	private Integer objectClass;
	private String objectOwner;
	private String creationTime;
	private String modificationTime;
	private MdekAddressBean lastEditor;

	// General
	private String generalShortDescription;
	private String generalDescription;
	private List<MdekAddressBean> generalAddressTable;
	private List<CommentBean> commentTable;

	// Spatial
	private List<LocationBean> spatialRefAdminUnitTable;
	private List<LocationBean> spatialRefLocationTable;
	private Double spatialRefAltMin;
	private Double spatialRefAltMax;
	private Integer spatialRefAltMeasure;
	private String spatialRefAltVDate;
	private String spatialRefExplanation;
	/** NOTICE: moved from class 1 (Geoinformation/Karte) to spatial for all objects ! */
	private List<String> ref1SpatialSystemTable;

	// Time
	private String timeRefType;
	private Date timeRefDate1;
	private Date timeRefDate2;
	private Integer timeRefStatus;
	private Integer timeRefPeriodicity;
	private String timeRefIntervalNum;
	private String timeRefIntervalUnit;
	private List<TimeReferenceBean> timeRefTable;
	private String timeRefExplanation;

	// ExtraInfo
	private Integer extraInfoLangMetaDataCode;
	private Integer extraInfoLangDataCode;
	private Integer extraInfoPublishArea;
	private Integer extraInfoCharSetDataCode;
	private List<ConformityBean> extraInfoConformityTable;
	private List<String> extraInfoXMLExportTable;
	private List<String> extraInfoLegalBasicsTable;
	private String extraInfoPurpose;
	private String extraInfoUse;
	

	// Availability
	private List<String> availabilityAccessConstraints;
	private List<String> availabilityUseConstraints;
	private String availabilityDataFormatInspire;
	private List<DataFormatBean> availabilityDataFormatTable;
	private List<MediaOptionBean> availabilityMediaOptionsTable;
	private String availabilityOrderInfo;
	
	// Thesaurus
	private Boolean inspireRelevant;
	private List<Integer> thesaurusInspireTermsList;
	private List<SNSTopic> thesaurusTermsTable;
	private List<Integer> thesaurusTopicsList;
	private Boolean thesaurusEnvExtRes;
	private List<Integer> thesaurusEnvTopicsList;


	// Links
	private List<MdekDataBean> linksToObjectTable;
	private List<MdekDataBean> linksFromObjectTable;
	private List<MdekDataBean> linksFromPublishedObjectTable;
	private List<UrlBean> linksToUrlTable;
	private Integer relationType;
	private String relationTypeName;
	private String relationDescription;

	// Additional Fields
	private List<AdditionalFieldBean> additionalFields;

	// TODO Subclass this?
	// Object class 1 (Geoinformation/Karte)
	private String ref1ObjectIdentifier;
	private Integer ref1DataSet;
	private Double ref1Coverage;
	private List<Integer> ref1Representation;	
	private Integer ref1VFormatTopology;
	private List<VectorFormatDetailsBean> ref1VFormatDetails;
	private List<ScaleBean> ref1Scale;
	private Double ref1AltAccuracy;
	private Double ref1PosAccuracy;
	private List<LinkDataBean> ref1SymbolsText;
	private List<LinkDataBean> ref1KeysText;
	private String ref1BasisText;
	private String ref1DataBasisText;
	private String ref1ProcessText;
	private List<String> ref1Data;
	// Data Quality !
	private List<DQBean> dq109Table;
	private List<DQBean> dq110Table;
	private List<DQBean> dq112Table;
	private List<DQBean> dq113Table;
	private List<DQBean> dq114Table;
	private List<DQBean> dq115Table;
	private List<DQBean> dq117Table;
	private List<DQBean> dq120Table;
	private List<DQBean> dq125Table;
	private List<DQBean> dq126Table;
	private List<DQBean> dq127Table;

	// Object class 2 (Dokument/Bericht/Literatur)
	private String ref2Author;
	private String ref2Publisher;
	private String ref2PublishedIn;
	private String ref2PublishLocation;
	private String ref2PublishedInIssue;
	private String ref2PublishedInPages;
	private String ref2PublishedInYear;
	private String ref2PublishedISBN;
	private String ref2PublishedPublisher;
	private String ref2LocationText;
	private String ref2DocumentType;
	private String ref2BaseDataText;
	private String ref2BibData;
	private String ref2Explanation;

	// Object class 3 (Geodatendienst)
	private Integer ref3ServiceType;
	private List<Integer> ref3ServiceTypeTable;
	private List<String> ref3ServiceVersion;
	private String ref3SystemEnv;
	private String ref3History;
	private String ref3BaseDataText;
	private String ref3Explanation;
	private List<ScaleBean> ref3Scale;
	private List<OperationBean> ref3Operation;
	private Boolean ref3HasAccessConstraint;
	
	// Object class 4 (Vorhaben/Projekt/Programm)
	private String ref4ParticipantsText;
	private String ref4PMText;
	private String ref4Explanation;

	// Object class 5 (Vorhaben/Projekt/Programm)
	private List<DBContentBean> ref5dbContent;
	private String ref5MethodText;
	private String ref5Explanation;

	// Object class 6 (Dienst/Anwendung/Informationssystem)
	private Integer ref6ServiceType;
	private List<String> ref6ServiceVersion;
	private String ref6SystemEnv;
	private String ref6History;
	private String ref6BaseDataText;
	private String ref6Explanation;
	private List<ApplicationUrlBean> ref6UrlList;
	

	public List<DBContentBean> getRef5dbContent() {
		return ref5dbContent;
	}




	public void setRef5dbContent(List<DBContentBean> ref5dbContent) {
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



	public List<String> getRef3ServiceVersion() {
		return ref3ServiceVersion;
	}




	public void setRef3ServiceVersion(List<String> ref3ServiceVersion) {
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




	public List<OperationBean> getRef3Operation() {
		return ref3Operation;
	}




	public void setRef3Operation(List<OperationBean> ref3Operation) {
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
		this.setExtraInfoConformityTable(new ArrayList<ConformityBean>());
		this.setExtraInfoXMLExportTable(new ArrayList<String>());
		this.setExtraInfoLegalBasicsTable(new ArrayList<String>());
		this.setAvailabilityDataFormatTable(new ArrayList<DataFormatBean>());
		this.setAvailabilityMediaOptionsTable(new ArrayList<MediaOptionBean>());
		this.setThesaurusInspireTermsList(new ArrayList<Integer>());
		this.setThesaurusTermsTable(new ArrayList<SNSTopic>());
		this.setThesaurusTopicsList(new ArrayList<Integer>());
		this.setThesaurusEnvTopicsList(new ArrayList<Integer>());
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
		this.setRef1SpatialSystemTable(new ArrayList<String>());

		this.setDq109Table(new ArrayList<DQBean>());
		this.setDq110Table(new ArrayList<DQBean>());
		this.setDq112Table(new ArrayList<DQBean>());
		this.setDq113Table(new ArrayList<DQBean>());
		this.setDq114Table(new ArrayList<DQBean>());
		this.setDq115Table(new ArrayList<DQBean>());
		this.setDq117Table(new ArrayList<DQBean>());
		this.setDq120Table(new ArrayList<DQBean>());
		this.setDq125Table(new ArrayList<DQBean>());
		this.setDq126Table(new ArrayList<DQBean>());
		this.setDq127Table(new ArrayList<DQBean>());

		this.setRef3ServiceTypeTable(new ArrayList<Integer>());
		this.setRef3ServiceVersion(new ArrayList<String>());
		this.setRef3Scale(new ArrayList<ScaleBean>());
		this.setRef3Operation(new ArrayList<OperationBean>());
		this.setRef5dbContent(new ArrayList<DBContentBean>());
		this.setRef6ServiceVersion(new ArrayList<String>());
		this.setRef6UrlList(new ArrayList<ApplicationUrlBean>());
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



	public List<Integer> getThesaurusEnvTopicsList() {
		return thesaurusEnvTopicsList;
	}



	public void setThesaurusEnvTopicsList(List<Integer> thesaurusEnvTopicsList) {
		this.thesaurusEnvTopicsList = thesaurusEnvTopicsList;
	}



	public List<MdekAddressBean> getGeneralAddressTable() {
		return generalAddressTable;
	}

	public void setGeneralAddressTable(List<MdekAddressBean> generalAddress) {
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



	public List<LocationBean> getSpatialRefAdminUnitTable() {
		return spatialRefAdminUnitTable;
	}



	public void setSpatialRefAdminUnitTable(
			List<LocationBean> spatialRefAdminUnitTable) {
		this.spatialRefAdminUnitTable = spatialRefAdminUnitTable;
	}



	public List<LocationBean> getSpatialRefLocationTable() {
		return spatialRefLocationTable;
	}



	public void setSpatialRefLocationTable(
			List<LocationBean> spatialRefLocationTable) {
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



	public String getSpatialRefAltVDate() {
		return spatialRefAltVDate;
	}



	public void setSpatialRefAltVDate(String string) {
		this.spatialRefAltVDate = string;
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


	public Integer getExtraInfoLangMetaDataCode() {
		return extraInfoLangMetaDataCode;
	}



	public void setExtraInfoLangMetaDataCode(Integer extraInfoLangMetaData) {
		this.extraInfoLangMetaDataCode = extraInfoLangMetaData;
	}



	public Integer getExtraInfoLangDataCode() {
		return extraInfoLangDataCode;
	}



	public void setExtraInfoLangDataCode(Integer extraInfoLangData) {
		this.extraInfoLangDataCode = extraInfoLangData;
	}


	public Integer getExtraInfoPublishArea() {
		return extraInfoPublishArea;
	}



	public void setExtraInfoPublishArea(Integer extraInfoPublishArea) {
		this.extraInfoPublishArea = extraInfoPublishArea;
	}

	public Integer getExtraInfoCharSetDataCode() {
		return extraInfoCharSetDataCode;
	}



	public void setExtraInfoCharSetDataCode(Integer extraInfoCharSetData) {
		this.extraInfoCharSetDataCode = extraInfoCharSetData;
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


	public List<MdekDataBean> getLinksToObjectTable() {
		return linksToObjectTable;
	}


	public void setLinksToObjectTable(List<MdekDataBean> linksToObjectTable) {
		this.linksToObjectTable = linksToObjectTable;
	}


	public String getRelationDescription() {
		return relationDescription;
	}


	public void setRelationDescription(String relationDescription) {
		this.relationDescription = relationDescription;
	}




	public List<MdekDataBean> getLinksFromObjectTable() {
		return linksFromObjectTable;
	}




	public void setLinksFromObjectTable(List<MdekDataBean> linksFromObjectTable) {
		this.linksFromObjectTable = linksFromObjectTable;
	}




	public List<UrlBean> getLinksToUrlTable() {
		return linksToUrlTable;
	}




	public void setLinksToUrlTable(List<UrlBean> linksToUrlTable) {
		this.linksToUrlTable = linksToUrlTable;
	}




	public List<TimeReferenceBean> getTimeRefTable() {
		return timeRefTable;
	}




	public void setTimeRefTable(List<TimeReferenceBean> timeRefTable) {
		this.timeRefTable = timeRefTable;
	}




	public String getAvailabilityDataFormatInspire() {
		return availabilityDataFormatInspire;
	}




	public void setAvailabilityDataFormatInspire(
			String availabilityDataFormatInspire) {
		this.availabilityDataFormatInspire = availabilityDataFormatInspire;
	}




	public List<DataFormatBean> getAvailabilityDataFormatTable() {
		return availabilityDataFormatTable;
	}




	public void setAvailabilityDataFormatTable(
			List<DataFormatBean> availabilityDataFormatTable) {
		this.availabilityDataFormatTable = availabilityDataFormatTable;
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




	public List<Integer> getRef1Representation() {
		return ref1Representation;
	}




	public void setRef1Representation(List<Integer> ref1Representation) {
		this.ref1Representation = ref1Representation;
	}




	public Integer getRef1VFormatTopology() {
		return ref1VFormatTopology;
	}




	public void setRef1VFormatTopology(Integer ref1VFormatTopology) {
		this.ref1VFormatTopology = ref1VFormatTopology;
	}




	public List<VectorFormatDetailsBean> getRef1VFormatDetails() {
		return ref1VFormatDetails;
	}




	public void setRef1VFormatDetails(
			List<VectorFormatDetailsBean> ref1VFormatDetails) {
		this.ref1VFormatDetails = ref1VFormatDetails;
	}





	public List<ScaleBean> getRef1Scale() {
		return ref1Scale;
	}




	public void setRef1Scale(List<ScaleBean> ref1Scale) {
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




	public List<LinkDataBean> getRef1SymbolsText() {
		return ref1SymbolsText;
	}




	public void setRef1SymbolsText(List<LinkDataBean> ref1SymbolsText) {
		this.ref1SymbolsText = ref1SymbolsText;
	}




	public List<LinkDataBean> getRef1KeysText() {
		return ref1KeysText;
	}




	public void setRef1KeysText(List<LinkDataBean> ref1KeysText) {
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




	public List<String> getRef1Data() {
		return ref1Data;
	}




	public void setRef1Data(List<String> ref1Data) {
		this.ref1Data = ref1Data;
	}




	public String getRef3BaseDataText() {
		return ref3BaseDataText;
	}




	public void setRef3BaseDataText(String ref3BaseDataText) {
		this.ref3BaseDataText = ref3BaseDataText;
	}




	public List<CommentBean> getCommentTable() {
		return commentTable;
	}




	public void setCommentTable(List<CommentBean> commentTable) {
		this.commentTable = commentTable;
	}




	public List<MediaOptionBean> getAvailabilityMediaOptionsTable() {
		return availabilityMediaOptionsTable;
	}




	public void setAvailabilityMediaOptionsTable(
			List<MediaOptionBean> availabilityMediaOptionsTable) {
		this.availabilityMediaOptionsTable = availabilityMediaOptionsTable;
	}




	public List<SNSTopic> getThesaurusTermsTable() {
		return thesaurusTermsTable;
	}




	public void setThesaurusTermsTable(List<SNSTopic> thesaurusTermsTable) {
		this.thesaurusTermsTable = thesaurusTermsTable;
	}




	public List<Integer> getThesaurusTopicsList() {
		return thesaurusTopicsList;
	}




	public void setThesaurusTopicsList(List<Integer> thesaurusTopicsList) {
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




	public List<String> getRef1SpatialSystemTable() {
		return ref1SpatialSystemTable;
	}




	public void setRef1SpatialSystemTable(List<String> ref1SpatialSystem) {
		this.ref1SpatialSystemTable = ref1SpatialSystem;
	}




	public List<String> getExtraInfoXMLExportTable() {
		return extraInfoXMLExportTable;
	}




	public void setExtraInfoXMLExportTable(List<String> extraInfoXMLExportTable) {
		this.extraInfoXMLExportTable = extraInfoXMLExportTable;
	}




	public List<String> getExtraInfoLegalBasicsTable() {
		return extraInfoLegalBasicsTable;
	}




	public void setExtraInfoLegalBasicsTable(
			List<String> extraInfoLegalBasicsTable) {
		this.extraInfoLegalBasicsTable = extraInfoLegalBasicsTable;
	}




	public List<MdekDataBean> getLinksFromPublishedObjectTable() {
		return linksFromPublishedObjectTable;
	}




	public void setLinksFromPublishedObjectTable(
			List<MdekDataBean> linksFromPublishedObjectTable) {
		this.linksFromPublishedObjectTable = linksFromPublishedObjectTable;
	}




	public Boolean getWritePermission() {
		return writePermission;
	}




	public void setWritePermission(Boolean writePermission) {
		this.writePermission = writePermission;
	}


	


	public Boolean getMovePermission() {
        return movePermission;
    }




    public void setMovePermission(Boolean movePermission) {
        this.movePermission = movePermission;
    }




    public Boolean getWriteSinglePermission() {
		return writeSinglePermission;
	}




	public void setWriteSinglePermission(Boolean writeSinglePermission) {
		this.writeSinglePermission = writeSinglePermission;
	}




	public Boolean getWriteTreePermission() {
		return writeTreePermission;
	}




	public void setWriteTreePermission(Boolean writeTreePermission) {
		this.writeTreePermission = writeTreePermission;
	}




	public Boolean getIsPublished() {
		return isPublished;
	}




	public void setIsPublished(Boolean isPublished) {
		this.isPublished = isPublished;
	}




	public List<ConformityBean> getExtraInfoConformityTable() {
		return extraInfoConformityTable;
	}




	public void setExtraInfoConformityTable(
			List<ConformityBean> extraInfoConformityTable) {
		this.extraInfoConformityTable = extraInfoConformityTable;
	}




	public String getRef1ObjectIdentifier() {
		return ref1ObjectIdentifier;
	}




	public void setRef1ObjectIdentifier(String ref1ObjectIdentifier) {
		this.ref1ObjectIdentifier = ref1ObjectIdentifier;
	}




	public List<String> getAvailabilityAccessConstraints() {
		return availabilityAccessConstraints;
	}




	public void setAvailabilityAccessConstraints(
			List<String> availabilityAccessConstraints) {
		this.availabilityAccessConstraints = availabilityAccessConstraints;
	}




	public List<String> getAvailabilityUseConstraints() {
		return availabilityUseConstraints;
	}




	public void setAvailabilityUseConstraints(
			List<String> availabilityUseConstraints) {
		this.availabilityUseConstraints = availabilityUseConstraints;
	}




	public List<Integer> getRef3ServiceTypeTable() {
		return ref3ServiceTypeTable;
	}




	public void setRef3ServiceTypeTable(List<Integer> ref3ServiceTypeTable) {
		this.ref3ServiceTypeTable = ref3ServiceTypeTable;
	}




	public List<ScaleBean> getRef3Scale() {
		return ref3Scale;
	}




	public void setRef3Scale(List<ScaleBean> ref3Scale) {
		this.ref3Scale = ref3Scale;
	}




	public Integer getRef3ServiceType() {
		return ref3ServiceType;
	}




	public void setRef3ServiceType(Integer ref3ServiceType) {
		this.ref3ServiceType = ref3ServiceType;
	}




	public Boolean getWriteSubNodePermission() {
		return writeSubNodePermission;
	}




	public void setWriteSubNodePermission(Boolean writeSubNodePermission) {
		this.writeSubNodePermission = writeSubNodePermission;
	}




	public Boolean getWriteSubTreePermission() {
		return writeSubTreePermission;
	}




	public void setWriteSubTreePermission(Boolean writeSubTreePermission) {
		this.writeSubTreePermission = writeSubTreePermission;
	}




	public MdekAddressBean getAssignerUser() {
		return assignerUser;
	}




	public void setAssignerUser(MdekAddressBean assignerUser) {
		this.assignerUser = assignerUser;
	}




	public Date getAssignTime() {
		return assignTime;
	}




	public void setAssignTime(Date assignTime) {
		this.assignTime = assignTime;
	}




	public String getUserOperation() {
		return userOperation;
	}




	public void setUserOperation(String userOperation) {
		this.userOperation = userOperation;
	}




	public Boolean getIsMarkedDeleted() {
		return isMarkedDeleted;
	}




	public void setIsMarkedDeleted(Boolean isMarkedDeleted) {
		this.isMarkedDeleted = isMarkedDeleted;
	}




	public List<AdditionalFieldBean> getAdditionalFields() {
		return additionalFields;
	}




	public void setAdditionalFields(List<AdditionalFieldBean> additionalFields) {
		this.additionalFields = additionalFields;
	}




	public List<Integer> getThesaurusInspireTermsList() {
		return thesaurusInspireTermsList;
	}




	public void setThesaurusInspireTermsList(List<Integer> thesaurusInspireTermsList) {
		this.thesaurusInspireTermsList = thesaurusInspireTermsList;
	}

	
	public Boolean getRef3HasAccessConstraint() {
		return ref3HasAccessConstraint;
	}


	public void setRef3HasAccessConstraint(Boolean ref3HasAccessConstraint) {
		this.ref3HasAccessConstraint = ref3HasAccessConstraint;
	}




	public Integer getRef6ServiceType() {
		return ref6ServiceType;
	}




	public void setRef6ServiceType(Integer ref6ServiceType) {
		this.ref6ServiceType = ref6ServiceType;
	}




	public List<String> getRef6ServiceVersion() {
		return ref6ServiceVersion;
	}




	public void setRef6ServiceVersion(List<String> ref6ServiceVersion) {
		this.ref6ServiceVersion = ref6ServiceVersion;
	}




	public String getRef6SystemEnv() {
		return ref6SystemEnv;
	}




	public void setRef6SystemEnv(String ref6SystemEnv) {
		this.ref6SystemEnv = ref6SystemEnv;
	}




	public String getRef6History() {
		return ref6History;
	}




	public void setRef6History(String ref6History) {
		this.ref6History = ref6History;
	}




	public String getRef6BaseDataText() {
		return ref6BaseDataText;
	}




	public void setRef6BaseDataText(String ref6BaseDataText) {
		this.ref6BaseDataText = ref6BaseDataText;
	}




	public String getRef6Explanation() {
		return ref6Explanation;
	}




	public void setRef6Explanation(String ref6Explanation) {
		this.ref6Explanation = ref6Explanation;
	}




	public List<ApplicationUrlBean> getRef6UrlList() {
		return ref6UrlList;
	}




	public void setRef6UrlList(List<ApplicationUrlBean> ref6UrlList) {
		this.ref6UrlList = ref6UrlList;
	}


	public List<DQBean> getDq109Table() {
		return dq109Table;
	}
	public void setDq109Table(List<DQBean> dq109Table) {
		this.dq109Table = dq109Table;
	}

	public List<DQBean> getDq110Table() {
		return dq110Table;
	}
	public void setDq110Table(List<DQBean> dq110Table) {
		this.dq110Table = dq110Table;
	}

	public List<DQBean> getDq112Table() {
		return dq112Table;
	}
	public void setDq112Table(List<DQBean> dq112Table) {
		this.dq112Table = dq112Table;
	}

	public List<DQBean> getDq113Table() {
		return dq113Table;
	}
	public void setDq113Table(List<DQBean> dq113Table) {
		this.dq113Table = dq113Table;
	}

	public List<DQBean> getDq114Table() {
		return dq114Table;
	}
	public void setDq114Table(List<DQBean> dq114Table) {
		this.dq114Table = dq114Table;
	}

	public List<DQBean> getDq115Table() {
		return dq115Table;
	}
	public void setDq115Table(List<DQBean> dq115Table) {
		this.dq115Table = dq115Table;
	}

	public List<DQBean> getDq117Table() {
		return dq117Table;
	}
	public void setDq117Table(List<DQBean> dq117Table) {
		this.dq117Table = dq117Table;
	}

	public List<DQBean> getDq120Table() {
		return dq120Table;
	}
	public void setDq120Table(List<DQBean> dq120Table) {
		this.dq120Table = dq120Table;
	}

	public List<DQBean> getDq125Table() {
		return dq125Table;
	}
	public void setDq125Table(List<DQBean> dq125Table) {
		this.dq125Table = dq125Table;
	}

	public List<DQBean> getDq126Table() {
		return dq126Table;
	}
	public void setDq126Table(List<DQBean> dq126Table) {
		this.dq126Table = dq126Table;
	}

	public List<DQBean> getDq127Table() {
		return dq127Table;
	}
	public void setDq127Table(List<DQBean> dq127Table) {
		this.dq127Table = dq127Table;
	}




    public void setInspireRelevant(Boolean inspireRelevant) {
        this.inspireRelevant = inspireRelevant;
    }




    public Boolean getInspireRelevant() {
        return inspireRelevant;
    }
}
