package de.ingrid.portal.portlets;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;

import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;



public class SimpleSearchTeaserPortlet extends GenericVelocityPortlet
{
    private Integer selectedDataSource = new Integer(1);
    
    public void init(PortletConfig config) throws PortletException
    {
        super.init(config);
    }    
    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response) throws PortletException, IOException
    {
    	Context context = getContext(request);
    	context.put("ds", selectedDataSource);
        
        super.doView(request, response);
    }
    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException, IOException
    {
        String action = request.getParameter("action");
        
        if (action == null) {
        	return;
        } else if (action.equalsIgnoreCase("doChangeDS")) {
        	String ds = request.getParameter("ds");
        	selectedDataSource = Integer.decode(ds);
        }
    }    
}