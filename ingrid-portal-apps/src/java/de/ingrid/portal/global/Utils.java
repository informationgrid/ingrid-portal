/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.global;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.portal.forms.ActionForm;
import de.ingrid.utils.query.FieldQuery;
import de.ingrid.utils.query.IngridQuery;

/**
 * Global STATIC data and utility methods.
 * 
 * @author Martin Maidhof
 */
public class Utils {

    private final static Log log = LogFactory.getLog(Utils.class);

    /**
     * Get ActionForm from Session in given scope. Add new one of given Class if
     * not there yet !
     * 
     * @param request
     * @param afKey
     * @param afClass
     * @param sessionScope
     * @return
     */
    public static ActionForm getActionForm(PortletRequest request, String afKey, Class afClass, int sessionScope) {
        ActionForm af = getActionForm(request, afKey, sessionScope);
        if (af == null) {
            af = addActionForm(request, afKey, afClass, sessionScope);
        }

        return af;
    }

    /**
     * Convenient method for getting (incl. adding) action forms from
     * PortletSession.PORTLET_SCOPE
     * 
     * @param request
     * @param afKey
     * @param afClass
     * @return
     */
    public static ActionForm getActionForm(PortletRequest request, String afKey, Class afClass) {
        return getActionForm(request, afKey, afClass, PortletSession.PORTLET_SCOPE);
    }

    /**
     * Get ActionForm from Session in given scope, returns null if not there yet !
     * 
     * @param request
     * @param afKey
     * @param scope
     * @return
     */
    public static ActionForm getActionForm(PortletRequest request, String afKey, int scope) {
        return (ActionForm) request.getPortletSession().getAttribute(afKey, scope);
    }

    /**
     * Add new ActionForm of given Class to Session. Also calls init() on
     * ActionForm !
     * 
     * @param request
     * @param afKey
     * @param afClass
     * @return
     */
    public static ActionForm addActionForm(PortletRequest request, String afKey, Class afClass, int scope) {
        ActionForm af = null;
        try {
            af = (ActionForm) afClass.newInstance();
            af.init();
            request.getPortletSession().setAttribute(afKey, af, scope);
        } catch (Exception ex) {
            if (log.isErrorEnabled()) {
                log.error("Problems initial setup ActionForm of type " + afClass, ex);
            }
        }

        return af;
    }

    public static HashMap getPageNavigation(int startHit, int hitsPerPage, int numberOfHits, int numSelectorPages) {

        // pageSelector
        int currentSelectorPage = 0;
        int numberOfPages = 0;
        int firstSelectorPage = 0;
        int lastSelectorPage = 0;
        boolean selectorHasPreviousPage = false;
        boolean selectorHasNextPage = false;
        int numberOfFirstHit = 0;
        int numberOfLastHit = 0;

        if (numberOfHits != 0) {
            currentSelectorPage = startHit / hitsPerPage + 1;
            numberOfPages = numberOfHits / hitsPerPage;
            if (Math.ceil(numberOfHits % hitsPerPage) > 0) {
                numberOfPages++;
            }
            firstSelectorPage = 1;
            selectorHasPreviousPage = false;
            if (currentSelectorPage >= numSelectorPages) {
                firstSelectorPage = currentSelectorPage - (int) (numSelectorPages / 2);
                selectorHasPreviousPage = true;
            }
            lastSelectorPage = firstSelectorPage + numSelectorPages - 1;
            selectorHasNextPage = true;
            if (numberOfPages <= lastSelectorPage) {
                lastSelectorPage = numberOfPages;
                selectorHasNextPage = false;
            }
            numberOfFirstHit = (currentSelectorPage - 1) * hitsPerPage + 1;
            numberOfLastHit = numberOfFirstHit + hitsPerPage - 1;

            if (numberOfLastHit > numberOfHits) {
                numberOfLastHit = numberOfHits;
            }
        }

        HashMap pageSelector = new HashMap();
        pageSelector.put("currentSelectorPage", new Integer(currentSelectorPage));
        pageSelector.put("numberOfPages", new Integer(numberOfPages));
        pageSelector.put("numberOfSelectorPages", new Integer(numSelectorPages));
        pageSelector.put("firstSelectorPage", new Integer(firstSelectorPage));
        pageSelector.put("lastSelectorPage", new Integer(lastSelectorPage));
        pageSelector.put("selectorHasPreviousPage", new Boolean(selectorHasPreviousPage));
        pageSelector.put("selectorHasNextPage", new Boolean(selectorHasNextPage));
        pageSelector.put("hitsPerPage", new Integer(hitsPerPage));
        pageSelector.put("numberOfFirstHit", new Integer(numberOfFirstHit));
        pageSelector.put("numberOfLastHit", new Integer(numberOfLastHit));
        pageSelector.put("numberOfHits", new Integer(numberOfHits));

        return pageSelector;
    }

    /**
     * Validate the form of an email address. see
     * http://www.javapractices.com/Topic180.cjp
     * 
     * <P>
     * Return <code>true</code> only if
     * <ul>
     * <li> <code>aEmailAddress</code> can successfully construct an
     * {@link javax.mail.internet.InternetAddress}
     * <li> when parsed with a "@" delimiter, <code>aEmailAddress</code>
     * contains two tokens which satisfy
     * {@link hirondelle.web4j.util.Util#textHasContent}.
     * </ul>
     * 
     * <P>
     * The second condition arises since local email addresses, simply of the
     * form "albert", for example, are valid but almost always undesired.
     */
    public static boolean isValidEmailAddress(String aEmailAddress) {
        // org.apache.portals.gems.util.ValidationHelper doesn't work !
        // NOTICE: needs
        // MAVEN_REPO/org.apache.portals.jetspeed-2/jars/portals-gems-2.0.jar
        // if (!ValidationHelper.isEmailAddress(myEmail, true)) {
        // return false;
        // } else {
        // return true;
        // }

        if (aEmailAddress == null)
            return false;

        boolean result = true;
        try {
            // check for format syntax of RFC822, throws Exception if not valid !
            new InternetAddress(aEmailAddress);
            // check for name and email
            String[] tokens = aEmailAddress.split("@");
            boolean hasNameAndDomain = (tokens.length == 2) && (tokens[0].trim().length() > 0)
                    && tokens[1].trim().length() > 0;

            if (!hasNameAndDomain) {
                result = false;
            }
        } catch (AddressException ex) {
            if (log.isDebugEnabled()) {
                log.debug("checking email '" + aEmailAddress + "' caused AddressException", ex);
            }
            result = false;
        }
        return result;
    }

    public static String getShortURLStr(String urlStr, int maxLength) {

        if (urlStr.length() <= maxLength)
            return urlStr;

        URL url = null;
        try {
            url = new URL(urlStr);
        } catch (MalformedURLException e) {
            return "invalid url syntax";
        }

        String path = url.getPath();
        String host = url.getHost();
        String protocoll = url.getProtocol();
        String query = url.getQuery();
        int port = url.getPort();

        StringBuffer resultB = new StringBuffer();
        resultB.append(protocoll).append("://").append(host);
        if (port > -1) {
            resultB.append(":").append(port).append("/");
        } else {
            resultB.append("/");
        }
        int maxPathLength = maxLength / 2;
        String[] pathElements = path.split("/");
        if (pathElements.length > 3 && path.length() > maxPathLength) {
            StringBuffer resultPath = new StringBuffer();
            for (int i = 1; i < pathElements.length; i++) {
                resultPath.append(pathElements[i]).append("/");
                if (resultPath.length() + pathElements[pathElements.length - 1].length() + 5 > maxPathLength) {
                    resultB.append(resultPath).append(".../").append(pathElements[pathElements.length - 1]);
                    break;
                }
            }
        } else if (path.length() <= maxPathLength) {
            resultB.append(path.substring(1));
        } else if (path.length() > maxPathLength) {
            resultB.append("...").append(path.substring(path.length() - maxPathLength - 1, path.length()));
        }
        if (query != null) {
            if (resultB.length() < maxLength) {
                if (query.length() > maxLength - resultB.length()) {
                    resultB.append("?").append(query.substring(0, maxLength - resultB.length())).append("...");
                } else {
                    resultB.append("?").append(query);
                }
            } else {
                resultB.append("?...");
            }
        }
        return resultB.toString();
    }

    /**
     * Returns position of given value in given array.
     * @param array
     * @param value value to search for, if null is passed -1 is returned
     * @return position of value in array or -1
     */
    public static int getPosInArray(String[] array, String value) {
        if (array == null || value == null) {
            return -1;
        }
        for (int i = 0; i < array.length; i++) {
            if (array[i] != null && array[i].equals(value)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Adapt basic datatypes in query dependent from selected datatype in UserInterface
     * (the ones above the Simple Search Input).
     * @param query
     * @param selectedDS
     */
    public static void processBasicDataTypes(IngridQuery query, String selectedDS) {

        if (selectedDS.equals(Settings.SEARCH_DATASOURCE_ENVINFO)) {
            // remove not valid data sources from query
            removeBasicDataTypes(query);
            // TODO: do not set datatype:default, not processed in backend yet !
            // instead don't allow addresses
            //            query.addField(new FieldQuery(true, false, Settings.QFIELD_DATATYPE, Settings.QVALUE_DATATYPE_ENVINFO));
            // REMOVE ADRESS IPLUG
            query.addField(new FieldQuery(false, true, Settings.QFIELD_DATATYPE, Settings.QVALUE_DATATYPE_ADDRESS));
        } else if (selectedDS.equals(Settings.SEARCH_DATASOURCE_ADDRESS)) {
            // remove all manual input and set search to address !
            query.remove(Settings.QFIELD_DATATYPE);
            // ONLY ADRESS IPLUG
            query.addField(new FieldQuery(true, false, Settings.QFIELD_DATATYPE, Settings.QVALUE_DATATYPE_ADDRESS));
        } else if (selectedDS.equals(Settings.SEARCH_DATASOURCE_RESEARCH)) {
            // remove not valid data sources from query
            removeBasicDataTypes(query);
            query.addField(new FieldQuery(true, false, Settings.QFIELD_DATATYPE, Settings.SEARCH_DATASOURCE_RESEARCH));
        }
    }

    /**
     * Remove the Basic DataTypes from the query (the ones above the Simple Search Input) to obtain a "clean" query
     * @param query
     */
    // TODO: remove this helper method if functionality is in IngridQuery
    public static void removeBasicDataTypes(IngridQuery query) {
        FieldQuery[] dataTypesInQuery = query.getDataTypes();
        if (dataTypesInQuery.length == 0) {
            return;
        }

        ArrayList processedDataTypes = new ArrayList(Arrays.asList(dataTypesInQuery));
        for (Iterator iter = processedDataTypes.iterator(); iter.hasNext();) {
            FieldQuery field = (FieldQuery) iter.next();
            String value = field.getFieldValue();
            if (value == null || value.equals(Settings.QVALUE_DATATYPE_ENVINFO)
                    || value.equals(Settings.QVALUE_DATATYPE_ADDRESS)
                    || value.equals(Settings.QVALUE_DATATYPE_RESEARCH)) {
                iter.remove();
            }
        }
        // remove all old datatypes and set our new ones
        query.remove(Settings.QFIELD_DATATYPE);
        for (int i = 0; i < processedDataTypes.size(); i++) {
            query.addField((FieldQuery) processedDataTypes.get(i));
        }
    }

    /**
     * Remove the passed data type from the query
     * @param query
     */
    // TODO: remove this helper method if functionality is in IngridQuery
    public static boolean removeDataType(IngridQuery query, String datatypeValue) {
        boolean removed = false;
        FieldQuery[] dataTypesInQuery = query.getDataTypes();
        if (dataTypesInQuery.length == 0) {
            return removed;
        }

        ArrayList processedDataTypes = new ArrayList(Arrays.asList(dataTypesInQuery));
        for (Iterator iter = processedDataTypes.iterator(); iter.hasNext();) {
            FieldQuery field = (FieldQuery) iter.next();
            String value = field.getFieldValue();
            if (value != null && value.equals(datatypeValue)) {
                iter.remove();
                removed = true;
            }
        }
        // remove all old datatypes and set our new ones
        query.remove(Settings.QFIELD_DATATYPE);
        for (int i = 0; i < processedDataTypes.size(); i++) {
            query.addField((FieldQuery) processedDataTypes.get(i));
        }
        
        return removed;
    }
}
