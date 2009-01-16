/*
 * Copyright (c) 2007 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.scheduler.jobs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.plist.XMLPropertyListConfiguration.PListNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.ingrid.portal.hibernate.HibernateUtil;
import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;
import de.ingrid.portal.om.IngridProvider;
import de.ingrid.portal.om.IngridRSSSource;
import de.ingrid.utils.PlugDescription;


/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class IngridMonitorProviderCheckJob extends IngridMonitorAbstractJob {

	public static final String COMPONENT_TYPE 	= "component.monitor.general.type.provider.check";
	
	public static final String JOB_ID 			= "ProviderCheck";

	private final static Log log 				= LogFactory.getLog(IngridMonitorProviderCheckJob.class);
	
	private final String NEW_LINE_PLAIN			= "\r\n";
	
	private final String NEW_LINE_WEB			= "<br>";

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

		int status 					= 0;
		String statusCode 			= null;
		HashMap allProvider 		= new HashMap();
		List<String> exclude		= new ArrayList();
		Session session 			= HibernateUtil.currentSession();
		boolean found				= false;
		String errorMessage			= "";
//        Transaction tx = null;
        
		try {
			startTimer();
			
			// get all iPlugs connected to the iBus
			PlugDescription[] hits = IBUSInterfaceImpl.getInstance().getAllIPlugs();
			
			// get all known provider from the database
			List<IngridProvider> allIngridProviderInDB = session.createCriteria(IngridProvider.class).list();
			
			// list of provider that shall be excluded
			if (dataMap.containsKey(PARAM_EXCLUDED_PROVIDER)) { // can happen in the beginning
				exclude = Arrays.asList(dataMap.getString(PARAM_EXCLUDED_PROVIDER).split(","));
			}
			
			List<String> allProviderInDB = new ArrayList();
			
			// get the short names of all providers of all iPlugs
			for (IngridProvider ip : allIngridProviderInDB) {
				allProviderInDB.add(ip.getIdent());
			}
			
			// get from each iPlug the containing provider and check if their provider
			// are also in the local DB
			for (int i=0; i<hits.length; i++) {
				ArrayList missingProvider = new ArrayList();
				for (String provider : hits[i].getProviders()) {
					if (!allProviderInDB.contains(provider) && !exclude.contains(provider)) {
						missingProvider.add(provider);
					}
				}
				if (!missingProvider.isEmpty()) {
					errorMessage += writeErrorMessage(hits[i], missingProvider);
				}
			}
			
			computeTime(dataMap, stopTimer());
			
			if (hits.length == 0) {
				status = STATUS_ERROR;
				statusCode = STATUS_CODE_ERROR_NO_IPLUGS;
			} else if (errorMessage != "") {
				status = STATUS_ERROR;
				statusCode = errorMessage;
			} else {
				status = STATUS_OK;
				statusCode = STATUS_CODE_NO_ERROR;
			}
		} catch (Throwable e) {
			status = STATUS_ERROR;
			statusCode = e.getClass().getName() + ":" + e.getMessage();
			e.printStackTrace();
		}
		
		// make the errorMessage more readable in the browser
		String webErrorMessage = errorMessage.replace(NEW_LINE_PLAIN, NEW_LINE_WEB);
		
		// has to be the web version, otherwise event-occurrences wouldn't be counted correctly
		updateJobData(context, status, webErrorMessage);
		
		dataMap.put(PARAM_STATUS_CODE, errorMessage);
		sendAlertMail(context);
		
		// set here finally the status code for the web browser
		//*************************************
		dataMap.put(PARAM_STATUS_CODE, webErrorMessage);
		//*************************************
		
		updateJob(context);

		if (log.isDebugEnabled()) {
			log.debug("Job (" + context.getJobDetail().getName() + ") finished in "
					+ (System.currentTimeMillis() - startTime) + " ms.");
		}
	}
	
	private String writeErrorMessage(PlugDescription plugDesc, ArrayList missingProvider ) {
		String errorMessage = "";
		
		if (!missingProvider.isEmpty()) {
			String newLine = NEW_LINE_PLAIN;
			
			errorMessage += plugDesc.getPlugId() + newLine;
			errorMessage += plugDesc.getPersonSureName() + " " + plugDesc.getPersonName() + newLine;
			errorMessage += plugDesc.getPersonMail() + newLine;
			errorMessage += plugDesc.getPlugId() + newLine;
			errorMessage += "Missing provider: " + missingProvider.toString() + newLine;
			errorMessage += newLine;
		}
		
		return errorMessage;
	}
}
