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
        if (log.isDebugEnabled()) {
            log.debug("Executing UpdateCodeListsFromIGEJob...");
        }
        
        CodeListService clService = getClServiceFromBean(jobExecutionContext);
        boolean success = clService.updateFromServer(getLastModifiedTimestampFromDb());
        
        if (log.isDebugEnabled()) {
            log.debug("UpdateCodeListsFromIGEJob finished! (successful = " + success + ")");
        }
    }

    private Long getLastModifiedTimestampFromDb() {
        List<String> iplugList = connectionFacade.getMdekClientCaller().getRegisteredIPlugs();
        Long lowestTimestamp = null;
        for (String iplug : iplugList) {
            IngridDocument response = connectionFacade.getMdekCallerCatalog().getLastModifiedTimestampOfSyslists(iplug, getCatAdminUuid(iplug));
            Long timestamp = MdekCatalogUtils.extractLastModifiedTimestampFromResponse(response);
            if (lowestTimestamp == null || lowestTimestamp > timestamp) {
                lowestTimestamp = timestamp;
            }
        }
        return lowestTimestamp;
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
