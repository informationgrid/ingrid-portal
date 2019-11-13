/*-
 * **************************************************-
 * InGrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2019 wemove digital solutions GmbH
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

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import de.ingrid.mdek.MdekKeys;
import de.ingrid.mdek.caller.IMdekCallerQuery;
import de.ingrid.mdek.caller.IMdekClientCaller;
import de.ingrid.mdek.handler.ConnectionFacade;
import de.ingrid.mdek.job.repository.IJobRepository;
import de.ingrid.mdek.upload.storage.Storage;
import de.ingrid.mdek.upload.storage.StorageItem;
import de.ingrid.mdek.util.MdekUtils;
import de.ingrid.utils.IngridDocument;

public class UploadCleanupJob extends QuartzJobBean {

    // Separator used in file paths
    public static final String PATH_SEPARATOR = "/";

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    /**
     * Pattern to match external links in additional_field_data.data column
     * NOTE: this data entry consists of the object's UUID and the actual location combined by a forward slash
     * So we have to test the following cases:
     * - {UUID}/{//location}:      Link without protocol
     * - {UUID}/{http://location}: Link with HTTP or any other protocol
     */
    private static final Pattern LINK_PATTERN = Pattern.compile(".*/(//|[^:/]+://).*");

    private static final Logger log = Logger.getLogger(UploadCleanupJob.class);

    public class FileReference {
        public String file;
        public String path;
        public LocalDate expiryDate;

        public FileReference(final String file, final String path, final LocalDate expiryDate) {
            this.file = file;
            this.path = path;
            this.expiryDate = expiryDate;
        }
        @Override
        public String toString() {
            return this.path+" (date: "+(this.expiryDate != null ? dateFormatter.format(this.expiryDate) : null)+")";
        }
    }

    private ConnectionFacade connectionFacade;
    private Storage storage;
    private Integer deleteFileMinAge;
    private LocalDateTime referenceDateTime = LocalDateTime.now();

    /**
     * Set the connection facade used for communication.
     * @param connectionFacade
     */
    public void setConnectionFacade(final ConnectionFacade connectionFacade) {
        this.connectionFacade = connectionFacade;
    }

    /**
     * Set the storage to cleanup.
     * @param storage
     */
    public void setStorage(final Storage storage) {
        this.storage = storage;
    }

    /**
     * Set the minimum age that unreferenced files must have before deletion
     * @param deleteFileMinAge
     */
    public void setDeleteFileMinAge(final Integer deleteFileMinAge) {
        this.deleteFileMinAge = deleteFileMinAge;
    }

    /**
     * Set the reference time for calculating expiry of files.
     * Defaults to now if not set explicitly.
     * @param referenceDateTime
     */
    public void setReferenceDate(final LocalDateTime referenceDateTime) {
        this.referenceDateTime = referenceDateTime;
    }

    @Override
    protected void executeInternal(final JobExecutionContext ctx) throws JobExecutionException {
        log.info("Executing UploadCleanupJob...");

        final LocalDate referenceDate = this.referenceDateTime.toLocalDate();
        log.info("Reference time: "+this.referenceDateTime);
        log.info("Reference date: "+referenceDate);
        log.info("Minimum file age: "+this.deleteFileMinAge+" seconds");

        try {
            final IMdekClientCaller caller = this.connectionFacade.getMdekClientCaller();
            final List<String> iplugList = caller.getRegisteredIPlugs();
            if (iplugList == null || iplugList.isEmpty()) {
                throw new JobExecutionException("No iPlugs found.");
            }

            log.debug("Number of iplugs found: "+iplugList.size());
            for (final String plugId : iplugList) {
                log.debug("Processing iplug: "+plugId);
                try {
                    // initialize file maps
                    final Map<String, StorageItem> allFiles = new HashMap<>();
                    final Map<String, StorageItem> unreferencedFiles = new HashMap<>();
                    final Map<String, StorageItem> expiredFiles = new HashMap<>();
                    final Map<String, StorageItem> unexpiredFiles = new HashMap<>();

                    // collect files from the file storage
                    log.debug("Collecting files from the storage");
                    final StorageItem[] fileItems = this.storage.list(plugId);
                    log.debug("Number of files found in the storage: "+fileItems.length);

                    if (fileItems.length > 0) {
                         // initialize file maps
                        for (final StorageItem item : fileItems) {
                            final String uri = item.getUri(false);
                            allFiles.put(uri, item);

                            // put all files in the unreferenced map first
                            // files that are referenced in a document will be removed from the map
                            unreferencedFiles.put(uri, item);
                        }

                        // get uploads from the iplug
                        log.debug("Getting documents from iplug: "+plugId);
                        final Map<String, List<FileReference>> uploads = this.getUploads(plugId);

                        // get uploads from data sets that do not have a phase, i.e. Negative Vorpruefungen
                        // merge the results
                        final Map<String, List<FileReference>> uploadsWithoutPhases = this.getUploadsFromDataSetsWithoutPhases(plugId);
                        for (final String key : uploadsWithoutPhases.keySet()) {
                            if (uploads.containsKey( key )) {
                                final List<FileReference> fileReferences = uploads.get( key );
                                fileReferences.addAll( uploadsWithoutPhases.get( key ) );
                            } else {
                                uploads.put( key, uploadsWithoutPhases.get( key ) );
                            }
                        }

                        final Set<String> uploadUris = uploads.keySet();

                        // process uploads
                        log.debug("Number of documents: "+uploadUris.size());
                        log.debug("Collecting file references from documents");
                        for (final String uploadUri : uploadUris) {
                            // get the references
                            final List<FileReference> references = uploads.get(uploadUri);
                            log.debug("Found reference: "+uploadUri+" in "+references.size()+" document(s): "+references+")");

                            // remove referenced file from unreferenced list
                            if (unreferencedFiles.containsKey(uploadUri)) {
                                unreferencedFiles.remove(uploadUri);
                            }

                            // get the storage item for the reference
                            final StorageItem item = allFiles.get(uploadUri);
                            if (item == null) {
                                log.warn("File "+uploadUri+" does not exist in the storage");
                                continue;
                            }

                            // get latest expire date from references
                            // null values count as infinite
                            LocalDate expiryDate = null;
                            for (final FileReference reference : references) {
                                if (expiryDate == null ||
                                        (reference.expiryDate == null || reference.expiryDate.isAfter(expiryDate))) {
                                    expiryDate = reference.expiryDate;
                                }
                            }
                            // handle expired files
                            // NOTE: a file might be expired in one catalog, but not in another,
                            // so if a file is added/removed by one catalog, it might be removed/added by another
                            // but at the end all expired files (and only those) must be contained in the expired files list
                            if (!item.isArchived() && expiryDate != null && expiryDate.isBefore(referenceDate)) {
                                expiredFiles.put(uploadUri, item);
                            }
                            else if (expiredFiles.containsKey(uploadUri)) {
                                expiredFiles.remove(uploadUri);
                            }
                            // handle files to be restored
                            if (item.isArchived() &&
                                    (expiryDate == null || expiryDate.isEqual(referenceDate) || expiryDate.isAfter(referenceDate))) {
                                unexpiredFiles.put(uploadUri, item);
                            }
                        }

                        // cleanup
                        this.doCleanup(unreferencedFiles, expiredFiles, unexpiredFiles);
                    }
                    else {
                        log.debug("Nothing to clean up");
                    }
                }
                catch (final Exception e) {
                    // current iplug failed
                    this.logError("Processing "+plugId+" failed", e);
                }
            }
        }
        catch (final Exception ex) {
            this.logError(ex.toString(), ex);
            log.info("Aborted UploadCleanupJob");
            return;
        }

        log.info("Finished UploadCleanupJob");
    }

    /**
     * Get all uploads referenced in the given iPlug from data sets with phases in additional file data tables
     * @param plugId
     * @return Map<String, List<FileReference>>
     * @throws JobExecutionException
     */
    private Map<String, List<FileReference>> getUploads(final String plugId) throws JobExecutionException {
        final Map<String, List<FileReference>> resultList = new HashMap<>();
        final IMdekCallerQuery mdekCallerQuery = this.connectionFacade.getMdekCallerQuery();

        if (mdekCallerQuery == null) {
            return resultList;
        }

        // get uploads from working copies and published objects
        // NOTE: it is only possible to execute "from ObjectNode oNode" HQL selects
        final String[] fks = new String[]{"objId", "objIdPublished"};
        for (final String fk : fks) {
            final String qString = "select fdLink.data, fdLink.parentFieldId, fdLink.sort, " +
                    " obj.objName, obj.objUuid, " +
                    " fdRoot.fieldKey, fdPhase.fieldKey, fdPhase.fieldKey, fdDocs.fieldKey, fdLink.sort " +
                "from ObjectNode oNode, " +
                    " T01Object obj, " +
                    " AdditionalFieldData fdRoot, " +
                    " AdditionalFieldData fdPhase, " +
                    " AdditionalFieldData fdDocs, " +
                    " AdditionalFieldData fdLink " +
                "where " +
                    " oNode."+fk+" = obj.id " +
                    " and obj.id = fdRoot.objId" +
                    " and fdRoot.id = fdPhase.parentFieldId" +
                    " and fdPhase.id = fdDocs.parentFieldId" +
                    " and fdDocs.id = fdLink.parentFieldId" +
                    " and fdLink.fieldKey = 'link'";

            final IngridDocument response = mdekCallerQuery.queryHQLToMap(plugId, qString, null, "");
            if (response.getBoolean(IJobRepository.JOB_INVOKE_SUCCESS)) {
                final IngridDocument result = MdekUtils.getResultFromResponse(response);
                if (result != null) {
                    @SuppressWarnings("unchecked")
                    final
                    List<IngridDocument> objs = (List<IngridDocument>) result.get(MdekKeys.OBJ_ENTITIES);
                    if (objs != null) {
                        for (final IngridDocument objEntity : objs) {
                            final String file = objEntity.getString("fdLink.data");

                            // exclude absolute links
                            if (!LINK_PATTERN.matcher(file).matches()) {
                                final String path = objEntity.getString("obj.objName") + "(" + objEntity.getString("obj.objUuid") + ")" + PATH_SEPARATOR +
                                        objEntity.getString("fdRoot.fieldKey") + PATH_SEPARATOR + objEntity.getString("fdPhase.fieldKey") + PATH_SEPARATOR +
                                        objEntity.getString("fdDocs.fieldKey") + PATH_SEPARATOR + objEntity.getInt("fdLink.sort");
                                String date = null;

                                // select expiry date (might not exist)
                                // NOTE: this is done in an extra query since HQL does not support
                                // left outer joins on unrelated tables in the used version
                                // NOTE: it is only possible to execute "from ObjectNode oNode" HQL selects
                                final Long parentId = objEntity.getLong("fdLink.parentFieldId");
                                final Integer sort = objEntity.getInt("fdLink.sort");
                                final String qStringSub = "select fdExpires.data " +
                                    "from ObjectNode oNode, " +
                                        " T01Object obj, " +
                                        " AdditionalFieldData fdRoot, " +
                                        " AdditionalFieldData fdPhase, " +
                                        " AdditionalFieldData fdDocs, " +
                                        " AdditionalFieldData fdExpires " +
                                    "where " +
                                        " oNode."+fk+" = obj.id " +
                                        " and obj.id = fdRoot.objId" +
                                        " and fdRoot.id = fdPhase.parentFieldId" +
                                        " and fdPhase.id = fdDocs.parentFieldId" +
                                        " and fdDocs.id = fdExpires.parentFieldId" +
                                        " and fdExpires.parentFieldId = "+parentId+
                                        " and fdExpires.sort = "+sort+
                                        " and fdExpires.fieldKey = 'expires'";
                                final IngridDocument responseSub = mdekCallerQuery.queryHQLToMap(plugId, qStringSub, null, "");
                                if (responseSub.getBoolean(IJobRepository.JOB_INVOKE_SUCCESS)) {
                                    final IngridDocument resultSub = MdekUtils.getResultFromResponse(responseSub);
                                    if (resultSub != null) {
                                        @SuppressWarnings("unchecked")
                                        final
                                        List<IngridDocument> objsSub = (List<IngridDocument>) resultSub.get(MdekKeys.OBJ_ENTITIES);
                                        if (objsSub != null && objsSub.size() == 1) {
                                            date = objsSub.get(0).getString("fdExpires.data");
                                        }
                                    }
                                    else {
                                        throw new JobExecutionException("Job execution returned null response from plugId: " + plugId);
                                    }
                                }
                                else {
                                    throw new JobExecutionException(responseSub.getString("job_invoke_error_message"));
                                }

                                // create file reference
                                final FileReference reference = new FileReference(
                                        file, path, (date != null && date.length() > 0) ? LocalDate.parse(date, dateFormatter) : null
                                );
                                if (!resultList.containsKey(file)) {
                                    final List<FileReference> references = new ArrayList<>();
                                    references.add(reference);
                                    resultList.put(file, references);
                                }
                                else {
                                    resultList.get(file).add(reference);
                                }
                            }
                        }
                    }
                    else {
                        throw new JobExecutionException("Job returned null as OBJ_ENTITIES from plugId: " + plugId);
                    }
                }
                else {
                    throw new JobExecutionException("Job execution returned null response from plugId: " + plugId);
                }
            }
            else {
                throw new JobExecutionException(response.getString("job_invoke_error_message"));
            }
        }

        return resultList;
    }

    /**
     * Get all uploads referenced in the given iPlug from data sets WITHOUT phases in additional file data tables.
     *
     * "Negative Vorpruefungen" is such dataset type for instance.
     *
     * @param plugId
     * @return Map<String, List<FileReference>>
     * @throws JobExecutionException
     */
    private Map<String, List<FileReference>> getUploadsFromDataSetsWithoutPhases(final String plugId) throws JobExecutionException {
        final Map<String, List<FileReference>> resultList = new HashMap<>();
        final IMdekCallerQuery mdekCallerQuery = this.connectionFacade.getMdekCallerQuery();

        if (mdekCallerQuery == null) {
            return resultList;
        }

        // get uploads from working copies and published objects
        // NOTE: it is only possible to execute "from ObjectNode oNode" HQL selects
        final String[] fks = new String[]{"objId", "objIdPublished"};
        for (final String fk : fks) {
            final String qString = "select fdLink.data, fdLink.parentFieldId, fdLink.sort, " +
                    " obj.objName, obj.objUuid, " +
                    " fdDocs.fieldKey, fdLink.sort " +
                "from ObjectNode oNode, " +
                    " T01Object obj, " +
                    " AdditionalFieldData fdDocs, " +
                    " AdditionalFieldData fdLink " +
                "where " +
                    " oNode."+fk+" = obj.id " +
                    " and obj.id = fdDocs.objId" +
                    " and fdDocs.id = fdLink.parentFieldId" +
                    " and fdLink.fieldKey = 'link'";

            final IngridDocument response = mdekCallerQuery.queryHQLToMap(plugId, qString, null, "");
            if (response.getBoolean(IJobRepository.JOB_INVOKE_SUCCESS)) {
                final IngridDocument result = MdekUtils.getResultFromResponse(response);
                if (result != null) {
                    @SuppressWarnings("unchecked")
                    final
                    List<IngridDocument> objs = (List<IngridDocument>) result.get(MdekKeys.OBJ_ENTITIES);
                    if (objs != null) {
                        for (final IngridDocument objEntity : objs) {
                            final String file = objEntity.getString("fdLink.data");

                            // exclude absolute links
                            if (!LINK_PATTERN.matcher(file).matches()) {
                                final String path = objEntity.getString("obj.objName") + "(" + objEntity.getString("obj.objUuid") + ")" + PATH_SEPARATOR +
                                        objEntity.getString("fdDocs.fieldKey") + PATH_SEPARATOR + objEntity.getInt("fdLink.sort");
                                String date = null;

                                // select expiry date (might not exist)
                                // NOTE: this is done in an extra query since HQL does not support
                                // left outer joins on unrelated tables in the used version
                                // NOTE: it is only possible to execute "from ObjectNode oNode" HQL selects
                                final Long parentId = objEntity.getLong("fdLink.parentFieldId");
                                final Integer sort = objEntity.getInt("fdLink.sort");
                                final String qStringSub = "select fdExpires.data " +
                                    "from ObjectNode oNode, " +
                                        " T01Object obj, " +
                                        " AdditionalFieldData fdDocs, " +
                                        " AdditionalFieldData fdExpires " +
                                    "where " +
                                        " oNode."+fk+" = obj.id " +
                                        " and obj.id = fdDocs.objId" +
                                        " and fdDocs.id = fdExpires.parentFieldId" +
                                        " and fdExpires.parentFieldId = "+parentId+
                                        " and fdExpires.sort = "+sort+
                                        " and fdExpires.fieldKey = 'expires'";
                                final IngridDocument responseSub = mdekCallerQuery.queryHQLToMap(plugId, qStringSub, null, "");
                                if (responseSub.getBoolean(IJobRepository.JOB_INVOKE_SUCCESS)) {
                                    final IngridDocument resultSub = MdekUtils.getResultFromResponse(responseSub);
                                    if (resultSub != null) {
                                        @SuppressWarnings("unchecked")
                                        final
                                        List<IngridDocument> objsSub = (List<IngridDocument>) resultSub.get(MdekKeys.OBJ_ENTITIES);
                                        if (objsSub != null && objsSub.size() == 1) {
                                            date = objsSub.get(0).getString("fdExpires.data");
                                        }
                                    }
                                    else {
                                        throw new JobExecutionException("Job execution returned null response from plugId: " + plugId);
                                    }
                                }
                                else {
                                    throw new JobExecutionException(responseSub.getString("job_invoke_error_message"));
                                }

                                // create file reference
                                final FileReference reference = new FileReference(
                                        file, path, (date != null && date.length() > 0) ? LocalDate.parse(date, dateFormatter) : null
                                );
                                if (!resultList.containsKey(file)) {
                                    final List<FileReference> references = new ArrayList<>();
                                    references.add(reference);
                                    resultList.put(file, references);
                                }
                                else {
                                    resultList.get(file).add(reference);
                                }
                            }
                        }
                    }
                    else {
                        throw new JobExecutionException("Job returned null as OBJ_ENTITIES from plugId: " + plugId);
                    }
                }
                else {
                    throw new JobExecutionException("Job execution returned null response from plugId: " + plugId);
                }
            }
            else {
                throw new JobExecutionException(response.getString("job_invoke_error_message"));
            }
        }

        return resultList;
    }

    /**
     * Cleanup
     * @param unreferencedFiles
     * @param expiredFiles
     * @param unexpiredFiles
     */
    private void doCleanup(
            final Map<String, StorageItem> unreferencedFiles,
            final Map<String, StorageItem> expiredFiles,
            final Map<String, StorageItem> unexpiredFiles
    ) {
        // delete unreferenced files
        log.debug("Deleting unreferenced files");
        int deleteCount = 0;
        for (final String file : unreferencedFiles.keySet()) {
            final StorageItem item = unreferencedFiles.get(file);
            // get file age
            final long age = LocalDateTime.from(item.getLastModifiedDate()).
                    until(this.referenceDateTime, ChronoUnit.SECONDS);
            if (this.deleteFileMinAge == null || age >= this.deleteFileMinAge) {
                log.info("Deleting file: "+file+" (age: "+age+" seconds)");
                try {
                    this.storage.delete(item.getPath(), item.getFile());
                    deleteCount++;
                }
                catch (final IOException e) {
                    // log error, but keep the job running
                    this.logError("File "+file+" could not be deleted", e);
                }
            }
            else {
                 log.debug("Keeping file: "+file+" (age: "+age+" seconds)");
            }
        }
        log.debug("Number of deleted files: "+deleteCount);

        // archive not archived expired files
        log.debug("Archiving expired files");
        int archiveCount = 0;
        for (final String file : expiredFiles.keySet()) {
            final StorageItem item = expiredFiles.get(file);
            log.info("Archiving file: "+file);
            try {
                this.storage.archive(item.getPath(), item.getFile());
                archiveCount++;
            }
            catch (final IOException e) {
                // log error, but keep the job running
                this.logError("File "+file+" could not be archived", e);
            }
        }
        log.debug("Number of archived files: "+archiveCount);

        // restore not expired archived files
        log.debug("Restoring archived files");
        int restoreCount = 0;
        for (final String file : unexpiredFiles.keySet()) {
            final StorageItem item = unexpiredFiles.get(file);
            log.info("Restoring file: "+file);
            try {
                this.storage.restore(item.getPath(), item.getFile());
                restoreCount++;
            }
            catch (final IOException e) {
                // log error, but keep the job running
                this.logError("File "+file+" could not be restored", e);
            }
        }
        log.debug("Number of restored files: "+restoreCount);

        // clean up
        log.debug("Cleaning up storage");
        try {
            this.storage.cleanup();
        }
        catch (final IOException e) {
            // log error, but keep the job running
            this.logError("Error cleaning up the storage", e);
        }
    }

    /**
     * Log an error
     * @param error
     * @param t
     */
    private void logError(final String error, final Throwable t) {
        log.error("Error while running UploadCleanupJob: "+error, t);
    }
}
