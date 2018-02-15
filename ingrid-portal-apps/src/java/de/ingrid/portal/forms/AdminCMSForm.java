/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2018 wemove digital solutions GmbH
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
package de.ingrid.portal.forms;

import java.util.List;

import javax.portlet.PortletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import de.ingrid.portal.global.Utils;
import de.ingrid.portal.hibernate.HibernateUtil;
import de.ingrid.portal.om.IngridCMS;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class AdminCMSForm extends ActionForm {

    private final static Logger log = LoggerFactory.getLogger(AdminCMSForm.class);

    public static final String SESSION_KEY = AdminCMSForm.class.getName();

    private static final long serialVersionUID = 8006464356149088253L;

    public static final String FIELD_KEY = "item_key";

    public static final String FIELD_DESCRIPTION = "item_description";

    // this is the new generic field title where any language can be appended
    public static final String FIELD_TITLE = "title_";

    // this is the new generic field value where any language can be appended
    public static final String FIELD_VALUE = "value_";
    
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
        String[] languages = Utils.getLanguagesShortAsArray();
        for (String lang : languages) {
        	setInput(FIELD_TITLE + lang, request.getParameter(FIELD_TITLE + lang));
        	setInput(FIELD_VALUE + lang, request.getParameter(FIELD_VALUE + lang));
        }
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
        if (!hasInput(FIELD_VALUE + "de")) {
            setError(FIELD_VALUE + "de", "admin.cms.error.noValueDE");
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
