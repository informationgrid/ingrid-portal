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
import de.ingrid.portal.om.IngridContent;

/**
 * Form Handler for Content Management of Ingrid Content.
 * 
 * @author Joachim Müller
 */
public class AdminContentIngridContentForm extends ActionForm {

    private final static Log log = LogFactory.getLog(AdminContentIngridContentForm.class);

    private static final long serialVersionUID = 8335389649101260301L;

    public static final String FIELD_ITEM_KEY = "item_key";
    public static final String FIELD_ITEM_LANG = "item_lang";
    public static final String FIELD_ITEM_TITLE = "item_title";
    public static final String FIELD_ITEM_VALUE = "item_value";
    public static final String FIELD_ITEM_CHANGED = "item_changed";
    public static final String FIELD_ITEM_CHANGEDBY = "item_changed_by";

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
                setInput(FIELD_ITEM_KEY + i, request.getParameter(FIELD_ITEM_KEY + i));
                setInput(FIELD_ITEM_LANG + i, request.getParameter(FIELD_ITEM_LANG + i));
                setInput(FIELD_ITEM_TITLE + i, request.getParameter(FIELD_ITEM_TITLE + i));
                setInput(FIELD_ITEM_VALUE + i, request.getParameter(FIELD_ITEM_VALUE + i));
                setInput(FIELD_ITEM_CHANGED + i, request.getParameter(FIELD_ITEM_CHANGED + i));
                setInput(FIELD_ITEM_CHANGEDBY + i, request.getParameter(FIELD_ITEM_CHANGEDBY + i));
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
                ArrayList newKeys = new ArrayList(intNumEntities);
                ArrayList newLangs = new ArrayList(intNumEntities);
                for (int i = 0; i < intNumEntities; i++) {

                    // IDENT
                    fieldName = FIELD_ITEM_KEY + i;
                    if (!hasInput(fieldName)) {
                        setError(fieldName, "content.content.edit.error.itemkey");
                        allOk = false;
                    } else {
                        String itemKey = getInput(fieldName);
                        String itemLang = getInput(FIELD_ITEM_LANG + i);
                        // check whether we add multiple new records where new keys are the same !
                        if (newKeys.contains(itemKey)) {
                            int idx = newKeys.indexOf(itemKey);
                            if (((String)newLangs.get(idx)).equals(itemLang)) {
                                setError(fieldName, "content.content.edit.error.doubleKey");
                                allOk = false;
                            }
                        } else {
                            newKeys.add(itemKey);
                            newLangs.add(itemLang);
                            // check database whether other record has same key/lang
                            Long id = null;
                            try {
                                id = new Long(getInput(PARAM_ID + i));
                            } catch (Exception ex) {
                            }
                            Session session = HibernateUtil.currentSession();
                            Criteria crit = session.createCriteria(IngridContent.class).add(
                                    Restrictions.eq(FIELD_ITEM_KEY, itemKey)).add(Restrictions.eq(FIELD_ITEM_LANG, itemLang));
                            if (id != null) {
                                crit.add(Restrictions.ne(PARAM_ID, id));
                            }
                            List foundItems = UtilsDB.getValuesFromDB(crit, session, null, true);
                            if (!foundItems.isEmpty()) {
                                setError(fieldName, "content.content.edit.error.doubleKey");
                                allOk = false;
                            }
                        }
                    }

                    // LANG
                    fieldName = FIELD_ITEM_LANG + i;
                    if (!hasInput(fieldName)) {
                        setError(fieldName, "content.content.edit.error.noLang");
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
