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
public class SearchExtEnvAreaContentsForm extends ActionForm {

    private static final long serialVersionUID = -4672005952193155256L;

    /** attribute name of action form in session */
    public static final String SESSION_KEY = SearchExtEnvAreaContentsForm.class.getName();

    /** field names (name of request parameter) */
    public static final String FIELD_CONTENT_TYPE = "content_type";

    public static final String VALUE_CONTENT_TYPE_ALL = "1";
    public static final String VALUE_CONTENT_TYPE_TOPICS = "2";
    public static final String VALUE_CONTENT_TYPE_SERVICE = "3";
    
    /**
     * @see de.ingrid.portal.forms.ActionForm#init()
     */
    public void init() {
        clear();
        setInput(FIELD_CONTENT_TYPE, VALUE_CONTENT_TYPE_ALL);
    }

    /**
     * @see de.ingrid.portal.forms.ActionForm#populate(javax.portlet.PortletRequest)
     */
    public void populate(PortletRequest request) {
        clearInput();
        setInput(FIELD_CONTENT_TYPE, request.getParameter(FIELD_CONTENT_TYPE));
    }

    /**
     * @see de.ingrid.portal.forms.ActionForm#validate()
     */
    public boolean validate() {
        // no validation required
        return true;
    }

}
