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
public class ServiceForm extends ActionForm {

    /** attribute name of ServiceForm in session */
    public static final String SESS_ATTRIB = "service_form";

    /** parameter name of "rubric" checkbox group in form */
    public static final String PARAM_RUBRIC = "rubric";
    public static final String DEFAULT_RUBRIC = "1";

    /** parameter name of "partner" selection list in form */
    public static final String PARAM_PARTNER = "partner";
    public static final String DEFAULT_PARTNER = "1";

    /** parameter name of "grouping" radio group in form */
    public static final String PARAM_GROUPING = "grouping";
    public static final String DEFAULT_GROUPING = "1";

    /**
     * @see de.ingrid.portal.forms.ActionForm#init()
     */
    public void init() {
        clearInput();
        setInput(PARAM_RUBRIC, DEFAULT_RUBRIC);
        setInput(PARAM_PARTNER, DEFAULT_PARTNER);
        setInput(PARAM_GROUPING, DEFAULT_GROUPING);        
    }

    /**
     * @see de.ingrid.portal.forms.ActionForm#populate(javax.portlet.PortletRequest)
     */
    public void populate(PortletRequest request) {
        String rubric = request.getParameter(PARAM_RUBRIC);
        String partner = request.getParameter(PARAM_PARTNER);
        String grouping = request.getParameter(PARAM_GROUPING);
        setInput(PARAM_RUBRIC, request.getParameter(PARAM_RUBRIC));
        setInput(PARAM_PARTNER, request.getParameter(PARAM_PARTNER));
        setInput(PARAM_GROUPING, request.getParameter(PARAM_GROUPING));
    }

    /**
     * @see de.ingrid.portal.forms.ActionForm#validate()
     */
    public boolean validate() {
        // TODO Auto-generated method stub
        return false;
    }

}
