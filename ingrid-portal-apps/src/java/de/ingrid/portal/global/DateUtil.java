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
public class DateUtil {

    private final static Log log = LogFactory.getLog(ChronicleTeaserPortlet.class);
    
    
    /**
     * Parses a string for a date pattern (yyyy-mm-dd or yyyy) to the local date
     * representation of the date.
     * 
     * @param dateStr The date string.
     * @param locale The Locale.
     * @return The localized date string.
     */
    public static String parseDateToLocale(String dateStr, Locale locale) {
        String result = null;
        SimpleDateFormat df;
        SimpleDateFormat portalFormat;
        if (dateStr == null)
            return result;
        if (dateStr.matches("[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]")) {
            try {
                df = new SimpleDateFormat("yyyy-MM-dd");
                portalFormat = new SimpleDateFormat("d MMM yyyy", locale);
                result = portalFormat.format(df.parse((String) dateStr));
            } catch (ParseException e) {
                log.warn("error parsing from date.", e);
            }
        } else if (dateStr.matches("[0-9][0-9][0-9][0-9]")) {
            try {
                df = new SimpleDateFormat("yyyy");
                portalFormat = new SimpleDateFormat("yyyy", locale);
                result = portalFormat.format(df.parse((String) dateStr));
            } catch (ParseException e) {
                log.warn("error parsing from date.", e);
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
        Date from = DateUtil.parseDateString(dateStrFrom);
        Date to = DateUtil.parseDateString(dateStrTo);
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
        if (dateString.matches("[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]")) {
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
        }
        return result;
    }

}
