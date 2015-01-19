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
/*
 * Copyright (c) 1997-2006 by wemove GmbH
 */
package de.ingrid.portal.portlets.admin;

import java.lang.reflect.Array;

import javax.portlet.ActionRequest;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;

import de.ingrid.portal.forms.AdminRSSForm;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.om.IngridRSSSource;

/**
 * Portlet handling content management of RSS feeds
 * 
 * @author martin@wemove.com
 */
public class ContentRSSPortlet extends ContentPortlet {

    // private final static Logger log =
    // LoggerFactory.getLogger(ContentRSSPortlet.class);

    /**
     * @see javax.portlet.Portlet#init(javax.portlet.PortletConfig)
     */
    public void init(PortletConfig config) throws PortletException {
        super.init(config);

        // set specific stuff in mother class
        psmlPage = "/portal/administration/admin-content-rss.psml";
        viewDefault = "/WEB-INF/templates/administration/content_rss.vm";
        viewEdit = "/WEB-INF/templates/administration/edit_rss.vm";
        viewNew = "/WEB-INF/templates/administration/edit_rss.vm";
        dbEntityClass = IngridRSSSource.class;
        viewTitleKey = "admin.title.rss";
    }

    /**
     * Set up Entity from parameters in request.
     * 
     * @param request
     * @return
     */
    protected Object[] getDBEntities(PortletRequest request) {
        IngridRSSSource[] dbEntities = null;
        Long[] ids = convertIds(getIds(request));
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
                if (dbEntities[i].getCategories() == null || dbEntities[i].getCategories().length() == 0) {
                    dbEntities[i].setCategories("all");
                }
            }
        }

        return dbEntities;
    }

    /**
     * Redefine method, we have to check stuff.
     * 
     * @see de.ingrid.portal.portlets.admin.ContentPortlet#doActionUpdate(javax.portlet.ActionRequest)
     */
    protected void doActionUpdate(ActionRequest request) {
        AdminRSSForm af = (AdminRSSForm) Utils.getActionForm(request, KEY_ACTION_FORM, AdminRSSForm.class);
        af.populate(request);
        af.validate();
        if (af.hasErrors()) {
            return;
        }

        super.doActionUpdate(request);
    }

    /**
     * Redefine method, we have to check stuff.
     * 
     * @see de.ingrid.portal.portlets.admin.ContentPortlet#doActionSave(javax.portlet.ActionRequest)
     */
    protected void doActionSave(ActionRequest request) {
        AdminRSSForm af = (AdminRSSForm) Utils.getActionForm(request, KEY_ACTION_FORM, AdminRSSForm.class);
        af.populate(request);
        af.validate();
        if (af.hasErrors()) {
            return;
        }

        super.doActionSave(request);
    }

}
