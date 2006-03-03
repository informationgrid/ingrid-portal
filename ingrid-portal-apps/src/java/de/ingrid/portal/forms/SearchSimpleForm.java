/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.forms;

import javax.portlet.PortletRequest;

import de.ingrid.portal.global.Settings;

/**
 * Form Handler for Simple Search form. Stores and validates form input.
 * 
 * @author Martin Maidhof
 */
public class SearchSimpleForm extends ActionForm {

    /** attribute name of action form in session */
    public static final String SESSION_KEY = "search_simple_form";

    /** field names (name of request parameter) */
    public static final String FIELD_QUERY = Settings.PARAM_QUERY_STRING;

    /**
     * initial values. initial Query not static ! can be set from outside and may differ because of Locale
     */
    protected String INITIAL_QUERY = "";

    /**
     * @see de.ingrid.portal.forms.ActionForm#init()
     */
    public void init() {
        clearInput();
        setInput(FIELD_QUERY, INITIAL_QUERY);
    }

    /**
     * @see de.ingrid.portal.forms.ActionForm#populate(javax.portlet.PortletRequest)
     */
    public void populate(PortletRequest request) {
        clearInput();
        setInput(FIELD_QUERY, request.getParameter(FIELD_QUERY));
    }

    /**
     * NOTICE: Return value indicates whether query should be performed or not !!!
     * If input is empty the initial default value is set. Returns true if a query
     * was entered. 
     * @see de.ingrid.portal.forms.ActionForm#validate()
     */
    public boolean validate() {
        boolean doQuery = true;
        clearErrors();

        // check query
        if (getInput(FIELD_QUERY).trim().length() == 0) {
            setInput(FIELD_QUERY, INITIAL_QUERY);
        }
        if (getInput(FIELD_QUERY).trim().equals(INITIAL_QUERY)) {
            doQuery = false;
        }

        return doQuery;
    }

    /**
     * Set initial query dependent from locale !
     * @param initial_query The iNITIAL_QUERY to set.
     */
    public void setINITIAL_QUERY(String initialQuery) {
        INITIAL_QUERY = initialQuery;
    }
    
    /**
     * Get initial query which was set from outside (dependent from locale)
     */
    public String getINITIAL_QUERY() {
        return INITIAL_QUERY;
    }
}
