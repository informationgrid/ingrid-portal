/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2023 wemove digital solutions GmbH
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
package de.ingrid.mdek.quartz.jobs;

import de.ingrid.codelists.quartz.jobs.UpdateCodeListsJob;
import de.ingrid.mdek.beans.Record;
import de.ingrid.mdek.caller.IMdekCallerQuery;
import de.ingrid.mdek.caller.IMdekCallerSecurity;
import de.ingrid.mdek.caller.IMdekClientCaller;
import de.ingrid.mdek.dwr.services.GetCapabilitiesService;
import de.ingrid.mdek.handler.ConnectionFacade;
import de.ingrid.mdek.job.repository.Pair;
import de.ingrid.mdek.services.persistence.db.IHQLExecuter;
import de.ingrid.utils.IngridDocument;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static de.ingrid.mdek.MdekKeys.OBJ_ENTITIES;

public class UpdateExternalCoupledResources extends UpdateCodeListsJob {

    private static final Logger log = Logger.getLogger(UpdateExternalCoupledResources.class);

    private static final String SEPARATOR = "#**#";

    private ConnectionFacade connectionFacade;
    private GetCapabilitiesService getCapabilitiesService;
    private IMdekCallerQuery mdekCallerQuery;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext)
            throws JobExecutionException {
        log.info("Executing UpdateExternalCoupledResources...");

        try {
            final IMdekClientCaller caller = this.connectionFacade.getMdekClientCaller();
            final IMdekCallerSecurity mdekCallerSecurity = this.connectionFacade.getMdekCallerSecurity();
            final List<String> iplugList = caller.getRegisteredIPlugs();
            if (iplugList == null || iplugList.isEmpty()) {
                throw new JobExecutionException("No iPlugs found.");
            }

            log.debug("Number of iplugs found: " + iplugList.size());
            for (final String plugId : iplugList) {
                updateExternalResourcesInfo(plugId);
            }

            if (iplugList.size() > 0) {
                log.info("UpdateExternalCoupledResources finished!");
            } else {
                log.info("No iPlug connected to update codelists to!");
            }
        } catch (JobExecutionException e) {
            log.error("Error during Job: UpdateExternalCoupledResources", e);
        }
    }

    private void updateExternalResourcesInfo(String plugId) {
        List<DocumentShortInfo> coupledResources = getUuidsFromServices(plugId);
        Map<String, String> urlCache = new HashMap<>();
        Summary summary = new Summary();

        for (DocumentShortInfo coupledResource : coupledResources) {
            updateForUuid(plugId, coupledResource, urlCache, summary);
        }

        log.info("Summary for iPlug: " + plugId);
        log.info("Checked Coupled Resource URLs: " + summary.count);
        log.info("Corrupted URLs: " + summary.corrupt);
        log.info("Updated Coupled Resource Identifiers: " + summary.updated);

    }

    private void updateForUuid(String plugId, DocumentShortInfo coupledResource, Map<String, String> urlCache, Summary summary) {

        log.debug("Checking URL: " + coupledResource.url);

        String latestIdentifier = urlCache.get(coupledResource.url);
        if (latestIdentifier == null) {
            // if url already in cache but with null value, then we already requested this corrupt URL
            if (!urlCache.containsKey(coupledResource.url)) {
                latestIdentifier = getIdentifierFromExternalResource(coupledResource.url);
                urlCache.put(coupledResource.url, latestIdentifier);
                summary.count++;
            } else {
                log.debug("Corrupted URL already checked ... skipping");
                return;
            }
        } else {
            log.debug("Found URL result in cache: " + coupledResource.url);
        }

        // return if resource is corrupted or not available
        if (latestIdentifier == null) {
            log.warn("Coupled Resource seems to be corrupted: " + coupledResource.url + " for: " + coupledResource.uuid);
            summary.corrupt++;
            return;
        }

        // return if no change
        if (latestIdentifier.equals(coupledResource.description)) {
            log.debug("Coupled Resource Identifier did not change");
            return;
        }

        log.info("External Coupled Resource Identifer changed from: " + coupledResource.description + " to: " + latestIdentifier + " for UUID: " + coupledResource.uuid);

        List<Pair> pairList = generateUpdateParameter(latestIdentifier, coupledResource.urlRefId);
        IngridDocument response = mdekCallerQuery.updateByHQL(plugId, pairList);
        summary.updated++;

        handleUpdateResponse(pairList.get(0), response);
    }

    private List<Pair> generateUpdateParameter(String latestIdentifier, long urlRefId) {
        Pair updatePair = new Pair(
                IHQLExecuter.HQL_UPDATE,
                "UPDATE T017UrlRef SET descr = '" + latestIdentifier + "' WHERE id = " + urlRefId);
        List<Pair> pairList = new ArrayList<>();
        pairList.add(updatePair);
        return pairList;
    }

    private void handleUpdateResponse(Pair updatePair, IngridDocument response) {
        IngridDocument result = de.ingrid.mdek.util.MdekUtils.getResultFromResponse(response);
        if (result != null) {
            Boolean state = (Boolean) result.get(IHQLExecuter.HQL_STATE);
            List<Pair> queryResult = (List<Pair>) result.get(IHQLExecuter.HQL_RESULT);
            if (!state || !queryResult.get(0).getValue().equals(1)) {
                log.error("Coupled Resource description could not be updated");
            }
        } else {
            log.error("Query for updating coupled resource description seems to have lead to an error: " + updatePair.getValue());
        }
    }

    private String getIdentifierFromExternalResource(String url) {

        log.debug("Fetch record from URL: " + url);
        Record record = getCapabilitiesService.getRecordById(url);
        if (record == null || record.getUuid() == null || record.getIdentifier() == null) {
            return null;
        }
        return record.getIdentifier() + SEPARATOR + record.getUuid();
    }

    private List<DocumentShortInfo> getUuidsFromServices(String plugId) {
        String query = "SELECT DISTINCT urlRef.id, objData.objUuid, urlRef.urlLink, urlRef.descr FROM ObjectNode oNode\n" +
                ", T01Object objData\n" +
                ", T017UrlRef urlRef WHERE (oNode.objId = objData.id OR oNode.objIdPublished = objData.id) AND objData.objClass = 3 AND urlRef.objId = objData.id AND urlRef.specialRef = 3600";

        IngridDocument response = connectionFacade.getMdekCallerQuery().queryHQLToMap(plugId, query, null, "");
        IngridDocument result = de.ingrid.mdek.util.MdekUtils.getResultFromResponse(response);
        List<IngridDocument> hits = (List<IngridDocument>) result.get(OBJ_ENTITIES);
        return hits.stream()
                .map(hit -> new DocumentShortInfo(
                        hit.getLong("urlRef.id"),
                        hit.getString("objData.objUuid"),
                        hit.getString("urlRef.urlLink"),
                        hit.getString("urlRef.descr")))
                .collect(Collectors.toList());
    }

    public void setConnectionFacade(ConnectionFacade connectionFacade) {
        this.connectionFacade = connectionFacade;
        this.mdekCallerQuery = connectionFacade.getMdekCallerQuery();
    }

    public void setGetCapabilitiesService(GetCapabilitiesService getCapabilitiesService) {
        this.getCapabilitiesService = getCapabilitiesService;
    }

    private class DocumentShortInfo {
        public long urlRefId;
        public String uuid;
        public String url;
        public String description;

        public DocumentShortInfo(long id, String uuid, String url, String description) {
            this.urlRefId = id;
            this.uuid = uuid;
            this.url = url;
            this.description = description;
        }
    }

    private class Summary {
        public int count = 0;
        public int corrupt = 0;
        public int updated = 0;
    }
}
