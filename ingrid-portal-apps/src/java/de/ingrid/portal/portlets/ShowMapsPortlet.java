package de.ingrid.portal.portlets;

import java.io.IOException;
import java.util.HashMap;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.MimeResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.UtilsMapServiceManager;

public class ShowMapsPortlet extends GenericVelocityPortlet {

    private final static Logger log = LoggerFactory.getLogger(ShowMapsPortlet.class);

    public void init(PortletConfig config) throws PortletException {
        super.init(config);
    }

    public void doView(RenderRequest request, RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);
        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()), request.getLocale());
        context.put("MESSAGES", messages);

        // read preferences
        PortletPreferences prefs = request.getPreferences();
        String hKey = prefs.getValue("helpKey", null);
        if (hKey != null && hKey.length() > 0) {
            context.put("hKey", hKey);
        }

        if (request.getParameter("action") != null) {
            if (request.getParameter("action").equals("doTmpService")) {
                HashMap kml = new HashMap();

                try {
                    kml = UtilsMapServiceManager.createKmlFromIDF(request.getParameter(Settings.RESULT_KEY_PLUG_ID),
                            Integer.parseInt(request.getParameter(Settings.RESULT_KEY_DOC_ID)));

                    context.put("kmlUrl", kml.get("kml_url"));
                    context.put("kmlTitle", kml.get("coord_class"));

                } catch (NumberFormatException e) {
                    log.error("NumberFormatException: " + e);
                } catch (ParserConfigurationException e) {
                    log.error("ParserConfigurationException: " + e);
                } catch (SAXException e) {
                    log.error("SAXException: " + e);
                } catch (Exception e) {
                    log.error("Exception: " + e);
                }
            }
        }
        super.doView(request, response);
    }

    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {

    }

    /**
     * @see javax.portlet.GenericPortlet#doHeaders(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
     */
    protected void doHeaders(RenderRequest  request, RenderResponse  response) {
        String languageString = request.getLocale().getLanguage();

		if (PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_WEBMAPCLIENT_DEBUG, false)) {
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
	        setScriptHeader(response, "", "var viewConfiguration = 'view';");
	        setScriptHeader(response, "/ingrid-webmap-client/auth", "");

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
	        setScriptHeader(response, "/ingrid-webmap-client/lib/geoext.ux/Locale.js", "");
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
			// Styles
			setLinkHeader(response, "theme-access", "/ingrid-webmap-client/lib/extjs/resources/css/ext-all-access.css");
			setLinkHeader(response, "theme-neptune", "/ingrid-webmap-client/lib/extjs/resources/css/ext-all-neptune.css");
			setLinkHeader(response, "theme-gray", "/ingrid-webmap-client/lib/extjs/resources/css/ext-all-gray.css");
			setLinkHeader(response, "theme-all", "/ingrid-webmap-client/lib/extjs/resources/css/ext-all.css");
			
			setLinkHeader(response, "", "/ingrid-webmap-client/frontend/css/style.css");
			setLinkHeader(response, "", "/ingrid-webmap-client/lib/geoext.ux/FeatureEditing/resources/css/feature-editing.css");
			setLinkHeader(response, "", "/ingrid-webmap-client/lib/openlayers.addins/loadingpanel-min.css");
			setLinkHeader(response, "", "/ingrid-webmap-client/lib/flashmessage1.1.1/Ext.ux.MessageBox.flash-min.css");
			
	        // JScript
	        setScriptHeader(response, "", "var languageCode = '"+languageString+"';");
	        setScriptHeader(response, "", "var viewConfiguration = 'view';");
	        setScriptHeader(response, "/ingrid-webmap-client/auth", "");
	        setScriptHeader(response, "/ingrid-webmap-client/lib/geoext.ux/Locale.js", "");

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
