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
package de.ingrid.mdek.dwr.services;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.directwebremoting.io.FileTransfer;

import au.com.bytecode.opencsv.CSVWriter;
import de.ingrid.codelists.CodeListService;
import de.ingrid.mdek.MdekKeys;
import de.ingrid.mdek.MdekKeysSecurity;
import de.ingrid.mdek.MdekUtils.CsvRequestType;
import de.ingrid.mdek.beans.AnalyzeJobInfoBean;
import de.ingrid.mdek.beans.JobInfoBean;
import de.ingrid.mdek.beans.SNSLocationUpdateJobInfoBean;
import de.ingrid.mdek.beans.SNSUpdateJobInfoBean;
import de.ingrid.mdek.beans.URLJobInfoBean;
import de.ingrid.mdek.beans.address.MdekAddressBean;
import de.ingrid.mdek.beans.object.MdekDataBean;
import de.ingrid.mdek.caller.IMdekCallerCatalog;
import de.ingrid.mdek.caller.IMdekCallerQuery;
import de.ingrid.mdek.caller.IMdekCallerSecurity;
import de.ingrid.mdek.caller.IMdekClientCaller;
import de.ingrid.mdek.handler.CatalogRequestHandler;
import de.ingrid.mdek.handler.ConnectionFacade;
import de.ingrid.mdek.job.MdekException;
import de.ingrid.mdek.quartz.MdekJobHandler;
import de.ingrid.mdek.quartz.MdekJobHandler.JobType;
import de.ingrid.mdek.quartz.jobs.util.URLState;
import de.ingrid.mdek.util.MdekCatalogUtils;
import de.ingrid.mdek.util.MdekErrorUtils;
import de.ingrid.mdek.util.MdekSecurityUtils;
import de.ingrid.mdek.util.MdekUtils;
import de.ingrid.utils.IngridDocument;

public class CatalogManagementServiceImpl {

	private static final Logger log = Logger
			.getLogger(CatalogManagementServiceImpl.class);

	private MdekJobHandler mdekJobHandler;

	private CatalogRequestHandler catalogRequestHandler;

	private ConnectionFacade connectionFacade;
	
	private CodeListService codelistService;

	public void startUrlValidatorJob() {
		mdekJobHandler.startUrlValidatorJob();
	}

	public void stopUrlValidatorJob() {
		mdekJobHandler.stopJob(JobType.URL_VALIDATOR);
		
        // also do not forget to tell the backend that job was canceled
		IngridDocument jobInfo = new IngridDocument();
		jobInfo.put(MdekKeys.URL_RESULT, new ArrayList<Map<String, Object>>());
        jobInfo.put(MdekKeys.CAP_RESULT, new ArrayList<Map<String, Object>>());
		jobInfo.putBoolean( MdekKeys.JOBINFO_IS_UPDATE, true);
		jobInfo.putBoolean( MdekKeys.JOBINFO_IS_FINISHED, true);
		connectionFacade.getMdekCallerCatalog().setURLInfo( connectionFacade.getCurrentPlugId(), jobInfo, MdekSecurityUtils.getCurrentUserUuid() );
	}

	public URLJobInfoBean getUrlValidatorJobInfo() {
		return (URLJobInfoBean) mdekJobHandler.getJobInfo(JobType.URL_VALIDATOR);
	}

	public void updateDBUrlJobInfo(List<Map<String, String>> sourceUrls, String targetUrl) {
		List<IngridDocument> urlList = new ArrayList<>();

		for (Map<String, String> map : sourceUrls) {
			IngridDocument urlDoc = new IngridDocument();
			urlDoc.put(MdekKeys.URL_RESULT_OBJECT_UUID, map.get("objectUuid"));
			urlDoc.put(MdekKeys.URL_RESULT_URL, map.get("url"));
			urlDoc.put(MdekKeys.URL_RESULT_STATE, URLState.State.NOT_CHECKED.toString());
			urlList.add(urlDoc);
		}

		IMdekCallerCatalog mdekCallerCatalog = connectionFacade.getMdekCallerCatalog();
		IngridDocument response = mdekCallerCatalog.updateURLInfo(
				connectionFacade.getCurrentPlugId(),
				urlList,
				targetUrl,
				MdekSecurityUtils.getCurrentUserUuid());

		IMdekClientCaller mdekClientCaller = connectionFacade.getMdekClientCaller();
		if (mdekClientCaller.getResultFromResponse(response) == null) {
			MdekErrorUtils.handleError(response);
		}
	}

	public void replaceUrls(List<Map<String, String>> sourceUrls, String targetUrl, String type) {
		List<IngridDocument> urlList = new ArrayList<>();

		for (Map<String, String> map : sourceUrls) {
			IngridDocument urlDoc = new IngridDocument();
			urlDoc.put(MdekKeys.URL_RESULT_OBJECT_UUID, map.get("objectUuid"));
			urlDoc.put(MdekKeys.URL_RESULT_URL, map.get("url"));
			urlList.add(urlDoc);
		}

		IMdekCallerCatalog mdekCallerCatalog = connectionFacade.getMdekCallerCatalog();
		IngridDocument response = mdekCallerCatalog.replaceURLs(
				connectionFacade.getCurrentPlugId(),
				urlList,
				targetUrl,
				type,
				MdekSecurityUtils.getCurrentUserUuid());

		IMdekClientCaller mdekClientCaller = connectionFacade.getMdekClientCaller();
		if (mdekClientCaller.getResultFromResponse(response) == null) {
			MdekErrorUtils.handleError(response);
		}
	}

	public List<MdekDataBean> getDuplicateObjects() {
		IMdekCallerQuery mdekCallerQuery = connectionFacade.getMdekCallerQuery();

		String qString = "select obj.objUuid, obj.objClass, obj.objName, obj.objDescr "
			+ "from ObjectNode oNode "
				+ "inner join oNode.t01ObjectPublished obj "
			+ "where oNode.objIdPublished = oNode.objId "
			+ "order by obj.objName";

		IngridDocument response = mdekCallerQuery.queryHQLToMap(
				connectionFacade.getCurrentPlugId(), qString, null, "");
		IngridDocument result = MdekUtils.getResultFromResponse(response);

		List<MdekDataBean> resultList = new ArrayList<>();
		if (result != null) {
			@SuppressWarnings("unchecked")
            List<IngridDocument> objs = (List<IngridDocument>) result.get(MdekKeys.OBJ_ENTITIES);
			if (objs != null) {
				for (IngridDocument objEntity : objs) {
					if (isDuplicate(objEntity, objs)) {
						MdekDataBean obj = new MdekDataBean();
						obj.setUuid(objEntity.getString("obj.objUuid"));
						obj.setObjectClass(objEntity.getInt("obj.objClass"));
						obj.setTitle(objEntity.getString("obj.objName"));
						obj.setGeneralDescription(objEntity.getString("obj.objDescr"));
						resultList.add(obj);
					}
				}
			}
		}
		return resultList;
	}

	private static boolean isDuplicate(IngridDocument objEntity, List<IngridDocument> list) {
		String objName = objEntity.getString("obj.objName");
		int count = 0;
		for (IngridDocument item : list) {
			if (item.getString("obj.objName").equals(objName))
				count++;
			if (count > 1) {
				return true;
			}
		}
		return false;
	}

	public void startSNSUpdateJob(String locale) {
			mdekJobHandler.startSNSUpdateJob(locale);
	}

	public void stopSNSUpdateJob() {
		mdekJobHandler.stopJob(JobType.SNS_UPDATE);
	}

	public SNSUpdateJobInfoBean getSNSUpdateJobInfo() {
		return (SNSUpdateJobInfoBean) mdekJobHandler.getJobInfo(JobType.SNS_UPDATE);
	}

	public FileTransfer getSNSUpdateResultAsCSV() {
		StringWriter writer = new StringWriter();
		CSVWriter csvWriter = new CSVWriter(writer, ';');
		csvWriter.writeAll( getSNSUpdateJobInfo().getEntries() );

		return new FileTransfer("snsUpdate.csv", "text/comma-separated-values", writer.toString().getBytes(StandardCharsets.ISO_8859_1));
	}


	public void startSNSLocationUpdateJob(String locale) {
		mdekJobHandler.startSNSLocationUpdateJob(locale);
	}

	public void stopSNSLocationUpdateJob() {
		mdekJobHandler.stopJob(JobType.SNS_LOCATION_UPDATE);
	}

	public SNSLocationUpdateJobInfoBean getSNSLocationUpdateJobInfo() {
		return (SNSLocationUpdateJobInfoBean) mdekJobHandler.getJobInfo(JobType.SNS_LOCATION_UPDATE);
	}

	public FileTransfer getSNSLocationUpdateResultAsCSV() {
		StringWriter writer = new StringWriter();
		CSVWriter csvWriter = new CSVWriter(writer, ';');
		csvWriter.writeAll( getSNSLocationUpdateJobInfo().getEntries() );

		return new FileTransfer("snsLocationUpdate.csv", "text/comma-separated-values", writer.toString().getBytes(StandardCharsets.ISO_8859_1));
	}


	public AnalyzeJobInfoBean analyze() {
		try {
			return catalogRequestHandler.analyze();

		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.error("MdekException while starting analysis job.", e);
			throw new RuntimeException(MdekErrorUtils.convertToRuntimeException(e));
		}
	}
	

	public void setMdekJobHandler(MdekJobHandler mdekJobHandler) {
		this.mdekJobHandler = mdekJobHandler;
	}

	public void setConnectionFacade(ConnectionFacade connectionFacade) {
		this.connectionFacade = connectionFacade;
	}

	public void setCatalogRequestHandler(CatalogRequestHandler catalogRequestHandler) {
		this.catalogRequestHandler = catalogRequestHandler;
	}
	
	public List<MdekDataBean> getObjectsOfAddressByType(String addressUuid, Integer referenceTypeId, int maxNumHits) {
		return catalogRequestHandler.getObjectsOfAddressByType(addressUuid, referenceTypeId, maxNumHits);
	}
	
	public List<MdekDataBean> getObjectsOfResponsibleUser(String responsibleUserUuid, int maxNumHits) {
		return catalogRequestHandler.getObjectsOfResponsibleUser(responsibleUserUuid, maxNumHits);
	}
	
	public List<MdekAddressBean> getAddressesOfResponsibleUser(String responsibleUserUuid, int maxNumHits) {
		return catalogRequestHandler.getAddressesOfResponsibleUser(responsibleUserUuid, maxNumHits);
	}
	
	public FileTransfer getCsvData( String uuid, CsvRequestType type ) {
		return new FileTransfer("export.csv.gz", "x-gzip", catalogRequestHandler.getCsvData(uuid, type));
	}
	
	public void replaceAddress(String oldUuid, String newUuid) {
		try {
			IngridDocument response = catalogRequestHandler.replaceAddress(oldUuid, newUuid);
			
			IMdekClientCaller mdekClientCaller = connectionFacade.getMdekClientCaller();
			if (mdekClientCaller.getResultFromResponse(response) == null) {
				MdekErrorUtils.handleError(response);
			}

		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.error("MdekException while replacing address.", e);
			throw MdekErrorUtils.convertToRuntimeException(e);
		}
	}

	public void rebuildSysListData() {
		try {
			catalogRequestHandler.rebuildSysListData();

		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.error("MdekException while starting 'rebuild sysList' job.", e);
			throw MdekErrorUtils.convertToRuntimeException(e);
		}
	}

	public JobInfoBean getRebuildSysListDataJobInfo() {
		try {
			return catalogRequestHandler.getRebuildJobInfo();

		} catch (MdekException e) {
			// Wrap the MdekException in a RuntimeException so dwr can convert it
			log.error("MdekException while fetching 'rebuild sysList' job info.", e);
			throw MdekErrorUtils.convertToRuntimeException(e);
		}
	}

	public List<String> getConnectedIPlugs() {
	    return connectionFacade.getMdekClientCaller().getRegisteredIPlugs();
	}
	
    public List<Map<String, String>> getConnectedCataloguesInfo() {
        List<Map<String, String>> result = new ArrayList<>();
        List<String> iplugs = getConnectedIPlugs();
        for (String iplug : iplugs) {
            Map<String, String> m = new HashMap<>();
            m.put("iplug", iplug);
            // check if iplug is in mdek db
            String catAdminUuid = getCatAdminUuid(iplug);
            if (catAdminUuid != null) {
                String login = MdekSecurityUtils.getLoginFromUuidAndIPlug(catAdminUuid, iplug);
                if (login != null)
                    m.put("admin", login);
            }
            result.add(m);
        }
        return result;
    }
    
    private String getCatAdminUuid(String plugId) {
        try {
            IMdekCallerSecurity mdekCallerSecurity = connectionFacade.getMdekCallerSecurity();
            IngridDocument response = mdekCallerSecurity.getCatalogAdmin(plugId, "");
            IngridDocument result = de.ingrid.mdek.util.MdekUtils.getResultFromResponse(response);
            return (String) result.get(MdekKeysSecurity.IDC_USER_ADDR_UUID);
        } catch (Exception e) {
            return null;
        }
    }
    
    public Long getLastModifiedCodelistTimestamp() {
    	String plugId = connectionFacade.getCurrentPlugId();
    	String uuid = MdekSecurityUtils.getCurrentUserUuid();
    	IngridDocument response = connectionFacade.getMdekCallerCatalog().getLastModifiedTimestampOfSyslists(plugId, uuid);
        return MdekCatalogUtils.extractLastModifiedTimestampFromResponse(response);
    }
    
    /**
     * Get all codelists from the codelist repository and update the database with 
     * these values. If the codelist repo could not be reached, the initial codelist
     * is used instead.
     * 
     * @return true if codelists have been fetched from Repository, otherwise false when
     * from local initial codelist
     */
    public boolean forceUpdateCodelists() {
    	// try to get all codelists from repo first
    	Object repoCodelists = codelistService.updateFromServer(-1l);
        
    	// if repo could not be reached use local initial codelist
    	if (repoCodelists == null) {
    		codelistService.persistToAll( codelistService.getInitialCodelists() );
    		return false;
    	} else {
    		return true;
    	}
    }

	public void setCodelistService(CodeListService codelistService) {
		this.codelistService = codelistService;
	}
}
