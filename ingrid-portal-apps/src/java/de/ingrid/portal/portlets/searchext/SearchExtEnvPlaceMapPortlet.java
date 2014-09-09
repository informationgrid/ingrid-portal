/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets.searchext;

import java.io.IOException;
import java.lang.reflect.Field;
import java.security.Principal;
import java.util.Collection;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;

import org.apache.jetspeed.headerresource.HeaderResource;
import org.apache.jetspeed.portlet.PortletHeaderRequest;
import org.apache.jetspeed.portlet.PortletHeaderResponse;
import org.apache.jetspeed.portlet.SupportsHeaderPhase;
import org.apache.jetspeed.request.RequestContext;
import org.apache.portals.messaging.PortletMessaging;
import org.apache.velocity.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class SearchExtEnvPlaceMapPortlet extends SearchExtEnvPlace  implements SupportsHeaderPhase{

    private final static Logger log = LoggerFactory.getLogger(SearchExtEnvPlaceMapPortlet.class);

    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);

        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()));
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
    public void doHeader(PortletHeaderRequest request, PortletHeaderResponse response)
			throws PortletException {
        // this is an UGLY hack but otherwise I do not see how to access the 
        // locale of the request. It is used to set the language code for the
        // map client.
        Field f;
        String languageString = null;
        try {
            f = request.getClass().getDeclaredField("requestContext");
            f.setAccessible(true);
            RequestContext requestContext = (RequestContext) f.get(request);
            languageString = requestContext.getLocale().getLanguage();
        } catch (Exception e) {
            log.warn("Error accessing request context in header phase.", e);
        }
    	HeaderResource headerResource = response.getHeaderResource();
    	if(PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_WEBMAPCLIENT_DEBUG, false)){
	        headerResource.addHeaderInfo(
        		"<!-- start output header phase -->" +
				"<!-- Styles -->" +
        		"<link rel=\"stylesheet\" type=\"text/css\" href=\"/ingrid-webmap-client/lib/extjs/resources/css/ext-all.css\" />"+
				"<link rel=\"stylesheet\" type=\"text/css\" href=\"/ingrid-webmap-client/frontend/css/style.css\" />"+
				"<link rel=\"stylesheet\" type=\"text/css\" href=\"/ingrid-webmap-client/lib/openlayers.addins/loadingpanel.css\" />"+
				"<link rel=\"stylesheet\" type=\"text/css\" href=\"/ingrid-webmap-client/lib/geoext.ux/FeatureEditing/resources/css/feature-editing.css\" />"+  
				"<link rel=\"stylesheet\" type=\"text/css\" href=\"/ingrid-webmap-client/lib/flashmessage1.1.1/Ext.ux.MessageBox.flash.css\" />"+
				
				"<!-- Script -->" +
        		"<script type=\"text/javascript\">var languageCode = '"+languageString+"';</script>" +
                "<script type=\"text/javascript\">var viewConfiguration = 'search';</script>" +
                "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/auth\"></script>" +
                
				"<!-- Extjs -->" +
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/extjs/ext-all-debug.js\"></script>" +
				
				"<!-- OpenLayers -->" +
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/openlayers/lib/OpenLayers.js\"></script>" +
				
				"<!-- GeoExt -->" +
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/shared/js/loader.js\"></script>"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/openlayers/lib/OpenLayers/Lang/"+languageString+".js\"></script>" +
				"<script type=\"text/javascript\">OpenLayers.Lang.setCode('"+languageString+"');</script>"+ 
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/geoext.ux/FeatureEditing/resources/lang/"+languageString+".js\"></script>"+
				
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/extjs.ux/plugins/CustomTreeFeature.js\"></script>"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/proj4js/lib/proj4js-compressed.js\"></script>"+
				"<!-- openlayers extensions -->"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/openlayers.addins/LoadingPanel.js\"></script>"+
				"<!-- extjs extensions -->\r\n" + 
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/extjs.ux/Extjs.ux.Notification.js\"></script>\r\n" + 
				"<!-- geoext extensions -->"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/geoext.ux/SimplePrint.js\"></script>"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/geoext.ux/Locale.js\"></script>"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/geoext.ux/BWaStrLocatorComboBox.js\"></script>"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/geoext.ux/PortalSearchComboBox.js\"></script>"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/geoext.ux/AllSearchComboBox.js\"></script>"+
				
				"<!-- custom code -->"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/utils/FileSaver/FileSaver.js\"></script>"+
				
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/geoext.ux/LayerManager/ux/data/FormatStore.js\"></script>"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/geoext.ux/LayerManager/ux/data/Export.js\"></script>"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/geoext.ux/LayerManager/ux/data/Import.js\"></script>"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/geoext.ux/LayerManager/ux/widgets/LayerManagerExportPanel.js\"></script>"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/geoext.ux/LayerManager/ux/widgets/LayerManagerImportPanel.js\"></script>"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/geoext.ux/LayerManager/ux/widgets/LayerManagerWindow.js\"></script>"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/geoext.ux/LayerManager/ux/widgets/LayerManagerExportWindow.js\"></script>"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/geoext.ux/Styler/ux/LayerStyleManager.js\"></script>"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/geoext.ux/Styler/ux/widgets/StyleSelectorComboBox.js\"></script>"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/geoext.ux/Styler/ux/widgets/StyleSelectorPalette.js\"></script>"+
				
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/geoext.ux/FeatureEditing/ux/widgets/FeatureEditingControler.js\"></script>"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/geoext.ux/FeatureEditing/ux/widgets/form/FeatureEditingPanel.js\"></script>"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/geoext.ux/FeatureEditing/ux/widgets/form/RedLiningPanel.js\"></script>"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/geoext.ux/FeatureEditing/ux/widgets/form/FeaturePanel.js\"></script>"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/geoext.ux/FeatureEditing/ux/widgets/plugins/ExportFeature.js\"></script>"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/geoext.ux/FeatureEditing/ux/widgets/plugins/CloseFeatureDialog.js\"></script>"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/geoext.ux/FeatureEditing/ux/widgets/plugins/ExportFeatures.js\"></script>"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/geoext.ux/FeatureEditing/ux/widgets/plugins/ImportFeatures.js\"></script>"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/geoext.ux/FeatureEditing/ux/data/FeatureEditingDefaultStyleStore.js\"></script>"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/geoext.ux/FeatureEditing/ux/util/Clone.js\"></script>"+
				
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/shared/js/config.js\"></script>"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/shared/js/Message.js\"></script>"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/shared/js/Configuration.js\"></script>"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/shared/js/data/StoreHelper.js\"></script>"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/shared/js/model/WmsProxy.js\"></script>"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/data/Session.js\"></script>"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/data/SessionState.js\"></script>"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/data/Service.js\"></script>"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/data/MapUtils.js\"></script>"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/data/BWaStrUtils.js\"></script>"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/data/ServiceContainer.js\"></script>"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/data/WMSCapabilitiesReader.js\"></script>" + 
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/controls/ActiveServicesPanel.js\"></script>"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/controls/ServiceCategoryPanel.js\"></script>"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/controls/ServiceTreeLayerNodeView.js\"></script>"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/controls/ServiceTreeLayerNode.js\"></script>"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/controls/ServiceTreeLoader.js\"></script>"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/controls/SearchCategoryPanel.js\"></script>"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/controls/SettingsDialog.js\"></script>"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/controls/OpacityDialog.js\"></script>"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/controls/NewServicePanel.js\"></script>"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/controls/MetaDataDialog.js\"></script>"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/controls/FeatureInfoControl.js\"></script>"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/controls/FeatureInfoDialog.js\"></script>"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/controls/LoadDialog.js\"></script>"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/controls/SaveDialog.js\"></script>"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/controls/DownloadDialog.js\"></script>"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/controls/PrintDialog.js\"></script>"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/controls/WelcomeDialog.js\"></script>"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/controls/LegendDialog.js\"></script>"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/controls/SearchPanel.js\"></script>"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/controls/PositionDialog.js\"></script>"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/controls/BWaStrDialog.js\"></script>"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/controls/BWaStrPanelResult.js\"></script>"+
				
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/Workspace.js\"></script>"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/main.js\"></script>"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/flashmessage1.1.1/Ext.ux.MessageBox.flash.js\"></script>"+
				"<!-- end output header phase -->"
        );
	//path: /ingrid-webmap-client/frontend/
		}else{
	        headerResource.addHeaderInfo(
				"<!-- start output header phase -->" +
				"<!-- Styles -->" +
				"<link rel=\"stylesheet\" type=\"text/css\" href=\"/ingrid-webmap-client/lib/geoext.ux/FeatureEditing/resources/css/feature-editing.css\" />"+  
				"<link rel=\"stylesheet\" type=\"text/css\" href=\"/ingrid-webmap-client/lib/extjs/resources/css/ext-all.css\" />"+
				"<link rel=\"stylesheet\" type=\"text/css\" href=\"/ingrid-webmap-client/lib/openlayers.addins/loadingpanel-min.css\" />"+
				"<link rel=\"stylesheet\" type=\"text/css\" href=\"/ingrid-webmap-client/frontend/css/style.css\" />"+
				"<link rel=\"stylesheet\" type=\"text/css\" href=\"/ingrid-webmap-client/lib/flashmessage1.1.1/Ext.ux.MessageBox.flash-min.css\" />"+		

				"<script type=\"text/javascript\">var languageCode = '"+languageString+"';</script>" +
				"<script type=\"text/javascript\">var viewConfiguration = 'search';</script>" +
                "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/auth\"></script>" +
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/geoext.ux/Locale.js\"></script>" +
				
				"<!-- Extjs -->" +
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/extjs/ext-all.js\"></script>" +
				
				"<!-- OpenLayers -->" +
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/openlayers/OpenLayers.js\"></script>" +
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/openlayers/lib/OpenLayers/Lang/"+languageString+".js\"></script>" +
				
				"<!-- GeoExt -->" +
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/shared/js/loader-min.js\"></script>"+
				"<script type=\"text/javascript\">OpenLayers.Lang.setCode('"+languageString+"');</script>"+ 
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/geoext.ux/FeatureEditing/resources/lang/"+languageString+".js\"></script>"+
				
				"<!-- Plugins -->" +
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/extjs.ux/plugins/CustomTreeFeature.js\"></script>"+

				"<!-- FileSaver -->" +
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/utils/FileSaver/FileSaver.js\"></script>"+

				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/proj4js/lib/proj4js-compressed.js\"></script>"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/frontend-all-min.js\"></script>"+
				"<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/flashmessage1.1.1/Ext.ux.MessageBox.flash-min.js\"></script>"+
				"<!-- end output header phase -->");
		}
	}
}