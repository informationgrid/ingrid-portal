/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.portal.forms;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.portlet.PortletRequest;

import org.apache.commons.fileupload.FileItem;

import de.ingrid.portal.global.Utils;

/**
 * Form Handler for Contact page. Stores and validates form input.
 * 
 * @author Martin Maidhof
 */
public class ContactZammadForm extends ActionForm {

    private static final long serialVersionUID = -8772366310279327463L;

    /** attribute name of action form in session */
    public static final String SESSION_KEY = "contact_form";

    public static final String FIELD_SUBJECT = "user_subject";

    public static final String FIELD_MESSAGE = "user_message";

    public static final String FIELD_CHK_TECHNICAL = "user_technical";

    public static final String FIELD_COMPANY = "user_company";

    public static final String FIELD_EMAIL = "user_email";

    public static final String FIELD_NAME = "user_name";

    public static final String FIELD_UPLOAD = "upload";

    public static final String FIELD_NAME_HONEYPOT = "name";

    public static final String FIELD_MESSAGE_HONEYPOT = "message";

    public static final String FIELD_EMAIL_HONEYPOT = "email";

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
        setInput(FIELD_SUBJECT, request.getParameter(FIELD_SUBJECT));
        setInput(FIELD_MESSAGE, request.getParameter(FIELD_MESSAGE));
        setInput(FIELD_CHK_TECHNICAL, request.getParameter(FIELD_CHK_TECHNICAL));
        setInput(FIELD_COMPANY, request.getParameter(FIELD_COMPANY));
        setInput(FIELD_EMAIL, request.getParameter(FIELD_EMAIL));
        setInput(FIELD_NAME, request.getParameter(FIELD_NAME));
       }

    /**
     * @see de.ingrid.portal.forms.ActionForm#validate()
     */
    public boolean validate() {
        boolean allOk = true;
        clearErrors();

        // honeypot detection
        if (hasInput(FIELD_NAME_HONEYPOT) || hasInput(FIELD_MESSAGE_HONEYPOT) || hasInput(FIELD_EMAIL_HONEYPOT)) {
            allOk = false;
        }
        // regular field validity check
        if (!hasInput(FIELD_MESSAGE)) {
            setError(FIELD_MESSAGE, "contact.error.noMessage");
            allOk = false;
        }

        if (!hasInput(FIELD_SUBJECT)) {
            setError(FIELD_SUBJECT, "contact.error.noSubject");
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

    public void setErrorUpload(){
    	clearErrors();
    	setError(FIELD_UPLOAD, "contact.error.upload");
    }
    
    public void populate(List<FileItem> items) throws UnsupportedEncodingException {
        if(items != null){
            clearInput();
            for(FileItem item : items){
                switch (item.getFieldName()) {
                    case FIELD_SUBJECT:
                        setInput(FIELD_SUBJECT, item.getString("UTF-8"));
                        break;
                    case FIELD_MESSAGE:
                        setInput(FIELD_MESSAGE, item.getString("UTF-8"));
                        break;
                    case FIELD_CHK_TECHNICAL:
                        setInput(FIELD_CHK_TECHNICAL, item.getString("UTF-8"));
                        break;
                    case FIELD_COMPANY:
                        setInput(FIELD_COMPANY, item.getString("UTF-8"));
                        break;
                    case FIELD_EMAIL:
                        setInput(FIELD_EMAIL, item.getString("UTF-8"));
                        break;
                    case FIELD_NAME:
                        setInput(FIELD_NAME, item.getString("UTF-8"));
                        break;
                    case FIELD_NAME_HONEYPOT:
                        setInput(FIELD_NAME_HONEYPOT, item.getString("UTF-8"));
                        break;
                    case FIELD_MESSAGE_HONEYPOT:
                        setInput(FIELD_MESSAGE_HONEYPOT, item.getString("UTF-8"));
                        break;
                    case FIELD_EMAIL_HONEYPOT:
                        setInput(FIELD_EMAIL_HONEYPOT, item.getString("UTF-8"));
                        break;
                }
            }
        }
    }
    
}
