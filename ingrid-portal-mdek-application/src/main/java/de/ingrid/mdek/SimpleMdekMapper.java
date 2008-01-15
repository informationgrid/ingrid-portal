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
import de.ingrid.mdek.dwr.Location;
import de.ingrid.mdek.dwr.MdekAddressBean;
import de.ingrid.mdek.dwr.MdekDataBean;
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
		mdekObj.setObjectClass((Integer) obj.get(MdekKeys.CLASS));

		WorkState workState = EnumUtil.mapDatabaseToEnumConst(WorkState.class, (String) obj.get(MdekKeys.WORK_STATE));
		mdekObj.setWorkState(StringEscapeUtils.escapeHtml(workState.toString()));
		// DocType defines the icon which is displayed in the tree view. Move this to EntryService?

		mdekObj.setHasChildren((Boolean) obj.get(MdekKeys.HAS_CHILD));
		mdekObj.setObjectName((String) obj.get(MdekKeys.TITLE));
		mdekObj.setGeneralAddressTable(mapToGeneralAddressTable((List<HashMap<String, Object>>) obj.get(MdekKeys.ADR_REFERENCES_TO)));
		mdekObj.setCreationTime(convertTimestampToDisplayDate((String) obj.get(MdekKeys.DATE_OF_CREATION)));
		mdekObj.setModificationTime(convertTimestampToDisplayDate((String) obj.get(MdekKeys.DATE_OF_LAST_MODIFICATION)));

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
//		mdekObj.setTimeRefTable((ArrayList<HashMap<String, String>>) mapToTimeRefTable((List<HashMap<String, Object>>) obj.get(MdekKeys.MISSING)));
		mdekObj.setTimeRefExplanation((String) obj.get(MdekKeys.DESCRIPTION_OF_TEMPORAL_DOMAIN));
		
		// ExtraInfo
		mdekObj.setExtraInfoLangMetaData((String) obj.get(MdekKeys.METADATA_LANGUAGE));
		mdekObj.setExtraInfoLangData((String) obj.get(MdekKeys.DATA_LANGUAGE));
		mdekObj.setExtraInfoPublishArea((Integer) obj.get(MdekKeys.PUBLICATION_CONDITION));
		mdekObj.setExtraInfoPurpose((String) obj.get(MdekKeys.DATASET_INTENSIONS));
		mdekObj.setExtraInfoUse((String) obj.get(MdekKeys.DATASET_USAGE));
//		mdekObj.setExtraInfoXMLExportTable((ArrayList<String>) mapToExtraInfoXMLExportTable((List<String>) obj.get(MdekKeys.MISSING)));
//		mdekObj.setExtraInfoLegalBasicsTable((ArrayList<String>) mapToExtraInfoLegalBasicsTable((List<String>) obj.get(MdekKeys.MISSING)));

		// Availability
//		mdekObj.setAvailabilityDataFormatTable((ArrayList<HashMap<String, String>>) mapToAvailabilityDataFormatTable((List<HashMap<String, Object>>) obj.get(MdekKeys.MISSING)));
//		mdekObj.setAvailabilityMediaOptionsTable((ArrayList<HashMap<String, String>>) mapToAvailabilityMediaOptionsTable((List<HashMap<String, Object>>) obj.get(MdekKeys.MISSING)));
		mdekObj.setAvailabilityOrderInfo((String) obj.get(MdekKeys.ORDERING_INSTRUCTIONS));
		mdekObj.setAvailabilityNoteUse((String) obj.get(MdekKeys.USE_CONSTRAINTS));
		mdekObj.setAvailabilityCosts((String) obj.get(MdekKeys.FEES));
		
		// Thesaurus
//		mdekObj.setThesaurusTermsTable((ArrayList<HashMap<String, String>>) mapToThesaurusTermsTable((List<HashMap<String, Object>>) obj.get(MdekKeys.MISSING)));
//		mdekObj.setThesaurusTopicsList((ArrayList<String>) mapToThesaurusTopicsList((List<String>) obj.get(MdekKeys.MISSING)));
//		mdekObj.setThesaurusFreeTermsList((ArrayList<String>) mapToThesaurusFreeTermsList((List<String>) obj.get(MdekKeys.MISSING)));
//		mdekObj.setThesaurusEnvExtRes((Boolean) obj.get(MdekKeys.MISSING));
//		mdekObj.setThesaurusEnvTopicsList((ArrayList<String>) mapToThesaurusEnvTopicsList((List<String>) obj.get(MdekKeys.MISSING)));
//		mdekObj.setThesaurusEnvCatsList((ArrayList<String>) mapToThesaurusEnvCatsList((List<String>) obj.get(MdekKeys.MISSING)));
//
		// Links
//		mdekObj.setLinksToTable((ArrayList<HashMap<String, String>>) mapToLinksToTable((List<HashMap<String, Object>>) obj.get(MdekKeys.MISSING)));
//		mdekObj.setLinksFromTable((ArrayList<HashMap<String, String>>) mapToLinksFromTable((List<HashMap<String, Object>>) obj.get(MdekKeys.MISSING)));
		mdekObj.setLinksToObjectTable(mapToObjectLinksTable((List<HashMap<String, Object>>) obj.get(MdekKeys.OBJ_REFERENCES_TO)));
		mdekObj.setLinksFromObjectTable(mapToObjectLinksTable((List<HashMap<String, Object>>) obj.get(MdekKeys.OBJ_REFERENCES_FROM)));
		mdekObj.setRelationTypeName((String) obj.get(MdekKeys.RELATION_TYPE_NAME));
		mdekObj.setRelationDescription((String) obj.get(MdekKeys.RELATION_DESCRIPTION));

		
		// TODO Should we move the gui specific settings to another object / to the entry service?
		mdekObj.setNodeAppType("O");
		String nodeDocType = "Class" + ((Integer) obj.get(MdekKeys.CLASS));
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
//		mdekObj.setTimeRefTable((ArrayList<HashMap<String, String>>) mapToTimeRefTable((List<HashMap<String, Object>>) obj.get(MdekKeys.MISSING)));

		// ExtraInfo
		udkObj.put(MdekKeys.METADATA_LANGUAGE, data.getExtraInfoLangMetaData());
		udkObj.put(MdekKeys.DATA_LANGUAGE, data.getExtraInfoLangData());
		udkObj.put(MdekKeys.PUBLICATION_CONDITION, data.getExtraInfoPublishArea());
		udkObj.put(MdekKeys.DATASET_INTENSIONS, data.getExtraInfoPurpose());
		udkObj.put(MdekKeys.DATASET_USAGE, data.getExtraInfoUse());
//		mdekObj.setExtraInfoXMLExportTable((ArrayList<String>) mapToExtraInfoXMLExportTable((List<String>) obj.get(MdekKeys.MISSING)));
//		mdekObj.setExtraInfoLegalBasicsTable((ArrayList<String>) mapToExtraInfoLegalBasicsTable((List<String>) obj.get(MdekKeys.MISSING)));

		// Availability
//		mdekObj.setAvailabilityDataFormatTable((ArrayList<HashMap<String, String>>) mapToAvailabilityDataFormatTable((List<HashMap<String, Object>>) obj.get(MdekKeys.MISSING)));
//		mdekObj.setAvailabilityMediaOptionsTable((ArrayList<HashMap<String, String>>) mapToAvailabilityMediaOptionsTable((List<HashMap<String, Object>>) obj.get(MdekKeys.MISSING)));
		udkObj.put(MdekKeys.ORDERING_INSTRUCTIONS, data.getAvailabilityOrderInfo());
		udkObj.put(MdekKeys.USE_CONSTRAINTS, data.getAvailabilityNoteUse());
		udkObj.put(MdekKeys.FEES, data.getAvailabilityCosts());

		// Links
		udkObj.put(MdekKeys.OBJ_REFERENCES_TO, mapFromObjectLinksTable(data.getLinksToObjectTable()));
		udkObj.put(MdekKeys.OBJ_REFERENCES_FROM, mapFromObjectLinksTable(data.getLinksFromObjectTable()));
		udkObj.put(MdekKeys.RELATION_TYPE_NAME, data.getRelationTypeName());
		udkObj.put(MdekKeys.RELATION_DESCRIPTION, data.getRelationDescription());

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

	private static ArrayList<IngridDocument> mapFromLocationTables(ArrayList<Location> locationSNS, ArrayList<Location> locationFree) {
		ArrayList<IngridDocument> resultList = new ArrayList<IngridDocument>();
		if (locationFree != null) {
			for (Location loc : locationFree) {
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
			for (Location loc : locationSNS) {
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
	
	private static ArrayList<Location> mapToSpatialRefAdminUnitTable(List<HashMap<String, Object>> locList) {
		ArrayList<Location> resultList = new ArrayList<Location>();
		for (HashMap<String, Object> location : locList) {
			String locationType = (String) location.get(MdekKeys.LOCATION_TYPE);
			if (locationType.equals("G")) {
				Location loc = new Location(); 
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
	
	private static ArrayList<Location> mapToSpatialRefLocationTable(List<HashMap<String, Object>> locList) {
		ArrayList<Location> resultList = new ArrayList<Location>();
		for (HashMap<String, Object> location : locList) {
			String locationType = (String) location.get(MdekKeys.LOCATION_TYPE);
			if (locationType.equals("F")) {
				Location loc = new Location(); 
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