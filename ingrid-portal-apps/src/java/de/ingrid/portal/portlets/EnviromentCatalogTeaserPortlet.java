package de.ingrid.portal.portlets;

import java.io.IOException;

import javax.portlet.PortletConfig;
import javax.portlet.PortletException;

import org.apache.portals.bridges.velocity.GenericVelocityPortlet;



public class EnviromentCatalogTeaserPortlet extends GenericVelocityPortlet
{
    public void init(PortletConfig config) throws PortletException
    {
        super.init(config);
    }    
    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response) throws PortletException, IOException
    {
        super.doView(request, response);
    }
}