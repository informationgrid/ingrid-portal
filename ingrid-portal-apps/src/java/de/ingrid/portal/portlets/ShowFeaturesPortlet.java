package de.ingrid.portal.portlets;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;

public class ShowFeaturesPortlet extends GenericVelocityPortlet {

	private final static Logger log = LoggerFactory.getLogger(ShowFeaturesPortlet.class);
	
	public final static String DEFAULT_TEMPLATE = "/WEB-INF/templates/features.vm";
	public final static String OVERVIEW_TEMPLATE = "/WEB-INF/templates/features_content.vm";

	public void init(PortletConfig config) throws PortletException {
		super.init(config);
	}

	public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);
        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()));
        context.put("MESSAGES", messages);
        
        // show different snapshots in English and German!
        context.put("lang", "de".equals(request.getLocale().getLanguage().toLowerCase()) ? "" : "_en");

        // read preferences
        PortletPreferences prefs = request.getPreferences();
        String hKey = prefs.getValue("helpKey", null);
        if (hKey != null && hKey.length() > 0) {
            context.put("hKey", hKey);
        }
        
        if(Utils.getLoggedOn(request)){
        	context.put("logged", "true");        	
        }
        
        String action = request.getParameter(Settings.PARAM_ACTION);
		if(action != null && action.length() > 0){
			if (action.startsWith("do")) {
				if(action.equals("doSearch")){
					response.setTitle("<span class='feature_title'>"+messages.getString("feature.searchSimple.title")+"</span>");
				}else if (action.equals("doEnvironment")){
					response.setTitle("<span class='feature_title'>"+messages.getString("feature.envSearch.title")+"</span>");
				}else if (action.equals("doMeasures")){
					response.setTitle("<span class='feature_title'>"+messages.getString("feature.measuresSearch.title")+"</span>");
				}else if (action.equals("doService")){
					response.setTitle("<span class='feature_title'>"+messages.getString("feature.serviceSearch.title")+"</span>");
				}else if (action.equals("doCatalog")){
					response.setTitle("<span class='feature_title'>"+messages.getString("feature.catalogSearch.title")+"</span>");
				}else if (action.equals("doMap")){
					response.setTitle("<span class='feature_title'>"+messages.getString("feature.mapSearch.title")+"</span>");
				}else if (action.equals("doChronicle")){
					response.setTitle("<span class='feature_title'>"+messages.getString("feature.chronicleSearch.title")+"</span>");
				}
				
				setDefaultViewPage(OVERVIEW_TEMPLATE);
	    	    context.put("feature", action);
			}
	    }else{
    		setDefaultViewPage(DEFAULT_TEMPLATE);
    	}
        
        if (log.isDebugEnabled()) {
        }
       
        context.put("enableMaps", PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_ENABLE_MAPS, Boolean.FALSE));
        context.put("enableTopic", PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_ENABLE_TOPIC, Boolean.FALSE));
        context.put("enableService", PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_ENABLE_SERVICE, Boolean.FALSE));
        context.put("enableChronicle", PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_ENABLE_CHRONICLE, Boolean.FALSE));
        context.put("enableMeasure", PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_ENABLE_MEASURE, Boolean.FALSE));
        context.put("enableSearchCatalog", PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_ENABLE_SEARCH_CATALOG, Boolean.FALSE));
        context.put("enableSearchCatalogThesaurus", PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_ENABLE_SEARCH_CATALOG_THESAURUS, Boolean.FALSE));
        
        super.doView(request, response);
    }

	public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException, IOException {
	 String action = request.getParameter(Settings.PARAM_ACTION);
		 if(action != null && action.length() > 0){
			if(action.equals("doSearch")){
				actionResponse.sendRedirect(actionResponse.encodeURL(Settings.PAGE_SEARCH_RESULT));
			}else if (action.equals("doEnvironment")){
				actionResponse.sendRedirect(actionResponse.encodeURL(Settings.PAGE_ENVIRONMENT));
			}else if (action.equals("doMeasures")){
				actionResponse.sendRedirect(actionResponse.encodeURL(Settings.PAGE_MEASURES));
			}else if (action.equals("doService")){
				actionResponse.sendRedirect(actionResponse.encodeURL(Settings.PAGE_SERVICE));
			}else if (action.equals("doCatalog")){
				if(PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_ENABLE_SEARCH_CATALOG, Boolean.FALSE)){
					actionResponse.sendRedirect(actionResponse.encodeURL("/portal/search-catalog/search-catalog-hierarchy.psml"));
				}else if(PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_ENABLE_SEARCH_CATALOG_THESAURUS, Boolean.FALSE)){
					actionResponse.sendRedirect(actionResponse.encodeURL("/portal/search-catalog/search-catalog-thesaurus.psml"));
				}
			}else if (action.equals("doMap")){
				actionResponse.sendRedirect(actionResponse.encodeURL("/portal/main-maps.psml"));
			}else if (action.equals("doChronicle")){
				actionResponse.sendRedirect(actionResponse.encodeURL(Settings.PAGE_CHRONICLE));
			}
	 	}
	}

}
