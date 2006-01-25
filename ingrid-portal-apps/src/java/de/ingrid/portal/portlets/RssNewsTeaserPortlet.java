package de.ingrid.portal.portlets;

import java.io.IOException;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;

import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

import de.ingrid.portal.hibernate.HibernateManager;
import de.ingrid.portal.om.IngridRSSStore;
import de.ingrid.portal.utils.Utils;



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
        Session session = this.fHibernateManager.getSession();

        // read preferences
        PortletPreferences prefs = request.getPreferences();

        int noOfEntriesDisplayed = Integer.parseInt(prefs.getValue("noOfEntriesDisplayed", "3"));
        
        List rssEntries = session.createCriteria(IngridRSSStore.class).addOrder(Order.desc("publishedDate")).setMaxResults(noOfEntriesDisplayed).list();
        
        context.put("rssEntries", rssEntries);
        
        super.doView(request, response);
    }
}