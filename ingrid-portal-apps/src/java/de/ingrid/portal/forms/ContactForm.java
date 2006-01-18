/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.forms;

import javax.portlet.PortletRequest;

import de.ingrid.portal.utils.Utils;

/**
 * Form Handler for Contact page. Stores and validates form input.
 * 
 * @author Martin Maidhof
 */
public class ContactForm extends ActionForm {

    /** attribute name of action form in session */
    public static final String SESSION_KEY = "contact_form";

    public static final String FIELD_MESSAGE = "message";

    public static final String FIELD_FIRSTNAME = "firstname";

    public static final String FIELD_LASTNAME = "lastname";

    public static final String FIELD_COMPANY = "company";

    public static final String FIELD_PHONE = "phone";

    public static final String FIELD_EMAIL = "email";

    public static final String FIELD_ACTIVITY = "activity";

    public static final String FIELD_INTEREST = "interest";

    public static final String FIELD_NEWSLETTER = "newsletter";

    /**
     * @see de.ingrid.portal.forms.ActionForm#init()
     */
    public void init() {
        clearInput();
        // no default data to set
    }

    /**
     * @see de.ingrid.portal.forms.ActionForm#populate(javax.portlet.PortletRequest)
     */
    public void populate(PortletRequest request) {
        clearInput();
        setInput(FIELD_MESSAGE, request.getParameter(FIELD_MESSAGE));
        setInput(FIELD_FIRSTNAME, request.getParameter(FIELD_FIRSTNAME));
        setInput(FIELD_LASTNAME, request.getParameter(FIELD_LASTNAME));
        setInput(FIELD_COMPANY, request.getParameter(FIELD_COMPANY));
        setInput(FIELD_PHONE, request.getParameter(FIELD_PHONE));
        setInput(FIELD_EMAIL, request.getParameter(FIELD_EMAIL));
        setInput(FIELD_ACTIVITY, request.getParameter(FIELD_ACTIVITY));
        setInput(FIELD_INTEREST, request.getParameter(FIELD_INTEREST));
        setInput(FIELD_NEWSLETTER, request.getParameter(FIELD_NEWSLETTER));
    }

    /**
     * @see de.ingrid.portal.forms.ActionForm#validate()
     */
    public boolean validate() {
        boolean allOk = true;
        clearErrors();

        if (!hasInput(FIELD_MESSAGE)) {
            setError(FIELD_MESSAGE, "contact.error.noMessage");
            allOk = false;
        }
        if (!hasInput(FIELD_FIRSTNAME)) {
            setError(FIELD_FIRSTNAME, "contact.error.noFirstName");
            allOk = false;
        }
        if (!hasInput(FIELD_LASTNAME)) {
            setError(FIELD_LASTNAME, "contact.error.noLastName");
            allOk = false;
        }
        if (!hasInput(FIELD_EMAIL)) {
            setError(FIELD_EMAIL, "contact.error.noEmail");
            allOk = false;
        } else {
            String myEmail = getInput(FIELD_EMAIL);
            if (!Utils.isValidEmailAddress(myEmail)) {
                setError(FIELD_EMAIL, "contact.error.emailNotValid");
                allOk = false;
            }
        }

        return allOk;
    }
}
