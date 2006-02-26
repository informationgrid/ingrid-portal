package de.ingrid.portal.portlets;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;

import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.portal.global.IngridResourceBundle;

public class InfoPortlet extends GenericVelocityPortlet {

    /** InfoPortlet default template and title if not set via PSML */
    public final static String DEFAULT_TEMPLATE = "/WEB-INF/templates/default_info.vm";

    public final static String DEFAULT_TITLE_KEY = "info.default.title";

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
        String myTitleKey = prefs.getValue("infoTitleKey", DEFAULT_TITLE_KEY);

        setDefaultViewPage(myView);
        response.setTitle(messages.getString(myTitleKey));

        super.doView(request, response);
    }

    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {
    }

}