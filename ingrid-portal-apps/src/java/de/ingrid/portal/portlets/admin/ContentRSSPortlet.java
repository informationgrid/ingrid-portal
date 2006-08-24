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
import org.apache.velocity.context.Context;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import de.ingrid.portal.global.UtilsDB;
import de.ingrid.portal.hibernate.HibernateUtil;
import de.ingrid.portal.om.IngridRSSSource;

/**
 * Portlet handling content management of RSS feeds
 *
 * @author martin@wemove.com
 */
public class ContentRSSPortlet extends ContentPortlet {

    private final static Log log = LogFactory.getLog(ContentRSSPortlet.class);

    // PAGE

    private final static String MY_PAGE = "/ingrid-portal/portal/administration/admin-content-rss.psml";

    // VIEW TEMPLATES

    private final static String TEMPLATE_BROWSE = "/WEB-INF/templates/administration/content_rss.vm";

    private final static String TEMPLATE_EDIT = "/WEB-INF/templates/administration/edit_rss.vm";

    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#doView(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
     */
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        try {
            Context context = getContext(request);

            // reset state ? may be necessary on initial call (e.g. called from other page)
            handleState(request);

            // default view
            boolean doDefaultView = true;
            setDefaultViewPage(TEMPLATE_BROWSE);

            // handle action
            String action = getAction(request);

            // EDIT
            if (action.equals(PARAMV_ACTION_DO_EDIT)) {
                try {
                    // get data from database
                    String id = getId(request);
                    if (id != null) {
                        Session session = HibernateUtil.currentSession();
                        Criteria crit = session.createCriteria(IngridRSSSource.class).add(
                                Restrictions.eq("id", new Long(id)));
                        crit.setMaxResults(1);
                        List rssSource = UtilsDB.getValuesFromDB(crit, session, null, true);

                        // put to render context and switch view
                        context.put("mode", "edit");
                        context.put("rssSource", rssSource.get(0));
                        setDefaultViewPage(TEMPLATE_EDIT);
                        doDefaultView = false;
                    }
                } catch (Exception ex2) {
                    if (log.isErrorEnabled()) {
                        log.error("Problems fetching RSS feed to edit:", ex2);
                    }
                }
            }

            // NEW
            if (action.equals(PARAMV_ACTION_DO_NEW)) {
                context.put("mode", "new");
                setDefaultViewPage(TEMPLATE_EDIT);
                doDefaultView = false;
            }

            // REFRESH, DELETE, SAVE
            if (doDefaultView) {
                // get data from database
                String sortColumn = getSortColumn(request, "id");
                boolean ascendingOrder = isAscendingOrder(request);
                Session session = HibernateUtil.currentSession();
                Criteria crit = null;
                if (ascendingOrder) {
                    crit = session.createCriteria(IngridRSSSource.class).addOrder(Order.asc(sortColumn));
                } else {
                    crit = session.createCriteria(IngridRSSSource.class).addOrder(Order.desc(sortColumn));
                }
                List rssSources = UtilsDB.getValuesFromDB(crit, session, null, true);

                // put to render context
                context.put("rssSources", rssSources);
            }
        } catch (Exception ex) {
            if (log.isErrorEnabled()) {
                log.error("Problems processing doView:", ex);
            }
        }

        super.doView(request, response);
    }

    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#processAction(javax.portlet.ActionRequest, javax.portlet.ActionResponse)
     */
    public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {
        // set page to show
        request.setAttribute(PARAM_PAGE, MY_PAGE);

        super.processAction(request, response);
    }

    /**
     * @see de.ingrid.portal.portlets.admin.ContentPortlet#doUpdate(javax.portlet.ActionRequest)
     */
    protected void doSave(ActionRequest request) {
        IngridRSSSource rssSource = getDBEntity(request);
        UtilsDB.saveDBObject(rssSource);
    }

    /**
     * @see de.ingrid.portal.portlets.admin.ContentPortlet#doSave(javax.portlet.ActionRequest)
     */
    protected void doUpdate(ActionRequest request) {
        IngridRSSSource rssSource = getDBEntity(request);
        UtilsDB.updateDBObject(rssSource);
    }

    /**
     * @see de.ingrid.portal.portlets.admin.ContentPortlet#doDelete(javax.portlet.ActionRequest)
     */
    protected void doDelete(ActionRequest request) {
        IngridRSSSource rssSource = getDBEntity(request);
        UtilsDB.deleteDBObject(rssSource);
    }

    /**
     * Set up Entity from parameters in request.
     * @param request
     * @return
     */
    protected IngridRSSSource getDBEntity(ActionRequest request) {
        // set up entity
        IngridRSSSource rssSource = new IngridRSSSource();
        try {
            rssSource.setId(new Long(getId(request)));
        } catch (Exception ex) {
        }
        rssSource.setProvider(request.getParameter("provider"));
        rssSource.setDescription(request.getParameter("description"));
        rssSource.setUrl(request.getParameter("url"));
        rssSource.setLanguage(request.getParameter("language"));
        rssSource.setCategories(request.getParameter("categories"));

        return rssSource;
    }
}
