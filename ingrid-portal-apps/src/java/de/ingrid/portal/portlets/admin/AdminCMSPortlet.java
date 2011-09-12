/*
 * Copyright (c) 1997-2006 by wemove GmbH
 */
package de.ingrid.portal.portlets.admin;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Date;
import java.util.HashSet;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.generic.ListTool;
import org.hibernate.Session;

import de.ingrid.portal.forms.ActionForm;
import de.ingrid.portal.forms.AdminCMSForm;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.global.UtilsDB;
import de.ingrid.portal.global.UtilsString;
import de.ingrid.portal.hibernate.HibernateUtil;
import de.ingrid.portal.om.IngridCMS;
import de.ingrid.portal.om.IngridCMSItem;

/**
 * Portlet handling content management of Partners
 * 
 * @author joachim@wemove.com
 */
public class AdminCMSPortlet extends ContentPortlet {

    private final static Logger log = LoggerFactory.getLogger(ContentPortlet.class);

    /**
     * @see javax.portlet.Portlet#init(javax.portlet.PortletConfig)
     */
    public void init(PortletConfig config) throws PortletException {
        super.init(config);

        // set specific stuff in mother class
        psmlPage = "/portal/administration/admin-cms.psml";
        viewDefault = "/WEB-INF/templates/administration/admin_cms_browser.vm";
        viewEdit = "/WEB-INF/templates/administration/admin_cms_edit.vm";
        viewNew = "/WEB-INF/templates/administration/admin_cms_edit.vm";
        dbEntityClass = IngridCMS.class;
        viewTitleKey = "admin.title.content";
    }

    /**
     * Set up Entity from parameters in request.
     * 
     * @param request
     * @return
     */
    protected Object[] getDBEntities(PortletRequest request) {
        IngridCMS[] dbEntities = null;
        Long id = null;
        AdminCMSForm af = (AdminCMSForm) Utils.getActionForm(request, AdminCMSForm.SESSION_KEY, AdminCMSForm.class);
        try {
            id = new Long(af.getInput(AdminCMSForm.PARAM_ID));
        } catch (NumberFormatException e) {
        }
        // set up entity
        if (id != null) {
            dbEntities = (IngridCMS[]) Array.newInstance(IngridCMS.class, 1);
            dbEntities[0] = new IngridCMS();
            dbEntities[0].setId(id);
            dbEntities[0].setItemChanged(new Date());
            dbEntities[0].setItemChangedBy(request.getUserPrincipal().getName());
            dbEntities[0].setItemDescription(af.getInput(AdminCMSForm.FIELD_DESCRIPTION));
            dbEntities[0].setItemKey(af.getInput(AdminCMSForm.FIELD_KEY));

            HashSet localizedItems = new HashSet();
            
            String[] languages = Utils.getLanguagesShortAsArray();
            for (String lang : languages) {
            	IngridCMSItem item = new IngridCMSItem();	            
	            item.setItemLang(lang);
	            item.setItemTitle(af.getInput(AdminCMSForm.FIELD_TITLE + lang));
	            item.setItemValue(af.getInput(AdminCMSForm.FIELD_VALUE + lang));
	            item.setItemChanged(new Date());
	            item.setItemChangedBy(request.getUserPrincipal().getName());
	            localizedItems.add(item);
            }
            dbEntities[0].setLocalizedItems(localizedItems);
        }

        return dbEntities;
    }

    /**
     * Redefine method, we have to check stuff.
     * 
     * @see de.ingrid.portal.portlets.admin.ContentPortlet#doActionUpdate(javax.portlet.ActionRequest)
     */
    protected void doActionUpdate(ActionRequest request) {
        AdminCMSForm af = (AdminCMSForm) Utils.getActionForm(request, AdminCMSForm.SESSION_KEY, AdminCMSForm.class);
        af.populate(request);
        af.validate();
        if (af.hasErrors()) {
            return;
        }

        Session session = HibernateUtil.currentSession();
        IngridCMS cmsEntry = (IngridCMS) session.load(dbEntityClass, new Long(af.getInput(AdminCMSForm.PARAM_ID)));

        cmsEntry.setItemChanged(new Date());
        cmsEntry.setItemChangedBy(request.getUserPrincipal().getName());
        cmsEntry.setItemDescription(af.getInput(AdminCMSForm.FIELD_DESCRIPTION));
        cmsEntry.setItemKey(af.getInput(AdminCMSForm.FIELD_KEY));

        String[] languages = Utils.getLanguagesShortAsArray();
        for (String lang : languages) {
        	IngridCMSItem item = cmsEntry.getLocalizedEntry(lang);
        	if (item == null) {
        		item = new IngridCMSItem();
        		item.setItemLang(lang);
        		cmsEntry.getLocalizedItems().add(item);
        	}
	        item.setItemTitle(af.getInput(AdminCMSForm.FIELD_TITLE + lang));
	        item.setItemValue(af.getInput(AdminCMSForm.FIELD_VALUE + lang));
	        item.setItemChanged(new Date());
	        item.setItemChangedBy(request.getUserPrincipal().getName());
		}

        UtilsDB.updateDBObject(cmsEntry);

        af.addMessage("admin.cms.update.success");
    }

    /**
     * @see de.ingrid.portal.portlets.admin.ContentPortlet#processAction(javax.portlet.ActionRequest,
     *      javax.portlet.ActionResponse)
     */
    public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {

        if (request.getParameter(PARAMV_ACTION_DB_DO_SAVE) != null) {
            AdminCMSForm f = (AdminCMSForm) Utils.getActionForm(request, AdminCMSForm.SESSION_KEY, AdminCMSForm.class);
            f.clear();
            f.clearMessages();
            // call sub method
            doActionSave(request);
            response.setRenderParameter(Settings.PARAM_ACTION, PARAMV_ACTION_DB_DO_SAVE);
            response.setRenderParameter("cmd", "action processed");
        } else if (request.getParameter(PARAMV_ACTION_DB_DO_UPDATE) != null) {
            AdminCMSForm f = (AdminCMSForm) Utils.getActionForm(request, AdminCMSForm.SESSION_KEY, AdminCMSForm.class);
            f.clear();
            f.clearMessages();
            // call sub method
            doActionUpdate(request);
            response.setRenderParameter(Settings.PARAM_ACTION, PARAMV_ACTION_DB_DO_UPDATE);
            response.setRenderParameter("cmd", "action processed");
        } else {
            super.processAction(request, response);
        }
    }

    /**
     * @see de.ingrid.portal.portlets.admin.ContentPortlet#doViewEdit(javax.portlet.RenderRequest)
     */
    protected boolean doViewEdit(RenderRequest request) {
        AdminCMSForm f = (AdminCMSForm) Utils.getActionForm(request, AdminCMSForm.SESSION_KEY, AdminCMSForm.class);
        f.clear();
        f.clearMessages();

        return super.doViewEdit(request);
    }

    /**
     * @see de.ingrid.portal.portlets.admin.ContentPortlet#doViewAfterSave(javax.portlet.RenderRequest)
     */
    protected boolean doViewAfterSave(RenderRequest request) {
        ActionForm af = (AdminCMSForm) Utils.getActionForm(request, AdminCMSForm.SESSION_KEY, AdminCMSForm.class);
        setDefaultViewPage(viewNew);
        Context context = getContext(request);
        context.put("actionForm", af);
        context.put(CONTEXT_MODE, CONTEXTV_MODE_NEW);
        Object[] entities = getDBEntities(request);
        context.put(CONTEXT_ENTITIES, entities);
        return false;
    }

    /**
     * @see de.ingrid.portal.portlets.admin.ContentPortlet#doViewAfterUpdate(javax.portlet.RenderRequest)
     */
    protected boolean doViewAfterUpdate(RenderRequest request) {
        ActionForm af = (AdminCMSForm) Utils.getActionForm(request, AdminCMSForm.SESSION_KEY, AdminCMSForm.class);
        setDefaultViewPage(viewEdit);
        Context context = getContext(request);
        context.put("actionForm", af);
        context.put(CONTEXT_MODE, CONTEXTV_MODE_EDIT);
        String lang = Utils.checkSupportedLanguage(request.getLocale().getLanguage());
        
        // add the velocity tool to access arrays
        ListTool listTool = new ListTool();
        context.put("ListTool", listTool);
        context.put("languagesNames", Utils.getLanguagesFullAsArray(lang));
        context.put("languagesShort", Utils.getLanguagesShortAsArray());
        
        Object[] entities = getDBEntities(request);
        context.put(CONTEXT_ENTITIES, entities);
        return false;
    }

    /**
     * @see de.ingrid.portal.portlets.admin.ContentPortlet#doViewNew(javax.portlet.RenderRequest)
     */
    protected boolean doViewNew(RenderRequest request) {
        try {
            AdminCMSForm f = (AdminCMSForm) Utils.getActionForm(request, AdminCMSForm.SESSION_KEY, AdminCMSForm.class);

            Object[] newEntity = { dbEntityClass.newInstance() };

            Context context = getContext(request);
            context.put(CONTEXT_MODE, CONTEXTV_MODE_NEW);
            context.put(CONTEXT_UTILS_STRING, new UtilsString());
            context.put(CONTEXT_ENTITIES, newEntity);

            String cmd = request.getParameter("cmd");
            if (cmd == null) {
                f.clear();
                f.clearMessages();
            }

            context.put("actionForm", f);

            setDefaultViewPage(viewNew);
            return true;
        } catch (Exception ex) {
            if (log.isErrorEnabled()) {
                log.error("Problems adding new entity:", ex);
            }
        }
        return false;

    }

    /**
     * @see de.ingrid.portal.portlets.admin.ContentPortlet#doActionDelete(javax.portlet.ActionRequest)
     */
    protected void doActionDelete(ActionRequest request) {
        Session session = HibernateUtil.currentSession();
        Long[] ids = ContentPortlet.convertIds(getIds(request));
        for (int i = 0; i < ids.length; i++) {
            IngridCMS cmsEntry = (IngridCMS) session.load(dbEntityClass, ids[i]);
            UtilsDB.deleteDBObject(cmsEntry);
        }
    }

    /**
     * Redefine method, we have to check stuff.
     * 
     * @see de.ingrid.portal.portlets.admin.ContentPortlet#doActionSave(javax.portlet.ActionRequest)
     */
    protected void doActionSave(ActionRequest request) {
        AdminCMSForm af = (AdminCMSForm) Utils.getActionForm(request, AdminCMSForm.SESSION_KEY, AdminCMSForm.class);
        af.populate(request);
        af.validate();
        if (af.hasErrors()) {
            return;
        }

        IngridCMS cmsEntry = new IngridCMS();
        cmsEntry.setItemChanged(new Date());
        cmsEntry.setItemChangedBy(request.getUserPrincipal().getName());
        cmsEntry.setItemDescription(af.getInput(AdminCMSForm.FIELD_DESCRIPTION));
        cmsEntry.setItemKey(af.getInput(AdminCMSForm.FIELD_KEY));

        HashSet localizedItems = new HashSet();
        
        String[] languages = Utils.getLanguagesShortAsArray();
        for (String lang : languages) {
	        IngridCMSItem item = new IngridCMSItem();
	        item.setItemLang(lang);
	        item.setItemTitle(af.getInput(AdminCMSForm.FIELD_TITLE + lang));
	        item.setItemValue(af.getInput(AdminCMSForm.FIELD_VALUE + lang));
	        item.setItemChanged(new Date());
	        item.setItemChangedBy(request.getUserPrincipal().getName());
	        localizedItems.add(item);
        }
        
        cmsEntry.setLocalizedItems(localizedItems);

        UtilsDB.saveDBObject(cmsEntry);

        af.addMessage("admin.cms.save.success");

    }
}
