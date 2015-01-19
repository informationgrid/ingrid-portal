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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.portlet.PortletRequest;

import de.ingrid.portal.global.Settings;

/**
 * Form Handler for Environment Chronicles page. Stores and validates form
 * input.
 * 
 * @author Martin Maidhof
 */
public class ChronicleSearchForm extends ActionForm {

    private static final long serialVersionUID = 7744744902434768965L;

    /** attribute name of action form in session */
    public static final String SESSION_KEY = "chronicles_form";

    /** field name of "event" checkbox group in form */
    public static final String FIELD_EVENT = "event";

    /** field name of "time" radio button group in form */
    public static final String FIELD_TIME_SELECT = "time_select";

    /** field name of "time from" text field in form */
    public static final String FIELD_TIME_FROM = "time_from";

    /** field name of "time to" text field in form */
    public static final String FIELD_TIME_TO = "time_to";

    /** field name of "time at" text field in form */
    public static final String FIELD_TIME_AT = "time_at";

    /** field name of "search term" text field in form */
    public static final String FIELD_SEARCH = "search";

    /** field VALUE if time reference is "von bis" */
    public static final String FIELDV_TIME_SELECT_PERIOD = "period";

    /** field VALUE if time reference is "am" */
    public static final String FIELDV_TIME_SELECT_DATE = "date";

    /** WHEN MULTIPLE VALUES USE "''" TO SEPARATE VALUES !!!!!!!!! */
    protected static String INITIAL_EVENT_TYPES = "";

    protected static final String INITIAL_TIME_SELECT = FIELDV_TIME_SELECT_PERIOD;

    protected static final DateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");

    /**
     * @see de.ingrid.portal.forms.ActionForm#init()
     */
    public void init() {
        clearInput();
        setInput(FIELD_EVENT, INITIAL_EVENT_TYPES);
        setInput(FIELD_TIME_SELECT, INITIAL_TIME_SELECT);
    }

    /**
     * NOTICE: We DON'T CLEAR ANY FIELDS IN THE FORM, just take over the given
     * params into the according field (but that field then ONLY contains the
     * new values). In this way, we can initialize the form with default values
     * and JUST TAKE OVER THE NEW ONES !. Use clearInput() to clear the form
     * before populating !  
     * @see de.ingrid.portal.forms.ActionForm#populate(javax.portlet.PortletRequest)
     */
    public void populate(PortletRequest request) {
        if (request.getParameterValues(FIELD_EVENT) != null) {
            setInput(FIELD_EVENT, request.getParameterValues(FIELD_EVENT));
        }
        if (request.getParameterValues(FIELD_TIME_SELECT) != null) {
            setInput(FIELD_TIME_SELECT, request.getParameterValues(FIELD_TIME_SELECT));
        }
        if (request.getParameterValues(FIELD_TIME_FROM) != null) {
            setInput(FIELD_TIME_FROM, request.getParameter(FIELD_TIME_FROM));
        }
        if (request.getParameterValues(FIELD_TIME_TO) != null) {
            setInput(FIELD_TIME_TO, request.getParameter(FIELD_TIME_TO));
        }
        if (request.getParameterValues(FIELD_TIME_AT) != null) {
            setInput(FIELD_TIME_AT, request.getParameter(FIELD_TIME_AT));
        }
        if (request.getParameterValues(FIELD_SEARCH) != null) {
            setInput(FIELD_SEARCH, request.getParameter(FIELD_SEARCH));
        }
    }

    /**
     * @see de.ingrid.portal.forms.ActionForm#validate()
     */
    public boolean validate() {
        boolean allOk = true;
        clearErrors();

        // check event
        if (!hasInput(FIELD_EVENT)) {
            setError(FIELD_EVENT, "chronicle.form.error.noEvent");
            allOk = false;
        }

        // check time
        String inputTimeSelect = getInput(FIELD_TIME_SELECT);
        String inputTimeFrom = getInput(FIELD_TIME_FROM);
        String inputTimeTo = getInput(FIELD_TIME_TO);
        String inputTimeAt = getInput(FIELD_TIME_AT);
        if (inputTimeSelect.equals(FIELDV_TIME_SELECT_PERIOD)) {
            // first remove "other input" of radio button group
            setInput(FIELD_TIME_AT, "");

            Date fromDate = null;
            Date toDate = null;
            if (inputTimeFrom.length() != 0) {
                fromDate = getDate(inputTimeFrom);
                if (fromDate == null) {
                    setError(FIELD_TIME_FROM, "chronicle.form.error.invalidTimeFrom");
                    allOk = false;
                }
                if (inputTimeTo.length() == 0) {
                    setError(FIELD_TIME_TO, "chronicle.form.error.noTimeTo");
                    allOk = false;
                }
            }
            if (inputTimeTo.length() != 0) {
                toDate = getDate(inputTimeTo);
                if (toDate == null) {
                    setError(FIELD_TIME_TO, "chronicle.form.error.invalidTimeTo");
                    allOk = false;
                }
                if (inputTimeFrom.length() == 0) {
                    setError(FIELD_TIME_FROM, "chronicle.form.error.noTimeFrom");
                    allOk = false;
                }
            }
            if (fromDate != null && toDate != null) {
                Calendar fromCal = new GregorianCalendar();
                fromCal.setTime(fromDate);
                Calendar toCal = new GregorianCalendar();
                toCal.setTime(toDate);
                if (fromCal.after(toCal)) {
                    setError(FIELD_TIME_FROM, "chronicle.form.error.invalidPeriod");
                    // set empty error, just for highlighting field label
                    setError(FIELD_TIME_TO, "");
                    allOk = false;
                }
            }
        } else if (inputTimeSelect.equals(FIELDV_TIME_SELECT_DATE)) {
            // first remove "other input" of radio button group
            setInput(FIELD_TIME_FROM, "");
            setInput(FIELD_TIME_TO, "");

            if (inputTimeAt.length() != 0) {
                if (getDate(inputTimeAt) == null) {
                    setError(FIELD_TIME_AT, "chronicle.form.error.invalidTimeAt");
                    allOk = false;
                }
            }
        }

        return allOk;
    }

    /**
     * Set the initially selected event types.
     * @param eventTypes
     */
    public static void setInitialSelectedEventTypes(List eventTypes) {
        String allType = Settings.PARAMV_ALL.concat(VALUE_SEPARATOR).concat(VALUE_SEPARATOR);
        INITIAL_EVENT_TYPES = allType.concat(getInitialSelectString(eventTypes));
    }

    /**
     * @return Returns the INITIAL_EVENT_TYPES.
     */
    public static String getINITIAL_EVENT_TYPES() {
        return INITIAL_EVENT_TYPES;
    }

    /**
     * Returns the Date of the given INPUT Value from Form.
     * @param dateString
     * @return null if input not valid
     */
    public static Date getDate(String dateString) {
        try {
            return (Date) dateFormatter.parse(dateString);
        } catch (ParseException e) {
        }
        return null;
    }
}
