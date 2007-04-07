/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.forms;

import javax.portlet.PortletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.portal.global.Utils;
import de.ingrid.portal.scheduler.jobs.IngridMonitorG2KJob;

/**
 * Form Handler for Content Management of Providers.
 * 
 * @author joachim@wemove.com
 */
public class AdminComponentMonitorForm extends ActionForm {

	private final static Log log = LogFactory.getLog(AdminComponentMonitorForm.class);

	private static final long serialVersionUID = 8335389649101265303L;

    /** attribute name of action form in session */
    public static final String SESSION_KEY = "component_monitor_form";

    public static final String FIELD_ID = "id";

	public static final String FIELD_TITLE = "title";

	public static final String FIELD_TYPE = "type";

	public static final String FIELD_QUERY = "query";

	public static final String FIELD_SERVICE_URL = "serviceurl";

	public static final String FIELD_INTERVAL = "interval";

	public static final String FIELD_TIMEOUT = "timeout";
	
	public static final String FIELD_ACTIVE = "active";

	public static final String FIELD_CONTACT_EMAILS = "contact_email";

	public static final String FIELD_CONTACT_THRESHOLDS = "contact_threshold";

	public static final String FIELD_CONTACT_EMAIL_NEW = "contact_email_new";

	public static final String FIELD_CONTACT_THRESHOLD_NEW = "contact_threshold_new";

	public static final String PARAM_NUM_CONTACTS = "numContacts";
	

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
		setInput(FIELD_TITLE, request.getParameter(FIELD_TITLE));
		setInput(FIELD_ID, request.getParameter(FIELD_ID));
		setInput(FIELD_TYPE, request.getParameter(FIELD_TYPE));
		setInput(FIELD_QUERY, request.getParameter(FIELD_QUERY));
		setInput(FIELD_INTERVAL, request.getParameter(FIELD_INTERVAL));
		setInput(FIELD_TIMEOUT, request.getParameter(FIELD_TIMEOUT));
		setInput(FIELD_SERVICE_URL, request.getParameter(FIELD_SERVICE_URL));
		setInput(FIELD_ACTIVE, request.getParameter(FIELD_ACTIVE));
		setInput(FIELD_CONTACT_EMAIL_NEW, request.getParameter(FIELD_CONTACT_EMAIL_NEW));
		setInput(FIELD_CONTACT_THRESHOLD_NEW, request.getParameter(FIELD_CONTACT_THRESHOLD_NEW));
		this.setInput(FIELD_CONTACT_EMAILS, request.getParameterValues(FIELD_CONTACT_EMAILS));
		this.setInput(FIELD_CONTACT_THRESHOLDS, request.getParameterValues(FIELD_CONTACT_THRESHOLDS));
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
			if (!hasInput(FIELD_QUERY)) {
				setError(FIELD_QUERY, "component.monitor.form.error.missing.query");
				allOk = false;
			}
			if (!hasInput(FIELD_SERVICE_URL) && getInput(FIELD_TYPE).equals(IngridMonitorG2KJob.COMPONENT_TYPE)) {
				setError(FIELD_SERVICE_URL, "component.monitor.form.error.invalid.serviceurl");
				allOk = false;
			}
			
			try {
				int interval = Integer.parseInt(getInput(FIELD_INTERVAL));
				if (interval < 30) {
					setError(FIELD_INTERVAL, "component.monitor.form.error.invalid.interval");
					allOk = false;
				}
			} catch (Exception e) {
				setError(FIELD_INTERVAL, "component.monitor.form.error.invalid.interval");
				allOk = false;
			}
			try {
				int interval = Integer.parseInt(getInput(FIELD_TIMEOUT));
				if (interval < 500) {
					setError(FIELD_TIMEOUT, "component.monitor.form.error.invalid.timeout");
					allOk = false;
				}
			} catch (Exception e) {
				setError(FIELD_TIMEOUT, "component.monitor.form.error.invalid.timeout");
				allOk = false;
			}
			if (hasInput(FIELD_CONTACT_EMAIL_NEW) && !Utils.isValidEmailAddress(getInput(FIELD_CONTACT_EMAIL_NEW))) {
				setError(FIELD_CONTACT_EMAIL_NEW, "component.monitor.form.error.invalid.email");
				allOk = false;
			}
			
			if (hasInput(FIELD_CONTACT_THRESHOLD_NEW) || (hasInput(FIELD_CONTACT_THRESHOLD_NEW) && !hasInput(FIELD_CONTACT_THRESHOLD_NEW))) {
				try {
					int interval = Integer.parseInt(getInput(FIELD_CONTACT_THRESHOLD_NEW));
					if (interval < 1) {
						setError(FIELD_CONTACT_THRESHOLD_NEW, "component.monitor.form.error.invalid.threshold");
						allOk = false;
					}
				} catch (Exception e) {
					setError(FIELD_CONTACT_THRESHOLD_NEW, "component.monitor.form.error.invalid.threshold");
					allOk = false;
				}
			}

			String[] emails = this.getInputAsArray(FIELD_CONTACT_EMAILS);
			String[] threshholds = this.getInputAsArray(FIELD_CONTACT_THRESHOLDS);
			if (emails != null) {
				for (int i = 0; i < emails.length; i++) {
					if (!Utils.isValidEmailAddress(emails[i])) {
						setError(FIELD_CONTACT_EMAILS + (i+1), "component.monitor.form.error.invalid.email");
						allOk = false;
					}
					try {
						int interval = Integer.parseInt(threshholds[i]);
						if (interval < 1) {
							setError(FIELD_CONTACT_THRESHOLDS + (i+1), "component.monitor.form.error.invalid.threshold");
							allOk = false;
						}
					} catch (Exception e) {
						setError(FIELD_CONTACT_THRESHOLDS + (i+1), "component.monitor.form.error.invalid.threshold");
						allOk = false;
					}
				}
			}
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
