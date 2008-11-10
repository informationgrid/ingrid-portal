package de.ingrid.mdek.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.ingrid.mdek.DataMapperInterface;
import de.ingrid.mdek.EnumUtil;
import de.ingrid.mdek.MdekKeys;
import de.ingrid.mdek.MdekUtils.ObjectType;
import de.ingrid.mdek.MdekUtils.WorkState;
import de.ingrid.mdek.beans.address.MdekAddressBean;
import de.ingrid.mdek.beans.object.MdekDataBean;
import de.ingrid.mdek.beans.query.ObjectSearchResultBean;
import de.ingrid.mdek.beans.query.ObjectStatisticsResultBean;
import de.ingrid.mdek.beans.query.StatisticsBean;
import de.ingrid.utils.IngridDocument;

public class MdekObjectUtils {
	
	// Injected via Spring
	private static DataMapperInterface dataMapper;


	public static IngridDocument convertFromObjectRepresentation(MdekDataBean obj) {
		return (IngridDocument) dataMapper.convertFromObjectRepresentation(obj);
	}

	
	public static MdekDataBean extractSingleObject(IngridDocument doc) {
			return dataMapper.getDetailedObjectRepresentation(doc);
	}

	public static MdekDataBean extractSingleObjectFromResponse(IngridDocument response) {
		IngridDocument result = MdekUtils.getResultFromResponse(response);

		if (result != null) {
			return dataMapper.getDetailedObjectRepresentation(result);
		} else {
			MdekErrorUtils.handleError(response);
			return null;
		}		
	}

	public static ArrayList<MdekDataBean> extractDetailedObjects(IngridDocument doc) {
		ArrayList<MdekDataBean> results = new ArrayList<MdekDataBean>();

		if (doc != null) {
			List<IngridDocument> objs = (List<IngridDocument>) doc.get(MdekKeys.OBJ_ENTITIES);
			if (objs != null) {
				for (IngridDocument objEntity : objs) {
					results.add(dataMapper.getDetailedObjectRepresentation(objEntity));
				}
			}
		}
		return results;
	}

	public static ArrayList<HashMap<String, Object>> extractObjectsFromResponse(IngridDocument response) {
		IngridDocument result = MdekUtils.getResultFromResponse(response);

		ArrayList<HashMap<String, Object>> nodeList = null;

		if (result != null) {
			nodeList = new ArrayList<HashMap<String, Object>>();
			List<IngridDocument> objs = (List<IngridDocument>) result.get(MdekKeys.OBJ_ENTITIES);
			for (IngridDocument objEntity : objs) {
				nodeList.add(dataMapper.getSimpleObjectRepresentation(objEntity));
			}
		} else {
			MdekErrorUtils.handleError(response);
		}
		return nodeList;
	}

	
	public static HashMap<String, Object> extractSingleSimpleObjectFromResponse(IngridDocument response) {
		IngridDocument result = MdekUtils.getResultFromResponse(response);

		if (result != null) {
			return dataMapper.getSimpleObjectRepresentation(result);
		} else {
			MdekErrorUtils.handleError(response);
			return null;
		}
	}	

	public static ObjectSearchResultBean extractObjectSearchResultsFromResponse(IngridDocument response) {
		IngridDocument result = MdekUtils.getResultFromResponse(response);

		ObjectSearchResultBean searchResult = new ObjectSearchResultBean();

		if (result != null) {
			List<IngridDocument> objs = (List<IngridDocument>) result.get(MdekKeys.OBJ_ENTITIES);
			ArrayList<MdekDataBean> nodeList = new ArrayList<MdekDataBean>();
			if (objs == null) {
				return null;
			}

			for (IngridDocument objEntity : objs) {
				nodeList.add(dataMapper.getDetailedObjectRepresentation(objEntity));
			}

			if (result.get(MdekKeys.TOTAL_NUM) != null) {
				searchResult.setNumHits(((Long) result.get(MdekKeys.TOTAL_NUM)).intValue());				
			} else {
				searchResult.setNumHits(0);
			}

			if (result.get(MdekKeys.TOTAL_NUM_PAGING) != null) {
				searchResult.setTotalNumHits(((Long) result.get(MdekKeys.TOTAL_NUM_PAGING)).intValue());
			} else {
				searchResult.setTotalNumHits(0);
			}

			searchResult.setResultList(nodeList);

		} else {
			MdekErrorUtils.handleError(response);
		}
		return searchResult;
	}

	public static ObjectStatisticsResultBean extractObjectStatistics(IngridDocument result) {
		ObjectStatisticsResultBean res = new ObjectStatisticsResultBean();
		Map<Integer, StatisticsBean> resultMap = new HashMap<Integer, StatisticsBean>();

		Object[] objClasses = EnumUtil.getDbValues(ObjectType.class);
		Object[] workStates = EnumUtil.getDbValues(WorkState.class);
		for (Object objClass : objClasses) {
			StatisticsBean stats = new StatisticsBean();
			Map<String, Long> resClassMap = new HashMap<String, Long>();
			IngridDocument classMap = (IngridDocument) result.get(objClass);

			for (Object workState : workStates) {
				// dwr uses the 'toString' method to convert enums to javascript strings. Therefore, if we use enums
				// we end up with the wrong identifiers on the client. Use strings instead.
//				resClassMap.put(WorkState.valueOf((String) workState), (Long) classMap.get(workState));
				resClassMap.put((String) workState, (Long) classMap.get(workState));
			}
			stats.setNumTotal((Long) classMap.get(MdekKeys.TOTAL_NUM));
			stats.setClassMap(resClassMap);

			resultMap.put((Integer) objClass, stats);
		}
		res.setResultMap(resultMap);

		return res;
	}

	
	public static void setInitialValues(MdekDataBean obj) {
		dataMapper.setInitialValues(obj);
	}

	public DataMapperInterface getDataMapper() {
		return dataMapper;
	}

	public void setDataMapper(DataMapperInterface dataMapper) {
		MdekObjectUtils.dataMapper = dataMapper;
	}
}
