/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.forms;

import javax.portlet.PortletRequest;

import de.ingrid.portal.config.PortalConfig;

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
    public static final String FIELD_RANKING = "ranking";
    public static final String FIELD_GROUPING = "grouping";
    public static final String FIELD_INCL_META = "incl_meta";

    private static final String VALUE_GROUPING_INIT = "domain";
    private static final String VALUE_GROUPING_NONE = "none";
    private static final String VALUE_RANKING_INIT = "score";
    
    
    /**
     * @see de.ingrid.portal.forms.ActionForm#init()
     */
    public void init() {
        clear();
    	if (PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_ENABLE_DEFAULT_GROUPING_DOMAIN, false)) {
    		setInput(FIELD_GROUPING, VALUE_GROUPING_INIT);
    	} else {
    		setInput(FIELD_GROUPING, VALUE_GROUPING_NONE);
    	}
        setInput(FIELD_RANKING, VALUE_RANKING_INIT);
    }

    /**
     * @see de.ingrid.portal.forms.ActionForm#populate(javax.portlet.PortletRequest)
     */
    public void populate(PortletRequest request) {
        clearInput();
        setInput(FIELD_RANKING, request.getParameter(FIELD_RANKING));
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
