/*
 * Copyright (c) 1997-2006 by wemove GmbH
 */
package de.ingrid.portal.portlets.admin;

import java.lang.reflect.Array;

import javax.portlet.ActionRequest;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;

import de.ingrid.portal.forms.AdminContentPartnerForm;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.om.IngridProvider;

/**
 * Portlet handling content management of providers
 *
 * @author joachim@wemove.com
 */
public class ContentProviderPortlet extends ContentPortlet {

    //    private final static Log log = LogFactory.getLog(ContentPartnerPortlet.class);

    /**
     * @see javax.portlet.Portlet#init(javax.portlet.PortletConfig)
     */
    public void init(PortletConfig config) throws PortletException {
        super.init(config);

        // set specific stuff in mother class
        psmlPage = "/ingrid-portal/portal/administration/admin-content-provider.psml";
        viewDefault = "/WEB-INF/templates/administration/content_provider.vm";
        viewEdit = "/WEB-INF/templates/administration/edit_provider.vm";
        viewNew = "/WEB-INF/templates/administration/edit_provider.vm";
        dbEntityClass = IngridProvider.class;
    }

    /**
     * Set up Entity from parameters in request.
     * @param request
     * @return
     */
    protected Object[] getDBEntities(PortletRequest request) {
        IngridProvider[] dbEntities = null;
        Long[] ids = convertIds(getIds(request));
        // set up entity
        if (ids != null) {
            dbEntities = (IngridProvider[]) Array.newInstance(IngridProvider.class, ids.length);
            for (int i = 0; i < ids.length; i++) {
                dbEntities[i] = new IngridProvider();
                dbEntities[i].setId(ids[i]);
                try {
                    dbEntities[i].setIdent(request.getParameter("ident" + i).toLowerCase());
                } catch (Exception ex) {
                }
                dbEntities[i].setName(request.getParameter("name" + i));
                dbEntities[i].setUrl(request.getParameter("url" + i));
                try {
                    int sortKey = new Integer(request.getParameter("sortkey" + i)).intValue();
                    dbEntities[i].setSortkey(sortKey);
                } catch (Exception ex) {
                }
                try {
                    int sortKeyPartner = new Integer(request.getParameter("sortkey_partner" + i)).intValue();
                    dbEntities[i].setSortkeyPartner(sortKeyPartner);
                } catch (Exception ex) {
                }
            }
        }

        return dbEntities;
    }

    /**
     * Redefine method, we have to check stuff.
     * @see de.ingrid.portal.portlets.admin.ContentPortlet#doActionUpdate(javax.portlet.ActionRequest)
     */
    protected void doActionUpdate(ActionRequest request) {
        AdminContentPartnerForm af = (AdminContentPartnerForm) Utils.getActionForm(request, KEY_ACTION_FORM,
                AdminContentPartnerForm.class);
        af.populate(request);
        af.validate();
        if (af.hasErrors()) {
            return;
        }

        super.doActionUpdate(request);
    }

    /**
     * Redefine method, we have to check stuff.
     * @see de.ingrid.portal.portlets.admin.ContentPortlet#doActionSave(javax.portlet.ActionRequest)
     */
    protected void doActionSave(ActionRequest request) {
        AdminContentPartnerForm af = (AdminContentPartnerForm) Utils.getActionForm(request, KEY_ACTION_FORM,
                AdminContentPartnerForm.class);
        af.populate(request);
        af.validate();
        if (af.hasErrors()) {
            return;
        }

        super.doActionSave(request);
    }
}
