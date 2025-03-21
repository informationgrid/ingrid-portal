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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.jsp.jstl.core.Config;

import org.apache.log4j.Logger;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;

import de.ingrid.mdek.EnumUtil;
import de.ingrid.mdek.MdekKeys;
import de.ingrid.mdek.MdekKeysSecurity;
import de.ingrid.mdek.MdekUtilsSecurity.IdcPermission;
import de.ingrid.mdek.MdekUtilsSecurity.IdcRole;
import de.ingrid.mdek.beans.JobInfoBean;
import de.ingrid.mdek.beans.VersionInformation;
import de.ingrid.mdek.beans.query.AddressExtSearchParamsBean;
import de.ingrid.mdek.beans.query.AddressSearchResultBean;
import de.ingrid.mdek.beans.query.CSVSearchResultBean;
import de.ingrid.mdek.beans.query.ObjectExtSearchParamsBean;
import de.ingrid.mdek.beans.query.ObjectSearchResultBean;
import de.ingrid.mdek.beans.query.SearchResultBean;
import de.ingrid.mdek.beans.query.SearchTermBean;
import de.ingrid.mdek.beans.query.ThesaurusStatisticsResultBean;
import de.ingrid.mdek.beans.security.Group;
import de.ingrid.mdek.beans.security.Permission;
import de.ingrid.mdek.beans.security.User;
import de.ingrid.mdek.job.repository.IJobRepository;
import de.ingrid.mdek.job.repository.Pair;
import de.ingrid.utils.IngridDocument;

public class MdekUtils {

	private static final Logger log = Logger.getLogger(MdekUtils.class);

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
			return new ArrayList<>();
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

	private static final SimpleDateFormat timestampFormatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");

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

		List<VersionInformation> verList = new ArrayList<>();

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
			
			// check whether we have version of IGC schema and add if present !
			String igcVersion = extractIGCVersionFromResponse(response);
			if (igcVersion != null) {
				v = new VersionInformation();
				v.setName("IGC version (schema in database)");
				v.setVersion(igcVersion);
				verList.add(v);
			}

		} else {
			MdekErrorUtils.handleError(response);
		}
		return verList;		
	}

	/** Return IGC version (schema in database) from backend response. Returns null if not found. */
	public static String extractIGCVersionFromResponse(IngridDocument response) {
		IngridDocument result = MdekUtils.getResultFromResponse(response);

		String igcVersion = null;

		if (result != null) {
			String[] sysValues = (String[]) result.get(MdekKeys.SYS_GENERIC_KEY_VALUES);
			if (sysValues != null && sysValues.length > 0) {
				igcVersion = sysValues[0];
			}
		} else {
			MdekErrorUtils.handleError(response);
		}
		
		return igcVersion;		
	}

	public static JobInfoBean extractJobInfoFromResponse(IngridDocument response) {
		IngridDocument result = MdekUtils.getResultFromResponse(response);
		if (result != null) {
			JobInfoBean job = new JobInfoBean();
			job.setDescription(result.getString(MdekKeys.RUNNINGJOB_TYPE));
			job.setNumEntities((Integer) result.get(MdekKeys.RUNNINGJOB_NUMBER_TOTAL_ENTITIES));
			job.setNumProcessedEntities((Integer) result.get(MdekKeys.RUNNINGJOB_NUMBER_PROCESSED_ENTITIES));
			job.setNumProcessedObjects((Integer) result.get(MdekKeys.RUNNINGJOB_NUMBER_PROCESSED_OBJECTS));
			job.setNumProcessedAddresses((Integer) result.get(MdekKeys.RUNNINGJOB_NUMBER_PROCESSED_ADDRESSES));
			job.setNumObjects((Integer) result.get(MdekKeys.RUNNINGJOB_NUMBER_TOTAL_OBJECTS));
			job.setNumAddresses((Integer) result.get(MdekKeys.RUNNINGJOB_NUMBER_TOTAL_ADDRESSES));
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
			searchResult.setTotalNumHits((Long) result.get(MdekKeys.TOTAL_NUM));
			searchResult.setData((byte[]) result.get(MdekKeys.CSV_RESULT));
		} else {
			MdekErrorUtils.handleError(response);
		}

		return searchResult;
	}

	public static List<Group> extractSecurityGroupsFromResponse(IngridDocument response) {
		IngridDocument result = MdekUtils.getResultFromResponse(response);

		List<Group> groupList = new ArrayList<>();
		
		if (result != null) {
			List<IngridDocument> groups = (List<IngridDocument>) result.get(MdekKeysSecurity.GROUPS);
			for (IngridDocument group : groups) {
				Group g = new Group();
				g.setName((String) group.get(MdekKeys.NAME));
				g.setId((Long) group.get(MdekKeysSecurity.ID));
				IngridDocument lastEditorDoc = (IngridDocument) group.get(MdekKeys.MOD_USER);
				if (lastEditorDoc != null)
					g.setLastEditor((String) lastEditorDoc.get(MdekKeys.UUID));
				g.setCreationTime(convertTimestampToDate((String) group.get(MdekKeys.DATE_OF_CREATION)));
				g.setModificationTime(convertTimestampToDate((String) group.get(MdekKeys.DATE_OF_LAST_MODIFICATION)));				

				g.setObjectPermissions(getObjectPermissions((ArrayList<IngridDocument>) result.get(MdekKeysSecurity.IDC_OBJECT_PERMISSIONS)));
				g.setAddressPermissions(getAddressPermissions((ArrayList<IngridDocument>) result.get(MdekKeysSecurity.IDC_ADDRESS_PERMISSIONS)));
				g.setGroupPermissions(getGroupPermissions((ArrayList<IngridDocument>) result.get(MdekKeysSecurity.IDC_USER_PERMISSIONS)));

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
			group.setId((Long) result.get(MdekKeysSecurity.ID));
			IngridDocument lastEditorDoc = (IngridDocument) result.get(MdekKeys.MOD_USER);
			if (lastEditorDoc != null)
				group.setLastEditor((String) lastEditorDoc.get(MdekKeys.UUID));
			group.setCreationTime(convertTimestampToDate((String) result.get(MdekKeys.DATE_OF_CREATION)));
			group.setModificationTime(convertTimestampToDate((String) result.get(MdekKeys.DATE_OF_LAST_MODIFICATION)));

			group.setObjectPermissions(getObjectPermissions((ArrayList<IngridDocument>) result.get(MdekKeysSecurity.IDC_OBJECT_PERMISSIONS)));
			group.setAddressPermissions(getAddressPermissions((ArrayList<IngridDocument>) result.get(MdekKeysSecurity.IDC_ADDRESS_PERMISSIONS)));
			group.setGroupPermissions(getGroupPermissions((ArrayList<IngridDocument>) result.get(MdekKeysSecurity.IDC_USER_PERMISSIONS)));

		} else {
			MdekErrorUtils.handleError(response);
		}

		return group;
	}

	public static void checkForErrors(IngridDocument response) {
		IngridDocument result = MdekUtils.getResultFromResponse(response);
		if (result == null) {
			MdekErrorUtils.handleError(response);
		}
	}

	private static List<Permission> getAddressPermissions(List<IngridDocument> docList) {
		List<Permission> resultList = new ArrayList<>();

		if (docList == null) {
			return resultList;
		}

		for (IngridDocument doc : docList) {
			Permission p = new Permission();
			p.setUuid((String) doc.get(MdekKeys.UUID));
			String permissionType = (String) doc.get(MdekKeysSecurity.IDC_PERMISSION);
			p.setPermission(EnumUtil.mapDatabaseToEnumConst(IdcPermission.class, permissionType));
			p.setAddress(MdekAddressUtils.extractSingleAddress(doc));
			resultList.add(p);
		}

		return resultList;
	}
	
	private static List<Permission> getObjectPermissions(List<IngridDocument> docList) {
		List<Permission> resultList = new ArrayList<>();

		if (docList == null) {
			return resultList;
		}

		for (IngridDocument doc : docList) {
			Permission p = new Permission();
			p.setUuid((String) doc.get(MdekKeys.UUID));
			String permissionType = (String) doc.get(MdekKeysSecurity.IDC_PERMISSION);
			p.setPermission(EnumUtil.mapDatabaseToEnumConst(IdcPermission.class, permissionType));
			p.setObject(MdekObjectUtils.extractSingleObject(doc));
			resultList.add(p);
		}

		return resultList;
	}

    /**
     * Extract user permissions from response document. It expects to find the permissions as list of
     * documents under the key 'idc-permissions'. 
     * 
     * @param response
     * @return A List of permissions. If no permission can be found, the list is empty.
     */
    public static List<Permission> extractUserPermissionsFromResponse(IngridDocument response) {
        List<Permission> resultList = new ArrayList<>();
        IngridDocument result = MdekUtils.getResultFromResponse(response);
        if(result != null) {
            List<IngridDocument> permissions = (List<IngridDocument>) result.get(MdekKeysSecurity.IDC_PERMISSIONS);
            if (permissions == null) {
                return resultList;
            }
    
            for (IngridDocument doc : permissions) {
                Permission p = new Permission();
                String permissionType = (String) doc.get(MdekKeysSecurity.IDC_PERMISSION);
                p.setPermission(EnumUtil.mapDatabaseToEnumConst(IdcPermission.class, permissionType));
                resultList.add(p);
            }
        }
        return resultList;
    }
	
	
	private static List<IdcPermission> getGroupPermissions(List<IngridDocument> docList) {
		List<IdcPermission> resultList = new ArrayList<>();

		if (docList == null) {
			return resultList;
		}

		for (IngridDocument doc : docList) {
			String permDbVal = (String) doc.get(MdekKeysSecurity.IDC_PERMISSION);
			IdcPermission userPermission = EnumUtil.mapDatabaseToEnumConst(IdcPermission.class, permDbVal);
			resultList.add(userPermission);
		}

		return resultList;
	}

	private static List<IngridDocument> convertGroupPermissionsToIngridDocs(List<IdcPermission> pList) {
		List<IngridDocument> resultList = new ArrayList<>();

		if (pList == null) {
			return resultList;
		}

		for (IdcPermission perm : pList) {
			IngridDocument doc = new IngridDocument();
			doc.put(MdekKeysSecurity.IDC_PERMISSION, perm.getDbValue());
			resultList.add(doc);
		}

		return resultList;
	}
	
	private static List<IngridDocument> convertPermissionsToIngridDocs(List<Permission> pList) {
		List<IngridDocument> resultList = new ArrayList<>();

		if (pList == null) {
			return resultList;
		}

		for (Permission perm : pList) {
			IngridDocument doc = new IngridDocument();
			doc.put(MdekKeys.UUID, perm.getUuid());
			doc.put(MdekKeysSecurity.IDC_PERMISSION, perm.getPermission().getDbValue());

			resultList.add(doc);
		}

		return resultList;
	}

	public static IngridDocument convertSecurityGroupToIngridDoc(Group group) {
		IngridDocument result = new IngridDocument();

		if (group == null) {
			return result;
		}

		result.put(MdekKeys.NAME, group.getName());
		result.put(MdekKeysSecurity.ID, group.getId());

		result.put(MdekKeysSecurity.IDC_ADDRESS_PERMISSIONS, convertPermissionsToIngridDocs(group.getAddressPermissions()));
		result.put(MdekKeysSecurity.IDC_OBJECT_PERMISSIONS, convertPermissionsToIngridDocs(group.getObjectPermissions()));
		result.put(MdekKeysSecurity.IDC_USER_PERMISSIONS, convertGroupPermissionsToIngridDocs(group.getGroupPermissions()));

		return result;
	}

	public static User extractSecurityUserFromResponse(IngridDocument response) {
		IngridDocument result = MdekUtils.getResultFromResponse(response);

		User user = new User();
		if (result != null) {
			user.setId((Long) result.get(MdekKeysSecurity.IDC_USER_ID));
			user.setAddressUuid((String) result.get(MdekKeysSecurity.IDC_USER_ADDR_UUID));
			user.setGroupIds(Arrays.asList((Long[]) result.get(MdekKeysSecurity.IDC_GROUP_IDS)));
			Integer role = (Integer) result.get(MdekKeysSecurity.IDC_ROLE);
			IdcRole idcRole = EnumUtil.mapDatabaseToEnumConst(IdcRole.class, role);

			user.setRole(role);
			user.setRoleName(idcRole.toString());
			user.setParentUserId((Long) result.get(MdekKeysSecurity.PARENT_IDC_USER_ID));
			user.setHasChildren((Boolean) result.get(MdekKeys.HAS_CHILD));

			// Detailed info
			user.setCreationTime(convertTimestampToDate((String) result.get(MdekKeys.DATE_OF_CREATION)));
			user.setModificationTime(convertTimestampToDate((String) result.get(MdekKeys.DATE_OF_LAST_MODIFICATION)));
			IngridDocument lastEditorDoc = (IngridDocument) result.get(MdekKeys.MOD_USER);
			if (lastEditorDoc != null)
				user.setLastEditor((String) lastEditorDoc.get(MdekKeys.UUID));

			user.setAddress(MdekAddressUtils.extractSingleAddressFromResponse(response));

		} else {
			MdekErrorUtils.handleError(response);
		}

		return user;
	}

	public static List<User> extractSecurityUsersFromResponse(IngridDocument response) {
		IngridDocument result = MdekUtils.getResultFromResponse(response);

		List<User> userList = new ArrayList<>();
		if (result != null) {
			List<IngridDocument> users = (List<IngridDocument>) result.get(MdekKeysSecurity.IDC_USERS);
			for (IngridDocument user : users) {
				User u = new User();
				u.setParentUserId((Long) user.get(MdekKeysSecurity.PARENT_IDC_USER_ID));
				u.setGroupIds(Arrays.asList((Long[]) user.get(MdekKeysSecurity.IDC_GROUP_IDS)));
				u.setHasChildren((Boolean) user.get(MdekKeys.HAS_CHILD));
				u.setId((Long) user.get(MdekKeysSecurity.IDC_USER_ID));
				u.setAddressUuid((String) user.get(MdekKeysSecurity.IDC_USER_ADDR_UUID));
				Integer role = (Integer) user.get(MdekKeysSecurity.IDC_ROLE);
				IdcRole idcRole = EnumUtil.mapDatabaseToEnumConst(IdcRole.class, role);

				u.setRole(role);
				u.setRoleName(idcRole.toString());

				// Extract optional idc permissions
				List<IngridDocument> idcPermissions = (List<IngridDocument>) user.get(MdekKeysSecurity.IDC_PERMISSIONS);
				if (idcPermissions != null) {
					List<IdcPermission> pList = new ArrayList<>();
					for (IngridDocument permissionDoc : idcPermissions) {
						String pStr = (String) permissionDoc.get(MdekKeysSecurity.IDC_PERMISSION);
						pList.add(EnumUtil.mapDatabaseToEnumConst(IdcPermission.class, pStr));
					}
					u.setPermissions(pList);
				}

				u.setAddress(MdekAddressUtils.extractSingleAddress(user));

				userList.add(u);
			}

		} else {
			MdekErrorUtils.handleError(response);
		}

		return userList;
	}

	
	public static IngridDocument convertSecurityUserToIngridDoc(User user) {
		IngridDocument result = new IngridDocument();

		if (user == null) {
			return result;
		}

		// if user has address data included then first map this one (and user data afterwards !)
		if (user.getAddress() != null) {
			result = MdekAddressUtils.convertFromAddressRepresentation(user.getAddress());
		}

		result.put(MdekKeysSecurity.IDC_USER_ID, user.getId());
		result.put(MdekKeysSecurity.IDC_USER_ADDR_UUID, user.getAddressUuid());
		List<Long> groupIds = user.getGroupIds();
		result.put(MdekKeysSecurity.IDC_GROUP_IDS, groupIds.toArray(new Long[groupIds.size()]));
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
		result.put(MdekKeys.SEARCH_TYPE, query.getSearchType());

		result.put(MdekKeys.OBJ_CLASSES, query.getObjClasses());

		List<IngridDocument> thesaurusTerms = new ArrayList<>();
		for (String snsId : query.getThesaurusTerms()) {
			IngridDocument snsTopic = new IngridDocument();
			snsTopic.put(MdekKeys.TERM_SNS_ID, snsId);
			thesaurusTerms.add(snsTopic);
		}
		result.put(MdekKeys.THESAURUS_TERMS, thesaurusTerms);
		result.put(MdekKeys.THESAURUS_RELATION, query.getThesaurusRelation());
		
		List<IngridDocument> geoThesaurusTerms = new ArrayList<>();
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

	public static ThesaurusStatisticsResultBean extractThesaurusStatistics(IngridDocument result) {
		ThesaurusStatisticsResultBean res = new ThesaurusStatisticsResultBean();
		List<SearchTermBean> searchTermList = new ArrayList<>();

		for (IngridDocument doc : (List<IngridDocument>) result.get(MdekKeys.STATISTICS_SEARCHTERM_LIST)) {
			SearchTermBean searchTerm = new SearchTermBean();
			searchTerm.setTerm((String) doc.get(MdekKeys.TERM_NAME));
			searchTerm.setNumOccurences((Long) doc.get(MdekKeys.TOTAL_NUM));
			searchTermList.add(searchTerm);
		}

		res.setNumHitsTotal((Long) result.get(MdekKeys.TOTAL_NUM_PAGING));
		res.setNumTermsTotal((Long) result.get(MdekKeys.TOTAL_NUM));
		res.setSearchTermList(searchTermList);

		return res;
	}
	
	public static Locale getLocaleFromSession() {
        WebContext wctx = WebContextFactory.get();
        return (Locale)wctx.getSession().getAttribute(Config.FMT_LOCALE + ".session");
	}
}
