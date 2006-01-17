/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.forms;

import javax.portlet.PortletRequest;

/**
 * Form Handler for Environment Chronicles page. Stores and validates form
 * input.
 * 
 * @author Martin Maidhof
 */
public class ChronicleForm extends ActionForm {

    /** attribute name of action form in session */
    public static final String SESSION_KEY = "chronicles_form";

    /** field name of "event" checkbox group in form */
    public static final String FIELD_EVENT = "event";

    /** field name of "time" radio button group in form */
    public static final String FIELD_TIME_SELECT = "time_select";

    /** field name of "time from" text field in form */
    public static final String FIELD_TIME_FROM = "time_from";

    /** field name of "time to" text field in form */
    public static final String FIELD_TIME_TO = "time_to";

    /** field name of "time at" text field in form */
    public static final String FIELD_TIME_AT = "time_at";

    /** field name of "search term" text field in form */
    public static final String FIELD_SEARCH = "search";

    /** WHEN MULTIPLE VALUES USE "''" TO SEPARATE VALUES !!!!!!!!! */
    public static final String INITIAL_EVENT = "all''act''his''law''fou''cat''con''lit''nat''dir''shi''acc";

    public static final String INITIAL_TIME_SELECT = "period";

    /**
     * @see de.ingrid.portal.forms.ActionForm#init()
     */
    public void init() {
        clearInput();
        setInput(FIELD_EVENT, INITIAL_EVENT);
        setInput(FIELD_TIME_SELECT, INITIAL_TIME_SELECT);
    }

    /**
     * @see de.ingrid.portal.forms.ActionForm#populate(javax.portlet.PortletRequest)
     */
    public void populate(PortletRequest request) {
        clearInput();
        setInput(FIELD_EVENT, request.getParameterValues(FIELD_EVENT));
        setInput(FIELD_TIME_SELECT, request.getParameterValues(FIELD_TIME_SELECT));
        setInput(FIELD_TIME_FROM, request.getParameter(FIELD_TIME_FROM));
        setInput(FIELD_TIME_TO, request.getParameter(FIELD_TIME_TO));
        setInput(FIELD_TIME_AT, request.getParameter(FIELD_TIME_AT));
        setInput(FIELD_SEARCH, request.getParameter(FIELD_SEARCH));
    }

    /**
     * @see de.ingrid.portal.forms.ActionForm#validate()
     */
    public boolean validate() {
        boolean allOk = true;
        clearErrors();

        // check rubric
        if (!hasInput(FIELD_EVENT)) {
            setError(FIELD_EVENT, "chronicle.error.noEvent");
            allOk = false;
        }

        return allOk;
    }
}
