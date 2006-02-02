/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.forms;

import javax.portlet.PortletRequest;

/**
 * Form Handler for Environment Search page. Stores and validates form input.
 * 
 * @author Martin Maidhof
 */
public class EnvironmentSearchForm extends ActionForm {

    /** attribute name of action form in session */
    public static final String SESSION_KEY = "envsearch_form";

    /** field names (name of request parameter) */
    public static final String FIELD_THEMES = "themes";

    public static final String FIELD_CATEGORY = "category";

    public static final String FIELD_PARTNER = "partner";

    public static final String FIELD_GROUPING = "grouping";

    /** initial values */
    public static final String INITIAL_THEMES = "all";

    public static final String INITIAL_CATEGORY = "all";

    public static final String INITIAL_PARTNER = "all";

    public static final String INITIAL_GROUPING = "none";

    /**
     * @see de.ingrid.portal.forms.ActionForm#init()
     */
    public void init() {
        clearErrors();
        clearInput();
        setInput(FIELD_THEMES, INITIAL_THEMES);
        setInput(FIELD_CATEGORY, INITIAL_CATEGORY);
        setInput(FIELD_PARTNER, INITIAL_PARTNER);
        setInput(FIELD_GROUPING, INITIAL_GROUPING);
    }

    /**
     * NOTICE: We DON'T CLEAR ANY FIELDS IN THE FORM, just take over the given
     * params into the according field (but that field then ONLY contains the
     * new values). In this way, we can initialize the form with default values
     * and JUST TAKE OVER THE NEW ONES !. Use clearInput() to clear the form
     * before populating !  
     * @see de.ingrid.portal.forms.ActionForm#populate(javax.portlet.PortletRequest)
     */
    public void populate(PortletRequest request) {
        if (request.getParameterValues(FIELD_THEMES) != null) {
            setInput(FIELD_THEMES, request.getParameterValues(FIELD_THEMES));
        }
        if (request.getParameterValues(FIELD_CATEGORY) != null) {
            setInput(FIELD_CATEGORY, request.getParameterValues(FIELD_CATEGORY));
        }
        if (request.getParameterValues(FIELD_PARTNER) != null) {
            setInput(FIELD_PARTNER, request.getParameterValues(FIELD_PARTNER));
        }
        if (request.getParameterValues(FIELD_GROUPING) != null) {
            setInput(FIELD_GROUPING, request.getParameter(FIELD_GROUPING));
        }
    }

    /**
     * @see de.ingrid.portal.forms.ActionForm#validate()
     */
    public boolean validate() {
        boolean allOk = true;
        clearErrors();

        // check rubric
        if (!hasInput(FIELD_THEMES)) {
            setError(FIELD_THEMES, "envSearch.error.noTheme");
            allOk = false;
        }
        if (!hasInput(FIELD_CATEGORY)) {
            setError(FIELD_CATEGORY, "envSearch.error.noCategory");
            allOk = false;
        }
        if (!hasInput(FIELD_PARTNER)) {
            setError(FIELD_PARTNER, "envSearch.error.noPartner");
            allOk = false;
        }

        return allOk;
    }
}
