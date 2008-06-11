package de.ingrid.mdek;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

import de.ingrid.mdek.MdekUtils.WorkState;
import de.ingrid.mdek.MdekUtilsSecurity.IdcPermission;
import de.ingrid.mdek.beans.CommentBean;
import de.ingrid.mdek.beans.KeyValuePair;
import de.ingrid.mdek.beans.address.MdekAddressBean;
import de.ingrid.mdek.beans.object.DBContentBean;
import de.ingrid.mdek.beans.object.DataFormatBean;
import de.ingrid.mdek.beans.object.LinkDataBean;
import de.ingrid.mdek.beans.object.LocationBean;
import de.ingrid.mdek.beans.object.MdekDataBean;
import de.ingrid.mdek.beans.object.MediaOptionBean;
import de.ingrid.mdek.beans.object.OperationBean;
import de.ingrid.mdek.beans.object.OperationParameterBean;
import de.ingrid.mdek.beans.object.ScaleBean;
import de.ingrid.mdek.beans.object.TimeReferenceBean;
import de.ingrid.mdek.beans.object.UrlBean;
import de.ingrid.mdek.beans.object.VectorFormatDetailsBean;
import de.ingrid.mdek.dwr.services.sns.SNSTopic;
import de.ingrid.utils.IngridDocument;

public class MdekMapper implements DataMapperInterface {

	private final static Logger log = Logger.getLogger(MdekMapper.class);
	private SysListCache sysListMapper;
	
	// -- Dispatch to the local private methods --
	public MdekDataBean getDetailedObjectRepresentation(Object obj) {
		if (obj instanceof HashMap)
			return getDetailedObjectRepresentation((HashMap<String, Object>) obj);
		else
			return null;
	}
	public HashMap<String, Object> getSimpleObjectRepresentation(Object obj) {
		if (obj instanceof HashMap)
			return getSimpleObjectRepresentation((HashMap<String, Object>) obj);
		else
			return null;
	}

	public HashMap<String, Object> getSimpleAddressRepresentation(Object obj) {
		if (obj instanceof HashMap)
			return getSimpleAddressRepresentation((HashMap<String, Object>) obj);
		else
			return null;
	}
	public MdekAddressBean getDetailedAddressRepresentation(Object obj) {
		if (obj instanceof HashMap)
			return getDetailedAddressRepresentation((HashMap<String, Object>) obj);
		else
			return null;
	}
	// --
	
	private MdekDataBean getDetailedObjectRepresentation(
			HashMap<String, Object> obj) {

		// by default, assertions are disabled at runtime.
		// Start the server with -ea to enable this method call
		try { assert testDetailedInputConformity(obj); }
		catch (AssertionError e) { e.printStackTrace(); }

		// TODO Check if we have to convert an address or object

//		log.debug("Converting the following object:");
//		printHashMap(obj);

		MdekDataBean mdekObj = new MdekDataBean();

		// General
		mdekObj.setGeneralShortDescription((String) obj.get(MdekKeys.DATASET_ALTERNATE_NAME));
		mdekObj.setGeneralDescription((String) obj.get(MdekKeys.ABSTRACT));
		mdekObj.setUuid((String) obj.get(MdekKeys.UUID));
		mdekObj.setParentUuid((String) obj.get(MdekKeys.PARENT_UUID));
		mdekObj.setTitle((String) obj.get(MdekKeys.TITLE));
		Integer objClass = (Integer) obj.get(MdekKeys.CLASS);
		if (objClass == null) {
			mdekObj.setObjectClass(0);
		} else {
			mdekObj.setObjectClass(objClass);
		}

		MdekAddressBean responsibleUser = getDetailedAddressRepresentation(obj.get(MdekKeysSecurity.RESPONSIBLE_USER));
		if (responsibleUser != null) {
			mdekObj.setObjectOwner(responsibleUser.getUuid());
		}

		String workStateStr = (String) obj.get(MdekKeys.WORK_STATE); 
		WorkState workState = null;
		if (workStateStr != null) {
			workState = EnumUtil.mapDatabaseToEnumConst(WorkState.class, workStateStr);
		} else {
			workState = WorkState.IN_BEARBEITUNG;
		}
		mdekObj.setWorkState(StringEscapeUtils.escapeHtml(workState.toString()));

		mdekObj.setHasChildren((Boolean) obj.get(MdekKeys.HAS_CHILD));
		mdekObj.setObjectName((String) obj.get(MdekKeys.TITLE));
		mdekObj.setGeneralAddressTable(mapToGeneralAddressTable((List<HashMap<String, Object>>) obj.get(MdekKeys.ADR_REFERENCES_TO)));
		mdekObj.setCreationTime(convertTimestampToDisplayDate((String) obj.get(MdekKeys.DATE_OF_CREATION)));
		mdekObj.setModificationTime(convertTimestampToDisplayDate((String) obj.get(MdekKeys.DATE_OF_LAST_MODIFICATION)));
		mdekObj.setLastEditor(getDetailedAddressRepresentation(obj.get(MdekKeys.MOD_USER)));

		List<IngridDocument> idcPermissions = (List<IngridDocument>) obj.get(MdekKeysSecurity.IDC_PERMISSIONS);
		mdekObj.setWritePermission(hasWritePermission(idcPermissions));
		mdekObj.setWriteSinglePermission(hasWriteSinglePermission(idcPermissions));
		mdekObj.setWriteTreePermission(hasWriteTreePermission(idcPermissions));

		mdekObj.setIsPublished((Boolean) obj.get(MdekKeys.IS_PUBLISHED));
		
		// Comments
		mdekObj.setCommentTable(mapToCommentTable((List<HashMap<String, Object>>) obj.get(MdekKeys.COMMENT_LIST)));

		// Catalogue
//		Map<String, Object> catDetails = (Map<String, Object>) obj.get(MdekKeys.CATALOG);
//		if (catDetails != null) {
//			mdekObj.setCatalogUuid((String) catDetails.get(MdekKeys.UUID));			
//			mdekObj.setCatalogName((String) catDetails.get(MdekKeys.CATALOG_NAME));
//		}

		// Information about the parent object
		Map<String, Object> parentDetails = (Map<String, Object>) obj.get(MdekKeys.PARENT_INFO);
		if (parentDetails != null) {
			mdekObj.setParentPublicationCondition((Integer) parentDetails.get(MdekKeys.PUBLICATION_CONDITION));
		}		
		
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
		mdekObj.setExtraInfoLangMetaData((String) convertLanguageCodeToIdentifier((String) obj.get(MdekKeys.METADATA_LANGUAGE)));
		mdekObj.setExtraInfoLangData((String) convertLanguageCodeToIdentifier((String) obj.get(MdekKeys.DATA_LANGUAGE)));

		mdekObj.setExtraInfoPublishArea((Integer) obj.get(MdekKeys.PUBLICATION_CONDITION));
		mdekObj.setExtraInfoPurpose((String) obj.get(MdekKeys.DATASET_INTENSIONS));
		mdekObj.setExtraInfoUse((String) obj.get(MdekKeys.DATASET_USAGE));
		mdekObj.setExtraInfoXMLExportTable(mapToExtraInfoXMLExportTable((List<HashMap<String, Object>>) obj.get(MdekKeys.EXPORTS)));
		mdekObj.setExtraInfoLegalBasicsTable(mapToExtraInfoLegalBasicsTable((List<HashMap<String, Object>>) obj.get(MdekKeys.LEGISLATIONS)));

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

		intList = (ArrayList<Integer>) obj.get(MdekKeys.ENV_TOPICS);
		if (intList != null)
			mdekObj.setThesaurusEnvTopicsList(intList);
		
		intList = (ArrayList<Integer>) obj.get(MdekKeys.ENV_CATEGORIES);
		if (intList != null)
			mdekObj.setThesaurusEnvCatsList(intList);
		
		// Links
//		mdekObj.setLinksToTable((ArrayList<HashMap<String, String>>) mapToLinksToTable((List<HashMap<String, Object>>) obj.get(MdekKeys.MISSING)));
//		mdekObj.setLinksFromTable((ArrayList<HashMap<String, String>>) mapToLinksFromTable((List<HashMap<String, Object>>) obj.get(MdekKeys.MISSING)));
		mdekObj.setLinksToObjectTable(mapToObjectLinksTable((List<HashMap<String, Object>>) obj.get(MdekKeys.OBJ_REFERENCES_TO)));
		mdekObj.setLinksFromObjectTable(mapToObjectLinksTable((List<HashMap<String, Object>>) obj.get(MdekKeys.OBJ_REFERENCES_FROM)));
		mdekObj.setLinksFromPublishedObjectTable(mapToObjectLinksTable((List<HashMap<String, Object>>) obj.get(MdekKeys.OBJ_REFERENCES_FROM_PUBLISHED_ONLY)));
		mdekObj.setLinksToUrlTable(mapToUrlLinksTable((List<HashMap<String, Object>>) obj.get(MdekKeys.LINKAGES)));
		mdekObj.setRelationType((Integer) obj.get(MdekKeys.RELATION_TYPE_REF));
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
			KeyValuePair kvp = mapToKeyValuePair(td1Map, MdekKeys.REFERENCESYSTEM_ID, MdekKeys.COORDINATE_SYSTEM);
			mdekObj.setRef1SpatialSystem(kvp.getValue());
			mdekObj.setRef1Coverage((Double) td1Map.get(MdekKeys.DEGREE_OF_RECORD));
			mdekObj.setRef1AltAccuracy((Double) td1Map.get(MdekKeys.POS_ACCURACY_VERTICAL));
			mdekObj.setRef1PosAccuracy((Double) td1Map.get(MdekKeys.RESOLUTION));
			mdekObj.setRef1BasisText((String) td1Map.get(MdekKeys.TECHNICAL_BASE));
			mdekObj.setRef1DataBasisText((String) td1Map.get(MdekKeys.DATA));
			
			ArrayList<String> strList = (ArrayList<String>) td1Map.get(MdekKeys.FEATURE_TYPE_LIST);
			if (strList != null)
				mdekObj.setRef1Data(strList);
			
			intList = (ArrayList<Integer>) td1Map.get(MdekKeys.SPATIAL_REPRESENTATION_TYPE_LIST);
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
			kvp = mapToKeyValuePair(td2Map, MdekKeys.TYPE_OF_DOCUMENT_KEY, MdekKeys.TYPE_OF_DOCUMENT);
			mdekObj.setRef2DocumentType(kvp.getValue());
			mdekObj.setRef2BaseDataText((String) td2Map.get(MdekKeys.SOURCE));
			mdekObj.setRef2BibData((String) td2Map.get(MdekKeys.ADDITIONAL_BIBLIOGRAPHIC_INFO));
			mdekObj.setRef2Explanation((String) td2Map.get(MdekKeys.DESCRIPTION_OF_TECH_DOMAIN));
			break;
		case 3:
			Map<String, Object> td3Map = (Map<String, Object>) obj.get(MdekKeys.TECHNICAL_DOMAIN_SERVICE);
			if (td3Map == null)
				break;
			kvp = mapToKeyValuePair(td3Map, MdekKeys.SERVICE_TYPE_KEY, MdekKeys.SERVICE_TYPE);
			mdekObj.setRef3ServiceType(kvp.getValue());
			mdekObj.setRef3SystemEnv((String) td3Map.get(MdekKeys.SYSTEM_ENVIRONMENT));
			mdekObj.setRef3History((String) td3Map.get(MdekKeys.SYSTEM_HISTORY));
			mdekObj.setRef3BaseDataText((String) td3Map.get(MdekKeys.DATABASE_OF_SYSTEM));
			mdekObj.setRef3ServiceVersion((ArrayList<String>) td3Map.get(MdekKeys.SERVICE_VERSION_LIST));
			mdekObj.setRef3Explanation((String) td3Map.get(MdekKeys.DESCRIPTION_OF_TECH_DOMAIN));
			mdekObj.setRef3Operation(mapToOperationTable((List<HashMap<String, Object>>) td3Map.get(MdekKeys.SERVICE_OPERATION_LIST), kvp.getKey()));

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
		
		String nodeDocType = getObjectDocType(obj);
		mdekObj.setNodeDocType(nodeDocType);

		return mdekObj;
	}
	
	private MdekAddressBean getDetailedAddressRepresentation(
			HashMap<String, Object> adr) {
		
//		log.debug("Converting the following address:");
//		printHashMap(adr);

		MdekAddressBean mdekAddress = new MdekAddressBean();

		// Information about the parent object
		Map<String, Object> parentDetails = (Map<String, Object>) adr.get(MdekKeys.PARENT_INFO);
		if (parentDetails != null) {
//			mdekAddress.setParentPublicationCondition((Integer) parentDetails.get(MdekKeys.PUBLICATION_CONDITION));
			mdekAddress.setParentClass((Integer) parentDetails.get(MdekKeys.CLASS));
		}

		MdekAddressBean responsibleUser = getDetailedAddressRepresentation(adr.get(MdekKeysSecurity.RESPONSIBLE_USER));
		if (responsibleUser != null) {
			mdekAddress.setAddressOwner(responsibleUser.getUuid());
		}

		List<IngridDocument> idcPermissions = (List<IngridDocument>) adr.get(MdekKeysSecurity.IDC_PERMISSIONS);
		mdekAddress.setWritePermission(hasWritePermission(idcPermissions));
		mdekAddress.setWriteSinglePermission(hasWriteSinglePermission(idcPermissions));
		mdekAddress.setWriteTreePermission(hasWriteTreePermission(idcPermissions));

		mdekAddress.setIsPublished((Boolean) adr.get(MdekKeys.IS_PUBLISHED));

		
		// General Information
		mdekAddress.setUuid((String) adr.get(MdekKeys.UUID));
		mdekAddress.setParentUuid((String) adr.get(MdekKeys.PARENT_UUID));
		Integer adrClass = (Integer) adr.get(MdekKeys.CLASS);
		if (adrClass == null) {
			// Set the initial class according to the parent Class
			Integer parentClass = mdekAddress.getParentClass();
			if (parentClass == null) mdekAddress.setAddressClass(0);	// Root Address is parent -> Institution
			else if (parentClass == 0) mdekAddress.setAddressClass(0);	// Institution is parent -> Institution
			else if (parentClass == 1) mdekAddress.setAddressClass(1);	// Unit is parent -> Unit
		} else {
			mdekAddress.setAddressClass(adrClass);
		}

		String workStateStr = (String) adr.get(MdekKeys.WORK_STATE); 
		WorkState workState = null;
		if (workStateStr != null) {
			workState = EnumUtil.mapDatabaseToEnumConst(WorkState.class, workStateStr);
		} else {
			workState = WorkState.IN_BEARBEITUNG;
			adr.put(MdekKeys.WORK_STATE, "B");
		}
		mdekAddress.setWorkState(StringEscapeUtils.escapeHtml(workState.toString()));
		mdekAddress.setHasChildren((Boolean) adr.get(MdekKeys.HAS_CHILD));		
		mdekAddress.setCreationTime(convertTimestampToDisplayDate((String) adr.get(MdekKeys.DATE_OF_CREATION)));
		mdekAddress.setModificationTime(convertTimestampToDisplayDate((String) adr.get(MdekKeys.DATE_OF_LAST_MODIFICATION)));
		mdekAddress.setLastEditor(getDetailedAddressRepresentation(adr.get(MdekKeys.MOD_USER)));

		// Class specific information
		mdekAddress.setOrganisation((String) adr.get(MdekKeys.ORGANISATION));
		mdekAddress.setName((String) adr.get(MdekKeys.NAME));
		mdekAddress.setGivenName((String) adr.get(MdekKeys.GIVEN_NAME));
/*		
		mdekAddress.setNameForm(new KeyValuePair((Integer) adr.get(MdekKeys.NAME_FORM_KEY), (String) adr.get(MdekKeys.NAME_FORM)));
		mdekAddress.setTitleOrFunction(new KeyValuePair((Integer) adr.get(MdekKeys.TITLE_OR_FUNCTION_KEY), (String) adr.get(MdekKeys.TITLE_OR_FUNCTION)));
*/
		mdekAddress.setNameForm(mapToKeyValuePair(adr, MdekKeys.NAME_FORM_KEY, MdekKeys.NAME_FORM).getValue());
		mdekAddress.setTitleOrFunction(mapToKeyValuePair(adr, MdekKeys.TITLE_OR_FUNCTION_KEY, MdekKeys.TITLE_OR_FUNCTION).getValue());

		// Common information
		mdekAddress.setStreet((String) adr.get(MdekKeys.STREET));
		mdekAddress.setCountryCode((String) adr.get(MdekKeys.POSTAL_CODE_OF_COUNTRY));
		mdekAddress.setPostalCode((String) adr.get(MdekKeys.POSTAL_CODE));
		mdekAddress.setCity((String) adr.get(MdekKeys.CITY));
		mdekAddress.setPobox((String) adr.get(MdekKeys.POST_BOX));
		mdekAddress.setPoboxPostalCode((String) adr.get(MdekKeys.POST_BOX_POSTAL_CODE));
		mdekAddress.setAddressDescription((String) adr.get(MdekKeys.ADDRESS_DESCRIPTION));
		mdekAddress.setTask((String) adr.get(MdekKeys.FUNCTION));
		mdekAddress.setCommunication(mapToCommunicationTable((List<HashMap<String, Object>>) adr.get(MdekKeys.COMMUNICATION)));

		// Thesaurus
		mdekAddress.setThesaurusTermsTable(mapToThesTermsTable((List<HashMap<String, Object>>) adr.get(MdekKeys.SUBJECT_TERMS)));
		mdekAddress.setThesaurusFreeTermsTable(mapToThesFreeTermsTable((List<HashMap<String, Object>>) adr.get(MdekKeys.SUBJECT_TERMS)));

		// References
		mdekAddress.setLinksFromObjectTable(mapToObjectLinksTable((List<HashMap<String, Object>>) adr.get(MdekKeys.OBJ_REFERENCES_FROM)));
		mdekAddress.setLinksFromPublishedObjectTable(mapToObjectLinksTable((List<HashMap<String, Object>>) adr.get(MdekKeys.OBJ_REFERENCES_FROM_PUBLISHED_ONLY)));
		mdekAddress.setParentInstitutions(mapToGeneralAddressTable((List<HashMap<String, Object>>) adr.get(MdekKeys.PATH_ORGANISATIONS)));

/*
		mdekAddress.setTypeOfRelation((Integer) adr.get(MdekKeys.RELATION_TYPE_ID));
		mdekAddress.setNameOfRelation((String) adr.get(MdekKeys.RELATION_TYPE_NAME));
		mdekAddress.setRefOfRelation((Integer) adr.get(MdekKeys.RELATION_TYPE_REF));
*/
		Integer relationRef = (Integer) adr.get(MdekKeys.RELATION_TYPE_REF);
		Integer relationId  = (Integer) adr.get(MdekKeys.RELATION_TYPE_ID);

		if (relationRef == null || relationRef == -1) {
			// Free entry
			mdekAddress.setNameOfRelation((String) adr.get(MdekKeys.RELATION_TYPE_NAME));
			mdekAddress.setTypeOfRelation(-1);
			mdekAddress.setRefOfRelation(-1);			
		} else {
			// Lookup the value from relationRef
			mdekAddress.setNameOfRelation(sysListMapper.getValueFromListId(relationRef, relationId));
			mdekAddress.setRefOfRelation(relationRef);
			mdekAddress.setTypeOfRelation(relationId);
		}


		// Comments
		mdekAddress.setCommentTable(mapToCommentTable((List<HashMap<String, Object>>) adr.get(MdekKeys.COMMENT_LIST)));
		
		// TODO Should we move the gui specific settings to another object / to the entry service?
		mdekAddress.setNodeAppType("A");

		adr.put(MdekKeys.CLASS, mdekAddress.getAddressClass());
		String nodeDocType = getAddressDocType(adr);
		mdekAddress.setNodeDocType(nodeDocType);
		
		return mdekAddress;
	}
		
	private static String getObjectDocType(Map<String, Object> obj) {
		String nodeDocType = "Class" + ((Integer) obj.get(MdekKeys.CLASS));
		String workState = (String) obj.get(MdekKeys.WORK_STATE); 
		// If workState... nodeDocType += "_"+workState ?		
		if (workState != null && workState.equalsIgnoreCase("B")) {
			if (obj.get(MdekKeys.IS_PUBLISHED) != null && (Boolean)obj.get(MdekKeys.IS_PUBLISHED)) {
				nodeDocType += "_BV";
			} else {
				nodeDocType += "_B";
			}
		}

		return nodeDocType;
	}

	private static String getAddressDocType(Map<String, Object> adr) {
		String nodeDocType = null;
		Integer adrClass = (Integer) adr.get(MdekKeys.CLASS);
		if (adrClass == null)
				return "Institution_B";

		switch (adrClass) {
		case 0:
			nodeDocType = "Institution";
			break;
		case 1:
			nodeDocType = "InstitutionUnit";
			break;
		case 2:
			nodeDocType = "InstitutionPerson";
			break;
		case 3:
			nodeDocType = "PersonAddress";
			break;
		default:
			nodeDocType = "Institution";
			break;
		}

		String workState = (String) adr.get(MdekKeys.WORK_STATE); 
		// If workState... nodeDocType += "_"+workState ?		
		if (workState != null && workState.equalsIgnoreCase("B")) {
			if (adr.get(MdekKeys.IS_PUBLISHED) != null && (Boolean) adr.get(MdekKeys.IS_PUBLISHED)) {
				nodeDocType += "_BV";
			} else {
				nodeDocType += "_B";
			}
		}
		return nodeDocType;
	}

	private HashMap<String, Object> getSimpleObjectRepresentation(
			HashMap<String, Object> obj) {
		
		// by default, assertions are disabled at runtime.
		// Start the server with -ea to enable this method call
		try { assert testSimpleInputConformity(obj); }
		catch (AssertionError e) { e.printStackTrace(); }

		HashMap<String, Object> mdekObj = new HashMap<String, Object>();

		// The object UUID is used to identify widgets in the gui.
		// TODO attach the values to the object but don't use them to identify widgets
		mdekObj.put(MDEK_ID, obj.get(MdekKeys.UUID));
		mdekObj.put(MDEK_TITLE, obj.get(MdekKeys.TITLE));
		mdekObj.put(MDEK_HAS_CHILDREN, obj.get(MdekKeys.HAS_CHILD));
		mdekObj.put(MDEK_IS_PUBLISHED, obj.get(MdekKeys.IS_PUBLISHED));

		List<IngridDocument> idcPermissions = (List<IngridDocument>) obj.get(MdekKeysSecurity.IDC_PERMISSIONS);
		mdekObj.put(MDEK_USER_WRITE_PERMISSION, hasWritePermission(idcPermissions));
		mdekObj.put(MDEK_USER_WRITE_SINGLE_PERMISSION, hasWriteSinglePermission(idcPermissions));
		mdekObj.put(MDEK_USER_WRITE_TREE_PERMISSION, hasWriteTreePermission(idcPermissions));

		String nodeDocType = getObjectDocType(obj);
		mdekObj.put(MDEK_DOCTYPE, nodeDocType);

		return mdekObj;
	}

	private HashMap<String, Object> getSimpleAddressRepresentation(
			HashMap<String, Object> adr) {
		
		// by default, assertions are disabled at runtime.
		// Start the server with -ea to enable this method call
//		try { assert testSimpleInputConformity(adr); }
//		catch (AssertionError e) { e.printStackTrace(); }

		HashMap<String, Object> mdekAdr = new HashMap<String, Object>();

		mdekAdr.put(MDEK_ID, adr.get(MdekKeys.UUID));
		Integer adrClass = (Integer) adr.get(MdekKeys.CLASS);
		mdekAdr.put(MDEK_CLASS, adrClass);

		if (adrClass == 0 || adrClass == 1)
			mdekAdr.put(MDEK_TITLE, adr.get(MdekKeys.ORGANISATION));

		else if (adrClass == 2) {
			String title = "";
			title += adr.get(MdekKeys.NAME);
			String givenName = (String) adr.get(MdekKeys.GIVEN_NAME);
			if (givenName != null && givenName.trim().length() != 0)
				title += ", "+givenName;
			mdekAdr.put(MDEK_TITLE, title);

		} else if (adrClass == 3) {
			String title = "";
			String name = (String) adr.get(MdekKeys.NAME);
			String givenName = (String) adr.get(MdekKeys.GIVEN_NAME);
			String organisation = (String) adr.get(MdekKeys.ORGANISATION);
			if (name != null) {
				name = name.trim();
				if (name.length() != 0)
					title += name;
			}

			if (givenName != null){
				givenName = givenName.trim();
				if (givenName.length() != 0) {
					if (title.length() != 0)
						title += ", ";
					title += givenName;
				}
			}

			if (organisation != null) {
				organisation = organisation.trim();
				if (organisation.length() != 0)
					title += " ("+organisation+")";
			}

			mdekAdr.put(MDEK_TITLE, title.trim());
		}

		mdekAdr.put(MDEK_HAS_CHILDREN, adr.get(MdekKeys.HAS_CHILD));
		mdekAdr.put(MDEK_IS_PUBLISHED, adr.get(MdekKeys.IS_PUBLISHED));
//		mdekAdr.put(MDEK_USER_WRITE_PERMISSION, adr.get(MdekKeysSecurity.IDC_PERMISSION_HAS_ACCESS));
		List<IngridDocument> idcPermissions = (List<IngridDocument>) adr.get(MdekKeysSecurity.IDC_PERMISSIONS);
		mdekAdr.put(MDEK_USER_WRITE_PERMISSION, hasWritePermission(idcPermissions));
		mdekAdr.put(MDEK_USER_WRITE_SINGLE_PERMISSION, hasWriteSinglePermission(idcPermissions));
		mdekAdr.put(MDEK_USER_WRITE_TREE_PERMISSION, hasWriteTreePermission(idcPermissions));

		String adrDocType = getAddressDocType(adr);
		mdekAdr.put(MDEK_DOCTYPE, adrDocType);

		return mdekAdr;
	}

	public Object convertFromAddressRepresentation(MdekAddressBean data){
		IngridDocument udkAdr = new IngridDocument();

		// General Information
		udkAdr.put(MdekKeys.PARENT_UUID, data.getParentUuid());
		udkAdr.put(MdekKeys.UUID, data.getUuid());
		udkAdr.put(MdekKeys.CLASS, data.getAddressClass());

		IngridDocument responsibleUser = new IngridDocument();
		responsibleUser.put(MdekKeys.UUID, data.getAddressOwner());
		udkAdr.put(MdekKeys.RESPONSIBLE_USER, responsibleUser);

		// Class specific information
		udkAdr.put(MdekKeys.ORGANISATION, data.getOrganisation());
		udkAdr.put(MdekKeys.NAME, data.getName());
		udkAdr.put(MdekKeys.GIVEN_NAME, data.getGivenName());
		KeyValuePair kvp = mapFromKeyValue(MdekKeys.NAME_FORM_KEY, data.getNameForm());
		if (kvp.getValue() != null || kvp.getKey() != -1) {
			udkAdr.put(MdekKeys.NAME_FORM, kvp.getValue());
			udkAdr.put(MdekKeys.NAME_FORM_KEY, kvp.getKey());
		}
		kvp = mapFromKeyValue(MdekKeys.TITLE_OR_FUNCTION_KEY, data.getTitleOrFunction());
		if (kvp.getValue() != null || kvp.getKey() != -1) {
			udkAdr.put(MdekKeys.TITLE_OR_FUNCTION, kvp.getValue());
			udkAdr.put(MdekKeys.TITLE_OR_FUNCTION_KEY, kvp.getKey());
		}

		// Common information
		udkAdr.put(MdekKeys.STREET, data.getStreet());
		udkAdr.put(MdekKeys.POSTAL_CODE_OF_COUNTRY, data.getCountryCode());
		udkAdr.put(MdekKeys.POSTAL_CODE, data.getPostalCode());
		udkAdr.put(MdekKeys.CITY, data.getCity());
		udkAdr.put(MdekKeys.POST_BOX, data.getPobox());
		udkAdr.put(MdekKeys.POST_BOX_POSTAL_CODE, data.getPoboxPostalCode());
		udkAdr.put(MdekKeys.ADDRESS_DESCRIPTION, data.getAddressDescription());
		udkAdr.put(MdekKeys.FUNCTION, data.getTask());
		udkAdr.put(MdekKeys.COMMUNICATION, mapFromCommunicationTable(data.getCommunication()));

		//Thesaurus
		udkAdr.put(MdekKeys.SUBJECT_TERMS, mapFromThesTermTables(data.getThesaurusTermsTable(), data.getThesaurusFreeTermsTable()));
		
		// Comments
		udkAdr.put(MdekKeys.COMMENT_LIST, mapFromCommentTable(data.getCommentTable()));

//		log.debug("Converted the following address to an IngridDocument:");
//		printHashMap(udkAdr);
		cleanUpHashMap(udkAdr);
		return udkAdr;
	}
	
	public Object convertFromObjectRepresentation(MdekDataBean data){
		IngridDocument udkObj = new IngridDocument();

		// General
		udkObj.put(MdekKeys.ABSTRACT, data.getGeneralDescription());
		udkObj.put(MdekKeys.DATASET_ALTERNATE_NAME, data.getGeneralShortDescription());
		udkObj.put(MdekKeys.UUID, data.getUuid());
		udkObj.put(MdekKeys.PARENT_UUID, data.getParentUuid());
		udkObj.put(MdekKeys.TITLE, data.getObjectName());

		IngridDocument responsibleUser = new IngridDocument();
		responsibleUser.put(MdekKeys.UUID, data.getObjectOwner());
		udkObj.put(MdekKeys.RESPONSIBLE_USER, responsibleUser);

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
		udkObj.put(MdekKeys.METADATA_LANGUAGE, convertLanguageIdentifierToCode(data.getExtraInfoLangMetaData()));
		udkObj.put(MdekKeys.DATA_LANGUAGE, convertLanguageIdentifierToCode(data.getExtraInfoLangData()));
		udkObj.put(MdekKeys.PUBLICATION_CONDITION, data.getExtraInfoPublishArea());
		udkObj.put(MdekKeys.DATASET_INTENSIONS, data.getExtraInfoPurpose());
		udkObj.put(MdekKeys.DATASET_USAGE, data.getExtraInfoUse());
		udkObj.put(MdekKeys.EXPORTS, mapFromExtraInfoXMLExportTable(data.getExtraInfoXMLExportTable()));
		udkObj.put(MdekKeys.LEGISLATIONS, mapFromExtraInfoLegalBasicsTable(data.getExtraInfoLegalBasicsTable()));


		// Availability
		// Only map for object class == 0
		if (data.getObjectClass() != 0) {
			udkObj.put(MdekKeys.DATA_FORMATS, mapFromAvailDataFormatTable(data.getAvailabilityDataFormatTable()));
			udkObj.put(MdekKeys.MEDIUM_OPTIONS, mapFromAvailMediaOptionsTable(data.getAvailabilityMediaOptionsTable()));
			udkObj.put(MdekKeys.ORDERING_INSTRUCTIONS, data.getAvailabilityOrderInfo());
			udkObj.put(MdekKeys.USE_CONSTRAINTS, data.getAvailabilityNoteUse());
			udkObj.put(MdekKeys.FEES, data.getAvailabilityCosts());
		}

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
		udkObj.put(MdekKeys.RELATION_DESCRIPTION, data.getRelationDescription());

		Integer key = data.getRelationType();
		if (key != null && key != -1) {
			udkObj.put(MdekKeys.RELATION_TYPE_REF, data.getRelationType());
		} else {
			udkObj.put(MdekKeys.RELATION_TYPE_REF, data.getRelationType());
			udkObj.put(MdekKeys.RELATION_TYPE_NAME, data.getRelationTypeName());
		}

		switch(data.getObjectClass()) {
		case 0:	// Object of type 0 doesn't have any special values
			break;
		case 1:
			IngridDocument td1Map = new IngridDocument();			
			td1Map.put(MdekKeys.HIERARCHY_LEVEL, data.getRef1DataSet());
			td1Map.put(MdekKeys.VECTOR_TOPOLOGY_LEVEL, data.getRef1VFormatTopology());
			KeyValuePair kvp = mapFromKeyValue(MdekKeys.REFERENCESYSTEM_ID, data.getRef1SpatialSystem());
			if (kvp.getValue() != null || kvp.getKey() != -1) {
				td1Map.put(MdekKeys.COORDINATE_SYSTEM, kvp.getValue());
				td1Map.put(MdekKeys.REFERENCESYSTEM_ID, kvp.getKey());
			}
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
			kvp = mapFromKeyValue(MdekKeys.TYPE_OF_DOCUMENT_KEY, data.getRef2DocumentType());
			if (kvp.getValue() != null || kvp.getKey() != -1) {
				td2Map.put(MdekKeys.TYPE_OF_DOCUMENT, kvp.getValue());
				td2Map.put(MdekKeys.TYPE_OF_DOCUMENT_KEY, kvp.getKey());
			}
			td2Map.put(MdekKeys.SOURCE, data.getRef2BaseDataText());
			td2Map.put(MdekKeys.ADDITIONAL_BIBLIOGRAPHIC_INFO, data.getRef2BibData());
			td2Map.put(MdekKeys.DESCRIPTION_OF_TECH_DOMAIN, data.getRef2Explanation());
			udkObj.put(MdekKeys.TECHNICAL_DOMAIN_DOCUMENT, td2Map);
			break;
		case 3:
			IngridDocument td3Map = new IngridDocument();			
			kvp = mapFromKeyValue(MdekKeys.SERVICE_TYPE_KEY, data.getRef3ServiceType());
			if (kvp.getValue() != null || kvp.getKey() != -1) {
				td3Map.put(MdekKeys.SERVICE_TYPE, kvp.getValue());
				td3Map.put(MdekKeys.SERVICE_TYPE_KEY, kvp.getKey());
			}
			td3Map.put(MdekKeys.SYSTEM_ENVIRONMENT, data.getRef3SystemEnv());
			td3Map.put(MdekKeys.SYSTEM_HISTORY, data.getRef3History());
			td3Map.put(MdekKeys.DATABASE_OF_SYSTEM, data.getRef3BaseDataText());
			td3Map.put(MdekKeys.SERVICE_VERSION_LIST, data.getRef3ServiceVersion());
			td3Map.put(MdekKeys.DESCRIPTION_OF_TECH_DOMAIN, data.getRef3Explanation());
			td3Map.put(MdekKeys.SERVICE_OPERATION_LIST, mapFromOperationTable(data.getRef3Operation(), kvp.getKey()));
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

//		log.debug("Converted the following object to an IngridDocument:");
//		printHashMap(udkObj);
		cleanUpHashMap(udkObj);
		return udkObj;
	}

	
	public void setInitialValues(MdekAddressBean addr) {
		addr.setNameForm(sysListMapper.getInitialValue(MdekKeys.NAME_FORM_KEY));
		addr.setTitleOrFunction(sysListMapper.getInitialValue(MdekKeys.TITLE_OR_FUNCTION_KEY));
	}

	public void setInitialValues(MdekDataBean obj) {
		obj.setSpatialRefAltVDate(sysListMapper.getInitialKeyFromListId(VERTICAL_EXTENT_VDATUM_ID));
		obj.setSpatialRefAltMeasure(sysListMapper.getInitialKeyFromListId(VERTICAL_EXTENT_UNIT_ID));
		obj.setTimeRefPeriodicity(sysListMapper.getInitialKeyFromListId(TIME_PERIOD_ID));
		obj.setTimeRefStatus(sysListMapper.getInitialKeyFromListId(TIME_STATUS_ID));
		obj.setRef1DataSet(sysListMapper.getInitialKeyFromListId(HIERARCHY_LEVEL_ID));
		// TODO Check if Ref1VFormatTopology is set properly
		obj.setRef1VFormatTopology(sysListMapper.getInitialKeyFromListId(VECTOR_TOPOLOGY_LEVEL_ID));

		Integer key = sysListMapper.getInitialKeyFromListId(TIME_SCALE_ID);
		if (key != null) { obj.setTimeRefIntervalUnit(key.toString()); };

		obj.setRef1SpatialSystem(sysListMapper.getInitialValue(MdekKeys.REFERENCESYSTEM_ID));
		obj.setRef3ServiceType(sysListMapper.getInitialValue(MdekKeys.SERVICE_TYPE_KEY));

		key = sysListMapper.getInitialKeyFromListId(DATA_LANGUAGE_ID);
		if (key != null) {
			obj.setExtraInfoLangData(key.toString());
			obj.setExtraInfoLangMetaData(key.toString());
		}

		if (obj.getExtraInfoPublishArea() == null) {
			obj.setExtraInfoPublishArea(sysListMapper.getInitialKeyFromListId(PUBLICATION_CONDITION_ID));
		}
	}

	// ------------------------------- Helper Methods -----------------------------------

	
	/****************************************************************************
	 * Mapping from the Mdek gui representation to the IngridDocument Structure *
	 ****************************************************************************/
	
	private KeyValuePair mapFromKeyValue(String key, String val) {
		Integer k = sysListMapper.getKey(key, val);
		
		if (k == null) {
			if (val != null && val.trim().length() == 0) {
				val = null;
			}
			return new KeyValuePair(new Integer(-1), val);
		} else {
			return new KeyValuePair(k, null);
		}
	}

	private ArrayList<IngridDocument> mapFromGeneralAddressTable(ArrayList<MdekAddressBean> adrTable) {
		ArrayList<IngridDocument> resultList = new ArrayList<IngridDocument>();

		for (MdekAddressBean address : adrTable) {
			IngridDocument mappedEntry = new IngridDocument();
			mappedEntry.put(MdekKeys.UUID, address.getUuid());

			String val = address.getNameOfRelation();
			Integer key = sysListMapper.getKeyFromListId(MDEK_ADDRESS_REF_SPECIAL_ID, val);
			if (key != null) {
				// Found special address ref
				mappedEntry.put(MdekKeys.RELATION_TYPE_ID, key);
				mappedEntry.put(MdekKeys.RELATION_TYPE_REF, new Integer(MDEK_ADDRESS_REF_SPECIAL_ID));				
			} else {
				key = sysListMapper.getKeyFromListId(MDEK_ADDRESS_REF_ID, val);
				if (key != null) {
					// Found normal address ref
					mappedEntry.put(MdekKeys.RELATION_TYPE_ID, key);
					mappedEntry.put(MdekKeys.RELATION_TYPE_REF, new Integer(MDEK_ADDRESS_REF_ID));					
				} else {
					// Could not resolve -> free entry
					mappedEntry.put(MdekKeys.RELATION_TYPE_NAME, val);
					mappedEntry.put(MdekKeys.RELATION_TYPE_ID, new Integer(-1));					
				}
			}

			resultList.add(mappedEntry);
		}
		return resultList;
	}

	private ArrayList<IngridDocument> mapFromCommunicationTable(List<HashMap<String, String>> commMap){
		ArrayList<IngridDocument> resultList = new ArrayList<IngridDocument>(); 

		for (HashMap<String, String> comm : commMap) {
			IngridDocument mappedEntry = new IngridDocument();
			mappedEntry.put(MdekKeys.COMMUNICATION_DESCRIPTION, comm.get(MDEK_ADDRESS_COMM_DESCRIPTION));
			KeyValuePair kvp = mapFromKeyValue(MdekKeys.COMMUNICATION_MEDIUM_KEY, comm.get(MDEK_ADDRESS_COMM_MEDIUM));
			if (kvp.getValue() != null || kvp.getKey() != -1) {
				mappedEntry.put(MdekKeys.COMMUNICATION_MEDIUM, kvp.getValue());
				mappedEntry.put(MdekKeys.COMMUNICATION_MEDIUM_KEY, kvp.getKey());
			}
			mappedEntry.put(MdekKeys.COMMUNICATION_VALUE, comm.get(MDEK_ADDRESS_COMM_VALUE));
			resultList.add(mappedEntry);
		}

		return resultList;	
	}
	
	private ArrayList<IngridDocument> mapFromObjectLinksTable(ArrayList<MdekDataBean> objList) {
		ArrayList<IngridDocument> resultList = new ArrayList<IngridDocument>();
		if (objList == null)
			return resultList;

		for (MdekDataBean obj : objList) {
			IngridDocument mappedEntry = (IngridDocument) convertFromObjectRepresentation(obj);
			resultList.add(mappedEntry);
		}

		return resultList;
	}

	private ArrayList<IngridDocument> mapFromUrlLinksTable(ArrayList<UrlBean> urlList) {
		ArrayList<IngridDocument> resultList = new ArrayList<IngridDocument>();
		if (urlList == null)
			return resultList;

		for (UrlBean url : urlList) {
			IngridDocument mappedUrl = new IngridDocument();
			KeyValuePair kvp = mapFromKeyValue(MdekKeys.LINKAGE_DATATYPE_KEY, url.getDatatype());
			if (kvp.getValue() != null || kvp.getKey() != -1) {
				mappedUrl.put(MdekKeys.LINKAGE_DATATYPE, kvp.getValue());
				mappedUrl.put(MdekKeys.LINKAGE_DATATYPE_KEY, kvp.getKey());
			}
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
	
	private ArrayList<IngridDocument> mapFromLocationTables(ArrayList<LocationBean> locationSNS, ArrayList<LocationBean> locationFree) {
		ArrayList<IngridDocument> resultList = new ArrayList<IngridDocument>();
		if (locationFree != null) {
			for (LocationBean loc : locationFree) {
				IngridDocument res = new IngridDocument();
				res.put(MdekKeys.LOCATION_TYPE, "F");

				KeyValuePair kvp = mapFromKeyValue(MdekKeys.LOCATION_NAME_KEY, loc.getName());
				if (kvp.getValue() != null || kvp.getKey() != -1) {
					res.put(MdekKeys.LOCATION_NAME, kvp.getValue());
					res.put(MdekKeys.LOCATION_NAME_KEY, kvp.getKey());
				}
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


	private ArrayList<IngridDocument> mapFromTimeRefTable(ArrayList<TimeReferenceBean> refList) {
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

	private ArrayList<IngridDocument> mapFromExtraInfoXMLExportTable(ArrayList<String> refList) {
		ArrayList<IngridDocument> resultList = new ArrayList<IngridDocument>();
		if (refList == null)
			return resultList;

		for (String ref : refList) {
			IngridDocument result = new IngridDocument();
			KeyValuePair kvp = mapFromKeyValue(MdekKeys.EXPORT_KEY, ref);
			result.put(MdekKeys.EXPORT_KEY, kvp.getKey());
			result.put(MdekKeys.EXPORT_VALUE, kvp.getValue());
			resultList.add(result);
		}
		return resultList;
	}
	
	private ArrayList<IngridDocument> mapFromExtraInfoLegalBasicsTable(ArrayList<String> refList) {
		ArrayList<IngridDocument> resultList = new ArrayList<IngridDocument>();
		if (refList == null)
			return resultList;

		for (String ref : refList) {
			IngridDocument result = new IngridDocument();
			KeyValuePair kvp = mapFromKeyValue(MdekKeys.LEGISLATION_KEY, ref);
			result.put(MdekKeys.LEGISLATION_KEY, kvp.getKey());
			result.put(MdekKeys.LEGISLATION_VALUE, kvp.getValue());
			resultList.add(result);
		}
		return resultList;
	}

	private ArrayList<IngridDocument> mapFromAvailDataFormatTable(ArrayList<DataFormatBean> refList) {
		ArrayList<IngridDocument> resultList = new ArrayList<IngridDocument>();
		if (refList == null)
			return resultList;

		for (DataFormatBean ref : refList) {
			IngridDocument result = new IngridDocument();
			result.put(MdekKeys.FORMAT_FILE_DECOMPRESSION_TECHNIQUE, ref.getCompression());
			KeyValuePair kvp = mapFromKeyValue(MdekKeys.FORMAT_NAME_KEY, ref.getName());
			if (kvp.getValue() != null || kvp.getKey() != -1) {
				result.put(MdekKeys.FORMAT_NAME, kvp.getValue());
				result.put(MdekKeys.FORMAT_NAME_KEY, kvp.getKey());
			}
			result.put(MdekKeys.FORMAT_SPECIFICATION, ref.getPixelDepth());
			result.put(MdekKeys.FORMAT_VERSION, ref.getVersion());
			resultList.add(result);
		}
		return resultList;
	}


	private ArrayList<IngridDocument> mapFromAvailMediaOptionsTable(ArrayList<MediaOptionBean> refList) {
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


	private ArrayList<IngridDocument> mapFromThesTermTables(ArrayList<SNSTopic> snsList, ArrayList<String> freeList) {
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


	private ArrayList<IngridDocument> mapFromVFormatDetailsTable(ArrayList<VectorFormatDetailsBean> vFormatList) {
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


	private ArrayList<IngridDocument> mapFromScaleTable(ArrayList<ScaleBean> scaleList) {
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


	private ArrayList<IngridDocument> mapFromSymLinkDataTable(ArrayList<LinkDataBean> linkList) {
		ArrayList<IngridDocument> resultList = new ArrayList<IngridDocument>();
		if (linkList == null)
			return resultList;

		for (LinkDataBean l : linkList) {
			IngridDocument result = new IngridDocument();
			result.put(MdekKeys.SYMBOL_DATE, convertDateToTimestamp(l.getDate()));
			KeyValuePair kvp = mapFromKeyValue(MdekKeys.SYMBOL_CAT_KEY, l.getTitle());
			if (kvp.getValue() != null || kvp.getKey() != -1) {
				result.put(MdekKeys.SYMBOL_CAT, kvp.getValue());
				result.put(MdekKeys.SYMBOL_CAT_KEY, kvp.getKey());
			}
			result.put(MdekKeys.SYMBOL_EDITION, l.getVersion());
			resultList.add(result);
		}
		return resultList;
	}

	private ArrayList<IngridDocument> mapFromKeyLinkDataTable(ArrayList<LinkDataBean> linkList) {
		ArrayList<IngridDocument> resultList = new ArrayList<IngridDocument>();
		if (linkList == null)
			return resultList;

		for (LinkDataBean l : linkList) {
			IngridDocument result = new IngridDocument();
			result.put(MdekKeys.KEY_DATE, convertDateToTimestamp(l.getDate()));
			KeyValuePair kvp = mapFromKeyValue(MdekKeys.SUBJECT_CAT_KEY, l.getTitle());
			if (kvp.getValue() != null || kvp.getKey() != -1) {
				result.put(MdekKeys.SUBJECT_CAT, kvp.getValue());
				result.put(MdekKeys.SUBJECT_CAT_KEY, kvp.getKey());
			}
			result.put(MdekKeys.EDITION, l.getVersion());
			resultList.add(result);
		}
		return resultList;
	}

	private ArrayList<IngridDocument> mapFromDbContentTable(ArrayList<DBContentBean> dbList) {
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


	private ArrayList<IngridDocument> mapFromOperationTable(ArrayList<OperationBean> opList, Integer serviceType) {
		ArrayList<IngridDocument> resultList = new ArrayList<IngridDocument>();
		if (opList == null)
			return resultList;

		for (OperationBean op : opList) {
			IngridDocument result = new IngridDocument();
			if (serviceType == -1) {
				result.put(MdekKeys.SERVICE_OPERATION_NAME, op.getName());
				result.put(MdekKeys.SERVICE_OPERATION_NAME_KEY, new Integer(-1));
			} else {
				KeyValuePair kvp = mapFromKeyValue(MdekKeys.SERVICE_OPERATION_NAME_KEY+"."+serviceType, op.getName());
//				result.put(MdekKeys.SERVICE_OPERATION_NAME, kvp.getValue());
				result.put(MdekKeys.SERVICE_OPERATION_NAME_KEY, kvp.getKey());
			}
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

	private ArrayList<IngridDocument> mapFromOperationParamTable(ArrayList<OperationParameterBean> opList) {
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


	private ArrayList<IngridDocument> mapFromCommentTable(ArrayList<CommentBean> commentList) {
		ArrayList<IngridDocument> resultList = new ArrayList<IngridDocument>();
		if (commentList == null)
			return resultList;

		for (CommentBean c : commentList) {
			IngridDocument result = new IngridDocument();
			result.put(MdekKeys.COMMENT, c.getComment());
			IngridDocument createUserDoc = new IngridDocument();
			createUserDoc.put(MdekKeys.UUID, c.getUser().getUuid());
			result.put(MdekKeys.CREATE_USER, createUserDoc);
			result.put(MdekKeys.CREATE_TIME, convertDateToTimestamp(c.getDate()));
			resultList.add(result);
		}
		return resultList;
	}

	
	/****************************************************************************
	 * Mapping from the IngridDocument Structure to the Mdek gui representation *
	 ****************************************************************************/

	private KeyValuePair mapToKeyValuePair(Map<String, Object> obj, String key, String value) {
		Integer k = (Integer) obj.get(key);
		String val = (String) obj.get(value);
		if (k != null && k != -1) {
			val = sysListMapper.getValue(key, k);
		}

		if (val != null && val.trim().length() == 0) {
			val = null;
		}

		return new KeyValuePair(k, val);
	}
	
	private ArrayList<MdekAddressBean> mapToGeneralAddressTable(List<HashMap<String, Object>> adrTable) {
		ArrayList<MdekAddressBean> resultList = new ArrayList<MdekAddressBean>(); 
		if (adrTable == null)
			return resultList;

		for (HashMap<String, Object> adr : adrTable) {
			resultList.add(getDetailedAddressRepresentation(adr));
		}
		return resultList;
	}


	private ArrayList<HashMap<String, String>> mapToCommunicationTable(List<HashMap<String, Object>> commMap){
		ArrayList<HashMap<String, String>> resultCommMap = new ArrayList<HashMap<String, String>>(); 
		if (commMap == null)
			return resultCommMap;

		for (HashMap<String, Object> comm : commMap) {
			HashMap<String, String> resultComm = new HashMap<String, String>();
			resultComm.put(MDEK_ADDRESS_COMM_DESCRIPTION, (String) comm.get(MdekKeys.COMMUNICATION_DESCRIPTION));
			KeyValuePair kvp = mapToKeyValuePair(comm, MdekKeys.COMMUNICATION_MEDIUM_KEY, MdekKeys.COMMUNICATION_MEDIUM);
			resultComm.put(MDEK_ADDRESS_COMM_MEDIUM, kvp.getValue());
			resultComm.put(MDEK_ADDRESS_COMM_VALUE, (String) comm.get(MdekKeys.COMMUNICATION_VALUE));
			resultCommMap.add(resultComm);
		}

		return resultCommMap;	
	}
	
	private ArrayList<MdekDataBean> mapToObjectLinksTable(List<HashMap<String, Object>> objList) {
		ArrayList<MdekDataBean> resultList = new ArrayList<MdekDataBean>(); 
		if (objList == null)
			return resultList;

		for (HashMap<String, Object> obj : objList) {
			resultList.add(getDetailedObjectRepresentation(obj));
		}
		return resultList;
	}

	private ArrayList<UrlBean> mapToUrlLinksTable(List<HashMap<String, Object>> objList) {
		ArrayList<UrlBean> resultList = new ArrayList<UrlBean>(); 
		if (objList == null)
			return resultList;
		
		for (HashMap<String, Object> obj : objList) {
			UrlBean url = new UrlBean();
			KeyValuePair kvp = mapToKeyValuePair(obj, MdekKeys.LINKAGE_DATATYPE_KEY, MdekKeys.LINKAGE_DATATYPE);
			url.setDatatype(kvp.getValue());
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
	
	
	private ArrayList<LocationBean> mapToSpatialRefAdminUnitTable(List<HashMap<String, Object>> locList) {
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
	
	private ArrayList<LocationBean> mapToSpatialRefLocationTable(List<HashMap<String, Object>> locList) {
		ArrayList<LocationBean> resultList = new ArrayList<LocationBean>();
		if (locList == null)
			return resultList;

		for (HashMap<String, Object> location : locList) {
			String locationType = (String) location.get(MdekKeys.LOCATION_TYPE);
			if (locationType.equals("F")) {
				LocationBean loc = new LocationBean(); 
				loc.setType((String) location.get(MdekKeys.LOCATION_TYPE));
//				loc.setName((String) location.get(MdekKeys.LOCATION_NAME));
				KeyValuePair kvp = mapToKeyValuePair(location, MdekKeys.LOCATION_NAME_KEY, MdekKeys.LOCATION_NAME);
				loc.setName(kvp.getValue());
				loc.setLongitude1((Double) location.get(MdekKeys.WEST_BOUNDING_COORDINATE));
				loc.setLatitude1((Double) location.get(MdekKeys.SOUTH_BOUNDING_COORDINATE));
				loc.setLongitude2((Double) location.get(MdekKeys.EAST_BOUNDING_COORDINATE));
				loc.setLatitude2((Double) location.get(MdekKeys.NORTH_BOUNDING_COORDINATE));
				resultList.add(loc);
			}
		}
		return resultList;
	}


	private ArrayList<TimeReferenceBean> mapToTimeRefTable(List<HashMap<String, Object>> refList) {
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

	
	private ArrayList<String> mapToExtraInfoXMLExportTable(List<HashMap<String, Object>> refList) {
		ArrayList<String> resultList = new ArrayList<String>();
		if (refList == null)
			return resultList;
		
		for (HashMap<String, Object> ref : refList) {
			KeyValuePair kvp = mapToKeyValuePair(ref, MdekKeys.EXPORT_KEY, MdekKeys.EXPORT_VALUE);
			resultList.add(kvp.getValue());
		}
		return resultList;
	}

	private ArrayList<String> mapToExtraInfoLegalBasicsTable(List<HashMap<String, Object>> refList) {
		ArrayList<String> resultList = new ArrayList<String>();
		if (refList == null)
			return resultList;
		for (HashMap<String, Object> ref : refList) {
			KeyValuePair kvp = mapToKeyValuePair(ref, MdekKeys.LEGISLATION_KEY, MdekKeys.LEGISLATION_VALUE);
			resultList.add(kvp.getValue());
		}
		return resultList;
	}


	private ArrayList<DataFormatBean> mapToAvailDataFormatTable(List<HashMap<String, Object>> refList) {
		ArrayList<DataFormatBean> resultList = new ArrayList<DataFormatBean>();
		if (refList == null)
			return resultList;
		for (HashMap<String, Object> ref : refList) {
			DataFormatBean df = new DataFormatBean();
			KeyValuePair kvp = mapToKeyValuePair(ref, MdekKeys.FORMAT_NAME_KEY, MdekKeys.FORMAT_NAME);
			df.setName(kvp.getValue());
//			df.setNameKey(kvp.getKey());
			df.setCompression((String) ref.get(MdekKeys.FORMAT_FILE_DECOMPRESSION_TECHNIQUE));
			df.setPixelDepth((String) ref.get(MdekKeys.FORMAT_SPECIFICATION));
			df.setVersion((String) ref.get(MdekKeys.FORMAT_VERSION));
			resultList.add(df);
		}
		return resultList;
	}

	private ArrayList<MediaOptionBean> mapToAvailMediaOptionsTable(List<HashMap<String, Object>> refList) {
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


	private ArrayList<SNSTopic> mapToThesTermsTable(List<HashMap<String, Object>> topicList) {
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

	private ArrayList<String> mapToThesFreeTermsTable(List<HashMap<String, Object>> topicList) {
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


	private ArrayList<VectorFormatDetailsBean> mapToVFormatDetailsTable(List<HashMap<String, Object>> vFormatList) {
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


	private ArrayList<ScaleBean> mapToScaleTable(List<HashMap<String, Object>> scaleList) {
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


	private ArrayList<LinkDataBean> mapToSymLinkDataTable(List<HashMap<String, Object>> linkList) {
		ArrayList<LinkDataBean> resultList = new ArrayList<LinkDataBean>();
		if (linkList == null)
			return resultList;
		for (HashMap<String, Object> topic : linkList) {
			LinkDataBean l = new LinkDataBean();
			l.setDate(convertTimestampToDate((String) topic.get(MdekKeys.SYMBOL_DATE)));
			KeyValuePair kvp = mapToKeyValuePair(topic, MdekKeys.SYMBOL_CAT_KEY, MdekKeys.SYMBOL_CAT);
			l.setTitle((String) kvp.getValue());
			l.setVersion((String) topic.get(MdekKeys.SYMBOL_EDITION));
			resultList.add(l);
		}
		return resultList;
	}

	private  ArrayList<LinkDataBean> mapToKeyLinkDataTable(List<HashMap<String, Object>> linkList) {
		ArrayList<LinkDataBean> resultList = new ArrayList<LinkDataBean>();
		if (linkList == null)
			return resultList;
		for (HashMap<String, Object> topic : linkList) {
			LinkDataBean l = new LinkDataBean();
			l.setDate(convertTimestampToDate((String) topic.get(MdekKeys.KEY_DATE)));
			KeyValuePair kvp = mapToKeyValuePair(topic, MdekKeys.SUBJECT_CAT_KEY, MdekKeys.SUBJECT_CAT);
			l.setTitle((String) kvp.getValue());
			l.setVersion((String) topic.get(MdekKeys.EDITION));
			resultList.add(l);
		}
		return resultList;
	}

	private ArrayList<DBContentBean> mapToDbContentTable(List<HashMap<String, Object>> dbList) {
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


	private ArrayList<OperationBean> mapToOperationTable(List<HashMap<String, Object>> opList, Integer serviceType) {
		ArrayList<OperationBean> resultList = new ArrayList<OperationBean>();
		if (opList == null)
			return resultList;
		for (HashMap<String, Object> operation : opList) {
			OperationBean op = new OperationBean();
			if (serviceType == null || serviceType == -1) {
				op.setName((String) operation.get(MdekKeys.SERVICE_OPERATION_NAME));
			} else {
				String val = sysListMapper.getValue(MdekKeys.SERVICE_OPERATION_NAME_KEY+"."+serviceType, (Integer) operation.get(MdekKeys.SERVICE_OPERATION_NAME_KEY));
				op.setName(val);				
			}
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


	private ArrayList<OperationParameterBean> mapToOperationParamTable(List<HashMap<String, Object>> opList) {
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

	

	private ArrayList<CommentBean> mapToCommentTable(List<HashMap<String, Object>> commentList) {
		ArrayList<CommentBean> resultList = new ArrayList<CommentBean>();
		if (commentList == null)
			return resultList;
		for (HashMap<String, Object> comment : commentList) {
			CommentBean c = new CommentBean();
			c.setComment((String) comment.get(MdekKeys.COMMENT));
			IngridDocument createUserDoc = (IngridDocument) comment.get(MdekKeys.CREATE_USER);
			c.setUser(getDetailedAddressRepresentation(createUserDoc));
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
	private boolean hasWritePermission(List<IngridDocument> permissionList) {
		// TODO Implement tree and single user permissions
/*
		System.out.println("HAS_WRITE_ACCESS: " + MdekUtilsSecurity.hasWritePermission(permissionList));
		System.out.println("HAS_WRITE_TREE_ACCESS: " + MdekUtilsSecurity.hasWriteTreePermission(permissionList));
		System.out.println("HAS_WRITE_SINGLE_ACCESS: " + MdekUtilsSecurity.hasWriteSinglePermission(permissionList));
*/
		return MdekUtilsSecurity.hasWritePermission(permissionList);
	}

	private boolean hasWriteSinglePermission(List<IngridDocument> permissionList) {
		return MdekUtilsSecurity.hasWriteSinglePermission(permissionList);
	}
	private boolean hasWriteTreePermission(List<IngridDocument> permissionList) {
		return MdekUtilsSecurity.hasWriteTreePermission(permissionList);
	}


	private final static SimpleDateFormat timestampFormatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");

	private static Date convertTimestampToDate(String timeStamp) {
		if (timeStamp != null && timeStamp.length() != 0) {
			try {
				Date date = timestampFormatter.parse(timeStamp);
				return date;
			} catch (Exception ex){
				log.debug("Problems parsing timestamp from database: " + timeStamp, ex);
				return null;
			}
		} else {
			return null;
		}
	}

	private static String convertDateToTimestamp(Date date) {
		if (date == null || (date.getTime() == 0L)) {
			return null;
		} else {
			return MdekUtils.dateToTimestamp(date);
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
		TreeSet<Map.Entry<String, Object>> treeSet = new TreeSet<Map.Entry<String, Object>>(new MapEntryComparator());
		treeSet.addAll(entrySet);

		for (Map.Entry<String, Object> entry : treeSet) {
			log.debug("Key: "+entry.getKey()+" Value: "+entry.getValue());
		}
	}

    static public class MapEntryComparator implements Comparator<Map.Entry<String, Object>> {
    	public final int compare(Map.Entry<String, Object> entryA, Map.Entry<String, Object> entryB) {
            try {
            	return entryA.getKey().compareTo(entryB.getKey());
            } catch (Exception e) {
                return 0;
            }
        }
    }

	public SysListCache getSysListMapper() {
		return sysListMapper;
	}
	public void setSysListMapper(SysListCache sysListMapper) {
		this.sysListMapper = sysListMapper;
	}	


	public void cleanUpHashMap(IngridDocument doc) {
		if (doc == null)
			return;

		Collection values = doc.values();
		for (Iterator it = values.iterator(); it.hasNext();) {			
			Object element = it.next();
			if (element == null) {
				it.remove();				
			} else if (element instanceof String) {
				if (((String) element).trim().length() == 0) {
					it.remove();
				}
			} else if (element instanceof ArrayList) {
				if (((ArrayList) element).isEmpty()) {
					it.remove();
				}
			} else if (element instanceof IngridDocument) {
				cleanUpHashMap((IngridDocument) element);
				if (((IngridDocument) element).isEmpty()) {
					it.remove();
				}
			}
		}
	}

	public static String convertLanguageIdentifierToCode(String identifier) {
		if (identifier == null || identifier.length() == 0) {
			return identifier;
		} else if (identifier.equals(LANGUAGE_ID_GERMAN)) {
			return LANGUAGE_CODE_GERMAN;
		} else if (identifier.equals(LANGUAGE_ID_ENGLISH)) {
			return LANGUAGE_CODE_ENGLISH;
		} else {
			log.debug("Could not convert language identifier '"+identifier+"' to code.");
			return "";
		}
	}

	public static String convertLanguageCodeToIdentifier(String code) {
		if (code == null || code.length() == 0) {
			return code;
		} else if (code.equals(LANGUAGE_CODE_GERMAN)) {
			return LANGUAGE_ID_GERMAN;
		} else if (code.equals(LANGUAGE_CODE_ENGLISH)) {
			return LANGUAGE_ID_ENGLISH;
		} else {
			log.debug("Could not convert language code '"+code+"' to identifier.");
			return "";
		}
	}
}