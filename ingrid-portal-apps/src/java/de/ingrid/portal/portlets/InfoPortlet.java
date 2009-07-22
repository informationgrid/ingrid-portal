package de.ingrid.portal.portlets;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;

import org.apache.jetspeed.request.RequestContext;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.global.IngridResourceBundle;

public class InfoPortlet extends GenericVelocityPortlet {

    /** InfoPortlet default template and title if not set via PSML */
    public final static String DEFAULT_TEMPLATE = "/WEB-INF/templates/default_info.vm";

    public final static String DEFAULT_TITLE_KEY = "info.default.title";

    private final static String SITEMAP_TEMPLATE = "/WEB-INF/templates/sitemap.vm";
    
    
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

        String myView = prefs.getValue("infoTemplate", DEFAULT_TEMPLATE);
        String myTitleKey = prefs.getValue("titleKey", DEFAULT_TITLE_KEY);
        String myLink = prefs.getValue("infoLink", "");
        
        setDefaultViewPage(myView);
        response.setTitle(messages.getString(myTitleKey));
        
        if (myLink.length() > 0) {
            String infoLink = response.encodeURL(((RequestContext)request.getAttribute(RequestContext.REQUEST_PORTALENV)).getRequest().getContextPath() + myLink);
            context.put("infoLink", infoLink);
        }
        if (myView.equalsIgnoreCase(SITEMAP_TEMPLATE)) {
            context.put("webmaster_email", PortalConfig.getInstance().getString(PortalConfig.EMAIL_WEBMASTER, "webmaster@portalu.de"));
            context.put("enableMeasure", PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_ENABLE_MEASURE, Boolean.FALSE));
            context.put("enableService", PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_ENABLE_SERVICE, Boolean.FALSE));
            context.put("enableEnviroment", PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_ENABLE_ENVIROMENT, Boolean.FALSE));
            context.put("enableMaps", PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_ENABLE_MAPS, Boolean.FALSE));
            context.put("enableChronicle", PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_ENABLE_CHRONICLE, Boolean.FALSE));
            context.put("enableSearchCatalog", PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_ENABLE_SEARCH_CATALOG, Boolean.FALSE));
            context.put("enableSearchCatalogThesaurus", PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_ENABLE_SEARCH_CATALOG_THESAURUS, Boolean.FALSE));
            context.put("enableNewsletter", PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_ENABLE_NEWSLETTER, Boolean.TRUE));
        }

        super.doView(request, response);
    }

    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {
    }

}