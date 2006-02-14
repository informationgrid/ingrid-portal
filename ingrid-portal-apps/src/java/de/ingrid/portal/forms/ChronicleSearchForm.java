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

import javax.portlet.PortletRequest;

/**
 * Form Handler for Environment Chronicles page. Stores and validates form
 * input.
 * 
 * @author Martin Maidhof
 */
public class ChronicleSearchForm extends ActionForm {

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

    /** WHEN MULTIPLE VALUES USE "''" TO SEPARATE VALUES !!!!!!!!! */
    public static final String INITIAL_EVENT = "all''act''his''law''fou''cat''con''lit''nat''dir''shi''acc";

    public static final String INITIAL_TIME_SELECT = "period";

    DateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");

    /**
     * @see de.ingrid.portal.forms.ActionForm#init()
     */
    public void init() {
        clearInput();
        setInput(FIELD_EVENT, INITIAL_EVENT);
        setInput(FIELD_TIME_SELECT, INITIAL_TIME_SELECT);
    }

    /**
     * @see de.ingrid.portal.forms.ActionForm#populate(javax.portlet.PortletRequest)
     */
    public void populate(PortletRequest request) {
        clearInput();
        setInput(FIELD_EVENT, request.getParameterValues(FIELD_EVENT));
        setInput(FIELD_TIME_SELECT, request.getParameterValues(FIELD_TIME_SELECT));
        setInput(FIELD_TIME_FROM, request.getParameter(FIELD_TIME_FROM));
        setInput(FIELD_TIME_TO, request.getParameter(FIELD_TIME_TO));
        setInput(FIELD_TIME_AT, request.getParameter(FIELD_TIME_AT));
        setInput(FIELD_SEARCH, request.getParameter(FIELD_SEARCH));
    }

    /**
     * @see de.ingrid.portal.forms.ActionForm#validate()
     */
    public boolean validate() {
        boolean allOk = true;
        clearErrors();

        // check event
        if (!hasInput(FIELD_EVENT)) {
            setError(FIELD_EVENT, "chronicle.error.noEvent");
            allOk = false;
        }

        // check time
        String inputTimeSelect = getInput(FIELD_TIME_SELECT);
        String inputTimeFrom = getInput(FIELD_TIME_FROM);
        String inputTimeTo = getInput(FIELD_TIME_TO);
        String inputTimeAt = getInput(FIELD_TIME_AT);
        if (inputTimeSelect.equals("period")) {
            // first remove "other input" of radio button group
            setInput(FIELD_TIME_AT, "");

            Date fromDate = null;
            Date toDate = null;
            if (inputTimeFrom.length() != 0) {
                fromDate = getDate(inputTimeFrom);
                if (fromDate == null) {
                    setError(FIELD_TIME_FROM, "chronicle.error.invalidTimeFrom");
                    allOk = false;
                }
                if (inputTimeTo.length() == 0) {
                    setError(FIELD_TIME_TO, "chronicle.error.noTimeTo");
                    allOk = false;
                }
            }
            if (inputTimeTo.length() != 0) {
                toDate = getDate(inputTimeTo);
                if (toDate == null) {
                    setError(FIELD_TIME_TO, "chronicle.error.invalidTimeTo");
                    allOk = false;
                }
                if (inputTimeFrom.length() == 0) {
                    setError(FIELD_TIME_FROM, "chronicle.error.noTimeFrom");
                    allOk = false;
                }
            }
            if (fromDate != null && toDate != null) {                
                Calendar fromCal = new GregorianCalendar();
                fromCal.setTime(fromDate);
                Calendar toCal = new GregorianCalendar();
                toCal.setTime(toDate);
                if (fromCal.after(toCal)) {
                    setError(FIELD_TIME_FROM, "chronicle.error.invalidPeriod");
                    // set empty error, just for highlighting field label
                    setError(FIELD_TIME_TO, "");
                    allOk = false;
                }
            }
        } else if (inputTimeSelect.equals("date")) {
            // first remove "other input" of radio button group
            setInput(FIELD_TIME_FROM, "");
            setInput(FIELD_TIME_TO, "");

            if (inputTimeAt.length() != 0) {
                if (getDate(inputTimeAt) == null) {
                    setError(FIELD_TIME_AT, "chronicle.error.invalidTimeAt");
                    allOk = false;
                }
            }
        }

        return allOk;
    }

    private Date getDate(String dateString) {
        try {
            return (Date) dateFormatter.parse(dateString);
        } catch (ParseException e) {
        }
        return null;
    }
}
