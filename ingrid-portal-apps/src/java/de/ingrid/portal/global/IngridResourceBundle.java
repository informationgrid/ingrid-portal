/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.global;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Resource bundle extending default functionality, e.g. also
 * taking other resources into account when requesting localized resource.
 * Encapsulates bundle which may be passed from Jetspeed (InlinePortletResourceBundle).
 * If so, we can't access locale from encapsulated bundle, so we also have to pass Locale
 * in constructor !
 */
public class IngridResourceBundle {

    /** ResourceBundle may be from Jetspeed: e.g. InlinePortletResourceBundle.
     * Then getLocale returns null :(  */
    ResourceBundle r = null;

    /** encapsulates parameters which will be formatted into message ! */
    ArrayList<String> parameters = null;
    
    /** Locale can't be fetched from encapsulated bundle if this is a jetspeed bundle.
     * So we store locale here. */
    Locale bundleLocale = null;

    /** If passed bundle is fetched from PortletConfig then this is a bundle from Jetspeed
     * (InlinePortletResourceBundle) which unfortunately returns null calling getLocale ! 
     * So we also pass locale, which we need for fetching other resources !
     * @param r the bundle to first fetch resources from
     * @param bundleLocale the locale of the bundle used for requesting other bundles.
     * Pass null if unknown, then locale is fetched from bundle (which may be null when
     * Jetspeed bundle is passed !).
     */
    public IngridResourceBundle(ResourceBundle r, Locale bundleLocale) {
        this.r = r;
        this.bundleLocale = bundleLocale;
        if (bundleLocale == null) {
        	this.bundleLocale = r.getLocale();
        }
    }

    /**
     * Reset the parameters which will be formatted into message
     */
    public void resetMsgParams() {
        parameters = null;
    }

    /**
     * Set a parameter which will be formatted into a message
     * 
     * @param param
     */
    public void setMsgParam(String param) {
        if (this.parameters == null) {
            parameters = new ArrayList<String>();
        }
        parameters.add(param);
    }

    /**
     * Try to get a message first from a special profile resource bundle and if
     * it couldn't be found then try the encapsulated resource. Also takes
     * CommonResources, ProfileResources into account.
     * 
     * @param key
     * @return
     */
    public String getString(String key) {
        try {
            return getProfileString(key);
        } catch (Exception e) {
            try {
                return r.getString(key);
            } catch (Exception ex) {
                return getCommonString(key);
            }
        }
    }

    public String getProfileString(String key) throws Exception {
        ResourceBundle profileRes = ResourceBundle.getBundle("de.ingrid.portal.resources.ProfileResources", this.bundleLocale);
        if (!profileRes.getLocale().getLanguage().equals(this.bundleLocale.getLanguage())) {
            throw new Exception("Profile resource language '" + profileRes.getLocale()
                    + "' does not match portlet resource '" + this.bundleLocale.getLanguage()
                    + "', fall back to portlet resource!");
        }
        return profileRes.getString(key);
    }

    /**
     * Get a message only from CommonResources
     * 
     * @param key
     * @return
     */
    public String getCommonString(String key) {
        try {
            ResourceBundle commonRes = ResourceBundle.getBundle("de.ingrid.portal.resources.CommonResources", this.bundleLocale);
            return commonRes.getString(key);
        } catch (Exception e) {
            return key;
        }
    }

    /**
     * Get a message from encapsulated resource with encapsulated parameters
     * formatted into message !
     * 
     * @param key
     * @return
     */
    public String getStringWithMsgParams(String key) {
        String message = this.getString(key);
        try {
            MessageFormat mf = new MessageFormat(message);
            return mf.format(this.parameters.toArray());
        } catch (Exception e) {
            return message;
        }
    }

    /**
     * Get a message from encapsulated resource, also takes CommonResources into
     * account. Message key is "generalKey" + "keyPostfix"
     * 
     * @param generalKey
     * @param keyPostfix
     * @return "" if message not found !
     */
    public String getString(String generalKey, String keyPostfix) {
        String retValue = "";
        try {
            String key = generalKey.concat(keyPostfix);
            retValue = getString(key);
            if (retValue.equals(key)) {
                retValue = "";
            }
        } catch (Exception e) {
            retValue = "";
        }
        return retValue;
    }

}
