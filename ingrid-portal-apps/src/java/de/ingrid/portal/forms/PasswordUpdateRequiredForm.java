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
public class PasswordUpdateRequiredForm extends ActionForm {

    private static final long serialVersionUID = 6699128809151974919L;

    /** attribute name of action form in session */
    public static final String SESSION_KEY = PasswordUpdateRequiredForm.class.getName();

    public static final String FIELD_LOGIN = "login";

    public static final String FIELD_PW = "password";

    public static final String FIELD_PW_CONFIRM = "password_confirm";

    public static final String FIELD_PW_OLD = "password_old";

    public static final String FIELD_PW_NEW = "password_new";

    public static final String FIELD_PW_NEW_CONFIRM = "password_new_confirm";

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
        
        setInput(FIELD_LOGIN, request.getParameter(FIELD_LOGIN));
        setInput(FIELD_PW, request.getParameter(FIELD_PW));
        setInput(FIELD_PW_CONFIRM, request.getParameter(FIELD_PW_CONFIRM));
        setInput(FIELD_PW_OLD, request.getParameter(FIELD_PW_OLD));
        setInput(FIELD_PW_NEW, request.getParameter(FIELD_PW_NEW));
        setInput(FIELD_PW_NEW_CONFIRM, request.getParameter(FIELD_PW_NEW_CONFIRM));
    }

    /**
     * @see de.ingrid.portal.forms.ActionForm#validate()
     */
    public boolean validate() {
        boolean allOk = true;
        clearErrors();
        
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
        return allOk;
    }

    public boolean validate(boolean isPasswordRequired) {
        boolean allOk = true;
        clearErrors();

        if (!isPasswordRequired) {
            if (hasInput(FIELD_PW)) {
                String password = getInput(FIELD_PW);
                if (!Utils.isStrengthPassword(password)) {
                   setError(FIELD_PW, "account.create.error.worstPassword");
                   allOk = false;
               }
            } else {
                setError(FIELD_PW, "account.create.error.worstPassword");
                allOk = false;
            }
            if (!getInput(FIELD_PW_CONFIRM).equals(getInput(FIELD_PW))) {
                setError(FIELD_PW_CONFIRM, "account.edit.error.noPasswordConfirm");
                allOk = false;
            }
        } else {
            if (!hasInput(FIELD_PW_OLD)) {
                setError(FIELD_PW_OLD, "account.edit.error.noPasswordOld");
                allOk = false;
            } 
            if (!hasInput(FIELD_PW_NEW)) {
                setError(FIELD_PW_NEW, "account.edit.error.noPasswordNew");
                allOk = false;
            } else {
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
        }
        return allOk;
    }

}
