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
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.forms;

import javax.portlet.PortletRequest;

/**
 * TODO Describe your created type (class, etc.) here.
 *
 * @author joachim@wemove.com
 */
public class SearchSaveForm extends ActionForm {

    /**
     * TODO: Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = -3707403164047986781L;

    /** attribute name of action form in session */
    public static final String SESSION_KEY = SearchSaveForm.class.getName();
    
    public static final String FIELD_SAVE_ENTRY_NAME = "save_entry_name";

    /**
     * @see de.ingrid.portal.forms.ActionForm#init()
     */
    public void init() {
        clear();
    }

    /**
     * @see de.ingrid.portal.forms.ActionForm#populate(javax.portlet.PortletRequest)
     */
    public void populate(PortletRequest request) {
        clearInput();
        setInput(FIELD_SAVE_ENTRY_NAME, request.getParameter(FIELD_SAVE_ENTRY_NAME));
    }

    /**
     * @see de.ingrid.portal.forms.ActionForm#validate()
     */
    public boolean validate() {
        boolean doQuery = true;
        clearErrors();

        // check field for emtpy entry
        if (getInput(FIELD_SAVE_ENTRY_NAME).trim().length() == 0) {
            doQuery = false;
        }
        return doQuery;
    }

}
