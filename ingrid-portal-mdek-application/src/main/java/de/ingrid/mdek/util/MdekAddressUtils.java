package de.ingrid.mdek.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.ingrid.mdek.DataMapperInterface;
import de.ingrid.mdek.MdekKeys;
import de.ingrid.mdek.beans.AddressSearchResultBean;
import de.ingrid.mdek.beans.MdekAddressBean;
import de.ingrid.utils.IngridDocument;

public class MdekAddressUtils {

	// Injected via Spring
	private static DataMapperInterface dataMapper;
	
	public static IngridDocument convertFromAddressRepresentation(MdekAddressBean adr) {
		return (IngridDocument) dataMapper.convertFromAddressRepresentation(adr);
	}

	
	public static HashMap<String, Object> extractSingleSimpleAddressFromResponse(IngridDocument response) {
		IngridDocument result = MdekUtils.getResultFromResponse(response);

		if (result != null) {
			return dataMapper.getSimpleAddressRepresentation(result);
		} else {
			MdekErrorUtils.handleError(response);
			return null;
		}
	}

	public static MdekAddressBean extractSingleAddressFromResponse(IngridDocument response) {
		IngridDocument result = MdekUtils.getResultFromResponse(response);

		if (result != null) {
			return dataMapper.getDetailedAddressRepresentation(result);
		} else {
			MdekErrorUtils.handleError(response);
			return null;
		}
	}

	public static ArrayList<MdekAddressBean> extractDetailedAddresses(IngridDocument doc) {
		ArrayList<MdekAddressBean> results = new ArrayList<MdekAddressBean>();

		if (doc != null) {
			List<IngridDocument> adrs = (List<IngridDocument>) doc.get(MdekKeys.ADR_ENTITIES);
			if (adrs != null) {
				for (IngridDocument adrEntity : adrs) {
					results.add(dataMapper.getDetailedAddressRepresentation(adrEntity));
				}
			}
		}
		return results;
	}

	public static ArrayList<HashMap<String, Object>> extractAddressesFromResponse(IngridDocument response) {
		IngridDocument result = MdekUtils.getResultFromResponse(response);

		ArrayList<HashMap<String, Object>> nodeList = null;

		if (result != null) {
			nodeList = new ArrayList<HashMap<String, Object>>();
			List<IngridDocument> adrs = (List<IngridDocument>) result.get(MdekKeys.ADR_ENTITIES);
			for (IngridDocument adrEntity : adrs) {
				nodeList.add(dataMapper.getSimpleAddressRepresentation(adrEntity));
			}
		} else {
			MdekErrorUtils.handleError(response);
		}
		return nodeList;
	}

	public static AddressSearchResultBean extractAddressSearchResultsFromResponse(IngridDocument response) {
		IngridDocument result = MdekUtils.getResultFromResponse(response);

		AddressSearchResultBean searchResult = new AddressSearchResultBean();

		if (result != null) {
			List<IngridDocument> adrs = (List<IngridDocument>) result.get(MdekKeys.ADR_ENTITIES);
			ArrayList<MdekAddressBean> nodeList = new ArrayList<MdekAddressBean>();

			if (adrs == null) {
				return null;
			}

			for (IngridDocument adrEntity : adrs) {
				nodeList.add(dataMapper.getDetailedAddressRepresentation(adrEntity));
			}

			searchResult.setNumHits((Integer) result.get(MdekKeys.SEARCH_NUM_HITS));
			searchResult.setTotalNumHits((Long) result.get(MdekKeys.SEARCH_TOTAL_NUM_HITS));
			searchResult.setResultList(nodeList);
		} else {
			MdekErrorUtils.handleError(response);
		}
		return searchResult;
	}


	public DataMapperInterface getDataMapper() {
		return dataMapper;
	}

	public void setDataMapper(DataMapperInterface dataMapper) {
		MdekAddressUtils.dataMapper = dataMapper;
	}
}
