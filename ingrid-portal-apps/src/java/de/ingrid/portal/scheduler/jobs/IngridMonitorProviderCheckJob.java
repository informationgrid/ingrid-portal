/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.portal.scheduler.jobs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.Session;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.ingrid.ibus.client.BusClientFactory;
import de.ingrid.portal.global.UtilsDB;
import de.ingrid.portal.hibernate.HibernateUtil;
import de.ingrid.portal.om.IngridPartner;
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

	private static final Logger log 				= LoggerFactory.getLogger(IngridMonitorProviderCheckJob.class);
	
	private static final String NEW_LINE_PLAIN			= "\r\n";
	
	private static final String NEW_LINE_WEB			= "<br>";

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

		int status 						= 0;
		String statusCode 				= null;
		List<String> exclude			= new ArrayList<>();
		Map<String,String> resultsProvider	= new HashMap<>();
		Session session 				= HibernateUtil.currentSession();
		String errorMessage				= "";
        
		try {
			startTimer();
			
			// get all iPlugs connected to the iBus
			PlugDescription[] hits = BusClientFactory.createBusClient().getNonCacheableIBus().getAllIPlugs();
			
			// get all known provider from the database
			List<IngridProvider> allIngridProviderInDB = session.createCriteria(IngridProvider.class).list();
			
			// list of provider that shall be excluded
			if (dataMap.containsKey(PARAM_EXCLUDED_PROVIDER)) { // can happen in the beginning
				exclude = Arrays.asList(dataMap.getString(PARAM_EXCLUDED_PROVIDER).split(","));
			}
			
			List<String> allProviderInDB = new ArrayList<>();
			
			// get the short names of all providers of all iPlugs
			for (IngridProvider ip : allIngridProviderInDB) {
				allProviderInDB.add(ip.getIdent());
			}
			
			// get from each iPlug the containing provider and check if their provider
			// are also in the local DB
			for (int i=0; i<hits.length; i++) {
				Map<String, List> missingProvider = new HashMap();
				Map<String, List> iPlugProvider = getProvider(hits[i]);
				
				for (Map.Entry<String, List> entry : iPlugProvider.entrySet()) {
					String partner = entry.getKey();
					List<Provider> providers = (List)entry.getValue();
					for (Provider provider : providers) {
						if (!allProviderInDB.contains(provider.getShortName()) && !exclude.contains(provider.getShortName())) {
							if (missingProvider.get(partner) == null) {
								missingProvider.put(partner, new ArrayList());
							}
							missingProvider.get(partner).add(provider);
						}
					}
				}
				
				if (!missingProvider.isEmpty()) {
					errorMessage += writeErrorMessage(hits[i], missingProvider);
				}
				
				addProviderToLocalDB(missingProvider, resultsProvider);
			}
			
			errorMessage += writeUpdateSummary(resultsProvider);
			
			computeTime(dataMap, stopTimer());
			
			if (hits.length == 0) {
				status = STATUS_ERROR;
				statusCode = STATUS_CODE_ERROR_NO_IPLUGS;
			} else if (!errorMessage.equals("")) {
				status = STATUS_ERROR;
				statusCode = errorMessage;
			} else {
				status = STATUS_OK;
				statusCode = STATUS_CODE_NO_ERROR;
			}
		} catch (Exception e) {
			status = STATUS_ERROR;
			statusCode = e.getClass().getName() + ":" + e.getMessage();
			log.error("Error on execute.", e);
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
	
	/**
	 * Get all provider from the metadata of all iPlugs. If no metadata could be found
	 * it'll be tried to get the information from the plugdescription.
	 * @param plugDescripton
	 * @return
	 */
	private Map getProvider( PlugDescription plugDescripton ) {
		Map<String,List> allIPlugProvider	= new HashMap<>();
		Metadata metadata 					= null;
		IPlugOperator plugOperator 			= null;
		
		try {
			metadata = BusClientFactory.createBusClient().getNonCacheableIBus().getMetadata(plugDescripton.getProxyServiceURL());
		} catch (Exception e) {
			if (log.isDebugEnabled()) {
				log.debug("Error getting MetaData from bus for iPlug: " + plugDescripton.getPlugId() + 
					". Does the method exist? (" + e.getLocalizedMessage() + ")");
			}
            log.error("Error on getProvider.", e);
		}
				
		if (metadata != null) {
			plugOperator = (IPlugOperator) metadata.getMetadata(AbstractIPlugOperatorInjector.IPLUG_OPERATOR);
		
			// if more data could be found then get the missing long name
			if (plugOperator != null) {
				List<Partner> allPartner = plugOperator.getPartners();
				for (Partner partner: allPartner) {
					// keep the mapping to the partner!
					allIPlugProvider.put(partner.getShortName(), partner.getProviders());
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
				List<Provider> provList = new ArrayList<>();
				provList.add(newProvider);
				allIPlugProvider.put("unknown", provList);
			}
		}
		
		return allIPlugProvider;
	}
	
	/**
	 * Prepare a detailed message with the provider that couldn't be found.
	 * @param plugDesc
	 * @param missingProvider
	 * @return
	 */
	private String writeErrorMessage(PlugDescription plugDesc, Map<String, List> missingProvider ) {
		String errorMessage = "";
		
		if (!missingProvider.isEmpty()) {
			String newLine = NEW_LINE_PLAIN;
			
			errorMessage += "iPlug-Name: " + plugDesc.getDataSourceName() + newLine;
			errorMessage += "Ansprechpartner: " + plugDesc.getPersonSureName() + " " + plugDesc.getPersonName() + newLine;
			errorMessage += "Email: " + plugDesc.getPersonMail() + newLine;
			errorMessage += "iPlug-ID: " + plugDesc.getPlugId() + newLine;
			errorMessage += "Fehlende Anbieter: ";
			for (Map.Entry<String, List> entry : missingProvider.entrySet()) {
				//String partner = (String)entry.getKey();
				List<Provider> providers = (List)entry.getValue();
		        for (Provider provider : providers) {
		        	//errorMessage += provider.getShortName() + "(" + partner + ")" + ", ";
		        	errorMessage += provider.getShortName() + ", ";
		        }
			}
			// remove trailing ', '
			errorMessage = errorMessage.substring(0, errorMessage.length()-2);
			errorMessage += newLine + newLine;
		}
		
		return errorMessage;
	}
	
	/**
	 * Add all missing provider to the local database if possible.
	 * @param missingProvider
	 * @return
	 */
	private void addProviderToLocalDB(Map<String, List> missingProvider, Map<String, String> result) {
		for (Map.Entry<String, List> entry : missingProvider.entrySet()) {
			String partner = entry.getKey();
			List<Provider> providers = (List)entry.getValue();
			
	        for (Provider provider : providers) {
	        	// skip this one if it's already occurred
				if (result.containsKey(provider.getShortName())) {
					continue;
				}
	        	if (provider.getDisplayName() != null) {
	        		IngridProvider iP = new IngridProvider();
	        		iP.setIdent(provider.getShortName());
	        		iP.setName(provider.getDisplayName());
	        		iP.setSortkeyPartner((int)getIdForPartner(partner));
	        		//iP.setSortkey(sortkey);
	        		//iP.setSortkeyPartner();
	        		iP.setUrl("");
	        		//iP.setId(0L);
	
	        		UtilsDB.saveDBObject(iP);
	        		result.put(provider.getShortName(), provider.getDisplayName());
	        	} else {
	        		// add to a list of provider that didn't offer a long name
	        		result.put(provider.getShortName(), null);
	        	}
	        }
        }        
	}
	
	/**
	 * Output all provider that couldn't be added to the database.
	 * @param noInfoProvider
	 * @return
	 */
	private String writeUpdateSummary( Map<String,String> resultsProvider) {
		String addedProvider 	= "";
		String nullProvider  	= "";
		String newLine 			= NEW_LINE_PLAIN;
		String summary 			= "";
		
		// prepare and distinguish between added and not added provider to the DB
		for (Map.Entry<String,String> entry : resultsProvider.entrySet()) {
			String shortName = entry.getKey();
			String longName  = entry.getValue();
			
			if (longName == null) {
				nullProvider += shortName + ", ";
			} else {
				addedProvider += shortName + " => " + longName + newLine;
			}
		}
		
		if (!nullProvider.isEmpty() || !addedProvider.isEmpty()) {
			summary = newLine + "-----------------------" + newLine + newLine;
			summary += "Anbieter, die zur Datenbank hinzugef\u00FCgt werden konnten: " + newLine;
			summary += addedProvider;
			
			if (addedProvider.isEmpty()) {
				summary += "keine";
			}
		}
		
		if (!nullProvider.isEmpty()) {
			summary += newLine + newLine;
			summary += "Anbieter, die nicht zur Datenbank hinzugef\u00FCgt werden konnten: " + newLine;
			summary += nullProvider;
			
			summary += newLine + newLine;
			summary += "Grund sind fehlende oder leere MetaDaten!";
		}
		
		return summary;
	}

	/**
	 * Return the id of a partner from the database. This is needed to establish
	 * a connection between provider and partner table.
	 * @param partner
	 * @return
	 */
	private long getIdForPartner(String partner) {
        List<IngridPartner> partners = UtilsDB.getPartners();
        for (IngridPartner iP : partners) {
        	if (iP.getIdent().equals(partner)) {
        		return iP.getId();
        	}
        }
        return -1;
	}
}
