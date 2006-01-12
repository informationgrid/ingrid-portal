/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.forms;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import javax.portlet.PortletRequest;

/**
 * Super class of all Form Handlers. Defines framework for form validation and
 * error messages.
 * 
 * @author Martin Maidhof
 */
public abstract class ActionForm implements Serializable {

    /** HTML: break (<br>) */
    public static final String HTML_LINE_FEED = "<br />";

    /** HTML: checkbox, radio button pre selection string ("checked") */
    public static final String HTML_CHECKED = "checked=\"checked\"";

    /** HTML: listbox, pre selection string ("selected") */
    public static final String HTML_SELECTED = "selected=\"selected\"";

    /**
     * encapsulates form input data. NOTICE: HashMap is NOT SYNCHRONIZED
     * shouldn't be a problem, there aren't multiple threads accessing this one
     * (one form per user !)
     */
    protected HashMap input = new HashMap();

    /**
     * encapsulates form input validation error messages. We use LinkedHashMap
     * to keep insertion order ! NOTICE: LinkedHashMap is NOT SYNCHRONIZED
     * shouldn't be a problem, there aren't multiple threads accessing this one
     * (one form per user !)
     */
    protected LinkedHashMap errors = new LinkedHashMap();

    /**
     * initialize form with default values
     */
    public abstract void init();

    /**
     * populate with parameters from request
     * 
     * @param request
     */
    public abstract void populate(PortletRequest request);

    /**
     * validate form input and update encapsulated validation error messages.
     * 
     * @return boolean false if form validation caused errors
     */
    public abstract boolean validate();

    // ==============
    // INPUT HANDLING
    // ==============
    /**
     * Get Input Data of specific field.
     * 
     * @param key
     * @return input data or "" if no input
     */
    public String getInput(String key) {
        Object inputVal = input.get(key.trim());
        return (inputVal == null) ? "" : inputVal.toString();
    }

    /**
     * Set Input Data for specific field.
     * 
     * @param key
     * @param data
     */
    public void setInput(String key, String data) {
        input.put(key, data);
    }

    public void clearInput() {
        input.clear();
    }

    // ==============
    // ERROR HANDLING
    // ==============

    /**
     * Get validation error message for a specific key (e.g. fieldname)
     * 
     * @param key
     * @return error message or "" if no error occured
     */
    public String getError(String key) {
        Object errorMsg = errors.get(key.trim());
        return (errorMsg == null) ? "" : errorMsg.toString();
    }

    /**
     * Get all validation error messages in separate lines
     * 
     * @return "" if no error occured on validation, else all error messages
     */
    public String getErrors() {
        String allErrors = "";

        // Iterate over the keys in the map
        Iterator it = errors.values().iterator();
        while (it.hasNext()) {
            allErrors = allErrors + it.next() + HTML_LINE_FEED;
        }

        return allErrors;
    }

    /**
     * Set an error message
     * 
     * @param key
     *            The key for the message (e.g. the according name of the field
     *            which caused the error !)
     * @param msg
     *            The error message
     */
    public void setError(String key, String msg) {
        errors.put(key, msg);
    }

    /**
     * Clear all Error Messages
     */
    public void clearErrors() {
        errors.clear();
    }

    // =======================
    // CHECK SELECTION HELPERS
    // =======================

    /**
     * Check whether a specific value in a multiple input "field" is selected
     * (e.g. in SelectBox Group, Listbox with multiple selection ...)
     * 
     * @param fieldName
     *            name of parameter (is key to our hash map)
     * @param checkValue
     *            selection value to check
     * @param returnValue
     *            the value to return if selection is true. Pass here our static
     *            consts (CHECKED for checkbox, SELECTED for listbox)
     * @return if selection is true the passed returnValue otherwise empty
     *         String
     */
    public String isValueSelected(String fieldName, String checkValue, String returnValue) {
        String currValue = getInput(fieldName);
        if (currValue == null || currValue.indexOf(checkValue) == -1) {
            return "";
        }

        return returnValue;
    }

    /**
     * Check whether a specific value in a multiple input "field" is selected
     * (e.g. in SelectBox Group, Listbox with multiple selection ...) including
     * check for a default value IF NOTHING IS SELECTED IN THE "FIELD" (only
     * then defaultValue is checked !).
     * 
     * @param fieldName
     *            name of parameter (is key to our hash map)
     * @param checkValue
     *            selection value to check
     * @param defaultValue
     *            the default selection to compare with if no current selection
     *            in "field"
     * @param returnValue
     *            the value to return if selection is true (that means if the
     *            checkValue is currently selected in the "field" or if nothing
     *            is selected in the "field" and the checkValue is the
     *            defaultSelection). Pass here our static consts (CHECKED for
     *            checkbox, SELECTED for listbox)
     * @return if selection is true the passed returnValue otherwise empty
     *         String
     */
    public String isValueSelected(String fieldName, String checkValue, String defaultValue, String returnValue) {
        String currValue = getInput(fieldName);
        if (currValue == null || currValue.length() == 0) {
            // Nothing selected, check for default value
            if (defaultValue.indexOf(checkValue) != -1) {
                return returnValue;
            }
        } else if (currValue.indexOf(checkValue) != -1) {
            // something selected and is our check value
            return returnValue;
        }

        return "";
    }

    /**
     * Check whether the given "field" has input.
     * 
     * @param fieldName
     *            name of parameter (field name)
     * @return String true if field has input (has value or something is
     *         selected)
     */
    public boolean hasInput(String fieldName) {
        String currValue = getInput(fieldName);
        if (currValue == null || currValue.length() == 0) {
            return false;
        }

        return true;
    }

}
