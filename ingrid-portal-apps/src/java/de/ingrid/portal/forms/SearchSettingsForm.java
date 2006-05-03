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
public class SearchSettingsForm extends ActionForm {

    private static final long serialVersionUID = -740486322279385676L;

    /** attribute name of action form in session */
    public static final String SESSION_KEY = SearchSettingsForm.class.getName();

    /** field names (name of request parameter) */
    public static final String FIELD_SORTING = "sorting";
    public static final String FIELD_GROUPING = "grouping";
    public static final String FIELD_INCL_META = "incl_meta";

    private static final String VALUE_GROUPING_INIT = "none";
    private static final String VALUE_SORTING_INIT = "score";
    
    
    /**
     * @see de.ingrid.portal.forms.ActionForm#init()
     */
    public void init() {
        clear();
        setInput(FIELD_GROUPING, VALUE_GROUPING_INIT);
        setInput(FIELD_SORTING, VALUE_SORTING_INIT);
    }

    /**
     * @see de.ingrid.portal.forms.ActionForm#populate(javax.portlet.PortletRequest)
     */
    public void populate(PortletRequest request) {
        clearInput();
        setInput(FIELD_SORTING, request.getParameter(FIELD_SORTING));
        setInput(FIELD_GROUPING, request.getParameter(FIELD_GROUPING));
        setInput(FIELD_INCL_META, request.getParameter(FIELD_INCL_META));
    }

    /**
     * @see de.ingrid.portal.forms.ActionForm#validate()
     */
    public boolean validate() {
        return true;
    }

}
