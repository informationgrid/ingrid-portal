/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.UtilsString;
import de.ingrid.portal.hibernate.HibernateUtil;
import de.ingrid.portal.om.IngridRSSStore;

public class RssNewsTeaserPortlet extends GenericVelocityPortlet {
    
    private final static Logger log = LoggerFactory.getLogger(RssNewsTeaserPortlet.class);

    public void init(PortletConfig config) throws PortletException {
        super.init(config);
    }

    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {

        Context context = getContext(request);

        IngridResourceBundle messages = new IngridResourceBundle(getPortletConfig().getResourceBundle(
                request.getLocale()), request.getLocale());
        context.put("MESSAGES", messages);

        PortletPreferences prefs = request.getPreferences();
        String titleKey = prefs.getValue("titleKey", "teaser.environment.title");
        response.setTitle(messages.getString(titleKey));

        Session session = HibernateUtil.currentSession();
        Transaction tx = null;

        int noOfEntriesDisplayed = Integer.parseInt(prefs.getValue("noOfEntriesDisplayed", "3"));
        
        /* used number of entries from ingrid-portal-apps.properties if property portal.rss.news.number is define */
        if(PortalConfig.getInstance().getString(PortalConfig.PORTAL_RSS_NEWS_NUMBER) != null){
        	noOfEntriesDisplayed = Integer.parseInt((PortalConfig.getInstance().getString(PortalConfig.PORTAL_RSS_NEWS_NUMBER)));
        }
        
        List rssEntries = null;

        try {
            tx = session.beginTransaction();
            rssEntries = session.createCriteria(IngridRSSStore.class).addOrder(Order.desc("publishedDate"))
                    .setMaxResults(noOfEntriesDisplayed).list();
            tx.commit();
        } catch (Throwable t) {
            log.error("Error getting RSS entries", t);
            if (tx != null) {
                tx.rollback();
            }
            throw new PortletException(t.getMessage());
        } finally {
            HibernateUtil.closeSession();
        }

        context.put("rssEntries", rssEntries);
        context.put("strutils", new UtilsString());

        super.doView(request, response);
    }
}
