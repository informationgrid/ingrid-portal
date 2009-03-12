package de.ingrid.mdek.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import de.ingrid.mdek.EnumUtil;
import de.ingrid.mdek.MdekKeys;
import de.ingrid.mdek.MdekUtils.AdditionalFieldType;
import de.ingrid.mdek.beans.AdditionalFieldBean;
import de.ingrid.mdek.beans.AnalyzeJobInfoBean;
import de.ingrid.mdek.beans.CatalogBean;
import de.ingrid.mdek.beans.CodeListJobInfoBean;
import de.ingrid.mdek.beans.ExportJobInfoBean;
import de.ingrid.mdek.beans.GenericValueBean;
import de.ingrid.mdek.beans.JobInfoBean;
import de.ingrid.mdek.beans.URLJobInfoBean;
import de.ingrid.mdek.beans.AdditionalFieldBean.Type;
import de.ingrid.mdek.beans.JobInfoBean.EntityType;
import de.ingrid.mdek.beans.object.LocationBean;
import de.ingrid.mdek.caller.MdekCaller;
import de.ingrid.mdek.quartz.jobs.util.URLObjectReference;
import de.ingrid.mdek.quartz.jobs.util.URLState;
import de.ingrid.mdek.quartz.jobs.util.URLState.State;
import de.ingrid.mdek.services.catalog.dbconsistency.ErrorReport;
import de.ingrid.utils.IngridDocument;

public class MdekCatalogUtils {

	private final static Logger log = Logger.getLogger(MdekCatalogUtils.class);	

	private static final String SYS_GUI_ID = "id";
	private static final String SYS_GUI_MODE = "mode";

	public static Integer[] extractSysListIdsFromResponse(IngridDocument response) {
		IngridDocument result = MdekUtils.getResultFromResponse(response);
		if (result != null) {
			return (Integer[]) result.get(MdekKeys.SYS_LIST_IDS);

		} else {
			MdekErrorUtils.handleError(response);
			return null;
		}
	}

	// Returns a map containing the mapped sysLists from response:
	// { listId1 : [ [ entry1Name, entry1Id, entry1Default ],
	//               [ entry2Name, entry2Id, entry2Default ]
	//             ],
	//   listId2 : ...
	// }
	public static Map<Integer, List<String[]>> extractSysListFromResponse(IngridDocument response) {
		IngridDocument result = MdekUtils.getResultFromResponse(response);
		if (result != null) {
			Map<Integer, List<String[]>> resultMap = new HashMap<Integer, List<String[]>>();
			Set<String> listKeys = (Set<String>) result.keySet();
			for (String listKey : listKeys) {
				IngridDocument listDocument = (IngridDocument) result.get(listKey);
				ArrayList<String[]> resultList = new ArrayList<String[]>();
				Integer listId = (Integer) listDocument.get(MdekKeys.LST_ID);

				Integer[] entryIds = (Integer[]) listDocument.get(MdekKeys.LST_ENTRY_IDS);
				String[] entryNames = (String[]) (listDocument.get(MdekKeys.LST_ENTRY_NAMES_DE) != null ? listDocument.get(MdekKeys.LST_ENTRY_NAMES_DE) : listDocument.get(MdekKeys.LST_ENTRY_NAMES_EN));
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

	public static List<AdditionalFieldBean> extractSysAdditionalFieldsFromResponse(IngridDocument response) {
		IngridDocument result = MdekUtils.getResultFromResponse(response);
		if (result != null) {
			List<AdditionalFieldBean> resultList = new ArrayList<AdditionalFieldBean>();
			for (String key : (Set<String>) result.keySet()) {
				IngridDocument fieldDefinition = (IngridDocument) result.get(key);
				Long fieldId = fieldDefinition.getLong(MdekKeys.SYS_ADDITIONAL_FIELD_IDENTIFIER);
				Integer fieldSize = fieldDefinition.getInt(MdekKeys.SYS_ADDITIONAL_FIELD_LENGTH);
				String fieldName = fieldDefinition.getString(MdekKeys.SYS_ADDITIONAL_FIELD_NAME);
				String fieldTypeStr = fieldDefinition.getString(MdekKeys.SYS_ADDITIONAL_FIELD_TYPE);
				AdditionalFieldType dbFieldType = EnumUtil.mapDatabaseToEnumConst(AdditionalFieldType.class, fieldTypeStr);
				Type fieldType = (AdditionalFieldType.LIST == dbFieldType)? Type.LIST : Type.TEXT;

				String listLanguage = null;
				String[] listEntries = null;
				if (Type.LIST == fieldType) {
					for (String fieldDefinitionKey : (Set<String>) fieldDefinition.keySet()) {
						if (fieldDefinitionKey.startsWith(MdekKeys.SYS_ADDITIONAL_FIELD_LIST_ITEMS_KEY_PREFIX)) {
							listLanguage = fieldDefinitionKey.substring(MdekKeys.SYS_ADDITIONAL_FIELD_LIST_ITEMS_KEY_PREFIX.length());
							listEntries = (String[]) fieldDefinition.get(fieldDefinitionKey);
							break;
						}
					}
				}

				AdditionalFieldBean additionalField = new AdditionalFieldBean();
				additionalField.setId(fieldId);
				additionalField.setName(fieldName);
				additionalField.setType(fieldType);
				additionalField.setSize(fieldSize);
				additionalField.setListLanguage(listLanguage);
				List<String> entries = (listEntries != null)? Arrays.asList(listEntries) : null;
				additionalField.setListEntries(entries);
				resultList.add(additionalField);
			}
			return resultList;

		} else {
			MdekErrorUtils.handleError(response);
			return null;
		}
	}

	public static List<IngridDocument> convertSysAdditionalFields(List<AdditionalFieldBean> additionalFields) {
		List<IngridDocument> resultDocs = new ArrayList<IngridDocument>();

		if (additionalFields != null) {
			for (AdditionalFieldBean additionalField : additionalFields) {
				IngridDocument additionalFieldDoc = new IngridDocument();
				additionalFieldDoc.put(MdekKeys.SYS_ADDITIONAL_FIELD_IDENTIFIER, additionalField.getId());
				additionalFieldDoc.putInt(MdekKeys.SYS_ADDITIONAL_FIELD_LENGTH, additionalField.getSize());
				additionalFieldDoc.put(MdekKeys.SYS_ADDITIONAL_FIELD_NAME, additionalField.getName());
				String fieldType = additionalField.getType() == Type.LIST ? AdditionalFieldType.LIST.getDbValue() : AdditionalFieldType.TEXT.getDbValue();
				additionalFieldDoc.put(MdekKeys.SYS_ADDITIONAL_FIELD_TYPE, fieldType);

				if (Type.LIST == additionalField.getType()) {
					String listKey = MdekKeys.SYS_ADDITIONAL_FIELD_LIST_ITEMS_KEY_PREFIX + additionalField.getListLanguage();
					if (additionalField.getListEntries() != null) {
						String[] entries = additionalField.getListEntries().toArray(new String[0]);
						additionalFieldDoc.put(listKey, entries);
					}
					additionalFieldDoc.put(MdekKeys.SYS_ADDITIONAL_FIELD_LIST_TYPE, "Z");
				}
				resultDocs.add(additionalFieldDoc);
			}
		}

		return resultDocs;
	}

	public static void addIdsToSysAdditionalFields(List<AdditionalFieldBean> additionalFields, IngridDocument response) {
		IngridDocument result = MdekUtils.getResultFromResponse(response);
		if (result != null) {
			Long[] additionalFieldIds = (Long[]) result.get(MdekKeys.SYS_ADDITIONAL_FIELD_IDS);
			if (additionalFields != null && additionalFieldIds != null) {
				if (additionalFields.size() != additionalFieldIds.length) {
					log.error("returned number of IDs does not match the number of additional fields. Can't merge IDs!");
					return;
				}

				for (int index = 0; index < additionalFields.size(); ++index) {
					AdditionalFieldBean additionalField = additionalFields.get(index);
					if (additionalField.getId() == null) {
						// New Id has to be stored in the additional field
						additionalField.setId(additionalFieldIds[index]);

					} else if (additionalField.getId().longValue() != additionalFieldIds[index]) {
						log.error("returned additional field ID does not match the current additional field ID!");
					}
				}
			}

		} else {
			MdekErrorUtils.handleError(response);
		}
	}

	public static List<Map<String, String>> extractSysGuisFromResponse(IngridDocument response) {
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
	}

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
	
			resultCat.setUuid(result.getString(MdekKeys.UUID));
			resultCat.setCatalogName(result.getString(MdekKeys.CATALOG_NAME));
			resultCat.setPartnerName(result.getString(MdekKeys.PARTNER_NAME));
			resultCat.setProviderName(result.getString(MdekKeys.PROVIDER_NAME));
			resultCat.setCountry(result.getString(MdekKeys.COUNTRY));
			resultCat.setLanguage(result.getString(MdekKeys.LANGUAGE));
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

			} else if (jobInfoDoc.get(MdekKeys.JOBINFO_NUM_ADDRESSES) != null) {
				jobInfo.setNumProcessedEntities(jobInfoDoc.getInt(MdekKeys.JOBINFO_NUM_ADDRESSES));
				jobInfo.setNumEntities(jobInfoDoc.getInt(MdekKeys.JOBINFO_TOTAL_NUM_ADDRESSES));
				jobInfo.setEntityType(EntityType.ADDRESS);
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

	public static IngridDocument convertFromCatalogRepresentation(CatalogBean cat) {
		IngridDocument catDoc = new IngridDocument();

		catDoc.put(MdekKeys.UUID, cat.getUuid());
		catDoc.put(MdekKeys.CATALOG_NAME, cat.getCatalogName());
		catDoc.put(MdekKeys.PARTNER_NAME, cat.getPartnerName());
		catDoc.put(MdekKeys.PROVIDER_NAME, cat.getProviderName());
		catDoc.put(MdekKeys.COUNTRY, cat.getCountry());
		catDoc.put(MdekKeys.LANGUAGE, cat.getLanguage());
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

	
	/*
	private static void addCodeListJobInfoFromResponse(IngridDocument response,
			AnalyzeJobInfoBean analyzeJobInfo) {
		IngridDocument codeListResult = MdekUtils.getResultFromResponse(response);
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
	}*/
}
