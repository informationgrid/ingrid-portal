/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.search;

import java.util.HashMap;

import javax.portlet.PortletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.portals.messaging.PortletMessaging;

import de.ingrid.portal.forms.SearchSimpleForm;
import de.ingrid.portal.global.Settings;

/**
 * Global STATIC data and utility methods for SEARCH !
 * 
 * @author Martin Maidhof
 */
public class UtilsSearch {

    private final static Log log = LogFactory.getLog(UtilsSearch.class);

    /**
     * name of request parameters for search !
     */
    private final static String PARAM_QUERY = SearchSimpleForm.FIELD_QUERY;

    private final static String PARAM_DATASOURCE = "ds";

    private final static String PARAM_RESULT_PAGE = "rp";

    /**
     * Returns the search Parameter String for the URL which will be concatenated to the URL path.
     * @param request
     * @return
     */
    public static String getURLParams(PortletRequest request) {
        String EQUALS = "=";
        String SEPARATOR = "&";
        StringBuffer params = new StringBuffer("?");
        String param = null;

        // Query !
        param = request.getParameter(PARAM_QUERY);
        if (param == null) {
            param = (String) PortletMessaging.receive(request, Settings.MSG_TOPIC_SEARCH, Settings.MSG_QUERY_STRING);
        }
        if (param != null) {
            params.append(PARAM_QUERY);
            params.append(EQUALS);
            params.append(param);
        }

        // datasource
        param = (String) PortletMessaging.receive(request, Settings.MSG_TOPIC_SEARCH, Settings.MSG_DATASOURCE);
        if (param != null) {
            params.append(SEPARATOR);
            params.append(PARAM_DATASOURCE);
            params.append(EQUALS);
            params.append(param);
        }

        // result page position
        param = (String) PortletMessaging.receive(request, Settings.MSG_TOPIC_SEARCH, Settings.MSG_RESULT_PAGE);
        if (param != null) {
            params.append(SEPARATOR);
            params.append(PARAM_RESULT_PAGE);
            params.append(EQUALS);
            params.append(param);
        }

        if (log.isInfoEnabled()) {
            log.info("search URL parameters: " + params);
        }
        return params.toString();
    }

    /**
     * Generate PageNavigation data for rendering
     * @param startHit
     * @param hitsPerPage
     * @param numberOfHits
     * @param numSelectorPages
     * @return
     */
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

}
