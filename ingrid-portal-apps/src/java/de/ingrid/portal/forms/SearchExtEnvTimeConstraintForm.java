/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.forms;

import javax.portlet.PortletRequest;

/**
 * TODO Describe your created type (class, etc.) here.
 *
 * @author joachim@wemove.com
 */
public class SearchExtEnvTimeConstraintForm extends ActionForm {

    private static final long serialVersionUID = 8809471913897989572L;

    /** attribute name of action form in session */
    public static final String SESSION_KEY = SearchExtEnvPlaceMapForm.class.getName();

    /** field names (name of request parameter) */
    
    public static final String FIELD_RADIO_TIME_SELECT = "radio_time_select";
    public static final String FIELD_FROM = "from";
    public static final String FIELD_TO = "to";
    public static final String FIELD_AT = "at";
    public static final String FIELD_CHK1 = "chk1";
    public static final String FIELD_CHK2 = "chk2";

    public static final String VALUE_FROM_TO = "1";
    public static final String VALUE_AT = "2";

    
    /**
     * @see de.ingrid.portal.forms.ActionForm#init()
     */
    public void init() {
        clear();
        setInput(FIELD_CHK1, "on");
        setInput(FIELD_CHK2, "on");
        setInput(FIELD_RADIO_TIME_SELECT, VALUE_FROM_TO);
    }

    /**
     * @see de.ingrid.portal.forms.ActionForm#populate(javax.portlet.PortletRequest)
     */
    public void populate(PortletRequest request) {
        clearInput();
        setInput(FIELD_RADIO_TIME_SELECT, request.getParameter(FIELD_RADIO_TIME_SELECT));
        setInput(FIELD_FROM, request.getParameter(FIELD_FROM));
        setInput(FIELD_TO, request.getParameter(FIELD_TO));
        setInput(FIELD_AT, request.getParameter(FIELD_AT));
        setInput(FIELD_CHK1, request.getParameter(FIELD_CHK1));
        setInput(FIELD_CHK2, request.getParameter(FIELD_CHK2));
    }

    /**
     * @see de.ingrid.portal.forms.ActionForm#validate()
     */
    public boolean validate() {
        boolean valid = true;
        clearErrors();
        if (getInput(FIELD_RADIO_TIME_SELECT).equals("1")) {
            if (!hasInput(FIELD_FROM)) {
                setError(FIELD_FROM, "searchExtEnvTimeConstraint.error.no_from");
                valid = false;
            }
            if (!hasInput(FIELD_TO)) {
                setError(FIELD_TO, "searchExtEnvTimeConstraint.error.no_to");
                valid = false;
            }
            if (!getInput(FIELD_FROM).matches("[0-3][0-9]\\.[0-1][0-9]\\.[0-9][0-9][0-9][0-9]")) {
                setError(FIELD_FROM, "searchExtEnvTimeConstraint.error.invalid_date");
                valid = false;
            }
            if (!getInput(FIELD_TO).matches("[0-3][0-9]\\.[0-1][0-9]\\.[0-9][0-9][0-9][0-9]")) {
                setError(FIELD_TO, "searchExtEnvTimeConstraint.error.invalid_date");
                valid = false;
            }
        } else if(getInput(FIELD_RADIO_TIME_SELECT).equals("2")) {
            if (!hasInput(FIELD_AT)) {
                setError(FIELD_AT, "searchExtEnvTimeConstraint.error.no_at");
                valid = false;
            }
            if (!getInput(FIELD_AT).matches("[0-3][0-9]\\.[0-1][0-9]\\.[0-9][0-9][0-9][0-9]")) {
                setError(FIELD_AT, "searchExtEnvTimeConstraint.error.invalid_date");
                valid = false;
            }
        } else {
            setError("", "searchExtEnvTimeConstraint.error");
            valid = false;
        }
        return valid;
    }

}
