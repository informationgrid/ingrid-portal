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
public class SearchExtAdrTopicModeForm extends ActionForm {

    private static final long serialVersionUID = -3606951276575977795L;

    /** attribute name of action form in session */
    public static final String SESSION_KEY = SearchExtAdrTopicModeForm.class.getName();

    /** field names (name of request parameter) */
    public static final String FIELD_SEARCH_MODE = "search_mode";
    public static final String FIELD_SEARCH_SCOPE = "search_scope";

    public static final String VALUE_SEARCH_MODE_SUBSTRING = "1";
    public static final String VALUE_SEARCH_MODE_WORD = "2";
    public static final String VALUE_SEARCH_SCOPE_ALL = "1";
    public static final String VALUE_SEARCH_SCOPE_LIMITED = "2";
    
    
    /**
     * @see de.ingrid.portal.forms.ActionForm#init()
     */
    public void init() {
        clear();
        setInput(FIELD_SEARCH_MODE, VALUE_SEARCH_MODE_WORD);
        setInput(FIELD_SEARCH_SCOPE, VALUE_SEARCH_SCOPE_ALL);
    }

    /**
     * @see de.ingrid.portal.forms.ActionForm#populate(javax.portlet.PortletRequest)
     */
    public void populate(PortletRequest request) {
        clearInput();
        setInput(FIELD_SEARCH_MODE, request.getParameter(FIELD_SEARCH_MODE));
        setInput(FIELD_SEARCH_SCOPE, request.getParameter(FIELD_SEARCH_SCOPE));
    }

    /**
     * @see de.ingrid.portal.forms.ActionForm#validate()
     */
    public boolean validate() {
        return true;
    }

}
