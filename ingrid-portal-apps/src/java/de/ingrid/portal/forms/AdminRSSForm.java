/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.forms;

import java.util.List;

import javax.portlet.PortletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import de.ingrid.portal.global.UtilsDB;
import de.ingrid.portal.hibernate.HibernateUtil;
import de.ingrid.portal.om.IngridRSSSource;

/**
 * Form Handler for Content Management of Partners.
 * 
 * @author Martin Maidhof
 */
public class AdminRSSForm extends ActionForm {

    private final static Logger log = LoggerFactory.getLogger(AdminRSSForm.class);

    private static final long serialVersionUID = 8335389649101260301L;

    public static final String FIELD_PROVIDER = "provider";

    public static final String FIELD_DESCRIPTION = "description";

    public static final String FIELD_URL = "url";

    public static final String FIELD_LANGUAGE = "language";

    public static final String FIELD_CATEGORIES = "categories";

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
                setInput(FIELD_PROVIDER + i, request.getParameter(FIELD_PROVIDER + i));
                setInput(FIELD_DESCRIPTION + i, request.getParameter(FIELD_DESCRIPTION + i));
                setInput(FIELD_URL + i, request.getParameter(FIELD_URL + i));
                setInput(FIELD_LANGUAGE + i, request.getParameter(FIELD_LANGUAGE + i));
                if (request.getParameter(FIELD_CATEGORIES + i) == null
                        || request.getParameter(FIELD_CATEGORIES + i).length() == 0) {
                    setInput(FIELD_CATEGORIES + i, "all");
                } else {
                    setInput(FIELD_CATEGORIES + i, request.getParameter(FIELD_CATEGORIES + i));
                }
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
                for (int i = 0; i < intNumEntities; i++) {

                    fieldName = FIELD_PROVIDER + i;
                    if (!hasInput(fieldName)) {
                        setError(fieldName, "admin.rss.error.missing.provider");
                        allOk = false;
                    }
                    fieldName = FIELD_URL + i;
                    if (!hasInput(fieldName)) {
                        setError(fieldName, "admin.rss.error.missing.url");
                        allOk = false;
                    } else {
                        // check database whether other record (other id) has
                        // same ident (kuerzel)
                        Long id = null;
                        try {
                            id = new Long(getInput(PARAM_ID + i));
                        } catch (Exception ex) {
                        }
                        String url = getInput(fieldName);
                        Session session = HibernateUtil.currentSession();
                        Criteria crit = session.createCriteria(IngridRSSSource.class).add(
                                Restrictions.eq(FIELD_URL, url));
                        if (id != null) {
                            crit.add(Restrictions.ne(PARAM_ID, id));
                        }
                        List foundURLs = UtilsDB.getValuesFromDB(crit, session, null, true);
                        if (foundURLs != null && !foundURLs.isEmpty()) {
                            setError(fieldName, "admin.rss.error.url.exists");
                            allOk = false;
                        }
                    }
                    fieldName = FIELD_LANGUAGE + i;
                    if (!hasInput(fieldName)) {
                        setError(fieldName, "admin.rss.error.missing.language");
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
