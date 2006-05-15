package de.ingrid.portal.portlets;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletSession;

import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.portal.global.Utils;
import de.ingrid.portal.interfaces.WMSInterface;
import de.ingrid.portal.interfaces.impl.WMSInterfaceImpl;
import de.ingrid.portal.interfaces.om.WMSServiceDescriptor;
import de.ingrid.portal.search.PageState;



public class ShowMapsPortlet extends GenericVelocityPortlet
{
    public void init(PortletConfig config) throws PortletException
    {
        super.init(config);
    }    
    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response) throws PortletException, IOException
    {
    	Context context = getContext(request);
        boolean hasJavaScript = Utils.isJavaScriptEnabled(request);

        PortletSession session = request.getPortletSession();
        
        String wmsServiceUrl = request.getParameter("wms_url");
        
        WMSInterface service = WMSInterfaceImpl.getInstance();
        String wmsURL;
        if (wmsServiceUrl != null && wmsServiceUrl.length() > 0) {
            wmsURL = service.getWMSAddedServiceURL(new WMSServiceDescriptor("", wmsServiceUrl), session.getId(), hasJavaScript);
        } else {
            wmsURL = service.getWMSViewerURL(session.getId(), hasJavaScript);
        }
        
        // read preferences
        PortletPreferences prefs = request.getPreferences();
        String hKey = prefs.getValue("helpKey", null);
        if (hKey != null && hKey.length() > 0) {
            context.put("hKey", hKey);
        }
        
        context.put("wmsURL", wmsURL);
        
        
        super.doView(request, response);
    }
    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException, IOException
    {
        String action = request.getParameter("action");

        PortletSession session = request.getPortletSession();
        
        PageState ps = (PageState) session.getAttribute("show_maps_portlet_page_state");
    	if (ps == null) {
    		ps = new PageState(this.getClass().getName());
    		ps = initPageState(ps);
    		session.setAttribute("show_maps_portlet_page_state", ps);
    	}
        
        if (action == null) {
        	return;
        }
    }    
    
    
    private PageState initPageState(PageState ps) {
		return ps;
    }
    
}
