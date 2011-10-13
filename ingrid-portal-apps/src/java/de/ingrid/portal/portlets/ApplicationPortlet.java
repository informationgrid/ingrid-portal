package de.ingrid.portal.portlets;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;

import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;

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
        
        String action = request.getParameter(Settings.PARAM_ACTION);
		if(action != null && action.length() > 0){
			response.setTitle("<span class='application_title'>"+messages.getString("application.title.tab."+action)+"</span>");
			context.put("app_menu", action);
		}else{
			response.setTitle("<span class='application_title'>"+messages.getString("application.title.tab.0")+"</span>");
			context.put("app_menu", "0");
		}
		
       super.doView(request, response);
    }
	
	public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException, IOException {
		IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()));
        
		String action = request.getParameter(Settings.PARAM_ACTION);
		if(action != null && action.length() > 0){
			if(!action.startsWith("extern")){
				actionResponse.sendRedirect(actionResponse.encodeURL("/portal/application/main-application-"+action+".psml"));
			}else{
				actionResponse.sendRedirect(actionResponse.encodeURL(messages.getString("application.link."+action)));
			}
		}
	}
}
