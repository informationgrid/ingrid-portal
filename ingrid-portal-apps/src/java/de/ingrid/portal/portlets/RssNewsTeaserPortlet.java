package de.ingrid.portal.portlets;

import java.io.IOException;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.PortletException;

import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.portal.hibernate.HibernateManager;
import de.ingrid.portal.om.IngridRSSStore;



public class RssNewsTeaserPortlet extends GenericVelocityPortlet
{

    HibernateManager fHibernateManager = null;
    
    public void init(PortletConfig config) throws PortletException
    {
        super.init(config);
        fHibernateManager = HibernateManager.getInstance();
    }    
    
    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response) throws PortletException, IOException
    {
        
        Context context = getContext(request);

        List rssEntries = this.fHibernateManager.loadAllData(IngridRSSStore.class, 0);
        
        context.put("rssEntries", rssEntries);
        
        super.doView(request, response);
    }
}