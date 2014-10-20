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

public class WeatherTeaserPortlet extends GenericVelocityPortlet {

    public void init(PortletConfig config) throws PortletException {
        super.init(config);
    }

    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);

        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()), request.getLocale());
        context.put("MESSAGES", messages);
        
        context.put("DWD_PATH", PortalConfig.getInstance().getString(PortalConfig.TEASER_WEATHER_DWD_PATH, ""));
        context.put("DWD_MOVIE", PortalConfig.getInstance().getString(PortalConfig.TEASER_WEATHER_DWD_MOVIE, ""));
        
        PortletPreferences prefs = request.getPreferences();
        String titleKey = prefs.getValue("titleKey", "teaser.weather.title");
        response.setTitle(messages.getString(titleKey));

        super.doView(request, response);
    }

    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {
    }
}