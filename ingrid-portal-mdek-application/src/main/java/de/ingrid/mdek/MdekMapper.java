package de.ingrid.mdek;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import de.ingrid.mdek.MdekUtils.PublishType;
import de.ingrid.mdek.MdekUtils.SearchtermType;
import de.ingrid.mdek.MdekUtils.UserOperation;
import de.ingrid.mdek.MdekUtils.WorkState;
import de.ingrid.mdek.MdekUtilsSecurity.IdcPermission;
import de.ingrid.mdek.beans.CommentBean;
import de.ingrid.mdek.beans.KeyValuePair;
import de.ingrid.mdek.beans.TreeNodeBean;
import de.ingrid.mdek.beans.address.CommunicationBean;
import de.ingrid.mdek.beans.address.MdekAddressBean;
import de.ingrid.mdek.beans.object.AdditionalFieldBean;
import de.ingrid.mdek.beans.object.ConformityBean;
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
import de.ingrid.mdek.beans.object.UsageLimitationBean;
import de.ingrid.mdek.beans.object.VectorFormatDetailsBean;
import de.ingrid.mdek.dwr.services.sns.SNSTopic;
import de.ingrid.mdek.dwr.services.sns.SNSTopic.Source;
import de.ingrid.mdek.dwr.services.sns.SNSTopic.Type;
import de.ingrid.utils.IngridDocument;
import edu.emory.mathcs.backport.java.util.Arrays;

public class MdekMapper implements DataMapperInterface {

	private final static Logger log = Logger.getLogger(MdekMapper.class);
	private SysListCache sysListMapper;
	private ResourceBundle snsResourceBundle;

	// Init Method is called by the Spring Framework on initialization
    public void init() throws Exception {
		// Fetch the sns resource bundle for location topic type mapping
    	snsResourceBundle = ResourceBundle.getBundle("sns");
    }


	@SuppressWarnings("unchecked")
	public MdekDataBean getDetailedObjectRepresentation(IngridDocument obj) {
		if (obj == null) {
			return null;
		}

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

		MdekAddressBean responsibleUser = getDetailedAddressRepresentation((IngridDocument) obj.get(MdekKeysSecurity.RESPONSIBLE_USER));
		if (responsibleUser != null) {
			mdekObj.setObjectOwner(responsibleUser.getUuid());
		}

		// QA Fields
		MdekAddressBean assignerUser = getDetailedAddressRepresentation((IngridDocument) obj.get(MdekKeys.ASSIGNER_USER));
		if (assignerUser != null) {
			mdekObj.setAssignerUser(assignerUser);
		}
		mdekObj.setAssignTime(convertTimestampToDate((String) obj.get(MdekKeys.ASSIGN_TIME)));
		UserOperation u = (UserOperation) obj.get(MdekKeys.RESULTINFO_USER_OPERATION);
		if (u != null) {
			mdekObj.setUserOperation(u.toString());
		}

		String workStateStr = (String) obj.get(MdekKeys.WORK_STATE); 
		WorkState workState = null;
		if (workStateStr != null) {
			workState = EnumUtil.mapDatabaseToEnumConst(WorkState.class, workStateStr);
		} else {
			workState = WorkState.IN_BEARBEITUNG;
		}
		mdekObj.setWorkState(workState.getDbValue());

		mdekObj.setHasChildren((Boolean) obj.get(MdekKeys.HAS_CHILD));
		mdekObj.setObjectName((String) obj.get(MdekKeys.TITLE));
		mdekObj.setGeneralAddressTable(mapToGeneralAddressTable((List<IngridDocument>) obj.get(MdekKeys.ADR_REFERENCES_TO)));
		mdekObj.setCreationTime(convertTimestampToDisplayDate((String) obj.get(MdekKeys.DATE_OF_CREATION)));
		mdekObj.setModificationTime(convertTimestampToDisplayDate((String) obj.get(MdekKeys.DATE_OF_LAST_MODIFICATION)));
		mdekObj.setLastEditor(getDetailedAddressRepresentation((IngridDocument) obj.get(MdekKeys.MOD_USER)));

		List<IngridDocument> idcPermissions = (List<IngridDocument>) obj.get(MdekKeysSecurity.IDC_PERMISSIONS);
		mdekObj.setWritePermission(hasWritePermission(idcPermissions));
		mdekObj.setWriteSinglePermission(hasWriteSinglePermission(idcPermissions));
		mdekObj.setWriteTreePermission(hasWriteTreePermission(idcPermissions));
		mdekObj.setWriteSubTreePermission(hasWriteSubTreePermission(idcPermissions));

		mdekObj.setIsPublished((Boolean) obj.get(MdekKeys.IS_PUBLISHED));
		String markDeleted = (String) obj.get(MdekKeys.MARK_DELETED);
		mdekObj.setIsMarkedDeleted(markDeleted != null && markDeleted.equals("Y"));

		// Comments
		mdekObj.setCommentTable(mapToCommentTable((List<IngridDocument>) obj.get(MdekKeys.COMMENT_LIST)));

		// Information about the parent object
		IngridDocument parentDetails = (IngridDocument) obj.get(MdekKeys.PARENT_INFO);
		if (parentDetails != null) {
			mdekObj.setParentPublicationCondition((Integer) parentDetails.get(MdekKeys.PUBLICATION_CONDITION));
		}		
		
		// Spatial
		mdekObj.setSpatialRefAdminUnitTable(mapToSpatialRefAdminUnitTable((List<IngridDocument>) obj.get(MdekKeys.LOCATIONS)));
		mdekObj.setSpatialRefLocationTable(mapToSpatialRefLocationTable((List<IngridDocument>) obj.get(MdekKeys.LOCATIONS)));
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
		mdekObj.setTimeRefIntervalNum((String) obj.get(MdekKeys.TIME_SCALE));
		mdekObj.setTimeRefIntervalUnit((String) obj.get(MdekKeys.TIME_STEP));
		mdekObj.setTimeRefTable(mapToTimeRefTable((List<IngridDocument>) obj.get(MdekKeys.DATASET_REFERENCES)));
		mdekObj.setTimeRefExplanation((String) obj.get(MdekKeys.DESCRIPTION_OF_TEMPORAL_DOMAIN));

		// ExtraInfo
		mdekObj.setExtraInfoLangMetaDataCode((Integer)obj.get(MdekKeys.METADATA_LANGUAGE_CODE));
		mdekObj.setExtraInfoLangDataCode((Integer)obj.get(MdekKeys.DATA_LANGUAGE_CODE));

		mdekObj.setExtraInfoPublishArea((Integer) obj.get(MdekKeys.PUBLICATION_CONDITION));

		// Inspire field
		mdekObj.setExtraInfoConformityTable(mapToExtraInfoConformityTable((List<IngridDocument>) obj.get(MdekKeys.CONFORMITY_LIST)));

		mdekObj.setExtraInfoPurpose((String) obj.get(MdekKeys.DATASET_INTENTIONS));
		mdekObj.setExtraInfoUse((String) obj.get(MdekKeys.DATASET_USAGE));
		mdekObj.setExtraInfoXMLExportTable(mapToExtraInfoXMLExportTable((List<IngridDocument>) obj.get(MdekKeys.EXPORT_CRITERIA)));
		mdekObj.setExtraInfoLegalBasicsTable(mapToExtraInfoLegalBasicsTable((List<IngridDocument>) obj.get(MdekKeys.LEGISLATIONS)));

		// Availability
		// Inspire field
		mdekObj.setAvailabilityUsageLimitationTable(mapToAvailUsageLimitationTable((List<IngridDocument>) obj.get(MdekKeys.ACCESS_LIST)));
		
		mdekObj.setAvailabilityOrderInfo((String) obj.get(MdekKeys.ORDERING_INSTRUCTIONS));
		mdekObj.setAvailabilityDataFormatTable(mapToAvailDataFormatTable((List<IngridDocument>) obj.get(MdekKeys.DATA_FORMATS)));
		mdekObj.setAvailabilityMediaOptionsTable(mapToAvailMediaOptionsTable((List<IngridDocument>) obj.get(MdekKeys.MEDIUM_OPTIONS)));
		
		// Thesaurus
		mdekObj.setThesaurusInspireTermsList(mapToInspireTermTable((List<IngridDocument>) obj.get(MdekKeys.SUBJECT_TERMS_INSPIRE)));
		mdekObj.setThesaurusTermsTable(mapToThesTermsTable((List<IngridDocument>) obj.get(MdekKeys.SUBJECT_TERMS)));

		List<Integer> intList = (List<Integer>) obj.get(MdekKeys.TOPIC_CATEGORIES);
		if (intList != null)
			mdekObj.setThesaurusTopicsList(intList);

		String isCatalogStr = (String) obj.get(MdekKeys.IS_CATALOG_DATA);
		if (isCatalogStr != null && isCatalogStr.equalsIgnoreCase("Y")) {
			mdekObj.setThesaurusEnvExtRes(true);
		} else {
			mdekObj.setThesaurusEnvExtRes(false);
		}

		intList = (List<Integer>) obj.get(MdekKeys.ENV_TOPICS);
		if (intList != null)
			mdekObj.setThesaurusEnvTopicsList(intList);
		
		intList = (List<Integer>) obj.get(MdekKeys.ENV_CATEGORIES);
		if (intList != null)
			mdekObj.setThesaurusEnvCatsList(intList);
		
		// Links
		mdekObj.setLinksToObjectTable(mapToObjectLinksTable((List<IngridDocument>) obj.get(MdekKeys.OBJ_REFERENCES_TO)));
		mdekObj.setLinksFromObjectTable(mapToObjectLinksTable((List<IngridDocument>) obj.get(MdekKeys.OBJ_REFERENCES_FROM)));
		mdekObj.setLinksFromPublishedObjectTable(mapToObjectLinksTable((List<IngridDocument>) obj.get(MdekKeys.OBJ_REFERENCES_FROM_PUBLISHED_ONLY)));
		mdekObj.setLinksToUrlTable(mapToUrlLinksTable((List<IngridDocument>) obj.get(MdekKeys.LINKAGES)));
		mdekObj.setRelationType((Integer) obj.get(MdekKeys.RELATION_TYPE_REF));
		mdekObj.setRelationTypeName((String) obj.get(MdekKeys.RELATION_TYPE_NAME));
		mdekObj.setRelationDescription((String) obj.get(MdekKeys.RELATION_DESCRIPTION));

		// Additional Fields
		mdekObj.setAdditionalFields(mapToAdditionalFields((List<IngridDocument>) obj.get(MdekKeys.ADDITIONAL_FIELDS)));

		switch(mdekObj.getObjectClass()) {
		case 0:	// Object of type 0 doesn't have any special values
			break;
		case 1:
			IngridDocument td1Map = (IngridDocument) obj.get(MdekKeys.TECHNICAL_DOMAIN_MAP);
			if (td1Map == null)
				break;

			mdekObj.setRef1ObjectIdentifier((String) td1Map.get(MdekKeys.DATASOURCE_UUID));
			mdekObj.setRef1DataSet((Integer) td1Map.get(MdekKeys.HIERARCHY_LEVEL));
			mdekObj.setRef1VFormatTopology((Integer) td1Map.get(MdekKeys.VECTOR_TOPOLOGY_LEVEL));
			KeyValuePair kvp = mapToKeyValuePair(td1Map, MdekKeys.REFERENCESYSTEM_ID, MdekKeys.COORDINATE_SYSTEM);
			mdekObj.setRef1SpatialSystem(kvp.getValue());
			mdekObj.setRef1Coverage((Double) td1Map.get(MdekKeys.DEGREE_OF_RECORD));
			mdekObj.setRef1AltAccuracy((Double) td1Map.get(MdekKeys.POS_ACCURACY_VERTICAL));
			mdekObj.setRef1PosAccuracy((Double) td1Map.get(MdekKeys.RESOLUTION));
			mdekObj.setRef1BasisText((String) td1Map.get(MdekKeys.TECHNICAL_BASE));
			mdekObj.setRef1DataBasisText((String) td1Map.get(MdekKeys.DATA));
			
			List<String> strList = (List<String>) td1Map.get(MdekKeys.FEATURE_TYPE_LIST);
			if (strList != null)
				mdekObj.setRef1Data(strList);
			
			intList = (List<Integer>) td1Map.get(MdekKeys.SPATIAL_REPRESENTATION_TYPE_LIST);
			if (intList != null)
				mdekObj.setRef1Representation(intList);
			
			mdekObj.setRef1VFormatDetails(mapToVFormatDetailsTable((List<IngridDocument>) td1Map.get(MdekKeys.GEO_VECTOR_LIST)));
			mdekObj.setRef1Scale(mapToScaleTable((List<IngridDocument>) td1Map.get(MdekKeys.PUBLICATION_SCALE_LIST)));
			mdekObj.setRef1SymbolsText(mapToSymLinkDataTable((List<IngridDocument>) td1Map.get(MdekKeys.SYMBOL_CATALOG_LIST)));
			mdekObj.setRef1KeysText(mapToKeyLinkDataTable((List<IngridDocument>) td1Map.get(MdekKeys.KEY_CATALOG_LIST)));
			mdekObj.setRef1ProcessText((String) td1Map.get(MdekKeys.METHOD_OF_PRODUCTION));
			break;
		case 2:
			IngridDocument td2Map = (IngridDocument) obj.get(MdekKeys.TECHNICAL_DOMAIN_DOCUMENT);
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
			IngridDocument td3Map = (IngridDocument) obj.get(MdekKeys.TECHNICAL_DOMAIN_SERVICE);
			if (td3Map == null)
				break;

			mdekObj.setRef3ServiceType((Integer) td3Map.get(MdekKeys.SERVICE_TYPE_KEY));
			mdekObj.setRef3ServiceTypeTable(mapToServiceTypeTable((List<IngridDocument>) td3Map.get(MdekKeys.SERVICE_TYPE2_LIST)));

			mdekObj.setRef3SystemEnv((String) td3Map.get(MdekKeys.SYSTEM_ENVIRONMENT));
			mdekObj.setRef3History((String) td3Map.get(MdekKeys.SYSTEM_HISTORY));
			mdekObj.setRef3BaseDataText((String) td3Map.get(MdekKeys.DATABASE_OF_SYSTEM));
			mdekObj.setRef3ServiceVersion((List<String>) td3Map.get(MdekKeys.SERVICE_VERSION_LIST));
			mdekObj.setRef3Explanation((String) td3Map.get(MdekKeys.DESCRIPTION_OF_TECH_DOMAIN));
			mdekObj.setRef3Scale(mapToScaleTable((List<IngridDocument>) td3Map.get(MdekKeys.PUBLICATION_SCALE_LIST)));
			mdekObj.setRef3Operation(mapToOperationTable((List<IngridDocument>) td3Map.get(MdekKeys.SERVICE_OPERATION_LIST), (Integer) td3Map.get(MdekKeys.SERVICE_TYPE_KEY)));

			break;
		case 4:
			IngridDocument td4Map = (IngridDocument) obj.get(MdekKeys.TECHNICAL_DOMAIN_PROJECT);
			if (td4Map == null)
				break;
			mdekObj.setRef4ParticipantsText((String) td4Map.get(MdekKeys.MEMBER_DESCRIPTION));
			mdekObj.setRef4PMText((String) td4Map.get(MdekKeys.LEADER_DESCRIPTION));
			mdekObj.setRef4Explanation((String) td4Map.get(MdekKeys.DESCRIPTION_OF_TECH_DOMAIN));
			break;
		case 5:
			IngridDocument td5Map = (IngridDocument) obj.get(MdekKeys.TECHNICAL_DOMAIN_DATASET);
			if (td5Map == null)
				break;
			mdekObj.setRef5Explanation((String) td5Map.get(MdekKeys.DESCRIPTION_OF_TECH_DOMAIN));
			mdekObj.setRef5MethodText((String) td5Map.get(MdekKeys.METHOD));
			mdekObj.setRef5dbContent(mapToDbContentTable((List<IngridDocument>) td5Map.get(MdekKeys.PARAMETERS)));
			break;
		}
		
		
		mdekObj.setNodeAppType("O");
		
		String nodeDocType = getObjectDocType(obj);
		mdekObj.setNodeDocType(nodeDocType);

		return mdekObj;
	}
	
	@SuppressWarnings("unchecked")
	public MdekAddressBean getDetailedAddressRepresentation(IngridDocument adr) {
		if (adr == null) {
			return null;
		}

		MdekAddressBean mdekAddress = new MdekAddressBean();

		// Information about the parent object
		IngridDocument parentDetails = (IngridDocument) adr.get(MdekKeys.PARENT_INFO);
		if (parentDetails != null) {
			mdekAddress.setParentClass((Integer) parentDetails.get(MdekKeys.CLASS));
		}

		MdekAddressBean responsibleUser = getDetailedAddressRepresentation((IngridDocument) adr.get(MdekKeysSecurity.RESPONSIBLE_USER));
		if (responsibleUser != null) {
			mdekAddress.setAddressOwner(responsibleUser.getUuid());
		}

		// QA Fields
		MdekAddressBean assignerUser = getDetailedAddressRepresentation((IngridDocument) adr.get(MdekKeys.ASSIGNER_USER));
		if (assignerUser != null) {
			mdekAddress.setAssignerUser(assignerUser);
		}
		mdekAddress.setAssignTime(convertTimestampToDate((String) adr.get(MdekKeys.ASSIGN_TIME)));
		UserOperation u = (UserOperation) adr.get(MdekKeys.RESULTINFO_USER_OPERATION);
		if (u != null) {
			mdekAddress.setUserOperation(u.toString());
		}

		List<IngridDocument> idcPermissions = (List<IngridDocument>) adr.get(MdekKeysSecurity.IDC_PERMISSIONS);
		mdekAddress.setWritePermission(hasWritePermission(idcPermissions));
		mdekAddress.setWriteSinglePermission(hasWriteSinglePermission(idcPermissions));
		mdekAddress.setWriteTreePermission(hasWriteTreePermission(idcPermissions));
		mdekAddress.setWriteSubTreePermission(hasWriteSubTreePermission(idcPermissions));

		mdekAddress.setIsPublished((Boolean) adr.get(MdekKeys.IS_PUBLISHED));
		String markDeleted = (String) adr.get(MdekKeys.MARK_DELETED);
		mdekAddress.setIsMarkedDeleted(markDeleted != null && markDeleted.equals("Y"));
		
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
		mdekAddress.setWorkState(workState.getDbValue());
		mdekAddress.setHasChildren((Boolean) adr.get(MdekKeys.HAS_CHILD));		
		mdekAddress.setCreationTime(convertTimestampToDisplayDate((String) adr.get(MdekKeys.DATE_OF_CREATION)));
		mdekAddress.setModificationTime(convertTimestampToDisplayDate((String) adr.get(MdekKeys.DATE_OF_LAST_MODIFICATION)));
		mdekAddress.setLastEditor(getDetailedAddressRepresentation((IngridDocument) adr.get(MdekKeys.MOD_USER)));

		// Class specific information
		mdekAddress.setOrganisation((String) adr.get(MdekKeys.ORGANISATION));
		mdekAddress.setName((String) adr.get(MdekKeys.NAME));
		mdekAddress.setGivenName((String) adr.get(MdekKeys.GIVEN_NAME));

		mdekAddress.setNameForm(mapToKeyValuePair(adr, MdekKeys.NAME_FORM_KEY, MdekKeys.NAME_FORM).getValue());
		mdekAddress.setTitleOrFunction(mapToKeyValuePair(adr, MdekKeys.TITLE_OR_FUNCTION_KEY, MdekKeys.TITLE_OR_FUNCTION).getValue());
		mdekAddress.setCountryName(mapToKeyValuePair(adr, MdekKeys.COUNTRY_CODE, MdekKeys.COUNTRY_NAME).getValue());

		// Common information
		mdekAddress.setStreet((String) adr.get(MdekKeys.STREET));
		mdekAddress.setCountryCode((Integer) adr.get(MdekKeys.COUNTRY_CODE));
		mdekAddress.setPostalCode((String) adr.get(MdekKeys.POSTAL_CODE));
		mdekAddress.setCity((String) adr.get(MdekKeys.CITY));
		mdekAddress.setPobox((String) adr.get(MdekKeys.POST_BOX));
		mdekAddress.setPoboxPostalCode((String) adr.get(MdekKeys.POST_BOX_POSTAL_CODE));
		mdekAddress.setAddressDescription((String) adr.get(MdekKeys.ADDRESS_DESCRIPTION));
		mdekAddress.setTask((String) adr.get(MdekKeys.FUNCTION));
		mdekAddress.setCommunication(mapToCommunicationTable((List<IngridDocument>) adr.get(MdekKeys.COMMUNICATION)));

		// Thesaurus
		mdekAddress.setThesaurusTermsTable(mapToThesTermsTable((List<IngridDocument>) adr.get(MdekKeys.SUBJECT_TERMS)));

		// References
		mdekAddress.setLinksFromObjectTable(mapToObjectLinksTable((List<IngridDocument>) adr.get(MdekKeys.OBJ_REFERENCES_FROM)));
		mdekAddress.setLinksFromPublishedObjectTable(mapToObjectLinksTable((List<IngridDocument>) adr.get(MdekKeys.OBJ_REFERENCES_FROM_PUBLISHED_ONLY)));
		mdekAddress.setParentInstitutions(mapToGeneralAddressTable((List<IngridDocument>) adr.get(MdekKeys.PATH_ORGANISATIONS)));
		mdekAddress.setTotalNumReferences((Integer) adr.get(MdekKeys.OBJ_REFERENCES_FROM_TOTAL_NUM));

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
		mdekAddress.setCommentTable(mapToCommentTable((List<IngridDocument>) adr.get(MdekKeys.COMMENT_LIST)));
		
		mdekAddress.setNodeAppType("A");

		adr.put(MdekKeys.CLASS, mdekAddress.getAddressClass());
		String nodeDocType = getAddressDocType(adr);
		mdekAddress.setNodeDocType(nodeDocType);
		
		return mdekAddress;
	}
		
	private static String getObjectDocType(IngridDocument obj) {
		String nodeDocType = "Class" + ((Integer) obj.get(MdekKeys.CLASS));
		String workState = (String) obj.get(MdekKeys.WORK_STATE); 
		Boolean isPublished = (Boolean) obj.get(MdekKeys.IS_PUBLISHED) == null ? false : (Boolean) obj.get(MdekKeys.IS_PUBLISHED);

		if (workState != null) {
			if (workState.equals("V") && isPublished) {
				return nodeDocType;
			}

			nodeDocType += "_"+workState;
			nodeDocType += isPublished ? "V" : "";
		}
		return nodeDocType;
	}


	private static String getAddressDocType(IngridDocument adr) {
		String nodeDocType = null;
		Integer adrClass = (Integer) adr.get(MdekKeys.CLASS);
		String workState = (String) adr.get(MdekKeys.WORK_STATE); 
		Boolean isPublished = (Boolean) adr.get(MdekKeys.IS_PUBLISHED) == null ? false : (Boolean) adr.get(MdekKeys.IS_PUBLISHED);

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

		if (workState != null) {
			if (workState.equals("V") && isPublished) {
				return nodeDocType;
			}

			nodeDocType += "_"+workState;
			nodeDocType += isPublished ? "V" : "";
		}

		return nodeDocType;
	}

	public TreeNodeBean getSimpleObjectRepresentation(IngridDocument doc) {
		TreeNodeBean treeNode = getTreeNodeWithCommonParameters(doc);

		treeNode.setTitle((String) doc.get(MdekKeys.TITLE));
		String nodeDocType = getObjectDocType(doc);
		treeNode.setNodeDocType(nodeDocType);

		return treeNode;
	}

	public TreeNodeBean getSimpleAddressRepresentation(IngridDocument doc) {
		TreeNodeBean treeNode = getTreeNodeWithCommonParameters(doc);

		treeNode.setTitle(buildAddressTitle(doc));
		Integer adrClass = (Integer) doc.get(MdekKeys.CLASS);
		treeNode.setObjectClass(adrClass);
		String adrDocType = getAddressDocType(doc);
		treeNode.setNodeDocType(adrDocType);

		return treeNode;
	}

	@SuppressWarnings("unchecked")
	private TreeNodeBean getTreeNodeWithCommonParameters(IngridDocument doc) {
		TreeNodeBean treeNode = new TreeNodeBean();
		treeNode.setId((String) doc.get(MdekKeys.UUID));
		treeNode.setIsFolder((Boolean) doc.get(MdekKeys.HAS_CHILD));
		treeNode.setIsPublished((Boolean) doc.get(MdekKeys.IS_PUBLISHED));
		boolean markedDeleted = doc.get(MdekKeys.MARK_DELETED) == null ? false : ((String) doc.get(MdekKeys.MARK_DELETED)).equalsIgnoreCase("Y");
		treeNode.setIsMarkedDeleted(markedDeleted);
		List<IngridDocument> idcPermissions = (List<IngridDocument>) doc.get(MdekKeysSecurity.IDC_PERMISSIONS);
		treeNode.setUserWritePermission(hasWritePermission(idcPermissions));
		treeNode.setUserWriteSinglePermission(hasWriteSinglePermission(idcPermissions));
		treeNode.setUserWriteTreePermission(hasWriteTreePermission(idcPermissions));
		treeNode.setUserWriteSubTreePermission(hasWriteSubTreePermission(idcPermissions));

		return treeNode;
	}


	private static String buildAddressTitle(IngridDocument adr) {
		Integer adrClass = (Integer) adr.get(MdekKeys.CLASS);
		String title = "";
		switch (adrClass) {
		case 0:
			// Fall through
		case 1:
			title = (String) adr.get(MdekKeys.ORGANISATION);
			break;

		case 2:
			title = (String) adr.get(MdekKeys.NAME);
			String givenName = (String) adr.get(MdekKeys.GIVEN_NAME);
			if (givenName != null && givenName.trim().length() != 0)
				title += ", " + givenName;
			break;

		case 3:
			String name = (String) adr.get(MdekKeys.NAME);
			givenName = (String) adr.get(MdekKeys.GIVEN_NAME);
			String organisation = (String) adr.get(MdekKeys.ORGANISATION);
			if (name != null) {
				title += name.trim();
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

			title = title.trim();
			break;
		}

		return title;
	}


	public IngridDocument convertFromAddressRepresentation(MdekAddressBean data){
		IngridDocument udkAdr = new IngridDocument();

		// General Information
		udkAdr.put(MdekKeys.PARENT_UUID, data.getParentUuid());
		udkAdr.put(MdekKeys.UUID, data.getUuid());
		udkAdr.put(MdekKeys.CLASS, data.getAddressClass());
		udkAdr.put(MdekKeys.WORK_STATE, data.getWorkState());

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
		kvp = mapFromKeyValue(MdekKeys.COUNTRY_CODE, data.getCountryName());
		if (kvp.getValue() != null || kvp.getKey() != -1) {
			udkAdr.put(MdekKeys.COUNTRY_NAME, kvp.getValue());
			udkAdr.put(MdekKeys.COUNTRY_CODE, kvp.getKey());
		}
		

		// Common information
		udkAdr.put(MdekKeys.STREET, data.getStreet());
		udkAdr.put(MdekKeys.POSTAL_CODE, data.getPostalCode());
		udkAdr.put(MdekKeys.CITY, data.getCity());
		udkAdr.put(MdekKeys.POST_BOX, data.getPobox());
		udkAdr.put(MdekKeys.POST_BOX_POSTAL_CODE, data.getPoboxPostalCode());
		udkAdr.put(MdekKeys.ADDRESS_DESCRIPTION, data.getAddressDescription());
		udkAdr.put(MdekKeys.FUNCTION, data.getTask());
		udkAdr.put(MdekKeys.COMMUNICATION, mapFromCommunicationTable(data.getCommunication()));

		//Thesaurus
		udkAdr.put(MdekKeys.SUBJECT_TERMS, mapFromThesTermTable(data.getThesaurusTermsTable()));
		
		// Comments
		udkAdr.put(MdekKeys.COMMENT_LIST, mapFromCommentTable(data.getCommentTable()));

		cleanUpHashMap(udkAdr);
		return udkAdr;
	}
	
	public IngridDocument convertFromObjectRepresentation(MdekDataBean data){
		IngridDocument udkObj = new IngridDocument();

		// General
		udkObj.put(MdekKeys.ABSTRACT, data.getGeneralDescription());
		udkObj.put(MdekKeys.DATASET_ALTERNATE_NAME, data.getGeneralShortDescription());
		udkObj.put(MdekKeys.UUID, data.getUuid());
		udkObj.put(MdekKeys.PARENT_UUID, data.getParentUuid());
		udkObj.put(MdekKeys.TITLE, data.getObjectName());
		udkObj.put(MdekKeys.WORK_STATE, data.getWorkState());

		IngridDocument responsibleUser = new IngridDocument();
		responsibleUser.put(MdekKeys.UUID, data.getObjectOwner());
		udkObj.put(MdekKeys.RESPONSIBLE_USER, responsibleUser);

		// extrahieren des int Wertes für die Objekt-Klasse
		udkObj.put(MdekKeys.CLASS, data.getObjectClass());
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
		udkObj.put(MdekKeys.TIME_SCALE, data.getTimeRefIntervalNum());
		udkObj.put(MdekKeys.TIME_STEP, data.getTimeRefIntervalUnit());
		udkObj.put(MdekKeys.DESCRIPTION_OF_TEMPORAL_DOMAIN, data.getTimeRefExplanation());
		udkObj.put(MdekKeys.DATASET_REFERENCES, mapFromTimeRefTable(data.getTimeRefTable()));

		// ExtraInfo
		udkObj.put(MdekKeys.METADATA_LANGUAGE_CODE, data.getExtraInfoLangMetaDataCode());
		udkObj.put(MdekKeys.DATA_LANGUAGE_CODE, data.getExtraInfoLangDataCode());
		udkObj.put(MdekKeys.PUBLICATION_CONDITION, data.getExtraInfoPublishArea());
		udkObj.put(MdekKeys.CONFORMITY_LIST, mapFromExtraInfoConformityTable(data.getExtraInfoConformityTable()));
		udkObj.put(MdekKeys.DATASET_INTENTIONS, data.getExtraInfoPurpose());
		udkObj.put(MdekKeys.DATASET_USAGE, data.getExtraInfoUse());
		udkObj.put(MdekKeys.EXPORT_CRITERIA, mapFromExtraInfoXMLExportTable(data.getExtraInfoXMLExportTable()));
		udkObj.put(MdekKeys.LEGISLATIONS, mapFromExtraInfoLegalBasicsTable(data.getExtraInfoLegalBasicsTable()));


		// Availability
		if (data.getObjectClass() != null && data.getObjectClass() != 0) {
			udkObj.put(MdekKeys.ACCESS_LIST, mapFromAvailUsageLimitationTable(data.getAvailabilityUsageLimitationTable()));			
			udkObj.put(MdekKeys.DATA_FORMATS, mapFromAvailDataFormatTable(data.getAvailabilityDataFormatTable()));
			udkObj.put(MdekKeys.MEDIUM_OPTIONS, mapFromAvailMediaOptionsTable(data.getAvailabilityMediaOptionsTable()));
			udkObj.put(MdekKeys.ORDERING_INSTRUCTIONS, data.getAvailabilityOrderInfo());
		}

		//Thesaurus
		udkObj.put(MdekKeys.SUBJECT_TERMS_INSPIRE, mapFromInspireTermTable(data.getThesaurusInspireTermsList()));
		udkObj.put(MdekKeys.SUBJECT_TERMS, mapFromThesTermTable(data.getThesaurusTermsTable()));
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

		// Additional Fields
		udkObj.put(MdekKeys.ADDITIONAL_FIELDS, mapFromAdditionalFields(data.getAdditionalFields()));

		int objClass = data.getObjectClass() != null ? data.getObjectClass() : 0; 
		switch(objClass) {
		case 0:	// Object of type 0 doesn't have any special values
			break;
		case 1:
			IngridDocument td1Map = new IngridDocument();			
			td1Map.put(MdekKeys.DATASOURCE_UUID, data.getRef1ObjectIdentifier());
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
			td3Map.put(MdekKeys.SERVICE_TYPE_KEY, data.getRef3ServiceType());

			td3Map.put(MdekKeys.SERVICE_TYPE2_LIST, mapFromServiceTypeTable(data.getRef3ServiceTypeTable()));
			td3Map.put(MdekKeys.SYSTEM_ENVIRONMENT, data.getRef3SystemEnv());
			td3Map.put(MdekKeys.SYSTEM_HISTORY, data.getRef3History());
			td3Map.put(MdekKeys.DATABASE_OF_SYSTEM, data.getRef3BaseDataText());
			td3Map.put(MdekKeys.SERVICE_VERSION_LIST, data.getRef3ServiceVersion());
			td3Map.put(MdekKeys.DESCRIPTION_OF_TECH_DOMAIN, data.getRef3Explanation());
			td3Map.put(MdekKeys.PUBLICATION_SCALE_LIST, mapFromScaleTable(data.getRef3Scale()));
			td3Map.put(MdekKeys.SERVICE_OPERATION_LIST, mapFromOperationTable(data.getRef3Operation(), data.getRef3ServiceType()));
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

		cleanUpHashMap(udkObj);
		return udkObj;
	}

	
	public void setInitialValues(MdekAddressBean addr) {
		if (null == addr.getNameForm()) {
			addr.setNameForm(sysListMapper.getInitialValueFromListId(4300));
		}
		if (null == addr.getTitleOrFunction()) {
			addr.setTitleOrFunction(sysListMapper.getInitialValueFromListId(4305));
		}
		if (null == addr.getCountryCode()) {
			addr.setCountryCode(sysListMapper.getInitialKeyFromListId(6200));
		}
	}

	@SuppressWarnings("unchecked")
	public void setInitialValues(MdekDataBean obj) {
		// Set default values for a new object.
		// The default in obj will be set iff the corresponding values are null or empty lists.

		if (null == obj.getRef1SpatialSystem()) {
			obj.setRef1SpatialSystem(sysListMapper.getInitialValueFromListId(100));
		}
		if (null == obj.getTimeRefPeriodicity()) {
			obj.setTimeRefPeriodicity(sysListMapper.getInitialKeyFromListId(518));
		}
		if (null == obj.getTimeRefStatus()) {
			obj.setTimeRefStatus(sysListMapper.getInitialKeyFromListId(523));
		}
		if (null == obj.getRef1DataSet()) {
			obj.setRef1DataSet(sysListMapper.getInitialKeyFromListId(525));
		}
		if ((null == obj.getRef1Representation() || obj.getRef1Representation().size() == 0) && null != sysListMapper.getInitialKeyFromListId(526)) {
			obj.setRef1Representation(Arrays.asList(new Integer[] { sysListMapper.getInitialKeyFromListId(526) }));
		}
		if ((null == obj.getThesaurusTopicsList() || obj.getThesaurusTopicsList().size() == 0) && null != sysListMapper.getInitialKeyFromListId(527)) {
			obj.setThesaurusTopicsList(Arrays.asList(new Integer[] { sysListMapper.getInitialKeyFromListId(527) }));
		}
		if ((null == obj.getExtraInfoLegalBasicsTable() || obj.getExtraInfoLegalBasicsTable().size() == 0) && null != sysListMapper.getInitialKeyFromListId(1350)) {
			obj.setExtraInfoLegalBasicsTable(Arrays.asList(new String[] { sysListMapper.getInitialValueFromListId(1350) }));
		}
		if ((null == obj.getExtraInfoXMLExportTable() || obj.getExtraInfoXMLExportTable().size() == 0) && null != sysListMapper.getInitialKeyFromListId(1370)) {
			obj.setExtraInfoXMLExportTable(Arrays.asList(new String[] { sysListMapper.getInitialValueFromListId(1370) }));
		}
		if (null == obj.getRef2DocumentType()) {
			obj.setRef2DocumentType(sysListMapper.getInitialValueFromListId(3385));
		}
		if (null == obj.getRef3ServiceType()) {
			obj.setRef3ServiceType(sysListMapper.getInitialKeyFromListId(5100));
		}
		if ((null == obj.getRef3ServiceTypeTable() || obj.getRef3ServiceTypeTable().size() == 0) && null != sysListMapper.getInitialKeyFromListId(5200)) {
			obj.setRef3ServiceTypeTable(Arrays.asList(new Integer[] { sysListMapper.getInitialKeyFromListId(5200) }));
		}

		if (null != sysListMapper.getInitialKeyFromListId(99999999)) {
			if (null == obj.getExtraInfoLangDataCode()) {
				obj.setExtraInfoLangDataCode(sysListMapper.getInitialKeyFromListId(99999999));
			}
			if (null == obj.getExtraInfoLangMetaDataCode()) {
				obj.setExtraInfoLangMetaDataCode(sysListMapper.getInitialKeyFromListId(99999999));
			}
		}

		// The publication condition is usually derived from the parent object. If it is already
		// set, don't modify it. Otherwise try to set the initial value if it exists. If no intial
		// value is found, initialize it with publish type 'Internet'
		if (obj.getExtraInfoPublishArea() == null) {
			Integer pubCondKey = sysListMapper.getInitialKeyFromListId(3571);

			if (pubCondKey != null) {
				obj.setExtraInfoPublishArea(pubCondKey);

			} else {
				obj.setExtraInfoPublishArea(PublishType.INTERNET.getDbValue());
			}
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

	private List<IngridDocument> mapFromGeneralAddressTable(List<MdekAddressBean> adrTable) {
		List<IngridDocument> resultList = new ArrayList<IngridDocument>();

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

	private List<IngridDocument> mapFromCommunicationTable(List<CommunicationBean> commMap){
		List<IngridDocument> resultList = new ArrayList<IngridDocument>(); 

		for (CommunicationBean comm : commMap) {
			IngridDocument mappedEntry = new IngridDocument();
			mappedEntry.put(MdekKeys.COMMUNICATION_DESCRIPTION, comm.getDescription());
			KeyValuePair kvp = mapFromKeyValue(MdekKeys.COMMUNICATION_MEDIUM_KEY, comm.getMedium());
			if (kvp.getValue() != null || kvp.getKey() != -1) {
				mappedEntry.put(MdekKeys.COMMUNICATION_MEDIUM, kvp.getValue());
				mappedEntry.put(MdekKeys.COMMUNICATION_MEDIUM_KEY, kvp.getKey());
			}
			mappedEntry.put(MdekKeys.COMMUNICATION_VALUE, comm.getValue());
			resultList.add(mappedEntry);
		}

		return resultList;	
	}
	
	private List<IngridDocument> mapFromObjectLinksTable(List<MdekDataBean> objList) {
		List<IngridDocument> resultList = new ArrayList<IngridDocument>();
		if (objList == null)
			return resultList;

		for (MdekDataBean obj : objList) {
			IngridDocument mappedEntry = (IngridDocument) convertFromObjectRepresentation(obj);
			resultList.add(mappedEntry);
		}

		return resultList;
	}

	private List<IngridDocument> mapFromAdditionalFields(List<AdditionalFieldBean> additionalFieldList) {
		List<IngridDocument> resultList = new ArrayList<IngridDocument>();
		if (additionalFieldList == null)
			return resultList;

		for (AdditionalFieldBean additionalField : additionalFieldList) {
			IngridDocument additionalFieldDoc = new IngridDocument();
			additionalFieldDoc.putLong(MdekKeys.SYS_ADDITIONAL_FIELD_IDENTIFIER, additionalField.getIdentifier());
			additionalFieldDoc.put(MdekKeys.SYS_ADDITIONAL_FIELD_NAME, additionalField.getName());
			additionalFieldDoc.put(MdekKeys.ADDITIONAL_FIELD_VALUE, additionalField.getValue());
			resultList.add(additionalFieldDoc);
		}

		return resultList;
	}

	private List<IngridDocument> mapFromUrlLinksTable(List<UrlBean> urlList) {
		List<IngridDocument> resultList = new ArrayList<IngridDocument>();
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
	
	private List<IngridDocument> mapFromLocationTables(List<LocationBean> locationSNS, List<LocationBean> locationFree) {
		List<IngridDocument> resultList = new ArrayList<IngridDocument>();
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
				res.put(MdekKeys.SNS_TOPIC_TYPE, loc.getTopicTypeId());
				resultList.add(res);
			}
		}
		return resultList;
	}

	private List<IngridDocument> mapFromTimeRefTable(List<TimeReferenceBean> refList) {
		List<IngridDocument> resultList = new ArrayList<IngridDocument>();
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

	private List<IngridDocument> mapFromExtraInfoConformityTable(List<ConformityBean> conList) {
		List<IngridDocument> resultList = new ArrayList<IngridDocument>();
		if (conList == null)
			return resultList;

		for (ConformityBean con : conList) {
			IngridDocument result = new IngridDocument();
			result.put(MdekKeys.CONFORMITY_DEGREE_KEY, con.getLevel());
			result.put(MdekKeys.CONFORMITY_SPECIFICATION, con.getSpecification());
			result.put(MdekKeys.CONFORMITY_PUBLICATION_DATE, convertDateToTimestamp(con.getDate()));
			resultList.add(result);
		}
		return resultList;
	}

	private List<IngridDocument> mapFromExtraInfoXMLExportTable(List<String> refList) {
		List<IngridDocument> resultList = new ArrayList<IngridDocument>();
		if (refList == null)
			return resultList;

		for (String ref : refList) {
			IngridDocument result = new IngridDocument();
			KeyValuePair kvp = mapFromKeyValue(MdekKeys.EXPORT_CRITERION_KEY, ref);
			result.put(MdekKeys.EXPORT_CRITERION_KEY, kvp.getKey());
			result.put(MdekKeys.EXPORT_CRITERION_VALUE, kvp.getValue());
			resultList.add(result);
		}
		return resultList;
	}
	
	private List<IngridDocument> mapFromExtraInfoLegalBasicsTable(List<String> refList) {
		List<IngridDocument> resultList = new ArrayList<IngridDocument>();
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

	private List<IngridDocument> mapFromAvailUsageLimitationTable(List<UsageLimitationBean> ulList) {
		List<IngridDocument> resultList = new ArrayList<IngridDocument>();
		if (ulList == null)
			return resultList;

		for (UsageLimitationBean ul : ulList) {
			IngridDocument result = new IngridDocument();
			result.put(MdekKeys.ACCESS_RESTRICTION_KEY, ul.getLimit());
			result.put(MdekKeys.ACCESS_TERMS_OF_USE, ul.getRequirement());
			resultList.add(result);
		}
		return resultList;
	}

	
	private List<IngridDocument> mapFromAvailDataFormatTable(List<DataFormatBean> refList) {
		List<IngridDocument> resultList = new ArrayList<IngridDocument>();
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


	private List<IngridDocument> mapFromAvailMediaOptionsTable(List<MediaOptionBean> refList) {
		List<IngridDocument> resultList = new ArrayList<IngridDocument>();
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

	private List<IngridDocument> mapFromInspireTermTable(List<Integer> inspireTermList) {
		List<IngridDocument> resultList = new ArrayList<IngridDocument>();
		if (inspireTermList != null) {
			for (Integer identifier : inspireTermList) {
				IngridDocument res = new IngridDocument();
				res.put(MdekKeys.TERM_TYPE, SearchtermType.INSPIRE.getDbValue());
				res.put(MdekKeys.TERM_ENTRY_ID, identifier);

				resultList.add(res);
			}
		}
		return resultList;
	}

	public static List<IngridDocument> mapFromThesTermTable(List<SNSTopic> snsList) {
		List<IngridDocument> resultList = new ArrayList<IngridDocument>();
		if (snsList != null) {
			for (SNSTopic t : snsList) {
				IngridDocument res = null;
				if (t != null) {
					res = new IngridDocument();
					switch(t.getSource()) {
						case GEMET:
							res.put(MdekKeys.TERM_TYPE, SearchtermType.GEMET.getDbValue());
							res.put(MdekKeys.TERM_NAME, t.getTitle());
							res.put(MdekKeys.TERM_ALTERNATE_NAME, t.getAlternateTitle());
							res.put(MdekKeys.TERM_SNS_ID, t.getTopicId());
							res.put(MdekKeys.TERM_GEMET_ID, t.getGemetId());
							break;
	
						case UMTHES:
							res.put(MdekKeys.TERM_TYPE, SearchtermType.UMTHES.getDbValue());
							res.put(MdekKeys.TERM_NAME, t.getTitle());
							res.put(MdekKeys.TERM_SNS_ID, t.getTopicId());
							break;
	
						case FREE:
							res.put(MdekKeys.TERM_TYPE, SearchtermType.FREI.getDbValue());
							res.put(MdekKeys.TERM_NAME, t.getTitle());
							break;
						default:
							break;
					}
				}
				resultList.add(res);
			}
		}
		return resultList;
	}


	private List<IngridDocument> mapFromVFormatDetailsTable(List<VectorFormatDetailsBean> vFormatList) {
		List<IngridDocument> resultList = new ArrayList<IngridDocument>();
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

	private List<IngridDocument> mapFromServiceTypeTable(List<Integer> serviceTypeList) {
		List<IngridDocument> resultList = new ArrayList<IngridDocument>();
		if (serviceTypeList == null) {
			return resultList;
		}
		
		for (Integer i : serviceTypeList) {
			IngridDocument result = new IngridDocument();
			result.put(MdekKeys.SERVICE_TYPE2_KEY, i);
			resultList.add(result);
		}
		return resultList;
	}
	
	private List<IngridDocument> mapFromScaleTable(List<ScaleBean> scaleList) {
		List<IngridDocument> resultList = new ArrayList<IngridDocument>();
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


	private List<IngridDocument> mapFromSymLinkDataTable(List<LinkDataBean> linkList) {
		List<IngridDocument> resultList = new ArrayList<IngridDocument>();
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

	private List<IngridDocument> mapFromKeyLinkDataTable(List<LinkDataBean> linkList) {
		List<IngridDocument> resultList = new ArrayList<IngridDocument>();
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

	private List<IngridDocument> mapFromDbContentTable(List<DBContentBean> dbList) {
		List<IngridDocument> resultList = new ArrayList<IngridDocument>();
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


	private List<IngridDocument> mapFromOperationTable(List<OperationBean> opList, Integer serviceType) {
		List<IngridDocument> resultList = new ArrayList<IngridDocument>();
		if (opList == null)
			return resultList;

		for (OperationBean op : opList) {
			IngridDocument result = new IngridDocument();
			if (serviceType == 5 || serviceType == 6) {
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

	private List<IngridDocument> mapFromOperationParamTable(List<OperationParameterBean> opList) {
		List<IngridDocument> resultList = new ArrayList<IngridDocument>();
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


	private List<IngridDocument> mapFromCommentTable(List<CommentBean> commentList) {
		List<IngridDocument> resultList = new ArrayList<IngridDocument>();
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

	private KeyValuePair mapToKeyValuePair(IngridDocument obj, String key, String value) {
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
	
	private List<MdekAddressBean> mapToGeneralAddressTable(List<IngridDocument> adrTable) {
		List<MdekAddressBean> resultList = new ArrayList<MdekAddressBean>(); 
		if (adrTable == null)
			return resultList;

		for (IngridDocument adr : adrTable) {
			resultList.add(getDetailedAddressRepresentation(adr));
		}
		return resultList;
	}


	private List<CommunicationBean> mapToCommunicationTable(List<IngridDocument> commMap){
		List<CommunicationBean> resultCommMap = new ArrayList<CommunicationBean>(); 
		if (commMap == null)
			return resultCommMap;

		for (IngridDocument comm : commMap) {
			CommunicationBean resultComm = new CommunicationBean();
			resultComm.setDescription((String) comm.get(MdekKeys.COMMUNICATION_DESCRIPTION));
			KeyValuePair kvp = mapToKeyValuePair(comm, MdekKeys.COMMUNICATION_MEDIUM_KEY, MdekKeys.COMMUNICATION_MEDIUM);
			resultComm.setMedium(kvp.getValue());
			resultComm.setValue((String) comm.get(MdekKeys.COMMUNICATION_VALUE));
			resultCommMap.add(resultComm);
		}

		return resultCommMap;	
	}
	
	private List<MdekDataBean> mapToObjectLinksTable(List<IngridDocument> objList) {
		List<MdekDataBean> resultList = new ArrayList<MdekDataBean>(); 
		if (objList == null)
			return resultList;

		for (IngridDocument obj : objList) {
			resultList.add(getDetailedObjectRepresentation(obj));
		}
		return resultList;
	}

	private List<UrlBean> mapToUrlLinksTable(List<IngridDocument> objList) {
		List<UrlBean> resultList = new ArrayList<UrlBean>(); 
		if (objList == null)
			return resultList;
		
		for (IngridDocument obj : objList) {
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


	private List<AdditionalFieldBean> mapToAdditionalFields(List<IngridDocument> additionalFieldList) {
		List<AdditionalFieldBean> resultList = new ArrayList<AdditionalFieldBean>();
		if (additionalFieldList == null) {
			return resultList;
		}

		for (IngridDocument additionalFieldDoc : additionalFieldList) {
			AdditionalFieldBean additionalField = new AdditionalFieldBean();
			additionalField.setIdentifier(additionalFieldDoc.getLong(MdekKeys.SYS_ADDITIONAL_FIELD_IDENTIFIER));
			additionalField.setName(additionalFieldDoc.getString(MdekKeys.SYS_ADDITIONAL_FIELD_NAME));
			additionalField.setValue(additionalFieldDoc.getString(MdekKeys.ADDITIONAL_FIELD_VALUE));
			resultList.add(additionalField);
		}
		return resultList;
	}


	private List<LocationBean> mapToSpatialRefAdminUnitTable(List<IngridDocument> locList) {
		List<LocationBean> resultList = new ArrayList<LocationBean>();
		if (locList == null)
			return resultList;

		for (IngridDocument location : locList) {

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
				loc.setTopicTypeId((String) location.get(MdekKeys.SNS_TOPIC_TYPE));
				loc.setTopicType(mapFromSNSTopicTypeId((String) location.get(MdekKeys.SNS_TOPIC_TYPE)));
				loc.setLocationExpiredAt(convertTimestampToDate((String) location.get(MdekKeys.LOCATION_EXPIRED_AT)));
				resultList.add(loc);
			}
		}
		return resultList;
	}

	private String mapFromSNSTopicTypeId(String topicTypeId) {
		// topicTypeId is one of the SNS topic Types (use2Type, use6Type, ...)
		// The sns resource bundle is used to resolve the different types
    	// If the type can not be resolved, null is returned
		try {
    		return snsResourceBundle.getString("sns.topic.ref."+topicTypeId);

    	} catch (Exception e) {
    		return null;
    	}
	}

	private List<LocationBean> mapToSpatialRefLocationTable(List<IngridDocument> locList) {
		List<LocationBean> resultList = new ArrayList<LocationBean>();
		if (locList == null)
			return resultList;

		for (IngridDocument location : locList) {
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


	private List<TimeReferenceBean> mapToTimeRefTable(List<IngridDocument> refList) {
		List<TimeReferenceBean> resultList = new ArrayList<TimeReferenceBean>();
		if (refList == null)
			return resultList;

		for (IngridDocument ref : refList) {
			TimeReferenceBean tr = new TimeReferenceBean();
			tr.setDate(convertTimestampToDate((String) ref.get(MdekKeys.DATASET_REFERENCE_DATE)));
			tr.setType((Integer) ref.get(MdekKeys.DATASET_REFERENCE_TYPE));
			resultList.add(tr);
		}
		return resultList;
	}

	
	private List<ConformityBean> mapToExtraInfoConformityTable(List<IngridDocument> conList) {
		List<ConformityBean> resultList = new ArrayList<ConformityBean>();
		if (conList == null)
			return resultList;

		for (IngridDocument con : conList) {
			ConformityBean c = new ConformityBean();
			c.setLevel((Integer) con.get(MdekKeys.CONFORMITY_DEGREE_KEY));
			c.setSpecification((String) con.get(MdekKeys.CONFORMITY_SPECIFICATION));
			c.setDate(convertTimestampToDate((String) con.get(MdekKeys.CONFORMITY_PUBLICATION_DATE)));
			resultList.add(c);
		}
		return resultList;
	}

	private List<String> mapToExtraInfoXMLExportTable(List<IngridDocument> refList) {
		List<String> resultList = new ArrayList<String>();
		if (refList == null)
			return resultList;
		
		for (IngridDocument ref : refList) {
			KeyValuePair kvp = mapToKeyValuePair(ref, MdekKeys.EXPORT_CRITERION_KEY, MdekKeys.EXPORT_CRITERION_VALUE);
			resultList.add(kvp.getValue());
		}
		return resultList;
	}

	private List<String> mapToExtraInfoLegalBasicsTable(List<IngridDocument> refList) {
		List<String> resultList = new ArrayList<String>();
		if (refList == null)
			return resultList;
		for (IngridDocument ref : refList) {
			KeyValuePair kvp = mapToKeyValuePair(ref, MdekKeys.LEGISLATION_KEY, MdekKeys.LEGISLATION_VALUE);
			resultList.add(kvp.getValue());
		}
		return resultList;
	}

	private List<UsageLimitationBean> mapToAvailUsageLimitationTable(List<IngridDocument> ulList) {
		List<UsageLimitationBean> resultList = new ArrayList<UsageLimitationBean>();
		if (ulList == null)
			return resultList;

		for (IngridDocument ul : ulList) {
			UsageLimitationBean u = new UsageLimitationBean();
			u.setLimit((Integer) ul.get(MdekKeys.ACCESS_RESTRICTION_KEY));
			u.setRequirement((String) ul.get(MdekKeys.ACCESS_TERMS_OF_USE));
			resultList.add(u);
		}
		return resultList;
	}


	private List<DataFormatBean> mapToAvailDataFormatTable(List<IngridDocument> refList) {
		List<DataFormatBean> resultList = new ArrayList<DataFormatBean>();
		if (refList == null)
			return resultList;
		for (IngridDocument ref : refList) {
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

	private List<MediaOptionBean> mapToAvailMediaOptionsTable(List<IngridDocument> refList) {
		List<MediaOptionBean> resultList = new ArrayList<MediaOptionBean>();
		if (refList == null)
			return resultList;
		for (IngridDocument ref : refList) {
			MediaOptionBean mo = new MediaOptionBean();
			mo.setName((Integer) ref.get(MdekKeys.MEDIUM_NAME));
			mo.setLocation((String) ref.get(MdekKeys.MEDIUM_NOTE));
			mo.setTransferSize((Double) ref.get(MdekKeys.MEDIUM_TRANSFER_SIZE));
			resultList.add(mo);
		}
		return resultList;
	}


	private List<Integer> mapToInspireTermTable(List<IngridDocument> topicList) {
		List<Integer> resultList = new ArrayList<Integer>();

		if (topicList != null) {
			for (IngridDocument topic : topicList) {
				resultList.add((Integer) topic.get(MdekKeys.TERM_ENTRY_ID));
			}
		}

		return resultList;
	}


	public static List<SNSTopic> mapToThesTermsTable(List<IngridDocument> topicList) {
		List<SNSTopic> resultList = new ArrayList<SNSTopic>();
		if (topicList == null)
			return resultList;
		for (IngridDocument topic : topicList) {
			SNSTopic t = new SNSTopic();
			t.setType(Type.DESCRIPTOR);
			String type = (String) topic.get(MdekKeys.TERM_TYPE);
			if (type.equalsIgnoreCase(SearchtermType.GEMET.getDbValue())) {
				t.setSource(Source.GEMET);
				t.setTitle((String) topic.get(MdekKeys.TERM_NAME));
				t.setAlternateTitle((String) topic.get(MdekKeys.TERM_ALTERNATE_NAME));
				t.setTopicId((String) topic.get(MdekKeys.TERM_SNS_ID));
				t.setGemetId((String) topic.get(MdekKeys.TERM_GEMET_ID));

			} else if (type.equalsIgnoreCase(SearchtermType.UMTHES.getDbValue())) {
				t.setSource(Source.UMTHES);
				t.setTitle((String) topic.get(MdekKeys.TERM_NAME));
				t.setTopicId((String) topic.get(MdekKeys.TERM_SNS_ID));

			} else if (type.equalsIgnoreCase(SearchtermType.FREI.getDbValue())) {
				t.setSource(Source.FREE);
				t.setTitle((String) topic.get(MdekKeys.TERM_NAME));
			}
			resultList.add(t);
		}
		return resultList;
	}

	private List<VectorFormatDetailsBean> mapToVFormatDetailsTable(List<IngridDocument> vFormatList) {
		List<VectorFormatDetailsBean> resultList = new ArrayList<VectorFormatDetailsBean>();
		if (vFormatList == null)
			return resultList;
		for (IngridDocument vFormat : vFormatList) {
			VectorFormatDetailsBean v = new VectorFormatDetailsBean();
			v.setGeometryType((Integer) vFormat.get(MdekKeys.GEOMETRIC_OBJECT_TYPE));
			v.setNumElements((Integer) vFormat.get(MdekKeys.GEOMETRIC_OBJECT_COUNT));
			resultList.add(v);
		}
		return resultList;
	}


	private List<Integer> mapToServiceTypeTable(List<IngridDocument> serviceTypeList) {
		List<Integer> resultList = new ArrayList<Integer>();
		if (serviceTypeList == null) {
			return resultList;
		}
		for (IngridDocument serviceType : serviceTypeList) {
			resultList.add((Integer) serviceType.get(MdekKeys.SERVICE_TYPE2_KEY));
		}
		return resultList;
	}
	
	private List<ScaleBean> mapToScaleTable(List<IngridDocument> scaleList) {
		List<ScaleBean> resultList = new ArrayList<ScaleBean>();
		if (scaleList == null)
			return resultList;
		for (IngridDocument topic : scaleList) {
			ScaleBean s = new ScaleBean();
			s.setGroundResolution((Double) topic.get(MdekKeys.RESOLUTION_GROUND));
			s.setScale((Integer) topic.get(MdekKeys.SCALE));
			s.setScanResolution((Double) topic.get(MdekKeys.RESOLUTION_SCAN));
			resultList.add(s);
		}
		return resultList;
	}


	private List<LinkDataBean> mapToSymLinkDataTable(List<IngridDocument> linkList) {
		List<LinkDataBean> resultList = new ArrayList<LinkDataBean>();
		if (linkList == null)
			return resultList;
		for (IngridDocument topic : linkList) {
			LinkDataBean l = new LinkDataBean();
			l.setDate(convertTimestampToDate((String) topic.get(MdekKeys.SYMBOL_DATE)));
			KeyValuePair kvp = mapToKeyValuePair(topic, MdekKeys.SYMBOL_CAT_KEY, MdekKeys.SYMBOL_CAT);
			l.setTitle((String) kvp.getValue());
			l.setVersion((String) topic.get(MdekKeys.SYMBOL_EDITION));
			resultList.add(l);
		}
		return resultList;
	}

	private  List<LinkDataBean> mapToKeyLinkDataTable(List<IngridDocument> linkList) {
		List<LinkDataBean> resultList = new ArrayList<LinkDataBean>();
		if (linkList == null)
			return resultList;
		for (IngridDocument topic : linkList) {
			LinkDataBean l = new LinkDataBean();
			l.setDate(convertTimestampToDate((String) topic.get(MdekKeys.KEY_DATE)));
			KeyValuePair kvp = mapToKeyValuePair(topic, MdekKeys.SUBJECT_CAT_KEY, MdekKeys.SUBJECT_CAT);
			l.setTitle((String) kvp.getValue());
			l.setVersion((String) topic.get(MdekKeys.EDITION));
			resultList.add(l);
		}
		return resultList;
	}

	private List<DBContentBean> mapToDbContentTable(List<IngridDocument> dbList) {
		List<DBContentBean> resultList = new ArrayList<DBContentBean>();
		if (dbList == null)
			return resultList;
		for (IngridDocument content : dbList) {
			DBContentBean db = new DBContentBean();
			db.setParameter((String) content.get(MdekKeys.PARAMETER));
			db.setAdditionalData((String) content.get(MdekKeys.SUPPLEMENTARY_INFORMATION));
			resultList.add(db);
		}
		return resultList;
	}


	private List<OperationBean> mapToOperationTable(List<IngridDocument> opList, Integer serviceType) {
		List<OperationBean> resultList = new ArrayList<OperationBean>();
		if (opList == null)
			return resultList;
		for (IngridDocument operation : opList) {
			OperationBean op = new OperationBean();
			if (serviceType == null || serviceType == 5 || serviceType == 6) {
				op.setName((String) operation.get(MdekKeys.SERVICE_OPERATION_NAME));
			} else {
				String val = sysListMapper.getValue(MdekKeys.SERVICE_OPERATION_NAME_KEY+"."+serviceType, (Integer) operation.get(MdekKeys.SERVICE_OPERATION_NAME_KEY));
				op.setName(val);				
			}
			op.setDescription((String) operation.get(MdekKeys.SERVICE_OPERATION_DESCRIPTION));
			op.setPlatform((List<String>) operation.get(MdekKeys.PLATFORM_LIST));
			op.setMethodCall((String) operation.get(MdekKeys.INVOCATION_NAME));
			op.setParamList(mapToOperationParamTable((List<IngridDocument>) operation.get(MdekKeys.PARAMETER_LIST)));
			op.setAddressList((List<String>) operation.get(MdekKeys.CONNECT_POINT_LIST));
			op.setDependencies((List<String>) operation.get(MdekKeys.DEPENDS_ON_LIST));
			resultList.add(op);
		}
		return resultList;
	}


	private List<OperationParameterBean> mapToOperationParamTable(List<IngridDocument> opList) {
		List<OperationParameterBean> resultList = new ArrayList<OperationParameterBean>();
		if (opList == null)
			return resultList;
		for (IngridDocument operation : opList) {
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

	

	private List<CommentBean> mapToCommentTable(List<IngridDocument> commentList) {
		List<CommentBean> resultList = new ArrayList<CommentBean>();
		if (commentList == null)
			return resultList;
		for (IngridDocument comment : commentList) {
			CommentBean c = new CommentBean();
			c.setComment((String) comment.get(MdekKeys.COMMENT));
			IngridDocument createUserDoc = (IngridDocument) comment.get(MdekKeys.CREATE_USER);
			c.setUser(getDetailedAddressRepresentation(createUserDoc));
			c.setDate(convertTimestampToDate((String) comment.get(MdekKeys.CREATE_TIME)));
			resultList.add(c);
		}
		return resultList;
	}


	/********************************
	 * Miscellaneous Helper Methods *
	 ********************************/
	private boolean hasWritePermission(List<IngridDocument> permissionList) {
		return MdekUtilsSecurity.hasWritePermission(permissionList);
	}

	private boolean hasWriteSinglePermission(List<IngridDocument> permissionList) {
		return MdekUtilsSecurity.hasWriteSinglePermission(permissionList);
	}
	private boolean hasWriteTreePermission(List<IngridDocument> permissionList) {
		return MdekUtilsSecurity.hasWriteTreePermission(permissionList);
	}
	private boolean hasWriteSubTreePermission(List<IngridDocument> permissionList) {
		if (permissionList != null) {
			for (IngridDocument permissionDoc : permissionList) {
				IdcPermission idcPerm = EnumUtil.mapDatabaseToEnumConst(IdcPermission.class, permissionDoc.get(MdekKeysSecurity.IDC_PERMISSION));
				if (idcPerm == IdcPermission.DUMMY_WRITE_SUBTREE) {
					return true;
				}
			}
		}

		return false;
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

	public SysListCache getSysListMapper() {
		return sysListMapper;
	}
	public void setSysListMapper(SysListCache sysListMapper) {
		this.sysListMapper = sysListMapper;
	}	


	private void cleanUpHashMap(IngridDocument doc) {
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
/*
	private static String convertLanguageIdentifierToCode(String identifier) {
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

	private static String convertLanguageCodeToIdentifier(String code) {
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
*/
}