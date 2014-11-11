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
public class SearchExtAdrPlaceReferenceForm extends ActionForm {

    private static final long serialVersionUID = -8926883694637904748L;

    /** attribute name of action form in session */
    public static final String SESSION_KEY = SearchExtAdrPlaceReferenceForm.class.getName();

    /** field names (name of request parameter) */
    public static final String FIELD_STREET = "street";
    public static final String FIELD_ZIP = "zip";
    public static final String FIELD_CITY = "city";

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
        setInput(FIELD_STREET, request.getParameter(FIELD_STREET));
        setInput(FIELD_ZIP, request.getParameter(FIELD_ZIP));
        setInput(FIELD_CITY, request.getParameter(FIELD_CITY));
    }

    /**
     * @see de.ingrid.portal.forms.ActionForm#validate()
     */
    public boolean validate() {
        return true;
    }

}
