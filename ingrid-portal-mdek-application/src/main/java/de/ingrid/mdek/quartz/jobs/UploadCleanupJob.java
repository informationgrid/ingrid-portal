package de.ingrid.mdek.quartz.jobs;

import java.io.IOException;
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
import de.ingrid.mdek.upload.storage.Storage;
import de.ingrid.mdek.upload.storage.StorageItem;
import de.ingrid.mdek.util.MdekUtils;
import de.ingrid.utils.IngridDocument;

public class UploadCleanupJob extends QuartzJobBean {

    private final static Logger log = Logger.getLogger(UploadCleanupJob.class);

    private ConnectionFacade connectionFacade;
    private Storage storage;

    public void setConnectionFacade(ConnectionFacade connectionFacade) {
        this.connectionFacade = connectionFacade;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    @Override
    protected void executeInternal(JobExecutionContext ctx) throws JobExecutionException {
        log.debug("Executing UploadCleanupJob...");

        // collect all files from the file storage
        log.debug("Collecting files from the storage");
        StorageItem[] items;
        try {
            items = this.storage.list();
        }
        catch (IOException e) {
            throw new JobExecutionException(e);
        }
        log.debug("Number of files found in the storage: "+items.length);

        if (items.length > 0) {
            // prepare file map (path -> item)
            // files that are referenced in a document will be removed from the map
            Map<String, StorageItem> fileMap = new HashMap<String, StorageItem>();
            for (StorageItem item : items) {
                // initialize all files as not referenced
                fileMap.put(item.getUri(), item);
            }

            // collect file references from documents
            log.debug("Collecting file references from iplugs");
            IMdekClientCaller caller = this.connectionFacade.getMdekClientCaller();
            List<String> iplugList = caller.getRegisteredIPlugs();
            log.debug("Number of iplugs found: "+iplugList.size());
            for (String plugId : iplugList) {
                log.debug("Getting documents from iplug: "+plugId);
                Map<String, List<String>> uploads = this.getUploads(plugId);
                Set<String> fileReferences = uploads.keySet();

                log.debug("Number of documents: "+fileReferences.size());
                log.debug("Collecting file references from documents");
                for (String fileReference : fileReferences) {
                    if (fileMap.containsKey(fileReference)) {
                        // mark file as referenced
                        log.debug("Found file reference: "+fileReference+" (from document(s): "+uploads.get(fileReference)+")");
                        fileMap.remove(fileReference);
                    }
                }
            }

            // delete unreferenced files
            log.debug("Deleting unreferenced files");
            int deleteCount = 0;
            for (String file : fileMap.keySet()) {
                StorageItem item = fileMap.get(file);
                log.debug("Deleting file: "+file);
                try {
                    this.storage.delete(item.getPath(), item.getFile());
                    deleteCount++;
                }
                catch (IOException e) {
                    // log error, but keep the job running
                    log.error("File "+file+" could not be deleted: "+e);
                }
            }
            log.debug("Number of deleted files: "+deleteCount);
        }
        log.debug("Finished UploadCleanupJob");
    }

    /**
     * Get all uploads referenced in the given iPlug
     * @param plugId
     * @return Map<String, List<String>>
     */
    private Map<String, List<String>> getUploads(String plugId) {
        Map<String, List<String>> resultList = new HashMap<String, List<String>>();
        IMdekCallerQuery mdekCallerQuery = this.connectionFacade.getMdekCallerQuery();

        if (mdekCallerQuery == null) {
            return resultList;
        }

        // get uploads from working copies and published objects
        String[] fks = new String[]{"objId", "objIdPublished"};
        for (String fk : fks) {
            String qString = "select fdLink.data, obj.objName, obj.objUuid, " +
                    " fdRoot.fieldKey, fdPhase.fieldKey, fdPhase.fieldKey, fdDocs.fieldKey " +
                "from ObjectNode oNode, " +
                    " T01Object obj, " +
                    " AdditionalFieldData fdRoot, " +
                    " AdditionalFieldData fdPhase, " +
                    " AdditionalFieldData fdDocs, " +
                    " AdditionalFieldData fdLink " +
                "where " +
                    " oNode." + fk + " = obj.id " +
                    " and obj.id = fdRoot.objId" +
                    " and fdRoot.id = fdPhase.parentFieldId" +
                    " and fdPhase.id = fdDocs.parentFieldId" +
                    " and fdDocs.id = fdLink.parentFieldId" +
                    " and fdLink.fieldKey = 'link'";
            IngridDocument response = mdekCallerQuery.queryHQLToMap(plugId, qString, null, "");
            IngridDocument result = MdekUtils.getResultFromResponse(response);

            if (result != null) {
                @SuppressWarnings("unchecked")
                List<IngridDocument> objs = (List<IngridDocument>) result.get(MdekKeys.OBJ_ENTITIES);
                if (objs != null) {
                    for (IngridDocument objEntity : objs) {
                        String file = objEntity.getString("fdLink.data");
                        String path = objEntity.getString("obj.objName") + "[" + objEntity.getString("obj.objUuid") + "]/" +
                                objEntity.getString("fdRoot.fieldKey") + "/" + objEntity.getString("fdPhase.fieldKey") + "/" +
                                objEntity.getString("fdDocs.fieldKey");
                        if (!resultList.containsKey(file)) {
                            List<String> paths = new ArrayList<String>();
                            paths.add(path);
                            resultList.put(file, paths);
                        }
                        else {
                            resultList.get(file).add(path);
                        }
                    }
                }
            }
        }

        return resultList;
    }
}
