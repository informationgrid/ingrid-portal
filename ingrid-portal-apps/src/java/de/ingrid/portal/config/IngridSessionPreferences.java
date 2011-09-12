/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.config;

import java.util.HashMap;

import javax.portlet.ActionRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.search.SearchState;

/**
 * encapsulates user session preferences.
 * 
 * @author joachim@wemove.com
 */
public class IngridSessionPreferences extends HashMap {

    private static final long serialVersionUID = -9079742501129971482L;

    private final static Logger log = LoggerFactory.getLogger(Utils.class);

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
            log.error("Unable to create new instance of class '" + cl.getName() + "'", e);
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
