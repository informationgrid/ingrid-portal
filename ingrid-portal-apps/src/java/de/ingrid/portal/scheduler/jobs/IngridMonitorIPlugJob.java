/*
 * Copyright (c) 2007 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.scheduler.jobs;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.ParseException;
import de.ingrid.utils.queryparser.QueryStringParser;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class IngridMonitorIPlugJob extends IngridMonitorAbstractJob {

    public static String ERROR_QUERY_PARSE_EXCEPTION = "component.monitor.iplug.error.query.parse.exception";
    
    /**
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     */
    public void execute(JobExecutionContext context) throws JobExecutionException {
        
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        String query = dataMap.getString(QUERY);
        int timeout = dataMap.getInt(TIMEOUT);
        if (timeout == 0) {
            timeout = 30000;
            dataMap.put(TIMEOUT, timeout);
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.GERMAN);
        dataMap.put(LAST_CHECK, dateFormat.format(new Date()));
        
        try {
            IngridQuery q = QueryStringParser.parse(query);
            IngridHits hits = IBUSInterfaceImpl.getInstance().search(q, 10, 1, 1, timeout);
            if (hits.length() == 0) {
                dataMap.putAsString(STATUS, STATUS_ERROR);
                dataMap.put(STATUS_CODE, ERROR_NO_HITS);
            } else {
                dataMap.putAsString(STATUS, STATUS_OK);
                dataMap.put(STATUS_CODE, NO_ERROR);
            }
        } catch (ParseException e) {
            dataMap.putAsString(STATUS, STATUS_ERROR);
            dataMap.put(STATUS_CODE, ERROR_QUERY_PARSE_EXCEPTION);
        } catch (InterruptedException e) {
            dataMap.putAsString(STATUS, STATUS_ERROR);
            dataMap.put(STATUS_CODE, ERROR_TIMEOUT);
        } catch (Throwable e) {
            dataMap.putAsString(STATUS, STATUS_ERROR);
            dataMap.put(STATUS_CODE, ERROR_UNSPECIFIC);
        }
    }
    
    

}
