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
package de.ingrid.portal.global;

import org.apache.commons.beanutils.PropertyUtils;

import java.text.Collator;
import java.util.*;

public class UniversalSorter {
    private Locale lang;
    
    public UniversalSorter(Locale locLang) {
        lang = locLang;
    }
    
    @SuppressWarnings("unchecked")
    public Collection sort(List list, String property) {
        //return 
        Collections.sort(list, new GermanLangComparator(property, lang));
        return list;
    }
    
    public class GermanLangComparator implements Comparator {
        private String field;
        private Locale lang;
        
        public GermanLangComparator(String sField, Locale locLang) {
            field = sField;
            lang  = locLang;
        }

        public final int compare(Object a, Object b) {
            try {
                String aField = (String)PropertyUtils.getProperty(a, field);
                String bField = (String)PropertyUtils.getProperty(b, field);
                
                // Get the collator for the German Locale 
                Collator gerCollator = Collator.getInstance(lang);
                return gerCollator.compare(aField, bField);
            } catch (Exception e) {
                return 0;
            }
        }
    }
}
