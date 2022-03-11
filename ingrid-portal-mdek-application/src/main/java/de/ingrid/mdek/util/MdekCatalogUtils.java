/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2022 wemove digital solutions GmbH
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
import java.util.Set;

import org.apache.log4j.Logger;

import com.thoughtworks.xstream.XStream;

import de.ingrid.mdek.MdekKeys;
import de.ingrid.mdek.beans.AnalyzeJobInfoBean;
import de.ingrid.mdek.beans.CatalogBean;
import de.ingrid.mdek.beans.CodeListJobInfoBean;
import de.ingrid.mdek.beans.ExportJobInfoBean;
import de.ingrid.mdek.beans.GenericValueBean;
import de.ingrid.mdek.beans.JobInfoBean;
import de.ingrid.mdek.beans.JobInfoBean.EntityType;
import de.ingrid.mdek.beans.SNSLocationUpdateJobInfoBean;
import de.ingrid.mdek.beans.SNSLocationUpdateResult;
import de.ingrid.mdek.beans.SNSTopicUpdateResult;
import de.ingrid.mdek.beans.SNSUpdateJobInfoBean;
import de.ingrid.mdek.beans.SysList;
import de.ingrid.mdek.beans.URLJobInfoBean;
import de.ingrid.mdek.beans.object.LocationBean;
import de.ingrid.mdek.caller.MdekCaller;
import de.ingrid.mdek.quartz.jobs.util.URLObjectReference;
import de.ingrid.mdek.quartz.jobs.util.URLState;
import de.ingrid.mdek.quartz.jobs.util.URLState.State;
import de.ingrid.mdek.services.catalog.dbconsistency.ErrorReport;
import de.ingrid.utils.IngridDocument;
import de.ingrid.utils.udk.UtilsLanguageCodelist;

public class MdekCatalogUtils {

	private static final Logger log = Logger.getLogger(MdekCatalogUtils.class);	

	private static XStream xstream = new XStream();


	public static Integer[] extractSysListIdsFromResponse(IngridDocument response) {
		List<SysList> result = extractSysListInfosFromResponse(response);
		List<Integer> listIdsTemp = new ArrayList<>();
        for (SysList codelist : result) {
            listIdsTemp.add(codelist.getId());
        }
        return listIdsTemp.toArray(new Integer[0]);
	}
	
	public static List<SysList> extractSysListInfosFromResponse(IngridDocument response) {
        IngridDocument result = MdekUtils.getResultFromResponse(response);
        List<SysList> codelists = new ArrayList<>();
        
        List<Object> docs = result.getArrayList(MdekKeys.LST_SYSLISTS);
        for (IngridDocument doc : (List<IngridDocument>)(List<?>)docs) {
            SysList codelist = new SysList();
            codelist.setId(doc.getInt(MdekKeys.LST_ID));
            codelist.setMaintainable(doc.getInt(MdekKeys.LST_MAINTAINABLE) == 1);
            codelists.add(codelist);
        }
        return codelists;
    }

	/**
	 * Returns a map containing the mapped sysLists from response:<p>
	 * { listId1 : [ [ entry1Name, entry1Id, entry1Default ],<br>
	 *               [ entry2Name, entry2Id, entry2Default ]<br>
	 *             ],<br>
	 *   listId2 : ...<br>
	 * } 
	 */
	public static Map<Integer, List<String[]>> extractSysListFromResponse(IngridDocument response) {
		IngridDocument result = MdekUtils.getResultFromResponse(response);
		if (result != null) {
			Map<Integer, List<String[]>> resultMap = new HashMap<>();
			Set<Object> listKeys = result.keySet();
			for (Object listKey : listKeys) {
				IngridDocument listDocument = (IngridDocument) result.get(listKey);
				List<String[]> resultList = new ArrayList<>();
				Integer listId = (Integer) listDocument.get(MdekKeys.LST_ID);

				Integer[] entryIds = (Integer[]) listDocument.get(MdekKeys.LST_ENTRY_IDS);
				String[] entryNames = (String[]) listDocument.get(MdekKeys.LST_ENTRY_NAMES);
				Integer defaultIndex = (Integer) listDocument.get(MdekKeys.LST_DEFAULT_ENTRY_INDEX);
				String[] entryData = (String[]) listDocument.get(MdekKeys.LST_ENTRY_DATA);

				if (entryIds != null && entryNames != null) {
					for (int index = 0; index < entryIds.length; ++index) {
						boolean isDefault = defaultIndex != null ? defaultIndex == index : false;
						
						resultList.add( new String[] {
								entryNames[index],
								entryIds[index].toString(),
								isDefault ? "Y" : "N",
								entryData[index]
						});
					}
				}

				resultMap.put(listId, resultList);
			}

			return resultMap;

		} else {
			MdekErrorUtils.handleError(response);
			return null;
		}
	}

	public static String[] extractFreeSysListEntriesFromResponse(IngridDocument response) {
		IngridDocument result = MdekUtils.getResultFromResponse(response);
		if (result != null) {
			return (String[]) result.get(MdekKeys.LST_FREE_ENTRY_NAMES);

		} else {
			MdekErrorUtils.handleError(response);
			return new String[0];
		}
	}

	public static String convertSysListsToXML(IngridDocument resultDe, IngridDocument resultEn) {
		Set<String> listKeys = (Set<String>)(Set<?>) resultDe.keySet();
		List<SysList> sysLists = new ArrayList<>();

		for (String listKey : listKeys) {
			SysList sysList = new SysList();

			IngridDocument listDocumentDe = (IngridDocument) resultDe.get(listKey);
			IngridDocument listDocumentEn = (IngridDocument) resultEn.get(listKey);
			Integer listId = (Integer) listDocumentDe.get(MdekKeys.LST_ID);

			Integer[] entryIds = (Integer[]) listDocumentDe.get(MdekKeys.LST_ENTRY_IDS);
			String[] entryNamesDe = (String[]) listDocumentDe.get(MdekKeys.LST_ENTRY_NAMES);
			String[] entryNamesEn = (String[]) listDocumentEn.get(MdekKeys.LST_ENTRY_NAMES);
			Integer defaultIndex = (Integer) listDocumentDe.get(MdekKeys.LST_DEFAULT_ENTRY_INDEX);
			Boolean maintainable = (de.ingrid.mdek.MdekUtils.YES_INTEGER == ((Integer) listDocumentDe.get(MdekKeys.LST_MAINTAINABLE)));

			sysList.setId(listId);
			sysList.setDeEntries(entryNamesDe);
			sysList.setEnEntries(entryNamesEn);
			sysList.setDefaultIndex(defaultIndex);
			sysList.setEntryIds(entryIds);
			sysList.setMaintainable(maintainable);

			sysLists.add(sysList);
		}

		xstream.alias("sysList", SysList.class);
		return xstream.toXML(sysLists);
	}

	public static List<SysList> convertXMLToSysLists(String xmlDoc) {
		xstream.alias("sysList", SysList.class);
		return (List<SysList>) xstream.fromXML(xmlDoc);
	}

	public static List<GenericValueBean> extractSysGenericKeysFromResponse(IngridDocument response) {
		IngridDocument result = MdekUtils.getResultFromResponse(response);
		List<GenericValueBean> genericValueList = new ArrayList<>();

		log.debug(result);

		for (Map.Entry<Object, Object> genericValueEntry : result.entrySet()) {
			GenericValueBean genericValue = new GenericValueBean();
			genericValue.setKey((String) genericValueEntry.getKey());
			genericValue.setValue((String) genericValueEntry.getValue());
			genericValueList.add(genericValue);
		}

		return genericValueList;
	}

	public static CatalogBean extractCatalogFromResponse(IngridDocument response) {
		IngridDocument result = MdekUtils.getResultFromResponse(response);
		
		if (result != null) {
			CatalogBean resultCat = new CatalogBean();
	
			log.debug("MDEK KEYS:");
			log.debug(result.getInt(MdekKeys.LANGUAGE_CODE));
			log.debug(result.getString(MdekKeys.LANGUAGE_NAME));
			
			resultCat.setUuid(result.getString(MdekKeys.UUID));
			resultCat.setCatalogName(result.getString(MdekKeys.CATALOG_NAME));
			resultCat.setCatalogNamespace(result.getString(MdekKeys.CATALOG_NAMESPACE));
			resultCat.setPartnerName(result.getString(MdekKeys.PARTNER_NAME));
			resultCat.setProviderName(result.getString(MdekKeys.PROVIDER_NAME));
			resultCat.setCountryCode(result.getInt(MdekKeys.COUNTRY_CODE));
			resultCat.setLanguageCode(result.getInt(MdekKeys.LANGUAGE_CODE));
			resultCat.setLanguageShort(getLanguageShort(resultCat.getLanguageCode()));
			resultCat.setWorkflowControl(result.getString(MdekKeys.WORKFLOW_CONTROL));
			resultCat.setExpiryDuration((Integer) result.get(MdekKeys.EXPIRY_DURATION));
			resultCat.setDateOfCreation(MdekUtils.convertTimestampToDate((String) result.get(MdekKeys.DATE_OF_CREATION)));
			resultCat.setDateOfLastModification(MdekUtils.convertTimestampToDate((String) result.get(MdekKeys.DATE_OF_LAST_MODIFICATION)));
			resultCat.setLocation(mapToLocationBean((IngridDocument) result.get(MdekKeys.CATALOG_LOCATION)));
			resultCat.setAtomUrl(result.getString(MdekKeys.CATALOG_ATOM_URL));			

			IngridDocument modUserDoc = (IngridDocument) result.get(MdekKeys.MOD_USER);
			if (modUserDoc != null)
				resultCat.setModUuid((String) modUserDoc.get(MdekKeys.UUID));

			return resultCat;
		} else {
			MdekErrorUtils.handleError(response);
			return null;
		}
	}

	public static String getLanguageShort(Integer languageCode) {
		if (languageCode.compareTo(UtilsLanguageCodelist.getCodeFromShortcut("en")) == 0)
			return "en";
		else if (languageCode.compareTo(UtilsLanguageCodelist.getCodeFromShortcut("de")) == 0)
			return "de";
		else {
			log.debug("Language ("+languageCode+") not supported! Using 'de' as default!");
			return "de";
		}
	}

	public static JobInfoBean extractJobInfoFromResponse(IngridDocument response) {
		JobInfoBean jobInfo = new JobInfoBean();
		addGeneralJobInfoFromResponse(response, jobInfo);
		return jobInfo;
	}

	public static ExportJobInfoBean extractExportJobInfoFromResponse(IngridDocument response) {
		ExportJobInfoBean exportJobInfo = new ExportJobInfoBean();
		addGeneralJobInfoFromResponse(response, exportJobInfo);
		addExportJobInfoFromResponse(response, exportJobInfo);

		return exportJobInfo;
	}

	public static JobInfoBean extractReindexJobInfoFromResponse(IngridDocument response) {
		JobInfoBean jobInfo = new JobInfoBean();
		addGeneralJobInfoFromResponse(response, jobInfo);
		addReindexJobInfoFromResponse(response, jobInfo);
		return jobInfo;
	}

	private static void addGeneralJobInfoFromResponse(IngridDocument response, JobInfoBean jobInfo) {
		IngridDocument jobInfoDoc = MdekUtils.getResultFromResponse(response);
		if (jobInfoDoc != null) {
			jobInfo.setDescription(jobInfoDoc.getString(MdekKeys.JOBINFO_MESSAGES));
			jobInfo.setFrontendMessages(jobInfoDoc.getString(MdekKeys.JOBINFO_FRONTEND_MESSAGES));
			jobInfo.setStartTime(MdekUtils.convertTimestampToDate(jobInfoDoc.getString(MdekKeys.JOBINFO_START_TIME)));
			jobInfo.setEndTime(MdekUtils.convertTimestampToDate(jobInfoDoc.getString(MdekKeys.JOBINFO_END_TIME)));
			if (jobInfoDoc.get(MdekKeys.JOBINFO_NUM_OBJECTS) != null) {
				jobInfo.setNumProcessedEntities(jobInfoDoc.getInt(MdekKeys.JOBINFO_NUM_OBJECTS));
				jobInfo.setNumEntities(jobInfoDoc.getInt(MdekKeys.JOBINFO_TOTAL_NUM_OBJECTS));
				jobInfo.setEntityType(EntityType.OBJECT);
				jobInfo.setNumProcessedObjects(jobInfoDoc.getInt(MdekKeys.JOBINFO_NUM_OBJECTS));
                jobInfo.setNumObjects(jobInfoDoc.getInt(MdekKeys.JOBINFO_TOTAL_NUM_OBJECTS));
			}
			if (jobInfoDoc.get(MdekKeys.JOBINFO_NUM_ADDRESSES) != null) {
				jobInfo.setNumProcessedEntities(jobInfoDoc.getInt(MdekKeys.JOBINFO_NUM_ADDRESSES));
				jobInfo.setNumEntities(jobInfoDoc.getInt(MdekKeys.JOBINFO_TOTAL_NUM_ADDRESSES));
				jobInfo.setEntityType(EntityType.ADDRESS);
                jobInfo.setNumProcessedAddresses(jobInfoDoc.getInt(MdekKeys.JOBINFO_NUM_ADDRESSES));
                jobInfo.setNumAddresses(jobInfoDoc.getInt(MdekKeys.JOBINFO_TOTAL_NUM_ADDRESSES));
			}

			// Check if an exception occured while executing the job and add it to JobInfoBean
			Exception jobException = MdekCaller.getExceptionFromJobInfo(jobInfoDoc);
			if (jobException != null) {
				jobInfo.setException(jobException);
			}

		} else {
			MdekErrorUtils.handleError(response);
		}
	}

	private static void addExportJobInfoFromResponse(IngridDocument response, ExportJobInfoBean exportJobInfo) {
		IngridDocument jobInfoDoc = MdekUtils.getResultFromResponse(response);
		if (jobInfoDoc != null) {
			exportJobInfo.setResult((byte[]) jobInfoDoc.get(MdekKeys.EXPORT_RESULT));

		} else {
			MdekErrorUtils.handleError(response);
		}
	}

	private static void addURLJobInfoFromResponse(IngridDocument response, URLJobInfoBean urlJobInfo) {
		IngridDocument urlRefDoc = MdekUtils.getResultFromResponse(response);
		if (urlRefDoc != null) {
			List<Map<String, Object>> urlResult = (List<Map<String, Object>>) urlRefDoc.get(MdekKeys.URL_RESULT);
			List<Map<String, Object>> capResult = (List<Map<String, Object>>) urlRefDoc.get(MdekKeys.CAP_RESULT);
			
			urlJobInfo.setUrlObjectReferences(getUrlReferencesFromResult(urlResult));
			urlJobInfo.setCapabilitiesReferences(getUrlReferencesFromResult(capResult));

		} else {
			MdekErrorUtils.handleError(response);
		}
	}

    private static List<URLObjectReference> getUrlReferencesFromResult(List<Map<String, Object>> urlResult) {
        List<URLObjectReference> urlReferences = new ArrayList<>();
        if (urlResult != null) {
        	for (Map<String, Object> urlRef : urlResult) {
        		URLObjectReference urlObjectRef = new URLObjectReference();
        		urlObjectRef.setObjectClass((Integer) urlRef.get(MdekKeys.URL_RESULT_OBJECT_CLASS));
        		urlObjectRef.setObjectName((String) urlRef.get(MdekKeys.URL_RESULT_OBJECT_NAME));
        		urlObjectRef.setObjectUuid((String) urlRef.get(MdekKeys.URL_RESULT_OBJECT_UUID));
        		urlObjectRef.setUrlReferenceDescription((String) urlRef.get(MdekKeys.URL_RESULT_REFERENCE_DESCRIPTION));
        		URLState urlState = new URLState((String) urlRef.get(MdekKeys.URL_RESULT_URL));
        		urlState.setState(State.valueOf((String) urlRef.get(MdekKeys.URL_RESULT_STATE)));
        		urlState.setResponseCode((Integer) urlRef.get(MdekKeys.URL_RESULT_RESPONSE_CODE));
        		urlObjectRef.setUrlState(urlState);
        		urlReferences.add(urlObjectRef);
        	}
        }
        return urlReferences;
    }

	private static void addReindexJobInfoFromResponse(IngridDocument response, JobInfoBean jobInfo) {
		IngridDocument jobInfoDoc = MdekUtils.getResultFromResponse(response);
		if (jobInfoDoc != null) {
			jobInfo.setNumProcessedEntities((Integer) jobInfoDoc.get(MdekKeys.JOBINFO_NUM_ENTITIES));
			jobInfo.setNumEntities((Integer) jobInfoDoc.get(MdekKeys.JOBINFO_TOTAL_NUM_ENTITIES));
			jobInfo.setDescription((String) jobInfoDoc.get(MdekKeys.JOBINFO_ENTITY_TYPE));

		} else {
			MdekErrorUtils.handleError(response);
		}
	}

	public static IngridDocument convertFromCatalogRepresentation(CatalogBean cat) {
		IngridDocument catDoc = new IngridDocument();

		catDoc.put(MdekKeys.UUID, cat.getUuid());
		catDoc.put(MdekKeys.CATALOG_NAME, cat.getCatalogName());
		catDoc.put(MdekKeys.CATALOG_NAMESPACE, cat.getCatalogNamespace());
		catDoc.put(MdekKeys.PARTNER_NAME, cat.getPartnerName());
		catDoc.put(MdekKeys.PROVIDER_NAME, cat.getProviderName());
		catDoc.put(MdekKeys.COUNTRY_CODE, cat.getCountryCode());
		catDoc.put(MdekKeys.LANGUAGE_CODE, cat.getLanguageCode());
		catDoc.put(MdekKeys.WORKFLOW_CONTROL, cat.getWorkflowControl());
		catDoc.put(MdekKeys.EXPIRY_DURATION, cat.getExpiryDuration());
		catDoc.put(MdekKeys.CATALOG_LOCATION, mapLocationBeanToIngridDoc(cat.getLocation()));
		catDoc.put(MdekKeys.CATALOG_ATOM_URL, cat.getAtomUrl());

		return catDoc;
	}

	private static LocationBean mapToLocationBean(IngridDocument locationDoc) {
		LocationBean location = new LocationBean();

		if (locationDoc == null) {
			return location;
		}

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

	private static IngridDocument mapLocationBeanToIngridDoc(LocationBean loc) {
		IngridDocument locDoc = new IngridDocument();

		locDoc.put(MdekKeys.LOCATION_TYPE, "G");
		locDoc.put(MdekKeys.LOCATION_NAME, loc.getName());
		locDoc.put(MdekKeys.LOCATION_CODE, loc.getNativeKey());
		locDoc.put(MdekKeys.LOCATION_SNS_ID, loc.getTopicId());
		locDoc.put(MdekKeys.WEST_BOUNDING_COORDINATE, loc.getLongitude1());
		locDoc.put(MdekKeys.SOUTH_BOUNDING_COORDINATE, loc.getLatitude1());
		locDoc.put(MdekKeys.EAST_BOUNDING_COORDINATE, loc.getLongitude2());
		locDoc.put(MdekKeys.NORTH_BOUNDING_COORDINATE, loc.getLatitude2());
		locDoc.put(MdekKeys.SNS_TOPIC_TYPE, loc.getTopicTypeId());

		return locDoc;
	}

	public static List<Map<String, Object>> convertFromUrlJobResult(List<URLObjectReference> urlObjectReferences) {
		List<Map<String, Object>> urlInfoList = new ArrayList<>();
		if (urlObjectReferences != null) {
			for (URLObjectReference ref : urlObjectReferences) {
				URLState urlState = ref.getUrlState();
				Map<String, Object> urlInfo = new HashMap<>();
				urlInfo.put(MdekKeys.URL_RESULT_URL, urlState.getUrl());
				urlInfo.put(MdekKeys.URL_RESULT_STATE, urlState.getState().toString());
				urlInfo.put(MdekKeys.URL_RESULT_RESPONSE_CODE, urlState.getResponseCode());
				urlInfo.put(MdekKeys.URL_RESULT_OBJECT_CLASS, ref.getObjectClass());
				urlInfo.put(MdekKeys.URL_RESULT_OBJECT_NAME, ref.getObjectName());
				urlInfo.put(MdekKeys.URL_RESULT_OBJECT_UUID, ref.getObjectUuid());
				urlInfo.put(MdekKeys.URL_RESULT_REFERENCE_DESCRIPTION, ref.getUrlReferenceDescription());
				urlInfoList.add(urlInfo);
			}
		}
		return urlInfoList;
	}

	public static URLJobInfoBean extractUrlJobInfoFromResponse(IngridDocument response) {
		URLJobInfoBean urlJobInfo = new URLJobInfoBean();
		addGeneralJobInfoFromResponse(response, urlJobInfo);
		addURLJobInfoFromResponse(response, urlJobInfo);

		return urlJobInfo;
	}
	
	public static AnalyzeJobInfoBean extractAnalyzeJobInfoFromResponse(IngridDocument response) {
		AnalyzeJobInfoBean analyzeJobInfo = new AnalyzeJobInfoBean();
		addGeneralJobInfoFromResponse(response, analyzeJobInfo);
		addAnalyzeJobInfoFromResponse(response, analyzeJobInfo);

		return analyzeJobInfo;
	}

	private static void addAnalyzeJobInfoFromResponse(IngridDocument response,
			AnalyzeJobInfoBean analyzeJobInfo) {
		IngridDocument analyzeResult = MdekUtils.getResultFromResponse(response);
		List<ErrorReport> errorReports = new ArrayList<>();
		if (analyzeResult != null) {
			List<IngridDocument> errorReportDocList = (List<IngridDocument>)(List<?>)analyzeResult.getArrayList(MdekKeys.VALIDATION_RESULT);
			if (errorReportDocList != null) {
				for (IngridDocument errorReportDoc : errorReportDocList) {
					ErrorReport errorReport = new ErrorReport(
							errorReportDoc.getString(MdekKeys.VALIDATION_MESSAGE),
							errorReportDoc.getString(MdekKeys.VALIDATION_SOLUTION)); 
					errorReports.add(errorReport);
				}
			}
			analyzeJobInfo.setErrorReports(errorReports);

		} else {
			MdekErrorUtils.handleError(response);
		}
	}
	
	public static CodeListJobInfoBean extractCodeListInfoFromResponse(IngridDocument response) {
		CodeListJobInfoBean codeListJobInfo = new CodeListJobInfoBean();
		addGeneralJobInfoFromResponse(response, codeListJobInfo);
		return codeListJobInfo;
	}

	public static SNSUpdateJobInfoBean extractSNSUpdateJobInfoFromResponse(
			IngridDocument response) {
		SNSUpdateJobInfoBean jobInfo = new SNSUpdateJobInfoBean();
		addGeneralJobInfoFromResponse(response, jobInfo);
		addSNSUpdateJobInfoFromResponse(response, jobInfo);
		return jobInfo;
	}


	private static void addSNSUpdateJobInfoFromResponse(IngridDocument response, SNSUpdateJobInfoBean jobInfo) {
		IngridDocument snsResult = MdekUtils.getResultFromResponse(response);
		if (snsResult != null) {
			jobInfo.setNumProcessedEntities((Integer) snsResult.get(MdekKeys.JOBINFO_NUM_ENTITIES));
			jobInfo.setNumEntities((Integer) snsResult.get(MdekKeys.JOBINFO_TOTAL_NUM_ENTITIES));
			jobInfo.setDescription((String) snsResult.get(MdekKeys.JOBINFO_ENTITY_TYPE));

			List<Map<String, Object>> updateMessages = (List<Map<String, Object>>) snsResult.get(MdekKeys.JOBINFO_TERMS_UPDATED);

			List<SNSTopicUpdateResult> snsUpdateResults = new ArrayList<>();
			if (updateMessages != null) {
				for (Map<String, Object> updateMessage : updateMessages) {
					snsUpdateResults.add(new SNSTopicUpdateResult(updateMessage));
				}
			}

			jobInfo.setSnsUpdateResults(snsUpdateResults);

		} else {
			MdekErrorUtils.handleError(response);
		}
	}

	public static SNSLocationUpdateJobInfoBean extractSNSLocationUpdateJobInfoFromResponse(
			IngridDocument response, boolean extractObjectEntities) {
		SNSLocationUpdateJobInfoBean jobInfo = new SNSLocationUpdateJobInfoBean();
		addGeneralJobInfoFromResponse(response, jobInfo);
		addSNSLocationUpdateJobInfoFromResponse(response, jobInfo, extractObjectEntities);
		return jobInfo;
	}

	private static void addSNSLocationUpdateJobInfoFromResponse(IngridDocument response, SNSLocationUpdateJobInfoBean jobInfo, boolean extractObjectEntities) {
		IngridDocument snsResult = MdekUtils.getResultFromResponse(response);
		if (snsResult != null) {
			jobInfo.setNumProcessedEntities((Integer) snsResult.get(MdekKeys.JOBINFO_NUM_ENTITIES));
			jobInfo.setNumEntities((Integer) snsResult.get(MdekKeys.JOBINFO_TOTAL_NUM_ENTITIES));
			jobInfo.setDescription((String) snsResult.get(MdekKeys.JOBINFO_ENTITY_TYPE));

			List<Map<String, Object>> updateMessages = (List<Map<String, Object>>) snsResult.get(MdekKeys.JOBINFO_LOCATIONS_UPDATED);

			List<SNSLocationUpdateResult> snsUpdateResults = new ArrayList<>();
			if (updateMessages != null) {
				for (Map<String, Object> updateMessage : updateMessages) {
					snsUpdateResults.add(new SNSLocationUpdateResult(updateMessage, extractObjectEntities));
				}
			}

			jobInfo.setSnsUpdateResults(snsUpdateResults);

		} else {
			MdekErrorUtils.handleError(response);
		}
	}

	public static Long extractLastModifiedTimestampFromResponse(IngridDocument response) {
	    IngridDocument result = MdekUtils.getResultFromResponse(response);
	    return (Long) result.get(MdekKeys.LST_LAST_MODIFIED);
	}
}
