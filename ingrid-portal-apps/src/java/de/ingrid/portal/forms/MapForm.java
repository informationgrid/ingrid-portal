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
package de.ingrid.portal.forms;

import javax.portlet.PortletRequest;

public class MapForm extends ActionForm {


	private static final long serialVersionUID = 5284771128504510508L;

	/** attribute name of action form in session */
    public static final String SESSION_KEY = "map_form";

    public static final String FIELD_MAP_NAME = "mapurl";
    
    /**
     * @see de.ingrid.portal.forms.ActionForm#init()
     */
    public void init() {
        clearInput();
        // no default data to set
    }

    /**
     * @see de.ingrid.portal.forms.ActionForm#populate(javax.portlet.PortletRequest)
     */
    public void populate(PortletRequest request) {
        clearInput();
        setInput(FIELD_MAP_NAME, request.getParameter(FIELD_MAP_NAME));
       }

    public boolean validate() {
        boolean allOk = true;
        clearErrors();

        if (!hasInput(FIELD_MAP_NAME)) {
            setError(FIELD_MAP_NAME, "maps.error.noName");
            allOk = false;
        }
        
        return allOk;
    }
}
