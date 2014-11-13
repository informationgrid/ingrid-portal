/*
 * **************************************************-
 * Ingrid Portal Mdek
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
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
package de.ingrid.portal.portlets.mdek.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.portlet.PortletException;

import org.apache.log4j.Logger;

import de.ingrid.mdek.MdekKeys;
import de.ingrid.mdek.caller.IMdekCallerCatalog;
import de.ingrid.mdek.util.MdekUtils;
import de.ingrid.utils.IngridDocument;

public class MdekPortletUtils {

	private final static Logger log = Logger.getLogger(MdekPortletUtils.class);

    /** Throws Exception if frontend IGC Version does NOT fit to backend IGC Version ! */
    public static void checkIGCCompatibility(String plugId,
    		IMdekCallerCatalog mdekCallerCatalog) throws Throwable {
    	// get Frontend Version (from mdek-api jar) ! we do it dynamically to be compatible with older versions !
    	String frontendVersion = null;
    	try {
//        	frontendVersion = de.ingrid.mdek.Versioning.NEEDED_IGC_VERSION;

    		ClassLoader classLoader = MdekKeys.class.getClassLoader();
    		Class versioningClass = classLoader.loadClass("de.ingrid.mdek.Versioning");
    		Field versioningField = versioningClass.getField("NEEDED_IGC_VERSION");
    		// static field, so we pass null !
    		frontendVersion = (String) versioningField.get(null);

    	} catch (Exception ex) {
    	}

    	// if frontend version not present (old api jar)
    	if (frontendVersion == null) {
    		log.warn("Frontend IGC Version could NOT be extracted from mdek-api (\"old\" jar) !");
    	} else {
    		if (log.isInfoEnabled()) {
        		log.info("Extracted Frontend IGC Version from mdek-api: " + frontendVersion);
    		}
    	}
    	
    	// fetch version of backend ! we do it dynamically to be compatible with older versions !
    	String backendVersion = null;
    	try {
        	IngridDocument response = mdekCallerCatalog.getVersion(plugId);

//        	backendVersion = MdekUtils.extractIGCVersionFromResponse(response);

        	// NOTICE: invoking this method may deliver a runtime exception if backend throws exception !
            Method meth = MdekUtils.class.getMethod("extractIGCVersionFromResponse", new Class[]{IngridDocument.class});
            backendVersion = (String) meth.invoke(new MdekUtils(), new Object[]{response});

    	} catch (InvocationTargetException e) {
    		// encapsulates exception thrown by extract method (e.g. when response contains error) !
    		// We pass on so will be displayed in portlet !
    		throw e.getCause();
    	} catch (NoSuchMethodException e) {
    		log.warn("Method for extracting backend version not found (MdekUtils.extractIGCVersionFromResponse in portal-mdek-application.jar) !");
    	} catch (Exception e) {
    	}

    	// if backend version not present (old backend !?)
    	if (backendVersion == null) {
    		log.warn("Backend IGC Version could not be determined (\"old\" backend !?) !");
    	} else {
    		if (log.isInfoEnabled()) {
        		log.info("Determined Backend IGC Version: " + backendVersion);
    		}
    	}
    	
    	boolean conflicting = false;
    	if (frontendVersion == null) {
    		if (backendVersion != null) {
    			conflicting = true;
        		log.error("Frontend version could NOT be extracted from mdek-api (\"old\" jar) BUT backend version was delivered ! -> does not match !");
    		}    		
    	} else {
    		// frontendVersion != null
    		
    		if (backendVersion == null) {
    			conflicting = true;
        		log.error("Frontend version could be extracted from mdek-api (jar) BUT backend version was NOT delivered (\"old\" backend)! -> does not match !");
    		} else {
    			if (!frontendVersion.equals(backendVersion)) {
        			conflicting = true;
    			}
    		}
    	}
    	
    	if (conflicting) {
    		String errMsg = "Conflicts between IGE frontend (IGC version: " + frontendVersion +
    			") and backend (IGC version: " + backendVersion +
    			") ! Please install matching front- and backend components !";
    		PortletException exc = new PortletException (errMsg);
    		log.error("Conflicting versions of IGE front- and backend !", exc);
			throw exc;
    	}
    }
}
