/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.search;

import java.net.URLEncoder;
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

    /** page for displaying results */
    public final static String PAGE_SEARCH_RESULT = "/ingrid-portal/portal/main-search.psml";

    /**
     * names and values of URL request parameters for search !
     */
    public final static String PARAM_ACTION = "action";

    public final static String PARAM_ACTION_NEW_SEARCH = "doSearch";

    public final static String PARAM_ACTION_NEW_DATASOURCE = "doChangeDS";

    public final static String PARAM_QUERY = SearchSimpleForm.FIELD_QUERY;

    public final static String PARAM_DATASOURCE = "ds";

    public final static String PARAM_STARTHIT_RANKED = "rstart";

    public final static String PARAM_STARTHIT_UNRANKED = "nrstart";

    /**
     * Returns the search Parameter String for the URL which will be concatenated to the URL path.
     * NOTICE: The passed request may be from different portlets containing different data. We first
     * try to fetch every parameter directly from request, if null we fetch them from messages (application scope)
     * @param request
     * @return
     */
    public static String getURLParams(PortletRequest request) {
        String EQUALS = "=";
        String SEPARATOR = "&";
        StringBuffer params = new StringBuffer("?");
        String param = null;

        try {
            // Action, ONLY READ FROM REQUEST, NO PERMANENT STATE !
            String action = request.getParameter(PARAM_ACTION);
            if (action == null) {
                action = "";
            }
            if (action.length() > 0) {
                params.append(PARAM_ACTION);
                params.append(EQUALS);
                params.append(action);
            }

            // query string!
            // DON'T READ from permanent state (message) if new query is performed
            // and only add as param if not empty
            param = request.getParameter(PARAM_QUERY);
            if (param == null && !action.equals(PARAM_ACTION_NEW_SEARCH)) {
                param = (String) PortletMessaging
                        .receive(request, Settings.MSG_TOPIC_SEARCH, Settings.MSG_QUERY_STRING);
            }
            if (param != null && param.trim().length() != 0) {
                params.append(SEPARATOR);
                params.append(PARAM_QUERY);
                params.append(EQUALS);
                params.append(URLEncoder.encode(param, "UTF-8"));
            }

            // datasource
            param = request.getParameter(PARAM_DATASOURCE);
            if (param == null) {
                param = (String) PortletMessaging.receive(request, Settings.MSG_TOPIC_SEARCH, Settings.MSG_DATASOURCE);
            }
            if (param != null) {
                params.append(SEPARATOR);
                params.append(PARAM_DATASOURCE);
                params.append(EQUALS);
                params.append(param);
            }

            // DO THE FOLLOWING STUFF ONLY IF NO NEW SEARCH WAS SUBMITTED !
            if (!action.equals(PARAM_ACTION_NEW_SEARCH) &&
                !action.equals(PARAM_ACTION_NEW_DATASOURCE)) {

                // start hit ranked search results
                param = request.getParameter(PARAM_STARTHIT_RANKED);
                if (param == null) {
                    param = (String) PortletMessaging.receive(request, Settings.MSG_TOPIC_SEARCH,
                            Settings.MSG_STARTHIT_RANKED);
                }
                if (param != null) {
                    params.append(SEPARATOR);
                    params.append(PARAM_STARTHIT_RANKED);
                    params.append(EQUALS);
                    params.append(param);
                }

                // start hit unranked search results
                param = request.getParameter(PARAM_STARTHIT_UNRANKED);
                if (param == null) {
                    param = (String) PortletMessaging.receive(request, Settings.MSG_TOPIC_SEARCH,
                            Settings.MSG_STARTHIT_UNRANKED);
                }
                if (param != null) {
                    params.append(SEPARATOR);
                    params.append(PARAM_STARTHIT_UNRANKED);
                    params.append(EQUALS);
                    params.append(param);
                }                
            }
        } catch (Exception ex) {
            if (log.isErrorEnabled()) {
                log.error("Problems generating URL search parameters, WE DON'T PASS SEARCH PARAMETERS IN URL !", ex);
            }
            params = new StringBuffer("");
        }

        if (log.isInfoEnabled()) {
            log.info("URL search parameters: " + params);
        }

        return params.toString();
    }

    public static boolean adaptSearchState(PortletRequest request, String msgKey, String msgValue) {
        boolean msgPublished = true;
        try {
            if (msgValue == null) {
                PortletMessaging.cancel(request, Settings.MSG_TOPIC_SEARCH, msgKey);
                msgPublished = false;
            } else {
                PortletMessaging.publish(request, Settings.MSG_TOPIC_SEARCH, msgKey, msgValue);
            }            
        } catch (Exception ex) {
            if (log.isErrorEnabled()) {
                log.error("FAILED to adapt Search State (consuming/setting message), msg=" + msgKey + ", value=" + msgValue, ex);
            }
        }

        return msgPublished;
    }

    public static void resetSearchState(PortletRequest request) {
        // state for parameters in URL !
        PortletMessaging.cancel(request, Settings.MSG_TOPIC_SEARCH, Settings.MSG_QUERY_STRING);
        PortletMessaging.cancel(request, Settings.MSG_TOPIC_SEARCH, Settings.MSG_DATASOURCE);
        PortletMessaging.cancel(request, Settings.MSG_TOPIC_SEARCH, Settings.MSG_STARTHIT_RANKED);
        PortletMessaging.cancel(request, Settings.MSG_TOPIC_SEARCH, Settings.MSG_STARTHIT_UNRANKED);

        // further state for logic, caching etc.
        PortletMessaging.cancel(request, Settings.MSG_TOPIC_SEARCH, Settings.MSG_QUERY);
        PortletMessaging.cancel(request, Settings.MSG_TOPIC_SEARCH, Settings.MSG_SEARCH_RESULT_RANKED);
        PortletMessaging.cancel(request, Settings.MSG_TOPIC_SEARCH, Settings.MSG_SEARCH_RESULT_UNRANKED);
        PortletMessaging.cancel(request, Settings.MSG_TOPIC_SEARCH, Settings.MSG_QUERY_STATE);
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
