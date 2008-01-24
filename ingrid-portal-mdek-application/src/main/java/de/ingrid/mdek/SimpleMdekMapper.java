package de.ingrid.mdek;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

import de.ingrid.mdek.MdekUtils.WorkState;
import de.ingrid.mdek.dwr.CommentBean;
import de.ingrid.mdek.dwr.DBContentBean;
import de.ingrid.mdek.dwr.DataFormatBean;
import de.ingrid.mdek.dwr.LinkDataBean;
import de.ingrid.mdek.dwr.LocationBean;
import de.ingrid.mdek.dwr.MdekAddressBean;
import de.ingrid.mdek.dwr.MdekDataBean;
import de.ingrid.mdek.dwr.MediaOptionBean;
import de.ingrid.mdek.dwr.OperationBean;
import de.ingrid.mdek.dwr.OperationParameterBean;
import de.ingrid.mdek.dwr.ScaleBean;
import de.ingrid.mdek.dwr.TimeReferenceBean;
import de.ingrid.mdek.dwr.UrlBean;
import de.ingrid.mdek.dwr.VectorFormatDetailsBean;
import de.ingrid.mdek.dwr.sns.SNSTopic;
import de.ingrid.utils.IngridDocument;

public class SimpleMdekMapper implements DataMapperInterface {

	private final static Logger log = Logger.getLogger(SimpleMdekMapper.class);

	
	// -- Dispatch to the local private methods --
	public MdekDataBean getDetailedMdekRepresentation(Object obj) {
		if (obj instanceof HashMap)
			return getDetailedMdekRepresentation((HashMap<String, Object>) obj);
		else
			return null;		
	}
	public HashMap<String, Object> getSimpleMdekRepresentation(Object obj) {
		if (obj instanceof HashMap)
			return getSimpleMdekRepresentation((HashMap<String, Object>) obj);
		else
			return null;
	}
	// --


	private static MdekDataBean getDetailedMdekRepresentation(
			HashMap<String, Object> obj) {

		// by default, assertions are disabled at runtime.
		// Start the server with -ea to enable this method call
		try { assert testDetailedInputConformity(obj); }
		catch (AssertionError e) { e.printStackTrace(); }

		// TODO Check if we have to convert an address or object

		log.debug("Received the following object from MdekCaller:");
		printHashMap(obj);

		MdekDataBean mdekObj = new MdekDataBean();

		// General
		mdekObj.setGeneralShortDescription((String) obj.get(MdekKeys.DATASET_ALTERNATE_NAME));
		mdekObj.setGeneralDescription((String) obj.get(MdekKeys.ABSTRACT));
		mdekObj.setUuid((String) obj.get(MdekKeys.UUID));
		mdekObj.setTitle((String) obj.get(MdekKeys.TITLE));
		Integer objClass = (Integer) obj.get(MdekKeys.CLASS);
		if (objClass == null) {
			mdekObj.setObjectClass(0);
		} else {
			mdekObj.setObjectClass(objClass);
		}

		String workStateStr = (String) obj.get(MdekKeys.WORK_STATE); 
		WorkState workState = null;
		if (workStateStr != null) {
			workState = EnumUtil.mapDatabaseToEnumConst(WorkState.class, workStateStr);
		} else {
			workState = WorkState.IN_BEARBEITUNG;
		}
		mdekObj.setWorkState(StringEscapeUtils.escapeHtml(workState.toString()));
		// DocType defines the icon which is displayed in the tree view. Move this to EntryService?

		mdekObj.setHasChildren((Boolean) obj.get(MdekKeys.HAS_CHILD));
		mdekObj.setObjectName((String) obj.get(MdekKeys.TITLE));
		mdekObj.setGeneralAddressTable(mapToGeneralAddressTable((List<HashMap<String, Object>>) obj.get(MdekKeys.ADR_REFERENCES_TO)));
		mdekObj.setCreationTime(convertTimestampToDisplayDate((String) obj.get(MdekKeys.DATE_OF_CREATION)));
		mdekObj.setModificationTime(convertTimestampToDisplayDate((String) obj.get(MdekKeys.DATE_OF_LAST_MODIFICATION)));

		// Comments
		mdekObj.setCommentTable(mapToCommentTable((List<HashMap<String, Object>>) obj.get(MdekKeys.COMMENT_LIST)));
		
		// Spatial
		mdekObj.setSpatialRefAdminUnitTable(mapToSpatialRefAdminUnitTable((List<HashMap<String, Object>>) obj.get(MdekKeys.LOCATIONS)));
		mdekObj.setSpatialRefLocationTable(mapToSpatialRefLocationTable((List<HashMap<String, Object>>) obj.get(MdekKeys.LOCATIONS)));
		mdekObj.setSpatialRefAltMin((Double) obj.get(MdekKeys.VERTICAL_EXTENT_MINIMUM));
		mdekObj.setSpatialRefAltMax((Double) obj.get(MdekKeys.VERTICAL_EXTENT_MAXIMUM));
		mdekObj.setSpatialRefAltMeasure((Integer) obj.get(MdekKeys.VERTICAL_EXTENT_UNIT));
		mdekObj.setSpatialRefAltVDate((Integer) obj.get(MdekKeys.VERTICAL_EXTENT_VDATUM));
		mdekObj.setSpatialRefExplanation((String) obj.get(MdekKeys.DESCRIPTION_OF_SPATIAL_DOMAIN));

		// Time
		mdekObj.setTimeRefType((String) obj.get(MdekKeys.TIME_TYPE));
		mdekObj.setTimeRefDate1(convertTimestampToDate((String) obj.get(MdekKeys.BEGINNING_DATE)));
		mdekObj.setTimeRefDate2(convertTimestampToDate((String) obj.get(MdekKeys.ENDING_DATE)));
		mdekObj.setTimeRefStatus((Integer) obj.get(MdekKeys.TIME_STATUS));
		mdekObj.setTimeRefPeriodicity((Integer) obj.get(MdekKeys.TIME_PERIOD));
		mdekObj.setTimeRefIntervalNum((String) obj.get(MdekKeys.TIME_STEP));
		mdekObj.setTimeRefIntervalUnit((String) obj.get(MdekKeys.TIME_SCALE));
		mdekObj.setTimeRefTable((ArrayList<TimeReferenceBean>) mapToTimeRefTable((List<HashMap<String, Object>>) obj.get(MdekKeys.DATASET_REFERENCES)));
		mdekObj.setTimeRefExplanation((String) obj.get(MdekKeys.DESCRIPTION_OF_TEMPORAL_DOMAIN));

		// ExtraInfo
		mdekObj.setExtraInfoLangMetaData((String) obj.get(MdekKeys.METADATA_LANGUAGE));
		mdekObj.setExtraInfoLangData((String) obj.get(MdekKeys.DATA_LANGUAGE));
		mdekObj.setExtraInfoPublishArea((Integer) obj.get(MdekKeys.PUBLICATION_CONDITION));
		mdekObj.setExtraInfoPurpose((String) obj.get(MdekKeys.DATASET_INTENSIONS));
		mdekObj.setExtraInfoUse((String) obj.get(MdekKeys.DATASET_USAGE));
		
		ArrayList<String> strList = (ArrayList<String>) obj.get(MdekKeys.EXPORTS);
		if (strList != null)
			mdekObj.setExtraInfoXMLExportTable(strList);
		
		strList = (ArrayList<String>) obj.get(MdekKeys.LEGISLATIONS);
		if (strList != null)
			mdekObj.setExtraInfoLegalBasicsTable(strList);

		
		// Availability
		mdekObj.setAvailabilityOrderInfo((String) obj.get(MdekKeys.ORDERING_INSTRUCTIONS));
		mdekObj.setAvailabilityNoteUse((String) obj.get(MdekKeys.USE_CONSTRAINTS));
		mdekObj.setAvailabilityCosts((String) obj.get(MdekKeys.FEES));
		mdekObj.setAvailabilityDataFormatTable(mapToAvailDataFormatTable((List<HashMap<String, Object>>) obj.get(MdekKeys.DATA_FORMATS)));
		mdekObj.setAvailabilityMediaOptionsTable(mapToAvailMediaOptionsTable((List<HashMap<String, Object>>) obj.get(MdekKeys.MEDIUM_OPTIONS)));
		
		// Thesaurus
		mdekObj.setThesaurusTermsTable(mapToThesTermsTable((List<HashMap<String, Object>>) obj.get(MdekKeys.SUBJECT_TERMS)));
		mdekObj.setThesaurusFreeTermsTable(mapToThesFreeTermsTable((List<HashMap<String, Object>>) obj.get(MdekKeys.SUBJECT_TERMS)));

		ArrayList<Integer> intList = (ArrayList<Integer>) obj.get(MdekKeys.TOPIC_CATEGORIES);
		if (intList != null)
			mdekObj.setThesaurusTopicsList(intList);

		String isCatalogStr = (String) obj.get(MdekKeys.IS_CATALOG_DATA);
		if (isCatalogStr != null && isCatalogStr.equalsIgnoreCase("Y")) {
			mdekObj.setThesaurusEnvExtRes(true);
		} else {
			mdekObj.setThesaurusEnvExtRes(false);
		}

		strList = (ArrayList<String>) obj.get(MdekKeys.ENV_TOPICS);
		if (strList != null)
			mdekObj.setThesaurusEnvTopicsList(strList);
		
		strList = (ArrayList<String>) obj.get(MdekKeys.ENV_CATEGORIES);
		if (strList != null)
			mdekObj.setThesaurusEnvCatsList(strList);
		
		// Links
//		mdekObj.setLinksToTable((ArrayList<HashMap<String, String>>) mapToLinksToTable((List<HashMap<String, Object>>) obj.get(MdekKeys.MISSING)));
//		mdekObj.setLinksFromTable((ArrayList<HashMap<String, String>>) mapToLinksFromTable((List<HashMap<String, Object>>) obj.get(MdekKeys.MISSING)));
		mdekObj.setLinksToObjectTable(mapToObjectLinksTable((List<HashMap<String, Object>>) obj.get(MdekKeys.OBJ_REFERENCES_TO)));
		mdekObj.setLinksFromObjectTable(mapToObjectLinksTable((List<HashMap<String, Object>>) obj.get(MdekKeys.OBJ_REFERENCES_FROM)));
		mdekObj.setLinksToUrlTable(mapToUrlLinksTable((List<HashMap<String, Object>>) obj.get(MdekKeys.LINKAGES)));
		mdekObj.setRelationTypeName((String) obj.get(MdekKeys.RELATION_TYPE_NAME));
		mdekObj.setRelationDescription((String) obj.get(MdekKeys.RELATION_DESCRIPTION));

		switch(mdekObj.getObjectClass()) {
		case 0:	// Object of type 0 doesn't have any special values
			break;
		case 1:
			Map<String, Object> td1Map = (Map<String, Object>) obj.get(MdekKeys.TECHNICAL_DOMAIN_MAP);
			if (td1Map == null)
				break;
			mdekObj.setRef1DataSet((Integer) td1Map.get(MdekKeys.HIERARCHY_LEVEL));
			mdekObj.setRef1VFormatTopology((Integer) td1Map.get(MdekKeys.VECTOR_TOPOLOGY_LEVEL));
			mdekObj.setRef1SpatialSystem((String) td1Map.get(MdekKeys.COORDINATE_SYSTEM));
			mdekObj.setRef1SpatialSystemId((Integer) td1Map.get(MdekKeys.REFERENCESYSTEM_ID));
			mdekObj.setRef1Coverage((Double) td1Map.get(MdekKeys.DEGREE_OF_RECORD));
			mdekObj.setRef1AltAccuracy((Double) td1Map.get(MdekKeys.POS_ACCURACY_VERTICAL));
			mdekObj.setRef1PosAccuracy((Double) td1Map.get(MdekKeys.RESOLUTION));
			mdekObj.setRef1BasisText((String) td1Map.get(MdekKeys.TECHNICAL_BASE));
			mdekObj.setRef1DataBasisText((String) td1Map.get(MdekKeys.DATA));
			
			strList = (ArrayList<String>) obj.get(MdekKeys.FEATURE_TYPE_LIST);
			if (strList != null)
				mdekObj.setRef1Data(strList);
			
			intList = (ArrayList<Integer>) obj.get(MdekKeys.SPATIAL_REPRESENTATION_TYPE_LIST);
			if (intList != null)
				mdekObj.setRef1Representation(intList);
			
			mdekObj.setRef1VFormatDetails(mapToVFormatDetailsTable((List<HashMap<String, Object>>) td1Map.get(MdekKeys.GEO_VECTOR_LIST)));
			mdekObj.setRef1Scale(mapToScaleTable((List<HashMap<String, Object>>) td1Map.get(MdekKeys.PUBLICATION_SCALE_LIST)));
			mdekObj.setRef1SymbolsText(mapToSymLinkDataTable((List<HashMap<String, Object>>) td1Map.get(MdekKeys.SYMBOL_CATALOG_LIST)));
			mdekObj.setRef1KeysText(mapToKeyLinkDataTable((List<HashMap<String, Object>>) td1Map.get(MdekKeys.KEY_CATALOG_LIST)));
			mdekObj.setRef1ProcessText((String) td1Map.get(MdekKeys.METHOD_OF_PRODUCTION));
			break;
		case 2:
			Map<String, Object> td2Map = (Map<String, Object>) obj.get(MdekKeys.TECHNICAL_DOMAIN_DOCUMENT);
			if (td2Map == null)
				break;
			mdekObj.setRef2Author((String) td2Map.get(MdekKeys.AUTHOR));
			mdekObj.setRef2Publisher((String) td2Map.get(MdekKeys.EDITOR));
			mdekObj.setRef2PublishedIn((String) td2Map.get(MdekKeys.PUBLISHED_IN));
			mdekObj.setRef2PublishLocation((String) td2Map.get(MdekKeys.PUBLISHING_PLACE));
			mdekObj.setRef2PublishedInIssue((String) td2Map.get(MdekKeys.VOLUME));
			mdekObj.setRef2PublishedInPages((String) td2Map.get(MdekKeys.PAGES));
			mdekObj.setRef2PublishedInYear((String) td2Map.get(MdekKeys.YEAR));
			mdekObj.setRef2PublishedISBN((String) td2Map.get(MdekKeys.ISBN));
			mdekObj.setRef2PublishedPublisher((String) td2Map.get(MdekKeys.PUBLISHER));
			mdekObj.setRef2LocationText((String) td2Map.get(MdekKeys.LOCATION));
			mdekObj.setRef2DocumentType((String) td2Map.get(MdekKeys.TYPE_OF_DOCUMENT));
			mdekObj.setRef2BaseDataText((String) td2Map.get(MdekKeys.SOURCE));
			mdekObj.setRef2BibData((String) td2Map.get(MdekKeys.ADDITIONAL_BIBLIOGRAPHIC_INFO));
			mdekObj.setRef2Explanation((String) td2Map.get(MdekKeys.DESCRIPTION_OF_TECH_DOMAIN));
			break;
		case 3:
			Map<String, Object> td3Map = (Map<String, Object>) obj.get(MdekKeys.TECHNICAL_DOMAIN_SERVICE);
			if (td3Map == null)
				break;
			mdekObj.setRef3ServiceType((String) td3Map.get(MdekKeys.SERVICE_TYPE));
			mdekObj.setRef3SystemEnv((String) td3Map.get(MdekKeys.SYSTEM_ENVIRONMENT));
			mdekObj.setRef3History((String) td3Map.get(MdekKeys.SYSTEM_HISTORY));
			mdekObj.setRef3BaseDataText((String) td3Map.get(MdekKeys.DATABASE_OF_SYSTEM));
			mdekObj.setRef3ServiceVersion((ArrayList<String>) td3Map.get(MdekKeys.SERVICE_VERSION_LIST));
			mdekObj.setRef3Explanation((String) td3Map.get(MdekKeys.SERVICE_OPERATION_DESCRIPTION));
			mdekObj.setRef3Operation(mapToOperationTable((List<HashMap<String, Object>>) td3Map.get(MdekKeys.SERVICE_OPERATION_LIST)));

			break;
		case 4:
			Map<String, Object> td4Map = (Map<String, Object>) obj.get(MdekKeys.TECHNICAL_DOMAIN_PROJECT);
			if (td4Map == null)
				break;
			mdekObj.setRef4ParticipantsText((String) td4Map.get(MdekKeys.MEMBER_DESCRIPTION));
			mdekObj.setRef4PMText((String) td4Map.get(MdekKeys.LEADER_DESCRIPTION));
			mdekObj.setRef4Explanation((String) td4Map.get(MdekKeys.DESCRIPTION_OF_TECH_DOMAIN));
			break;
		case 5:
			Map<String, Object> td5Map = (Map<String, Object>) obj.get(MdekKeys.TECHNICAL_DOMAIN_DATASET);
			if (td5Map == null)
				break;
			mdekObj.setRef5Explanation((String) td5Map.get(MdekKeys.DESCRIPTION_OF_TECH_DOMAIN));
			mdekObj.setRef5MethodText((String) td5Map.get(MdekKeys.METHOD));
			mdekObj.setRef5dbContent(mapToDbContentTable((List<HashMap<String, Object>>) td5Map.get(MdekKeys.PARAMETERS)));
			break;
		}
		
		
		// TODO Should we move the gui specific settings to another object / to the entry service?
		mdekObj.setNodeAppType("O");
		String nodeDocType = "Class" + mdekObj.getObjectClass();
		// If workState... nodeDocType += "_"+workState ?		
		if (workState.getDbValue().equalsIgnoreCase("B")) {
			nodeDocType += "_B";
		}
		mdekObj.setNodeDocType(nodeDocType);


		return mdekObj;
	}

	private HashMap<String, Object> getSimpleMdekRepresentation(
			HashMap<String, Object> obj) {
		
		// by default, assertions are disabled at runtime.
		// Start the server with -ea to enable this method call
		try { assert testSimpleInputConformity(obj); }
		catch (AssertionError e) { e.printStackTrace(); }

		HashMap<String, Object> mdekObj = new HashMap<String, Object>();

		// The object UUID is used to identify widgets in the gui.
		// TODO attach the values to the object but don't use them to identify widgets
		mdekObj.put(MDEK_OBJECT_ID, obj.get(MdekKeys.UUID));
		mdekObj.put(MDEK_OBJECT_TITLE, obj.get(MdekKeys.TITLE));
		mdekObj.put(MDEK_OBJECT_HAS_CHILDREN, obj.get(MdekKeys.HAS_CHILD));
		mdekObj.put(MDEK_OBJECT_IS_PUBLISHED, obj.get(MdekKeys.IS_PUBLISHED));
		// DocType defines the icon which is displayed in the tree view. Move this to EntryService?
		String nodeDocType = "Class" + ((Integer) obj.get(MdekKeys.CLASS));
		String workState = (String) obj.get(MdekKeys.WORK_STATE); 
		// If workState... nodeDocType += "_"+workState ?		
		if (workState.equalsIgnoreCase("B")) {
			nodeDocType += "_B";
		}

		mdekObj.put(MDEK_OBJECT_DOCTYPE, nodeDocType);

		return mdekObj;
	}

	public Object convertFromMdekRepresentation(MdekDataBean data){
		IngridDocument udkObj = new IngridDocument();

		// General
		udkObj.put(MdekKeys.ABSTRACT, data.getGeneralDescription());
		udkObj.put(MdekKeys.DATASET_ALTERNATE_NAME, data.getGeneralShortDescription());
		udkObj.put(MdekKeys.UUID, data.getUuid());
		udkObj.put(MdekKeys.PARENT_UUID, data.getParentUuid());
		udkObj.put(MdekKeys.TITLE, data.getObjectName());
		// extrahieren des int Wertes für die Objekt-Klasse
		udkObj.put(MdekKeys.CLASS, data.getObjectClass());
//		udkObj.put(MdekKeys.HAS_CHILD, data.getHasChildren());
		udkObj.put(MdekKeys.ADR_REFERENCES_TO, mapFromGeneralAddressTable(data.getGeneralAddressTable()));

		// Comments
		udkObj.put(MdekKeys.COMMENT_LIST, mapFromCommentTable(data.getCommentTable()));
		
		// Spatial
		udkObj.put(MdekKeys.LOCATIONS, mapFromLocationTables(data.getSpatialRefAdminUnitTable(), data.getSpatialRefLocationTable()));
		udkObj.put(MdekKeys.VERTICAL_EXTENT_MINIMUM, data.getSpatialRefAltMin());
		udkObj.put(MdekKeys.VERTICAL_EXTENT_MAXIMUM, data.getSpatialRefAltMax());
		udkObj.put(MdekKeys.VERTICAL_EXTENT_UNIT, data.getSpatialRefAltMeasure());
		udkObj.put(MdekKeys.VERTICAL_EXTENT_VDATUM, data.getSpatialRefAltVDate());
		udkObj.put(MdekKeys.DESCRIPTION_OF_SPATIAL_DOMAIN, data.getSpatialRefExplanation());

		// Time
		udkObj.put(MdekKeys.TIME_TYPE, data.getTimeRefType());
		udkObj.put(MdekKeys.BEGINNING_DATE, convertDateToTimestamp(data.getTimeRefDate1()));
		udkObj.put(MdekKeys.ENDING_DATE, convertDateToTimestamp(data.getTimeRefDate2()));
		udkObj.put(MdekKeys.TIME_STATUS, data.getTimeRefStatus());
		udkObj.put(MdekKeys.TIME_PERIOD, data.getTimeRefPeriodicity());
		udkObj.put(MdekKeys.TIME_STEP, data.getTimeRefIntervalNum());
		udkObj.put(MdekKeys.TIME_SCALE, data.getTimeRefIntervalUnit());
		udkObj.put(MdekKeys.DESCRIPTION_OF_TEMPORAL_DOMAIN, data.getTimeRefExplanation());
		udkObj.put(MdekKeys.DATASET_REFERENCES, mapFromTimeRefTable(data.getTimeRefTable()));

		// ExtraInfo
		udkObj.put(MdekKeys.METADATA_LANGUAGE, data.getExtraInfoLangMetaData());
		udkObj.put(MdekKeys.DATA_LANGUAGE, data.getExtraInfoLangData());
		udkObj.put(MdekKeys.PUBLICATION_CONDITION, data.getExtraInfoPublishArea());
		udkObj.put(MdekKeys.DATASET_INTENSIONS, data.getExtraInfoPurpose());
		udkObj.put(MdekKeys.DATASET_USAGE, data.getExtraInfoUse());
		udkObj.put(MdekKeys.EXPORTS, data.getExtraInfoXMLExportTable());
		udkObj.put(MdekKeys.LEGISLATIONS, data.getExtraInfoLegalBasicsTable());


		// Availability
		udkObj.put(MdekKeys.DATA_FORMATS, mapFromAvailDataFormatTable(data.getAvailabilityDataFormatTable()));
		udkObj.put(MdekKeys.MEDIUM_OPTIONS, mapFromAvailMediaOptionsTable(data.getAvailabilityMediaOptionsTable()));
		udkObj.put(MdekKeys.ORDERING_INSTRUCTIONS, data.getAvailabilityOrderInfo());
		udkObj.put(MdekKeys.USE_CONSTRAINTS, data.getAvailabilityNoteUse());
		udkObj.put(MdekKeys.FEES, data.getAvailabilityCosts());

		//Thesaurus
		udkObj.put(MdekKeys.SUBJECT_TERMS, mapFromThesTermTables(data.getThesaurusTermsTable(), data.getThesaurusFreeTermsTable()));
		udkObj.put(MdekKeys.TOPIC_CATEGORIES, data.getThesaurusTopicsList());
		udkObj.put(MdekKeys.ENV_TOPICS, data.getThesaurusEnvTopicsList());
		udkObj.put(MdekKeys.ENV_CATEGORIES, data.getThesaurusEnvCatsList());
		if (data.getThesaurusEnvExtRes() != null) {
			if (data.getThesaurusEnvExtRes().booleanValue()) {
				udkObj.put(MdekKeys.IS_CATALOG_DATA, "Y");
			} else {
				udkObj.put(MdekKeys.IS_CATALOG_DATA, "N");
			}
		}

		// Links
		udkObj.put(MdekKeys.OBJ_REFERENCES_TO, mapFromObjectLinksTable(data.getLinksToObjectTable()));
		udkObj.put(MdekKeys.LINKAGES, mapFromUrlLinksTable(data.getLinksToUrlTable()));
		udkObj.put(MdekKeys.OBJ_REFERENCES_FROM, mapFromObjectLinksTable(data.getLinksFromObjectTable()));
		udkObj.put(MdekKeys.RELATION_TYPE_NAME, data.getRelationTypeName());
		udkObj.put(MdekKeys.RELATION_DESCRIPTION, data.getRelationDescription());

		switch(data.getObjectClass()) {
		case 0:	// Object of type 0 doesn't have any special values
			break;
		case 1:
			IngridDocument td1Map = new IngridDocument();			
			td1Map.put(MdekKeys.HIERARCHY_LEVEL, data.getRef1DataSet());
			td1Map.put(MdekKeys.VECTOR_TOPOLOGY_LEVEL, data.getRef1VFormatTopology());
			td1Map.put(MdekKeys.COORDINATE_SYSTEM, data.getRef1SpatialSystem());
			td1Map.put(MdekKeys.REFERENCESYSTEM_ID, data.getRef1SpatialSystemId());
			td1Map.put(MdekKeys.DEGREE_OF_RECORD, data.getRef1Coverage());
			td1Map.put(MdekKeys.POS_ACCURACY_VERTICAL, data.getRef1AltAccuracy());
			td1Map.put(MdekKeys.RESOLUTION, data.getRef1PosAccuracy());
			td1Map.put(MdekKeys.TECHNICAL_BASE, data.getRef1BasisText());
			td1Map.put(MdekKeys.DATA, data.getRef1DataBasisText());
			td1Map.put(MdekKeys.METHOD_OF_PRODUCTION, data.getRef1ProcessText());
			td1Map.put(MdekKeys.FEATURE_TYPE_LIST, data.getRef1Data());
			td1Map.put(MdekKeys.PUBLICATION_SCALE_LIST, mapFromScaleTable(data.getRef1Scale()));
			td1Map.put(MdekKeys.SYMBOL_CATALOG_LIST, mapFromSymLinkDataTable(data.getRef1SymbolsText()));
			td1Map.put(MdekKeys.KEY_CATALOG_LIST, mapFromKeyLinkDataTable(data.getRef1KeysText()));
			td1Map.put(MdekKeys.SPATIAL_REPRESENTATION_TYPE_LIST, data.getRef1Representation());
			td1Map.put(MdekKeys.GEO_VECTOR_LIST, mapFromVFormatDetailsTable(data.getRef1VFormatDetails()));
			udkObj.put(MdekKeys.TECHNICAL_DOMAIN_MAP, td1Map);			
			break;
		case 2:
			IngridDocument td2Map = new IngridDocument();			
			td2Map.put(MdekKeys.AUTHOR, data.getRef2Author());
			td2Map.put(MdekKeys.EDITOR, data.getRef2Publisher());
			td2Map.put(MdekKeys.PUBLISHED_IN, data.getRef2PublishedIn());
			td2Map.put(MdekKeys.PUBLISHING_PLACE, data.getRef2PublishLocation());
			td2Map.put(MdekKeys.VOLUME, data.getRef2PublishedInIssue());
			td2Map.put(MdekKeys.PAGES, data.getRef2PublishedInPages());
			td2Map.put(MdekKeys.YEAR, data.getRef2PublishedInYear());
			td2Map.put(MdekKeys.ISBN, data.getRef2PublishedISBN());
			td2Map.put(MdekKeys.PUBLISHER, data.getRef2PublishedPublisher());
			td2Map.put(MdekKeys.LOCATION, data.getRef2LocationText());
			td2Map.put(MdekKeys.TYPE_OF_DOCUMENT, data.getRef2DocumentType());
			td2Map.put(MdekKeys.SOURCE, data.getRef2BaseDataText());
			td2Map.put(MdekKeys.ADDITIONAL_BIBLIOGRAPHIC_INFO, data.getRef2BibData());
			td2Map.put(MdekKeys.DESCRIPTION_OF_TECH_DOMAIN, data.getRef2Explanation());
			udkObj.put(MdekKeys.TECHNICAL_DOMAIN_DOCUMENT, td2Map);
			break;
		case 3:
			IngridDocument td3Map = new IngridDocument();			

			td3Map.put(MdekKeys.SERVICE_TYPE, data.getRef3ServiceType());
			td3Map.put(MdekKeys.SYSTEM_ENVIRONMENT, data.getRef3SystemEnv());
			td3Map.put(MdekKeys.SYSTEM_HISTORY, data.getRef3History());
			td3Map.put(MdekKeys.DATABASE_OF_SYSTEM, data.getRef3BaseDataText());
			td3Map.put(MdekKeys.SERVICE_VERSION_LIST, data.getRef3ServiceVersion());
			td3Map.put(MdekKeys.SERVICE_OPERATION_DESCRIPTION, data.getRef3Explanation());
			td3Map.put(MdekKeys.SERVICE_OPERATION_LIST, mapFromOperationTable(data.getRef3Operation()));
			udkObj.put(MdekKeys.TECHNICAL_DOMAIN_SERVICE, td3Map);
			break;
		case 4:
			IngridDocument td4Map = new IngridDocument();			
			td4Map.put(MdekKeys.MEMBER_DESCRIPTION, data.getRef4ParticipantsText());
			td4Map.put(MdekKeys.LEADER_DESCRIPTION, data.getRef4PMText());
			td4Map.put(MdekKeys.DESCRIPTION_OF_TECH_DOMAIN, data.getRef4Explanation());
			udkObj.put(MdekKeys.TECHNICAL_DOMAIN_PROJECT, td4Map);
			break;
		case 5:
			IngridDocument td5Map = new IngridDocument();			
			td5Map.put(MdekKeys.DESCRIPTION_OF_TECH_DOMAIN, data.getRef5Explanation());
			td5Map.put(MdekKeys.METHOD, data.getRef5MethodText());
			td5Map.put(MdekKeys.PARAMETERS, mapFromDbContentTable(data.getRef5dbContent()));
			udkObj.put(MdekKeys.TECHNICAL_DOMAIN_DATASET, td5Map);
			break;
		}

		log.debug("Sending the following object to MdekCaller:");
		printHashMap(udkObj);

		return udkObj;
	}

	
	
	// ------------------------------- Helper Methods -----------------------------------

	
	/****************************************************************************
	 * Mapping from the Mdek gui representation to the IngridDocument Structure *
	 ****************************************************************************/
	
	private static ArrayList<IngridDocument> mapFromGeneralAddressTable(ArrayList<MdekAddressBean> adrTable) {
		ArrayList<IngridDocument> resultList = new ArrayList<IngridDocument>();

		for (MdekAddressBean address : adrTable) {
			IngridDocument mappedEntry = new IngridDocument();
			mappedEntry.put(MdekKeys.UUID, address.getUuid());
			mappedEntry.put(MdekKeys.RELATION_TYPE_ID, address.getTypeOfRelation());
			mappedEntry.put(MdekKeys.RELATION_TYPE_NAME, address.getNameOfRelation());
			resultList.add(mappedEntry);
		}
		return resultList;
	}

	private static ArrayList<IngridDocument> mapFromObjectLinksTable(ArrayList<MdekDataBean> objList) {
		ArrayList<IngridDocument> resultList = new ArrayList<IngridDocument>();
		if (objList == null)
			return resultList;

		SimpleMdekMapper m = new SimpleMdekMapper();

		for (MdekDataBean obj : objList) {
			IngridDocument mappedEntry = (IngridDocument) m.convertFromMdekRepresentation(obj);
			resultList.add(mappedEntry);
		}

		return resultList;
	}

	private static ArrayList<IngridDocument> mapFromUrlLinksTable(ArrayList<UrlBean> urlList) {
		ArrayList<IngridDocument> resultList = new ArrayList<IngridDocument>();
		if (urlList == null)
			return resultList;

		for (UrlBean url : urlList) {
			IngridDocument mappedUrl = new IngridDocument();
			mappedUrl.put(MdekKeys.LINKAGE_DATATYPE, url.getDatatype());
			mappedUrl.put(MdekKeys.LINKAGE_DESCRIPTION, url.getDescription());
			mappedUrl.put(MdekKeys.LINKAGE_ICON_TEXT, url.getIconText());
			mappedUrl.put(MdekKeys.LINKAGE_ICON_URL, url.getIconUrl());
			mappedUrl.put(MdekKeys.LINKAGE_NAME, url.getName());
			mappedUrl.put(MdekKeys.LINKAGE_REFERENCE, url.getRelationTypeName());
			mappedUrl.put(MdekKeys.LINKAGE_REFERENCE_ID, url.getRelationType());
			mappedUrl.put(MdekKeys.LINKAGE_URL, url.getUrl());
			mappedUrl.put(MdekKeys.LINKAGE_URL_TYPE, url.getUrlType());
			mappedUrl.put(MdekKeys.LINKAGE_VOLUME, url.getVolume());
			resultList.add(mappedUrl);
		}
		return resultList;
	}
	
	private static ArrayList<IngridDocument> mapFromLocationTables(ArrayList<LocationBean> locationSNS, ArrayList<LocationBean> locationFree) {
		ArrayList<IngridDocument> resultList = new ArrayList<IngridDocument>();
		if (locationFree != null) {
			for (LocationBean loc : locationFree) {
				IngridDocument res = new IngridDocument();
				res.put(MdekKeys.LOCATION_TYPE, "F");
				res.put(MdekKeys.LOCATION_NAME, loc.getName());
				res.put(MdekKeys.WEST_BOUNDING_COORDINATE, loc.getLongitude1());
				res.put(MdekKeys.SOUTH_BOUNDING_COORDINATE, loc.getLatitude1());
				res.put(MdekKeys.EAST_BOUNDING_COORDINATE, loc.getLongitude2());
				res.put(MdekKeys.NORTH_BOUNDING_COORDINATE, loc.getLatitude2());
				resultList.add(res);
			}
		}
		if (locationSNS != null) {
			for (LocationBean loc : locationSNS) {
				IngridDocument res = new IngridDocument();
				res.put(MdekKeys.LOCATION_TYPE, "G");
				res.put(MdekKeys.LOCATION_NAME, loc.getName());
				res.put(MdekKeys.LOCATION_CODE, loc.getNativeKey());
				res.put(MdekKeys.LOCATION_SNS_ID, loc.getTopicId());
				res.put(MdekKeys.WEST_BOUNDING_COORDINATE, loc.getLongitude1());
				res.put(MdekKeys.SOUTH_BOUNDING_COORDINATE, loc.getLatitude1());
				res.put(MdekKeys.EAST_BOUNDING_COORDINATE, loc.getLongitude2());
				res.put(MdekKeys.NORTH_BOUNDING_COORDINATE, loc.getLatitude2());
				resultList.add(res);
			}
		}
		return resultList;
	}


	private static ArrayList<IngridDocument> mapFromTimeRefTable(ArrayList<TimeReferenceBean> refList) {
		ArrayList<IngridDocument> resultList = new ArrayList<IngridDocument>();
		if (refList == null)
			return resultList;

		for (TimeReferenceBean ref : refList) {
			IngridDocument result = new IngridDocument();
			result.put(MdekKeys.DATASET_REFERENCE_DATE, convertDateToTimestamp(ref.getDate()));
			result.put(MdekKeys.DATASET_REFERENCE_TYPE, ref.getType());
			resultList.add(result);
		}
		return resultList;
	}


	private static ArrayList<IngridDocument> mapFromAvailDataFormatTable(ArrayList<DataFormatBean> refList) {
		ArrayList<IngridDocument> resultList = new ArrayList<IngridDocument>();
		if (refList == null)
			return resultList;

		for (DataFormatBean ref : refList) {
			IngridDocument result = new IngridDocument();
			result.put(MdekKeys.FORMAT_FILE_DECOMPRESSION_TECHNIQUE, ref.getCompression());
			result.put(MdekKeys.FORMAT_NAME, ref.getName());
			result.put(MdekKeys.FORMAT_SPECIFICATION, ref.getPixelDepth());
			result.put(MdekKeys.FORMAT_VERSION, ref.getVersion());
			resultList.add(result);
		}
		return resultList;
	}


	private static ArrayList<IngridDocument> mapFromAvailMediaOptionsTable(ArrayList<MediaOptionBean> refList) {
		ArrayList<IngridDocument> resultList = new ArrayList<IngridDocument>();
		if (refList == null)
			return resultList;

		for (MediaOptionBean ref : refList) {
			IngridDocument result = new IngridDocument();
			result.put(MdekKeys.MEDIUM_NAME, ref.getName());
			result.put(MdekKeys.MEDIUM_NOTE, ref.getLocation());
			result.put(MdekKeys.MEDIUM_TRANSFER_SIZE, ref.getTransferSize());
			resultList.add(result);
		}
		return resultList;
	}


	private static ArrayList<IngridDocument> mapFromThesTermTables(ArrayList<SNSTopic> snsList, ArrayList<String> freeList) {
		ArrayList<IngridDocument> resultList = new ArrayList<IngridDocument>();
		if (snsList != null) {
			for (SNSTopic t : snsList) {
				IngridDocument res = new IngridDocument();
				res.put(MdekKeys.TERM_TYPE, "T");
				res.put(MdekKeys.TERM_NAME, t.getTitle());
				res.put(MdekKeys.TERM_SNS_ID, t.getTopicId());
				resultList.add(res);
			}
		}
		if (freeList != null) {
			for (String s : freeList) {
				IngridDocument res = new IngridDocument();
				res.put(MdekKeys.TERM_TYPE, "F");
				res.put(MdekKeys.TERM_NAME, s);
				resultList.add(res);
			}
		}
		return resultList;
	}


	private static ArrayList<IngridDocument> mapFromVFormatDetailsTable(ArrayList<VectorFormatDetailsBean> vFormatList) {
		ArrayList<IngridDocument> resultList = new ArrayList<IngridDocument>();
		if (vFormatList == null)
			return resultList;

		for (VectorFormatDetailsBean v : vFormatList) {
			IngridDocument result = new IngridDocument();
			result.put(MdekKeys.GEOMETRIC_OBJECT_TYPE, v.getGeometryType());
			result.put(MdekKeys.GEOMETRIC_OBJECT_COUNT, v.getNumElements());
			resultList.add(result);
		}
		return resultList;
	}


	private static ArrayList<IngridDocument> mapFromScaleTable(ArrayList<ScaleBean> scaleList) {
		ArrayList<IngridDocument> resultList = new ArrayList<IngridDocument>();
		if (scaleList == null)
			return resultList;

		for (ScaleBean s : scaleList) {
			IngridDocument result = new IngridDocument();
			result.put(MdekKeys.RESOLUTION_GROUND, s.getGroundResolution());
			result.put(MdekKeys.SCALE, s.getScale());
			result.put(MdekKeys.RESOLUTION_SCAN, s.getScanResolution());
			resultList.add(result);
		}
		return resultList;
	}


	private static ArrayList<IngridDocument> mapFromSymLinkDataTable(ArrayList<LinkDataBean> linkList) {
		ArrayList<IngridDocument> resultList = new ArrayList<IngridDocument>();
		if (linkList == null)
			return resultList;

		for (LinkDataBean l : linkList) {
			IngridDocument result = new IngridDocument();
			result.put(MdekKeys.SYMBOL_DATE, convertDateToTimestamp(l.getDate()));
			result.put(MdekKeys.SYMBOL_CAT, l.getTitle());
			result.put(MdekKeys.SYMBOL_EDITION, l.getVersion());
			resultList.add(result);
		}
		return resultList;
	}

	private static ArrayList<IngridDocument> mapFromKeyLinkDataTable(ArrayList<LinkDataBean> linkList) {
		ArrayList<IngridDocument> resultList = new ArrayList<IngridDocument>();
		if (linkList == null)
			return resultList;

		for (LinkDataBean l : linkList) {
			IngridDocument result = new IngridDocument();
			result.put(MdekKeys.KEY_DATE, convertDateToTimestamp(l.getDate()));
			result.put(MdekKeys.SUBJECT_CAT, l.getTitle());
			result.put(MdekKeys.EDITION, l.getVersion());
			resultList.add(result);
		}
		return resultList;
	}

	private static ArrayList<IngridDocument> mapFromDbContentTable(ArrayList<DBContentBean> dbList) {
		ArrayList<IngridDocument> resultList = new ArrayList<IngridDocument>();
		if (dbList == null)
			return resultList;

		for (DBContentBean db : dbList) {
			IngridDocument result = new IngridDocument();
			result.put(MdekKeys.PARAMETER, db.getParameter());
			result.put(MdekKeys.SUPPLEMENTARY_INFORMATION, db.getAdditionalData());
			resultList.add(result);
		}
		return resultList;
	}


	private static ArrayList<IngridDocument> mapFromOperationTable(ArrayList<OperationBean> opList) {
		ArrayList<IngridDocument> resultList = new ArrayList<IngridDocument>();
		if (opList == null)
			return resultList;

		for (OperationBean op : opList) {
			IngridDocument result = new IngridDocument();
			result.put(MdekKeys.SERVICE_OPERATION_NAME, op.getName());
			result.put(MdekKeys.SERVICE_OPERATION_DESCRIPTION, op.getDescription());
			result.put(MdekKeys.PLATFORM_LIST, op.getPlatform());
			result.put(MdekKeys.INVOCATION_NAME, op.getMethodCall());
			result.put(MdekKeys.PARAMETER_LIST, mapFromOperationParamTable(op.getParamList()));
			result.put(MdekKeys.CONNECT_POINT_LIST, op.getAddressList());
			result.put(MdekKeys.DEPENDS_ON_LIST, op.getDependencies());
			resultList.add(result);
		}
		return resultList;
	}

	private static ArrayList<IngridDocument> mapFromOperationParamTable(ArrayList<OperationParameterBean> opList) {
		ArrayList<IngridDocument> resultList = new ArrayList<IngridDocument>();
		if (opList == null)
			return resultList;

		for (OperationParameterBean op : opList) {
			IngridDocument result = new IngridDocument();
			result.put(MdekKeys.PARAMETER_NAME, op.getName());
			result.put(MdekKeys.DIRECTION, op.getDirection());
			result.put(MdekKeys.DESCRIPTION, op.getDescription());
			result.put(MdekKeys.OPTIONALITY, op.getOptional());
			result.put(MdekKeys.REPEATABILITY, op.getMultiple());
			resultList.add(result);
		}
		return resultList;
	}


	private static ArrayList<IngridDocument> mapFromCommentTable(ArrayList<CommentBean> commentList) {
		ArrayList<IngridDocument> resultList = new ArrayList<IngridDocument>();
		if (commentList == null)
			return resultList;

		for (CommentBean c : commentList) {
			IngridDocument result = new IngridDocument();
			result.put(MdekKeys.COMMENT, c.getComment());
			result.put(MdekKeys.CREATE_UUID, c.getUser());
			result.put(MdekKeys.CREATE_TIME, convertDateToTimestamp(c.getDate()));
			resultList.add(result);
		}
		return resultList;
	}

	
	/****************************************************************************
	 * Mapping from the IngridDocument Structure to the Mdek gui representation *
	 ****************************************************************************/

	private static ArrayList<MdekAddressBean> mapToGeneralAddressTable(List<HashMap<String, Object>> adrTable) {
		ArrayList<MdekAddressBean> resultTable = new ArrayList<MdekAddressBean>(); 
		if (adrTable == null)
			return resultTable;

		for (HashMap<String, Object> tableRow : adrTable) {
			MdekAddressBean address = new MdekAddressBean();

			address.setUuid((String) tableRow.get(MdekKeys.UUID));
			address.setInformation((String) tableRow.get(MdekKeys.TITLE_OR_FUNCTION));
			address.setAddressClass((Integer) tableRow.get(MdekKeys.CLASS));
			address.setGivenName((String) tableRow.get(MdekKeys.GIVEN_NAME));
			address.setStreet((String) tableRow.get(MdekKeys.STREET));
			address.setCountryCode((String) tableRow.get(MdekKeys.POSTAL_CODE_OF_COUNTRY));
			address.setCity((String) tableRow.get(MdekKeys.CITY));
			address.setPoboxPostalCode((String) tableRow.get(MdekKeys.POST_BOX_POSTAL_CODE));
			address.setPoboxPostalCode((String) tableRow.get(MdekKeys.POST_BOX));
			address.setFunction((String) tableRow.get(MdekKeys.FUNCTION));
			address.setAddressDescription((String) tableRow.get(MdekKeys.ADDRESS_DESCRIPTION));
			address.setOrganisation((String) tableRow.get(MdekKeys.ORGANISATION));
			address.setNameForm((String) tableRow.get(MdekKeys.NAME_FORM));
			address.setTitleOrFunction((String) tableRow.get(MdekKeys.TITLE_OR_FUNCTION));
			address.setTypeOfRelation((Integer) tableRow.get(MdekKeys.RELATION_TYPE_ID));
			address.setNameOfRelation((String) tableRow.get(MdekKeys.RELATION_TYPE_NAME));

			// Build name
			if (tableRow.get(MdekKeys.NAME) != null) {
				String name = (String) tableRow.get(MdekKeys.NAME) + ", " + (String) tableRow.get(MdekKeys.GIVEN_NAME); 
				address.setName(name);
			}
			else if (tableRow.get(MdekKeys.ORGANISATION) != null) {
				address.setName((String) tableRow.get(MdekKeys.ORGANISATION));
			}

			// Build communication map
			List<HashMap<String, String>> commMap = (List<HashMap<String, String>>) tableRow.get(MdekKeys.COMMUNICATION);
			ArrayList<HashMap<String, String>> resultCommMap = new ArrayList<HashMap<String, String>>(); 
			
			if (commMap != null) {
				for (HashMap<String, String> comm : commMap) {
					HashMap<String, String> resultComm = new HashMap<String, String>();
					resultComm.put(MDEK_GENERAL_ADDRESS_TABLE_COMM_DESCRIPTION, comm.get((String) MdekKeys.COMMUNICATION_DESCRIPTION));
					resultComm.put(MDEK_GENERAL_ADDRESS_TABLE_COMM_MEDIUM, comm.get((String) MdekKeys.COMMUNICATION_MEDIUM));
					resultComm.put(MDEK_GENERAL_ADDRESS_TABLE_COMM_VALUE, comm.get((String) MdekKeys.COMMUNICATION_VALUE));
					resultCommMap.add(resultComm);
				}
			}
			address.setCommunication(resultCommMap);

			resultTable.add(address);
		}
		return resultTable;
	}

	private static ArrayList<MdekDataBean> mapToObjectLinksTable(List<HashMap<String, Object>> objList) {
		ArrayList<MdekDataBean> resultList = new ArrayList<MdekDataBean>(); 
		if (objList == null)
			return resultList;

		for (HashMap<String, Object> obj : objList) {
			resultList.add(getDetailedMdekRepresentation(obj));
		}
		return resultList;
	}

	private static ArrayList<UrlBean> mapToUrlLinksTable(List<HashMap<String, Object>> objList) {
		ArrayList<UrlBean> resultList = new ArrayList<UrlBean>(); 
		if (objList == null)
			return resultList;
		
		for (HashMap<String, Object> obj : objList) {
			UrlBean url = new UrlBean();
			url.setDatatype((String) obj.get(MdekKeys.LINKAGE_DATATYPE));
			url.setDescription((String) obj.get(MdekKeys.LINKAGE_DESCRIPTION));
			url.setIconText((String) obj.get(MdekKeys.LINKAGE_ICON_TEXT));
			url.setIconUrl((String) obj.get(MdekKeys.LINKAGE_ICON_URL));
			url.setName((String) obj.get(MdekKeys.LINKAGE_NAME));
			url.setRelationTypeName((String) obj.get(MdekKeys.LINKAGE_REFERENCE));
			url.setRelationType((Integer) obj.get(MdekKeys.LINKAGE_REFERENCE_ID));
			url.setUrl((String) obj.get(MdekKeys.LINKAGE_URL));
			url.setUrlType((Integer) obj.get(MdekKeys.LINKAGE_URL_TYPE));
			url.setVolume((String) obj.get(MdekKeys.LINKAGE_VOLUME));
			resultList.add(url);
		}
		return resultList;
	}
	
	
	private static ArrayList<LocationBean> mapToSpatialRefAdminUnitTable(List<HashMap<String, Object>> locList) {
		ArrayList<LocationBean> resultList = new ArrayList<LocationBean>();
		if (locList == null)
			return resultList;

		for (HashMap<String, Object> location : locList) {

			String locationType = (String) location.get(MdekKeys.LOCATION_TYPE);
			if (locationType.equals("G")) {
				LocationBean loc = new LocationBean(); 
				loc.setType((String) location.get(MdekKeys.LOCATION_TYPE));
				loc.setName((String) location.get(MdekKeys.LOCATION_NAME));
				loc.setNativeKey((String) location.get(MdekKeys.LOCATION_CODE));
				loc.setTopicId((String) location.get(MdekKeys.LOCATION_SNS_ID));
				loc.setLongitude1((Double) location.get(MdekKeys.WEST_BOUNDING_COORDINATE));
				loc.setLatitude1((Double) location.get(MdekKeys.SOUTH_BOUNDING_COORDINATE));
				loc.setLongitude2((Double) location.get(MdekKeys.EAST_BOUNDING_COORDINATE));
				loc.setLatitude2((Double) location.get(MdekKeys.NORTH_BOUNDING_COORDINATE));
				resultList.add(loc);
			}
		}
		return resultList;
	}
	
	private static ArrayList<LocationBean> mapToSpatialRefLocationTable(List<HashMap<String, Object>> locList) {
		ArrayList<LocationBean> resultList = new ArrayList<LocationBean>();
		if (locList == null)
			return resultList;

		for (HashMap<String, Object> location : locList) {
			String locationType = (String) location.get(MdekKeys.LOCATION_TYPE);
			if (locationType.equals("F")) {
				LocationBean loc = new LocationBean(); 
				loc.setType((String) location.get(MdekKeys.LOCATION_TYPE));
				loc.setName((String) location.get(MdekKeys.LOCATION_NAME));
				loc.setLongitude1((Double) location.get(MdekKeys.WEST_BOUNDING_COORDINATE));
				loc.setLatitude1((Double) location.get(MdekKeys.SOUTH_BOUNDING_COORDINATE));
				loc.setLongitude2((Double) location.get(MdekKeys.EAST_BOUNDING_COORDINATE));
				loc.setLatitude2((Double) location.get(MdekKeys.NORTH_BOUNDING_COORDINATE));
				resultList.add(loc);
			}
		}
		return resultList;
	}


	private static ArrayList<TimeReferenceBean> mapToTimeRefTable(List<HashMap<String, Object>> refList) {
		ArrayList<TimeReferenceBean> resultList = new ArrayList<TimeReferenceBean>();
		if (refList == null)
			return resultList;

		for (HashMap<String, Object> ref : refList) {
			TimeReferenceBean tr = new TimeReferenceBean();
			tr.setDate(convertTimestampToDate((String) ref.get(MdekKeys.DATASET_REFERENCE_DATE)));
			tr.setType((Integer) ref.get(MdekKeys.DATASET_REFERENCE_TYPE));
			resultList.add(tr);
		}
		return resultList;
	}


	private static ArrayList<DataFormatBean> mapToAvailDataFormatTable(List<HashMap<String, Object>> refList) {
		ArrayList<DataFormatBean> resultList = new ArrayList<DataFormatBean>();
		if (refList == null)
			return resultList;
		for (HashMap<String, Object> ref : refList) {
			DataFormatBean df = new DataFormatBean();
			df.setName((String) ref.get(MdekKeys.FORMAT_NAME));
			df.setCompression((String) ref.get(MdekKeys.FORMAT_FILE_DECOMPRESSION_TECHNIQUE));
			df.setPixelDepth((String) ref.get(MdekKeys.FORMAT_SPECIFICATION));
			df.setVersion((String) ref.get(MdekKeys.FORMAT_VERSION));
			resultList.add(df);
		}
		return resultList;
	}

	private static ArrayList<MediaOptionBean> mapToAvailMediaOptionsTable(List<HashMap<String, Object>> refList) {
		ArrayList<MediaOptionBean> resultList = new ArrayList<MediaOptionBean>();
		if (refList == null)
			return resultList;
		for (HashMap<String, Object> ref : refList) {
			MediaOptionBean mo = new MediaOptionBean();
			mo.setName((Integer) ref.get(MdekKeys.MEDIUM_NAME));
			mo.setLocation((String) ref.get(MdekKeys.MEDIUM_NOTE));
			mo.setTransferSize((Double) ref.get(MdekKeys.MEDIUM_TRANSFER_SIZE));
			resultList.add(mo);
		}
		return resultList;
	}


	private static ArrayList<SNSTopic> mapToThesTermsTable(List<HashMap<String, Object>> topicList) {
		ArrayList<SNSTopic> resultList = new ArrayList<SNSTopic>();
		if (topicList == null)
			return resultList;
		for (HashMap<String, Object> topic : topicList) {
			SNSTopic t = new SNSTopic();
			String type = (String) topic.get(MdekKeys.TERM_TYPE);
			if (type.equalsIgnoreCase("T")) {
				t.setTitle((String) topic.get(MdekKeys.TERM_NAME));
				t.setTopicId((String) topic.get(MdekKeys.TERM_SNS_ID));
				resultList.add(t);
			}
		}
		return resultList;
	}

	private static ArrayList<String> mapToThesFreeTermsTable(List<HashMap<String, Object>> topicList) {
		ArrayList<String> resultList = new ArrayList<String>();
		if (topicList == null)
			return resultList;
		for (HashMap<String, Object> topic : topicList) {
			String type = (String) topic.get(MdekKeys.TERM_TYPE);
			if (type.equalsIgnoreCase("F")) {
				resultList.add((String) topic.get(MdekKeys.TERM_NAME));
			}
		}
		return resultList;
	}


	private static ArrayList<VectorFormatDetailsBean> mapToVFormatDetailsTable(List<HashMap<String, Object>> vFormatList) {
		ArrayList<VectorFormatDetailsBean> resultList = new ArrayList<VectorFormatDetailsBean>();
		if (vFormatList == null)
			return resultList;
		for (HashMap<String, Object> vFormat : vFormatList) {
			VectorFormatDetailsBean v = new VectorFormatDetailsBean();
			v.setGeometryType((Integer) vFormat.get(MdekKeys.GEOMETRIC_OBJECT_TYPE));
			v.setNumElements((Integer) vFormat.get(MdekKeys.GEOMETRIC_OBJECT_COUNT));
			resultList.add(v);
		}
		return resultList;
	}


	private static ArrayList<ScaleBean> mapToScaleTable(List<HashMap<String, Object>> scaleList) {
		ArrayList<ScaleBean> resultList = new ArrayList<ScaleBean>();
		if (scaleList == null)
			return resultList;
		for (HashMap<String, Object> topic : scaleList) {
			ScaleBean s = new ScaleBean();
			s.setGroundResolution((Double) topic.get(MdekKeys.RESOLUTION_GROUND));
			s.setScale((Integer) topic.get(MdekKeys.SCALE));
			s.setScanResolution((Double) topic.get(MdekKeys.RESOLUTION_SCAN));
			resultList.add(s);
		}
		return resultList;
	}


	private static ArrayList<LinkDataBean> mapToSymLinkDataTable(List<HashMap<String, Object>> linkList) {
		ArrayList<LinkDataBean> resultList = new ArrayList<LinkDataBean>();
		if (linkList == null)
			return resultList;
		for (HashMap<String, Object> topic : linkList) {
			LinkDataBean l = new LinkDataBean();
			l.setDate(convertTimestampToDate((String) topic.get(MdekKeys.SYMBOL_DATE)));
			l.setTitle((String) topic.get(MdekKeys.SYMBOL_CAT));
			l.setVersion((String) topic.get(MdekKeys.SYMBOL_EDITION));
			resultList.add(l);
		}
		return resultList;
	}

	private static ArrayList<LinkDataBean> mapToKeyLinkDataTable(List<HashMap<String, Object>> linkList) {
		ArrayList<LinkDataBean> resultList = new ArrayList<LinkDataBean>();
		if (linkList == null)
			return resultList;
		for (HashMap<String, Object> topic : linkList) {
			LinkDataBean l = new LinkDataBean();
			l.setDate(convertTimestampToDate((String) topic.get(MdekKeys.KEY_DATE)));
			l.setTitle((String) topic.get(MdekKeys.SUBJECT_CAT));
			l.setVersion((String) topic.get(MdekKeys.EDITION));
			resultList.add(l);
		}
		return resultList;
	}

	private static ArrayList<DBContentBean> mapToDbContentTable(List<HashMap<String, Object>> dbList) {
		ArrayList<DBContentBean> resultList = new ArrayList<DBContentBean>();
		if (dbList == null)
			return resultList;
		for (HashMap<String, Object> content : dbList) {
			DBContentBean db = new DBContentBean();
			db.setParameter((String) content.get(MdekKeys.PARAMETER));
			db.setAdditionalData((String) content.get(MdekKeys.SUPPLEMENTARY_INFORMATION));
			resultList.add(db);
		}
		return resultList;
	}


	private static ArrayList<OperationBean> mapToOperationTable(List<HashMap<String, Object>> opList) {
		ArrayList<OperationBean> resultList = new ArrayList<OperationBean>();
		if (opList == null)
			return resultList;
		for (HashMap<String, Object> operation : opList) {
			OperationBean op = new OperationBean();
			op.setName((String) operation.get(MdekKeys.SERVICE_OPERATION_NAME));
			op.setDescription((String) operation.get(MdekKeys.SERVICE_OPERATION_DESCRIPTION));
			op.setPlatform((ArrayList<String>) operation.get(MdekKeys.PLATFORM_LIST));
			op.setMethodCall((String) operation.get(MdekKeys.INVOCATION_NAME));
			op.setParamList(mapToOperationParamTable((List<HashMap<String, Object>>) operation.get(MdekKeys.PARAMETER_LIST)));
			op.setAddressList((ArrayList<String>) operation.get(MdekKeys.CONNECT_POINT_LIST));
			op.setDependencies((ArrayList<String>) operation.get(MdekKeys.DEPENDS_ON_LIST));
			resultList.add(op);
		}
		return resultList;
	}


	private static ArrayList<OperationParameterBean> mapToOperationParamTable(List<HashMap<String, Object>> opList) {
		ArrayList<OperationParameterBean> resultList = new ArrayList<OperationParameterBean>();
		if (opList == null)
			return resultList;
		for (HashMap<String, Object> operation : opList) {
			OperationParameterBean op = new OperationParameterBean();
			op.setName((String) operation.get(MdekKeys.PARAMETER_NAME));
			op.setDirection((String) operation.get(MdekKeys.DIRECTION));
			op.setDescription((String) operation.get(MdekKeys.DESCRIPTION));
			op.setOptional((Integer) operation.get(MdekKeys.OPTIONALITY));
			op.setMultiple((Integer) operation.get(MdekKeys.REPEATABILITY));
			resultList.add(op);
		}
		return resultList;
	}

	

	private static ArrayList<CommentBean> mapToCommentTable(List<HashMap<String, Object>> commentList) {
		ArrayList<CommentBean> resultList = new ArrayList<CommentBean>();
		if (commentList == null)
			return resultList;
		for (HashMap<String, Object> comment : commentList) {
			CommentBean c = new CommentBean();
			c.setComment((String) comment.get(MdekKeys.COMMENT));
			c.setUser((String) comment.get(MdekKeys.CREATE_UUID));
			c.setDate(convertTimestampToDate((String) comment.get(MdekKeys.CREATE_TIME)));
			resultList.add(c);
		}
		return resultList;
	}

	
	 /***********************************************************
	 * Several Methods for testing Input and Output Conformity *
	 ***********************************************************/

	private static boolean testDetailedInputConformity(HashMap<String, Object> obj)
	{
		Object val = null;
		Integer valInt = null;
//		val = obj.get(MdekKeys.ABSTRACT);
//      assert val != null : "Input HashMap does not contain a key: MdekKeys.ABSTRACT";
//		assert val instanceof String : "MdekKeys.ABSTRACT";

		val = obj.get(MdekKeys.UUID);
		assert val != null : "Input HashMap does not contain a key: MdekKeys.UUID";
		assert val instanceof String : "Input HashMap value for key MdekKeys.UUID is not of type String";

		val = obj.get(MdekKeys.TITLE);
		assert val != null : "Input HashMap does not contain a key: MdekKeys.TITLE";
		assert val instanceof String : "Input HashMap value for key MdekKeys.TITLE is not of type String";

		val = obj.get(MdekKeys.CLASS);
		assert val != null : "Input HashMap does not contain a key: MdekKeys.CLASS";
		assert val instanceof Integer : "Input HashMap value for key MdekKeys.CLASS is not of type Integer";

		valInt = (Integer) val;
		assert (valInt >= 0 && valInt <= 5) : "Input HashMap value for key MdekKeys.CLASS is not in the range of 0 and 5";

		return true;
	}

	private static boolean testSimpleInputConformity(HashMap<String, Object> obj)
	{
		Object val = null;
		Integer valInt = null;
//		val = obj.get(MdekKeys.ABSTRACT);
//      assert val != null : "Input HashMap does not contain a key: MdekKeys.ABSTRACT";
//		assert val instanceof String : "MdekKeys.ABSTRACT";

		val = obj.get(MdekKeys.UUID);
		assert val != null : "Input HashMap does not contain a key: MdekKeys.UUID";
		assert val instanceof String : "Input HashMap value for key MdekKeys.UUID is not of type String";

		val = obj.get(MdekKeys.TITLE);
		assert val != null : "Input HashMap does not contain a key: MdekKeys.TITLE";
		assert val instanceof String : "Input HashMap value for key MdekKeys.TITLE is not of type String";

		val = obj.get(MdekKeys.CLASS);
		assert val != null : "Input HashMap does not contain a key: MdekKeys.CLASS";
		assert val instanceof Integer : "Input HashMap value for key MdekKeys.CLASS is not of type Integer";

		valInt = (Integer) val;
		assert (valInt >= 0 && valInt <= 5) : "Input HashMap value for key MdekKeys.CLASS is not in the range of 0 and 5";

		val = obj.get(MdekKeys.HAS_CHILD);
		assert val != null : "Input HashMap does not contain a key: MdekKeys.HAS_CHILD";
		assert val instanceof Boolean : "Input HashMap value for key MdekKeys.CLASS is not of type Boolean";

		return true;
	}

	
	
	
	/********************************
	 * Miscellaneous Helper Methods *
	 ********************************/
	private final static SimpleDateFormat timestampFormatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");

	private static Date convertTimestampToDate(String timeStamp) {
		if (timeStamp != null && timeStamp.length() != 0) {
			try {
				Date date = timestampFormatter.parse(timeStamp);
				return date;
			} catch (Exception ex){
				log.error("Problems parsing timestamp from database: " + timeStamp, ex);
				return null;
			}
		} else {
			return null;
		}
	}

	private static String convertDateToTimestamp(Date date) {
		if (date != null) {
			return MdekUtils.dateToTimestamp(date);
		} else {
			return null;
		}
	}

		
	private static String convertTimestampToDisplayDate(String timeStamp) {
		if (timeStamp != null) {
			return MdekUtils.timestampToDisplayDate(timeStamp);
		} else {
			return null;
		}
	}

	
	private static void printHashMap(HashMap<String, Object> map) {
		Set<Map.Entry<String, Object>> entrySet = map.entrySet();
		for (Map.Entry<String, Object> entry : entrySet) {
			log.debug("Key: "+entry.getKey()+" Value: "+entry.getValue());
		}
	}

	
}