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
public class SearchExtResTopicAttributesForm extends ActionForm {

    private static final long serialVersionUID = -5260994242784162895L;

    /** attribute name of action form in session */
    public static final String SESSION_KEY = SearchExtResTopicAttributesForm.class.getName();

    /** field names (name of request parameter) */
    public static final String FIELD_DB_TITLE = "db_title";
    public static final String FIELD_DB_INSTITUTE = "db_institute";
    public static final String FIELD_DB_PM = "db_pm";
    public static final String FIELD_DB_STAFF = "db_staff";
    public static final String FIELD_DB_ORG = "db_org";
    public static final String FIELD_TERM_FROM = "term_from";
    public static final String FIELD_TERM_TO = "term_to";
    
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
        setInput(FIELD_DB_TITLE, request.getParameter(FIELD_DB_TITLE));
        setInput(FIELD_DB_INSTITUTE, request.getParameter(FIELD_DB_INSTITUTE));
        setInput(FIELD_DB_PM, request.getParameter(FIELD_DB_PM));
        setInput(FIELD_DB_STAFF, request.getParameter(FIELD_DB_STAFF));
        setInput(FIELD_DB_ORG, request.getParameter(FIELD_DB_ORG));
        setInput(FIELD_TERM_FROM, request.getParameter(FIELD_TERM_FROM));
        setInput(FIELD_TERM_TO, request.getParameter(FIELD_TERM_TO));
    }

    /**
     * @see de.ingrid.portal.forms.ActionForm#validate()
     */
    public boolean validate() {
        boolean valid = true;
        clearErrors();
        if (!hasInput(FIELD_DB_TITLE) && 
                !hasInput(FIELD_DB_INSTITUTE) && 
                !hasInput(FIELD_DB_PM) && 
                !hasInput(FIELD_DB_STAFF) && 
                !hasInput(FIELD_DB_ORG) && 
                !hasInput(FIELD_TERM_FROM) && 
                !hasInput(FIELD_TERM_TO)) {
            setError("", "searchExtResTopicAttributes.error.supply_field");
            valid = false;
        }
        if (hasInput(FIELD_TERM_FROM) && !getInput(FIELD_TERM_FROM).matches("[0-3][0-9]\\.[0-1][0-9]\\.[0-9][0-9][0-9][0-9]")) {
            setError(FIELD_TERM_FROM, "searchExtResTopicAttributes.error.invalid_from");
            valid = false;
        }
        if (hasInput(FIELD_TERM_TO) && !getInput(FIELD_TERM_TO).matches("[0-3][0-9]\\.[0-1][0-9]\\.[0-9][0-9][0-9][0-9]")) {
            setError(FIELD_TERM_TO, "searchExtResTopicAttributes.error.invalid_to");
            valid = false;
        }
        
        return valid;    
    }

}
