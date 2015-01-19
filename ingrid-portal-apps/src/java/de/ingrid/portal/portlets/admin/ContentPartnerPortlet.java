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
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;

import org.apache.velocity.context.Context;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.forms.AdminContentPartnerForm;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.global.UtilsDB;
import de.ingrid.portal.hibernate.HibernateUtil;
import de.ingrid.portal.om.IngridPartner;

/**
 * Portlet handling content management of Partners
 *
 * @author martin@wemove.com
 */
public class ContentPartnerPortlet extends ContentPortlet {

	private final static Logger log = LoggerFactory.getLogger(ContentPartnerPortlet.class);

    /**
     * @see javax.portlet.Portlet#init(javax.portlet.PortletConfig)
     */
    public void init(PortletConfig config) throws PortletException {
        super.init(config);

        // set specific stuff in mother class
        psmlPage = "/portal/administration/admin-content-partner.psml";
        viewDefault = "/WEB-INF/templates/administration/content_partner.vm";
        viewEdit = "/WEB-INF/templates/administration/edit_partner.vm";
        viewNew = "/WEB-INF/templates/administration/edit_partner.vm";
        dbEntityClass = IngridPartner.class;
        viewTitleKey = "admin.title.partner";
        disablePartnerProviderEdit = PortalConfig.DISABLE_PARTNER_PROVIDER_EDIT;
    }

    /**
     * Set up Entity from parameters in request.
     * @param request
     * @return
     */
    protected Object[] getDBEntities(PortletRequest request) {
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
    
    protected boolean doViewDefault(RenderRequest request) {
        try {
            // always refresh !
            refreshBrowserState(request);
            ContentBrowserState state = getBrowserState(request);

            // get data from database
            String sortColumn = getSortColumn(request, "id");
            boolean ascendingOrder = isAscendingOrder(request);
            Session session = HibernateUtil.currentSession();
            Criteria crit = session.createCriteria(dbEntityClass);
            if (ascendingOrder) {
                crit.addOrder(Order.asc(sortColumn));
            } else {
                crit.addOrder(Order.desc(sortColumn));
            }
            crit.setFirstResult(state.getFirstRow());
            crit.setMaxResults(PortalConfig.getInstance().getInt(PortalConfig.PORTAL_ADMIN_NUMBER_ROW_PARTNER, state.getMaxRows()));
            state.setMaxRows(PortalConfig.getInstance().getInt(PortalConfig.PORTAL_ADMIN_NUMBER_ROW_PARTNER, state.getMaxRows()));
            
            List displayResults = UtilsDB.getValuesFromDB(crit, session, null, true);
            
            // put to render context
            Context context = getContext(request);
            context.put(CONTEXT_ENTITIES, displayResults);
            context.put(CONTEXT_BROWSER_STATE, state);
            setDefaultViewPage(viewDefault);
            return true;
        } catch (Exception ex) {
            if (log.isErrorEnabled()) {
                log.error("Problems processing default view:", ex);
            }
        }

        return false;
    }
}
