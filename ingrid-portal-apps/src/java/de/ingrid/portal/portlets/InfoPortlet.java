/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2016 wemove digital solutions GmbH
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
                request.getLocale()), request.getLocale());
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
            context.put("webmaster_email", PortalConfig.getInstance().getString(PortalConfig.EMAIL_WEBMASTER, "info@informationgrid.eu"));
            context.put("enableMeasure", PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_ENABLE_MEASURE, Boolean.FALSE));
            context.put("enableAbout", PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_ENABLE_ABOUT, Boolean.FALSE));
            context.put("enablePartner", PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_ENABLE_PARTNER, Boolean.FALSE));
            context.put("enableMaps", PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_ENABLE_MAPS, Boolean.FALSE));
            context.put("enableChronicle", PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_ENABLE_CHRONICLE, Boolean.FALSE));
            context.put("enableSearchCatalog", PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_ENABLE_SEARCH_CATALOG, Boolean.FALSE));
        }
        context.put("enableApplication", PortalConfig.getInstance().getBoolean(PortalConfig.PORTAL_ENABLE_APPLICATION, Boolean.FALSE));
        super.doView(request, response);
    }

    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {
    }

}
