package de.ingrid.mdek.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.ingrid.mdek.MdekKeys;
import de.ingrid.mdek.beans.CatalogBean;
import de.ingrid.mdek.beans.object.LocationBean;
import de.ingrid.utils.IngridDocument;

public class MdekCatalogUtils {

	public static Map<Integer, List<String[]>> extractSysListFromResponse(IngridDocument response) {
		IngridDocument result = MdekUtils.getResultFromResponse(response);
		if (result != null) {
			Map<Integer, List<String[]>> resultMap = new HashMap<Integer, List<String[]>>();
			Set<String> listKeys = (Set<String>) result.keySet();
			for (String listKey : listKeys) {
				IngridDocument listDocument = (IngridDocument) result.get(listKey);
				ArrayList<String[]> resultList = new ArrayList<String[]>();
				Integer listId = (Integer) listDocument.get(MdekKeys.LST_ID);

				List<IngridDocument> entries = (List<IngridDocument>) listDocument.get(MdekKeys.LST_ENTRY_LIST);
//				resultList.add( new String[] {"", ""} );
				for (IngridDocument entry : entries) {
//					resultList.add( new String[] {StringEscapeUtils.escapeJavaScript(entry.getString(MdekKeys.ENTRY_NAME)), ((Integer) entry.get(MdekKeys.ENTRY_ID)).toString()} );
					resultList.add( new String[] {entry.getString(MdekKeys.ENTRY_NAME), ((Integer) entry.get(MdekKeys.ENTRY_ID)).toString(), (String) entry.get(MdekKeys.IS_DEFAULT) } );
				}
				resultMap.put(listId, resultList);
			}
			return resultMap;

		} else {
			MdekErrorUtils.handleError(response);
			return null;
		}
	}

	public static CatalogBean extractCatalogFromResponse(IngridDocument response) {
		IngridDocument result = MdekUtils.getResultFromResponse(response);
		
		if (result != null) {
			CatalogBean resultCat = new CatalogBean();
	
			resultCat.setUuid(result.getString(MdekKeys.UUID));
			resultCat.setCatalogName(result.getString(MdekKeys.CATALOG_NAME));
			resultCat.setCountry(result.getString(MdekKeys.COUNTRY));
			resultCat.setWorkflowControl(result.getString(MdekKeys.WORKFLOW_CONTROL));
			resultCat.setExpiryDuration((Integer) result.get(MdekKeys.EXPIRY_DURATION));
			resultCat.setDateOfCreation(MdekUtils.convertTimestampToDate((String) result.get(MdekKeys.DATE_OF_CREATION)));
			resultCat.setDateOfLastModification(MdekUtils.convertTimestampToDate((String) result.get(MdekKeys.DATE_OF_LAST_MODIFICATION)));
			resultCat.setLocation(mapToLocationBean((IngridDocument) result.get(MdekKeys.CATALOG_LOCATION)));			

			IngridDocument modUserDoc = (IngridDocument) result.get(MdekKeys.MOD_USER);
			if (modUserDoc != null)
				resultCat.setModUuid((String) modUserDoc.get(MdekKeys.UUID));

			return resultCat;
		} else {
			MdekErrorUtils.handleError(response);
			return null;
		}
	}

	private static LocationBean mapToLocationBean(IngridDocument locationDoc) {
		LocationBean location = new LocationBean();
		location.setType((String) locationDoc.get(MdekKeys.LOCATION_TYPE));
		location.setName((String) locationDoc.get(MdekKeys.LOCATION_NAME));
		location.setNativeKey((String) locationDoc.get(MdekKeys.LOCATION_CODE));
		location.setTopicId((String) locationDoc.get(MdekKeys.LOCATION_SNS_ID));
		location.setLongitude1((Double) locationDoc.get(MdekKeys.WEST_BOUNDING_COORDINATE));
		location.setLatitude1((Double) locationDoc.get(MdekKeys.SOUTH_BOUNDING_COORDINATE));
		location.setLongitude2((Double) locationDoc.get(MdekKeys.EAST_BOUNDING_COORDINATE));
		location.setLatitude2((Double) locationDoc.get(MdekKeys.NORTH_BOUNDING_COORDINATE));
		return location;
	}
}
