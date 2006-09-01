/*
 * Copyright (c) 1997-2006 by wemove GmbH
 */
package de.ingrid.portal.portlets.admin;

import java.lang.reflect.Array;

import javax.portlet.ActionRequest;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;

import de.ingrid.portal.global.UtilsDB;
import de.ingrid.portal.om.IngridPartner;

/**
 * Portlet handling content management of Partners
 *
 * @author martin@wemove.com
 */
public class ContentPartnerPortlet extends ContentPortlet {

    //    private final static Log log = LogFactory.getLog(ContentPartnerPortlet.class);

    /**
     * @see javax.portlet.Portlet#init(javax.portlet.PortletConfig)
     */
    public void init(PortletConfig config) throws PortletException {
        super.init(config);

        // set specific stuff in mother class
        psmlPage = "/ingrid-portal/portal/administration/admin-content-partner.psml";
        viewDefault = "/WEB-INF/templates/administration/content_partner.vm";
        viewEdit = "/WEB-INF/templates/administration/edit_partner.vm";
        viewNew = "/WEB-INF/templates/administration/edit_partner.vm";
        dbEntityClass = IngridPartner.class;
    }

    /**
     * Set up Entity from parameters in request.
     * @param request
     * @return
     */
    protected Object[] getDBEntities(ActionRequest request) {
        IngridPartner[] dbEntities = null;
        Long[] ids = convertIds(getIds(request));
        // set up entity
        if (ids != null) {
            dbEntities = (IngridPartner[]) Array.newInstance(IngridPartner.class, ids.length);
            for (int i = 0; i < ids.length; i++) {
                dbEntities[i] = new IngridPartner();
                dbEntities[i].setId(ids[i]);
                try {
                    dbEntities[i].setIdent(request.getParameter("ident" + i).toLowerCase());
                } catch (Exception ex) {
                }
                dbEntities[i].setName(request.getParameter("name" + i));
                try {
                    int sortKey = new Integer(request.getParameter("sortkey" + i)).intValue();
                    dbEntities[i].setSortkey(sortKey);
                } catch (Exception ex) {
                }
            }
        }

        return dbEntities;
    }

    /**
     * Redefine method, we have to check idents.
     * @see de.ingrid.portal.portlets.admin.ContentPortlet#doSave(javax.portlet.ActionRequest)
     */
    protected void doSave(ActionRequest request) {
        Object[] entities = getDBEntities(request);
        UtilsDB.saveDBObjects(entities);
    }


}
