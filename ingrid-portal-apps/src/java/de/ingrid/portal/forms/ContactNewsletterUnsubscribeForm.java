/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.forms;

import javax.portlet.PortletRequest;

import de.ingrid.portal.global.Utils;

/**
 * TODO Describe your created type (class, etc.) here.
 *
 * @author joachim@wemove.com
 */
public class ContactNewsletterUnsubscribeForm extends ActionForm {

    /**
     * TODO: Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = -8478111879299118798L;

    /** attribute name of action form in session */
    public static final String SESSION_KEY = "contact_newsletter_unsubscribe_form";

    public static final String FIELD_EMAIL = "email";

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
    }

    /**
     * @see de.ingrid.portal.forms.ActionForm#validate()
     */
    public boolean validate() {
        boolean allOk = true;
        clearErrors();

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
