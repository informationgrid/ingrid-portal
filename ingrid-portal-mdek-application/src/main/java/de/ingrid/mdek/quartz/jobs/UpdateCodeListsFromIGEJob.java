package de.ingrid.mdek.quartz.jobs;

import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.ingrid.codelists.CodeListService;
import de.ingrid.codelists.quartz.jobs.UpdateCodeListsJob;
import de.ingrid.mdek.MdekKeysSecurity;
import de.ingrid.mdek.caller.IMdekCallerSecurity;
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
            boolean success = clService.updateFromServer(timestamp);
            
            // update db with initial codelists from service if no codelist could be fetched
            // and there never has been an update to the db (timestamp does not exist!)
            if (success == false && timestamp == -1) {
                clService.persistToAll(clService.getCodeLists());
            }
            
            log.info("UpdateCodeListsFromIGEJob finished! (successful = " + success + ")");
        } else {
            log.info("No iPlug connected to update codelists to!");
        }
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
