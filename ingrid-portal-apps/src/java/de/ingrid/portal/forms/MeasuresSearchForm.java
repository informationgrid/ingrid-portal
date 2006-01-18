/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.forms;

import javax.portlet.PortletRequest;

/**
 * Form Handler for Measures Search portlet. Stores and validates form input.
 * 
 * @author Martin Maidhof
 */
public class MeasuresSearchForm extends ActionForm {

    /** attribute name of action form in session */
    public static final String SESSION_KEY = "messearch_form";

    /** field names (name of request parameter) */
    public static final String FIELD_RUBRIC = "rubric";

    public static final String FIELD_PARTNER = "partner";

    public static final String FIELD_GROUPING = "grouping";

    /**
     * initial values. WHEN MULTIPLE VALUES USE "''" TO SEPARATE VALUES
     * !!!!!!!!!
     */
    public static final String INITIAL_RUBRIC = "all''ground''air''water''other";

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
            setError(FIELD_RUBRIC, "measuresSearch.error.noRubric");
            allOk = false;
        }
        if (!hasInput(FIELD_PARTNER)) {
            setError(FIELD_PARTNER, "measuresSearch.error.noPartner");
            allOk = false;
        }

        return allOk;
    }
}
