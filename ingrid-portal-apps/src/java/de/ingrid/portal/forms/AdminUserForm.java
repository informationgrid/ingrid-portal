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
public class AdminUserForm extends ActionForm {

    private static final long serialVersionUID = -4915342847190175138L;

    /** attribute name of action form in session */
    public static final String SESSION_KEY = EditAccountForm.class.getName();

    public static final String FIELD_SALUTATION = "salutation";

    public static final String FIELD_FIRSTNAME = "firstname";

    public static final String FIELD_LASTNAME = "lastname";

    public static final String FIELD_EMAIL = "email";

    public static final String FIELD_ID = "id";

    public static final String FIELD_PASSWORD_OLD = "password_old";

    public static final String FIELD_PASSWORD_NEW = "password_new";

    public static final String FIELD_PASSWORD_NEW_CONFIRM = "password_new_confirm";

    public static final String FIELD_STREET = "street";

    public static final String FIELD_POSTALCODE = "postalcode";

    public static final String FIELD_CITY = "city";

    public static final String FIELD_ATTENTION = "attention";

    public static final String FIELD_AGE = "age";

    public static final String FIELD_INTEREST = "interest";

    public static final String FIELD_PROFESSION = "profession";

    public static final String FIELD_SUBSCRIBE_NEWSLETTER = "subscribe_newsletter";

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
            setInput(FIELD_SALUTATION, request.getParameter(FIELD_SALUTATION));
            setInput(FIELD_FIRSTNAME, request.getParameter(FIELD_FIRSTNAME));
            setInput(FIELD_LASTNAME, request.getParameter(FIELD_LASTNAME));
            setInput(FIELD_EMAIL, request.getParameter(FIELD_EMAIL));
            if (this.getInput(FIELD_MODE).equals("new")) {
                setInput(FIELD_ID, request.getParameter(FIELD_ID));
            }
            setInput(FIELD_PASSWORD_OLD, request.getParameter(FIELD_PASSWORD_OLD));
            setInput(FIELD_PASSWORD_NEW, request.getParameter(FIELD_PASSWORD_NEW));
            setInput(FIELD_PASSWORD_NEW_CONFIRM, request.getParameter(FIELD_PASSWORD_NEW_CONFIRM));
            setInput(FIELD_STREET, request.getParameter(FIELD_STREET));
            setInput(FIELD_POSTALCODE, request.getParameter(FIELD_POSTALCODE));
            setInput(FIELD_CITY, request.getParameter(FIELD_CITY));
            setInput(FIELD_ATTENTION, request.getParameter(FIELD_ATTENTION));
            setInput(FIELD_AGE, request.getParameter(FIELD_AGE));
            setInput(FIELD_INTEREST, request.getParameter(FIELD_INTEREST));
            setInput(FIELD_PROFESSION, request.getParameter(FIELD_PROFESSION));
            setInput(FIELD_SUBSCRIBE_NEWSLETTER, request.getParameter(FIELD_SUBSCRIBE_NEWSLETTER));
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
        }
        if (hasInput(FIELD_ID) && getInput(FIELD_ID).matches(Settings.FORBIDDEN_LOGINS_REGEXP_STR)) {
            setError(FIELD_ID, "account.create.error.invalidLogin");
            setInput(FIELD_TAB, "1");
            allOk = false;
        }
        if (this.getInput(FIELD_MODE).equals("new")) {
            if (!hasInput(FIELD_PASSWORD_NEW)) {
                setError(FIELD_PASSWORD_NEW, "account.edit.error.noPasswordNew");
                setInput(FIELD_TAB, "1");
                allOk = false;
            }
            if (!getInput(FIELD_PASSWORD_NEW_CONFIRM).equals(getInput(FIELD_PASSWORD_NEW))) {
                setError(FIELD_PASSWORD_NEW_CONFIRM, "account.edit.error.noPasswordConfirm");
                setInput(FIELD_TAB, "1");
                allOk = false;
            }
        } else {
            if(!isAdmin){
	        	if (!hasInput(FIELD_PASSWORD_OLD) && hasInput(FIELD_PASSWORD_NEW)) {
	                setError(FIELD_PASSWORD_OLD, "account.edit.error.noPasswordOld");
	                setInput(FIELD_TAB, "1");
	                allOk = false;
	            }
            }
            if (hasInput(FIELD_PASSWORD_OLD) && !hasInput(FIELD_PASSWORD_NEW)) {
                setError(FIELD_PASSWORD_NEW, "account.edit.error.noPasswordNew");
                setInput(FIELD_TAB, "1");
                allOk = false;
            }
            if (!getInput(FIELD_PASSWORD_NEW_CONFIRM).equals(getInput(FIELD_PASSWORD_NEW))) {
                setError(FIELD_PASSWORD_NEW_CONFIRM, "account.edit.error.noPasswordConfirm");
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
