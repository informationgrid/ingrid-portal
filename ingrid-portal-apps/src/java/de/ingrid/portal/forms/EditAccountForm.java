/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2020 wemove digital solutions GmbH
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
public class EditAccountForm extends ActionForm {

    private static final long serialVersionUID = 6699128809151974919L;

    /** attribute name of action form in session */
    public static final String SESSION_KEY = EditAccountForm.class.getName();

    public static final String FIELD_SALUTATION = "salutation";

    public static final String FIELD_FIRSTNAME = "firstname";

    public static final String FIELD_LASTNAME = "lastname";

    public static final String FIELD_EMAIL = "email";

    public static final String FIELD_PW_OLD = "password_old";

    public static final String FIELD_PW_NEW = "password_new";

    public static final String FIELD_PW_NEW_CONFIRM = "password_new_confirm";
    
    public static final String FIELD_STREET = "street";

    public static final String FIELD_POSTALCODE = "postalcode";

    public static final String FIELD_CITY = "city";
    
    public static final String FIELD_ATTENTION = "attention";

    public static final String FIELD_AGE = "age";
    
    public static final String FIELD_INTEREST = "interest";

    public static final String FIELD_PROFESSION = "profession";

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
        setInput(FIELD_PW_OLD, request.getParameter(FIELD_PW_OLD));
        setInput(FIELD_PW_NEW, request.getParameter(FIELD_PW_NEW));
        setInput(FIELD_PW_NEW_CONFIRM, request.getParameter(FIELD_PW_NEW_CONFIRM));
        setInput(FIELD_STREET, request.getParameter(FIELD_STREET));
        setInput(FIELD_POSTALCODE, request.getParameter(FIELD_POSTALCODE));
        setInput(FIELD_CITY, request.getParameter(FIELD_CITY));
        setInput(FIELD_ATTENTION, request.getParameter(FIELD_ATTENTION));
        setInput(FIELD_AGE, request.getParameter(FIELD_AGE));
        setInput(FIELD_INTEREST, request.getParameter(FIELD_INTEREST));
        setInput(FIELD_PROFESSION, request.getParameter(FIELD_PROFESSION));
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
        if (!hasInput(FIELD_PW_OLD) && hasInput(FIELD_PW_NEW)) {
            setError(FIELD_PW_OLD, "account.edit.error.noPasswordOld");
            allOk = false;
        } 
        if (hasInput(FIELD_PW_OLD) && !hasInput(FIELD_PW_NEW)) {
            setError(FIELD_PW_NEW, "account.edit.error.noPasswordNew");
            allOk = false;
        }
        if (hasInput(FIELD_PW_NEW)) {
            String password = getInput(FIELD_PW_NEW);
            if (!Utils.isStrengthPassword(password)) {
               setError(FIELD_PW_NEW, "account.create.error.worstPassword");
               allOk = false;
           }
        }
        if (!getInput(FIELD_PW_NEW_CONFIRM).equals(getInput(FIELD_PW_NEW))) {
            setError(FIELD_PW_NEW_CONFIRM, "account.edit.error.noPasswordConfirm");
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
