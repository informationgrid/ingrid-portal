/*
 * Copyright (c) 1997-2006 by wemove GmbH
 */
package de.ingrid.portal.portlets.admin;

import java.io.IOException;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

import de.ingrid.portal.global.UtilsDB;
import de.ingrid.portal.hibernate.HibernateUtil;
import de.ingrid.portal.om.IngridRSSSource;

/**
 * Portlet handling content management of RSS feeds
 *
 * @author martin@wemove.com
 */
public class ContentRSSPortlet extends GenericVelocityPortlet {

    private final static Log log = LogFactory.getLog(ContentRSSPortlet.class);

    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#doView(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
     */
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        try {
            Context context = getContext(request);

            // get data from request
            String sortColumn = request.getParameter("sortColumn");
            if (sortColumn == null) {
                sortColumn = "provider";
            }
            String sortType = request.getParameter("sortType");
            if (sortType == null) {
                sortType = "asc";
            }

            // get feeds from database
            Session session = HibernateUtil.currentSession();
            Criteria selectCrit = null;
            if (sortType.equals("asc")) {
                selectCrit = session.createCriteria(IngridRSSSource.class).addOrder(Order.asc(sortColumn));
            } else {
                selectCrit = session.createCriteria(IngridRSSSource.class).addOrder(Order.desc(sortColumn));
            }
            List rssSources = UtilsDB.getValuesFromDB(selectCrit, session, null, true);

            // put to render context
            context.put("rssSources", rssSources);
        } catch (Exception ex) {
            if (log.isErrorEnabled()) {
                log.error("Problems processing Content of RSS Feeds", ex);
            }
        }

        super.doView(request, response);
    }

    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#processAction(javax.portlet.ActionRequest, javax.portlet.ActionResponse)
     */
    public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {
        // TODO Auto-generated method stub
        super.processAction(request, response);
    }

}
