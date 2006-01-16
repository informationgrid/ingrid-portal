/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.utils;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.portal.forms.ActionForm;

/**
 * Global STATIC data and methods.
 * 
 * @author Martin Maidhof
 */
public class Utils {

    private final static Log log = LogFactory.getLog(Utils.class);

    /**
     * Create an ActionForm of given Class and put it in the Session if not
     * there yet.
     * 
     * @param request
     * @param afKey
     * @param afClass
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static ActionForm getActionForm(PortletRequest request, String afKey, Class afClass) {
        PortletSession session = request.getPortletSession();
        ActionForm af = (ActionForm) session.getAttribute(afKey);
        if (af == null) {
            try {
                af = (ActionForm) afClass.newInstance();
                af.init();
                session.setAttribute(afKey, af);
            } catch (Exception ex) {
                if (log.isErrorEnabled()) {
                    log.error("Problems initial setup ActionForm of type " + afClass, ex);
                }
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
            // check for format syntax of RFC822
            InternetAddress emailAddr = new InternetAddress(aEmailAddress);
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

}
