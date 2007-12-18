package de.ingrid.mdek;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

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


	private MdekDataBean getDetailedMdekRepresentation(
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
//		mdekObj.setGeneralShortDescription((String) obj.get(MdekKeys.MISSING));
		mdekObj.setGeneralDescription((String) obj.get(MdekKeys.ABSTRACT));
		mdekObj.setId((String) obj.get(MdekKeys.UUID));
		mdekObj.setTitle((String) obj.get(MdekKeys.TITLE));
		mdekObj.setObjectClass((Integer) obj.get(MdekKeys.CLASS));
		mdekObj.setNodeDocType("Class"+((Integer) obj.get(MdekKeys.CLASS) + 1));
		mdekObj.setHasChildren((Boolean) obj.get(MdekKeys.HAS_CHILD));
		mdekObj.setObjectName((String) obj.get(MdekKeys.TITLE));
		mdekObj.setGeneralAddressTable(mapToGeneralAddressTable((List<HashMap<String, Object>>) obj.get(MdekKeys.ADR_ENTITIES)));

		// Spatial
//		mdekObj.setSpatialRefAdminUnitTable((ArrayList<HashMap<String, String>>) mapToSpatialRefAdminUnitTable((List<HashMap<String, Object>>) obj.get(MdekKeys.MISSING)));
//		mdekObj.setSpatialRefCoordsAdminUnitTable((ArrayList<HashMap<String, String>>) mapToSpatialRefCoordsAdminUnitTable((List<HashMap<String, Object>>) obj.get(MdekKeys.MISSING)));
//		mdekObj.setSpatialRefLocationTable((ArrayList<HashMap<String, String>>) mapToSpatialRefLocationTable((List<HashMap<String, Object>>) obj.get(MdekKeys.MISSING)));
//		mdekObj.setSpatialRefCoordsLocationTable((ArrayList<HashMap<String, String>>) mapToSpatialRefCoordsLocationTable((List<HashMap<String, Object>>) obj.get(MdekKeys.MISSING)));
//		mdekObj.setSpatialRefAltMin((Float) obj.get(MdekKeys.MISSING));
//		mdekObj.setSpatialRefAltMax((Float) obj.get(MdekKeys.MISSING));
//		mdekObj.setSpatialRefAltMeasure((String) obj.get(MdekKeys.MISSING));
//		mdekObj.setSpatialRefAltVDate((String) obj.get(MdekKeys.MISSING));
//		mdekObj.setSpatialRefExplanation((String) obj.get(MdekKeys.MISSING));

		// Time
//		mdekObj.setTimeRefType((String) obj.get(MdekKeys.MISSING));
//		mdekObj.setTimeRefDate1((Date) obj.get(MdekKeys.MISSING));
//		mdekObj.setTimeRefDate2((Date) obj.get(MdekKeys.MISSING));
//		mdekObj.setTimeRefStatus((String) obj.get(MdekKeys.MISSING));
//		mdekObj.setTimeRefPeriodicity((String) obj.get(MdekKeys.MISSING));
//		mdekObj.setTimeRefIntervalNum((String) obj.get(MdekKeys.MISSING));
//		mdekObj.setTimeRefIntervalUnit((String) obj.get(MdekKeys.MISSING));
//		mdekObj.setTimeRefTable((ArrayList<HashMap<String, String>>) mapToTimeRefTable((List<HashMap<String, Object>>) obj.get(MdekKeys.MISSING)));
//		mdekObj.setTimeRefExplanation((String) obj.get(MdekKeys.MISSING));
		
		// ExtraInfo
//		mdekObj.setExtraInfoLangMetaData((String) obj.get(MdekKeys.MISSING));
//		mdekObj.setExtraInfoLangData((String) obj.get(MdekKeys.MISSING));
//		mdekObj.setExtraInfoPublishArea((String) obj.get(MdekKeys.MISSING));
//		mdekObj.setExtraInfoPurpose((String) obj.get(MdekKeys.MISSING));
//		mdekObj.setExtraInfoUse((String) obj.get(MdekKeys.MISSING));
//		mdekObj.setExtraInfoXMLExportTable((ArrayList<String>) mapToExtraInfoXMLExportTable((List<String>) obj.get(MdekKeys.MISSING)));
//		mdekObj.setExtraInfoLegalBasicsTable((ArrayList<String>) mapToExtraInfoLegalBasicsTable((List<String>) obj.get(MdekKeys.MISSING)));

		// Availability
//		mdekObj.setAvailabilityDataFormatTable((ArrayList<HashMap<String, String>>) mapToAvailabilityDataFormatTable((List<HashMap<String, Object>>) obj.get(MdekKeys.MISSING)));
//		mdekObj.setAvailabilityMediaOptionsTable((ArrayList<HashMap<String, String>>) mapToAvailabilityMediaOptionsTable((List<HashMap<String, Object>>) obj.get(MdekKeys.MISSING)));
//		mdekObj.setAvailabilityOrderInfo((String) obj.get(MdekKeys.MISSING));
//		mdekObj.setAvailabilityNoteUse((String) obj.get(MdekKeys.MISSING));
//		mdekObj.setAvailabilityCosts((String) obj.get(MdekKeys.MISSING));
		
		// Thesaurus
//		mdekObj.setThesaurusTermsTable((ArrayList<HashMap<String, String>>) mapToThesaurusTermsTable((List<HashMap<String, Object>>) obj.get(MdekKeys.MISSING)));
//		mdekObj.setThesaurusTopicsList((ArrayList<String>) mapToThesaurusTopicsList((List<String>) obj.get(MdekKeys.MISSING)));
//		mdekObj.setThesaurusFreeTermsList((ArrayList<String>) mapToThesaurusFreeTermsList((List<String>) obj.get(MdekKeys.MISSING)));
//		mdekObj.setThesaurusEnvExtRes((Boolean) obj.get(MdekKeys.MISSING));
//		mdekObj.setThesaurusEnvTopicsList((ArrayList<String>) mapToThesaurusEnvTopicsList((List<String>) obj.get(MdekKeys.MISSING)));
//		mdekObj.setThesaurusEnvCatsList((ArrayList<String>) mapToThesaurusEnvCatsList((List<String>) obj.get(MdekKeys.MISSING)));
//
//		mdekObj.setLinksToTable((ArrayList<HashMap<String, String>>) mapToLinksToTable((List<HashMap<String, Object>>) obj.get(MdekKeys.MISSING)));
//		mdekObj.setLinksFromTable((ArrayList<HashMap<String, String>>) mapToLinksFromTable((List<HashMap<String, Object>>) obj.get(MdekKeys.MISSING)));
		
		
		// TODO Should we move the gui specific settings to another object / to the entry service?
		mdekObj.setNodeAppType("O");

		return mdekObj;
	}

	private HashMap<String, Object> getSimpleMdekRepresentation(
			HashMap<String, Object> obj) {
		
		// by default, assertions are disabled at runtime.
		// Start the server with -ea to enable this method call
		try { assert testSimpleInputConformity(obj); }
		catch (AssertionError e) { e.printStackTrace(); }

		HashMap<String, Object> mdekObj = new HashMap<String, Object>();

		mdekObj.put(MDEK_OBJECT_UUID, obj.get(MdekKeys.UUID));
		mdekObj.put(MDEK_OBJECT_TITLE, obj.get(MdekKeys.TITLE));
		mdekObj.put(MDEK_OBJECT_DOCTYPE, "Class"+((Integer) obj.get(MdekKeys.CLASS) + 1));
		mdekObj.put(MDEK_OBJECT_HAS_CHILDREN, obj.get(MdekKeys.HAS_CHILD));

		return mdekObj;
	}

	public Object convertFromMdekRepresentation(MdekDataBean data){
		IngridDocument udkObj = new IngridDocument();

		udkObj.put(MdekKeys.ABSTRACT, data.getGeneralDescription());
		udkObj.put(MdekKeys.UUID, data.getId());
		udkObj.put(MdekKeys.TITLE, data.getObjectName());
		// extrahieren des int Wertes für die Objekt-Klasse
		udkObj.put(MdekKeys.CLASS, Integer.parseInt(data.getNodeDocType().substring(5)) + 1);
//		udkObj.put(MdekKeys.HAS_CHILD, data.getHasChildren());
		udkObj.put(MdekKeys.ADR_ENTITIES, mapFromGeneralAddressTable(data.getGeneralAddressTable()));
		
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
			mappedEntry.put(MdekKeys.UUID, address.getId());
			resultList.add(mappedEntry);
		}
		return resultList;
	}

	
	
	
	/****************************************************************************
	 * Mapping from the IngridDocument Structure to the Mdek gui representation *
	 ****************************************************************************/

	private static ArrayList<MdekAddressBean> mapToGeneralAddressTable(List<HashMap<String, Object>> adrTable) {
		ArrayList<MdekAddressBean> resultTable = new ArrayList<MdekAddressBean>(); 

		for (HashMap<String, Object> tableRow : adrTable) {
			MdekAddressBean address = new MdekAddressBean();

			address.setId((String) tableRow.get(MdekKeys.UUID));
			address.setInformation((String) tableRow.get(MdekKeys.TITLE_OR_FUNCTION));
			address.setIcon(((Integer) tableRow.get(MdekKeys.CLASS)).toString());
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
	
	private static void printHashMap(HashMap<String, Object> map) {
		Set<Map.Entry<String, Object>> entrySet = map.entrySet();
		for (Map.Entry<String, Object> entry : entrySet) {
			log.debug("Key: "+entry.getKey()+" Value: "+entry.getValue());
		}
	}

	
}