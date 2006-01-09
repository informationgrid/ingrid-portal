package de.ingrid.portal.portlets;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;

import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.portal.search.PageState;



public class MeasuresSearchPortlet extends GenericVelocityPortlet
{
    public void init(PortletConfig config) throws PortletException
    {
        super.init(config);
    }    
    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response) throws PortletException, IOException
    {
    	Context context = getContext(request);
        
        super.doView(request, response);
    }
    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException, IOException
    {
        String action = request.getParameter("action");

    	PortletSession session = request.getPortletSession();
    	PageState ps = (PageState) session.getAttribute("measures_search_portlet_page_state");
    	if (ps == null) {
    		ps = new PageState(this.getClass().getName());
    		ps = initPageState(ps);
    		session.setAttribute("measures_search_portlet_page_state", ps);
    	}
        
        if (action == null) {
        	return;
        }
    }    
    
    
    private PageState initPageState(PageState ps) {
		return ps;
    }
    
}