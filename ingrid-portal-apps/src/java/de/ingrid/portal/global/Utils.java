/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.global;

import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.administration.AdministrationEmailException;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import de.ingrid.portal.config.PortalConfig;
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
     * for all parameters in request !
     * @param request
     * @return
     */
    public static String getURLParams(PortletRequest request) {
        StringBuffer urlParams = new StringBuffer("");

        Enumeration paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = (String) paramNames.nextElement();
            String[] paramValues = request.getParameterValues(paramName);
            String newParam = toURLParam(paramValues, paramName);
            if (urlParams.length() != 0 && newParam.length() > 0) {
                urlParams.append("&");
            }
            urlParams.append(newParam);
        }

        return urlParams.toString();
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
            urlParam.append(toURLParam(paramName, values[i]));
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
    public static String toURLParam(String paramName, String value) {
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
     * Append a new urlParameter to the given url Parameters.
     * @param currentURLParams
     * @param newURLParam
     */
    public static void appendURLParameter(StringBuffer currentURLParams, String newURLParam) {
        if (newURLParam != null && newURLParam.length() > 0) {
            if (!currentURLParams.toString().equals("?")) {
                currentURLParams.append("&");
            }
            currentURLParams.append(newURLParam);
        }
    }

    /**
     * Returns, if the session has been marked as JavaScript- enabled.
     * 
     * @param request The RenderRequest to check.
     * @return True for JavaScript enabled, false for not.
     */
    public static boolean isJavaScriptEnabled(RenderRequest request) {
        String hasJavaScriptStr = (String) request.getPortletSession().getAttribute(Settings.MSG_HAS_JAVASCRIPT);
        if (hasJavaScriptStr != null && hasJavaScriptStr.equals(Settings.MSGV_TRUE)) {
            return true;
        } else {
            return false;
        }
    }

    public static String getMD5Hash(String val) {
        MessageDigest mdAlgorithm = null;
        try {
            mdAlgorithm = MessageDigest.getInstance("MD5");
            mdAlgorithm.update(val.getBytes());

            String plainText = "";

            byte[] digest = mdAlgorithm.digest();
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < digest.length; i++) {
                plainText = Integer.toHexString(0xFF & digest[i]);

                if (plainText.length() < 2) {
                    plainText = "0" + plainText;
                }

                hexString.append(plainText);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error("unable to create MD5 hash from String '" + val + "'", e);
            return null;
        }

    }

    public static String mergeTemplate(PortletConfig portletConfig, Map attributes, String attributesName,
            String template) throws AdministrationEmailException {

        VelocityContext context = new VelocityContext();
        context.put(attributesName, attributes);
        StringWriter sw = new StringWriter();

        try {
            Velocity.init();
            //            String realTemplatePath = portletConfig.getPortletContext().getRealPath(template);
            Template vTemplate = Velocity.getTemplate(template);
            sw = new StringWriter();
            vTemplate.merge(context, sw);
        } catch (Exception e) {
            log.error("failed to merge velocity template: " + template, e);
        }
        String buffer = sw.getBuffer().toString();
        return buffer;
    }

    public static void sendEmail(String from, String subject, String[] to, String text, HashMap headers) {

        boolean debug = true;

        Properties props = new Properties();
        props.put("mail.smtp.host", PortalConfig.getInstance().getString(PortalConfig.EMAIL_SMTP_SERVER, "localhost"));

        // create some properties and get the default Session
        Session session = Session.getDefaultInstance(props, null);
        session.setDebug(debug);

        // create a message
        Message msg = new MimeMessage(session);

        try {
            // set the from and to address
            InternetAddress addressFrom = new InternetAddress(from);
            msg.setFrom(addressFrom);

            InternetAddress[] addressTo = new InternetAddress[to.length];
            for (int i = 0; i < to.length; i++) {
                addressTo[i] = new InternetAddress(to[i]);
            }
            msg.setRecipients(Message.RecipientType.TO, addressTo);

            // set custom headers
            if (headers != null) {
                Set keySet = headers.keySet();
                Iterator it = keySet.iterator();
                while (it.hasNext()) {
                    String key = it.next().toString();
                    msg.addHeader(key, (String) headers.get(key));
                }
            }

            // Setting the Subject and Content Type
            msg.setSubject(subject);
            msg.setContent(text, "text/plain");
            Transport.send(msg);
        } catch (AddressException e) {
            log.error("invalid email address format", e);
        } catch (MessagingException e) {
            log.error("error sending email.", e);
        }

    }

}
