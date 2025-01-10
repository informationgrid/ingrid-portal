/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.mdek.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.ingrid.mdek.DataMapperInterface;
import de.ingrid.mdek.EnumUtil;
import de.ingrid.mdek.MdekKeys;
import de.ingrid.mdek.MdekUtils.ObjectType;
import de.ingrid.mdek.MdekUtils.WorkState;
import de.ingrid.mdek.beans.TreeNodeBean;
import de.ingrid.mdek.beans.object.MdekDataBean;
import de.ingrid.mdek.beans.query.ObjectSearchResultBean;
import de.ingrid.mdek.beans.query.ObjectStatisticsResultBean;
import de.ingrid.mdek.beans.query.StatisticsBean;
import de.ingrid.utils.IngridDocument;

@Service
public class MdekObjectUtils {
	
	// Injected via Spring
	private static DataMapperInterface dataMapper;
	
	@Autowired
	public MdekObjectUtils(DataMapperInterface mapper) {
	    MdekObjectUtils.dataMapper = mapper;
	}


	public static IngridDocument convertFromObjectRepresentation(MdekDataBean obj) {
		return dataMapper.convertFromObjectRepresentation(obj);
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

	public static List<MdekDataBean> extractDetailedObjects(IngridDocument doc) {
		List<MdekDataBean> results = new ArrayList<>();

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

	public static List<MdekDataBean> extractDetailedObjectsFromResponse(IngridDocument response) {
		IngridDocument result = MdekUtils.getResultFromResponse(response);
		if (result != null) {
			return extractDetailedObjects(result);
		} else {
			MdekErrorUtils.handleError(response);
			return new ArrayList<>();
		}
	}

	public static List<TreeNodeBean> extractObjectsFromResponse(IngridDocument response) {
		IngridDocument result = MdekUtils.getResultFromResponse(response);

		List<TreeNodeBean> nodeList = null;

		if (result != null) {
			nodeList = new ArrayList<>();
			List<IngridDocument> objs = (List<IngridDocument>) result.get(MdekKeys.OBJ_ENTITIES);
			for (IngridDocument objEntity : objs) {
				nodeList.add(dataMapper.getSimpleObjectRepresentation(objEntity));
			}
		} else {
			MdekErrorUtils.handleError(response);
		}
		return nodeList;
	}

	
	public static TreeNodeBean extractSingleSimpleObjectFromResponse(IngridDocument response) {
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
			List<MdekDataBean> nodeList = new ArrayList<>();
			if (objs == null) {
				return null;
			}

			for (IngridDocument objEntity : objs) {
				nodeList.add(dataMapper.getDetailedObjectRepresentation(objEntity));
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
			searchResult.setResultList(nodeList);

			// Additional data
			Map<String, String> additionalData = new HashMap<>();
			searchResult.setAdditionalData(additionalData);
			Long totalNumQAAssigned = (Long) result.get(MdekKeys.TOTAL_NUM_QA_ASSIGNED);
			if (totalNumQAAssigned != null) {
				additionalData.put(MdekKeys.TOTAL_NUM_QA_ASSIGNED, totalNumQAAssigned.toString());
			}
			Long totalNumQAReassigned = (Long) result.get(MdekKeys.TOTAL_NUM_QA_REASSIGNED);
			if (totalNumQAReassigned != null) {
				additionalData.put(MdekKeys.TOTAL_NUM_QA_REASSIGNED, totalNumQAReassigned.toString());
			}

		} else {
			MdekErrorUtils.handleError(response);
		}
		return searchResult;
	}

	public static ObjectStatisticsResultBean extractObjectStatistics(IngridDocument result) {
		ObjectStatisticsResultBean res = new ObjectStatisticsResultBean();
		Map<Integer, StatisticsBean> resultMap = new HashMap<>();

		Object[] objClasses = EnumUtil.getDbValues(ObjectType.class);
		Object[] workStates = EnumUtil.getDbValues(WorkState.class);
		for (Object objClass : objClasses) {
			StatisticsBean stats = new StatisticsBean();
			Map<String, Long> resClassMap = new HashMap<>();
			IngridDocument classMap = (IngridDocument) result.get(objClass);

			for (Object workState : workStates) {
				// dwr uses the 'toString' method to convert enums to javascript strings. Therefore, if we use enums
				// we end up with the wrong identifiers on the client. Use strings instead.
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
