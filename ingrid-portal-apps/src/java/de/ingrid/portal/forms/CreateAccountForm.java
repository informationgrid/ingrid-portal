/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.forms;

import javax.portlet.PortletRequest;

import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;

/**
 * TODO Describe your created type (class, etc.) here.
 *
 * @author joachim@wemove.com
 */
public class CreateAccountForm extends ActionForm {

    private static final long serialVersionUID = 7352464470933474681L;

    /** attribute name of action form in session */
    public static final String SESSION_KEY = "createaccount_form";

    public static final String FIELD_SALUTATION = "salutation";

    public static final String FIELD_FIRSTNAME = "firstname";

    public static final String FIELD_LASTNAME = "lastname";

    public static final String FIELD_EMAIL = "email";

    public static final String FIELD_LOGIN = "login";
    
    public static final String FIELD_PASSWORD = "password";

    public static final String FIELD_PASSWORD_CONFIRM = "password_confirm";
    
    public static final String FIELD_STREET = "street";

    public static final String FIELD_POSTALCODE = "postalcode";

    public static final String FIELD_CITY = "city";
    
    public static final String FIELD_ATTENTION = "attention";

    public static final String FIELD_AGE = "age";
    
    public static final String FIELD_INTEREST = "interest";

    public static final String FIELD_PROFESSION = "profession";

    public static final String FIELD_SUBSCRIBE_NEWSLETTER = "subscribe_newsletter";

    /**
     * @see de.ingrid.portal.forms.ActionForm#init()
     */
    public void init() {
        clearInput();
    }

    /**
     * @see de.ingrid.portal.forms.ActionForm#populate(javax.portlet.PortletRequest)
     */
    public void populate(PortletRequest request) {
        clearInput();
        
        setInput(FIELD_SALUTATION, request.getParameter(FIELD_SALUTATION));
        setInput(FIELD_FIRSTNAME, request.getParameter(FIELD_FIRSTNAME));
        setInput(FIELD_LASTNAME, request.getParameter(FIELD_LASTNAME));
        setInput(FIELD_EMAIL, request.getParameter(FIELD_EMAIL));
        setInput(FIELD_LOGIN, request.getParameter(FIELD_LOGIN));
        setInput(FIELD_PASSWORD, request.getParameter(FIELD_PASSWORD));
        setInput(FIELD_PASSWORD_CONFIRM, request.getParameter(FIELD_PASSWORD_CONFIRM));
        setInput(FIELD_STREET, request.getParameter(FIELD_STREET));
        setInput(FIELD_POSTALCODE, request.getParameter(FIELD_POSTALCODE));
        setInput(FIELD_CITY, request.getParameter(FIELD_CITY));
        setInput(FIELD_ATTENTION, request.getParameter(FIELD_ATTENTION));
        setInput(FIELD_AGE, request.getParameter(FIELD_AGE));
        setInput(FIELD_INTEREST, request.getParameter(FIELD_INTEREST));
        setInput(FIELD_PROFESSION, request.getParameter(FIELD_PROFESSION));
        setInput(FIELD_SUBSCRIBE_NEWSLETTER, request.getParameter(FIELD_SUBSCRIBE_NEWSLETTER));
    }

    /**
     * @see de.ingrid.portal.forms.ActionForm#validate()
     */
    public boolean validate() {
        boolean allOk = true;
        clearErrors();

        if (!hasInput(FIELD_SALUTATION)) {
            setError(FIELD_SALUTATION, "account.edit.error.noSalutation");
            allOk = false;
        } 
        if (!hasInput(FIELD_FIRSTNAME)) {
            setError(FIELD_FIRSTNAME, "account.edit.error.noFirstName");
            allOk = false;
        } 
        if (!hasInput(FIELD_LASTNAME)) {
            setError(FIELD_LASTNAME, "account.edit.error.noLastName");
            allOk = false;
        } 
        if (!hasInput(FIELD_LOGIN)) {
            setError(FIELD_LOGIN, "account.create.error.noLogin");
            allOk = false;
        } 
        if (hasInput(FIELD_LOGIN) && getInput(FIELD_LOGIN).matches(Settings.FORBIDDEN_LOGINS_REGEXP_STR)) {
            setError(FIELD_LOGIN, "account.create.error.invalidLogin");
            allOk = false;
        }
        if (!hasInput(FIELD_PASSWORD)) {
            setError(FIELD_PASSWORD, "account.create.error.noPassword");
            allOk = false;
        } 
        if (!getInput(FIELD_PASSWORD_CONFIRM).equals(getInput(FIELD_PASSWORD))) {
            setError(FIELD_PASSWORD_CONFIRM, "account.edit.error.noPasswordConfirm");
            allOk = false;
        } 
        if (!hasInput(FIELD_EMAIL)) {
            setError(FIELD_EMAIL, "account.edit.error.noEmail");
            allOk = false;
        } else {
            String myEmail = getInput(FIELD_EMAIL);
            if (!Utils.isValidEmailAddress(myEmail)) {
                setError(FIELD_EMAIL, "account.edit.error.emailNotValid");
                allOk = false;
            }
        }

        return allOk;    
    }

}
