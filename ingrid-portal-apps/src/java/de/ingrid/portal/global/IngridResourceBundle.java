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

    public String getString(String key) {
        try {
            return r.getString(key);
        } catch (Exception e) {
            return getCommonString(key);
        }
    }

    public String getCommonString(String key) {
        try {
            ResourceBundle commonRes = ResourceBundle.getBundle("de.ingrid.portal.resources.CommonResources", r.getLocale());
            return commonRes.getString(key);
        } catch (Exception e) {
            return key;
        }
    }

}
