/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets.searchext;

import java.io.IOException;

import javax.portlet.ActionResponse;
import javax.portlet.PortletException;

import org.apache.jetspeed.headerresource.HeaderResource;
import org.apache.jetspeed.portlet.PortletHeaderRequest;
import org.apache.jetspeed.portlet.PortletHeaderResponse;
import org.apache.jetspeed.portlet.SupportsHeaderPhase;

/**
 * This portlet is the abstract base class of all "wizard" portlets in the EnvironmentInfo/Place
 * tab of the extended search. Encapsulates common stuff.
 *
 * @author martin@wemove.com
 */
abstract class SearchExtEnvPlace extends SearchExtEnv implements SupportsHeaderPhase{

    // TAB values from action request (request parameter)

    protected final static String PARAMV_TAB_GEOTHESAURUS = "5";

    protected final static String PARAMV_TAB_MAP = "6";

    // PAGES FOR TABS

    protected final static String PAGE_GEOTHESAURUS = "/portal/search-extended/search-ext-env-place-geothesaurus.psml";

    protected final static String PAGE_MAP = "/portal/search-extended/search-ext-env-place-map.psml";

    protected void processTab(ActionResponse actionResponse, String tab) throws PortletException, IOException {
        if (tab.equals(PARAMV_TAB_GEOTHESAURUS)) {
            actionResponse.sendRedirect(actionResponse.encodeURL(PAGE_GEOTHESAURUS));
        } else if (tab.equals(PARAMV_TAB_MAP)) {
            actionResponse.sendRedirect(actionResponse.encodeURL(PAGE_MAP));
        } else {
            super.processTab(actionResponse, tab);
        }
    }
	public void doHeader(PortletHeaderRequest request, PortletHeaderResponse response)
			throws PortletException {
		

        HeaderResource headerResource = response.getHeaderResource();
        
        
        headerResource.addHeaderInfo("<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/extjs/adapter/ext/ext-base-debug.js\"></script>" +
        							 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/extjs/ext-all-debug.js\"></script>" +
        							 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/openlayers/lib/OpenLayers.js\"></script>" +
        							 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/geoext/lib/GeoExt.js\"></script>"+
        							 "<link rel=\"stylesheet\" type=\"text/css\" href=\"/ingrid-webmap-client/lib/extjs/resources/css/ext-all.css\" />"+
        							 "<link rel=\"stylesheet\" type=\"text/css\" href=\"/ingrid-webmap-client/frontend/css/style.css\" />"+
        							 "<script type=\"text/javascript\" src=\"http://proj4js.org/lib/proj4js-compressed.js\"></script>"+
        							 "<!-- openlayers extensions -->"+
        							 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/openlayers.addins/LoadingPanel.js\"></script>"+
        							 "<link rel=\"stylesheet\" type=\"text/css\" href=\"/ingrid-webmap-client/lib/openlayers.addins/loadingpanel.css\" />"+
        							 "<!-- geoext extensions -->"+
        							 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/geoext.ux/SimplePrint.js\"></script>"+
        							 "<!-- custom code -->"+
        							 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/shared/js/config.js\"></script>"+
        							 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/shared/js/Message.js\"></script>"+
        							 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/shared/js/Configuration.js\"></script>"+
        							 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/shared/js/data/StoreHelper.js\"></script>"+
        							 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/shared/js/model/WmsProxy.js\"></script>"+
        							 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/data/Session.js\"></script>"+
        							 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/data/SessionState.js\"></script>"+
        							 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/data/Service.js\"></script>"+
        							 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/controls/ActiveServicesPanel.js\"></script>"+
        							 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/controls/ServiceCategoryPanel.js\"></script>"+
        							 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/controls/SettingsDialog.js\"></script>"+
        							 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/controls/OpacityDialog.js\"></script>"+
        							 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/controls/NewServiceDialog.js\"></script>"+
        							 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/controls/MetaDataDialog.js\"></script>"+
        							 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/controls/FeatureInfoControl.js\"></script>"+
        							 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/controls/FeatureInfoDialog.js\"></script>"+
        							 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/controls/LoadDialog.js\"></script>"+
        							 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/controls/SaveDialog.js\"></script>"+
        							 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/controls/PrintDialog.js\"></script>"+
        							 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/PanelWorkspace.js\"></script>"+
        							 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/main.js\"></script>");
		
	
    
	//path: /ingrid-webmap-client/frontend/

    
	}
}