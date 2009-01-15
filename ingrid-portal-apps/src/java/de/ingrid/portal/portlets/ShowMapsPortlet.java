package de.ingrid.portal.portlets;

import java.io.IOException;
import java.security.Principal;
import java.util.Collection;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.portal.global.IngridPersistencePrefs;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.interfaces.impl.WMSInterfaceImpl;
import de.ingrid.portal.search.UtilsSearch;

public class ShowMapsPortlet extends GenericVelocityPortlet {

    private final static Log log = LogFactory.getLog(ShowMapsPortlet.class);
	
	public void init(PortletConfig config) throws PortletException {
        super.init(config);
    }

    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
        Context context = getContext(request);
        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()));
        context.put("MESSAGES", messages);

        String wmsURL = UtilsSearch.getWMSURL(request, request.getParameter("wms_url"), true);

        // read preferences
        PortletPreferences prefs = request.getPreferences();
        String hKey = prefs.getValue("helpKey", null);
        if (hKey != null && hKey.length() > 0) {
            context.put("hKey", hKey);
        }
        
        if (log.isDebugEnabled()) {
        	log.debug("Open WMS viewer with the following URL: " + wmsURL);
        }
        context.put("wmsURL", wmsURL);

        // enable the save button if the query was set AND a user is logged on
        if (Utils.getLoggedOn(request)) {
            context.put("enableSave", "true");
            context.put("wmsServicesSaved", request.getParameter("wmsServicesSaved"));
            String wmc = (String)IngridPersistencePrefs.getPref(request.getUserPrincipal().getName(), IngridPersistencePrefs.WMC_DOCUMENT);
            if (wmc != null && wmc.length() > 0) {
            	context.put("enableLoad", "true");
            }
            context.put("mapBenderVersion", WMSInterfaceImpl.getInstance().getMapbenderVersion());

        }

        super.doView(request, response);
    }

    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {
        String action = request.getParameter("action");

        if (action != null && action.equals("doSaveWMC") && Utils.getLoggedOn(request)) {
            Principal principal = request.getUserPrincipal();
        	// get the WMC from mapbender
        	String wmc = WMSInterfaceImpl.getInstance().getWMCDocument(request.getPortletSession().getId());
            IngridPersistencePrefs.setPref(principal.getName(), IngridPersistencePrefs.WMC_DOCUMENT, wmc);
            actionResponse.setRenderParameter("wmsServicesSaved", "1");
        } else if (action != null && action.equals("doSaveWMSServices") && Utils.getLoggedOn(request)) {
            Principal principal = request.getUserPrincipal();
            // get the WMS Services
            Collection c = WMSInterfaceImpl.getInstance().getWMSServices(request.getPortletSession().getId());
            IngridPersistencePrefs.setPref(principal.getName(), IngridPersistencePrefs.WMS_SERVICES, c);
            actionResponse.setRenderParameter("wmsServicesSaved", "1");
        	
        } else if (action != null && action.equals("doLoadWMC") && Utils.getLoggedOn(request)) {
            // set the WMC in mapbender
            Principal principal = request.getUserPrincipal();
            String wmc = (String)IngridPersistencePrefs.getPref(principal.getName(), IngridPersistencePrefs.WMC_DOCUMENT);
            if (wmc != null && wmc.length() > 0) {
            	WMSInterfaceImpl.getInstance().setWMCDocument(wmc, request.getPortletSession().getId());
            }
        }
    }

}
