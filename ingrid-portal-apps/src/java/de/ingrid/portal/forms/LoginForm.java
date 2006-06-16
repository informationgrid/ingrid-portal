/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.forms;

import javax.portlet.PortletRequest;

import org.apache.jetspeed.login.LoginConstants;

/**
 * TODO Describe your created type (class, etc.) here.
 *
 * @author joachim@wemove.com
 */
public class LoginForm extends ActionForm {

    private static final long serialVersionUID = 3907769848429033341L;

    /** attribute name of action form in session */
    public static final String SESSION_KEY = "login_form";

    public static final String FIELD_USERNAME = LoginConstants.USERNAME;

    public static final String FIELD_PASSWORD = LoginConstants.PASSWORD;

    public static final String FIELD_DESTINATION = LoginConstants.DESTINATION;

    /**
     * initial values. can be set from outside and may differ because of Locale
     */
    protected String INITIAL_USERNAME = "";

    protected String INITIAL_PASSWORD = "";

    /**
     * @see de.ingrid.portal.forms.ActionForm#init()
     */
    public void init() {
        clearInput();
        setInput(FIELD_USERNAME, INITIAL_USERNAME);
        setInput(FIELD_PASSWORD, INITIAL_PASSWORD);
    }

    /**
     * @see de.ingrid.portal.forms.ActionForm#populate(javax.portlet.PortletRequest)
     */
    public void populate(PortletRequest request) {
        clearInput();
        setInput(FIELD_USERNAME, request.getParameter(FIELD_USERNAME));
        setInput(FIELD_PASSWORD, request.getParameter(FIELD_PASSWORD));
        setInput(FIELD_DESTINATION, request.getParameter(FIELD_DESTINATION));
    }

    /**
     * @see de.ingrid.portal.forms.ActionForm#validate()
     */
    public boolean validate() {
        boolean allOk = true;
        clearErrors();

        if (!hasInput(FIELD_USERNAME) || FIELD_USERNAME.equals(INITIAL_USERNAME)) {
            setError(FIELD_USERNAME, "login.error.noUsername");
            allOk = false;
        }
        if (!hasInput(FIELD_PASSWORD) || FIELD_PASSWORD.equals(INITIAL_PASSWORD)) {
            setError(FIELD_PASSWORD, "login.error.noPassword");
            allOk = false;
        }

        return allOk;
    }

    /**
     * @return Returns the iNITIAL_PASSWORD.
     */
    public String getINITIAL_PASSWORD() {
        return INITIAL_PASSWORD;
    }

    /**
     * @param initial_password The iNITIAL_PASSWORD to set.
     */
    public void setINITIAL_PASSWORD(String initial_password) {
        INITIAL_PASSWORD = initial_password;
    }

    /**
     * @return Returns the iNITIAL_USERNAME.
     */
    public String getINITIAL_USERNAME() {
        return INITIAL_USERNAME;
    }

    /**
     * @param initial_username The iNITIAL_USERNAME to set.
     */
    public void setINITIAL_USERNAME(String initial_username) {
        INITIAL_USERNAME = initial_username;
    }

}
