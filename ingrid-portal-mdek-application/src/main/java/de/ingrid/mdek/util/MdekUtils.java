package de.ingrid.mdek.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import de.ingrid.mdek.MdekKeys;
import de.ingrid.mdek.MdekKeysSecurity;
import de.ingrid.mdek.beans.JobInfoBean;
import de.ingrid.mdek.beans.VersionInformation;
import de.ingrid.mdek.beans.query.AddressExtSearchParamsBean;
import de.ingrid.mdek.beans.query.AddressSearchResultBean;
import de.ingrid.mdek.beans.query.CSVSearchResultBean;
import de.ingrid.mdek.beans.query.ObjectExtSearchParamsBean;
import de.ingrid.mdek.beans.query.ObjectSearchResultBean;
import de.ingrid.mdek.beans.query.SearchResultBean;
import de.ingrid.mdek.beans.security.Group;
import de.ingrid.mdek.beans.security.User;
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

	private static String convertDateToTimestamp(Date date) {
		if (date == null) {
			return null;
		} else {
			return timestampFormatter.format(date);
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

	public static List<Group> extractSecurityGroupsFromResponse(IngridDocument response) {
		IngridDocument result = MdekUtils.getResultFromResponse(response);

		ArrayList<Group> groupList = new ArrayList<Group>();
		
		if (result != null) {
			List<IngridDocument> groups = (List<IngridDocument>) result.get(MdekKeysSecurity.GROUPS);
			for (IngridDocument group : groups) {
				Group g = new Group();
				g.setName((String) group.get(MdekKeys.NAME));
				g.setId((Long) group.get(MdekKeysSecurity.IDC_GROUP_ID));
				g.setLastEditor((String) group.get(MdekKeys.MOD_UUID));
				g.setCreationTime(convertTimestampToDate((String) group.get(MdekKeys.DATE_OF_CREATION)));
				g.setModificationTime(convertTimestampToDate((String) group.get(MdekKeys.DATE_OF_LAST_MODIFICATION)));
				groupList.add(g);
			}
		}

		return groupList;
	}

	public static Group extractSecurityGroupFromResponse(IngridDocument response) {
		IngridDocument result = MdekUtils.getResultFromResponse(response);

		Group group = new Group();
		if (result != null) {
			group.setName((String) result.get(MdekKeys.NAME));
			group.setId((Long) result.get(MdekKeysSecurity.IDC_GROUP_ID));
			group.setLastEditor((String) result.get(MdekKeys.MOD_UUID));
			group.setCreationTime(convertTimestampToDate((String) result.get(MdekKeys.DATE_OF_CREATION)));
			group.setModificationTime(convertTimestampToDate((String) result.get(MdekKeys.DATE_OF_LAST_MODIFICATION)));
		} else {
			MdekErrorUtils.handleError(response);
		}

		return group;
	}

	public static IngridDocument convertSecurityGroupToIngridDoc(Group group) {
		IngridDocument result = new IngridDocument();

		if (group == null) {
			return result;
		}

		result.put(MdekKeys.NAME, group.getName());
		result.put(MdekKeysSecurity.IDC_GROUP_ID, group.getId());

		return result;
	}

	public static User extractSecurityUserFromResponse(IngridDocument response) {
		IngridDocument result = MdekUtils.getResultFromResponse(response);

		User user = new User();
		if (result != null) {
			user.setId((Long) result.get(MdekKeysSecurity.IDC_USER_ID));
			user.setAddressUuid((String) result.get(MdekKeysSecurity.IDC_USER_ADDR_UUID));
			user.setGroupId((Long) result.get(MdekKeysSecurity.IDC_GROUP_ID));
			user.setRole((Integer) result.get(MdekKeysSecurity.IDC_ROLE));
			user.setParentUserId((Integer) result.get(MdekKeysSecurity.PARENT_IDC_USER_ID));

			// Detailed info
			user.setCreationTime(convertTimestampToDate((String) result.get(MdekKeys.DATE_OF_CREATION)));
			user.setModificationTime(convertTimestampToDate((String) result.get(MdekKeys.DATE_OF_LAST_MODIFICATION)));
			user.setLastEditor((String) result.get(MdekKeys.MOD_UUID));
		} else {
			MdekErrorUtils.handleError(response);
		}

		return user;
	}

	public static IngridDocument convertSecurityUserToIngridDoc(User user) {
		IngridDocument result = new IngridDocument();

		if (user == null) {
			return result;
		}

		result.put(MdekKeysSecurity.IDC_USER_ID, user.getId());
		result.put(MdekKeysSecurity.IDC_USER_ADDR_UUID, user.getAddressUuid());
		result.put(MdekKeysSecurity.IDC_GROUP_ID, user.getGroupId());
		result.put(MdekKeysSecurity.IDC_ROLE, user.getRole());
		result.put(MdekKeysSecurity.PARENT_IDC_USER_ID, user.getParentUserId());

		return result;
	}

	public static IngridDocument convertAddressExtSearchParamsToIngridDoc(AddressExtSearchParamsBean query) {
		IngridDocument result = new IngridDocument();
		
		if (query == null) {
			return result;
		}


		// TODO Add missing values when they're added to MdekUtils
		result.put(MdekKeys.QUERY_TERM, query.getQueryTerm());
		result.put(MdekKeys.RELATION, query.getRelation());
		result.put(MdekKeys.SEARCH_TYPE, query.getSearchType());
		result.put(MdekKeys.SEARCH_RANGE, query.getSearchRange());
		result.put(MdekKeys.STREET, query.getStreet());
		result.put(MdekKeys.POSTAL_CODE, query.getZipCode());
		result.put(MdekKeys.CITY, query.getCity());

		return result;
	}

	public static IngridDocument convertObjectExtSearchParamsToIngridDoc(ObjectExtSearchParamsBean query) {
		IngridDocument result = new IngridDocument();
		
		if (query == null) {
			return result;
		}

		result.put(MdekKeys.QUERY_TERM, query.getQueryTerm());
		result.put(MdekKeys.RELATION, query.getRelation());

		result.put(MdekKeys.OBJ_CLASSES, query.getObjClasses());

		ArrayList<IngridDocument> thesaurusTerms = new ArrayList<IngridDocument>();
		for (String snsId : query.getThesaurusTerms()) {
			IngridDocument snsTopic = new IngridDocument();
			snsTopic.put(MdekKeys.TERM_SNS_ID, snsId);
			thesaurusTerms.add(snsTopic);
		}
		result.put(MdekKeys.THESAURUS_TERMS, thesaurusTerms);
		result.put(MdekKeys.THESAURUS_RELATION, query.getThesaurusRelation());
		
		ArrayList<IngridDocument> geoThesaurusTerms = new ArrayList<IngridDocument>();
		for (String snsId : query.getGeoThesaurusTerms()) {
			IngridDocument snsTopic = new IngridDocument();
			snsTopic.put(MdekKeys.LOCATION_SNS_ID, snsId);
			geoThesaurusTerms.add(snsTopic);
		}
		result.put(MdekKeys.GEO_THESAURUS_TERMS, geoThesaurusTerms);
		result.put(MdekKeys.GEO_THESAURUS_RELATION, query.getGeoThesaurusRelation());

		result.put(MdekKeys.CUSTOM_LOCATION, query.getCustomLocation());

		result.put(MdekKeys.TIME_FROM, convertDateToTimestamp(query.getTimeBegin()));
		result.put(MdekKeys.TIME_TO, convertDateToTimestamp(query.getTimeEnd()));
		result.put(MdekKeys.TIME_AT, convertDateToTimestamp(query.getTimeAt()));

		result.put(MdekKeys.TIME_INTERSECT, query.getTimeIntersects());
		result.put(MdekKeys.TIME_CONTAINS, query.getTimeContains());
		
		return result;
	}
}
