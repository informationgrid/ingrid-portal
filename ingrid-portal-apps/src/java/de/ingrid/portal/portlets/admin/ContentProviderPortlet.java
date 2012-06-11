/*
 * Copyright (c) 1997-2006 by wemove GmbH
 */
package de.ingrid.portal.portlets.admin;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.velocity.context.Context;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.forms.AdminContentPartnerForm;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.global.UtilsDB;
import de.ingrid.portal.hibernate.HibernateUtil;
import de.ingrid.portal.om.IngridProvider;

/**
 * Portlet handling content management of providers
 * 
 * @author joachim@wemove.com
 */
public class ContentProviderPortlet extends ContentPortlet {

    private final static Logger log = LoggerFactory.getLogger(ContentProviderPortlet.class);

    private static final String PARAMV_ACTION_DO_IMPORT = "doImport";

    /**
     * @see javax.portlet.Portlet#init(javax.portlet.PortletConfig)
     */
    public void init(PortletConfig config) throws PortletException {
        super.init(config);

        // set specific stuff in mother class
        psmlPage = "/portal/administration/admin-content-provider.psml";
        viewDefault = "/WEB-INF/templates/administration/content_provider.vm";
        viewEdit = "/WEB-INF/templates/administration/edit_provider.vm";
        viewNew = "/WEB-INF/templates/administration/edit_provider.vm";
        dbEntityClass = IngridProvider.class;
        viewTitleKey = "admin.title.provider";
        disablePartnerProviderEdit = PortalConfig.DISABLE_PARTNER_PROVIDER_EDIT;
    }

    /**
     * Set up Entity from parameters in request.
     * 
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
     * 
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
     * @see de.ingrid.portal.portlets.admin.ContentPortlet#doView(javax.portlet.RenderRequest,
     *      javax.portlet.RenderResponse)
     */
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        Context context = getContext(request);

        context.put("partners", UtilsDB.getPartners());

        // handle action
        String action = getAction(request);
        // IMPORT
        if (action.equals(PARAMV_ACTION_DO_IMPORT)) {
            context.put("import_message", request.getParameter("import_message"));
        }
        super.doView(request, response);
    }
    
    protected boolean doViewDefault(RenderRequest request) {
        try {
            // always refresh !
//            refreshBrowserState(request);
            ContentBrowserState state = getBrowserState(request);

            // get data from database
            String sortColumn = getSortColumn(request, "id");
            boolean ascendingOrder = isAscendingOrder(request);
            Session session = HibernateUtil.currentSession();
            Criteria crit = session.createCriteria(dbEntityClass);
            Map<String, String> filterCriteria = state.getFilterCriteria();
            if (filterCriteria.get("filterCriteriaProviderIdent") != null && filterCriteria.get("filterCriteriaProviderIdent").length() > 0) {
                crit.add((Restrictions.like("ident", filterCriteria.get("filterCriteriaProviderIdent").replaceAll("\\*", "\\%"))));
            }
            if (filterCriteria.get("filterCriteriaProviderName") != null && filterCriteria.get("filterCriteriaProviderName").length() > 0) {
                crit.add((Restrictions.like("name", filterCriteria.get("filterCriteriaProviderName").replaceAll("\\*", "\\%"))));
            }
            if (ascendingOrder) {
                crit.addOrder(Order.asc(sortColumn));
            } else {
                crit.addOrder(Order.desc(sortColumn));
            }
            List totalResults = UtilsDB.getValuesFromDB(crit, session, null, false);
            state.setTotalNumRows(totalResults.size());
            
            crit.setFirstResult(state.getFirstRow());
            crit.setMaxResults(PortalConfig.getInstance().getInt(PortalConfig.PORTAL_ADMIN_NUMBER_ROW_PROVIDER, state.getMaxRows()));
            state.setMaxRows(PortalConfig.getInstance().getInt(PortalConfig.PORTAL_ADMIN_NUMBER_ROW_PROVIDER, state.getMaxRows()));
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

    /**
     * Redefine method, we have to check stuff.
     * 
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

    /**
     * @see de.ingrid.portal.portlets.admin.ContentPortlet#processAction(javax.portlet.ActionRequest,
     *      javax.portlet.ActionResponse)
     */
    public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {
        // handle action
        String action = getAction(request);
        // IMPORT
        if (action.equals(PARAMV_ACTION_DO_IMPORT)) {
            response.setRenderParameter("action", PARAMV_ACTION_DO_IMPORT);
            String data = request.getParameter("csvData").trim();
            String[] lines = data.split("\r");
            int cnt = 0;
            Session session = HibernateUtil.currentSession();
            Transaction tx = null;
            tx = session.beginTransaction();
            try {
                for (int i = 0; i < lines.length; i++) {
                    String[] fields = lines[i].split("\t");
                    Long id = null;
                    id = Long.valueOf(fields[0].trim());
                    if (id.longValue() > 0) {
                        List entities = session.createCriteria(dbEntityClass).add(Restrictions.eq("ident", fields[1]))
                                .list();
                        IngridProvider p;
                        if (entities.size() > 0) {
                            p = (IngridProvider) entities.iterator().next();
                            p.setName(fields[2].trim());
                            p.setUrl(fields[3].trim());
                            p.setSortkey(Integer.parseInt(fields[4].trim()));
                            p.setSortkeyPartner(Integer.parseInt(fields[5].trim()));
                            session.update(p);
                        } else {
                            p = new IngridProvider();
                            p.setId(id);
                            p.setIdent(fields[1].trim());
                            p.setName(fields[2].trim());
                            p.setUrl(fields[3].trim());
                            p.setSortkey(Integer.parseInt(fields[4].trim()));
                            p.setSortkeyPartner(Integer.parseInt(fields[5].trim()));
                            session.save(p);
                        }
                        cnt++;
                    }
                }
                tx.commit();
                response.setRenderParameter("import_message", "Es wurden " + cnt + " Anbieter erfolgreich importiert.");
            } catch (Exception e) {
                if (tx != null) {
                    tx.rollback();
                }
                log.error("Error while importing providers.", e);
                response.setRenderParameter("import_message", "Fehler beim Importieren des Anbieters in Zeile " + cnt
                        + ". Es wurden keine Anbieter importiert.");
            } finally {
                HibernateUtil.closeSession();
            }
        } else {
            super.processAction(request, response);
        }
    }
}
