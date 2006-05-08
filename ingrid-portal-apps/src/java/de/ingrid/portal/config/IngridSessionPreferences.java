/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.config;

import java.util.HashMap;

/**
 * TODO Describe your created type (class, etc.) here.
 *
 * @author joachim@wemove.com
 */
public class IngridSessionPreferences extends HashMap {

    private static final long serialVersionUID = -9079742501129971482L;

    /** attribute name of action form in session */
    public static final String SESSION_KEY = IngridSessionPreferences.class.getName();

    
    public static final String SEARCH_SETTING_RANKING = "ranking";
    public static final String SEARCH_SETTING_GROUPING = "grouping";
    public static final String SEARCH_SETTING_INCL_META = "incl_meta";
    
    
}
