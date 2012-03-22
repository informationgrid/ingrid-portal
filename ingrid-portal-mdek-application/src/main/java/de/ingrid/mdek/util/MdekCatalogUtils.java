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
import de.ingrid.utils.udk.UtilsUDKCodeLists;
import de.ingrid.utils.udk.UtilsUDKCodeLists.ParseType;

public class MdekCatalogUtils {

	private final static Logger log = Logger.getLogger(MdekCatalogUtils.class);	

	//private static final String SYS_GUI_ID = "id";
	//private static final String SYS_GUI_MODE = "mode";
	
	private static XStream xstream = new XStream();


	public static Integer[] extractSysListIdsFromResponse(IngridDocument response) {
		IngridDocument result = MdekUtils.getResultFromResponse(response);
		if (result != null) {
			return (Integer[]) result.get(MdekKeys.SYS_LIST_IDS);

		} else {
			MdekErrorUtils.handleError(response);
			return null;
		}
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
			Map<Integer, List<String[]>> resultMap = new HashMap<Integer, List<String[]>>();
			Set<String> listKeys = (Set<String>) result.keySet();
			for (String listKey : listKeys) {
				IngridDocument listDocument = (IngridDocument) result.get(listKey);
				List<String[]> resultList = new ArrayList<String[]>();
				Integer listId = (Integer) listDocument.get(MdekKeys.LST_ID);

				Integer[] entryIds = (Integer[]) listDocument.get(MdekKeys.LST_ENTRY_IDS);
				String[] entryNames = (String[]) listDocument.get(MdekKeys.LST_ENTRY_NAMES);
				Integer defaultIndex = (Integer) listDocument.get(MdekKeys.LST_DEFAULT_ENTRY_INDEX);

				if (entryIds != null && entryNames != null) {
					for (int index = 0; index < entryIds.length; ++index) {
						boolean isDefault = defaultIndex != null ? defaultIndex == index : false;
						
						resultList.add( new String[] {
								entryNames[index],
								entryIds[index].toString(),
								isDefault ? "Y" : "N"
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
			return null;
		}
	}

	/** Remove metadata from syslist value if there is metadata. Returns cleared or unchanged value !*/
	public static String removeMetadataFromSysListEntry(Integer listId, String entryValue) {
		String retValue = entryValue;
		
		if (de.ingrid.mdek.MdekUtils.MdekSysList.OBJ_CONFORMITY_SPECIFICATION.getDbValue().equals(listId)) {
			retValue = UtilsUDKCodeLists.parseCodeListEntryName(entryValue, ParseType.DATE_AT_END)[0];
		}

		return retValue;
	}

	/** Prepare all syslists read from backend for displaying in syslist. All metadata in syslist values is removed ! */
	public static  Map<Integer, List<String[]>> removeMetadataFromSysLists(Map<Integer, List<String[]>> sysListsFromDB) {
		for (Integer listId : sysListsFromDB.keySet()) {
			List<String[]> clearedList = cloneSysListRemoveMetadata(listId, sysListsFromDB.get(listId));
			if (clearedList != null) {
				sysListsFromDB.put(listId, clearedList);
			}
		}
		
		return sysListsFromDB;
	}

	/** Removes metadata from syslist and returns "cleared" syslist to be used in selection lists.
	 * NOTICE: if the list has no metadata null is returned otherwise the list without metadata.
	 * @param listId list id determining whether list has metadata
	 * @param inList list from database maybe containing metadata
	 * @return null if no metadata in list, if syslist has metadata then cleared syslist is returned
	 */
	public static List<String[]> cloneSysListRemoveMetadata(Integer listId, List<String[]> inList) {
		List<String[]> returnList = null;

		ParseType parseType = null;
		if (de.ingrid.mdek.MdekUtils.MdekSysList.OBJ_CONFORMITY_SPECIFICATION.getDbValue().equals(listId)) {
			parseType = ParseType.DATE_AT_END;
		}
		
		if (parseType != null) {
			returnList = new ArrayList<String[]>();
			for (String[] entry : inList) {
				returnList.add(new String[] {
						UtilsUDKCodeLists.parseCodeListEntryName(entry[0], parseType)[0],
						entry[1],
						entry[2]
					});
			}			
		}

		return returnList;
	}

	public static String convertSysListsToXML(IngridDocument result) {
		Set<String> listKeys = (Set<String>) result.keySet();
		List<SysList> sysLists = new ArrayList<SysList>();

		for (String listKey : listKeys) {
			SysList sysList = new SysList();

			IngridDocument listDocument = (IngridDocument) result.get(listKey);
			Integer listId = (Integer) listDocument.get(MdekKeys.LST_ID);

			Integer[] entryIds = (Integer[]) listDocument.get(MdekKeys.LST_ENTRY_IDS);
			String[] entryNamesDe = (String[]) listDocument.get(MdekKeys.LST_ENTRY_NAMES_DE);
			String[] entryNamesEn = (String[]) listDocument.get(MdekKeys.LST_ENTRY_NAMES_EN);
			Integer defaultIndex = (Integer) listDocument.get(MdekKeys.LST_DEFAULT_ENTRY_INDEX);
			Boolean maintainable = (de.ingrid.mdek.MdekUtils.YES_INTEGER == ((Integer) listDocument.get(MdekKeys.LST_MAINTAINABLE)));

			sysList.setId(listId);
			sysList.setDeEntries(entryNamesDe);
			sysList.setEnEntries(entryNamesEn);
			sysList.setDefaultIndex(defaultIndex);
			sysList.setEntryIds(entryIds);
			sysList.setMaintainable(maintainable);

			sysLists.add(sysList);
		}

		xstream.alias("sysList", SysList.class);
		String xmlDoc = xstream.toXML(sysLists);
		return xmlDoc;
	}

	public static List<SysList> convertXMLToSysLists(String xmlDoc) {
		xstream.alias("sysList", SysList.class);
		List<SysList> sysLists = (List<SysList>) xstream.fromXML(xmlDoc);
		return sysLists;
	}

	/*public static List<Map<String, String>> extractSysGuisFromResponse(IngridDocument response) {
		IngridDocument result = MdekUtils.getResultFromResponse(response);
		List<Map<String, String>> sysGuiList = new ArrayList<Map<String,String>>();

		Set<Map.Entry<String, Map<String, Object>>> entrySet = result.entrySet();
		Iterator<Map.Entry<String, Map<String, Object>>> it = entrySet.iterator();

		while (it.hasNext()) {
			Map.Entry<String, Map<String, Object>> entry = it.next();
			Map<String, Object> guiEntry = entry.getValue();
			
			Map<String, String> res = new HashMap<String, String>();
			res.put(SYS_GUI_ID, (String) guiEntry.get(MdekKeys.SYS_GUI_ID));
			res.put(SYS_GUI_MODE, ((Integer) guiEntry.get(MdekKeys.SYS_GUI_BEHAVIOUR)).toString());

			sysGuiList.add(res);
		}
		
		return sysGuiList;
	}

	public static List<IngridDocument> convertFromSysGuiRepresentation(List<Map<String, String>> sysGuiList) {
		List<IngridDocument> result = new ArrayList<IngridDocument>();

		for (Map<String, String> sysGuiEntry : sysGuiList) {
			IngridDocument doc = new IngridDocument();
			doc.put(MdekKeys.SYS_GUI_ID, sysGuiEntry.get(SYS_GUI_ID));
			doc.put(MdekKeys.SYS_GUI_BEHAVIOUR, new Integer(sysGuiEntry.get(SYS_GUI_MODE)));

			result.add(doc);
		}
		return result;
	}*/

	public static List<GenericValueBean> extractSysGenericKeysFromResponse(IngridDocument response) {
		IngridDocument result = MdekUtils.getResultFromResponse(response);
		List<GenericValueBean> genericValueList = new ArrayList<GenericValueBean>();

		log.debug(result);

		for (Map.Entry<String, String> genericValueEntry : (Set<Map.Entry<String, String>>) result.entrySet()) {
			GenericValueBean genericValue = new GenericValueBean();
			genericValue.setKey(genericValueEntry.getKey());
			genericValue.setValue(genericValueEntry.getValue());
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
			//log.debug(result.getString(MdekKeys.LANGUAGE_SHORTCUT));
			
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

			IngridDocument modUserDoc = (IngridDocument) result.get(MdekKeys.MOD_USER);
			if (modUserDoc != null)
				resultCat.setModUuid((String) modUserDoc.get(MdekKeys.UUID));

			return resultCat;
		} else {
			MdekErrorUtils.handleError(response);
			return null;
		}
	}

	private static String getLanguageShort(Integer languageCode) {
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
		List<URLObjectReference> urlObjectReferences = new ArrayList<URLObjectReference>();
		if (urlRefDoc != null) {
			List<Map<String, Object>> urlResult = (List<Map<String, Object>>) urlRefDoc.get(MdekKeys.URL_RESULT);
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
					urlObjectReferences.add(urlObjectRef);
				}
			}
			urlJobInfo.setUrlObjectReferences(urlObjectReferences);

		} else {
			MdekErrorUtils.handleError(response);
		}
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
		List<Map<String, Object>> urlInfoList = new ArrayList<Map<String, Object>>();
		if (urlObjectReferences != null) {
			for (URLObjectReference ref : urlObjectReferences) {
				URLState urlState = ref.getUrlState();
				Map<String, Object> urlInfo = new HashMap<String, Object>();
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
		List<ErrorReport> errorReports = new ArrayList<ErrorReport>();
		if (analyzeResult != null) {
			List<IngridDocument> errorReportDocList = analyzeResult.getArrayList(MdekKeys.VALIDATION_RESULT);
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
		//addCodeListJobInfoFromResponse(response, codeListJobInfo);

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

			List<SNSTopicUpdateResult> snsUpdateResults = new ArrayList<SNSTopicUpdateResult>();
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

			List<SNSLocationUpdateResult> snsUpdateResults = new ArrayList<SNSLocationUpdateResult>();
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