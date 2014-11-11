/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
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
/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.forms;

import javax.portlet.PortletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ingrid.portal.global.Utils;
import de.ingrid.portal.upgradeclient.IngridComponent;

/**
 * Form Handler for Content Management of Providers.
 * 
 * @author joachim@wemove.com
 */
public class AdminComponentUpdateForm extends ActionForm {

	private final static Logger log = LoggerFactory.getLogger(AdminComponentUpdateForm.class);

	private static final long serialVersionUID = 8335389649101265303L;

    /** attribute name of action form in session */
    public static final String SESSION_KEY          = "component_update_form";

    public static final String FIELD_ID             = "componentId";
    
    public static final String OLD_FIELD_ID         = "oldComponentId";

	public static final String FIELD_TITLE          = "componentTitle";

	public static final String FIELD_TYPE           = "componentType";

	public static final String FIELD_ACTIVE         = "active";
	
	public static final String FIELD_INFO           = "componentInfo";
	
	public static final String FIELD_VERSION        = "componentVersion";
	
	public static final String FIELD_ANY_DISTRIBUTIONS = "anyDistributions";
	
	public static final String FIELD_CONTACT_EMAILS = "contact_email";
	
	public static final String FIELD_CONTACT_EMAILS_NEW = "contact_email_new";

	public static final String FIELD_STATUS         = "status";
	
	public static final String FIELD_ERROR_MSG      = "error_msg";
	
	public static final String FIELD_CONTACT_EMAILS_NUM = "contact_email_num";
	

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
		clear();
		setInput(FIELD_TITLE,                 request.getParameter(FIELD_TITLE));
		setInput(FIELD_ID,                    request.getParameter(FIELD_ID));
		setInput(OLD_FIELD_ID,                request.getParameter(OLD_FIELD_ID));
		setInput(FIELD_TYPE,                  request.getParameter(FIELD_TYPE));
		setInput(FIELD_ACTIVE,                request.getParameter(FIELD_ACTIVE));
		setInput(FIELD_INFO,                  request.getParameter(FIELD_INFO));
		setInput(FIELD_VERSION,               request.getParameter(FIELD_VERSION));
		setInput(FIELD_CONTACT_EMAILS,        request.getParameterValues(FIELD_CONTACT_EMAILS));
		setInput(FIELD_CONTACT_EMAILS_NEW,    request.getParameterValues(FIELD_CONTACT_EMAILS_NEW));
		setInput(FIELD_ANY_DISTRIBUTIONS,     request.getParameterValues(FIELD_ANY_DISTRIBUTIONS));
	}
	
	public void initialize(IngridComponent component) {
	    clear();
        setInput(FIELD_TITLE,               component.getName());
        setInput(FIELD_ID,                  component.getId());
        setInput(OLD_FIELD_ID,              component.getId());
        setInput(FIELD_TYPE,                component.getType());
        setInput(FIELD_INFO,                component.getInfo());
        setInput(FIELD_VERSION,             component.getVersion());
        setInput(FIELD_CONTACT_EMAILS,      component.getEmails().toArray(new String[0]));
        setInput(FIELD_CONTACT_EMAILS_NUM,  Integer.toString(component.getEmails().size()));
	}

	/**
	 * @see de.ingrid.portal.forms.ActionForm#validate()
	 */
	public boolean validate() {
		boolean allOk = true;
		clearErrors();

		// check input
		try {

			if (!hasInput(FIELD_ID)) {
				setError(FIELD_ID, "component.monitor.form.error.missing.id");
				allOk = false;
			}
			if (!hasInput(FIELD_TITLE)) {
				setError(FIELD_TITLE, "component.monitor.form.error.missing.title");
				allOk = false;
			}

			if (hasInput(FIELD_CONTACT_EMAILS_NEW)) {
                if (!Utils.isValidEmailAddress(getInput(FIELD_CONTACT_EMAILS_NEW))) {
                    setError(FIELD_CONTACT_EMAILS_NEW, "component.monitor.form.error.invalid.email");
                    allOk = false;
                }
			}
			
			if (getInput(FIELD_TYPE).equals("OTHER") && !getInput(FIELD_ANY_DISTRIBUTIONS).equals("false")) {
			    setError(FIELD_TYPE, "component.monitor.form.error.missing.type");
                allOk = false;
			}
            
			/*String[] emails = this.getInputAsArray(FIELD_CONTACT_EMAILS);
			if (emails != null) {
				for (int i = 0; i < emails.length; i++) {
					if (!Utils.isValidEmailAddress(emails[i])) {
						setError(FIELD_CONTACT_EMAILS + (i+1), "component.monitor.form.error.invalid.email");
						allOk = false;
					}
				}
			}*/
		} catch (Throwable t) {
			if (log.isErrorEnabled()) {
				log.error("Error validating input.", t);
			}
			allOk = false;
		}

		return allOk;
	}
	
	public Integer getInputAsInteger(String fieldName, Integer defaultValue) {
		try {
			return Integer.valueOf(getInput(fieldName));
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
	
}
