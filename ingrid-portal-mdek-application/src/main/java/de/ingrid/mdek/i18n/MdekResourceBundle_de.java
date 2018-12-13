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
 * Specialised {@code MdekResourceBundle} for the "de" locale. This class is
 * needed to properly load the localised resource bundles in the German
 * language.
 *
 * @author Vikram Notay
 */
public final class MdekResourceBundle_de extends MdekResourceBundle {
    public MdekResourceBundle_de() {
        super(Locale.GERMAN);
    }
}
