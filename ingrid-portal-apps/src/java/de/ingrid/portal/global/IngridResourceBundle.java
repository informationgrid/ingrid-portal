/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.global;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * TODO Describe your created type (class, etc.) here.
 *
 * @author joachim@wemove.com
 */
public class IngridResourceBundle {

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
     * @param param
     */
    public void setMsgParam(String param) {
        if (this.parameters == null) {
            parameters = new ArrayList();
        }
        parameters.add(param);
    }

    /**
     * Get a message from encapsulated resource, also takes CommonResources into account
     * @param key
     * @return
     */
    public String getString(String key) {
        try {
            return r.getString(key);
        } catch (Exception e) {
            return getCommonString(key);
        }
    }

    /**
     * Get a message only from CommonResources
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
     * Get a message from encapsulated resource with encapsulated parameters formatted into message !
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
     * Get a message from encapsulated resource, also takes CommonResources into account.
     * Message key is "generalKey" + "keyPostfix"
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
