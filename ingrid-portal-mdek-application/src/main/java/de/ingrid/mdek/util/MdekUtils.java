package de.ingrid.mdek.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import de.ingrid.mdek.MdekKeys;
import de.ingrid.mdek.beans.AddressSearchResultBean;
import de.ingrid.mdek.beans.CSVSearchResultBean;
import de.ingrid.mdek.beans.JobInfoBean;
import de.ingrid.mdek.beans.ObjectSearchResultBean;
import de.ingrid.mdek.beans.SearchResultBean;
import de.ingrid.mdek.beans.VersionInformation;
import de.ingrid.mdek.job.repository.IJobRepository;
import de.ingrid.mdek.job.repository.Pair;
import de.ingrid.utils.IngridDocument;

public class MdekUtils {

	private final static Logger log = Logger.getLogger(MdekUtils.class);

	public static IngridDocument extractAdditionalInformationFromResponse(IngridDocument response) {
		IngridDocument result = getResultFromResponse(response);
		if (result != null) {
			return result;
		} else {
			MdekErrorUtils.handleError(response);
			return null;
		}
	}

	public static List<String> extractPathFromResponse(IngridDocument response) {
		IngridDocument result = getResultFromResponse(response);
		if (result != null) {
			List<String> uuidList = (List<String>) result.get(MdekKeys.PATH);
			return uuidList;
		} else {
			MdekErrorUtils.handleError(response);
			return null;
		}
	}

	public static IngridDocument getResultFromResponse(IngridDocument mdekResponse) {
		IngridDocument result = null;

		boolean success = mdekResponse.getBoolean(IJobRepository.JOB_INVOKE_SUCCESS);
		if (success) {
			List pairList = (List) mdekResponse.get(IJobRepository.JOB_INVOKE_RESULTS);
			Pair pair = (Pair) pairList.get(0);
			result = (IngridDocument) pair.getValue();
		}

		return result;
	}

	private final static SimpleDateFormat timestampFormatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");

	public static Date convertTimestampToDate(String timeStamp) {
		if (timeStamp != null && timeStamp.length() != 0) {
			try {
				Date date = timestampFormatter.parse(timeStamp);
				return date;
			} catch (Exception ex){
				log.debug("Problems parsing timestamp from database: " + timeStamp, ex);
				return null;
			}
		} else {
			return null;
		}
	}

	public static List<VersionInformation> extractVersionInformationFromResponse(IngridDocument response) {
		IngridDocument result = MdekUtils.getResultFromResponse(response);

		ArrayList<VersionInformation> verList = new ArrayList<VersionInformation>();

		if (result != null) {
			// API Version
			VersionInformation v = new VersionInformation();
			v.setName(result.getString(MdekKeys.API_BUILD_NAME));
			v.setVersion(result.getString(MdekKeys.API_BUILD_VERSION));
			v.setBuildNumber(result.getString(MdekKeys.API_BUILD_NUMBER));
			try {
				v.setTimeStamp(new Date(Long.valueOf(result.getString(MdekKeys.API_BUILD_TIMESTAMP))));
			} catch (NumberFormatException e) {
				v.setTimeStamp(new Date());
			}
			verList.add(v);
	
			// Server Version
			v = new VersionInformation();
			v.setName(result.getString(MdekKeys.SERVER_BUILD_NAME));
			v.setVersion(result.getString(MdekKeys.SERVER_BUILD_VERSION));
			v.setBuildNumber(result.getString(MdekKeys.SERVER_BUILD_NUMBER));
			try {
				v.setTimeStamp(new Date(Long.valueOf(result.getString(MdekKeys.SERVER_BUILD_TIMESTAMP))));
			} catch (NumberFormatException e) {
				v.setTimeStamp(new Date());
			}
			verList.add(v);

		} else {
			MdekErrorUtils.handleError(response);
			return null;
		}
		return verList;		
	}

	public static JobInfoBean extractJobInfoFromResponse(IngridDocument response) {
		IngridDocument result = MdekUtils.getResultFromResponse(response);

		if (result != null) {
			JobInfoBean job = new JobInfoBean();
			job.setDescription(result.getString(MdekKeys.RUNNINGJOB_DESCRIPTION));
			job.setNumEntities((Integer) result.get(MdekKeys.RUNNINGJOB_NUMBER_TOTAL_ENTITIES));
			job.setNumProcessedEntities((Integer) result.get(MdekKeys.RUNNINGJOB_NUMBER_PROCESSED_ENTITIES));
			return job;
		} else {
			MdekErrorUtils.handleError(response);
			return null;
		}		
	}

	public static SearchResultBean extractSearchResultsFromResponse(IngridDocument response) {
		ObjectSearchResultBean objResult = MdekObjectUtils.extractObjectSearchResultsFromResponse(response);
		AddressSearchResultBean adrResult = MdekAddressUtils.extractAddressSearchResultsFromResponse(response);
		CSVSearchResultBean csvResult = extractCSVSearchResultsFromResponse(response);

		SearchResultBean searchResult = new SearchResultBean();
		searchResult.setObjectSearchResult(objResult);
		searchResult.setAddressSearchResult(adrResult);
		searchResult.setCsvSearchResult(csvResult);
		
		return searchResult;
	}

	private static CSVSearchResultBean extractCSVSearchResultsFromResponse(IngridDocument response) {
		IngridDocument result = MdekUtils.getResultFromResponse(response);

		CSVSearchResultBean searchResult = new CSVSearchResultBean();

		if (result != null) {
//			searchResult.setNumHits(((Long) result.get(MdekKeys.SEARCH_TOTAL_NUM_HITS)));
			searchResult.setTotalNumHits((Long) result.get(MdekKeys.SEARCH_TOTAL_NUM_HITS));
			searchResult.setData((String) result.get(MdekKeys.CSV_RESULT));
		} else {
			MdekErrorUtils.handleError(response);
		}

		return searchResult;
	}
}
