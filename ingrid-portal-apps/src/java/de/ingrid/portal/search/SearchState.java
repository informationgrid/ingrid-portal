/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.search;

import javax.portlet.PortletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.portals.messaging.PortletMessaging;

import de.ingrid.portal.forms.ActionForm;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;

/**
 * Global STATIC data and utility methods for SEARCH STATE !
 * 
 * @author Martin Maidhof
 */
public class SearchState {

    private final static Log log = LogFactory.getLog(SearchState.class);

    /**
     * Returns the current state of a Catalogue page as URL Parameters, which can be concatenated
     * to the URL path (for bookmarking !). The specific state of the page is encapsulated in the
     * passed ActionForm.  
     * NOTICE: The passed ActionForm has already to REFLECT THE CURRENT STATE of the form input !!!
     * If passed ActionForm is null, the parameters are ONLY extracted from request (e.g. for teaser call,
     * when teaser call, the request has to contain a parameter "rubric").
     * @param request
     * @param af
     * @return
     */
    public static String getURLParamsCatalogueSearch(PortletRequest request, ActionForm af) {
        StringBuffer result = new StringBuffer("?");

        // Action parameter, determines what to do (important for bookmarking, to react in view method)
        // (read only from request)
        String urlParam = Utils.toURLParam(Settings.PARAM_ACTION, request.getParameter(Settings.PARAM_ACTION));
        Utils.appendURLParameter(result, urlParam);

        // Generate parameters of form input via ActionForm !
        if (af != null) {
            urlParam = af.toURLParams();
            Utils.appendURLParameter(result, urlParam);
        } else {
            urlParam = Utils.toURLParam(Settings.PARAM_RUBRIC, request.getParameter(Settings.PARAM_RUBRIC));
            Utils.appendURLParameter(result, urlParam);
        }

        // start hit of search results (read only from request !)
        urlParam = Utils.toURLParam(Settings.PARAM_STARTHIT_RANKED, request
                .getParameter(Settings.PARAM_STARTHIT_RANKED));
        Utils.appendURLParameter(result, urlParam);

        if (log.isInfoEnabled()) {
            log.info("Catalogue: URL parameters: " + result);
        }

        return result.substring(0, result.length());
    }

    /**
     * Returns the URL Parameters of the current state of the main search for bookmarking.
     * @param request
     * @return
     */
    public static String getURLParamsMainSearch(PortletRequest request) {
        StringBuffer result = new StringBuffer("?");

        try {
            // Action parameter, determines what to do (important for bookmarking, to react in view method)
            // (read only from request)
            String action = Utils.toURLParam(Settings.PARAM_ACTION, request.getParameter(Settings.PARAM_ACTION));
            Utils.appendURLParameter(result, action);

            // query string (read from state)
            String paramValue = getSearchStateObjectAsString(request, Settings.PARAM_QUERY_STRING);
            String urlParam = Utils.toURLParam(Settings.PARAM_QUERY_STRING, paramValue);
            Utils.appendURLParameter(result, urlParam);

            // datasource (read from state)
            paramValue = getSearchStateObjectAsString(request, Settings.PARAM_DATASOURCE);
            urlParam = Utils.toURLParam(Settings.PARAM_DATASOURCE, paramValue);
            Utils.appendURLParameter(result, urlParam);

            // start hit of ranked search results (read from state)
            paramValue = getSearchStateObjectAsString(request, Settings.PARAM_STARTHIT_RANKED);
            urlParam = Utils.toURLParam(Settings.PARAM_STARTHIT_RANKED, paramValue);
            Utils.appendURLParameter(result, urlParam);

            // start hit of unranked search results (read from state)
            paramValue = getSearchStateObjectAsString(request, Settings.PARAM_STARTHIT_UNRANKED);
            urlParam = Utils.toURLParam(Settings.PARAM_STARTHIT_UNRANKED, paramValue);
            Utils.appendURLParameter(result, urlParam);

        } catch (Exception ex) {
            if (log.isErrorEnabled()) {
                log.error("Problems generating URL search parameters, WE DON'T PASS SEARCH PARAMETERS IN URL !", ex);
            }
            result = new StringBuffer("");
        }

        if (log.isInfoEnabled()) {
            log.info("Main Search: URL parameters: " + result);
        }

        return result.substring(0, result.length());
    }

    /**
     * Add the passed Object (Message) to the Search State, ONLY IF NOT NULL. If the object
     * is null, the Search State keeps its former value and false is returned. Otherwise
     * the value is set in the SearchState and true is returned (former value is replaced).
     * @param request
     * @param objectKey
     * @param objectValue
     * @return
     */
    public static boolean adaptSearchStateIfNotNull(PortletRequest request, String objectKey, Object objectValue) {
        if (objectValue != null) {
            return adaptSearchState(request, objectKey, objectValue);
        }

        return false;
    }

    /**
     * Add the passed Object (Message) to the Search State. If the passed object is null,
     * the object is removed from the state and false is returned. Otherwise the object is
     * added and true is returned.
     * @param request
     * @param objectKey (=messageKey)
     * @param objectValue (=messageValue)
     * @return true = object added, false = object removed
     */
    public static boolean adaptSearchState(PortletRequest request, String objectKey, Object objectValue) {
        boolean msgPublished = true;
        try {
            if (objectValue == null) {
                PortletMessaging.cancel(request, Settings.MSG_TOPIC_SEARCH, objectKey);
                msgPublished = false;
            } else {
                PortletMessaging.publish(request, Settings.MSG_TOPIC_SEARCH, objectKey, objectValue);
            }
        } catch (Exception ex) {
            if (log.isErrorEnabled()) {
                log.error("FAILED to adapt Search State (consuming/setting message), msg=" + objectKey + ", value="
                        + objectValue, ex);
            }
        }

        return msgPublished;
    }

    /**
     * Reset the given object in the SearchState, meaning the object is removed !
     * @param request
     * @param objectKey
     */
    public static void resetSearchStateObject(PortletRequest request, String objectKey) {
        adaptSearchState(request, objectKey, null);
    }

    /**
     * Clear the search state meaning remove all search data from the state (query string, search parameters,
     * result cache, messages ...)
     * @param request
     */
    public static void resetSearchState(PortletRequest request) {
        // state for parameters in URL !
        PortletMessaging.cancel(request, Settings.MSG_TOPIC_SEARCH, Settings.PARAM_QUERY_STRING);
        PortletMessaging.cancel(request, Settings.MSG_TOPIC_SEARCH, Settings.PARAM_DATASOURCE);
        PortletMessaging.cancel(request, Settings.MSG_TOPIC_SEARCH, Settings.PARAM_STARTHIT_RANKED);
        PortletMessaging.cancel(request, Settings.MSG_TOPIC_SEARCH, Settings.PARAM_STARTHIT_UNRANKED);

        // further state for logic, caching etc.
        PortletMessaging.cancel(request, Settings.MSG_TOPIC_SEARCH, Settings.MSG_QUERY);
        PortletMessaging.cancel(request, Settings.MSG_TOPIC_SEARCH, Settings.MSG_SEARCH_RESULT_RANKED);
        PortletMessaging.cancel(request, Settings.MSG_TOPIC_SEARCH, Settings.MSG_SEARCH_RESULT_UNRANKED);
        PortletMessaging.cancel(request, Settings.MSG_TOPIC_SEARCH, Settings.MSG_QUERY_EXECUTION_TYPE);
    }

    /**
     * Get the Object with the given key from search state. Returns null if object is not set
     * @param request
     * @param objectKey
     * @return
     */
    public static Object getSearchStateObject(PortletRequest request, String objectKey) {
        return PortletMessaging.receive(request, Settings.MSG_TOPIC_SEARCH, objectKey);
    }

    /**
     * Get the Object with the given Key as String. Returns "" if object is not set.
     * @param request
     * @param objectKey
     * @return
     */
    public static String getSearchStateObjectAsString(PortletRequest request, String objectKey) {
        String returnString = "";
        Object stateObject = getSearchStateObject(request, objectKey);
        if (stateObject != null) {
            returnString = stateObject.toString();
        }
        return returnString;
    }

}
