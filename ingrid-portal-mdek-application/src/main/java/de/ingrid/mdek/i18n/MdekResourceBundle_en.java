/*-
 * **************************************************-
 * InGrid Portal MDEK Application
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
package de.ingrid.mdek.i18n;

import java.util.Locale;

/**
 * Specialised {@code MdekResourceBundle} for the "en" locale. This class is
 * needed to properly load the localised resource bundles in the English
 * language.
 *
 * @author Vikram Notay
 */
public class MdekResourceBundle_en extends MdekResourceBundle {
    public MdekResourceBundle_en(){
        super(Locale.ENGLISH);
    }
}
