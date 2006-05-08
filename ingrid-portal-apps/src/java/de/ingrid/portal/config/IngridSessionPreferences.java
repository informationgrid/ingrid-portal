/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.config;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.portal.global.Utils;

/**
 * TODO Describe your created type (class, etc.) here.
 *
 * @author joachim@wemove.com
 */
public class IngridSessionPreferences extends HashMap {

    private static final long serialVersionUID = -9079742501129971482L;
    
    private final static Log log = LogFactory.getLog(Utils.class);

    /** attribute name of action form in session */
    public static final String SESSION_KEY = IngridSessionPreferences.class.getName();

    
    public static final String SEARCH_SETTING_RANKING = "ranking";
    public static final String SEARCH_SETTING_GROUPING = "grouping";
    public static final String SEARCH_SETTING_INCL_META = "incl_meta";
    public static final String QUERY_HISTORY = "query_history";
    
    /**
     * Get object from preference map. If it does not exists, create a new instance 
     * on the class.
     * 
     * @param key The key of the preference.
     * @param cl The class of the returned type.
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
            log.error("Unable to create new instance of class '" + cl.getName() +"'", e);
        }
        return obj;
    }
    
    
    
}
