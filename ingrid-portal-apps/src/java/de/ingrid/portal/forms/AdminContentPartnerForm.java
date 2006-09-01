/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.forms;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import de.ingrid.portal.global.UtilsDB;
import de.ingrid.portal.hibernate.HibernateUtil;
import de.ingrid.portal.om.IngridPartner;
import de.ingrid.portal.portlets.admin.ContentPartnerPortlet;

/**
 * Form Handler for Content Management of Partners.
 * 
 * @author Martin Maidhof
 */
public class AdminContentPartnerForm extends ActionForm {

    private final static Log log = LogFactory.getLog(ContentPartnerPortlet.class);

    private static final long serialVersionUID = 8335389649101260309L;

    public static final String FIELD_IDENT = "ident";

    public static final String FIELD_NAME = "name";

    public static final String FIELD_SORTKEY = "sortkey";

    public static final String PARAM_ID = "id";

    /**
     * @see de.ingrid.portal.forms.ActionForm#init()
     */
    public void init() {
        clearInput();
    }

    /**
     * @see de.ingrid.portal.forms.ActionForm#populate(javax.portlet.PortletRequest)
     */
    public void populate(PortletRequest request) {
        clear();
        String[] ids = request.getParameterValues(PARAM_ID);
        if (ids != null) {
            setInput("numEntities", new Integer(ids.length).toString());
            for (int i = 0; i < ids.length; i++) {
                setInput(PARAM_ID + i, ids[i]);
                setInput(FIELD_IDENT + i, request.getParameter(FIELD_IDENT + i));
                setInput(FIELD_NAME + i, request.getParameter(FIELD_NAME + i));
                setInput(FIELD_SORTKEY + i, request.getParameter(FIELD_SORTKEY + i));
            }
        }
    }

    /**
     * @see de.ingrid.portal.forms.ActionForm#validate()
     */
    public boolean validate() {
        boolean allOk = true;
        clearErrors();

        // check input
        try {
            String numEntities = getInput("numEntities");
            if (numEntities != null) {
                int intNumEntities = new Integer(numEntities).intValue();
                String fieldName = "";
                ArrayList newIdents = new ArrayList(intNumEntities);
                for (int i = 0; i < intNumEntities; i++) {

                    // IDENT
                    fieldName = FIELD_IDENT + i;
                    if (!hasInput(fieldName)) {
                        setError(fieldName, "content.partner.edit.error.noIdent");
                        allOk = false;
                    } else {
                        String ident = getInput(fieldName);
                        // check whether we add multiple new records where new idents are the same !
                        if (newIdents.contains(ident)) {
                            setError(fieldName, "content.partner.edit.error.doubleIdent");
                            allOk = false;
                        } else {
                            newIdents.add(ident);
                            // check database whether other record (other id) has same ident (kuerzel)
                            Long id = null;
                            try {
                                id = new Long(getInput(PARAM_ID + i));
                            } catch (Exception ex) {
                            }
                            Session session = HibernateUtil.currentSession();
                            Criteria crit = session.createCriteria(IngridPartner.class).add(
                                    Restrictions.eq(FIELD_IDENT, ident));
                            if (id != null) {
                                crit.add(Restrictions.ne(PARAM_ID, id));
                            }
                            List foundPartners = UtilsDB.getValuesFromDB(crit, session, null, true);
                            if (!foundPartners.isEmpty()) {
                                setError(fieldName, "content.partner.edit.error.doubleIdent");
                                allOk = false;
                            }
                        }
                    }

                    // NAME
                    fieldName = FIELD_NAME + i;
                    if (!hasInput(fieldName)) {
                        setError(fieldName, "content.partner.edit.error.noName");
                        allOk = false;
                    }
                }
            }
        } catch (Throwable t) {
            if (log.isErrorEnabled()) {
                log.error("Error validating input.", t);
            }
            allOk = false;
        } finally {
            HibernateUtil.closeSession();
        }

        return allOk;
    }
}
