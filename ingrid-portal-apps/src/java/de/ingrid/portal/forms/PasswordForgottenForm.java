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
public class PasswordForgottenForm extends ActionForm {

    private static final long serialVersionUID = 4794576308390298403L;

    /** attribute name of action form in session */
    public static final String SESSION_KEY = "createaccount_form";

    public static final String FIELD_EMAIL = "email";

    public static final String FIELD_LOGIN = "login";

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
        
        setInput(FIELD_EMAIL, request.getParameter(FIELD_EMAIL));
        setInput(FIELD_LOGIN, request.getParameter(FIELD_LOGIN));
    }

    /**
     * @see de.ingrid.portal.forms.ActionForm#validate()
     */
    public boolean validate() {
        boolean allOk = true;
        clearErrors();

        if (!hasInput(FIELD_EMAIL)) {
            setError(FIELD_EMAIL, "password.forgotten.error.noEmail");
            allOk = false;
        } else {
            String myEmail = getInput(FIELD_EMAIL);
            if (Utils.isInvalidInput(myEmail)) {
                clearInput(FIELD_EMAIL);
                setError(FIELD_EMAIL, "password.forgotten.error.emailNotValid");
                allOk = false;
            } else if (!Utils.isValidEmailAddress(myEmail)) {
                setError(FIELD_EMAIL, "password.forgotten.error.emailNotValid");
                allOk = false;
            }
        }

        if (hasInput(FIELD_LOGIN)) {
            String login = getInput(FIELD_LOGIN);
            if(Utils.isInvalidInput(login)) {
                setError(PasswordForgottenForm.FIELD_LOGIN, "password.forgotten.error.loginNotExists");
                clearInput(FIELD_LOGIN);
                allOk = false;
            }
        }
        return allOk;
    }

}
