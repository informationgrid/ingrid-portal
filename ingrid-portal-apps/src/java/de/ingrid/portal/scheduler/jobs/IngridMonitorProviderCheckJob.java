/*
 * Copyright (c) 2007 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.scheduler.jobs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
		ArrayList allProvider 		= new ArrayList();
		ArrayList missingProvider 	= new ArrayList();
		Session session 			= HibernateUtil.currentSession();
		boolean found				= false;
//        Transaction tx = null;
        
		try {
			startTimer();
			
			// get all iPlugs connected to the iBus
			PlugDescription[] hits = IBUSInterfaceImpl.getInstance().getAllIPlugs();
			
			// get from each iPlug the containing provider
			for (int i=0; i<hits.length; i++) {
				String[] provider = hits[i].getProviders();
				for (int j=0; j<provider.length; j++) {
					allProvider.add(provider[j]);
				}
			}
			
			List<String> exclude = Arrays.asList(dataMap.getString(PARAM_EXCLUDED_PROVIDER).split(","));
			
			// get the provider stored in the local database
			//tx = session.beginTransaction();
			IngridProvider providerInDB = null;
			List allProviderInDB = session.createCriteria(IngridProvider.class).list();
	        //tx.commit();

			// compare the provider with the ones in the database
			for (int i=0; i<allProvider.size(); i++) {
				for (int j=0; j<allProviderInDB.size(); j++) {
					String ident = ((IngridProvider)allProviderInDB.get(j)).getIdent();
					if (allProvider.get(i).equals(ident)) {
						found = true;
						break;
					}
				}
				if (!found && !exclude.contains(allProvider.get(i))) {
					missingProvider.add(allProvider.get(i));
					if (log.isDebugEnabled()) {
						log.debug("Provider (" + allProvider.get(i) + ") not found in local DB!");
					}
				}
				found = false;
			}
			
			/*
			// wrong way around ... must iterate through allProvider!
			for (int j=0; j<allProviderInDB.size(); j++) {
				String ident = ((IngridProvider)allProviderInDB.get(j)).getIdent();
				if (!allProvider.contains(ident)) {
					missingProvider.add(ident);
					if (log.isDebugEnabled()) {
						log.debug("Provider (" + ident + ") not found in local DB!");
					}
				}
			}*/
			
			computeTime(dataMap, stopTimer());
			
			if (hits.length == 0) {
				status = STATUS_ERROR;
				statusCode = STATUS_CODE_ERROR_NO_IPLUGS;
			} else if (!missingProvider.isEmpty()) {
				status = STATUS_ERROR;
				statusCode = missingProvider.toString();
			} else {
				status = STATUS_OK;
				statusCode = STATUS_CODE_NO_ERROR;
			}
		} catch (Throwable e) {
			status = STATUS_ERROR;
			statusCode = e.getClass().getName() + ":" + e.getMessage();
			e.printStackTrace();
		}
		
		updateJobData(context, status, statusCode);
		sendAlertMail(context);
		updateJob(context);

		if (log.isDebugEnabled()) {
			log.debug("Job (" + context.getJobDetail().getName() + ") finished in "
					+ (System.currentTimeMillis() - startTime) + " ms.");
		}
	}
}
