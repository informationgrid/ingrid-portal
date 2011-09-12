/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.global;

import java.util.ResourceBundle;

/**
 * TODO Describe your created type (class, etc.) here.
 *
 * @author joachim@wemove.com
 */
public class IngridResourceBundle {

    ResourceBundle r = null;

    /**
     * @param r
     */
    public IngridResourceBundle(ResourceBundle r) {
        super();
        this.r = r;
    }


    /**
     * Try to get a message first from a special profile resource bundle and if it 
     * couldn't be found then try the encapsulated resource. Also takes CommonResources into account.
     * @param key
     * @return
     */
    public String getString(String key) {
        try {
            return r.getString(key);
        } catch (Exception ex) {
            return key;
        }
    }
    
    /**
     * Get the localized string of a key. If it isn't available then return
     * the defined defaultString instead.
     * 
     * @param key is the key to look for in resource bundle
     * @param defaultString is the string to use if key wasn't found
     * @return the localized string or defined one if it wasn't defined in resource bundle 
     */
    public String getStringOrDefault(String key, String defaultString) {
        String localizedString = getString(key);
        if (key.equals(localizedString))
            return defaultString;
        else
            return localizedString;
    }
}
