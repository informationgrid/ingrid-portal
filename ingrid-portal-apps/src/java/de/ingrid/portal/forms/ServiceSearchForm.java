/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.forms;

import javax.portlet.PortletRequest;

/**
 * Form Handler for Service page. Stores and validates form input.
 * 
 * @author Martin Maidhof
 */
public class ServiceSearchForm extends ActionForm {

    /** attribute name of action form in session */
    public static final String SESSION_KEY = "service_form";

    /** field name of "rubric" checkbox group in form */
    public static final String FIELD_RUBRIC = "rubric";

    /** field name of "partner" selection list in form */
    public static final String FIELD_PARTNER = "partner";

    /** field name of "grouping" radio group in form */
    public static final String FIELD_GROUPING = "grouping";

    /** WHEN MULTIPLE VALUES USE "''" TO SEPARATE VALUES !!!!!!!!! */
    public static final String INITIAL_RUBRIC = "all''press''public''events";

    public static final String INITIAL_PARTNER = "all";

    public static final String INITIAL_GROUPING = "none";

    /**
     * @see de.ingrid.portal.forms.ActionForm#init()
     */
    public void init() {
        clearInput();
        setInput(FIELD_RUBRIC, INITIAL_RUBRIC);
        setInput(FIELD_PARTNER, INITIAL_PARTNER);
        setInput(FIELD_GROUPING, INITIAL_GROUPING);
    }

    /**
     * @see de.ingrid.portal.forms.ActionForm#populate(javax.portlet.PortletRequest)
     */
    public void populate(PortletRequest request) {
        clearInput();
        setInput(FIELD_RUBRIC, request.getParameterValues(FIELD_RUBRIC));
        setInput(FIELD_PARTNER, request.getParameterValues(FIELD_PARTNER));
        setInput(FIELD_GROUPING, request.getParameter(FIELD_GROUPING));
    }

    /**
     * @see de.ingrid.portal.forms.ActionForm#validate()
     */
    public boolean validate() {
        boolean allOk = true;
        clearErrors();

        // check rubric
        if (!hasInput(FIELD_RUBRIC)) {
            setError(FIELD_RUBRIC, "serviceSearch.error.noRubric");
            allOk = false;
        }
        if (!hasInput(FIELD_PARTNER)) {
            setError(FIELD_PARTNER, "serviceSearch.error.noPartner");
            allOk = false;
        }

        return allOk;
    }
}
