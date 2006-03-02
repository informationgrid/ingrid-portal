/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.search;

import java.net.URLEncoder;

import javax.portlet.PortletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.portals.messaging.PortletMessaging;

import de.ingrid.portal.global.Settings;

/**
 * Global STATIC data and utility methods for SEARCH STATE !
 * 
 * @author Martin Maidhof
 */
public class SearchState {

    private final static Log log = LogFactory.getLog(SearchState.class);

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
            param = request.getParameter(Settings.PARAM_QUERY);
            if (param == null && !action.equals(Settings.PARAMV_ACTION_NEW_SEARCH)) {
                param = (String) PortletMessaging
                        .receive(request, Settings.MSG_TOPIC_SEARCH, Settings.MSG_QUERY_STRING);
            }
            if (param != null && param.trim().length() != 0) {
                params.append(SEPARATOR);
                params.append(Settings.PARAM_QUERY);
                params.append(EQUALS);
                params.append(URLEncoder.encode(param, "UTF-8"));
            }

            // datasource
            param = request.getParameter(Settings.PARAM_DATASOURCE);
            if (param == null) {
                param = (String) PortletMessaging.receive(request, Settings.MSG_TOPIC_SEARCH, Settings.MSG_DATASOURCE);
            }
            if (param != null) {
                params.append(SEPARATOR);
                params.append(Settings.PARAM_DATASOURCE);
                params.append(EQUALS);
                params.append(param);
            }

            // DO THE FOLLOWING STUFF ONLY IF NO NEW SEARCH WAS SUBMITTED !
            if (!action.equals(Settings.PARAMV_ACTION_NEW_SEARCH) &&
                !action.equals(Settings.PARAMV_ACTION_NEW_DATASOURCE)) {

                // start hit ranked search results
                param = request.getParameter(Settings.PARAM_STARTHIT_RANKED);
                if (param == null) {
                    param = (String) PortletMessaging.receive(request, Settings.MSG_TOPIC_SEARCH,
                            Settings.MSG_STARTHIT_RANKED);
                }
                if (param != null) {
                    params.append(SEPARATOR);
                    params.append(Settings.PARAM_STARTHIT_RANKED);
                    params.append(EQUALS);
                    params.append(param);
                }

                // start hit unranked search results
                param = request.getParameter(Settings.PARAM_STARTHIT_UNRANKED);
                if (param == null) {
                    param = (String) PortletMessaging.receive(request, Settings.MSG_TOPIC_SEARCH,
                            Settings.MSG_STARTHIT_UNRANKED);
                }
                if (param != null) {
                    params.append(SEPARATOR);
                    params.append(Settings.PARAM_STARTHIT_UNRANKED);
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

}
