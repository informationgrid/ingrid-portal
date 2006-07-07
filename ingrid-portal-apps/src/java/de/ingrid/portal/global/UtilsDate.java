/*
 * Copyright (c) 1997-2006 by wemove GmbH
 */
package de.ingrid.portal.global;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.portal.portlets.ChronicleTeaserPortlet;

/**
 * This class contains date conversion helper functions.
 *
 * @author joachim@wemove.com
 */
public class UtilsDate {

    private final static Log log = LogFactory.getLog(ChronicleTeaserPortlet.class);

    /**
     * Parses a string for a date pattern to the local date
     * representation of the date.
     * 
     * @param dateStr The date string.
     * @param locale The Locale.
     * @return The localized date string.
     */
    public static String parseDateToLocale(String dateStr, Locale locale) {
        String result = null;
        SimpleDateFormat portalFormat = new SimpleDateFormat("yyyy", locale);
        if (dateStr == null)
            return result;
        if (dateStr.matches("[0-9][0-9][0-9][0-9]-[0-1][0-9]-[0-3][0-9]")) {
            portalFormat.applyPattern("dd.MM.yyyy");
            result = portalFormat.format(parseDateString(dateStr));
        } else if (dateStr.matches("[0-9][0-9][0-9][0-9]-[0-1][0-9]")) {
            portalFormat.applyPattern("MM.yyyy");
            result = portalFormat.format(parseDateString(dateStr));
        } else if (dateStr.matches("[0-1][0-9]\\.[0-9][0-9][0-9][0-9]")) {
            // already correct
            result = dateStr;
        } else if (dateStr.matches("[0-3][0-9]\\.[0-1][0-9]\\.[0-9][0-9][0-9][0-9]")) {
            // already correct
            result = dateStr;
        } else if (dateStr.matches("[0-9][0-9][0-9][0-9]")) {
            portalFormat.applyPattern("yyyy");
            result = portalFormat.format(parseDateString(dateStr));
        } else if (dateStr.matches("[0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]")) {
            portalFormat.applyPattern("dd.MM.yyyy");
            result = portalFormat.format(parseDateString(dateStr));
        } else if (dateStr.matches("[0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9][0-2][0-9][0-5][0-9][0-5][0-9]")) {
            portalFormat.applyPattern("dd.MM.yyyy HH:mm:ss");
            result = portalFormat.format(parseDateString(dateStr));
        } else if (dateStr.matches("[0-9][0-9][0-9][0-9]0000")) {
            portalFormat.applyPattern("yyyy");
            result = portalFormat.format(parseDateString(dateStr));
        } else if (dateStr.matches("[0-9][0-9][0-9][0-9][0-1][0-9]00")) {
            portalFormat.applyPattern("MM/yyyy");
            result = portalFormat.format(parseDateString(dateStr));
        } else if (dateStr.matches("[0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]000000")) {
            portalFormat.applyPattern("dd.MM.yyyy");
            result = portalFormat.format(parseDateString(dateStr));
        }
        return result;
    }

    /**
     * Parses a date string to a Date. Accepting the following patterns:
     * 
     * - yyyy-MM-dd
     * - yyyy
     * - yyyyMMddHHmmss
     * - yyyyMMdd
     * - yyyy0000
     * - yyyyMM00
     * - yyyyMMdd000000
     * 
     * @param dateString The String representing the date.
     * 
     * @return The Date.
     */
    public static Date parseDateString(String dateString) {
        Date result = null;
        if (dateString == null)
            return result;
        SimpleDateFormat df = new SimpleDateFormat();
        try {
            if (dateString.matches("[0-9][0-9][0-9][0-9]-[0-1][0-9]-[0-3][0-9]")) {
                df.applyPattern("yyyy-MM-dd");
                result = df.parse(dateString);
            } else if (dateString.matches("[0-9][0-9][0-9][0-9]-[0-1][0-9]")) {
                df.applyPattern("yyyy-MM");
                result = df.parse(dateString);
            } else if (dateString.matches("[0-9][0-9][0-9][0-9]")) {
                df.applyPattern("yyyy");
                result = df.parse(dateString);
            } else if (dateString.matches("[0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]000000")) {
                df.applyPattern("yyyyMMdd");
                result = df.parse(dateString.substring(0, 8));
            } else if (dateString.matches("[0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9][0-2][0-9][0-5][0-9][0-5][0-9]")) {
                df.applyPattern("yyyyMMddHHmmss");
                result = df.parse(dateString);
            } else if (dateString.matches("[0-9][0-9][0-9][0-9]0000")) {
                df.applyPattern("yyyy");
                result = df.parse(dateString.substring(0, 4));
            } else if (dateString.matches("[0-9][0-9][0-9][0-9][0-1][0-9]00")) {
                df.applyPattern("yyyyMM");
                result = df.parse(dateString.substring(0, 6));
            } else if (dateString.matches("[0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]")) {
                df.applyPattern("yyyyMMdd");
                result = df.parse(dateString);
            }
        } catch (ParseException e) {
            if (log.isWarnEnabled()) {
                log.warn("error parsing from date.", e);
            }
        }
        return result;
    }

    /**
     * Parses a date string with format <i>srcFormat</i> and 
     * returns the string with format <i>destFormat</i>.
     * 
     * @param dateStr The date string.
     * @param srcFormat The source format to parse the date string (i.e. "ddMMyyyy").
     * @param destFormat The format to return the parsed date (i.e. "yyyy-MM-dd")
     * @return The resulting date string.
     */
    public static String convertDateString(String dateStr, String srcFormat, String destFormat) {
        String result = null;
        SimpleDateFormat df = new SimpleDateFormat();
        SimpleDateFormat portalFormat = new SimpleDateFormat("yyyy");
        try {
            df.applyPattern(srcFormat);
            portalFormat.applyPattern(destFormat);
            result = portalFormat.format(df.parse((String) dateStr));
        } catch (ParseException e) {
            if (log.isErrorEnabled()) {
                log.error("error parsing date (" + dateStr + ") with format (" + srcFormat + ") to format (" + destFormat
                        + ").", e);
            }
        }
        return result;
    }

    /** Returns the years between dateStrFrom and dateStringTo. Both can have the formats
     * yyyy-mm-dd or yyyy.
     * 
     * @param dateStrFrom The String representing from.
     * @param dateStrTo The String representing to.
     * @return The years between the dates.
     */
    public static int yearsBetween(String dateStrFrom, String dateStrTo) {
        Date from = UtilsDate.parseDateString(dateStrFrom);
        Date to = UtilsDate.parseDateString(dateStrTo);
        if (from != null && to != null) {
            Calendar cal = new GregorianCalendar();
            cal.setTime(from);
            int fromYear = cal.get(Calendar.YEAR);
            cal.setTime(to);
            int toYear = cal.get(Calendar.YEAR);
            return toYear - fromYear;
        } else {
            return -1;
        }
    }

    /**
     * Get output String for view template. 
     * @param dateStrFrom
     * @param dateStrTo
     * @param locale
     * @return
     */
    public static String getOutputString(String dateStrFrom, String dateStrTo, Locale locale) {
        String from = UtilsDate.parseDateToLocale(dateStrFrom, locale);
        String to = UtilsDate.parseDateToLocale(dateStrTo, locale);

        if (from != null && to != null) {
            if (from.equals(to)) {
                return from;
            }
            return from.concat(" - ").concat(to);
        } else if (from != null) {
            return from;
        } else if (to != null) {
            return to;
        }

        return "";
    }

    /**
     * Returns the Form Input String for maximum date ("to" value to use if only "from" specified)
     * @return
     */
    public static String getInputDateMax() {
        SimpleDateFormat portalFormat = new SimpleDateFormat("dd.MM.yyyy");
        return portalFormat.format(new Date());
    }

    /**
     * Returns the Form Input String for minimum date ("from" value to use if only "to" specified)
     * @return
     */
    public static String getInputDateMin() {
        return "01.01.0000";
    }

    /**
     * Checks the Topic Date and returns the according input date for the from field
     * @param topicDate
     * @param locale
     * @return
     */
    public static String getInputDateFrom(String topicDate, Locale locale) {
        String from = UtilsDate.parseDateToLocale(topicDate, locale);
        if (from == null) {
            return null;
        }

        if (from.length() == 4) {
            // yyyy
            from = "01.01." + from;
        } else if (from.length() == 7) {
            // MM.yyyy
            from = "01." + from;
        }

        return from;
    }

    /**
     * Checks the Topic Date and returns the according input date for the to field
     * @param topicDate
     * @param locale
     * @return
     */
    public static String getInputDateTo(String topicDate, Locale locale) {
        String to = UtilsDate.parseDateToLocale(topicDate, locale);
        if (to == null) {
            return null;
        }

        if (to.length() == 4) {
            // yyyy
            to = "31.12." + to;
        } else if (to.length() == 7) {
            // MM.yyyy
            to = "31." + to;
        }

        return to;
    }
}
