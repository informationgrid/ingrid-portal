/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.search;

import javax.portlet.PortletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final static Logger log = LoggerFactory.getLogger(SearchState.class);

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

        result.append(Utils.getURLParams(request));

        // Generate parameters of form input via ActionForm !
        if (af != null) {
            String urlParam = af.toURLParams();
            Utils.appendURLParameter(result, urlParam);
        }

        if (log.isDebugEnabled()) {
            log.debug("Catalogue: URL parameters: " + result);
        }

        return result.substring(0, result.length());
    }

   	/**
     * Returns the URL Parameters of the current state of the main search for bookmarking.
     * Redirects to getURLParamsMainSearch(PortletRequest, String) with the standard Topic.
     * @param request
     * @return
     */
    public static String getURLParamsMainSearch(PortletRequest request) {
    	return getURLParamsMainSearch(request, Settings.MSG_TOPIC_SEARCH);
    }
   	
    
    /**
     * Returns the URL Parameters of the current state of the main search for bookmarking.
     * @param request
     * @param msgTopic	
     * @return
     */
    public static String getURLParamsMainSearch(PortletRequest request, String msgTopic) {
        StringBuffer result = new StringBuffer("?");

        try {
            // Action parameter, determines what to do (important for bookmarking, to react in view method)
            // (read only from request)
            String action = Utils.toURLParam(Settings.PARAM_ACTION, request.getParameter(Settings.PARAM_ACTION));
            Utils.appendURLParameter(result, action);
            
            // add provider from simple search form
            Utils.appendURLParameter(result, Utils.toURLParam(Settings.PARAM_PROVIDER, request.getParameter(Settings.PARAM_PROVIDER)));
            
            // query string (read from state)
            String paramValue = getSearchStateObjectAsString(request, Settings.PARAM_QUERY_STRING, msgTopic);
            String urlParam = Utils.toURLParam(Settings.PARAM_QUERY_STRING, paramValue);
            Utils.appendURLParameter(result, urlParam);

            // datasource (read from state)
            if(Utils.isJavaScriptEnabled(request) == false){
            	paramValue = getSearchStateObjectAsString(request, Settings.PARAM_DATASOURCE, msgTopic);
                urlParam = Utils.toURLParam(Settings.PARAM_DATASOURCE, paramValue);
                Utils.appendURLParameter(result, urlParam);
            }
            
            // ranking (read from state)
            paramValue = getSearchStateObjectAsString(request, Settings.PARAM_RANKING, msgTopic);
            urlParam = Utils.toURLParam(Settings.PARAM_RANKING, paramValue);
            Utils.appendURLParameter(result, urlParam);

            // grouping (read from state)
            paramValue = getSearchStateObjectAsString(request, Settings.PARAM_GROUPING, msgTopic);
            urlParam = Utils.toURLParam(Settings.PARAM_GROUPING, paramValue);
            Utils.appendURLParameter(result, urlParam);

            // filter (read from state)
            paramValue = getSearchStateObjectAsString(request, Settings.PARAM_FILTER, msgTopic);
            urlParam = Utils.toURLParam(Settings.PARAM_FILTER, paramValue);
            Utils.appendURLParameter(result, urlParam);

            // subject for filter (read from state)
            paramValue = getSearchStateObjectAsString(request, Settings.PARAM_SUBJECT, msgTopic);
            urlParam = Utils.toURLParam(Settings.PARAM_SUBJECT, paramValue);
            Utils.appendURLParameter(result, urlParam);

            // start hits (relevant for NO GROUPING)

            // start hit of ranked search results (read from state)
            paramValue = getSearchStateObjectAsString(request, Settings.PARAM_STARTHIT_RANKED, msgTopic);
            urlParam = Utils.toURLParam(Settings.PARAM_STARTHIT_RANKED, paramValue);
            Utils.appendURLParameter(result, urlParam);

            // start hit of unranked search results (read from state)
            paramValue = getSearchStateObjectAsString(request, Settings.PARAM_STARTHIT_UNRANKED, msgTopic);
            urlParam = Utils.toURLParam(Settings.PARAM_STARTHIT_UNRANKED, paramValue);
            Utils.appendURLParameter(result, urlParam);

            // start pages (relevant for GROUPING)

            // current selector page (set only for grouping) (read from state)
            paramValue = getSearchStateObjectAsString(request, Settings.PARAM_CURRENT_SELECTOR_PAGE, msgTopic);
            urlParam = Utils.toURLParam(Settings.PARAM_CURRENT_SELECTOR_PAGE, paramValue);
            Utils.appendURLParameter(result, urlParam);

            // current selector page (for unranked results) (read from state)
            paramValue = getSearchStateObjectAsString(request, Settings.PARAM_CURRENT_SELECTOR_PAGE_UNRANKED, msgTopic);
            urlParam = Utils.toURLParam(Settings.PARAM_CURRENT_SELECTOR_PAGE_UNRANKED, paramValue);
            Utils.appendURLParameter(result, urlParam);

        } catch (Exception ex) {
            if (log.isErrorEnabled()) {
                log.error("Problems generating URL search parameters, WE DON'T PASS SEARCH PARAMETERS IN URL !", ex);
            }
            result = new StringBuffer("");
        }

        if (log.isDebugEnabled()) {
            log.debug("Main Search: URL parameters: " + result);
        }

        return result.substring(0, result.length());
    }

    /**
     * Add the passed Object (Message) to the Search State, ONLY IF NOT NULL. If the object
     * is null, the Search State keeps its former value and false is returned. Otherwise
     * the value is set in the SearchState and true is returned (former value is replaced).
     * Redirects to adaptSearchStateIfNotNull(PortletRequest, String, Object, String) with the standard Topic.
     * @param request
     * @param objectKey
     * @param objectValue
     * @return
     */
    public static boolean adaptSearchStateIfNotNull(PortletRequest request, String objectKey, Object objectValue) {
            return adaptSearchStateIfNotNull(request, objectKey, objectValue, Settings.MSG_TOPIC_SEARCH);
    }

    /**
     * Add the passed Object (Message) to the Search State, ONLY IF NOT NULL. If the object
     * is null, the Search State keeps its former value and false is returned. Otherwise
     * the value is set in the SearchState and true is returned (former value is replaced).
     * @param request
     * @param objectKey
     * @param objectValue
     * @param msgTopic
     * @return
     */
    public static boolean adaptSearchStateIfNotNull(PortletRequest request, String objectKey, Object objectValue, String msgTopic) {
        if (objectValue != null) {
            return adaptSearchState(request, objectKey, objectValue, msgTopic);
        }

        return false;
    }

    
    /**
     * Add the passed Object (Message) to the Search State. If the passed object is null,
     * the object is removed from the state and false is returned. Otherwise the object is
     * added and true is returned.
     * Redirects to adaptSearchState(PortletRequest, String, Object, String) with the standard Topic.
     * @param request
     * @param objectKey (=messageKey)
     * @param objectValue (=messageValue)
     * @return true = object added, false = object removed
     */
    public static boolean adaptSearchState(PortletRequest request, String objectKey, Object objectValue) {
    	return adaptSearchState(request, objectKey, objectValue, Settings.MSG_TOPIC_SEARCH);
    }
    	
    /**
     * Add the passed Object (Message) to the Search State. If the passed object is null,
     * the object is removed from the state and false is returned. Otherwise the object is
     * added and true is returned.
     * @param request
     * @param objectKey (=messageKey)
     * @param objectValue (=messageValue)
     * @param msgTopic
     * @return true = object added, false = object removed
     */
    public static boolean adaptSearchState(PortletRequest request, String objectKey, Object objectValue, String msgTopic) {
        boolean msgPublished = true;
        try {
            if (objectValue == null) {
                PortletMessaging.cancel(request, msgTopic, objectKey);
                msgPublished = false;
            } else {
                // when setting new query string, replace carriage return with blank !
                // in extended search, carriage return is possible !
                if (objectKey.equals(Settings.PARAM_QUERY_STRING)){
                    objectValue = ((String) objectValue).replaceAll("\r\n", " ");
                }
                PortletMessaging.publish(request, msgTopic, objectKey, objectValue);
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
     *  Redirects to resetSearchState(PortletRequest, String) with the standard Topic.
     * @param request
     * @param objectKey
     */
    public static void resetSearchStateObject(PortletRequest request, String objectKey) {
    	resetSearchStateObject(request, objectKey, Settings.MSG_TOPIC_SEARCH);
    }
    
    /**
     * Reset the given object in the SearchState, meaning the object is removed !
     * @param request
     * @param objectKey
     * @param msgTopic
     */
    public static void resetSearchStateObject(PortletRequest request, String objectKey, String msgTopic) {
        adaptSearchState(request, objectKey, null, msgTopic);
    }

    /**
     * Clear the search state meaning remove all search data from the state (query string, search parameters,
     * result cache, messages ...). Redirects to resetSearchState(PortletRequest, String) with the standard Topic.
     * @param request
     */
    public static void resetSearchState(PortletRequest request) {
    	resetSearchState(request, Settings.MSG_TOPIC_SEARCH);
    }
    
    
    /**
     * Clear the search state meaning remove all search data from the state (query string, search parameters,
     * result cache, messages ...)
     * @param request
     * @param msgTopic
     */
    public static void resetSearchState(PortletRequest request, String msgTopic) {
        try {
            // state for parameters in URL !
            // NOTICE: don't set INITIAL VALUES, will be transformed to URL parameters (not necessary for initial values) !
            PortletMessaging.cancel(request, msgTopic, Settings.PARAM_QUERY_STRING);
            PortletMessaging.cancel(request, msgTopic, Settings.PARAM_DATASOURCE);
            PortletMessaging.cancel(request, msgTopic, Settings.PARAM_STARTHIT_RANKED);
            PortletMessaging.cancel(request, msgTopic, Settings.PARAM_STARTHIT_UNRANKED);
            PortletMessaging.cancel(request, msgTopic, Settings.PARAM_CURRENT_SELECTOR_PAGE);
            PortletMessaging.cancel(request, msgTopic, Settings.PARAM_CURRENT_SELECTOR_PAGE_UNRANKED);
            PortletMessaging.cancel(request, msgTopic, Settings.PARAM_RANKING);
            PortletMessaging.cancel(request, msgTopic, Settings.PARAM_GROUPING);
            PortletMessaging.cancel(request, msgTopic, Settings.PARAM_SUBJECT);
            PortletMessaging.cancel(request, msgTopic, Settings.PARAM_FILTER);
            // this one is still missing when generating URL params !
            PortletMessaging.cancel(request, msgTopic, Settings.PARAM_GROUPING_STARTHITS);

            // further state for logic, caching etc.
            PortletMessaging.cancel(request, msgTopic, Settings.MSG_QUERY);
            PortletMessaging.cancel(request, msgTopic, Settings.MSG_SEARCH_FINISHED_RANKED);
            PortletMessaging.cancel(request, msgTopic, Settings.MSG_SEARCH_RESULT_RANKED);
            PortletMessaging.cancel(request, msgTopic, Settings.MSG_SEARCH_FINISHED_UNRANKED);
            PortletMessaging.cancel(request, msgTopic, Settings.MSG_SEARCH_RESULT_UNRANKED);
            PortletMessaging.cancel(request, msgTopic, Settings.MSG_QUERY_EXECUTION_TYPE);
        } catch (Exception ex) {
            if (log.isErrorEnabled()) {
                log.error("FAILED to reset Search State", ex);
            }
        }
    }

    /**
     * Get the Object with the given key from search state. Returns null if object is not set.
     * Redirects to getSearchStateObject(PortletRequest, String, String) with the standard Topic. 
     * @param request
     * @param objectKey
     * @return
     */
    public static Object getSearchStateObject(PortletRequest request, String objectKey) {
    	return getSearchStateObject(request, objectKey, Settings.MSG_TOPIC_SEARCH);
    }

    /**
     * Get the Object with the given key from search state. Returns null if object is not set
     * @param request
     * @param objectKey
     * @param msgTopic
     * @return
     */
    public static Object getSearchStateObject(PortletRequest request, String objectKey, String msgTopic) {
        return PortletMessaging.receive(request, msgTopic, objectKey);
    }

    /**
     * Get the Object with the given Key as String. Returns "" if object is not set.
     * Redirects to getSearchStateObjectAsString(PortletRequest, String, String) with the standard Topic.
     * @param request
     * @param objectKey
     * @return
     */
    public static String getSearchStateObjectAsString(PortletRequest request, String objectKey) {
    	return getSearchStateObjectAsString(request, objectKey, Settings.MSG_TOPIC_SEARCH);
    }

    
    /**
     * Get the Object with the given Key as String. Returns "" if object is not set.
     * @param request
     * @param objectKey
     * @param msgTopic
     * @return
     */
    public static String getSearchStateObjectAsString(PortletRequest request, String objectKey, String msgTopic) {
        String returnString = "";
        Object stateObject = getSearchStateObject(request, objectKey, msgTopic);
        if (stateObject != null) {
            returnString = stateObject.toString();
        }
        return returnString;
    }

}
