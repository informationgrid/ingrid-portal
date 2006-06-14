package de.ingrid.portal.portlets;

import java.io.IOException;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;

import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;

import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.UtilsString;
import de.ingrid.portal.hibernate.HibernateUtil;
import de.ingrid.portal.om.IngridRSSStore;



public class RssNewsPortlet extends GenericVelocityPortlet
{

    public void init(PortletConfig config) throws PortletException
    {
        super.init(config);
    }    
    
    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response) throws PortletException, IOException
    {
        
        Context context = getContext(request);

        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()));
        context.put("MESSAGES", messages);

        Session session = HibernateUtil.currentSession();
        Transaction tx = null;

        // read preferences
        PortletPreferences prefs = request.getPreferences();

        int noOfEntriesDisplayed = Integer.parseInt(prefs.getValue("startWithEntry", "3"));
        
        List rssEntries = null;
        
        try {
            tx = session.beginTransaction();
            rssEntries = session.createCriteria(IngridRSSStore.class).addOrder(Order.desc("publishedDate")).setFirstResult(noOfEntriesDisplayed).list();
            tx.commit();
        } catch (Throwable t) {
            if (tx != null) {
                tx.rollback();
            }
            throw new PortletException( t.getMessage() );
        } finally {
            HibernateUtil.closeSession();
        }
        
        context.put("rssEntries", rssEntries);
        context.put("strutils", new UtilsString());
        
        super.doView(request, response);
    }
}