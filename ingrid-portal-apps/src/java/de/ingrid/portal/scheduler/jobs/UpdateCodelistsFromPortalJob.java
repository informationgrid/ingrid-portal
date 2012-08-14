package de.ingrid.portal.scheduler.jobs;

import org.apache.log4j.Logger;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.ingrid.codelists.CodeListService;
import de.ingrid.portal.global.CodeListServiceFactory;

public class UpdateCodelistsFromPortalJob extends IngridMonitorAbstractJob {

    private final static Logger log = Logger.getLogger(UpdateCodelistsFromPortalJob.class);

    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap  = context.getJobDetail().getJobDataMap();
        int status          = 0;
        String statusCode   = null;
        
        startTimer();
        
        log.info("Executing UpdateCodelistsFromPortalJob...");
        CodeListService codelistService = CodeListServiceFactory.instance();
        Object modifiedCodelists = codelistService.updateFromServer(codelistService.getLastModifiedTimestamp());
        if (modifiedCodelists != null) {
            status = STATUS_OK;
            statusCode = STATUS_CODE_NO_ERROR;
        } else {
            status = STATUS_ERROR;
            statusCode = STATUS_CODE_ERROR_NO_HITS;
        }
        
        log.info("UpdateCodelistsFromPortalJob finished! (successful = " + (modifiedCodelists == null ? false : true) + ")");
        
        computeTime(dataMap, stopTimer());
        updateJobData(context, status, statusCode);
        updateJob(context);
    }
    
}
