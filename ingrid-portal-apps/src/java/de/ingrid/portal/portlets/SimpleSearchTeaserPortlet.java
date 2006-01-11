package de.ingrid.portal.portlets;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;

import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;


/**
 * TODO Please add comments!
 *
 * created 11.01.2006
 *
 * @author joachim
 * @version
 * 
 */
public class SimpleSearchTeaserPortlet extends GenericVelocityPortlet
{
    /* (non-Javadoc)
     * @see javax.portlet.Portlet#init(javax.portlet.PortletConfig)
     */
    public void init(PortletConfig config) throws PortletException
    {
        super.init(config);
    }
    
    /* (non-Javadoc)
     * @see javax.portlet.GenericPortlet#doView(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
     */
    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response) throws PortletException, IOException
    {
    	Context context = getContext(request);
    	PortletSession session = request.getPortletSession();
    	String selectedDS = (String) session.getAttribute("selectedDS");
    	if (selectedDS == null || selectedDS.length() == 0) {
    	    selectedDS = "1";
    	}
    	context.put("ds", selectedDS);
        
        super.doView(request, response);
    }
    
    
    /* (non-Javadoc)
     * @see javax.portlet.Portlet#processAction(javax.portlet.ActionRequest, javax.portlet.ActionResponse)
     */
    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException, IOException
    {
        String action = request.getParameter("action");
    	PortletSession session = request.getPortletSession();
        if (action == null) {
        	return;
        } else if (action.equalsIgnoreCase("doChangeDS")) {
        	session.setAttribute("selectedDS", request.getParameter("ds"), PortletSession.APPLICATION_SCOPE);
        }
    }    
    
}
