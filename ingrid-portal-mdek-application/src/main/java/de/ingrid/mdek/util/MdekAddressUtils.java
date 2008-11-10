package de.ingrid.mdek.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.ingrid.mdek.DataMapperInterface;
import de.ingrid.mdek.EnumUtil;
import de.ingrid.mdek.MdekKeys;
import de.ingrid.mdek.MdekUtils.AddressType;
import de.ingrid.mdek.MdekUtils.WorkState;
import de.ingrid.mdek.beans.address.MdekAddressBean;
import de.ingrid.mdek.beans.query.AddressSearchResultBean;
import de.ingrid.mdek.beans.query.AddressStatisticsResultBean;
import de.ingrid.mdek.beans.query.StatisticsBean;
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

	public static MdekAddressBean extractSingleAddress(IngridDocument doc) {
		return dataMapper.getDetailedAddressRepresentation(doc);		
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

			if (result.get(MdekKeys.TOTAL_NUM) != null) {
				searchResult.setNumHits((Long) result.get(MdekKeys.TOTAL_NUM));				
			} else {
				searchResult.setNumHits(0);
			}

			if (result.get(MdekKeys.TOTAL_NUM_PAGING) != null) {
				searchResult.setTotalNumHits((Long) result.get(MdekKeys.TOTAL_NUM_PAGING));
			} else {
				searchResult.setTotalNumHits(0);
			}

			// Additional data
			Map<String, String> additionalData = new HashMap<String, String>();
			searchResult.setAdditionalData(additionalData);
			Long totalNumQAAssigned = (Long) result.get(MdekKeys.TOTAL_NUM_QA_ASSIGNED);
			if (totalNumQAAssigned != null) {
				additionalData.put(MdekKeys.TOTAL_NUM_QA_ASSIGNED, totalNumQAAssigned.toString());
			}
			Long totalNumQAReassigned = (Long) result.get(MdekKeys.TOTAL_NUM_QA_REASSIGNED);
			if (totalNumQAReassigned != null) {
				additionalData.put(MdekKeys.TOTAL_NUM_QA_REASSIGNED, totalNumQAReassigned.toString());
			}

			searchResult.setResultList(nodeList);
		} else {
			MdekErrorUtils.handleError(response);
		}
		return searchResult;
	}

	public static String createAddressTitle(String institution, String lastName, String firstName) {
		institution = institution == null ? "" : institution.trim();
		lastName = lastName == null ? "" : lastName.trim();
		firstName = firstName == null ? "" : firstName.trim();

		if (institution.length() != 0) {
			return institution;

		} else {
			return firstName.length() != 0 ? lastName+", "+firstName : lastName;
		}
	}

	public static AddressStatisticsResultBean extractAddressStatistics(IngridDocument result) {
		AddressStatisticsResultBean res = new AddressStatisticsResultBean();
		Map<Integer, StatisticsBean> resultMap = new HashMap<Integer, StatisticsBean>();

		Object[] adrClasses = EnumUtil.getDbValues(AddressType.class);
		Object[] workStates = EnumUtil.getDbValues(WorkState.class);
		for (Object adrClass : adrClasses) {
			StatisticsBean stats = new StatisticsBean();
			Map<String, Long> resClassMap = new HashMap<String, Long>();
			IngridDocument classMap = (IngridDocument) result.get(adrClass);

			if (classMap == null) {
				for (Object workState : workStates) {
					resClassMap.put((String) workState, 0L);
				}
				stats.setNumTotal(0L);
				stats.setClassMap(resClassMap);

			} else {
				for (Object workState : workStates) {
					// dwr uses the 'toString' method to convert enums to javascript strings. Therefore, if we use enums
					// we end up with the wrong identifiers on the client. Use strings instead.
	//				resClassMap.put(WorkState.valueOf((String) workState), (Long) classMap.get(workState));
					resClassMap.put((String) workState, (Long) classMap.get(workState));
				}
				stats.setNumTotal((Long) classMap.get(MdekKeys.TOTAL_NUM));
				stats.setClassMap(resClassMap);
			}

			resultMap.put((Integer) adrClass, stats);
		}
		res.setResultMap(resultMap);

		return res;
	}

	
	public static void setInitialValues(MdekAddressBean addr) {
		dataMapper.setInitialValues(addr);
	}
	

	public DataMapperInterface getDataMapper() {
		return dataMapper;
	}

	public void setDataMapper(DataMapperInterface dataMapper) {
		MdekAddressUtils.dataMapper = dataMapper;
	}
}
