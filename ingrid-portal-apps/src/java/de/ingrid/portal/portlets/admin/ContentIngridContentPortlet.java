/*
 * Copyright (c) 1997-2006 by wemove GmbH
 */
package de.ingrid.portal.portlets.admin;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.util.Date;

import javax.portlet.ActionRequest;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;

import de.ingrid.portal.forms.AdminContentPartnerForm;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.om.IngridContent;
import de.ingrid.portal.om.IngridContent;

/**
 * Portlet handling content management of Partners
 *
 * @author martin@wemove.com
 */
public class ContentIngridContentPortlet extends ContentPortlet {

    //    private final static Log log = LogFactory.getLog(ContentPartnerPortlet.class);

    /**
     * @see javax.portlet.Portlet#init(javax.portlet.PortletConfig)
     */
    public void init(PortletConfig config) throws PortletException {
        super.init(config);

        // set specific stuff in mother class
        psmlPage = "/ingrid-portal/portal/administration/admin-ingrid-content.psml";
        viewDefault = "/WEB-INF/templates/administration/browse_ingrid_content.vm";
        viewEdit = "/WEB-INF/templates/administration/edit_ingrid_content.vm";
        viewNew = "/WEB-INF/templates/administration/edit_ingrid_content.vm";
        dbEntityClass = IngridContent.class;
    }

    /**
     * Set up Entity from parameters in request.
     * @param request
     * @return
     */
    protected Object[] getDBEntities(PortletRequest request) {
        IngridContent[] dbEntities = null;
        Long[] ids = convertIds(getIds(request));
        // set up entity
        if (ids != null) {
            dbEntities = (IngridContent[]) Array.newInstance(IngridContent.class, ids.length);
            for (int i = 0; i < ids.length; i++) {
                dbEntities[i] = new IngridContent();
                dbEntities[i].setId(ids[i]);
                dbEntities[i].setItemKey(request.getParameter("item_key" + i));
                dbEntities[i].setItemLang(request.getParameter("item_lang" + i));
                dbEntities[i].setItemTitle(request.getParameter("item_title" + i));
                dbEntities[i].setItemValue(request.getParameter("item_value" + i));
                dbEntities[i].setItemChangedBy(request.getParameter("item_changed_by" + i));
                
                try {
                    dbEntities[i].setItemChanged(DateFormat.getInstance().parse(request.getParameter("item_value" + i)));
                } catch (Exception ex) { }
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
