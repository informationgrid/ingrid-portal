/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.global;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.portal.forms.ActionForm;

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
     * Returns the "GET" Parameter representation for the URL (WITHOUT leading "?" or "&")
     * @param values
     * @param paramName
     * @return
     */
    public static String toURLParam(String[] values, String paramName) {
        StringBuffer urlParam = new StringBuffer("");
        int i = 0;
        for (i = 0; i < values.length; i++) {
            if (urlParam.length() != 0) {
                urlParam.append("&");
            }
            urlParam.append(toURLParam(values[i], paramName));
        }

        return urlParam.toString();
    }

    /**
     * Returns the "GET" Parameter representation for the URL (WITHOUT leading "?" or "&")
     * NOTICE: if string is null or empty, return value is ""
     * @param value
     * @param paramName
     * @return
     */
    public static String toURLParam(String value, String paramName) {
        if (value == null || value.length() == 0) {
            return "";
        }
        
        String urlParam = null;
        try {
            urlParam = paramName + "=" + URLEncoder.encode(value, "UTF-8");
        } catch (Exception ex) {
            if (log.isErrorEnabled()) {
                log.error("Problems generating URL representation of parameter: " + paramName + "=" + value
                        + "We generate NO Parameter !", ex);
            }
            urlParam = "";
        }

        return urlParam;
    }

    /**
     * Append a new urlParameter to the given url Parameters 
     * @param currentURLParams
     * @param newURLParam
     */
    public static void appendURLParameter(StringBuffer currentURLParams, String newURLParam) {
        if (newURLParam != null && newURLParam.length() > 0) {
            if (currentURLParams.length() > 0) {
                currentURLParams.append("&");
            }
            currentURLParams.append(newURLParam);
        }
    }
}
