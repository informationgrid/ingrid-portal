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

import javax.portlet.PortletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ingrid.portal.global.Utils;
import de.ingrid.portal.scheduler.jobs.IngridMonitorCSWJob;
import de.ingrid.portal.scheduler.jobs.IngridMonitorIPlugJob;
import de.ingrid.portal.scheduler.jobs.IngridMonitorRSSCheckerJob;
import de.ingrid.portal.scheduler.jobs.IngridMonitorSNSJob;

/**
 * Form Handler for Content Management of Providers.
 * 
 * @author joachim@wemove.com
 */
public class AdminComponentMonitorForm extends ActionForm {

	private final static Logger log = LoggerFactory.getLogger(AdminComponentMonitorForm.class);

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
	
	public static final String FIELD_STATUS = "status";
	
	public static final String FIELD_ERROR_MSG = "error_msg";
	
	public static final String FIELD_LAST_EXECUTION = "last_execution";
	
	public static final String FIELD_NEXT_EXECUTION = "next_execution";
	
	public static final String FIELD_LAST_OK_EXECUTION = "last_ok_execution";
	
	public static final String FIELD_NUM_EXECUTIONS = "num_executions";
	
	public static final String FIELD_AVERAGE_EXECTIME = "average_exec_time";
	
	public static final String FIELD_EXCLUDED_PROVIDER = "excl_provider";
	

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
		//setInput(FIELD_QUERY, request.getParameter(FIELD_QUERY));
		setInput(FIELD_INTERVAL, request.getParameter(FIELD_INTERVAL));
		setInput(FIELD_TIMEOUT, request.getParameter(FIELD_TIMEOUT));
		//setInput(FIELD_SERVICE_URL, request.getParameter(FIELD_SERVICE_URL));
		setInput(FIELD_QUERY, request.getParameter(FIELD_QUERY));
		setInput(FIELD_SERVICE_URL, request.getParameter(FIELD_SERVICE_URL));
		setInput(FIELD_EXCLUDED_PROVIDER, request.getParameter(FIELD_EXCLUDED_PROVIDER));
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
			if (getInput(FIELD_TYPE).equals(IngridMonitorIPlugJob.COMPONENT_TYPE)
					|| getInput(FIELD_TYPE).equals(IngridMonitorCSWJob.COMPONENT_TYPE)
					|| getInput(FIELD_TYPE).equals(IngridMonitorSNSJob.COMPONENT_TYPE)) {
				if (!hasInput(FIELD_QUERY)) {
					setError(FIELD_QUERY, "component.monitor.form.error.missing.query");
					allOk = false;
				} else if (getInput(FIELD_TYPE).equals(IngridMonitorIPlugJob.COMPONENT_TYPE) && !getInput(FIELD_QUERY).contains("cache:off")) {
					setError(FIELD_QUERY, "component.monitor.form.error.cache.on");
					allOk = false;
				}
			}
			if (!hasInput(FIELD_SERVICE_URL)
					&& (getInput(FIELD_TYPE).equals(IngridMonitorCSWJob.COMPONENT_TYPE)
					|| getInput(FIELD_TYPE).equals(IngridMonitorRSSCheckerJob.COMPONENT_TYPE))) {
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
