/*
 * Copyright (c) 1997-2006 by wemove GmbH
 */
package de.ingrid.portal.portlets.admin;

import java.io.IOException;
import java.lang.reflect.Array;
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
                    Long[] ids = getIds(request);
                    if (ids != null) {
                        Session session = HibernateUtil.currentSession();
                        Criteria crit = session.createCriteria(IngridRSSSource.class).add(Restrictions.in("id", ids));
                        List rssSources = UtilsDB.getValuesFromDB(crit, session, null, true);

                        // put to render context and switch view
                        context.put(CONTEXT_MODE, CONTEXTV_MODE_EDIT);
                        context.put(CONTEXT_ENTITIES, rssSources);
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
                context.put(CONTEXT_MODE, CONTEXTV_MODE_NEW);
                context.put(CONTEXT_ENTITIES, new IngridRSSSource[] { new IngridRSSSource() });
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
                context.put(CONTEXT_ENTITIES, rssSources);
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
        IngridRSSSource[] entities = getDBEntities(request);
        UtilsDB.saveDBObjects(entities);
    }

    /**
     * @see de.ingrid.portal.portlets.admin.ContentPortlet#doSave(javax.portlet.ActionRequest)
     */
    protected void doUpdate(ActionRequest request) {
        IngridRSSSource[] entities = getDBEntities(request);
        UtilsDB.updateDBObjects(entities);
    }

    /**
     * @see de.ingrid.portal.portlets.admin.ContentPortlet#doDelete(javax.portlet.ActionRequest)
     */
    protected void doDelete(ActionRequest request) {
        IngridRSSSource[] entities = getDBEntities(request);
        UtilsDB.deleteDBObjects(entities);
    }

    /**
     * Set up Entity from parameters in request.
     * @param request
     * @return
     */
    protected IngridRSSSource[] getDBEntities(ActionRequest request) {
        IngridRSSSource[] dbEntities = null;
        Long[] ids = getIds(request);

        // set up entity
        if (ids != null) {
            dbEntities = (IngridRSSSource[]) Array.newInstance(IngridRSSSource.class, ids.length);
            for (int i = 0; i < ids.length; i++) {
                dbEntities[i] = new IngridRSSSource();
                dbEntities[i].setId(ids[i]);
                dbEntities[i].setProvider(request.getParameter("provider" + i));
                dbEntities[i].setDescription(request.getParameter("description" + i));
                dbEntities[i].setUrl(request.getParameter("url" + i));
                dbEntities[i].setLanguage(request.getParameter("language" + i));
                dbEntities[i].setCategories(request.getParameter("categories" + i));
            }
        }

        return dbEntities;
    }
}
