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

public class SearchNominatimPortlet extends GenericVelocityPortlet {
	
    /** our nominatim view template */
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
        response.setTitle(messages.getString("searchNominatim.title"));

        // set up query for nominatim
        String nominatimQueryString = "";
        if (query != null) {
            TermQuery[] terms = UtilsSearch.getAllTerms(query);
            terms = UtilsSearch.removeDoubleTerms(terms);
            for (TermQuery tq : terms) {
                if (tq.getType() == TermQuery.TERM) {
                	nominatimQueryString += tq.getTerm() + " ";
                }
            }
            nominatimQueryString = nominatimQueryString.trim();
            if (nominatimQueryString.length() == 0) {
            	// No terms found, request nominatim with full ingrid query string, maybe some results.
                nominatimQueryString = SearchState.getSearchStateObjectAsString(request, Settings.PARAM_QUERY_STRING).trim();
            }
        }

        context.put("nominatimURL", prefs.getValue("nominatimURL", "No nominatimURL specified in SearchNominatimPortlet"));
        context.put("nominatimQuery", nominatimQueryString);

        context.put("getMapURL", prefs.getValue("getMapURL", "No getMapURL specified in SearchNominatimPortlet"));
        context.put("getMapWIDTH", prefs.getValue("getMapWIDTH", "200"));
        context.put("getMapBBOXDelta", prefs.getValue("getMapBBOXDelta", "0.1"));

        super.doView(request, response);
    }

    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {
    }

}