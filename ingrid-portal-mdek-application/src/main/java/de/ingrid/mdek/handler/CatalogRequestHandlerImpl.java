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
package de.ingrid.mdek.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.ingrid.mdek.MdekUtils.CsvRequestType;
import de.ingrid.mdek.MdekUtils.MdekSysList;
import de.ingrid.mdek.beans.AnalyzeJobInfoBean;
import de.ingrid.mdek.beans.CatalogBean;
import de.ingrid.mdek.beans.ExportJobInfoBean;
import de.ingrid.mdek.beans.GenericValueBean;
import de.ingrid.mdek.beans.JobInfoBean;
import de.ingrid.mdek.beans.SysList;
import de.ingrid.mdek.beans.address.MdekAddressBean;
import de.ingrid.mdek.beans.object.MdekDataBean;
import de.ingrid.mdek.caller.IMdekCaller.AddressArea;
import de.ingrid.mdek.caller.IMdekCallerCatalog;
import de.ingrid.mdek.job.IJob.JobType;
import de.ingrid.mdek.persistence.db.model.UserData;
import de.ingrid.mdek.util.MdekAddressUtils;
import de.ingrid.mdek.util.MdekCatalogUtils;
import de.ingrid.mdek.util.MdekErrorUtils;
import de.ingrid.mdek.util.MdekObjectUtils;
import de.ingrid.mdek.util.MdekSecurityUtils;
import de.ingrid.mdek.util.MdekUtils;
import de.ingrid.utils.IngridDocument;

@Service("catalogRequestHandler")
public class CatalogRequestHandlerImpl implements CatalogRequestHandler {

    private final static Logger log = Logger.getLogger(CatalogRequestHandlerImpl.class);

    // Injected by Spring
    @Autowired
    private ConnectionFacade connectionFacade;

    // Initialized by spring through the init method
    private IMdekCallerCatalog mdekCallerCatalog;
    
    public void init() {
        mdekCallerCatalog = connectionFacade.getMdekCallerCatalog();
    }
    

    public List<SysList> getAllSysListInfos() {
        IngridDocument response = mdekCallerCatalog.getSysLists(connectionFacade.getCurrentPlugId(), null, null, MdekSecurityUtils.getCurrentUserUuid());
        return MdekCatalogUtils.extractSysListInfosFromResponse(response);
    }

    public Map<Integer, List<String[]>> getSysLists(Integer[] listIds, String languageCode) {
        IngridDocument response = mdekCallerCatalog.getSysLists(connectionFacade.getCurrentPlugId(), listIds, languageCode, MdekSecurityUtils.getCurrentUserUuid());
        return MdekCatalogUtils.extractSysListFromResponse(response);
    }

    public void storeSysList(Integer listId, boolean maintainable, Integer defaultEntryIndex, Integer[] entryIds,
            String[] entriesGerman, String[] entriesEnglish, String[] data) {
        IngridDocument response = mdekCallerCatalog.storeSysList(
                connectionFacade.getCurrentPlugId(),
                listId,
                maintainable,
                defaultEntryIndex, entryIds, entriesGerman, entriesEnglish, data, 
                MdekSecurityUtils.getCurrentUserUuid());

        IngridDocument result = MdekUtils.getResultFromResponse(response);
        if (result == null) {
            MdekErrorUtils.handleError(response);
        }
    }

    public String[] getFreeListEntries(MdekSysList sysList) {
        IngridDocument response = mdekCallerCatalog.getFreeListEntries(
                connectionFacade.getCurrentPlugId(),
                sysList,
                MdekSecurityUtils.getCurrentUserUuid());

        return MdekCatalogUtils.extractFreeSysListEntriesFromResponse(response);
    }

    public void replaceFreeEntryWithSysListEntry(String freeEntry, MdekSysList sysList, Integer sysListEntryId, String sysListEntryName) {
        IngridDocument response = mdekCallerCatalog.replaceFreeEntryWithSyslistEntry(
                connectionFacade.getCurrentPlugId(),
                freeEntry,
                sysList,
                sysListEntryId,
                sysListEntryName,
                MdekSecurityUtils.getCurrentUserUuid());

        IngridDocument result = MdekUtils.getResultFromResponse(response);
        if (result == null) {
            MdekErrorUtils.handleError(response);
        }
    }


    // Returns a xml document as string containing the lists for all listIds
    // If listIds is null, all existing sysLists will be exported
    public String exportSysLists(Integer[] listIds) {
        if (listIds == null) {
            List<SysList> codelists = getAllSysListInfos();
            List<Integer> listIdsTemp = new ArrayList<Integer>();
            for (SysList codelist : codelists) {
                listIdsTemp.add(codelist.getId());
            }
            listIds = listIdsTemp.toArray(new Integer[0]);
        }

        IngridDocument responseDe = mdekCallerCatalog.getSysLists(
                connectionFacade.getCurrentPlugId(),
                listIds,
                "de",
                MdekSecurityUtils.getCurrentUserUuid());
        
        IngridDocument responseEn = mdekCallerCatalog.getSysLists(
                connectionFacade.getCurrentPlugId(),
                listIds,
                "en",
                MdekSecurityUtils.getCurrentUserUuid());


        IngridDocument resultDe = MdekUtils.getResultFromResponse(responseDe);
        IngridDocument resultEn = MdekUtils.getResultFromResponse(responseEn);
        if (resultDe != null) {
            return MdekCatalogUtils.convertSysListsToXML(resultDe, resultEn);

        } else {
            MdekErrorUtils.handleError(responseDe);
            return null;
        }
    }

    // Convert the given xml doc to List<SysList> via xstream and store
    // the sysLists in the db
    public void importSysLists(String xmlDoc) {
        List<SysList> sysLists = MdekCatalogUtils.convertXMLToSysLists(xmlDoc);
        if (sysLists != null) {
            for (SysList sysList : sysLists) {
                log.debug("store sysList with id: " + sysList.getId());
                storeSysList(
                        sysList.getId(),
                        sysList.getMaintainable(),
                        sysList.getDefaultIndex(),
                        sysList.getEntryIds(),
                        sysList.getDeEntries(),
                        sysList.getEnEntries(),
                        sysList.getData());
            }
        }
    }

    public List<GenericValueBean> getSysGenericValues(String[] keyNames) {
        IngridDocument response = mdekCallerCatalog.getSysGenericKeys(connectionFacade.getCurrentPlugId(), keyNames, MdekSecurityUtils.getCurrentUserUuid());
        return MdekCatalogUtils.extractSysGenericKeysFromResponse(response);
    }
    
    public List<GenericValueBean> getSysGenericValues(String[] keyNames, HttpServletRequest request) {
        IngridDocument response = mdekCallerCatalog.getSysGenericKeys(connectionFacade.getCurrentPlugId(request), keyNames, MdekSecurityUtils.getCurrentUserUuid(request));
        return MdekCatalogUtils.extractSysGenericKeysFromResponse(response);
    }

    public void storeSysGenericValues(List<GenericValueBean> genericValues) {
        if (genericValues != null) {
            List<String> keyNames = new ArrayList<String>();
            List<String> keyValues = new ArrayList<String>();
    
            for (GenericValueBean genericValue : genericValues) {
                keyNames.add(genericValue.getKey());
                keyValues.add(genericValue.getValue());
            }
    
            mdekCallerCatalog.storeSysGenericKeys(
                    connectionFacade.getCurrentPlugId(),
                    keyNames.toArray(new String[]{}),
                    keyValues.toArray(new String[]{}),
                    MdekSecurityUtils.getCurrentUserUuid());
        }
    }

    public CatalogBean getCatalogData() {
        IngridDocument response = mdekCallerCatalog.fetchCatalog(connectionFacade.getCurrentPlugId(), MdekSecurityUtils.getCurrentUserUuid());
        return MdekCatalogUtils.extractCatalogFromResponse(response);
    }

    public CatalogBean storeCatalogData(CatalogBean cat) {
        IngridDocument catDoc = (IngridDocument) MdekCatalogUtils.convertFromCatalogRepresentation(cat);

        log.debug("Sending the following catalog for storage:");
        log.debug(catDoc);

        IngridDocument response = mdekCallerCatalog.storeCatalog(connectionFacade.getCurrentPlugId(), catDoc, true, MdekSecurityUtils.getCurrentUserUuid());
        return MdekCatalogUtils.extractCatalogFromResponse(response);
    }

    public void exportFreeAddresses(boolean includeWorkingCopies) {
        IngridDocument response = exportAddressBranch(null, false, AddressArea.ALL_FREE_ADDRESSES, includeWorkingCopies);
        MdekCatalogUtils.extractJobInfoFromResponse(response);
    }

    public void exportTopAddresses(boolean exportChildren, boolean includeWorkingCopies) {
        IngridDocument response = exportAddressBranch(null, exportChildren, AddressArea.ALL_ADDRESSES, includeWorkingCopies);
        MdekCatalogUtils.extractJobInfoFromResponse(response);
    }

    public void exportAddressBranch(String rootUuid, boolean exportChildren, boolean includeWorkingCopies) {
        IngridDocument response = exportAddressBranch(rootUuid, exportChildren, null, includeWorkingCopies);
        MdekCatalogUtils.extractJobInfoFromResponse(response);
    }

    private IngridDocument exportAddressBranch(String rootUuid, boolean exportChildren, AddressArea addressArea, boolean includeWorkingCopies) {
        return mdekCallerCatalog.exportAddressBranch(
                connectionFacade.getCurrentPlugId(),
                rootUuid, !exportChildren,
                addressArea, includeWorkingCopies, MdekSecurityUtils.getCurrentUserUuid());
    }

    public void exportObjectBranch(String rootUuid, boolean exportChildren, boolean includeWorkingCopies) {
        IngridDocument response = mdekCallerCatalog.exportObjectBranch(
                connectionFacade.getCurrentPlugId(),
                rootUuid,
                !exportChildren, includeWorkingCopies, MdekSecurityUtils.getCurrentUserUuid());

        MdekCatalogUtils.extractJobInfoFromResponse(response);
    }

    public void exportObjectsWithCriteria(String exportCriteria, boolean includeWorkingCopies) {
        IngridDocument response = mdekCallerCatalog.exportObjects(
                connectionFacade.getCurrentPlugId(),
                exportCriteria, includeWorkingCopies,
                MdekSecurityUtils.getCurrentUserUuid());
        MdekCatalogUtils.extractJobInfoFromResponse(response);
    }

    public ExportJobInfoBean getExportInfo(boolean includeExportData) {
        IngridDocument response = mdekCallerCatalog.getExportInfo(connectionFacade.getCurrentPlugId(), includeExportData, MdekSecurityUtils.getCurrentUserUuid());
        return MdekCatalogUtils.extractExportJobInfoFromResponse(response);
    }

    public void importEntities(UserData currentUser, List<byte[]> importData, String targetObjectUuid, String targetAddressUuid,
            String frontendMappingProtocol, boolean publishImmediately, boolean doSeparateImport, boolean copyNodeIfPresent) {
        IngridDocument response = mdekCallerCatalog.importEntities(
                currentUser.getPlugId(),
                importData,
                targetObjectUuid, targetAddressUuid,
                publishImmediately, doSeparateImport,
                copyNodeIfPresent, frontendMappingProtocol,
                currentUser.getAddressUuid());
        MdekCatalogUtils.extractJobInfoFromResponse(response);
    }

    public JobInfoBean getImportInfo() {
        IngridDocument response = mdekCallerCatalog.getJobInfo(
                connectionFacade.getCurrentPlugId(),
                JobType.IMPORT,
                MdekSecurityUtils.getCurrentUserUuid());

        return MdekCatalogUtils.extractJobInfoFromResponse(response);
    }

    public void cancelRunningJob() {
        mdekCallerCatalog.cancelRunningJob(connectionFacade.getCurrentPlugId(), MdekSecurityUtils.getCurrentUserUuid());
    }

    public AnalyzeJobInfoBean analyze() {
        IngridDocument response = mdekCallerCatalog.analyze(
                connectionFacade.getCurrentPlugId(),
                MdekSecurityUtils.getCurrentUserUuid());

        return MdekCatalogUtils.extractAnalyzeJobInfoFromResponse(response);
    }

    public ConnectionFacade getConnectionFacade() {
        return connectionFacade;
    }


    public void setConnectionFacade(ConnectionFacade connectionFacade) {
        this.connectionFacade = connectionFacade;
    }
    
    public List<MdekDataBean> getObjectsOfAddressByType(String addressUuid, Integer referenceTypeId, int maxNumHits) {
        IngridDocument response = mdekCallerCatalog.getObjectsOfAddressByType(
                connectionFacade.getCurrentPlugId(),
                addressUuid,
                referenceTypeId,
                maxNumHits,
                MdekSecurityUtils.getCurrentUserUuid());
        return MdekObjectUtils.extractDetailedObjectsFromResponse(response);
    }
    
    public List<MdekDataBean> getObjectsOfResponsibleUser(String responsibleUserUuid, int maxNumHits) {
        IngridDocument response = mdekCallerCatalog.getObjectsOfResponsibleUser(
                connectionFacade.getCurrentPlugId(),
                responsibleUserUuid,
                maxNumHits,
                MdekSecurityUtils.getCurrentUserUuid());

        return MdekObjectUtils.extractDetailedObjectsFromResponse(response);
    }
    
    public List<MdekAddressBean> getAddressesOfResponsibleUser(String responsibleUserUuid, int maxNumHits) {
        IngridDocument response = mdekCallerCatalog.getAddressesOfResponsibleUser(
                connectionFacade.getCurrentPlugId(),
                responsibleUserUuid,
                maxNumHits,
                MdekSecurityUtils.getCurrentUserUuid());
        return MdekAddressUtils.extractDetailedAddressesFromResponse(response);
    }
    
    public byte[] getCsvData(String uuid, CsvRequestType type) {
        IngridDocument doc = mdekCallerCatalog.getCsvData(connectionFacade.getCurrentPlugId(), type, uuid, MdekSecurityUtils.getCurrentUserUuid());
        return MdekUtils.extractSearchResultsFromResponse(doc).getCsvSearchResult().getData();
    }


    public IngridDocument replaceAddress(String oldUuid, String newUuid) {
        return mdekCallerCatalog.replaceAddress(connectionFacade.getCurrentPlugId(), oldUuid, newUuid, MdekSecurityUtils.getCurrentUserUuid());     
    }

    public void rebuildSysListData() {
        IngridDocument response = mdekCallerCatalog.rebuildSyslistData(
                connectionFacade.getCurrentPlugId(),
                MdekSecurityUtils.getCurrentUserUuid());

        if (null == MdekUtils.getResultFromResponse(response)) {
            MdekErrorUtils.handleError(response);
        }
    }

    public JobInfoBean getRebuildJobInfo() {
        IngridDocument response = mdekCallerCatalog.getJobInfo(
                connectionFacade.getCurrentPlugId(),
                JobType.REBUILD_SYSLISTS,
                MdekSecurityUtils.getCurrentUserUuid());

        return MdekCatalogUtils.extractReindexJobInfoFromResponse(response);
    }
}
