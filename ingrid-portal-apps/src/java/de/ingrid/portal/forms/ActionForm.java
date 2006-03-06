/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.forms;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import javax.portlet.PortletRequest;

import de.ingrid.portal.global.UtilsString;
import de.ingrid.portal.global.Utils;

/**
 * Super class of all Form Handlers. Defines framework for form validation and
 * error messages.
 * 
 * @author Martin Maidhof
 */
public abstract class ActionForm implements Serializable {

    protected static final String VALUE_SEPARATOR = "'";

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
     * Get Input Data of specific field. Returns "" if no input.
     */
    public String getInput(String field) {
        String inputVal = (String) input.get(field);
        if (inputVal == null) {
            return "";
        }
        String result = inputVal.substring(1, inputVal.length() - 1);
        return result;
    }

    /**
     * Get Input Data of specific field. Returns "" if no input.
     */
    public String getInputHTMLEscaped(String field) {
        String inputVal = getInput(field);
        return UtilsString.htmlescape(inputVal);
    }

    /**
     * Get Input of specific Field as String Array. Returns Array with "" if no input.
     * @param field
     * @return
     */
    public String[] getInputAsArray(String field) {
        String input = getInput(field);
        return input.split(VALUE_SEPARATOR + VALUE_SEPARATOR);
    }

    /**
     * Check whether the given field has input ! NOTICE: uses trim() to remove
     * leading and trailing white spaces from input and then checks whether
     * string has length > 0
     */
    public boolean hasInput(String field) {
        if (getInput(field).trim().length() > 0) {
            return true;
        }
        return false;
    }

    /**
     * Set Input Data for single value field
     */
    public void setInput(String field, String data) {
        if (data == null || data.length() == 0) {
            input.remove(field);
            return;
        }
        StringBuffer dataWithSep = new StringBuffer();
        dataWithSep.append(VALUE_SEPARATOR);
        dataWithSep.append(data);
        dataWithSep.append(VALUE_SEPARATOR);
        input.put(field, dataWithSep.toString());
    }

    /**
     * Set Input Data for multiple value field. NOTICE: String Array is
     * converted to one String with the encapsulated Separator String
     * surrounding each value !
     */
    public void setInput(String field, String[] dataArray) {
        if (dataArray == null) {
            input.remove(field);
            return;
        }

        StringBuffer dataWithSep = new StringBuffer();
        if (dataArray.length > 0) {
            for (int i = 0; i < dataArray.length; i++) {
                dataWithSep.append(VALUE_SEPARATOR);
                dataWithSep.append(dataArray[i]);
                dataWithSep.append(VALUE_SEPARATOR);
            }
        }
        input.put(field, dataWithSep.toString());
    }

    public void clearInput() {
        input.clear();
    }

    // ==============
    // ERROR HANDLING
    // ==============

    /**
     * Are there errors encapsulated (on validation)
     * 
     * @return true if there are errors
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    /**
     * Is there an input error in the specified field ?
     */
    public boolean hasErrorInField(String field) {
        return errors.containsKey(field);
    }

    /**
     * Return all encapsulated error messages. Remove empty error messages !
     * NOTICE: empty error messages ("") can be added for a field, so the field
     * label is highlighted ! These messages aren't displayed (no separate line) !
     */
    public Collection getAllErrors() {
        if (!errors.containsValue("") && !errors.containsValue(null)) {
            return errors.values();
        }

        // remove "empty errors" (as often executed as elements in the list to
        // guarantee removal of all instances !)
        ArrayList myErrors = new ArrayList(errors.values());
        for (int i = 0; i < myErrors.size(); i++) {
            myErrors.remove("");
            myErrors.remove(null);
        }

        return myErrors;
    }

    /**
     * Set an error message for a field. NOTICE: A Field can only have ONE Error
     * Message ! You can set an empty message for a field, so the field label
     * will be highlighted ! This empty message won't be displayed as error.
     */
    public void setError(String field, String msg) {
        errors.put(field, msg);
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
     * Check if the given value was entered in given field. Includes multiple
     * input "fields" (e.g. in SelectBox Group, Listbox with multiple selection
     * ...)
     */
    public boolean isCurrentInput(String fieldName, String value) {
        String currValue = (String) input.get(fieldName);
        if (currValue == null) {
            return false;
        }

        StringBuffer dataWithSep = new StringBuffer();
        dataWithSep.append(VALUE_SEPARATOR);
        dataWithSep.append(value);
        dataWithSep.append(VALUE_SEPARATOR);

        if (currValue.indexOf(dataWithSep.toString()) == -1) {
            return false;
        }

        return true;
    }

    // =======================
    // MISC
    // =======================

    /**
     * Clear everything in this form, so there's no input and no errors.
     */
    public void clear() {
        clearInput();
        clearErrors();
    }
    
    /**
     * Get the current input of the form encoded as URL parameters (WITHOUT leading "?" !)
     * @return
     */
    public String toURLParams() {
        StringBuffer urlParams = new StringBuffer();

        Iterator iterator = input.keySet().iterator();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            String[] values = getInputAsArray(key);
            String urlParam = Utils.toURLParam(values, key);
            if (urlParams.length() > 0 && urlParam.length() > 0) {
                urlParams.append("&");
            }
            urlParams.append(urlParam);
        }
        return urlParams.toString();
    }

}
