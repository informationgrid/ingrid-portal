/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2020 wemove digital solutions GmbH
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

import de.ingrid.portal.global.UtilsDB;
import de.ingrid.portal.hibernate.HibernateUtil;
import de.ingrid.portal.om.IngridPartner;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.portlet.PortletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Form Handler for Content Management of Partners.
 * 
 * @author Martin Maidhof
 */
public class AdminContentPartnerForm extends ActionForm {

    private static final Logger log = LoggerFactory.getLogger(AdminContentPartnerForm.class);

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
            setInput("numEntities", Integer.toString(ids.length));
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
                int intNumEntities = Integer.parseInt(numEntities);
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
                                log.error("Error on validate.", ex);
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
        } catch (Exception t) {
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
