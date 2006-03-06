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
        SimpleDateFormat df = new SimpleDateFormat();
        SimpleDateFormat portalFormat = new SimpleDateFormat("yyyy", locale);
        if (dateStr == null)
            return result;
        try {
            if (dateStr.matches("[0-9][0-9][0-9][0-9]-[0-1][0-9]-[0-3][0-9]")) {
                df.applyPattern("yyyy-MM-dd");
                portalFormat.applyPattern("dd.MM.yyyy");
                result = portalFormat.format(df.parse(dateStr));
            } else if (dateStr.matches("[0-9][0-9][0-9][0-9]")) {
                df.applyPattern("yyyy");
                portalFormat.applyPattern("yyyy");
                result = portalFormat.format(df.parse(dateStr));
            } else if (dateStr.matches("[0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]")) {
                df.applyPattern("yyyyMMdd");
                portalFormat.applyPattern("dd.MM.yyyy");
                result = portalFormat.format(df.parse(dateStr));
            } else if (dateStr.matches("[0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9][0-2][0-9][0-5][0-9][0-5][0-9]")) {
                df.applyPattern("yyyyMMddHHmmss");
                portalFormat.applyPattern("dd.MM.yyyy HH:mm:ss");
                result = portalFormat.format(df.parse(dateStr));
            } else if (dateStr.matches("[0-9][0-9][0-9][0-9]0000")) {
                df.applyPattern("yyyy");
                portalFormat.applyPattern("yyyy");
                result = portalFormat.format(df.parse(dateStr.substring(0,4)));
            } else if (dateStr.matches("[0-9][0-9][0-9][0-9][0-1][0-9]00")) {
                df.applyPattern("yyyyMM");
                portalFormat.applyPattern("MM/yyyy");
                result = portalFormat.format(df.parse(dateStr.substring(0,6)));
            } else if (dateStr.matches("[0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]000000")) {
                df.applyPattern("yyyyMMdd");
                portalFormat.applyPattern("dd.MM.yyyy");
                result = portalFormat.format(df.parse(dateStr.substring(0,8)));
            }
        } catch (ParseException e) {
            log.warn("error parsing from date.", e);
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
     * Parses a date string with the format 'yyyy-mm-dd' or 'yyyy'
     * to a Date.
     * 
     * @param dateString The String representing the date.
     * 
     * @return The Date.
     */
    public static Date parseDateString(String dateString) {
        Date result = null;
        SimpleDateFormat df;
        if (dateString.matches("[0-9][0-9][0-9][0-9]-[0-1][0-9]-[0-3][0-9]")) {
            try {
                df = new SimpleDateFormat("yyyy-MM-dd");
                result = df.parse((String) dateString);
            } catch (ParseException e) {
                log.warn("error parsing from date.", e);
            }
        } else if (dateString.matches("[0-9][0-9][0-9][0-9]")) {
            try {
                df = new SimpleDateFormat("yyyy");
                result = df.parse((String) dateString);
            } catch (ParseException e) {
                log.warn("error parsing from date.", e);
            }
        } else if (dateString.matches("[0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9][0-2][0-9][0-5][0-9][0-5][0-9]")) {
            try {
                df = new SimpleDateFormat("yyyyMMddHHmmss");
                result = df.parse((String) dateString);
            } catch (ParseException e) {
                log.warn("error parsing from date.", e);
            }
        }
        return result;
    }

}
