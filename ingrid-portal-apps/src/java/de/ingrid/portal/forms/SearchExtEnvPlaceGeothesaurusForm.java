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
public class SearchExtEnvPlaceGeothesaurusForm extends ActionForm {

    private static final long serialVersionUID = -3243020432308518967L;
    
    /** attribute name of action form in session */
    public static final String SESSION_KEY = SearchExtEnvPlaceGeothesaurusForm.class.getName();

    /** field names (name of request parameter) */
    public static final String FIELD_SEARCH_TERM = "search_term";
    
    /**
     * @see de.ingrid.portal.forms.ActionForm#init()
     */
    public void init() {
        clear();
    }

    /**
     * @see de.ingrid.portal.forms.ActionForm#populate(javax.portlet.PortletRequest)
     */
    public void populate(PortletRequest request) {
        clearInput();
        setInput(FIELD_SEARCH_TERM, request.getParameter(FIELD_SEARCH_TERM));
    }

    /**
     * @see de.ingrid.portal.forms.ActionForm#validate()
     */
    public boolean validate() {
        boolean valid = true;
        clearErrors();
        if (!hasInput(FIELD_SEARCH_TERM)) {
            setError(FIELD_SEARCH_TERM, "searchExtEnvPlaceGeothesaurus.error.no_term");
            valid = false;
        }
        return valid;
    }

}
