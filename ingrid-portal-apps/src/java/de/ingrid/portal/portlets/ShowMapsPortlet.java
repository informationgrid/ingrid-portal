package de.ingrid.portal.portlets;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.jetspeed.headerresource.HeaderResource;
import org.apache.jetspeed.portlet.PortletHeaderRequest;
import org.apache.jetspeed.portlet.PortletHeaderResponse;
import org.apache.jetspeed.portlet.SupportsHeaderPhase;
import org.apache.jetspeed.request.RequestContext;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.global.UtilsMapServiceManager;

public class ShowMapsPortlet extends GenericVelocityPortlet implements SupportsHeaderPhase {

    private final static Logger log = LoggerFactory.getLogger(ShowMapsPortlet.class);

    public void init(PortletConfig config) throws PortletException {
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
        String hKey = prefs.getValue("helpKey", null);
        if (hKey != null && hKey.length() > 0) {
            context.put("hKey", hKey);
        }

        if (Utils.getLoggedOn(request)) {
            context.put("logged", "true");
            context.put("mapUserId", request.getUserPrincipal().getName());
        } else {
            context.put("mapUserId", "NoId");
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
        } else if (request.getParameter("wms_url") != null) {
            context.put("wms", request.getParameter("wms_url"));
        }

        super.doView(request, response);
    }

    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {

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
                "<script type=\"text/javascript\">var languageCode = '"+languageString+"';</script>" +
                "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/extjs/adapter/ext/ext-base-debug.js\"></script>" +
        							 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/extjs/ext-all-debug.js\"></script>" +
        							 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/openlayers/lib/OpenLayers.js\"></script>" +
        							 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/geoext/lib/GeoExt.js\"></script>"+
        							 "<link rel=\"stylesheet\" type=\"text/css\" href=\"/ingrid-webmap-client/lib/extjs/resources/css/ext-all.css\" />"+
        							 "<link rel=\"stylesheet\" type=\"text/css\" href=\"/ingrid-webmap-client/frontend/css/style.css\" />"+
        							 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/proj4js/lib/proj4js-compressed.js\"></script>"+
        							 "<!-- openlayers extensions -->"+
        							 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/openlayers.addins/LoadingPanel.js\"></script>"+
        							 "<link rel=\"stylesheet\" type=\"text/css\" href=\"/ingrid-webmap-client/lib/openlayers.addins/loadingpanel.css\" />"+
        							 "<!-- extjs extensions -->\r\n" + 
        							 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/extjs.ux/Extjs.ux.Notification.js\"></script>\r\n" + 
        							 "<!-- geoext extensions -->"+
        							 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/geoext.ux/SimplePrint.js\"></script>"+
                                     "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/geoext.ux/Locale.js\"></script>"+
        							 "<!-- custom code -->"+
        							 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/shared/js/config.js\"></script>"+
        							 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/shared/js/Message.js\"></script>"+
        							 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/shared/js/Configuration.js\"></script>"+
        							 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/shared/js/data/StoreHelper.js\"></script>"+
        							 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/shared/js/model/WmsProxy.js\"></script>"+
        							 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/data/Session.js\"></script>"+
        							 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/data/SessionState.js\"></script>"+
        							 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/data/Service.js\"></script>"+
                                     "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/data/MapUtils.js\"></script>"+
        							 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/controls/ActiveServicesPanel.js\"></script>"+
        							 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/controls/ServiceCategoryPanel.js\"></script>"+
        							 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/controls/SearchCategoryPanel.js\"></script>"+
        							 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/controls/SettingsDialog.js\"></script>"+
        							 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/controls/OpacityDialog.js\"></script>"+
        							 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/controls/NewServiceDialog.js\"></script>"+
        							 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/controls/MetaDataDialog.js\"></script>"+
        							 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/controls/FeatureInfoControl.js\"></script>"+
        							 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/controls/FeatureInfoDialog.js\"></script>"+
        							 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/controls/LoadDialog.js\"></script>"+
        							 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/controls/SaveDialog.js\"></script>"+
        							 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/controls/DownloadDialog.js\"></script>"+
        							 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/controls/PrintDialog.js\"></script>"+
        							 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/controls/WelcomeDialog.js\"></script>"+
        							 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/Workspace.js\"></script>"+
        							 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/main.js\"></script>"+
        							 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/flashmessage1.1.1/Ext.ux.MessageBox.flash.js\"></script>"+
        							 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/data/ServiceContainer.js\"></script>"+
        							 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/data/WMSCapabilitiesReader.js\"></script>" + 
        							 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/controls/ServiceTreeLoader.js\"></script>"+
        							 "<link rel=\"stylesheet\" type=\"text/css\" href=\"/ingrid-webmap-client/lib/flashmessage1.1.1/Ext.ux.MessageBox.flash.css\" />"+
       				                "<!-- end output header phase -->"

        							 
        );
		
	
    
	//path: /ingrid-webmap-client/frontend/
		}else{
	        headerResource.addHeaderInfo(
	                "<!-- start output header phase -->" +
	                "<script type=\"text/javascript\">var languageCode = '"+languageString+"';</script>" +
	                "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/extjs/adapter/ext/ext-base.js\"></script>" +
					 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/extjs/ext-all.js\"></script>" +
					 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/openlayers/OpenLayers.js\"></script>" +
					 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/geoext/script/GeoExt.js\"></script>"+
					 
					 "<link rel=\"stylesheet\" type=\"text/css\" href=\"/ingrid-webmap-client/lib/extjs/resources/css/ext-all.css\" />"+
					 "<link rel=\"stylesheet\" type=\"text/css\" href=\"/ingrid-webmap-client/lib/openlayers.addins/loadingpanel-min.css\" />"+
					 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/proj4js/lib/proj4js-compressed.js\"></script>"+

					 "<link rel=\"stylesheet\" type=\"text/css\" href=\"/ingrid-webmap-client/frontend/css/style-min.css\" />"+

					 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/frontend/js/frontend-all-min.js\"></script>"+
					 "<script type=\"text/javascript\" src=\"/ingrid-webmap-client/lib/flashmessage1.1.1/Ext.ux.MessageBox.flash-min.js\"></script>"+
					 "<link rel=\"stylesheet\" type=\"text/css\" href=\"/ingrid-webmap-client/lib/flashmessage1.1.1/Ext.ux.MessageBox.flash-min.css\" />"+		
	                 "<!-- end output header phase -->");


		}
		
    
	}

}
