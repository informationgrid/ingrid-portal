/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
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

import de.ingrid.mdek.DataMapperInterface;
import de.ingrid.mdek.EnumUtil;
import de.ingrid.mdek.MdekKeys;
import de.ingrid.mdek.MdekUtils.AddressType;
import de.ingrid.mdek.MdekUtils.WorkState;
import de.ingrid.mdek.beans.TreeNodeBean;
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

	
	public static TreeNodeBean extractSingleSimpleAddressFromResponse(IngridDocument response) {
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

	public static List<MdekAddressBean> extractDetailedAddresses(IngridDocument doc) {
		List<MdekAddressBean> results = new ArrayList<MdekAddressBean>();

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

	public static List<MdekAddressBean> extractDetailedAddressesFromResponse(IngridDocument response) {
		IngridDocument result = MdekUtils.getResultFromResponse(response);
		if (result != null) {
			return extractDetailedAddresses(result);
		} else {
			MdekErrorUtils.handleError(response);
			return null;
		}
	}

	public static List<TreeNodeBean> extractAddressesFromResponse(IngridDocument response) {
		IngridDocument result = MdekUtils.getResultFromResponse(response);

		List<TreeNodeBean> nodeList = null;

		if (result != null) {
			nodeList = new ArrayList<TreeNodeBean>();
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
			List<MdekAddressBean> nodeList = new ArrayList<MdekAddressBean>();

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
	
	public static String extractInstitutions(MdekAddressBean address) {
        String organisations = "";
        if (address.getParentInstitutions().size() > 0) {
            for (int i = address.getParentInstitutions().size()-1; i >= 0; --i) {
                if (address.getParentInstitutions().get(i).getAddressClass() == 0) {
                    // Only display the first institution we encounter and break
                    organisations = address.getParentInstitutions().get(i).getOrganisation()+"\n"+organisations;
                    break;
    
                } else if (address.getParentInstitutions().get(i).getAddressClass() == 1) {
                    organisations = "\t"+address.getParentInstitutions().get(i).getOrganisation()+"\n"+organisations;
                }
            }
            // also check if we (as an institution or unit!) have an organisation set
            if (address.getAddressClass() == 1 || address.getAddressClass() == 0 && address.getOrganisation() != null)
                organisations += address.getOrganisation();
        } else {
            organisations = address.getOrganisation();
            if (organisations == null)
                return null;
        }
        return organisations.trim();
    }

	public DataMapperInterface getDataMapper() {
		return dataMapper;
	}

	public void setDataMapper(DataMapperInterface dataMapper) {
		MdekAddressUtils.dataMapper = dataMapper;
	}
}
