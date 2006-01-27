/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.global;

import java.util.HashMap;

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

    public static HashMap getPageNavigation(int startHit, int hitsPerPage, int numberOfHits, int numSelectorPages) {

        // pageSelector
        int currentSelectorPage;
        int numberOfPages;
        int firstSelectorPage;
        int lastSelectorPage;
        boolean selectorHasPreviousPage;
        boolean selectorHasNextPage;
        int numberOfFirstHit;
        int numberOfLastHit;

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
