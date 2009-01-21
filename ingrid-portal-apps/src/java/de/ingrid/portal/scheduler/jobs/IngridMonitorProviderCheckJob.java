/*
 * Copyright (c) 2007 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.scheduler.jobs;

import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.ingrid.ibus.client.BusClient;
import de.ingrid.portal.hibernate.HibernateUtil;
import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;
import de.ingrid.portal.om.IngridProvider;
import de.ingrid.utils.PlugDescription;
import de.ingrid.utils.metadata.AbstractIPlugOperatorInjector;
import de.ingrid.utils.metadata.Metadata;
import de.ingrid.utils.metadata.AbstractIPlugOperatorInjector.IPlugOperator;
import de.ingrid.utils.metadata.AbstractIPlugOperatorInjector.Partner;
import de.ingrid.utils.metadata.AbstractIPlugOperatorInjector.Provider;


/**
 * This job checks all connected iPlugs for their provider and compares them
 * with the one stored in the local database. If a provider is not found, it
 * means that the long name isn't available either which will result in an 
 * incomplete search result display.  
 * 
 * @author andre.wallat@wemove.com
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
		List<String> exclude		= new ArrayList<String>();
		Session session 			= HibernateUtil.currentSession();
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
			
			List<String> allProviderInDB = new ArrayList<String>();
			
			// get the short names of all providers of all iPlugs
			for (IngridProvider ip : allIngridProviderInDB) {
				allProviderInDB.add(ip.getIdent());
			}
			
			// get from each iPlug the containing provider and check if their provider
			// are also in the local DB
			for (int i=0; i<hits.length; i++) {
				ArrayList<Provider> missingProvider = new ArrayList<Provider>();
				List<Provider> iPlugProvider = getProvider(hits[i]);
				
				for (Provider provider : iPlugProvider) {
					if (!allProviderInDB.contains(provider.getShortName()) && !exclude.contains(provider.getShortName())) {
						missingProvider.add(provider);
						/*
						// ==========================
						Metadata metadata = BusClient.instance().getBus().getMetadata(hits[i].getPlugId());
						
						if (metadata != null) {
							IPlugOperator plugOperator = (IPlugOperator) metadata.getMetadata(AbstractIPlugOperatorInjector.IPLUG_OPERATOR);
						
							// if more data could be found then get the missing long name
							if (plugOperator != null) {
								List<Partner> allPartner = plugOperator.getPartners();
								for (Partner partner: allPartner) {
									String providerLongName = partner.getProvider(provider).getDisplayName();
									log.debug(" Long: " + providerLongName);
								}
							}
						}
						// ==========================
					 */
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
		String webErrorMessage = statusCode.replace(NEW_LINE_PLAIN, NEW_LINE_WEB);
		
		// has to be the web version, otherwise event-occurrences wouldn't be counted correctly
		updateJobData(context, status, webErrorMessage);
		
		dataMap.put(PARAM_STATUS_CODE, statusCode);
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
	
	private List<Provider> getProvider( PlugDescription plugDescripton ) {
		List<Provider> allIPlugProvider	= new ArrayList<Provider>();
		Metadata metadata 				= null;
		IPlugOperator plugOperator 		= null;
		
		try {
			metadata = BusClient.instance().getBus().getMetadata(plugDescripton.getPlugId());
		} catch (IOException e) {
			if (log.isDebugEnabled()) {
				log.debug("Error getting MetaData from bus for iPlug: " + plugDescripton.getPlugId());
			}
		} catch (UndeclaredThrowableException e) {
			if (log.isDebugEnabled()) {
				log.debug("Error getting MetaData from bus for iPlug: " + plugDescripton.getPlugId() + 
					". Does the method exist? (" + e.getLocalizedMessage() + ")");
			}
		}
				
		if (metadata != null) {
			plugOperator = (IPlugOperator) metadata.getMetadata(AbstractIPlugOperatorInjector.IPLUG_OPERATOR);
		
			// if more data could be found then get the missing long name
			if (plugOperator != null) {
				List<Partner> allPartner = plugOperator.getPartners();
				for (Partner partner: allPartner) {
					allIPlugProvider.addAll(partner.getProviders());
				}
			}
		} 
		
		// backward compatibility
		// information is stored in plugDescription directly and not in metadata
		if (metadata == null || plugOperator == null) {
			String[] provider = plugDescripton.getProviders();
			for (String p : provider) {
				Provider newProvider = new Provider();
				newProvider.setShortName(p);
				allIPlugProvider.add(newProvider);
			}
		}
		
		return allIPlugProvider;
	}
	
	private String writeErrorMessage(PlugDescription plugDesc, ArrayList<Provider> missingProvider ) {
		String errorMessage = "";
		
		if (!missingProvider.isEmpty()) {
			String newLine = NEW_LINE_PLAIN;
			
			//errorMessage += newLine;
			errorMessage += "iPlug-Name: " + plugDesc.getDataSourceName() + newLine;
			errorMessage += "Ansprechpartner: " + plugDesc.getPersonSureName() + " " + plugDesc.getPersonName() + newLine;
			errorMessage += "Email: " + plugDesc.getPersonMail() + newLine;
			errorMessage += "iPlug-ID: " + plugDesc.getPlugId() + newLine;
			errorMessage += "Fehlende Anbieter: ";
			for (Provider p : missingProvider) {
				errorMessage += p.getShortName() + ",";
			}
			// remove trailing ','
			errorMessage = errorMessage.substring(0, errorMessage.length()-1);
			errorMessage += newLine + newLine;
		}
		
		return errorMessage;
	}
}
