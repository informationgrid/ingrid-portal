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
public class SearchExtEnvAreaSourcesForm extends ActionForm {

    private static final long serialVersionUID = -696997019850499684L;

    /** attribute name of action form in session */
    public static final String SESSION_KEY = SearchExtEnvAreaSourcesForm.class.getName();

    /** field names (name of request parameter) */
    
    public static final String FIELD_CHK_SOURCES = "sources";
    public static final String FIELD_CHK_META = "meta";

    public static final String VALUE_SOURCE_ALL = "all";
    public static final String VALUE_SOURCE_WWW = "www";
    public static final String VALUE_META_ALL = "all";
    public static final String VALUE_META_0 = "meta_0";
    public static final String VALUE_META_1 = "meta_1";
    public static final String VALUE_META_2 = "meta_2";
    public static final String VALUE_META_3 = "meta_3";
    public static final String VALUE_META_4 = "meta_4";
    public static final String VALUE_META_5 = "meta_5";
    public static final String VALUE_META_6 = "meta_6";
    public static final String VALUE_SOURCE_FIS = "fis";
    
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
        this.setInput(FIELD_CHK_SOURCES, request.getParameterValues(FIELD_CHK_SOURCES));
        this.setInput(FIELD_CHK_META, request.getParameterValues(FIELD_CHK_META));
    }

    /**
     * @see de.ingrid.portal.forms.ActionForm#validate()
     */
    public boolean validate() {
        return true;
    }

}
