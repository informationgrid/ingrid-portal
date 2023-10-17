/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2023 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.portal.forms;

import de.ingrid.portal.global.Utils;

import javax.portlet.PortletRequest;

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

    public static final String FIELD_FIRSTNAME = "user_firstname";

    public static final String FIELD_LASTNAME = "user_lastname";

    public static final String FIELD_EMAIL = "user_email";

    public static final String FIELD_LOGIN = "login";
    
    public static final String FIELD_PW = "password";

    public static final String FIELD_PW_CONFIRM = "password_confirm";
    
    public static final String FIELD_STREET = "street";

    public static final String FIELD_POSTALCODE = "postalcode";

    public static final String FIELD_CITY = "city";

    /** attribute name of honeypot in session */
    public static final String FIELD_HONEYPOT_FIRSTNAME = "firstname";

    public static final String FIELD_HONEYPOT_LASTNAME = "lastname";

    public static final String FIELD_HONEYPOT_EMAIL = "email";

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
        
        setInput(FIELD_SALUTATION, request.getParameter(FIELD_SALUTATION).trim());
        setInput(FIELD_FIRSTNAME, request.getParameter(FIELD_FIRSTNAME).trim());
        setInput(FIELD_LASTNAME, request.getParameter(FIELD_LASTNAME).trim());
        setInput(FIELD_EMAIL, request.getParameter(FIELD_EMAIL).trim());
        // Show error message if spaces exist on login input
        setInput(FIELD_LOGIN, request.getParameter(FIELD_LOGIN));
        setInput(FIELD_PW, request.getParameter(FIELD_PW).trim());
        setInput(FIELD_PW_CONFIRM, request.getParameter(FIELD_PW_CONFIRM).trim());
        setInput(FIELD_STREET, request.getParameter(FIELD_STREET).trim());
        setInput(FIELD_POSTALCODE, request.getParameter(FIELD_POSTALCODE).trim());
        setInput(FIELD_CITY, request.getParameter(FIELD_CITY).trim());
        // set honeypot attributes for validate
        setInput(FIELD_HONEYPOT_FIRSTNAME, request.getParameter(FIELD_HONEYPOT_FIRSTNAME).trim());
        setInput(FIELD_HONEYPOT_LASTNAME, request.getParameter(FIELD_HONEYPOT_LASTNAME).trim());
        setInput(FIELD_HONEYPOT_EMAIL, request.getParameter(FIELD_HONEYPOT_EMAIL).trim());
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
        } else {
            if (Utils.isInvalidInput(getInput(FIELD_FIRSTNAME))) {
                setError(FIELD_FIRSTNAME, "account.create.error.input.sign");
                allOk = false;
            }
        }
        if (!hasInput(FIELD_LASTNAME)) {
            setError(FIELD_LASTNAME, "account.edit.error.noLastName");
            allOk = false;
        } else {
            if (Utils.isInvalidInput(getInput(FIELD_LASTNAME))) {
                setError(FIELD_LASTNAME, "account.create.error.input.sign");
                allOk = false;
            }
        }
        if (!hasInput(FIELD_LOGIN)) {
            setError(FIELD_LOGIN, "account.create.error.noLogin");
            allOk = false;
        } else {
            String login = getInput(FIELD_LOGIN);
            if (!Utils.isValidLogin(login)) {
                setError(FIELD_LOGIN, "account.create.error.invalidLogin");
                allOk = false;
            }
        }
        if (!hasInput(FIELD_PW)) {
            setError(FIELD_PW, "account.create.error.noPassword");
            allOk = false;
        } else {
            String password = getInput(FIELD_PW);
            if (Utils.isInvalidInput(password)) {
                setError(FIELD_PW, "account.create.error.password.sign");
                clearInput(FIELD_PW);
                allOk = false;;
            } else if (!Utils.isStrengthPassword(password)) {
                setError(FIELD_PW, "account.create.error.worstPassword");
                allOk = false;
            }
        }
        if (!getInput(FIELD_PW_CONFIRM).equals(getInput(FIELD_PW))) {
            setError(FIELD_PW_CONFIRM, "account.edit.error.noPasswordConfirm");
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
        // check if honeypots are filled
        if (hasInput(FIELD_HONEYPOT_FIRSTNAME) || hasInput(FIELD_HONEYPOT_LASTNAME) || hasInput(FIELD_HONEYPOT_EMAIL)) {
            allOk = false;
        }

        if (hasInput(FIELD_STREET)) {
            if (Utils.isInvalidInput(getInput(FIELD_STREET))) {
                setError(FIELD_STREET, "account.create.error.input.sign");
                allOk = false;;
            }
        }
        if (hasInput(FIELD_POSTALCODE)) {
            if (Utils.isInvalidInput(getInput(FIELD_POSTALCODE))) {
                setError(FIELD_POSTALCODE, "account.create.error.input.sign");
                allOk = false;;
            }
        }
        if (hasInput(FIELD_CITY)) {
            if (Utils.isInvalidInput(getInput(FIELD_CITY))) {
                setError(FIELD_CITY, "account.create.error.input.sign");
                allOk = false;;
            }
        }
        return allOk;
    }

}
