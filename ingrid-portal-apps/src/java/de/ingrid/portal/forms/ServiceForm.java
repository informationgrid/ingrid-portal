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

    public static final String DEFAULT_RUBRIC = "1,2,3,4";

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
        setInput(PARAM_RUBRIC, request.getParameterValues(PARAM_RUBRIC));
        setInput(PARAM_PARTNER, request.getParameterValues(PARAM_PARTNER));
        setInput(PARAM_GROUPING, request.getParameter(PARAM_GROUPING));
    }

    /**
     * @see de.ingrid.portal.forms.ActionForm#validate()
     */
    public boolean validate() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * Check whether given value is selected in rubric
     * 
     * @param value
     * @return HTML checked string or empty string
     */
    public String checkRubricSelection(String value) {
        return isValueSelected(PARAM_RUBRIC, value, HTML_CHECKED);
    }

    /**
     * Check whether given value is selected as partner
     * 
     * @param value
     * @return HTML select string or empty string
     */
    public String checkPartnerSelection(String value) {
        return isValueSelected(PARAM_PARTNER, value, HTML_SELECTED);
    }

    /**
     * Check whether given value is selected in grouping
     * 
     * @param value
     * @return HTML checked string or empty string
     */
    public String checkGroupingSelection(String value) {
        return isValueSelected(PARAM_GROUPING, value, HTML_CHECKED);
    }

    /**
     * static getters FOR VELOCITY !
     */
    public static String getPARAM_RUBRIC() {
        return PARAM_RUBRIC;
    }

    public static String getPARAM_PARTNER() {
        return PARAM_PARTNER;
    }

    public static String getPARAM_GROUPING() {
        return PARAM_GROUPING;
    }

}
