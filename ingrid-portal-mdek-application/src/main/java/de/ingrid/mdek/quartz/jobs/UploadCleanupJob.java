package de.ingrid.mdek.quartz.jobs;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    private final static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final static Logger log = Logger.getLogger(UploadCleanupJob.class);

    public class FileReference {
    	public String file;
        public String path;
        public LocalDate expiryDate;

        public FileReference(String file, String path, LocalDate expiryDate) {
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
    private LocalDate referenceDate = LocalDate.now();

    /**
     * Set the connection facade used for communication.
     * @param connectionFacade
     */
    public void setConnectionFacade(ConnectionFacade connectionFacade) {
        this.connectionFacade = connectionFacade;
    }

    /**
     * Set the storage to cleanup.
     * @param storage
     */
    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    /**
     * Set the reference date for calculating expiry of files.
     * Defaults to today if not set explicitly.
     * @param referenceDate
     */
    public void setReferenceDate(LocalDate referenceDate) {
        this.referenceDate = referenceDate;
    }

    @Override
    protected void executeInternal(JobExecutionContext ctx) throws JobExecutionException {
        log.debug("Executing UploadCleanupJob...");

        // initialize file maps
        Map<String, StorageItem> allFiles = new HashMap<String, StorageItem>();
        Map<String, StorageItem> unreferencedFiles = new HashMap<String, StorageItem>();
        Map<String, StorageItem> expiredFiles = new HashMap<String, StorageItem>();
        Map<String, StorageItem> unexpiredFiles = new HashMap<String, StorageItem>();
        try {
            // collect all files from the file storage
            log.debug("Collecting files from the storage");
            StorageItem[] fileItems = this.storage.list();
            log.debug("Number of files found in the storage: "+fileItems.length);

            if (fileItems.length > 0) {
                 // initialize file maps
                for (StorageItem item : fileItems) {
                    String uri = item.getUri();
                    allFiles.put(uri, item);

                    // put all files in the unreferenced map first
                    // files that are referenced in a document will be removed from the map
                    unreferencedFiles.put(uri, item);
                }

                // collect file references from documents for each catalog
                log.debug("Collecting file references from iplugs");
                IMdekClientCaller caller = this.connectionFacade.getMdekClientCaller();
                List<String> iplugList = caller.getRegisteredIPlugs();
                log.debug("Number of iplugs found: "+iplugList.size());
                for (String plugId : iplugList) {
                    log.debug("Getting documents from iplug: "+plugId);
                    Map<String, List<FileReference>> uploads = this.getUploads(plugId);
                    Set<String> uploadUris = uploads.keySet();

                    log.debug("Number of documents: "+uploadUris.size());
                    log.debug("Collecting file references from documents");
                    for (String uploadUri : uploadUris) {
                        // get the references
                        List<FileReference> references = uploads.get(uploadUri);
                        log.debug("Found reference: "+uploadUri+" in "+references.size()+" document(s): "+references+")");

                        // remove referenced file from unreferenced list
                        if (unreferencedFiles.containsKey(uploadUri)) {
                            unreferencedFiles.remove(uploadUri);
                        }

                        // get the storage item for the reference
                        StorageItem item = allFiles.get(uploadUri);
                        if (item == null) {
                            log.warn("File "+uploadUri+" does not exist in the storage");
                            continue;
                        }

                        // get latest expire date from references
                        // null values count as infinite
                        LocalDate expiryDate = null;
                        for (FileReference reference : references) {
                            if (expiryDate == null ||
                                    (reference.expiryDate == null || reference.expiryDate.isAfter(expiryDate))) {
                                expiryDate = reference.expiryDate;
                            }
                        }
                        // handle expired files
                        // NOTE: a file might be expired in one catalog, but not in another,
                        // so if a file is added/removed by one catalog, it might be removed/added by another
                        // but at the end all expired files (and only those) must be contained in the expired files list
                        if (!item.isArchived() && expiryDate != null && expiryDate.isBefore(this.referenceDate)) {
                            expiredFiles.put(uploadUri, item);
                        }
                        else if (expiredFiles.containsKey(uploadUri)) {
                            expiredFiles.remove(uploadUri);
                        }
                        // handle files to be restored
                        if (item.isArchived() &&
                                (expiryDate == null || expiryDate.isEqual(this.referenceDate) || expiryDate.isAfter(this.referenceDate))) {
                            unexpiredFiles.put(uploadUri, item);
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            // any exception in this stage must abort the job
            // to prevent falsely deletion of files
            this.logError(e.toString(), e);
            log.debug("Aborted UploadCleanupJob");
            return;
        }

        // delete unreferenced files
        log.debug("Deleting unreferenced files");
        int deleteCount = 0;
        for (String file : unreferencedFiles.keySet()) {
            StorageItem item = unreferencedFiles.get(file);
            log.info("Deleting file: "+file);
            try {
                this.storage.delete(item.getPath(), item.getFile());
                deleteCount++;
            }
            catch (IOException e) {
                // log error, but keep the job running
                this.logError("File "+file+" could not be deleted", e);
            }
        }
        log.debug("Number of deleted files: "+deleteCount);

        // archive not archived expired files
        log.debug("Archiving expired files");
        int archiveCount = 0;
        for (String file : expiredFiles.keySet()) {
            StorageItem item = expiredFiles.get(file);
            log.info("Archiving file: "+file);
            try {
                this.storage.archive(item.getPath(), item.getFile());
                archiveCount++;
            }
            catch (IOException e) {
                // log error, but keep the job running
                this.logError("File "+file+" could not be archived", e);
            }
        }
        log.debug("Number of archived files: "+archiveCount);

        // restore not expired archived files
        log.debug("Restoring archived files");
        int restoreCount = 0;
        for (String file : unexpiredFiles.keySet()) {
            StorageItem item = unexpiredFiles.get(file);
            log.info("Restoring file: "+file);
            try {
                this.storage.restore(item.getPath(), item.getFile());
                restoreCount++;
            }
            catch (IOException e) {
                // log error, but keep the job running
                this.logError("File "+file+" could not be restored", e);
            }
        }
        log.debug("Number of restored files: "+restoreCount);

        log.debug("Finished UploadCleanupJob");
    }

    /**
     * Get all uploads referenced in the given iPlug
     * @param plugId
     * @return Map<String, List<FileReference>>
     * @throws JobExecutionException
     */
    private Map<String, List<FileReference>> getUploads(String plugId) throws JobExecutionException {
        Map<String, List<FileReference>> resultList = new HashMap<String, List<FileReference>>();
        IMdekCallerQuery mdekCallerQuery = this.connectionFacade.getMdekCallerQuery();

        if (mdekCallerQuery == null) {
            return resultList;
        }

        // get uploads from working copies and published objects
        String[] fks = new String[]{"objId", "objIdPublished"};
        for (String fk : fks) {
            String qString = "select fdLink.data, fdLink.parentFieldId, fdLink.sort, " +
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
            IngridDocument response = mdekCallerQuery.queryHQLToMap(plugId, qString, null, "");
            if (response.getBoolean(IJobRepository.JOB_INVOKE_SUCCESS)) {
                IngridDocument result = MdekUtils.getResultFromResponse(response);
                if (result != null) {
                    @SuppressWarnings("unchecked")
                    List<IngridDocument> objs = (List<IngridDocument>) result.get(MdekKeys.OBJ_ENTITIES);
                    if (objs != null) {
                        for (IngridDocument objEntity : objs) {
                            String file = objEntity.getString("fdLink.data");
                            String path = objEntity.getString("obj.objName") + "(" + objEntity.getString("obj.objUuid") + ")/" +
                                    objEntity.getString("fdRoot.fieldKey") + "/" + objEntity.getString("fdPhase.fieldKey") + "/" +
                                    objEntity.getString("fdDocs.fieldKey") + "/" + objEntity.getInt("fdLink.sort");
                            String date = null;

                            // select expiry date (might not exist)
                            // NOTE: this is done in an extra query since HQL does not support
                            // left outer joins on unrelated tables in the used version
                            Long parentId = objEntity.getLong("fdLink.parentFieldId");
                            Integer sort = objEntity.getInt("fdLink.sort");
                            String qStringSub = "select fdExpires.data " +
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
                            IngridDocument responseSub = mdekCallerQuery.queryHQLToMap(plugId, qStringSub, null, "");
                            if (responseSub.getBoolean(IJobRepository.JOB_INVOKE_SUCCESS)) {
                                IngridDocument resultSub = MdekUtils.getResultFromResponse(responseSub);
                                if (resultSub != null) {
                                    @SuppressWarnings("unchecked")
                                    List<IngridDocument> objsSub = (List<IngridDocument>) resultSub.get(MdekKeys.OBJ_ENTITIES);
                                    if (objsSub != null && objsSub.size() == 1) {
                                        date = objsSub.get(0).getString("fdExpires.data");
                                    }
                                }
                            }

                            // create file reference
                            FileReference reference = new FileReference(
                                    file, path, (date != null && date.length() > 0) ? LocalDate.parse(date, dateFormatter) : null
                            );
                            if (!resultList.containsKey(file)) {
                                List<FileReference> references = new ArrayList<FileReference>();
                                references.add(reference);
                                resultList.put(file, references);
                            }
                            else {
                                resultList.get(file).add(reference);
                            }
                        }
                    }
                }
            }
            else {
                throw new JobExecutionException(response.getString("job_invoke_error_message"));
            }
        }

        return resultList;
    }

    /**
     * Log an error
     * @param error
     * @param t
     */
    private void logError(String error, Throwable t) {
        log.error("Error while running UploadCleanupJob: "+error, t);
    }
}
