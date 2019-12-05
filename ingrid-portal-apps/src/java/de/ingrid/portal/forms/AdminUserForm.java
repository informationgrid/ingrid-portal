/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2019 wemove digital solutions GmbH
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
public class AdminUserForm extends ActionForm {

    private static final long serialVersionUID = -4915342847190175138L;

    /** attribute name of action form in session */
    public static final String SESSION_KEY = EditAccountForm.class.getName();

    public static final String FIELD_SALUTATION = "salutation";

    public static final String FIELD_FIRSTNAME = "firstname";

    public static final String FIELD_LASTNAME = "lastname";

    public static final String FIELD_EMAIL = "email";

    public static final String FIELD_ID = "id";

    public static final String FIELD_PW_OLD = "password_old";

    public static final String FIELD_PW_NEW = "password_new";

    public static final String FIELD_PW_NEW_CONFIRM = "password_new_confirm";

    public static final String FIELD_STREET = "street";

    public static final String FIELD_POSTALCODE = "postalcode";

    public static final String FIELD_CITY = "city";

    public static final String FIELD_MODE = "mode";

    public static final String FIELD_TAB = "tab";

    public static final String FIELD_CHK_ADMIN_PORTAL = "admin_portal";

    public static final String FIELD_CHK_ADMIN_PARTNER = "admin_partner";

    public static final String FIELD_CHK_ADMIN_INDEX = "admin_index";

    public static final String FIELD_CHK_ADMIN_CATALOG = "admin_catalog";

    public static final String FIELD_PARTNER = "partner";

    public static final String FIELD_PARTNER_NAME = "partner_name";

    public static final String FIELD_PROVIDER = "provider";

    public static final String FIELD_LAYOUT_PERMISSION = "layoutPermission";

    public static final String FIELD_CHK_ENABLED = "enabled";

    public static final String FIELD_CHK_PW_UPDATE_REQUIRED = "password_update_required";

    public static final String FIELD_CHK_SEND_INFO = "send_info";

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
        setInput(FIELD_MODE, request.getParameter(FIELD_MODE));
        setInput(FIELD_ID, request.getParameter(FIELD_ID));
        setInput(FIELD_TAB, request.getParameter(FIELD_TAB));
        setInput(FIELD_LAYOUT_PERMISSION, request.getParameter(FIELD_LAYOUT_PERMISSION));

        // if tab 1 was selected onle populate fields from tab1
        if (this.getInput(FIELD_TAB).equals("1") || this.getInput(FIELD_MODE).equals("new")) {
            setInput(FIELD_SALUTATION, request.getParameter(FIELD_SALUTATION).trim());
            setInput(FIELD_FIRSTNAME, request.getParameter(FIELD_FIRSTNAME).trim());
            setInput(FIELD_LASTNAME, request.getParameter(FIELD_LASTNAME).trim());
            setInput(FIELD_EMAIL, request.getParameter(FIELD_EMAIL).trim());
            if (this.getInput(FIELD_MODE).equals("new")) {
                // Show error message if spaces exist on login input
                setInput(FIELD_ID, request.getParameter(FIELD_ID));
            }
            if(request.getParameter(FIELD_PW_OLD) != null) {
                setInput(FIELD_PW_OLD, request.getParameter(FIELD_PW_OLD).trim());
            }
            setInput(FIELD_PW_NEW, request.getParameter(FIELD_PW_NEW).trim());
            setInput(FIELD_PW_NEW_CONFIRM, request.getParameter(FIELD_PW_NEW_CONFIRM).trim());
            setInput(FIELD_STREET, request.getParameter(FIELD_STREET).trim());
            setInput(FIELD_POSTALCODE, request.getParameter(FIELD_POSTALCODE).trim());
            setInput(FIELD_CITY, request.getParameter(FIELD_CITY).trim());
            setInput(FIELD_CHK_ENABLED, request.getParameter(FIELD_CHK_ENABLED));
            setInput(FIELD_CHK_PW_UPDATE_REQUIRED, request.getParameter(FIELD_CHK_PW_UPDATE_REQUIRED));
            setInput(FIELD_CHK_SEND_INFO, request.getParameter(FIELD_CHK_SEND_INFO));
            // if tab 2 was selected onle populate fields from tab2
        } else if (this.getInput(FIELD_TAB).equals("2")) {
            setInput(FIELD_CHK_ADMIN_PORTAL, request.getParameter(FIELD_CHK_ADMIN_PORTAL));
            setInput(FIELD_CHK_ADMIN_PARTNER, request.getParameter(FIELD_CHK_ADMIN_PARTNER));
            setInput(FIELD_CHK_ADMIN_INDEX, request.getParameter(FIELD_CHK_ADMIN_INDEX));
            setInput(FIELD_CHK_ADMIN_CATALOG, request.getParameter(FIELD_CHK_ADMIN_CATALOG));
            setInput(FIELD_PARTNER, request.getParameter(FIELD_PARTNER));
            setInput(FIELD_PROVIDER, request.getParameterValues(FIELD_PROVIDER));
        }
    }

    /**
     * @see de.ingrid.portal.forms.ActionForm#validate()
     */
    public boolean validate(){
    	return validate(false);
    }
    
    public boolean validate(boolean isAdmin) {
        boolean allOk = true;
        clearErrors();

        // check data on TAB 1
        if (!hasInput(FIELD_SALUTATION)) {
            setError(FIELD_SALUTATION, "account.edit.error.noSalutation");
            setInput(FIELD_TAB, "1");
            allOk = false;
        }
        if (!hasInput(FIELD_FIRSTNAME)) {
            setError(FIELD_FIRSTNAME, "account.edit.error.noFirstName");
            setInput(FIELD_TAB, "1");
            allOk = false;
        }
        if (!hasInput(FIELD_LASTNAME)) {
            setError(FIELD_LASTNAME, "account.edit.error.noLastName");
            setInput(FIELD_TAB, "1");
            allOk = false;
        }
        if (!hasInput(FIELD_ID)) {
            setError(FIELD_ID, "account.create.error.noLogin");
            setInput(FIELD_TAB, "1");
            allOk = false;
        } else {
            String login = getInput(FIELD_ID);
            if (!Utils.isValidLogin(login) && this.getInput(FIELD_MODE).equals("new")) {
                setError(FIELD_ID, "account.create.error.invalidLogin");
                setInput(FIELD_TAB, "1");
                allOk = false;
            }
        }
        if (this.getInput(FIELD_MODE).equals("new")) {
            if (!hasInput(FIELD_PW_NEW)) {
                setError(FIELD_PW_NEW, "account.edit.error.noPasswordNew");
                setInput(FIELD_TAB, "1");
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
                setInput(FIELD_TAB, "1");
                allOk = false;
            }
        } else {
            if(!isAdmin && !hasInput(FIELD_PW_OLD) && hasInput(FIELD_PW_NEW)){
                setError(FIELD_PW_OLD, "account.edit.error.noPasswordOld");
                setInput(FIELD_TAB, "1");
                allOk = false;
            }
            if (hasInput(FIELD_PW_OLD) && !hasInput(FIELD_PW_NEW)) {
                setError(FIELD_PW_NEW, "account.edit.error.noPasswordNew");
                setInput(FIELD_TAB, "1");
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
                setInput(FIELD_TAB, "1");
                allOk = false;
            }
            if (hasInput(FIELD_CHK_SEND_INFO) && !hasInput(FIELD_PW_NEW)) {
                setError(FIELD_PW_NEW, "account.edit.error.noPasswordNew");
                setError(FIELD_CHK_SEND_INFO, "account.edit.error.send.info");
                setInput(FIELD_TAB, "1");
                allOk = false;
            }
        }
        if (!hasInput(FIELD_EMAIL)) {
            setError(FIELD_EMAIL, "account.edit.error.noEmail");
            setInput(FIELD_TAB, "1");
            allOk = false;
        } else {
            String myEmail = getInput(FIELD_EMAIL);
            if (!Utils.isValidEmailAddress(myEmail)) {
                setError(FIELD_EMAIL, "account.edit.error.emailNotValid");
                setInput(FIELD_TAB, "1");
                allOk = false;
            }
        }
        if (!hasInput(FIELD_PARTNER)
                && (hasInput(FIELD_CHK_ADMIN_PARTNER) || hasInput(FIELD_CHK_ADMIN_INDEX) || hasInput(FIELD_CHK_ADMIN_CATALOG))) {
            setError(FIELD_PARTNER, "account.edit.error.noPartnerSelected");
            // only switch tabs if there where no errors before
            if (allOk) {
                setInput(FIELD_TAB, "2");
            }
            allOk = false;
        }
        if (!hasInput(FIELD_PROVIDER) && (hasInput(FIELD_CHK_ADMIN_INDEX) || hasInput(FIELD_CHK_ADMIN_CATALOG))) {
            setError(FIELD_PROVIDER, "account.edit.error.noProviderSelected");
            // only switch tabs if there where no errors before
            if (allOk) {
                setInput(FIELD_TAB, "2");
            }
            allOk = false;
        }

        return allOk;
    }
}
