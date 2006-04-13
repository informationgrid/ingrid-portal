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
public class SearchExtEnvPlaceMapForm extends ActionForm {

    private static final long serialVersionUID = 2308183206172379352L;

    /** attribute name of action form in session */
    public static final String SESSION_KEY = SearchExtEnvPlaceMapForm.class.getName();

    /** field names (name of request parameter) */
    public static final String FIELD_CHK1 = "chk_1";
    public static final String FIELD_CHK2 = "chk_2";
    public static final String FIELD_CHK3 = "chk_3";
    
    /**
     * @see de.ingrid.portal.forms.ActionForm#init()
     */
    public void init() {
        clearInput();
        setInput(FIELD_CHK1, "on");
    }

    /**
     * @see de.ingrid.portal.forms.ActionForm#populate(javax.portlet.PortletRequest)
     */
    public void populate(PortletRequest request) {
        clearInput();
        setInput(FIELD_CHK1, request.getParameter(FIELD_CHK1));
        setInput(FIELD_CHK2, request.getParameter(FIELD_CHK2));
        setInput(FIELD_CHK3, request.getParameter(FIELD_CHK3));
    }

    /**
     * @see de.ingrid.portal.forms.ActionForm#validate()
     */
    public boolean validate() {
        boolean valid = true;
        clearErrors();
        if (!hasInput(FIELD_CHK1) && !hasInput(FIELD_CHK2) && !hasInput(FIELD_CHK3)) {
            setError("constraint", "searchExtEnvPlaceMap.error.no_constraint");
            valid = false;
        }
        return valid;
    }

}
