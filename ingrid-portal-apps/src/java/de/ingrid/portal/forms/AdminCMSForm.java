/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.forms;

import java.util.List;

import javax.portlet.PortletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import de.ingrid.portal.hibernate.HibernateUtil;
import de.ingrid.portal.om.IngridCMS;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class AdminCMSForm extends ActionForm {

    private final static Log log = LogFactory.getLog(AdminCMSForm.class);

    public static final String SESSION_KEY = AdminCMSForm.class.getName();

    private static final long serialVersionUID = 8006464356149088253L;

    public static final String FIELD_KEY = "item_key";

    public static final String FIELD_DESCRIPTION = "item_description";

    public static final String FIELD_TITLE_DE = "title_de";

    public static final String FIELD_TITLE_EN = "title_en";

    public static final String FIELD_VALUE_DE = "value_de";

    public static final String FIELD_VALUE_EN = "value_en";

    public static final String FIELD_MODE = "mode";

    public static final String PARAM_ID = "id";

    /**
     * @see de.ingrid.portal.forms.ActionForm#init()
     */
    public void init() {
        clearInput();
        clearMessages();
    }

    /**
     * @see de.ingrid.portal.forms.ActionForm#populate(javax.portlet.PortletRequest)
     */
    public void populate(PortletRequest request) {
        clear();
        setInput(PARAM_ID, request.getParameter(PARAM_ID));
        setInput(FIELD_KEY, request.getParameter(FIELD_KEY));
        setInput(FIELD_DESCRIPTION, request.getParameter(FIELD_DESCRIPTION));
        setInput(FIELD_TITLE_DE, request.getParameter(FIELD_TITLE_DE));
        setInput(FIELD_TITLE_EN, request.getParameter(FIELD_TITLE_EN));
        setInput(FIELD_VALUE_DE, request.getParameter(FIELD_VALUE_DE));
        setInput(FIELD_VALUE_EN, request.getParameter(FIELD_VALUE_EN));
        setInput(FIELD_MODE, request.getParameter(FIELD_MODE));
    }

    /**
     * @see de.ingrid.portal.forms.ActionForm#validate()
     */
    public boolean validate() {
        boolean allOk = true;
        clearErrors();
        if (!hasInput(FIELD_KEY)) {
            setError(FIELD_KEY, "admin.cms.error.noKey");
            allOk = false;
        }
        if (!hasInput(FIELD_VALUE_DE)) {
            setError(FIELD_VALUE_DE, "admin.cms.error.noValueDE");
            allOk = false;
        }
        if (hasInput(FIELD_MODE) && getInput(FIELD_MODE).equals("new")) {
            Session session = HibernateUtil.currentSession();
            List entities = session.createCriteria(IngridCMS.class)
                    .add(Restrictions.eq("itemKey", getInput(FIELD_KEY))).list();
            if (entities.size() > 0) {
                setError(FIELD_KEY, "admin.cms.error.duplicateKey");
                allOk = false;
            }
        }

        return allOk;
    }

}
