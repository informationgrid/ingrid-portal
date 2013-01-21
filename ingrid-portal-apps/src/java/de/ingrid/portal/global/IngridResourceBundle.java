/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.global;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class IngridResourceBundle {

    private static final Log log = LogFactory.getLog(IngridResourceBundle.class);

    ResourceBundle r = null;

    /**
     * encapsulates parameters which will be formatted into message !
     */
    ArrayList parameters = null;

    /**
     * @param r
     */
    public IngridResourceBundle(ResourceBundle r) {
        super();
        this.r = r;
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
            parameters = new ArrayList();
        }
        parameters.add(param);
    }

    /**
     * Try to get a message first from a special profile resource bundle and if
     * it couldn't be found then try the encapsulated resource. Also takes
     * CommonResources into account.
     * 
     * @param key
     * @return
     */
    public String getString(String key) {
        try {
            if (log.isDebugEnabled()) {
                log.debug("Get localization for key '" + key + "' and locale '" + r.getLocale().toString() + "'.");
            }
            return getProfileString(key);
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("Problem accessing the profile resource! Fall back to portlet specific resource file.", e);
            }
            try {
                return r.getString(key);
            } catch (Exception ex) {
                if (log.isDebugEnabled()) {
                    log.debug("Problem accessing the portlet specific resource! Fall back to common resource file.", e);
                }
                return getCommonString(key);
            }
        }
    }

    public String getProfileString(String key) throws Exception {
        ResourceBundle profileRes = ResourceBundle.getBundle("de.ingrid.portal.resources.ProfileResources", r
                .getLocale());
        if (!profileRes.getLocale().getLanguage().equals(r.getLocale().getLanguage())) {
            throw new Exception("Profile resource language '" + profileRes.getLocale()
                    + "' does not match portlet resource '" + r.getLocale().getLanguage()
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
            ResourceBundle commonRes = ResourceBundle.getBundle("de.ingrid.portal.resources.CommonResources", r
                    .getLocale());
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
