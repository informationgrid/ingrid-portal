/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets.searchext;

import java.io.IOException;
import java.security.Principal;
import java.util.Collection;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.MimeResponse;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.portals.messaging.PortletMessaging;
import org.apache.velocity.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.forms.SearchExtEnvPlaceMapForm;
import de.ingrid.portal.global.IngridPersistencePrefs;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.global.UtilsQueryString;
import de.ingrid.portal.interfaces.impl.WMSInterfaceImpl;

/**
 * This portlet handles the fragment of the map input in the extended search.
 * 
 * @author martin@wemove.com
 */
public class SearchExtEnvPlaceMapPortlet extends SearchExtEnvPlace {

    private final static Logger log = LoggerFactory.getLogger(SearchExtEnvPlaceMapPortlet.class);

    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);

        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()), request.getLocale());
        context.put("MESSAGES", messages);

        SearchExtEnvPlaceMapForm f = (SearchExtEnvPlaceMapForm) Utils.getActionForm(request,
                SearchExtEnvPlaceMapForm.SESSION_KEY, SearchExtEnvPlaceMapForm.class);
        String cmd = request.getParameter("cmd");

        if (cmd == null) {
            f.init();
        }
        context.put("actionForm", f);

        // set positions in main and sub tab
        context.put(VAR_MAIN_TAB, PARAMV_TAB_PLACE);
        context.put(VAR_SUB_TAB, PARAMV_TAB_MAP);

        /*
        // enable the save button if the query was set AND a user is logged on
        if (Utils.getLoggedOn(request)) {
            context.put("enableSave", "true");
            context.put("wmsServicesSaved", request.getParameter("wmsServicesSaved"));
        }
        */
        super.doView(request, response);
    }

    /**
     * NOTICE: on actions in same page we redirect to ourself with url param
     * determining the view template. If no view template is passed per URL
     * param, the start template is rendered !
     * 
     * @see javax.portlet.Portlet#processAction(javax.portlet.ActionRequest,
     *      javax.portlet.ActionResponse)
     */
    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {
        String action = request.getParameter(Settings.PARAM_ACTION);
        if (action == null) {
            action = "";
        }
        String submittedAddToQuery = request.getParameter("submitAddToQuery");

        if (submittedAddToQuery != null) {
            actionResponse.setRenderParameter("cmd", "form_sent");
            SearchExtEnvPlaceMapForm f = (SearchExtEnvPlaceMapForm) Utils.getActionForm(request,
                    SearchExtEnvPlaceMapForm.SESSION_KEY, SearchExtEnvPlaceMapForm.class);
            f.clearErrors();

            f.populate(request);
            if (!f.validate()) {
                return;
            }
            String searchTerm = "";
          if(!f.getInput(SearchExtEnvPlaceMapForm.FIELD_AREAID).equals("")){
        	  searchTerm = searchTerm.concat("areaid:").concat(f.getInput(SearchExtEnvPlaceMapForm.FIELD_AREAID));
        	  
          }else{
        	  
	          String coordinates = "x1:".concat(f.getInput(SearchExtEnvPlaceMapForm.FIELD_X1));
	          coordinates = coordinates.concat(" y1:").concat(f.getInput(SearchExtEnvPlaceMapForm.FIELD_Y1));
	          coordinates = coordinates.concat(" x2:").concat(f.getInput(SearchExtEnvPlaceMapForm.FIELD_X2));
	          coordinates = coordinates.concat(" y2:").concat(f.getInput(SearchExtEnvPlaceMapForm.FIELD_Y2));            
	          if (f.hasInput(SearchExtEnvPlaceMapForm.FIELD_CHK1)) {
	          searchTerm = searchTerm.concat("(").concat(coordinates).concat(" coord:inside)");
	          }
		      if (f.hasInput(SearchExtEnvPlaceMapForm.FIELD_CHK2)) {
		          if (searchTerm.length() > 0) {
		              searchTerm = searchTerm.concat(" OR ");
		          }
		          searchTerm = searchTerm.concat("(").concat(coordinates).concat(" coord:intersect)");
		      }
		      if (f.hasInput(SearchExtEnvPlaceMapForm.FIELD_CHK3)) {
		          if (searchTerm.length() > 0) {
		              searchTerm = searchTerm.concat(" OR ");
		          }
		          searchTerm = searchTerm.concat("(").concat(coordinates).concat(" coord:include)");
		      }
		      if (searchTerm.length() == 0) {
		          searchTerm = searchTerm.concat(coordinates);
		      }	
          }
          searchTerm = "(".concat(searchTerm).concat(")");
      
    String queryStr = (String) PortletMessaging.receive(request, Settings.MSG_TOPIC_SEARCH,
    		Settings.PARAM_QUERY_STRING);
    PortletMessaging.publish(request, Settings.MSG_TOPIC_SEARCH, Settings.PARAM_QUERY_STRING,
    		UtilsQueryString.addTerm(queryStr, searchTerm, UtilsQueryString.OP_AND));
      
      
            // Zur Suchanfrage hinzufuegen
//else if (wmsDescriptor.getType() == WMSSearchDescriptor.WMS_SEARCH_COMMUNITY_CODE) {
//                    searchTerm = searchTerm.concat("areaid:").concat(wmsDescriptor.getCommunityCode());
//                }
//                searchTerm = UtilsQueryString.stripQueryWhitespace(searchTerm);
//
//                String queryStr = (String) PortletMessaging.receive(request, Settings.MSG_TOPIC_SEARCH,
//                        Settings.PARAM_QUERY_STRING);
//                PortletMessaging.publish(request, Settings.MSG_TOPIC_SEARCH, Settings.PARAM_QUERY_STRING,
//                        UtilsQueryString.addTerm(queryStr, searchTerm, UtilsQueryString.OP_AND));
//            }
        } else if (action.equalsIgnoreCase("doSaveWMSServices") && Utils.getLoggedOn(request)) {
            // get the WMS Services
            Collection c = WMSInterfaceImpl.getInstance().getWMSServices(request.getPortletSession().getId());
            Principal principal = request.getUserPrincipal();
            IngridPersistencePrefs.setPref(principal.getName(), IngridPersistencePrefs.WMS_SERVICES, c);
            actionResponse.setRenderParameter("wmsServicesSaved", "1");
        } else if (action.equalsIgnoreCase(Settings.PARAMV_ACTION_CHANGE_TAB)) {
            String newTab = request.getParameter(Settings.PARAM_TAB);
            processTab(actionResponse, newTab);
        }
    }

    /**
     * @see javax.portlet.GenericPortlet#doHeaders(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
     */
    protected void doHeaders(RenderRequest  request, RenderResponse  response) {
        String languageString = request.getLocale().getLanguage();

		// Styles
		setLinkHeader(response, "theme-access", "/ingrid-webmap-client/lib/extjs/resources/css/ext-all-access.css");
		setLinkHeader(response, "theme-neptune", "/ingrid-webmap-client/lib/extjs/resources/css/ext-all-neptune.css");
		setLinkHeader(response, "theme-gray", "/ingrid-webmap-client/lib/extjs/resources/css/ext-all-gray.css");
		setLinkHeader(response, "theme-all", "/ingrid-webmap-client/lib/extjs/resources/css/ext-all.css");

		setLinkHeader(response, "", "/ingrid-webmap-client/frontend/css/style.css");
		setLinkHeader(response, "", "/ingrid-webmap-client/lib/openlayers.addins/loadingpanel.css");
		setLinkHeader(response, "", "/ingrid-webmap-client/lib/geoext.ux/FeatureEditing/resources/css/feature-editing.css");
		setLinkHeader(response, "", "/ingrid-webmap-client/lib/flashmessage1.1.1/Ext.ux.MessageBox.flash.css");

        // JScript
        setScriptHeader(response, "", "var languageCode = '"+languageString+"';");
        setScriptHeader(response, "", "var viewConfiguration = 'search';");
        setScriptHeader(response, "/ingrid-webmap-client/auth", "");
        setScriptHeader(response, "/ingrid-webmap-client/lib/geoext.ux/Locale.js", "");


		if (PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_WEBMAPCLIENT_DEBUG, false)) {
	        // Extjs
	        setScriptHeader(response, "/ingrid-webmap-client/lib/extjs/ext-all-debug.js", "");

	        // OpenLayers
	        setScriptHeader(response, "/ingrid-webmap-client/lib/openlayers/lib/OpenLayers.js", "");

	        // GeoExt
	        setScriptHeader(response, "/ingrid-webmap-client/shared/js/loader.js", "");
	        setScriptHeader(response, "/ingrid-webmap-client/lib/openlayers/lib/OpenLayers/Lang/"+languageString+".js", "");
	        setScriptHeader(response, "", "OpenLayers.Lang.setCode('"+languageString+"');");
	        setScriptHeader(response, "/ingrid-webmap-client/lib/geoext.ux/FeatureEditing/resources/lang/"+languageString+".js", "");

	        setScriptHeader(response, "/ingrid-webmap-client/lib/extjs.ux/plugins/CustomTreeFeature.js", "");
	        setScriptHeader(response, "/ingrid-webmap-client/lib/proj4js/lib/proj4js-compressed.js", "");
	        // openlayers extensions
	        setScriptHeader(response, "/ingrid-webmap-client/lib/openlayers.addins/LoadingPanel.js", "");
	        // extjs extensions
	        setScriptHeader(response, "/ingrid-webmap-client/lib/extjs.ux/Extjs.ux.Notification.js", "");
	        // geoext extensions
	        setScriptHeader(response, "/ingrid-webmap-client/lib/geoext.ux/SimplePrint.js", "");
	        setScriptHeader(response, "/ingrid-webmap-client/lib/geoext.ux/BWaStrLocatorComboBox.js", "");
	        setScriptHeader(response, "/ingrid-webmap-client/lib/geoext.ux/PortalSearchComboBox.js", "");
	        setScriptHeader(response, "/ingrid-webmap-client/lib/geoext.ux/AllSearchComboBox.js", "");
	        // custom code
	        setScriptHeader(response, "/ingrid-webmap-client/lib/utils/FileSaver/FileSaver.js", "");
	        
	        setScriptHeader(response, "/ingrid-webmap-client/lib/geoext.ux/LayerManager/ux/data/FormatStore.js", "");
	        setScriptHeader(response, "/ingrid-webmap-client/lib/geoext.ux/LayerManager/ux/data/Export.js", "");
	        setScriptHeader(response, "/ingrid-webmap-client/lib/geoext.ux/LayerManager/ux/data/Import.js", "");
	        setScriptHeader(response, "/ingrid-webmap-client/lib/geoext.ux/LayerManager/ux/widgets/LayerManagerExportPanel.js", "");
	        setScriptHeader(response, "/ingrid-webmap-client/lib/geoext.ux/LayerManager/ux/widgets/LayerManagerImportPanel.js", "");
	        setScriptHeader(response, "/ingrid-webmap-client/lib/geoext.ux/LayerManager/ux/widgets/LayerManagerWindow.js", "");
	        setScriptHeader(response, "/ingrid-webmap-client/lib/geoext.ux/LayerManager/ux/widgets/LayerManagerExportWindow.js", "");
	        setScriptHeader(response, "/ingrid-webmap-client/lib/geoext.ux/Styler/ux/LayerStyleManager.js", "");
	        setScriptHeader(response, "/ingrid-webmap-client/lib/geoext.ux/Styler/ux/widgets/StyleSelectorComboBox.js", "");
	        setScriptHeader(response, "/ingrid-webmap-client/lib/geoext.ux/Styler/ux/widgets/StyleSelectorPalette.js", "");

	        setScriptHeader(response, "/ingrid-webmap-client/lib/geoext.ux/FeatureEditing/ux/widgets/FeatureEditingControler.js", "");
	        setScriptHeader(response, "/ingrid-webmap-client/lib/geoext.ux/FeatureEditing/ux/widgets/form/FeatureEditingPanel.js", "");
	        setScriptHeader(response, "/ingrid-webmap-client/lib/geoext.ux/FeatureEditing/ux/widgets/form/RedLiningPanel.js", "");
	        setScriptHeader(response, "/ingrid-webmap-client/lib/geoext.ux/FeatureEditing/ux/widgets/form/FeaturePanel.js", "");
	        setScriptHeader(response, "/ingrid-webmap-client/lib/geoext.ux/FeatureEditing/ux/widgets/plugins/ExportFeature.js", "");
	        setScriptHeader(response, "/ingrid-webmap-client/lib/geoext.ux/FeatureEditing/ux/widgets/plugins/CloseFeatureDialog.js", "");
	        setScriptHeader(response, "/ingrid-webmap-client/lib/geoext.ux/FeatureEditing/ux/widgets/plugins/ExportFeatures.js", "");
	        setScriptHeader(response, "/ingrid-webmap-client/lib/geoext.ux/FeatureEditing/ux/widgets/plugins/ImportFeatures.js", "");
	        setScriptHeader(response, "/ingrid-webmap-client/lib/geoext.ux/FeatureEditing/ux/data/FeatureEditingDefaultStyleStore.js", "");
	        setScriptHeader(response, "/ingrid-webmap-client/lib/geoext.ux/FeatureEditing/ux/util/Clone.js", "");

	        setScriptHeader(response, "/ingrid-webmap-client/shared/js/config.js", "");
	        setScriptHeader(response, "/ingrid-webmap-client/shared/js/Message.js", "");
	        setScriptHeader(response, "/ingrid-webmap-client/shared/js/Configuration.js", "");
	        setScriptHeader(response, "/ingrid-webmap-client/shared/js/data/StoreHelper.js", "");
	        setScriptHeader(response, "/ingrid-webmap-client/shared/js/model/WmsProxy.js", "");
	        setScriptHeader(response, "/ingrid-webmap-client/frontend/js/data/Session.js", "");
	        setScriptHeader(response, "/ingrid-webmap-client/frontend/js/data/SessionState.js", "");
	        setScriptHeader(response, "/ingrid-webmap-client/frontend/js/data/Service.js", "");
	        setScriptHeader(response, "/ingrid-webmap-client/frontend/js/data/MapUtils.js", "");
	        setScriptHeader(response, "/ingrid-webmap-client/frontend/js/data/BWaStrUtils.js", "");
	        setScriptHeader(response, "/ingrid-webmap-client/frontend/js/data/ServiceContainer.js", "");
	        setScriptHeader(response, "/ingrid-webmap-client/frontend/js/data/WMSCapabilitiesReader.js", "");
	        setScriptHeader(response, "/ingrid-webmap-client/frontend/js/controls/ActiveServicesPanel.js", "");
	        setScriptHeader(response, "/ingrid-webmap-client/frontend/js/controls/ServiceCategoryPanel.js", "");
	        setScriptHeader(response, "/ingrid-webmap-client/frontend/js/controls/ServiceTreeLayerNodeView.js", "");
	        setScriptHeader(response, "/ingrid-webmap-client/frontend/js/controls/ServiceTreeLayerNode.js", "");
	        setScriptHeader(response, "/ingrid-webmap-client/frontend/js/controls/ServiceTreeLoader.js", "");
	        setScriptHeader(response, "/ingrid-webmap-client/frontend/js/controls/SearchCategoryPanel.js", "");
	        setScriptHeader(response, "/ingrid-webmap-client/frontend/js/controls/SettingsDialog.js", "");
	        setScriptHeader(response, "/ingrid-webmap-client/frontend/js/controls/OpacityDialog.js", "");
	        setScriptHeader(response, "/ingrid-webmap-client/frontend/js/controls/NewServicePanel.js", "");
	        setScriptHeader(response, "/ingrid-webmap-client/frontend/js/controls/MetaDataDialog.js", "");
	        setScriptHeader(response, "/ingrid-webmap-client/frontend/js/controls/FeatureInfoControl.js", "");
	        setScriptHeader(response, "/ingrid-webmap-client/frontend/js/controls/FeatureInfoDialog.js", "");
	        setScriptHeader(response, "/ingrid-webmap-client/frontend/js/controls/LoadDialog.js", "");
	        setScriptHeader(response, "/ingrid-webmap-client/frontend/js/controls/SaveDialog.js", "");
	        setScriptHeader(response, "/ingrid-webmap-client/frontend/js/controls/DownloadDialog.js", "");
	        setScriptHeader(response, "/ingrid-webmap-client/frontend/js/controls/PrintDialog.js", "");
	        setScriptHeader(response, "/ingrid-webmap-client/frontend/js/controls/WelcomeDialog.js", "");
	        setScriptHeader(response, "/ingrid-webmap-client/frontend/js/controls/LegendDialog.js", "");
	        setScriptHeader(response, "/ingrid-webmap-client/frontend/js/controls/SearchPanel.js", "");
	        setScriptHeader(response, "/ingrid-webmap-client/frontend/js/controls/PositionDialog.js", "");
	        setScriptHeader(response, "/ingrid-webmap-client/frontend/js/controls/BWaStrDialog.js", "");
	        setScriptHeader(response, "/ingrid-webmap-client/frontend/js/controls/BWaStrPanelResult.js", "");

	        setScriptHeader(response, "/ingrid-webmap-client/frontend/js/Workspace.js", "");
	        setScriptHeader(response, "/ingrid-webmap-client/frontend/js/main.js", "");
	        setScriptHeader(response, "/ingrid-webmap-client/lib/flashmessage1.1.1/Ext.ux.MessageBox.flash.js", "");

	//path: /ingrid-webmap-client/frontend/
		} else {
	        // Extjs
	        setScriptHeader(response, "/ingrid-webmap-client/lib/extjs/ext-all.js", "");

	        // OpenLayers
	        setScriptHeader(response, "/ingrid-webmap-client/lib/openlayers/OpenLayers.js", "");
	        setScriptHeader(response, "/ingrid-webmap-client/lib/openlayers/lib/OpenLayers/Lang/"+languageString+".js", "");

	        // GeoExt
	        setScriptHeader(response, "/ingrid-webmap-client/shared/js/loader-min.js", "");
	        setScriptHeader(response, "", "OpenLayers.Lang.setCode('"+languageString+"');");
	        setScriptHeader(response, "/ingrid-webmap-client/lib/geoext.ux/FeatureEditing/resources/lang/"+languageString+".js", "");
	        // Plugins
	        setScriptHeader(response, "/ingrid-webmap-client/lib/extjs.ux/plugins/CustomTreeFeature.js", "");
	        // FileSaver
	        setScriptHeader(response, "/ingrid-webmap-client/lib/utils/FileSaver/FileSaver.js", "");
	        
	        setScriptHeader(response, "/ingrid-webmap-client/lib/proj4js/lib/proj4js-compressed.js", "");
	        setScriptHeader(response, "/ingrid-webmap-client/frontend/js/frontend-all-min.js", "");
	        setScriptHeader(response, "/ingrid-webmap-client/lib/flashmessage1.1.1/Ext.ux.MessageBox.flash-min.js", "");
		}
	}

    private void setLinkHeader(RenderResponse response, String idAttribute, String hrefAttribute) {
        Element elem = response.createElement("link");
        if (idAttribute != null && idAttribute.length() > 0) {
            elem.setAttribute("id", idAttribute);
        }
        elem.setAttribute("rel", "stylesheet");
        elem.setAttribute("type", "text/css");
        elem.setAttribute("href", hrefAttribute);
        response.addProperty(MimeResponse.MARKUP_HEAD_ELEMENT, elem);
    }

    private void setScriptHeader(RenderResponse response, String srcAttribute, String textContent) {
        Element elem = response.createElement("script");
        elem.setAttribute("type", "text/javascript");
        if (srcAttribute != null && srcAttribute.length() > 0) {
	        elem.setAttribute("src", srcAttribute);        	
        }
        elem.setTextContent(textContent);
        response.addProperty(MimeResponse.MARKUP_HEAD_ELEMENT, elem);
    }
}