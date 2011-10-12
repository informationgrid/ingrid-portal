package de.ingrid.portal.portlets;

import java.io.IOException;

import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;

import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ingrid.portal.global.IngridResourceBundle;

public class ApplicationPortlet extends GenericVelocityPortlet {

	private final static Logger log = LoggerFactory.getLogger(ApplicationPortlet.class);
	
	public final static String DEFAULT_TEMPLATE = "/WEB-INF/templates/application.vm";
	
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

        String myTitleKey = prefs.getValue("titleKey", "");
        response.setTitle(messages.getString(myTitleKey));
        
       super.doView(request, response);
    }

}
