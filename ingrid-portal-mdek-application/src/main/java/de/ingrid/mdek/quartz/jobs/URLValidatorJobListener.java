/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2019 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.mdek.quartz.jobs;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

import de.ingrid.mdek.MdekKeys;
import de.ingrid.mdek.MdekUtils;
import de.ingrid.mdek.caller.IMdekCallerCatalog;
import de.ingrid.mdek.quartz.jobs.util.URLObjectReference;
import de.ingrid.mdek.util.MdekCatalogUtils;
import de.ingrid.utils.IngridDocument;


public class URLValidatorJobListener implements JobListener {

	private static final Logger log = Logger.getLogger(URLValidatorJobListener.class);	

	private final String listenerName;
	private final String plugId;
	// We need to store the userUuid since the method 'jobWasExecuted' is invoked by quartz in another thread.
	// Therefore we can't get the username via MdekSecurityUtils.getCurrentUserUuid().
	private final String userUuid;
	private final IMdekCallerCatalog mdekCallerCatalog;

	public URLValidatorJobListener(String listenerName, IMdekCallerCatalog mdekCallerCatalog, String userUuid, String plugId) {
		this.listenerName = listenerName;
		this.mdekCallerCatalog = mdekCallerCatalog;
		this.userUuid = userUuid;
		this.plugId = plugId;
	}

	public String getName() {
		return listenerName;
	}


	public void jobWasExecuted(JobExecutionContext jobExecutionContext,
			JobExecutionException jobExecutionException) {
	    Map<String, List<URLObjectReference>> results = (Map<String, List<URLObjectReference>>) jobExecutionContext.getResult();
		List<URLObjectReference> urlObjectReferences = results.get(MdekKeys.URL_RESULT);
		List<URLObjectReference> capabilitiesReferences = results.get(MdekKeys.CAP_RESULT);

		if (urlObjectReferences != null) {
		    int from = 0;
		    int step = 500;
		    int to = from + step;
		    boolean moreData = true;
		    while (moreData) {
		        int fromUrl = from > urlObjectReferences.size() ? urlObjectReferences.size() : from; 
		        int toUrl = to > urlObjectReferences.size() ? urlObjectReferences.size() : to; 
		        List<URLObjectReference> subListUrl = urlObjectReferences.subList( fromUrl, toUrl );
		        
		        fromUrl = from > capabilitiesReferences.size() ? capabilitiesReferences.size() : from; 
		        toUrl = to > capabilitiesReferences.size() ? capabilitiesReferences.size() : to; 
		        List<URLObjectReference> subListCap = capabilitiesReferences.subList( fromUrl, toUrl );
    			
		        from += step;
		        to += step;
		        
		        log.debug("sending URL Job result to the backend.");
    			IngridDocument jobInfo = new IngridDocument();
    			jobInfo.put(MdekKeys.URL_RESULT, MdekCatalogUtils.convertFromUrlJobResult(subListUrl));
    			jobInfo.put(MdekKeys.CAP_RESULT, MdekCatalogUtils.convertFromUrlJobResult(subListCap));
    			jobInfo.put(MdekKeys.JOBINFO_START_TIME, MdekUtils.dateToTimestamp(jobExecutionContext.getFireTime()));
    			jobInfo.putBoolean( MdekKeys.JOBINFO_IS_UPDATE, true );
    			// if there's no more data then we can add the finish flag
    			if (from > urlObjectReferences.size() && from > capabilitiesReferences.size()) {
    			    jobInfo.putBoolean( MdekKeys.JOBINFO_IS_FINISHED, true );
    			    moreData = false;
    			} else {
    			    jobInfo.putBoolean( MdekKeys.JOBINFO_IS_FINISHED, false );
    			}
    			
    			try {
    	            mdekCallerCatalog.setURLInfo(plugId, jobInfo, userUuid);
    	            if (log.isDebugEnabled()) {
    	                log.debug("URL Validator Job result has been stored in the DB. Results " + (from-step) + " to " + (to-step));
    	            }
    	        } catch (Exception e) {
    	            log.error( "Error during writing new URL analysis", e );
    	        }
		    }

		} else {
			log.debug("URL Validator Job result was null. Job has probably been canceled. Result will not be stored in the DB!");
		}
	}

	// Do nothing for the following two methods
	public void jobExecutionVetoed(JobExecutionContext arg0) {}
	public void jobToBeExecuted(JobExecutionContext arg0) {}
}
