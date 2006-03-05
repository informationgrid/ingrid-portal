/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.search;

import java.net.URLEncoder;

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

    private final static String EQUALS = "=";

    private final static String PARAM_SEPARATOR = "&";

    /**
     * Returns the current state of the Service page as URL Parameters, which can be concatenated
     * to the URL path (for bookmarking !).
     * NOTICE: The passed ActionForm has already to REFLECT THE CURRENT STATE of the form input !!!
     * @param request
     * @return
     */
    public static String getURLParamsService(PortletRequest request, ActionForm af) {
        StringBuffer result = new StringBuffer("?");

        // Action parameter, determines what to do (important for bookmarking, to react in view method)
        String urlParam = Utils.toURLParam(request.getParameter(Settings.PARAM_ACTION), Settings.PARAM_ACTION);
        if (urlParam.length() > 0) {
            result.append(urlParam);
        }

        // Generate form input parameters via ActionForm !
        urlParam = af.toURLParams();
        if (urlParam.length() > 0) {
            if (result.length() > 0) {
                result.append(PARAM_SEPARATOR);
            }
            result.append(urlParam);
        }

        // start hit of search results
        urlParam = Utils.toURLParam(request.getParameter(Settings.PARAM_STARTHIT_RANKED), Settings.PARAM_STARTHIT_RANKED);
        if (urlParam.length() > 0) {
            if (result.length() > 0) {
                result.append(PARAM_SEPARATOR);
            }
            result.append(urlParam);
        }

        if (log.isInfoEnabled()) {
            log.info("Service: URL parameters: " + result);
        }

        return result.toString();
    }

    /**
     * Returns the URL Parameters for the current state of the main search for bookmarking.
     * NOTICE: The passed request may be from different portlets containing different data. We first
     * try to fetch every parameter directly from request, if null we fetch them from SearchState
     * (messages in application scope).
     * @param request
     * @return
     */
    public static String getURLParams(PortletRequest request) {
        StringBuffer params = new StringBuffer("?");
        String param = null;

        try {
            // Action, ONLY READ FROM REQUEST, NO PERMANENT STATE !
            String action = request.getParameter(Settings.PARAM_ACTION);
            if (action == null) {
                action = "";
            }
            if (action.length() > 0) {
                params.append(Settings.PARAM_ACTION);
                params.append(EQUALS);
                params.append(action);
            }

            // query string!
            // DON'T READ from permanent state (message) if new query is performed
            // and only add as param if not empty
            param = request.getParameter(Settings.PARAM_QUERY_STRING);
            if (param == null && !action.equals(Settings.PARAMV_ACTION_NEW_SEARCH)) {
                param = (String) PortletMessaging.receive(request, Settings.MSG_TOPIC_SEARCH,
                        Settings.PARAM_QUERY_STRING);
            }
            if (param != null && param.trim().length() != 0) {
                params.append(PARAM_SEPARATOR);
                params.append(Settings.PARAM_QUERY_STRING);
                params.append(EQUALS);
                params.append(URLEncoder.encode(param, "UTF-8"));
            }

            // datasource
            param = request.getParameter(Settings.PARAM_DATASOURCE);
            if (param == null) {
                param = (String) PortletMessaging
                        .receive(request, Settings.MSG_TOPIC_SEARCH, Settings.PARAM_DATASOURCE);
            }
            if (param != null) {
                params.append(PARAM_SEPARATOR);
                params.append(Settings.PARAM_DATASOURCE);
                params.append(EQUALS);
                params.append(URLEncoder.encode(param, "UTF-8"));
            }

            // DO THE FOLLOWING STUFF ONLY IF NO NEW SEARCH WAS SUBMITTED !
            if (!action.equals(Settings.PARAMV_ACTION_NEW_SEARCH)
                    && !action.equals(Settings.PARAMV_ACTION_NEW_DATASOURCE)) {

                // start hit ranked search results
                param = request.getParameter(Settings.PARAM_STARTHIT_RANKED);
                if (param == null) {
                    param = (String) PortletMessaging.receive(request, Settings.MSG_TOPIC_SEARCH,
                            Settings.PARAM_STARTHIT_RANKED);
                }
                if (param != null) {
                    params.append(PARAM_SEPARATOR);
                    params.append(Settings.PARAM_STARTHIT_RANKED);
                    params.append(EQUALS);
                    params.append(URLEncoder.encode(param, "UTF-8"));
                }

                // start hit unranked search results
                param = request.getParameter(Settings.PARAM_STARTHIT_UNRANKED);
                if (param == null) {
                    param = (String) PortletMessaging.receive(request, Settings.MSG_TOPIC_SEARCH,
                            Settings.PARAM_STARTHIT_UNRANKED);
                }
                if (param != null) {
                    params.append(PARAM_SEPARATOR);
                    params.append(Settings.PARAM_STARTHIT_UNRANKED);
                    params.append(EQUALS);
                    params.append(URLEncoder.encode(param, "UTF-8"));
                }
            }
        } catch (Exception ex) {
            if (log.isErrorEnabled()) {
                log.error("Problems generating URL search parameters, WE DON'T PASS SEARCH PARAMETERS IN URL !", ex);
            }
            params = new StringBuffer("");
        }

        if (log.isInfoEnabled()) {
            log.info("Main Search: URL parameters: " + params);
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
                log.error("FAILED to adapt Search State (consuming/setting message), msg=" + msgKey + ", value="
                        + msgValue, ex);
            }
        }

        return msgPublished;
    }

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

}
