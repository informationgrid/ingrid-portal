/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2022 wemove digital solutions GmbH
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
package de.ingrid.portal.config;

import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.search.SearchState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.portlet.ActionRequest;
import java.util.HashMap;

/**
 * encapsulates user session preferences.
 * 
 * @author joachim@wemove.com
 */
public class IngridSessionPreferences extends HashMap {

    private static final long serialVersionUID = -9079742501129971482L;

    private static final Logger log = LoggerFactory.getLogger(Utils.class);

    /** attribute name of action form in session */
    public static final String SESSION_KEY = IngridSessionPreferences.class.getName();

    public static final String SEARCH_SETTING_RANKING = "ranking";

    public static final String SEARCH_SETTING_GROUPING = "grouping";

    public static final String SEARCH_SETTING_INCL_META = "incl_meta";

    public static final String QUERY_HISTORY = "query_history";

    public static final String RESTRICTING_PROVIDER = "restricting_provider";
    
    public IngridSessionPreferences() {
    	// initial values
    	if (PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_ENABLE_DEFAULT_GROUPING_DOMAIN, false)) {
	    	// default grouping
	    	this.put(IngridSessionPreferences.SEARCH_SETTING_GROUPING, Settings.PARAMV_GROUPING_DOMAIN);
    	}
    }

    /**
     * Get object from preference map. If it does not exists, create a new
     * instance on the class.
     * 
     * @param key
     *            The key of the preference.
     * @param cl
     *            The class of the returned type.
     * @return The (initialized) object.
     */
    public Object getInitializedObject(String key, Class cl) {

        Object obj = null;
        try {
            obj = get(key);
            if (obj == null) {
                obj = cl.newInstance();
                put(key, obj);
            }
        } catch (Exception e) {
            if(log.isErrorEnabled()) {
                log.error("Unable to create new instance of class '" + cl.getName() + "'", e);
            }
        }
        return obj;
    }

    /**
     * Adapts the current SearchState to the User Settings here !
     * So temporary values from bookmarking are replaced with UI settings (UserPreferences ...).
     * Next time when generating URL params from SearchState these ones will be used !   
     */
    public void adaptSearchState(ActionRequest request) {
        SearchState.adaptSearchState(request, Settings.PARAM_RANKING,
        	this.get(SEARCH_SETTING_RANKING));
        SearchState.adaptSearchState(request, Settings.PARAM_GROUPING,
        	this.get(SEARCH_SETTING_GROUPING));
    }
}
