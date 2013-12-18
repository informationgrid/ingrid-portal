package de.ingrid.mdek.quartz.jobs;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.ingrid.codelists.CodeListService;
import de.ingrid.codelists.quartz.jobs.UpdateCodeListsJob;
import de.ingrid.mdek.MdekKeysSecurity;
import de.ingrid.mdek.caller.IMdekCallerSecurity;
import de.ingrid.mdek.caller.IMdekClientCaller;
import de.ingrid.mdek.handler.ConnectionFacade;
import de.ingrid.mdek.util.MdekCatalogUtils;
import de.ingrid.utils.IngridDocument;

public class UpdateCodeListsFromIGEJob extends UpdateCodeListsJob {

    private final static Logger log = Logger.getLogger(UpdateCodeListsFromIGEJob.class);
    private ConnectionFacade connectionFacade;
    
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext)
            throws JobExecutionException {
        log.info("Executing UpdateCodeListsFromIGEJob...");
        
        CodeListService clService = getClServiceFromBean(jobExecutionContext);
        Long timestamp = getLastModifiedTimestampFromDb();
        
        // if at least one iPlug was found/connected
        if (timestamp != null) {
            Object modifiedCodelists = clService.updateFromServer(timestamp);
            
            // update db with initial codelists from service if no codelist could be fetched
            // and there never has been an update to the db (timestamp does not exist!)
            if (modifiedCodelists == null && timestamp == -1) {
                clService.persistToAll(clService.getCodeLists());
            }
            
            // reindex codelist changes if codelists have changed or initially updated
            if (modifiedCodelists != null || timestamp == -1) {
            	updateIndexForAllIPlugs();
            }
            
            log.info("UpdateCodeListsFromIGEJob finished! (timestamp = " + timestamp + ", successful = " + (modifiedCodelists == null ? false : true) + ")");
        } else {
            log.info("No iPlug connected to update codelists to!");
        }
    }
    
    private void updateIndexForAllIPlugs() {
    	IMdekClientCaller caller = connectionFacade.getMdekClientCaller();
        List<String> iplugList = caller.getRegisteredIPlugs();
        log.debug("Number of iplugs found: "+iplugList.size());
        for (String plugId : iplugList) {
            String user = getCatAdminUuid(plugId);
	         // rebuild syslist so that all documents have updated syslist values
	         // for those who allow free entries!
	         try {
	             rebuildSyslists(plugId, user);
	         } catch (UndeclaredThrowableException e) {
	             // a timeout exception is normal for a longer running process
	         }
        }
    }
    
    private boolean rebuildSyslists(String plugId, String catAdminUuid) {
        if (log.isDebugEnabled()) {
            log.debug("Call backend to rebuild syslist data (reindex) ...");
        }
        IngridDocument response = connectionFacade.getMdekCallerCatalog().rebuildSyslistData(plugId, catAdminUuid);
        if (null == de.ingrid.mdek.util.MdekUtils.getResultFromResponse(response)) {
            return false;
        }
        return true;
    }

    private Long getLastModifiedTimestampFromDb() {
        List<String> iplugList = connectionFacade.getMdekClientCaller().getRegisteredIPlugs();
        
        if (iplugList.isEmpty())
            return null;
        
        Long lowestTimestamp = null;
        for (String iplug : iplugList) {
            IngridDocument response = connectionFacade.getMdekCallerCatalog().getLastModifiedTimestampOfSyslists(iplug, getCatAdminUuid(iplug));
            Long timestamp = MdekCatalogUtils.extractLastModifiedTimestampFromResponse(response);
            if (lowestTimestamp == null || lowestTimestamp > timestamp) {
                lowestTimestamp = timestamp;
            }
        }
        return lowestTimestamp == null ? -1 : lowestTimestamp;
    }
    
    private String getCatAdminUuid(String plugId) {
        try {
            IMdekCallerSecurity mdekCallerSecurity = connectionFacade.getMdekCallerSecurity();
            IngridDocument response = mdekCallerSecurity.getCatalogAdmin(plugId, "");
            IngridDocument result = de.ingrid.mdek.util.MdekUtils.getResultFromResponse(response);
            return (String) result.get(MdekKeysSecurity.IDC_USER_ADDR_UUID);
        } catch (Exception e) {
            return "";
        }
    }
    
    public void setConnectionFacade(ConnectionFacade connectionFacade) {
        this.connectionFacade = connectionFacade;
    }
}
