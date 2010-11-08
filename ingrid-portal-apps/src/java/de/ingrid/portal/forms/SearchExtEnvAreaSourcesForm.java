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
public class SearchExtEnvAreaSourcesForm extends ActionForm {

    private static final long serialVersionUID = -696997019850499684L;

    /** attribute name of action form in session */
    public static final String SESSION_KEY = SearchExtEnvAreaSourcesForm.class.getName();

    /** field names (name of request parameter) */
    
    public static final String FIELD_CHK_SOURCES = "sources";
    public static final String FIELD_CHK_META = "meta";

    public static final String VALUE_SOURCE_ALL = "all";
    public static final String VALUE_SOURCE_WWW = "www";
    public static final String VALUE_META_ALL = "all";
    public static final String VALUE_META_0 = "meta_0";
    public static final String VALUE_META_1 = "meta_1";
    public static final String VALUE_META_2 = "meta_2";
    public static final String VALUE_META_3 = "meta_3";
    public static final String VALUE_META_4 = "meta_4";
    public static final String VALUE_META_5 = "meta_5";
    public static final String VALUE_META_6 = "meta_6";
    public static final String VALUE_SOURCE_FIS = "fis";
    
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
        this.setInput(FIELD_CHK_SOURCES, request.getParameterValues(FIELD_CHK_SOURCES));
        this.setInput(FIELD_CHK_META, request.getParameterValues(FIELD_CHK_META));
    }

    /**
     * @see de.ingrid.portal.forms.ActionForm#validate()
     */
    public boolean validate() {
        return true;
    }

}
