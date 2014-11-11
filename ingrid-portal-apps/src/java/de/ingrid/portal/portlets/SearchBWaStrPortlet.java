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
package de.ingrid.portal.portlets;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;

import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.search.SearchState;
import de.ingrid.portal.search.UtilsSearch;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.query.TermQuery;

public class SearchBWaStrPortlet extends GenericVelocityPortlet {
	
    /** our BWaStr view template */
    private String defaultViewTemplate;

    public void init(PortletConfig config) throws PortletException {
    	defaultViewTemplate = config.getInitParameter(PARAM_VIEW_PAGE);
        super.init(config);
    }

    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);

        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()));
        context.put("MESSAGES", messages);

        // read preferences
        PortletPreferences prefs = request.getPreferences();

        // IngridQuery from state  (set in SimpleSearch Portlet)
        IngridQuery query = (IngridQuery) SearchState.getSearchStateObject(request, Settings.MSG_QUERY);

        // if no query display "info" ?
        Boolean showInfoOnEmptyQuery = Boolean.parseBoolean(prefs.getValue("showInfoOnEmptyQuery", "false"));
        if (query == null && showInfoOnEmptyQuery) {
            String myView = prefs.getValue("infoTemplate", "/WEB-INF/templates/search_simple_info.vm");
            setDefaultViewPage(myView);
            response.setTitle(messages.getString("searchSimple.info.title"));
            context.put("enableFeatureType", PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_ENABLE_FEATURE_TYPE, false));

            super.doView(request, response);
            return;
        }

        setDefaultViewPage(defaultViewTemplate);
        response.setTitle(messages.getString("searchBwastr.title"));

        // set up query for BWaStr
        // Only take FIRST term ! BWaStr locator can't be queried with multiple terms !
        String bwastrQueryString = "";
        if (query != null) {
            TermQuery[] terms = UtilsSearch.getAllTerms(query);
            terms = UtilsSearch.removeDoubleTerms(terms);
            for (TermQuery tq : terms) {
                if (tq.getType() == TermQuery.TERM) {
                	bwastrQueryString += tq.getTerm();
                	break;
                }
            }
            bwastrQueryString = bwastrQueryString.trim();
            if (bwastrQueryString.length() == 0) {
            	// No terms found, request BWaStr with full ingrid query string, maybe some results.
                bwastrQueryString = SearchState.getSearchStateObjectAsString(request, Settings.PARAM_QUERY_STRING).trim();
            }
        }

        context.put("bwastrLocatorURL", prefs.getValue("bwastrLocatorURL", "No bwastrLocatorURL specified in SearchBWaStrPortlet"));
        context.put("bwastrGeokodierungURL", prefs.getValue("bwastrGeokodierungURL", "No bwastrGeokodierungURL specified in SearchBWaStrPortlet"));
        context.put("bwastrQuery", bwastrQueryString);

        context.put("getMapURL", prefs.getValue("getMapURL", "No getMapURL specified in SearchBWaStrPortlet"));
        context.put("getMapWIDTH", prefs.getValue("getMapWIDTH", "200"));
        context.put("getMapBBOXDelta", prefs.getValue("getMapBBOXDelta", "0.005"));

        super.doView(request, response);
    }

    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {
    }

}