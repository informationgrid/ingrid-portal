/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets.searchext;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;

import org.apache.portals.messaging.PortletMessaging;
import org.apache.velocity.context.Context;

import de.ingrid.portal.forms.SearchExtEnvAreaSourcesForm;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.global.UtilsQueryString;

/**
 * This portlet handles the fragment of "Sources" input in the extended search
 *
 * @author martin@wemove.com
 */
public class SearchExtEnvAreaSourcesPortlet extends SearchExtEnvArea {

    private static final String DATATYPE_WWW = "datatype:www";
    private static final String DATATYPE_METADATA = "datatype:metadata";
    private static final String DATATYPE_FIS = "datatype:fis";
    private static final String DATATYPE_DEFAULT = "datatype:default";

    private static final String METACLASS_DATABASE = "metaclass:database";
    private static final String METACLASS_SERVICE = "metaclass:service";
    private static final String METACLASS_DOCUMENT = "metaclass:document";
    private static final String METACLASS_MAP = "metaclass:map";
    private static final String METACLASS_JOB = "metaclass:job";
    private static final String METACLASS_PROJECT = "metaclass:project";
    
    
    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);

        SearchExtEnvAreaSourcesForm f = (SearchExtEnvAreaSourcesForm) Utils.getActionForm(request, SearchExtEnvAreaSourcesForm.SESSION_KEY, SearchExtEnvAreaSourcesForm.class);        

        String cmd = request.getParameter("cmd");
        
        if (cmd == null) {
            f.init();
        }
        context.put("actionForm", f);
        
        // set positions in main and sub tab
        context.put(VAR_MAIN_TAB, PARAMV_TAB_AREA);
        context.put(VAR_SUB_TAB, PARAMV_TAB_SOURCES);

        super.doView(request, response);
    }

    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {
        String action = request.getParameter(Settings.PARAM_ACTION);
        if (action == null) {
            action = "";
        }
        String submittedAddToQuery = request.getParameter("submitAddToQuery");

        if (submittedAddToQuery != null) {
            actionResponse.setRenderParameter("cmd", "form_sent");
            SearchExtEnvAreaSourcesForm f = (SearchExtEnvAreaSourcesForm) Utils.getActionForm(request, SearchExtEnvAreaSourcesForm.SESSION_KEY, SearchExtEnvAreaSourcesForm.class);        
            f.clearErrors();
            
            f.populate(request);
            if (!f.validate()) {
                return;
            }

            // Zur Suchanfrage hinzufuegen
            String queryStr = (String) PortletMessaging.receive(request, Settings.MSG_TOPIC_SEARCH, Settings.PARAM_QUERY_STRING);
            String subTerm = "";
            String[] sources = f.getInputAsArray(SearchExtEnvAreaSourcesForm.FIELD_CHK_SOURCES);
            String[] meta = f.getInputAsArray(SearchExtEnvAreaSourcesForm.FIELD_CHK_META);
            queryStr = UtilsQueryString.replaceTerm(queryStr, DATATYPE_WWW, "");
            queryStr = UtilsQueryString.replaceTerm(queryStr, DATATYPE_METADATA, "");
            queryStr = UtilsQueryString.replaceTerm(queryStr, DATATYPE_FIS, "");
            queryStr = UtilsQueryString.replaceTerm(queryStr, METACLASS_DATABASE, "");
            queryStr = UtilsQueryString.replaceTerm(queryStr, METACLASS_SERVICE, "");
            queryStr = UtilsQueryString.replaceTerm(queryStr, METACLASS_DOCUMENT, "");
            queryStr = UtilsQueryString.replaceTerm(queryStr, METACLASS_MAP, "");
            queryStr = UtilsQueryString.replaceTerm(queryStr, METACLASS_JOB, "");
            queryStr = UtilsQueryString.replaceTerm(queryStr, METACLASS_PROJECT, "");
            queryStr = UtilsQueryString.replaceTerm(queryStr, "-".concat(DATATYPE_WWW), "");
            queryStr = UtilsQueryString.replaceTerm(queryStr, "-".concat(DATATYPE_METADATA), "");
            queryStr = UtilsQueryString.replaceTerm(queryStr, "-".concat(DATATYPE_FIS), "");
            HashMap datatypes = new LinkedHashMap();
            datatypes.put("-".concat(DATATYPE_WWW), "1");
            datatypes.put("-".concat(DATATYPE_FIS), "1");
            datatypes.put("-".concat(DATATYPE_METADATA), "1");
            for (int i=0; i<sources.length; i++) {
                if (sources[i].equals(SearchExtEnvAreaSourcesForm.VALUE_SOURCE_ALL)) {
                    datatypes.put(DATATYPE_DEFAULT, "1");
                } 
                if (sources[i].equals(SearchExtEnvAreaSourcesForm.VALUE_SOURCE_WWW)) {
                    datatypes.put(DATATYPE_WWW, "1");
                    datatypes.remove("-".concat(DATATYPE_WWW));
                }
                if (sources[i].equals(SearchExtEnvAreaSourcesForm.VALUE_SOURCE_FIS)) {
                    datatypes.put(DATATYPE_FIS, "1");
                    datatypes.remove("-".concat(DATATYPE_FIS));
                }
            }
            HashMap metaclasses = new LinkedHashMap();
            for (int i=0; i<meta.length; i++) {
                if (meta[i].equals(SearchExtEnvAreaSourcesForm.VALUE_META_ALL)) {
                    datatypes.put(DATATYPE_METADATA, "1");
                    datatypes.remove("-".concat(DATATYPE_METADATA));
                    // empty meta classes, all metaclasses are selected
                    metaclasses = new LinkedHashMap();
                    break;
                }
                if (meta[i].equals(SearchExtEnvAreaSourcesForm.VALUE_META_0)) {
                    metaclasses.put(METACLASS_JOB, "1");
                }
                if (meta[i].equals(SearchExtEnvAreaSourcesForm.VALUE_META_1)) {
                    metaclasses.put(METACLASS_MAP, "1");
                }
                if (meta[i].equals(SearchExtEnvAreaSourcesForm.VALUE_META_2)) {
                    metaclasses.put(METACLASS_DOCUMENT, "1");
                }
                if (meta[i].equals(SearchExtEnvAreaSourcesForm.VALUE_META_3)) {
                    metaclasses.put(METACLASS_SERVICE, "1");
                }
                if (meta[i].equals(SearchExtEnvAreaSourcesForm.VALUE_META_4)) {
                    metaclasses.put(METACLASS_PROJECT, "1");
                }
                if (meta[i].equals(SearchExtEnvAreaSourcesForm.VALUE_META_5)) {
                    metaclasses.put(METACLASS_DATABASE, "1");
                }
            }
            // remove meta exclusion if we have a meta data class selection
            if (metaclasses.size() > 0) {
                datatypes.remove("-".concat(DATATYPE_METADATA));
                datatypes.put(DATATYPE_METADATA, "1");
            }
            // build the subquery
            if (!datatypes.containsKey(DATATYPE_DEFAULT) && (metaclasses.size() > 0 || datatypes.size() > 0)) {
                Iterator it = datatypes.keySet().iterator();
                while (it.hasNext()) {
                    String datatype = (String)it.next();
                    subTerm = subTerm.concat(datatype);
                    if (it.hasNext()) {
                        subTerm = subTerm.concat(" ");
                    }
                }
                
                it = metaclasses.keySet().iterator();
                if (it.hasNext()) {
                    subTerm = subTerm.concat(" (");
                    while (it.hasNext()) {
                        String metaclass = (String)it.next();
                        subTerm = subTerm.concat(metaclass);
                        if (it.hasNext()) {
                            subTerm = subTerm.concat(" OR ");
                        }
                    }
                    subTerm = subTerm.concat(")");
                }
            }
            queryStr = UtilsQueryString.stripQueryWhitespace(queryStr);
            PortletMessaging.publish(request, Settings.MSG_TOPIC_SEARCH, Settings.PARAM_QUERY_STRING, UtilsQueryString.addTerm(queryStr, subTerm, UtilsQueryString.OP_AND));

        } else if (action.equalsIgnoreCase(Settings.PARAMV_ACTION_CHANGE_TAB)) {
            String newTab = request.getParameter(Settings.PARAM_TAB);
            processTab(actionResponse, newTab);
        }
    }
}