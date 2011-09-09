/*
 * Copyright (c) 2007 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.scheduler.jobs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
    
    
/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class IngridMonitorCSWJob extends IngridMonitorAbstractJob {

	public static final String STATUS_CODE_ERROR_QUERY_PARSE_EXCEPTION = "component.monitor.iplug.error.query.parse.exception";

	public static final String STATUS_CODE_ERROR_INVALID_SERVICE_URL = "component.monitor.general.error.invalid.service.url";

	public static final String STATUS_CODE_ERROR_INVALID_HTTP_REQUEST = "component.monitor.general.g2k.invalid.http.request";

	public static final String COMPONENT_TYPE = "component.monitor.general.type.csw";

	private final static Log log = LogFactory.getLog(IngridMonitorCSWJob.class);

	/**
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	public void execute(JobExecutionContext context) throws JobExecutionException {

		JobDataMap dataMap = context.getJobDetail().getJobDataMap();
		long startTime = 0;

		boolean isActive = dataMap.getInt(PARAM_ACTIVE) == 1;
		if (!isActive) {
			if (log.isDebugEnabled()) {
				log.debug("Job (" + context.getJobDetail().getName() + ") is inactive. Exiting."); 
			}
			return;
		} else {
			if (log.isDebugEnabled()) {
				startTime = System.currentTimeMillis();
				log.debug("Job (" + context.getJobDetail().getName() + ") is executed...");
			}
		}

		String query = dataMap.getString(PARAM_QUERY);

		int timeout;
		try {
			timeout = dataMap.getInt(PARAM_TIMEOUT);
		} catch (Exception e1) {
			timeout = 0;
		}
		if (timeout == 0) {
			timeout = 30000;
			dataMap.put(PARAM_TIMEOUT, timeout);
		}
		String serviceUrl = dataMap.getString(IngridMonitorAbstractJob.PARAM_SERVICE_URL);

		int status = 0;
		String statusCode = null;
		try {
			startTimer();
			
			URL url = new URL(serviceUrl);
		    URLConnection conn = url.openConnection();
		    conn.setDoOutput(true);
		    conn.setRequestProperty("CONTENT-TYPE", "text/xml");
		    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		    wr.write(getCSWRequestString(query, "2.0.2"));
		    wr.flush();

		    // Get the response
		    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		    String line;
		    String numberOfMatchedHitsStr = null;
            int numberOfMatchedHits = 0;
		    while ((line = rd.readLine()) != null) {
		        // Process line...
		        int start = line.indexOf("numberOfRecordsMatched");
		        if (start != -1) {
		            numberOfMatchedHitsStr = line.substring(start+24, line.indexOf("\"", start+24));
		            numberOfMatchedHits = Integer.parseInt(numberOfMatchedHitsStr);
		            break;
		        }
		    }
		    wr.close();
		    rd.close();
			
			if (numberOfMatchedHits == 0) {
				status = STATUS_ERROR;
				statusCode = STATUS_CODE_ERROR_NO_HITS;
			} else {
				status = STATUS_OK;
				statusCode = STATUS_CODE_NO_ERROR;
			}
		} catch (MalformedURLException e) {
			status = STATUS_ERROR;
			statusCode = STATUS_CODE_ERROR_INVALID_SERVICE_URL;
			if (log.isDebugEnabled()) {
				log.debug("Error processing URL Request.", e);
			}
		} catch (SocketTimeoutException e) {
			status = STATUS_ERROR;
			statusCode = STATUS_CODE_ERROR_TIMEOUT;
			if (log.isDebugEnabled()) {
				log.debug("Error processing URL Request.", e);
			}
		} catch (IOException e) {
			status = STATUS_ERROR;
			statusCode = STATUS_CODE_ERROR_TIMEOUT;
			if (log.isDebugEnabled()) {
				log.debug("Error processing URL Request.", e);
			}
		} catch (Throwable e) {
			status = STATUS_ERROR;
			statusCode = STATUS_CODE_ERROR_UNSPECIFIC;
			if (log.isDebugEnabled()) {
				e.printStackTrace();
			}	
		}

		computeTime(dataMap, stopTimer());
		
		updateJobData(context, status, statusCode);
		sendAlertMail(context);
		updateJob(context);

		if (log.isDebugEnabled()) {
			log.debug("Job (" + context.getJobDetail().getName() + ") finished in "
					+ (System.currentTimeMillis() - startTime) + " ms.");
		}
	}
	
	private String getCSWRequestString(String query, String version) {
	    return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
	            "<csw:GetRecords maxRecords=\"1\" outputSchema=\"http://www.isotc211.org/2005/gmd\" " +
	    		"resultType=\"results\" outputFormat=\"application/xml\" service=\"CSW\" startPosition=\"1\" " +
	    		"version=\""+version+"\" xmlns:csw=\"http://www.opengis.net/cat/csw/2.0.2\">" +
	    		"<csw:Query typeNames=\"csw:datasetcollection\">" +
    	    	    "<csw:ElementSetName>brief</csw:ElementSetName>" +
    	    	    "<csw:Constraint version=\"1.1.0\">" +
    	    	      "<csw:CqlText>AnyText like '%"+query+"%'</csw:CqlText>"+
    	    	    "</csw:Constraint>" +
    	    	  "</csw:Query>" +
	    		"</csw:GetRecords>";
	}
}
